<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 25px;">
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
          <el-form-item label-width="220px" :label="$i18nMy.t('申请人')" prop="createBy.name" :rules="[]">
            <el-input v-model="inputForm.createBy.name" :disabled='true' ></el-input>
          </el-form-item>
        </el-col>
	      <el-col  :span="12">
          <el-form-item label-width="220px" :label="$i18nMy.t('申请人部门')" prop="createByOffice.name"  :rules="[  ]">
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
        <el-col :span="12" v-show="false">
          <el-form-item label-width="220px" label="VAT" prop="vat" :rules="[ ]">
            <el-input-number :controls="false" v-model="inputForm.vat" oninput="value=value.replace(/[^0-9.]/g,'')" style="width:80px; text-align: right;" ></el-input-number>%
          </el-form-item>
        </el-col>
      </el-row>
      <br/>
      <el-form size="small" ref="inputFormFA" :disabled="!((status=='start')||(parentForm=='TaskForm'&&isFA)||(taskDefKey=='FormModify'))" >
      <el-row :gutter="0" v-if="procDefKey=='prpo'||status=='start'||parentForm=='TaskFormDetail'||isFA||taskDefKey=='FormModify'">
        <p style="float: left;" class="sub-title">
          {{$i18nMy.t('供应商报价和合同上传')}}
        </p><div style="float: left;"><el-button size="small" :disabled="detailInfo.length==0" @click="addTabListGroup()" round  type="primary" icon="el-icon-plus" style="float: left;margin-top: 7px;margin-left: 10px;padding: 5px 5px;" ></el-button></div>
        <div style="width: 100%;overflow: auto; border:1px solid #EBEEF5">
          <table style="min-width: 100%;border-collapse: collapse; border:1px solid #EBEEF5" class="supplierTable"
          cellspacing="0" bordercolor="#EBEEF5" bgcolor="#fff" >
            <thead>
              <tr class="head-background-color head1-height">
                <th style="min-width:100px;" width="12%" colspan="3"><font color="red">*</font>{{$i18nMy.t('供应商名称')}}</th>
                <th style="min-width:100px;" width="12%" colspan="1">{{$i18nMy.t('付款条件')}}</th>

                <th width="8%" >{{$i18nMy.t('总金额')}}</th>
                <th width="12%" colspan="2" >{{$i18nMy.t('总金额(含VAT)')}}</th>
                <th width="120px"><font color="red">*</font>{{$i18nMy.t('币种')}}</th>
                <th >{{$i18nMy.t('预计到货日期')}}</th>
                <!-- <th>{{$i18nMy.t('预计最晚到货日期')}}</th> -->
                <th width="10%" >{{$i18nMy.t('备注')}}</th>
                <!-- <th><font color="red">*</font>{{$i18nMy.t('文件类型')}}</th> -->
                <th colspan="3"><font color="red">*</font>{{$i18nMy.t('附件')}}</th>
                <!-- <th><font color="red">*</font>{{$i18nMy.t('关联项目')}}</th>
                <th>{{$i18nMy.t('上传者')}}</th>
                <th>{{$i18nMy.t('上传日期')}}</th> -->
                <th colspan="1">{{$i18nMy.t('操作')}}</th>
              </tr>
            </thead>
            <tbody v-for="(item, index) in supplierInfo" :key="'index_'+index">
              <tr class="data-content" style="background-color: #fff3cf;">
              <td colspan="3"  >
                <el-input type="textarea" size="small" v-if="item.edit" v-model="item.supplierName" maxlength="100" :placeholder="$i18nMy.t('长度不超过100')" ></el-input>
                <span v-else class="my-span">
                  {{item.supplierName}}
                </span>
              </td>
              <td colspan="1" style="min-width: 200px;">
                <el-input type="textarea" size="small" v-if="item.edit" v-model="item.paymentTerms" maxlength="800" :placeholder="$i18nMy.t('长度不超过800')"></el-input>
                <!-- <el-select size="small" v-model="item.paymentTerms"
                  v-if="item.edit" :placeholder="$i18nMy.t('请选择')">
                  <el-option v-for="item in $dictUtils.getDictList('pr_payment_terms')" on :key="item.value" :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select> -->
                <span v-else class="my-span">
                  {{item.paymentTerms}}
                </span>
              </td>
              <td  class="my-right">
                  {{$common.toThousands(item.originalPrice)}}
              </td>
              <td colspan="2"  class="my-right">
                  {{$common.toThousands(item.originalVatPrice)}}
              </td>
              <td  >
                <el-select @change="currencyChange(item)"  size="small" v-model="item.currency"
                  v-if="item.edit" :placeholder="$i18nMy.t('请选择')">
                  <el-option v-for="item in $dictUtils.getDictList('pr_currency')" on :key="item.value" :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
                <span v-else>
                  {{$dictUtils.getDictLabel("pr_currency",item.currency, '-')}}
                </span>
              </td>
              <td  class="my-right">
                  {{$common.parseTime(item.expectArrivalDate, '{y}-{m}-{d}')}}
              </td>
              <!-- <td  class="my-right">
                  {{item.expectLastArrivalDate}}
              </td> -->
              <td  style="min-width:100px;" >
                <el-input type="textarea" v-if="item.edit" v-model="item.remarks" maxlength="300" :placeholder="$i18nMy.t('长度不超过300')"></el-input>
                <span class="my-span" v-else>
                  {{item.remarks}}
                </span>
              </td>
              <!-- <td  style="width: 132px;">
                <el-select  size="small" v-model="item.docList[0].documentType" v-if="item.edit" :placeholder="$i18nMy.t('请选择')">
                  <el-option v-for="item in $dictUtils.getDictList('pr_document_type')" :key="item.value" :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
                <span v-else>
                  {{$dictUtils.getDictLabel("pr_document_type",item.docList[0].documentType, '-')}}
                </span>
              </td> -->
              <td colspan="3"  style="max-width: 120px; min-width: 110px; text-align: left;">
                <el-upload :class="item.docList[0].attachment.split('|').length<5?'':'hide'" :disabled="!item.edit"
                  :action="`${$http.BASE_URL}/sys/file/webupload/oss/upload?uploadPath=flow/pr`"
                      :headers="{token: $cookie.get('token')}"
                      :on-preview="(file, fileList) => {$window.open((file.response && file.response.url) || file.url)}"
                      :on-success="(response, file, fileList) => {
                         item.docList[0].attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                      }"
                      :on-remove="(file, fileList) => {
                        //item.docList[0].attachment =''
                         item.docList[0].attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                      }"
                      :before-remove="(file, fileList) => {
                        // return $confirm($i18nMy.t('确定移除')+` ${file.name}?`)
                      }"
                      :before-upload = "beforeAvatarUpload"
                      multiple
                      :limit="5"
                      :on-exceed="(files, fileList) =>{
                        $message.warning(`${$i18nMy.t('当前限制选择 5 个文件')}`)
                      }"
                      :file-list="attachmentsArra[item.id][item.docList[0].id]">
                      <el-button :disabled="!item.edit" style="padding: 5px 5px;" round size="small" type="primary" >{{$i18nMy.t('选择文件')}}</el-button>
                    </el-upload>
              </td>
              <!-- <td style="width: 80px;">
                <el-select  size="small" multiple v-model="item.docList[0].linkToItems" v-if="item.edit" :placeholder="$i18nMy.t('请选择')">
                  <el-option v-for="item in detailInfo" :key="item.serialNumber" :label="item.serialNumber"
                    :value="item.serialNumber">
                  </el-option>
                </el-select>
                <span v-else>
                  {{item.docList[0].linkToItems.join(',')}}
                </span>
              </td>
              <td >
                  {{item.docList[0].uploadedBy}}
              </td>
              <td class="my-right" style="width: 80px;">
                  {{item.docList[0].uploadedDate}}
              </td> -->
              <!-- <td  width="30px">
                <el-button v-if="item.edit" :disabled="item.docList.length ==1" type="danger" size="small" icon="el-icon-minus" @click="delDoc(index,0)" class="operationButton"></el-button>
              </td> -->
              <td  width="120px" align="center" style="padding-left: 10px">
                <el-button v-if="item.edit" type="success" size="small" icon="el-icon-check" @click="confirmTabListGroup(index)" style="float: left; "></el-button>
                <el-button v-if="!item.edit" type="primary" size="small" icon="el-icon-edit" @click="changeTabListGroup(index)" style="float: left; "></el-button>
                <el-button v-if="item.edit" type="danger" size="small" icon="el-icon-delete" @click="delTabListGroup(index)" style="margin-left:10px; float: left; "></el-button>
              </td>
              </tr>
<!-- 第一行  -->
              <tr class="data-content"  style="background-color: #fff3cf;" v-for="(item2, index2) in item.docList.slice(1)" :key="'index2_'+index2"  >
                <!-- <td >
                    <el-select  size="small" v-model="item2.documentType" v-if="item.edit" :placeholder="$i18nMy.t('请选择')">
                      <el-option v-for="item in $dictUtils.getDictList('pr_document_type')" :key="item.value" :label="item.label"
                        :value="item.value">
                      </el-option>
                    </el-select>
                    <span v-else>
                      {{$dictUtils.getDictLabel("pr_document_type",item2.documentType, '-')}}
                    </span>
                </td> -->
                <td colspan="3" style="max-width: 120px; min-width: 110px; text-align: left;" >
                  <el-upload :class="item2.attachment.split('|').length<5?'':'hide'" :disabled="!item.edit"
                    :action="`${$http.BASE_URL}/sys/file/webupload/oss/upload?uploadPath=flow/pr`"
                        :headers="{token: $cookie.get('token')}"
                        :on-preview="(file, fileList) => {$window.open((file.response && file.response.url) || file.url)}"
                        :on-success="(response, file, fileList) => {
                           item2.attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                        }"
                        :on-remove="(file, fileList) => {
                           // item2.attachment =''
                           item2.attachment = fileList.map(item => (item.response && item.response.url) || item.url).join('|')
                        }"
                        :before-remove="(file, fileList) => {
                          // return $confirm($i18nMy.t('确定移除')+` ${file.name}?`)
                        }"
                        :before-upload = "beforeAvatarUpload"
                        multiple
                        :limit="5"
                        :on-exceed="(files, fileList) =>{
                          $message.warning(`${$i18nMy.t('当前限制选择 5 个文件')}`)
                        }"
                        :file-list="attachmentsArra[item.id][item2.id]">
                        <el-button :disabled="!item.edit" style="padding: 5px 30px;" round size="small" type="primary" >{{$i18nMy.t('选择文件')}}</el-button>
                      </el-upload>
                </td>
                <!-- <td >
                  <el-select  multiple size="small" v-model="item2.linkToItems" v-if="item.edit" :placeholder="$i18nMy.t('请选择')">
                    <el-option v-for="item in detailInfo" :key="item.serialNumber" :label="item.serialNumber"
                      :value="item.serialNumber">
                    </el-option>
                  </el-select>
                  <span v-else>
                    {{item2.linkToItems.join(',')}}
                  </span>
                </td> -->
                <!-- <td >
                    {{item2.uploadedBy}}
                </td>
                <td class="my-right">
                    {{item2.uploadedDate}}
                </td> -->
                <!-- <td class="width-50">
                  <el-button v-if="item.edit" type="danger" size="small" icon="el-icon-minus" @click="delDoc(index,index2+1)" class="operationButton"></el-button>
                </td> -->
              </tr>
              <!-- <tr style="background-color: #fff3cf; border-bottom: 1px solid #EBEEF5;">
                <td colspan="7" style="padding: 5px 0px 5px 5px;">
                 <el-button size="small" :disabled="!item.edit" round @click="addDocList(index)" type="primary" icon="el-icon-plus" style="float: left;margin-left: 10px;padding: 5px 5px;" ></el-button>
                </td>
              </tr> -->
              <tr class="head-background-color head2-height">
                <td width="1%"  style="background-color: #FFFFFF;border:none"></td>
                <td width="2%" class="first-td">{{$i18nMy.t('序号')}}</td>
                <td colspan="1">{{$i18nMy.t('物品')}}</span></td>
                <td colspan="1">{{$i18nMy.t('品牌名称')}} - {{$i18nMy.t('型号')}}</td>
                <!-- <td>{{$i18nMy.t('型号')}}</td> -->
                <td width="105px">{{$i18nMy.t('单价')}}</td>
                <td width="65px">VAT(%)</td>
                <td width="105px">{{$i18nMy.t('单价(含VAT)')}}</td>
                <td>{{$i18nMy.t('请求数量')}}</td>
                <!-- <td><font color="red">*</font>MOQ</td> -->
                <td width="170px" >{{$i18nMy.t('预计到货日期')}}</td>
                <!-- <td>{{$i18nMy.t('预计最晚到货日期')}}</td> -->
                <td width="45px" colspan="1"><font color="red">*</font>{{$i18nMy.t('采纳')}}</td>
                <td width="" colspan="5"><font color="red">*</font>{{$i18nMy.t('原因')}}</td>
              </tr>
              <tr class="data-content" v-for="(item3, index3) in item.detailInfo" :key="'index3_'+index3">
                <td style="background-color: #FFFFFF;border:none"></td>
                <td class="first-td">{{item3.serialNumber}}</td>
                <td colspan="1"><span class="my-span">{{item3.item}}</span></td>
                <td colspan="1"><span class="my-span">{{item3.brandName}} - {{item3.modelNo}}</span></td>
                <!-- <td> {{item3.modelNo}} </td> -->
                <td class="my-right">
                  <el-input  v-on:input="calculationPrice(index)" v-only-num.float="item3" disabled size="small" v-if="item.edit" v-model="item3.unitPrice"  ></el-input>
                  <span v-else>
                    {{$common.toThousands(item3.unitPrice)}}
                  </span>
                </td>
                <td class="my-right">
                  <el-input  v-on:input="calculationPrice(index)" v-only-num.float="item3"  size="small" v-if="item.edit" v-model="item3.vat"  ></el-input>
                  <span v-else>
                    {{item3.vat}}
                  </span>
                </td>
                <td class="my-right">
                  <el-input  v-on:input="calculationPrice(index)" v-only-num.float="item3"  size="small" v-if="item.edit" v-model="item3.vatUnitPrice"  ></el-input>
                  <span v-else>
                    {{$common.toThousands(item3.vatUnitPrice)}}
                  </span>
                </td>
                <td> {{item3.quantity}} - {{item3.uom}}</td>
                <!-- <td class="my-right">
                  <el-input v-on:input="calculationPrice(index)"  v-only-num="item3"  size="small" v-if="item.edit" v-model="item3.moq" ></el-input>
                  <span v-else>
                    {{$common.toThousands(item3.moq)}}
                  </span>
                </td> -->
                <td class="my-right">
                  <el-date-picker  size="small"  v-if="item.edit"  v-model="item3.expectArrivalDate" type="date"
                    value-format="yyyy-MM-dd" :placeholder="$i18nMy.t('选择日期时间')">
                  </el-date-picker>
                  <span v-else>
                    {{$common.parseTime(item3.expectArrivalDate, '{y}-{m}-{d}')}}
                  </span>
                </td>
                <!-- <td class="my-right">
                  <el-date-picker  size="small"  v-if="item.edit"  v-model="item3.expectLastArrivalDate" type="date"
                    value-format="yyyy-MM-dd" :placeholder="$i18nMy.t('选择日期时间')">
                  </el-date-picker>
                  <span v-else>
                    {{item3.expectLastArrivalDate}}
                  </span>
                </td> -->

                <td colspan="1">
                  <el-form size="small" :model="inputForm" ref="inputFormFA" :disabled="!((status=='start'&&procDefKey=='prpo')||(parentForm=='TaskForm'&&isFA))" >
                    <el-checkbox @change="awardedChange(item,item3.serialNumber)" :disabled="item.edit"  v-model="item3.awarded" ></el-checkbox>
                  </el-form>
                </td>
                <td colspan="5">
                  <el-form size="small" :model="inputForm" ref="inputFormFA" :disabled="!((status=='start'&&procDefKey=='prpo')||(parentForm=='TaskForm'&&isFA))" >
                    <el-input type="textarea" v-on:input="inputReason(index)" size="small" maxlength="300" :placeholder="$i18nMy.t('长度不超过300')" v-model="item3.reason" :disabled="item.edit||!((status=='start'&&procDefKey=='prpo')||(parentForm=='TaskForm'&&isFA))" ></el-input>
                  </el-form>
                </td>
              </tr>
              <tr>
                <td colspan="16" style="height: 1px;background-color:#EBEEF5"></td>
              </tr>
            </tbody>
          </table>
        </div>
      </el-row>
      </el-form>
      <br/>
      <el-row :gutter="0">
      <p class="sub-title">
        {{$i18nMy.t('中标供应商和更新的单价详细信息')}}
      </p>
      <div style="width: 100%;overflow: auto;" >
        <table style="min-width: 100%;border-collapse: collapse; border:1px solid #EBEEF5" class="supplierTable"
         cellspacing="0" bordercolor="#EBEEF5" bgcolor="#fff">
          <thead>
            <tr class="head-background-color">
              <th>{{$i18nMy.t('序号')}}</th>
              <th>{{$i18nMy.t('物品')}}</th>
              <th>{{$i18nMy.t('品牌名称')}} - {{$i18nMy.t('型号')}}</th>
              <th>{{$i18nMy.t('币种')}}</th>
              <th>{{$i18nMy.t('单价')}}</th>
              <th>VAT(%)</th>
              <th>{{$i18nMy.t('单价(含VAT)')}}</th>
              <th>{{$i18nMy.t('请求数量')}}</th>
              <!-- <th>UOM</th>
              <th></th> -->
              <th>{{$i18nMy.t('报价金额')}}</th>
              <th>{{$i18nMy.t('报价金额(增值税)')}}</th>
              <th>{{$i18nMy.t('港币金额')}}</th>
              <th>{{$i18nMy.t('港币金额(增值税)')}}</th>
            </tr>
          </thead>
          <tbody v-for="(item, index) in detailInfo">
            <tr class="data-content" style="background-color: #fff3cf;">
              <td>{{item.serialNumber}}</td>
              <td><span class="my-span">{{item.item}}</span></td>
              <td><span class="my-span">{{item.brandName}} - {{item.modelNo}}</span></td>
              <td>{{item.currency}}</td>
              <td class="my-right">{{$common.toThousands(item.unitPrice)}}</td>
              <td class="my-right">{{$common.toThousands(item.vat)}}</td>
              <td class="my-right">{{$common.toThousands(item.vatUnitPrice)}}</td>
              <td >{{$common.toThousands(item.quantity)}} - {{item.uom}}</td>
              <!-- <td>{{item.uom}}</td>
              <td class="my-right"></td> -->
              <td class="my-right">{{$common.toThousands(item.docAmount)}}</td>
              <td class="my-right">{{$common.toThousands(item.docVatAmount)}}</td>
              <td class="my-right">
                <span v-if="!isNaN(item.docAmount*item.exRate)">
                  {{$common.toThousands((item.docAmount*item.exRate).toFixed(2))}}
                </span>
              </td>
              <td class="my-right">
                <span v-if="!isNaN(item.docVatAmount*item.exRate)">
                  {{$common.toThousands((item.docVatAmount*item.exRate).toFixed(2))}}
                </span>
              </td>
            </tr>
            <tr class="head-background-color head2-height">
              <td style="background-color: #FFFFFF;border:none"></td>
              <td class="first-td" width="15%">{{$i18nMy.t('供应商名称')}}</td>
              <td width="15%">{{$i18nMy.t('付款条件')}}</td>
              <td>{{$i18nMy.t('币种')}}</td>
              <td>{{$i18nMy.t('单价')}}</td>
              <td>VAT(%)</td>
              <td>{{$i18nMy.t('单价(含VAT)')}}</td>
              <!-- <td>MOQ</td> -->
              <td colspan="2">{{$i18nMy.t('预计到货日期')}}</td>
              <!-- <td>{{$i18nMy.t('预计最晚到货日期')}}</td>
              <td colspan="2">{{$i18nMy.t('相关文档')}}</td> -->
              <td>{{$i18nMy.t('采纳')}}</td>
              <td colspan="2" width="150px">{{$i18nMy.t('原因')}}</td>
            </tr>
            <tr class="data-content" v-for="(item, index) in supplierInfoByDetailInfo[item.item]" >
              <td style="background-color: #FFFFFF;border:none"></td>
              <td class="first-td"><span class="my-span">{{item.supplierName}}</span></td>
              <td ><span class="my-span">{{item.paymentTerms}}</span></td>
              <td >{{item.currency}}</td>
              <td class="my-right">{{$common.toThousands(item.unitPrice)}}</td>
              <td class="my-right">{{$common.toThousands(item.vat)}}</td>
              <td class="my-right">{{$common.toThousands(item.vatUnitPrice)}}</td>
              <!-- <td class="my-right">{{item.moq}}</td> -->
              <td colspan="2" class="my-right">{{$common.parseTime(item.expectArrivalDate, '{y}-{m}-{d}')}}</td>
              <!-- <td class="my-right">{{item.expectLastArrivalDate}}</td>
              <td colspan="2" >
                <a v-for="(file, index2) in item.relatedQuotation"
                  @click="$window.open(item.fileUrlList[index2])"
                  class="el-upload-list__item-name my_a" style="width: 100%;">
                  {{file}}
                </a>
              </td> -->
              <td ><el-checkbox :disabled="true" v-model="item.awarded"></el-checkbox></td>
              <td colspan="2" width="150px"><span class="my-span">{{item.reason}}</span></td>
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
        procDefKey: 'prpo',
        taskDefKey: '',
        parentForm: '',
        isFC: false,
        isFA: false,
        status: '',
        isCopy: false,
        flowStage:'start',
        detailInfo:[],
        supplierInfo:[],
        supplierInfoByDetailInfo:{},
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
          exRateT2List: [],
          vat:null,
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
      //this.init({})
    },
    methods: {
      init(query) {
        this.$dictUtils.getSqlDictList('GET_T2_EXCHANGE_RATE',{},(data1) => {
          this.exRateT2List = data1
        })
        if (query&&query.businessId) {
          Object.assign(this.$data, this.$options.data.call(this))
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
              // -------start-------
              if(!this.$common.isEmpty(this.inputForm.supplierInfo)){
                this.supplierInfo = JSON.parse(this.inputForm.supplierInfo)
                for(var i=0;i<this.supplierInfo.length;i++){
                  for(var j=0;this.supplierInfo[i].docList!=null&&j<this.supplierInfo[i].docList.length;j++){
                    if(this.supplierInfo[i].docList[j].attachment!=null){
                      if(this.supplierInfo[i].id ==null){
                        this.supplierInfo[i].id = this.$common.uuid();
                      }
                      if(this.supplierInfo[i].docList[j].id ==null){
                        this.supplierInfo[i].docList[j].id = this.$common.uuid();
                      }
                      //var item = this.supplierInfo[i].docList[j].attachment
                      let arr = this.supplierInfo[i].docList[j].attachment.split("|")

                      if(this.attachmentsArra[this.supplierInfo[i].id]==null){
                        this.attachmentsArra[this.supplierInfo[i].id]={}
                      }

                      if(this.attachmentsArra[this.supplierInfo[i].id][this.supplierInfo[i].docList[j].id]==null){
                        /* this.attachmentsArra[this.supplierInfo[i].id][this.supplierInfo[i].docList[j].id]=
                          [{name: decodeURIComponent(item.substring(item.lastIndexOf('/') + 1)), url: item}] */
                          this.attachmentsArra[this.supplierInfo[i].id][this.supplierInfo[i].docList[j].id] = []
                          for (var k=0; k<arr.length; k++) {
                            var item=arr[k] //this.supplementaryDoc[i].attachment
                            console.log(item)
                            this.attachmentsArra[this.supplierInfo[i].id][this.supplierInfo[i].docList[j].id].push({name: decodeURIComponent(item.substring(item.lastIndexOf('/') + 1)), url: item})
                          }
                      }
                    }
                  }
                }
              }
              this._getSupplierInfoByDetailInfoList()
              this._getSupplierArrivalDate()
              this._updateDetailInfoDocUnitPrice()
              // -------end--------
              this.loading = false
            })
          })
        }else{
          Object.assign(this.$data, this.$options.data.call(this))
        }
        this.procDefKey = query.procDefKey
        this.taskDefKey = query.taskDefKey
        if (query.status) {
          this.status = query.status
        }
        if (query.taskDefKey && query.taskDefKey.indexOf('FC')>0) {
          this.isFC = true
        }
        if (query.taskDefKey && query.taskDefKey.indexOf('FA')>0) {
          this.isFA = true
        }
        if (query.parentForm) {
          this.parentForm = query.parentForm
        }
      },
      currencyChange(obj){
        if(this.supplierInfo.length>0&&
          !this.$common.isEmpty(obj.currency)){
          var _pThis=this
          if (_pThis.exRateT2List.length==0){ // T2 的汇率没拿过来，则从本地字典拿
            _pThis.exRateT2List = this.$dictUtils.getDictList('pr_currency_rate')
          }
          var dict= this.$common.find(_pThis.exRateT2List ,function(e){return e.label == obj.currency})
          obj.exRate= parseFloat(dict==null?1:dict.value)
        }
      },

      beforeAvatarUpload:function(file){
          const isLt50M = file.size / 1024 / 1024 < 50;                         //限制文件大小50M

          if (!isLt50M) {
              this.$message.error('文件大小不能超过 50MB!');
          }
          return isLt50M
      },

      checkForm(){
        if(this.supplierInfo.length == 0){
           this.$message.warning($i18nMy.t('供应商列表不能为空'))
           return false
        }
        /* for(var i=0;i<this.supplierInfo.length;i++){
          if(this.supplierInfo[i].edit){
            this.$message.warning($i18nMy.t('供应商列表还有未保存的记录'))
            return false;
          }
        } */
        if (!this.checkSupplierList()) {
          return false
        }
        var awardedSize=0
        for(var i=0;i<this.supplierInfo.length;i++){
          for(var j=0;j<this.supplierInfo[i].detailInfo.length;j++){
            if(this.supplierInfo[i].detailInfo[j].awarded){
              awardedSize++
            }
          }
        }
        if(awardedSize != this.detailInfo.length && (this.procDefKey == 'prpo' || this.isFA)){ // IT资产流程
          this.$message.warning($i18nMy.t('请选择供应商'))
          return false;
        }
        return true
      },

      checkSupplierList(){
        for(var i=0; i<this.supplierInfo.length; i++){
          if(this.supplierInfo[i].edit){
            this.$message.warning($i18nMy.t('供应商列表还有未保存的记录'))
            return false
          }
        }
        return true
      },

      // 表单提交
      saveForm(callBack) {
        if(!this.checkForm()){
          return
        }
        this.inputForm.detailInfo=JSON.stringify(this.detailInfo)
        this.inputForm.supplierInfo=JSON.stringify(this.supplierInfo)
        this.inputForm.totalBaseAmount=this.inputForm.exRate*this.inputForm.totalContractAmount

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
        var obj={attachment:'',documentType:"Quotation",uploadedBy:this.$store.state.user.loginName,uploadedDate:this.$common.formatDate(new Date())}
        var uuid=this.$common.uuid();
        this.attachmentsArra[uuid]={}
        var tmpObj = {id: uuid, edit:true,docListSize:2,docList:[obj],detailInfo:JSON.parse(JSON.stringify(this.detailInfo))}
        for(var i=0;i<tmpObj.detailInfo.length;i++){
          tmpObj.detailInfo[i].moq=1
          tmpObj.detailInfo[i].vat=parseFloat(this.inputForm.vat||0)
          tmpObj.detailInfo[i].unitPrice = ""
          tmpObj.detailInfo[i].vatUnitPrice = ""
        }
        this.supplierInfo.push(tmpObj)
      },
      addDocList(index){
        var obj={attachment:'',documentType:"Quotation",uploadedBy:this.$store.state.user.loginName,uploadedDate:this.$common.formatDate(new Date())}
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
          obj.supplierName = supplierInfo.supplierName
          obj.paymentTerms = supplierInfo.paymentTerms
          obj.currency = supplierInfo.currency
          obj.unitPrice= supplierInfo.detailInfo[i].unitPrice
          obj.vat= supplierInfo.detailInfo[i].vat
          obj.vatUnitPrice = supplierInfo.detailInfo[i].vatUnitPrice
          obj.moq = supplierInfo.detailInfo[i].moq
          obj.expectArrivalDate = supplierInfo.detailInfo[i].expectArrivalDate
          obj.expectLastArrivalDate = supplierInfo.detailInfo[i].expectLastArrivalDate
          obj.awarded = supplierInfo.detailInfo[i].awarded
          obj.reason = supplierInfo.detailInfo[i].reason
          var fileList=[]
          var fileUrlList=[]
          for(var j=0;j<supplierInfo.docList.length;j++){
            if(supplierInfo.docList[j].linkToItems && supplierInfo.docList[j].linkToItems.indexOf(supplierInfo.detailInfo[i].serialNumber)>=0){
              var pathIndex=supplierInfo.docList[j].attachment.lastIndexOf("/")+1
              var fileName=supplierInfo.docList[j].attachment.substr(pathIndex)
              fileList.push(fileName)
              fileUrlList.push(supplierInfo.docList[j].attachment)
            }
          }
          obj.relatedQuotation =fileList
          obj.fileUrlList=fileUrlList
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
      _setDetailInfoAmount(obj){
        obj.docAmount = parseFloat((obj.unitPrice*obj.realQuantity).toFixed(2))
        obj.docVatAmount = parseFloat((obj.vatUnitPrice*obj.realQuantity).toFixed(2))
      },
      _updateDetailInfoDocUnitPrice(){
        for(var i=0;i<this.detailInfo.length;i++){
          var obj=this.detailInfo[i]
          obj.supplierName = ''
          obj.unitPrice = null
          obj.vat = null
          obj.vatUnitPrice = null
          obj.docUnitPrice = 0
          obj.docAmount  = 0
          obj.docVatAmount = 0
          obj.exRate = null
          obj.currency = null
        }
        this.inputForm.totalVatContractAmount = 0
        this.inputForm.totalContractAmount = 0
        this.inputForm.totalVatBaseAmount =0
        this.inputForm.totalBaseAmount =0
        for(var i=0;i<this.supplierInfo.length;i++){
          if(!this.supplierInfo[i].edit){
            for(var j=0;j<this.supplierInfo[i].detailInfo.length;j++){
              if(this.supplierInfo[i].detailInfo[j].awarded){
                var serialNumber = this.supplierInfo[i].detailInfo[j].serialNumber
                var obj=this.$common.find(this.detailInfo,
                   function(e){return e.serialNumber== serialNumber})
                obj.supplierName = this.supplierInfo[i].supplierName
                obj.unitPrice = this.supplierInfo[i].detailInfo[j].unitPrice
                obj.vat = this.supplierInfo[i].detailInfo[j].vat
                obj.exRate = this.supplierInfo[i].exRate
                obj.currency = this.supplierInfo[i].currency
                obj.vatUnitPrice = this.supplierInfo[i].detailInfo[j].vatUnitPrice
                var size = parseInt(this.supplierInfo[i].detailInfo[j].moq||"0")
                if(size < this.supplierInfo[i].detailInfo[j].quantity){
                  size=parseInt(this.supplierInfo[i].detailInfo[j].quantity)
                }
                obj.realQuantity=size
                this._setDetailInfoAmount(obj)
                this.inputForm.totalContractAmount += (obj.docAmount||0)
                this.inputForm.totalVatContractAmount += (obj.docVatAmount||0)
                this.inputForm.totalVatBaseAmount +=
                  parseFloat((obj.exRate*(obj.docVatAmount||0)).toFixed(2))
                this.inputForm.totalBaseAmount +=
                  parseFloat((obj.exRate*(obj.docAmount||0)).toFixed(2))
              }
            }
          }
        }
        this.inputForm.totalVatContractAmount = parseFloat(this.inputForm.totalVatContractAmount.toFixed(2))
        this.inputForm.totalContractAmount = parseFloat(this.inputForm.totalContractAmount.toFixed(2))

        this.inputForm.totalVatBaseAmount = parseFloat(this.inputForm.totalVatBaseAmount.toFixed(2))
        this.inputForm.totalBaseAmount = parseFloat(this.inputForm.totalBaseAmount.toFixed(2))
        this.inputForm.contractCurrency =  this.detailInfo[0].currency
        this.inputForm.exRate = this.detailInfo[0].exRate
        for(var i=0;i< this.detailInfo.length;i++){
          if(this.detailInfo[i].currency !=null &&
             this.detailInfo[i].currency != this.inputForm.contractCurrency){
            this.inputForm.contractCurrency="other"
            this.inputForm.exRate=null
            this.inputForm.totalVatContractAmount = ""
            this.inputForm.totalContractAmount = ""
          }
        }
      },
      awardedChange(supplierInfo,itemSerialNumber){
        for(var j=0;j<supplierInfo.detailInfo.length;j++){
          if(supplierInfo.detailInfo[j].serialNumber == itemSerialNumber &&
             supplierInfo.detailInfo[j].awarded){
              if(this.$common.isEmpty(supplierInfo.detailInfo[j].unitPrice)&&
                 this.$common.isEmpty(supplierInfo.detailInfo[j].vatUnitPrice)) {
                   this.$nextTick(() => {
                     supplierInfo.detailInfo[j].awarded=false
                     this.$message.warning($i18nMy.t('被采纳的报价价格不能为空'))
                   })
                   return
              }
          }
        }
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
      inputReason(index){
        this._getSupplierInfoByDetailInfoList()
      },
      calculationPrice(index){
        var originalPrice =0
        var originalVatPrice =0
        for(var i=0;i<this.supplierInfo[index].detailInfo.length;i++){
          this.supplierInfo[index].detailInfo[i].vat = parseFloat(this.supplierInfo[index].detailInfo[i].vat||"0")
          /* 先输入不含税单价
          if(isNaN(this.supplierInfo[index].detailInfo[i].unitPrice) || this.supplierInfo[index].detailInfo[i].unitPrice == ''){
            this.supplierInfo[index].detailInfo[i].unitPrice = ''
            this.supplierInfo[index].detailInfo[i].vatUnitPrice = ''
          }else{
            this.supplierInfo[index].detailInfo[i].vatUnitPrice = this.supplierInfo[index].detailInfo[i].unitPrice * (100+this.supplierInfo[index].detailInfo[i].vat)/100
          } */
          /*先输入含税单价 start*/
          if(isNaN(this.supplierInfo[index].detailInfo[i].vatUnitPrice) || this.supplierInfo[index].detailInfo[i].vatUnitPrice == ''){
            this.supplierInfo[index].detailInfo[i].unitPrice = ''
            this.supplierInfo[index].detailInfo[i].vatUnitPrice = ''
          }else{
            this.supplierInfo[index].detailInfo[i].unitPrice = parseFloat((this.supplierInfo[index].detailInfo[i].vatUnitPrice / ((100+this.supplierInfo[index].detailInfo[i].vat)/100)).toFixed(2))
          }
          /*先输入含税单价 end*/
          if(isNaN(this.supplierInfo[index].detailInfo[i].vatUnitPrice)){
            this.supplierInfo[index].detailInfo[i].vatUnitPrice = ''
          }
          if(isNaN(this.supplierInfo[index].detailInfo[i].quantity)){
            this.supplierInfo[index].detailInfo[i].quantity = ''
          }
          originalPrice+=parseFloat(this.supplierInfo[index].detailInfo[i].unitPrice||"0")*
            parseInt(this.supplierInfo[index].detailInfo[i].quantity||"0")
          originalVatPrice+=parseFloat(this.supplierInfo[index].detailInfo[i].vatUnitPrice||"0")*
            parseInt(this.supplierInfo[index].detailInfo[i].quantity||"0")
        }
        originalPrice = parseFloat(originalPrice.toFixed(2))
        this.supplierInfo[index].originalPrice=originalPrice

        originalVatPrice = parseFloat(originalVatPrice.toFixed(2))
        this.supplierInfo[index].originalVatPrice=originalVatPrice

      },
      _getSupplierArrivalDate(){
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
           return
        }
        for(var i=0;i<this.supplierInfo[index].docList.length;i++){
          if(this.$common.isEmpty(this.supplierInfo[index].currency)){
             this.$message.warning($i18nMy.t('币种不能为空'))
             return
          }
          /* if(this.$common.isEmpty(this.supplierInfo[index].docList[i].documentType)){
             this.$message.warning($i18nMy.t('文档类型不能为空'))
             return
          } */
          // debugger
          if(this.$common.isEmpty(this.supplierInfo[index].docList[i].attachment)){
             this.$message.warning($i18nMy.t('附件不能为空'))
             return
          }
          /* if(this.$common.isEmpty(this.supplierInfo[index].docList[i].linkToItems)){
             this.$message.warning($i18nMy.t('关联项目不能为空'))
             return
          } */
        }
        /* var unitPriceNull=0
        var vatUnitPriceNull=0 */
        for(var i=0;i<this.supplierInfo[index].detailInfo.length;i++){
          /* if(this.$common.isEmpty(this.supplierInfo[index].detailInfo[i].unitPrice)){
             unitPriceNull++
          }
          if(this.$common.isEmpty(this.supplierInfo[index].detailInfo[i].vatUnitPrice)){
             vatUnitPriceNull++
          }
          if(this.$common.isEmpty(this.supplierInfo[index].detailInfo[i].vat)){
             this.$message.warning($i18nMy.t('VAT不能为空'))
             return
          }
          if(this.$common.isEmpty(this.supplierInfo[index].detailInfo[i].moq)){
             this.$message.warning($i18nMy.t('MOQ不能为空'))
             return
          } */
          if(!this.$common.isEmpty(this.supplierInfo[index].detailInfo[i].vatUnitPrice)&&this.$common.isEmpty(this.supplierInfo[index].detailInfo[i].unitPrice)){
             this.$message.warning($i18nMy.t('物品')+this.supplierInfo[index].detailInfo[i].item+$i18nMy.t('单价不能为空'))
             return
          }

          if(!this.$common.isEmpty(this.supplierInfo[index].detailInfo[i].unitPrice)&&
             !this.$common.isEmpty(this.supplierInfo[index].detailInfo[i].vatUnitPrice)&&
             !this.$common.isEmpty(this.supplierInfo[index].detailInfo[i].vat)){
                if(Math.abs(this.supplierInfo[index].detailInfo[i].vatUnitPrice -
                    (this.supplierInfo[index].detailInfo[i].unitPrice*
                    (100+parseFloat(this.supplierInfo[index].detailInfo[i].vat||0))/100))>0.1){
                  this.$message.warning($i18nMy.t('单价*(1+VAT) != 单价(VAT)'))
                  return
                }
             }
        }
        /* if(!(unitPriceNull==0||vatUnitPriceNull==0)){
          this.$message.warning($i18nMy.t('单价不能为空'))
          return
        } */
        this.supplierInfo[index].edit =false
        this._getSupplierInfoByDetailInfoList()
        this._updateDetailInfoDocUnitPrice()
        this._getSupplierArrivalDate()
        this.detailInfo.splice(this.detailInfo.length) // 触发更新
        this.supplierInfo.splice(this.supplierInfo.length)

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
      },
      updateSupplierByDetailInfo(){  // 用于处理page1的设备列表变更对page2的影响
        for(var j=0; j<this.supplierInfo.length; j++){
          var supplierDetailInfo = JSON.parse(JSON.stringify(this.detailInfo))
          for(var i=0; i<supplierDetailInfo.length; i++){
            for(var k=0; k<this.supplierInfo[j].detailInfo.length; k++){
              // 在this.supplierInfo[j].detailInfo找到对应的item的k，用于处理page1中的detailInfo有增删的情况，
              // 此时page1中的detailInfo与原this.supplierInfo[j].detailInfo的length可能不一致
              if (supplierDetailInfo[i]['item'] == this.supplierInfo[j].detailInfo[k]['item']){
                // debugger
                for(var key in this.supplierInfo[j].detailInfo[k]){// 把this.supplierInfo[j].detailInfo['k']的专有值付给新的中间值supplierDetailInfo

                  if (key == 'supplierName' || key == 'currency' || key == 'unitPrice' || key == 'vat' || key == 'vatUnitPrice' || key == 'expectArrivalDate'
                    || key == 'docAmount' || key == 'docVatAmount' || key == 'baseAmount' || key == 'baseVatAmount' || key == 'awarded' || key == 'reason') {
                      //if (key == 'brandName' || key == 'modelNo' || key == 'item' || key == 'quantity' || key == 'uom' || key == 'expectArrivalDate' ) {
                        supplierDetailInfo[i][key] = this.supplierInfo[j].detailInfo[k][key]
                        //this.supplierInfo[j].detailInfo[i][key] = this.detailInfo[i][key]
                      //}
                  }
                }
              }
            }
          }
          this.supplierInfo[j].detailInfo = supplierDetailInfo //新的中间值supplierDetailInfo塞回this.supplierInfo[j].detailInfo
          this.calculationPrice(j)
        }
        this._getSupplierArrivalDate()  // 更新每个供应商的商品的最早到货时间
        this._getSupplierInfoByDetailInfoList() // 更新商品（1）-供应商（N）表
        this._updateDetailInfoDocUnitPrice()  // 更新商品（1）-供应商（N）表的总价格
      }
    }
  }
</script>
<style scoped lang = "less">
  .el-form-item {
      margin-bottom: 10px;
  }
  .my-right{
    text-align: right;
  }
  .my-left{
    text-align: left;
  }
  .my-center{
    text-align: center;
  }
  .width-150{
    /* min-width: 130px;
    width : 130px; */
    text-align: center;
  }
  .width-100{
   /* min-width: 90px;
    width : 90px; */
    text-align: center;
  }
  .width-200{
    /* min-width: 180px;
    width : 180px; */
    text-align: center;
  }
  .width-400{
    /* min-width: 300px;
    width : 300px; */
  }
  .width-50{
    /* min-width: 50px;
    width : 50px; */
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
  .my-span{
     white-space:pre-wrap;
     word-break:break-all;
     word-wrap:break-word;
     line-break: anywhere;
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
  .my_a:focus,.my_a:hover {
      cursor: pointer;
  }
  ::v-deep .el-checkbox__input.is-disabled.is-checked .el-checkbox__inner::after {
    border-color: white;
  }

  ::v-deep .el-checkbox__input.is-disabled.is-checked .el-checkbox__inner {
    background-color: #8a8a8a;
  }
</style>
