<script setup lang="ts">
import { ref } from 'vue'
import { ElDialog, ElSwitch, ElButton, ElSlider } from 'element-plus'

// 定义Props
interface Props {
  visible: boolean
}

const props = defineProps<Props>()

// 定义事件
const emit = defineEmits<{
  'update:visible': [value: boolean]
  close: []
}>()

// 设置项
const settings = ref({
  voicePlayback: true,
  autoSuggestion: true,
  messageNotification: false,
  saveHistory: true,
  voiceVolume: 70,
  responseSpeed: 2
})

// 处理关闭
const handleClose = () => {
  emit('update:visible', false)
  emit('close')
}

// 保存设置
const saveSettings = () => {
  // 这里可以保存设置到本地存储或发送到服务器
  localStorage.setItem('ai-assistant-settings', JSON.stringify(settings.value))
  handleClose()
}

// 重置设置
const resetSettings = () => {
  settings.value = {
    voicePlayback: true,
    autoSuggestion: true,
    messageNotification: false,
    saveHistory: true,
    voiceVolume: 70,
    responseSpeed: 2
  }
}

// 响应速度选项
const speedOptions = [
  { value: 1, label: '慢速' },
  { value: 2, label: '正常' },
  { value: 3, label: '快速' }
]
</script>

<template>
  <el-dialog
    :model-value="visible"
    title="AI助手设置"
    width="500px"
    :before-close="handleClose"
    @update:model-value="$emit('update:visible', $event)"
  >
    <div class="settings-content">
      <!-- 语音设置 -->
      <div class="setting-section">
        <h4 class="section-title">语音设置</h4>
        
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">语音播报</span>
            <span class="setting-desc">开启后AI回复将自动播放语音</span>
          </div>
          <el-switch v-model="settings.voicePlayback" />
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">语音音量</span>
            <span class="setting-desc">调节语音播报的音量大小</span>
          </div>
          <div class="volume-control">
            <el-slider 
              v-model="settings.voiceVolume" 
              :min="0" 
              :max="100" 
              :disabled="!settings.voicePlayback"
              style="width: 120px;"
            />
            <span class="volume-value">{{ settings.voiceVolume }}%</span>
          </div>
        </div>
      </div>

      <!-- 交互设置 -->
      <div class="setting-section">
        <h4 class="section-title">交互设置</h4>
        
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">自动建议</span>
            <span class="setting-desc">显示相关问题建议</span>
          </div>
          <el-switch v-model="settings.autoSuggestion" />
        </div>
        
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">响应速度</span>
            <span class="setting-desc">AI回复的速度设置</span>
          </div>
          <div class="speed-control">
            <div class="speed-options">
              <label 
                v-for="option in speedOptions" 
                :key="option.value"
                class="speed-option"
                :class="{ active: settings.responseSpeed === option.value }"
              >
                <input 
                  v-model="settings.responseSpeed" 
                  type="radio" 
                  :value="option.value"
                  style="display: none;"
                >
                {{ option.label }}
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- 通知设置 -->
      <div class="setting-section">
        <h4 class="section-title">通知设置</h4>
        
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">消息提醒</span>
            <span class="setting-desc">收到新消息时显示通知</span>
          </div>
          <el-switch v-model="settings.messageNotification" />
        </div>
      </div>

      <!-- 隐私设置 -->
      <div class="setting-section">
        <h4 class="section-title">隐私设置</h4>
        
        <div class="setting-item">
          <div class="setting-info">
            <span class="setting-label">对话历史保存</span>
            <span class="setting-desc">保存对话记录以便下次访问</span>
          </div>
          <el-switch v-model="settings.saveHistory" />
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="resetSettings">重置</el-button>
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="saveSettings">保存</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<style scoped>
.settings-content {
  max-height: 500px;
  overflow-y: auto;
}

.setting-section {
  margin-bottom: 24px;
}

.setting-section:last-child {
  margin-bottom: 0;
}

.section-title {
  margin: 0 0 16px 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 8px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 0;
  border-bottom: 1px solid #fafafa;
}

.setting-item:last-child {
  border-bottom: none;
}

.setting-info {
  flex: 1;
  margin-right: 16px;
}

.setting-label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.setting-desc {
  font-size: 12px;
  color: #8c8c8c;
  line-height: 1.4;
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 12px;
}

.volume-value {
  font-size: 12px;
  color: #8c8c8c;
  min-width: 35px;
}

.speed-control {
  display: flex;
  align-items: center;
}

.speed-options {
  display: flex;
  gap: 8px;
}

.speed-option {
  padding: 4px 12px;
  border: 1px solid #d9d9d9;
  border-radius: 6px;
  font-size: 12px;
  color: #595959;
  cursor: pointer;
  transition: all 0.3s ease;
  user-select: none;
}

.speed-option:hover {
  border-color: #1890ff;
  color: #1890ff;
}

.speed-option.active {
  background: #1890ff;
  border-color: #1890ff;
  color: white;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 滚动条样式 */
.settings-content::-webkit-scrollbar {
  width: 6px;
}

.settings-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.settings-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.settings-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>