package com.jeeplus.modules.flowable.common.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.el.ExpressionManager;
import org.flowable.engine.FormService;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.TaskFormData;
import org.flowable.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.TaskService;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.jeeplus.common.utils.Collections3;
import com.jeeplus.common.utils.SpringContextHolder;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.database.datamodel.entity.DataSet;
import com.jeeplus.modules.database.datamodel.service.DataSetService;
import com.jeeplus.modules.database.datamodel.utils.DataSourceUtils;
import com.jeeplus.modules.extension.entity.FlowAssignee;
import com.jeeplus.modules.extension.entity.TaskDefExtension;
import com.jeeplus.modules.extension.service.TaskDefExtensionService;
import com.jeeplus.modules.flowable.utils.FlowableUtils;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.entity.Post;
import com.jeeplus.modules.sys.entity.Role;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.service.OfficeService;
import com.jeeplus.modules.sys.service.PostService;
import com.jeeplus.modules.sys.service.RoleService;
import com.jeeplus.modules.sys.service.UserService;
import com.jeeplus.modules.sys.utils.UserUtils;

public class ExtUserTaskActivityBehavior extends UserTaskActivityBehavior {	
    private static final long serialVersionUID = 7711531472879418236L;

    public ExtUserTaskActivityBehavior(UserTask userTask) {
        super(userTask);
    }


    /**
     * 分配办理人员
     */
    @Override
    protected void handleAssignments(TaskService taskService, String assignee, String owner, List<String> candidateUsers, List<String> candidateGroups, TaskEntity task, ExpressionManager expressionManager, DelegateExecution execution) {
        Process process = SpringContextHolder.getBean (RepositoryService.class).getBpmnModel (task.getProcessDefinitionId ()).getMainProcess ();
        FlowElement flowElement = process.getFlowElement(task.getTaskDefinitionKey());
        Boolean isMultiInstance = FlowableUtils.isFlowElementMultiInstance (flowElement);
        if(isMultiInstance){
            super.handleAssignments (taskService, assignee, owner, candidateUsers, candidateGroups, task, expressionManager, execution);
            return;
        }
        
        TaskDefExtension taskDefExtension = new TaskDefExtension ();
        taskDefExtension.setTaskDefId (task.getTaskDefinitionKey ());
        taskDefExtension.setProcessDefId (process.getId ());
        List<TaskDefExtension> list = SpringContextHolder.getBean (TaskDefExtensionService.class).findList (taskDefExtension);
        HashSet<String> candidateUserIds = new HashSet<> ();
        if (list.size () > 0) {
            taskDefExtension = list.get (0); 
            List<FlowAssignee> assigneeList =  SpringContextHolder.getBean(TaskDefExtensionService.class).get(taskDefExtension.getId()).getFlowAssigneeList ();
            for (FlowAssignee flowAssignee : assigneeList) {
                switch (flowAssignee.getType ()) {
                    case "user":
                        candidateUserIds.addAll (Arrays.asList (flowAssignee.getValue ().split (",")));
                        break;
                    case "post":
                        if(StringUtils.isNotBlank (flowAssignee.getValue ())){
                            Post post = SpringContextHolder.getBean (PostService.class).get (flowAssignee.getValue ());
                            User user = new User();
                            user.setPost (post);
                            List<User> userList = SpringContextHolder.getBean (UserService.class).findList (user);
                            candidateUserIds.addAll ( Collections3.extractToList (userList, "id"));
                        }

                        break;
                    case "company":
                        if(StringUtils.isNotBlank (flowAssignee.getValue ())){
                            Office office = SpringContextHolder.getBean (OfficeService.class).get (flowAssignee.getValue ());
                            User user = new User();
                            user.setCompany (office);
                            List<User> userList = SpringContextHolder.getBean (UserService.class).findList (user);
                            candidateUserIds.addAll ( Collections3.extractToList (userList, "id"));
                        }

                        break;
                    case "depart":
                        if(StringUtils.isNotBlank (flowAssignee.getValue ())){
                            Office office = SpringContextHolder.getBean (OfficeService.class).get (flowAssignee.getValue ());
                            User user = new User();
                            user.setOffice (office);
                            List<User> userList = SpringContextHolder.getBean (UserService.class).findList (user);
                            candidateUserIds.addAll ( Collections3.extractToList (userList, "id"));
                        }

                        break;
                    case "role":
                        if(StringUtils.isNotBlank (flowAssignee.getValue ())){
                            String[] roleIds = flowAssignee.getValue ().split (",");
                            for(String roleId: roleIds){
                                Role role = SpringContextHolder.getBean (RoleService.class).get (roleId);
                                User user = new User (role);
                                List<User> userList = SpringContextHolder.getBean (UserService.class).findList (user);
                                candidateUserIds.addAll ( Collections3.extractToList (userList, "id"));
                            }
                        }
                        break;
                    case "applyUserId":
                        candidateUserIds.add ("${applyUserId}");
                        break;
                    case "previousExecutor":
                        HistoricTaskInstance lastHisTask = SpringContextHolder.getBean (HistoryService.class).createHistoricTaskInstanceQuery ().processInstanceId (task.getProcessInstanceId ()).finished ()
                                .includeProcessVariables ().orderByHistoricTaskInstanceEndTime ().desc ().list ().get (0);

                        candidateUserIds.add(lastHisTask.getAssignee ());
                        break;
                    case "currentUserId":
                        candidateUserIds.add(UserUtils.getUser ().getId ());
                        break;
                    case "sql":
                    	FormService formService=SpringContextHolder.getBean (FormService.class);
                    	TaskFormData formData= formService.getTaskFormData(task.getId());
                    	Map<String, Object> vars = SpringContextHolder.getBean (RuntimeService.class).getVariables(task.getExecutionId());
                    	String sql=flowAssignee.getValue ();
                    	Map<String,String>params=new HashMap<String, String>();
                    	for(FormProperty f:formData.getFormProperties()){
                    		params.put(f.getName(), f.getValue());
                    	}
						if(vars !=null){
							for(Map.Entry<String, Object> v: vars.entrySet()){
								if(v.getValue()!=null){
									params.put(v.getKey(), v.getValue().toString());
								}
	            			}
						}
						String [] tmp=taskDefExtension.getTaskDefId().split("__");
						if(tmp.length>0){
							params.put("taskDefId", tmp[0]);
						}
                    	
                    	if(sql.startsWith("sqlKey:")){
                    		JSONArray userMaps= DataSourceUtils.getDataBySql(sql.substring(7), params);
                    		for(int i=0;i<userMaps.size();i++ ){
                    			JSONObject userMap = userMaps.getJSONObject(i);
                            	candidateUserIds.add(userMap.getString("id"));
                            }
                    	}
                    	else{
                    		for(Map.Entry<String, String> v: params.entrySet()){
                    			sql=sql.replaceAll("\\#\\{"+v.getKey()+"\\}", v.getValue());
	            			}
                    		List<Map<String, Object>>  userMaps = SpringContextHolder.getBean (JdbcTemplate.class).queryForList (sql);
                            for(Map<String, Object>userMap:userMaps ){
                            	candidateUserIds.add(String.valueOf(userMap.get ("id")));
                            }
                    	}
                    	
                        break;
                    case "custom":
                        //根据你的自定义标记，请自行实现
                        break;
                }
            }
        }
        List<String> candidateIds = new ArrayList<> (candidateUserIds);
        
        String candidateIdStr = "";
        for (String cid : candidateIds) {
        	candidateIdStr += cid;
        }
        if (StringUtils.isEmpty(candidateIdStr)) {
        	throw new RuntimeException("Error: Task assignee is null");
        }
        //此处可以根据业务逻辑自定义
        if (candidateIds.size () == 0) {
            super.handleAssignments (taskService, null, owner, Lists.newArrayList (), Lists.newArrayList (), task, expressionManager, execution);
        } else if (candidateIds.size () == 1) {
            super.handleAssignments (taskService, candidateIds.get (0), owner, Lists.newArrayList (), Lists.newArrayList (), task, expressionManager, execution);
        } else if (candidateIds.size () > 1) {
            super.handleAssignments (taskService, null, owner, candidateIds, null, task, expressionManager, execution);
        }


    }
}

