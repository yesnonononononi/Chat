// 方式1：直接解构导入 WebSocketServer（推荐）
import { WebSocketServer } from 'ws';

// 启动服务端（注意：用 WebSocketServer 而非 WebSocket.Server）
const wss = new WebSocketServer({ port: 8080 });
console.log('测试服务端启动：ws://localhost:8080');

// 监听客户端连接
wss.on('connection', (ws) => {
  console.log('客户端已连接');

  // 1. 响应客户端心跳（收到 ping 回复 pong）
  ws.on('message', (data) => {
    const msg = typeof data === "string" ? data : data.toString("utf-8");
    const serverMsg = JSON.parse(msg);
    console.log('收到客户端消息：', serverMsg);
    
    // 心跳响应
    if (serverMsg.type === 'notice' && serverMsg.msg === 'ping') {
      ws.send(JSON.stringify({ type: 'notice', msg: 'pong' }));
    }
  });

  // 2. 模拟「服务端主动断连」（测试客户端重连）
  setTimeout(() => {
    console.log('服务端主动断开连接');
    ws.close(1001, '模拟异常断连'); // 非1000状态码表示异常关闭
  }, 15000);

  // 3. 监听客户端断连
  ws.on('close', (code, reason) => {
    console.log(`客户端断开连接：code=${code}, reason=${reason.toString('utf-8')}`);
  });

  // 4. 监听连接错误
  ws.on('error', (err) => {
    console.error('客户端连接错误：', err);
  });
});

// 监听服务端启动错误
wss.on('error', (err) => {
  console.error('服务端启动失败：', err);
});
