<template>
  <div class="bg-white rounded-xl p-8 mb-6 shadow-sm border border-gray-100">
    <div class="flex items-center gap-5 mb-8 pb-5 border-b border-gray-100">
      <div class="text-3xl font-bold text-blue-600">{{ ticketDetail.trainCode }}</div>
      <div class="bg-blue-50 text-blue-600 px-3 py-1 rounded-full text-xs font-medium">{{ ticketDetail.trainType }}</div>
      <div class="text-gray-500 text-base">{{ formatDate(ticketDetail.departureDate) }}</div>
    </div>

    <div class="grid grid-cols-[1fr_auto_1fr] gap-10 items-center max-w-4xl mx-auto">
      <!-- Departure -->
      <div class="text-left">
        <div class="mb-2">
          <div class="text-4xl font-bold text-gray-900 leading-none mb-1">{{ formatTime(ticketDetail.startTime) }}</div>
          <div class="text-xs text-gray-400">{{ formatDate(ticketDetail.startTime) }}</div>
        </div>
        <div class="text-xl text-gray-800 mb-1 font-medium">{{ ticketDetail.originStation.name }}</div>
        <div class="text-sm text-gray-500">{{ ticketDetail.originStation.platform }}站台</div>
      </div>

      <!-- Journey Info -->
      <div class="flex flex-col items-center gap-3 w-full">
        <div class="flex items-center gap-1.5 text-gray-500 text-sm">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
          <span>{{ formatDuration(ticketDetail.duration) }}</span>
        </div>
        <div class="flex flex-col items-center w-full gap-1">
          <div class="w-32 h-0.5 bg-gradient-to-r from-blue-500 to-green-500 rounded-full relative">
            <div class="absolute left-0 top-1/2 -translate-y-1/2 w-2 h-2 bg-blue-500 rounded-full border-2 border-white"></div>
            <div class="absolute right-0 top-1/2 -translate-y-1/2 w-2 h-2 bg-green-500 rounded-full border-2 border-white"></div>
          </div>
          <div class="text-xs text-gray-400">{{ ticketDetail.stopCount }}站</div>
        </div>
      </div>

      <!-- Arrival -->
      <div class="text-right">
        <div class="mb-2">
          <div class="text-4xl font-bold text-gray-900 leading-none mb-1">{{ formatTime(ticketDetail.endTime) }}</div>
          <div class="text-xs text-gray-400">{{ formatDate(ticketDetail.endTime) }}</div>
        </div>
        <div class="text-xl text-gray-800 mb-1 font-medium">{{ ticketDetail.destinationStation.name }}</div>
        <div class="text-sm text-gray-500">{{ ticketDetail.destinationStation.platform }}站台</div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  ticketDetail: {
    type: Object,
    required: true
  }
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  const weekday = weekdays[date.getDay()]
  return `${year}-${month}-${day} 周${weekday}`
}

// 格式化时间
const formatTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${hours}:${minutes}`
}

// 格式化ISO 8601持续时间为友好显示
const formatDuration = (isoDuration) => {
  if (!isoDuration) return '计算中'

  const match = isoDuration.match(/^PT(-?\d+(?:\.\d+)?H)?(-?\d+(?:\.\d+)?M)?(-?\d+(?:\.\d+)?S)?$/)
  if (!match) {
    return isoDuration
  }

  const hoursStr = match[1] ? match[1].replace('H', '') : '0'
  const minutesStr = match[2] ? match[2].replace('M', '') : '0'
  const secondsStr = match[3] ? match[3].replace('S', '') : '0'

  const hours = Math.abs(parseInt(hoursStr))
  const minutes = Math.abs(parseInt(minutesStr))
  const seconds = Math.abs(parseFloat(secondsStr))

  if (hours > 0 && minutes > 0) {
    return `${hours}小时${minutes}分钟`
  } else if (hours > 0) {
    return `${hours}小时`
  } else if (minutes > 0) {
    return `${minutes}分钟`
  } else if (seconds > 0) {
    return `${Math.round(seconds)}秒`
  } else {
    return '计算中'
  }
}
</script>

