<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 36px;">
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
          <el-form-item label-width="220px" :label="$i18nMy.t('申请单号')" prop="applicationNo" :rules="[]">
            <el-input v-model="inputForm.applicationNo" :disabled='true' :placeholder="$i18nMy.t('系统自动生成')"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('申请人')" prop="createBy.name" :rules="[]">
            <el-input v-model="inputForm.createBy.name" :disabled='true' :placeholder="$i18nMy.t('申请人')"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('申请时间')" prop="createDate" :rules="[]">
            <el-input v-model="inputForm.createDate" :disabled='true' :placeholder="$i18nMy.t('申请时间')"></el-input>
          </el-form-item>
        </el-col>
	      <el-col  :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('申请人部门')" prop="createByOffice.name"
                :rules="[  ]">
                <el-input v-model="inputForm.createByOffice.name" :disabled='true' :placeholder="$i18nMy.t('申请人部门')"></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('项目描述')" prop="projectName" :rules="[{required: true, message:$i18nMy.t('项目描述不能为空'), trigger:'blur'}]">
            <el-input v-model="inputForm.projectName" :placeholder="$i18nMy.t('请填写项目描述')" maxlength="100"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('采购地区')" prop="applySiteCode" :rules="[{required: true, message:$i18nMy.t('采购地区不能为空'), trigger:'blur'}]">
            <el-select v-model="inputForm.applySiteCode" :placeholder="$i18nMy.t('请选择')" style="width: 100%;" @change="siteChange">
              <el-option v-for="item in $dictUtils.getDictList('apply_site_code')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('用户部门')" prop="requesterDepartment.id" :rules="[{required: true, message:$i18nMy.t('用户部门不能为空'), trigger:'blur'}]">
            <SelectTree ref="requesterDepartment" v-if="ifSiteChange" :props="{
                    value: 'id',             // ID字段名
                    label: 'name',         // 显示名称
                    children: 'children'    // 子级字段名
                  }" :url="`/sys/office/treeData?type=2&parentCode=${inputForm.applySiteCode}`" :value="inputForm.requesterDepartment.id" :clearable="true"
              :accordion="true" @getValue="(value, name) => {inputForm.requesterDepartment.id=value; inputForm.requesterDepartment.name=name}" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('用户姓名')" prop="requester" :rules="[{required: true, message:$i18nMy.t('请填写用户姓名'), trigger:'blur'}]">
            <el-input v-model="inputForm.requester" :placeholder="$i18nMy.t('请填写用户姓名')" maxlength="30"></el-input>
            <!-- <user-select :limit='1' :value="inputForm.requester" @getValue='(value) => {inputForm.requester=value}'>
            </user-select> -->
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('费用类型')" prop="expenseType" :rules="[{required: true, message:'费用类型不能为空', trigger:'blur'}
                 ]">
            <el-select v-model="inputForm.expenseType" :placeholder="$i18nMy.t('请选择')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictList('expense_type')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('要求到货时间')" prop="expectArrivalDate" :rules="[
                 ]">
            <el-date-picker v-model="inputForm.expectArrivalDate" type="date" style="width: 100%;"
              value-format="yyyy-MM-dd HH:mm:ss" :placeholder="$i18nMy.t('选择日期时间')">
            </el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('签约方公司')" prop="legalEntity" :rules="[{required: true, message:'签约方公司不能为空', trigger:'blur'}
                 ]">
            <el-select v-model="inputForm.legalEntity" :placeholder="$i18nMy.t('请选择')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictListWithKey('all_company')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('成本中心')" prop="costCenter" :rules="[{required: true, message:'成本中心不能为空', trigger:'blur'}
                 ]">
            <el-select v-model="inputForm.costCenter" :placeholder="$i18nMy.t('请选择')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictListWithKey('cost_center')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('固定资产类别')" prop="assetGroup" :rules="[{required: true, message:'固定资产类别不能为空', trigger:'blur'}
                 ]">
            <el-select v-model="inputForm.assetGroup" :placeholder="$i18nMy.t('请选择')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictListWithKey('asset_group')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('技术支持部门')" prop="technicalAdvisor" :rules="[ ]">
            <el-select v-model="inputForm.technicalAdvisor" :placeholder="$i18nMy.t('请选择')" style="width: 100%;" clearable>
             <el-option v-for="item in $dictUtils.getDictList('technical_advisor')" :key="item.value" :label="item.label"
               :value="item.value">
             </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('是否预算内')" prop="isBudget" :rules="[
                 ]">
            <el-radio-group v-model="inputForm.isBudget">
              <el-radio v-for="item in $dictUtils.getDictList('yes_no')" :label="item.value" :key="item.value">
                {{item.label}}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('申购优先级')" prop="requestRiority" :rules="[{required: true, message:'申购优先级不能为空', trigger:'blur'}
                 ]">
            <el-select v-model="inputForm.requestRiority" :placeholder="$i18nMy.t('请选择')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictList('request_priority')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <!-- <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('预算类型')" prop="budgetType" :rules="[]">
            <el-select v-model="inputForm.budgetType" :placeholder="$i18nMy.t('请选择')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictList('it_pr_budget')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col> -->
        <!-- <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('审批通过日期')" prop="approvedDate" :rules="[
                 ]">
            <el-date-picker v-model="inputForm.approvedDate" type="datetime" style="width: 100%;"
              value-format="yyyy-MM-dd HH:mm:ss" :placeholder="$i18nMy.t('选择日期时间')">
            </el-date-picker>
          </el-form-item>
        </el-col> -->

      </el-row>
      <el-row :gutter="15">
        <el-col :span="6">
          <el-form-item label-width="220px" :label="$i18nMy.t('合同币种')" prop="contractCurrency" :rules="[
                 ]">
             {{ $dictUtils.getDictLabel("pr_currency", inputForm.contractCurrency, '-') }}
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label-width="150px" :label="$i18nMy.t('汇率')" prop="exRate" :rules="[]">
            {{inputForm.exRate}}
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label-width="220px" :label="$i18nMy.t('合同总价')" prop="totalContractAmount" :rules="[]">
            <div class="myformText1" v-if="(inputForm.totalContractAmount||'')!=''">
              {{$common.toThousands(inputForm.totalContractAmount.toFixed(2))}}
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item label-width="10px" label=""  :rules="[]">
            <div class="myformText2" v-if="(inputForm.totalVatContractAmount||'')!=''">
              {{$common.toThousands(inputForm.totalVatContractAmount.toFixed(2))}}(VAT)
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item label-width="10px" label=""  :rules="[]">
            {{ $dictUtils.getDictLabel("pr_currency", inputForm.contractCurrency, '-') }}
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('基础币种')" prop="baseCurrency" :rules="[ ]">
            {{ $dictUtils.getDictLabel("pr_currency", inputForm.baseCurrency, '-') }}
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item label-width="220px" :label="$i18nMy.t('基础币种总价')" prop="totalBaseAmount" :rules="[]">
            <div class="myformText1" v-if="inputForm.totalBaseAmount !=null&&inputForm.totalVatBaseAmount !=''" >
              {{$common.toThousands(inputForm.totalBaseAmount.toFixed(2))}}
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item label-width="10px" label=""  prop="totalVatBaseAmount" :rules="[]">
            <div class="myformText2" v-if="inputForm.totalVatBaseAmount !=null&&inputForm.totalVatBaseAmount !=''"  >
              {{$common.toThousands(inputForm.totalVatBaseAmount.toFixed(2))}}(VAT)
            </div>
          </el-form-item>

        </el-col>
        <el-col :span="3">
          <el-form-item label-width="10px" label="" :rules="[]">
            {{ $dictUtils.getDictLabel("pr_currency", inputForm.baseCurrency, '-') }}
          </el-form-item>
        </el-col>
      </el-row>

      <el-row >
        <el-tabs type="border-card" v-model="activeName">
          <el-tab-pane v-for="(item, index) in tabs" :label="item" :key ="index" style="overflow-x:auto;overflow-y:hidden ;">
           <el-row v-if="index==0">
             <el-button size="small" @click="addTabListGroup()" round type="primary" icon="el-icon-plus" style="float: left;margin-left: 10px;padding: 5px 5px;" >
             </el-button>
           </el-row>
           <el-table :data="detailInfo" height="300px" class="table" size="small" border  >
              <el-table-column prop="serialNumber" width="50" align="center" :label="$i18nMy.t('序号')"> </el-table-column>
              <el-table-column prop="item" align="left"  :label="'* '+$i18nMy.t('物品')" >
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-input  size="small" v-model="row.item" :placeholder="$i18nMy.t('请输入内容')" ></el-input>
                  </template>
                  <span v-else>{{ row.item }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="itemDescription" v-if="index == 0" align="left" :label="$i18nMy.t('描述')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-input  size="small" v-model="row.itemDescription" :placeholder="$i18nMy.t('请输入内容')" ></el-input>
                  </template>
                  <span v-else>{{ row.itemDescription }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="brandName" align="left" :label="$i18nMy.t('品牌')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-input  size="small" v-model="row.brandName" :placeholder="$i18nMy.t('请输入内容')" ></el-input>
                  </template>
                  <span v-else>{{ row.brandName }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="modelNo" align="left" :label="$i18nMy.t('型号')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-input  size="small" v-model="row.modelNo" :placeholder="$i18nMy.t('请输入内容')" ></el-input>
                  </template>
                  <span v-else>{{ row.modelNo }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="supplierName"  v-if="index == 2" align="left" :label="$i18nMy.t('供应商名称')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-input  size="small" :disabled="flowStage=='start'?true:false"  v-model="row.supplierName" placeholder="" ></el-input>
                  </template>
                  <span v-else>{{ row.supplierName }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="unitPrice" width="120" align="right" :label="'* '+$i18nMy.t('市场价格')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-input  size="small" v-only-num.float="row"
                      v-model="row.unitPrice"
                      placeholder="" ></el-input>
                  </template>
                  <span v-else>{{ $common.toThousands(row.unitPrice) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="docUnitPrice" v-if="index == 2" align="right" :label="$i18nMy.t('报价单单价')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-input  size="small" v-only-num.float="row"
                    :disabled="flowStage=='start'?true:false"   v-model="row.docUnitPrice" placeholder="" ></el-input>
                  </template>
                  <span v-else>{{ $common.toThousands(row.docUnitPrice)}}</span>
                </template>
              </el-table-column>
              <el-table-column prop="quantity" width="155" align="right" :label="'* '+$i18nMy.t('数量')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-input-number  size="small" v-model="row.quantity" :step="1"  :min="1" :max="100" :label="$i18nMy.t('数量')"></el-input-number>
                  </template>
                  <span v-else>{{ $common.toThousands(row.quantity) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="uom" width="100" align="left" :label="'* '+$i18nMy.t('单位')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-select  size="small" v-model="row.uom" placeholder="" style="width: 100%;">
                      <el-option v-for="item in $dictUtils.getDictList('purchasing_unit')" :key="item.value" :label="item.label"
                        :value="item.value">
                      </el-option>
                    </el-select>
                  </template>
                  <span v-else>{{ $common.toThousands(row.uom) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="expectArrivalDate" width="150" v-if="index == 0" align="right" :label="'* '+$i18nMy.t('预计到达时间')"    >
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-date-picker  size="small" v-model="row.expectArrivalDate" type="date"
                      value-format="yyyy-MM-dd" :placeholder="$i18nMy.t('选择日期时间')">
                    </el-date-picker>
                  </template>
                  <span v-else>{{ row.expectArrivalDate }}</span>
                </template>
              </el-table-column>

              <el-table-column prop="vat" v-if="index == 2" align="right" :label="$i18nMy.t('VAT')">
                <template slot-scope="{row}">
                  <span  v-if="inputForm.vat !=null"> {{ inputForm.vat*100 }} %</span>
                </template>
              </el-table-column>

              <el-table-column  v-if="index == 2" align="center" :label="$i18nMy.t('报价单币种')">
                <template>
                  <el-table-column prop="docAmount" align="right" :label="$i18nMy.t('总数')">
                    <template slot-scope="{row}">
                      <template v-if="row.edit">
                        <el-input  size="small" v-only-num.float="row"
                        :disabled="flowStage=='start'?true:false"   v-model="row.docAmount" placeholder="" ></el-input>
                      </template>
                      <span v-else>
                        <span v-if="(row.docAmount||'')!=''">
                          {{ $common.toThousands(row.docAmount.toFixed(2)) }}
                        </span>
                      </span>
                    </template>
                  </el-table-column>

                  <el-table-column prop="docVatAmount" align="right" :label="$i18nMy.t('总数')+'(VAT)'">
                    <template slot-scope="{row}">
                      <template v-if="row.edit">
                        <el-input  size="small" v-only-num.float="row"
                        :disabled="flowStage=='start'?true:false"   v-model="row.docVatAmount" placeholder="" ></el-input>
                      </template>
                      <span v-else>
                        <span v-if="(row.docVatAmount||'')!=''">
                          {{ $common.toThousands(row.docVatAmount.toFixed(2)) }}
                        </span>
                      </span>
                    </template>
                  </el-table-column>
                </template>
              </el-table-column>

              <el-table-column v-if="index != 0" align="center" :label="$i18nMy.t('基础报价')" >
                <template>
                  <el-table-column prop="baseQuantity" align="right" :label="$i18nMy.t('总数')">
                    <template slot-scope="{row}">
                      <span v-if="!isNaN(row.docAmount*inputForm.exRate)">
                        {{$common.toThousands((row.docAmount*inputForm.exRate).toFixed(2))}}
                      </span>
                    </template>
                  </el-table-column>

                  <el-table-column v-if="index == 2" prop="baseVatQuantity" align="right" :label="$i18nMy.t('总数')+'(VAT)'"   >
                    <template slot-scope="{row}">
                      <span  v-if="!isNaN(row.docVatAmount*inputForm.exRate)">
                        {{$common.toThousands((row.docVatAmount*inputForm.exRate).toFixed(2))}}
                      </span>
                    </template>
                  </el-table-column>
                </template>
              </el-table-column>

              <el-table-column width="130" align="left" :label="$i18nMy.t('操作')" class-name="td-operate">
                <template slot-scope="{row}">
                  <el-button v-if="row.edit" type="success" size="small" icon="el-icon-check" @click="confirmTabListGroup(row)" style="float: left; "></el-button>
                  <el-button v-if="!row.edit" type="primary" size="small" icon="el-icon-edit" @click="changeTabListGroup(row)" style="float: left; "></el-button>
                  <el-button type="danger" size="small" icon="el-icon-delete" @click="delTabListGroup(row)" style="float: left; "></el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-tab-pane>
        </el-tabs>
      </el-row>
      <el-row :gutter="15">
        <el-col :span="24">
          <p style="text-align: left;margin: 10px 0px 10px 0px;font-size: 20px;font-weight: 500;">
            {{$i18nMy.t('申请理由')}}
          </p>
        </el-col>
        <el-col :span="24">
        <el-form-item class="updown" :label="$i18nMy.t('申购目的')" prop="purchasePurpose" :rules="[{required: true, message:$i18nMy.t('请填写申购目的'), trigger:'blur'}]">
          <el-input type="textarea" style="width: 100%;" v-model="inputForm.purchasePurpose" :placeholder="$i18nMy.t('请填写申购目的')"></el-input>
         </el-form-item>
        </el-col>
        <el-col :span="24" style="margin-top: 10px;">
          <label for="roi" class="el-form-item__label">{{$i18nMy.t('投资回报分析')}}</label>
          <el-input type="textarea" style="width: 100%;" v-model="inputForm.roi" :placeholder="$i18nMy.t('请填写ROI')"></el-input>
        </el-col>
        <el-col :span="24" style="margin-top: 10px;">
          <label for="noBudgetExplain"  style="margin-top: 10px;" class="el-form-item__label">{{$i18nMy.t('预算外说明')}}</label>
          <el-input type="textarea"  v-model="inputForm.noBudgetExplain" :placeholder="$i18nMy.t('请填写预算外说明')"></el-input>
        </el-col>
        <el-col :span="24" style="margin-top: 10px;">
          <label for="paymentSpecial"  style="margin-top: 10px;" class="el-form-item__label">{{$i18nMy.t('支付说明')}}</label>
          <el-input type="textarea"  v-model="inputForm.paymentSpecial" :placeholder="$i18nMy.t('请填写支付说明')"></el-input>
        </el-col>
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
        activeName:'0',
        ifSiteChange: true,
        isCopy: false,
        title: '',
        method: '',
        loading: false,
        flowStage:'start',
        detailInfo:[],
        tabs:[$i18nMy.t('基础信息'),$i18nMy.t('技术信息'),$i18nMy.t('财务信息')],
        inputForm: {
          id: '',
          createBy:{id:this.$store.state.user.id, name: this.$store.state.user.name},
          createDate:this.$common.formatTime(new Date()),
          createByOffice: {
            id: this.$store.state.user.office.id,
            name: this.$store.state.user.office.name
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
          exRate: '',
          vat:null,
          contractCurrency: '',
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
      siteChange(){
        this.ifSiteChange = false;
        this.$nextTick(() => {
          this.ifSiteChange = true;
        })
      },
      initCreateBy(){
        if(this.$common.isEmpty(this.$store.state.user.id)){
          setTimeout(this.initCreateBy,500)
          return
        }
        this.inputForm.createBy.id=this.$store.state.user.id
        this.inputForm.createBy.name=this.$store.state.user.name
        this.inputForm.createDate=this.$common.formatTime(new Date())
        this.inputForm.createByOffice.id = this.$store.state.user.office.id
        this.inputForm.baseCurrency= 'HKD'//this.$dictUtils.getDictList('pr_currency')[0].value
        this.inputForm.vat = null
      },
      init(query) {
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
              if (!this.$common.isEmpty(this.inputForm.detailInfo)){
                this.detailInfo = JSON.parse(this.inputForm.detailInfo)
                this.inputForm.totalVatContractAmount=0
                for(var i=0;i<this.detailInfo.length;i++){
                  this.inputForm.totalVatContractAmount+=this.detailInfo[i].docVatAmount
                }
              }
              this.loading = false
            })
          })
        }
        else{
          Object.assign(this.$data, this.$options.data.call(this))
          this.initCreateBy()
        }
      },
      // 表单提交
      saveForm(callBack) {
        this.inputForm.detailInfo=JSON.stringify(this.detailInfo)
        if(this.detailInfo.length ==0){
           this.$message.warning($i18nMy.t('物品列表不能为空'))
           return ;
        }
        for(var i=0;i<this.detailInfo.length;i++){
          if(this.detailInfo[i].edit){
            this.$message.warning($i18nMy.t('物品列表还有未保存的记录'))
            return ;
          }
        }
        if(this.inputForm.expenseType=='OPEX'&&this.inputForm.totalBaseAmount>2000){
           this.$message.warning("OPEX "+$i18nMy.t('金额必须小于')+" 2,000HKD")
           return ;
        }

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
      _sortDetailInfo(){
        for(var i=0;i<this.detailInfo.length;i++){
          this.detailInfo[i].serialNumber=i+1
        }
        this.detailInfo=this.detailInfo.slice()
      },
      addTabListGroup(){
        this.detailInfo.push({edit:true,serialNumber:this.detailInfo.length+1})
        this.detailInfo=this.detailInfo.slice()
      },
      confirmTabListGroup(row){
        if(this.$common.isEmpty(row.item)){
           this.$message.warning($i18nMy.t('物品') + $i18nMy.t('不能为空'))
        }
        else if(this.$common.isEmpty(row.quantity)){
           this.$message.warning($i18nMy.t('数量') + $i18nMy.t('不能为空'))
        }
        else if(this.$common.isEmpty(row.uom)){
           this.$message.warning($i18nMy.t('单位') + $i18nMy.t('不能为空'))
        }
        else if(this.$common.isEmpty(row.expectArrivalDate)){
           this.$message.warning($i18nMy.t('预计到达时间') + $i18nMy.t('不能为空'))
        }
        else if(this.$common.isEmpty(row.unitPrice)){
          this.$message.warning($i18nMy.t('单价') + $i18nMy.t('不能为空'))
          this.activeName='1'
        }
        else{
          row.edit =false
        }
      },
      delTabListGroup(row){
        var index=this.detailInfo.indexOf(row)
        if (index > -1) {
          this.detailInfo.splice(index, 1)
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
  .el-form-item {
      margin-bottom: 10px;
  }
  .el-form-item__label {
    line-height: 25px;
  }
  .el-table th.must>.cell:before {
  	content: '*';
  	color: #ff1818;
  }
  .myformText1{
    text-align: right;
    width: 70px;
  }
  .myformText2{
    text-align: right;
    width: 110px;
  }

  .updown ::v-deep label{float:none !important;}

  .updown ::v-deep label+div{float:none !important;margin-left:0px !important;}
</style>
