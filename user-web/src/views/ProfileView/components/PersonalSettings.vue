<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { 
  ElCard, 
  ElForm, 
  ElFormItem, 
  ElSwitch, 
  ElSelect, 
  ElOption, 
  ElButton, 
  ElMessage, 
  ElSkeleton,
  ElIcon,
  ElDivider,
  ElAlert,
  ElSlider,
  ElColorPicker,
  ElTimePicker
} from 'element-plus'
import { 
  Setting,
  Notification,
  Moon,
  Sunny,
  Monitor,
  Bell,
  Message,
  Phone,
  Connection
} from '@element-plus/icons-vue'
import type { FormInstance } from 'element-plus'

// 定义个人设置接口
interface PersonalSettings {
  // 通知设置
  emailNotification: boolean
  smsNotification: boolean
  pushNotification: boolean
  orderNotification: boolean
  promotionNotification: boolean
  systemNotification: boolean
  
  // 界面设置
  theme: 'light' | 'dark' | 'auto'
  language: 'zh-CN' | 'en-US'
  fontSize: number
  primaryColor: string
  
  // 隐私设置
  profileVisible: boolean
  onlineStatus: boolean
  dataCollection: boolean
  
  // 功能设置
  autoLogin: boolean
  rememberPassword: boolean
  quickBooking: boolean
  
  // 提醒设置
  departureReminder: boolean
  reminderTime: string // 提前提醒时间
  reminderMethod: 'email' | 'sms' | 'push'
}

// 定义属性
interface Props {
  settings: PersonalSettings | null
  loading: boolean
}

// 定义事件
interface Emits {
  updateSettings: [settings: Partial<PersonalSettings>]
  resetSettings: []
}

const props = withDefaults(defineProps<Props>(), {
  settings: null,
  loading: false
})

const emit = defineEmits<Emits>()

// 表单引用
const formRef = ref<FormInstance>()

// 表单数据
const formData = reactive<PersonalSettings>({
  // 通知设置
  emailNotification: true,
  smsNotification: true,
  pushNotification: true,
  orderNotification: true,
  promotionNotification: false,
  systemNotification: true,
  
  // 界面设置
  theme: 'light',
  language: 'zh-CN',
  fontSize: 14,
  primaryColor: '#1890ff',
  
  // 隐私设置
  profileVisible: true,
  onlineStatus: true,
  dataCollection: false,
  
  // 功能设置
  autoLogin: false,
  rememberPassword: true,
  quickBooking: true,
  
  // 提醒设置
  departureReminder: true,
  reminderTime: '30',
  reminderMethod: 'push'
})

// 选项配置
const themeOptions = [
  { label: '浅色主题', value: 'light', icon: Sunny },
  { label: '深色主题', value: 'dark', icon: Moon },
  { label: '跟随系统', value: 'auto', icon: Monitor }
]

const languageOptions = [
  { label: '简体中文', value: 'zh-CN' },
  { label: 'English', value: 'en-US' }
]

const reminderTimeOptions = [
  { label: '15分钟前', value: '15' },
  { label: '30分钟前', value: '30' },
  { label: '1小时前', value: '60' },
  { label: '2小时前', value: '120' },
  { label: '1天前', value: '1440' }
]

const reminderMethodOptions = [
  { label: '推送通知', value: 'push', icon: Bell },
  { label: '短信提醒', value: 'sms', icon: Phone },
  { label: '邮件提醒', value: 'email', icon: Message }
]

// 计算属性
const displaySettings = computed(() => props.settings || formData)

// 初始化表单数据
const initFormData = () => {
  if (props.settings) {
    Object.assign(formData, props.settings)
  }
}

// 监听设置变化
const watchSettings = () => {
  if (props.settings) {
    initFormData()
  }
}

// 保存设置
const saveSettings = async () => {
  try {
    emit('updateSettings', { ...formData })
    ElMessage.success('设置保存成功')
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存设置失败')
  }
}

// 重置设置
const resetSettings = async () => {
  try {
    emit('resetSettings')
    ElMessage.success('设置重置成功')
  } catch (error) {
    console.error('重置设置失败:', error)
    ElMessage.error('重置设置失败')
  }
}

// 获取主题图标
const getThemeIcon = (theme: string) => {
  const option = themeOptions.find(opt => opt.value === theme)
  return option?.icon || Sunny
}

// 获取提醒方式图标
const getReminderIcon = (method: string) => {
  const option = reminderMethodOptions.find(opt => opt.value === method)
  return option?.icon || Bell
}

// 字体大小格式化
const formatFontSize = (value: number) => {
  return `${value}px`
}

// 初始化
watchSettings()
</script>

<template>
  <div class="personal-settings">
    <ElCard class="settings-card" shadow="hover">
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <ElIcon :size="20" color="#1890ff">
              <Setting />
            </ElIcon>
            <span class="header-title">个人设置</span>
          </div>
          <div class="header-right">
            <ElButton @click="resetSettings" :loading="loading">
              重置设置
            </ElButton>
            <ElButton type="primary" @click="saveSettings" :loading="loading">
              保存设置
            </ElButton>
          </div>
        </div>
      </template>
      
      <!-- 加载状态 -->
      <ElSkeleton v-if="loading" animated>
        <template #template>
          <div class="settings-skeleton">
            <el-skeleton-item v-for="i in 8" :key="i" variant="rect" style="height: 60px; margin-bottom: 16px;" />
          </div>
        </template>
      </ElSkeleton>
      
      <!-- 设置内容 -->
      <div v-else class="settings-content">
        <ElForm ref="formRef" :model="formData" label-width="120px" class="settings-form">
          <!-- 通知设置 -->
          <div class="settings-section">
            <div class="section-header">
              <ElIcon :size="18" color="#1890ff">
                <Notification />
              </ElIcon>
              <h3 class="section-title">通知设置</h3>
            </div>
            
            <div class="settings-grid">
              <ElFormItem label="邮件通知">
                <ElSwitch 
                  v-model="formData.emailNotification" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">接收重要邮件通知</span>
              </ElFormItem>
              
              <ElFormItem label="短信通知">
                <ElSwitch 
                  v-model="formData.smsNotification" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">接收短信提醒</span>
              </ElFormItem>
              
              <ElFormItem label="推送通知">
                <ElSwitch 
                  v-model="formData.pushNotification" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">接收浏览器推送</span>
              </ElFormItem>
              
              <ElFormItem label="订单通知">
                <ElSwitch 
                  v-model="formData.orderNotification" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">订单状态变更通知</span>
              </ElFormItem>
              
              <ElFormItem label="优惠通知">
                <ElSwitch 
                  v-model="formData.promotionNotification" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">优惠活动推送</span>
              </ElFormItem>
              
              <ElFormItem label="系统通知">
                <ElSwitch 
                  v-model="formData.systemNotification" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">系统维护等重要通知</span>
              </ElFormItem>
            </div>
          </div>
          
          <ElDivider />
          
          <!-- 界面设置 -->
          <div class="settings-section">
            <div class="section-header">
              <ElIcon :size="18" color="#1890ff">
                <Palette />
              </ElIcon>
              <h3 class="section-title">界面设置</h3>
            </div>
            
            <div class="settings-grid">
              <ElFormItem label="主题模式">
                <ElSelect v-model="formData.theme" style="width: 200px;">
                  <ElOption 
                    v-for="option in themeOptions" 
                    :key="option.value" 
                    :label="option.label" 
                    :value="option.value"
                  >
                    <div class="option-item">
                      <ElIcon :size="16">
                        <component :is="option.icon" />
                      </ElIcon>
                      <span>{{ option.label }}</span>
                    </div>
                  </ElOption>
                </ElSelect>
                <span class="setting-desc">选择界面主题</span>
              </ElFormItem>
              
              <ElFormItem label="语言设置">
                <ElSelect v-model="formData.language" style="width: 200px;">
                  <ElOption 
                    v-for="option in languageOptions" 
                    :key="option.value" 
                    :label="option.label" 
                    :value="option.value" 
                  />
                </ElSelect>
                <span class="setting-desc">选择界面语言</span>
              </ElFormItem>
              
              <ElFormItem label="字体大小">
                <div class="slider-container">
                  <ElSlider 
                    v-model="formData.fontSize" 
                    :min="12" 
                    :max="18" 
                    :step="1"
                    :format-tooltip="formatFontSize"
                    style="width: 200px;"
                  />
                  <span class="slider-value">{{ formatFontSize(formData.fontSize) }}</span>
                </div>
                <span class="setting-desc">调整界面字体大小</span>
              </ElFormItem>
              
              <ElFormItem label="主题色">
                <ElColorPicker 
                  v-model="formData.primaryColor" 
                  :predefine="['#1890ff', '#52c41a', '#faad14', '#f5222d', '#722ed1', '#13c2c2']"
                />
                <span class="setting-desc">自定义主题颜色</span>
              </ElFormItem>
            </div>
          </div>
          
          <ElDivider />
          
          <!-- 隐私设置 -->
          <div class="settings-section">
            <div class="section-header">
              <ElIcon :size="18" color="#1890ff">
                <Lock />
              </ElIcon>
              <h3 class="section-title">隐私设置</h3>
            </div>
            
            <div class="settings-grid">
              <ElFormItem label="资料可见性">
                <ElSwitch 
                  v-model="formData.profileVisible" 
                  active-text="公开" 
                  inactive-text="私密"
                />
                <span class="setting-desc">是否公开个人资料</span>
              </ElFormItem>
              
              <ElFormItem label="在线状态">
                <ElSwitch 
                  v-model="formData.onlineStatus" 
                  active-text="显示" 
                  inactive-text="隐藏"
                />
                <span class="setting-desc">是否显示在线状态</span>
              </ElFormItem>
              
              <ElFormItem label="数据收集">
                <ElSwitch 
                  v-model="formData.dataCollection" 
                  active-text="允许" 
                  inactive-text="拒绝"
                />
                <span class="setting-desc">是否允许收集使用数据</span>
              </ElFormItem>
            </div>
          </div>
          
          <ElDivider />
          
          <!-- 功能设置 -->
          <div class="settings-section">
            <div class="section-header">
              <ElIcon :size="18" color="#1890ff">
                <Setting />
              </ElIcon>
              <h3 class="section-title">功能设置</h3>
            </div>
            
            <div class="settings-grid">
              <ElFormItem label="自动登录">
                <ElSwitch 
                  v-model="formData.autoLogin" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">下次访问自动登录</span>
              </ElFormItem>
              
              <ElFormItem label="记住密码">
                <ElSwitch 
                  v-model="formData.rememberPassword" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">浏览器记住登录密码</span>
              </ElFormItem>
              
              <ElFormItem label="快速订票">
                <ElSwitch 
                  v-model="formData.quickBooking" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">启用快速订票功能</span>
              </ElFormItem>
            </div>
          </div>
          
          <ElDivider />
          
          <!-- 提醒设置 -->
          <div class="settings-section">
            <div class="section-header">
              <ElIcon :size="18" color="#1890ff">
                <Bell />
              </ElIcon>
              <h3 class="section-title">提醒设置</h3>
            </div>
            
            <div class="settings-grid">
              <ElFormItem label="出行提醒">
                <ElSwitch 
                  v-model="formData.departureReminder" 
                  active-text="开启" 
                  inactive-text="关闭"
                />
                <span class="setting-desc">出行前自动提醒</span>
              </ElFormItem>
              
              <ElFormItem label="提醒时间" v-if="formData.departureReminder">
                <ElSelect v-model="formData.reminderTime" style="width: 200px;">
                  <ElOption 
                    v-for="option in reminderTimeOptions" 
                    :key="option.value" 
                    :label="option.label" 
                    :value="option.value" 
                  />
                </ElSelect>
                <span class="setting-desc">提前多长时间提醒</span>
              </ElFormItem>
              
              <ElFormItem label="提醒方式" v-if="formData.departureReminder">
                <ElSelect v-model="formData.reminderMethod" style="width: 200px;">
                  <ElOption 
                    v-for="option in reminderMethodOptions" 
                    :key="option.value" 
                    :label="option.label" 
                    :value="option.value"
                  >
                    <div class="option-item">
                      <ElIcon :size="16">
                        <component :is="option.icon" />
                      </ElIcon>
                      <span>{{ option.label }}</span>
                    </div>
                  </ElOption>
                </ElSelect>
                <span class="setting-desc">选择提醒方式</span>
              </ElFormItem>
            </div>
          </div>
        </ElForm>
        
        <!-- 设置说明 -->
        <ElAlert
          title="设置说明"
          type="info"
          :closable="false"
          class="settings-alert"
        >
          <template #default>
            <p>• 设置修改后需要点击"保存设置"才能生效</p>
            <p>• 部分设置可能需要重新登录后生效</p>
            <p>• 重置设置将恢复所有选项到默认状态</p>
          </template>
        </ElAlert>
      </div>
    </ElCard>
  </div>
</template>

<style scoped>
.personal-settings {
  max-width: 900px;
}

.settings-card {
  border-radius: 12px;
  overflow: hidden;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.header-right {
  display: flex;
  gap: 8px;
}

.settings-skeleton {
  padding: 8px 0;
}

.settings-content {
  padding: 8px 0;
}

.settings-form {
  margin-bottom: 24px;
}

.settings-section {
  margin-bottom: 32px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
}

.section-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #262626;
}

.settings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px 40px;
}

:deep(.el-form-item) {
  margin-bottom: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

:deep(.el-form-item__label) {
  font-weight: 500;
  color: #262626;
  line-height: 1.4;
}

:deep(.el-form-item__content) {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.setting-desc {
  font-size: 12px;
  color: #8c8c8c;
  line-height: 1.4;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.slider-container {
  display: flex;
  align-items: center;
  gap: 16px;
}

.slider-value {
  font-size: 14px;
  color: #595959;
  min-width: 40px;
}

.settings-alert {
  margin-top: 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .header-right {
    justify-content: center;
  }
  
  .settings-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  :deep(.el-form-item__label) {
    width: 100px !important;
  }
}

@media (max-width: 480px) {
  .slider-container {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .slider-value {
    text-align: center;
  }
  
  :deep(.el-form-item__label) {
    width: 80px !important;
    font-size: 14px;
  }
}

/* 开关样式优化 */
:deep(.el-switch) {
  --el-switch-on-color: #1890ff;
  --el-switch-off-color: #dcdfe6;
}

:deep(.el-switch__label) {
  font-size: 12px;
  color: #8c8c8c;
}

:deep(.el-switch__label.is-active) {
  color: #1890ff;
}

/* 滑块样式优化 */
:deep(.el-slider__runway) {
  background-color: #f5f5f5;
}

:deep(.el-slider__bar) {
  background-color: #1890ff;
}

:deep(.el-slider__button) {
  border-color: #1890ff;
}

/* 颜色选择器样式优化 */
:deep(.el-color-picker__trigger) {
  border-radius: 6px;
}
</style>