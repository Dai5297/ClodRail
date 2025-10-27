<template>
  <div class="notice-section">
    <h4 class="subsection-title">
      <el-icon size="18" color="#fa8c16">
        <InfoFilled />
      </el-icon>
      购票须知
    </h4>
    <div class="notice-content">
      <div class="notice-item">
        <el-icon size="16" color="#1890ff"><Check /></el-icon>
        <span>请提前30分钟到达车站，预留充足时间进站</span>
      </div>
      <div class="notice-item">
        <el-icon size="16" color="#1890ff"><Check /></el-icon>
        <span>购票时请确认身份证信息准确无误</span>
      </div>
      <div class="notice-item">
        <el-icon size="16" color="#1890ff"><Check /></el-icon>
        <span>儿童票需要在成人陪同下购买和使用</span>
      </div>
      <div class="notice-item">
        <el-icon size="16" color="#1890ff"><Check /></el-icon>
        <span>退改签请按照铁路部门相关规定执行</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { InfoFilled, Check } from '@element-plus/icons-vue'
</script>

<style scoped>
.notice-section {
  margin-bottom: 20px;
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

.notice-content {
  background: #f8f9fa;
  padding: 24px;
  border-radius: 12px;
  border-left: 4px solid #1890ff;
}

.notice-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
  font-size: 14px;
  color: #555;
}

.notice-item:last-child {
  margin-bottom: 0;
}
</style>

