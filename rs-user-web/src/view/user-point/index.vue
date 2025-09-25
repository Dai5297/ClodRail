<template>
  <div class="user-points-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2 class="page-title">积分管理</h2>
      <p class="page-desc">查看您的积分信息和明细记录</p>
    </div>

    <!-- 积分概览 -->
    <div class="points-overview" v-loading="overviewLoading">
      <div class="overview-card">
        <div class="card-header">
          <h3 class="card-title">我的积分</h3>
          <el-icon class="card-icon"><Trophy /></el-icon>
        </div>
        <div class="card-content">
          <div class="points-info">
            <div class="current-points">
              <span class="points-number">{{ pointsInfo.currentPoints || 0 }}</span>
              <span class="points-unit">积分</span>
            </div>
            <div class="points-details">
              <div class="detail-item">
                <span class="label">累计获得：</span>
                <span class="value">{{ pointsInfo.totalEarned || 0 }} 积分</span>
              </div>
              <div class="detail-item">
                <span class="label">累计消费：</span>
                <span class="value">{{ pointsInfo.totalSpent || 0 }} 积分</span>
              </div>
              <div class="detail-item">
                <span class="label">即将过期：</span>
                <span class="value expire-warning">{{ pointsInfo.expiringSoon || 0 }} 积分</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 积分明细 -->
    <div class="points-history">
      <div class="history-header">
        <h3 class="history-title">积分明细</h3>
        <div class="history-filter">
          <el-form :model="filterForm" inline>
            <el-form-item label="类型">
              <el-select v-model="filterForm.type" placeholder="全部类型" clearable @change="handleFilter">
                <el-option label="全部" value="" />
                <el-option label="获得" value="earn" />
                <el-option label="消费" value="spend" />
                <el-option label="过期" value="expire" />
              </el-select>
            </el-form-item>
            <el-form-item label="时间">
              <el-date-picker
                  v-model="filterForm.dateRange"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  format="YYYY-MM-DD"
                  value-format="YYYY-MM-DD"
                  @change="handleFilter"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleFilter">查询</el-button>
              <el-button @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>
      </div>

      <div class="history-list" v-loading="historyLoading">
        <div v-if="historyList.length === 0" class="empty-state">
          <el-empty description="暂无积分记录" />
        </div>
        <div v-else>
          <div
              v-for="record in historyList"
              :key="record.id"
              class="history-item"
          >
            <div class="item-icon" :class="getTypeClass(record.type)">
              <el-icon>
                <Plus v-if="record.type === 'earn'" />
                <Minus v-else-if="record.type === 'spend'" />
                <Clock v-else />
              </el-icon>
            </div>
            <div class="item-content">
              <div class="item-title">{{ record.description }}</div>
              <div class="item-time">{{ record.createTime }}</div>
            </div>
            <div class="item-points" :class="getPointsClass(record.type)">
              {{ getPointsPrefix(record.type) }}{{ record.points }}
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination" v-if="total > 0">
        <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Trophy, Plus, Minus, Clock } from '@element-plus/icons-vue'
import { getPointsInfo, getPointsHistory } from '@/api/points.js'

export default {
  name: 'UserPoints',
  components: {
    Trophy,
    Plus,
    Minus,
    Clock
  },
  setup() {
    const overviewLoading = ref(false)
    const historyLoading = ref(false)
    const pointsInfo = ref({})
    const historyList = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)

    const filterForm = reactive({
      type: '',
      dateRange: null
    })

    const fetchPointsInfo = async () => {
      try {
        overviewLoading.value = true
        const data = await getPointsInfo()
        pointsInfo.value = data || {}
      } catch (error) {
        ElMessage.error('获取积分信息失败')
      } finally {
        overviewLoading.value = false
      }
    }

    const fetchPointsHistory = async () => {
      try {
        historyLoading.value = true
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          type: filterForm.type || undefined,
          startDate: filterForm.dateRange?.[0],
          endDate: filterForm.dateRange?.[1]
        }

        const data = await getPointsHistory(params)
        historyList.value = data.records || []
        total.value = data.total || 0
      } catch (error) {
        ElMessage.error('获取积分明细失败')
      } finally {
        historyLoading.value = false
      }
    }

    const handleFilter = () => {
      currentPage.value = 1
      fetchPointsHistory()
    }

    const handleReset = () => {
      filterForm.type = ''
      filterForm.dateRange = null
      currentPage.value = 1
      fetchPointsHistory()
    }

    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      fetchPointsHistory()
    }

    const handleCurrentChange = (page) => {
      currentPage.value = page
      fetchPointsHistory()
    }

    const getTypeClass = (type) => {
      const classMap = {
        'earn': 'earn-icon',
        'spend': 'spend-icon',
        'expire': 'expire-icon'
      }
      return classMap[type] || 'default-icon'
    }

    const getPointsClass = (type) => {
      const classMap = {
        'earn': 'earn-points',
        'spend': 'spend-points',
        'expire': 'expire-points'
      }
      return classMap[type] || 'default-points'
    }

    const getPointsPrefix = (type) => {
      return type === 'earn' ? '+' : '-'
    }

    onMounted(() => {
      fetchPointsInfo()
      fetchPointsHistory()
    })

    return {
      overviewLoading,
      historyLoading,
      pointsInfo,
      historyList,
      total,
      currentPage,
      pageSize,
      filterForm,
      fetchPointsInfo,
      fetchPointsHistory,
      handleFilter,
      handleReset,
      handleSizeChange,
      handleCurrentChange,
      getTypeClass,
      getPointsClass,
      getPointsPrefix
    }
  }
}
</script>

<style scoped>
.user-points-page {
  padding: 0;
}

.page-header {
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #262626;
  margin: 0 0 8px 0;
}

.page-desc {
  font-size: 14px;
  color: #8c8c8c;
  margin: 0;
}

.points-overview {
  margin-bottom: 32px;
}

.overview-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  color: white;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.card-icon {
  font-size: 24px;
  opacity: 0.8;
}

.points-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.current-points {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.points-number {
  font-size: 36px;
  font-weight: 700;
}

.points-unit {
  font-size: 16px;
  opacity: 0.9;
}

.points-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
  text-align: right;
}

.detail-item {
  display: flex;
  gap: 8px;
  font-size: 14px;
}

.detail-item .label {
  opacity: 0.8;
}

.detail-item .value {
  font-weight: 500;
}

.expire-warning {
  color: #ffd666;
}

.points-history {
  background: white;
  border-radius: 8px;
  padding: 24px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.history-title {
  font-size: 16px;
  font-weight: 600;
  color: #262626;
  margin: 0;
}

.history-filter {
  display: flex;
  align-items: center;
}

.history-list {
  min-height: 300px;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
}

.history-item:last-child {
  border-bottom: none;
}

.item-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 16px;
}

.earn-icon {
  background: #f6ffed;
  color: #52c41a;
}

.spend-icon {
  background: #fff2e8;
  color: #fa8c16;
}

.expire-icon {
  background: #f5f5f5;
  color: #8c8c8c;
}

.item-content {
  flex: 1;
}

.item-title {
  font-size: 14px;
  color: #262626;
  margin-bottom: 4px;
}

.item-time {
  font-size: 12px;
  color: #8c8c8c;
}

.item-points {
  font-size: 16px;
  font-weight: 600;
}

.earn-points {
  color: #52c41a;
}

.spend-points {
  color: #fa8c16;
}

.expire-points {
  color: #8c8c8c;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .points-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .points-details {
    align-self: stretch;
    text-align: left;
  }

  .history-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>