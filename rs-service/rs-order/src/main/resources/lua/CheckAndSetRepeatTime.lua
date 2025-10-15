---
--- Created by dai.
--- DateTime: 2025/10/13 17:10
---
local key = KEYS[1]
local start = tonumber(ARGV[1])
local end_ = tonumber(ARGV[2])

-- 检查 [start, end] 区间是否有任意一位为 1
for i = start, end_ do
    if redis.call("GETBIT", key, i) == 1 then
        return 0  -- 有冲突
    end
end

-- 无冲突，设置所有位为 1
for i = start, end_ do
    redis.call("SETBIT", key, i, 1)
end

return 1  -- 成功