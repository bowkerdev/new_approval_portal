/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.pr.mapper;

import org.springframework.stereotype.Repository;
import com.jeeplus.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.jeeplus.modules.flow.pr.entity.OaPrNew;

/**
 * PR申请单MAPPER接口
 * @author zhimi
 * @version 2021-06-29
 */
@Mapper
@Repository
public interface OaPrNewMapper extends BaseMapper<OaPrNew> {
	
}