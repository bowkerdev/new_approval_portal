<template>
  <el-dialog :title="title" :visible.sync="visible">
    <el-upload
      ref="upload"
      class="upload-wrap"
      drag
      :limit="1"
      action="https://commontools.bowkerasia.com/zhimitool/ie/taskQueue/push/import"
      :headers="{ token: token, tokenType: tokenType, language: language }"
      :on-success="uploadSuccess"
      :on-error="handleError"
      :data="uploadData"
      :file-list="filesList"
      :show-file-list="true"
      :auto-upload="false"
    >
      <i class="el-icon-upload"></i>
      <div class="el-upload__text">{{ $i18nMy.t('将文件拖到此处，或') }}<em>{{ $i18nMy.t('点击上传') }}</em></div>
    </el-upload>
    <div class="upload-button">
      <el-button type="primary" @click="downloadTpl()">
        {{ $i18nMy.t("下载模板") }}
      </el-button>
      <el-button type="primary" @click="submitUpload()">
        {{ $i18nMy.t("确认上传") }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
export default {
  name: "CommonUpload",
  props: {
    templateKey: {
      type: String,
      required: true,
    },
    uploadKey: {
      type: String,
      required: true,
    },
    uploadParam: {
      type: String,
      default: '{}',
    },
    titleMsg: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      title: this.titleMsg ||this.$i18nMy.t("上传文件"),
      visible: false,
      uploadData: {
        "importConfig.configKey": this.uploadKey,
        "param": this.uploadParam,
      },
      token: "",
      filesList: [],
      tokenType: "",
      language: localStorage.getItem("lang")
    };
  },
  watch: {
    uploadParam(val) {
      this.uploadData.param = val
    }
  },
  created() {
    this.token = this.$cookie.get(process.env.VUE_APP_SSO_TYPE + "_token")
    this.tokenType = process.env.VUE_APP_SSO_TYPE
  },
  methods: {
    downloadTpl() {
      this.$utils.download(this.templateKey)
    },
    submitUpload() {
      this.$refs.upload.submit()
    },
    uploadSuccess(res, file) {
      this.filesList = [];
      if (res.success) {
        this.$message.success({
          dangerouslyUseHTMLString: true,
          message: res.msg,
        });
        var dynamicMenuRoutes = JSON.parse(
          sessionStorage.getItem("dynamicMenuRoutes") || "[]"
        );
        const route = dynamicMenuRoutes.filter(
          (item) =>
            item.path === "/https://commontools.bowkerasia.com/commontoolsH5/"
        );
        if (route.length >= 1) {
          this.$router.push({ path: "/" + route[0].fullPath });
        }
      } else {
        this.$message.error(res.msg);
      }
      this.visible = false;
    },
    handleError(err, file, fileList) {
      this.$message.error("文件上传失败");
    }
  }
}
</script>

<style scoped>
.upload-wrap {
  text-align: center;
}
.upload-button {
  margin-top: 20px;
  display: flex;
  justify-content: space-around;
  align-items: center;
}
.upload-wrap ::v-deep .el-upload.el-upload--text, 
.upload-wrap ::v-deep .el-upload-dragger {
  width: 100%;
}
</style>
