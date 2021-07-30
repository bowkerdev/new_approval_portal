/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.pr.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.flow.pr.entity.OaPrNew;
import com.jeeplus.modules.flow.pr.mapper.OaPrNewMapper;

/**
 * PR申请单Service
 * @author zhimi
 * @version 2021-07-30
 */
@Service
@Transactional(readOnly = true)
public class OaPrNewService extends CrudService<OaPrNewMapper, OaPrNew> {


	public OaPrNew get(String id) {
		OaPrNew oaPrNew = super.get(id);
		return oaPrNew;
	}

	public List<OaPrNew> findList(OaPrNew oaPrNew) {
		return super.findList(oaPrNew);
	}

	public Page<OaPrNew> findPage(Page<OaPrNew> page, OaPrNew oaPrNew) {
		return super.findPage(page, oaPrNew);
	}

	@Transactional(readOnly = false)
	public void save(OaPrNew oaPrNew) {
		super.save(oaPrNew);
	}

	@Transactional(readOnly = false)
	public void delete(OaPrNew oaPrNew) {
		super.delete(oaPrNew);
	}

}