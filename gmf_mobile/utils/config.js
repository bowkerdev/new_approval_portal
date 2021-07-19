export default {
	setGlobalDataDefault:function(app){
		// 小程序运行时isDebugger=false;  H5调试界面时isDebugger=true, 这样避免拿不到微信的东西时不会报错
		app.globalData.isDebugger=true ;
		//app.globalData.apiUrl='http://111.59.93.26:16004/digital/';
		//app.globalData.domainUrl='http://111.59.93.26:16004';
		
		//app.globalData.apiUrl='http://127.0.0.1:8082/digital/';
		//app.globalData.domainUrl='http://127.0.0.1:8082';
		
		app.globalData.apiUrl='/digital/';
		app.globalData.domainUrl='';
		app.globalData.dictTypeList='';
		app.$http.defaults.baseURL=app.globalData.apiUrl;
	}
}
