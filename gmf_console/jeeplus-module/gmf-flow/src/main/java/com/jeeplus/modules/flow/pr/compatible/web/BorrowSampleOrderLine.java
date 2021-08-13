/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.pr.compatible.web;

import java.util.Date;

import lombok.Data;

/**
 * 板衣借还审批Entity
 * @author maxteng
 * @version 2018-08-23
 */
@Data
public class BorrowSampleOrderLine  {
	private String barcode;		// 条码
	private String sampleCode;		// 板衣编号
	private String sampleType;		// 板衣类型
	private String locationName;		// 架位
	private String workingNo;		// 客款号
	private String article;		// 组合色
	private String categories;		// 组别
	private String season;		// 季节
	private String size;		// 尺码
	private String bNo;		// 宝嘉款号 
	private String merchandiser;		// 跟单人
	private String status;		// 状态
	private String factory;		// 工厂
	private String remark;		// 备注
	private Date createTime;		// 创建时间
	private String createByName;  
	private Date updateTime;		// 更新时间
	private String updateByName;  
	private String materialType;		// 物料类型
	public BorrowSampleOrderLine() {
		super();
	}

}