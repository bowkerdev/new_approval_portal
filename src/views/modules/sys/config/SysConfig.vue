<template>
<div class="el-scrollbar__wrap overflow-auto wrap-white padding-20">
  <div class="el-scrollbar__view">
  <el-tabs v-model="activeName" >
    <el-tab-pane :label="$i18nMy.t('登录')" name="first">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <h3>{{$i18nMy.t('单一登陆配置')}}</h3>
        </div>
        <el-form size="small" ref="loginFormSetting" :model="loginFormSetting">
          <el-form-item>
             <el-checkbox true-label="1" false-label="0" v-model="loginFormSetting.multiAccountLogin">{{$i18nMy.t('单一登录')}}</el-checkbox>
          </el-form-item>
          <el-form-item>
            <el-radio-group v-model="loginFormSetting.singleLoginType">
              <el-radio :disabled="loginFormSetting.multiAccountLogin ==='0'" label="1">{{$i18nMy.t('后登陆踢出先登录')}}</el-radio>
              <el-radio :disabled="loginFormSetting.multiAccountLogin ==='0'" label="2">{{$i18nMy.t('已登陆禁止再登陆')}}</el-radio>
            </el-radio-group>
          </el-form-item>
           <el-form-item>
            <el-button type="primary" @click="doSubmit(loginFormSetting)">{{$i18nMy.t('保存')}}</el-button>
          </el-form-item>
        </el-form>


      </el-card>
    </el-tab-pane>
    <el-tab-pane :label="$i18nMy.t('外观')" name="second">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <h3>{{$i18nMy.t('外观配置')}}</h3>
        </div>
        <el-form label-width="150px" :model="themeFormSetting">
          <el-form-item :label="$i18nMy.t('产品标题')">
            <el-input v-model="themeFormSetting.productName"></el-input>
          </el-form-item>
          <el-form-item label="产品logo">
            <el-upload
              class="avatar-uploader"
              :action="`${this.$http.BASE_URL}/sys/file/webupload/upload?uploadPath=logo`"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeAvatarUpload"
               :show-file-list="false">
              <img v-if="themeFormSetting.logo" :src="themeFormSetting.logo" class="avatar">
              <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            </el-upload>
          </el-form-item>
          <el-form-item :label="$i18nMy.t('默认布局')">
              <el-select v-model="themeFormSetting.defaultLayout" :placeholder="$i18nMy.t('请选择')"  style="width: 100%;">
                <el-option
                  v-for="item in layouts"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
            </el-select>
          </el-form-item>
          <el-form-item :label="$i18nMy.t('默认主题')">
            <div class="tag-group" style="margin-top:7px">
              <el-tooltip effect="dark" :content="item.key" placement="top-start" v-for="(item, index) in colorList" :key="index">
                <el-tag :color="item.color" class="themeColorTag" @click="themeFormSetting.defaultTheme = item.color">
                  <i class="el-icon-check themeColorFont" v-if="item.color === themeFormSetting.defaultTheme"></i>
                </el-tag>
              </el-tooltip>
            </div>
          </el-form-item>
          <el-form-item :label="$i18nMy.t('首页地址')">
              <el-input v-model="themeFormSetting.homeUrl"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="doSubmit(themeFormSetting)">{{$i18nMy.t('保存')}}</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-tab-pane>
    <el-tab-pane :label="$i18nMy.t('短信')" name="third">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <h3>阿里大鱼短信配置(<a href="https://dayu.aliyun.com" target="_blank">{{$i18nMy.t('官网')}}</a>)</h3>
        </div>
        <el-form label-width="150px" :model="smsFormSetting">
          <el-form-item label="accessKeyId" :rules="[
                  {required: true, message:'必填项不能为空', trigger:'blur'}
                 ]">
            <el-input v-model="smsFormSetting.accessKeyId" placeholder="此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)"></el-input>
          </el-form-item>
          <el-form-item label="accessKeySecret" :rules="[
                  {required: true, message:'必填项不能为空', trigger:'blur'}
                 ]">
            <el-input v-model="smsFormSetting.accessKeySecret" placeholder="此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)"></el-input>
          </el-form-item>
          <el-form-item :label="$i18nMy.t('短信签名')" :rules="[
                  {required: true, message:'必填项不能为空', trigger:'blur'}
                 ]">
            <el-input v-model="smsFormSetting.signature" placeholder="必填:短信签名-可在短信控制台中找到"></el-input>
          </el-form-item>
          <el-form-item :label="$i18nMy.t('短信模板')" :rules="[
                  {required: true, message:'必填项不能为空', trigger:'blur'}
                 ]">
            <el-input v-model="smsFormSetting.templateCode" placeholder="必填:短信模板编号-可在短信控制台中找到"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="doSubmit(smsFormSetting)">{{$i18nMy.t('保存')}}</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </el-tab-pane>
    <el-tab-pane :label="$i18nMy.t('邮箱')" name="fourth">
      <el-card class="box-card">
        <div slot="header" class="clearfix">
          <h3>{{$i18nMy.t('发件人账户')}}</h3>
        </div>
         <el-form label-width="150px" :model="emailFormSetting">
          <el-form-item :label="$i18nMy.t('邮箱服务器地址')" :rules="[
                  {required: true, message:'必填项不能为空', trigger:'blur'}
                 ]">
            <el-input v-model="emailFormSetting.smtp" placeholder="此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)"></el-input>
          </el-form-item>
          <el-form-item :label="$i18nMy.t('邮箱服务器端口')" :rules="[
                  {required: true, message:'必填项不能为空', trigger:'blur'}
                 ]">
            <el-input v-model="emailFormSetting.port" placeholder="此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)"></el-input>
          </el-form-item>
          <el-form-item :label="$i18nMy.t('系统邮箱地址')" :rules="[
                  {required: true, message:'必填项不能为空', trigger:'blur'}
                 ]">
            <el-input v-model="emailFormSetting.mailName" placeholder="必填:短信签名-可在短信控制台中找到"></el-input>
          </el-form-item>
          <el-form-item :label="$i18nMy.t('系统邮箱密码')" :rules="[
                  {required: true, message:'必填项不能为空', trigger:'blur'}
                 ]">
            <el-input v-model="emailFormSetting.mailPassword" placeholder="必填:短信模板编号-可在短信控制台中找到"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="doSubmit(emailFormSetting)">{{$i18nMy.t('保存')}}</el-button>
          </el-form-item>
        </el-form>

      </el-card>
    </el-tab-pane>
  </el-tabs>
  </div>
</div>
</template>
<script>
  import theme_color_list from '@/utils/theme-color'
  export default {
    name: 'sys-config-SysConfig',
    data () {
      return {
        activeName: 'first',
        loginFormSetting: {
          multiAccountLogin: '0',
          singleLoginType: '1'
        },
        themeFormSetting: {
          defaultTheme: '#1890FF',
          productName: '',
          logo: '',
          defaultLayout: '',
          homeUrl: ''
        },
        smsFormSetting: {
          accessKeyId: '',
          accessKeySecret: '',
          signature: '',
          templateCode: ''
        },
        emailFormSetting: {
          smtp: '',
          port: '',
          mailName: '',
          mailPassword: ''
        },
        colorList: theme_color_list,
        layouts: [
          {label: '横向菜单', value: 'top'},
          {label: '左侧菜单', value: 'left'}
        ]
      }
    },
    activated () {
      this.$http.get('/sys/sysConfig/queryById').then(({data}) => {
        this.loginFormSetting = this.recover(this.loginFormSetting, data.config)
        this.themeFormSetting = this.recover(this.themeFormSetting, data.config)
        this.smsFormSetting = this.recover(this.smsFormSetting, data.config)
        this.emailFormSetting = this.recover(this.emailFormSetting, data.config)
      })
    },
    methods: {
      handleAvatarSuccess (res, file) {
        if(res.success){
          this.themeFormSetting.logo = res.url
        }
        else{
           this.$message.error(res.msg)
        }
      },

      beforeAvatarUpload (file) {
        const isJPG = file.type.indexOf('image/') >= 0
        const isLt2M = file.size / 1024 / 1024 < 2

        if (!isJPG) {
          this.$message.error('上传LOGO只能是图片格式!')
          return false
        }
        if (!isLt2M) {
          this.$message.error('上传LOGO大小不能超过 2MB!')
          return false
        }
        return true
      },
      doSubmit (form) {
        this.$http.post('/sys/sysConfig/save', form).then(({data}) => {
          if (data.success) {
            this.$message.success(data.msg)
            localStorage.setItem('defaultLayout', data.config.defaultLayout)
            localStorage.setItem('defaultTheme', data.config.defaultTheme)
            this.$events.$emit('updateTheme', data.config.defaultTheme)
            this.$store.commit('config/updateConfig', data.config)
          }
        })
      }
    }
  }
</script>
<style>
.themeColorTag{
  width:25px !important;
  height:25px !important;
}
 .themeColorTag.el-tag + .el-tag {
    margin-left: 5px;
    margin-bottom: 0px;
}
.themeColorFont{
  position: absolute;
  color: #fff;
  margin-top: 3px;
  margin-left: -6px;
  font-weight: bold;
  font-size: 16px;
}
</style>