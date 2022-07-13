package com.jeeplus.modules.flowable.listener;

import com.jeeplus.common.utils.SpringContextHolder;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.flowable.mapper.FlowMapper;
import com.jeeplus.modules.flowable.service.FlowTaskService;
import com.jeeplus.modules.sys.service.DictTypeService;
import com.jeeplus.modules.sys.service.SysSimpleLanguageService;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.HashMap;
import java.util.Map;

public class UpdateProcStatusListener implements ExecutionListener {
	private static final long serialVersionUID = 1L;
	private static FlowTaskService flowTaskService= null;
    private static FlowMapper flowMapper= null;
	private static void init() {
		if(flowTaskService==null || flowMapper ==null){
			init2();
		}
	}
	private synchronized static void init2() {
		if(flowTaskService==null){
			flowTaskService= SpringContextHolder.getBean(FlowTaskService.class);
		}
		if(flowMapper==null){
			flowMapper= SpringContextHolder.getBean(FlowMapper.class);
		}
	}
    
    public void notify(DelegateExecution delegateExecution) {
    	init();
        String processInstanceId = delegateExecution.getProcessInstanceId();
        String eventName = delegateExecution.getEventName();
        try {
            HashMap flowMap = flowTaskService.getFlowMapPid(processInstanceId,eventName);
            Map<String,Object> varsMap = (Map<String, Object>) flowMap.get("vars");
            if(varsMap!=null){
                Object businessTableObj = varsMap.get("businessTable");
                if(businessTableObj!=null && StringUtils.isNotBlank(businessTableObj.toString())){
                    String businessTable=businessTableObj.toString();
                    //执行更新操作
                    if ("start".equals(eventName)) {
                        System.out.println("start=========");
                    }else if ("end".equals(eventName)) {
                        //审核作废
                        flowMapper.updateProcStatusByProcInsId(processInstanceId,"-1",businessTable);
                        System.out.println("end=========");
                    } else if ("take".equals(eventName)) {//连线监听
                        //审核通过：（1）更新流程状态；（2）如果发布的是服务，更新该人的提供服务标签；如果发布的是需求，更新该人的关注标签
                        flowMapper.updateProcStatusByProcInsId(processInstanceId,"9",businessTable);
                        System.out.println("take=========");
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
