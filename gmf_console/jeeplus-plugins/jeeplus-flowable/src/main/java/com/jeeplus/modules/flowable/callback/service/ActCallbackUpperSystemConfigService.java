/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.callback.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.CrudService;
import com.jeeplus.modules.flowable.callback.entity.ActCallbackUpperSystemConfig;
import com.jeeplus.modules.flowable.callback.mapper.ActCallbackUpperSystemConfigMapper;

/**
 * 配置设置Service
 * @author LJ
 * @version 2020-09-25
 */
@Service
@Transactional(readOnly = true)
public class ActCallbackUpperSystemConfigService extends CrudService<ActCallbackUpperSystemConfigMapper, ActCallbackUpperSystemConfig> {

	public ActCallbackUpperSystemConfig get(String id) {
		return super.get(id);
	}
	
	public List<ActCallbackUpperSystemConfig> findListByOaKey(String oaKey) {
		return mapper.findListByOaKey(oaKey);
	}
	
	public List<ActCallbackUpperSystemConfig> findList(ActCallbackUpperSystemConfig actCallbackUpperSystemConfig) {
		return super.findList(actCallbackUpperSystemConfig);
	}
	
	public Page<ActCallbackUpperSystemConfig> findPage(Page<ActCallbackUpperSystemConfig> page, ActCallbackUpperSystemConfig actCallbackUpperSystemConfig) {
		return super.findPage(page, actCallbackUpperSystemConfig);
	}
	
	@Transactional(readOnly = false)
	public void save(ActCallbackUpperSystemConfig actCallbackUpperSystemConfig) {
		super.save(actCallbackUpperSystemConfig);
	}
	
	@Transactional(readOnly = false)
	public void delete(ActCallbackUpperSystemConfig actCallbackUpperSystemConfig) {
		super.delete(actCallbackUpperSystemConfig);
	}
	
}