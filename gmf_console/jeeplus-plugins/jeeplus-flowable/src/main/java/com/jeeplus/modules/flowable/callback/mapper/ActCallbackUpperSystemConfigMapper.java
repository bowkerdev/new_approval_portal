/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.callback.mapper;

import org.springframework.stereotype.Repository;
import com.jeeplus.core.persistence.BaseMapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import com.jeeplus.modules.flowable.callback.entity.ActCallbackUpperSystemConfig;

/**
 * *MAPPER接口
 * @author LJ
 * @version 2021-08-13
 */
@Mapper
@Repository
public interface ActCallbackUpperSystemConfigMapper extends BaseMapper<ActCallbackUpperSystemConfig> {
	List<ActCallbackUpperSystemConfig> findListByOaKey(String oaKey);
}