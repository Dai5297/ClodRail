<template>
  <nav class="space-y-1">
    <router-link 
      v-for="item in menuItems" 
      :key="item.path"
      :to="item.path"
      class="flex items-center gap-3 px-4 py-3 rounded-xl text-slate-600 hover:bg-slate-50 hover:text-primary font-medium transition-colors"
      :class="{ 'bg-blue-50 text-primary': isExactActive(item) }"
    >
      <div class="w-8 h-8 rounded-lg bg-slate-100 flex items-center justify-center text-slate-500 group-hover:text-primary transition-colors">
        <i :class="item.icon"></i>
      </div>
      {{ item.label }}
    </router-link>
  </nav>
</template>

<script setup>
import { useRoute } from 'vue-router'

defineProps({
  menuItems: {
    type: Array,
    default: () => []
  }
})

const route = useRoute()

const isExactActive = (item) => {
  if (item.exact) {
    return route.path === item.path
  }
  return route.path.startsWith(item.path) && (item.path !== '/user' || route.path === '/user')
}
</script>
