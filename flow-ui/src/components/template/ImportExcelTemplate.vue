<template>
  <el-dialog
    :title="title"
    :close-on-clidck-modal="false"
    class="orderSet"
    :before-close="handleDialogClose"
    :show-close="false"
    :visible.sync="visible">
    <div class="page upload-template">
      <!--导入入口-->
      <div  class="bg-white top">
        <el-upload
          class="upload-demo"
          :action="url"
          ref="upload"
          drag
          :data = "uploadParam"
          :before-upload="handleBefore"
          :on-success="uploadSuccess"
          :show-file-list="true">
          <i class="el-icon-upload"></i>
          <div class="el-upload__text">{{$i18nMy.t('将文件拖到此处，或')}}<em>{{$i18nMy.t('点击选择文件')}}</em></div>
          <div class="el-upload__text" style="color: red; margin-top: 15px;">{{remarks}}</em></div>
        </el-upload>
        <el-row style="padding-top: 20px">
          <el-col :span="24">
            <el-button type="primary" @click="downloadTpl()" size="small">{{$i18nMy.t('下载模板')}}</el-button>
            <span style="height: 32px;line-height: 32px;padding-left: 10px;">{{$i18nMy.t('只允许导入“xls”或“xlsx”格式文件！')}}</span>
          </el-col>
        </el-row>
      </div>
      <!--遮罩层-->
      <div class="loading" v-if="loading">
        <h4 class="tips">{{ tips }}</h4>
        <!--进度条-->
        <el-progress type="line" :percentage="percentage" class="progress" :show-text="true"></el-progress>
      </div>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="dialogClose">{{$i18nMy.t('关闭')}}</el-button>
    </span>
  </el-dialog>
</template>
<script>
export default {
  name: "UploadFile",
  data() {
    return {
      loading: false,
      visible:false,
      percentage: 0,
      tips: '',
      title:'导入Excel',
      timeInterval:'',
      uploadParam:{
        taskId:''
      },
      fileList:[]
    }
  },
  props: {
    url: {
      type: String,
      default: () => {
        return ""
      }
    },
    remarks: {
      type: String,
      default: () => {
        return ''
      }
    },
    templateUrl:{
      type: String,
      default: () => {
        return ""
      }
    },
    taskId:{
      type: String,
      default: () => {
        return ""
      }
    },
  },
  methods: {
    init(title){
      let _that = this;
      if('' !== (title || '')){
        _that.title = title;
      }
      _that.visible = true;
      _that.percentage = 0;
      _that.loading = false;
    },
    dialogClose(){
      let _that = this;
      clearInterval(_that.timeInterval);
      console.log("关闭"+_that.taskId+"导入弹框");
      _that.visible = false
    },
    handleDialogClose(){

    },
    getProgressRate(){
      let _that = this;
      clearInterval(_that.timeInterval);
      _that.timeInterval = setInterval(function (){
        _that.$http({
          url: '/sys/sysImportLog/import/log',
          method: 'get',
          params: {
            'taskId': _that.uploadParam.taskId,
          }
        }).then(({data}) => {
          if (data && data.success){
            if(data.rows.length > 0){
              _that.percentage = data.rows[0].progressRate;
              console.log($i18nMy.t('当前进度：')+_that.percentage)
              if (parseInt(_that.percentage || 0) >= 100) {
                clearInterval(_that.timeInterval);
              }
            }
          }else{
            _that.$message.error(data.msg)
            _that.closePercentage();
            clearInterval(_that.timeInterval);
          }
        }, (error) => {
          console.log(error);
          _that.$message.error(error)
          _that.closePercentage();
          clearInterval(_that.timeInterval);
        })
      },2000);
    },
    closePercentage(){
      let _that = this;
      _that.percentage = 0;
      _that.loading = false;
    },

    dataURLtoBlob:function(dataurl) {
      var raw =window.atob(dataurl);
      var rawLength=raw.length;
      var uInt8Array=new Uint8Array(rawLength);
      for(var i=0;i<rawLength;i++){
        uInt8Array[i]=raw.charCodeAt(i);
      }
      return new Blob([uInt8Array],{type:'application/vnd.ms-excel'})
    },
    downloadFile:function (url,name){
      var a = document.createElement("a")
      a.setAttribute("href",url)
      a.setAttribute("download",name)
      a.setAttribute("target","_blank")
      var clickEvent = document.createEvent("MouseEvents");
      clickEvent.initEvent("click", true, true);
      a.dispatchEvent(clickEvent);
    },
    downloadFileByBase64:function(base64,name){
      var myBlob = this.dataURLtoBlob(base64)
      var myUrl = URL.createObjectURL(myBlob)
      this.downloadFile(myUrl,name)
    },
    // 导入成功
    uploadSuccess (res, file) {
      let _that = this;
      if (res.success) {
        _that.$message.success({dangerouslyUseHTMLString: true,
          message: res.msg})
      } else {
        _that.$message.error(res.msg)
      }
      setTimeout(function (){
        _that.closePercentage();
        _that.$emit('refreshDataList')
      },500)
    },
    // 下载模板
    downloadTpl () {
      let _that = this;
      _that.$utils.download(_that.templateUrl)
    },
    handleBefore(file) {
      let _that = this;
      _that.uploadParam.taskId = _that.taskId + new Date().getTime();
      _that.submitUpload();
      console.log(file)
    },
    submitUpload() {
      let _that = this;
      _that.loading = true;
      _that.percentage = 0;
      _that.tips = $i18nMy.t('正在上传中……');
      _that.getProgressRate();
      // _that.$refs.upload.submit();
    }
  }
}
</script>

<style>
.upload-template .el-upload,
.upload-template .el-upload .el-upload-dragger{
  width: 100% !important;
}
.uploadfile{
  width: 360px;
  height: 360px;
  position: absolute;
  top: 50%;
  left: 50%;
  margin-left: -100px;
}
.loading{
  position: absolute;
  left: 0;
  top: 0;
  right: 0;
  bottom: 0;
  background: black;
  opacity: 0.8;
}
.progress{
  width: 200px;
  height: 200px;
  position: absolute;
  top: 50%;
  left: 50%;
  margin-left: -100px;
}
.tips{
  color: #409eff;
  position: absolute;
  top: 50%;
  left: 50%;
  margin-left: -100px;
  margin-top: -150px;
}

</style>
