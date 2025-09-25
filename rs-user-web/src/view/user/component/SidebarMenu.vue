<template>
  <ul class="menu-list">
    <li v-for="item in menuItems" :key="item.path" class="menu-item">
      <router-link 
        :to="item.path" 
        class="menu-link" 
        :exact-active-class="item.exact ? 'active' : ''"
        :active-class="!item.exact ? 'active' : ''"
      >
        <el-icon class="menu-icon">
          <component :is="item.icon" />
        </el-icon>
        <span>{{ item.label }}</span>
      </router-link>
    </li>
  </ul>
</template>

<script setup>
defineProps({
  menuItems: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-item {
  margin-bottom: 8px;
}

.menu-link {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  color: #595959;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.3s;
  font-size: 14px;
}

.menu-link:hover {
  background-color: #f0f8ff;
  color: #1890FF;
}

.menu-link.active {
  background-color: #e6f7ff;
  color: #1890FF;
  font-weight: 500;
}

.menu-icon {
  margin-right: 12px;
  font-size: 16px;
}
</style>