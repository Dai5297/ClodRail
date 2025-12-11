<template>
  <div class="tips">
    <h4 class="tips-title">
      <el-icon><InfoFilled /></el-icon>
      温馨提示
    </h4>
    <ul class="tips-list">
      <li>车票信息已发送至您的手机和邮箱</li>
      <li>请提前到站，凭身份证直接检票乘车</li>
      <li>如需改签或退票，请在订单页面操作</li>
      <li>如有疑问，请拨打客服热线：400-123-4567</li>
    </ul>
  </div>
</template>

<script setup>
import { InfoFilled } from '@element-plus/icons-vue'
</script>

<style scoped>
.tips {
  background: #e6f7ff;
  border: 1px solid #91d5ff;
  border-radius: 8px;
  padding: 20px;
  text-align: left;
}

.tips-title {
  font-size: 16px;
  color: #0050b3;
  margin: 0 0 12px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.tips-list {
  margin: 0;
  padding-left: 20px;
  color: #0050b3;
  font-size: 14px;
  line-height: 1.8;
}

.tips-list li {
  margin-bottom: 6px;
}
</style>



