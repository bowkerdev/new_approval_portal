<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 36px;">
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

        <el-form size="small" :model="inputForm" ref="inputFormSite" :disabled="formReadOnly">
          <el-col :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('采购地区')" prop="applySiteCode" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'change'}]">
              <el-select v-model="inputForm.applySiteCode" :placeholder="$i18nMy.t('请选择')" style="width: 100%;" @change="siteChange">
                <el-option v-for="item in $dictUtils.getDictList('apply_site_code')" :key="item.value" :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-form>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('用户姓名')" prop="requester" :rules="[{required: true, message:$i18nMy.t('请填写用户姓名'), trigger:'blur'}]">
            <el-input v-model="inputForm.requester" :placeholder="$i18nMy.t('请填写用户姓名')" maxlength="30"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('用户部门')" prop="requesterDepartment" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'change'}]">
            <!-- <SelectTree ref="requesterDepartment" v-if="ifSiteChange" :props="{
                    value: 'id',             // ID字段名
                    label: 'name',         // 显示名称
                    children: 'children'    // 子级字段名
                  }" :url="`/sys/office/treeData?type=2&parentCode=${inputForm.applySiteCode}`" :value="inputForm.requesterDepartment.id" :clearable="true"
              :accordion="true" @getValue="(value, name) => {inputForm.requesterDepartment.id=value; inputForm.requesterDepartment.name=name}" />
              <el-input v-if="!ifSiteChange" :placeholder="$i18nMy.t('请选择')" disabled></el-input> -->
              <el-select v-model="inputForm.requesterDepartment" :placeholder="$i18nMy.t('请选择')" filterable allow-create clearable style="width: 100%;" >
                <el-option v-for="item in deptList" :key="item.value" :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form size="small" :model="inputForm" ref="inputFormFA3" :disabled="!((parentForm==='TaskForm'&&(isFC||isFA)))" >
          <el-form-item label-width="220px" :label="$i18nMy.t('费用类型')" prop="expenseType" :rules="[{required: (parentForm==='TaskForm'&&(isFC||isFA)), message:$i18nMy.t('不能为空'), trigger:'change'}]">
            <el-select v-model="inputForm.expenseType" :placeholder="$i18nMy.t('由FC编辑')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictList('expense_type')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('要求到货时间')" prop="expectArrivalDate" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'blur'}
                 ]">
            <el-date-picker v-model="inputForm.expectArrivalDate" type="date" style="width: 100%;" @change="checkExpectArrivalDate"
              value-format="yyyy-MM-dd" :placeholder="$i18nMy.t('选择日期时间')">
            </el-date-picker>
          </el-form-item>
        </el-col>

        <el-col :span="12" v-if="procDefKey === 'prpo_non_it'">
          <!-- <el-form-item label-width="220px" :label="$i18nMy.t('技术支持部门')" prop="technicalAdvisor" :rules="[ ]">
            <el-select v-model="inputForm.technicalAdvisor" :placeholder="$i18nMy.t('请选择')" style="width: 100%;" clearable>
             <el-option v-for="item in $dictUtils.getDictList('technical_advisor')" :key="item.value" :label="item.label"
               :value="item.value">
             </el-option>
            </el-select>
          </el-form-item> -->
          <el-form-item label-width="220px" :label="$i18nMy.t('技术支持部门')" prop="technicalAdvisor" :rules="[ ]">
           <!-- <SelectTree ref="technicalAdvisor" v-if="ifSiteChange" :props="{
                    value: 'id',             // ID字段名
                    label: 'name',         // 显示名称
                    children: 'children'    // 子级字段名
                  }" :url="`/sys/office/treeData?type=2&parentCode=${inputForm.applySiteCode}`" :value="inputForm.technicalAdvisor.id" :clearable="true"
              :accordion="true" @getValue="(value, name) => {inputForm.technicalAdvisor.id=value; inputForm.technicalAdvisor.name=name}" />
              <el-input v-if="!ifSiteChange" :placeholder="$i18nMy.t('请选择')" disabled></el-input> -->
            <el-select v-model="inputForm.technicalAdvisor" :placeholder="$i18nMy.t('请选择')" filterable style="width: 100%;" multiple>
              <el-option v-for="item in $dictUtils.getDictList('technical_advisor')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('申购优先级')" prop="requestRiority" :title="$i18nMy.t('requestRiorityDesc')" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'change'}]">
            <el-select v-model="inputForm.requestRiority" :placeholder="$i18nMy.t('请选择')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictList('request_priority')" :key="item.value" :label="item.label"
                :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
          <el-col :span="12">
            <el-form-item style="height: 34px;" label-width="220px" :label="$i18nMy.t('是否预算内')" prop="isBudget" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'change'}]">
              <el-radio-group v-model="inputForm.isBudget">
                <el-radio v-for="item in $dictUtils.getDictList('yes_no')" :label="item.value" :key="item.value">
                  {{item.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('项目描述')" prop="projectName" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'blur'}]">
              <el-input type="textarea" v-model="inputForm.projectName" :placeholder="$i18nMy.t('长度不超过500')" maxlength="500"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label-width="220px" :label="$i18nMy.t('预算备注')" prop="budgetRemark" :rules="[]">
              <el-input type="textarea" v-model="inputForm.budgetRemark" :placeholder="$i18nMy.t('长度不超过500')" maxlength="500"></el-input>
            </el-form-item>
          </el-col>
          <el-form size="small" :model="inputForm" ref="inputFormFA1" :disabled="!(status==='start'||(parentForm==='TaskForm'&&isFA))" >
          <el-col :span="12" >
            <el-form-item  label-width="220px" :label="$i18nMy.t('成本中心')" prop="costCenter" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'change'}]">
              <el-select v-model="inputForm.costCenter" :placeholder="$i18nMy.t('请选择')" filterable style="width: 100%;" >
                <el-option v-for="item in $dictUtils.getDictListWithKey('cost_center')" :key="item.value" :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          </el-form>
          <el-form size="small" :model="inputForm" ref="inputFormFA2" :disabled="!((parentForm==='TaskForm'&&isFA))" >
          <el-col :span="12" >
            <el-form-item label-width="220px" :label="$i18nMy.t('固定资产类别')" prop="assetGroup" :rules="[{required: (parentForm==='TaskForm'&&isFA), message:$i18nMy.t('不能为空'), trigger:'blur'}]">
              <el-select v-model="inputForm.assetGroup" :placeholder="$i18nMy.t('由FA编辑')" filterable style="width: 100%;" >
                <el-option v-for="item in $dictUtils.getDictListWithKey('asset_group')" :key="item.value" :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          </el-form>
          <el-form size="small" :model="inputForm" ref="inputFormFC"
              :disabled="!(parentForm==='TaskForm'&&((procDefKey==='prpo_non_it'&&isFC) || (!formReadOnly&&procDefKey!='prpo_non_it')))"  label-width="140px" >
              <!-- v-if="formReadOnly || isFC || procDefKey!='prpo_non_it'" label-width="140px" > -->
            <el-col :span="12" >
                <!-- <el-form-item label-width="220px" :label="$i18nMy.t('签约方公司')" prop="legalEntity" :rules="[{required: (parentForm==='TaskForm'&&isFC), message:$i18nMy.t('不能为空'), trigger:'blur'}]">-->            <el-form-item label-width="220px" :label="$i18nMy.t('签约方公司')" prop="legalEntity" :rules="[]">
                <el-select v-model="inputForm.legalEntity" :placeholder="$i18nMy.t('由FC编辑')" filterable style="width: 100%;">
                  <el-option v-for="item in $dictUtils.getDictListWithKey('all_company')" :key="item.value" :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
              </el-form-item>
            </el-col>
          </el-form>
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
          <el-form-item label-width="220px" :label="$i18nMy.t('合同总价')" prop="contractCurrency" :rules="[
                 ]">
              <div v-if="inputForm.contractCurrency !=null && inputForm.contractCurrency !=''">
                {{ $dictUtils.getDictLabel("pr_currency", inputForm.contractCurrency, $i18nMy.t('多种币种')) }}
              </div>
              <div v-else>
                -
              </div>
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item label-width="10px"  :rules="[]">
            <div class="myformText1" v-if="(inputForm.totalContractAmount||'')!=''">
              {{$common.toThousands(inputForm.totalContractAmount.toFixed(2))}}
            </div>
            <div v-else>
              -
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item label-width="10px"  :rules="[]">
            <div class="myformText2" v-if="(inputForm.totalVatContractAmount||'')!=''">
              {{$common.toThousands(inputForm.totalVatContractAmount.toFixed(2))}}(VAT)
            </div>
            <div v-else>
              -
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('汇率')" prop="exRate" :rules="[]">
            {{inputForm.exRate||'-'}}
          </el-form-item>
        </el-col>



        <el-col :span="6">
          <el-form-item label-width="220px" :label="$i18nMy.t('基础币种总价')" prop="baseCurrency" :rules="[ ]">
            {{ $dictUtils.getDictLabel("pr_currency", inputForm.baseCurrency, '-') }}
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item label-width="10px"   prop="totalBaseAmount" :rules="[]">
            <div class="myformText1" v-if="inputForm.totalBaseAmount !=null&&inputForm.totalBaseAmount !=''" >
              {{$common.toThousands(inputForm.totalBaseAmount.toFixed(2))}}
            </div>
            <div v-else>
              -
            </div>
          </el-form-item>
        </el-col>
        <el-col :span="3">
          <el-form-item label-width="10px"   prop="totalVatBaseAmount" :rules="[]">
            <div class="myformText2" v-if="inputForm.totalVatBaseAmount !=null&&inputForm.totalVatBaseAmount !=''"  >
              {{$common.toThousands(inputForm.totalVatBaseAmount.toFixed(2))}}(VAT)
            </div>
            <div v-else>
              -
            </div>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row >
        <el-tabs type="border-card" v-model="activeName">
          <el-tab-pane v-for="(item, index) in tabs" :label="item" :key ="index" style="overflow-x:auto;overflow-y:hidden;">
           <el-row v-if="index==0">
             <el-button size="small" @click="addTabListGroup()" round type="primary" icon="el-icon-plus" style="float: left;margin-left: 10px;padding: 5px 5px;" >
             </el-button>
           </el-row>
           <el-table :data="detailInfo" height="300px" class="table" size="small" border  >
              <el-table-column prop="serialNumber" width="50" align="center" :label="$i18nMy.t('序号')"> </el-table-column>
              <el-table-column prop="item" min-width="280" align="left" >
                <template slot="header">
                  <font color="red" style="font-weight: bold;">*</font>&nbsp;{{$i18nMy.t('物品')}}
                </template>
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-input type="textarea" size="small" v-model="row.item" maxlength="300" :placeholder="$i18nMy.t('长度不超过300')" ></el-input>
                  </template>
                  <span v-else class="my-span">{{ row.item }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="itemDescription" min-width="320" v-if="index == 0" align="left" :label="$i18nMy.t('描述')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-form size="small" :model="inputForm" ref="inputFormFA" :disabled="!(status==='start'||(parentForm==='TaskForm'&&isFA))" >
                      <el-input type="textarea" size="small" v-model="row.itemDescription" maxlength="800" :placeholder="$i18nMy.t('长度不超过800')" ></el-input>
                    </el-form>
                  </template>
                  <span v-else class="my-span">{{ row.itemDescription }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="brandName" width="200" align="left" :label="$i18nMy.t('品牌')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-form size="small" :model="inputForm" ref="inputFormFA" :disabled="!(status==='start'||(parentForm==='TaskForm'&&isFA))" >
                      <el-input type="textarea" size="small" v-model="row.brandName" maxlength="50" :placeholder="$i18nMy.t('长度不超过50')" ></el-input>
                    </el-form>
                  </template>
                  <span v-else class="my-span">{{ row.brandName }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="modelNo" width="200" align="left" :label="$i18nMy.t('型号')">
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-form size="small" :model="inputForm" ref="inputFormFA" :disabled="!(status==='start'||(parentForm==='TaskForm'&&isFA))" >
                      <el-input type="textarea" size="small" v-model="row.modelNo" maxlength="100" :placeholder="$i18nMy.t('长度不超过100')" ></el-input>
                    </el-form>
                  </template>
                  <span v-else class="my-span">{{ row.modelNo }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="supplierName" v-if="index == 1" align="left" :label="$i18nMy.t('供应商名称')">
                <template slot-scope="{row}">
                  <!-- <template v-if="row.edit">
                    <el-input  size="small" :disabled="flowStage=='start'?true:false"  v-model="row.supplierName" placeholder="" ></el-input>
                  </template>
                  <span v-else>{{ row.supplierName }}</span> -->
                  {{ row.supplierName }}
                </template>
              </el-table-column>
              <el-table-column prop="currency" width="70" v-if="index == 1" align="left" :label="$i18nMy.t('币种')">
                <template slot-scope="{row}">
                  {{ row.currency }}
                </template>
              </el-table-column>
              <el-table-column prop="unitPrice" width="80" v-if="index == 1" align="right" :label="$i18nMy.t('单价')">
                <template slot-scope="{row}">
                    {{ $common.toThousands(row.unitPrice) }}
                </template>
              </el-table-column>
              <el-table-column prop="vat" width="60" v-if="index == 1" align="right" label="VAT%">
                <template slot-scope="{row}">
                  <span  v-if="row.vat !=null"> {{ row.vat }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="vatUnitPrice" width="140" v-if="index == 1" align="right" :label="$i18nMy.t('单价(含VAT)')">
                <template slot-scope="{row}">
                    {{ $common.toThousands(row.vatUnitPrice) }}
                </template>
              </el-table-column>

              <el-table-column prop="quantity" width="90" align="right" >
                <template slot="header">
                  <font color="red" style="font-weight: bold;">*</font>&nbsp;{{$i18nMy.t('数量')}}
                </template>
                <template slot-scope="{row}">
                  <template v-if="row.edit&&!isFA">
                    <el-input-number style="width: 75px;" size="small" :controls="false" v-model="row.quantity" :step="1"  :min="1" :max="10000" :label="$i18nMy.t('数量')"></el-input-number>
                  </template>
                  <span v-else>{{ $common.toThousands(row.quantity) }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="uom" width="100" align="left" >
                <template slot="header">
                  <font color="red" style="font-weight: bold;">*</font>&nbsp;{{$i18nMy.t('单位')}}
                </template>
                <template slot-scope="{row}">
                  <template v-if="row.edit&&!isFA">
                    <el-select  size="small" v-model="row.uom" placeholder="" style="width: 100%;">
                      <el-option v-for="item in $dictUtils.getDictList('purchasing_unit')" :key="item.value" :label="item.label"
                        :value="item.value">
                      </el-option>
                    </el-select>
                  </template>
                  <span v-else>{{row.uom}}</span>
                </template>
              </el-table-column>
              <el-table-column prop="expectArrivalDate" width="150" align="right">
                <template slot="header">
                  <font color="red" style="font-weight: bold;">*</font>&nbsp;{{$i18nMy.t('预计到达时间')}}
                </template>
                <template slot-scope="{row}">
                  <template v-if="row.edit">
                    <el-date-picker  size="small" v-model="row.expectArrivalDate" type="date" @change="checkExpectArrivalDate"
                      value-format="yyyy-MM-dd" :placeholder="$i18nMy.t('选择日期时间')">
                    </el-date-picker>
                  </template>
                  <span v-else>{{ $common.parseTime(row.expectArrivalDate, '{y}-{m}-{d}') }}</span>
                </template>
              </el-table-column>

              <el-table-column  v-if="index == 1" align="center" :label="$i18nMy.t('报价单币种')">
                <template>
                  <el-table-column prop="docAmount" align="right" :label="$i18nMy.t('总数')">
                    <template slot-scope="{row}">
                      <!-- <template v-if="row.edit">
                        <el-input  size="small" v-only-num.float="row"
                        :disabled="flowStage=='start'?true:false"   v-model="row.docAmount" placeholder="" ></el-input>
                      </template>
                      <span v-else>
                        <span v-if="(row.docAmount||'')!=''">
                          {{ $common.toThousands(row.docAmount.toFixed(2)) }}
                        </span>
                      </span> -->
                      <span v-if="(row.docAmount||'')!=''">
                        {{ $common.toThousands(row.docAmount.toFixed(2)) }}
                      </span>
                    </template>
                  </el-table-column>

                  <el-table-column width="120px" prop="docVatAmount" align="right" :label="$i18nMy.t('总数(VAT)')">
                    <template slot-scope="{row}">
                      <!-- <template v-if="row.edit">
                        <el-input  size="small" v-only-num.float="row"
                        :disabled="flowStage=='start'?true:false"   v-model="row.docVatAmount" placeholder="" ></el-input>
                      </template>
                      <span v-else>
                        <span v-if="(row.docVatAmount||'')!=''">
                          {{ $common.toThousands(row.docVatAmount.toFixed(2)) }}
                        </span>
                      </span> -->
                      <span v-if="(row.docVatAmount||'')!=''">
                        {{ $common.toThousands(row.docVatAmount.toFixed(2)) }}
                      </span>
                    </template>
                  </el-table-column>

                </template>
              </el-table-column>

              <el-table-column v-if="index == 1" align="center" label="HKD" >
                <template>
                  <el-table-column prop="baseAmount" align="right" :label="$i18nMy.t('总数')">
                    <template slot-scope="{row}">
                      <span v-if="!isNaN(row.docAmount*row.exRate)">
                        {{$common.toThousands((row.docAmount*row.exRate).toFixed(2))}}
                      </span>
                    </template>
                  </el-table-column>

                  <el-table-column width="120px" prop="baseVatAmount" align="right" :label="$i18nMy.t('总数(VAT)')"   >
                    <template slot-scope="{row}">
                      <span  v-if="!isNaN(row.docVatAmount*row.exRate)">
                        {{$common.toThousands((row.docVatAmount*row.exRate).toFixed(2))}}
                      </span>
                    </template>
                  </el-table-column>
                </template>
              </el-table-column>

              <el-table-column width="120" align="center" :label="$i18nMy.t('操作')" class-name="td-operate">
                <template slot-scope="{row}">
                  <el-form size="small" ref="inputFormFA" :disabled="!(status==='start'||(parentForm==='TaskForm'&&isFA))" >
                    <el-button v-if="row.edit"  type="success" size="small" icon="el-icon-check" @click="confirmTabListGroup(row)" style="float: left; "></el-button>
                    <el-button v-if="!row.edit" type="primary" size="small" icon="el-icon-edit"  @click="changeTabListGroup(row)"  style="float: left; "></el-button>
                  </el-form>
                  <el-button type="danger" size="small" icon="el-icon-delete" @click="delTabListGroup(row)" style="margin-left:10px; float: left; "></el-button>
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
        <el-form-item class="updown" :label="$i18nMy.t('申购目的')" prop="purchasePurpose" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'blur'}]">
          <el-input type="textarea" style="width: 100%;" v-model="inputForm.purchasePurpose" maxlength="800" :placeholder="$i18nMy.t('长度不超过800')"></el-input>
          <!-- <div style="color: #005DF7; font-size: 12px; cursor: pointer;" @click="toDocPage"> → {{$i18nMy.t('前往[补充文件]页')}}</div> -->
         </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item class="updown" :label="$i18nMy.t('投资回报分析')" prop="roi" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'blur'}]">
            <el-input type="textarea" style="width: 100%;" v-model="inputForm.roi" maxlength="800" :placeholder="$i18nMy.t('长度不超过800')"></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="24" v-if="inputForm.isBudget!='1'" >
          <el-form-item class="updown" :label="$i18nMy.t('预算外说明')" prop="noBudgetExplain" :rules="[{required: true, message:$i18nMy.t('不能为空'), trigger:'blur'}]">
            <el-input type="textarea" style="width: 100%;" v-model="inputForm.noBudgetExplain" maxlength="800" :placeholder="$i18nMy.t('长度不超过800')"></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item class="updown" :label="$i18nMy.t('支付说明')" prop="paymentSpecial" :rules="[]">
            <el-input type="textarea" style="width: 100%;" v-model="inputForm.paymentSpecial" maxlength="800" :placeholder="$i18nMy.t('长度不超过800')"></el-input>
           </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <br><div style="color: #005DF7; font-size: 12px; cursor: pointer; float: left; " @click="toSupplierPage"> → {{$i18nMy.t('前往[供应商报价和合同]页')}}</div>
    <div style="color: #005DF7; font-size: 12px; cursor: pointer; float: left; margin-left: 50px;" @click="toDocPage"> → {{$i18nMy.t('前往[补充文件]页')}}</div>
  </div>
</template>

<script>
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  import UserSelect from '@/components/userSelect'
  export default {
    data() {
      return {
        parentPage: null,
        topPage: null,
        activeName:'0',
        ifSiteChange: false,
        ifIT: false,
        parentForm: '',
        isFC: false,
        isFA: false,
        status: '',
        isCopy: false,
        title: '',
        method: '',
        loading: false,
        deptList: [],
        procDefKey: '',
        taskDefKey: '',
        flowStage:'start',
        detailInfo: [],
        tabs:[$i18nMy.t('基础信息'),/* $i18nMy.t('技术信息'), */$i18nMy.t('财务信息')],
        checkFormList:['inputFormFC','inputFormFA1','inputFormFA2','inputFormFA3','inputFormSite'],
        inputForm: {
          id: '',
          procDefKey: '',
          createBy:{id:this.$store.state.user.id, name: this.$store.state.user.name},
          createDate:this.$common.formatTime(new Date()),
          createByOffice: {
            id: this.$store.state.user.office.id,
            name: this.$store.state.user.office.name
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
          budgetRemark: '',
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
          remarks: '',
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
    watch: {
      'inputForm.technicalAdvisor': {
        handler(newV,oldv) {
          if(newV!=null&&newV.includes('Group SEA Manager')&&!newV.includes('Local SEA Manager')){
            this.$message.warning(this.$i18nMy.t('必须先选择Local SEA Manager才能选择Group SEA Manager'))
            this.$nextTick(() => {
              this.inputForm.technicalAdvisor.splice(this.inputForm.technicalAdvisor.indexOf('Group SEA Manager'), 1);
            })
          }
        }
      },
      'inputForm.totalVatBaseAmount': {
        handler(newV,oldv) {
          if (newV!=null && newV>=5000000 && this.taskDefKey=="CeoOffice") {
            this.topPage.changeButtons()
          }
        }
      }
    },
    methods: {
      siteChange(){
        this.ifSiteChange = false
        this.inputForm.requesterDepartment = ''
        let _that=this
        this.$dictUtils.getSqlDictList('GET_DEPT_WITH_HEAD',{site: this.inputForm.applySiteCode},function(data){
          _that.deptList = data
        })
        this.$nextTick(() => {
          this.ifSiteChange = true
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
      init(query, parentPage, topPage) {
        
        // 重置表单
        this.$refs.inputForm.resetFields();
        this.checkFormList.forEach(formRef => {
          const refEl = this.$refs[formRef]
          if (refEl) {
            refEl.resetFields();
          }
        });

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
              if (topPage && topPage.auditForm && data.oaPrNew.remarks && data.oaPrNew.remarks.indexOf(query.taskDefKey+'##')==0) {
                topPage.auditForm.message = (data.oaPrNew.remarks).replace(query.taskDefKey+'##','')
              }
              this.inputForm.createDate = this.$common.formatTime(new Date(new Date(data.oaPrNew.createDate).getTime() + 8*3600*1000))
              this.inputForm.technicalAdvisor = this.toArray(this.inputForm.technicalAdvisor)
              this.inputForm.procDefKey = query.procDefKey
              this.ifSiteChange = true;
              this.siteChange()
              this.inputForm.requesterDepartment = data.oaPrNew.requesterDepartment
              if (this.isCopy) {
                if (query.status && query.status == "reopen") {
                  this.inputForm.id = ''
                  this.inputForm.applicationNo = this.inputForm.applicationNo + '-REOPEN'
                }else {
                  this.inputForm.id = ''
                  this.inputForm.applicationNo = ''
                }
              }
              if (!this.$common.isEmpty(this.inputForm.detailInfo)){
                this.detailInfo = JSON.parse(this.inputForm.detailInfo)
                this.activeName='0'
              }
              this.loading = false
            })
          })
        }
        else{
          Object.assign(this.$data, this.$options.data.call(this))
          this.inputForm.procDefKey = query.procDefKey
          this.initCreateBy()
        }
        this.procDefKey = query.procDefKey
        this.taskDefKey = query.taskDefKey + ''

        if (this.procDefKey === 'prpo_non_it') {
          this.ifIT = true
        }
        if (query.taskDefKey && query.taskDefKey.indexOf('FC')>0) {
          this.isFC = true
        }
        if (query.taskDefKey && query.taskDefKey.indexOf('FA')>0) {
          this.isFA = true
        }
        if (query.status) {
          this.status = query.status
        }
        if (this.taskDefKey == 'FormModify') {  // Modify PR 当做start处理
          this.status = 'start'
        }
        if (query.parentForm) {
          this.parentForm = query.parentForm
        }
        this.parentPage = parentPage
        this.topPage = topPage
      },
      toArray(str) {
          if (typeof str === 'undefined' || str === null || str === "") {
              return "";
          }
          return str.split(',');
      },
      checkForm() {
        let rtnVal = true;
        let illegalAttr = [];
        this.$refs['inputForm'].validate((valid, obj) => {
          if (!valid) {
            rtnVal = false
            illegalAttr = Object.keys(obj)
          }
        })

        for (var index in this.checkFormList) {
          if (this.$refs[this.checkFormList[index]]) {
            this.$refs[this.checkFormList[index]].validate((valid, obj) => {
              if (!valid) {
                rtnVal = false
                illegalAttr = illegalAttr.concat(Object.keys(obj))
              }
            })
          }
        }

        // 有非法输入，滚动到错误的地方
        if (illegalAttr.length) {
          //  滚动到第一个地方
          const key = illegalAttr[0];
          const container = document.getElementById('taskFromContainer');
          const domEl = document.querySelector("label[for=" + key +"]");
          const domElTop = domEl.getBoundingClientRect().top;
          // 94: 顶部固定栏， 40：预留margin
          container.scrollTop = container.scrollTop + domElTop - 94 - 40;
        }

        if(!rtnVal){
          this.$message.warning($i18nMy.t('请输入完整信息'))
          return false
        }

        if(this.detailInfo.length == 0){
          this.$message.warning($i18nMy.t('物品列表不能为空'))
          return false
        }
        
        return rtnVal
      },
      checkItemForm(){
        for(var i=0;i<this.detailInfo.length;i++){
          if(this.detailInfo[i].edit){
            this.$message.warning($i18nMy.t('物品列表还有未保存的记录'))
            return false
          }
        }
        return true
      },
      // 表单提交
      saveForm(callBack) {
        this.inputForm.detailInfo=JSON.stringify(this.detailInfo)
        if(this.detailInfo.length ==0){
           this.$message.warning($i18nMy.t('物品列表不能为空'))
           return ;
        }
        /* for(var i=0;i<this.detailInfo.length;i++){
          if(this.detailInfo[i].edit){
            this.$message.warning($i18nMy.t('物品列表还有未保存的记录'))
            return ;
          }
        } */
        if (!this.checkItemForm()) {
          return
        }

        /* if(this.inputForm.totalBaseAmount>=2000 || this.inputForm.totalVatBaseAmount>=2000){
          this.inputForm.expenseType = 'CAPEX'
        } else {
          this.inputForm.expenseType = 'OPEX'
        } */

        /* if(this.inputForm.expenseType=='OPEX'&&this.inputForm.totalBaseAmount>2000){
           this.$message.warning("OPEX "+$i18nMy.t('金额必须小于')+" 2,000HKD")
           return ;
        }*/
        for (var index in this.checkFormList) {
          if (this.$refs[this.checkFormList[index]]) {
            this.$refs[this.checkFormList[index]].validate((valid) => {
              if (!valid) {
                return
              }
            })
          }
        }

        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.inputForm.technicalAdvisor = this.inputForm.technicalAdvisor.toString()

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
          } else {
            this.$message.warning($i18nMy.t('信息录入不完整'))
          }
        })
      },

      // 表单草稿提交
      saveAsDraft(callBack) {
        if (!this.inputForm.applySiteCode) {
          this.$message.warning($i18nMy.t('采购地区不能为空'))
          return
        }
        if (!this.checkItemForm()) {
          return
        }
        this.inputForm.detailInfo=JSON.stringify(this.detailInfo)

        this.$refs['inputFormSite'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.inputForm.technicalAdvisor = this.inputForm.technicalAdvisor.toString()
            this.inputForm.remarks = this.taskDefKey + '##' + this.topPage.auditForm.message

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
                 this.inputForm.technicalAdvisor = this.toArray(this.inputForm.technicalAdvisor)
                 callBack(data.businessTable, data.businessId)
              }
              else{
                this.$message.error(data.msg)
              }
            })
          }
        })
      },

      selectExpectArrivalDate(row){
        if (this.inputForm.expectArrivalDate=='' || row.expectArrivalDate < this.inputForm.expectArrivalDate) {
          this.inputForm.expectArrivalDate = row.expectArrivalDate
        }
      },

      checkExpectArrivalDate(){
        if (this.inputForm.expectArrivalDate && this.inputForm.expectArrivalDate < this.inputForm.createDate) {
          this.$message.warning($i18nMy.t('日期不能小于当前时间'))
          this.inputForm.expectArrivalDate = ''
        }
        for(var i=0;i<this.detailInfo.length;i++){
          if (this.detailInfo[i].expectArrivalDate) {
            if (this.detailInfo[i].expectArrivalDate < this.inputForm.createDate) {
              this.$message.warning($i18nMy.t('日期不能小于当前时间'))
              this.detailInfo[i].expectArrivalDate = ''
            }
            if (i==0) {
              this.inputForm.expectArrivalDate = this.detailInfo[i].expectArrivalDate;
            }
            if (!this.inputForm.expectArrivalDate || this.detailInfo[i].expectArrivalDate < this.inputForm.expectArrivalDate) {
              this.inputForm.expectArrivalDate = this.detailInfo[i].expectArrivalDate;
            }
          }
        }
      },

      _sortDetailInfo(){
        for(var i=0;i<this.detailInfo.length;i++){
          this.detailInfo[i].serialNumber=i+1
        }
        this.detailInfo=this.detailInfo.slice()
      },
      addTabListGroup(){
        if (!this.checkItemForm()) {
          return
        }
        this.detailInfo.push({edit:true,serialNumber:this.detailInfo.length+1,quantity:1,uom:'PCS',expectArrivalDate:this.inputForm.expectArrivalDate})
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
        else{
          row.edit =false
        }

        if (this.parentPage) {
          this.parentPage.updatePage2DataByDetailInfo()
        }
      },
      delTabListGroup(row){
        var index=this.detailInfo.indexOf(row)
        if (index > -1) {
          this.detailInfo.splice(index, 1)
        }
        this._sortDetailInfo()
        this.checkExpectArrivalDate()

        if (this.parentPage) {
          this.parentPage.updatePage2DataByDetailInfo()
        }
      },
      changeTabListGroup(row){
        row.edit =true
      },
      toSupplierPage(){
        if (this.parentPage) {
          this.parentPage.activeName = 'OaPrNewFormForSupplier'
        }
      },
      toDocPage(){
        if (this.parentPage) {
          this.parentPage.activeName = 'OaPrNewFormForDoc'
        }
      }
    }
  }
</script>
<style scoped lang = "less">
  .el-form-item {
      margin-bottom: 15px;
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
  .el-col.el-col-12 {
    min-height:49px;
  }
  .my-span{
     white-space:pre-wrap;
     word-break:keep-all;
     word-wrap:anywhere;
     overflow-wrap: anywhere;
  }

  .updown ::v-deep label{float:none !important;}

  .updown ::v-deep label+div{float:none !important;margin-left:0px !important;}

</style>
