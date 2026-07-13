<template>
  <div class="user-profile-card bg-white rounded-3xl p-8 shadow-sm text-center relative overflow-hidden border border-slate-100">
    <div class="profile-rail absolute top-0 left-0 w-full h-24 bg-[#0B2559]"></div>
    <div class="relative z-10">
      <div class="w-24 h-24 mx-auto rounded-full border-4 border-white shadow-md bg-white flex items-center justify-center text-3xl font-bold text-primary mb-4 overflow-hidden">
        <img v-if="userInfo.avatar" :src="userInfo.avatar" class="w-full h-full object-cover" />
        <span v-else>{{ displayName }}</span>
      </div>
      <h3 class="text-xl font-bold text-slate-900">{{ userInfo.realName || userInfo.username || '用户' }}</h3>
      <p class="text-slate-500 text-sm mt-1">{{ userLevel }}</p>
    </div>
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
      email: '',
      avatar: ''
    })
  },
  userLevel: {
    type: String,
    default: '普通用户'
  }
})

const displayName = computed(() => {
  const name = props.userInfo.realName || props.userInfo.username || 'U'
  return name.charAt(0).toUpperCase()
})
</script>

<style scoped>
.user-profile-card {
  border-color: #dde6f0;
  border-radius: 12px;
  box-shadow: 0 10px 24px rgba(11, 37, 89, 0.06);
}

.profile-rail {
  height: 84px;
  background:
    linear-gradient(135deg, #0b2559, #1677ff),
    repeating-linear-gradient(90deg, transparent 0 18px, rgba(255, 255, 255, 0.18) 18px 19px);
}

.user-profile-card :deep(.text-primary) {
  color: #1677ff;
}
</style>
