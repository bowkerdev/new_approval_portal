import Vue from 'vue'
import VueI18n from 'vue-i18n'
import request from './request.js'

var i18nMy = {}

if(window.$i18nMy == null){
  window.$i18nMy = i18nMy
}
i18nMy.initFlag == ""
i18nMy.langData = {}

function saveLang(key,languageData){
	if (window.$i18nMy.initFlag == "OK"&&
	key != null && key != '' && languageData[key] == null &&
	window.translationedList.indexOf(key) < 0) {
	  console.log("后台国际化："+key)
	  window.translationedList.push(key);
	  request.post('/sys/sysSimpleLanguage/save/automation',
		{code : key},{'Content-Type':'application/json'}).then(({
	    data
	  }) => {}).catch((e) => {})
	}
}

i18nMy.t = function t(key) {
  var res=$i18nMy.langData[key]
  if(res !=null){
    return res;
  }
  var values = [],
    len = arguments.length - 1;
  while (len-- > 0) values[len] = arguments[len + 1];

  var languageData = localStorage.getItem('languageData') || "{}"
  languageData = JSON.parse(languageData);

  if(window.translationedList == null){
    window.translationedList = []
  }
  if(window.$i18nMy.initFlag != "OK"){
	setTimeout(()=> {saveLang(key,languageData)},3000) 
  }else{
	saveLang(key,languageData)
  }
  if(languageData[key] !=null){
    switch (Vue.config.lang){
      case 'en-US':window.$i18nMy.langData[key]=languageData[key].en; break;
      case 'zh-CN':window.$i18nMy.langData[key]=languageData[key].cn; break;
      case 'zh-TW':window.$i18nMy.langData[key]=languageData[key].hk; break;
      case 'vn':window.$i18nMy.langData[key]=languageData[key].vn; break;
      case 'cam':window.$i18nMy.langData[key]=languageData[key].cam; break;
      default: break;
    }
  }
  return window.$i18nMy.langData[key]||key

};

request.get('/sys/sysSimpleLanguage/getAll')
.then(({data}) => {
  if (data && data.success) {
    localStorage.setItem('languageData', JSON.stringify(data.lanMap || '{}'))
    var m = window.$setI18n(localStorage.getItem('lang'))
    window.$i18nMy.initFlag="OK"
  }
}).catch((e) => {
  console.log(e.message)
})

export default i18nMy
