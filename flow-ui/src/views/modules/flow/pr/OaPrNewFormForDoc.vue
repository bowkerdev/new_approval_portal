<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden;">
    <el-form size="small" :model="inputForm" ref="inputForm" v-loading="loading" :disabled="formReadOnly"
      label-width="140px" style="width: calc(100% - 25px);">
      <el-row :gutter="15">
        <el-col :span="24">
          <p style="text-align: center;margin: 20px;font-size: 20px;font-weight: 800;">
            Win Hanverky Group Purchase Requisition Form
          </p>
        </el-col>
        <el-col :span="12">
            <el-form-item  label="创建者" prop="requester" :rules="[]">
              <user-select :limit='1' :value="inputForm.createBy.id" :disabled='true' @getValue='(value) => {inputForm.createBy.id=value}'>
              </user-select>
            </el-form-item>
          </el-col>
          <el-col  :span="12">
            <el-form-item label="创建者部门" prop="createByOffice.id"  :rules="[  ]">
              <SelectTree ref="createByOffice" :disabled='true'
                :props="{value: 'id',label: 'name',  children: 'children'}"
                url="/sys/office/treeData?type=2"  :value="inputForm.createByOffice.id"
                :clearable="true"   :accordion="true"   @getValue="(value) => {inputForm.createByOffice.id=value}"/>
             </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户姓名" prop="requester" :rules="[]">
              <el-input v-model="inputForm.requester" :disabled='true' placeholder="请填写用户姓名"></el-input>
              <!-- <user-select :limit='1' :value="inputForm.requester" @getValue='(value) => {inputForm.requester=value}'>
              </user-select> -->
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="用户部门" prop="requesterDepartment.id" :rules="[ ]">
              <SelectTree ref="requesterDepartment" :disabled='true' :props="{value: 'id',label: 'name', children: 'children'}"
                url="/sys/office/treeData?type=2" :value="inputForm.requesterDepartment.id" :clearable="true"
                :accordion="true" @getValue="(value) => {inputForm.requesterDepartment.id=value}" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="固定资产类型" prop="assetGroup" :rules="[]">
              <el-select v-model="inputForm.assetGroup" :disabled='true' placeholder="请选择" style="width: 100%;">
                <el-option v-for="item in $dictUtils.getDictList('asset_group')" :key="item.value" :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成本中心" prop="costCenter" :rules="[]">
              <el-select v-model="inputForm.costCenter" :disabled='true' placeholder="请选择" style="width: 100%;">
                <el-option v-for="item in $dictUtils.getDictList('cost_center')" :key="item.value" :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      <el-row :gutter="15">
        <el-table :data="detailInfo" height="300px" class="table" size="small" border="none" style="border: 1px solid #EBEEF5 !important ; margin-left: 10px">
           <el-table-column prop="serialNumber" width="50" align="center" :label="$i18nMy.t('序号')"> </el-table-column>
           <el-table-column prop="item" width="200" align="center" :label="$i18nMy.t('物品')">
           </el-table-column>
           <el-table-column prop="brandName" width="200"  align="center" :label="$i18nMy.t('品牌')">
           </el-table-column>
           <el-table-column prop="modelNo" width="200" align="center" :label="$i18nMy.t('型号')">
           </el-table-column>
           <el-table-column prop="unitPrice" width="200" align="center" :label="$i18nMy.t('单价')">
           </el-table-column>
           <el-table-column prop="docUnitPrice" width="200" align="center" :label="$i18nMy.t('文件单价')">
           </el-table-column>
           <el-table-column prop="quantity" width="155" align="center" :label="$i18nMy.t('数量')">
           </el-table-column>
           <el-table-column prop="uom" width="100"  align="center" :label="$i18nMy.t('单位')">
           </el-table-column>
           <el-table-column  align="center" :label="$i18nMy.t('文档报价')">
             <template>
               <el-table-column prop="docQuantity" width="155"  align="center" :label="$i18nMy.t('数量')">
               </el-table-column>
               <el-table-column prop="docVatQuantity" width="155" align="center" :label="$i18nMy.t('数量')+'VAT'">
               </el-table-column>
             </template>
           </el-table-column>
           <el-table-column align="center" :label="$i18nMy.t('基础报价')" >
             <template>
               <el-table-column prop="baseQuantity" width="155" align="center" :label="$i18nMy.t('数量')"   >
               </el-table-column>
               <el-table-column width="155" prop="baseVatQuantity" align="center" :label="$i18nMy.t('数量')+'VAT'">
               </el-table-column>
             </template>
           </el-table-column>
         </el-table>
      </el-row>
      <el-row :gutter="0">
        <p style="text-align: left;margin: 10px 0px 10px 0px;font-size: 20px;font-weight: 500;">
          补充文件
        </p>
      </el-row>
      <el-row :gutter="15">
        <el-button size="small" @click="addTabListGroup()" type="primary" icon="el-icon-plus" style="float: left;margin-left: 10px" >
        </el-button>
      </el-row>
      <el-row :gutter="15">
          <el-table :data="supplementaryDoc" height="300px" class="table" style="border: 1px solid #EBEEF5 !important ; margin-left: 10px">
            <el-table-column prop="serialNumber" width="50" align="center" :label="$i18nMy.t('序号')"> </el-table-column>
            <el-table-column prop="description" width="200" align="center" :label="$i18nMy.t('描述')">
              <template slot-scope="{row}">
                <template v-if="row.edit">
                  <el-input  size="small" v-model="row.description" :placeholder="$i18nMy.t('请输入内容')" ></el-input>
                </template>
                <span v-else>{{ row.description }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="documentType" width="120"  align="center" :label="$i18nMy.t('文件类型')"   >
              <template slot-scope="{row}">
                <template v-if="row.edit">
                  <el-select  size="small" v-model="row.documentType"  placeholder="请选择">
                    <el-option v-for="item in $dictUtils.getDictList('pr_document_type')" :key="item.value" :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
                </template>
                <span v-else>{{ row.documentType }}</span>
              </template>
            </el-table-column>

            <el-table-column prop="attachment" width="250" align="center" :label="$i18nMy.t('附件')"   >
              <template slot-scope="{row}">
                <el-upload :class="row.attachment==''?'':'hide'"
                  :action="`${$http.BASE_URL}/sys/file/webupload/oss/upload?uploadPath=flow/pr`"
                      :headers="{token: $cookie.get('token')}"
                      :on-preview="(file, fileList) => {$window.location.href = (file.response && file.response.url) || file.url}"
                      :on-success="(response, file, fileList) => {
                         row.attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                      }"
                      :on-remove="(file, fileList) => {
                        row.attachment =''
                      }"
                      :before-remove="(file, fileList) => {
                        return $confirm(`确定移除 ${file.name}？`)
                      }"
                      :limit="1"
                      :on-exceed="(files, fileList) =>{
                        $message.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
                      }"
                      :file-list="attachmentsArra[row.id]">
                      <el-button :disabled="!row.edit" style="padding: 5px 30px;"  size="small" type="primary" >上传</el-button>
                    </el-upload>
              </template>
            </el-table-column>

            <el-table-column prop="uploaderDepartment"  align="center" :label="$i18nMy.t('上传者部门')"   >
            </el-table-column>

            <el-table-column width="120" prop="uploadedBy"  align="center" :label="$i18nMy.t('上传者')"   >
            </el-table-column>

            <el-table-column prop="uploadedDate"  align="center" :label="$i18nMy.t('上传日期')"   >
            </el-table-column>

            <el-table-column width="150" align="center" :label="$i18nMy.t('操作')" class-name="td-operate">
              <template slot-scope="{row}">
                <el-button v-if="row.edit" type="success" size="small" icon="el-icon-check" @click="confirmTabListGroup(row)" style="float: right;margin-left: 5px;"></el-button>
                <el-button v-if="!row.edit" type="primary" size="small" icon="el-icon-edit" @click="changeTabListGroup(row)" style="float: right;margin-left: 5px;"></el-button>
                <el-button v-if="!row.edit" type="danger" size="small" icon="el-icon-delete" @click="delTabListGroup(row)" style="float: right;margin-left: 5px;"></el-button>
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
        flowStage:'start',
        detailInfo:[],
        supplementaryDoc:[],
        attachmentsArra:{},
        inputForm: {
          id: '',
          createBy:{id:this.$store.state.user.id},
          createDate:this.$common.formatTime(new Date()),
          createByOffice: {
            id: this.$store.state.user.office.id
          },
          applicationNo: '',
          projectName: '',
          applySiteCode: '',
          requesterDepartment: {
            id: ''
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
          totalContractAmount: '',
          baseCurrency: '',
          totalBaseAmount: '',
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
        if (query&&query.businessId) {
          this.loading = true
          this.inputForm.id = query.businessId
          this.$nextTick(() => {
            this.$http({
              url: `/flow/pr/oaPrNew/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({
              data
            }) => {
              this.inputForm = this.recover(this.inputForm, data.oaPrNew)
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
        else{
          this.$message.warning($i18nMy.t('数据异常'))
        }
      },
      // 表单提交
      saveForm(callBack) {
        debugger
        if(this.supplementaryDoc.length ==0){
           this.$message.warning($i18nMy.t('请上传文档'))
           return ;
        }
        for(var i=0;i<this.supplementaryDoc.length;i++){
          if(this.supplementaryDoc[i].edit){
            this.$message.warning($i18nMy.t('附件列表还有未保存的记录'))
            return ;
          }
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
        debugger
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
