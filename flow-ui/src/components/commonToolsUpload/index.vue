<template>
<div>
    <el-upload
      class="upload-demo"
      :action="`${this.commonToolsApi}/import`"
      :headers="{token:token, tokenType:tokenType,language:language}"
      :on-success="uploadSuccess"
      :on-error="handleError"
      :data="uploadData"
      :file-list="templateArra"
      :show-file-list="true">
      <el-button type="primary">{{$i18nMy.t('点击上传')}}</el-button>
    </el-upload>
</div>
</template>
<script>
export default {
  data () {
    return {
       uploadData:{"importConfig.configKey":this.configKey,param:this.param},
       token:"",
       templateArra:[],
       tokenType:"",
       language:localStorage.getItem('lang'),
       commonToolsApi :"https://commontools.bowkerasia.com/zhimitool/ie/taskQueue/push"
    }
  },
  props: {
    configKey: String,
    param: {
      type: String,
      default: () => { return JSON.stringify({test:1,test2:2}) }
    }
  },
  components: {

  },
  created () {
    this.token = this.$cookie.get(process.env.VUE_APP_SSO_TYPE+'_token')
    this.tokenType = process.env.VUE_APP_SSO_TYPE;
  },
  methods: {
    setParam(param){
      this.uploadData.param=param
    },
    uploadSuccess(res, file){
      this.templateArra=[]
      if (res.success) {
        this.$message.success({dangerouslyUseHTMLString: true, message: res.msg})
        var dynamicMenuRoutes = JSON.parse(sessionStorage.getItem('dynamicMenuRoutes') || '[]')
        const route =dynamicMenuRoutes.filter(item => item.path === '/https://commontools.bowkerasia.com/commontoolsH5/')
        if (route.length >= 1) {
          this.$router.push({path:"/"+route[0].fullPath})
        }
      } else {
        this.$message.error(res.msg)
      }
    },
    handleError(err, file, fileList) {
      this.$message.error("文件上传失败")
    }
  }
}
</script>
<style>

</style>
