<template>
  <div class="bg-white rounded-xl shadow-sm p-8 border border-gray-100">
    <div class="mb-10 last:mb-0">
      <h3 class="text-lg font-semibold text-gray-900 border-b-2 border-gray-100 pb-3 mb-6 flex items-center gap-2">
        <svg class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
        基本信息
      </h3>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-x-8 gap-y-6">
        <div class="flex flex-col gap-1.5">
          <label class="text-sm font-medium text-gray-500">用户名</label>
          <div class="text-gray-900 font-medium">{{ userInfo.username || '-' }}</div>
        </div>
        <div class="flex flex-col gap-1.5">
          <label class="text-sm font-medium text-gray-500">真实姓名</label>
          <div class="text-gray-900 font-medium">{{ userInfo.realName || '-' }}</div>
        </div>
        <div class="flex flex-col gap-1.5">
          <label class="text-sm font-medium text-gray-500">手机号</label>
          <div class="text-gray-900 font-medium">{{ userInfo.phone || '-' }}</div>
        </div>
        <div class="flex flex-col gap-1.5">
          <label class="text-sm font-medium text-gray-500">邮箱</label>
          <div class="text-gray-900 font-medium">{{ userInfo.email || '-' }}</div>
        </div>
        <div class="flex flex-col gap-1.5">
          <label class="text-sm font-medium text-gray-500">性别</label>
          <div class="text-gray-900 font-medium">{{ userInfo.gender || '-' }}</div>
        </div>
        <div class="flex flex-col gap-1.5">
          <label class="text-sm font-medium text-gray-500">生日</label>
          <div class="text-gray-900 font-medium">{{ userInfo.birthday || '-' }}</div>
        </div>
        <div class="flex flex-col gap-1.5">
          <label class="text-sm font-medium text-gray-500">身份证号</label>
          <div class="text-gray-900 font-medium">{{ formatIdCard(userInfo.idCard) || '-' }}</div>
        </div>
        <div class="flex flex-col gap-1.5 md:col-span-2">
          <label class="text-sm font-medium text-gray-500">地址</label>
          <div class="text-gray-900 font-medium">{{ userInfo.address || '-' }}</div>
        </div>
      </div>
    </div>
    
    <div v-if="userInfo.introduction" class="mb-0">
      <h3 class="text-lg font-semibold text-gray-900 border-b-2 border-gray-100 pb-3 mb-6 flex items-center gap-2">
        <svg class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"></path></svg>
        个人简介
      </h3>
      <div class="bg-gray-5 rounded-lg p-6 text-gray-700 leading-relaxed border border-gray-100">
        {{ userInfo.introduction }}
      </div>
    </div>
  </div>
</template>

<script setup>
// Props
defineProps({
  userInfo: {
    type: Object,
    default: () => ({})
  }
})

// 格式化身份证号（隐藏中间部分）
const formatIdCard = (idCard) => {
  if (!idCard) return ''
  if (idCard.length === 18) {
    return idCard.substring(0, 6) + '********' + idCard.substring(14)
  }
  return idCard
}
</script>