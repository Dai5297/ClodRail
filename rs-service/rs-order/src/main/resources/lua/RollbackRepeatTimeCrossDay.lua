-- 回滚跨天的时间冲突bitmap
-- KEYS[1]: startDateKey (开始日期的bitmap key)
-- KEYS[2]: endDateKey (结束日期的bitmap key)
-- ARGV[1]: startSite (开始时间位置)
-- ARGV[2]: endSite (结束时间位置)
-- ARGV[3]: startDateEpoch (开始日期的epoch天数)
-- ARGV[4]: endDateEpoch (结束日期的epoch天数)

local startDateKey = KEYS[1]
local endDateKey = KEYS[2]
local startSite = tonumber(ARGV[1])
local endSite = tonumber(ARGV[2])
local startDateEpoch = tonumber(ARGV[3])
local endDateEpoch = tonumber(ARGV[4])

-- 回滚开始日期：从startSite到一天结束
for i = startSite, 71 do
    redis.call('SETBIT', startDateKey, i, 0)
end

-- 回滚结束日期：从一天开始到endSite
for i = 0, endSite do
    redis.call('SETBIT', endDateKey, i, 0)
end

-- 回滚中间的完整天数
local currentEpoch = startDateEpoch + 1
while currentEpoch < endDateEpoch do
    local middleDateKey = string.gsub(startDateKey, ":" .. startDateEpoch .. ":", ":" .. currentEpoch .. ":")
    for i = 0, 71 do
        redis.call('SETBIT', middleDateKey, i, 0)
    end
    currentEpoch = currentEpoch + 1
end

-- 重新设置过期时间，不删除key以防止影响同一乘客的其他车票
redis.call('EXPIRE', startDateKey, 259200)
redis.call('EXPIRE', endDateKey, 259200)

-- 为中间日期也设置过期时间
currentEpoch = startDateEpoch + 1
while currentEpoch < endDateEpoch do
    local middleDateKey = string.gsub(startDateKey, ":" .. startDateEpoch .. ":", ":" .. currentEpoch .. ":")
    redis.call('EXPIRE', middleDateKey, 259200)
    currentEpoch = currentEpoch + 1
end

return 1