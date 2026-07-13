<template>
  <component
    :is="tag"
    class="surface-card"
    :class="[
      `surface-card--${padding}`,
      { 'surface-card--hoverable': hoverable, 'surface-card--elevated': elevated, 'surface-card--borderless': !bordered },
    ]"
  >
    <header v-if="$slots.header || $slots.title || $slots.extra || title || subtitle" class="surface-card__header">
      <div v-if="$slots.title || title || subtitle" class="surface-card__heading">
        <slot name="title">
          <h2 v-if="title" class="surface-card__title">{{ title }}</h2>
        </slot>
        <p v-if="subtitle" class="surface-card__subtitle">{{ subtitle }}</p>
      </div>
      <div v-if="$slots.extra" class="surface-card__extra"><slot name="extra" /></div>
      <slot name="header" />
    </header>
    <slot />
    <footer v-if="$slots.footer" class="surface-card__footer"><slot name="footer" /></footer>
  </component>
</template>

<script setup>
defineProps({
  tag: { type: String, default: 'section' },
  title: { type: String, default: '' },
  subtitle: { type: String, default: '' },
  padding: {
    type: String,
    default: 'md',
    validator: (value) => ['none', 'sm', 'md', 'lg'].includes(value),
  },
  bordered: { type: Boolean, default: true },
  elevated: { type: Boolean, default: false },
  hoverable: { type: Boolean, default: false },
})
</script>

<style scoped>
.surface-card {
  overflow: hidden;
  border: 1px solid var(--rail-line);
  border-radius: var(--rail-card-radius);
  background: var(--rail-surface);
  color: var(--rail-ink);
}

.surface-card--none { padding: 0; }
.surface-card--sm { padding: 16px; }
.surface-card--md { padding: clamp(18px, 3vw, 24px); }
.surface-card--lg { padding: clamp(24px, 4vw, 32px); }
.surface-card--borderless { border-color: transparent; }
.surface-card--elevated { box-shadow: var(--rail-shadow-soft); }

.surface-card--hoverable {
  transition: border-color 160ms ease, box-shadow 160ms ease, transform 160ms ease;
}

.surface-card--hoverable:hover {
  border-color: #bad7ff;
  box-shadow: var(--rail-shadow-soft);
  transform: translateY(-2px);
}

.surface-card__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.surface-card__heading {
  min-width: 0;
}

.surface-card__title {
  margin: 0;
  color: var(--rail-navy);
  font-size: 18px;
  font-weight: 700;
  line-height: 1.4;
}

.surface-card__subtitle {
  margin: 4px 0 0;
  color: var(--rail-muted);
  font-size: 14px;
  line-height: 1.6;
}

.surface-card__extra {
  flex: 0 0 auto;
}

.surface-card__footer {
  padding-top: 18px;
  margin-top: 18px;
  border-top: 1px solid var(--rail-line);
}

@media (prefers-reduced-motion: reduce) {
  .surface-card--hoverable:hover { transform: none; }
}

@media (max-width: 640px) {
  .surface-card__header {
    flex-wrap: wrap;
  }

  .surface-card__extra {
    max-width: 100%;
  }
}
</style>
