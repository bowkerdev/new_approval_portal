<template>
<div>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form size="small" :model="inputForm" :rules="dataRule" ref="inputForm" v-loading="loading" :class="method==='view'?'readonly':''" :disabled="method==='view'" @keyup.enter.native="doSubmit()"
             label-width="120px" @submit.native.prevent>
      <el-row  :gutter="15">
        <el-col :span="24">
            <el-form-item label="名称" prop="name">
			        <el-input v-model="inputForm.name" :placeholder="$i18nMy.t('请填写名称')"     ></el-input>
	         </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item label="编码" prop="code">
			        <el-input :readonly="isDefaultButton(inputForm.code)" v-model="inputForm.code" :placeholder="$i18nMy.t('请输入按钮编码')"     ></el-input>
           </el-form-item>
           <el-form-item>
              <div class="tip">
                <p>注意：按钮编码不能重复，系统按钮以_flow_开头，自定义按钮不能以_flow_开头。
                  系统按钮和自定义按钮的区别是，系统按钮是绑定具体的action进行提交，如果你定义了系统按钮，必须在代码中实现对应的action方法。
                  自定义按钮无需在代码中添加action方法，触发自定义按钮时调用的是【同意】按钮对应的action，并把该按钮对应的code设置为true、其他自定义按钮对应的code设置为false作为流程变量进行提交。</p>
              </div>
           </el-form-item>  
        </el-col>
        <el-col :span="24">
            <el-form-item label="排序" prop="sort">
			        <el-input-number v-model="inputForm.sort" :placeholder="$i18nMy.t('请填写排序')"     ></el-input-number>
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
      var validatePass2 = (rule, value, callback) => {
        if (this.method === 'add' && this.isDefaultButton(value)) {
          callback(new Error('请勿使用系统预设编码!'))
        } else {
          callback()
        }
      }
      return {
        title: '',
        method: '',
        visible: false,
        loading: false,
        buttonTab: '0',
        inputForm: {
          id: '',
          name: '',
          code: '',
          sort: 0
        },
        dataRule: {
          name: [{required: true, message: '名称不能为空', trigger: 'blur'}],
          code: [{required: true, message: '编码不能为空', trigger: 'blur'},
                  {validator: validatePass2, trigger: 'blur'}],
          sort: [{required: true, message: '排序不能为空', trigger: 'blur'}]
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
          this.title = $i18nMy.t('新建常用按钮')
        } else if (method === 'edit') {
          this.title = $i18nMy.t('修改常用按钮')
        } else if (method === 'view') {
          this.title = $i18nMy.t('查看常用按钮')
        }
        this.visible = true
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          this.buttonTab = '0'
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.$http({
              url: `/extension/button/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.button)
            })
          }
        })
      },
      isDefaultButton (code) {
        let defaultButtons = ['_flow_save', '_flow_agree', '_flow_reject', '_flow_back', '_flow_add_multi_instance', '_flow_del_multi_instance', '_flow_transfer', '_flow_delegate', '_flow_stop', '_flow_print']
        return defaultButtons.filter((val) => {
          return code === val
        }).length > 0
      },
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: `/extension/button/save`,
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