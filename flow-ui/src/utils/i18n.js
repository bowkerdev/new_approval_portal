import Vue from 'vue'
import VueI18n from 'vue-i18n'
import enLocale from 'element-ui/lib/locale/lang/en'
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'
import hkLocale from 'element-ui/lib/locale/lang/zh-TW'
import vnLocale from 'element-ui/lib/locale/lang/vi'
import camLocale from 'element-ui/lib/locale/lang/th' // 可能是泰语

const showCompleteMenuNameConfig = ['en-US']

Vue.config.lang = localStorage.getItem('lang') || 'en-US'
// Vue.config.lang = 'en-US'
var langList = [{
    lang: 'zh-CN',
    src: "/static/images/china.png",
    text: "中文"
  },
  /* {
    lang: 'zh-TW',
    src: "/static/images/cnhk.png",
    text: "中國香港 繁體中文"
  }, */
  {
    lang: 'en-US',
    src: "/static/images/united-kingdom.png",
    text: "English"
  },
 /* {
    lang: 'vn',
    src: "/static/images/vn.png",
    text: "VN Việt Nam"
  },
  {
    lang: 'cam',
    src: "/static/images/cam.png",
    text: "CAM Cambodia"
  }, */
]
const setup = lang => {
  localStorage.setItem('lang', lang)
  Vue.config.lang = lang
  if(window.$i18nMy != null){
    var languageData = localStorage.getItem('languageData') || "{}"
    languageData = JSON.parse(languageData);
    window.$i18nMy.langData = {}
    for (var key in languageData) {
      if (languageData[key] != null) {
        switch (lang){
          case 'en-US':window.$i18nMy.langData[key]=languageData[key].en; break;
          case 'zh-CN':window.$i18nMy.langData[key]=languageData[key].cn; break;
          case 'zh-TW':window.$i18nMy.langData[key]=languageData[key].hk; break;
          case 'vn':window.$i18nMy.langData[key]=languageData[key].vn; break;
          case 'cam':window.$i18nMy.langData[key]=languageData[key].cam; break;
          default: break;
        }
      }
    }
  }
  const messages = {
    'en-US': {
      ...enLocale
    },
    'zh-CN': {
      ...zhLocale
    },
    'zh-TW': {
      ...hkLocale
    },
    'vn': {
      ...vnLocale
    },
    'cam': {
      ...camLocale
    }
  }
  return messages
}
const messages = setup(Vue.config.lang)

Vue.use(VueI18n)
const i18n = new VueI18n({
  locale: Vue.config.lang,
  messages
})
window.$i18n = i18n
window.$setI18n = setup
window.langList = langList

export function currentLangInshowCompName() {
  const currentLang = localStorage.getItem('lang') || Vue.config.lang
  return showCompleteMenuNameConfig.indexOf(currentLang) > -1
}

export default i18n
