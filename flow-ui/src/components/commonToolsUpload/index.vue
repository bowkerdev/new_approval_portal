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
       commonToolsApi :"https://commontools.bowkerasia.com/zhimitool/ie/taskQueue/push" ,
       commonToolsProcessUrl :"https://commontools.bowkerasia.com/commontools-ui-bowker/#/ie/UploadDownloadContent"
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
    debugger
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
        debugger
        window.open(this.commonToolsProcessUrl+"?token="+
          this.token+"&tokenType="+this.tokenType+"&isIframe=true" ,"通用平台",null,true)
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
