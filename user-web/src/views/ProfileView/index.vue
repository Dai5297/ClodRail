<script setup lang="ts">
// ProfileView - 个人中心页面主文件
import { ref, onMounted, computed, withDefaults } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElButton, ElIcon } from 'element-plus'
import { User } from '@element-plus/icons-vue'

// 导入子组件
import UserProfile from './components/UserProfile.vue'
import AccountSecurity from './components/AccountSecurity.vue'
import PersonalSettings from './components/PersonalSettings.vue'
import OrderHistory from './components/OrderHistory.vue'
import PointsInfo from './components/PointsInfo.vue'
import ProfileTabs from './components/ProfileTabs.vue'
import {getUserInfo, updateUserInfo} from "@/api/auth.ts";

// 定义用户信息接口
interface UserInfo {
  id: string,
  username: string,
  birthday: string,
  address: string,
  introduction: string,
  icon: string,
  email: string,
  phone: string,
  realName: string,
  gender: string,
  idCard: string,
  createTime: string,
  lastLoginTime: string,
}

interface UserInfoReq {
  id: string,
  username: string,
  birthday: string,
  address: string,
  introduction: string,
  icon: string,
  email: string,
  phone: string,
  realName: string,
  gender: string,
  idCard: string,
}

// 定义属性
interface Props {
  userId?: string
}

const props = withDefaults(defineProps<Props>(), {
  userId: ''
})

// 页面状态
const loading = ref(false)
const activeTab = ref('profile')
const route = useRoute()
const router = useRouter()

// 用户信息
const userInfo = ref<UserInfo>({
  id: '',
  username: '',
  birthday: '',
  address: '',
  introduction: '',
  icon: '',
  email: '',
  phone: '',
  realName: '',
  gender: '',
  idCard: '',
  createTime: '',
  lastLoginTime: '',
})

// 积分信息
const pointsInfo = ref({
  totalPoints: 2580,
  availablePoints: 2580,
  usedPoints: 1420,
  expiredPoints: 200,
  expiringPoints: 300,
  expiringDate: '2024-12-31',
  level: 'gold',
  levelProgress: 65,
  nextLevelPoints: 5000,
  thisYearEarned: 1800,
  thisYearSpent: 1420
})

// 积分记录
const pointsRecords = ref([])
const pointsTotal = ref(0)

// 订单信息
const orders = ref([])
const ordersTotal = ref(0)

// 个人设置
const personalSettings = ref({
  // 通知设置
  emailNotification: true,
  smsNotification: false,
  pushNotification: true,
  orderNotification: true,
  promotionNotification: false,
  systemNotification: true,

  // 界面设置
  theme: 'light' as 'light' | 'dark' | 'auto',
  language: 'zh-CN' as 'zh-CN' | 'en-US',
  fontSize: 14,
  primaryColor: '#1890ff',

  // 隐私设置
  profileVisible: true,
  onlineStatus: true,
  dataCollection: false,

  // 功能设置
  autoLogin: true,
  rememberPassword: true,
  quickBooking: true,

  // 提醒设置
  departureReminder: true,
  reminderTime: '30',
  reminderMethod: 'push' as 'email' | 'sms' | 'push'
})

// 安全信息
const securityInfo = ref({
  hasPassword: true,
  phoneVerified: true,
  emailVerified: false,
  realNameVerified: true,
  phone: '138****8888',
  email: 'user@example.com',
  realName: '张三',
  idCard: '110101199001011234',
  lastPasswordChange: '2024-01-01T10:00:00',
  loginDevices: [
    {
      id: '1',
      deviceName: 'Windows PC - Chrome',
      location: '北京市朝阳区',
      lastLogin: '2024-01-15T14:20:00',
      isCurrent: true
    },
    {
      id: '2',
      deviceName: 'iPhone 15 - Safari',
      location: '北京市海淀区',
      lastLogin: '2024-01-14T09:30:00',
      isCurrent: false
    }
  ]
})

// 标签页配置
const tabConfig = [
  { key: 'profile', label: '个人资料', icon: 'User' },
  { key: 'security', label: '账户安全', icon: 'Lock' },
  { key: 'settings', label: '个人设置', icon: 'Setting' },
  { key: 'orders', label: '订单记录', icon: 'Document' },
  { key: 'points', label: '积分信息', icon: 'Star' }
]

// 页面初始化
onMounted(async () => {
  console.log('ProfileView mounted')
  await loadUserInfo()

  // 从路由参数获取活动标签
  const tab = route.query.tab as string
  if (tab && tabConfig.some(t => t.key === tab)) {
    activeTab.value = tab
  }
})

// 加载用户信息
const loadUserInfo = async () => {
  try {
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 这里应该调用实际的API
    const response = await getUserInfo()
    userInfo.value = response.data

    console.log('用户信息加载完成:', userInfo.value)
  } catch (error) {
    console.error('加载用户信息失败:', error)
    ElMessage.error('加载用户信息失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理标签切换
const handleTabChange = (tabKey: string) => {
  activeTab.value = tabKey

  // 更新路由查询参数
  router.push({
    query: { ...route.query, tab: tabKey }
  })
}

// 处理用户信息更新
const handleUserInfoUpdate = async (updatedInfo: Partial<UserInfoReq>) => {
  try {
    loading.value = true

    // 这里应该调用实际的API
    const res = await updateUserInfo(updatedInfo);

    console.log(res)
    if (res.code === 200) {
      ElMessage.success('个人信息更新成功')
      userInfo.value = updatedInfo
    }else {
      ElMessage.error('更新失败,' + res.data)
    }
  } catch (error) {
    console.error('更新用户信息失败:', error)
    ElMessage.error('更新失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理头像上传
const handleAvatarUpload = async (file: File) => {
  try {
    loading.value = true

    // 模拟文件上传
    await new Promise(resolve => setTimeout(resolve, 2000))

    // 这里应该调用实际的上传API
    // const response = await uploadAvatar(file)
    // userInfo.value.avatar = response.data.url

    // 模拟上传成功
    const reader = new FileReader()
    reader.onload = (e) => {
      userInfo.value.avatar = e.target?.result as string
    }
    reader.readAsDataURL(file)

    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    ElMessage.error('头像上传失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理积分记录加载
const handleLoadPointsRecords = async (params: any) => {
  try {
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 这里应该调用实际的API
    // const response = await getPointsRecords(params)
    // pointsRecords.value = response.data.records
    // pointsTotal.value = response.data.total

    console.log('积分记录加载完成')
  } catch (error) {
    console.error('加载积分记录失败:', error)
    ElMessage.error('加载积分记录失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理跳转积分商城
const handleGoToMall = () => {
  router.push('/points-mall')
}

// 处理订单记录加载
const handleLoadOrders = async (params: any) => {
  try {
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 这里应该调用实际的API
    // const response = await getOrders(params)
    // orders.value = response.data.orders
    // ordersTotal.value = response.data.total

    console.log('订单记录加载完成')
  } catch (error) {
    console.error('加载订单记录失败:', error)
    ElMessage.error('加载订单记录失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理查看订单详情
const handleViewOrder = (orderId: string) => {
  router.push(`/order/${orderId}`)
}

// 处理下载车票
const handleDownloadTicket = async (orderId: string) => {
  try {
    // 模拟下载
    await new Promise(resolve => setTimeout(resolve, 1000))
    ElMessage.success('车票下载成功')
  } catch (error) {
    console.error('下载车票失败:', error)
    ElMessage.error('下载车票失败，请重试')
  }
}

// 处理个人设置更新
const handleUpdateSettings = async (settings: any) => {
  try {
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 这里应该调用实际的API
    // await updatePersonalSettings(settings)

    // 更新本地数据
    Object.assign(personalSettings.value, settings)

    ElMessage.success('设置更新成功')
  } catch (error) {
    console.error('更新设置失败:', error)
    ElMessage.error('更新设置失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理重置设置
const handleResetSettings = async () => {
  try {
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 重置为默认设置
    Object.assign(personalSettings.value, {
      // 通知设置
      emailNotification: true,
      smsNotification: false,
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
      autoLogin: true,
      rememberPassword: true,
      quickBooking: true,

      // 提醒设置
      departureReminder: true,
      reminderTime: '30',
      reminderMethod: 'push'
    })

    ElMessage.success('设置已重置为默认值')
  } catch (error) {
    console.error('重置设置失败:', error)
    ElMessage.error('重置设置失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理安全相关操作
const handleChangePassword = async (data: { oldPassword: string; newPassword: string }) => {
  try {
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 这里应该调用实际的API
    // await changePassword(data)

    securityInfo.value.lastPasswordChange = new Date().toISOString()

    ElMessage.success('密码修改成功')
  } catch (error) {
    console.error('修改密码失败:', error)
    ElMessage.error('修改密码失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleBindPhone = async (data: { phone: string; code: string }) => {
  try {
    loading.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    securityInfo.value.phone = data.phone
    securityInfo.value.phoneVerified = true

    ElMessage.success('手机号绑定成功')
  } catch (error) {
    console.error('绑定手机号失败:', error)
    ElMessage.error('绑定手机号失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleBindEmail = async (data: { email: string; code: string }) => {
  try {
    loading.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    securityInfo.value.email = data.email
    securityInfo.value.emailVerified = true

    ElMessage.success('邮箱绑定成功')
  } catch (error) {
    console.error('绑定邮箱失败:', error)
    ElMessage.error('绑定邮箱失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleRealNameAuth = async (data: { realName: string; idCard: string }) => {
  try {
    loading.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    securityInfo.value.realName = data.realName
    securityInfo.value.idCard = data.idCard
    securityInfo.value.realNameVerified = true

    ElMessage.success('实名认证成功')
  } catch (error) {
    console.error('实名认证失败:', error)
    ElMessage.error('实名认证失败，请重试')
  } finally {
    loading.value = false
  }
}

const handleSendVerifyCode = async (type: 'phone' | 'email', target: string) => {
  try {
    await new Promise(resolve => setTimeout(resolve, 500))
    ElMessage.success(`验证码已发送至${type === 'phone' ? '手机' : '邮箱'}`)
  } catch (error) {
    console.error('发送验证码失败:', error)
    ElMessage.error('发送验证码失败，请重试')
  }
}

const handleLogoutDevice = async (deviceId: string) => {
  try {
    loading.value = true

    await new Promise(resolve => setTimeout(resolve, 1000))

    securityInfo.value.loginDevices = securityInfo.value.loginDevices.filter(device => device.id !== deviceId)

    ElMessage.success('设备已下线')
  } catch (error) {
    console.error('设备下线失败:', error)
    ElMessage.error('设备下线失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理密码修改
const handlePasswordChange = async (passwordData: any) => {
  try {
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 这里应该调用实际的API
    // await changePassword(passwordData)

    ElMessage.success('密码修改成功')
  } catch (error) {
    console.error('密码修改失败:', error)
    ElMessage.error('密码修改失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理手机号绑定
const handlePhoneBind = async (phoneData: any) => {
  try {
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 这里应该调用实际的API
    // await bindPhone(phoneData)

    userInfo.value.phone = phoneData.phone
    ElMessage.success('手机号绑定成功')
  } catch (error) {
    console.error('手机号绑定失败:', error)
    ElMessage.error('手机号绑定失败，请重试')
  } finally {
    loading.value = false
  }
}

// 处理邮箱绑定
const handleEmailBind = async (emailData: any) => {
  try {
    loading.value = true

    // 模拟API调用
    await new Promise(resolve => setTimeout(resolve, 1000))

    // 这里应该调用实际的API
    // await bindEmail(emailData)

    userInfo.value.email = emailData.email
    ElMessage.success('邮箱绑定成功')
  } catch (error) {
    console.error('邮箱绑定失败:', error)
    ElMessage.error('邮箱绑定失败，请重试')
  } finally {
    loading.value = false
  }
}

// 刷新页面数据
const refreshData = () => {
  loadUserInfo()
}
</script>

<template>
  <div class="profile-view">
    <!-- 页面头部 -->
    <div class="profile-header">
      <div class="header-content">
        <ElIcon :size="24" color="#1890ff">
          <User />
        </ElIcon>
        <div class="header-text">
          <h1 class="page-title">个人中心</h1>
          <p class="page-subtitle">管理您的个人信息和账户设置</p>
        </div>
      </div>

      <div class="header-actions">
        <ElButton
          type="primary"
          :loading="loading"
          @click="refreshData"
        >
          刷新数据
        </ElButton>
      </div>
    </div>

    <!-- 标签页导航 -->
    <ProfileTabs
      :active-tab="activeTab"
      :tab-config="tabConfig"
      :loading="loading"
      @tab-change="handleTabChange"
    />

    <!-- 标签页内容 -->
    <div class="profile-content">
      <!-- 个人资料 -->
      <UserProfile
        v-if="activeTab === 'profile'"
        :user-info="userInfo"
        :loading="loading"
        @updateProfile="handleUserInfoUpdate"
        @upload-avatar="handleAvatarUpload"
      />

      <!-- 账户安全 -->
      <AccountSecurity
        v-else-if="activeTab === 'security'"
        :security-info="securityInfo"
        :loading="loading"
        @change-password="handleChangePassword"
        @bind-phone="handleBindPhone"
        @bind-email="handleBindEmail"
        @real-name-auth="handleRealNameAuth"
        @send-verify-code="handleSendVerifyCode"
        @logout-device="handleLogoutDevice"
      />

      <!-- 个人设置 -->
      <PersonalSettings
        v-else-if="activeTab === 'settings'"
        :settings="personalSettings"
        :loading="loading"
        @update-settings="handleUpdateSettings"
        @reset-settings="handleResetSettings"
      />

      <!-- 订单记录 -->
      <OrderHistory
        v-else-if="activeTab === 'orders'"
        :orders="orders"
        :total="ordersTotal"
        :loading="loading"
        @load-orders="handleLoadOrders"
        @view-order="handleViewOrder"
        @download-ticket="handleDownloadTicket"
      />

      <!-- 积分信息 -->
      <PointsInfo
        v-else-if="activeTab === 'points'"
        :points-info="pointsInfo"
        :points-records="pointsRecords"
        :total="pointsTotal"
        :loading="loading"
        @load-records="handleLoadPointsRecords"
        @go-to-mall="handleGoToMall"
      />
    </div>
  </div>
</template>

<style scoped>
.profile-view {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 0;
}

.profile-header {
  background: white;
  padding: 24px 32px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  line-height: 1.2;
}

.page-subtitle {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
  font-weight: 500;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.profile-content {
  padding: 24px 32px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-header {
    padding: 16px 20px;
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-content {
    width: 100%;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .page-title {
    font-size: 20px;
  }

  .page-subtitle {
    font-size: 13px;
  }

  .profile-content {
    padding: 16px 20px;
  }
}
</style>
