<template>
<div class="el-scrollbar__wrap wrap-white padding-20">
  <div class="el-scrollbar__view">
    <el-form size="small" :inline="true" :model="inputForm" ref="inputForm" label-width="120px">
      <el-form-item :label="$i18nMy.t('二维码内容')" prop="encoderContent" :rules="[{required: true, message:'内容不能为空', trigger:'blur'}]">
        <el-input  style="width: 500px" v-model="inputForm.encoderContent" :placeholder="$i18nMy.t('请输入要生成二维码的字符串')"></el-input> 
          
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="doSubmit">{{$i18nMy.t('提交')}}</el-button>
      </el-form-item>
    </el-form>
    <div style="padding-left:120px; padding-top:20px" v-if="imageUrl">
        <el-image :src="imageUrl"/>
        <p>{{$i18nMy.t('请使用微信扫一扫')}}</p>
    </div>
  </div>
</div>
</template>

<script>
  export default {
    data () {
      return {
        inputForm: {
          encoderContent: 'http://www.jeeplus.org'
        },
        imageUrl: ''
      }
    },
    methods: {
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: `/tools/TwoDimensionCodeController/createTwoDimensionCode`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              if (data && data.success) {
                this.imageUrl = data.filePath + `?t=${new Date().getTime()}`
              }
            })
          }
        })
      }
    }
  }
</script>