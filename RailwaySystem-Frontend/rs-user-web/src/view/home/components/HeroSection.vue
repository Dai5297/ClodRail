<template>
  <section class="rail-hero" aria-labelledby="home-hero-title">
    <div class="rail-hero__grid" aria-hidden="true"></div>
    <svg class="rail-scene" viewBox="0 0 1440 480" preserveAspectRatio="xMidYMax slice" aria-hidden="true">
      <path class="rail-scene__distant" d="M-40 366 C 240 302, 430 398, 730 326 S 1180 278, 1510 335" />
      <path class="rail-scene__track" d="M-60 438 C 270 342, 480 462, 770 372 S 1160 304, 1510 354" />
      <path class="rail-scene__track rail-scene__track--second" d="M-65 466 C 280 362, 488 486, 780 395 S 1172 326, 1515 377" />
      <g class="rail-scene__train" transform="translate(930 250)">
        <g class="rail-scene__train-motion">
          <path d="M22 84h226c21 0 43 7 62 21l51 38H0l6-39c2-12 7-20 16-20Z" />
          <path d="M82 99h145l24 22H72z" class="rail-scene__window" />
          <path d="m248 99 56 22h-38z" class="rail-scene__window" />
          <circle cx="78" cy="145" r="9" />
          <circle cx="277" cy="145" r="9" />
        </g>
      </g>
    </svg>

    <div class="rail-hero__inner">
      <div class="rail-hero__copy">
        <p class="rail-hero__eyebrow"><span></span> ClodRail 铁路出行</p>
        <h1 id="home-hero-title">下一程，从这里出发</h1>
        <p class="rail-hero__description">查询全国铁路车次与余票，选好日期和车站，即刻规划行程。</p>
        <div class="rail-hero__promise" aria-label="服务特点">
          <span><i class="ri-flashlight-line" aria-hidden="true"></i> 实时查询</span>
          <span><i class="ri-shield-check-line" aria-hidden="true"></i> 安心购票</span>
          <span><i class="ri-customer-service-2-line" aria-hidden="true"></i> 全程服务</span>
        </div>
      </div>

      <div class="search-card">
        <div class="search-card__heading">
          <div>
            <p>单程车票</p>
            <h2>查询你的下一趟列车</h2>
          </div>
          <span class="search-card__status"><i aria-hidden="true"></i> 车次信息实时更新</span>
        </div>

        <div v-if="stationLoading" class="station-state" role="status" aria-live="polite">
          <span class="station-state__spinner" aria-hidden="true"></span>
          正在加载车站，请稍候
        </div>

        <div v-else-if="stationError" class="station-state station-state--error" role="alert">
          <i class="ri-error-warning-line" aria-hidden="true"></i>
          <span>{{ stationError }}</span>
          <button type="button" @click="loadHotStations">重新加载</button>
        </div>

        <div v-else-if="!stationOptions.length" class="station-state station-state--empty" role="status">
          <i class="ri-map-pin-line" aria-hidden="true"></i>
          <span>暂无可用车站，请重新加载</span>
          <button type="button" @click="loadHotStations">重新加载</button>
        </div>

        <div class="journey-line" aria-hidden="true">
          <span class="journey-line__station"></span>
          <span class="journey-line__track"></span>
          <i class="ri-train-line"></i>
          <span class="journey-line__track"></span>
          <span class="journey-line__station journey-line__station--arrival"></span>
        </div>

        <div class="search-form">
          <div class="search-field search-field--departure">
            <label for="home-departure">出发站</label>
            <div class="search-control">
              <i class="ri-map-pin-2-line" aria-hidden="true"></i>
              <a-select
                id="home-departure"
                v-model:value="searchForm.departure"
                show-search
                placeholder="请选择出发站"
                class="station-select"
                :options="stationOptions"
                :filter-option="filterOption"
                :loading="stationLoading"
                :disabled="stationLoading || !stationOptions.length"
                :bordered="false"
                not-found-content="未找到匹配车站"
                @change="handleDepartureChange"
              >
                <template #option="{ label, code }">
                  <div class="station-option">
                    <span>{{ label }}</span>
                    <span>{{ code }}</span>
                  </div>
                </template>
              </a-select>
            </div>
          </div>

          <button
            type="button"
            class="swap-button"
            :disabled="!searchForm.departureId || !searchForm.destinationId"
            aria-label="交换出发站和到达站"
            title="交换出发站和到达站"
            @click="exchangeCities"
          >
            <i class="ri-arrow-left-right-line" aria-hidden="true"></i>
          </button>

          <div class="search-field search-field--destination">
            <label for="home-destination">到达站</label>
            <div class="search-control">
              <i class="ri-map-pin-time-line" aria-hidden="true"></i>
              <a-select
                id="home-destination"
                v-model:value="searchForm.destination"
                show-search
                placeholder="请选择到达站"
                class="station-select"
                :options="stationOptions"
                :filter-option="filterOption"
                :loading="stationLoading"
                :disabled="stationLoading || !stationOptions.length"
                :bordered="false"
                not-found-content="未找到匹配车站"
                @change="handleDestinationChange"
              >
                <template #option="{ label, code }">
                  <div class="station-option">
                    <span>{{ label }}</span>
                    <span>{{ code }}</span>
                  </div>
                </template>
              </a-select>
            </div>
          </div>

          <div class="search-field search-field--date">
            <label for="home-departure-date">出发日期</label>
            <div class="search-control">
              <i class="ri-calendar-line" aria-hidden="true"></i>
              <a-date-picker
                id="home-departure-date"
                v-model:value="searchForm.departureDate"
                class="journey-datepicker"
                :bordered="false"
                placeholder="选择日期"
                :disabled-date="disabledDate"
                value-format="YYYY-MM-DD"
                :show-suffix-icon="false"
              />
            </div>
          </div>

          <button
            type="button"
            class="search-button"
            :disabled="searchLoading || stationLoading || !stationOptions.length"
            @click="handleSearch"
          >
            <span v-if="searchLoading" class="search-button__spinner" aria-hidden="true"></span>
            <i v-else class="ri-search-line" aria-hidden="true"></i>
            {{ searchLoading ? '正在查询' : '查询车票' }}
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { getHotStations } from '@/api/station'
import { message } from 'ant-design-vue'
import { pinyin } from 'pinyin-pro'
import dayjs from 'dayjs'

const router = useRouter()
const searchLoading = ref(false)
const stationLoading = ref(false)
const stationError = ref('')

const searchForm = reactive({
  departure: undefined,
  destination: undefined,
  departureId: null,
  destinationId: null,
  departureDate: undefined,
})

const hotStations = ref([])

const fallbackStations = [
  { id: 1, name: '北京', code: 'BJP' },
  { id: 2, name: '上海', code: 'SHH' },
  { id: 3, name: '广州', code: 'GZQ' },
  { id: 4, name: '深圳', code: 'SZQ' },
  { id: 5, name: '成都', code: 'CDW' },
  { id: 6, name: '重庆', code: 'CQW' },
]

const stationOptions = computed(() =>
  hotStations.value.map((station) => ({
    value: station.name,
    label: station.name,
    code: station.code,
    id: station.id,
  })),
)

const filterOption = (input, option) => {
  const query = input.toLowerCase()
  const name = String(option.label || '').toLowerCase()
  const code = String(option.code || '').toLowerCase()
  const pinyinFirst = pinyin(option.label || '', { pattern: 'first', toneType: 'none' }).toLowerCase()
  const pinyinFull = pinyin(option.label || '', { toneType: 'none' }).toLowerCase()

  return name.includes(query) || code.includes(query) || pinyinFirst.includes(query) || pinyinFull.includes(query)
}

const handleDepartureChange = (value, option) => {
  searchForm.departureId = option.id
}

const handleDestinationChange = (value, option) => {
  searchForm.destinationId = option.id
}

const loadHotStations = async () => {
  stationLoading.value = true
  stationError.value = ''

  try {
    const response = await getHotStations()
    if (response.code !== 200 || !Array.isArray(response.data)) {
      throw new Error('invalid station response')
    }

    hotStations.value = response.data
    if (hotStations.value.length >= 2) {
      const [start, end] = hotStations.value
      searchForm.departure = start.name
      searchForm.departureId = start.id
      searchForm.destination = end.name
      searchForm.destinationId = end.id
    }
  } catch (error) {
    hotStations.value = fallbackStations
    searchForm.departure = fallbackStations[0].name
    searchForm.departureId = fallbackStations[0].id
    searchForm.destination = fallbackStations[1].name
    searchForm.destinationId = fallbackStations[1].id
    stationError.value = ''
    return
    stationError.value = '车站数据加载失败，请检查网络后重试'
    message.error('加载车站数据失败')
  } finally {
    stationLoading.value = false
  }
}

const exchangeCities = () => {
  const tempName = searchForm.departure
  const tempId = searchForm.departureId

  searchForm.departure = searchForm.destination
  searchForm.departureId = searchForm.destinationId
  searchForm.destination = tempName
  searchForm.destinationId = tempId
}

const disabledDate = (current) => current && current < dayjs().startOf('day')

const handleSearch = async () => {
  if (!searchForm.departureId) {
    message.error('请选择出发站')
    return
  }
  if (!searchForm.destinationId) {
    message.error('请选择到达站')
    return
  }
  if (searchForm.departureId === searchForm.destinationId) {
    message.error('出发站和到达站不能相同')
    return
  }
  if (!searchForm.departureDate) {
    message.error('请选择出发日期')
    return
  }

  searchLoading.value = true
  try {
    await router.push({
      path: '/ticket',
      query: {
        originStationId: searchForm.departureId,
        destinationStationId: searchForm.destinationId,
        date: searchForm.departureDate,
      },
    })
  } finally {
    searchLoading.value = false
  }
}

onMounted(async () => {
  searchForm.departureDate = dayjs().format('YYYY-MM-DD')
  await loadHotStations()
})
</script>

<style scoped>
.rail-hero {
  position: relative;
  min-height: 620px;
  overflow: hidden;
  background: var(--rail-navy);
  color: #fff;
}

.rail-hero::before {
  position: absolute;
  top: 0;
  right: 0;
  width: 42%;
  height: 5px;
  background: var(--rail-action);
  content: '';
}

.rail-hero__grid {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(255, 255, 255, 0.035) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.035) 1px, transparent 1px);
  background-size: 48px 48px;
  mask-image: linear-gradient(to bottom, #000 0%, transparent 80%);
}

.rail-scene {
  position: absolute;
  right: -5%;
  bottom: 0;
  width: min(72vw, 1050px);
  height: 440px;
  opacity: 0.7;
}

.rail-scene__distant,
.rail-scene__track {
  fill: none;
  stroke-linecap: round;
}

.rail-scene__distant {
  stroke: rgba(126, 168, 224, 0.24);
  stroke-width: 2;
}

.rail-scene__track {
  stroke: rgba(255, 255, 255, 0.22);
  stroke-width: 5;
}

.rail-scene__track--second {
  stroke: rgba(255, 255, 255, 0.11);
  stroke-width: 2;
  stroke-dasharray: 14 20;
}

.rail-scene__train-motion {
  fill: rgba(255, 255, 255, 0.13);
  stroke: rgba(255, 255, 255, 0.34);
  stroke-width: 2;
  animation: train-arrive 900ms ease-out both;
}

.rail-scene__window {
  fill: rgba(89, 158, 242, 0.26);
  stroke: rgba(255, 255, 255, 0.2);
}

.rail-hero__inner {
  position: relative;
  z-index: 2;
  width: min(100% - 32px, 1200px);
  margin: 0 auto;
  padding: 72px 0 64px;
}

.rail-hero__copy {
  max-width: 660px;
}

.rail-hero__eyebrow {
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0 0 18px;
  color: #b9d5f5;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.16em;
  text-transform: uppercase;
}

.rail-hero__eyebrow span {
  width: 28px;
  height: 2px;
  background: var(--rail-action);
}

.rail-hero h1 {
  max-width: 640px;
  margin: 0;
  font-size: clamp(42px, 5vw, 68px);
  font-weight: 800;
  letter-spacing: -0.045em;
  line-height: 1.08;
}

.rail-hero__description {
  max-width: 590px;
  margin: 20px 0 0;
  color: #d5e4f5;
  font-size: clamp(16px, 2vw, 19px);
  line-height: 1.75;
}

.rail-hero__promise {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-top: 24px;
  color: #b9cbe0;
  font-size: 13px;
}

.rail-hero__promise span {
  display: inline-flex;
  align-items: center;
  gap: 7px;
}

.rail-hero__promise i {
  color: #78b4ff;
  font-size: 16px;
}

.search-card {
  position: relative;
  margin-top: 48px;
  padding: clamp(22px, 3vw, 32px);
  border-radius: var(--rail-hero-radius);
  background: #fff;
  box-shadow: 0 24px 56px rgba(1, 15, 43, 0.28);
  color: var(--rail-ink);
  animation: search-card-enter 500ms 120ms ease-out both;
}

.search-card__heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.search-card__heading p {
  margin: 0 0 5px;
  color: var(--rail-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.search-card__heading h2 {
  margin: 0;
  color: var(--rail-navy);
  font-size: clamp(20px, 2.5vw, 26px);
  font-weight: 750;
}

.search-card__status {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding-top: 8px;
  color: var(--rail-muted);
  font-size: 12px;
}

.search-card__status i {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: var(--rail-success);
  box-shadow: 0 0 0 4px rgba(22, 160, 106, 0.12);
}

.station-state {
  display: flex;
  align-items: center;
  gap: 9px;
  min-height: 38px;
  margin: -8px 0 18px;
  padding: 8px 12px;
  border: 1px solid #cfe2fb;
  border-radius: 8px;
  background: #f5f9ff;
  color: #315278;
  font-size: 13px;
}

.station-state--error {
  border-color: #ffd1d5;
  background: #fff7f7;
  color: var(--rail-danger);
}

.station-state--empty {
  border-color: var(--rail-line);
  background: var(--rail-background);
  color: var(--rail-muted);
}

.station-state button {
  margin-left: auto;
  padding: 4px 10px;
  border: 1px solid currentColor;
  border-radius: 6px;
  background: #fff;
  color: inherit;
  cursor: pointer;
  font-size: 12px;
  font-weight: 700;
}

.station-state__spinner,
.search-button__spinner {
  width: 15px;
  height: 15px;
  border: 2px solid currentColor;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 700ms linear infinite;
}

.journey-line {
  display: grid;
  grid-template-columns: 12px 1fr 24px 1fr 12px;
  align-items: center;
  width: calc(58.333% - 8px);
  margin: 0 0 8px 18px;
  color: var(--rail-primary);
}

.journey-line__station {
  width: 11px;
  height: 11px;
  border: 3px solid var(--rail-primary);
  border-radius: 50%;
  background: #fff;
}

.journey-line__station--arrival {
  border-color: var(--rail-action);
}

.journey-line__track {
  height: 1px;
  background: repeating-linear-gradient(90deg, #9bc4f5 0 8px, transparent 8px 13px);
}

.journey-line i {
  text-align: center;
  font-size: 18px;
}

.search-form {
  display: grid;
  grid-template-columns: minmax(0, 1.6fr) 44px minmax(0, 1.6fr) minmax(190px, 1.15fr) minmax(142px, 0.9fr);
  gap: 12px;
  align-items: end;
}

.search-field {
  min-width: 0;
}

.search-field label {
  display: block;
  margin: 0 0 8px 2px;
  color: #667085;
  font-size: 12px;
  font-weight: 700;
}

.search-control {
  position: relative;
  display: flex;
  align-items: center;
  height: 54px;
  border: 1px solid var(--rail-line);
  border-radius: var(--rail-control-radius);
  background: #fff;
  transition: border-color 150ms ease, box-shadow 150ms ease;
}

.search-control:focus-within {
  border-color: var(--rail-primary);
  box-shadow: var(--rail-focus-ring);
}

.search-control > i {
  position: absolute;
  z-index: 2;
  left: 15px;
  color: var(--rail-primary);
  font-size: 19px;
  pointer-events: none;
}

.station-select,
.journey-datepicker {
  width: 100%;
  height: 100%;
}

.swap-button {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  margin: 0 auto 9px;
  border: 1px solid #bad7ff;
  border-radius: 50%;
  background: var(--rail-primary-soft);
  color: var(--rail-primary);
  cursor: pointer;
  font-size: 17px;
  transition: background-color 150ms ease, transform 180ms ease;
}

.swap-button:hover:not(:disabled) {
  background: #dcecff;
  transform: rotate(180deg);
}

.swap-button:disabled {
  border-color: var(--rail-line);
  background: #f4f6f8;
  color: #a7b0bd;
  cursor: not-allowed;
}

.search-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 54px;
  border: 1px solid var(--rail-action);
  border-radius: var(--rail-control-radius);
  background: var(--rail-action);
  box-shadow: 0 8px 18px rgba(255, 130, 0, 0.2);
  color: #fff;
  cursor: pointer;
  font-size: 15px;
  font-weight: 750;
  transition: background-color 150ms ease, border-color 150ms ease, transform 150ms ease;
}

.search-button:hover:not(:disabled) {
  border-color: var(--rail-action-hover);
  background: var(--rail-action-hover);
  transform: translateY(-1px);
}

.search-button:disabled {
  border-color: #c9d1dc;
  background: #c9d1dc;
  box-shadow: none;
  cursor: not-allowed;
}

.station-option {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  width: 100%;
}

.station-option span:last-child {
  color: #98a2b3;
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 12px;
}

.station-select :deep(.ant-select-selector) {
  display: flex !important;
  align-items: center !important;
  height: 100% !important;
  padding: 0 12px 0 44px !important;
  background: transparent !important;
  box-shadow: none !important;
}

.station-select :deep(.ant-select-selection-search) {
  inset-inline-start: 44px !important;
}

.station-select :deep(.ant-select-selection-item),
.station-select :deep(.ant-select-selection-placeholder) {
  font-size: 16px;
}

.station-select :deep(.ant-select-selection-item) {
  color: var(--rail-ink);
  font-weight: 650;
}

.search-card :deep(.journey-datepicker.ant-picker) {
  width: 100%;
  height: 100%;
  padding: 0 12px 0 44px;
  background: transparent;
  box-shadow: none;
}

.search-card :deep(.journey-datepicker .ant-picker-input > input) {
  color: var(--rail-ink);
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 16px;
  font-weight: 650;
}

@keyframes train-arrive {
  from { opacity: 0; transform: translate(80px, 0); }
  to { opacity: 1; transform: translate(0, 0); }
}

@keyframes search-card-enter {
  from { opacity: 0; transform: translateY(16px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 1024px) {
  .search-form {
    grid-template-columns: minmax(0, 1fr) 44px minmax(0, 1fr) minmax(180px, 0.9fr);
  }

  .search-button {
    grid-column: 1 / -1;
  }

  .journey-line {
    width: calc(66.666% - 2px);
  }
}

@media (max-width: 767px) {
  .rail-hero {
    min-height: 0;
  }

  .rail-hero__inner {
    padding: 48px 0 40px;
  }

  .rail-scene {
    right: -45%;
    width: 130vw;
    opacity: 0.4;
  }

  .rail-hero h1 {
    font-size: clamp(38px, 11vw, 52px);
  }

  .rail-hero__promise {
    gap: 12px 18px;
  }

  .search-card {
    margin-top: 34px;
  }

  .search-card__heading {
    margin-bottom: 20px;
  }

  .search-card__status,
  .journey-line {
    display: none;
  }

  .search-form {
    grid-template-columns: 1fr 44px 1fr;
  }

  .search-field--date,
  .search-button {
    grid-column: 1 / -1;
  }
}

@media (max-width: 520px) {
  .rail-hero__inner {
    width: min(100% - 24px, 1200px);
  }

  .rail-hero__description {
    font-size: 16px;
  }

  .search-card {
    padding: 20px 16px;
    border-radius: 12px;
  }

  .search-form {
    grid-template-columns: 1fr;
    gap: 14px;
  }

  .swap-button {
    justify-self: end;
    margin: -5px 10px -5px 0;
    transform: rotate(90deg);
  }

  .swap-button:hover:not(:disabled) {
    transform: rotate(270deg);
  }

  .search-field--date,
  .search-button {
    grid-column: auto;
  }

  .station-state {
    align-items: flex-start;
    flex-wrap: wrap;
  }

  .station-state button {
    margin-left: 24px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .rail-scene__train-motion,
  .search-card,
  .station-state__spinner,
  .search-button__spinner {
    animation: none;
  }

  .swap-button:hover:not(:disabled),
  .search-button:hover:not(:disabled) {
    transform: none;
  }
}

.rail-hero {
  min-height: 455px;
  overflow: visible;
  background:
    linear-gradient(180deg, rgba(3, 24, 68, 0.02), rgba(3, 24, 68, 0.2)),
    url('/images/cloudrail-home-hero-v2.png') center 46% / cover no-repeat,
    #041f4d;
}

.rail-hero::before {
  inset: 0;
  width: auto;
  height: auto;
  background: linear-gradient(180deg, transparent 58%, rgba(2, 18, 48, 0.34));
}

.rail-hero::after {
  display: none;
}

.rail-hero__grid,
.rail-scene {
  display: none;
}

.rail-scene {
  right: 0;
  bottom: 14px;
  left: 0;
  width: 100%;
  height: 360px;
  opacity: 0.82;
}

.rail-scene__train {
  display: none;
}

.rail-scene__track {
  stroke: rgba(123, 174, 255, 0.58);
  stroke-width: 4;
}

.rail-scene__track--second {
  stroke: rgba(255, 255, 255, 0.32);
}

.rail-hero__inner {
  width: min(100% - 112px, 1400px);
  min-height: 455px;
  padding: 0;
  display: flex;
  align-items: flex-end;
}

.rail-hero__copy {
  display: none;
}

.search-card {
  position: relative;
  z-index: 3;
  width: 100%;
  margin: 0 auto -78px;
  padding: 24px 42px 26px;
  border: 1px solid rgba(221, 230, 240, 0.92);
  border-radius: 12px;
  box-shadow: 0 12px 30px rgba(11, 37, 89, 0.14);
}

.search-card__heading {
  margin-bottom: 18px;
}

.search-card__heading p,
.search-card__status,
.journey-line {
  display: none;
}

.search-card__heading h2 {
  font-size: 0;
}

.search-card__heading h2::after {
  color: #0b2559;
  content: '车票查询';
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 0;
}

.search-form {
  grid-template-columns: minmax(0, 1.5fr) 44px minmax(0, 1.5fr) minmax(210px, 0.95fr) minmax(150px, 0.72fr);
  gap: 22px;
}

.search-field--departure,
.search-field--destination {
  position: relative;
}

.search-field--departure::after {
  position: absolute;
  right: -12px;
  bottom: 27px;
  left: calc(100% - 8px);
  height: 2px;
  background: #1677ff;
  content: '';
}

.search-control {
  height: 58px;
  border-color: transparent;
  border-bottom-color: #dde6f0;
  border-radius: 0;
  background: transparent;
}

.search-control:focus-within {
  border-color: transparent;
  border-bottom-color: #1677ff;
  box-shadow: none;
}

.search-control > i {
  left: 0;
}

.station-select :deep(.ant-select-selector),
.search-card :deep(.journey-datepicker.ant-picker) {
  padding-left: 30px !important;
}

.station-select :deep(.ant-select-selection-search) {
  inset-inline-start: 30px !important;
}

.station-select :deep(.ant-select-selection-item),
.search-card :deep(.journey-datepicker .ant-picker-input > input) {
  color: #0b1326;
  font-size: 24px;
  font-weight: 800;
}

.swap-button {
  width: 42px;
  height: 42px;
  margin-bottom: 8px;
  border-color: #1677ff;
  background: #ffffff;
  box-shadow: 0 0 0 4px #eaf3ff;
}

.search-button {
  height: 58px;
  border-radius: 8px;
  font-size: 22px;
}

@media (max-width: 1024px) {
  .rail-hero__inner {
    width: min(100% - 32px, 1200px);
  }

  .search-form {
    grid-template-columns: minmax(0, 1fr) 44px minmax(0, 1fr);
  }
}

@media (max-width: 767px) {
  .rail-hero {
    min-height: 620px;
  }

  .rail-hero__inner {
    min-height: 620px;
    width: min(100% - 24px, 1200px);
  }

  .rail-hero::after {
    top: 88px;
    right: -190px;
    width: 620px;
  }

  .search-card {
    margin-bottom: -120px;
    padding: 22px 16px;
  }

  .search-form {
    grid-template-columns: 1fr;
    gap: 14px;
  }

  .search-field--departure::after {
    display: none;
  }

  .swap-button {
    justify-self: end;
    margin: -4px 10px -4px 0;
  }
}
</style>
