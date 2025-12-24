import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import i18n from './locales'
import './styles/theme.css'

// 引入 Element Plus
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

// 引入 Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 使用 Element Plus，配置中文
app.use(ElementPlus, {
  locale: zhCn,
})

app.use(router)
app.use(i18n)

app.mount('#app')
