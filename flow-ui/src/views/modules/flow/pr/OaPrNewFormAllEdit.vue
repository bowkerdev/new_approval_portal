<template>
  <div style="height: 100%">
        <el-tabs  type="border-card" v-model="activeName">
          <el-tab-pane :label="$i18nMy.t('主要信息')" >
            <OaPrNewForm ref="oaPrNewForm" ></OaPrNewForm>
          </el-tab-pane>
          <el-tab-pane :label="$i18nMy.t('补充文件')" >
            <OaPrNewFormForDoc ref="oaPrNewFormForDoc" ></OaPrNewFormForDoc>
          </el-tab-pane>
          <el-tab-pane :label="$i18nMy.t('供应商报价和合同')" >
            <OaPrNewFormForSupplier  ref="oaPrNewFormForSupplier" ></OaPrNewFormForSupplier>
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
        activeName:'0',
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
        if(val!='0'){
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
        if(val=='0'&&oldVal =='2'){// 第三个界面进入第一个界面，才需要更新第一个界面的数据
          this.setPage1Data()
        }
        else if(val=='1'){// 点击第二个tab
          if(oldVal =='0'){
            this.setPage3Data()
          }
          else if(oldVal =='2'){
            this.setPage1Data()
          }
          this.setPage2Data()
        }
        else if(val=='2'&&oldVal =='0'){
          this.setPage3Data()
        }
      }
    },
    methods: {
      setPage1Data(){
        if(!this.detailInfoEdit&&
          !this.compArray(this.$refs.oaPrNewFormForSupplier.detailInfo,this.$refs.oaPrNewForm.detailInfo)){
          this.$refs.oaPrNewForm.detailInfo=JSON.parse(
            JSON.stringify(this.$refs.oaPrNewFormForSupplier.detailInfo))
          this.$refs.oaPrNewForm.currencyChange()
        }
      },
      setPage2Data(){
        this.$refs.oaPrNewFormForDoc.inputForm=this.$refs.oaPrNewForm.inputForm
        this.$refs.oaPrNewFormForDoc.detailInfo=this.$refs.oaPrNewForm.detailInfo
      },
      setPage3Data(){
        this.$refs.oaPrNewFormForSupplier.inputForm=this.$refs.oaPrNewForm.inputForm
        if(!this.compArray(this.$refs.oaPrNewFormForSupplier.detailInfo,this.$refs.oaPrNewForm.detailInfo)){
          console.log("清理数据")
          this.$refs.oaPrNewFormForSupplier.supplierInfo = []
          this.$refs.oaPrNewFormForSupplier.supplierInfoByDetailInfo = []
          this.$refs.oaPrNewFormForSupplier.detailInfo=JSON.parse(
            JSON.stringify(this.$refs.oaPrNewForm.detailInfo))
        }
      },
      init(query) {
        this.businessId=query.businessId
        this.$refs.oaPrNewForm.init(query)
        this.$refs.oaPrNewFormForDoc.init(query)
        this.$refs.oaPrNewFormForSupplier.init(query)
      },
      compArray(arr1,arr2){
        if(arr1.length != arr2.length){
          return false
        }
        for(var i=0;arr1.length > i;i++){
          for(var key in arr1[i]){
            if(!((arr1[i][key] ==null ||arr1[i][key].toString() =='NaN')&&
                (arr2[i][key] ==null ||arr2[i][key].toString() =='NaN'))){
              if(arr1[i][key] != arr2[i][key]){
                return false
              }
            }
          }
          for(var key in arr2[i]){ // 如果 arr2 key 多一个值，需反过来比对
            if(!((arr1[i][key] ==null ||arr1[i][key].toString() =='NaN')&&
                (arr2[i][key] ==null ||arr2[i][key].toString() =='NaN'))){
              if(arr1[i][key] != arr2[i][key]){
                return false
              }
            }
          }
        }
        return true;
      },
      // 表单提交
      saveForm(callBack) {
        if(this.activeName !=0){
          this.$refs.oaPrNewForm.detailInfo=this.$refs.oaPrNewFormForSupplier.detailInfo
        }
        if(!this.$refs.oaPrNewFormForDoc.checkForm()){
          this.activeName= '1'
          return ;
        }
        if(!this.$refs.oaPrNewFormForSupplier.checkForm()){
          this.activeName= '2'
          return ;
        }
        this.activeName= '0'
        this.$refs.oaPrNewForm.inputForm.supplierInfo=
          JSON.stringify(this.$refs.oaPrNewFormForSupplier.supplierInfo)

        this.$refs.oaPrNewForm.inputForm.supplementaryDoc=
          JSON.stringify(this.$refs.oaPrNewFormForDoc.supplementaryDoc)

        this.$refs.oaPrNewForm.saveForm((businessTable, businessId) => {
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
