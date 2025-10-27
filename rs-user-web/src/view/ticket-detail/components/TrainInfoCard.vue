<template>
  <div class="train-info-card">
    <div class="train-header">
      <div class="train-number">{{ ticketDetail.trainCode }}</div>
      <div class="train-type">{{ ticketDetail.trainType }}</div>
      <div class="train-date">{{ formatDate(ticketDetail.departureDate) }}</div>
    </div>

    <div class="route-info">
      <div class="station-info departure">
        <div class="time-display">
          <div class="time">{{ formatTime(ticketDetail.startTime) }}</div>
          <div class="date">{{ formatDate(ticketDetail.startTime) }}</div>
        </div>
        <div class="station">{{ ticketDetail.originStation.name }}</div>
        <div class="platform">{{ ticketDetail.originStation.platform }}站台</div>
      </div>

      <div class="journey-info">
        <div class="duration">
          <el-icon>
            <Clock/>
          </el-icon>
          <span>{{ formatDuration(ticketDetail.duration) }}</span>
        </div>
        <div class="route-line">
          <div class="line"></div>
          <div class="stops">{{ ticketDetail.stopCount }}站</div>
        </div>
      </div>

      <div class="station-info arrival">
        <div class="time-display">
          <div class="time">{{ formatTime(ticketDetail.endTime) }}</div>
          <div class="date">{{ formatDate(ticketDetail.endTime) }}</div>
        </div>
        <div class="station">{{ ticketDetail.destinationStation.name }}</div>
        <div class="platform">{{ ticketDetail.destinationStation.platform }}站台</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Clock } from '@element-plus/icons-vue'

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
.train-info-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.train-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.train-number {
  font-size: 28px;
  font-weight: 600;
  color: #1890ff;
}

.train-type {
  background: #f0f9ff;
  color: #1890ff;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 12px;
}

.train-date {
  color: #666;
  font-size: 16px;
}

.route-info {
  display: grid;
  grid-template-columns: 1fr auto 1fr;
  gap: 40px;
  align-items: center;
}

.station-info {
  text-align: center;
}

.station-info.departure {
  text-align: left;
}

.station-info.arrival {
  text-align: right;
}

.time-display {
  margin-bottom: 8px;
}

.time {
  font-size: 32px;
  font-weight: 600;
  color: #333;
  line-height: 1;
  margin-bottom: 4px;
}

.date {
  font-size: 12px;
  color: #999;
  line-height: 1;
}

.station {
  font-size: 18px;
  color: #333;
  margin-bottom: 4px;
}

.platform {
  font-size: 14px;
  color: #999;
}

.journey-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.duration {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 14px;
}

.route-line {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
}

.line {
  width: 100px;
  height: 2px;
  background: linear-gradient(to right, #1890ff, #52c41a);
  border-radius: 1px;
}

.stops {
  font-size: 12px;
  color: #999;
}

@media (max-width: 768px) {
  .route-info {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .station-info.departure,
  .station-info.arrival {
    text-align: center;
  }
}
</style>

