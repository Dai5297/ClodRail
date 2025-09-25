<script setup lang="ts">
import { ref } from 'vue'
import { ElInput, ElIcon } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

// 定义属性
interface Props {
  activeTab: string
  tabCounts: Record<string, number> & {
    all: number
    pending: number
    paid: number
    completed: number
    cancelled: number
    refunded: number
  }
}

const props = withDefaults(defineProps<Props>(), {
  activeTab: 'all',
  tabCounts: () => ({
    all: 0,
    pending: 0,
    paid: 0,
    completed: 0,
    cancelled: 0,
    refunded: 0
  })
})

// 定义事件
const emit = defineEmits(['tabChange', 'ticket'])

// 搜索关键词
const searchKeyword = ref('')

// 标签配置
const tabs = [
  { key: 'all', label: '全部订单', icon: '📋' },
  { key: 'pending', label: '待支付', icon: '⏰' },
  { key: 'paid', label: '已支付', icon: '✅' },
  { key: 'completed', label: '已完成', icon: '🎯' },
  { key: 'cancelled', label: '已取消', icon: '❌' },
  { key: 'refunded', label: '已退款', icon: '💰' }
]

// 切换标签
const handleTabClick = (tab: string) => {
  emit('tabChange', tab)
}

// 搜索处理
const handleSearch = () => {
  emit('search', searchKeyword.value)
}

// 清空搜索
const clearSearch = () => {
  searchKeyword.value = ''
  emit('search', '')
}
</script>

<template>
  <div class="order-tabs">
    <!-- 搜索栏 -->
    <div class="search-section">
      <div class="search-wrapper">
        <ElInput
          v-model="searchKeyword"
          placeholder="搜索订单号、车次号、出发地、目的地..."
          class="search-input"
          clearable
          @input="handleSearch"
          @clear="clearSearch"
        >
          <template #prefix>
            <ElIcon><Search /></ElIcon>
          </template>
        </ElInput>
      </div>
    </div>

    <!-- 状态标签 -->
    <div class="tabs-wrapper">
      <div class="tabs-container">
        <div
          v-for="tab in tabs"
          :key="tab.key"
          class="tab-item"
          :class="{ active: activeTab === tab.key }"
          @click="handleTabClick(tab.key)"
        >
          <div class="tab-content">
            <span class="tab-icon">{{ tab.icon }}</span>
            <span class="tab-label">{{ tab.label }}</span>
            <span v-if="tabCounts[tab.key] > 0" class="tab-count">
              {{ tabCounts[tab.key] }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.order-tabs {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
  overflow: hidden;
}

.search-section {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.search-wrapper {
  max-width: 400px;
  margin: 0 auto;
}

.search-input {
  --el-input-border-radius: 20px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tabs-wrapper {
  padding: 0;
}

.tabs-container {
  display: flex;
  overflow-x: auto;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.tabs-container::-webkit-scrollbar {
  display: none;
}

.tab-item {
  flex: 1;
  min-width: 120px;
  padding: 16px 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 3px solid transparent;
  position: relative;
}

.tab-item:hover {
  background: #f8f9fa;
}

.tab-item.active {
  background: #e6f4ff;
  border-bottom-color: #1890ff;
}

.tab-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.tab-icon {
  font-size: 20px;
  margin-bottom: 2px;
}

.tab-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  text-align: center;
  line-height: 1.2;
}

.tab-item.active .tab-label {
  color: #1890ff;
  font-weight: 600;
}

.tab-count {
  background: #ff4d4f;
  color: white;
  font-size: 12px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 18px;
  height: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 8px;
  right: 8px;
}

.tab-item.active .tab-count {
  background: #1890ff;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-section {
    padding: 15px;
  }

  .tab-item {
    min-width: 100px;
    padding: 12px 8px;
  }

  .tab-icon {
    font-size: 18px;
  }

  .tab-label {
    font-size: 12px;
  }
}
</style>
