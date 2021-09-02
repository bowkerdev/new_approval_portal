import Vue from 'vue'
import VueI18n from 'vue-i18n'

const showCompleteMenuNameConfig = ['en-US']

Vue.config.lang = localStorage.getItem('lang') || 'en-US'
// Vue.config.lang = 'en-US'
var langList = [{
    lang: 'zh-CN',
    src: "/static/images/china.png",
    text: "中国 中文"
  },
  {
    lang: 'zh-TW',
    src: "/static/images/cnhk.png",
    text: "中國香港 繁體中文"
  },
  {
    lang: 'en-US',
    src: "/static/images/united-kingdom.png",
    text: "EN English"
  },
  {
    lang: 'vn',
    src: "/static/images/vn.png",
    text: "VN Việt Nam"
  },
  {
    lang: 'cam',
    src: "/static/images/cam.png",
    text: "CAM Cambodia"
  },
]
const setup = lang => {
  localStorage.setItem('lang', lang||Vue.config.lang)
  Vue.config.lang = lang
  if(window.$i18nMy != null){
    var languageData = localStorage.getItem('languageData') || "{}"
    languageData = JSON.parse(languageData);
    window.$i18nMy.langData = {}
    for (var key in languageData) {
      if (languageData[key] != null) {
        switch (Vue.config.lang){
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
}

window.$setI18n = setup
window.langList = langList

export function currentLangInshowCompName() {
  const currentLang = localStorage.getItem('lang') || Vue.config.lang
  return showCompleteMenuNameConfig.indexOf(currentLang) > -1
}
