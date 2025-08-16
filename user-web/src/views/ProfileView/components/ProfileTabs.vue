<script setup lang="ts">
import { computed } from 'vue'
import { ElTabs, ElTabPane, ElIcon, ElSkeleton } from 'element-plus'
import { 
  User,
  Lock,
  Setting,
  Document,
  Star
} from '@element-plus/icons-vue'

// 定义标签配置接口
interface TabConfig {
  key: string
  label: string
  icon: string
}

// 定义属性
interface Props {
  activeTab: string
  tabConfig: TabConfig[]
  loading: boolean
}

// 定义事件
interface Emits {
  tabChange: [tabKey: string]
}

const props = withDefaults(defineProps<Props>(), {
  activeTab: 'profile',
  tabConfig: () => [],
  loading: false
})

const emit = defineEmits<Emits>()

// 图标映射
const iconMap: Record<string, any> = {
  'User': User,
  'Lock': Lock,
  'Setting': Setting,
  'Document': Document,
  'Star': Star
}

// 获取图标组件
const getIconComponent = (iconName: string) => {
  return iconMap[iconName] || User
}

// 处理标签切换
const handleTabClick = (tabKey: string) => {
  if (tabKey !== props.activeTab) {
    emit('tabChange', tabKey)
  }
}

// 获取标签样式类
const getTabClass = (tabKey: string) => {
  return {
    'tab-item': true,
    'tab-item-active': tabKey === props.activeTab
  }
}
</script>

<template>
  <div class="profile-tabs">
    <!-- 加载状态 -->
    <ElSkeleton v-if="loading" animated>
      <template #template>
        <div class="tabs-skeleton">
          <el-skeleton-item 
            v-for="i in 5" 
            :key="i"
            variant="rect" 
            style="width: 120px; height: 40px; margin-right: 16px; display: inline-block;" 
          />
        </div>
      </template>
    </ElSkeleton>
    
    <!-- 标签页内容 -->
    <div v-else class="tabs-container">
      <ElTabs 
        :model-value="activeTab" 
        class="custom-tabs"
        @tab-click="(pane) => handleTabClick(pane.paneName as string)"
      >
        <ElTabPane
          v-for="tab in tabConfig"
          :key="tab.key"
          :name="tab.key"
          :label="tab.label"
          class="tab-pane"
        >
          <template #label>
            <div :class="getTabClass(tab.key)">
              <ElIcon :size="16" class="tab-icon">
                <component :is="getIconComponent(tab.icon)" />
              </ElIcon>
              <span class="tab-label">{{ tab.label }}</span>
            </div>
          </template>
        </ElTabPane>
      </ElTabs>
    </div>
  </div>
</template>

<style scoped>
.profile-tabs {
  background: white;
  border-bottom: 1px solid #f0f0f0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.tabs-skeleton {
  padding: 16px 32px;
}

.tabs-container {
  padding: 0 32px;
}

.custom-tabs {
  --el-tabs-header-height: 60px;
}

.tab-pane {
  display: none;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  border-radius: 8px;
  transition: all 0.3s ease;
  cursor: pointer;
  font-weight: 500;
  color: #666;
  background: transparent;
  border: 1px solid transparent;
}

.tab-item:hover {
  color: #1890ff;
  background: #f0f8ff;
  border-color: #e6f4ff;
}

.tab-item-active {
  color: #1890ff;
  background: #e6f4ff;
  border-color: #91caff;
  font-weight: 600;
}

.tab-item-active .tab-icon {
  color: #1890ff;
}

.tab-icon {
  transition: color 0.3s ease;
  color: inherit;
}

.tab-label {
  font-size: 14px;
  white-space: nowrap;
  transition: color 0.3s ease;
}

/* 自定义 Element Plus Tabs 样式 */
:deep(.el-tabs__header) {
  margin: 0;
  border-bottom: none;
}

:deep(.el-tabs__nav-wrap) {
  padding: 16px 0;
}

:deep(.el-tabs__nav-scroll) {
  overflow-x: auto;
  overflow-y: hidden;
}

:deep(.el-tabs__nav) {
  border: none;
  display: flex;
  gap: 8px;
}

:deep(.el-tabs__item) {
  border: none;
  padding: 0;
  margin: 0;
  height: auto;
  line-height: 1;
  color: inherit;
  font-size: inherit;
  font-weight: inherit;
}

:deep(.el-tabs__item:hover) {
  color: inherit;
}

:deep(.el-tabs__item.is-active) {
  color: inherit;
  border-bottom: none;
}

:deep(.el-tabs__active-bar) {
  display: none;
}

:deep(.el-tabs__content) {
  display: none;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .tabs-container {
    padding: 0 20px;
  }
  
  .tabs-skeleton {
    padding: 12px 20px;
  }
  
  :deep(.el-tabs__nav-wrap) {
    padding: 12px 0;
  }
  
  :deep(.el-tabs__nav) {
    gap: 4px;
  }
  
  .tab-item {
    padding: 10px 12px;
    gap: 6px;
  }
  
  .tab-icon {
    font-size: 14px;
  }
  
  .tab-label {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  :deep(.el-tabs__nav-scroll) {
    -webkit-overflow-scrolling: touch;
  }
  
  :deep(.el-tabs__nav) {
    flex-wrap: nowrap;
    min-width: max-content;
  }
  
  .tab-item {
    padding: 8px 10px;
    min-width: max-content;
  }
  
  .tab-label {
    font-size: 12px;
  }
}

/* 滚动条样式 */
:deep(.el-tabs__nav-scroll)::-webkit-scrollbar {
  height: 4px;
}

:deep(.el-tabs__nav-scroll)::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 2px;
}

:deep(.el-tabs__nav-scroll)::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 2px;
}

:deep(.el-tabs__nav-scroll)::-webkit-scrollbar-thumb:hover {
  background: #bfbfbf;
}
</style>