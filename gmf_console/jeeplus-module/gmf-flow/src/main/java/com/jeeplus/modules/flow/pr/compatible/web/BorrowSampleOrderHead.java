/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.pr.compatible.web;

import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import lombok.Data;

/**
 * 板衣借还审批Entity
 * @author Lewis
 * @version 2018-08-23
 */
@Data
public class BorrowSampleOrderHead {
	private Long orderId;//**
	private String orderNo;		// 任务单编号
	private String materialType;		// 物料类型
	private String approveType;		// 审批类型
	private String approveStatus;		// 审批状态
    private String groupIds;//***
	private String groupNames;		// 组名
	private String fromWarehouseName;		// 起始仓库
	private String targetWarehouseName;		// 目标仓库
	private String actualApproverName;		// 实际审批人
	private Date approveTime;		// 审批时间
	private String createByName;		// 创建人
    private String createByLoginName; //**
	private Date createTime;		// 创建时间
	private String updateByName;		// 上次更新人
	private Date updateTime;		// 上次更新时间
	
	private String factory;
	private String borrowBy;
	private String toLocation;
	private String remark;
	private Date estimateReturnedTime;
	
	private List<BorrowSampleOrderLine> borrowSampleOrderLineList = Lists.newArrayList();		// 子表列表
	
	public BorrowSampleOrderHead() {
		super();
	}
}