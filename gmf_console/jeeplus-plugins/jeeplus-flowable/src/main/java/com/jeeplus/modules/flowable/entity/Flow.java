package com.jeeplus.modules.flowable.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.TimeUtils;
import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.modules.flowable.utils.Variable;
import lombok.Data;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 工作流Entity
 * @author liugaofeng
 * @version 2018-08-01
 */
@Data
public class Flow extends DataEntity<Flow> {

	private static final long serialVersionUID = 1L;

	private String taskId;        // 任务编号
	private String taskName;    // 任务名称
	private String taskDefKey;    // 任务定义Key（任务环节标识）

	private String formType; //表单类型
	private boolean formReadOnly; //表单只读

	private String procInsId;    // 流程实例ID
	private String procDefId;    // 流程定义ID
	private String procDefKey;    // 流程定义Key（流程定义标识）
	private String procDefName;
	private String status;

	private String businessTable;    // 业务绑定Table
	private String businessId;        // 业务绑定ID

	private String title;        // 任务标题

	private boolean isNextGatewaty;        // 下一个任务节点是否是网关

	private  TaskComment comment; //评论
	@JsonIgnore
	private Task task;            // 任务对象
	@JsonIgnore
	private ProcessDefinition procDef;    // 流程定义对象
	@JsonIgnore
	private ProcessInstance procIns;    // 流程实例对象
	private HistoricProcessInstance hisProcIns;    // 历史流程实例对象
	private HistoricTaskInstance histTask; // 历史任务
	private HistoricActivityInstance histIns;    //历史活动任务

	private String assignee; // 任务执行人编号
	private String assigneeName; // 任务执行人名称

	private Variable vars;        // 流程变量
//	private Variable taskVars; 	// 流程任务变量

	private Date beginDate;    // 开始查询日期
	private Date endDate;    // 结束查询日期

	private List<Flow> list; // 任务列表


	private String formUrl;//流程表单地址
	private String lastTaskDefKey;// 上一处理环节

	public Flow() {
		super();
	}


	public String getTaskId() {
		if (taskId == null && task != null) {
			taskId = task.getId();
		}
		return taskId;
	}

	public String getTaskName() {
		if (taskName == null && task != null) {
			taskName = task.getName();
		}
		return taskName;
	}


	public String getTaskDefKey() {
		if (taskDefKey == null && task != null) {
			taskDefKey = task.getTaskDefinitionKey();
		}
		return taskDefKey;
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getTaskCreateDate() {
		if (task != null) {
			return task.getCreateTime();
		}
		return null;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getTaskEndDate() {
		if (histTask != null) {
			return histTask.getEndTime();
		}
		return null;
	}



	public String getProcDefName() {
		if (!StringUtils.isBlank(procDefName)) {
			return procDefName;
		}
		return procDef == null ? "" : procDef.getName();
	}


	public void setProcIns(ProcessInstance procIns) {
		this.procIns = procIns;
		if (procIns != null && procIns.getBusinessKey() != null && procIns.getBusinessKey().contains(":")) {
			String[] ss = procIns.getBusinessKey().split(":");
			setBusinessTable(ss[0]);
			setBusinessId(ss[1]);
		} else if (procIns != null && procIns.getBusinessKey() != null) {
			setBusinessId(procIns.getBusinessKey());
		}
	}

	public void setFinishedProcIns(HistoricProcessInstance procIns) {
		//this.procIns = procIns;
		if (procIns != null && procIns.getBusinessKey() != null && procIns.getBusinessKey().contains(":")) {
			String[] ss = procIns.getBusinessKey().split(":");
			setBusinessTable(ss[0]);
			setBusinessId(ss[1]);
		} else if (procIns != null && procIns.getBusinessKey() != null) {
			setBusinessId(procIns.getBusinessKey());
		}
	}


	public String getProcDefId() {
		if (procDefId == null && task != null) {
			procDefId = task.getProcessDefinitionId();
		}
		return procDefId;
	}


	public String getProcInsId() {
		if (procInsId == null && task != null) {
			procInsId = task.getProcessInstanceId();
		}
		return procInsId;
	}


	public String getAssignee() {
		if (assignee == null && task != null) {
			assignee = task.getAssignee();
		}
		return assignee;
	}


	/**
	 * 通过Map设置流程变量值
	 *
	 * @param map
	 */
	public void setVars(Map<String, Object> map) {
		this.vars = new Variable(map);
	}


	/**
	 * 获取流程定义KEY
	 *
	 * @return
	 */
	public String getProcDefKey() {
		if (StringUtils.isBlank(procDefKey) && StringUtils.isNotBlank(procDefId)) {
			procDefKey = StringUtils.split(procDefId, ":")[0];
		}
		return procDefKey;
	}


	/**
	 * 获取过去的任务历时
	 *
	 * @return
	 */
	public String getDurationTime() {
		/*if (histIns != null && histIns.getDurationInMillis() != null) {
			return TimeUtils.toTimeString(histIns.getDurationInMillis()*1000*60).replace("天", "days ").replace("时", "hrs ").replace("分", "min ").replace("秒", "s");
		}*/
		if (histIns != null && histIns.getStartTime() != null && histIns.getEndTime() != null) {
			long times=(histIns.getEndTime().getTime() - histIns.getStartTime().getTime())/1000;
			if(times > 0){
				long day = times/3600/24;
				long hour =times%(3600*24)/3600;
				long minute = times%(3600)/60;
				long second = times%60;
				return day+"days "+hour+"hrs "+minute+"min "+second+"s";
			}
			
		}		
		return "";
	}


	@Override
	public void preInsert() {

	}

	@Override
	public void preUpdate() {

	}


}


