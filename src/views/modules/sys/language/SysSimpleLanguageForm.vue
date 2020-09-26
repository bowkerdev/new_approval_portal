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
            <el-form-item :label="$i18nMy.t('简体中文')" prop="cn"
                :rules="[
                 ]">
              <el-input v-model="inputForm.cn" :placeholder="$i18nMy.t('请填写简体中文')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item :label="$i18nMy.t('繁体中文')" prop="hk"
                :rules="[
                 ]">
              <el-input v-model="inputForm.hk" :placeholder="$i18nMy.t('请填写繁体中文')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item :label="$i18nMy.t('英文')" prop="en"
                :rules="[
                 ]">
              <el-input v-model="inputForm.en" :placeholder="$i18nMy.t('请填写英文')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item :label="$i18nMy.t('越南文')" prop="vn"
                :rules="[
                 ]">
              <el-input v-model="inputForm.vn" :placeholder="$i18nMy.t('请填写越南文')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item :label="$i18nMy.t('柬埔寨')" prop="cam"
                :rules="[
                 ]">
              <el-input v-model="inputForm.cam" :placeholder="$i18nMy.t('请填写柬埔寨')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item :label="$i18nMy.t('识别码')" prop="code"
                :rules="[
                  {required: true, message:'识别码不能为空', trigger:'blur'}
                 ]">
              {{inputForm.code}}
           </el-form-item>
        </el-col>
    <el-col :span="24">
     <el-form-item label-width="0">
            <el-tabs v-model="sysSimpleLanguageTab">
            </el-tabs>
           </el-form-item>
          </el-col>
        </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>{{$i18nMy.t('确定')}}</el-button>
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
        sysSimpleLanguageTab: '0',
        inputForm: {
          id: '',
          cn: '',
          hk: '',
          en: '',
          vn: '',
          cam: '',
          code: ''
        }
      }
    },
    components: {
    },
    methods: {
      init (method, id) {
        this.method = method
        if (method === 'add') {
          this.title = `新建国际化`
        } else if (method === 'edit') {
          this.title = '修改国际化'
        } else if (method === 'view') {
          this.title = '查看国际化'
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          this.inputForm.id = id
          this.sysSimpleLanguageTab = '0'
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/sys/sysSimpleLanguage/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.sysSimpleLanguage)
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
              url: `/sys/sysSimpleLanguage/save`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              if (data && data.success) {
                this.visible = false
                this.$message.success(data.msg)
                this.$emit('refreshDataList')
              }
              this.loading = false
            })
          }
        })
      }
    }
  }
</script>