<template>
  <section class="overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm" aria-labelledby="order-info-title">
    <header class="flex flex-col gap-2 border-b border-slate-200 px-5 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6">
      <div>
        <p class="mb-1 text-xs font-semibold uppercase tracking-wider text-primary">行程确认</p>
        <h3 id="order-info-title" class="text-lg font-bold text-ink">订单信息</h3>
      </div>
      <span class="font-mono text-xs text-slate-500">订单号 {{ orderInfo.orderNumber }}</span>
    </header>

    <div class="p-5 sm:p-6">
      <div class="rounded-xl border border-blue-100 bg-background p-4 sm:p-5">
        <div class="mb-5 flex items-center justify-between gap-3 border-b border-blue-100 pb-3">
          <div class="flex items-center gap-3">
            <span class="rounded-md bg-navy px-2.5 py-1 font-mono text-lg font-bold text-white">{{ orderInfo.trainCode }}</span>
            <span class="text-sm font-medium text-slate-600">{{ formatDate(orderInfo.date) }}</span>
          </div>
          <span class="rounded-full border border-blue-200 bg-white px-2.5 py-1 text-xs font-medium text-primary">
            {{ orderInfo.seatType?.name || orderInfo.seatTypeName || '二等座' }}
          </span>
        </div>

        <div class="grid grid-cols-[1fr_auto_1fr] items-center gap-3 sm:gap-6">
          <div>
            <p class="font-mono text-2xl font-bold tabular-nums text-ink sm:text-3xl">{{ formatTime(orderInfo.startTime) }}</p>
            <p class="mt-1 text-sm font-semibold text-slate-700">{{ orderInfo.originStation?.name || '出发站' }}</p>
          </div>
          <div class="flex min-w-[76px] flex-col items-center" aria-hidden="true">
            <span class="mb-1 text-[10px] text-slate-400">直达</span>
            <span class="relative block h-px w-full bg-primary after:absolute after:-right-0.5 after:-top-[3px] after:h-2 after:w-2 after:rotate-45 after:border-r after:border-t after:border-primary" />
          </div>
          <div class="text-right">
            <p class="font-mono text-2xl font-bold tabular-nums text-ink sm:text-3xl">{{ formatTime(orderInfo.endTime) }}</p>
            <p class="mt-1 text-sm font-semibold text-slate-700">{{ orderInfo.destinationStation?.name || '到达站' }}</p>
          </div>
        </div>
      </div>

      <div class="mt-6">
        <h4 class="mb-3 flex items-center gap-2 text-sm font-bold text-ink">
          <span class="h-4 w-1 rounded-full bg-primary" />
          乘车人
        </h4>
        <div class="divide-y divide-slate-100 rounded-lg border border-slate-200">
          <div
            v-for="(passenger, index) in orderInfo.passengers"
            :key="index"
            class="flex flex-col gap-3 p-4 sm:flex-row sm:items-center sm:justify-between"
          >
            <div>
              <div class="flex items-center gap-2">
                <span class="font-semibold text-ink">{{ passenger.name }}</span>
                <span class="rounded bg-primary-soft px-2 py-0.5 text-xs font-medium text-primary">{{ getPassengerTypeText(passenger.passengerType) }}</span>
              </div>
              <p class="mt-1 text-sm text-slate-500">
                {{ orderInfo.seatType?.name || orderInfo.seatTypeName || '二等座' }}
                <span v-if="passenger.seatPosition"> · {{ passenger.seatPosition }}座</span>
              </p>
            </div>
            <div class="text-left sm:text-right">
              <p v-if="passenger.discount > 0" class="text-xs text-slate-400 line-through">原价 ¥{{ formatAmount(passenger.basePrice) }}</p>
              <p class="font-mono text-lg font-bold tabular-nums text-ink">¥{{ formatAmount(passenger.actualPrice ?? orderInfo.ticketPrice) }}</p>
            </div>
          </div>
          <div v-if="!orderInfo.passengers?.length" class="p-4 text-sm text-slate-500">暂无乘车人信息</div>
        </div>
      </div>

      <dl class="mt-6 grid gap-3 border-t border-dashed border-slate-200 pt-5 text-sm sm:grid-cols-2">
        <div class="flex justify-between gap-4"><dt class="text-slate-500">订单状态</dt><dd class="font-medium text-slate-800">{{ getOrderStatusText(orderInfo.status) }}</dd></div>
        <div class="flex justify-between gap-4"><dt class="text-slate-500">创建时间</dt><dd class="font-mono text-slate-800">{{ formatDateTime(orderInfo.createTime) }}</dd></div>
        <div class="flex justify-between gap-4"><dt class="text-slate-500">乘车人数</dt><dd class="font-medium text-slate-800">{{ orderInfo.passengers?.length || 0 }} 人</dd></div>
        <div v-if="orderInfo.discountAmount > 0" class="flex justify-between gap-4"><dt class="text-slate-500">优惠金额</dt><dd class="font-medium text-emerald-600">-¥{{ formatAmount(orderInfo.discountAmount) }}</dd></div>
      </dl>

      <div class="mt-5 flex items-end justify-between border-t border-slate-200 pt-5">
        <span class="text-base font-semibold text-ink">订单应付</span>
        <span class="font-mono text-3xl font-bold tabular-nums text-action">¥{{ formatAmount(orderInfo.totalAmount) }}</span>
      </div>
    </div>
  </section>
</template>

<script setup>
defineProps({
  orderInfo: {
    type: Object,
    required: true
  }
})

const toDate = (dateStr) => {
  const date = new Date(dateStr)
  return Number.isNaN(date.getTime()) ? null : date
}

const formatDate = (dateStr) => {
  const date = toDate(dateStr)
  if (!date) return '--'
  const weekdays = ['日', '一', '二', '三', '四', '五', '六']
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} 周${weekdays[date.getDay()]}`
}

const formatTime = (dateStr) => {
  const date = toDate(dateStr)
  if (!date) return '--:--'
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatDateTime = (dateStr) => {
  const date = toDate(dateStr)
  if (!date) return '--'
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const formatAmount = (amount) => Number(amount || 0).toFixed(2)

const getOrderStatusText = (status) => ({ 0: '待支付', 1: '已支付', 2: '已取消', 3: '已退款' })[status] || '未知状态'
const getPassengerTypeText = (type) => ({ 1: '成人', 2: '儿童', 3: '学生', 4: '老人' })[type] || '成人'
</script>
