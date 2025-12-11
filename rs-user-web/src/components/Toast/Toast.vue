<template>
  <div class="fixed top-4 left-1/2 transform -translate-x-1/2 z-[9999] flex flex-col gap-2 pointer-events-none">
    <TransitionGroup
      enter-active-class="transition ease-out duration-300"
      enter-from-class="opacity-0 -translate-y-2"
      enter-to-class="opacity-100 translate-y-0"
      leave-active-class="transition ease-in duration-200"
      leave-from-class="opacity-100 translate-y-0"
      leave-to-class="opacity-0 -translate-y-2"
    >
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="pointer-events-auto min-w-[300px] max-w-md bg-white rounded-lg shadow-lg border-l-4 p-4 flex items-start gap-3"
        :class="{
          'border-green-500': toast.type === 'success',
          'border-red-500': toast.type === 'error',
          'border-yellow-500': toast.type === 'warning',
          'border-blue-500': toast.type === 'info'
        }"
      >
        <!-- Icons based on type -->
        <div class="flex-shrink-0 pt-0.5">
          <svg v-if="toast.type === 'success'" class="w-5 h-5 text-green-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
          <svg v-if="toast.type === 'error'" class="w-5 h-5 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
          <svg v-if="toast.type === 'warning'" class="w-5 h-5 text-yellow-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"></path></svg>
          <svg v-if="toast.type === 'info'" class="w-5 h-5 text-blue-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
        </div>
        
        <div class="flex-1">
          <h3 class="text-sm font-medium text-gray-900" v-if="toast.title">{{ toast.title }}</h3>
          <p class="text-sm text-gray-500" :class="{'mt-1': toast.title}">{{ toast.message }}</p>
        </div>

        <button @click="remove(toast.id)" class="flex-shrink-0 text-gray-400 hover:text-gray-500 focus:outline-none">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"></path></svg>
        </button>
      </div>
    </TransitionGroup>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const toasts = ref([])
let idCounter = 0

const add = (options) => {
  const id = idCounter++
  // Handle string input
  if (typeof options === 'string') {
    options = { message: options }
  }
  
  const toast = {
    id,
    type: options.type || 'info',
    title: options.title,
    message: options.message,
    duration: options.duration || 3000
  }
  
  toasts.value.push(toast)

  if (toast.duration > 0) {
    setTimeout(() => {
      remove(id)
    }, toast.duration)
  }
}

const remove = (id) => {
  const index = toasts.value.findIndex(t => t.id === id)
  if (index !== -1) {
    toasts.value.splice(index, 1)
  }
}

defineExpose({ add, remove })
</script>
