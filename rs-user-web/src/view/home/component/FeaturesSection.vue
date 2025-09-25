<template>
  <section class="features-section">
    <div class="features-container">
      <h2 class="features-title">
        <el-icon size="32" color="#1890ff">
          <Star />
        </el-icon>
        平台特色
      </h2>
      
      <div class="features-grid">
        <div 
          v-for="(feature, index) in featureList" 
          :key="index"
          class="feature-card"
          @click="handleFeatureClick(feature)"
        >
          <div class="feature-icon" :style="{ backgroundColor: feature.color }">
            <el-icon size="28">
              <component :is="feature.icon" />
            </el-icon>
          </div>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-desc">{{ feature.description }}</p>
          <div class="feature-stats" v-if="feature.stats">
            <div class="stat-item" v-for="stat in feature.stats" :key="stat.label">
              <span class="stat-value">{{ stat.value }}</span>
              <span class="stat-label">{{ stat.label }}</span>
            </div>
          </div>
          <div class="feature-action">
            <el-button type="text" size="small" @click.stop="handleLearnMore(feature)">
              了解更多 →
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Lightning,
  ChatDotRound,
  Present,
  HelpFilled,
  Timer,
  TrendCharts
} from '@element-plus/icons-vue'

const router = useRouter()

// 功能特色数据
const featureList = ref([
  {
    id: 'fast-booking',
    title: '极速购票',
    description: '智能抢票算法，毫秒级响应，让您快人一步抢到心仪车票',
    icon: Lightning,
    color: '#ff6b6b',
    stats: [
      { label: '响应时间', value: '<100ms' },
      { label: '成功率', value: '99.9%' }
    ]
  },
  {
    id: 'ai-recommend',
    title: 'AI智能推荐',
    description: '基于大数据分析，为您推荐最优出行方案和性价比路线',
    icon: ChatDotRound,
    color: '#4ecdc4',
    stats: [
      { label: '推荐准确率', value: '95%' },
      { label: '节省时间', value: '30min' }
    ]
  },
  {
    id: 'points-mall',
    title: '积分商城',
    description: '出行积分兑换礼品，让每一次旅行都更有价值和意义',
    icon: Present,
    color: '#45b7d1',
    stats: [
      { label: '礼品种类', value: '500+' },
      { label: '兑换率', value: '1:1' }
    ]
  },
  {
    id: 'security',
    title: '安全保障',
    description: '多重安全验证，保护您的个人信息和资金安全',
    icon: HelpFilled,
    color: '#96ceb4',
    stats: [
      { label: '安全等级', value: 'AAA' },
      { label: '保障金额', value: '100万' }
    ]
  },
  {
    id: 'quick-refund',
    title: '快速退改',
    description: '支持在线退票改签，手续费透明，操作简单便捷',
    icon: Timer,
    color: '#feca57',
    stats: [
      { label: '退票时效', value: '5分钟' },
      { label: '数据准确率', value: '99.99%' }
    ]
  },
  {
    id: 'analytics',
    title: '出行分析',
    description: '个人出行数据分析，消费统计，帮您更好地规划出行预算',
    icon: TrendCharts,
    color: '#ff9ff3',
    stats: [
      { label: '分析维度', value: '20+' },
      { label: '报告类型', value: '5种' }
    ]
  }
])

// 处理功能卡片点击
const handleFeatureClick = (feature) => {
  // 根据功能ID跳转到对应页面
  switch (feature.id) {
    case 'fast-booking':
      router.push('/tickets')
      break
    case 'ai-recommend':
      router.push('/recommend')
      break
    case 'points-mall':
      router.push('/points')
      break
    case 'security':
      router.push('/security')
      break
    case 'quick-refund':
      router.push('/refund')
      break
    case 'analytics':
      router.push('/analytics')
      break
    default:
      console.log('点击功能:', feature.title)
  }
}

// 处理了解更多点击
const handleLearnMore = (feature) => {
  // 跳转到功能详情页面
  router.push(`/features/${feature.id}`)
}
</script>

<style scoped>
.features-section {
  padding: 80px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  position: relative;
}

.features-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="%23ffffff" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="%23ffffff" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="%23ffffff" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="%23ffffff" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="%23ffffff" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  pointer-events: none;
}

.features-container {
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
}

.features-title {
  text-align: center;
  font-size: 36px;
  margin-bottom: 60px;
  color: #2c3e50;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  font-weight: 700;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 30px;
}

.feature-card {
  background: white;
  padding: 40px 30px;
  border-radius: 20px;
  text-align: center;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s;
}

.feature-card:hover::before {
  left: 100%;
}

.feature-card:hover {
  transform: translateY(-10px) scale(1.02);
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 0 auto 25px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  position: relative;
  transition: all 0.3s ease;
}

.feature-card:hover .feature-icon {
  transform: scale(1.1) rotate(5deg);
}

.feature-title {
  font-size: 24px;
  margin-bottom: 15px;
  color: #2c3e50;
  font-weight: 600;
}

.feature-desc {
  color: #7f8c8d;
  line-height: 1.8;
  margin-bottom: 25px;
  font-size: 16px;
}

.feature-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 25px;
  padding: 20px 0;
  border-top: 1px solid #ecf0f1;
  border-bottom: 1px solid #ecf0f1;
}

.stat-item {
  text-align: center;
}

.stat-value {
  display: block;
  font-size: 20px;
  font-weight: bold;
  color: #e74c3c;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #95a5a6;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.feature-action {
  margin-top: 20px;
}

.feature-action .el-button {
  color: #3498db;
  font-weight: 500;
  transition: all 0.3s ease;
}

.feature-action .el-button:hover {
  color: #2980b9;
  transform: translateX(5px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .features-section {
    padding: 60px 15px;
  }
  
  .features-title {
    font-size: 28px;
    margin-bottom: 40px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .feature-card {
    padding: 30px 20px;
  }
  
  .feature-icon {
    width: 60px;
    height: 60px;
  }
  
  .feature-title {
    font-size: 20px;
  }
  
  .feature-desc {
    font-size: 14px;
  }
  
  .feature-stats {
    flex-direction: column;
    gap: 15px;
  }
}

@media (max-width: 480px) {
  .features-title {
    font-size: 24px;
    flex-direction: column;
    gap: 10px;
  }
  
  .feature-card {
    padding: 25px 15px;
  }
}
</style>