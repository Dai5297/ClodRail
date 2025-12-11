<template>
  <div class="session-list">
    <div class="search-bar">
      <el-input
        v-model="searchQuery"
        :placeholder="$t('service.searchPlaceholder')"
        prefix-icon="Search"
        clearable
      />
    </div>
    
    <div class="list-container">
      <div 
        v-for="session in filteredSessions" 
        :key="session.id"
        class="session-item"
        :class="{ active: currentSessionId === session.id }"
        @click="$emit('select-session', session)"
      >
        <div class="avatar-wrapper">
          <el-avatar :src="session.avatar" :size="40" />
          <div v-if="session.unread > 0" class="unread-badge">{{ session.unread }}</div>
        </div>
        
        <div class="session-info">
          <div class="top-row">
            <span class="name">{{ session.name }}</span>
            <span class="time">{{ formatTime(session.lastMessageTime) }}</span>
          </div>
          <div class="bottom-row">
            <span class="last-message">{{ session.lastMessage }}</span>
          </div>
        </div>
      </div>
      
      <div v-if="filteredSessions.length === 0" class="no-results">
        {{ $t('service.noSessions') }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

const props = defineProps({
  sessions: {
    type: Array,
    default: () => []
  },
  currentSessionId: {
    type: [String, Number],
    default: null
  }
})

defineEmits(['select-session'])

const searchQuery = ref('')

const filteredSessions = computed(() => {
  if (!searchQuery.value) return props.sessions
  const query = searchQuery.value.toLowerCase()
  return props.sessions.filter(session => 
    session.name.toLowerCase().includes(query) || 
    session.lastMessage.toLowerCase().includes(query)
  )
})

const formatTime = (timestamp) => {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const now = new Date()
  
  // 如果是今天，显示 HH:mm
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: false })
  }
  
  // 如果是昨天，显示昨天
  const yesterday = new Date(now)
  yesterday.setDate(now.getDate() - 1)
  if (date.toDateString() === yesterday.toDateString()) {
    return '昨天'
  }
  
  // 其他显示日期
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}
</script>

<style scoped>
.session-list {
  display: flex;
  flex-direction: column;
  height: 100%;
  border-right: 1px solid var(--border-color);
  background: var(--bg-color);
}

.search-bar {
  padding: 16px;
  border-bottom: 1px solid var(--border-color);
}

.list-container {
  flex: 1;
  overflow-y: auto;
}

.session-item {
  display: flex;
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.2s;
  border-bottom: 1px solid var(--border-color-light);
}

.session-item:hover {
  background-color: var(--bg-color-secondary);
}

.session-item.active {
  background-color: var(--el-color-primary-light-9);
}

:global(html.dark) .session-item.active {
  background-color: var(--el-color-primary-dark-2);
}

.avatar-wrapper {
  position: relative;
  margin-right: 12px;
}

.unread-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background-color: var(--el-color-danger);
  color: white;
  font-size: 10px;
  height: 16px;
  min-width: 16px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  border: 2px solid var(--bg-color);
}

.session-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.top-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.name {
  font-weight: 500;
  color: var(--text-color);
  font-size: 14px;
}

.time {
  font-size: 12px;
  color: var(--text-color-secondary);
}

.bottom-row {
  display: flex;
}

.last-message {
  font-size: 13px;
  color: var(--text-color-secondary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.no-results {
  padding: 20px;
  text-align: center;
  color: var(--text-color-secondary);
  font-size: 14px;
}
</style>
