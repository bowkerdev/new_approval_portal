/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Process;
import org.flowable.editor.constants.ModelDataJsonConstants;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.Model;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.flowable.ui.common.security.SecurityUtils;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.service.BaseService;
import com.jeeplus.modules.flowable.constant.FlowableConstant;
import com.jeeplus.modules.flowable.entity.TaskComment;
import com.jeeplus.modules.flowable.mapper.FlowMapper;
import com.jeeplus.modules.flowable.service.converter.json.FlowModelService;
import com.jeeplus.modules.flowable.vo.ActVo;
import com.jeeplus.modules.flowable.vo.ActionType;
import com.jeeplus.modules.flowable.vo.ProcessStatus;
import com.jeeplus.modules.flowable.vo.ProcessVo;
import com.jeeplus.modules.flowable.vo.TaskVo;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * ??????????????????Service
 *
 * @author jeeplus
 * @version 2016-11-03
 */
@Service
@Transactional(readOnly = true)
public class FlowProcessService extends BaseService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private FlowModelService flowModelService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private FlowableBpmnModelService flowableBpmnModelService;
    @Autowired
    private FlowMapper flowMapper;
    
    /**
     * ??????key????????????
     */
    public ProcessDefinition getProcessDefinitionByKey(String key) {

        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .latestVersion().orderByProcessDefinitionKey().asc();
        processDefinitionQuery.processDefinitionKey(key);
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();
        if(processDefinitionList.size() > 0){
            return  processDefinitionList.get(0);
        }else {
            return null;
        }
    }

    public boolean isAuth(User user, String processDefId) {
        List<IdentityLink> identityLinks = repositoryService.getIdentityLinksForProcessDefinition(processDefId);
        if(identityLinks.size() == 0){
            return true;
        }
        for (IdentityLink identityLink : identityLinks ) {
            if(user.getId().equals(identityLink.getUserId())){
                return true;
            }
            if((","+user.getRoleIds()+",").contains(","+identityLink.getGroupId()+",")) {
                return true;
            }
        }
        return false;
    }
    /**
     * ??????????????????
     */
    public Page<Map> processList(Page<Map> page, String category) {
        ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery().active ()
                .latestVersion().orderByProcessDefinitionKey().asc();
        if (StringUtils.isNotEmpty(category)) {
            processDefinitionQuery.processDefinitionCategory(category);
        }

        List<ProcessDefinition> processDefinitionList = Lists.newArrayList();
        processDefinitionList = processDefinitionQuery.list();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            if(this.isAuth(UserUtils.getUser(), processDefinition.getId())){
                String deploymentId = processDefinition.getDeploymentId();
                Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
                Map pMap = new HashMap<>();
                pMap.put("id", processDefinition.getId());
                pMap.put("category", processDefinition.getCategory());
                pMap.put("key", processDefinition.getKey());
                pMap.put("name", processDefinition.getName());
                pMap.put("version", "V:" + processDefinition.getVersion());
                pMap.put("resourceName", processDefinition.getResourceName());
                pMap.put("diagramResourceName", processDefinition.getDiagramResourceName());
                pMap.put("deploymentId", processDefinition.getDeploymentId());
                pMap.put("suspended", processDefinition.isSuspended());
                pMap.put("deploymentTime", deployment.getDeploymentTime());
                page.getList().add(pMap);
            }
        }
        page.setCount(page.getList().size());
        return page;
    }

    /**
     * ??????????????????
     */
    public Page<ProcessVo> runningList(Page<ProcessInstance> page, String procInsId, String procDefKey, String title) throws Exception {
        List<ProcessVo> result = new ArrayList<ProcessVo>();
        Page<ProcessVo> resultPage = new Page<>();
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery().includeProcessVariables().orderByStartTime().desc();

        if (StringUtils.isNotBlank(procInsId)) {
            processInstanceQuery.processInstanceId(procInsId);
        }

        if (StringUtils.isNotBlank(procDefKey)) {
            processInstanceQuery.processDefinitionKey(procDefKey);
        }
        
        if (StringUtils.isNotBlank(title)) {
        	processInstanceQuery.variableValueLike("title", "%"+title+"%");
        }
        
        page.setCount(processInstanceQuery.count());
        resultPage.setCount(processInstanceQuery.count());

        List<ProcessInstance> runningList = Lists.newArrayList();
        if (page.getMaxResults() == -1) {//?????????
            runningList = processInstanceQuery.list();
        } else {
            runningList = processInstanceQuery.listPage(page.getFirstResult(), page.getMaxResults());
        }

        for (ProcessInstance p : runningList) {
            ProcessVo processVo  = queryProcessState(p.getProcessDefinitionId (), p.getId());
            processVo.setProcessInstanceId (p.getProcessInstanceId());
            processVo.setProcessDefinitionId (p.getProcessDefinitionId());
            processVo.setProcessDefinitionName (p.getProcessDefinitionName ());
            processVo.setActivityId (p.getActivityId ());
            processVo.setVars (p.getProcessVariables());
            User user = UserUtils.get(String.valueOf(p.getProcessVariables().get("applyUserId")));
            if (user==null) {
            	user = new User();
            	user.setName(String.valueOf(p.getProcessVariables().get("applyUserId")));
            }
            processVo.setCreateBy(user);
            result.add(processVo);
        }
        resultPage.setList(result);
        return resultPage;
    }


    /**
     * ??????????????????
     */
    public Page<ProcessVo> historyList(Page<ProcessVo> page, String procInsId, String procDefKey, String title) throws Exception {
    	ProcessVo pVo = new ProcessVo();
    	pVo.setPage(page);
    	pVo.setTitle(title);
    	List<ProcessVo> list = flowMapper.findHistoryList(pVo) ;
    	List<ProcessVo> mapList = Lists.newArrayList();
 
        for(ProcessVo instance: list){        	
        	HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(instance.getProcessInstanceId()).includeProcessVariables().singleResult();
            ProcessVo processVo = queryProcessState(instance.getProcessDefinitionId (), instance.getProcessInstanceId());
            processVo.setVars (historicProcessInstance.getProcessVariables());
            processVo.setProcessDefinitionName (historicProcessInstance.getProcessDefinitionName());
            processVo.setStartTime (instance.getStartTime());
            processVo.setEndTime (instance.getEndTime());
            processVo.setProcessInstanceId (instance.getProcessInstanceId());
            processVo.setProcessDefinitionId (instance.getProcessDefinitionId());
            processVo.setDeleteReason (historicProcessInstance.getDeleteReason());
            User user = UserUtils.get(historicProcessInstance.getStartUserId());
            if (user==null) {
            	user = new User();
            	user.setName(historicProcessInstance.getStartUserId());
            }
            processVo.setCreateBy(user);
            mapList.add(processVo);
        }
        page.setList(mapList);
        return page;
    }

    /**
     * ???????????????????????????ID
     */
    public InputStream resourceRead(String procDefId, String proInsId, String resType) throws Exception {

        if (StringUtils.isBlank(procDefId)) {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(proInsId).singleResult();
            procDefId = processInstance.getProcessDefinitionId();
        }
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();

        String resourceName = "";
        if (resType.equals("image")) {
            resourceName = processDefinition.getDiagramResourceName();
        } else if (resType.equals("xml")) {
            resourceName = processDefinition.getResourceName();
        }

        InputStream resourceAsStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        return resourceAsStream;
    }

    /**
     * ???????????? - ??????
     *
     * @param file
     * @return
     */
    @Transactional(readOnly = false)
    public AjaxJson deploy(String exportDir, String category, MultipartFile file) {

        AjaxJson j = new AjaxJson();
        String message = "";

        String fileName = file.getOriginalFilename();

        try {
            InputStream fileInputStream = file.getInputStream();
            Deployment deployment = null;
            String extension = FilenameUtils.getExtension(fileName);
            if (extension.equals("zip") || extension.equals("bar")) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                deployment = repositoryService.createDeployment().addZipInputStream(zip).deploy();
            } else if (extension.equals("png")) {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            } else if (fileName.indexOf("bpmn20.xml") != -1) {
                deployment = repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            } else if (extension.equals("bpmn")) { // bpmn?????????????????????????????????bpmn20.xml
                String baseName = FilenameUtils.getBaseName(fileName);
                deployment = repositoryService.createDeployment().addInputStream(baseName + ".bpmn20.xml", fileInputStream).deploy();
            } else {
                j.setSuccess(false);
                j.setMsg("???????????????????????????" + extension);
                return j;
            }

            List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();

            // ??????????????????
            for (ProcessDefinition processDefinition : list) {
//					ActUtils.exportDiagramToFile(repositoryService, processDefinition, exportDir);
                repositoryService.setProcessDefinitionCategory(processDefinition.getId(), category);
                message += "?????????????????????ID=" + processDefinition.getId() + "<br/>";
            }

            if (list.size() == 0) {
                j.setSuccess(false);
                j.setMsg("??????????????????????????????");
                return j;
            }

        } catch (Exception e) {
            e.printStackTrace();
            j.setSuccess(false);
            j.setMsg("????????????:"+e.getMessage());
            return j;
        }
        j.setMsg(message);
        return j;
    }

    /**
     * ??????????????????
     */
    @Transactional(readOnly = false)
    public void updateCategory(String procDefId, String category) {
        repositoryService.setProcessDefinitionCategory(procDefId, category);
    }

    /**
     * ???????????????????????????
     */
    @Transactional(readOnly = false)
    public String updateState(String state, String procDefId) {
        if (state.equals("active")) {
            repositoryService.activateProcessDefinitionById(procDefId, true, null);
            return "?????????ID???[" + procDefId + "]??????????????????";
        } else if (state.equals("suspend")) {
            repositoryService.suspendProcessDefinitionById(procDefId, true, null);
            return "?????????ID???[" + procDefId + "]??????????????????";
        }
        return "?????????";
    }

    /**
     * ?????????????????????????????????
     *
     * @param procDefId
     * @throws UnsupportedEncodingException
     * @throws XMLStreamException
     */
    @Transactional(readOnly = false)
    public Model convertToModel(String procDefId) throws UnsupportedEncodingException, XMLStreamException {
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId).singleResult();
        InputStream bpmnStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(),
                processDefinition.getResourceName());
        XMLInputFactory xif = XMLInputFactory.newInstance();
        InputStreamReader in = new InputStreamReader(bpmnStream, "UTF-8");
        XMLStreamReader xtr = xif.createXMLStreamReader(in);
        BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

        BpmnJsonConverter converter = new BpmnJsonConverter();
        ObjectNode modelNode = converter.convertToJson(bpmnModel);
        Model modelData = repositoryService.newModel();
        modelData.setKey(processDefinition.getKey());
        modelData.setName(processDefinition.getResourceName());
        modelData.setCategory(processDefinition.getCategory());//.getDeploymentId());
        modelData.setDeploymentId(processDefinition.getDeploymentId());
        modelData.setVersion(Integer.parseInt(String.valueOf(repositoryService.createModelQuery().modelKey(modelData.getKey()).count()+1)));

        ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
        modelObjectNode.put(org.flowable.editor.constants.ModelDataJsonConstants.MODEL_NAME, processDefinition.getName());
        modelObjectNode.put(org.flowable.editor.constants.ModelDataJsonConstants.MODEL_REVISION, modelData.getVersion());
        modelObjectNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, processDefinition.getDescription());
        modelData.setMetaInfo(modelObjectNode.toString());

        this.modelService.saveModel(procDefId, modelData.getName(), modelData.getKey(), processDefinition.getDescription(), modelObjectNode.toString(), true, modelData.getVersion().toString(), SecurityUtils.getCurrentUserObject());

        return modelData;
    }

    /**
     * ???????????????????????????
     */
    public List<String> exportDiagrams(String exportDir) throws IOException {
        List<String> files = new ArrayList<String>();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery().list();

        for (ProcessDefinition processDefinition : list) {
            String diagramResourceName = processDefinition.getDiagramResourceName();
            String key = processDefinition.getKey();
            int version = processDefinition.getVersion();
            String diagramPath = "";

            InputStream resourceAsStream = repositoryService.getResourceAsStream(
                    processDefinition.getDeploymentId(), diagramResourceName);
            byte[] b = new byte[resourceAsStream.available()];

            @SuppressWarnings("unused")
            int len = -1;
            resourceAsStream.read(b, 0, b.length);

            // create file if not exist
            String diagramDir = exportDir + "/" + key + "/" + version;
            File diagramDirFile = new File(diagramDir);
            if (!diagramDirFile.exists()) {
                diagramDirFile.mkdirs();
            }
            diagramPath = diagramDir + "/" + diagramResourceName;
            File file = new File(diagramPath);

            // ??????????????????
            if (file.exists()) {
                // ?????????????????????????????????????????????????????????(????????????)
                logger.debug("diagram exist, ignore... : {}", diagramPath);

                files.add(diagramPath);
            } else {
                file.createNewFile();
                logger.debug("export diagram to : {}", diagramPath);

                // wirte bytes to file
                FileUtils.writeByteArrayToFile(file, b, true);

                files.add(diagramPath);
            }

        }

        return files;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param deploymentId ????????????ID
     */
    @Transactional(readOnly = false)
    public void deleteDeployment(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /**
     * ??????????????????????????????
     *
     * @param procInsId    ????????????ID
     * @param deleteReason ????????????????????????
     */
    @Transactional(readOnly = false)
    public void deleteProcIns(String procInsId, String deleteReason) {
        this.stopProcessInstanceById (procInsId, ProcessStatus.DELETED, deleteReason);
    }


    /**
     * ??????????????????
     *
     * @param procInsId    ????????????ID
     * @param processStatus    ????????????
     */
    @Transactional(readOnly = false)
    public void stopProcessInstanceById(String procInsId, ProcessStatus processStatus, String comment) {
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        if (processInstance != null) {
           Task currentTask = taskService.createTaskQuery ().processInstanceId (procInsId).list ().get (0);
            //1?????????????????????
            TaskComment taskComment = new TaskComment ();
            if(processStatus == ProcessStatus.REVOKE){
                taskComment.setType (ActionType.REVOKE.getType ());
                taskComment.setStatus (ActionType.REVOKE.getStatus ());
                taskComment.setMessage (comment);
            }else if(processStatus == ProcessStatus.STOP){
                taskComment.setType (ActionType.STOP.getType ());
                taskComment.setStatus (ActionType.STOP.getStatus ());
                taskComment.setMessage (comment);
            }else if(processStatus == ProcessStatus.REJECT){
                taskComment.setType (ActionType.REJECT.getType ());
                taskComment.setStatus (ActionType.REJECT.getStatus ());
                taskComment.setMessage (comment);
            }else if(processStatus == ProcessStatus.DELETED){
                taskComment.setType (ActionType.DELETED.getType ());
                taskComment.setStatus (ActionType.DELETED.getStatus ());
                taskComment.setMessage (comment);
            }



            taskService.addComment (currentTask.getId (),procInsId, taskComment.getCommentType (), taskComment.getFullMessage ());
            if(StringUtils.isBlank (currentTask.getAssignee ()))  { // ???????????????
                taskService.claim (currentTask.getId (), UserUtils.getUser ().getId ());
            }
            runtimeService.setVariable(procInsId, FlowableConstant.PROCESS_STATUS_CODE, processStatus.getCode ());
            List<EndEvent> endNodes = flowableBpmnModelService.findEndFlowElement(processInstance.getProcessDefinitionId());
            String endId = endNodes.get(0).getId();
            //2???????????????
            List<Execution> executions = runtimeService.createExecutionQuery().parentId(procInsId).list();
            List<String> executionIds = new ArrayList<>();
            executions.forEach(execution -> executionIds.add(execution.getId()));
            this.moveExecutionsToSingleActivityId(executionIds, endId);


        }
    }

    /**
     * ??????????????????
     *
     * @param procInsId    ????????????ID
     */
    @Transactional(readOnly = false)
    public void callBackProcessInstanceById(String procInsId) {

        this.stopProcessInstanceById (procInsId, ProcessStatus.REVOKE, ProcessStatus.REVOKE.getStatus ());


    }


    /**
     * ????????????
     */
    protected void moveExecutionsToSingleActivityId(List<String> executionIds, String activityId) {
        runtimeService.createChangeActivityStateBuilder()
                .moveExecutionsToSingleActivityId(executionIds, activityId)
                .changeState();
    }
    /**
     * ??????????????????
     */
    @Transactional(readOnly = false)
    public void delHistoryProcInsById(String procInsId) {
        historyService.deleteHistoricProcessInstance(procInsId);
    }

    /**
     * ??????????????????
     */
    public ProcessDefinition getProcessDefinition(String proDefId) {
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .latestVersion().orderByProcessDefinitionKey().asc();
        if (StringUtils.isNotBlank(proDefId)) {
            processDefinitionQuery.processDefinitionId(proDefId);
        }
        ProcessDefinition p = processDefinitionQuery.singleResult();
        return p;
    }


    /**
     * ??????????????????
     */
    public Map getImageStream(String processDefId){
            Map m = new HashMap();
            try {
                BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefId);
                byte[] bpmnBytes = flowModelService.getBpmnXML(bpmnModel);
                m.put("bpmnXml", new String(bpmnBytes,"UTF-8"));
                return m;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
    }



    /**
     * ????????????????????????
     *
     * @return
     * @throws Exception
     */
    public ProcessVo queryProcessState( String processDefinitionId, String processInstanceId) throws Exception {
        ProcessVo processVo = new ProcessVo ();
        List<ActVo> actList = flowMapper.findLastestHiActList(processInstanceId);
    	if (actList!=null && actList.size()>0){ 
    		processVo.setAct( actList.get(0) );
    	}
        // ??????????????????ID??????????????????
        ProcessInstance pi = runtimeService.createProcessInstanceQuery ()
                .processInstanceId (processInstanceId).singleResult ();
        if (pi != null) {
            if (pi.isSuspended ()) {
                //????????????
                processVo.setProcessStatus (ProcessStatus.SUSPENDED);
                return processVo;
            } else {
                //????????????
                processVo.setProcessStatus (ProcessStatus.WAITING);
                List<Task> taskList = taskService.createTaskQuery ().processInstanceId (processInstanceId).list ();
                if (taskList.size()>0) {
	                Task currentTask = taskList.get (0);
	                processVo.setTask (new TaskVo (currentTask));
	                processVo.setTaskName (currentTask.getName ());
                }
                
                return processVo;
            }

        } else {
            HistoricProcessInstance pi2 = historyService.createHistoricProcessInstanceQuery ().processInstanceId (processInstanceId).singleResult ();
//            String endActivityId = pi2.getEndActi vityId ();
            HistoricActivityInstance historicActivityInstance = historyService.createHistoricActivityInstanceQuery ().processInstanceId (processInstanceId).finished ().orderByHistoricActivityInstanceEndTime ().desc ().list ().stream ()
                    .filter (activity -> activity.getActivityType ().equals (BpmnXMLConstants.ELEMENT_TASK_USER)
                            || activity.getActivityType ().equals (BpmnXMLConstants.ELEMENT_EVENT_END)
                            || activity.getActivityType ().equals (BpmnXMLConstants.ELEMENT_EVENT_START)).distinct ().collect (Collectors.toList ()).get (0);
            Process process = repositoryService.getBpmnModel (processDefinitionId).getMainProcess ();
            FlowNode flowElement = (FlowNode) process.getFlowElement (historicActivityInstance.getActivityId (), true);
            if (pi2 != null) {

                HistoricVariableInstance processStatusInstance = historyService.createHistoricVariableInstanceQuery ().processInstanceId (processInstanceId).variableName (FlowableConstant.PROCESS_STATUS_CODE).singleResult ();

                if (pi2.getDeleteReason () == null && processStatusInstance == null) {
                    processVo.setProcessStatus (ProcessStatus.AGREE);
                    processVo.setTaskName (flowElement.getName ());
                    return processVo;
                } else if (pi2.getDeleteReason () != null) {
                    processVo.setProcessStatus (ProcessStatus.STOP);
                    processVo.setTaskName (flowElement.getName ());
                    processVo.setDeleteReason (pi2.getDeleteReason ());
                    return processVo;
                } else {
                    int code =Integer.valueOf (processStatusInstance.getValue ().toString ());
                    ProcessStatus processStatus = ProcessStatus.value (code);
                    processVo.setProcessStatus (processStatus);
                    processVo.setTaskName (flowElement.getName ());
                    return processVo;
                }

            } else {
                processVo.setProcessStatus (ProcessStatus.DELETED);
                processVo.setTaskName (flowElement.getName ());
                processVo.setDeleteReason (pi2.getDeleteReason ());
                return processVo;
            }

        }
    }

}
