<template>
  <div style="height: 100%">
        <el-tabs  type="border-card" v-model="activeName">
          <el-tab-pane :label="$i18nMy.t('主要信息')" >
            <OaPrNewForm  ref="oaPrNewForm" ></OaPrNewForm>
          </el-tab-pane>
          <el-tab-pane :label="$i18nMy.t('补充文件')" >
            <OaPrNewFormForDoc ref="oaPrNewFormForDoc" ></OaPrNewFormForDoc>
          </el-tab-pane>
        </el-tabs>
  </div>
</template>

<script>
  import OaPrNewForm from './OaPrNewForm'
  import OaPrNewFormForDoc from './OaPrNewFormForDoc'
  export default {
    data() {
      return {
        activeName:'0',
        title: '',
        method: '',
        loading: false,
        businessId:""
      }
    },
    props: {
      formReadOnly: {
        type: Boolean,
        default: false
      }
    },
    components: {
      OaPrNewForm,
      OaPrNewFormForDoc
    },
    watch:{
      activeName(val, oldVal){//普通的watch监听
        if(val=='0'){
          this.$refs.oaPrNewForm.inputForm.supplementaryDoc=
            JSON.stringify(this.$refs.oaPrNewFormForDoc.supplementaryDoc)
        }
        else{
          this.$refs.oaPrNewFormForDoc.inputForm=this.$refs.oaPrNewForm.inputForm
          this.$refs.oaPrNewFormForDoc.detailInfo=this.$refs.oaPrNewForm.detailInfo
        }
      }
    },
    activated() {
      //var query={businessId:'77e400d5c0734a7fa55e43d6369c66ed'}
      //this.init(query)
    },
    methods: {
      init(query) {
        this.businessId=query.businessId
        this.$refs.oaPrNewForm.init(query)
        this.$refs.oaPrNewFormForDoc.init(query)
      },
      // 表单提交
      saveForm(callBack) {
        this.$refs.oaPrNewForm.inputForm.supplementaryDoc=
          JSON.stringify(this.$refs.oaPrNewFormForDoc.supplementaryDoc)

        this.$refs.oaPrNewForm.saveForm((businessTable, businessId) => {
           callBack(businessTable, businessId)
        })
        callBack("oa_pr_new", this.businessId)
      }
    }
  }
</script>
<style scoped lang = "less">
  .el-form-item {
      margin-bottom: 2px;
  }
</style>
