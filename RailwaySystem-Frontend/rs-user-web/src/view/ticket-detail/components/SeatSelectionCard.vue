<template>
  <section class="rounded-xl border border-line bg-white p-5 shadow-soft sm:p-6" aria-labelledby="seat-selection-title">
    <div class="mb-5 flex items-start justify-between gap-4 border-b border-line pb-4">
      <div>
        <span class="inline-flex rounded-md bg-primary-soft px-2.5 py-1 text-xs font-bold text-primary">第 2 站 · 席别</span>
        <h2 id="seat-selection-title" class="mt-2 text-lg font-bold text-navy">选择席别与座位偏好</h2>
      </div>
      <p class="hidden text-right text-xs leading-5 text-secondary/80 sm:block">单次最多选择 5 张车票<br />座位偏好以实际出票为准</p>
    </div>

    <div v-if="seatTypes.length" class="space-y-3">
      <div
        v-for="seatType in seatTypes"
        :key="seatType.type"
        class="w-full rounded-lg border p-4 transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 sm:p-5"
        :class="{
          'cursor-pointer border-primary bg-[color-mix(in_srgb,var(--rail-primary)_5%,var(--rail-surface))] shadow-[inset_4px_0_0_var(--rail-primary)]': selectedSeatType?.type === seatType.type,
          'cursor-pointer border-line hover:border-primary-hover': selectedSeatType?.type !== seatType.type && seatType.remainingSeats > 0,
          'cursor-not-allowed border-line/70 bg-background opacity-70': seatType.remainingSeats === 0,
        }"
        role="button"
        :tabindex="seatType.remainingSeats > 0 ? 0 : -1"
        :aria-pressed="selectedSeatType?.type === seatType.type"
        :aria-disabled="seatType.remainingSeats === 0"
        @click="handleSelectSeatType(seatType)"
        @keydown.enter="handleSelectSeatType(seatType)"
        @keydown.space.prevent="handleSelectSeatType(seatType)"
      >
        <div class="flex items-start justify-between gap-4">
          <div class="min-w-0">
            <div class="flex flex-wrap items-center gap-2">
              <span class="font-bold text-ink">{{ seatType.name }}</span>
              <span
                class="rounded px-2 py-0.5 text-xs font-semibold"
                :class="seatType.remainingSeats > 0 ? 'bg-[#edf8f2] text-[#16794b]' : 'bg-background text-secondary/80'"
              >
                {{ seatType.remainingSeats > 0 ? `余票 ${seatType.remainingSeats} 张` : '已售罄' }}
              </span>
            </div>
            <p class="mt-2 text-xs leading-5 text-secondary sm:text-sm">{{ seatType.features || '舒适座席，具体座位以出票结果为准' }}</p>
          </div>
          <div class="shrink-0 text-right">
            <span class="data-number text-xl font-bold text-action">￥{{ seatType.price }}</span>
            <p class="text-xs text-secondary/80">每人</p>
          </div>
        </div>

        <div
          v-if="selectedSeatType?.type === seatType.type && seatPositions[seatType.type]?.length"
          class="mt-5 border-t border-primary/20 pt-5"
          @click.stop
          @keydown.stop
        >
          <div class="mb-3 flex flex-wrap items-center justify-between gap-2">
            <p class="text-sm font-bold text-secondary">选择座位位置</p>
            <p class="text-xs text-secondary/80">已选 {{ selectedPositions.length }} / 5</p>
          </div>
          <div class="grid grid-cols-2 gap-2 sm:grid-cols-[repeat(auto-fit,minmax(124px,1fr))]">
            <div
              v-for="position in seatPositions[seatType.type]"
              :key="position.code"
              class="relative rounded-lg border p-3 text-center transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
              :class="{
                'cursor-pointer border-primary bg-white': getPositionCount(position.code) > 0,
                'cursor-pointer border-line bg-white hover:border-primary-hover hover:bg-[color-mix(in_srgb,var(--rail-primary)_3%,var(--rail-surface))]': getPositionCount(position.code) === 0 && position.remainingSeats > 0,
                'cursor-not-allowed border-line/70 bg-background opacity-60': position.remainingSeats === 0,
              }"
              role="button"
              :tabindex="position.remainingSeats > 0 ? 0 : -1"
              :aria-label="`${position.name}${position.code}座，余票${position.remainingSeats}张，已选${getPositionCount(position.code)}张`"
              :aria-disabled="position.remainingSeats === 0"
              @click="handleMainClick(position.code)"
              @keydown.enter="handleMainClick(position.code)"
              @keydown.space.prevent="handleMainClick(position.code)"
            >
              <div
                v-if="getPositionCount(position.code) > 0"
                class="absolute -right-2 -top-3 z-20 flex overflow-hidden rounded-lg border border-primary/25 bg-white shadow-sm"
                @click.stop
              >
                <button
                  type="button"
                  class="flex h-7 w-7 items-center justify-center text-secondary transition-colors hover:bg-primary-soft focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-primary"
                  :aria-label="`减少一个${position.name}座`"
                  @click="handleUpdateCount(position.code, -1)"
                >
                  −
                </button>
                <span class="data-number flex h-7 w-7 select-none items-center justify-center bg-primary-soft text-sm font-bold text-primary">{{ getPositionCount(position.code) }}</span>
                <button
                  type="button"
                  class="flex h-7 w-7 items-center justify-center font-bold text-primary transition-colors hover:bg-primary-soft focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-inset focus-visible:ring-primary"
                  :aria-label="`增加一个${position.name}座`"
                  @click="handleUpdateCount(position.code, 1)"
                >
                  +
                </button>
              </div>
              <div class="mb-2 flex items-center justify-center gap-2">
                <span class="data-number min-w-7 rounded border border-primary/25 bg-primary-soft px-2 text-lg font-bold text-primary">{{ position.code }}</span>
                <span class="text-sm font-semibold text-secondary">{{ position.name }}</span>
              </div>
              <p class="data-number text-xs font-medium" :class="position.remainingSeats > 0 ? 'text-[#16794b]' : 'text-secondary/80'">余 {{ position.remainingSeats }} 张</p>
            </div>
          </div>
        </div>

        <p
          v-else-if="selectedSeatType?.type === seatType.type"
          class="mt-4 rounded-lg border border-dashed border-line bg-white px-4 py-3 text-sm text-secondary"
        >
          暂无座位位置数据，可稍后重新选择该席别刷新。
        </p>
      </div>
    </div>

    <div v-else class="rounded-lg border border-dashed border-line bg-[color-mix(in_srgb,var(--rail-primary)_3%,var(--rail-surface))] px-5 py-10 text-center">
      <p class="font-semibold text-secondary">当前车次暂无可选席别</p>
      <p class="mt-1 text-sm text-secondary/80">请返回车票列表选择其他车次</p>
    </div>
  </section>
</template>

<script setup>
const props = defineProps({
  seatTypes: {
    type: Array,
    required: true
  },
  selectedSeatType: {
    type: Object,
    default: null
  },
  seatPositions: {
    type: Object,
    default: () => ({})
  },
  selectedPositions: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['select-seat-type', 'update-position-count'])

const handleSelectSeatType = (seatType) => {
  emit('select-seat-type', seatType)
}

const handleUpdateCount = (positionCode, delta) => {
  emit('update-position-count', positionCode, delta)
}

const handleMainClick = (positionCode) => {
  // 只有当数量为0时，点击卡片主体才增加
  if (getPositionCount(positionCode) === 0) {
    handleUpdateCount(positionCode, 1)
  }
}

const getPositionCount = (positionCode) => {
  return props.selectedPositions.filter(p => p === positionCode).length
}
</script>

<style scoped>
.data-number {
  font-family: "DIN Alternate", "Arial Narrow", Arial, sans-serif;
  font-variant-numeric: tabular-nums;
}
</style>
