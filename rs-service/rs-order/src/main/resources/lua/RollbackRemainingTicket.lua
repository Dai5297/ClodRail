local key = KEYS[1]
local addStore = ARGV[1]

local store = redis.call('GET', key)
redis.call('SET', key, store + addStore)