import uniRequest from 'uni-request';
import stringUtil from '@/utils/StringUtil'
import alertMessage from '@/utils/alertMessage.js'
const shiroApi=['sys/dict/getAll','dd/authority/useInfo'];

uniRequest.defaults.withCredentials=true;//让请求头携带参数cookie
uniRequest.defaults.baseURL = "";      //配置接口地址  
//uniRequest.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8';
uniRequest.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';

/**
 * 请求接口日志记录
 */
function _reqlog(req) {
    if (process.env.NODE_ENV === 'development') {
        console.log("请求地址：" + (req.url||""), req.data || req.params)
    }
    //TODO 调接口异步写入日志数据库
}

/**
 * 响应接口日志记录
 */
function _reslog(res) {
    if (process.env.NODE_ENV === 'development') {
		if(res!=null&&res.config!=null&&res.config.url!=null){
			console.log(`${res.config.url}响应结果：`, res.data)
		}
    }
}


// 拦截器 在请求之前拦截
uniRequest.interceptors.request.use(config => {
	function checkUrl(c){
		if(config.url.indexOf("http")==0){
			return false;
		}
		for(var i=0;i<shiroApi.length;i++){
			if(shiroApi[i][0]=="/"){
				shiroApi[i]=shiroApi[i].substr(1)
			}
			if(c.url[0]=="/"){
				c.url=c.url.substr(1)
			}
			if(c.url.indexOf(shiroApi[i])>=0){
				return false;
			}
		}
		return true;
	}
	if(checkUrl(config)){
		var app=getApp().$vm;
		//  #ifdef  H5
			app = getApp()
		// #endif 
		if(!app.$token.checkToken()||getApp().globalData.token.accessToken==""){
			config.refreshToken=true
		}
		else{
			if(!app.$token.setToken(config)){
				return  Promise.reject({msg:"token 失效"});
			}
		}
	}
	// #ifdef  MP-ALIPAY
	config.url=encodeURI(config.url)
	// #endif 
    _reqlog(config)
    return config
}, 
error => {
	console.log(error);
	return  Promise.reject();
})

// 拦截器 在请求之后拦截
uniRequest.interceptors.response.use(response => {
    _reslog(response);
	if(response.status=='401' || response.data.code=="401"){
		console.log(JSON.stringify(response));
		uni.setStorageSync("token",'');
		return  Promise.reject("token 过期");
	}
    return response
}, error => {
	console.log(error);
	if(error.data!=null&&error.data.code==401){
		uni.setStorageSync("token",'');
	}
	return  Promise.reject(error);
})

uniRequest.getHttpData=function(data){
	if(data.status==200){
		if(data.data.success){
			return data.data
		}
		else{
			alertMessage.toastDefault(data.data.msg);
		}
	}
	else{
		alertMessage.sysError(data.status);
	}
	return null; 
}

export default {uniRequest}