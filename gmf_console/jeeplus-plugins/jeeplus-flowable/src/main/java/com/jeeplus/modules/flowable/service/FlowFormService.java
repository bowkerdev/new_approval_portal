/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.flowable.engine.FormService;
import org.flowable.engine.IdentityService;
import org.flowable.engine.TaskService;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.service.BaseService;
import com.jeeplus.modules.flowable.common.email.SendEmailThread;
import com.jeeplus.modules.flowable.entity.Flow;
import com.jeeplus.modules.sys.utils.DictUtils;
import com.jeeplus.modules.sys.utils.UserUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 流程定义相关Service
 *
 * @author jeeplus
 * @version 2016-11-03
 */
@Service
@Transactional(readOnly = true)
public class FlowFormService extends BaseService {

    @Autowired
    private FlowTaskService flowTaskService;

    @Autowired
    private FormService formService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Transactional(readOnly = false)
    public AjaxJson submitStartFormDatas(String assignee, String processDefinitionId,String data) {
    	StartFormData formData = formService.getStartFormData(processDefinitionId);//拿取流程启动前的表单字段。
        List<FormProperty> formProperties = formData.getFormProperties();//获取表单字段值
        JSONArray jDatas=JSONArray.fromObject(data);
        
        List<JSONObject> procInfos=new ArrayList<JSONObject>();
        for(int indexData =0 ;jDatas.size() >indexData ;indexData++ ){
        	JSONObject procInfo= new JSONObject();
        	JSONObject jData = jDatas.getJSONObject(indexData);
            // 设置流程变量
            Map<String,String> formValues = new HashMap<String,String>();
            String userId = UserUtils.getUser().getId();
            String userName = UserUtils.get(userId).getName();
            formValues.put("userName", userName);

            // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
            identityService.setAuthenticatedUserId(userId);

            for(FormProperty formProperty:formProperties){
                if (formProperty.isWritable()) {
                    if(jData.has(formProperty.getId())){
                        String value = jData.getString(formProperty.getId());//拿取具体参数值
                        formValues.put(formProperty.getId(), value);
                    }
                }
            }
            
            String procDefKey = processDefinitionId.split(":")[0];
            String seq = flowTaskService.getSequence(StringUtils.upperCase(procDefKey), null);
            if (StringUtils.isEmpty(formValues.get("application_no"))){// String.valueOf() 会将null 转 "null"
    	        formValues.put("application_no", seq);
            }
            procInfo.put("applicationNo", formValues.get("application_no"));
            // 设置流程标题
            if (StringUtils.isBlank(formValues.get("title"))) {
                formValues.put("title", seq);
            }
            String procInsId = formService.submitStartFormData(processDefinitionId,formValues).getId();//启动流程，提交表单
            procInfo.put("processInstanceId", procInsId);
            //指定下一步处理人
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(procInsId).active().list();
            if(tasks.size()>0){
            	List<String> names= tasks.stream().map(t->t.getName()).collect(Collectors.toList());
            	procInfo.put("names", StringUtils.join(names,","));
            	List<String> assignees= tasks.stream().map(t->t.getAssignee()).collect(Collectors.toList());
            	procInfo.put("assignee",String.format(",%s,", StringUtils.join(assignees,",")));
            }
            if(tasks.size()==1){
            	if(StringUtils.isNotBlank(assignee)){
                    if(tasks.get(0) != null){
                        taskService.setAssignee(tasks.get(0).getId(), assignee);
                    }
                }
            }
            procInfos.add(procInfo);
            // 如满足邮件规则则发邮件
    		(new Thread(new SendEmailThread(procInsId, flowTaskService.getMsgId(procInsId)))).start();
        }
        return AjaxJson.success(DictUtils.getLanguageLabel("操作成功", "")).put("procInfos", procInfos);
    }
    
    @Transactional(readOnly = false)
    public AjaxJson submitTaskFormDatas(List<Flow> flows, JSONArray jDatas) {
    	List<JSONObject> procInfos=new ArrayList<JSONObject>();
        for(int indexJData =0 ;jDatas.size() >indexJData ;indexJData++ ){
        	Flow flow=flows.get(indexJData);
        	
        	TaskFormData taskFormData = formService.getTaskFormData(flow.getTaskId ());//根据任务ID拿取表单数据
            List<FormProperty> formProperties = taskFormData.getFormProperties();//获取表单字段值
        	JSONObject jData = jDatas.getJSONObject(indexJData);
        	Map<String, Object> formValues = new HashMap<String, Object>();
            Set<String> noCommitValues = new HashSet<String>();

            for (FormProperty formProperty : formProperties) {
                if (!formProperty.isWritable()) {
                   noCommitValues.add(formProperty.getId());
                }
            }

            for(Object str: jData.keySet()){
              if(!noCommitValues.contains(str.toString())){
                  Object value = jData.get(str.toString());//拿取具体参数值
                  formValues.put(str.toString(), value);    //将ID和value存入map中
              }
            }
            formValues.put("assignee", "");// 避免jackson序列化错误
            flowTaskService.complete(flow, formValues );  //提交用户任务表单并且完成任务。
            //指定下一步处理人
            if(StringUtils.isNotBlank(flow.getAssignee ())){
                Task task = taskService.createTaskQuery().processInstanceId(flow.getProcInsId ()).active().singleResult();
                if(task != null){
                    taskService.setAssignee(task.getId(), flow.getAssignee ());
                }
            }
            List<Task> tasks = taskService.createTaskQuery().processInstanceId(flow.getProcInsId ()).active().list();
            JSONObject procInfo= new JSONObject();
            if(tasks.size()>0){
            	List<String> names= tasks.stream().map(t->t.getName()).collect(Collectors.toList());
            	procInfo.put("names", StringUtils.join(names,","));
            	List<String> assignees= tasks.stream().map(t->t.getAssignee()).collect(Collectors.toList());
            	procInfo.put("assignee",String.format(",%s,", StringUtils.join(assignees,",")));
            	procInfo.put("processInstanceId", flow.getProcInsId());
            }
            procInfos.add(procInfo);
            // 如满足邮件规则则发邮件
            (new Thread(new SendEmailThread( flow.getProcInsId(), flowTaskService.getMsgId(flow.getProcInsId())))).start();
        }
        return AjaxJson.success(DictUtils.getLanguageLabel("操作成功", "")).put("procInfos", procInfos);
    }
}
