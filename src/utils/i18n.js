import Vue from 'vue'
import VueI18n from 'vue-i18n'

import enLocale from 'element-ui/lib/locale/lang/en'
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'

Vue.config.lang = 'en-US'

var z = {
  '确定': '确定',
  '重置': '重置',
  '确定删除所选项吗?': '',
  '请登录系统再操作！': '请登录系统再操作！',
  '表单名称': '表单名称'
}
var e = {
  '确定': 'OK',
  '重置': 'reset',
  '确定删除所选项吗?': 'delete ?',
  '请登录系统再操作！': 'pls login!',
  '表单名称': 'Form name'
}

const messages = {
  'en-US': { ...e,
    ...enLocale
  },
  'zh-CN': { ...z,
    ...zhLocale
  }
}
Vue.use(VueI18n)

const i18n = new VueI18n({
  locale: 'en-US',
  messages
})

export default i18n
