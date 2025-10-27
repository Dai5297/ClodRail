<template>
  <div class="user-profile">
    <div class="user-avatar">{{ displayName }}</div>
    <div class="user-name">{{ userInfo.realName || '用户' }}</div>
    <div class="user-level">{{ userLevel }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  userInfo: {
    type: Object,
    default: () => ({
      realName: '',
      username: '',
      phone: '',
      email: ''
    })
  },
  userLevel: {
    type: String,
    default: '普通用户'
  }
})

const displayName = computed(() => {
  return props.userInfo.realName?.charAt(0) || 'U'
})
</script>

<style scoped>
.user-profile {
  text-align: center;
  margin-bottom: 24px;
  padding-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.user-avatar {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #1890FF, #096dd9);
  margin: 0 auto 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
}

.user-name {
  font-size: 18px;
  font-weight: 600;
  color: #262626;
  margin-bottom: 8px;
}

.user-level {
  font-size: 14px;
  color: #8c8c8c;
  background: #f0f0f0;
  padding: 4px 12px;
  border-radius: 12px;
  display: inline-block;
}
</style>