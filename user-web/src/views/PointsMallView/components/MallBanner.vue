<script setup lang="ts">
// 定义Props
interface Props {
  userPoints: number
}

const props = defineProps<Props>()

// 格式化积分显示
const formatPoints = (points: number) => {
  return points.toLocaleString()
}
</script>

<template>
  <div class="mall-banner">
    <div class="banner-content">
      <!-- 左侧信息 -->
      <div class="banner-info">
        <h1 class="banner-title">
          <span class="title-icon">⭐</span>
          积分商城
        </h1>
        <p class="banner-subtitle">用积分兑换心仪好物，让每次出行都有惊喜</p>
        
        <div class="banner-features">
          <div class="feature-item">
            <span class="feature-icon">🎁</span>
            <span class="feature-text">精选好物</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🚀</span>
            <span class="feature-text">快速发货</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🔄</span>
            <span class="feature-text">7天退换</span>
          </div>
        </div>
      </div>
      
      <!-- 右侧积分信息 -->
      <div class="points-info">
        <div class="points-card">
          <div class="points-header">
            <span class="points-label">我的积分</span>
            <span class="points-icon">💎</span>
          </div>
          
          <div class="points-value">
            {{ formatPoints(userPoints) }}
          </div>
          
          <div class="points-actions">
            <button class="action-btn primary">
              <span class="btn-icon">📈</span>
              赚积分
            </button>
            <button class="action-btn secondary">
              <span class="btn-icon">📋</span>
              积分明细
            </button>
          </div>
        </div>
        
        <!-- 积分获取提示 -->
        <div class="points-tips">
          <div class="tip-item">
            <span class="tip-dot"></span>
            <span class="tip-text">购票消费1元获得10积分</span>
          </div>
          <div class="tip-item">
            <span class="tip-dot"></span>
            <span class="tip-text">每日签到可获得20积分</span>
          </div>
          <div class="tip-item">
            <span class="tip-dot"></span>
            <span class="tip-text">分享好友注册获得100积分</span>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 装饰元素 -->
    <div class="banner-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
  </div>
</template>

<style scoped>
.mall-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  padding: 32px;
  color: white;
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(102, 126, 234, 0.3);
}

.banner-content {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 40px;
  align-items: center;
  position: relative;
  z-index: 2;
}

/* 左侧信息样式 */
.banner-info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.banner-title {
  margin: 0;
  font-size: 32px;
  font-weight: 700;
  display: flex;
  align-items: center;
  gap: 12px;
}

.title-icon {
  font-size: 36px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.banner-subtitle {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
  line-height: 1.5;
}

.banner-features {
  display: flex;
  gap: 24px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 16px;
  border-radius: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.feature-icon {
  font-size: 16px;
}

.feature-text {
  font-size: 14px;
  font-weight: 500;
}

/* 右侧积分信息样式 */
.points-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.points-card {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  text-align: center;
}

.points-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.points-label {
  font-size: 14px;
  opacity: 0.8;
}

.points-icon {
  font-size: 20px;
}

.points-value {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.points-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  border: none;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn.primary {
  background: rgba(255, 255, 255, 0.9);
  color: #667eea;
}

.action-btn.primary:hover {
  background: white;
  transform: translateY(-1px);
}

.action-btn.secondary {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.action-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-1px);
}

.btn-icon {
  font-size: 12px;
}

/* 积分获取提示 */
.points-tips {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tip-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  opacity: 0.8;
}

.tip-dot {
  width: 4px;
  height: 4px;
  background: rgba(255, 255, 255, 0.6);
  border-radius: 50%;
  flex-shrink: 0;
}

.tip-text {
  line-height: 1.4;
}

/* 装饰元素 */
.banner-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 120px;
  height: 120px;
  top: -60px;
  right: -60px;
  animation-delay: 0s;
}

.circle-2 {
  width: 80px;
  height: 80px;
  bottom: -40px;
  left: 10%;
  animation-delay: 2s;
}

.circle-3 {
  width: 60px;
  height: 60px;
  top: 50%;
  right: 15%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .banner-content {
    grid-template-columns: 1fr 280px;
    gap: 32px;
  }
  
  .banner-title {
    font-size: 28px;
  }
  
  .title-icon {
    font-size: 32px;
  }
}

@media (max-width: 768px) {
  .mall-banner {
    padding: 24px;
  }
  
  .banner-content {
    grid-template-columns: 1fr;
    gap: 24px;
    text-align: center;
  }
  
  .banner-title {
    font-size: 24px;
    justify-content: center;
  }
  
  .banner-features {
    justify-content: center;
    flex-wrap: wrap;
    gap: 12px;
  }
  
  .feature-item {
    padding: 6px 12px;
  }
  
  .points-card {
    padding: 20px;
  }
  
  .points-value {
    font-size: 28px;
  }
}

@media (max-width: 480px) {
  .mall-banner {
    padding: 20px;
  }
  
  .banner-title {
    font-size: 20px;
  }
  
  .title-icon {
    font-size: 24px;
  }
  
  .banner-subtitle {
    font-size: 14px;
  }
  
  .banner-features {
    gap: 8px;
  }
  
  .feature-item {
    padding: 4px 8px;
  }
  
  .feature-text {
    font-size: 12px;
  }
  
  .points-value {
    font-size: 24px;
  }
}
</style>