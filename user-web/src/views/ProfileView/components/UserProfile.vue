<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { 
  ElCard, 
  ElForm, 
  ElFormItem, 
  ElInput, 
  ElSelect, 
  ElOption, 
  ElDatePicker, 
  ElButton, 
  ElAvatar, 
  ElUpload, 
  ElMessage, 
  ElMessageBox, 
  ElSkeleton,
  ElIcon,
  ElDivider
} from 'element-plus'
import { 
  Edit,
  Camera,
  User,
  Phone,
  Message,
  Calendar,
  Location
} from '@element-plus/icons-vue'
import type { UploadProps, FormInstance, FormRules } from 'element-plus'

// 定义用户信息接口
interface UserInfo {
  id: string
  username: string
  nickname: string
  avatar: string
  email: string
  phone: string
  gender: 'male' | 'female' | 'other'
  birthday: string
  realName: string
  idCard: string
  address: string
  bio: string
  registrationTime: string
  lastLoginTime: string
  isVerified: boolean
}

// 定义属性
interface Props {
  userInfo: UserInfo | null
  loading: boolean
}

// 定义事件
interface Emits {
  updateProfile: [data: Partial<UserInfo>]
  uploadAvatar: [file: File]
}

const props = withDefaults(defineProps<Props>(), {
  userInfo: null,
  loading: false
})

const emit = defineEmits<Emits>()

// 表单引用
const formRef = ref<FormInstance>()

// 编辑状态
const isEditing = ref(false)

// 表单数据
const formData = reactive<Partial<UserInfo>>({})

// 性别选项
const genderOptions = [
  { label: '男', value: 'male' },
  { label: '女', value: 'female' },
  { label: '其他', value: 'other' }
]

// 表单验证规则
const rules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  realName: [
    { min: 2, max: 10, message: '真实姓名长度在 2 到 10 个字符', trigger: 'blur' }
  ],
  idCard: [
    { pattern: /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/, message: '请输入正确的身份证号码', trigger: 'blur' }
  ],
  bio: [
    { max: 200, message: '个人简介不能超过 200 个字符', trigger: 'blur' }
  ]
}

// 计算属性
const displayUserInfo = computed(() => props.userInfo || {} as UserInfo)

// 格式化时间
const formatTime = (time: string) => {
  if (!time) return '-'
  return new Date(time).toLocaleString('zh-CN')
}

// 格式化生日
const formatBirthday = (birthday: string) => {
  if (!birthday) return '-'
  return new Date(birthday).toLocaleDateString('zh-CN')
}

// 获取性别显示文本
const getGenderText = (gender: string) => {
  const option = genderOptions.find(opt => opt.value === gender)
  return option?.label || '-'
}

// 脱敏处理身份证号
const maskIdCard = (idCard: string) => {
  if (!idCard) return '-'
  return idCard.replace(/(\d{6})\d{8}(\d{4})/, '$1********$2')
}

// 脱敏处理手机号
const maskPhone = (phone: string) => {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

// 开始编辑
const startEdit = () => {
  if (!props.userInfo) return
  
  // 复制用户信息到表单数据
  Object.assign(formData, {
    nickname: props.userInfo.nickname,
    email: props.userInfo.email,
    phone: props.userInfo.phone,
    gender: props.userInfo.gender,
    birthday: props.userInfo.birthday,
    realName: props.userInfo.realName,
    idCard: props.userInfo.idCard,
    address: props.userInfo.address,
    bio: props.userInfo.bio
  })
  
  isEditing.value = true
}

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false
  formRef.value?.resetFields()
}

// 保存编辑
const saveEdit = async () => {
  if (!formRef.value) return
  
  try {
    await formRef.value.validate()
    emit('updateProfile', { ...formData })
    isEditing.value = false
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

// 头像上传前检查
const beforeAvatarUpload: UploadProps['beforeUpload'] = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理头像上传
const handleAvatarUpload = (file: File) => {
  emit('uploadAvatar', file)
  return false // 阻止自动上传
}

// 确认删除头像
const confirmDeleteAvatar = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要删除当前头像吗？',
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    emit('updateProfile', { avatar: '' })
    ElMessage.success('头像删除成功')
  } catch {
    // 用户取消删除
  }
}

// 处理上传请求
const handleUploadRequest = () => {
  return new Promise((resolve: (value: any) => void) => resolve({}))
}
</script>

<template>
  <div class="user-profile">
    <ElCard class="profile-card" shadow="hover">
      <!-- 卡片头部 -->
      <template #header>
        <div class="card-header">
          <div class="header-left">
            <ElIcon :size="20" color="#1890ff">
              <User />
            </ElIcon>
            <span class="header-title">个人信息</span>
          </div>
          <div class="header-right">
            <ElButton 
              v-if="!isEditing" 
              type="primary" 
              :icon="Edit" 
              @click="startEdit"
              :loading="loading"
            >
              编辑资料
            </ElButton>
            <div v-else class="edit-actions">
              <ElButton @click="cancelEdit">取消</ElButton>
              <ElButton type="primary" @click="saveEdit" :loading="loading">
                保存
              </ElButton>
            </div>
          </div>
        </div>
      </template>
      
      <!-- 加载状态 -->
      <ElSkeleton v-if="loading" animated>
        <template #template>
          <div class="profile-skeleton">
            <div class="avatar-skeleton">
              <el-skeleton-item variant="circle" style="width: 100px; height: 100px;" />
            </div>
            <div class="info-skeleton">
              <el-skeleton-item v-for="i in 8" :key="i" variant="text" style="width: 200px; margin-bottom: 16px;" />
            </div>
          </div>
        </template>
      </ElSkeleton>
      
      <!-- 个人信息内容 -->
      <div v-else class="profile-content">
        <!-- 头像区域 -->
        <div class="avatar-section">
          <div class="avatar-container">
            <ElAvatar 
              :size="100" 
              :src="displayUserInfo.avatar" 
              class="user-avatar"
            >
              <ElIcon :size="40">
                <User />
              </ElIcon>
            </ElAvatar>
            
            <!-- 头像操作 -->
            <div class="avatar-actions">
              <ElUpload
                :show-file-list="false"
                :before-upload="beforeAvatarUpload"
                :http-request="handleUploadRequest"
                accept="image/*"
                @change="(file: any) => handleAvatarUpload(file.raw)"
              >
                <ElButton size="small" :icon="Camera" circle title="更换头像" />
              </ElUpload>
              
              <ElButton 
                v-if="displayUserInfo.avatar"
                size="small" 
                type="danger" 
                circle 
                title="删除头像"
                @click="confirmDeleteAvatar"
              >
                <ElIcon><Delete /></ElIcon>
              </ElButton>
            </div>
          </div>
          
          <!-- 用户基本信息 -->
          <div class="basic-info">
            <h3 class="username">{{ displayUserInfo.nickname || displayUserInfo.username }}</h3>
            <p class="user-id">ID: {{ displayUserInfo.id }}</p>
            <div class="verification-status">
              <ElIcon 
                :color="displayUserInfo.isVerified ? '#52c41a' : '#faad14'"
                :size="16"
              >
                <component :is="displayUserInfo.isVerified ? 'CircleCheck' : 'Warning'" />
              </ElIcon>
              <span :class="['status-text', displayUserInfo.isVerified ? 'verified' : 'unverified']">
                {{ displayUserInfo.isVerified ? '已实名认证' : '未实名认证' }}
              </span>
            </div>
          </div>
        </div>
        
        <ElDivider />
        
        <!-- 详细信息 -->
        <div class="detail-info">
          <!-- 非编辑状态 -->
          <div v-if="!isEditing" class="info-display">
            <div class="info-grid">
              <div class="info-item">
                <label class="info-label">
                  <ElIcon><User /></ElIcon>
                  昵称
                </label>
                <span class="info-value">{{ displayUserInfo.nickname || '-' }}</span>
              </div>
              
              <div class="info-item">
                <label class="info-label">
                  <ElIcon><Message /></ElIcon>
                  邮箱
                </label>
                <span class="info-value">{{ displayUserInfo.email || '-' }}</span>
              </div>
              
              <div class="info-item">
                <label class="info-label">
                  <ElIcon><Phone /></ElIcon>
                  手机号
                </label>
                <span class="info-value">{{ maskPhone(displayUserInfo.phone) }}</span>
              </div>
              
              <div class="info-item">
                <label class="info-label">
                  <ElIcon><User /></ElIcon>
                  性别
                </label>
                <span class="info-value">{{ getGenderText(displayUserInfo.gender) }}</span>
              </div>
              
              <div class="info-item">
                <label class="info-label">
                  <ElIcon><Calendar /></ElIcon>
                  生日
                </label>
                <span class="info-value">{{ formatBirthday(displayUserInfo.birthday) }}</span>
              </div>
              
              <div class="info-item">
                <label class="info-label">
                  <ElIcon><User /></ElIcon>
                  真实姓名
                </label>
                <span class="info-value">{{ displayUserInfo.realName || '-' }}</span>
              </div>
              
              <div class="info-item">
                <label class="info-label">
                  <ElIcon><Document /></ElIcon>
                  身份证号
                </label>
                <span class="info-value">{{ maskIdCard(displayUserInfo.idCard) }}</span>
              </div>
              
              <div class="info-item">
                <label class="info-label">
                  <ElIcon><Location /></ElIcon>
                  地址
                </label>
                <span class="info-value">{{ displayUserInfo.address || '-' }}</span>
              </div>
              
              <div class="info-item full-width">
                <label class="info-label">
                  <ElIcon><Edit /></ElIcon>
                  个人简介
                </label>
                <span class="info-value">{{ displayUserInfo.bio || '-' }}</span>
              </div>
            </div>
            
            <!-- 账户信息 -->
            <ElDivider content-position="left">账户信息</ElDivider>
            <div class="account-info">
              <div class="info-item">
                <label class="info-label">注册时间</label>
                <span class="info-value">{{ formatTime(displayUserInfo.registrationTime) }}</span>
              </div>
              <div class="info-item">
                <label class="info-label">最后登录</label>
                <span class="info-value">{{ formatTime(displayUserInfo.lastLoginTime) }}</span>
              </div>
            </div>
          </div>
          
          <!-- 编辑状态 -->
          <ElForm 
            v-else 
            ref="formRef" 
            :model="formData" 
            :rules="rules" 
            label-width="100px"
            class="edit-form"
          >
            <div class="form-grid">
              <ElFormItem label="昵称" prop="nickname">
                <ElInput v-model="formData.nickname" placeholder="请输入昵称" />
              </ElFormItem>
              
              <ElFormItem label="邮箱" prop="email">
                <ElInput v-model="formData.email" placeholder="请输入邮箱地址" />
              </ElFormItem>
              
              <ElFormItem label="手机号" prop="phone">
                <ElInput v-model="formData.phone" placeholder="请输入手机号码" />
              </ElFormItem>
              
              <ElFormItem label="性别" prop="gender">
                <ElSelect v-model="formData.gender" placeholder="请选择性别">
                  <ElOption 
                    v-for="option in genderOptions" 
                    :key="option.value" 
                    :label="option.label" 
                    :value="option.value" 
                  />
                </ElSelect>
              </ElFormItem>
              
              <ElFormItem label="生日" prop="birthday">
                <ElDatePicker 
                  v-model="formData.birthday" 
                  type="date" 
                  placeholder="请选择生日" 
                  style="width: 100%;"
                />
              </ElFormItem>
              
              <ElFormItem label="真实姓名" prop="realName">
                <ElInput v-model="formData.realName" placeholder="请输入真实姓名" />
              </ElFormItem>
              
              <ElFormItem label="身份证号" prop="idCard">
                <ElInput v-model="formData.idCard" placeholder="请输入身份证号码" />
              </ElFormItem>
              
              <ElFormItem label="地址" prop="address">
                <ElInput v-model="formData.address" placeholder="请输入地址" />
              </ElFormItem>
            </div>
            
            <ElFormItem label="个人简介" prop="bio" class="full-width">
              <ElInput 
                v-model="formData.bio" 
                type="textarea" 
                :rows="4" 
                placeholder="请输入个人简介" 
                maxlength="200"
                show-word-limit
              />
            </ElFormItem>
          </ElForm>
        </div>
      </div>
    </ElCard>
  </div>
</template>

<style scoped>
.user-profile {
  max-width: 800px;
}

.profile-card {
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

.edit-actions {
  display: flex;
  gap: 8px;
}

.profile-skeleton {
  display: flex;
  gap: 32px;
}

.avatar-skeleton {
  flex-shrink: 0;
}

.info-skeleton {
  flex: 1;
}

.profile-content {
  padding: 8px 0;
}

.avatar-section {
  display: flex;
  gap: 32px;
  align-items: flex-start;
}

.avatar-container {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  border: 3px solid #f0f0f0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-actions {
  position: absolute;
  bottom: -8px;
  right: -8px;
  display: flex;
  gap: 4px;
}

.basic-info {
  flex: 1;
}

.username {
  margin: 0 0 8px 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.user-id {
  margin: 0 0 12px 0;
  color: #8c8c8c;
  font-size: 14px;
}

.verification-status {
  display: flex;
  align-items: center;
  gap: 6px;
}

.status-text {
  font-size: 14px;
  font-weight: 500;
}

.status-text.verified {
  color: #52c41a;
}

.status-text.unverified {
  color: #faad14;
}

.detail-info {
  margin-top: 8px;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px 32px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info-item.full-width {
  grid-column: 1 / -1;
}

.info-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  color: #595959;
}

.info-value {
  font-size: 15px;
  color: #262626;
  word-break: break-all;
}

.account-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px 32px;
  margin-top: 16px;
}

.edit-form {
  margin-top: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px 32px;
}

.full-width {
  grid-column: 1 / -1;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .avatar-section {
    flex-direction: column;
    align-items: center;
    gap: 20px;
    text-align: center;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .account-info {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}

@media (max-width: 480px) {
  .card-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .header-right {
    display: flex;
    justify-content: center;
  }
  
  .username {
    font-size: 20px;
  }
}
</style>