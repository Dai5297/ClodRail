<template>
  <article class="mb-5 overflow-hidden rounded-xl border border-line bg-white shadow-soft">
    <div class="flex flex-wrap items-center gap-3 border-b border-line bg-[color-mix(in_srgb,var(--rail-primary)_3%,var(--rail-surface))] px-5 py-4 sm:px-7">
      <span class="rounded-md bg-navy px-3 py-1 text-xs font-bold tracking-wider text-white">第 1 站 · 车次</span>
      <span class="data-number text-2xl font-bold text-primary sm:text-3xl">{{ ticketDetail.trainCode }}</span>
      <span class="rounded-full border border-primary/25 bg-primary-soft px-2.5 py-1 text-xs font-semibold text-primary-active">{{ ticketDetail.trainType }}</span>
      <span class="ml-0 text-sm text-secondary sm:ml-auto">{{ formatDate(ticketDetail.departureDate) }}</span>
    </div>

    <div class="grid grid-cols-[1fr_64px_1fr] items-center gap-2 px-5 py-7 sm:grid-cols-[1fr_minmax(160px,260px)_1fr] sm:gap-8 sm:px-10 sm:py-9">
      <div class="min-w-0 text-left">
        <div class="mb-3">
          <time class="data-number block text-3xl font-bold leading-none text-navy sm:text-4xl">{{ formatTime(ticketDetail.startTime) }}</time>
          <span class="mt-1 hidden text-xs text-secondary/80 sm:block">{{ formatDate(ticketDetail.startTime) }}</span>
        </div>
        <p class="truncate text-lg font-bold text-ink sm:text-xl">{{ ticketDetail.originStation.name }}</p>
        <p class="mt-1 text-xs text-secondary sm:text-sm">{{ ticketDetail.originStation.platform }}站台 · 出发</p>
      </div>

      <div class="flex min-w-0 flex-col items-center gap-2 text-center">
        <div class="flex items-center gap-1.5 text-xs font-medium text-secondary sm:text-sm">
          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" /></svg>
          <span>{{ formatDuration(ticketDetail.duration) }}</span>
        </div>
        <div class="flex w-full items-center" aria-hidden="true">
          <span class="h-2.5 w-2.5 shrink-0 rounded-full border-2 border-primary bg-white" />
          <span class="h-0.5 min-w-4 flex-1 bg-primary" />
          <svg class="mx-1 h-5 w-5 shrink-0 text-primary" fill="currentColor" viewBox="0 0 24 24"><path d="M4 15.5V9.2c0-2.5 2-4.5 4.5-4.5h7c2.5 0 4.5 2 4.5 4.5v6.3h-2v2.3a1.2 1.2 0 0 1-2.4 0v-2.3H8.4v2.3a1.2 1.2 0 0 1-2.4 0v-2.3H4Zm2-2h12V9.2c0-1.4-1.1-2.5-2.5-2.5h-7A2.5 2.5 0 0 0 6 9.2v4.3Zm2-1.2a1.2 1.2 0 1 0 0-2.4 1.2 1.2 0 0 0 0 2.4Zm8 0a1.2 1.2 0 1 0 0-2.4 1.2 1.2 0 0 0 0 2.4Z" /></svg>
          <span class="h-0.5 min-w-4 flex-1 bg-primary" />
          <span class="h-2.5 w-2.5 shrink-0 rounded-full bg-action ring-2 ring-action-soft" />
        </div>
        <div class="text-xs text-secondary/80">
          经停 {{ ticketDetail.stopCount }} 站
        </div>
      </div>

      <div class="min-w-0 text-right">
        <div class="mb-3">
          <time class="data-number block text-3xl font-bold leading-none text-navy sm:text-4xl">{{ formatTime(ticketDetail.endTime) }}</time>
          <span class="mt-1 hidden text-xs text-secondary/80 sm:block">{{ formatDate(ticketDetail.endTime) }}</span>
        </div>
        <p class="truncate text-lg font-bold text-ink sm:text-xl">{{ ticketDetail.destinationStation.name }}</p>
        <p class="mt-1 text-xs text-secondary sm:text-sm">{{ ticketDetail.destinationStation.platform }}站台 · 到达</p>
      </div>
    </div>
  </article>
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

<style scoped>
.data-number {
  font-family: "DIN Alternate", "Arial Narrow", Arial, sans-serif;
  font-variant-numeric: tabular-nums;
}
</style>
