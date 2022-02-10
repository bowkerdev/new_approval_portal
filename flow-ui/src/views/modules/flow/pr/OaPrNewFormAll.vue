<template>
  <div style="height: 100%">
        <el-tabs  type="border-card" v-model="activeName">
          <el-tab-pane :label="$i18nMy.t('主要信息')"  name="oaPrNewForm" key="oaPrNewForm">
            <OaPrNewForm :formReadOnly="true"  ref="oaPrNewForm" ></OaPrNewForm>
          </el-tab-pane>
          <el-tab-pane :label="$i18nMy.t('供应商报价和合同')"  name="OaPrNewFormForSupplier" key="OaPrNewFormForSupplier">
            <OaPrNewFormForSupplier :formReadOnly="true" ref="oaPrNewFormForSupplier" ></OaPrNewFormForSupplier>
          </el-tab-pane>
          <el-tab-pane :label="$i18nMy.t('补充文件')"  name="OaPrNewFormForDoc" key="OaPrNewFormForDoc">
            <OaPrNewFormForDoc :formReadOnly="true" ref="oaPrNewFormForDoc" ></OaPrNewFormForDoc>
          </el-tab-pane>
        </el-tabs>
  </div>
</template>

<script>
  import OaPrNewForm from './OaPrNewForm'
  import OaPrNewFormForDoc from './OaPrNewFormForDoc'
  import OaPrNewFormForSupplier from './OaPrNewFormForSupplier'
  export default {
    data() {
      return {
        activeName: 'oaPrNewForm',
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
      OaPrNewFormForDoc,
      OaPrNewFormForSupplier
    },
    activated() {
      //this.init()
    },
    methods: {
      init(query) {
        this.activeName='oaPrNewForm'
        this.businessId=query.businessId
        this.$refs.oaPrNewForm.init(query, this)
        this.$refs.oaPrNewFormForDoc.init(query)
        this.$refs.oaPrNewFormForSupplier.init(query)
      },
      // 表单提交
      saveForm(callBack) {
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
