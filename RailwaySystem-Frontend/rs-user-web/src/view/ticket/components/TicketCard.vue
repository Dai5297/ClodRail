<template>
  <article class="ticket-card" :class="{ 'ticket-card--sold-out': !hasAvailableSeats }">
    <div class="ticket-card__train">
      <span class="ticket-card__label">车次</span>
      <strong class="ticket-card__code rail-data">{{ ticket.trainNumber || ticket.trainCode || '待定' }}</strong>
      <span class="ticket-card__availability" :class="{ 'is-sold-out': !hasAvailableSeats }">
        <span aria-hidden="true"></span>
        {{ hasAvailableSeats ? '可预订' : '已售罄' }}
      </span>
    </div>

    <div class="ticket-card__journey">
      <div class="ticket-card__station ticket-card__station--origin">
        <time class="ticket-card__time rail-data">{{ formatTime(ticket.startTime) }}</time>
        <strong>{{ ticket.originStation?.name || '未知站点' }}</strong>
        <span>出发</span>
      </div>

      <div class="ticket-card__track">
        <span class="ticket-card__duration">
          <i class="ri-time-line" aria-hidden="true"></i>
          {{ formatDuration(ticket.duration) }}
        </span>
        <div class="ticket-card__track-line" aria-hidden="true">
          <span></span>
          <i class="ri-train-line"></i>
          <span></span>
        </div>
        <span class="ticket-card__direct">运行时长</span>
      </div>

      <div class="ticket-card__station ticket-card__station--destination">
        <time class="ticket-card__time rail-data">{{ formatTime(ticket.endTime) }}</time>
        <strong>{{ ticket.destinationStation?.name || '未知站点' }}</strong>
        <span>到达</span>
      </div>
    </div>

    <div class="ticket-card__seats" aria-label="席别余票与价格">
      <div
        v-for="seatType in normalizedSeatTypes"
        :key="seatType.type"
        class="ticket-card__seat"
        :class="{ 'is-unavailable': seatType.remainingSeats === 0 }"
      >
        <span class="ticket-card__seat-name">{{ seatType.name }}</span>
        <strong class="ticket-card__seat-price rail-data">¥{{ formatPrice(seatType.price) }}</strong>
        <span class="ticket-card__seat-count" :class="seatStatusClass(seatType.remainingSeats)">
          {{ seatStatusText(seatType.remainingSeats) }}
        </span>
      </div>

      <p v-if="normalizedSeatTypes.length === 0" class="ticket-card__seat-empty">
        席别信息待更新
      </p>
    </div>

    <div class="ticket-card__action">
      <span class="ticket-card__price-label">最低票价</span>
      <div class="ticket-card__price rail-data" :class="{ 'is-sold-out': !hasAvailableSeats }">
        <template v-if="hasAvailableSeats && minPrice !== null">
          <small>¥</small>{{ formatPrice(minPrice) }}<span>起</span>
        </template>
        <template v-else-if="hasAvailableSeats">价格待更新</template>
        <template v-else>暂无余票</template>
      </div>
      <button
        type="button"
        :disabled="!hasAvailableSeats"
        :aria-label="hasAvailableSeats ? `预订${ticket.trainNumber || ticket.trainCode || '当前'}车次` : '当前车次无票'"
        @click="handleViewDetail"
      >
        {{ hasAvailableSeats ? '预订' : '无票' }}
        <i v-if="hasAvailableSeats" class="ri-arrow-right-line" aria-hidden="true"></i>
      </button>
    </div>
  </article>
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

const normalizedSeatTypes = computed(() => props.ticket.seatTypes || [])

const minPrice = computed(() => {
  const availablePrices = normalizedSeatTypes.value
    .filter(seat => seat.remainingSeats > 0)
    .map(seat => Number(seat.price))
    .filter(price => Number.isFinite(price))

  return availablePrices.length > 0 ? Math.min(...availablePrices) : null
})

const formatTime = (timeStr) => {
  if (!timeStr) return '--:--'
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

const formatPrice = (price) => {
  const value = Number(price)
  if (!Number.isFinite(value)) return '--'
  return Number.isInteger(value) ? value : value.toFixed(1)
}

const hasAvailableSeats = computed(() => {
  return normalizedSeatTypes.value.some(seat => seat.remainingSeats > 0)
})

const seatStatusText = (remainingSeats) => {
  if (remainingSeats > 10) return '有票'
  if (remainingSeats > 0) return `剩 ${remainingSeats} 张`
  return '无票'
}

const seatStatusClass = (remainingSeats) => {
  if (remainingSeats > 10) return 'is-available'
  if (remainingSeats > 0) return 'is-limited'
  return 'is-empty'
}

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
  const hours = Math.abs(parseInt(hoursStr))
  const minutes = Math.abs(parseInt(minutesStr))

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

<style scoped>
.ticket-card {
  position: relative;
  display: grid;
  grid-template-columns: 92px minmax(270px, 1.15fr) minmax(260px, 1fr) 124px;
  align-items: stretch;
  overflow: hidden;
  border: 1px solid var(--rail-line);
  border-radius: var(--rail-card-radius);
  background: var(--rail-surface);
  color: var(--rail-ink);
  transition: border-color 160ms ease, box-shadow 160ms ease, transform 160ms ease;
}

.ticket-card::before {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  width: 3px;
  background: var(--rail-primary);
  content: '';
}

.ticket-card:hover {
  border-color: var(--rail-primary-hover);
  box-shadow: var(--rail-shadow-soft);
  transform: translateY(-1px);
}

.ticket-card--sold-out::before {
  background: var(--rail-muted);
}

.ticket-card__train {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 18px 12px 18px 15px;
  border-right: 1px solid var(--rail-line);
  background: var(--rail-background);
  text-align: center;
}

.ticket-card__label,
.ticket-card__price-label {
  color: var(--rail-muted);
  font-size: 11px;
}

.ticket-card__code {
  margin: 4px 0 8px;
  color: var(--rail-navy);
  font-size: 23px;
  font-weight: 800;
  letter-spacing: -0.02em;
}

.ticket-card__availability {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  color: var(--rail-success);
  font-size: 11px;
  font-weight: 700;
}

.ticket-card__availability > span {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.ticket-card__availability.is-sold-out {
  color: var(--rail-muted);
}

.ticket-card__journey {
  display: grid;
  grid-template-columns: 76px minmax(90px, 1fr) 76px;
  gap: 12px;
  align-items: center;
  padding: 18px 20px;
  border-right: 1px solid var(--rail-line);
}

.ticket-card__station {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.ticket-card__station--destination {
  align-items: flex-end;
  text-align: right;
}

.ticket-card__time {
  color: var(--rail-ink);
  font-size: 26px;
  font-weight: 800;
  line-height: 1;
  letter-spacing: -0.03em;
}

.ticket-card__station strong {
  overflow: hidden;
  margin-top: 7px;
  color: var(--rail-navy);
  font-size: 14px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ticket-card__station > span {
  margin-top: 2px;
  color: var(--rail-muted);
  font-size: 10px;
}

.ticket-card__track {
  min-width: 0;
  color: var(--rail-muted);
  text-align: center;
}

.ticket-card__duration,
.ticket-card__direct {
  display: block;
  font-size: 11px;
  white-space: nowrap;
}

.ticket-card__duration i {
  margin-right: 3px;
  color: var(--rail-primary);
}

.ticket-card__track-line {
  display: flex;
  align-items: center;
  margin: 7px 0 5px;
  color: var(--rail-primary);
}

.ticket-card__track-line span {
  flex: 1;
  height: 1px;
  background: var(--rail-primary);
}

.ticket-card__track-line span:first-child::before,
.ticket-card__track-line span:last-child::after {
  display: block;
  width: 5px;
  height: 5px;
  margin-top: -2px;
  border-radius: 50%;
  background: var(--rail-primary);
  content: '';
}

.ticket-card__track-line span:last-child::after {
  margin-left: auto;
  background: var(--rail-action);
}

.ticket-card__track-line i {
  margin: 0 5px;
  font-size: 15px;
}

.ticket-card__direct {
  color: var(--rail-muted);
  font-size: 10px;
}

.ticket-card__seats {
  display: grid;
  grid-template-columns: repeat(2, minmax(108px, 1fr));
  align-content: center;
  gap: 6px;
  padding: 14px 16px;
  border-right: 1px solid var(--rail-line);
}

.ticket-card__seat {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 2px 8px;
  align-items: baseline;
  min-width: 0;
  padding: 7px 8px;
  border-radius: 6px;
  background: var(--rail-background);
}

.ticket-card__seat.is-unavailable {
  opacity: 0.7;
}

.ticket-card__seat-name {
  overflow: hidden;
  color: var(--rail-muted);
  font-size: 11px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.ticket-card__seat-price {
  color: var(--rail-navy);
  font-size: 13px;
  font-weight: 700;
}

.ticket-card__seat-count {
  grid-column: 1 / -1;
  font-size: 10px;
  font-weight: 700;
}

.ticket-card__seat-count.is-available {
  color: var(--rail-success);
}

.ticket-card__seat-count.is-limited {
  color: var(--rail-warning);
}

.ticket-card__seat-count.is-empty {
  color: var(--rail-muted);
}

.ticket-card__seat-empty {
  grid-column: 1 / -1;
  margin: 0;
  color: var(--rail-muted);
  font-size: 12px;
  text-align: center;
}

.ticket-card__action {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: center;
  padding: 16px;
  text-align: right;
}

.ticket-card__price {
  min-height: 28px;
  margin: 1px 0 9px;
  color: var(--rail-action-hover);
  font-size: 23px;
  font-weight: 800;
  line-height: 1;
}

.ticket-card__price small {
  margin-right: 2px;
  font-size: 13px;
}

.ticket-card__price span {
  margin-left: 2px;
  color: var(--rail-muted);
  font-family: inherit;
  font-size: 10px;
  font-weight: 400;
}

.ticket-card__price.is-sold-out {
  color: var(--rail-muted);
  font-size: 15px;
}

.ticket-card__action button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  min-height: 38px;
  border: 1px solid var(--rail-primary);
  border-radius: var(--rail-control-radius);
  background: var(--rail-primary);
  color: var(--rail-surface);
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 150ms ease, border-color 150ms ease;
}

.ticket-card__action button:hover:not(:disabled) {
  border-color: var(--rail-primary-active);
  background: var(--rail-primary-active);
}

.ticket-card__action button:focus-visible {
  outline: none;
  box-shadow: var(--rail-focus-ring);
}

.ticket-card__action button:disabled {
  border-color: var(--rail-line);
  background: var(--rail-background);
  color: var(--rail-muted);
  cursor: not-allowed;
}

@media (max-width: 1180px) {
  .ticket-card {
    grid-template-columns: 86px minmax(260px, 1.1fr) minmax(200px, 0.85fr) 112px;
  }

  .ticket-card__seats {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 760px) {
  .ticket-card {
    grid-template-columns: 80px minmax(0, 1fr) 112px;
  }

  .ticket-card__journey {
    border-right: 0;
  }

  .ticket-card__seats {
    grid-column: 1 / -1;
    grid-row: 2;
    grid-template-columns: repeat(auto-fit, minmax(105px, 1fr));
    border-top: 1px solid var(--rail-line);
    border-right: 0;
  }

  .ticket-card__action {
    border-left: 1px solid var(--rail-line);
  }
}

@media (max-width: 540px) {
  .ticket-card {
    grid-template-columns: 70px minmax(0, 1fr);
  }

  .ticket-card__train {
    padding-inline: 10px;
  }

  .ticket-card__code {
    font-size: 19px;
  }

  .ticket-card__journey {
    grid-template-columns: 64px minmax(54px, 1fr) 64px;
    gap: 7px;
    padding: 17px 12px;
  }

  .ticket-card__time {
    font-size: 22px;
  }

  .ticket-card__seats {
    grid-column: 1 / -1;
    grid-row: 2;
  }

  .ticket-card__action {
    grid-column: 1 / -1;
    grid-row: 3;
    display: grid;
    grid-template-columns: auto 1fr 112px;
    gap: 8px;
    align-items: center;
    padding: 12px;
    border-top: 1px solid var(--rail-line);
    border-left: 0;
    text-align: left;
  }

  .ticket-card__price {
    margin: 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .ticket-card,
  .ticket-card__action button {
    transition: none;
  }

  .ticket-card:hover {
    transform: none;
  }
}
</style>
