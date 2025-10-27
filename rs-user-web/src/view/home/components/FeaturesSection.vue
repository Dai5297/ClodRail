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
  User,
  ChromeFilled,
  Timer, 
  TrendCharts,
  Star,
  Clock,
  Tickets,
  Service
} from '@element-plus/icons-vue'

const router = useRouter()

// 功能特色数据
const featureList = ref([
  {
    id: 'fast-booking',
    title: '智能购票',
    description: '全国铁路实时票务查询，智能推荐最佳出行方案，让您的每次出行都更便捷',
    icon: Tickets,
    color: '#1890ff',
    stats: [
      { label: '覆盖车站', value: '3000+' },
      { label: '查询响应', value: '<200ms' }
    ]
  },
  {
    id: 'real-time-info',
    title: '实时信息',
    description: '列车时刻表、余票信息、晚点通知实时更新，出行信息一手掌握',
    icon: Clock,
    color: '#52c41a',
    stats: [
      { label: '数据更新', value: '实时' },
      { label: '信息准确率', value: '99.8%' }
    ]
  },
  {
    id: 'contact-management',
    title: '联系人管理',
    description: '便捷管理常用联系人信息，支持批量购票，家庭出行更轻松',
    icon: User,
    color: '#722ed1',
    stats: [
      { label: '联系人数量', value: '无限制' },
      { label: '信息加密', value: '256位' }
    ]
  },
  {
    id: 'security',
    title: '安全保障',
    description: '银行级安全防护，多重身份验证，保障您的个人信息和资金安全',
    icon: ChromeFilled,
    color: '#fa8c16',
    stats: [
      { label: '安全等级', value: 'AAA+' },
      { label: '加密标准', value: 'SSL' }
    ]
  },
  {
    id: 'quick-refund',
    title: '便民服务',
    description: '在线退票改签、电子发票、座位选择等便民服务，让出行更贴心',
    icon: Service,
    color: '#13c2c2',
    stats: [
      { label: '退改时效', value: '即时' },
      { label: '服务满意度', value: '98%' }
    ]
  },
  {
    id: 'points-system',
    title: '积分权益',
    description: '出行积分累积，会员等级升级，专享优惠折扣和贴心服务',
    icon: TrendCharts,
    color: '#eb2f96',
    stats: [
      { label: '积分比例', value: '1:1' },
      { label: '权益种类', value: '10+' }
    ]
  }
])

// 处理功能卡片点击
const handleFeatureClick = (feature) => {
  // 根据功能ID跳转到对应页面
  switch (feature.id) {
    case 'fast-booking':
      router.push('/ticket')
      break
    case 'real-time-info':
      router.push('/ticket')
      break
    case 'contact-management':
      router.push('/user/contact')
      break
    case 'security':
      router.push('/user/security')
      break
    case 'quick-refund':
      router.push('/user/order')
      break
    case 'points-system':
      router.push('/user/point')
      break
    default:
      console.log('点击功能:', feature.title)
  }
}

// 处理了解更多按钮点击
const handleLearnMore = (feature) => {
  // 根据功能ID跳转到详细介绍页面
  switch (feature.id) {
    case 'fast-booking':
      router.push('/ticket')
      break
    case 'real-time-info':
      router.push('/ticket')
      break
    case 'contact-management':
      router.push('/user/contact')
      break
    case 'security':
      router.push('/user/security')
      break
    case 'quick-refund':
      router.push('/user/order')
      break
    case 'points-system':
      router.push('/user/point')
      break
    default:
      console.log('了解更多:', feature.title)
  }
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
  background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #1890ff, #52c41a, #722ed1, #fa8c16, #13c2c2, #eb2f96);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.feature-card:hover::before {
  opacity: 1;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  border-color: #cbd5e1;
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

.feature-card:hover .feature-title {
  color: #1e293b;
}

.feature-card:hover .learn-more-btn {
  opacity: 1;
  transform: translateY(0);
}

.feature-title {
  font-size: 22px;
  font-weight: 600;
  color: #1e293b;
  margin: 20px 0 12px;
  transition: color 0.3s ease;
}

.feature-desc {
  color: #64748b;
  font-size: 15px;
  line-height: 1.6;
  margin-bottom: 24px;
  min-height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
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