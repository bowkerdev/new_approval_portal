/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.wf.mapper;

import org.springframework.stereotype.Repository;
import com.jeeplus.core.persistence.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.jeeplus.modules.flowable.wf.entity.WfUserGroup;

/**
 * 流程用户组设置MAPPER接口
 * @author zhimi
 * @version 2020-09-22
 */
@Mapper
@Repository
public interface WfUserGroupMapper extends BaseMapper<WfUserGroup> {

}