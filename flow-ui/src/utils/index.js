import Vue from 'vue'
import router from '@/router'
import store from '@/store'
import dictUtils from '@/utils/dictUtils'
import $http from './httpRequest'
import { isPlainObject } from 'lodash'
import { snakeCase } from "lodash"

export const operatorMap = {
	"_eq": "=",
	"_ne": "!=",
	"_not_like": "not like",
	"_like": "like",
	"_ge": ">=",
	"_le": "<=",
	"_not_in": "not in",
	"_in": "in",
	"_not_null": "is not",
	"_null": "is"
}

/**
 * 是否有权限
 * @param {*} key
 */
export function hasPermission (key) {
  return JSON.parse(sessionStorage.getItem('permissions') || '[]').indexOf(key) !== -1 || false
}
/**
 * 树形数据转换
 * @param {*} data list数据
 * @param {*} id 主键ID
 * @param {*} pid 上级ID
 * @param childrenKey 子list数据的key
 */
export function treeDataTranslate (data, id = 'id', pid = 'parentId', childrenKey = 'children') {
  let res = []
  let temp = {}
  for (let i = 0; i < data.length; i++) {
    temp[data[i][id]] = data[i]
  }
  for (let k = 0; k < data.length; k++) {
    if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
      if (!temp[data[k][pid]][childrenKey]) {
        temp[data[k][pid]][childrenKey] = []
      }
      if (!temp[data[k][pid]]['_level']) {
        temp[data[k][pid]]['_level'] = 1
      }
      data[k]['_level'] = temp[data[k][pid]]._level + 1
      temp[data[k][pid]][childrenKey].push(data[k])
    } else {
      res.push(data[k])
    }
  }
  return res
}

/**
 * 树形数据转换--占锐修改，增加自定义展示级别
 * @param {*} data list数据
 * @param {*} id 主键ID
 * @param {*} pid 上级ID
 * @param childrenKey 子list数据的key
 * @param level 展开级别
 * @param ifTop 是否默认展开顶级节点
 */
export function treeDataTranslateWithLevel (data, id = 'id', pid = 'parentId', childrenKey = 'childNodes', level = 2, ifTop = true) {
  // console.log('展开级别：', level)
  let result = []
  let res = []
  let resKeys = []
  let temp = {}
  for (let i = 0; i < data.length; i++) {
    temp[data[i][id]] = data[i]
  }
  for (let k = 0; k < data.length; k++) {
    if (temp[data[k][pid]] && data[k][id] !== data[k][pid]) {
      if (!temp[data[k][pid]][childrenKey]) {
        temp[data[k][pid]][childrenKey] = []
      }
      if (!temp[data[k][pid]]['_level']) {
        temp[data[k][pid]]['_level'] = 1
      }
      data[k]['_level'] = temp[data[k][pid]]._level + 1
      temp[data[k][pid]][childrenKey].push(data[k])

      // 如果小于设定展开的节点数字，通过children来展开 【 文档: https://element.eleme.cn/#/zh-CN/component/table （树形数据与懒加载）】
      // console.log(data[k])
      let _parentIds = data[k].parentIds.split(',')
      // console.log(_parentIds)
      if (_parentIds.length < level + 1) {
        resKeys.push(data[k].id)
      }
    } else {
      // 如果是顶级节点
      // console.log(data[k])
      if (ifTop) {
        resKeys.push(data[k].id) // 【2020年4月10日17:23:23 修复展开级别bug ,顶级节点不用默认展开】
      }
      res.push(data[k])
    }
  }
  result.dataList = res
  result.resKeys = resKeys
  return result
}
/**
 * 清除登录信息
 */
export function clearLoginInfo () {
  Vue.cookie.delete('token')
  store.commit('resetStore')
  router.options.isAddDynamicMenuRoutes = false
}


// {a: 1, b: {c:1}} => { 'a':1, 'b.c': 1 }
export function getParamFromSearchForm4Exp (searchForm, fatherName = '') {
	if (!isPlainObject(searchForm)) { return {} }
	var res = {}
	for(var key in searchForm) {
		const item = searchForm[key]
		const actulKey = fatherName? (fatherName + '.' + key): key
		if (!isPlainObject(item)) {
			// 非对象A
			res[actulKey] = item
		} else {
			// 对象
			res = { ...res, ...getParamFromSearchForm4Exp(item, actulKey) }
		}
	}
	return res
}

export function getParamString4Exp (searchForm) {
	var object = getParamFromSearchForm4Exp(searchForm)
	if (!isPlainObject(object)) { return "" }
	var res = " "
	for(var key in object) {
		const value = object[key]
		var field = key
		var op = "="
		for(var condition in operatorMap) {
			if (field.endsWith(condition)) {
				field = field.substring(0, field.length - condition.length);
				if (field.indexOf(".") <= -1){
					field = snakeCase(field)
					field = "a." + field
				} else {
					field = field.substring(0,field.lastIndexOf(".") + 1) + snakeCase(field.substring(field.lastIndexOf(".")))
				}
				op = operatorMap[condition]
			}
		}
		if (op == 'is' || op == 'is not'){
			var sql = " AND "
			sql = sql + field + " "
			sql = sql + op + " null"
			res += sql
		} else if (value) {
			var sql = " AND "
			sql = sql + field + " "
			sql = sql + op + " "
			if (op == 'like' || op == 'not like'){
				sql = sql + "'%" + value + "%'"
			} else if (op == 'in' || op == 'not in'){
				sql = sql + "('" + value.replace(",","','") + "')"
			} else {
				sql = sql + "'" + value + "'"
			}
			res += sql
		}
	}
	return res
}

/**
 * 表单对象赋值:
 * 对目标对象存在且源对象同样存在的属性，全部覆盖；
 * 目标对象不存在但是源对象存在的属性， 全部丢弃；
 * 目标对象存在但是源对象不存在的属性，如果是字符串赋值为空串，其余类型赋值为undefined
 */
export function recover (target, source) {
  if (target === undefined || target === null) { throw new TypeError('Cannot convert first argument to object') }
  var to = Object(target)
  if (source === undefined || source === null) { return to }
  var keysArray = Object.keys(Object(target))
  for (var nextIndex = 0, len = keysArray.length; nextIndex < len; nextIndex++) {
    var nextKey = keysArray[nextIndex]
    var desc = Object.getOwnPropertyDescriptor(target, nextKey)
    if (desc !== undefined && desc.enumerable) {
      if (to.hasOwnProperty(nextKey)) {
        if (to[nextKey] instanceof Array) {
          to[nextKey] = source[nextKey]
        } else if (to[nextKey] instanceof Object) {
          recover(to[nextKey], source[nextKey])
        } else if (source[nextKey] !== undefined) {
          to[nextKey] = source[nextKey]
        } else if (typeof (to[nextKey]) === 'string') {
          to[nextKey] = ''
        } else {
          to[nextKey] = undefined
        }
      }
    }
  }
  return to
}

/**
 * 表单对象赋值:
 * 对目标对象存在且源对象同样存在的属性，全部覆盖；
 * 目标对象不存在但是源对象存在的属性， 全部丢弃；
 * 目标对象存在但是源对象不存在的属性，保留目标对象的属性不做处理
 */
export function recoverNotNull (target, source) {
  if (target === undefined || target === null) { throw new TypeError('Cannot convert first argument to object') }
  var to = Object(target)
  if (source === undefined || source === null) { return to }
  var keysArray = Object.keys(Object(target))
  for (var nextIndex = 0, len = keysArray.length; nextIndex < len; nextIndex++) {
    var nextKey = keysArray[nextIndex]
    var desc = Object.getOwnPropertyDescriptor(target, nextKey)
    if (desc !== undefined && desc.enumerable) {
      if (to.hasOwnProperty(nextKey)) {
        if (to[nextKey] instanceof Array) {
          to[nextKey] = source[nextKey]
        } else if (to[nextKey] instanceof Object) {
          recover(to[nextKey], source[nextKey])
        } else if (source[nextKey] !== undefined) {
          to[nextKey] = source[nextKey]
        }
      }
    }
  }
  return to
}
export function download (url, params) {
  $http({
    method: 'GET',
    url: url,
    params: params,
    responseType: 'blob'
  }).then(response => {
    if (!response) {
      return
    }
    let link = document.createElement('a')
    link.href = window.URL.createObjectURL(new Blob([response.data]))
    link.target = '_blank'
    let filename = response.headers['content-disposition']
    link.download = decodeURI(filename)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  // eslint-disable-next-line handle-callback-err

  }).catch((error) => {

  })
}

export function downloadWithLoading (_this, url, params) {
  _this.loading = true
  $http({
    method: 'GET',
    url: url,
    params: params,
    responseType: 'blob'
  }).then(response => {
    if (!response) {
      return
    }
    let link = document.createElement('a')
    link.href = window.URL.createObjectURL(new Blob([response.data]))
    link.target = '_blank'
    let filename = response.headers['content-disposition']
    link.download = decodeURI(filename)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    // eslint-disable-next-line handle-callback-err
    _this.loading = false
  }).catch((error) => {
    _this.loading = false
  })
}

export function syncDownloadPost (url, params,pThis) {
  var ssoToken=Vue.cookie.get(process.env.VUE_APP_SSO_TYPE+'_token')
  if(ssoToken ==null){
    ssoToken=Vue.cookie.get("token")
    var tokenType=""
  }
  else{
    var tokenType=process.env.VUE_APP_SSO_TYPE
  }
  var tmp=dictUtils.getDictValue("sit_test","testToken","")
  if(tmp !=""){
    ssoToken = tmp
    tokenType="bowker_baseportal"
  }
  var param ={"exportConfig":{"configKey":url},param:JSON.stringify(params)}
  pThis.loading=true
  $http({
    method: 'POST',
    url:"https://commontools.bowkerasia.com/zhimitool/ie/exportConfig/syncExport",
    withCredentials:false,
    headers:{'Content-Type': 'application/json; charset=utf-8',
    "token":ssoToken,
    "tokenType":tokenType,
	"language":localStorage.getItem('lang')||'en-US'
    },
    data:param
  }).then(response => {
    pThis.loading=false
    if (response.status != 200 && !response.data.success) {
      debugger
    }
    else{
       window.open(response.data.taskData.resultFile)
    }
  }).catch((error) => {
    pThis.loading=false
    console.log("网络异常："+error)
  })
}

export function asyncDownloadPost (url, params) {
  var ssoToken=Vue.cookie.get(process.env.VUE_APP_SSO_TYPE+'_token')
  if(ssoToken ==null){
    ssoToken=Vue.cookie.get("token")
    var tokenType=""
  }
  else{
    var tokenType=process.env.VUE_APP_SSO_TYPE
  }
  var commonToolsApi ="https://commontools.bowkerasia.com/zhimitool/ie/taskQueue/push"
  $http({
    method: 'POST',
    url:commonToolsApi+'/export',
    withCredentials:false,
    headers:{'Content-Type': 'application/json; charset=utf-8',
    "token":ssoToken,
    "tokenType":tokenType
    },
    data: {sqlParam:JSON.stringify(params),configKey:url}
  }).then(response => {
    if (response.status != 200 && !response.data.success) {
      debugger
    }
    else{
      var dynamicMenuRoutes = JSON.parse(sessionStorage.getItem('dynamicMenuRoutes') || '[]')
      const route =dynamicMenuRoutes.filter(item => item.path === '/https://commontools.bowkerasia.com/commontoolsH5/')
      if (route.length >= 1) {
        router.push({path: "/"+route[0].fullPath})
      }
    }

  }).catch((error) => {

  })
}

export function downloadPost (url, params) {
  $http({
    method: 'POST',
    url: url,
    data: params,
    responseType: 'blob'
  }).then(response => {
    if (!response) {
      return
    }
    let link = document.createElement('a')
    link.href = window.URL.createObjectURL(new Blob([response.data]))
    link.target = '_blank'
    let filename = response.headers['content-disposition']
    link.download = decodeURI(filename)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  // eslint-disable-next-line handle-callback-err

  }).catch((error) => {

  })
}

export function sendEmail (procInstId, pThis) {
  var ssoToken = Vue.cookie.get(process.env.VUE_APP_SSO_TYPE + "_token")
  var tokenType;
  if(ssoToken == null){
    ssoToken = Vue.cookie.get("token")
    tokenType = "bowker_baseportal"
  } else {
    tokenType = process.env.VUE_APP_SSO_TYPE
  }
  //测试用token
  var tmp=dictUtils.getDictValue("sit_test","testToken","")
  if(tmp != ""){
    ssoToken = tmp
    tokenType = "bowker_baseportal"
  }
  // var param ={"exportConfig":{"configKey":url},param:JSON.stringify(params)}
  var param = {
    id: "386b6a0024784ba8b7d5d4dad60e9a65",
    sendTo: '',
    param: JSON.stringify({"procInstId":procInstId})
  }
  pThis.loading=true
debugger
  $http({
    method: "POST",
    url: "https://commontools.bowkerasia.com/zhimitool/msg/msgPushConfig/sendImmediately",
    withCredentials: false,
    headers: {
      "Content-Type": "application/json; charset=utf-8",
      "token": ssoToken,
      "tokenType": tokenType
    },
    data: param
  }).then(response => {
    pThis.loading = false
    if (response.status != 200 && !response.data.success) {
      debugger
    }
  }).catch((error) => {
    pThis.loading=false
    console.log("网络异常：" + error)
  })
}

// 占锐 新增根据链接地址下载文件
export function downloadZhanrui (url, fileName) {
  let link = document.createElement('a')
  link.href = url
  link.target = '_blank'
  let filename = fileName
  link.download = decodeURI(filename)
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

export function escapeHTML (a) {
  a = '' + a
  return a.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&apos;')
}
/**
* @function unescapeHTML 还原html脚本 < > & " '
* @param a -
*            字符串
*/
export function unescapeHTML (a) {
  a = '' + a
  return a.replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&amp;/g, '&').replace(/&quot;/g, '"').replace(/&apos;/g, "'")
}

export function printLogo () {
  console.info(
    '%c欢迎使用%cFlowable',
    'color: #ffffff; background: #000000; padding:5px 10px 5px 10px;font-size:40px;border-radius:12px 0 0 12px;', 'color: #000000; background: #FE9A00; padding:5px 10px;font-size:40px;border-radius:0 12px 12px 0;')
}

/**
 * 对象深拷贝
 */
export function deepClone (data) {
  var type = getObjType(data)
  var obj
  if (type === 'array') {
    obj = []
  } else if (type === 'object') {
    obj = {}
  } else {
    // 不再具有下一层次
    return data
  }
  if (type === 'array') {
    for (var i = 0, len = data.length; i < len; i++) {
      data[i] = (function () {
        if (data[i] === 0) {
          return data[i]
        }
        return data[i]
      }())
      delete data[i].$parent
      obj.push(deepClone(data[i]))
    }
  } else if (type === 'object') {
    for (var key in data) {
      delete data.$parent
      obj[key] = deepClone(data[key])
    }
  }
  return obj
};

export function getObjType (obj) {
  var toString = Object.prototype.toString
  var map = {
    '[object Boolean]': 'boolean',
    '[object Number]': 'number',
    '[object String]': 'string',
    '[object Function]': 'function',
    '[object Array]': 'array',
    '[object Date]': 'date',
    '[object RegExp]': 'regExp',
    '[object Undefined]': 'undefined',
    '[object Null]': 'null',
    '[object Object]': 'object'
  }
  if (obj instanceof Element) {
    return 'element'
  }
  return map[toString.call(obj)]
};
export function validatenull (val) {
  // 特殊判断
  if (val && parseInt(val) === 0) return false
  var list = ['$parent']
  if (typeof val === 'boolean') {
    return false
  }
  if (typeof val === 'number') {
    return false
  }
  if (val instanceof Array) {
    if (val.length === 0) return true
  } else if (val instanceof Object) {
    val = (0, deepClone)(val)
    list.forEach(function (ele) {
      delete val[ele]
    })
    if (JSON.stringify(val) === '{}') return true
  } else {
    if (val === 'null' || val == null || val === 'undefined' || val === undefined || val === '') {
      return true
    }
    return false
  }
  return false
}
function handleImageAdded (file, Editor, cursorLocation, resetUploader) {
  // An example of using FormData
  // NOTE: Your key could be different such as:
  // formData.append('file', file)
  var formData = new FormData()
  formData.append('file', file)

  $http({
    url: '/sys/file/webupload/upload?uploadPath=/vueEditor',
    method: 'POST',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
    .then(result => {
      let url = result.data.url // Get url from response
      Editor.insertEmbed(cursorLocation, 'image', url)
      resetUploader()
    })
    .catch(err => {
      console.log(err)
    })
}
function hashCode (str) {
  var hash = 0
  if (str.length === 0) return hash
  for (let i = 0; i < str.length; i++) {
    let char = str.charCodeAt(i)
    hash = ((hash << 5) - hash) + char
    hash = hash & hash // Convert to 32bit integer
  }
  return hash
}
export default {sendEmail, asyncDownloadPost, syncDownloadPost, getParamString4Exp, escapeHTML, hashCode, unescapeHTML, handleImageAdded, download, downloadWithLoading, downloadPost, downloadZhanrui, recover, recoverNotNull, hasPermission, treeDataTranslate, treeDataTranslateWithLevel, printLogo, deepClone, validatenull}
