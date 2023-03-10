/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.oa.mispolicy.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.flow.oa.mispolicy.entity.OaMisPolicyConfig;
import com.jeeplus.modules.flow.oa.mispolicy.mapper.OaMisPolicyConfigMapper;

/**
 * MIS PolicyService
 * @author zhimi
 * @version 2022-04-19
 */
@Service
@Transactional(readOnly = true)
public class OaMisPolicyConfigService extends CrudService<OaMisPolicyConfigMapper, OaMisPolicyConfig> {

	public OaMisPolicyConfig get(String id) {
		return super.get(id);
	}
	
	public List<OaMisPolicyConfig> findList(OaMisPolicyConfig oaMisPolicyConfig) {
		return super.findList(oaMisPolicyConfig);
	}
	
	public Page<OaMisPolicyConfig> findPage(Page<OaMisPolicyConfig> page, OaMisPolicyConfig oaMisPolicyConfig) {
		return super.findPage(page, oaMisPolicyConfig);
	}
	
	@Transactional(readOnly = false)
	public void save(OaMisPolicyConfig oaMisPolicyConfig) {
		super.save(oaMisPolicyConfig);
	}
	
	@Transactional(readOnly = false)
	public void delete(OaMisPolicyConfig oaMisPolicyConfig) {
		super.delete(oaMisPolicyConfig);
	}
	
}