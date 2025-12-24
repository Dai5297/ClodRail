<template>
  <div class="bg-white rounded-xl p-6 mb-5 shadow-sm border border-gray-100">
    <h3 class="flex items-center gap-2 text-lg font-medium text-gray-900 mb-6">
      <svg class="w-5 h-5 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path></svg>
      选择支付方式
    </h3>

    <div class="grid gap-4 mb-8">
      <div 
        v-for="method in paymentMethods" 
        :key="method.id"
        class="flex items-center justify-between p-5 border-2 rounded-xl cursor-pointer transition-all duration-200"
        :class="{ 
          'border-blue-500 bg-blue-50/50 shadow-sm': selectedMethod?.id === method.id,
          'border-gray-100 hover:border-blue-200 hover:bg-gray-50': selectedMethod?.id !== method.id && method.available,
          'opacity-60 cursor-not-allowed bg-gray-50': !method.available
        }"
        @click="handleSelectMethod(method)"
      >
        <div class="flex items-center gap-4">
          <div class="w-10 h-10 flex items-center justify-center bg-white rounded-full border border-gray-100 shadow-sm overflow-hidden">
             <!-- Alipay Icon -->
             <svg v-if="method.id === 'alipay'" class="w-full h-full" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
               <rect width="1024" height="1024" fill="#1677FF"/>
               <path d="M239.52 385.6h553.44v69.6H548.64c63.84 96.96 156.48 166.08 274.56 206.4l-38.4 62.4c-134.88-49.44-240-136.32-308.64-249.12l104.16-248.64h-80.64l-68.64 178.56h102.72c-20.16 36-44.64 69.12-72.96 99.84-51.36-43.2-91.2-100.32-114.24-169.44h-73.92c27.84 89.28 80.16 163.68 147.84 218.4-74.4 20.64-152.16 33.6-231.84 38.88l15.84 68.64c89.76-6.24 177.12-21.6 260.16-46.08l-87.84 213.12 70.08 28.8 174.72-421.44z" fill="#FFFFFF"/>
               <path d="M414.24 153.12h198.24v69.6H414.24z" fill="#FFFFFF"/>
             </svg>
             <!-- WeChat Pay Icon -->
             <svg v-else-if="method.id === 'wechat'" class="w-full h-full" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg">
               <rect width="1024" height="1024" fill="#07C160"/>
               <path d="M380 648c-103.8 0-188-75.2-188-168 0-92.8 84.2-168 188-168 103.8 0 188 75.2 188 168 0 92.8-84.2 168-188 168zm0-368c-123.8 0-224 89.6-224 200 0 61.6 31.4 117.2 81.8 155.6l-20.6 59.4 71.4-37c28.6 8 59 12.4 91.4 12.4 123.8 0 224-89.6 224-200S483.8 280 360 280z" fill="#FFFFFF"/>
               <path d="M718 424c-88.4 0-160 64-160 143 0 79 71.6 143 160 143 27.6 0 53.6-6.4 76.8-17.6l60.8 31.4-17.6-50.6c43-32.6 69.8-80 69.8-132.4 0-79-71.6-116.8-190-116.8z" fill="#FFFFFF"/>
             </svg>
             <!-- Default Icon -->
             <svg v-else class="w-6 h-6 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          </div>
          <div>
            <div class="font-medium text-gray-900">{{ method.name }}</div>
            <div class="text-sm text-gray-500 mt-0.5">{{ method.description }}</div>
          </div>
        </div>
        
        <!-- Custom Radio Indicator -->
        <div class="relative">
          <div class="w-5 h-5 rounded-full border-2 flex items-center justify-center transition-colors duration-200"
            :class="selectedMethod?.id === method.id ? 'border-blue-500 bg-blue-500' : 'border-gray-300 bg-white'"
          >
            <div class="w-2 h-2 rounded-full bg-white transform scale-0 transition-transform duration-200"
              :class="{ 'scale-100': selectedMethod?.id === method.id }"
            ></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 支付按钮 -->
    <div class="flex flex-col items-center gap-4">
      <button 
        @click="handlePay"
        :disabled="!selectedMethod || processing"
        class="w-full max-w-md h-12 flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white text-lg font-medium rounded-xl shadow-md hover:shadow-lg focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed disabled:shadow-none transition-all duration-200"
      >
        <svg v-if="processing" class="animate-spin -ml-1 mr-2 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
        </svg>
        <svg v-else class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 10h18M7 15h1m4 0h1m-7 4h12a3 3 0 003-3V8a3 3 0 00-3-3H6a3 3 0 00-3 3v8a3 3 0 003 3z"></path></svg>
        <span>立即支付</span>
        <span class="font-bold">￥{{ totalAmount }}</span>
      </button>
      
      <div class="flex items-center gap-1.5 text-xs text-green-600 bg-green-50 px-3 py-1 rounded-full">
        <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"></path></svg>
        <span>支付环境安全，信息已加密</span>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  paymentMethods: {
    type: Array,
    required: true
  },
  selectedMethod: {
    type: Object,
    default: null
  },
  totalAmount: {
    type: Number,
    required: true
  },
  processing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['select-method', 'pay'])

const handleSelectMethod = (method) => {
  if (method.available) {
    emit('select-method', method)
  }
}

const handlePay = () => {
  emit('pay')
}
</script>
