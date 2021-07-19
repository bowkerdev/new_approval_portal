/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.wf.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.flowable.wf.entity.WfDelegate;
import com.jeeplus.modules.flowable.wf.mapper.WfDelegateMapper;

/**
 * 流程委托设置Service
 * @author Jack
 * @version 2021-07-01
 */
@Service
@Transactional(readOnly = true)
public class WfDelegateService extends CrudService<WfDelegateMapper, WfDelegate> {

	public WfDelegate get(String id) {
		return super.get(id);
	}
	
	public List<WfDelegate> findList(WfDelegate wfDelegate) {
		return super.findList(wfDelegate);
	}
	
	public Page<WfDelegate> findPage(Page<WfDelegate> page, WfDelegate wfDelegate) {
		return super.findPage(page, wfDelegate);
	}
	
	@Transactional(readOnly = false)
	public void save(WfDelegate wfDelegate) {
		super.save(wfDelegate);
	}
	
	@Transactional(readOnly = false)
	public void delete(WfDelegate wfDelegate) {
		super.delete(wfDelegate);
	}
	
}