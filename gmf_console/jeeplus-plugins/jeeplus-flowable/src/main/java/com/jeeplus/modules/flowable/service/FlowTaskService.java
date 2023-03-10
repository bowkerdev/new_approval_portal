/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.ManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.runtime.ActivityInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.api.history.HistoricVariableInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.utils.SpringContextHolder;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.BaseService;
import com.jeeplus.modules.flowable.common.cmd.BackUserTaskCmd;
import com.jeeplus.modules.flowable.constant.FlowableConstant;
import com.jeeplus.modules.flowable.entity.Flow;
import com.jeeplus.modules.flowable.entity.TaskComment;
import com.jeeplus.modules.flowable.mapper.FlowMapper;
import com.jeeplus.modules.flowable.service.converter.json.FlowModelService;
import com.jeeplus.modules.flowable.utils.FlowableUtils;
import com.jeeplus.modules.flowable.utils.ProcessDefCache;
import com.jeeplus.modules.flowable.vo.ActVo;
import com.jeeplus.modules.flowable.vo.ActionType;
import com.jeeplus.modules.flowable.vo.HisTaskVo;
import com.jeeplus.modules.flowable.vo.ProcessStatus;
import com.jeeplus.modules.flowable.vo.ProcessVo;
import com.jeeplus.modules.flowable.vo.TaskVo;
import com.jeeplus.modules.flowable.wf.entity.WfDelegate;
import com.jeeplus.modules.flowable.wf.mapper.WfDelegateMapper;
import com.jeeplus.modules.sys.entity.DictValue;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.DictUtils;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * ??????????????????Service
 *
 * @author jeeplus
 * @version 2016-11-03
 */
@Service
@Transactional(readOnly = true)
public class FlowTaskService extends BaseService {

    @Autowired
    private FlowMapper flowMapper;
    @Autowired
    private WfDelegateMapper wfDelegateMapper;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FormService formService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IdentityService identityService;
    @Autowired
    private FlowModelService flowModelService;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private FlowProcessService flowProcessService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
	 * Home ??????????????????
	 * @param act
	 * @return
	 */
	public List<ProcessVo> delegateList(Flow flow){
		List<ProcessVo> result = new ArrayList<ProcessVo>();
		
		//?????????????????????????????????
		WfDelegate wd = new WfDelegate();
		wd.setDelegate(UserUtils.getUser());
		List<WfDelegate> wdList = wfDelegateMapper.findNowList(wd);
		for (WfDelegate wfDelegate : wdList){
			Page<ProcessVo> page = new Page<ProcessVo>();
			result.addAll(this.todoList(page, flow, wfDelegate.getOwner().getId(), true).getList());
		}
		this.removeDuplicateWithOrder(result);//??????
		
		/*//???????????????
		result.addAll(this.todoListForHomePage(act,userId, null, null, false));*/
		
		return result;
	}
	
	private void removeDuplicateWithOrder(List<ProcessVo> list) {
		Set<ProcessVo> set = new HashSet<ProcessVo>();
		List<ProcessVo> newList = new ArrayList<ProcessVo>();
		for (Iterator<ProcessVo> iter = list.iterator(); iter.hasNext();) {
			ProcessVo element = iter.next();
			if (set.add(element)){
				newList.add(element);
			}
		}
		list.clear();
		list.addAll(newList);
		System.out.println("======= remove duplicate " + list);
	}
	

    /**
     * ????????????????????????
     *
     * @return
     */
    public Page<ProcessVo> todoList(Page<ProcessVo> page, Flow flow) {
        return this.todoListBySQL(page, flow, UserUtils.getUser (), false);
    }
    
    private Page<ProcessVo> todoList(Page<ProcessVo> page, Flow flow, String userId, boolean delegateFlag) {
        List<HashMap<String, String>> result = new ArrayList<HashMap<String, String>> (); 
        // =============== ???????????????????????????????????????  ===============
        TaskQuery todoTaskQuery = taskService.createTaskQuery ().taskCandidateOrAssigned(userId).active ()
                .includeProcessVariables ().orderByTaskCreateTime ().desc ();

        // ??????????????????
        if (StringUtils.isNotBlank (flow.getProcDefKey ())) {
            todoTaskQuery.processDefinitionKeyIn (Arrays.asList(flow.getProcDefKey ().split(",")));
        }
        if (flow.getBeginDate () != null) {
            todoTaskQuery.taskCreatedAfter (flow.getBeginDate ());
        }
        if (flow.getEndDate () != null) {
            todoTaskQuery.taskCreatedBefore (flow.getEndDate ());
        }
        if (StringUtils.isNotBlank (flow.getTitle ())) {
            todoTaskQuery.processVariableValueLike (FlowableConstant.TITLE, "%" + flow.getTitle () + "%");
        }

        long total = todoTaskQuery.count ();
        page.setCount (total);

        int start = page.getFirstResult ();
        List<Task> todoList = Lists.newArrayList ();
        // ????????????
        if (page.getPageSize () == -1) {//?????????
            todoList = todoTaskQuery.list ();
        } else {
            todoList = todoTaskQuery.listPage (start, page.getMaxResults ());
        }

        for (Task task : todoList) {
            Process process = SpringContextHolder.getBean (RepositoryService.class).getBpmnModel (task.getProcessDefinitionId ()).getMainProcess ();
            ProcessVo processVo = new ProcessVo ();
            TaskVo taskVo = new TaskVo (task);
            taskVo.setProcessDefKey (process.getId ());
            if (delegateFlag) {
            	taskVo.setAssignee(UserUtils.get(taskVo.getAssignee()).getName());
			}
            processVo.setTask (taskVo);
            processVo.setVars (task.getProcessVariables ());
            processVo.setProcessDefinitionName ( ProcessDefCache.get (task.getProcessDefinitionId ()).getName ());
            processVo.setVersion (ProcessDefCache.get (task.getProcessDefinitionId ()).getVersion ());
            processVo.setStatus ("?????????");
            processVo.setRemarks(flowMapper.getRemarks(task.getProcessInstanceId(), StringUtils.split(task.getProcessDefinitionId(),":")[0])); 
            page.getList ().add (processVo);
        }

        return page;

    }
    
    private Page<ProcessVo> todoListBySQL(Page<ProcessVo> page, Flow flow, User user, boolean delegateFlag) { 
        // =============== ???????????????????????????????????????  ===============
         
    	ProcessVo pvo = new ProcessVo();
    	pvo.setProcessInstanceId(flow.getProcInsId());
    	pvo.getTask().setAssignee(user.getId());
    	pvo.setStartTime(flow.getBeginDate());
    	pvo.setEndTime(flow.getEndDate());
    	pvo.setTitle(flow.getTitle());
    	pvo.setProcessDefinitionKey(flow.getProcDefKey());
    	 
    	pvo.setPage(page); 
    	  
        List<ProcessVo> todoList = flowMapper.findAllTodoList(pvo);

        for (ProcessVo processVo : todoList) {
        	Task taskInst = taskService.createTaskQuery ().taskId(processVo.getTask().getId()).includeProcessVariables().singleResult();
        	if (processVo.getTask().getAssignee()!=null && !processVo.getTask().getAssignee().equals(user.getId())) {
        		processVo.getTask().setAssigneeName(processVo.getTask().getAssigneeName()+"(Delegate to "+user.getLoginName()+" : "+user.getName()+")");
        	}
            processVo.setVars (taskInst.getProcessVariables ());
            if (processVo.getVars()!=null && processVo.getVars().get("applyUserId")!=null) {
            	User applyUser = UserUtils.get((String)processVo.getVars().get("applyUserId"));
            	if (applyUser==null) {
            		UserUtils.getByLoginName((String)processVo.getVars().get("applyUserId"));
            	}
            	if (applyUser!=null) {
            		processVo.setApplyUserName(applyUser.getLoginName() + " : " + applyUser.getName());
            	} else {
            		processVo.setApplyUserName((String)processVo.getVars().get("applyUserId"));
            	}
            }            
            processVo.setProcessDefinitionName ( ProcessDefCache.get (processVo.getTask().getProcessDefinitionId ()).getName ());
            processVo.setVersion (ProcessDefCache.get (processVo.getTask().getProcessDefinitionId ()).getVersion ());
            processVo.setStatus ("?????????");
            processVo.setRemarks(flowMapper.getRemarks(processVo.getTask().getProcessInstanceId(), StringUtils.split(processVo.getTask().getProcessDefinitionId(),":")[0])); 
            page.getList ().add (processVo);
        }

        return page;
    }

    public Page<ProcessVo> allList(Page<ProcessVo> page, ProcessVo pvo) {
    	pvo.setPage(page);
        List<ProcessVo> todoList = flowMapper.findAllList(pvo);
        for (ProcessVo processVo : todoList) {
        	System.out.println("processVo.getProcessInstanceId() = "+processVo.getProcessInstanceId());
        	HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId (processVo.getProcessInstanceId()).includeProcessVariables().singleResult ();        	 
        	
        	if (pi!=null)
        		processVo.setVars (pi.getProcessVariables ()); 
        		String applyUserId = (String)pi.getProcessVariables ().get("applyUserId");
        		if (!StringUtils.isBlank(applyUserId)) {
        			User user = UserUtils.get(applyUserId);
        			if ( user != null ) {
        				processVo.setApplyUserName(user.getLoginName() + " : " + user.getName());
        			} else {
        				processVo.setApplyUserName(applyUserId);
        			}
        		}
        	else{
        		processVo.setVars (new HashMap<String, Object>());
        	}
            processVo.setProcessDefinitionName ( ProcessDefCache.get (processVo.getTask().getProcessDefinitionId ()).getName ());
            processVo.setVersion (ProcessDefCache.get (processVo.getTask().getProcessDefinitionId ()).getVersion ());
            //processVo.setStatus (getProcessStatus(processVo.getTask().getName()));
            if ("??????".equals(processVo.getStatus())) {
            	processVo.setStatus (flowMapper.getProcessStatus(processVo.getProcessInstanceId()));
			}
            processVo.setRemarks(flowMapper.getRemarks(processVo.getTask().getProcessInstanceId(), StringUtils.split(processVo.getTask().getProcessDefinitionId(),":")[0])); 
            page.getList ().add (processVo);
        }

        return page;
    }
    
    /**
     * ????????????????????????
     *
     * @param page
     * @return
     */
    public Page<HisTaskVo> historicList(Page<HisTaskVo> page, Flow act) {
        String userId = UserUtils.getUser ().getId ();

        HistoricTaskInstanceQuery histTaskQuery = historyService.createHistoricTaskInstanceQuery ().taskAssignee (userId).finished ()
                .includeProcessVariables ().orderByHistoricTaskInstanceEndTime ().desc ();

        // ??????????????????
        
        
        if ("app_flow_list".equals(act.getProcDefKey ())) {
        	List<DictValue> dictList = DictUtils.getDictList("app_flow_list");
            List<String> distStrList = new ArrayList<String>();
            for (DictValue dictValue : dictList) {
            	distStrList.add(dictValue.getValue());
            }
        	if (distStrList.size()>0) {
                histTaskQuery.processDefinitionKeyIn (distStrList);
            }
        } else if (StringUtils.isNotBlank (act.getProcDefKey ())) {
            histTaskQuery.processDefinitionKeyIn (Arrays.asList(act.getProcDefKey ().split(",")));
        }
        
        if (act.getBeginDate () != null) {
            histTaskQuery.taskCompletedAfter (act.getBeginDate ());
        }
        if (act.getEndDate () != null) {
            histTaskQuery.taskCompletedBefore (act.getEndDate ());
        }
        if (act.getTitle () != null) {
            histTaskQuery.processVariableValueLike (FlowableConstant.TITLE, "%" + act.getTitle () + "%");
        }

        // ????????????
        List<HistoricTaskInstance> histList0 = histTaskQuery.list ();
        List<HistoricTaskInstance> histList = Lists.newArrayList (); 
        HashMap hm = new HashMap();
        
        for (HistoricTaskInstance histTask : histList0) { // ???????????????????????????
        	if (!"1".equals(hm.get(histTask.getProcessInstanceId()))) {
        		hm.put(histTask.getProcessInstanceId(), "1");
        		histList.add(histTask);
        	}
        }
        
        // ????????????
        page.setCount (histList.size ());
        page.initialize ();
        
        if (page.getMaxResults () == -1) {
            // histList = histTaskQuery.list ();
        } else {
            // histList = histTaskQuery.listPage (page.getFirstResult (), page.getMaxResults ());
        	int maxIndex = page.getFirstResult () + page.getMaxResults ();
        	if (maxIndex > histList.size()) {
        		maxIndex = histList.size();
        	}
        	histList = histList.subList(page.getFirstResult (), maxIndex);
        }

        for (HistoricTaskInstance histTask : histList) {
            HisTaskVo hisTaskVo= new HisTaskVo (histTask);
            hisTaskVo.setProcessDefinitionName ( ProcessDefCache.get (histTask.getProcessDefinitionId ()).getName ());
            hisTaskVo.setBack (isBack (histTask));
            List<Task> currentTaskList = taskService.createTaskQuery ().processInstanceId (histTask.getProcessInstanceId ()).list ();
            if(((List) currentTaskList).size () > 0){
                TaskVo currentTaskVo =  new TaskVo (currentTaskList.get (0));
                hisTaskVo.setCurrentTask (currentTaskVo);
            }else{
            	//historyService.createHistoricTaskInstanceQuery ().processInstanceId(histTask.getProcessInstanceId()).orderByTaskCreateTime().desc();
            	List<ActVo> actList = flowMapper.findLastestHiActList(histTask.getProcessInstanceId());
            	if (actList!=null && actList.size()>0){ 
            		hisTaskVo.setCurrentTask(new TaskVo (actList.get(0)));
            	}
            }

            // ????????????????????????

            List<TaskComment> commentList = this.getTaskComments (histTask.getId ());
            if (commentList.size () > 0) {
                TaskComment comment = commentList.get (commentList.size ()-1);
                hisTaskVo.setComment (comment.getMessage ());
                hisTaskVo.setLevel (comment.getLevel ());
                hisTaskVo.setType (comment.getType ());
                hisTaskVo.setStatus (comment.getStatus ());
            }
            
            // update by jaydon 20210914
            hisTaskVo.setStatus (flowMapper.getProcessStatus(histTask.getProcessInstanceId()));
            
            hisTaskVo.setRemarks(flowMapper.getRemarks(histTask.getProcessInstanceId(), StringUtils.split(histTask.getProcessDefinitionId(),":")[0])); 
            
            page.getList ().add (hisTaskVo);
        }
        return page;
    }
    
    public List<Flow> historicTaskListWithOld(String procInsId) throws Exception {
    	List<Flow> actList = Lists.newArrayList ();
    	String oldProcInsId = flowMapper.getOldProcInsId(procInsId);
    	if (!StringUtils.isBlank(oldProcInsId)) {
    		actList.addAll(this.historicTaskList(oldProcInsId));
    	}
    	actList.addAll(this.historicTaskList(procInsId));
    	return actList;
    }

    /**
     * ??????????????????????????????
     *
     * @param procInsId ????????????
     */
    public List<Flow> historicTaskList(String procInsId) throws Exception {
        List<Flow> actList = Lists.newArrayList ();
        List<HistoricActivityInstance> list = Lists.newArrayList ();
        List<HistoricActivityInstance> historicActivityInstances2 = historyService.createHistoricActivityInstanceQuery ().processInstanceId (procInsId)
                .orderByHistoricActivityInstanceStartTime ().asc ().orderByHistoricActivityInstanceEndTime ().asc ().list ();
        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances2) {
            if (historicActivityInstance.getEndTime () != null) {
                list.add (historicActivityInstance);
            }
        }

        for (HistoricActivityInstance historicActivityInstance : historicActivityInstances2) {
            if (historicActivityInstance.getEndTime () == null) {
                list.add (historicActivityInstance);
            }
        }

        for (int i = 0; i < list.size (); i++) {
            HistoricActivityInstance histIns = list.get (i);
            
            // ????????????????????????????????????????????????????????????????????????
            if (StringUtils.isNotBlank (histIns.getAssignee ())
                    && historyService.createHistoricTaskInstanceQuery ().taskId (histIns.getTaskId ()).count () != 0
                    || BpmnXMLConstants.ELEMENT_TASK_USER.equals (histIns.getActivityType ()) && histIns.getEndTime () == null
                    || BpmnXMLConstants.ELEMENT_EVENT_START.equals (histIns.getActivityType ())
                    || BpmnXMLConstants.ELEMENT_EVENT_END.equals (histIns.getActivityType ())) {
                // ???????????????????????????
                Flow e = queryTaskState (histIns);
                System.out.println(e.getDurationTime()); 
                actList.add (e);
            }
        }
        return actList;
    }

    /**
     * ???????????????????????????????????????????????????KEY?????????????????????????????????????????????KEY???
     *
     * @return
     */
    public String getFormKey(String procDefId, String taskDefKey) {
        String formKey = "";
        if (StringUtils.isNotBlank (procDefId)) {
            if (StringUtils.isNotBlank (taskDefKey)) {
                try {
                    formKey = formService.getTaskFormKey (procDefId, taskDefKey);
                } catch (Exception e) {
                    formKey = "";
                }
            }
            if (StringUtils.isBlank (formKey)) {
                formKey = formService.getStartFormKey (procDefId);
            }
            if (StringUtils.isBlank (formKey)) {
                formKey = "/404";
            }
        }
        logger.debug ("getFormKey: {}", formKey);
        return formKey;
    }

    /**
     * ???????????????????????????????????????
     *
     * @param procInsId
     * @return
     */
    @Transactional(readOnly = false)
    public ProcessInstance getProcIns(String procInsId) {
        return runtimeService.createProcessInstanceQuery ().processInstanceId (procInsId).singleResult ();
    }

    /**
     * ????????????????????????????????????
     *
     * @param procInsId
     * @return
     */
    @Transactional(readOnly = false)
    public HistoricProcessInstance getFinishedProcIns(String procInsId) {
        return historyService.createHistoricProcessInstanceQuery ().processInstanceId (procInsId).singleResult ();
    }


    /**
     * ????????????????????????????????????
     *
     * @param user
     * @return
     */
    @Transactional(readOnly = false)
    public Page<ProcessVo> getMyStartedProcIns(User user, Page<ProcessVo> page, Flow flow) throws Exception {
        HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery ().startedBy (user.getId ()).includeProcessVariables ().orderByProcessInstanceStartTime ().desc ();
        if (flow.getBeginDate () != null) {
            query.startedAfter (flow.getBeginDate ());
        }
        if (flow.getEndDate () != null) {
            query.startedBefore (flow.getEndDate ());
        }
        if (StringUtils.isNotBlank (flow.getTitle ())) {
            query.variableValueLike (FlowableConstant.TITLE, "%" + flow.getTitle () + "%");
        }

        page.setCount (query.count ());
        List<HistoricProcessInstance> histList = Lists.newArrayList ();
        if (page.getMaxResults () == -1) {//?????????
            histList = query.list ();
        } else {
            histList = query.involvedUser (user.getId ()).listPage (page.getFirstResult (), page.getMaxResults ());
        }
        for (HistoricProcessInstance historicProcessInstance : histList) {
            ProcessVo processVo =  flowProcessService.queryProcessState (historicProcessInstance.getProcessDefinitionId (), historicProcessInstance.getId ());
            processVo.setRemarks(flowMapper.getRemarks(historicProcessInstance.getId(), historicProcessInstance.getProcessDefinitionKey())); 
            processVo.setEndTime (historicProcessInstance.getEndTime ());
            processVo.setStartTime (historicProcessInstance.getStartTime ());
            processVo.setProcessDefinitionId (historicProcessInstance.getProcessDefinitionId ());
            processVo.setProcessInstanceId (historicProcessInstance.getId ());
            processVo.setVars (historicProcessInstance.getProcessVariables ());
            processVo.setStatus (historicProcessInstance.getEndTime()==null ? "?????????" : "??????");
            if ("??????".equals(processVo.getStatus())) {
            	processVo.setStatus (flowMapper.getProcessStatus(processVo.getProcessInstanceId()));
			}
            processVo.setProcessDefinitionName (historicProcessInstance.getProcessDefinitionName ());
            processVo.setVersion ( historicProcessInstance.getProcessDefinitionVersion ());
            page.getList ().add (processVo);
        }

        return page;
    }


    /**
     * ????????????
     *
     * @param procDefKey    ????????????KEY
     * @param businessTable ???????????????
     * @param businessId    ???????????????
     * @param title         ??????????????????????????????????????????
     * @return ????????????ID
     */
    @Transactional(readOnly = false)
    public String startProcess(String procDefKey, String businessTable, String businessId, String title) {
        Map<String, Object> vars = Maps.newHashMap ();
        return startProcess (procDefKey, businessTable, businessId, title, vars);
    }

    @Transactional(readOnly = false)
    public String getSequence(String seqName, String bizCode){
    	return flowMapper.getSequence(seqName, bizCode);
    }
    
    public String getLatestProcessDefinitionId(String procDefKey){
    	return flowMapper.getLatestProcessDefinitionId(procDefKey);
    }
        
    /**
     * ????????????
     *
     * @param procDefKey    ????????????KEY
     * @param businessTable ???????????????
     * @param businessId    ???????????????
     * @param title         ??????????????????????????????????????????
     * @param vars          ????????????
     * @return ????????????ID
     */
    @SuppressWarnings("unused")
    @Transactional(readOnly = false)
    public String startProcess(String procDefKey, String businessTable, String businessId, String title, Map<String, Object> vars) {
    	// ???????????????????????????ProcInsId????????????????????????bug 
        Flow act = new Flow ();
        act.setBusinessTable (businessTable);// ????????????
        act.setBusinessId (businessId);  // ?????????ID
        String existsProcInsId = flowMapper.checkIfExistProcInsIdByBusinessId(act);
        if (!StringUtils.isEmpty(existsProcInsId)) {
        	throw new RuntimeException("System error: PROC_INST_ID already exists");
        }
    	
        //String userId = UserUtils.getUser().getLoginName();//ObjectUtils.toString(UserUtils.getUser().getId())
        // ??????????????????
        if (vars == null) {
            vars = Maps.newHashMap ();
        }

        String userId = (String) vars.get (FlowableConstant.INITIATOR);
        if (userId == null) {
            userId = UserUtils.getUser ().getId ();
        }
        String userName = UserUtils.get (userId).getName ();
        vars.put (FlowableConstant.USERNAME, userName);

        // ?????????????????????????????????ID???????????????????????????ID?????????activiti:initiator???
        identityService.setAuthenticatedUserId (userId);

        // ??????????????????
        if (StringUtils.isNotBlank (title)) {
            vars.put (FlowableConstant.TITLE, title);
        }
        
        // ?????????????????????????????????
        /*Map map = new HashMap();
        map.put("sql", "select * from " + businessTable + " where id = '" + businessId + "'" );
        List<Map> bizList = flowMapper.querySql(map);
        if (bizList!=null && bizList.size()>0){
        	Map bizMap = bizList.get(0);
        	vars.putAll(bizMap);
        }*/
        this.setVarByTable(businessTable, businessId, vars);
        
        //if (task.getProcessVariables().get("total_base_amount") != null) {
        if ( vars.get("total_base_amount") != null ) {
        	vars.put("gm_approved_total_base_amount", 0.00d);
        	vars.put("gm_approved_total_vat_base_amount", 0.00d);
        }

        vars.put ("flow_is_reopen", "no"); // ????????????PR?????????????????????????????????????????? jack
        if (!StringUtils.isEmpty(String.valueOf(vars.get("application_no")))) {
    		vars.put (FlowableConstant.TITLE, vars.get("application_no"));
    		if (String.valueOf(vars.get("application_no")).contains("-REOPEN"))  {
    			vars.put ("flow_is_reopen", "yes");
    		}
    	} else {
    		String seq = this.getSequence(StringUtils.upperCase(procDefKey), null);
    		vars.put (FlowableConstant.TITLE, seq);
    	}

        // ????????????
        ProcessInstance procIns = runtimeService.startProcessInstanceByKey (procDefKey, businessTable + ":" + businessId, vars);

        // ???????????????????????????ID
        /*Flow act = new Flow ();
        act.setBusinessTable (businessTable);// ????????????
        act.setBusinessId (businessId);  // ?????????ID
        */     
        act.setProcInsId (procIns.getId ());
        act.setVars (vars);
        flowMapper.updateProcInsIdByBusinessId (act);
        return act.getProcInsId ();
    }
    
    private void setVarByTable(String businessTable, String businessId, Map<String, Object> vars) {
    	if (StringUtils.isBlank(businessTable) || StringUtils.isBlank(businessId) || vars == null) { //????????????
    		return;
    	}
    	
        // ?????????????????????????????????
        Map map = new HashMap();
        map.put("sql", "select * from " + businessTable + " where id = '" + businessId + "'" );
        List<Map> bizList = flowMapper.querySql(map);
        if (bizList!=null && bizList.size()>0){
        	Map bizMap = bizList.get(0);
        	vars.putAll(bizMap);
        }
    }

    
    private void logBizTable(Flow flow) {
    	if (StringUtils.isBlank(flow.getBusinessTable()) || StringUtils.isBlank(flow.getBusinessId()) || StringUtils.isBlank(flow.getTaskId()) ) { //????????????
    		return;
    	}
    	
    	if (flow.getBusinessTable().equals("oa_pr_new")) { // ???????????? PR ?????????????????????, ???????????? ??? flow.getBusinessTable() + "_log", ???log_id???pk??? int???????????????????????????????????????task_id???task_def_key?????????????????????
    		Map map = new HashMap();
            map.put("sql", "insert into "+flow.getBusinessTable()+"_log select null, a.*, '"+flow.getTaskId()+"', '"+flow.getTaskDefKey()+"' from "+flow.getBusinessTable()+" a where a.id = '"+flow.getBusinessId()+"'" );
    		flowMapper.executeWriteSql(map);
    	}
    }

    /**
     * ????????????
     *
     * @param taskId       ??????ID
     * @param deleteReason ????????????
     */
    public void deleteTask(String taskId, String deleteReason) {
        taskService.deleteTask (taskId, deleteReason);
    }

    /**
     * ????????????
     *
     * @param taskId ??????ID
     * @param userId ????????????ID?????????????????????
     */
    @Transactional(readOnly = false)
    public void claim(String taskId, String userId) {
        taskService.claim (taskId, userId);
    }


    /**
     * ????????????, ???????????????
     *
     * @param vars      ????????????
     */
    @Transactional(readOnly = false)
    public void complete(Flow flow, Map<String, Object> vars) {
        // ????????????
        if (StringUtils.isNotBlank (flow.getProcInsId ())) {
            taskService.addComment (flow.getTaskId (), flow.getProcInsId (),flow.getComment ().getCommentType (), flow.getComment ().getFullMessage ());
        }

        // ??????????????????
        if (vars == null) {
            vars = Maps.newHashMap ();
        }
        
        //?????????????????????????????????
        this.setVarByTable(flow.getBusinessTable(), flow.getBusinessId(), vars);
        this.logBizTable(flow); // TODO

        vars.put("lastTaskDefKey", flow.getTaskDefKey());  
        vars.put("lastAssignee", UserUtils.getUser().getId()); // ??????????????????????????????,???????????????????????????????????????????????????,???????????????????????????????????????
        
        // ??????????????????
        if (StringUtils.isNotBlank (flow.getTitle ())) {
            vars.put (FlowableConstant.TITLE, flow.getTitle ());
        }

        Task task = taskService.createTaskQuery ().includeProcessVariables().taskId (flow.getTaskId ()).singleResult ();
        
        if ("FactoryGM".equals(flow.getTaskDefKey()) && vars.get("total_base_amount") != null && vars.get("total_vat_base_amount") != null) {
        	vars.put("gm_approved_total_base_amount", Double.valueOf(String.valueOf(task.getProcessVariables().get("total_base_amount")))); 
        	vars.put("gm_approved_total_vat_base_amount", Double.valueOf(String.valueOf(task.getProcessVariables().get("total_vat_base_amount")))); 
        }
        
        if (!UserUtils.getUser().getId().equals(task.getAssignee())) { //my delegate 
        	if ( !flow.getComment().getFullMessage().contains("Skip for same approver") ) {
	        	taskService.setVariableLocal(flow.getTaskId (), "owner", task.getAssignee());
	        	taskService.setAssignee(flow.getTaskId(), UserUtils.getUser().getId());
            }
        }
        
        // owner???????????????????????????????????????
        if (StringUtils.isNotBlank (task.getOwner ())) {
            DelegationState delegationState = task.getDelegationState ();
            switch (delegationState) {
                case PENDING:
                    taskService.resolveTask (flow.getTaskId ());
                    taskService.complete (flow.getTaskId (), vars);
                    break;

                case RESOLVED:
                    // ????????????????????????
                    break;

                default:
                    // ??????????????????
                    taskService.complete (flow.getTaskId (), vars);
                    break;
            }
        } else if(StringUtils.isBlank (task.getAssignee ()))  { // ???????????????
            // ????????????
            taskService.claim (flow.getTaskId (), UserUtils.getUser ().getId ());
            // ????????????
            taskService.complete (flow.getTaskId (), vars);
        } else  {
            // ????????????
            taskService.complete (flow.getTaskId (), vars);
        }
        
        // ======================================================================================= add by Jack 20210720 ????????????????????????????????????
		// ????????????????????????????????????????????????
		TaskQuery testQuery = taskService.createTaskQuery().processInstanceId(flow.getProcInsId());
		List<Task> todoList = testQuery.list();
		for (Task todo : todoList) {
			List<IdentityLink> list = taskService.getIdentityLinksForTask(todo.getId());
			
			/*String candidate = "";
			int flag=0;
			for(IdentityLink identityLink : list)
			{
				if(identityLink.getType().equals("candidate"))
				{
					candidate = identityLink.getUserId();
					flag++;
				}
			}
			if(flag==1)taskService.claim(todo.getId(), candidate);*/
			
			/*if (list.size() == 1){
				IdentityLink identityLink = list.get(0);
				if (identityLink.getType().equals("assignee") && identityLink.getUserId().contains("[") && identityLink.getUserId().contains("]") && identityLink.getUserId().contains(",")){
					taskService.deleteUserIdentityLink(todo.getId(), identityLink.getUserId(), identityLink.getType());
					String[] users =  StringUtils.trim(identityLink.getUserId().replace("[","").replace("]","").replaceAll(" ","")).split(",") ;
					for(int i=0;i<users.length;i++){
						taskService.addCandidateUser(todo.getId(), users[i]);
					}
				}
			}*/
			
			// ???????????????????????????????????????????????????,???????????????????????????????????????
			if (todo.getProcessDefinitionId().startsWith("prpo")) {
				for (IdentityLink identityLink : list){ 
					// if ("COMMENT__flow_agree".equals(flow.getComment().getCommentType()) && identityLink.getType().equals("assignee") && identityLink.getUserId().equals(vars.get("lastAssignee"))){
					if (!StringUtils.isEmpty(DictUtils.getDictLabel(flow.getComment().getCommentType(), "agree_action", "")) && identityLink.getType().equals("assignee") && identityLink.getUserId().equals(vars.get("lastAssignee"))){
						if (StringUtils.isEmpty(DictUtils.getDictLabel(todo.getName(), "cannot_skip_step", ""))) {
							flow.setTaskId(todo.getId());
							flow.getComment().setFullMessage("Skip for same approver");
							flow.setTaskDefKey(todo.getTaskDefinitionKey());
							this.complete(flow, vars);
						}
					}
				}
			}
			
		}
    }


    /**
     * ???????????????????????????
     */
    public Flow queryTaskState(HistoricActivityInstance histIns) {
        Flow e = new Flow ();
        e.setHistIns (histIns);
        // ???????????????????????????
        if (BpmnXMLConstants.ELEMENT_EVENT_START.equals (histIns.getActivityType ())) {
            List<HistoricProcessInstance> il = historyService.createHistoricProcessInstanceQuery ().processInstanceId (histIns.getProcessInstanceId ()).orderByProcessInstanceStartTime ().asc ().list ();
            if (il.size () > 0) {
                if (StringUtils.isNotBlank (il.get (0).getStartUserId ())) {
                    User user = UserUtils.get (il.get (0).getStartUserId ());
                    if (user != null) {
                        e.setAssignee (histIns.getAssignee ());
                        e.setAssigneeName (user.getLoginName() + " : " + user.getName ());
                    }
                }
            }
            TaskComment taskComment = new TaskComment ();
            taskComment.setStatus (FlowableConstant.START_EVENT_LABEL);
            taskComment.setMessage (FlowableConstant.START_EVENT_COMMENT);
            e.setComment (taskComment);
            return e;
        }
        if (BpmnXMLConstants.ELEMENT_EVENT_END.equals (histIns.getActivityType ())) {
            TaskComment taskComment = new TaskComment ();
            taskComment.setStatus (FlowableConstant.END_EVENT_LABEL);
            taskComment.setMessage (FlowableConstant.END_EVENT_COMMENT);
            e.setAssigneeName (FlowableConstant.SYSTEM_EVENT_COMMENT);
            e.setComment (taskComment);
            return e;
        }
        // ???????????????????????????
        if (StringUtils.isNotEmpty (histIns.getAssignee ())) {
        	e.setAssignee (histIns.getAssignee ());
            e.setAssigneeName (histIns.getAssignee ());
            User user = UserUtils.get (histIns.getAssignee ());
            if (user != null) {
                e.setAssigneeName (user.getLoginName() + " : " + user.getName ());
            }
        }
        
        // ????????????????????????
        if (StringUtils.isNotBlank (histIns.getTaskId ())) {
        	HistoricTaskInstance hisTaskIns = historyService.createHistoricTaskInstanceQuery ().taskId (histIns.getTaskId ()).includeTaskLocalVariables().singleResult();
            if (hisTaskIns.getTaskLocalVariables()!=null && hisTaskIns.getTaskLocalVariables().get("owner")!=null) {
            	User ownerUser = UserUtils.get((String)hisTaskIns.getTaskLocalVariables().get("owner"));
            	e.setAssigneeName(e.getAssigneeName() + " [Delegated by owner " + ownerUser.getLoginName() + " : " + ownerUser.getName() +"]" ); 
            }
            
            List<TaskComment> commentList = this.getTaskComments (histIns.getTaskId ());
            HistoricVariableInstanceQuery action = historyService.createHistoricVariableInstanceQuery ().processInstanceId (histIns.getProcessInstanceId ()).taskId (histIns.getTaskId ()).variableName ("_flow_button_name");
            if (commentList.size () > 0) {
                TaskComment comment = commentList.get (commentList.size ()-1);
                e.setComment (comment);
            }else {
            	e.setComment (new TaskComment ());
            }
        }
        //?????????????????????
        if(histIns.getEndTime () == null) {
            TaskComment taskComment = new TaskComment ();
            taskComment.setStatus (ActionType.WAITING.getStatus ());
            taskComment.setMessage (FlowableConstant.WAITING_EVENT_COMMENT);
            e.setComment (taskComment);
        }
        return e;
    }


    public List<TaskComment> getTaskComments(String taskId){
        return jdbcTemplate.query("select * from ACT_HI_COMMENT where TYPE_ like '"+TaskComment.prefix+"%' and TASK_ID_ = '" + taskId + "' order by TIME_ desc", new RowMapper<TaskComment>() {
            @Override
            public TaskComment mapRow(ResultSet rs, int rowNum) throws SQLException {
                TaskComment taskComment = new TaskComment ();
                taskComment.setCommentType (rs.getString ("TYPE_"));
                taskComment.setFullMessage (new String(rs.getBytes ("FULL_MSG_")));
                return taskComment;
            }
        });
    }


    public Map getDiagram(String processId) {
        Map m = new HashMap ();
        try {
            String processDefId = "";
            ProcessInstance pi = runtimeService.createProcessInstanceQuery ().processInstanceId (processId).singleResult ();
            //???????????????????????????
            if (pi == null) {
                processDefId = historyService.createHistoricProcessInstanceQuery ().processInstanceId (processId).singleResult ().getProcessDefinitionId ();
            } else {
                processDefId = pi.getProcessDefinitionId ();
            }
            BpmnModel bpmnModel = repositoryService.getBpmnModel (processDefId);
            List<HistoricActivityInstance> historyProcess = getHistoryProcess (processId);
            Set<String> activityIds = new LinkedHashSet<> ();
            List<String> flows = new ArrayList<> ();
            for (HistoricActivityInstance hi : historyProcess) {
                String activityType = hi.getActivityType ();
                if (activityType.equals (BpmnXMLConstants.ELEMENT_SEQUENCE_FLOW) || activityType.equals (BpmnXMLConstants.ELEMENT_GATEWAY_EXCLUSIVE)) {
                    flows.add (hi.getActivityId ());
                } else  if (StringUtils.isNotBlank (hi.getAssignee ())
                        && historyService.createHistoricTaskInstanceQuery ().taskId (hi.getTaskId ()).count () != 0
                        || BpmnXMLConstants.ELEMENT_TASK_USER.equals (hi.getActivityType ()) && hi.getEndTime () == null
                        || BpmnXMLConstants.ELEMENT_EVENT_START.equals (hi.getActivityType ())
                        || BpmnXMLConstants.ELEMENT_EVENT_END.equals (hi.getActivityType ())) {
                    activityIds.add (hi.getActivityId ());
                }
            }
            List<Task> tasks = taskService.createTaskQuery ().processInstanceId (processId).list ();
            for (Task task : tasks) {
                activityIds.add (task.getTaskDefinitionKey ());
            }
            byte[] bpmnBytes = flowModelService.getBpmnXML (bpmnModel);
            m.put ("bpmnXml", new String (bpmnBytes,"UTF-8"));
            m.put ("flows", flows);
            m.put ("activityIds", activityIds);
            return m;
        } catch (Exception e) {
            e.printStackTrace ();
        }
        return null;
    }


    /**
     * ????????????
     *
     * @param processId ??????id
     */
    public List<HistoricActivityInstance> getHistoryProcess(String processId) {
        List<HistoricActivityInstance> list = historyService // ????????????Service
                .createHistoricActivityInstanceQuery () // ??????????????????????????????
                .processInstanceId (processId) // ??????????????????id
                .finished ().orderByHistoricActivityInstanceEndTime ().asc ()
                .list ();
        return list;
    }



    /**
     * ??????????????????
     *
     * @param flow
     */
    @Transactional(readOnly = false)
    public void auditSave(Flow flow, Map vars) {
        flow.preUpdate ();
        complete (flow, vars);

    }


    /**
     * ????????????????????????
     */
    public boolean isBack(HistoricTaskInstance hisTask) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery ()
                .processInstanceId (hisTask.getProcessInstanceId ()).singleResult ();
        if (pi != null) {
            if (pi.isSuspended ()) {
                return false;
            } else {
                Task currentTask = taskService.createTaskQuery ().processInstanceId (hisTask.getProcessInstanceId ()).list ().get (0);
                HistoricTaskInstance lastHisTask = historyService.createHistoricTaskInstanceQuery ().processInstanceId (hisTask.getProcessInstanceId ()).finished ()
                        .includeProcessVariables ().orderByHistoricTaskInstanceEndTime ().desc ().list ().get (0);

                if (currentTask.getClaimTime () != null) {//???????????????
                    return false;
                }
                if (hisTask.getId ().equals (lastHisTask.getId ())) {
                    return true;
                }
                return false;
            }

        } else {
            return false;
        }
    }

    /*
     * ????????????
     */
    @Transactional(readOnly = false)
    public void backTask(String backTaskDefKey, String taskId, TaskComment comment) {
        Task task = taskService.createTaskQuery ().taskId (taskId).singleResult ();
        if(StringUtils.isBlank (task.getAssignee ())){
            taskService.claim (taskId, UserUtils.getUser ().getId ());
        }
        if ("FormModify".equals(backTaskDefKey) || "DocAdd".equals(backTaskDefKey) || backTaskDefKey.contains("_OnHold")) {
        	taskService.addComment (taskId, task.getProcessInstanceId (), comment.getCommentType (), comment.getFullMessage ());
            managementService.executeCommand (new BackUserTaskCmd (runtimeService,
                    taskId, backTaskDefKey));
            return ;
        }
        
        // ?????????????????????,??????????????????,???????????????????????????????????????
        ActivityInstance targetRealActivityInstance = runtimeService.createActivityInstanceQuery ().processInstanceId (task.getProcessInstanceId ()).activityId (backTaskDefKey).list ().get (0);
        if (targetRealActivityInstance.getActivityType ().equals (BpmnXMLConstants.ELEMENT_EVENT_START)) {
            flowProcessService.stopProcessInstanceById (task.getProcessInstanceId (), ProcessStatus.REJECT, comment.getMessage ());
        }else {
            taskService.addComment (taskId, task.getProcessInstanceId (), comment.getCommentType (), comment.getFullMessage ());
            managementService.executeCommand (new BackUserTaskCmd (runtimeService,
                    taskId, backTaskDefKey));
        }
        
        this.triggerAferComplete(task, comment);
    }
    
	private void triggerAferComplete(Task task, TaskComment comment) {
		// ????????????????????????????????????????????????
		TaskQuery testQuery = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId());
		List<Task> todoList = testQuery.list();
		for (Task todo : todoList) {
			List<IdentityLink> list = taskService.getIdentityLinksForTask(todo.getId());
			// ???????????????????????????????????????????????????,???????????????????????????????????????
			if (todo.getProcessDefinitionId().startsWith("prpo")) {
				for (IdentityLink identityLink : list) {
					String flag = flowMapper.checkIfSkip(task.getProcessInstanceId(), todo.getTaskDefinitionKey(),
							identityLink.getUserId());
					if (!"1".equals(flag)) {
						if (StringUtils.isEmpty(DictUtils.getDictLabel(todo.getName(), "cannot_skip_step", ""))) {
							Flow flow = new Flow();
							flow.setTaskId(todo.getId());
							comment.setFullMessage("Skip for same approver");
							comment.setCommentType("COMMENT__flow_agree");
							comment.setStatus("Approve");
							flow.setComment(comment); 
							flow.setTaskDefKey(todo.getTaskDefinitionKey());
							flow.setProcInsId(task.getProcessInstanceId());
							flow.setProcDefId(task.getProcessDefinitionId());
							flow.setBusinessTable("oa_pr_new");

							Map map = new HashMap();
							map.put("sql", "select id from oa_pr_new where proc_ins_id = '"
									+ task.getProcessInstanceId() + "'");
							List<Map> bizList = flowMapper.querySql(map);
							if (bizList != null && bizList.size() > 0) {
								Map bizMap = bizList.get(0);
								flow.setBusinessId((String) bizMap.get("id"));
							} else {
								throw new RuntimeException("System error: cannot find oa_pr_new data");
							}

							this.complete(flow, Maps.newHashMap());
						}
					}
				}
			}

		}
	}


    /**
     * ?????????????????????
     *
     * @param taskId
     * @return
     */
    public List<Flow> getBackNodes(String taskId) {
        Task taskEntity = taskService.createTaskQuery ().taskId (taskId).singleResult ();
        String processInstanceId = taskEntity.getProcessInstanceId ();
        String currActId = taskEntity.getTaskDefinitionKey ();
        String processDefinitionId = taskEntity.getProcessDefinitionId ();
        Process process = repositoryService.getBpmnModel (processDefinitionId).getMainProcess ();
        FlowNode currentFlowElement = (FlowNode) process.getFlowElement (currActId, true);
        List<ActivityInstance> activitys =
                runtimeService.createActivityInstanceQuery ().processInstanceId (processInstanceId).finished ().orderByActivityInstanceStartTime ().asc ().list ();
        List<String> activityIds =
                activitys.stream ().filter (activity -> activity.getActivityType ().equals (BpmnXMLConstants.ELEMENT_TASK_USER) || activity.getActivityType ().equals (BpmnXMLConstants.ELEMENT_EVENT_START)).filter (activity -> !activity.getActivityId ().equals (currActId)).map (ActivityInstance::getActivityId).distinct ().collect (Collectors.toList ());
        List<Flow> result = new ArrayList<> ();
        for (String activityId : activityIds) {
            FlowNode toBackFlowElement = (FlowNode) process.getFlowElement (activityId, true);
            if (FlowableUtils.isReachable (process, toBackFlowElement, currentFlowElement)) {
                Flow vo = new Flow ();
                vo.setTaskDefKey (activityId);
                vo.setTaskName (toBackFlowElement.getName ());
                vo.setTaskId (activityId);
                result.add (vo);
            }
        }
        return result;
    }

    @Transactional(readOnly = false)
    public void addSignTask(String taskId, List<String> userIds, String comment, Boolean flag) throws Exception {
        TaskEntityImpl taskEntity = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        //1.??????????????????????????????
        if (taskEntity != null) {
            //????????????????????????
            String parentTaskId = taskEntity.getParentTaskId();
            if (org.apache.commons.lang.StringUtils.isBlank(parentTaskId)) {
                taskEntity.setOwner(UserUtils.getUser ().getId ());
                taskEntity.setAssignee(null);
                taskEntity.setCountEnabled(true);
                if (flag) {
                    taskEntity.setScopeType(FlowableConstant.AFTER_ADDSIGN);
                } else {
                    taskEntity.setScopeType(FlowableConstant.BEFORE_ADDSIGN);
                }
                //1.2 ???????????????????????????
                taskService.saveTask(taskEntity);
            }
            //2.??????????????????
            this.createSignSubTasks(userIds, taskEntity);
            //3.??????????????????
            String type = flag ? ActionType.ADD_AFTER_MULTI_INSTANCE.toString() : ActionType.ADD_BEFORE_MULTI_INSTANCE.toString();
            taskService.addComment (taskId, taskEntity.getProcessInstanceId (), type, comment);
        } else {
            throw  new Exception ("?????????????????????????????????!");
        }
    }

    /**
     * ?????????????????????
     *
     * @param userIds     ????????????
     * @param taskEntity ?????????
     */
    private void createSignSubTasks(List<String> userIds, TaskEntity taskEntity) {
        if (CollectionUtils.isNotEmpty(userIds)) {
            String parentTaskId = taskEntity.getParentTaskId();
            if (org.apache.commons.lang.StringUtils.isBlank(parentTaskId)) {
                parentTaskId = taskEntity.getId();
            }
            String finalParentTaskId = parentTaskId;
            //1.?????????????????????????????????
            userIds.forEach(userCode -> {
                if (StringUtils.isNotBlank(userCode)) {
                    this.createSubTask(taskEntity, finalParentTaskId, userCode);
                }
            });
            String taskId = taskEntity.getId();
            if (org.apache.commons.lang.StringUtils.isBlank(taskEntity.getParentTaskId())) {
                //2.???????????????????????????????????????
                Task task = this.createSubTask(taskEntity, finalParentTaskId, UserUtils.getUser ().getId ());
                taskId = task.getId();
            }
            Task taskInfo = taskService.createTaskQuery().taskId(taskId).singleResult();
            if (null != taskInfo) {
                taskService.complete(taskId);
            }
            //??????????????????????????????????????????????????????????????????
            long candidateCount = taskService.createTaskQuery().taskId(parentTaskId).taskCandidateUser(UserUtils.getUser ().getId ()).count();
            if (candidateCount > 0) {
                taskService.deleteCandidateUser(parentTaskId, UserUtils.getUser ().getId ());
            }
        }
    }

    /**
     * ???????????????
     *
     * @param ptask    ???????????????
     * @param assignee ?????????????????????
     * @return
     */
    protected TaskEntity createSubTask(TaskEntity ptask, String ptaskId, String assignee) {
        TaskEntity task = null;
        if (ptask != null) {
            //1.???????????????
            task = (TaskEntity) taskService.newTask(UUID.randomUUID ().toString ());
            task.setCategory(ptask.getCategory());
            task.setDescription(ptask.getDescription());
            task.setTenantId(ptask.getTenantId());
            task.setAssignee(assignee);
            task.setName(ptask.getName());
            task.setParentTaskId(ptaskId);
            task.setProcessDefinitionId(ptask.getProcessDefinitionId());
            task.setProcessInstanceId(ptask.getProcessInstanceId());
            task.setTaskDefinitionKey(ptask.getTaskDefinitionKey());
            task.setTaskDefinitionId(ptask.getTaskDefinitionId());
            task.setPriority(ptask.getPriority());
            task.setCreateTime(new Date());
            taskService.saveTask(task);
        }
        return task;
    }
    
    /**
     * ????????????????????????
     *
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
	public Map queryProcessState(String processInstanceId) throws Exception {
        Map map = new HashMap ();
        // ??????????????????ID??????????????????
        ProcessInstance pi = runtimeService.createProcessInstanceQuery ()
                .processInstanceId (processInstanceId).singleResult ();
        if (pi != null) {
            if (pi.isSuspended ()) {
                //????????????
                map.put ("code", 0);
                map.put ("status", "?????????");
                return map;
            } else {
                //????????????
                map.put ("code", 1);
                map.put ("status", "?????????");
                List<Task> currentTasks = taskService.createTaskQuery ().includeIdentityLinks().processInstanceId (processInstanceId).active().list();
                String userId= UserUtils.getUser().getId();
                Task currentTask =null;
                if(StringUtils.isBlank(userId)){
                	currentTask = currentTasks.get(0);
                }
                else{
                	for(Task t:currentTasks){
                		if (userId.equals(t.getAssignee())){
                			currentTask=t;
                			break;
                		}
                		else{
                			for(IdentityLinkInfo u0: t.getIdentityLinks()) {
                				if(userId.equals(u0.getUserId())){
                					currentTask=t;
                        			break;
                				}
                			}
                		}
                	}
                	List<Task> tasks=currentTasks.stream().filter(c->userId.equals(c.getAssignee())).collect(Collectors.toList());
                	if(tasks.size()==1){
                		
                	}
                	else{
                		currentTask = currentTasks.get(0);
                	}
                }
                HashMap<String,String> taskMap = new HashMap<String,String> ();
                taskMap.put ("assignee", currentTask.getAssignee ());
                taskMap.put ("id", currentTask.getId ());
                taskMap.put ("name", currentTask.getName ());
                taskMap.put ("executionId", currentTask.getExecutionId ());
                taskMap.put ("taskDefinitionKey", currentTask.getTaskDefinitionKey ());
                map.put ("currentTask", taskMap);
                
                if(currentTasks.size()>1){
                	List<String> taskDefinitionKeys= currentTasks.stream().map(t->t.getTaskDefinitionKey()).collect(Collectors.toList());
                	taskMap.put("taskDefinitionKeys", StringUtils.join(taskDefinitionKeys));
                	
                	List<String> assignees= currentTasks.stream().map(t->t.getAssignee()).collect(Collectors.toList());
                	taskMap.put("assignees", StringUtils.join(assignees));
                	
                	List<String> ids= currentTasks.stream().map(t->t.getId()).collect(Collectors.toList());
                	taskMap.put("ids", StringUtils.join(ids));
                	
                	List<String> names= currentTasks.stream().map(t->t.getName()).collect(Collectors.toList());
                	taskMap.put("names", StringUtils.join(names));
                }
                return map;
            }
        } else {
            HistoricProcessInstance pi2 = historyService.createHistoricProcessInstanceQuery ().processInstanceId (processInstanceId).singleResult ();
            if (pi2 != null) {
                if (pi2.getDeleteReason () == null) {
                    map.put ("code", 2);
                    map.put ("status", "????????????");
                    return map;
                } else if (pi2.getDeleteReason ().equals (DictUtils.getLanguageLabel("????????????", ""))) {
                    map.put ("code", 3);
                    map.put ("status", "????????????");
                    return map;
                } else {
                    map.put ("code", 4);
                    map.put ("status", "????????????");
                    map.put ("deleteReason", pi2.getDeleteReason ());
                    return map;
                }

            } else {
                map.put ("code", 5);
                map.put ("status", "?????????");
                map.put ("deleteReason", pi2.getDeleteReason ());
                return map;
            }
        }
    }
    
   	@Transactional(readOnly = false)
    public HashMap getFlowMapPid(String pid) throws Exception {
    	return this.getFlowMapPid(pid, "end");
    }
    
    /**
     * ??????????????????idPID?????????????????? [????????????procDefMap?????????????????????procInsMap?????????????????????vars]
     * @param pid ????????????id
     * @author ??????
     * @date 2020???3???14???21:01:57
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Transactional(readOnly = false)
    public HashMap getFlowMapPid(String pid,String eventName) throws Exception {
      HistoricProcessInstanceQuery query = historyService.createHistoricProcessInstanceQuery()
              .processInstanceId(pid).includeProcessVariables().orderByProcessInstanceStartTime().desc();
      List<HistoricProcessInstance> histList = query.list();
      HashMap map = new HashMap();
      if(histList!=null&&histList.size()>0){
        HistoricProcessInstance historicProcessInstance =histList.get(0);
        //?????????????????? code=0
        Map state = queryProcessState(historicProcessInstance.getId());
        HashMap procInsMap = new HashMap();
        HashMap procDefMap = new HashMap();
        procInsMap.put("endTime", historicProcessInstance.getEndTime());
        procInsMap.put("startTime", historicProcessInstance.getStartTime());
        procInsMap.put("processInstanceId", historicProcessInstance.getId());
        procDefMap.put("processDefinitionKey", historicProcessInstance.getProcessDefinitionKey());    
        procDefMap.put("processDefinitionId", historicProcessInstance.getProcessDefinitionId());
        map.put("vars", historicProcessInstance.getProcessVariables());
        procDefMap.put("name", historicProcessInstance.getProcessDefinitionName());
        procDefMap.put("version", historicProcessInstance.getProcessDefinitionVersion());
        if(state.get("currentTask")!=null){
        	HashMap currentTask =(HashMap) state.get("currentTask");
        	//if (!"start".equals(eventName)) {
        		Map<String, Object> vars = runtimeService.getVariables((String) currentTask.get("executionId"));
             	map.put("vars",vars);
            //} 
        	if(StringUtils.isNoneBlank((String)currentTask.get("id"))){
        		List<TaskComment> commentList = this.getTaskComments ((String)currentTask.get("id"));
        		if(commentList.size()>0){
        			state.put("comments", commentList);
        		}
        	}
        }
        procInsMap.putAll(state);
        map.put("procDef", procDefMap);
        map.put("procIns", procInsMap);
      }
      return map;
    }
    
    /**
     *@Description  ????????????????????????
     *@Date 10:18 2019/4/17
     *@Param [processInstanceId, key]
      * processInstanceId ????????????id
      * key ???
     *@return java.lang.Object
     **/
     public Object getHisVariable(String processInstanceId,String key) {
         Object variable = null;
         List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
         for (int i = 0; i < list.size(); i++) {
             HistoricVariableInstance historicVariableInstance = list.get(i);
             if (historicVariableInstance.getVariableName().equals(key)) {
                 variable = historicVariableInstance.getValue();
             }
         }
  
         return variable;
     } 
     
     public String getMsgId(String procInstId) {
 		return flowMapper.getMsgId(procInstId);
 	}	


}
