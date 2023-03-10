/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.oa.mispolicy.mapper;

import org.springframework.stereotype.Repository;
import com.jeeplus.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.jeeplus.modules.flow.oa.mispolicy.entity.OaMisPolicyInst;

/**
 * MIS PolicyMAPPER接口
 * @author zhimi
 * @version 2022-04-21
 */
@Mapper
@Repository
public interface OaMisPolicyInstMapper extends BaseMapper<OaMisPolicyInst> {
	
}