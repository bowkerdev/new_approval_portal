<template>
<div class="el-scrollbar__wrap overflow-auto wrap-white padding-20">
  <div class="el-scrollbar__view">
    <el-form size="small" :model="inputForm" ref="inputForm"
             label-width="120px">
      <el-row  :gutter="15">
        <el-col :span="16">
            <el-form-item  :label="$i18nMy.t('发送到')"  prop="emailAddress"
                :rules="[
                  {required: true, message:'收件地址不能为空', trigger:'blur'}
                 ]">
          		   <el-input v-model="inputForm.emailAddress" :placeholder="$i18nMy.t('输入多个邮件地址请用英文符号;隔开')"></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="4">
            <el-form-item label-width="10px"> 
                 <el-button type="primary" @click="doSubmit">{{$i18nMy.t('提交')}}</el-button>
           </el-form-item>
        </el-col>
        <el-col :span="16">
            <el-form-item :label="$i18nMy.t('标题')" prop="title"
                :rules="[
                  {required: true, message:'标题不能为空', trigger:'blur'}
                 ]">
			        <el-input v-model="inputForm.title" :placeholder="$i18nMy.t('请输入标题')"     ></el-input>
	         </el-form-item>
        </el-col>
         <el-col :span="20">
            <el-form-item :label="$i18nMy.t('内容')" prop="content" :rules="[
                  {required: true, message:'内容不能为空', trigger:'blur'}
                 ]">
              <WangeEditor ref="editor"   v-model="inputForm.content"/>
            </el-form-item>
        </el-col>
        </el-row>
    </el-form>
</div>
</div>
</template>

<script>
  import WangeEditor from '@/components/editor/WangEditor'
  export default {
    data () {
      return {
        inputForm: {
          emailAddress: '',
          title: '',
          content: ''
        }
      }
    },
    components: {
      WangeEditor
    },
    methods: {
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: `/tools/email/send`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              if (data.success) {
                this.$message.info({dangerouslyUseHTMLString: true,
                  message: data.msg})
              }
            })
          }
        })
      }
    }
  }
</script>