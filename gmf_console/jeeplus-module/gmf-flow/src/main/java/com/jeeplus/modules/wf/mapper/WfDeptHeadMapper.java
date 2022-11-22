/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.wf.mapper;

import org.springframework.stereotype.Repository;
import com.jeeplus.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.jeeplus.modules.wf.entity.WfDeptHead;

/**
 * Department Head SettingMAPPER接口
 * @author zhimi
 * @version 2022-11-18
 */
@Mapper
@Repository
public interface WfDeptHeadMapper extends BaseMapper<WfDeptHead> {

}