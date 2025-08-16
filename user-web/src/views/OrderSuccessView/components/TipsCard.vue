<template>
  <div class="tips-card">
    <div class="card-header">
      <el-icon size="20" color="#faad14"><InfoFilled /></el-icon>
      <h3>温馨提示</h3>
    </div>
    
    <div class="tips-content">
      <ul class="tips-list">
        <li>请提前30分钟到达车站，携带有效身份证件进站乘车</li>
        <li>车票为实名制，请确保乘车人与车票信息一致</li>
        <li>如需改签或退票，请在开车前办理相关手续</li>
        <li>乘车时请保管好随身物品，注意人身安全</li>
        <li>列车禁止吸烟，请遵守车厢内相关规定</li>
      </ul>
    </div>
  </div>
</template>

<script setup lang="ts">
import { InfoFilled } from '@element-plus/icons-vue'
</script>

<style scoped>
.tips-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-left: 4px solid #faad14;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
}

.card-header h3 {
  margin: 0;
  font-size: 16px;
  color: #262626;
}

.tips-content {
  color: #595959;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  line-height: 1.8;
}

.tips-list li {
  margin-bottom: 8px;
  font-size: 14px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}
</style>