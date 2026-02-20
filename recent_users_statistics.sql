-- 统计最近n天新增的用户数量
-- 假设用户表名为 user，创建时间字段为 create_time

SELECT COUNT(*) as new_user_count
FROM user 
WHERE create_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
  AND is_delete = 1;

-- 或者使用具体的天数替代占位符
-- 例如统计最近7天新增用户：
SELECT COUNT(*) as new_user_count
FROM user 
WHERE create_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
  AND is_delete = 1;

-- 如果需要按天统计每天的新增用户数：
SELECT 
    DATE(create_time) as register_date,
    COUNT(*) as daily_new_users
FROM user 
WHERE create_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
  AND is_delete = 1
GROUP BY DATE(create_time)
ORDER BY register_date DESC;

-- 如果数据库使用的是不同的时间函数，可以根据实际情况调整：
-- MySQL: NOW(), DATE_SUB()
-- PostgreSQL: NOW(), CURRENT_DATE - INTERVAL '? DAY'
-- SQL Server: GETDATE(), DATEADD(DAY, -?, GETDATE())
-- Oracle: SYSDATE, SYSDATE - ?