<template>
  <section class="rounded-xl border border-line bg-white p-5 shadow-soft sm:p-6" aria-labelledby="passenger-title">
    <div class="mb-5 flex items-center justify-between gap-4 border-b border-line pb-4">
      <div>
        <span class="inline-flex rounded-md bg-primary-soft px-2.5 py-1 text-xs font-bold text-primary">第 3 站 · 乘车人</span>
        <h2 id="passenger-title" class="mt-2 text-lg font-bold text-navy">添加本次出行乘车人</h2>
      </div>
      <button
        type="button"
        class="inline-flex shrink-0 items-center rounded-lg bg-primary px-3 py-2 text-sm font-semibold text-white transition-colors hover:bg-primary-active focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:bg-secondary/40"
        :disabled="!selectedSeatType"
        @click="handleAddPassenger"
      >
        <svg class="mr-1.5 h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" /></svg>
        <span class="hidden sm:inline">添加乘车人</span>
        <span class="sm:hidden">添加</span>
      </button>
    </div>

    <div v-if="selectedPassengers.length === 0" class="rounded-lg border border-dashed border-line bg-[color-mix(in_srgb,var(--rail-primary)_3%,var(--rail-surface))] px-5 py-9 text-center">
      <svg class="mx-auto h-9 w-9 text-secondary/60" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" /></svg>
      <p class="mt-3 font-semibold text-secondary">{{ selectedSeatType ? '尚未添加乘车人' : '请先选择席别' }}</p>
      <p class="mt-1 text-sm text-secondary/80">{{ selectedSeatType ? '从常用联系人中选择本次乘车人' : '选择席别后，乘车人按钮将自动启用' }}</p>
    </div>

    <ol v-else class="space-y-3">
      <li
        v-for="(passenger, index) in selectedPassengers"
        :key="index"
        class="flex items-center justify-between gap-4 rounded-lg border border-line bg-[color-mix(in_srgb,var(--rail-primary)_3%,var(--rail-surface))] p-4 transition-colors hover:border-primary/25"
      >
        <div class="flex min-w-0 items-center gap-3">
          <span class="data-number flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-navy text-sm font-bold text-white">{{ index + 1 }}</span>
          <div class="min-w-0">
            <div class="flex flex-wrap items-center gap-2">
              <span class="font-bold text-ink">{{ passenger.name }}</span>
              <span class="rounded bg-primary-soft px-2 py-0.5 text-xs font-semibold text-primary-active">{{ passenger.passengerType === 1 ? '成人票' : '优惠票' }}</span>
            </div>
            <div class="mt-1 flex min-w-0 items-center gap-2 text-xs text-secondary sm:text-sm">
              <span>{{ passenger.passengerType === 1 ? '身份证' : '护照' }}</span>
              <span class="data-number truncate">{{ passenger.idCard }}</span>
            </div>
          </div>
        </div>
        <button
          type="button"
          class="shrink-0 rounded-lg p-2 text-secondary/80 transition-colors hover:bg-[#fff1f0] hover:text-[#cf3341] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#cf3341]"
          :aria-label="`移除乘车人${passenger.name}`"
          @click="handleRemovePassenger(index)"
        >
          <svg class="h-5 w-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" aria-hidden="true"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" /></svg>
        </button>
      </li>
    </ol>
  </section>
</template>

<script setup>
const props = defineProps({
  selectedPassengers: {
    type: Array,
    required: true
  },
  selectedSeatType: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['add-passenger', 'remove-passenger'])

const handleAddPassenger = () => {
  emit('add-passenger')
}

const handleRemovePassenger = (index) => {
  emit('remove-passenger', index)
}
</script>

<style scoped>
.data-number {
  font-family: "DIN Alternate", "Arial Narrow", Arial, sans-serif;
  font-variant-numeric: tabular-nums;
}
</style>
