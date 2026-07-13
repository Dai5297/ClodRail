<template>
  <section class="service-section" aria-labelledby="service-title">
    <div class="service-section__inner">
      <div class="section-heading">
        <div>
          <p>常用服务</p>
          <h2 id="service-title">出行服务，一步直达</h2>
        </div>
        <span>购票后的常用操作也能快速找到</span>
      </div>

      <div class="service-grid">
        <button
          v-for="feature in featureList"
          :key="feature.id"
          type="button"
          class="service-entry"
          @click="handleFeatureClick(feature)"
        >
          <span class="service-entry__icon" :class="`service-entry__icon--${feature.tone}`" aria-hidden="true">
            <i :class="feature.icon"></i>
          </span>
          <span class="service-entry__copy">
            <strong>{{ feature.title }}</strong>
            <small>{{ feature.description }}</small>
          </span>
          <i class="ri-arrow-right-s-line service-entry__arrow" aria-hidden="true"></i>
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const featureList = ref([
  {
    id: 'fast-booking',
    title: '车票查询',
    description: '查询车次、时刻与余票',
    icon: 'ri-ticket-2-line',
    tone: 'blue',
  },
  {
    id: 'real-time-info',
    title: '实时信息',
    description: '掌握行程与车次动态',
    icon: 'ri-time-line',
    tone: 'navy',
  },
  {
    id: 'contact-management',
    title: '常用乘车人',
    description: '维护联系人乘车信息',
    icon: 'ri-contacts-book-line',
    tone: 'cyan',
  },
  {
    id: 'quick-refund',
    title: '我的订单',
    description: '查看订单与售后进度',
    icon: 'ri-file-list-3-line',
    tone: 'orange',
  },
  {
    id: 'points-system',
    title: '积分权益',
    description: '查看积分与兑换记录',
    icon: 'ri-medal-line',
    tone: 'gold',
  },
  {
    id: 'security',
    title: '账号安全',
    description: '管理密码与安全设置',
    icon: 'ri-shield-check-line',
    tone: 'green',
  },
])

featureList.value = [
  {
    id: 'orders',
    title: '我的订单',
    description: '查看购票与兑换记录',
    icon: 'ri-file-list-3-line',
    tone: 'blue',
  },
  {
    id: 'contacts',
    title: '常用乘车人',
    description: '维护乘车人信息',
    icon: 'ri-user-3-line',
    tone: 'blue',
  },
  {
    id: 'refund',
    title: '退改签',
    description: '处理订单售后',
    icon: 'ri-refund-2-line',
    tone: 'blue',
  },
  {
    id: 'mall',
    title: '积分商城',
    description: '积分兑换出行好物',
    icon: 'ri-shopping-bag-3-line',
    tone: 'blue',
  },
  {
    id: 'assistant',
    title: 'AI 助手',
    description: '咨询行程与订单',
    icon: 'ri-robot-2-line',
    tone: 'blue',
  },
]

const handleFeatureClick = (feature) => {
  switch (feature.id) {
    case 'orders':
      router.push('/user/order')
      break
    case 'contacts':
      router.push('/user/contact')
      break
    case 'refund':
      router.push('/ticket')
      break
    case 'mall':
      router.push('/mall')
      break
    case 'assistant':
      router.push('/assistant')
      break
    case 'fast-booking':
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
  }
}
</script>

<style scoped>
.service-section {
  padding: 56px 0 34px;
  background: var(--rail-background);
}

.service-section__inner {
  width: min(100% - 32px, 1200px);
  margin: 0 auto;
}

.section-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 28px;
  margin-bottom: 22px;
}

.section-heading p {
  margin: 0 0 6px;
  color: var(--rail-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
}

.section-heading h2 {
  margin: 0;
  color: var(--rail-navy);
  font-size: clamp(24px, 3vw, 32px);
  font-weight: 760;
  letter-spacing: -0.02em;
}

.section-heading > span {
  padding-bottom: 4px;
  color: var(--rail-muted);
  font-size: 14px;
}

.service-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  overflow: hidden;
  border: 1px solid var(--rail-line);
  border-radius: var(--rail-card-radius);
  background: #fff;
}

.service-entry {
  display: grid;
  grid-template-columns: 46px minmax(0, 1fr) 20px;
  align-items: center;
  gap: 14px;
  min-height: 104px;
  padding: 18px 20px;
  border: 0;
  border-right: 1px solid var(--rail-line);
  border-bottom: 1px solid var(--rail-line);
  background: #fff;
  color: var(--rail-ink);
  cursor: pointer;
  text-align: left;
  transition: background-color 150ms ease;
}

.service-entry:nth-child(3n) {
  border-right: 0;
}

.service-entry:nth-last-child(-n + 3) {
  border-bottom: 0;
}

.service-entry:hover {
  background: #f8fbff;
}

.service-entry__icon {
  display: grid;
  place-items: center;
  width: 46px;
  height: 46px;
  border-radius: 10px;
  background: var(--rail-primary-soft);
  color: var(--rail-primary);
  font-size: 22px;
}

.service-entry__icon--navy { background: #edf1f7; color: var(--rail-navy); }
.service-entry__icon--cyan { background: #e9f7fa; color: #087f91; }
.service-entry__icon--orange { background: var(--rail-action-soft); color: var(--rail-action-hover); }
.service-entry__icon--gold { background: #fff8df; color: #a86800; }
.service-entry__icon--green { background: #eaf8f2; color: var(--rail-success); }

.service-entry__copy {
  display: grid;
  min-width: 0;
  gap: 5px;
}

.service-entry__copy strong {
  color: var(--rail-navy);
  font-size: 15px;
}

.service-entry__copy small {
  overflow: hidden;
  color: var(--rail-muted);
  font-size: 12px;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.service-entry__arrow {
  color: #98a2b3;
  font-size: 20px;
  transition: color 150ms ease, transform 150ms ease;
}

.service-entry:hover .service-entry__arrow {
  color: var(--rail-primary);
  transform: translateX(2px);
}

@media (max-width: 900px) {
  .service-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .service-entry:nth-child(3n) {
    border-right: 1px solid var(--rail-line);
  }

  .service-entry:nth-child(2n) {
    border-right: 0;
  }

  .service-entry:nth-last-child(-n + 3) {
    border-bottom: 1px solid var(--rail-line);
  }

  .service-entry:nth-last-child(-n + 2) {
    border-bottom: 0;
  }
}

@media (max-width: 600px) {
  .service-section {
    padding-top: 40px;
  }

  .service-section__inner {
    width: min(100% - 24px, 1200px);
  }

  .section-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
  }

  .section-heading > span {
    font-size: 13px;
  }

  .service-grid {
    grid-template-columns: 1fr;
  }

  .service-entry,
  .service-entry:nth-child(2n),
  .service-entry:nth-child(3n),
  .service-entry:nth-last-child(-n + 3),
  .service-entry:nth-last-child(-n + 2) {
    min-height: 92px;
    border-right: 0;
    border-bottom: 1px solid var(--rail-line);
  }

  .service-entry:last-child {
    border-bottom: 0;
  }
}

@media (prefers-reduced-motion: reduce) {
  .service-entry__arrow {
    transition: none;
  }

  .service-entry:hover .service-entry__arrow {
    transform: none;
  }
}

.service-section {
  padding: 122px 0 22px;
  background: var(--rail-background);
}

.service-section__inner {
  width: min(100% - 112px, 1400px);
}

.section-heading {
  display: none;
}

.service-grid {
  grid-template-columns: repeat(5, minmax(0, 1fr));
  border-color: rgba(221, 230, 240, 0.72);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.78);
  box-shadow: 0 12px 34px rgba(11, 37, 89, 0.07);
}

.service-entry {
  grid-template-columns: 62px minmax(0, 1fr);
  min-height: 96px;
  padding: 18px 26px;
  border-bottom: 0;
  background: rgba(255, 255, 255, 0.64);
}

.service-entry:nth-child(5) {
  border-right: 0;
}

.service-entry:nth-child(6) {
  display: none;
}

.service-entry__icon {
  width: 52px;
  height: 52px;
  border-radius: 8px;
  background: linear-gradient(180deg, #2b84ff, #1666df);
  color: #ffffff;
  font-size: 30px;
  box-shadow: 0 8px 18px rgba(22, 119, 255, 0.18);
}

.service-entry__icon--orange,
.service-entry__icon--gold,
.service-entry__icon--green,
.service-entry__icon--cyan,
.service-entry__icon--navy {
  background: linear-gradient(180deg, #2b84ff, #1666df);
  color: #ffffff;
}

.service-entry__copy strong {
  color: #0b1326;
  font-size: 20px;
  font-weight: 800;
}

.service-entry__copy small,
.service-entry__arrow {
  display: none;
}

@media (max-width: 1024px) {
  .service-section__inner {
    width: min(100% - 32px, 1200px);
  }

  .service-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .service-entry:nth-child(2n) {
    border-right: 0;
  }

  .service-entry:nth-child(5) {
    grid-column: 1 / -1;
  }
}

@media (max-width: 600px) {
  .service-section {
    padding-top: 150px;
  }

  .service-section__inner {
    width: min(100% - 24px, 1200px);
  }

  .service-grid {
    grid-template-columns: 1fr;
  }

  .service-entry {
    min-height: 82px;
    border-right: 0;
  }
}
</style>
