/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.wf.entity;

import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.entity.User;

import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * 流程用户组设置Entity
 * @author zhimi
 * @version 2020-09-22
 */
@Data
public class WfUserGroup extends DataEntity<WfUserGroup> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="公司", fieldType=Office.class, value="company.name", align=2, sort=7)
	private Office company;		// 公司
	@ExcelField(title="部门", fieldType=Office.class, value="department.name", align=2, sort=8)
	private Office department;		// 部门
	@ExcelField(title="流程标识", align=2, sort=9)
	private String procDefKey;		// 流程标识
	@ExcelField(title="组标识", align=2, sort=10)
	private String userGroup;		// 组标识
	@ExcelField(title="组标识辅助码", align=2, sort=11)
	private String userGroupCode;		// 组标识辅助码
	@ExcelField(title="用户", fieldType=User.class, value="user.name", align=2, sort=12)
	private User user;		// 用户
	
	public WfUserGroup() {
		super();
	}
	
	public WfUserGroup(String id){
		super(id);
	}

	public Office getCompany() {
		return company;
	}

	public void setCompany(Office company) {
		this.company = company;
	}

	public Office getDepartment() {
		return department;
	}

	public void setDepartment(Office department) {
		this.department = department;
	}

	public String getProcDefKey() {
		return procDefKey;
	}

	public void setProcDefKey(String procDefKey) {
		this.procDefKey = procDefKey;
	}

	public String getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(String userGroup) {
		this.userGroup = userGroup;
	}

	public String getUserGroupCode() {
		return userGroupCode;
	}

	public void setUserGroupCode(String userGroupCode) {
		this.userGroupCode = userGroupCode;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}