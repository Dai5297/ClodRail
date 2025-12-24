<template>
  <div class="bg-gray-50 p-4 rounded-xl border border-gray-100 mb-6">
    <div class="flex flex-col sm:flex-row gap-4 items-end sm:items-center flex-wrap">
      <!-- Name -->
      <div class="w-full sm:w-auto flex flex-col gap-1.5">
        <label class="text-sm font-medium text-gray-700">姓名</label>
        <div class="relative">
           <input
            v-model="searchForm.name"
            type="text"
            placeholder="请输入姓名"
            class="w-full sm:w-48 pl-9 pr-3 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm transition-shadow"
          />
          <div class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
             <svg class="h-4 w-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"></path></svg>
          </div>
        </div>
      </div>
      
      <!-- Passenger Type -->
      <div class="w-full sm:w-auto flex flex-col gap-1.5">
        <label class="text-sm font-medium text-gray-700">乘客类型</label>
        <div class="relative">
          <select
            v-model="searchForm.passengerType"
            class="w-full sm:w-40 appearance-none pl-3 pr-8 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm bg-white cursor-pointer transition-shadow"
          >
            <option :value="null">全部类型</option>
            <option :value="1">成人</option>
            <option :value="2">儿童</option>
            <option :value="3">学生</option>
            <option :value="4">老人</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-500">
             <svg class="fill-current h-4 w-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z"/></svg>
          </div>
        </div>
      </div>
      
      <!-- Status -->
      <div class="w-full sm:w-auto flex flex-col gap-1.5">
        <label class="text-sm font-medium text-gray-700">状态</label>
        <div class="relative">
          <select
            v-model="searchForm.status"
            class="w-full sm:w-32 appearance-none pl-3 pr-8 py-2 border border-gray-300 rounded-lg shadow-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 text-sm bg-white cursor-pointer transition-shadow"
          >
            <option :value="null">全部状态</option>
            <option :value="1">启用</option>
            <option :value="0">禁用</option>
          </select>
          <div class="pointer-events-none absolute inset-y-0 right-0 flex items-center px-2 text-gray-500">
             <svg class="fill-current h-4 w-4" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20"><path d="M9.293 12.95l.707.707L15.657 8l-1.414-1.414L10 10.828 5.757 6.586 4.343 8z"/></svg>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="flex gap-3 mt-2 sm:mt-0 sm:ml-auto w-full sm:w-auto">
        <button
          @click="handleSearch"
          class="flex-1 sm:flex-none inline-flex justify-center items-center px-4 py-2 border border-transparent text-sm font-medium rounded-lg shadow-sm text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all active:scale-95"
        >
          <svg class="mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          搜索
        </button>
        <button
          @click="handleReset"
          class="flex-1 sm:flex-none inline-flex justify-center items-center px-4 py-2 border border-gray-300 text-sm font-medium rounded-lg text-gray-700 bg-white hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 transition-all active:scale-95"
        >
          <svg class="mr-2 h-4 w-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
          </svg>
          重置
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  searchForm: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['search', 'reset'])

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  emit('reset')
}
</script>
