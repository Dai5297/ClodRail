---
--- Created by dai.
--- DateTime: 2025/10/13 17:10
---
local key = KEYS[1]
local start = tonumber(ARGV[1])
local end_ = tonumber(ARGV[2])

for i = start, end_ do
    redis.call("SETBIT", key, i, 0)
end