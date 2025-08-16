<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElIcon } from 'element-plus'
import { 
  TrendCharts, 
  Lock, 
  Iphone, 
  ChatDotRound,
  Timer,
  Star,
  Service
} from '@element-plus/icons-vue'

// 定义特色功能接口
interface Feature {
  id: string
  icon: string
  title: string
  description: string
  color: string
  bgColor: string
  details: string[]
}

// 特色功能数据
const features = ref<Feature[]>([
  {
    id: 'ai-recommend',
    icon: '🎯',
    title: '智能推荐',
    description: 'AI智能分析，为您推荐最优出行方案',
    color: '#1890ff',
    bgColor: '#f0f9ff',
    details: [
      '基于大数据分析用户偏好',
      '智能匹配最优车次',
      '个性化出行建议',
      '实时价格优化推荐'
    ]
  },
  {
    id: 'secure-payment',
    icon: '🔒',
    title: '安全支付',
    description: '多重安全保障，支付更安心',
    color: '#52c41a',
    bgColor: '#f6ffed',
    details: [
      '银行级安全加密',
      '多种支付方式支持',
      '实时风险监控',
      '资金安全保障'
    ]
  },
  {
    id: 'quick-booking',
    icon: '📱',
    title: '便捷购票',
    description: '一键购票，快速出行',
    color: '#faad14',
    bgColor: '#fffbe6',
    details: [
      '30秒快速下单',
      '智能座位推荐',
      '批量购票支持',
      '电子票据管理'
    ]
  },
  {
    id: 'ai-service',
    icon: '🤖',
    title: 'AI客服',
    description: '24小时智能客服，随时为您服务',
    color: '#722ed1',
    bgColor: '#f9f0ff',
    details: [
      '7×24小时在线服务',
      '智能问题解答',
      '多语言支持',
      '人工客服无缝切换'
    ]
  }
])

// 额外服务特色
const extraFeatures = ref([
  {
    icon: Lock,
    title: '出行保障',
    description: '全程出行保险，安心无忧'
  },
  {
    icon: Timer,
    title: '准点保证',
    description: '实时列车动态，准点率99%+'
  },
  {
    icon: Star,
    title: '会员特权',
    description: '积分兑换，专享优惠折扣'
  },
  {
    icon: Service,
    title: '贴心服务',
    description: '站内导航，无障碍出行'
  }
])

// 动画状态
const isVisible = ref(false)

// 页面挂载后触发动画
onMounted(() => {
  setTimeout(() => {
    isVisible.value = true
  }, 100)
})

// 获取图标组件
const getIconComponent = (iconName: string) => {
  const iconMap = {
    'Lock': Lock,
    'Timer': Timer,
    'Star': Star,
    'Service': Service
  }
  return iconMap[iconName as keyof typeof iconMap] || Service
}
</script>

<template>
  <section class="features-section">
    <div class="container">
      <!-- 标题区域 -->
      <div class="section-header">
        <div class="title-wrapper">
          <ElIcon :size="24" color="#1890ff">
            <Star />
          </ElIcon>
          <h2 class="section-title">服务特色</h2>
        </div>
        <p class="section-subtitle">专业的铁路出行服务，让每一次旅程都更美好</p>
      </div>
      
      <!-- 主要特色功能 -->
      <div class="features-grid" :class="{ 'animate-in': isVisible }">
        <div 
          v-for="(feature, index) in features" 
          :key="feature.id" 
          class="feature-card"
          :class="`feature-card-${index}`"
          :style="{ '--feature-color': feature.color, '--feature-bg': feature.bgColor }"
        >
          <!-- 卡片装饰 -->
          <div class="card-decoration">
            <div class="decoration-circle"></div>
            <div class="decoration-dots"></div>
          </div>
          
          <!-- 图标区域 -->
          <div class="feature-icon-wrapper">
            <div class="feature-icon">{{ feature.icon }}</div>
            <div class="icon-glow"></div>
          </div>
          
          <!-- 内容区域 -->
          <div class="feature-content">
            <h3 class="feature-title">{{ feature.title }}</h3>
            <p class="feature-description">{{ feature.description }}</p>
            
            <!-- 详细特点 -->
            <div class="feature-details">
              <div 
                v-for="(detail, detailIndex) in feature.details" 
                :key="detailIndex" 
                class="detail-item"
              >
                <div class="detail-dot"></div>
                <span class="detail-text">{{ detail }}</span>
              </div>
            </div>
          </div>
          
          <!-- 悬浮效果 -->
          <div class="hover-overlay"></div>
        </div>
      </div>
      
      <!-- 额外服务特色 -->
      <div class="extra-features">
        <h3 class="extra-title">更多贴心服务</h3>
        <div class="extra-grid">
          <div 
            v-for="(extra, index) in extraFeatures" 
            :key="index" 
            class="extra-item"
          >
            <div class="extra-icon">
              <ElIcon :size="20">
                <component :is="extra.icon" />
              </ElIcon>
            </div>
            <div class="extra-content">
              <h4 class="extra-item-title">{{ extra.title }}</h4>
              <p class="extra-item-desc">{{ extra.description }}</p>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 底部装饰 -->
      <div class="section-footer">
        <div class="footer-decoration">
          <div class="decoration-line"></div>
          <div class="decoration-center">
            <ElIcon :size="16" color="#d9d9d9">
              <Star />
            </ElIcon>
          </div>
          <div class="decoration-line"></div>
        </div>
        <p class="footer-text">让科技为出行赋能，让服务更有温度</p>
      </div>
    </div>
  </section>
</template>

<style scoped>
/* 服务特色 */
.features-section {
  padding: 60px 0;
  background: linear-gradient(135deg, #f5f5f5 0%, #fafafa 100%);
  position: relative;
  overflow: hidden;
}

.features-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="dots" width="20" height="20" patternUnits="userSpaceOnUse"><circle cx="10" cy="10" r="1" fill="%23e6f7ff" opacity="0.5"/></pattern></defs><rect width="100" height="100" fill="url(%23dots)"/></svg>') repeat;
  pointer-events: none;
}

.container {
  width: 100%;
  margin: 0;
  padding: 0 24px;
  position: relative;
  z-index: 1;
}

/* 标题区域 */
.section-header {
  text-align: center;
  margin-bottom: 48px;
}

.title-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-bottom: 12px;
}

.section-title {
  font-size: 28px;
  font-weight: bold;
  color: #262626;
  margin: 0;
}

.section-subtitle {
  font-size: 16px;
  color: #8c8c8c;
  margin: 0;
  max-width: 600px;
  margin: 0 auto;
  line-height: 1.6;
}

/* 特色功能网格 */
.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 32px;
  margin-bottom: 48px;
  opacity: 0;
  transform: translateY(30px);
  transition: all 0.8s ease;
}

.features-grid.animate-in {
  opacity: 1;
  transform: translateY(0);
}

.feature-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  position: relative;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  border: 1px solid #f0f0f0;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border-color: var(--feature-color);
}

.feature-card:hover .hover-overlay {
  opacity: 1;
}

.feature-card:hover .icon-glow {
  opacity: 1;
  transform: scale(1.2);
}

.feature-card:hover .card-decoration {
  opacity: 1;
  transform: scale(1.1);
}

/* 卡片装饰 */
.card-decoration {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 80px;
  height: 80px;
  opacity: 0;
  transform: scale(0.8);
  transition: all 0.4s ease;
  pointer-events: none;
}

.decoration-circle {
  width: 60px;
  height: 60px;
  background: var(--feature-bg);
  border-radius: 50%;
  position: absolute;
  top: 10px;
  right: 10px;
}

.decoration-dots {
  position: absolute;
  top: 0;
  right: 0;
  width: 40px;
  height: 40px;
  background: radial-gradient(circle, var(--feature-color) 1px, transparent 1px);
  background-size: 8px 8px;
  opacity: 0.3;
}

/* 图标区域 */
.feature-icon-wrapper {
  position: relative;
  display: inline-block;
  margin-bottom: 24px;
}

.feature-icon {
  font-size: 48px;
  line-height: 1;
  position: relative;
  z-index: 2;
}

.icon-glow {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 80px;
  height: 80px;
  background: var(--feature-bg);
  border-radius: 50%;
  opacity: 0;
  transition: all 0.4s ease;
  z-index: 1;
}

/* 内容区域 */
.feature-content {
  position: relative;
  z-index: 2;
}

.feature-title {
  font-size: 20px;
  font-weight: bold;
  color: #262626;
  margin: 0 0 12px 0;
  line-height: 1.3;
}

.feature-description {
  color: #8c8c8c;
  line-height: 1.6;
  margin: 0 0 20px 0;
  font-size: 14px;
}

/* 详细特点 */
.feature-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #595959;
}

.detail-dot {
  width: 4px;
  height: 4px;
  background: var(--feature-color);
  border-radius: 50%;
  flex-shrink: 0;
}

.detail-text {
  line-height: 1.4;
}

/* 悬浮覆盖层 */
.hover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, var(--feature-bg) 0%, transparent 100%);
  opacity: 0;
  transition: all 0.4s ease;
  pointer-events: none;
}

/* 卡片动画延迟 */
.feature-card-0 { transition-delay: 0s; }
.feature-card-1 { transition-delay: 0.1s; }
.feature-card-2 { transition-delay: 0.2s; }
.feature-card-3 { transition-delay: 0.3s; }

/* 额外服务特色 */
.extra-features {
  margin-bottom: 48px;
}

.extra-title {
  font-size: 20px;
  font-weight: bold;
  color: #262626;
  text-align: center;
  margin: 0 0 32px 0;
}

.extra-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.extra-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.extra-item:hover {
  border-color: #1890ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.1);
  transform: translateY(-2px);
}

.extra-icon {
  width: 40px;
  height: 40px;
  background: #f0f9ff;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #1890ff;
  flex-shrink: 0;
}

.extra-content {
  flex: 1;
}

.extra-item-title {
  font-size: 14px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 4px 0;
}

.extra-item-desc {
  font-size: 12px;
  color: #8c8c8c;
  margin: 0;
  line-height: 1.4;
}

/* 底部装饰 */
.section-footer {
  text-align: center;
}

.footer-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 16px;
}

.decoration-line {
  width: 60px;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, #d9d9d9 50%, transparent 100%);
}

.decoration-center {
  width: 32px;
  height: 32px;
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.footer-text {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
  font-style: italic;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .container {
    padding: 0 16px;
  }
  
  .features-grid {
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 24px;
  }
  
  .feature-card {
    padding: 24px;
  }
}

@media (max-width: 768px) {
  .features-section {
    padding: 40px 0;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .extra-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .section-title {
    font-size: 24px;
  }
  
  .section-subtitle {
    font-size: 14px;
  }
  
  .title-wrapper {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 12px;
  }
  
  .feature-card {
    padding: 20px;
  }
  
  .feature-icon {
    font-size: 40px;
  }
  
  .feature-title {
    font-size: 18px;
  }
  
  .extra-item {
    padding: 16px;
    gap: 12px;
  }
  
  .extra-icon {
    width: 36px;
    height: 36px;
  }
}
</style>