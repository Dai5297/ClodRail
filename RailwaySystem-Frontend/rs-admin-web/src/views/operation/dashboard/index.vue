<template>
  <div class="dashboard-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">仪表板</h1>
      <p class="page-description">系统运营数据概览</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-grid">
      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon users">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ statistics.totalUsers }}</h3>
            <p>总用户数</p>
            <small class="text-success">
              <el-icon><CaretTop /></el-icon>
              +5.2% 较上月
            </small>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon orders">
            <el-icon :size="32"><Tickets /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ statistics.totalOrders }}</h3>
            <p>总订单数</p>
            <small class="text-success">
              <el-icon><CaretTop /></el-icon>
              +12.8% 较上月
            </small>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon revenue">
            <el-icon :size="32"><Money /></el-icon>
          </div>
          <div class="stat-info">
            <h3>¥{{ statistics.totalRevenue }}</h3>
            <p>总营收</p>
            <small class="text-success">
              <el-icon><CaretTop /></el-icon>
              +8.5% 较上月
            </small>
          </div>
        </div>
      </el-card>

      <el-card class="stat-card">
        <div class="stat-content">
          <div class="stat-icon trains">
            <el-icon :size="32"><Van /></el-icon>
          </div>
          <div class="stat-info">
            <h3>{{ statistics.activeTrains }}</h3>
            <p>运行车次</p>
            <small class="text-info">
              <el-icon><Minus /></el-icon>
              稳定运行
            </small>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">订单趋势</span>
              <el-radio-group v-model="trendPeriod" size="small">
                <el-radio-button value="week">最近7天</el-radio-button>
                <el-radio-button value="month">最近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-placeholder">
            <el-icon :size="60" color="#d9d9d9"><DataLine /></el-icon>
            <p>图表数据开发中...</p>
          </div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card class="chart-card">
          <template #header>
            <span class="card-title">订单状态分布</span>
          </template>
          <div class="chart-placeholder">
            <el-icon :size="60" color="#d9d9d9"><PieChart /></el-icon>
            <p>图表数据开发中...</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近订单 -->
    <el-card class="recent-orders">
      <template #header>
        <div class="card-header">
          <span class="card-title">最近订单</span>
          <el-button type="primary" link>查看全部</el-button>
        </div>
      </template>
      <el-table :data="recentOrders" style="width: 100%">
        <el-table-column prop="orderId" label="订单号" width="180" />
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="trainNumber" label="车次" width="100" />
        <el-table-column prop="route" label="路线" width="200" />
        <el-table-column prop="amount" label="金额" width="100">
          <template #default="{ row }">
            ¥{{ row.amount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

// 统计数据
const statistics = reactive({
  totalUsers: '12,345',
  totalOrders: '8,642',
  totalRevenue: '1,234,567',
  activeTrains: '156'
})

// 趋势周期
const trendPeriod = ref('week')

// 最近订单（模拟数据）
const recentOrders = ref([
  {
    orderId: '202401150001',
    username: '张三',
    trainNumber: 'G1234',
    route: '北京 → 上海',
    amount: 550.00,
    status: 1,
    createTime: '2024-01-15 10:30:00'
  },
  {
    orderId: '202401150002',
    username: '李四',
    trainNumber: 'D5678',
    route: '广州 → 深圳',
    amount: 75.00,
    status: 1,
    createTime: '2024-01-15 10:25:00'
  },
  {
    orderId: '202401150003',
    username: '王五',
    trainNumber: 'G9012',
    route: '杭州 → 南京',
    amount: 180.00,
    status: 0,
    createTime: '2024-01-15 10:20:00'
  }
])

// 获取状态类型
const getStatusType = (status) => {
  const typeMap = {
    0: 'warning',
    1: 'success',
    2: 'info',
    3: 'danger'
  }
  return typeMap[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    0: '待支付',
    1: '已支付',
    2: '已完成',
    3: '已取消'
  }
  return textMap[status] || '未知'
}

// 加载数据
const loadData = () => {
  // TODO: 调用接口获取真实数据
  ElMessage.info('数据加载完成（模拟数据）')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard-page {
  width: 100%;
}

.page-header {
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.page-description {
  color: #666;
  font-size: 14px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-icon.users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.orders {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.revenue {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.trains {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info h3 {
  font-size: 28px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px 0;
}

.stat-info p {
  color: #666;
  font-size: 14px;
  margin: 0 0 4px 0;
}

.stat-info small {
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 2px;
}

.text-success {
  color: #67c23a;
}

.text-info {
  color: #909399;
}

/* 图表卡片 */
.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.chart-placeholder {
  height: 300px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
}

.chart-placeholder p {
  margin-top: 16px;
  font-size: 14px;
}

/* 最近订单 */
.recent-orders {
  margin-bottom: 20px;
}
</style>

