<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 25px; margin-left: 25px;">
    <el-form size="small" :model="inputForm" ref="inputForm" v-loading="loading" :disabled="formReadOnly"
      label-width="140px" style="width: calc(100% - 25px);">
      <el-row :gutter="15">
        <el-col :span="24">
          <p style="text-align: center;margin-top: 20px;font-size: 26px;font-weight: bold;">
            Win Hanverky Group
          </p>
          <p style="text-align: center;margin: 10px 0px 20px 0px;font-size: 16px;"  v-if="procDefKey === 'prpo' || procDefKey === 'pr'">
            {{$i18nMy.t('采购设备申请表（IT 设备）')}}<!-- Purchase Requisition Form -->
          </p>
          <p style="text-align: center;margin: 10px 0px 20px 0px;font-size: 16px;"  v-if="procDefKey === 'prpo_non_it'">
            {{$i18nMy.t('采购设备申请表（非IT设备）')}}<!-- Purchase Requisition Form -->
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
          <!-- <el-col :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('用户姓名')" prop="requester" :rules="[]">
              <el-input v-model="inputForm.requester" :disabled='true' :placeholder="$i18nMy.t('请填写用户姓名')"></el-input>
            </el-form-item>
          </el-col> -->

          <el-col :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('用户部门')" prop="requesterDepartment" :rules="[ ]">
              <el-input v-model="inputForm.requesterDepartment" :disabled='true' ></el-input>
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
              <template slot-scope="{row}">
                <span class="my-span">{{row.item}}</span>
              </template>
           </el-table-column>
           <el-table-column prop="brandName" align="left" :label="$i18nMy.t('品牌 - 型号')">
             <template slot-scope="{row}">
               <span class="my-span">{{row.brandName}} - {{row.modelNo}}</span>
             </template>
           </el-table-column>
           <!-- <el-table-column prop="modelNo" align="left" :label="$i18nMy.t('型号')">
           </el-table-column> -->
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
          <el-table-column prop="vatUnitPrice"  width="160" align="right" :label="$i18nMy.t('单价(含VAT)')">
            <template slot-scope="{row}">
                {{ $common.toThousands(row.vatUnitPrice) }}
            </template>
          </el-table-column>
           <el-table-column prop="quantity" width="120" align="left" :label="$i18nMy.t('数量')">
             <template slot-scope="{row}">
               {{$common.toThousands(row.quantity)}} - {{row.uom}}
             </template>
           </el-table-column>
           <!-- <el-table-column prop="uom" width="100"  align="left" :label="$i18nMy.t('单位')">
           </el-table-column> -->
           <el-table-column  align="center" :label="$i18nMy.t('报价单币种')">
             <template>
               <el-table-column prop="docAmount" width="100"  align="left" :label="$i18nMy.t('总数')" >
                 <template slot-scope="{row}" v-if="canViewTotalAmount()">
                   {{$common.toThousands(row.docAmount)}}
                 </template>
               </el-table-column>
               <el-table-column prop="docVatAmount" width="120" align="left" :label="$i18nMy.t('总数(VAT)')">
                 <template slot-scope="{row}" v-if="canViewTotalAmount()">
                   {{$common.toThousands(row.docVatAmount)}}
                 </template>
               </el-table-column>
             </template>
           </el-table-column>
           <el-table-column align="center" :label="$i18nMy.t('HKD')" >
             <template>
               <el-table-column prop="baseAmount" width="100" align="left" :label="$i18nMy.t('总数')"   >
                <template slot-scope="{row}" v-if="canViewTotalAmount()">
                  <span v-if="canViewTotalAmount() && !isNaN(row.docAmount*row.exRate)">
                    {{$common.toThousands((row.docAmount*row.exRate).toFixed(2))}}
                  </span>
                </template>
               </el-table-column>
               <el-table-column width="120" prop="baseVatAmount" align="left" :label="$i18nMy.t('总数(VAT)')">
                  <template slot-scope="{row}">
                    <span v-if="canViewTotalAmount() && !isNaN(row.docVatAmount*row.exRate)">
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
            <el-table-column prop="serialNumber" width="50" align="right" :label="$i18nMy.t('序号')">
              <template slot-scope="scope">
                {{scope.$index+1}}
              </template>
            </el-table-column>
            <!-- <el-table-column prop="documentType" width="220"  align="left" :label="$i18nMy.t('文件类型')"   >
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
            `${$http.BASE_URL}/sys/file/webupload/oss/upload?uploadPath=flow/pr` -->
            <el-table-column prop="attachment" align="left" :label="$i18nMy.t('附件')">
              <template slot-scope="{row}">
                <el-upload :class="row.attachment.split('|').length<5?'':'hide'" :disabled="!row.edit"
                    :action="`${$http.BASE_URL}/sys/file/webupload/oss/upload?uploadPath=flow/pr`"
                    :headers="{token: $cookie.get('token')}"
                    :on-preview="(file, fileList) => {$window.open((file.response && file.response.url) || file.url)}"
                    :on-success="(response, file, fileList) => {
                      row.attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                    }"
                    :on-remove="(file, fileList) => {
                      row.attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                    }"
                    :before-remove="(file, fileList) => {
                      return $confirm(`${$i18nMy.t('确定移除')} ${file.name}?`)
                    }"
                    multiple
                    :limit="5"
                    :on-exceed="(files, fileList) =>{
                      $message.warning(`${$i18nMy.t('当前限制选择 5 个文件')}`)
                    }"
                    :file-list="attachmentsArra[row.id]">
                    <el-button :disabled="!row.edit" style="padding: 5px 30px;" round size="small" type="primary" >{{$i18nMy.t('选择文件')}}</el-button>
                  </el-upload>

                <!-- <el-upload :class="row.attachment==''?'':'hide'"
                  action="#"
                      :headers="{token: $cookie.get('token')}"
                      :before-remove="beforeRemove"
                      :before-upload="beforeUpload"
                      :on-change="uploadChange"
                      :auto-upload="false"
                      :file-list="fileList"
                      multiple
                      >
                      <el-button :disabled="!row.edit" style="padding: 5px 30px;" round size="small" type="primary" >{{$i18nMy.t('选择文件')}}</el-button>
                    </el-upload>
                    <el-button :disabled="!row.edit" type="primary" style="padding: 5px 30px;" round size="small"  @click="submit">{{$i18nMy.t('上传')}}</el-button> -->
              </template>
            </el-table-column>
            <el-table-column prop="description" align="left" :label="$i18nMy.t('文件描述')">
              <template slot-scope="{row}">
                <template v-if="row.edit">
                  <el-input type="textarea" size="small" v-model="row.description" maxlength="300" :placeholder="$i18nMy.t('长度不超过300')" ></el-input>
                </template>
                <span v-else class="my-span">{{ row.description }}</span>
              </template>
            </el-table-column>
            <!-- <el-table-column prop="uploaderDepartment" width="180" align="left" :label="$i18nMy.t('上传者部门')"   >
            </el-table-column> -->

            <el-table-column width="150" prop="uploadedBy"  align="left" :label="$i18nMy.t('上传者')"   >
            </el-table-column>

            <el-table-column prop="uploadedDate" width="180"  align="left" :label="$i18nMy.t('上传日期')"   >
            </el-table-column>

            <el-table-column width="140" align="left" :label="$i18nMy.t('操作')" class-name="td-operate">
              <template slot-scope="{row}" v-if="row.uploadedBy==$store.state.user.name && row.uploadedDate>oldDataMaxDate">
                <el-button type="danger" size="small" icon="el-icon-delete" @click="delTabListGroup(row)" style="float: right;margin-left: 5px;"></el-button>
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
        topPage: null,
        title: '',
        method: '',
        loading: false,
        procDefKey: 'prpo',
        taskDefKey: '',
        processStatus: '',
        isCopy: false,
        flowStage:'start',
        detailInfo:[],
        supplementaryDoc:[],
        oldDataMaxDate: '',
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
          requesterDepartment: '',
          /* requesterDepartment: {
            id: '',
            name: ''
          }, */
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
          remarks: '',
          noBudgetExplain: '',
          paymentSpecial: '',
          detailInfo: '',
          supplementaryDoc: '',
          supplierInfo: '',
          tmpOldRow: null
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
      init(query, topPage, data) {
        this.supplementaryDoc=[]
        if (query.processStatus) {
          this.processStatus = query.processStatus
        }
        this.procDefKey = query.procDefKey
        this.taskDefKey = query.taskDefKey + ''
        this.topPage = topPage

        this.oldDataMaxDate = this.$common.formatTime(data.oaPrNew.beginCreateDate)
        //this.oldDataMaxDate = this.$common.formatTime(topPage.oldDataMaxDate)
        if (query&&query.businessId) {
          this.loading = true
          this.inputForm.id = (query.businessId+"").replace("__copy","")
          if (this.inputForm.id != query.businessId){ // copy
            this.isCopy = true
          }
          /* this.$nextTick(() => {
            this.$http({
              url: `/flow/pr/oaPrNew/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({
              data
            }) => { */
              this.inputForm = this.recover(this.inputForm, data.oaPrNew)
              if (topPage && data.oaPrNew.remarks && data.oaPrNew.remarks.indexOf(query.taskDefKey+'##')==0) {
                topPage.auditForm.message = (data.oaPrNew.remarks).replace(query.taskDefKey+'##','')
              }
              if (this.isCopy) {
                this.inputForm.id = ''
                this.inputForm.applicationNo = ''
              }
              this.detailInfo = JSON.parse(this.inputForm.detailInfo)

              if(!this.$common.isEmpty(this.inputForm.supplementaryDoc)){
                this.supplementaryDoc = JSON.parse(this.inputForm.supplementaryDoc)
                this.supplementaryDoc.sort(function(x, y){
                    return y.uploadedDate.localeCompare(x.uploadedDate)
                })

                for(var i=0;i<this.supplementaryDoc.length;i++){
                  if(this.supplementaryDoc[i].id ==null){
                    this.supplementaryDoc[i].id = this.$common.uuid()
                  }

                  this.attachmentsArra[this.supplementaryDoc[i].id] = []
                  let arr = this.supplementaryDoc[i].attachment.split("|")

                  for (var j=0; j<arr.length; j++) {
                    var item=arr[j] //this.supplementaryDoc[i].attachment
                    console.log(item)
                    this.attachmentsArra[this.supplementaryDoc[i].id].push({name: decodeURIComponent(item.substring(item.lastIndexOf('/') + 1)), url: item})
                  }
                }
              }
              this.loading = false
            /* })
          }) */
        }

      },

      canViewTotalAmount(){
        return (this.topPage && !this.topPage.isAfterL13) || this.processStatus=='结束' || this.hasPermission('flow:pr:geL13') || (this.hasPermission('flow:pr:L11-12') && this.topPage && this.topPage.$refs.form.getTotalAmount && this.topPage.$refs.form.getTotalAmount() < 10000)
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
        /* if(this.supplementaryDoc.length ==0){// 单独打开时，必须上传
           this.$message.warning($i18nMy.t('请上传文档'))
           return ;
        } */
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
                this.$message.success(data.msg)
                callBack(data.businessTable, data.businessId)
              }
              else{
                this.$message.error(data.msg)
              }
            })
          }
        })
      },
      saveAsDraft(callBack) {
        /* if(this.supplementaryDoc.length ==0){// 单独打开时，必须上传
           this.$message.warning($i18nMy.t('请上传文档'))
           return ;
        } */
        if(!this.checkForm()){
          return
        }
        this.inputForm.detailInfo=JSON.stringify(this.detailInfo)
        this.inputForm.supplementaryDoc=JSON.stringify(this.supplementaryDoc)
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            if (this.topPage.auditForm.message) {
              this.inputForm.remarks = this.taskDefKey + '##' + this.topPage.auditForm.message
            }
            this.$http({
              url: `/flow/pr/oaPrNew/save`,
              method: 'post',
              data: this.inputForm
            }).then(({
              data
            }) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
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

      /* beforeUpload(file) {
            // 获取上传文件大小
            const imgSize = Number(file.size / 1024 / 1024);
            if (imgSize > 10) {
              this.$msgbox({
                title: '',
                message: '文件大小不能超过10MB，请重新上传。',
                type: 'warning',
              });
              return false;
            }
            return true;
      },
      beforeRemove(file, fileList) {
         const index = fileList.indexOf(file);
         this.delIndex = index;
         this.fileIds.splice(this.delIndex, 1);
      },
      uploadChange(file, fileList) { // 这一步一定要写
            this.fileList = fileList;
      },
      submit() {
        debugger
         if (this.fileList.length) { // 如果有上传文件
              const formData = new FormData();
              this.fileList.forEach((item) => {
                  formData.append('files', item.raw); // 此处一定是append file.raw，files作为参数，是后端定义需要传的字段
              });
              this.$api.uploadFiles(formData).then((res) => { // 调用上传接口
              });
           }
      }, */

      addTabListGroup(){
        if (!this.checkForm()) {
          return
        }
        var uuid=this.$common.uuid()
        this.supplementaryDoc.unshift({
          id:uuid,
          edit:true,
          serialNumber:this.supplementaryDoc.length+1,
          uploaderDepartment:this.$store.state.user.office.name,
          uploadedDate:this.$common.formatTime(new Date()),
          uploadedBy:this.$store.state.user.name,
          attachment:''
        })
        this.attachmentsArra[uuid]=[]
      },
      confirmTabListGroup(row){
        if(this.$common.isEmpty(row.attachment)){
           this.$message.warning($i18nMy.t('文件不能为空'))
        } else if(this.$common.isEmpty(row.description)){
           this.$message.warning($i18nMy.t('文件描述不能为空'))
        }
        else{
          row.edit =false
          if (this.tmpOldRow != JSON.stringify(row)) {
            row.uploadedDate = this.$common.formatTime(new Date())
            this.supplementaryDoc.sort(function(x, y){
                return y.uploadedDate.localeCompare(x.uploadedDate);
            });
            this.attachmentsArra[row.id] = []
            let arr = row.attachment.split("|")
            for (var j=0; j<arr.length; j++) {
              var item=arr[j]
              this.attachmentsArra[row.id].push({name: decodeURIComponent(item.substring(item.lastIndexOf('/') + 1)), url: item})
            }
          }
        }
      },
      delTabListGroup(row){
        var index=this.supplementaryDoc.indexOf(row)
        if (index > -1) {
          this.supplementaryDoc.splice(index, 1)
        }
        // this._sortDetailInfo()
      },
      changeTabListGroup(row){
        this.tmpOldRow = JSON.stringify(row)
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
  .my-span{
     white-space:pre-wrap;
     word-break:keep-all;
     word-wrap:anywhere;
     overflow-wrap: anywhere;
  }
</style>
