local id = KEYS[1]
local cacheKey = ARGV[1] .. id
local holdTtl = ARGV[2]


-- 检查缓存是否存在
local result = redis.call('GET', cacheKey)

if result == "HOLD" then
    -- 如果缓存存在，直接返回结果
    return "PROCESS"   --已有方法正在执行
elseif (not result) then
    -- 设置执行占位符，并设置合理 TTL，避免极端情况下永远卡住
    -- 该 TTL 应当大于接口最大预期执行时间，否则可能出现 HOLD 过期导致并发穿透
    redis.call("set",cacheKey,"HOLD","EX",holdTtl);
    return "NONE"   --没有结果
else
    -- 如果缓存结果不是占位符,直接返回真实结果
    return result;  --真实结果
end