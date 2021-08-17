package com.jeeplus.modules.flowable.vo;

import java.util.Date;

import org.flowable.task.api.TaskInfo;

import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;

import lombok.Data;

/**
 * 当前活动任务节点
 */
@Data
public class ActVo {
    private String id;
    private String name;
    private String assignee;
    private String assigneeName;
    private String executionId;
    private String taskDefinitionKey;
    private Date createTime;
    private String processDefinitionId;
    private String processInstanceId;
    private String processDefKey;
 
}
