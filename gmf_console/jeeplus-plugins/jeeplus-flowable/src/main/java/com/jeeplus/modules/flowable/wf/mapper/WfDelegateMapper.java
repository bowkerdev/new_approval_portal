/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.wf.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.jeeplus.core.persistence.BaseMapper;
import com.jeeplus.modules.flowable.wf.entity.WfDelegate;

/**
 * 流程委托设置MAPPER接口
 * @author Jack
 * @version 2021-07-01
 */
@Mapper
@Repository
public interface WfDelegateMapper extends BaseMapper<WfDelegate> {
	
	public List<WfDelegate> findNowList(WfDelegate entity);
	
	public List<WfDelegate> findOverlapList(WfDelegate entity);
	
}