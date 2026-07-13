<template>
  <main class="ticket-page">
    <header class="ticket-page__masthead">
      <div class="ticket-page__masthead-inner">
        <div>
          <span class="ticket-page__kicker">CLODRAIL · 车票预订</span>
          <h1>车票查询</h1>
          <p>按车次信息牌快速比较时间、余票与价格。</p>
        </div>
        <div class="ticket-page__guide" aria-hidden="true">
          <span class="ticket-page__guide-dot"></span>
          <span class="ticket-page__guide-line"></span>
          <i class="ri-train-line"></i>
          <span class="ticket-page__guide-line"></span>
          <span class="ticket-page__guide-dot ticket-page__guide-dot--end"></span>
        </div>
      </div>
    </header>

    <div class="ticket-page__container">
      <TicketSearch
        :initial-params="searchParams"
        :show-title="false"
        search-button-text="重新查询"
        @search="handleSearch"
      />

      <div class="ticket-page__content">
        <aside class="ticket-filters" aria-labelledby="filter-title">
          <div class="ticket-filters__heading">
            <div>
              <span>FILTER</span>
              <h2 id="filter-title">筛选条件</h2>
            </div>
            <i class="ri-equalizer-2-line" aria-hidden="true"></i>
          </div>

          <div class="ticket-filters__groups">
            <section class="ticket-filters__group">
              <h3>车型</h3>
              <a-checkbox-group v-model:value="filters.trainTypes" class="ticket-filters__options">
                <a-checkbox value="G">G · 高铁</a-checkbox>
                <a-checkbox value="D">D · 动车</a-checkbox>
                <a-checkbox value="Z">Z · 直达</a-checkbox>
                <a-checkbox value="K">K · 普快</a-checkbox>
              </a-checkbox-group>
            </section>

            <section class="ticket-filters__group">
              <h3>出发时段</h3>
              <a-checkbox-group v-model:value="filters.departureTimes" class="ticket-filters__options">
                <a-checkbox value="0-6">凌晨 <small>00:00—06:00</small></a-checkbox>
                <a-checkbox value="6-12">上午 <small>06:00—12:00</small></a-checkbox>
                <a-checkbox value="12-18">下午 <small>12:00—18:00</small></a-checkbox>
                <a-checkbox value="18-24">晚上 <small>18:00—24:00</small></a-checkbox>
              </a-checkbox-group>
            </section>

            <section class="ticket-filters__group ticket-filters__group--availability">
              <a-checkbox v-model:checked="filters.hasTicket">只看有票车次</a-checkbox>
            </section>
          </div>
        </aside>

        <section class="ticket-results" aria-labelledby="result-title">
          <div class="ticket-results__toolbar">
            <div>
              <span class="ticket-results__eyebrow">SEARCH RESULT</span>
              <h2 id="result-title">可选车次</h2>
            </div>

            <div class="ticket-results__sort" aria-label="车次排序方式">
              <span>排序</span>
              <button
                type="button"
                :class="{ 'is-active': sortBy === 'startTime' }"
                :aria-pressed="sortBy === 'startTime'"
                @click="handleSort('startTime')"
              >
                出发时间
              </button>
              <button
                type="button"
                :class="{ 'is-active': sortBy === 'duration' }"
                :aria-pressed="sortBy === 'duration'"
                @click="handleSort('duration')"
              >
                耗时最短
              </button>
              <button
                type="button"
                :class="{ 'is-active': sortBy === 'price' }"
                :aria-pressed="sortBy === 'price'"
                @click="handleSort('price')"
              >
                价格最低
              </button>
            </div>

            <p class="ticket-results__count" aria-live="polite">
              <strong>{{ filteredTickets.length }}</strong>
              个车次
            </p>
          </div>

          <div v-if="loading" class="ticket-results__loading" aria-label="正在加载车次">
            <a-skeleton
              v-for="i in 3"
              :key="i"
              active
              :paragraph="{ rows: 4 }"
              class="ticket-results__skeleton"
            />
          </div>

          <div v-else-if="filteredTickets.length === 0" class="ticket-results__empty">
            <div class="ticket-results__empty-icon" aria-hidden="true">
              <i class="ri-train-line"></i>
            </div>
            <h3>暂无符合条件的车次</h3>
            <p>可以调整筛选条件，或更换出发日期后重新查询。</p>
          </div>

          <div v-else class="ticket-results__list">
            <TicketCard
              v-for="ticket in filteredTickets"
              :key="ticket.trainId"
              :ticket="ticket"
              @view-detail="handleViewDetail"
            />
          </div>
        </section>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { searchTickets } from '@/api/ticket'
import TicketSearch from '@/components/TicketSearch.vue'
import TicketCard from './components/TicketCard.vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const tickets = ref([])
const searchParams = reactive({})

const filters = reactive({
  trainTypes: [],
  departureTimes: [],
  hasTicket: false
})

const sortBy = ref('startTime')

// Initialize search params from route query
const initParams = () => {
  const { originStationId, destinationStationId, date } = route.query
  if (originStationId && destinationStationId && date) {
    searchParams.originStationId = originStationId
    searchParams.destinationStationId = destinationStationId
    searchParams.date = date
    fetchTickets()
  }
}

const fetchTickets = async () => {
  loading.value = true
  try {
    // Construct API params
    const params = {
      departureDate: searchParams.date,
      originStationId: searchParams.originStationId,
      destinationStationId: searchParams.destinationStationId
    }

    const response = await searchTickets(params)
    if (response.code === 200) {
      tickets.value = response.data?.records || []
    } else {
      message.error(response.message || '查询失败')
      tickets.value = []
    }
  } catch (error) {
    message.error('查询出错')
    tickets.value = []
  } finally {
    loading.value = false
  }
}

const handleSearch = (params) => {
  // Update route query to allow sharing/refreshing
  router.push({
    path: '/ticket',
    query: {
      originStationId: params.originStationId,
      destinationStationId: params.destinationStationId,
      date: params.date
    }
  }).then(() => {
    // Update local params and fetch
    searchParams.originStationId = params.originStationId
    searchParams.destinationStationId = params.destinationStationId
    searchParams.date = params.date
    fetchTickets()
  })
}

const handleSort = (type) => {
  sortBy.value = type
}

const handleViewDetail = (ticket) => {
  // Navigate to booking page or show modal
  router.push({
    path: '/ticket-detail',
    query: {
      ticketId: ticket.id,
      date: searchParams.date,
      originStationId: searchParams.originStationId,
      destinationStationId: searchParams.destinationStationId
    }
  })
}

const getMinAvailablePrice = (ticket) => {
  const availablePrices = (ticket.seatTypes || [])
    .filter(seat => seat.remainingSeats > 0)
    .map(seat => Number(seat.price))
    .filter(price => Number.isFinite(price))

  return availablePrices.length > 0 ? Math.min(...availablePrices) : Infinity
}

// Client-side filtering and sorting
const filteredTickets = computed(() => {
  let result = [...tickets.value]

  // Filter by train type
  if (filters.trainTypes.length > 0) {
    result = result.filter(t => {
      const trainNumber = t.trainNumber || t.trainCode || ''
      const type = trainNumber[0] || ''
      return filters.trainTypes.includes(type)
    })
  }

  // Filter by time
  if (filters.departureTimes.length > 0) {
    result = result.filter(t => {
      const hour = parseInt(t.startTime.split(':')[0])
      return filters.departureTimes.some(range => {
        const [start, end] = range.split('-').map(Number)
        return hour >= start && hour < end
      })
    })
  }

  // Filter by available tickets
  if (filters.hasTicket) {
    result = result.filter(t => t.seatTypes.some(s => s.remainingSeats > 0))
  }

  // Sort
  result.sort((a, b) => {
    if (sortBy.value === 'startTime') {
      return a.startTime.localeCompare(b.startTime)
    } else if (sortBy.value === 'duration') {
      return a.duration.localeCompare(b.duration)
    } else if (sortBy.value === 'price') {
      return getMinAvailablePrice(a) - getMinAvailablePrice(b)
    }
    return 0
  })

  return result
})

onMounted(() => {
  initParams()
})
</script>

<style scoped>
.ticket-page {
  min-height: 100vh;
  padding-bottom: 72px;
  background: var(--rail-background);
  color: var(--rail-ink);
}

.ticket-page__masthead {
  min-height: 188px;
  border-bottom: 1px solid var(--rail-line);
  background:
    linear-gradient(90deg, color-mix(in srgb, var(--rail-primary) 7%, transparent) 1px, transparent 1px) 0 0 / 84px 100%,
    var(--rail-surface);
}

.ticket-page__masthead-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: min(1180px, calc(100% - 32px));
  min-height: 188px;
  margin: 0 auto;
  padding-bottom: 30px;
}

.ticket-page__kicker,
.ticket-results__eyebrow,
.ticket-filters__heading span {
  color: var(--rail-primary);
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.16em;
}

.ticket-page__masthead h1 {
  margin: 8px 0 6px;
  color: var(--rail-navy);
  font-size: clamp(28px, 4vw, 38px);
  font-weight: 800;
  line-height: 1.2;
}

.ticket-page__masthead p {
  margin: 0;
  color: var(--rail-muted);
  font-size: 15px;
}

.ticket-page__guide {
  display: flex;
  align-items: center;
  width: min(360px, 34vw);
  color: var(--rail-primary);
}

.ticket-page__guide i {
  margin: 0 10px;
  font-size: 28px;
}

.ticket-page__guide-line {
  flex: 1;
  height: 2px;
  background: var(--rail-primary);
}

.ticket-page__guide-dot {
  width: 12px;
  height: 12px;
  border: 3px solid var(--rail-primary);
  border-radius: 50%;
  background: var(--rail-surface);
}

.ticket-page__guide-dot--end {
  border-color: var(--rail-action);
}

.ticket-page__container {
  position: relative;
  z-index: 1;
  width: min(1180px, calc(100% - 32px));
  margin: -30px auto 0;
}

.ticket-page__content {
  display: grid;
  grid-template-columns: 236px minmax(0, 1fr);
  gap: 20px;
  align-items: start;
  margin-top: 24px;
}

.ticket-filters {
  position: sticky;
  top: 88px;
  overflow: hidden;
  border: 1px solid var(--rail-line);
  border-radius: var(--rail-card-radius);
  background: var(--rail-surface);
}

.ticket-filters__heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 20px 16px;
  border-bottom: 1px solid var(--rail-line);
}

.ticket-filters__heading h2,
.ticket-results__toolbar h2 {
  margin: 3px 0 0;
  color: var(--rail-navy);
  font-size: 18px;
  line-height: 1.3;
}

.ticket-filters__heading i {
  color: var(--rail-primary);
  font-size: 20px;
}

.ticket-filters__group {
  padding: 18px 20px;
  border-bottom: 1px solid var(--rail-line);
}

.ticket-filters__group:last-child {
  border-bottom: 0;
}

.ticket-filters__group h3 {
  margin: 0 0 12px;
  color: var(--rail-muted);
  font-size: 12px;
  font-weight: 700;
}

.ticket-filters__options {
  display: flex;
  flex-direction: column;
  gap: 11px;
}

.ticket-filters__options small {
  margin-left: 4px;
  color: var(--rail-muted);
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 11px;
}

.ticket-filters__group--availability {
  background: var(--rail-primary-soft);
}

.ticket-results {
  min-width: 0;
}

.ticket-results__toolbar {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  gap: 22px;
  align-items: center;
  min-height: 74px;
  margin-bottom: 14px;
  padding: 12px 18px;
  border: 1px solid var(--rail-line);
  border-radius: var(--rail-card-radius);
  background: var(--rail-surface);
}

.ticket-results__sort {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.ticket-results__sort > span {
  margin-right: 4px;
  color: var(--rail-muted);
  font-size: 12px;
}

.ticket-results__sort button {
  padding: 7px 10px;
  border: 0;
  border-radius: 6px;
  background: transparent;
  color: var(--rail-muted);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 150ms ease, color 150ms ease;
}

.ticket-results__sort button:hover {
  background: var(--rail-background);
  color: var(--rail-ink);
}

.ticket-results__sort button.is-active {
  background: var(--rail-primary-soft);
  color: var(--rail-primary-active);
}

.ticket-results__sort button:focus-visible {
  outline: none;
  box-shadow: var(--rail-focus-ring);
}

.ticket-results__count {
  margin: 0;
  color: var(--rail-muted);
  font-size: 12px;
  white-space: nowrap;
}

.ticket-results__count strong {
  margin-right: 4px;
  color: var(--rail-navy);
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 22px;
  font-variant-numeric: tabular-nums;
}

.ticket-results__loading,
.ticket-results__list {
  display: grid;
  gap: 12px;
}

.ticket-results__skeleton,
.ticket-results__empty {
  padding: 24px;
  border: 1px solid var(--rail-line);
  border-radius: var(--rail-card-radius);
  background: var(--rail-surface);
}

.ticket-results__empty {
  padding-block: 64px;
  text-align: center;
}

.ticket-results__empty-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin: 0 auto 14px;
  border: 1px solid var(--rail-line);
  border-radius: 50%;
  background: var(--rail-background);
  color: var(--rail-primary);
  font-size: 28px;
}

.ticket-results__empty h3 {
  margin: 0;
  color: var(--rail-navy);
  font-size: 18px;
}

.ticket-results__empty p {
  margin: 8px 0 0;
  color: var(--rail-muted);
  font-size: 14px;
}

@media (max-width: 1020px) {
  .ticket-page__content {
    grid-template-columns: minmax(0, 1fr);
  }

  .ticket-filters {
    position: static;
  }

  .ticket-filters__groups {
    display: grid;
    grid-template-columns: 1fr 1.4fr 0.8fr;
  }

  .ticket-filters__group {
    border-right: 1px solid var(--rail-line);
    border-bottom: 0;
  }

  .ticket-filters__group:last-child {
    display: flex;
    align-items: center;
    border-right: 0;
  }
}

@media (max-width: 760px) {
  .ticket-page__masthead,
  .ticket-page__masthead-inner {
    min-height: 164px;
  }

  .ticket-page__masthead-inner {
    padding-bottom: 24px;
  }

  .ticket-page__guide {
    display: none;
  }

  .ticket-page__container {
    margin-top: -20px;
  }

  .ticket-filters__groups {
    grid-template-columns: 1fr 1fr;
  }

  .ticket-filters__group:nth-child(2) {
    border-right: 0;
  }

  .ticket-filters__group--availability {
    grid-column: 1 / -1;
    border-top: 1px solid var(--rail-line);
  }

  .ticket-results__toolbar {
    grid-template-columns: 1fr auto;
  }

  .ticket-results__sort {
    grid-column: 1 / -1;
    grid-row: 2;
    justify-content: flex-start;
    overflow-x: auto;
  }
}

@media (max-width: 520px) {
  .ticket-page {
    padding-bottom: 48px;
  }

  .ticket-page__masthead-inner,
  .ticket-page__container {
    width: min(100% - 20px, 1180px);
  }

  .ticket-page__masthead h1 {
    font-size: 28px;
  }

  .ticket-page__masthead p {
    font-size: 13px;
  }

  .ticket-page__content {
    margin-top: 16px;
  }

  .ticket-filters__groups {
    grid-template-columns: minmax(0, 1fr);
  }

  .ticket-filters__group,
  .ticket-filters__group:nth-child(2) {
    border-right: 0;
    border-bottom: 1px solid var(--rail-line);
  }

  .ticket-filters__group--availability {
    grid-column: auto;
    border-top: 0;
    border-bottom: 0;
  }

  .ticket-results__toolbar {
    gap: 12px;
    padding: 12px;
  }

  .ticket-results__sort button {
    white-space: nowrap;
  }
}

@media (prefers-reduced-motion: reduce) {
  .ticket-results__sort button {
    transition: none;
  }
}
</style>
