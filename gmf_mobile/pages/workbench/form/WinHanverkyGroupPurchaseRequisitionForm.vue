<template>
	<view>
		<view class="bg-white fixed flex  text-center" style="height: 45px;line-height: 45px;">
			<view class="cu-item flex-sub" :class="0 == tabIndex?'text-blue cur':'tab-default-title'" data-id="0" @tap="formTabSelect">
				主要信息
			</view>
			<view class="cu-item flex-sub" :class="1 == tabIndex?'text-blue cur':'tab-default-title'" data-id="1"  @tap="formTabSelect">
				补充文件
			</view>
		</view>
		<view v-show="0 === tabIndex">
			<view class="cu-card information">
				<view class="cu-form-group" style="padding: 0px;">
					<view class="title wd-100" style="text-align: center;font-weight: bold;font-size: 25px;padding-top: 20px;padding-bottom: 20px;">
						Win Hanverky Group Purchase Requisition Form
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('申请单号')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.applicationNo}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入申请单号')" v-model="inputForm.applicationNo" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('申请人')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.createBy.name}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入申请人')" v-model="inputForm.createBy.id" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('申请时间')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.createDate}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入申请时间')" v-model="inputForm.createDate" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('申请人部门')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.createByOffice.name}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入申请人部门')" v-model="inputForm.createByOffice.id" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('项目名称')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.projectName}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入项目名称')" v-model="inputForm.projectName" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('采购地区')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.applySiteCode}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入采购地区')" v-model="inputForm.applySiteCode" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('用户部门')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.requesterDepartment.name}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入用户部门')" v-model="inputForm.requesterDepartment.id" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('用户姓名')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.requester}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入用户姓名')" v-model="inputForm.requester" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('费用类型')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.expenseType}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入费用类型')" v-model="inputForm.expenseType" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('要求到货时间')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.expectArrivalDate}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入要求到货时间')" v-model="inputForm.expectArrivalDate" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('签约方公司')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.legalEntityLabel}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入签约方公司')" v-model="inputForm.legalEntity" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('成本中心')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.costCenterLabel}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入成本中心')" v-model="inputForm.costCenter" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('固定资产类型')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.assetGroupLabel}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入固定资产类型')" v-model="inputForm.assetGroup" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('技术支持部门')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.technicalAdvisorLabel}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入技术支持部门')" v-model="inputForm.technicalAdvisor" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left" style="height: 40px;line-height: 40px;">{{$i18nMy.t('是否预算内')}}</text>
						<text class="float-right">
							<radio-group>
								<label class="radio">
									 <radio class='radio' 
										 style="transform:scale(0.7)" 
										 :class="inputForm.isBudget=='1'?'checked':''" 
										 :checked="inputForm.isBudget=='1'?true:false" 
										 value="1" 
										 :disabled="formReadOnly">
									 </radio>
									 Yes
								</label>
								<label class="radio">
									<radio class='radio' 
										style="transform:scale(0.7)" 
										:class="inputForm.isBudget=='0'?'checked':''" 
										:checked="inputForm.isBudget=='0'?true:false" 
										value="0" 
										:disabled="formReadOnly">
									</radio>
									No
								</label>
							</radio-group>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('申购优先级')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.requestRiorityLabel}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入申购优先级')" v-model="inputForm.requestRiority" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('汇率')}}</text>
						<text class="float-right" v-if="formReadOnly">{{parseFloat(inputForm.exRate || 0).toFixed(2)}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入汇率')" v-model="inputForm.exRate" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('合同币种')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.contractCurrency}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入合同币种')" v-model="inputForm.contractCurrency" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('合同总价')}}</text>
						<text class="float-right">
							<text style="width: 38%;float: left; text-align: right;">{{parseFloat(inputForm.totalContractAmount || 0).toFixed(2)}}</text>
							<text style="width: 38%;float: left; text-align: right;">{{parseFloat(inputForm.totalContractAmount*inputForm.vat).toFixed(2)}}</text>
							<text style="width: 24%;float: left; text-align: right;">{{inputForm.contractCurrency}}</text>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('基础币种')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.baseCurrency}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入基础币种')" v-model="inputForm.baseCurrency" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('基础币种总价')}}</text>
						<text class="float-right">
							<text style="width: 38%;float: left; text-align: right;">{{parseFloat(inputForm.exRate*inputForm.totalContractAmount).toFixed(2)}}</text>
							<text style="width: 38%;float: left; text-align: right;">{{parseFloat(inputForm.exRate*inputForm.vat*inputForm.totalContractAmount).toFixed(2)}}</text>
							<text style="width: 24%;float: left; text-align: right;">{{inputForm.baseCurrency}}</text>
						</text>
					</view>
				</view>
			</view>
			<view class="cu-bar bg-white solid-bottom mg-t-10" >
				<view class="action">
					<text class="cuIcon-titles text-orange"></text>  <text style="font-size: 20px;font-weight: bold;">{{$i18nMy.t('设备采购清单')}}</text>
				</view>
			</view>
			<view class="cu-card" v-if="null == detailInfo || 0 == detailInfo.length">
				<view class="cu-form-group supplementary-document" style="padding-top: 10px;padding-bottom: 10px;">
					<view style="width: 100%;height: 100%;text-align: center;">
						没有设备采购清单
					</view>
				</view>
			</view>
			<view class="cu-card">
				<view class="cu-form-group supplementary-document"  v-for="(item,index) in detailInfo" :key="index" style="padding-top: 10px;padding-bottom: 10px;">
					<view style="width: 100%;height: 100%;">
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('序号')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.serialNumber}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('物品')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.item}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('品牌')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.brandName}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('型号')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.modelNo}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('供应商名称')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.supplierName}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">包含VAT</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.includedVat}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('市场价格')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.unitPrice || 0).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('报价单单价')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.docUnitPrice || 0).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('数量')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.quantity}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('单位')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.uom}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('报价单币种总数')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.docAmount || 0).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">报价单币种总数(VAT)</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.docVatAmount || 0).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('基础报价总数')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.docAmount*inputForm.exRate).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">基础报价总数(VAT)</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.docVatAmount*inputForm.exRate).toFixed(2)}}</view>
						</view>
					</view>
				</view>

			</view>
			<view class="cu-bar bg-white solid-bottom mg-t-10" >
				<view class="action">
					<text class="cuIcon-titles text-orange"></text> <text style="font-size: 20px;font-weight: bold;">{{$i18nMy.t('申请理由')}}</text>
				</view>
			</view>
			<view class="cu-card">
				<view class="cu-form-group">
					<view style="width: 100%;height: 100%;">
						<view style="width: 100%;height: 40px;line-height: 40px;font-weight: bold;">{{$i18nMy.t('申购目的')}}</view>
						<view class="detail-info-row">
							<textarea v-model="inputForm.purchasePurpose" :placeholder="$i18nMy.t('请填写申购目的')" style="margin-top: 0px;" :disabled="formReadOnly"></textarea>
						</view>
					</view>
				</view>
				<view class="cu-form-group">
					<view style="width: 100%;height: 100%;">
						<view style="width: 100%;height: 40px;line-height: 40px;font-weight: bold;">{{$i18nMy.t('投资回报分析')}}</view>
						<view class="detail-info-row">
							<textarea v-model="inputForm.roi" placeholder="请填写ROI" style="margin-top: 0px;" :disabled="formReadOnly"></textarea>
						</view>
					</view>
				</view>
				<view class="cu-form-group">
					<view style="width: 100%;height: 100%;">
						<view style="width: 100%;height: 40px;line-height: 40px;font-weight: bold;">{{$i18nMy.t('预算外说明')}}</view>
						<view class="detail-info-row">
							<textarea v-model="inputForm.noBudgetExplain" :placeholder="$i18nMy.t('请填写预算外说明')" style="margin-top: 0px;" :disabled="formReadOnly"></textarea>
						</view>
					</view>
				</view>
				<view class="cu-form-group">
					<view style="width: 100%;height: 100%;">
						<view style="width: 100%;height: 40px;line-height: 40px;font-weight: bold;">{{$i18nMy.t('支付说明')}}</view>
						<view class="detail-info-row">
							<textarea v-model="inputForm.paymentSpecial" :placeholder="$i18nMy.t('请填写支付说明')" style="margin-top: 0px;" :disabled="formReadOnly"></textarea>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view v-show="1 === tabIndex">
			<view class="cu-card information">
				<view class="cu-form-group" style="padding: 0px;">
					<view class="title wd-100" style="text-align: center;font-weight: bold;font-size: 25px;padding-top: 20px;padding-bottom: 20px;">
						Win Hanverky Group Purchase Requisition Form
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('申请人')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.createBy.name}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入申请人')" v-model="inputForm.createBy.id" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('申请人部门')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.createByOffice.name}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入申请人部门')" v-model="inputForm.createByOffice.id" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('用户姓名')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.requester}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入用户姓名')" v-model="inputForm.requester" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('用户部门')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.requesterDepartment.name}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入用户部门')" v-model="inputForm.requesterDepartment.id" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('固定资产类型')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.assetGroupLabel}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入固定资产类型')" v-model="inputForm.assetGroup" ></input>
						</text>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title wd-100">
						<text class="float-left">{{$i18nMy.t('成本中心')}}</text>
						<text class="float-right" v-if="formReadOnly">{{inputForm.costCenterLabel}}</text>
						<text class="float-right" v-if="!formReadOnly">
							<input :placeholder="$i18nMy.t('请输入成本中心')" v-model="inputForm.costCenter" ></input>
						</text>
					</view>
				</view>
			</view>
			<view class="cu-bar bg-white solid-bottom mg-t-10" >
				<view class="action">
					<text class="cuIcon-titles text-orange"></text>  <text style="font-size: 20px;font-weight: bold;">{{$i18nMy.t('采购清单')}}</text>
				</view>
			</view>
			<view class="cu-card" v-if="null == detailInfo || 0 == detailInfo.length">
				<view class="cu-form-group supplementary-document" style="padding-top: 10px;padding-bottom: 10px;">
					<view style="width: 100%;height: 100%;text-align: center;">
						没有采购清单
					</view>
				</view>
			</view>
			<view class="cu-card">
				<view class="cu-form-group supplementary-document"  v-for="(item,index) in detailInfo" :key="index" style="padding-top: 10px;padding-bottom: 10px;">
					<view style="width: 100%;height: 100%;">
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('序号')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.serialNumber}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('物品')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.item}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('品牌')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.brandName}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('单价')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.unitPrice || 0).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('文件单价')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.docUnitPrice || 0).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('数量')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.quantity}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('单位')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.uom}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('文档报价总数')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.docAmount || 0).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">文档报价总数VAT</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{parseFloat(item.docVatAmount || 0).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('基础报价总数')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right"> {{parseFloat(item.docAmount*inputForm.exRate).toFixed(2)}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">基础报价总数VAT</view>
							<view class="title detail-info-title detail-info-title-right text-right"> {{parseFloat(item.docVatAmount*inputForm.exRate).toFixed(2)}}</view>
						</view>
					</view>
				</view>
			</view>
			<view class="cu-bar bg-white solid-bottom mg-t-10" >
				<view class="action">
					<text class="cuIcon-titles text-orange"></text>  <text style="font-size: 20px;font-weight: bold;">{{$i18nMy.t('补充文件')}}</text>
				</view>
			</view>
			<view class="cu-card" v-if="null == supplierInfo || 0 == supplierInfo.length">
				<view class="cu-form-group supplementary-document" style="padding-top: 10px;padding-bottom: 10px;">
					<view style="width: 100%;height: 100%;text-align: center;">
						没有补充文件
					</view>
				</view>
			</view>
			<view class="cu-card">
				<view class="cu-form-group supplementary-document"  v-for="(item,index) in supplierInfo" :key="index" style="padding-top: 10px;padding-bottom: 10px;">
					<view style="width: 100%;height: 100%;">
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('序号')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.serialNumber}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('描述')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.description}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('文件类型')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.documentType}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('附件')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">
								<a :href="item.attachment" download="">{{item.attachmentFile}}</a>
							</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('上传者部门')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.uploaderDepartment}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('上传者')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.uploadedBy}}</view>
						</view>
						<view class="detail-info-row">
							<view class="title detail-info-title detail-info-title-left">{{$i18nMy.t('上传日期')}}</view>
							<view class="title detail-info-title detail-info-title-right text-right">{{item.uploadedDate}}</view>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				tabIndex:0,
				dictList:{
					legalEntity:this.getDictListWithKey(this.$dictUtils.getDictList('all_company')),
					costCenter:this.getDictListWithKey(this.$dictUtils.getDictList('cost_center')),
					assetGroup:this.getDictListWithKey(this.$dictUtils.getDictList('asset_group')),
					technicalAdvisor:this.$dictUtils.getDictList('technical_advisor'),
					requestRiority:this.$dictUtils.getDictList('request_priority'),
				},
				supplierInfo:[],
				detailInfo:[],
				inputForm: {
				  id: 'b24b20ab898544d19d1da4cc428a3e3d',
				  createBy:{
					  id:'',
					  name:''
				  },
				  createDate:'',
				  createByOffice: {
					id: '',
					name:''
				  },
				  applicationNo: '',
				  projectName: '',
				  applySiteCode: '',
				  requesterDepartment: {
					id: '',
					name:''
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
		  businessId: {
		    type: String,
		    default: ''
		  },
		  formReadOnly: {
		    type: Boolean,
		    default: false
		  }
		},
		components: {
		},
		watch: {
		  'businessId': {
		    handler (newVal) {
				var _that = this;
		      if (_that.businessId) {
		        _that.init(_that.businessId)
		      } else {
		        _that.$nextTick(() => {
		          // this.$refs.inputForm.reset()
		        })
		      }
		    },
		    immediate: true,
		    deep: false
		  }
		},
		methods: {
			formTabSelect : function (e) {
				var _that = this;
				_that.tabIndex = parseInt(e.currentTarget.dataset.id);
			},
			getDictListLabel : function (key,value){
				var _that = this;
				var data = _that.dictList[key];
				for(var i=0;i<data.length;i++){
					if(value == data[i].value){
						return data[i].label;
					}
				}
				return '-';
			},
			getDictListWithKey : function (dicts) {
			  for (var i=0; i<dicts.length; i++) {
			    dicts[i].label = dicts[i].value + ' - ' + dicts[i].label;
			  }
			  return dicts || []
			},
			formatSelectLabel : function(){
				var _that = this;
				for(var key in _that.dictList){
					_that.inputForm[key+"Label"] = _that.getDictListLabel(key,_that.inputForm[key]);
				}
			},
			formatAttachmentURL : function(){
				var _that = this;
				for(var i=0;i<_that.supplierInfo.length;i++){
					_that.supplierInfo[i].attachmentFile =  (_that.supplierInfo[i].attachment || '').substring((_that.supplierInfo[i].attachment || '').lastIndexOf('/') + 1)
				}
			},
			init : function(id){
				var _that = this;
				this.$http.get(`/flow/pr/oaPrNew/queryById?id=`+id).then(({data}) => {
				  _that.inputForm = _that.recover(_that.inputForm, data.oaPrNew);
				  _that.detailInfo = JSON.parse(_that.inputForm.detailInfo);
				  _that.supplierInfo = JSON.parse(_that.inputForm.supplementaryDoc);
				  _that.formatAttachmentURL();
				  _that.formatSelectLabel();
				})
			},
			saveForm : function(callback) {
				var _that = this;
				_that.inputForm.detailInfo = JSON.stringify(_that.detailInfo);
				_that.inputForm.supplierInfo = JSON.stringify(_that.supplierInfo);
				_that.inputForm.totalBaseAmount = _that.inputForm.exRate*_that.inputForm.totalContractAmount;
				_that.$http.post(`/flow/pr/oaPrNew/save`,_that.inputForm).then(({data}) => {
					if (data && data.success) {
					  callback(data.businessTable, data.businessId)
					}
				}).catch((e)=>{
					
				});
			}
		}
	}
</script>

<style lang="scss">
.information .cu-form-group .title{
	height: 100%;
	line-height: 1;
	padding-right: 0px;
}
.supplementary-document .detail-info-title{
	height: 100%;
	line-height: 1;
}
.information .cu-form-group .title .float-left{
	width: 100px;
	font-weight: bold;
}
.information .cu-form-group .title .float-right{
	width: calc(100% - 100px);
	text-align: right;
}
.tab-default-title{
	background-color: #f5f7fa;
}
.text-right{
	text-align: right;
}
.wd-100{
	width: 100%;
}
.float-right{
	float: right;
}
.float-left{
	float: left;
}
.detail-info-title{
	width: 50%;
	padding-right: 0px; 
	float: left;
}
.detail-info-row{
	width: 100%;
	min-height: 25px;
	display: flex;
	align-items: center;
	justify-content: flex-start;
}
.detail-info-title-left{
	width: 40%;
	word-break: break-word;
	text-align: left;
}
.detail-info-title-right{
	width: 60%;
	word-break: break-word;
	text-align: right;
}
.mg-t-10{
	margin-top:10px;
}
.uni-list-cell {
    justify-content: flex-start
}
.cu-form-group{

    uni-checkbox-group{
		text-align: right;
	}
	uni-radio-group {
		text-align: right;
	}
	uni-checkbox, uni-radio {
	    position: relative;
	    margin-top: 7px;
		margin-left:7px;
		margin-bottom: 7px;
	}
}
</style>
