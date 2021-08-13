/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.web;

import com.google.common.collect.Lists;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.flowable.entity.Flow;
import com.jeeplus.modules.flowable.service.FlowTaskService;
import com.jeeplus.modules.sys.utils.DictUtils;
import com.jeeplus.modules.sys.utils.UserUtils;
import net.sf.json.JSONObject;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.*;
import org.flowable.engine.form.FormProperty;
import org.flowable.engine.form.StartFormData;
import org.flowable.engine.form.TaskFormData;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 流程个人任务相关Controller
 * @author jeeplus
 * @version 2016-11-03
 */
@RestController
@RequestMapping("/flowable/form")
public class FlowableFormController extends BaseController {

    @Autowired
    private FlowTaskService flowTaskService;

    @Autowired
    private FormService formService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;


    /**
     * 提交表单
     *
     * @param processDefinitionId 流程定义ID
     */
    @PostMapping("submitStartFormData")
    @CrossOrigin
    public AjaxJson submitStartFormData(String assignee, String processDefinitionId, String title, @RequestParam("data") String data) {
    	if (!processDefinitionId.contains(":")) {
    		processDefinitionId = flowTaskService.getLatestProcessDefinitionId(processDefinitionId);
    	}
        StartFormData formData = formService.getStartFormData(processDefinitionId);//拿取流程启动前的表单字段。
        List<FormProperty> formProperties = formData.getFormProperties();//获取表单字段值
        JSONObject jData = JSONObject.fromObject(data);

        // 设置流程变量
        Map<String,String> formValues = new HashMap<String,String>();
        String userId = UserUtils.getUser().getId();
        String userName = UserUtils.get(userId).getName();
        formValues.put("userName", userName);

        // 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
        identityService.setAuthenticatedUserId(userId);

        // 设置流程标题
        if (StringUtils.isNotBlank(title)) {
            formValues.put("title", title);
        }

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
        formValues.put("application_no", seq);        
        formValues.put("title", seq); // update by Jack 20210722
        
        String procInsId = formService.submitStartFormData(processDefinitionId,formValues).getId();//启动流程，提交表单

        //指定下一步处理人
        if(StringUtils.isNotBlank(assignee)){
            Task task = taskService.createTaskQuery().processInstanceId(procInsId).active().singleResult();
            if(task != null){
                taskService.setAssignee(task.getId(), assignee);
            }
        }
        return AjaxJson.success(DictUtils.getLanguageLabel("启动成功", "")).put("procInsId", procInsId);
    }

    /**
     * 获取表单数据
     * @param taskId 任务ID
     */
    @GetMapping("getTaskFormData")
    public AjaxJson getTaskFormData(String taskId) {

        TaskFormData taskFormData = formService.getTaskFormData(taskId);//根据任务ID拿取表单数据
        return AjaxJson.success().put("taskFormData", taskFormData.getFormProperties());
    }

    /**
     * 获取表单启动弄数据
     *
     * @param processDefinitionId 流程定义id
     */
    @GetMapping("getStartFormData")
    public AjaxJson getStartFormData(String processDefinitionId) {

        StartFormData startFormData = formService.getStartFormData(processDefinitionId);//根据流程定义ID拿取表单数据
        return AjaxJson.success().put("startFormData", startFormData.getFormProperties());
    }

    /**
     * 获取历史表单数据
     *
     * @param processInstanceId 流程实例Id
     */
    @SuppressWarnings("rawtypes")
	@GetMapping("getHistoryTaskFormData")
    public AjaxJson getHistroyTaskFormData(String processInstanceId, String procDefId, String taskDefKey) {
        List<HistoricVariableInstance> historicVariableInstances=historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId)
                .list();
        HashMap<String,Object> historicVariableMap = new HashMap<>();
        for(HistoricVariableInstance historicVariableInstance: historicVariableInstances){
            historicVariableMap.put(historicVariableInstance.getVariableName(), historicVariableInstance.getValue());
        }

        List<Map> list = Lists.newArrayList();
        FlowElement node = repositoryService.getBpmnModel(procDefId).getFlowElement(taskDefKey);
        if(node!=null){
            List<org.flowable.bpmn.model.FormProperty> formPropertyList = Lists.newArrayList();
            if(node instanceof UserTask){
                formPropertyList = ((UserTask) node).getFormProperties();
            }else if(node instanceof StartEvent){
                formPropertyList = ((StartEvent) node).getFormProperties();
            }
            for(org.flowable.bpmn.model.FormProperty formProperty : formPropertyList){
                try {
                    HashMap<String,Object> formPropertyMap = new HashMap<>();

                    Field field =  formProperty.getClass().getDeclaredField("readable");
                    field.setAccessible(true);
                    boolean readable =(boolean) field.get(formProperty);
                    if(readable){
                        formPropertyMap.put("id", formProperty.getId());
                        formPropertyMap.put("name", formProperty.getName());
                        formPropertyMap.put("value", historicVariableMap.get(formProperty.getId()));
                        formPropertyMap.put("readable", readable);
                        list.add(formPropertyMap);
                    }
                }catch (Exception e){

                }

            }
        }else{
            List<FormProperty> formPropertyList = formService.getStartFormData(procDefId).getFormProperties();
            for(FormProperty formProperty : formPropertyList){
                try {
                    HashMap<String,Object> formPropertyMap = new HashMap<>();

                    boolean readable = formProperty.isReadable();
                    if(readable){
                        formPropertyMap.put("id", formProperty.getId());
                        formPropertyMap.put("name", formProperty.getName());
                        formPropertyMap.put("value", historicVariableMap.get(formProperty.getId()));
                        formPropertyMap.put("readable", readable);
                        list.add(formPropertyMap);
                    }
                }catch (Exception e){

                }

            }
        }


        return AjaxJson.success().put("taskFormData", list);
    }

    /**
     * 提交表单
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@PostMapping("submitTaskFormData")
    public AjaxJson submitTaskFormData(Flow flow, @RequestParam("data") String data) {
        TaskFormData taskFormData = formService.getTaskFormData(flow.getTaskId ());//根据任务ID拿取表单数据
        List<FormProperty> formProperties = taskFormData.getFormProperties();//获取表单字段值
        JSONObject jData = JSONObject.fromObject(data);
        Map<String, Object> formValues = new HashMap<String, Object>();
        HashSet noCommitValues = new HashSet();

        for (FormProperty formProperty : formProperties) {
            if (!formProperty.isWritable()) {
               noCommitValues.add(formProperty.getId());
            }
        }

        for(Object str: jData.keySet()){

          if(!noCommitValues.contains(str.toString())){
              Object o = jData.get(str.toString());
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
        return AjaxJson.success(DictUtils.getLanguageLabel("提交成功", ""));
    }


}
