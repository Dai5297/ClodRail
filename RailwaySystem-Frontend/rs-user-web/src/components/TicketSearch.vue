<template>
  <section
    class="ticket-search"
    :class="{ 'ticket-search--compact': !showTitle }"
    :aria-labelledby="showTitle ? 'ticket-search-title' : undefined"
    :aria-label="showTitle ? undefined : '车票查询'"
  >
    <header v-if="showTitle" class="ticket-search__header">
      <div>
        <span class="ticket-search__eyebrow">TRAIN SEARCH</span>
        <h2 id="ticket-search-title">查询车次</h2>
      </div>
      <p>选择出发站、到达站与日期，查看可预订车次。</p>
    </header>

    <form class="ticket-search__form" :aria-busy="searchLoading" @submit.prevent="handleSearch">
      <div class="ticket-search__route">
        <div class="ticket-search__field ticket-search__field--station">
          <label for="origin-station">出发站</label>
          <div class="ticket-search__control">
            <span class="ticket-search__marker ticket-search__marker--origin" aria-hidden="true"></span>
            <a-select
              id="origin-station"
              v-model:value="searchForm.originStationId"
              show-search
              placeholder="选择出发站"
              class="ticket-search__select"
              :options="stationOptions"
              :filter-option="filterOption"
              :loading="stationLoading"
              :bordered="false"
              aria-label="出发站"
            />
          </div>
        </div>

        <button
          type="button"
          class="ticket-search__swap"
          aria-label="交换出发站和到达站"
          title="交换出发站和到达站"
          @click="exchangeStations"
        >
          <i class="ri-arrow-left-right-line" aria-hidden="true"></i>
        </button>

        <div class="ticket-search__field ticket-search__field--station">
          <label for="destination-station">到达站</label>
          <div class="ticket-search__control">
            <span class="ticket-search__marker ticket-search__marker--destination" aria-hidden="true"></span>
            <a-select
              id="destination-station"
              v-model:value="searchForm.destinationStationId"
              show-search
              placeholder="选择到达站"
              class="ticket-search__select"
              :options="stationOptions"
              :filter-option="filterOption"
              :loading="stationLoading"
              :bordered="false"
              aria-label="到达站"
            />
          </div>
        </div>
      </div>

      <div class="ticket-search__field ticket-search__field--date">
        <label for="departure-date">出发日期</label>
        <div class="ticket-search__control">
          <i class="ri-calendar-line ticket-search__icon" aria-hidden="true"></i>
          <a-date-picker
            id="departure-date"
            v-model:value="searchForm.date"
            class="ticket-search__picker"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            :disabled-date="disabledDate"
            :bordered="false"
            :show-suffix-icon="false"
            aria-label="出发日期"
          />
        </div>
      </div>

      <button class="ticket-search__submit" type="submit" :disabled="searchLoading">
        <i
          :class="searchLoading ? 'ri-loader-4-line ticket-search__loader' : 'ri-search-line'"
          aria-hidden="true"
        ></i>
        <span>{{ searchLoading ? '正在查询' : searchButtonText }}</span>
      </button>
    </form>

    <p v-if="stationLoadFailed" class="ticket-search__status" role="status">
      <i class="ri-error-warning-line" aria-hidden="true"></i>
      站点列表暂时无法更新，请稍后重试。
    </p>
  </section>
</template>

<script setup>
import { ref, reactive, onMounted, watch, computed } from 'vue'
import { message } from 'ant-design-vue'
import { getStations } from '@/api/station'
import dayjs from 'dayjs'
import { pinyin } from 'pinyin-pro'

const props = defineProps({
  initialParams: {
    type: Object,
    default: () => ({})
  },
  showTitle: {
    type: Boolean,
    default: true
  },
  searchButtonText: {
    type: String,
    default: '搜索车次'
  }
})

const emit = defineEmits(['search'])

const searchLoading = ref(false)
const stationLoading = ref(false)
const stationLoadFailed = ref(false)
const searchForm = reactive({
  originStationId: undefined,
  destinationStationId: undefined,
  date: undefined
})

const stations = ref([])

const stationOptions = computed(() => {
  return stations.value.map(s => ({
    value: s.id,
    label: s.name,
    code: s.code
  }))
})

const filterOption = (input, option) => {
  const query = input.toLowerCase()
  const name = option.label.toLowerCase()
  const code = option.code ? option.code.toLowerCase() : ''
  const pinyinFirst = pinyin(option.label, { pattern: 'first', toneType: 'none' }).toLowerCase()
  const pinyinFull = pinyin(option.label, { toneType: 'none' }).toLowerCase()

  return name.includes(query) || code.includes(query) || pinyinFirst.includes(query) || pinyinFull.includes(query)
}

const disabledDate = (current) => {
  return current && current < dayjs().startOf('day')
}

const exchangeStations = () => {
  const temp = searchForm.originStationId
  searchForm.originStationId = searchForm.destinationStationId
  searchForm.destinationStationId = temp
}

const loadStations = async () => {
  stationLoading.value = true
  stationLoadFailed.value = false
  try {
    const response = await getStations()
    if (response.code === 200) {
      stations.value = response.data
    } else {
      stationLoadFailed.value = true
      message.error(response.message || '获取站点列表失败')
    }
  } catch (error) {
    stationLoadFailed.value = true
    message.error('获取站点列表失败')
  } finally {
    stationLoading.value = false
  }
}

const handleSearch = async () => {
  if (!searchForm.originStationId || !searchForm.destinationStationId || !searchForm.date) {
    message.warning('请完善搜索条件')
    return
  }

  searchLoading.value = true
  emit('search', {
    originStationId: searchForm.originStationId,
    destinationStationId: searchForm.destinationStationId,
    date: searchForm.date
  })
  searchLoading.value = false
}

const initSearchParams = () => {
  if (props.initialParams.originStationId) {
    searchForm.originStationId = parseInt(props.initialParams.originStationId)
  }
  if (props.initialParams.destinationStationId) {
    searchForm.destinationStationId = parseInt(props.initialParams.destinationStationId)
  }
  if (props.initialParams.date) {
    searchForm.date = props.initialParams.date
  } else {
    searchForm.date = dayjs().format('YYYY-MM-DD')
  }
}

watch(() => props.initialParams, () => {
  initSearchParams()
}, { deep: true, immediate: true })

onMounted(async () => {
  await loadStations()
  initSearchParams()
})
</script>

<style scoped>
.ticket-search {
  width: 100%;
  max-width: 1180px;
  margin: 0 auto;
  padding: 24px;
  border: 1px solid var(--rail-line);
  border-top: 3px solid var(--rail-primary);
  border-radius: var(--rail-hero-radius);
  background: var(--rail-surface);
  box-shadow: var(--rail-shadow-card);
}

.ticket-search--compact {
  padding: 20px;
  border-top-width: 2px;
  border-radius: var(--rail-card-radius);
  box-shadow: var(--rail-shadow-soft);
}

.ticket-search__header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 20px;
}

.ticket-search__eyebrow {
  display: block;
  margin-bottom: 3px;
  color: var(--rail-primary);
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.16em;
}

.ticket-search__header h2 {
  margin: 0;
  color: var(--rail-navy);
  font-size: 24px;
  line-height: 1.25;
}

.ticket-search__header p {
  margin: 0;
  color: var(--rail-muted);
  font-size: 14px;
}

.ticket-search__form {
  display: grid;
  grid-template-columns: minmax(360px, 2fr) minmax(190px, 0.9fr) minmax(132px, 0.58fr);
  gap: 16px;
  align-items: end;
}

.ticket-search__route {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 38px minmax(0, 1fr);
  gap: 8px;
  align-items: end;
}

.ticket-search__route::before {
  position: absolute;
  right: 28px;
  bottom: 24px;
  left: 28px;
  height: 2px;
  background: var(--rail-line);
  content: '';
  pointer-events: none;
}

.ticket-search__field {
  min-width: 0;
}

.ticket-search__field label {
  display: block;
  margin: 0 0 8px 2px;
  color: var(--rail-muted);
  font-size: 12px;
  font-weight: 700;
  line-height: 1;
}

.ticket-search__control {
  position: relative;
  height: 48px;
  border: 1px solid var(--rail-line);
  border-radius: var(--rail-control-radius);
  background: var(--rail-surface);
  transition: border-color 160ms ease, box-shadow 160ms ease;
}

.ticket-search__control:hover {
  border-color: var(--rail-primary-hover);
}

.ticket-search__control:focus-within {
  border-color: var(--rail-primary);
  box-shadow: var(--rail-focus-ring);
}

.ticket-search__marker {
  position: absolute;
  z-index: 2;
  top: 50%;
  left: 14px;
  width: 10px;
  height: 10px;
  border: 3px solid var(--rail-primary);
  border-radius: 50%;
  background: var(--rail-surface);
  transform: translateY(-50%);
  pointer-events: none;
}

.ticket-search__marker--destination {
  border-color: var(--rail-action);
}

.ticket-search__icon {
  position: absolute;
  z-index: 2;
  top: 50%;
  left: 14px;
  color: var(--rail-primary);
  font-size: 18px;
  transform: translateY(-50%);
  pointer-events: none;
}

.ticket-search__select,
.ticket-search__picker {
  width: 100%;
  height: 100%;
}

.ticket-search__swap {
  position: relative;
  z-index: 3;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  margin-bottom: 6px;
  border: 1px solid var(--rail-action);
  border-radius: 50%;
  background: var(--rail-action-soft);
  color: var(--rail-action-hover);
  cursor: pointer;
  transition: background-color 160ms ease, color 160ms ease, transform 180ms ease;
}

.ticket-search__swap:hover {
  background: var(--rail-action);
  color: var(--rail-surface);
  transform: rotate(180deg);
}

.ticket-search__swap:focus-visible,
.ticket-search__submit:focus-visible {
  outline: none;
  box-shadow: var(--rail-focus-ring);
}

.ticket-search__submit {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 48px;
  padding: 0 20px;
  border: 1px solid var(--rail-primary);
  border-radius: var(--rail-control-radius);
  background: var(--rail-primary);
  color: var(--rail-surface);
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 160ms ease, border-color 160ms ease;
}

.ticket-search__submit:hover:not(:disabled) {
  border-color: var(--rail-primary-active);
  background: var(--rail-primary-active);
}

.ticket-search__submit:disabled {
  cursor: wait;
  opacity: 0.65;
}

.ticket-search__loader {
  animation: ticket-search-spin 0.8s linear infinite;
}

.ticket-search__status {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 12px 0 0;
  color: var(--rail-danger);
  font-size: 13px;
}

:deep(.ticket-search__select .ant-select-selector),
:deep(.ticket-search__picker.ant-picker) {
  height: 100% !important;
  border: 0 !important;
  border-radius: inherit !important;
  background: transparent !important;
  box-shadow: none !important;
}

:deep(.ticket-search__select .ant-select-selector) {
  padding: 0 12px 0 36px !important;
}

:deep(.ticket-search__select .ant-select-selection-search) {
  inset-inline-start: 36px !important;
  inset-inline-end: 10px !important;
}

:deep(.ticket-search__select .ant-select-selection-item),
:deep(.ticket-search__select .ant-select-selection-placeholder) {
  line-height: 46px !important;
}

:deep(.ticket-search__picker.ant-picker) {
  padding: 0 12px 0 40px !important;
}

:deep(.ticket-search__picker input),
:deep(.ticket-search__select .ant-select-selection-item) {
  color: var(--rail-ink) !important;
  font-weight: 600;
}

:deep(.ticket-search__picker input::placeholder),
:deep(.ticket-search__select .ant-select-selection-placeholder) {
  color: var(--rail-muted) !important;
  font-weight: 400;
}

@keyframes ticket-search-spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 920px) {
  .ticket-search__form {
    grid-template-columns: minmax(0, 1fr) minmax(190px, 0.65fr);
  }

  .ticket-search__submit {
    grid-column: 1 / -1;
  }
}

@media (max-width: 640px) {
  .ticket-search,
  .ticket-search--compact {
    padding: 16px;
  }

  .ticket-search__header {
    display: block;
  }

  .ticket-search__header p {
    margin-top: 8px;
  }

  .ticket-search__form {
    grid-template-columns: minmax(0, 1fr);
    gap: 14px;
  }

  .ticket-search__route {
    grid-template-columns: minmax(0, 1fr) 34px minmax(0, 1fr);
    gap: 5px;
  }

  .ticket-search__route::before {
    right: 22px;
    left: 22px;
  }

  .ticket-search__field label {
    font-size: 11px;
  }

  .ticket-search__swap {
    width: 32px;
    height: 32px;
    margin-bottom: 8px;
  }
}

@media (max-width: 430px) {
  .ticket-search__route {
    grid-template-columns: minmax(0, 1fr);
    gap: 12px;
  }

  .ticket-search__route::before {
    top: 58px;
    bottom: 58px;
    left: 18px;
    width: 2px;
    height: auto;
  }

  .ticket-search__swap {
    position: absolute;
    top: 74px;
    right: 8px;
    margin: 0;
    transform: rotate(90deg);
  }

  .ticket-search__swap:hover {
    transform: rotate(270deg);
  }
}

@media (prefers-reduced-motion: reduce) {
  .ticket-search__swap,
  .ticket-search__control,
  .ticket-search__submit {
    transition: none;
  }

  .ticket-search__swap:hover,
  .ticket-search__swap {
    transform: none;
  }
}
</style>
