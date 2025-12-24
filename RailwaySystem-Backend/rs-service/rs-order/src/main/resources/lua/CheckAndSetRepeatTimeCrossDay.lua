-- 检查并设置跨天的时间冲突bitmap
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

-- 检查开始日期：从startSite到一天结束(71位，对应23:40)
for i = startSite, 71 do
    local bit = redis.call('GETBIT', startDateKey, i)
    if bit == 1 then
        return 0  -- 发现冲突
    end
end

-- 检查结束日期：从一天开始(0位)到endSite
for i = 0, endSite do
    local bit = redis.call('GETBIT', endDateKey, i)
    if bit == 1 then
        return 0  -- 发现冲突
    end
end

-- 检查中间的完整天数（如果存在）
local currentEpoch = startDateEpoch + 1
while currentEpoch < endDateEpoch do
    -- 构造中间日期的key
    local middleDateKey = string.gsub(startDateKey, ":" .. startDateEpoch .. ":", ":" .. currentEpoch .. ":")
    
    -- 检查整天是否有任何位被设置
    for i = 0, 71 do
        local bit = redis.call('GETBIT', middleDateKey, i)
        if bit == 1 then
            return 0  -- 发现冲突
        end
    end
    currentEpoch = currentEpoch + 1
end

-- 没有冲突，开始设置位
-- 设置开始日期
for i = startSite, 71 do
    redis.call('SETBIT', startDateKey, i, 1)
end

-- 设置结束日期
for i = 0, endSite do
    redis.call('SETBIT', endDateKey, i, 1)
end

-- 设置中间的完整天数
currentEpoch = startDateEpoch + 1
while currentEpoch < endDateEpoch do
    local middleDateKey = string.gsub(startDateKey, ":" .. startDateEpoch .. ":", ":" .. currentEpoch .. ":")
    for i = 0, 71 do
        redis.call('SETBIT', middleDateKey, i, 1)
    end
    currentEpoch = currentEpoch + 1
end

-- 设置过期时间为3天
redis.call('EXPIRE', startDateKey, 259200)
redis.call('EXPIRE', endDateKey, 259200)

-- 为中间日期也设置过期时间
currentEpoch = startDateEpoch + 1
while currentEpoch < endDateEpoch do
    local middleDateKey = string.gsub(startDateKey, ":" .. startDateEpoch .. ":", ":" .. currentEpoch .. ":")
    redis.call('EXPIRE', middleDateKey, 259200)
    currentEpoch = currentEpoch + 1
end

return 1  -- 成功