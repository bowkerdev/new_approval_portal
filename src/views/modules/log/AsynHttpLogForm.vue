<template>
<div>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form :model="inputForm" ref="inputForm" v-loading="loading" :class="method==='view'?'readonly':''"  :disabled="method==='view'"
             label-width="120px">
      <el-row  :gutter="15">
        <el-col :span="12">
            <el-form-item label="接口名称" prop="url"
                :rules="[
                 ]">
              <el-input v-model="inputForm.url" :placeholder="$i18nMy.t('请填写接口名称')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="是否正确执行" prop="isSuccee"
                :rules="[
                 ]">
              <el-input v-model="inputForm.isSuccee" :placeholder="$i18nMy.t('请填写是否正确执行')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="执行时间" prop="execTime"
                :rules="[
                 ]">
              <el-input v-model="inputForm.execTime" :placeholder="$i18nMy.t('请填写执行时间')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="入参" prop="param"
                :rules="[
                 ]">
              <el-input v-model="inputForm.param" :placeholder="$i18nMy.t('请填写入参')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="返回结果" prop="returnString"
                :rules="[
                 ]">
              <el-input v-model="inputForm.returnString" :placeholder="$i18nMy.t('请填写返回结果')"     ></el-input>
           </el-form-item>
        </el-col>
        </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{$i18nMy.t("关闭")}}</el-button>
      <el-button type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>{{$i18nMy.t("确定")}}</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
  export default {
    data () {
      return {
        title: '',
        method: '',
        visible: false,
        loading: false,
        inputForm: {
          id: '',
          url: '',
          isSuccee: '',
          execTime: '',
          param: '',
          returnString: ''
        }
      }
    },
    components: {
    },
    methods: {
      init (method, id) {
        this.method = method
        this.inputForm.id = id
        if (method === 'add') {
          this.title = $i18nMy.t(`新建日志`)
        } else if (method === 'edit') {
          this.title = $i18nMy.t('修改日志')
        } else if (method === 'view') {
          this.title = $i18nMy.t('查看日志')
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/log/asynHttpLog/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.asynHttpLog)
              this.loading = false
            })
          }
        })
      },
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: `/log/asynHttpLog/save`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.visible = false
                this.$message.success(data.msg)
                this.$emit('refreshDataList')
              }
            })
          }
        })
      }
    }
  }
</script>