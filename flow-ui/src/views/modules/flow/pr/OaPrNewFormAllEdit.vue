<template>
  <div style="height: 100%">
        <el-tabs  type="border-card" v-model="activeName">
          <el-tab-pane name="oaPrNewForm" key="oaPrNewForm">
            <span slot="label"><i class="el-icon-star-on"></i>{{$i18nMy.t('主要信息')}}</span>
            <OaPrNewForm :formReadOnly="isReadOnly" ref="oaPrNewForm" ></OaPrNewForm>
          </el-tab-pane>
          <el-tab-pane name="OaPrNewFormForSupplier" key="OaPrNewFormForSupplier">
            <span slot="label"><i class="el-icon-star-on"></i>{{$i18nMy.t('供应商报价和合同')}}</span>
            <OaPrNewFormForSupplier :formReadOnly="isReadOnly" ref="oaPrNewFormForSupplier" ></OaPrNewFormForSupplier>
          </el-tab-pane>
          <el-tab-pane name="OaPrNewFormForDoc" key="OaPrNewFormForDoc" :label="$i18nMy.t('补充文件')" >
            <OaPrNewFormForDoc :formReadOnly="parentForm!='TaskForm'" ref="oaPrNewFormForDoc" ></OaPrNewFormForDoc>
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
        isReadOnly: false,
        parentForm: '',
        activeName:'oaPrNewForm',
        title: '',
        method: '',
        loading: false,
        businessId:"",
        detailInfoEdit:false
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
    watch:{
      activeName(val, oldVal){//普通的watch监听
        if(val!='oaPrNewForm'){
          this.detailInfoEdit = false
          for(var i=0;i<this.$refs.oaPrNewForm.detailInfo.length;i++){
            if(this.$refs.oaPrNewForm.detailInfo[i].edit){
              this.$refs.oaPrNewForm.$message.warning($i18nMy.t('物品列表还有未保存的记录'))
              this.$nextTick(() => {
                this.activeName = oldVal
              })
              this.detailInfoEdit = true
              return
            }
          }
        }
        // 非直接关联数据，保存的时候同步
        if(val=='oaPrNewForm'&&oldVal =='OaPrNewFormForSupplier'){// 第三个界面进入第一个界面，才需要更新第一个界面的数据
          this.setPage1Data()
        }
        else if(val=='OaPrNewFormForDoc'){// 点击第二个tab
          if(oldVal =='oaPrNewForm'){
            this.setPage2Data()
          }
          else if(oldVal =='OaPrNewFormForSupplier'){
            this.setPage1Data()
          }
          this.setPage3Data()
        }
        else if(val=='OaPrNewFormForSupplier'&&oldVal =='oaPrNewForm'){
          this.setPage2Data()
        }
      }
    },
    methods: {
      setPage1Data(){
        if(!this.detailInfoEdit&&
          !this.compArray(this.$refs.oaPrNewFormForSupplier.detailInfo,this.$refs.oaPrNewForm.detailInfo)){
          this.$refs.oaPrNewForm.detailInfo=JSON.parse(
            JSON.stringify(this.$refs.oaPrNewFormForSupplier.detailInfo))
        }
      },
      setPage2Data(){
        this.$refs.oaPrNewFormForSupplier.inputForm=this.$refs.oaPrNewForm.inputForm
        if(!this.compArray(this.$refs.oaPrNewFormForSupplier.detailInfo,this.$refs.oaPrNewForm.detailInfo)){
          console.log("清理数据")
/*        this.$refs.oaPrNewFormForSupplier.supplierInfo = []
          this.$refs.oaPrNewFormForSupplier.supplierInfoByDetailInfo = []
          for(var i=0;i<this.$refs.oaPrNewForm.detailInfo.length;i++){
            this.$refs.oaPrNewForm.detailInfo[i].supplierName =''
            this.$refs.oaPrNewForm.detailInfo[i].unitPrice =''
            this.$refs.oaPrNewForm.detailInfo[i].vatUnitPrice =''
            this.$refs.oaPrNewForm.detailInfo[i].vat =''
            this.$refs.oaPrNewForm.detailInfo[i].docAmount =''
            this.$refs.oaPrNewForm.detailInfo[i].docVatAmount =''
          } */
          /* this.$refs.oaPrNewFormForSupplier.detailInfo = JSON.parse(JSON.stringify(this.$refs.oaPrNewForm.detailInfo))
          this.$refs.oaPrNewFormForSupplier.updateSupplierByDetailInfo() */
          this.updatePage2DataByDetailInfo()
        }
      },
      setPage3Data(){
        this.$refs.oaPrNewFormForDoc.inputForm=this.$refs.oaPrNewForm.inputForm
        this.$refs.oaPrNewFormForDoc.detailInfo=this.$refs.oaPrNewForm.detailInfo
        //setTimeout()
      },
      updatePage2DataByDetailInfo(){
        this.$refs.oaPrNewFormForSupplier.detailInfo = JSON.parse(JSON.stringify(this.$refs.oaPrNewForm.detailInfo))
        this.$refs.oaPrNewFormForSupplier.updateSupplierByDetailInfo()
        this.setPage1Data()
        this.setPage3Data()
      },

      init(query, parent) {
        this.activeName='oaPrNewForm'
        this.businessId=query.businessId
        if(query.readOnly || query.formReadOnly) {
          this.isReadOnly = ( (query.readOnly || query.formReadOnly) == "true" )
        }
        if (query.taskDefKey && (query.taskDefKey.indexOf('FC')>0 || query.taskDefKey.indexOf('FA')>0)) { // 对FA、FC两个角色的特殊处理，只允许修改部分特定字段，其他大部分字段不能改
          this.isReadOnly=true
        }
        if(query.parentForm) {
          this.parentForm = query.parentForm
        }
        this.$refs.oaPrNewForm.init(query, this, parent)
        this.$refs.oaPrNewFormForDoc.init(query)
        this.$refs.oaPrNewFormForSupplier.init(query)
      },
      compArray(arr1,arr2){
        if(arr1.length != arr2.length){
          return false
        }
        for(var i=0;arr1.length > i;i++){
          for(var key in arr1[i]){
            if(!((arr1[i][key] ==null || arr1[i][key].toString() =='NaN' || arr1[i][key].toString() =='0')&&
                (arr2[i][key] ==null || arr2[i][key].toString() =='NaN' || arr2[i][key].toString() =='0'))){
              if ( key == "exRate" && parseFloat(arr1[i][key]) != parseFloat(arr2[i][key])) {
                return false
              } else if (arr1[i][key] != arr2[i][key]) {
                return false
              }
            }
          }
          for(var key in arr2[i]){ // 如果 arr2 key 多一个值，需反过来比对
            if(!((arr1[i][key] ==null ||arr1[i][key].toString() =='NaN' || arr1[i][key].toString() =='0')&&
                (arr2[i][key] ==null ||arr2[i][key].toString() =='NaN' || arr2[i][key].toString() =='0'))){
              if ( key == "exRate" && parseFloat(arr1[i][key]) != parseFloat(arr2[i][key])) {
                return false
              } else if(arr1[i][key] != arr2[i][key]){
                return false
              }
            }
          }
        }
        return true;
      },
      // 表单提交
      saveForm(callBack) {
        if(this.activeName !='oaPrNewForm'){
          this.$refs.oaPrNewForm.detailInfo=this.$refs.oaPrNewFormForSupplier.detailInfo
        }
        if (!this.$refs.oaPrNewForm.checkForm()) {
          this.activeName= 'oaPrNewForm'
          return ;
        }
        if(!this.$refs.oaPrNewFormForSupplier.checkForm()){
          this.activeName= 'OaPrNewFormForSupplier'
          return ;
        }
        if(!this.$refs.oaPrNewFormForDoc.checkForm()){
          this.activeName= 'OaPrNewFormForDoc'
          return ;
        }
        this.activeName= 'oaPrNewForm'
        this.$refs.oaPrNewForm.inputForm.supplierInfo=
          JSON.stringify(this.$refs.oaPrNewFormForSupplier.supplierInfo)

        this.$refs.oaPrNewForm.inputForm.supplementaryDoc=
          JSON.stringify(this.$refs.oaPrNewFormForDoc.supplementaryDoc)

        this.$refs.oaPrNewForm.saveForm((businessTable, businessId) => {
           callBack(businessTable, businessId)
        })
      },
      getTotalAmount() {
        return this.$refs.oaPrNewForm.inputForm.totalVatBaseAmount;
      },
      // 表单提交
      saveAsDraft(callBack) {
        if(this.activeName !='oaPrNewForm'){
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
