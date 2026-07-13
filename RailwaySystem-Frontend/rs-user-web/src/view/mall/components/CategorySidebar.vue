<template>
  <div class="bg-white rounded-xl shadow-sm border border-[var(--rail-line)] overflow-hidden sticky top-24">
    <div class="p-4 border-b border-[var(--rail-line)] bg-[#F5F8FC]">
      <h3 class="font-bold text-[#172033] flex items-center gap-2">
        <i class="ri-apps-line text-[#1677FF]"></i> 商品分类
      </h3>
    </div>
    
    <div class="p-2">
      <ul class="space-y-1">
        <li v-for="category in categories" :key="category.code">
          <a
            href="#"
            class="flex items-center gap-3 px-4 py-3 rounded-lg transition-colors duration-200 group"
            :class="[
              selectedCategory === category.code
                ? 'bg-[#EAF3FF] text-[#1677FF] font-medium'
                : 'text-slate-600 hover:bg-[#F5F8FC] hover:text-[#172033]'
            ]"
            @click.prevent="handleCategoryClick(category.code)"
          >
            <span class="text-xl group-hover:scale-110 transition-transform duration-300">{{ category.icon }}</span>
            <span class="text-sm">{{ category.name }}</span>
            <i v-if="selectedCategory === category.code" class="ri-arrow-right-s-line ml-auto text-[#1677FF]"></i>
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
