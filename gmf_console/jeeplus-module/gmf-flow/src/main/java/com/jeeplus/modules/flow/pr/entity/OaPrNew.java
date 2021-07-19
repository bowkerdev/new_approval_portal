/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.pr.entity;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.flowable.entity.Flow;
import lombok.Data;

/**
 * PR申请单Entity
 * @author zhimi
 * @version 2021-06-29
 */
@Data
public class OaPrNew extends DataEntity<OaPrNew> {
	
	private static final long serialVersionUID = 1L;
	@NotNull(message="创建者部门不能为空")
	@ExcelField(title="创建者部门", fieldType=Office.class, value="createByOffice.name", align=2, sort=2)
	private Office createByOffice;		// 创建者部门
	@ExcelField(title="流程实例编号", align=2, sort=8)
	private String procInsId;		// 流程实例编号
	@ExcelField(title="申请单号", align=2, sort=9)
	private String applicationNo;		// 申请单号
	@ExcelField(title="项目名称", align=2, sort=10)
	private String projectName;		// 项目名称
	@ExcelField(title="所属工厂", dictType="db_type", align=2, sort=11)
	private String applySiteCode;		// 所属工厂
	@ExcelField(title="请求者部门", fieldType=Office.class, value="requesterDepartment.name", align=2, sort=12)
	private Office requesterDepartment;		// 请求者部门
	@ExcelField(title="请求者", align=2, sort=13)
	private String requester;		// 请求者
	@ExcelField(title="费用类型", dictType="db_type", align=2, sort=14)
	private String expenseType;		// 费用类型
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="要求到货时间", align=2, sort=15)
	private Date expectArrivalDate;		// 要求到货时间
	@ExcelField(title="签约公司", dictType="db_type", align=2, sort=16)
	private String legalEntity;		// 签约公司
	@ExcelField(title="成本中心", dictType="db_type", align=2, sort=17)
	private String costCenter;		// 成本中心
	@ExcelField(title="固定资产类型", dictType="db_type", align=2, sort=18)
	private String assetGroup;		// 固定资产类型
	@ExcelField(title="技术支持部门", align=2, sort=19)
	private String technicalAdvisor;		// 技术支持部门
	@ExcelField(title="预算类型", dictType="db_type", align=2, sort=20)
	private String budgetType;		// 预算类型
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="审批通过日期", align=2, sort=21)
	private Date approvedDate;		// 审批通过日期
	@ExcelField(title="是否预算内", dictType="db_type", align=2, sort=22)
	private String isBudget;		// 是否预算内
	@ExcelField(title="申购优先级", dictType="db_type", align=2, sort=23)
	private String requestRiority;		// 申购优先级
	@ExcelField(title="合同币种", dictType="db_type", align=2, sort=24)
	private String contractCurrency;		// 合同币种
	@ExcelField(title="汇率", align=2, sort=25)
	private Double exRate;		// 汇率
	@ExcelField(title="合同总价", align=2, sort=26)
	private Double totalContractAmount;		// 合同总价
	@ExcelField(title="基础币种", dictType="db_type", align=2, sort=27)
	private String baseCurrency;		// 基础币种
	@ExcelField(title="基础币种总价", align=2, sort=28)
	private Double totalBaseAmount;		// 基础币种总价
	@ExcelField(title="申购目的", align=2, sort=29)
	private String purchasePurpose;		// 申购目的
	@ExcelField(title="ROI", align=2, sort=30)
	private String roi;		// ROI
	@ExcelField(title="预算外说明", align=2, sort=31)
	private String noBudgetExplain;		// 预算外说明
	@ExcelField(title="支付说明", align=2, sort=32)
	private String paymentSpecial;		// 支付说明
	@ExcelField(title="基础明细", align=2, sort=33)
	private String detailInfo;		// 基础明细
	@ExcelField(title="补充材料", align=2, sort=34)
	private String supplementaryDoc;		// 补充材料
	@ExcelField(title="供应商材料", align=2, sort=35)
	private String supplierInfo;		// 供应商材料
	
	private Flow flow = new Flow();
	
	public OaPrNew() {
		super();
	}

	public OaPrNew(String id){
		super(id);
	}
	
}