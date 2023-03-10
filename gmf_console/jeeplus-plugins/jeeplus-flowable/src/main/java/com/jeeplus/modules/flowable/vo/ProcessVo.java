package com.jeeplus.modules.flowable.vo;

import lombok.Data;

import java.util.Date;
import java.util.Map;

import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.modules.flowable.wf.entity.WfDelegate;

@Data
public class ProcessVo extends DataEntity<ProcessVo>{
    private String processInstanceId; // 流程实例ID
    private String processDefinitionId; // 流程定义ID
    private String processDefinitionKey; // 流程定义ID
    private String processDefinitionName; // 流程名称
    private String title; // 流程标题
    private String activityId;
    private int version; // 流程版本
    private Map vars; // 流程变量
    private String applyUserName;
    private Date startTime; // 流程开始时间
    private Date endTime; //流程结束时间
    private String taskName; //流程当前节点名称
    private String deleteReason; //流程作废原因
    private HisTaskVo hisTask; // 历史流程节点
    private TaskVo task = new TaskVo(); //流程当前节点
    private ActVo act; //流程当前节点

    private int code; // 流程状态码
    private String status; //流程状态
    private String level; //状态级别，用于控制在前台显示的颜色

    public String getId(){ // 流程实例id 作为列表的id
        return processInstanceId;
    }

    public void setProcessStatus(ProcessStatus processStatus){
        this.code = processStatus.getCode ();
        this.status = processStatus.getStatus ();
        this.level = processStatus.getLevel ();
    }
}
