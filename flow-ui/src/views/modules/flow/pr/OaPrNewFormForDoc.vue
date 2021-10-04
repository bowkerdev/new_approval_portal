<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 25px; margin-left: 25px;">
    <el-form size="small" :model="inputForm" ref="inputForm" v-loading="loading" :disabled="formReadOnly"
      label-width="140px" style="width: calc(100% - 25px);">
      <el-row :gutter="15">
        <el-col :span="24">
          <p style="text-align: center;margin-top: 20px;font-size: 26px;font-weight: bold;">
            Win Hanverky Group
          </p>
          <p style="text-align: center;margin: 10px 0px 20px 0px;font-size: 16px;">
            {{$i18nMy.t('采购设备申请表（IT 设备）')}}<!-- Purchase Requisition Form -->
          </p>
        </el-col>
        <el-col :span="12">
            <el-form-item  label-width="220px" :label="$i18nMy.t('申请人')" prop="createBy.name" :rules="[]">
              <!-- <user-select :limit='1' :value="inputForm.createBy.id" :disabled='true' @getValue='(value) => {inputForm.createBy.id=value}'>
              </user-select> -->
              <el-input v-model="inputForm.createBy.name" :disabled='true' ></el-input>
            </el-form-item>
          </el-col>
          <el-col  :span="12">
            <el-form-item  label-width="220px" :label="$i18nMy.t('申请人部门')" prop="createByOffice.name"  :rules="[  ]">
              <el-input v-model="inputForm.createByOffice.name" :disabled='true' ></el-input>
             </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('用户姓名')" prop="requester" :rules="[]">
              <el-input v-model="inputForm.requester" :disabled='true' :placeholder="$i18nMy.t('请填写用户姓名')"></el-input>
              <!-- <user-select :limit='1' :value="inputForm.requester" @getValue='(value) => {inputForm.requester=value}'>
              </user-select> -->
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('用户部门')" prop="requesterDepartment.name" :rules="[ ]">
              <el-input v-model="inputForm.requesterDepartment.name" :disabled='true' ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('固定资产类型')" prop="assetGroup" :rules="[]">
              <el-select v-model="inputForm.assetGroup" :disabled='true' :placeholder="$i18nMy.t('请选择')" style="width: 100%;">
                <el-option v-for="item in $dictUtils.getDictList('asset_group')" :key="item.value" :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('成本中心')" prop="costCenter" :rules="[]">
              <el-select v-model="inputForm.costCenter" :disabled='true' :placeholder="$i18nMy.t('请选择')" style="width: 100%;">
                <el-option v-for="item in $dictUtils.getDictList('cost_center')" :key="item.value" :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      <el-row :gutter="15" style="margin-right: 10px">
        <el-table :data="detailInfo" height="300px" class="table" size="small" style="border: 1px solid #EBEEF5 !important ; margin-left: 10px;">
           <el-table-column prop="serialNumber" width="50" align="right" :label="$i18nMy.t('序号')"> </el-table-column>
           <el-table-column prop="item" align="left" :label="$i18nMy.t('物品')">
           </el-table-column>
           <el-table-column prop="brandName" align="left" :label="$i18nMy.t('品牌')">
           </el-table-column>
           <el-table-column prop="modelNo" align="left" :label="$i18nMy.t('型号')">
           </el-table-column>
           <el-table-column prop="unitPrice" width="100" align="right" :label="$i18nMy.t('单价')">
             <template slot-scope="{row}">
               {{$common.toThousands(row.unitPrice)}}
             </template>
           </el-table-column>
          <el-table-column prop="vat"  align="right" label="VAT%">
            <template slot-scope="{row}">
              <span  v-if="row.vat !=null"> {{ row.vat }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="vatUnitPrice"  width="120" align="right" :label="$i18nMy.t('市场价格')+'(VAT)'">
            <template slot-scope="{row}">
                {{ $common.toThousands(row.vatUnitPrice) }}
            </template>
          </el-table-column>
           <el-table-column prop="quantity" width="100" align="left" :label="$i18nMy.t('数量')">
             <template slot-scope="{row}">
               {{$common.toThousands(row.quantity)}}
             </template>
           </el-table-column>
           <!-- <el-table-column prop="uom" width="100"  align="left" :label="$i18nMy.t('单位')">
           </el-table-column> -->
           <el-table-column  align="left" :label="$i18nMy.t('文档报价')">
             <template>
               <el-table-column prop="docAmount" width="100"  align="left" :label="$i18nMy.t('总数')">
                 <template slot-scope="{row}">
                   {{$common.toThousands(row.docAmount)}}
                 </template>
               </el-table-column>
               <el-table-column prop="docVatAmount" width="100" align="left" :label="$i18nMy.t('总数')+'VAT'">
                 <template slot-scope="{row}">
                   {{$common.toThousands(row.docVatAmount)}}
                 </template>
               </el-table-column>
             </template>
           </el-table-column>
           <el-table-column align="left" :label="$i18nMy.t('基础报价')" >
             <template>
               <el-table-column prop="baseQuantity" width="100" align="left" :label="$i18nMy.t('总数')"   >
                <template slot-scope="{row}">
                  <span v-if="!isNaN(row.docAmount*row.exRate)">
                    {{$common.toThousands((row.docAmount*row.exRate).toFixed(2))}}
                  </span>
                </template>
               </el-table-column>
               <el-table-column width="100" prop="baseVatQuantity" align="left" :label="$i18nMy.t('总数')+'VAT'">
                  <template slot-scope="{row}">
                    <span  v-if="!isNaN(row.docVatAmount*row.exRate)">
                      {{$common.toThousands((row.docVatAmount*row.exRate).toFixed(2))}}
                    </span>
                  </template>
               </el-table-column>
             </template>
           </el-table-column>
         </el-table>
      </el-row>
      <el-row :gutter="0">
        <p style="float: left;text-align: left;margin: 20px 0px 5px 0px;font-size: 20px;font-weight: 500;">
          {{$i18nMy.t('补充文件')}}
        </p> <div style="float: left;">
        <el-button size="small" @click="addTabListGroup()" round type="primary" icon="el-icon-plus" style="float: left;margin-top: 20px;margin-left: 10px;padding: 5px 5px;" >
        </el-button></div>
      </el-row>
      <el-row :gutter="15" style="margin-right: 10px">
          <el-table :data="supplementaryDoc" height="300px" class="table" style="border: 1px solid #EBEEF5 !important ; margin-left: 10px">
            <el-table-column prop="serialNumber" width="50" align="right" :label="$i18nMy.t('序号')"> </el-table-column>
            <el-table-column prop="description" width="200" align="left" :label="$i18nMy.t('描述')">
              <template slot-scope="{row}">
                <template v-if="row.edit">
                  <el-input  size="small" v-model="row.description" :placeholder="$i18nMy.t('请输入内容')" ></el-input>
                </template>
                <span v-else>{{ row.description }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="documentType" width="120"  align="left" :label="$i18nMy.t('文件类型')"   >
              <template slot-scope="{row}">
                <template v-if="row.edit">
                  <el-select  size="small" v-model="row.documentType"  :placeholder="$i18nMy.t('请选择')">
                    <el-option v-for="item in $dictUtils.getDictList('pr_additional_document_type')" :key="item.value" :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </template>
                <span v-else>{{ row.documentType }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="attachment" align="left" :label="$i18nMy.t('附件')"   >
              <template slot-scope="{row}">
                <el-upload :class="row.attachment==''?'':'hide'"
                  :action="`${$http.BASE_URL}/sys/file/webupload/oss/upload?uploadPath=flow/pr`"
                      :headers="{token: $cookie.get('token')}"
                      :on-preview="(file, fileList) => {$window.open((file.response && file.response.url) || file.url)}"
                      :on-success="(response, file, fileList) => {
                         row.attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                      }"
                      :on-remove="(file, fileList) => {
                        row.attachment =''
                      }"
                      :before-remove="(file, fileList) => {
                        return $confirm(`${$i18nMy.t('确定移除')} ${file.name}?`)
                      }"
                      :limit="1"
                      :on-exceed="(files, fileList) =>{
                        $message.warning($common.stringFormat('当前限制选择 1 个文件，本次选择了 {0} 个文件，共选择了 {1} 个文件',files.length,files.length + fileList.length) )
                      }"
                      :file-list="attachmentsArra[row.id]">
                      <el-button :disabled="!row.edit" style="padding: 5px 30px;" round size="small" type="primary" >{{$i18nMy.t('上传')}}</el-button>
                    </el-upload>
              </template>
            </el-table-column>

            <el-table-column prop="uploaderDepartment" width="180" align="left" :label="$i18nMy.t('上传者部门')"   >
            </el-table-column>

            <el-table-column width="150" prop="uploadedBy"  align="left" :label="$i18nMy.t('上传者')"   >
            </el-table-column>

            <el-table-column prop="uploadedDate" width="180"  align="left" :label="$i18nMy.t('上传日期')"   >
            </el-table-column>

            <el-table-column width="120" align="left" :label="$i18nMy.t('操作')" class-name="td-operate">
              <template slot-scope="{row}">
                <el-button  type="danger" size="small" icon="el-icon-delete" @click="delTabListGroup(row)" style="float: right;margin-left: 5px;"></el-button>
                <el-button v-if="row.edit" type="success" size="small" icon="el-icon-check" @click="confirmTabListGroup(row)" style="float: right;margin-left: 5px;"></el-button>
                <el-button v-if="!row.edit" type="primary" size="small" icon="el-icon-edit" @click="changeTabListGroup(row)" style="float: right;margin-left: 5px;"></el-button>
              </template>
            </el-table-column>
          </el-table>
      </el-row>
    </el-form>
  </div>
</template>

<script>
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  import UserSelect from '@/components/userSelect'
  export default {
    data() {
      return {
        title: '',
        method: '',
        loading: false,
        isCopy: false,
        flowStage:'start',
        detailInfo:[],
        supplementaryDoc:[],
        attachmentsArra:{},
        inputForm: {
          id: '',
          createBy:{id:"", name:""},
          createDate:"",
          createByOffice: {
            id: '',
            name: ''
          },
          applicationNo: '',
          projectName: '',
          applySiteCode: '',
          requesterDepartment: {
            id: '',
            name: ''
          },
          requester: '',
          expenseType: '',
          expectArrivalDate: '',
          legalEntity: '',
          costCenter: '',
          assetGroup: '',
          technicalAdvisor: '',
          budgetType: '',
          approvedDate: '',
          isBudget: '',
          requestRiority: '',
          contractCurrency: '',
          exRate: '',
          vat:1,
          totalContractAmount: '',
          totalVatContractAmount:'',
          baseCurrency: '',
          totalBaseAmount: '',
          totalVatBaseAmount:'',
          purchasePurpose: '',
          roi: '',
          noBudgetExplain: '',
          paymentSpecial: '',
          detailInfo: '',
          supplementaryDoc: '',
          supplierInfo: ''
        }
      }
    },
    props: {
      formReadOnly: {
        type: Boolean,
        default: false
      }
    },
    components: {
      SelectTree,
      UserSelect
    },
    activated() {
      //this.init()
    },
    methods: {
      init(query) {
        //query = {"businessId":"b3a13937894c4eb5abc842386b24933c"}
        this.supplementaryDoc=[] 
        if (query&&query.businessId) {
          this.loading = true
          this.inputForm.id = (query.businessId).replace("__copy","")
          if (this.inputForm.id != query.businessId){ // copy
            this.isCopy = true
          }
          this.$nextTick(() => {
            this.$http({
              url: `/flow/pr/oaPrNew/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({
              data
            }) => {
              this.inputForm = this.recover(this.inputForm, data.oaPrNew)
              if (this.isCopy) {
                this.inputForm.id = ''
                this.inputForm.applicationNo = ''
              }
              this.detailInfo = JSON.parse(this.inputForm.detailInfo)
              if(!this.$common.isEmpty(this.inputForm.supplementaryDoc)){
                this.supplementaryDoc = JSON.parse(this.inputForm.supplementaryDoc)
                for(var i=0;i<this.supplementaryDoc.length;i++){
                  if(this.supplementaryDoc[i].id ==null){
                    this.supplementaryDoc[i].id = this.$common.uuid();
                  }
                  var item=this.supplementaryDoc[i].attachment
                  this.attachmentsArra[this.supplementaryDoc[i].id]=
                    [{name: decodeURIComponent(item.substring(item.lastIndexOf('/') + 1)), url: item}]
                }
              }
              this.loading = false
            })
          })
        }
      },
      checkForm(){
        for(var i=0;i<this.supplementaryDoc.length;i++){
          if(this.supplementaryDoc[i].edit){
            this.$message.warning($i18nMy.t('附件列表还有未保存的记录'))
            return ;
          }
        }
        return true
      },
      // 表单提交
      saveForm(callBack) {
        if(this.supplementaryDoc.length ==0){// 单独打开时，必须上传
           this.$message.warning($i18nMy.t('请上传文档'))
           return ;
        }
        if(!this.checkForm()){
          return
        }
        this.inputForm.detailInfo=JSON.stringify(this.detailInfo)
        this.inputForm.supplementaryDoc=JSON.stringify(this.supplementaryDoc)
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: `/flow/pr/oaPrNew/save`,
              method: 'post',
              data: this.inputForm
            }).then(({
              data
            }) => {
              this.loading = false
              if (data && data.success) {
                 callBack(data.businessTable, data.businessId)
              }
              else{
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      _sortDetailInfo(){
        for(var i=0;i<this.supplementaryDoc.length;i++){
          this.supplementaryDoc[i].serialNumber=i+1
        }
        this.supplementaryDoc=this.supplementaryDoc.slice()
      },
      addTabListGroup(){
        var uuid=this.$common.uuid()
        this.supplementaryDoc.push({
          id:uuid,
          edit:true,
          serialNumber:this.supplementaryDoc.length+1,
          uploaderDepartment:this.$store.state.user.office.name,
          uploadedDate:this.$common.formatTime(new Date()),
          uploadedBy:this.$store.state.user.name,
          attachment:''
        })
        attachmentsArra[uuid]=[]
      },
      confirmTabListGroup(row){
        if(this.$common.isEmpty(row.documentType)){
           this.$message.warning($i18nMy.t('文件类型不能为空'))
        }
        else if(this.$common.isEmpty(row.attachment)){
           this.$message.warning($i18nMy.t('文件不能为空'))
        }
        else{
          row.edit =false
        }
      },
      delTabListGroup(row){
        var index=this.supplementaryDoc.indexOf(row)
        if (index > -1) {
          this.supplementaryDoc.splice(index, 1)
        }
        this._sortDetailInfo()
      },
      changeTabListGroup(row){
        row.edit =true
      }
    }
  }
</script>
<style scoped lang = "less">
  .hide  ::v-deep .el-upload{
    display: none;
  }
  .hide ::v-deep .is-success{
    margin-top: 0px;
  }
  .el-form-item {
      margin-bottom: 10px;
  }
</style>
