<template>
  <div class="bg-white rounded-3xl p-8 shadow-sm text-center relative overflow-hidden border border-slate-100">
    <div class="absolute top-0 left-0 w-full h-24 bg-gradient-to-r from-blue-500 to-indigo-600"></div>
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
