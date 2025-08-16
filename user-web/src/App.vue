<script setup lang="ts">
// App.vue should only contain router-view for proper routing
</script>

<template>
  <router-view />
</template>

<style>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.5;
  color: #262626;
  background-color: #fff;
}

#app {
  height: 100%;
  width: 100%;
  min-height: 100vh;
  margin: 0;
  padding: 0;
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 链接样式重置 */
a {
  color: inherit;
  text-decoration: none;
}

/* 按钮样式重置 */
button {
  border: none;
  background: none;
  cursor: pointer;
}

/* 输入框样式重置 */
input, textarea, select {
  outline: none;
}

/* 列表样式重置 */
ul, ol {
  list-style: none;
}

/* 图片样式 */
img {
  max-width: 100%;
  height: auto;
  vertical-align: middle;
}

/* 表格样式重置 */
table {
  border-collapse: collapse;
  border-spacing: 0;
}

/* 响应式图片和媒体 */
img, video, canvas, audio, iframe, embed, object {
  display: block;
  max-width: 100%;
}

/* 清除浮动 */
.clearfix::after {
  content: '';
  display: table;
  clear: both;
}

/* 隐藏元素 */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.3s ease;
}

.page-enter-from,
.page-leave-to {
  opacity: 0;
}
</style>
