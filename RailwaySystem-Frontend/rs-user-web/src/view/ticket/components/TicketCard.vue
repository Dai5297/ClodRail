<template>
  <div class="bg-white rounded-2xl p-6 hover:shadow-soft transition-all duration-300 border border-transparent hover:border-slate-100 mb-4 group">
    <div class="flex flex-col lg:flex-row justify-between items-center gap-6">
      <!-- Train Info -->
      <div class="flex items-center gap-8 flex-1 w-full lg:w-auto">
        <div class="text-center w-24">
          <div class="text-3xl font-bold text-slate-900 mb-1 font-mono">{{ formatTime(ticket.startTime) }}</div>
          <div class="text-sm text-slate-500 font-medium">{{ ticket.originStation?.name || '未知' }}</div>
        </div>
        
        <div class="flex flex-col items-center flex-1 min-w-[120px]">
          <div class="text-xs text-slate-400 mb-2 font-medium flex items-center gap-1">
            <i class="ri-time-line"></i>
            {{ formatDuration(ticket.duration) }}
          </div>
          <div class="w-full h-1 bg-slate-100 rounded-full relative overflow-hidden">
            <div class="absolute inset-0 bg-slate-200 group-hover:bg-blue-200 transition-colors duration-500"></div>
          </div>
          <div class="mt-2 px-3 py-1 bg-blue-50 text-primary text-xs font-bold rounded-full">
            {{ ticket.trainCode }}
          </div>
        </div>

        <div class="text-center w-24">
          <div class="text-3xl font-bold text-slate-900 mb-1 font-mono">{{ formatTime(ticket.endTime) }}</div>
          <div class="text-sm text-slate-500 font-medium">{{ ticket.destinationStation?.name || '未知' }}</div>
        </div>
      </div>

      <div class="h-12 w-px bg-slate-100 hidden lg:block"></div>

      <!-- Seats -->
      <div class="flex flex-wrap justify-center gap-3 flex-1">
        <div 
          v-for="seatType in ticket.seatTypes" 
          :key="seatType.type"
          class="text-center min-w-[80px] p-2 rounded-xl border border-slate-100 hover:border-blue-200 hover:bg-blue-50/50 cursor-pointer transition-all"
          :class="{ 'opacity-60 bg-slate-50 grayscale': seatType.remainingSeats === 0 }"
        >
          <div class="text-xs text-slate-500 mb-1">{{ seatType.name }}</div>
          <div class="text-base font-bold text-primary mb-1">¥{{ seatType.price }}</div>
          <div :class="seatType.remainingSeats > 10 ? 'text-emerald-500' : (seatType.remainingSeats > 0 ? 'text-orange-500' : 'text-slate-400')" class="text-xs font-medium">
            {{ seatType.remainingSeats > 10 ? '有票' : (seatType.remainingSeats > 0 ? `剩${seatType.remainingSeats}` : '无票') }}
          </div>
        </div>
      </div>

      <!-- Action -->
      <div class="w-full lg:w-auto">
        <button 
          class="w-full lg:w-32 py-2.5 bg-primary hover:bg-blue-700 text-white rounded-xl font-bold shadow-lg shadow-blue-500/20 hover:shadow-blue-500/40 transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed"
          :disabled="!hasAvailableSeats"
          @click="handleViewDetail"
        >
          {{ hasAvailableSeats ? '预订' : '无票' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  ticket: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['view-detail'])

const formatTime = (timeStr) => {
  if (!timeStr) return ''
  // Handle 2025-12-11T15:06:48
  if (timeStr.includes('T')) {
    return timeStr.split('T')[1].substring(0, 5)
  }
  // Handle 2025-12-11 15:06:48
  if (timeStr.includes(' ')) {
    return timeStr.split(' ')[1].substring(0, 5)
  }
  // Handle 15:06:48 or 15:06
  return timeStr.substring(0, 5)
}

const hasAvailableSeats = computed(() => {
  return props.ticket.seatTypes && props.ticket.seatTypes.some(seat => seat.remainingSeats > 0)
})

const handleViewDetail = () => {
  emit('view-detail', props.ticket)
}

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
    return `${hours}时${minutes}分`
  } else if (hours > 0) {
    return `${hours}小时`
  } else if (minutes > 0) {
    return `${minutes}分钟`
  } else {
    return '计算中'
  }
}
</script>
