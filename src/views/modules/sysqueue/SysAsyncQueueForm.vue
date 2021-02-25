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
            <el-form-item label="类型" prop="type"
                :rules="[
                 ]">
              <el-input v-model="inputForm.type" :placeholder="$i18nMy.t('请填写类型')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="请求关联主键，可以处理一个请求是多个异步的问题" prop="requestKey"
                :rules="[
                 ]">
              <el-input v-model="inputForm.requestKey" :placeholder="$i18nMy.t('请填写请求关联主键，可以处理一个请求是多个异步的问题')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="开始处理时间，如果有值，说明已经被处理，不可重复处理" prop="dealTime"
                :rules="[
                 ]">
                <el-date-picker
                      style="width: 100%;"
                      v-model="inputForm.dealTime"
                      type="datetime"
                      value-format="yyyy-MM-dd HH:mm:ss"
		      :placeholder="$i18nMy.t('选择日期时间')">
                    </el-date-picker>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="产生" prop="param"
                :rules="[
                 ]">
              <el-input v-model="inputForm.param" :placeholder="$i18nMy.t('请填写产生')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="处理结果" prop="result"
                :rules="[
                 ]">
                <el-select v-model="inputForm.result"  placeholder="$i18nMy.t('请选择')"  style="width: 100%;">
                          <el-option
                            v-for="item in $dictUtils.getDictList('yes_no')"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                          </el-option>
                      </el-select>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="处理结果后的值" prop="resultMsg"
                :rules="[
                 ]">
              <el-input v-model="inputForm.resultMsg" :placeholder="$i18nMy.t('请填写处理结果后的值')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="备注信息" prop="remarks"
                :rules="[
                 ]">
          <el-input type="textarea" v-model="inputForm.remarks" :placeholder="$i18nMy.t('请填写备注信息')"     ></el-input>
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
          type: '',
          requestKey: '',
          dealTime: '',
          param: '',
          result: '',
          resultMsg: '',
          remarks: ''
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
          this.title = $i18nMy.t(`新建异步队列`)
        } else if (method === 'edit') {
          this.title = $i18nMy.t('修改异步队列')
        } else if (method === 'view') {
          this.title = $i18nMy.t('查看异步队列')
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/sysqueue/sysAsyncQueue/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.sysAsyncQueue)
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
              url: `/sysqueue/sysAsyncQueue/save`,
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