/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.jeeplus.core.persistence.BaseMapper;
import com.jeeplus.modules.flowable.entity.Flow;
import com.jeeplus.modules.flowable.vo.ProcessVo;

/**
 * 审批Mapper接口
 *
 * @author jeeplus
 * @version 2017-05-16
 */
@Mapper
@Repository
public interface FlowMapper extends BaseMapper<Flow> {
    int updateProcInsIdByBusinessId(Flow act);
    int updateProcStatusByProcInsId(@Param("procInsId")String procInsId,@Param("procStatus")String procStatus,@Param("businessTable")String businessTable);
    String getSequence(@Param("seqName") String seqName, @Param("bizCode") String bizCode);
    String getLatestProcessDefinitionId(@Param("procDefKey") String procDefKey);
 
    List<ProcessVo> findHistoryList(ProcessVo entity);
    List<Map> querySql(Map map);
}
