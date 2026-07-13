<template>
  <article class="result-card" :aria-busy="loading">
    <div v-if="loading" class="result-state" role="status" aria-live="polite">
      <span class="loading-mark" aria-hidden="true">
        <el-icon class="is-loading" :size="34"><Loading /></el-icon>
      </span>
      <p class="state-eyebrow">支付结果确认</p>
      <h1 class="result-title">正在核验订单</h1>
      <p class="result-message">请稍候，不要重复支付或关闭当前页面。</p>
      <div class="loading-track" aria-hidden="true"><span /></div>
    </div>

    <div v-else class="result-state" aria-live="polite">
      <span class="result-icon" :class="paymentSuccess ? 'is-success' : 'is-failure'" aria-hidden="true">
        <el-icon :size="42">
          <CircleCheck v-if="paymentSuccess" />
          <CircleClose v-else />
        </el-icon>
      </span>
      <p class="state-eyebrow">{{ paymentSuccess ? '支付已完成' : '支付未完成' }}</p>
      <h1 class="result-title">{{ paymentSuccess ? '支付成功' : '暂未确认支付成功' }}</h1>
      <p class="result-message">{{ resultMessage }}</p>

      <OrderInfoDisplay v-if="orderInfo" :order-info="orderInfo" />

      <div class="action-buttons">
        <button type="button" class="secondary-button" @click="handleGoToOrders">
          <el-icon><Document /></el-icon>
          查看我的订单
        </button>
        <button type="button" class="primary-button" @click="handleGoToHome">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </button>
      </div>

      <TipsSection v-if="paymentSuccess" />
      <div v-else class="failure-guide">
        <strong>下一步怎么做</strong>
        <p>先到“我的订单”查看状态；如款项已扣除，请勿重复支付，稍后刷新订单即可。</p>
      </div>
    </div>
  </article>
</template>

<script setup>
import { CircleCheck, CircleClose, Document, HomeFilled, Loading } from '@element-plus/icons-vue'
import OrderInfoDisplay from './OrderInfoDisplay.vue'
import TipsSection from './TipsSection.vue'

defineProps({
  paymentSuccess: {
    type: Boolean,
    required: true
  },
  resultMessage: {
    type: String,
    required: true
  },
  orderInfo: {
    type: Object,
    default: null
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['go-to-orders', 'go-to-home'])
const handleGoToOrders = () => emit('go-to-orders')
const handleGoToHome = () => emit('go-to-home')
</script>

<style scoped>
.result-card {
  position: relative;
  overflow: hidden;
  width: 100%;
  border: 1px solid var(--rail-line);
  border-top: 4px solid var(--rail-primary);
  border-radius: 16px;
  background: var(--rail-surface);
  box-shadow: 0 18px 50px color-mix(in srgb, var(--rail-navy) 12%, transparent);
}

.result-state {
  padding: 44px 40px 36px;
  text-align: center;
}

.result-icon,
.loading-mark {
  display: inline-flex;
  width: 72px;
  height: 72px;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
  border-radius: 50%;
}

.result-icon.is-success { background: color-mix(in srgb, var(--rail-success) 10%, var(--rail-surface)); color: var(--rail-success); }
.result-icon.is-failure { background: color-mix(in srgb, var(--rail-danger) 9%, var(--rail-surface)); color: var(--rail-danger); }
.loading-mark { background: var(--rail-primary-soft); color: var(--rail-primary); }

.state-eyebrow {
  margin: 0 0 8px;
  color: var(--rail-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
}

.result-title {
  margin: 0 0 12px;
  color: var(--rail-navy);
  font-size: 30px;
  font-weight: 750;
  line-height: 1.25;
}

.result-message {
  max-width: 480px;
  margin: 0 auto 28px;
  color: var(--rail-muted);
  font-size: 15px;
  line-height: 1.7;
}

.action-buttons {
  display: flex;
  justify-content: center;
  gap: 12px;
  margin: 28px 0;
}

.primary-button,
.secondary-button {
  display: inline-flex;
  min-height: 44px;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 0 22px;
  border-radius: 8px;
  font-weight: 650;
  cursor: pointer;
}

.primary-button { border: 1px solid var(--rail-primary); background: var(--rail-primary); color: var(--rail-surface); }
.primary-button:hover { background: var(--rail-primary-active); }
.secondary-button { border: 1px solid var(--rail-line); background: var(--rail-surface); color: var(--rail-ink); }
.secondary-button:hover { border-color: var(--rail-primary); color: var(--rail-primary); }
.primary-button:focus-visible,
.secondary-button:focus-visible { outline: 3px solid color-mix(in srgb, var(--rail-primary) 28%, transparent); outline-offset: 2px; }

.failure-guide {
  padding: 16px 18px;
  border: 1px solid color-mix(in srgb, var(--rail-warning) 30%, var(--rail-line));
  border-radius: 8px;
  background: color-mix(in srgb, var(--rail-warning) 8%, var(--rail-surface));
  color: var(--rail-warning);
  text-align: left;
}
.failure-guide strong { display: block; margin-bottom: 6px; font-size: 14px; }
.failure-guide p { margin: 0; font-size: 13px; line-height: 1.7; }

.loading-track {
  overflow: hidden;
  width: min(280px, 80%);
  height: 4px;
  margin: 8px auto 0;
  border-radius: 999px;
  background: color-mix(in srgb, var(--rail-primary) 16%, var(--rail-surface));
}
.loading-track span {
  display: block;
  width: 45%;
  height: 100%;
  border-radius: inherit;
  background: var(--rail-primary);
  animation: loading-slide 1.2s ease-in-out infinite alternate;
}
@keyframes loading-slide { to { transform: translateX(125%); } }

@media (prefers-reduced-motion: reduce) {
  .loading-track span { animation: none; width: 100%; opacity: 0.65; }
}

@media (max-width: 640px) {
  .result-state { padding: 32px 20px 24px; }
  .result-title { font-size: 24px; }
  .action-buttons { flex-direction: column; }
  .primary-button, .secondary-button { width: 100%; }
}
</style>
