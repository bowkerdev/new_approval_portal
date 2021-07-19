/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.wf.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.flowable.wf.entity.WfUserGroup;
import com.jeeplus.modules.flowable.wf.mapper.WfUserGroupMapper;

/**
 * 流程用户组设置Service
 * @author zhimi
 * @version 2020-09-22
 */
@Service
@Transactional(readOnly = true)
public class WfUserGroupService extends CrudService<WfUserGroupMapper, WfUserGroup> {

	public WfUserGroup get(String id) {
		return super.get(id);
	}
	
	public List<WfUserGroup> findList(WfUserGroup wfUserGroup) {
		return super.findList(wfUserGroup);
	}
	
	public Page<WfUserGroup> findPage(Page<WfUserGroup> page, WfUserGroup wfUserGroup) {
		return super.findPage(page, wfUserGroup);
	}
	
	@Transactional(readOnly = false)
	public void save(WfUserGroup wfUserGroup) {
		super.save(wfUserGroup);
	}
	
	@Transactional(readOnly = false)
	public void delete(WfUserGroup wfUserGroup) {
		super.delete(wfUserGroup);
	}
	
}