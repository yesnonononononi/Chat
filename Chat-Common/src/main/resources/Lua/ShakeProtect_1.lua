-- 本脚本在于将方法执行后的结果缓存进redis
local result = ARGV[1];

local id = KEYS[1];

local cacheKey =ARGV [2];

local ttl = ARGV[3];
local cacheName = cacheKey ..id;

local res = redis.call("get",cacheName);

if((not res) or res == "HOLD") then

    redis.call("set",cacheName,result,"EX",ttl);

end