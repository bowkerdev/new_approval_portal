<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 25px;">
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
          <el-form-item label="请求者" prop="requester" :rules="[]">
            <user-select :limit='1' :value="inputForm.requester" :disabled='true'  @getValue='(value) => {inputForm.requester=value}'>
            </user-select>
          </el-form-item>
        </el-col>

        <el-col :span="12">
          <el-form-item label="请求者部门" prop="requesterDepartment.id" :rules="[ ]">
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
      <br/>
      <el-row :gutter="0">
        <p style="float: left;" class="sub-title">
          供应商报价和合同上传
        </p><div style="float: left;"><el-button size="small" @click="addTabListGroup()" type="primary" icon="el-icon-plus" style="float: left;margin-top: 7px;margin-left: 10px;padding: 5px 30px;" ></el-button></div>
        <div style="width: 100%;overflow: auto; border:1px solid #EBEEF5">
          <table style="min-width: 100%;border-collapse: collapse; border:1px solid #EBEEF5" class="supplierTable"
          cellspacing="0" bordercolor="#EBEEF5" bgcolor="#fff" >
            <thead>
              <tr class="head-background-color head1-height">
              <th>供应商名称</th><th>付款条件</th><th>币种</th><th>原价</th><th>折扣价</th><th>最终报价</th><th>预计到货日期</th>
              <th>预计最晚到货日期</th><th>备注</th><th>文件类型</th><th colspan="3">附件</th><th>关联项目</th><th>上传者</th><th>上传日期</th>
              <th colspan="2">操作</th>
              </tr>
            </thead>
            <tbody v-for="(item, index) in supplierInfo" :key="'index_'+index">
              <tr class="data-content" style="background-color: #fff3cf;">
              <td :rowspan="item.docListSize" class="width-150">
                <el-input  size="small" v-if="item.edit" v-model="item.supplierName"  ></el-input>
                <span v-else>
                  {{item.supplierName}}
                </span>
              </td>
              <td :rowspan="item.docListSize" class="width-100">
                <el-input  size="small" v-if="item.edit" v-model="item.paymentTerms"  ></el-input>
                <span v-else>
                  {{item.paymentTerms}}
                </span>
              </td>
              <td :rowspan="item.docListSize" class="width-100">
                <el-select  size="small" v-model="item.currency" v-if="item.edit" placeholder="请选择">
                  <el-option v-for="item in $dictUtils.getDictList('pr_currency')" :key="item.value" :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
                <span v-else>
                  {{$dictUtils.getDictLabel("pr_currency",item.currency, '-')}}
                </span>
              </td>
              <td :rowspan="item.docListSize" class="width-100">
                  {{item.originalPrice}}
              </td>
              <td :rowspan="item.docListSize" class="width-100">
                  {{item.discountedAmount}}
              </td>
              <td :rowspan="item.docListSize" class="width-100">
                  {{item.totalOfferedPrice}}
              </td>
              <td :rowspan="item.docListSize" class="width-150">
                  {{item.expectArrivalDate}}
              </td>
              <td :rowspan="item.docListSize" class="width-150">
                  {{item.expectLastArrivalDate}}
              </td>
              <td :rowspan="item.docListSize" class="width-200">
                <el-input  size="small" v-if="item.edit" v-model="item.remarks"  ></el-input>
                <span v-else>
                  {{item.remarks}}
                </span>
              </td>

              <td class="width-100">
                <el-select  size="small" v-model="item.docList[0].documentType" v-if="item.edit" placeholder="请选择">
                  <el-option v-for="item in $dictUtils.getDictList('pr_document_type')" :key="item.value" :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
                <span v-else>
                  {{$dictUtils.getDictLabel("pr_document_type",item.docList[0].documentType, '-')}}
                </span>
              </td>
              <td colspan="3" class="width-400">
                <el-upload :class="item.docList[0].attachment==''?'':'hide'"
                  :action="`${$http.BASE_URL}/sys/file/webupload/oss/upload?uploadPath=flow/pr`"
                      :headers="{token: $cookie.get('token')}"
                      :on-preview="(file, fileList) => {$window.location.href = (file.response && file.response.url) || file.url}"
                      :on-success="(response, file, fileList) => {
                         item.docList[0].attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                      }"
                      :on-remove="(file, fileList) => {
                        item.docList[0].attachment =''
                      }"
                      :before-remove="(file, fileList) => {
                        return $confirm(`确定移除 ${file.name}？`)
                      }"
                      :limit="1"
                      :on-exceed="(files, fileList) =>{
                        $message.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
                      }"
                      :file-list="attachmentsArra[item.id][item.docList[0].id]">
                      <el-button :disabled="!item.edit" style="padding: 5px 30px;" size="small" type="primary" >上传</el-button>
                    </el-upload>
              </td>
              <td class="width-100">
                <el-select  size="small" multiple v-model="item.docList[0].linkToItems" v-if="item.edit" placeholder="请选择">
                  <el-option v-for="item in detailInfo" :key="item.serialNumber" :label="item.serialNumber"
                    :value="item.serialNumber">
                  </el-option>
                </el-select>
                <span v-else>
                  {{item.docList[0].linkToItems.join(',')}}
                </span>
              </td>
              <td class="width-100">
                  {{item.docList[0].uploadedBy}}
              </td>
              <td class="width-100">
                  {{item.docList[0].uploadedDate}}
              </td>
              <td class="width-50">
                <el-button v-if="item.edit" :disabled="item.docList.length ==1" type="danger" size="small" icon="el-icon-delete" @click="delDoc(index,0)" class="operationButton"></el-button>
              </td>
              <td :rowspan="item.docListSize" class="width-100">
                <el-button v-if="item.edit" type="success" size="small" icon="el-icon-check" @click="confirmTabListGroup(index)" class="operationButton"></el-button>
                <el-button v-if="!item.edit" type="primary" size="small" icon="el-icon-edit" @click="changeTabListGroup(index)" class="operationButton"></el-button>
                <el-button v-if="item.edit" type="danger" size="small" icon="el-icon-delete" @click="delTabListGroup(index)" class="operationButton"></el-button>
              </td>
              </tr>
<!-- 第一行  -->
              <tr class="data-content"  style="background-color: #fff3cf;" v-for="(item2, index2) in item.docList.slice(1)" :key="'index2_'+index2"  >
                <td class="width-100">
                    <el-select  size="small" v-model="item2.documentType" v-if="item.edit" placeholder="请选择">
                      <el-option v-for="item in $dictUtils.getDictList('pr_document_type')" :key="item.value" :label="item.label"
                        :value="item.value">
                      </el-option>
                    </el-select>
                    <span v-else>
                      {{$dictUtils.getDictLabel("pr_document_type",item2.documentType, '-')}}
                    </span>
                </td>
                <td colspan="3" class="width-400">
                  <el-upload :class="item2.attachment==''?'':'hide'"
                    :action="`${$http.BASE_URL}/sys/file/webupload/oss/upload?uploadPath=flow/pr`"
                        :headers="{token: $cookie.get('token')}"
                        :on-preview="(file, fileList) => {$window.location.href = (file.response && file.response.url) || file.url}"
                        :on-success="(response, file, fileList) => {
                           item2.attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                        }"
                        :on-remove="(file, fileList) => {
                          item2.attachment =''
                        }"
                        :before-remove="(file, fileList) => {
                          return $confirm(`确定移除 ${file.name}？`)
                        }"
                        :limit="1"
                        :on-exceed="(files, fileList) =>{
                          $message.warning(`当前限制选择 1 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`)
                        }"
                        :file-list="attachmentsArra[item.id][item2.id]">
                        <el-button :disabled="!item.edit" style="padding: 5px 30px;"  size="small" type="primary" >上传</el-button>
                      </el-upload>
                </td>
                <td class="width-100">
                  <el-select  multiple size="small" v-model="item2.linkToItems" v-if="item.edit" placeholder="请选择">
                    <el-option v-for="item in detailInfo" :key="item.serialNumber" :label="item.serialNumber"
                      :value="item.serialNumber">
                    </el-option>
                  </el-select>
                  <span v-else>
                    {{item2.linkToItems.join(',')}}
                  </span>
                </td>
                <td class="width-100">
                    {{item2.uploadedBy}}
                </td>
                <td class="width-100">
                    {{item2.uploadedDate}}
                </td>
                <td class="width-50">
                  <el-button v-if="item.edit" type="danger" size="small" icon="el-icon-delete" @click="delDoc(index,index2)" class="operationButton"></el-button>
                </td>
              </tr>
              <tr style="background-color: #fff3cf;">
                <td colspan="8">
                 <el-button size="small" :disabled="!item.edit"  @click="addDocList(index)" type="primary" icon="el-icon-plus" style="float: left;margin-left: 10px;padding: 5px 30px;" ></el-button>
                </td>
              </tr>
              <tr class="head-background-color head2-height">
                <td style="background-color: #FFFFFF;border:none"></td>
                <td class="first-td"><!-- 编号</td><td> -->项目</td><td>品牌名称</td><td>型号</td><td>提供单价</td><td>折扣单价</td>
                <td>MOQ</td><td>预计到货日期</td><td>预计最晚到货日期</td><td>采纳</td><td colspan="5">原因</td>
              </tr>
              <tr class="data-content" v-for="(item3, index3) in item.detailInfo" :key="'index3_'+index3">
                <td  style="background-color: #FFFFFF;border:none"></td>
                <td class="first-td"><!-- {{item3.serialNumber}}</td>
                <td> -->{{item3.item}} </td>
                <td> {{item3.brandName}} </td>
                <td> {{item3.modelNo}} </td>
                <td>
                  <el-input  v-on:input="calculationPrice(index)" v-only-num.float="item3"  size="small" v-if="item.edit" v-model="item3.offeredUnitPrice"  ></el-input>
                  <span v-else>
                    {{item3.offeredUnitPrice}}
                  </span>
                </td>
                <td>
                  <el-input  v-on:input="calculationPrice(index)" v-only-num.float="item3"  size="small" v-if="item.edit" v-model="item3.discountedUnitPrice"  ></el-input>
                  <span v-else>
                    {{item3.discountedUnitPrice}}
                  </span>
                </td>
                <td>
                  <el-input v-on:input="calculationPrice(index)"  v-only-num="item3"  size="small" v-if="item.edit" v-model="item3.moq" ></el-input>
                  <span v-else>
                    {{item3.moq}}
                  </span>
                </td>
                <td class="width-100">
                  <el-date-picker  size="small"  v-if="item.edit"  v-model="item3.expectArrivalDate" type="date"
                    value-format="yyyy-MM-dd" placeholder="选择日期时间">
                  </el-date-picker>
                  <span v-else>
                    {{item3.expectArrivalDate}}
                  </span>
                </td>
                <td class="width-100" >
                  <el-date-picker  size="small" class="width-200" v-if="item.edit"  v-model="item3.expectLastArrivalDate" type="date"
                    value-format="yyyy-MM-dd" placeholder="选择日期时间">
                  </el-date-picker>
                  <span v-else>
                    {{item3.expectLastArrivalDate}}
                  </span>
                </td>
                <td >
                  <el-checkbox @change="awardedChange(item,item3.serialNumber)" :disabled="item.edit"  v-model="item3.awarded" ></el-checkbox>
                </td>
                <td colspan="5">
                  <el-input  size="small" v-if="item.edit" v-model="item3.reason"  ></el-input>
                  <span v-else>
                    {{item3.reason}}
                  </span>
                </td>
              </tr>
              <tr>
                <td colspan="18" style="height: 1px;background-color:#EBEEF5"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </el-row>
      <br/>
      <el-row :gutter="0">
      <p class="sub-title">
        中标供应商和更新的单价详细信息
      </p>
      <div style="width: 100%;overflow: auto;" >
        <table style="min-width: 100%;border-collapse: collapse; border:1px solid #EBEEF5" class="supplierTable"
         cellspacing="0" bordercolor="#EBEEF5" bgcolor="#fff">
          <thead>
            <tr class="head-background-color">
              <th rowspan="2">编号</th><th rowspan="2">项目</th><th rowspan="2">品牌名称</th><th rowspan="2">型号</th>
              <th rowspan="2">市场价格</th><th rowspan="2">更新单价</th><th rowspan="2">请求数量</th><th rowspan="2">UOM</th>
              <th colspan="2">文档报价</th><th  colspan="2">基础报价</th>
            </tr>
            <tr class="head-background-color">
              <th>金额</th><th>金额(增值税)</th><th>金额</th><th>金额(增值税)</th>
            </tr>
          </thead>
          <tbody v-for="(item, index) in detailInfo">
            <tr class="data-content" style="background-color: #fff3cf;">
              <td>{{item.serialNumber}}</td>
              <td>{{item.item}}</td>
              <td>{{item.brandName}}</td>
              <td>{{item.modelNo}}</td>
              <td>{{item.unitPrice}}</td>
              <td>{{item.docUnitPrice}}</td>
              <td>{{item.quantity}}</td>
              <td>{{item.uom}}</td>

              <td>{{item.docAmount}}</td>
              <td>{{item.docVatAmount}}</td>
              <td>
                <span v-if="!isNaN(item.docAmount*inputForm.exRate)">
                  {{(item.docAmount*inputForm.exRate).toFixed(3)}}
                </span>
              </td>
              <td>
                <span v-if="!isNaN(item.docVatAmount*inputForm.exRate)">
                  {{(item.docVatAmount*inputForm.exRate).toFixed(3)}}
                </span>
              </td>
            </tr>
            <tr class="head-background-color head2-height">
              <td style="background-color: #FFFFFF;border:none"></td>
              <td class="first-td">供应商名称</td><td>付款条款</td><td>提供的货币</td><td>提供的单价</td><td>提供的单价（基础货币）</td>
              <td>MOQ</td><td>预计到货日期</td><td>预计最晚到货日期</td><td>相关文档</td><td>采纳</td><td>原因</td>
            </tr>
            <tr class="data-content" v-for="(item, index) in supplierInfoByDetailInfo[item.item]" >
              <td style="background-color: #FFFFFF;border:none"></td>
              <td class="first-td">{{item.supplierName}}</td>
              <td >{{item.paymentTerms}}</td>
              <td >{{item.currency}}</td>
              <td >{{item.offeredUnitPrice}}</td>
              <td >{{item.offeredBaseUnitPrice}}</td>
              <td >{{item.moq}}</td>
              <td >{{item.expectArrivalDate}}</td>
              <td >{{item.expectLastArrivalDate}}</td>
              <td >{{item.relatedQuotation}}</td>
              <td ><el-checkbox :disabled="true" v-model="item.awarded" ></el-checkbox></td>
              <td >{{item.reason}}</td>
            </tr>
          </tbody>
        </table>
        </div>
      </el-row>
      <br />
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
        supplierInfo:[],
        supplierInfoByDetailInfo:{},
        attachmentsArra:{},
        inputForm: {
          id: '',
          createBy:{id:""},
          createDate:"",
          createByOffice: {
            id:""
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
      //this.init({})
    },
    methods: {
      init(query) {
        if (query&&query.businessId) {
          Object.assign(this.$data, this.$options.data.call(this))
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
              if(!this.$common.isEmpty(this.inputForm.supplierInfo)){
                this.supplierInfo = JSON.parse(this.inputForm.supplierInfo)
                for(var i=0;i<this.supplierInfo.length;i++){
                  for(var j=0;j<this.supplierInfo[i].docList.length;j++){
                    if(this.supplierInfo[i].docList[j].attachment!=null){
                      if(this.supplierInfo[i].id ==null){
                        this.supplierInfo[i].id = this.$common.uuid();
                      }
                      if(this.supplierInfo[i].docList[j].id ==null){
                        this.supplierInfo[i].docList[j].id = this.$common.uuid();
                      }
                      var item=this.supplierInfo[i].docList[j].attachment
                      if(this.attachmentsArra[this.supplierInfo[i].id]==null){
                        this.attachmentsArra[this.supplierInfo[i].id]={}
                      }
                      if(this.attachmentsArra[this.supplierInfo[i].id][this.supplierInfo[i].docList[j].id]==null){
                        this.attachmentsArra[this.supplierInfo[i].id][this.supplierInfo[i].docList[j].id]=
                          [{name: decodeURIComponent(item.substring(item.lastIndexOf('/') + 1)), url: item}]
                      }
                    }
                  }
                }
              }
              this._getSupplierInfoByDetailInfoList()
              this._getSupplierArrivalDate()
              this._updateDetailInfoDocUnitPrice()
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
        if(this.supplierInfo.length ==0){
           this.$message.warning($i18nMy.t('供应商列表不能为空'))
           return ;
        }
        for(var i=0;i<this.supplierInfo.length;i++){
          if(this.supplierInfo[i].edit){
            this.$message.warning($i18nMy.t('供应商列表还有未保存的记录'))
            return ;
          }
        }
        this.inputForm.detailInfo=JSON.stringify(this.detailInfo)
        this.inputForm.supplierInfo=JSON.stringify(this.supplierInfo)

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
      addTabListGroup(){
        var obj={attachment:'',uploadedBy:this.$store.state.user.loginName,uploadedDate:this.$common.formatDate(new Date())}
        var uuid=this.$common.uuid();
        this.attachmentsArra[uuid]={}
        this.supplierInfo.push({id: uuid, edit:true,docListSize:2,docList:[obj],detailInfo:JSON.parse(JSON.stringify(this.detailInfo))})
      },
      addDocList(index){
        var obj={attachment:'',uploadedBy:this.$store.state.user.loginName,uploadedDate:this.$common.formatDate(new Date())}
        var id=this.supplierInfo[index].id
        var uuid=this.$common.uuid();
        obj.id=uuid
        this.attachmentsArra[id][uuid]=[]
        this.supplierInfo[index].docList.push(obj)
        this.supplierInfo[index].docListSize=this.supplierInfo[index].docList.length+1
      },
      delDoc(index,index2){
        this.supplierInfo[index].docList.splice(index2, 1)
        this.supplierInfo[index].docListSize=this.supplierInfo[index].docList.length+1
      },

      _getSupplierInfoByDetailInfo(supplierInfo){
        for(var i=0;i<supplierInfo.detailInfo.length;i++){
          var item=supplierInfo.detailInfo[i].item
          if(this.supplierInfoByDetailInfo[item]==null){
            this.supplierInfoByDetailInfo[item]=[]
          }
          var obj=this.$common.find(this.supplierInfoByDetailInfo[item],
            function(e){return e.supplierName==supplierInfo.supplierName})
          if(obj==null){
            obj={}
            this.supplierInfoByDetailInfo[item].push(obj)
          }
          obj.supplierName =supplierInfo.supplierName
          obj.paymentTerms =supplierInfo.paymentTerms
          obj.currency =supplierInfo.currency
          obj.offeredUnitPrice =supplierInfo.detailInfo[i].offeredUnitPrice
          obj.offeredBaseUnitPrice =supplierInfo.detailInfo[i].offeredUnitPrice
          obj.moq =supplierInfo.detailInfo[i].moq
          obj.expectArrivalDate =supplierInfo.detailInfo[i].expectArrivalDate
          obj.expectLastArrivalDate =supplierInfo.detailInfo[i].expectLastArrivalDate
          obj.awarded =supplierInfo.detailInfo[i].awarded
          obj.reason =supplierInfo.detailInfo[i].reason
          var fileList=[]
          for(var j=0;j<supplierInfo.docList.length;j++){
            if(supplierInfo.docList[j].linkToItems.indexOf(supplierInfo.detailInfo[i].serialNumber)>=0){
              var pathIndex=supplierInfo.docList[j].attachment.lastIndexOf("/")+1
              var fileName=supplierInfo.docList[j].attachment.substr(pathIndex)
              fileList.push(fileName)
            }
          }
          obj.relatedQuotation =fileList.join()
        }
      },
      _getSupplierInfoByDetailInfoList(){
        this.supplierInfoByDetailInfo={}
        for(var x=0;x<this.supplierInfo.length;x++){
          if(!this.supplierInfo[x].edit){
            this._getSupplierInfoByDetailInfo(this.supplierInfo[x])
          }
        }
        this.detailInfo.splice(this.detailInfo.length)
      },
      _updateDetailInfoDocUnitPrice(){
        debugger
        for(var i=0;i<this.detailInfo.length;i++){
          var obj=this.detailInfo[i]
          obj.docUnitPrice = 0
          obj.docAmount  =0
        }
        for(var i=0;i<this.supplierInfo.length;i++){
          if(!this.supplierInfo[i].edit){
            for(var j=0;j<this.supplierInfo[i].detailInfo.length;j++){
              if(this.supplierInfo[i].detailInfo[j].awarded){
                var serialNumber = this.supplierInfo[i].detailInfo[j].serialNumber
                var obj=this.$common.find(this.detailInfo,
                   function(e){return e.serialNumber== serialNumber})
                 obj.docUnitPrice = this.supplierInfo[i].detailInfo[j].offeredUnitPrice
                 obj.docAmount = this.supplierInfo[i].detailInfo[j].offeredUnitPrice*
                  parseInt(this.supplierInfo[i].detailInfo[j].moq||"0")
              }
            }
          }
        }
      },
      awardedChange(supplierInfo,itemSerialNumber){
        for(var i=0;i<this.supplierInfo.length;i++){
          if(supplierInfo!=this.supplierInfo[i]){
            for(var j=0;j<this.supplierInfo[i].detailInfo.length;j++){
              if(this.supplierInfo[i].detailInfo[j].serialNumber == itemSerialNumber &&
                 this.supplierInfo[i].detailInfo[j].awarded){
                this.supplierInfo[i].detailInfo[j].awarded=false
              }
            }
          }
        }
        this._getSupplierInfoByDetailInfoList()
        this._updateDetailInfoDocUnitPrice()
      },
      calculationPrice(index){
        var totalOfferedPrice =0
        for(var i=0;i<this.supplierInfo[index].detailInfo.length;i++){
          totalOfferedPrice+=parseInt(this.supplierInfo[index].detailInfo[i].offeredUnitPrice||"0")*
            parseInt(this.supplierInfo[index].detailInfo[i].moq||"0")
        }
        this.supplierInfo[index].originalPrice=totalOfferedPrice

        var discountedAmount =0
        for(var i=0;i<this.supplierInfo[index].detailInfo.length;i++){
          discountedAmount+=parseInt(this.supplierInfo[index].detailInfo[i].discountedUnitPrice||"0")*
            parseInt(this.supplierInfo[index].detailInfo[i].moq||"0")
        }
        this.supplierInfo[index].discountedAmount=discountedAmount

        if(this.supplierInfo[index].discountedAmount !=null && this.supplierInfo[index].discountedAmount !=0){
           this.supplierInfo[index].totalOfferedPrice = this.supplierInfo[index].discountedAmount
        }
        else{
          this.supplierInfo[index].totalOfferedPrice = this.supplierInfo[index].originalPrice
        }
      },
      _getSupplierArrivalDate(){
        debugger
        for(var h=0;h<this.supplierInfo.length;h++){
          var supplierInfo=this.supplierInfo[h]
          if(!supplierInfo.edit){
            var expectArrivalDate ='';
            var expectLastArrivalDate ='';
            for(var i=0;i<supplierInfo.detailInfo.length;i++){
              if(!this.$common.isEmpty(supplierInfo.detailInfo[i].expectArrivalDate)){
                if(this.$common.isEmpty(expectArrivalDate) ||
                expectArrivalDate.localeCompare(supplierInfo.detailInfo[i].expectArrivalDate)>0){
                  expectArrivalDate = supplierInfo.detailInfo[i].expectArrivalDate
                }
              }
              if(!this.$common.isEmpty(supplierInfo.detailInfo[i].expectLastArrivalDate)){
                if(this.$common.isEmpty(expectLastArrivalDate) ||
                expectLastArrivalDate.localeCompare(supplierInfo.detailInfo[i].expectLastArrivalDate)<0){
                  expectLastArrivalDate = supplierInfo.detailInfo[i].expectLastArrivalDate
                }
              }
            }
          }
          supplierInfo.expectArrivalDate=expectArrivalDate
          supplierInfo.expectLastArrivalDate=expectLastArrivalDate
        }
      },
      confirmTabListGroup(index){
        if(this.$common.isEmpty(this.supplierInfo[index].supplierName)){
           this.$message.warning($i18nMy.t('供应商不能为空'))
        }
        else{
          this.supplierInfo[index].edit =false
          this._getSupplierInfoByDetailInfoList()
          this._updateDetailInfoDocUnitPrice()
          this._getSupplierArrivalDate()
          this.detailInfo.splice(this.detailInfo.length) // 触发更新
          this.supplierInfo.splice(this.supplierInfo.length)
        }
      },
      delTabListGroup(index){
        this.supplierInfo.splice(index, 1)
        this._getSupplierInfoByDetailInfoList()
        this._updateDetailInfoDocUnitPrice()
        this.detailInfo.splice(this.detailInfo.length)
      },
      changeTabListGroup(index){
        this.supplierInfo[index].edit =true
        this.inputForm.detailInfo=JSON.stringify(this.detailInfo)
        this.inputForm.supplierInfo=JSON.stringify(this.supplierInfo)
        console.log(JSON.stringify(this.inputForm))
      }
    }
  }
</script>
<style scoped lang = "less">
  .el-form-item {
      margin-bottom: 10px;
  }
  .width-150{
    min-width: 120px;
    width : 120px;
    text-align: center;
  }
  .width-100{
    min-width: 90px;
    width : 90px;
    text-align: center;
  }
  .width-200{
    min-width: 180px;
    width : 180px;
    text-align: center;
  }
  .width-400{
    min-width: 300px;
    width : 300px;
  }
  .width-50{
    min-width: 50px;
    width : 50px;
    text-align: center;
  }
  .supplierTable .operationButton{
        padding: 2px 2px !important;
  }
  .supplierTable th{
     height: 35px;
    font-size: 12px;
    color: #909399;
  }

  .head-background-color{
    background-color: #f6f7fa;
  }
  .head-background-color td,
  .head-background-color th{
    border-right: 1px solid #EBEEF5;
    border-bottom: 1px solid #EBEEF5;
    font-size: 12px;
    color: #909399;
  }

  .first-td{
    border-left: 1px solid #EBEEF5;
  }

  .sub-title{
    text-align: left;
    margin: 10px 0px 10px 0px;
    font-size: 16px;
    font-weight: bold;
    color: #6a6a6a;
  }

  .data-content{
      height: 35px;
  }
  .data-content td{
      font-size: 12px;
      color: #606266;
      border-right: 1px solid #EBEEF5;
      border-bottom: 1px solid #EBEEF5;
  }
  .head1-height{
    height: 40px;
  }
  .head2-height{
    height: 40px;
    text-align: center;
  }
  td{
    text-align: center;
  }
  .hide  ::v-deep .el-upload{
    display: none;
  }
  .hide ::v-deep .is-success{
    margin-top: 0px;
  }

</style>
