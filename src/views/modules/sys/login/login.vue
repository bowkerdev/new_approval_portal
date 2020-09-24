<template>

  <div class="login-container">


    <div class="login-weaper animated bounceInDown" style="z-index:100">
      <div class="login-left">
                <div class="login-time">
          {{time}}
        </div>
        <p class="title">{{productName}}</p>

        <el-image src="/static/img/login.png">
    </el-image>


      </div>
      <div class="login-border">
        <div class="login-main">

          <h4 class="login-title">{{$i18nMy.t('登录')}}</h4>
          <h3 class="login-sub-title">{{$i18nMy.t('请使用账号密码登录')}}</h3>
            <el-form ref="inputForm" :model="inputForm" :rules="rules" class="login-form"  @keyup.enter.native="login()" @submit.native.prevent>

              <el-form-item  prop="username">
                <el-input type="text" :placeholder="{{$i18nMy.t('请输入账号')" v-model="inputForm.username">
                   <i slot="prefix" class="el-input__icon el-icon-user-solid"></i>
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input type="password" :placeholder="{{$i18nMy.t('请输入密码')" v-model="inputForm.password">
                   <i slot="prefix" class="el-input__icon el-icon-female"></i>
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="loading" class="login-submit" @click="login">{{$i18nMy.t('登录')}}</el-button>
              </el-form-item>
            </el-form>
        </div>
         <el-image class="right" src="/static/img/right.png"></el-image>


      </div>
    </div>
  </div>
</template>

<script>
  export default {
    data () {
      return {
        time: '',
        logo: '',
        productName: '',
        loading: false,
        inputForm: {
          username: 'admin',
          password: 'admin',
          uuid: '',
          captcha: ''
        },
        rules: {
          username: [
            {required: true, message: $i18nMy.t('帐号不能为空'), trigger: 'blur'}
          ],
          password: [
            {required: true, message: '密码不能为空', trigger: 'blur'}
          ],
          captcha: [
            {required: true, message: '验证码不能为空', trigger: 'blur'}
          ]
        },
        captchaPath: ''
      }
    },
    created () {
      this.getTime()
      setInterval(() => {
        this.getTime()
      }, 1000)
      this.checkLoginPage();
      console.log(window.$i18n.t("请使用账号密码登录"))
    },
    mounted () {
      this.$http.get('/sys/sysConfig/queryById').then(({data}) => {
        // this.logo = data.config.logo
        this.productName = data.config.productName
      })
    },
    methods: {
      checkLoginPage(){
        this.$http({
          url: '/sys/hasAllowLoginkey',
          method: 'get'
        }).then(({data}) => {
          if (data && data.success && data.hasAllowLoginkey &&
          (Vue.cookie.get('token')==null && Vue.cookie.get('token') == '')) {
            window.location.href = data.loginOutUrl
          }
        }).catch((e) => {
          console.log(e.message)
        })
      },
      // 提交表单
      login () {
        this.checkLoginPage();
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: '/sys/login',
              method: 'post',
              data: {
                'userName': this.inputForm.username,
                'password': this.inputForm.password
              }
            }).then(({data}) => {
              if (data && data.success) {
                this.$cookie.set('token', data.token)
                this.$cookie.set('refreshToken', data.refreshToken)
                this.$router.push({name: 'home'})
              } else {
                this.loading = false
                // this.getCaptcha()
              }
            })
          }
        })
      },
      getTime () {
        this.time = this.moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
      },
      // 获取验证码
      getCaptcha () {
        this.captchaPath = this.$http.BASE_URL + `/captcha.jpg`
      }
    }
  }
</script>

<style lang="scss">
@import '~@/assets/scss/login.scss';
</style>
