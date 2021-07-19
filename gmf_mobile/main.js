import Vue from 'vue'
import App from './App'
import http from './utils/http'
import common from './utils/common'
import token from './utils/token'
import utils from '@/utils'
//  #ifdef  H5
import * as dd from 'dingtalk-jsapi';
// #endif 

Vue.config.productionTip = false
Vue.prototype.$http = http.uniRequest 
Vue.prototype.$common = common
Vue.prototype.$token = token
Vue.prototype.$utils = utils



//  #ifdef  H5 || MP-ALIPAY
Vue.prototype.$dd = dd
// #endif 

App.mpType = 'app'

const app = new Vue({
    ...App
})
app.$mount()
