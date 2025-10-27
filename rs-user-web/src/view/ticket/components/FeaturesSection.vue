<template>
  <div class="features-section">
    <h4 class="subsection-title">
      <el-icon size="18" color="#52c41a">
        <Trophy />
      </el-icon>
      服务特色
    </h4>
    <div class="features-grid">
      <div class="feature-item">
        <el-icon size="24" color="#1890ff"><Clock /></el-icon>
        <div class="feature-content">
          <div class="feature-title">准点率高</div>
          <div class="feature-desc">高铁准点率达98%以上</div>
        </div>
      </div>
      <div class="feature-item">
        <el-icon size="24" color="#52c41a"><ChromeFilled /></el-icon>
        <div class="feature-content">
          <div class="feature-title">安全保障</div>
          <div class="feature-desc">全程安全监控保障</div>
        </div>
      </div>
      <div class="feature-item">
        <el-icon size="24" color="#ff6b35"><Service /></el-icon>
        <div class="feature-content">
          <div class="feature-title">贴心服务</div>
          <div class="feature-desc">24小时客服支持</div>
        </div>
      </div>
      <div class="feature-item">
        <el-icon size="24" color="#722ed1"><Cellphone /></el-icon>
        <div class="feature-content">
          <div class="feature-title">便捷购票</div>
          <div class="feature-desc">手机APP随时购票</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Trophy, Clock, ChromeFilled, Service, Cellphone } from '@element-plus/icons-vue'
</script>

<style scoped>
.features-section {
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

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 16px;
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.feature-content {
  flex: 1;
}

.feature-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.feature-desc {
  font-size: 14px;
  color: #666;
}

@media (max-width: 768px) {
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .feature-item {
    padding: 20px;
  }
}
</style>

