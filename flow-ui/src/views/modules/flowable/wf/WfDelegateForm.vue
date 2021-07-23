<template>
<div>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form :model="inputForm" size="small" ref="inputForm" v-loading="loading" :class="method==='view'?'readonly':''"  :disabled="method==='view'"
             label-width="120px">
      <el-row  :gutter="15">
        <el-col :span="12">
            <el-form-item label="委托人" prop="owner.id"
                :rules="[
                 ]">
                <user-select :limit='1' :value="inputForm.owner.id" @getValue='(value) => {inputForm.owner.id=value}'></user-select>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="代理人" prop="delegate.id"
                :rules="[
                 ]">
                <user-select :limit='1' :value="inputForm.delegate.id" @getValue='(value) => {inputForm.delegate.id=value}'></user-select>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime"
                :rules="[
                 ]">
              <el-input v-model="inputForm.startTime" :placeholder="$i18nMy.t('请填写开始时间')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime"
                :rules="[
                 ]">
              <el-input v-model="inputForm.endTime" :placeholder="$i18nMy.t('请填写结束时间')"     ></el-input>
           </el-form-item>
        </el-col>
        <!-- <el-col :span="12">
            <el-form-item label="task_name" prop="taskName"
                :rules="[
                 ]">
              <el-input v-model="inputForm.taskName" :placeholder="$i18nMy.t('请填写task_name')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="申请单号" prop="applicationNo"
                :rules="[
                 ]">
              <el-input v-model="inputForm.applicationNo" :placeholder="$i18nMy.t('请填写申请单号')"     ></el-input>
           </el-form-item>
        </el-col> -->
        </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="visible = false">关闭</el-button>
      <el-button size="small" type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>确定</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
  import UserSelect from '@/components/userSelect'
  export default {
    data () {
      return {
        title: '',
        method: '',
        visible: false,
        loading: false,
        inputForm: {
          id: '',
          owner: {
            id: ''
          },
          delegate: {
            id: ''
          },
          startTime: '',
          endTime: '',
          taskName: '',
          applicationNo: ''
        }
      }
    },
    components: {
      UserSelect
    },
    methods: {
      init (method, id) {
        this.method = method
        this.inputForm.id = id
        if (method === 'add') {
          this.title = `新建流程委托设置`
        } else if (method === 'edit') {
          this.title = '修改流程委托设置'
        } else if (method === 'view') {
          this.title = '查看流程委托设置'
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/flowable/wf/wfDelegate/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.wfDelegate)
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
              url: `/flowable/wf/wfDelegate/save`,
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