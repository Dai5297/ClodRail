import { createI18n } from 'vue-i18n'
import zhCn from './zh-cn'
import en from './en'

const i18n = createI18n({
    legacy: false, // 使用 Composition API
    locale: 'zh-cn', // 默认语言
    fallbackLocale: 'en',
    messages: {
        'zh-cn': zhCn,
        en
    }
})

export default i18n
