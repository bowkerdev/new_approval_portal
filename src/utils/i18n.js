import Vue from 'vue'
import VueI18n from 'vue-i18n'
import enLocale from 'element-ui/lib/locale/lang/en'
import zhLocale from 'element-ui/lib/locale/lang/zh-CN'
import hkLocale from 'element-ui/lib/locale/lang/zh-TW'
import vnLocale from 'element-ui/lib/locale/lang/vi'
import camLocale from 'element-ui/lib/locale/lang/th' // 可能是泰语
import http from '@/utils/httpRequest'

Vue.config.lang = localStorage.getItem('lang') || 'en-US'
//Vue.config.lang = 'en-US'
var zh = {}
var en = {}
var hk = {}
var vn = {}
var cam = {}

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

export const setup = lang => {
  localStorage.setItem('lang', lang)
  Vue.config.lang = lang
  var languageData = localStorage.getItem('languageData') || "{}"
  languageData = JSON.parse(languageData);
  for (var key in languageData) {
    if (languageData[key] != null) {
      zh[key] = languageData[key].cn
      en[key] = languageData[key].en
      hk[key] = languageData[key].hk
      vn[key] = languageData[key].vn
      cam[key] = languageData[key].cam
    }
  }
  const messages = {
    'en-US': { ...en,
      ...enLocale
    },
    'zh-CN': { ...zh,
      ...zhLocale
    },
    'zh-TW': { ...hk,
      ...hkLocale
    },
    'vn': { ...vn,
      ...vnLocale
    },
    'cam': { ...cam,
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

VueI18n.prototype.t = function t(key) {
  var values = [],
    len = arguments.length - 1;
  while (len-- > 0) values[len] = arguments[len + 1];

  var translationedList = localStorage.getItem('translationedList') || "";
  translationedList = translationedList.split(",")
  var languageData = localStorage.getItem('languageData') || "{}"
  languageData = JSON.parse(languageData);
  if (window.$i18n.initFlag == "OK"&&
  key != null && key != '' &&
  translationedList.indexOf(key) < 0 && languageData[key] == null &&
  key.indexOf("el.")!=0) {
    translationedList.push(key);
    http({
      url: '/sys/sysSimpleLanguage/save/automation?code=' + key,
      method: 'post'
    }).then(({
      data
    }) => {}).catch((e) => {})
  }
  return (ref = this)._t.apply(ref, [key, this.locale, this.messages, null].concat(values))
  var ref;
};
window.$i18n = i18n
window.$i18n.initFlag = ""
window.$setI18n = setup
window.langList = langList

http({
  url: '/sys/sysSimpleLanguage/getAll',
  method: 'get'
}).then(({
  data
}) => {
  if (data && data.success) {
    localStorage.setItem('languageData', JSON.stringify(data.lanMap || '{}'))
    var m = setup(localStorage.getItem('lang'))
    window.$i18n.locale = Vue.config.lang
    window.$i18n.initFlag="OK"
    window.$i18n.setLocaleMessage(Vue.config.lang, m[Vue.config.lang])
  }
}).catch((e) => {
  console.log(e.message)
})

export default i18n
