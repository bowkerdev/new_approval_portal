<template>

  <div class="login-container">
       <vue-particles
        color="#fff"
        :particleOpacity="0.7"
        :particlesNumber="60"
        shapeType="circle"
        :particleSize="4"
        linesColor="#fff"
        :linesWidth="1"
        :lineLinked="true"
        :lineOpacity="0.4"
        :linesDistance="150"
        :moveSpeed="2"
        :hoverEffect="true"
        hoverMode="grab"
        :clickEffect="true"
        clickMode="push"
        class="lizi"
      >
      </vue-particles>
    <div class="login-weaper animated bounceInDown" style="z-index:100">
      <div class="login-left">

        <div class="login-time">
          {{time}}
        </div>
        <img class="round"
             :src="logo"
             alt="" height="160px" width="160px" >
        <p class="title">{{productName}}</p>
      </div>
      <div class="login-border">
        <div class="login-main">
          <h4 class="login-title">
           用户登录
          </h4>
            <el-form ref="inputForm" :model="inputForm" :rules="rules" label-width="80px" @keyup.enter.native="login()" @submit.native.prevent>
              <el-form-item label="公司名称" prop="company">
                <el-input type="text" placeholder="请输入账号" v-model="inputForm.company"/>
              </el-form-item>
              <el-form-item label="账号" prop="username">
                <el-input type="text" placeholder="请输入账号" v-model="inputForm.username"/>
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input type="password" placeholder="请输入密码" v-model="inputForm.password"/>
              </el-form-item>
              <el-form-item label="验证码" prop="captcha">
                <el-input type="captcha" placeholder="验证码" style="width: 50%;" v-model="inputForm.captcha"/>
                <img :src="captchaPath" @click="refreshCaptcha" style="margin-left: 10px;margin-right: 10px;" />
                <a href="javascript:"  @click="refreshCaptcha" >看不清</a>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" :loading="loading" class="login-submit" @click="login">登录</el-button>
              </el-form-item>
            </el-form>
            <div style="padding-left: 110px;">
              <span style="color: black; font-size: 14px;">企业用户可在此注册, </span>
              <el-link type="primary" @click="register">点击注册</el-link>
            </div>
            <div style="padding-left: 110px;">
              <el-link type="primary" @click="toLoginControl">控制台登录</el-link>
            </div>
        </div>

      </div>
    </div>

    <!-- 弹窗, 注册企业 -->
    <SysTenantRegistForm  ref="sysTenantRegistForm"  ></SysTenantRegistForm>
  </div>
</template>

<script>
  import SysTenantRegistForm from '@/views/modules/sys/tenant/SysTenantRegistForm'

  export default {
    components: {
      SysTenantRegistForm
    },
    data () {
      return {
        time: '',
        logo: '',
        productName: '',
        loading: false,
        inputForm: {
          username: 'admin',
          password: 'admin',
          company:'LJ-test',
          uuid: '',
          captcha: ''
        },
        rules: {
          company: [
            {required: true, message: '公司名称不能为空', trigger: 'blur'}
          ],
          username: [
            {required: true, message: '帐号不能为空', trigger: 'blur'}
          ],
          password: [
            {required: true, message: '密码不能为空', trigger: 'blur'}
          ],
          captcha: [
            {required: true, message: '验证码不能为空', trigger: 'blur'}
          ]
        },
        captchaPath: '/gmfmes/servlet/validateCodeServlet?' + new Date().getTime()
      }
    },
    created () {
      this.getTime()
      setInterval(() => {
        this.getTime()
      }, 1000)
    },
    mounted () {
      this.$http.get('/sys/sysConfig/queryById').then(({data}) => {
        this.logo = data.config.logo
        this.productName = data.config.productName
      })
    },
    methods: {
      refreshCaptcha(){
        this.captchaPath='/gmfmes/servlet/validateCodeServlet?' + new Date().getTime();
      },
      // 提交表单
      login () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: "/sys/tenant/sysTenant/login",
              method: 'post',
              data: {
                'companyName' : this.inputForm.company,
                'userName': this.inputForm.username,
                'password': this.inputForm.password,
                'captcha':this.inputForm.captcha
              }
            }).then(({data}) => {
              debugger
              this.loading = false;
              if (data && data.success) {
                var loginInfo=JSON.parse(data.loginInfo);
                if(loginInfo.code==200){
                  var url = data.url+"portal/#/home?token="+loginInfo.token+"&refreshToken="+loginInfo.refreshToken
                  window.location.href = url;
                }
                else{
                  this.$message.error(loginInfo.msg);
                }
              } else {
                this.$message.error(data.msg);
              }
            })
          }
        })
      },
      getTime () {
        this.time = this.moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
      },
      // 获取验证码
      getCaptcha () {// TODO
        this.captchaPath = this.$http.BASE_URL + `/captcha.jpg`
      },
      // 注册账户
      register () {
        // this.$message.warning('暂不支持！')
        this.$refs.sysTenantRegistForm.init('add', '')
      },
      // LoginSaas
      toLoginControl () {
        this.$router.push({name: 'login'})
      }
    }
  }
</script>

<style lang="scss">
@import '~@/assets/scss/login.scss';

</style>
