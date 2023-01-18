<template>
  <div style="height: 100%">
        <el-tabs  type="border-card" v-model="activeName">
          <el-tab-pane :label="$i18nMy.t('主要信息')" name="oaPrNewForm" key="oaPrNewForm">
            <OaPrNewForm :formReadOnly="true"  ref="oaPrNewForm" ></OaPrNewForm>
          </el-tab-pane>
          <el-tab-pane :label="$i18nMy.t('供应商报价和合同')" name="OaPrNewFormForSupplier" key="OaPrNewFormForSupplier">
            <OaPrNewFormForSupplier :formReadOnly="true" ref="oaPrNewFormForSupplier" ></OaPrNewFormForSupplier>
          </el-tab-pane>
          <el-tab-pane :label="$i18nMy.t('补充文件')" name="OaPrNewFormForDoc" key="OaPrNewFormForDoc">
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
      init(query, parent) {
        this.activeName='oaPrNewForm' 
        this.businessId=(query.businessId).replace("__copy","")

        if (query.taskDefKey && (query.taskDefKey.indexOf('FC')>0 || query.taskDefKey.indexOf('FA')>0)) { // 对FA、FC两个角色的特殊处理，只允许修改部分特定字段，其他大部分字段不能改
          this.isReadOnly=true
        }

        this.$nextTick(() => {
          this.$http({
            url: `/flow/pr/oaPrNew/queryById?id=${this.businessId}`,
            method: 'get'
          }).then(({
            data
          }) => {
            this.$refs.oaPrNewForm.init(query, this, parent, data)
            this.$refs.oaPrNewFormForDoc.init(query, parent, data)
            this.$refs.oaPrNewFormForSupplier.init(query, this, parent, data)
          })
        })
      },
      updatePage2DataByDetailInfo(){
        // 更新第2页
        this.$refs.oaPrNewFormForSupplier.detailInfo=this.$refs.oaPrNewForm.detailInfo
        this.$refs.oaPrNewFormForSupplier.updateSupplierByDetailInfo()
        // 更新第3页
        this.$refs.oaPrNewFormForDoc.detailInfo=this.$refs.oaPrNewForm.detailInfo
      },
      // 表单提交
      saveForm(callBack) {
        callBack("oa_pr_new", this.businessId)
      },

      // 表单提交
      saveAsDraft(callBack) {
/*        if(this.activeName !='oaPrNewForm'){
          this.$refs.oaPrNewForm.detailInfo=this.$refs.oaPrNewFormForSupplier.detailInfo
        }
        if(!this.$refs.oaPrNewFormForSupplier.checkSupplierList()){
          this.activeName= 'OaPrNewFormForSupplier'
          return
        }
        if(!this.$refs.oaPrNewFormForDoc.checkForm()){
          this.activeName= 'OaPrNewFormForDoc'
          return
        }
        this.activeName= 'oaPrNewForm'
        this.$refs.oaPrNewForm.inputForm.supplierInfo=
          JSON.stringify(this.$refs.oaPrNewFormForSupplier.supplierInfo)

        this.$refs.oaPrNewForm.inputForm.supplementaryDoc=
          JSON.stringify(this.$refs.oaPrNewFormForDoc.supplementaryDoc)
 */
        this.$refs.oaPrNewForm.saveAsDraft((businessTable, businessId) => {
           callBack(businessTable, businessId)
        })
      }
    }
  }
</script>
<style scoped lang = "less">
  .el-form-item {
      margin-bottom: 2px;
  }
</style>
