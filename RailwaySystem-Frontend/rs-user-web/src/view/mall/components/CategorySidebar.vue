<template>
  <div class="bg-white rounded-2xl shadow-sm border border-slate-100 overflow-hidden sticky top-24">
    <div class="p-5 border-b border-slate-100 bg-slate-50/50">
      <h3 class="font-bold text-slate-800 flex items-center gap-2">
        <i class="ri-apps-line text-primary"></i> 商品分类
      </h3>
    </div>
    
    <div class="p-3">
      <ul class="space-y-1">
        <li v-for="category in categories" :key="category.code">
          <a
            href="#"
            class="flex items-center gap-3 px-4 py-3 rounded-xl transition-all duration-200 group"
            :class="[
              selectedCategory === category.code 
                ? 'bg-blue-50 text-primary font-medium' 
                : 'text-slate-600 hover:bg-slate-50 hover:text-slate-900'
            ]"
            @click.prevent="handleCategoryClick(category.code)"
          >
            <span class="text-xl group-hover:scale-110 transition-transform duration-300">{{ category.icon }}</span>
            <span class="text-sm">{{ category.name }}</span>
            <i v-if="selectedCategory === category.code" class="ri-arrow-right-s-line ml-auto text-primary"></i>
          </a>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getCategoryList } from '@/api/points'

const props = defineProps({
  selectedCategory: {
    type: String,
    default: 'all'
  }
})

const emit = defineEmits(['category-change'])

const categories = ref([{ code: 'all', name: '全部商品', icon: '🛍️' }])
const loading = ref(false)

const fetchCategories = async () => {
  try {
    loading.value = true
    const response = await getCategoryList()

    if (response && response.data && Array.isArray(response.data)) {
      const categoryList = response.data.map((categoryName) => ({
        code: categoryName,
        name: categoryName,
        icon: '🏷️',
      }))

      categories.value = [{ code: 'all', name: '全部商品', icon: '🛍️' }, ...categoryList]
    }
  } catch (error) {
    // console.error('获取分类失败', error)
  } finally {
    loading.value = false
  }
}

const handleCategoryClick = (categoryCode) => {
  emit('category-change', categoryCode)
}

onMounted(() => {
  fetchCategories()
})
</script>
