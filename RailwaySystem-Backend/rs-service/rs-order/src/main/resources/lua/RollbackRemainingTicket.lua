local key = KEYS[1]
local addStore = tonumber(ARGV[1])

local store = redis.call('GET', key)
if store then
    store = tonumber(store)
    redis.call('SET', key, store + addStore)
    return 1
else
    return 0
end