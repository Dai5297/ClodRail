<template>
  <section
    class="state-panel"
    :class="[`state-panel--${tone}`, { 'state-panel--compact': compact }]"
    :role="tone === 'error' ? 'alert' : 'status'"
    :aria-live="tone === 'error' ? 'assertive' : 'polite'"
  >
    <div class="state-panel__icon" aria-hidden="true">
      <slot name="icon">
        <span v-if="tone === 'loading'" class="state-panel__spinner" />
        <span v-else>{{ fallbackIcon }}</span>
      </slot>
    </div>
    <div class="state-panel__body">
      <component :is="titleTag" v-if="title" class="state-panel__title">{{ title }}</component>
      <p v-if="description" class="state-panel__description">{{ description }}</p>
      <slot />
      <div v-if="$slots.actions" class="state-panel__actions"><slot name="actions" /></div>
    </div>
  </section>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  tone: {
    type: String,
    default: 'empty',
    validator: (value) => ['empty', 'loading', 'error', 'success', 'info'].includes(value),
  },
  title: { type: String, default: '' },
  titleTag: {
    type: String,
    default: 'h2',
    validator: (value) => ['h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'p', 'div'].includes(value),
  },
  description: { type: String, default: '' },
  compact: { type: Boolean, default: false },
})

const fallbackIcon = computed(() => ({ empty: '—', error: '!', success: '✓', info: 'i' })[props.tone] || '')
</script>

<style scoped>
.state-panel {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  gap: 14px;
  min-height: 180px;
  padding: 40px 24px;
  border: 1px dashed var(--rail-line);
  border-radius: var(--rail-card-radius);
  background: #fbfcfe;
  text-align: left;
}

.state-panel--compact {
  min-height: auto;
  padding: 20px;
}

.state-panel__icon {
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #eef2f7;
  color: var(--rail-muted);
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 20px;
  font-weight: 700;
}

.state-panel--loading .state-panel__icon,
.state-panel--info .state-panel__icon { background: var(--rail-primary-soft); color: var(--rail-primary); }
.state-panel--error .state-panel__icon { background: #fff0f1; color: var(--rail-danger); }
.state-panel--success .state-panel__icon { background: #eaf8f2; color: var(--rail-success); }

.state-panel__body {
  max-width: 560px;
  padding-top: 3px;
}

.state-panel__title {
  margin: 0;
  color: var(--rail-navy);
  font-size: 17px;
  font-weight: 700;
  line-height: 1.45;
}

.state-panel__description {
  margin: 5px 0 0;
  color: var(--rail-muted);
  font-size: 14px;
  line-height: 1.65;
}

.state-panel__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.state-panel__spinner {
  width: 18px;
  height: 18px;
  border: 2px solid #bad7ff;
  border-top-color: var(--rail-primary);
  border-radius: 50%;
  animation: state-panel-spin 700ms linear infinite;
}

@keyframes state-panel-spin {
  to { transform: rotate(360deg); }
}

@media (max-width: 480px) {
  .state-panel { flex-direction: column; align-items: center; text-align: center; }
  .state-panel__actions { justify-content: center; }
}
</style>
