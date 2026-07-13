<template>
  <aside class="tips" aria-labelledby="success-tips-title">
    <h4 id="success-tips-title" class="tips-title">
      <el-icon aria-hidden="true"><InfoFilled /></el-icon>
      乘车提示
    </h4>
    <ul class="tips-list">
      <li>请提前到站，凭购票时使用的有效身份证件检票乘车</li>
      <li>车票和乘车人信息可在“我的订单”中随时查看</li>
      <li>如需改签或退票，请在订单详情页按铁路规则办理</li>
    </ul>
  </aside>
</template>

<script setup>
import { InfoFilled } from '@element-plus/icons-vue'
</script>

<style scoped>
.tips { padding: 16px 18px; border: 1px solid color-mix(in srgb, var(--rail-primary) 24%, var(--rail-line)); border-radius: 8px; background: var(--rail-primary-soft); text-align: left; }
.tips-title { display: flex; align-items: center; gap: 8px; margin: 0 0 10px; color: var(--rail-navy); font-size: 14px; font-weight: 700; }
.tips-list { margin: 0; padding-left: 20px; color: var(--rail-muted); font-size: 13px; line-height: 1.8; }
.tips-list li::marker { color: var(--rail-primary); }
</style>
