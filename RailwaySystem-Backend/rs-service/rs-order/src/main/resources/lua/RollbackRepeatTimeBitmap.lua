-- 回滚同一天的时间冲突bitmap
-- KEYS[1]: bitmap key (TICKET_USER_TIME + date + ":" + passengerId)
-- ARGV[1]: startSite (开始时间位置)
-- ARGV[2]: endSite (结束时间位置)

local key = KEYS[1]
local startSite = tonumber(ARGV[1])
local endSite = tonumber(ARGV[2])

-- 只清除指定时间范围内的位，不删除整个key
-- 因为同一个乘客在同一天可能有多张车票，删除整个key会影响其他车票
for i = startSite, endSite do
    redis.call('SETBIT', key, i, 0)
end

-- 重新设置过期时间，确保key最终会被清理
redis.call('EXPIRE', key, 259200)

return 1