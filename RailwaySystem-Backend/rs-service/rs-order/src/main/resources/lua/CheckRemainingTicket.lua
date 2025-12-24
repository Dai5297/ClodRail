local key = KEYS[1]
local needCount = tonumber(ARGV[1])

-- 获取当前库存（字符串 → 数字）
local currentStock = tonumber(redis.call('GET', key))

-- 如果库存不足，返回 0（失败）
if currentStock == nil or currentStock < needCount then
    return 0
end

-- 扣减库存
redis.call('DECRBY', key, needCount)
return 1