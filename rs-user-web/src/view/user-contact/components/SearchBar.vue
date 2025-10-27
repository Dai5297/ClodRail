<template>
  <div class="search-bar">
    <el-form :model="searchForm" inline>
      <el-form-item label="姓名">
        <el-input
          v-model="searchForm.name"
          placeholder="请输入姓名"
          clearable
          style="width: 200px"
        />
      </el-form-item>
      <el-form-item label="乘客类型">
        <el-select
          v-model="searchForm.passengerType"
          placeholder="请选择乘客类型"
          clearable
          style="width: 150px"
        >
          <el-option label="成人" :value="1" />
          <el-option label="儿童" :value="2" />
          <el-option label="学生" :value="3" />
          <el-option label="老人" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select
          v-model="searchForm.status"
          placeholder="请选择状态"
          clearable
          style="width: 120px"
        >
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="handleReset">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { Search, Refresh } from '@element-plus/icons-vue'

const props = defineProps({
  searchForm: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['search', 'reset'])

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  emit('reset')
}
</script>

<style scoped>
.search-bar {
  background: #fafafa;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}
</style>