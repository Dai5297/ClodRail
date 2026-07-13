<template>
  <section class="routes-section" aria-labelledby="popular-routes-title">
    <div class="routes-section__inner">
      <div class="routes-heading">
        <div>
          <p>热门线路</p>
          <h2 id="popular-routes-title">常选城市，快速启程</h2>
          <span>以下为常用路线参考信息，实际车次与票价以查询结果为准。</span>
        </div>
        <button type="button" class="routes-heading__more" @click="handleViewMore">
          前往车票查询 <i class="ri-arrow-right-line" aria-hidden="true"></i>
        </button>
      </div>

      <div class="route-grid">
        <button
          v-for="route in routeList"
          :key="route.id"
          type="button"
          class="route-card"
          :aria-label="`前往车票查询，${route.departure}到${route.arrival}仅为路线参考`"
          @click="handleRouteClick(route)"
        >
          <div class="route-card__topline">
            <span class="route-card__number">路线 {{ String(route.id).padStart(2, '0') }}</span>
            <span v-if="route.isHot" class="route-card__hot">热门</span>
          </div>

          <div class="route-card__journey">
            <div class="route-card__station">
              <strong>{{ route.departure }}</strong>
              <small>出发</small>
            </div>
            <div class="route-card__rail" aria-hidden="true">
              <i class="ri-train-line"></i>
              <span></span>
              <small>{{ route.duration }}</small>
            </div>
            <div class="route-card__station route-card__station--arrival">
              <strong>{{ route.arrival }}</strong>
              <small>到达</small>
            </div>
          </div>

          <div class="route-card__footer">
            <span>前往车票查询</span>
            <strong><small>¥</small>{{ route.minPrice }}<small> 起</small></strong>
            <i class="ri-arrow-right-line" aria-hidden="true"></i>
          </div>
        </button>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const routeList = ref([
  { id: 1, departure: '北京', arrival: '上海', minPrice: 553, duration: '最快 4小时28分', isHot: true },
  { id: 2, departure: '广州', arrival: '深圳', minPrice: 79, duration: '最快 1小时20分', isHot: true },
  { id: 3, departure: '成都', arrival: '重庆', minPrice: 154, duration: '最快 1小时13分', isHot: false },
  { id: 4, departure: '杭州', arrival: '南京', minPrice: 119, duration: '最快 1小时17分', isHot: false },
  { id: 5, departure: '西安', arrival: '郑州', minPrice: 174, duration: '最快 1小时30分', isHot: false },
  { id: 6, departure: '武汉', arrival: '长沙', minPrice: 152, duration: '最快 1小时20分', isHot: false },
])

const handleRouteClick = () => {
  router.push('/ticketSearch')
}

const handleViewMore = () => {
  router.push('/ticketSearch')
}
</script>

<style scoped>
.routes-section {
  padding: 46px 0 72px;
  background: var(--rail-background);
}

.routes-section__inner {
  width: min(100% - 32px, 1200px);
  margin: 0 auto;
}

.routes-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 28px;
  margin-bottom: 24px;
}

.routes-heading p {
  margin: 0 0 6px;
  color: var(--rail-primary);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
}

.routes-heading h2 {
  margin: 0;
  color: var(--rail-navy);
  font-size: clamp(24px, 3vw, 32px);
  font-weight: 760;
  letter-spacing: -0.02em;
}

.routes-heading span {
  display: block;
  margin-top: 8px;
  color: var(--rail-muted);
  font-size: 13px;
}

.routes-heading__more {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  flex: 0 0 auto;
  min-height: 40px;
  padding: 0 14px;
  border: 1px solid #bad7ff;
  border-radius: 8px;
  background: #fff;
  color: var(--rail-primary-active);
  cursor: pointer;
  font-size: 13px;
  font-weight: 700;
  transition: border-color 150ms ease, background-color 150ms ease;
}

.routes-heading__more:hover {
  border-color: var(--rail-primary);
  background: var(--rail-primary-soft);
}

.route-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.route-card {
  overflow: hidden;
  padding: 0;
  border: 1px solid var(--rail-line);
  border-radius: var(--rail-card-radius);
  background: #fff;
  color: var(--rail-ink);
  cursor: pointer;
  text-align: left;
  transition: border-color 160ms ease, box-shadow 160ms ease, transform 160ms ease;
}

.route-card:hover {
  border-color: #bad7ff;
  box-shadow: var(--rail-shadow-soft);
  transform: translateY(-2px);
}

.route-card__topline {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 38px;
  padding: 0 18px;
  border-bottom: 1px solid #edf1f6;
  background: #f9fbfd;
}

.route-card__number {
  color: #667085;
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

.route-card__hot {
  margin: 0;
  padding: 3px 7px;
  border-radius: 999px;
  background: var(--rail-action-soft);
  color: #b85900;
  font-size: 11px;
  font-weight: 700;
}

.route-card__journey {
  display: grid;
  grid-template-columns: minmax(58px, auto) 1fr minmax(58px, auto);
  align-items: center;
  gap: 13px;
  min-height: 122px;
  padding: 22px 18px 18px;
}

.route-card__station {
  display: grid;
  gap: 3px;
}

.route-card__station--arrival {
  text-align: right;
}

.route-card__station strong {
  color: var(--rail-navy);
  font-size: clamp(18px, 2vw, 22px);
  font-weight: 760;
  white-space: nowrap;
}

.route-card__station small {
  color: #98a2b3;
  font-size: 11px;
}

.route-card__rail {
  position: relative;
  display: grid;
  justify-items: center;
  min-width: 0;
  color: var(--rail-primary);
}

.route-card__rail i {
  position: relative;
  z-index: 2;
  padding: 0 7px;
  background: #fff;
  font-size: 19px;
  transition: transform 180ms ease;
}

.route-card:hover .route-card__rail i {
  transform: translateX(4px);
}

.route-card__rail span {
  position: absolute;
  top: 9px;
  right: 0;
  left: 0;
  height: 1px;
  background: repeating-linear-gradient(90deg, #a9caf2 0 7px, transparent 7px 12px);
}

.route-card__rail small {
  margin-top: 8px;
  color: #667085;
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 11px;
  text-align: center;
  white-space: nowrap;
}

.route-card__footer {
  display: grid;
  grid-template-columns: 1fr auto 24px;
  align-items: baseline;
  gap: 10px;
  padding: 14px 18px;
  border-top: 1px solid #edf1f6;
}

.route-card__footer > span {
  color: var(--rail-muted);
  font-size: 12px;
}

.route-card__footer strong {
  color: var(--rail-action-hover);
  font-family: 'Arial Narrow', Arial, sans-serif;
  font-size: 21px;
  font-variant-numeric: tabular-nums;
}

.route-card__footer strong small {
  font-size: 11px;
  font-weight: 600;
}

.route-card__footer > i {
  color: #98a2b3;
  font-size: 17px;
}

@media (max-width: 960px) {
  .route-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .routes-section {
    padding: 30px 0 52px;
  }

  .routes-section__inner {
    width: min(100% - 24px, 1200px);
  }

  .routes-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 16px;
  }

  .routes-heading__more {
    width: 100%;
    justify-content: center;
  }

  .route-grid {
    grid-template-columns: 1fr;
  }
}

@media (prefers-reduced-motion: reduce) {
  .route-card,
  .route-card__rail i {
    transition: none;
  }

  .route-card:hover,
  .route-card:hover .route-card__rail i {
    transform: none;
  }
}

.routes-section {
  padding: 24px 0 72px;
}

.routes-section__inner {
  width: min(100% - 112px, 1400px);
}

.routes-heading {
  margin-bottom: 14px;
}

.routes-heading p,
.routes-heading span,
.routes-heading__more {
  display: none;
}

.routes-heading h2 {
  font-size: 24px;
  font-weight: 800;
}

.route-grid {
  gap: 20px;
}

.route-card:nth-child(n + 4) {
  display: none;
}

.route-card {
  border-color: #dde6f0;
  border-radius: 8px;
  box-shadow: 0 8px 20px rgba(11, 37, 89, 0.06);
}

.route-card__topline {
  display: none;
}

.route-card__journey {
  min-height: 116px;
  padding: 22px 22px 12px;
}

.route-card__station strong {
  color: #0b1326;
  font-size: 22px;
}

.route-card__rail small {
  color: #1677ff;
  font-size: 15px;
}

.route-card__footer {
  padding: 12px 22px 18px;
  border-top: 0;
}

.route-card__footer strong {
  color: #ff8200;
}

@media (max-width: 1024px) {
  .routes-section__inner {
    width: min(100% - 32px, 1200px);
  }
}

@media (max-width: 640px) {
  .routes-section__inner {
    width: min(100% - 24px, 1200px);
  }
}
</style>
