package com.summit.chat.GlobalHandle.Log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.summit.chat.Constants.QueueConstants;
import com.summit.chat.model.entity.ck.SystemLog;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;


import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
public class ClickHouseLogAppend extends AppenderBase<ILoggingEvent>   {
    private final RabbitTemplate rabbitTemplate;
    public static final ConcurrentMap<String, Object> fileLock = new ConcurrentHashMap<>();
    private static Cache<String, SystemLog> cache;
    private final Object cacheLock = new Object();
    private static final int MAX_LOG_LENGTH = 1000;
    private static final int BEFORE_ACCESS_DURATION = 5;
    private static final int AFTER_ACCESS_DURATION = 2;
    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            5,
            10,
            1000,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1000),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    public ClickHouseLogAppend( RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void stop() {
        super.stop();
        long l = cache.estimatedSize();
        //持久化剩余日志
        if (l > 0) {
            try {
                synchronized (cacheLock) {
                    handleFull();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        log.info("【日志持久化】日志持久化完成，剩余日志：{}", l);
        cache.invalidateAll();
        threadPoolExecutor.shutdown();
        try {
            if (!threadPoolExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPoolExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPoolExecutor.shutdownNow();
        }
    }

    @Override
    public void start() {
        super.start();
        //创建缓存
        cache = Caffeine.newBuilder()
                .maximumSize(MAX_LOG_LENGTH)              //最大缓存1000个
                .expireAfterWrite(BEFORE_ACCESS_DURATION, TimeUnit.MINUTES)  //写入后5分钟过期
                .expireAfterAccess(AFTER_ACCESS_DURATION, TimeUnit.MINUTES)   //访问后2分钟过期
                .recordStats()  //统计(监控命中率)
                .build();
    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        try {
            long l = cache.estimatedSize();
            synchronized (cacheLock) {
                if (l >= MAX_LOG_LENGTH) {
                    handleFull();
                }
                Level level = iLoggingEvent.getLevel();
                String msg = iLoggingEvent.getFormattedMessage();
                SystemLog build = SystemLog.builder()
                        .content(msg)
                        .level(level.levelStr)
                        .createTime(Date.from(iLoggingEvent.getInstant()))
                        .build();
                cache.put(UUID.randomUUID().toString(), build);
            }
        } catch (Exception e) {
            log.error("【日志持久化】未知错误：{}", e.getMessage());
            //降级处理
            writeSingleLogToLocal(iLoggingEvent);
        }
    }

    private void handleFull() throws IOException {
        List<SystemLog> logList = null;
        if(cache == null){
            log.error("【日志持久化】缓存未初始化");
            return;
        }
        try {
            //1,获取缓存数据
            logList = new ArrayList<>(cache.asMap().values());

            CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
            //2,发送
            send(logList, correlationData);
            //3,清空缓存
            cache.invalidateAll();
        } catch (Exception e) {
            log.error("【日志持久化】日志写入失败，错误：{}", e.getMessage());
            writeToLocal(logList);
        }
    }

    public static ArrayList<SystemLog> getCacheLog(){
        return new ArrayList<>(cache.asMap().values());
    }


    /**
     * 日志写入本地
     *
     * @param list
     */
    public static void writeToLocal(List<SystemLog> list) {
        if (list == null || list.isEmpty()) return;
        String logPath = System.getProperty("user.dir") + "/log_backup/batch_log_" + System.currentTimeMillis() + ".log";
        Object lock = fileLock.computeIfAbsent(logPath, k -> new Object());
//这段代码的作用是为每个日志文件路径创建一个唯一的锁对象，用于保证多线程环境下对同一文件的写入操作是线程安全的。通过 `ConcurrentHashMap` 的 `computeIfAbsent` 方法，如果指定的 `logPath` 键不存在，则创建一个新的 `Object` 实例作为锁；如果已存在，则直接返回现有的锁对象。这样可以避免多个线程同时写入同一个日志文件时产生冲突。
        synchronized (lock) {
            try (FileWriter fileWriter = new FileWriter(logPath, true)) {
                for (SystemLog systemLog : list) {
                    String logline = String.format("%s %s %s %s\n", systemLog.getLevel(), DateUtil.format(systemLog.getCreateTime(), "yyyy-MM-dd HH:mm:ss"), systemLog.getContent(), systemLog.getId());
                    fileWriter.write(logline);
                }
                log.info("【日志持久化-降级】日志写入本地失败，备份文件路径：{}", logPath);
            } catch (Exception e) {
                log.error("【日志持久化-降级】日志写入本地失败，错误：{}", e.getMessage());
            } finally {
                fileLock.remove(logPath);
            }
        }
    }


    /**
     * 单条日志降级写入本地（补充方法）
     */
    private void writeSingleLogToLocal(ILoggingEvent event) {
        String logPath = System.getProperty("user.dir") + "/log_backup/single_log_" + System.currentTimeMillis() + ".log";
        Object lock = fileLock.computeIfAbsent(logPath, k -> new Object());

        synchronized (lock) {
            try (FileWriter fileWriter = new FileWriter(logPath, true)) {
                String logline = String.format("[%s] [时间：%s] [内容：%s]%n",
                        event.getLevel().levelStr,
                        DateUtil.format(DateTime.of(event.getTimeStamp()), "yyyy-MM-dd HH:mm:ss"),
                        event.getFormattedMessage());
                fileWriter.write(logline);
                log.info("【日志降级】单条日志写入本地成功，路径：{}", logPath);
            } catch (Exception e) {
                log.error("【日志降级】单条日志写入本地失败", e);
            } finally {
                fileLock.remove(logPath);
            }
        }
    }

    private void send(List<SystemLog> logList, CorrelationData correlationData){
          threadPoolExecutor.execute(() -> {
              try {
                  rabbitTemplate.convertAndSend(QueueConstants.LOG_EXCHANGE_NAME, QueueConstants.LOG_QUEUE_ROUTING_KEY, logList, correlationData);
              }catch (Exception e){
                  log.error("【日志持久化】发送失败，错误：{}", e.getMessage());
                  writeToLocal(logList);
              }
        });
    }
}
