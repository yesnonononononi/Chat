-- 清理HOLD占位符脚本
local id = KEYS[1]
local cacheKey = ARGV[1] .. id
local res = redis.call("GET", cacheKey)
-- 仅当值为HOLD时删除，避免误删真实业务结果
if res == "HOLD" then
    redis.call("DEL", cacheKey)
end