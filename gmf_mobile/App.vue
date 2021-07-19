<script>
import config from 'utils/config';
export default {
	globalData: {
		alInfo:{corpId:'dingd99058166d685147f5bf40eda33b7ba0'}, //   ding78a79362bba4adf135c2f4657eb6378f
		token:{expireTime:0,getTime:0,toke:null,refreshToken:null},
		userInfo:null,
		ddUserInfo:null
	},
	onLaunch: function() {
		console.log('App Launch');
		config.setGlobalDataDefault(this);
		this.info();
	},
	onShow: function() {
		console.log('App Show');
	},
	onHide: function() {
		console.log('App Hide');
	},
	methods:{
		info(){
			uni.setStorageSync("token",'')
			if(this.$token.checkToken()){
				var token=uni.getStorageSync("token")
				this.globalData.token=JSON.parse(token)
				var userInfo=uni.getStorageSync("userInfo")
				this.globalData.userInfo=JSON.parse(userInfo)
				var ddUserInfo=uni.getStorageSync("ddUserInfo")
				this.globalData.ddUserInfo=JSON.parse(ddUserInfo)
				console.log(JSON.stringify(this.globalData))
			}
			else{
				// this.$token.getAlUserInfo(null,this);
			}
		}
	}
};
</script>

<style>
/*每个页面公共css */ 
@import "colorui/main.css";
@import "colorui/icon.css";
@import "common/common.css";
</style>
