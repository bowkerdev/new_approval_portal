<template>
<div>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form size="small" :model="inputForm" ref="inputForm" :class="method==='view'?'readonly':''" :disabled="method==='view'" v-loading="loading" @keyup.enter.native="doSubmit()"
             label-width="120px" @submit.native.prevent>
      <el-row  :gutter="15">
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('名称')" prop="name"
                :rules="[
                  {required: true, message:'名称不能为空', trigger:'blur'}
                 ]">
			        <el-input v-model="inputForm.name" :placeholder="$i18nMy.t('请填写名称')"     ></el-input>
	         </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('表达式')" prop="expression"
                :rules="[
                  {required: true, message:'表达式不能为空', trigger:'blur'}
                 ]">
			        <el-input v-model="inputForm.expression" :placeholder="$i18nMy.t('请填写表达式')"     ></el-input>
	         </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('备注')" prop="remarks"
                :rules="[
                 ]">
					<el-input type="textarea" v-model="inputForm.remarks" :placeholder="$i18nMy.t('请填写备注')"     ></el-input>
	         </el-form-item>
        </el-col>
        </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button size="small" type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>{{$i18nMy.t('确定')}}</el-button>
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
        conditionTab: '0',
        inputForm: {
          id: '',
          name: '',
          expression: '',
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
          this.title = $i18nMy.t('新建流程表达式')
        } else if (method === 'edit') {
          this.title = $i18nMy.t('修改流程表达式')
        } else if (method === 'view') {
          this.title = $i18nMy.t('查看流程表达式')
        }
        this.visible = true
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          this.conditionTab = '0'
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.$http({
              url: `/extension/condition/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.condition)
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
              url: `/extension/condition/save`,
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