/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.oa.mispolicy.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.flow.oa.mispolicy.entity.OaMisPolicyInst;
import com.jeeplus.modules.flow.oa.mispolicy.mapper.OaMisPolicyInstMapper;

/**
 * MIS PolicyService
 * @author zhimi
 * @version 2022-04-21
 */
@Service
@Transactional(readOnly = true)
public class OaMisPolicyInstService extends CrudService<OaMisPolicyInstMapper, OaMisPolicyInst> {


	public OaMisPolicyInst get(String id) {
		OaMisPolicyInst oaMisPolicyInst = super.get(id);
		return oaMisPolicyInst;
	}

	public List<OaMisPolicyInst> findList(OaMisPolicyInst oaMisPolicyInst) {
		return super.findList(oaMisPolicyInst);
	}

	public Page<OaMisPolicyInst> findPage(Page<OaMisPolicyInst> page, OaMisPolicyInst oaMisPolicyInst) {
		return super.findPage(page, oaMisPolicyInst);
	}

	@Transactional(readOnly = false)
	public void save(OaMisPolicyInst oaMisPolicyInst) {
		super.save(oaMisPolicyInst);
	}

	@Transactional(readOnly = false)
	public void delete(OaMisPolicyInst oaMisPolicyInst) {
		super.delete(oaMisPolicyInst);
	}

}