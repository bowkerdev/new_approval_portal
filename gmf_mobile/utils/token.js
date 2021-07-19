export default {
	checkToken(){
		var token=uni.getStorageSync("token");
		if(token!=null&&token!=''){
			token=JSON.parse(token);
			var expiresInTime=token.getTime+parseInt(token.expireTime)-60000;// 提取60秒失效
			if(expiresInTime >new Date().getTime()){
				return true;
			}
		}
		return false;
	},
	getToken(app){
		app =app==null?getApp().$vm:app;
		//  #ifdef  H5
			app =app==null?getApp():app;
		// #endif 
		app.$token.getAlUserInfo(null,app)
	},
	waitToke(callBack){
		if(getApp().globalData.token.token == null){
			var pThis=this
			setTimeout(function(){pThis.waitToke(callBack)},1000);
		}
		else{
			callBack()
		}
	},
	setToken(config){
		config.headers.token = getApp().globalData.token.token;
		if(config.headers.token =="token_timeout"){
			return false;
		}
		else{
			return true;
		}
	},
	getAlUserInfoByCode(app,code,agentId,corpId){
		app.globalData.token={expireTime:0,getTime:0,toke:null,refreshToken:null}
		app.$http.get("dd/authority/useInfo?code="+code+"&agentId="+agentId +"&corpId="+(corpId||"")).then(function(res) {
			var data=app.$http.getHttpData(res);
			if(data!=null){
				app.globalData.token.getTime=new Date().getTime()
				app.globalData.token.token=data.token				
				app.globalData.token.refreshToken=data.refreshToken
				app.globalData.token.expireTime=data.expireTime
				uni.setStorageSync("token",JSON.stringify(app.globalData.token))
				uni.setStorageSync("userInfo",JSON.stringify(app.globalData.userInfo))
				uni.setStorageSync("ddUserInfo",JSON.stringify(app.globalData.ddUserInfo))
				app.globalData.userInfo=data.userInfo
				app.globalData.ddUserInfo=data.ddUserInfo
				console.log(JSON.stringify(app.globalData))
			}
		}).catch(function(err) {
			debugger
			// TODO
			console.log(err);
		});
	},
	getAlUserInfo(callBack,app){
		//  #ifdef  H5
		if(app.globalData.isDebugger){
			var url1=app.globalData.apiUrl
			var url2=app.globalData.domainUrl
			app.globalData={"alInfo":{"corpId":"dingd99058166d685147f5bf40eda33b7ba0"},"token":{"expireTime":86400000000,"getTime":1617707753598,"toke":null,"refreshToken":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE4NzY5MDc3NTMsInVzZXJuYW1lIjoiMTgyOTAwOTI4NjcifQ.k743Y8kT2YfU2etuFUKaahqopj_nyrxIHOiDG2JFvG8","token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MDQxMDc3NTMsInVzZXJuYW1lIjoiMTgyOTAwOTI4NjcifQ.dWCF1D3mv8PKt7N8CMXFY4YEZbxJnAJKSZgzP8YXusM"},"userInfo":{"id":"f7253685946549b98b5240ddaba4f133","createDate":"2021-03-30 10:39:59","updateDate":"2021-03-30 10:39:59","sysUser":{"id":"a9e0b70912874038a91bfbbf8b28395d","loginName":"18290092867","name":"Lewis Liu","admin":false,"roleIdList":[],"roleNames":"","roleIds":""},"userid":"0909040846672569","unionid":"2lrJ07Ra51bC1iSOiSXN6FhgiEiE","mobile":"18290092867"},"ddUserInfo":{"unionid":"2lrJ07Ra51bC1iSOiSXN6FhgiEiE","device_id":"ca250036c7a404562b4f4673df76ce74","sys_level":2,"name":"Lewis Liu","sys":true,"userid":"0909040846672569"},"isDebugger":false,"apiUrl":"http://127.0.0.1:8082/digital/","domainUrl":"http://127.0.0.1:8082","dictTypeList":""}
			app.globalData.apiUrl = url1
			app.globalData.domainUrl = url2
			uni.setStorageSync("token",JSON.stringify(app.globalData.token))
			uni.setStorageSync("userInfo",JSON.stringify(app.globalData.userInfo))
			uni.setStorageSync("ddUserInfo",JSON.stringify(app.globalData.ddUserInfo)) 
			if(callBack!=null){
				callBack()
			} 
			return;
		}
		// #endif 
		//  #ifdef  H5
		if(app.$dd !=null){
			var corpId=app==null?getApp().globalData.alInfo.corpId:app.globalData.alInfo.corpId;
			corpId='ding78a79362bba4adf135c2f4657eb6378f'  // 智米测试
			app.$dd.ready(function() {
				app.$dd.runtime.permission.requestAuthCode({
					corpId: corpId, // 企业id  1146997826
					onSuccess: function (info) {
						//getApp().$token.getAlUserInfoByCode(app,info.code,"1150032044",corpId) // Lewis测试
						getApp().$token.getAlUserInfoByCode(app,info.code,"1160452586",corpId) // Jack测试
						//getApp().$token.getAlUserInfoByCode(app,info.code,"1148986314",corpId)   
						if(callBack!=null){
							callBack()
						}
					},
				});
			});
		}
		// #endif 
		// #ifdef  MP-ALIPAY
			app.$dd.getAuthCode({
				success:function(res){
					getApp().$vm.$token.getAlUserInfoByCode(app,res.authCode,"1148719818") 
					if(callBack!=null){
						callBack()
					}
				},
				fail:function(err){
				}
			});
		// #endif 
	}
}