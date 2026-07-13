<template>
  <component :is="tag" class="page-shell" :class="{ 'page-shell--flush': !padded }">
    <div class="page-shell__inner" :style="{ maxWidth }">
      <header v-if="hasHeader" class="page-shell__header">
        <div class="page-shell__heading">
          <p v-if="eyebrow" class="page-shell__eyebrow">{{ eyebrow }}</p>
          <slot name="title">
            <h1 v-if="title" class="page-shell__title">{{ title }}</h1>
          </slot>
          <slot name="description">
            <p v-if="description" class="page-shell__description">{{ description }}</p>
          </slot>
        </div>
        <div v-if="$slots.actions" class="page-shell__actions">
          <slot name="actions" />
        </div>
      </header>

      <slot />
    </div>
  </component>
</template>

<script setup>
import { computed, useSlots } from 'vue'

const props = defineProps({
  tag: { type: String, default: 'div' },
  maxWidth: { type: String, default: '1200px' },
  padded: { type: Boolean, default: true },
  eyebrow: { type: String, default: '' },
  title: { type: String, default: '' },
  description: { type: String, default: '' },
})

const slots = useSlots()
const hasHeader = computed(
  () => props.eyebrow || props.title || props.description || slots.title || slots.description || slots.actions,
)
</script>

<style scoped>
.page-shell {
  min-height: 100%;
  padding: clamp(24px, 4vw, 48px) clamp(16px, 4vw, 32px);
  background: var(--rail-background);
}

.page-shell--flush {
  padding: 0;
}

.page-shell__inner {
  width: 100%;
  margin: 0 auto;
}

.page-shell__header {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.page-shell__heading {
  min-width: 0;
}

.page-shell__eyebrow {
  margin: 0 0 6px;
  color: var(--rail-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.page-shell__title {
  margin: 0;
  color: var(--rail-navy);
  font-size: clamp(24px, 3vw, 34px);
  font-weight: 700;
  line-height: 1.25;
}

.page-shell__description {
  max-width: 720px;
  margin: 8px 0 0;
  color: var(--rail-muted);
  font-size: 15px;
  line-height: 1.7;
}

.page-shell__actions {
  display: flex;
  flex: 0 0 auto;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
}

@media (max-width: 640px) {
  .page-shell__header {
    align-items: flex-start;
    flex-direction: column;
  }

  .page-shell__actions {
    width: 100%;
  }
}
</style>
