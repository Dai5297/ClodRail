<template>
  <div class="service-section">
    <div class="container">
      <h3>增值服务</h3>
      <div class="service-options">
        <el-checkbox-group v-model="localValue" @change="updateValue">
          <div class="service-item">
            <el-checkbox label="insurance">铁路意外险</el-checkbox>
            <span class="service-price">¥20/人</span>
            <span class="service-desc">为您的出行提供意外保障</span>
          </div>
          <div class="service-item">
            <el-checkbox label="meal">餐食预订</el-checkbox>
            <span class="service-price">¥30/份</span>
            <span class="service-desc">提前预订列车餐食</span>
          </div>
          <div class="service-item">
            <el-checkbox label="priority">优先检票</el-checkbox>
            <span class="service-price">¥10/人</span>
            <span class="service-desc">享受优先检票服务</span>
          </div>
        </el-checkbox-group>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'

// 定义props
const props = defineProps<{
  modelValue: string[]
  passengerCount: number
}>()

// 定义emits
const emit = defineEmits<{
  'update:modelValue': [value: string[]]
}>()

// 本地值
const localValue = ref<string[]>([...props.modelValue])

// 监听props变化
watch(() => props.modelValue, (newValue) => {
  localValue.value = [...newValue]
}, { deep: true })

// 更新值
const updateValue = () => {
  emit('update:modelValue', localValue.value)
}
</script>

<style scoped>
/* 增值服务 */
.service-section {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
}

.service-section h3 {
  margin: 0 0 16px 0;
  font-size: 16px;
}

.service-options {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.service-item:hover {
  border-color: #1890FF;
  background: #f6ffed;
}

.service-price {
  color: #ff4d4f;
  font-weight: 500;
}

.service-desc {
  color: #8c8c8c;
  font-size: 14px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }
}
</style>