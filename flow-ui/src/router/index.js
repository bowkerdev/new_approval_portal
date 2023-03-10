/**
 * 全站路由配置
 * 代码中路由统一使用path属性跳转
 */
import Vue from 'vue'
import Router from 'vue-router'
import http from '@/utils/httpRequest'
import common from '@/utils/common'
import {  isURL} from '@/utils/validate'
import {  clearLoginInfo} from '@/utils'
Vue.use(Router)

const routerPush = Router.prototype.push
Router.prototype.push = function push (location) {
  return routerPush.call(this, location).catch(error => error)
}
// 开发环境不使用懒加载
const _import = require('./import-' + process.env.NODE_ENV)
// 全局路由
const globalRoutes = [
  {path: '/login', component: _import('modules/sys/login/login'), name: 'login', meta: {title: '登录'}}
]

// 主入口路由
const mainRoutes = {
  path: '/',
  component: _import('main'),
  name: 'main',
  redirect: {name: 'home'},
  meta: {title: '整体布局'},
  children: [
    {path: '/redirect/:path(.*)', component: _import('modules/redirect/index')},
    {path: '/home', redirect: '',meta:{isHome:true} , name: 'home'},
    {path: '/flowable/task/TaskForm', component: _import('modules/flowable/task/TaskForm'), name: 'task-form', meta: {title: '流程表单'}},
    {path: '/flowable/task/TaskFormEdit', component: _import('modules/flowable/task/TaskFormEdit'), name: 'task-form-edit', meta: {title: '流程表单'}},
    {path: '/flowable/task/TaskFormDetail', component: _import('modules/flowable/task/TaskFormDetail'), name: 'task-form-detail', meta: {title: '流程表单详情'}},
    {path: '/form/generateList', component: _import('modules/form/GenerateList'), name: 'form-preview-list', meta: {title: '列表'}},
    {path: '/echarts/GenerateChart', component: _import('modules/echarts/GenerateChart'), name: 'echarts-generate', meta: {title: '预览图表'}},
    {path: '/form/explorer', component: null, name: 'form-explorer', meta: {title: '浏览器', type: 'iframe'}},
    {path: '/404', component: _import('common/404'), name: '404', meta: {title: '404未找到'}}

  ]
}

const router = new Router({
  mode: 'hash',
  scrollBehavior: () => ({y: 0}),
  isAddDynamicMenuRoutes: false, // 是否已经添加动态(菜单)路由
  routes: globalRoutes.concat(mainRoutes)
})
// 添加动态(菜单)路由
router.beforeEach((to, from, next) => {
  var ssoToken = getParamToken()
  if (process.env.VUE_APP_SSO_LOGIN != 'true'  && ssoToken != null) {
    window.location.href = window.location.origin + window.location.pathname
    return
  }
  try{
    if(sessionStorage.getItem('dictList')==null ){
      http({
        url: '/sys/dict/getAll',
        method: 'get'
      }).then(({data}) => {
        if (data && data.success) {
          sessionStorage.setItem('dictList', JSON.stringify(data.dicts || '[]'))
        }
      }).catch((e) => {
        console.log(e.message)
      })
    }
  }catch(e){
    console.log(e.message)
  }
  let token = Vue.cookie.get('token') || ssoToken
  if (!token || !/\S/.test(token) || to.name == 'login') { // token为空，跳转到login登录
    clearLoginInfo()
    if (process.env.VUE_APP_SSO_LOGIN === 'true') { // 如果是单点登录
      window.location.href = process.env.VUE_APP_SSO_SERVER
    } else {
      if (fnCurrentRouteType(to, globalRoutes) === 'global') {
        next()
      } else {
        next({name: 'login'})
      }
    }
  } else if (router.options.isAddDynamicMenuRoutes) { // 如果已经包含权限
    if (process.env.VUE_APP_SSO_LOGIN === 'true' && ssoToken != null && ssoToken != '' ) {
      if(deleteParamToken()){
        return
      }
    }
    next()
  } else { // 请求权限
    function dealError(e) {
      if (process.env.VUE_APP_SSO_LOGIN === 'true') {
        if(e.response.status == 401 && e.response.data.code == 401){
          console.log(e.response.data.msg, 'color:blue')
          alert(JSON.stringify(e))
          window.location.href = process.env.VUE_APP_SSO_SERVER
        }
      } else {
        next({  name: 'login'  })
      }
      console.log(`%c${e} 请求菜单列表和权限失败，跳转至登录页！！`, 'color:blue')
    }
    function getMenus(){
      http({
        url: '/sys/user/getMenus',
        method: 'get'
      }).then(({data}) => {
        if (data && data.success) {
          fnAddDynamicMenuRoutes(data.routerList)
          router.options.isAddDynamicMenuRoutes = true
          sessionStorage.setItem('allMenuList', JSON.stringify(data.menuList || '[]'))
          sessionStorage.setItem('permissions', JSON.stringify(data.permissions || '[]'))
          sessionStorage.setItem('dictList', JSON.stringify(data.dictList || '[]'))
          next({...to, replace: true})
        } else {
          sessionStorage.setItem('allMenuList', '[]')
          sessionStorage.setItem('permissions', '[]')
          sessionStorage.setItem('dictList', '[]')
          next()
        }
      }).catch((e) => {
        dealError(e)
      })
    }
    function getSysToken(){
      http({
        url: `/sys/tokenLogin?ssoToken=${ssoToken}&ssoTokenType=${process.env.VUE_APP_SSO_TYPE}`,
        method: 'get'
      }).then(({data}) => {
        if (data && data.success) {
          clearLoginInfo ()
          Vue.cookie.set(process.env.VUE_APP_SSO_TYPE+'_token', ssoToken)
          Vue.cookie.set('ssoTokenType', process.env.VUE_APP_SSO_TYPE)
          Vue.cookie.set('token', data.token)
          Vue.cookie.set('refreshToken', data.refreshToken)
          getMenus()
        } else {
          var e={response:{status:401,data:{code:401,msg:data.msg}}}
          dealError(e)
        }
      }).catch((e) => {
        dealError(e)
      })
    }
    if (process.env.VUE_APP_SSO_LOGIN === 'true' && ssoToken != null && ssoToken != '') {
      if (Vue.cookie.get(process.env.VUE_APP_SSO_TYPE + '_token') != ssoToken) {
        http({
          url: '/sys/logout',
          method: 'get'
        }).then(({data}) => {
           getSysToken()
        })
      }
      else{
        getSysToken()
      }
    }
    else{
      getMenus()
    }
  }
})

function getParamToken() {
  var ssoToken = common.getUrlParam("token")
  if (ssoToken == null) {
    ssoToken = common.getUrlParam("access_token", window.location.href)
  }
  return ssoToken;
}

function deleteParamToken() {
  var tmp = window.location.href.split('?')
  var tmp2 = ['access_token', 'refresh_token', 'token_type', 'expires_in', 'token']
  var paramList = []
  if (tmp.length > 1) {
    var needRedirect = false
    var param = tmp[1].replace('?', '').split('&')
    for (var i = 0; i < param.length; i++) {
      var tmp3 = param[i].split('=')
      if (tmp3.length > 1) {
        if (tmp2.indexOf(tmp3[0]) > -1) {
          if (tmp3[0] != 'token') {
            Vue.cookie.set(tmp3[0], tmp3[1])
          }
          needRedirect = true
        } else {
          paramList.push(param[i])
        }
      }
    }
    if (needRedirect) {
      setTimeout(() => {
        window.location.href = tmp[0] + '?' + paramList.join('&')
      }, 50)
      return true
    }
    return false
  }
}

/**
 * 判断当前路由类型, global: 全局路由, main: 主入口路由
 * @param {*} route 当前路由
 */
function fnCurrentRouteType(route, globalRoutes = []) {
  let temp = []
  for (let i = 0; i < globalRoutes.length; i++) {
    if (route.path === globalRoutes[i].path) {
      return 'global'
    } else if (globalRoutes[i].children && globalRoutes[i].children.length >= 1) {
      temp = temp.concat(globalRoutes[i].children)
    }
  }
  return temp.length >= 1 ? fnCurrentRouteType(route, temp) : 'main'
}

/**
 * 添加动态(菜单)路由
 * @param {*} menuList 菜单列表
 * @param {*} routes 递归创建的动态(菜单)路由
 */
function fnAddDynamicMenuRoutes (menuList = [], routes = []) {
  let temp = []
  for (let i = 0; i < menuList.length; i++) {
    if (menuList[i].children && menuList[i].children.length >= 1) {
      temp = temp.concat(menuList[i].children)
    }

    if (menuList[i].href && /\S/.test(menuList[i].href)) {
      menuList[i].href = menuList[i].href.replace(/[/]$/, '')
      const route = {
        path: menuList[i].href.split('?')[0],
        fullPath: menuList[i].href,
        component: null,
        name: menuList[i].href.replace(/^\//g, '').replace(/[/]/g, '-').replace(/[?]/g, '-').replace(/&/g, '-').replace(/=/g, '-'),
        meta: {
          parentIds: menuList[i].parentIds,
          menuId: menuList[i].id,
          title: menuList[i].name,
          isDynamic: true,
          type: menuList[i].target,
          affix: menuList[i].affix === '1',
          iframeUrl: ''
        }
      }
      // url以http[s]://开头, 通过iframe展示
      if (isURL(menuList[i].href)) {
        if (menuList[i].href.indexOf('#{token}')) {
          var ssoToken = Vue.cookie.get(process.env.VUE_APP_SSO_TYPE + '_token') || ''
          menuList[i].href = menuList[i].href.replace('#{ssoToken}', ssoToken)
          menuList[i].href = menuList[i].href.replace('#{ssoTokenType}', process.env.VUE_APP_SSO_TYPE)
          if (menuList[i].href.indexOf('language') == -1) {
            menuList[i].href += (menuList[i].href.indexOf('?') == -1 ? '?' : '&')
            menuList[i].href += 'language=' + Vue.config.lang
          }
        }
        route.path = '/' + route.path
        route.meta.iframeUrl = menuList[i].href
        route['meta']['type'] = 'iframe'
      } else if (menuList[i].target === 'iframe') {
        route['meta']['iframeUrl'] = `${process.env.VUE_APP_SERVER_URL}${menuList[i].href}`
      } else {
        try {
          if (menuList[i].href) {
            route['component'] = _import(`modules${menuList[i].href.split('?')[0]}`) || null
          }
        } catch (e) {
          console.log(e)
        }
      }
      routes.push(route)
    }
  }
  if (temp.length >= 1) {
    fnAddDynamicMenuRoutes(temp, routes)
  } else {
    mainRoutes.name = 'main-dynamic'
    mainRoutes.children = routes
    router.addRoute(mainRoutes)
    router.addRoute({path: '*', redirect: {name: '404'}})
    sessionStorage.setItem('dynamicMenuRoutes', JSON.stringify(mainRoutes.children || []))
  }
}

export default router
