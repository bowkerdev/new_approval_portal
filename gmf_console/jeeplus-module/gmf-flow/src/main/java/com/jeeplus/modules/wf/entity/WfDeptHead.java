/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.wf.entity;

import com.jeeplus.modules.sys.entity.User;

import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * Department Head SettingEntity
 * @author zhimi
 * @version 2022-11-18
 */
@Data
public class WfDeptHead extends DataEntity<WfDeptHead> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="Site", dictType="apply_site_code", align=2, sort=5)
	private String site;		// Site
	@ExcelField(title="Department", align=2, sort=6)
	private String dept;		// Department
	@ExcelField(title="Approver", fieldType=User.class, value="approver.name", align=2, sort=7)
	private User approver;		// Approver
	
	public WfDeptHead() {
		super();
	}
	
	public WfDeptHead(String id){
		super(id);
	}
}