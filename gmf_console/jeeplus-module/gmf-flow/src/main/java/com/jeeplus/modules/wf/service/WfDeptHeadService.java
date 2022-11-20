/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.wf.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.wf.entity.WfDeptHead;
import com.jeeplus.modules.wf.mapper.WfDeptHeadMapper;

/**
 * Department Head SettingService
 * @author zhimi
 * @version 2022-11-18
 */
@Service
@Transactional(readOnly = true)
public class WfDeptHeadService extends CrudService<WfDeptHeadMapper, WfDeptHead> {

	public WfDeptHead get(String id) {
		return super.get(id);
	}
	
	public List<WfDeptHead> findList(WfDeptHead wfDeptHead) {
		return super.findList(wfDeptHead);
	}
	
	public Page<WfDeptHead> findPage(Page<WfDeptHead> page, WfDeptHead wfDeptHead) {
		return super.findPage(page, wfDeptHead);
	}
	
	@Transactional(readOnly = false)
	public void save(WfDeptHead wfDeptHead) {
		super.save(wfDeptHead);
	}
	
	@Transactional(readOnly = false)
	public void delete(WfDeptHead wfDeptHead) {
		super.delete(wfDeptHead);
	}
	
}