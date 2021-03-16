<template>
	<div style="text-align: center;padding-top: 80px;">
		<el-link icon="el-icon-back" style="font-size: 18px;" @click="back" v-show="loginOutUrl">{{$i18nMy.t('返回登录')}}</el-link>
	</div>
</template>

<script>
  export default {
    data () {
      return {
        time: '',
        logo: '',
        loading: false,
        loginOutUrl:'',
        captchaPath: ''
      }
    },
    created () {
    	debugger
		this.loading = true;
		var url = '/sys/loginbowker'
		var token = this.$route.query.token;
		if(token){
			url += "?token=" + token;
		}
		this.$http({
			url: url,
			method: 'post'
		}).then(({data}) => {
			debugger
			if (data && data.success) {
				this.$cookie.set('token', data.token)
				this.$cookie.set('refreshToken', data.refreshToken)
				this.$router.push({name: 'home'})
			} else {
				this.loading = false
				if (data && data.loginOutUrl){
					this.loginOutUrl = data.loginOutUrl
				}
				/*
				if (data && data.msg){
					this.$message.error(data.msg)
				}
				if (data && data.loginOutUrl){
					setTimeout(function(){
						window.location.href = data.loginOutUrl
					}, 1500);
				}
				*/
			}
		})
    },
    mounted () {
      
    },
    methods: {
      getTime () {
        this.time = this.moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
      },
      
      back (){
      	if (this.loginOutUrl){
      		window.location.href = this.loginOutUrl
      	}
      }
    }
  }
</script>

<style lang="scss">
@import '~@/assets/scss/login.scss';
</style>
