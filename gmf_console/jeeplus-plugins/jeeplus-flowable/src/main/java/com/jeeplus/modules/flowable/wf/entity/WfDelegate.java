/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.wf.entity;

import com.jeeplus.modules.sys.entity.User;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 流程委托设置Entity
 * @author Jack
 * @version 2021-07-01
 */
@Data
public class WfDelegate extends DataEntity<WfDelegate> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="委托人", fieldType=User.class, value="owner.name", align=2, sort=7)
	private User owner;		// 委托人
	@ExcelField(title="代理人", fieldType=User.class, value="delegate.name", align=2, sort=8)
	private User delegate;		// 代理人
	@ExcelField(title="流程类型", align=2, sort=9)
	private String flowType;		// 流程类型
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="开始时间", align=2, sort=10)
	private Date startTime;		// 开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="结束时间", align=2, sort=11)
	private Date endTime;		// 结束时间
	@ExcelField(title="proc_def_key", align=2, sort=12)
	private String procDefKey;		// proc_def_key
	@ExcelField(title="proc_ins_id", align=2, sort=13)
	private String procInsId;		// proc_ins_id
	@ExcelField(title="task_id", align=2, sort=14)
	private String taskId;		// task_id
	@ExcelField(title="task_name", align=2, sort=15)
	private String taskName;		// task_name
	@ExcelField(title="申请单号", align=2, sort=16)
	private String applicationNo;		// 申请单号
	
	public WfDelegate() {
		super();
	}
	
	public WfDelegate(String id){
		super(id);
	}
}