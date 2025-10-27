<template>
  <div class="quick-search-section">
    <h4 class="subsection-title">
      <el-icon size="18" color="#1890ff">
        <Lightning />
      </el-icon>
      快速搜索
    </h4>
    <div class="quick-search-tips">
      <div class="tip-card" @click="handleClick('北京', '上海')">
        <div class="tip-route">北京 → 上海</div>
        <div class="tip-desc">京沪高铁主干线</div>
      </div>
      <div class="tip-card" @click="handleClick('北京', '广州')">
        <div class="tip-route">北京 → 广州</div>
        <div class="tip-desc">京广高铁主干线</div>
      </div>
      <div class="tip-card" @click="handleClick('上海', '深圳')">
        <div class="tip-route">上海 → 深圳</div>
        <div class="tip-desc">沪深高速通道</div>
      </div>
      <div class="tip-card" @click="handleClick('杭州', '北京')">
        <div class="tip-route">杭州 → 北京</div>
        <div class="tip-desc">京杭快速直达</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Lightning } from '@element-plus/icons-vue'

const emit = defineEmits(['quick-search'])

const handleClick = (origin, destination) => {
  emit('quick-search', { origin, destination })
}
</script>

<style scoped>
.quick-search-section {
  margin-bottom: 40px;
}

.subsection-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 20px;
}

.quick-search-tips {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 16px;
}

.tip-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.tip-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.tip-route {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 8px;
}

.tip-desc {
  font-size: 14px;
  opacity: 0.9;
}

@media (max-width: 768px) {
  .quick-search-tips {
    grid-template-columns: 1fr;
  }
  
  .tip-card {
    padding: 16px;
  }
}
</style>

