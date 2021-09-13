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
public class TaskVo {
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

    public TaskVo(TaskInfo task){
        this.id = task.getId ();
        this.name = task.getName ();
        this.assignee = task.getAssignee ();
        User user = UserUtils.get(task.getAssignee ());
        if (user != null) {
        	this.assigneeName = user.getLoginName() + " : " + user.getName();
        }
        this.executionId = task.getExecutionId ();
        this.taskDefinitionKey = task.getTaskDefinitionKey ();
        this.createTime = task.getCreateTime ();
        this.processDefinitionId = task.getProcessDefinitionId ();
        this.processInstanceId = task.getProcessInstanceId ();
    }
    
    public TaskVo(ActVo task){
        this.name = task.getName ();
        this.assignee = task.getAssignee ();
        this.assigneeName = task.getAssigneeName();
    }
    
    public TaskVo(){ 
    }
}
