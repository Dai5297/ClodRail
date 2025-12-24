-- 检查并设置同一天的时间冲突bitmap
-- KEYS[1]: bitmap key (TICKET_USER_TIME + date + ":" + passengerId)
-- ARGV[1]: startSite (开始时间位置)
-- ARGV[2]: endSite (结束时间位置)

local key = KEYS[1]
local startSite = tonumber(ARGV[1])
local endSite = tonumber(ARGV[2])

-- 检查时间范围内是否有冲突
for i = startSite, endSite do
    local bit = redis.call('GETBIT', key, i)
    if bit == 1 then
        -- 发现冲突，返回0表示失败
        return 0
    end
end

-- 没有冲突，设置时间范围内的所有位为1
for i = startSite, endSite do
    redis.call('SETBIT', key, i, 1)
end

-- 设置过期时间为3天（259200秒）
redis.call('EXPIRE', key, 259200)

-- 返回1表示成功
return 1