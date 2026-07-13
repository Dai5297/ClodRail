<template>
  <span class="status-badge" :class="`status-badge--${tone}`">
    <span v-if="dot" class="status-badge__dot" aria-hidden="true" />
    <slot>{{ label }}</slot>
  </span>
</template>

<script setup>
defineProps({
  label: { type: String, default: '' },
  tone: {
    type: String,
    default: 'neutral',
    validator: (value) => ['neutral', 'info', 'success', 'warning', 'danger'].includes(value),
  },
  dot: { type: Boolean, default: false },
})
</script>

<style scoped>
.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-height: 24px;
  padding: 2px 9px;
  border: 1px solid transparent;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 600;
  line-height: 18px;
  white-space: nowrap;
}

.status-badge__dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.status-badge--neutral { border-color: var(--rail-line); background: #f6f8fb; color: #526071; }
.status-badge--info { border-color: #bad7ff; background: var(--rail-primary-soft); color: var(--rail-primary-active); }
.status-badge--success { border-color: #afe3cf; background: #eaf8f2; color: #087a4f; }
.status-badge--warning { border-color: #f6d39b; background: var(--rail-action-soft); color: #a65300; }
.status-badge--danger { border-color: #f3b7bb; background: #fff0f1; color: #b4232c; }
</style>
