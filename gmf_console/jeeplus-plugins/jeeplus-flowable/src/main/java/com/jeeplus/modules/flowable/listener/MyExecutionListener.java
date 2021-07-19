package com.jeeplus.modules.flowable.listener;

import com.jeeplus.common.utils.SpringContextHolder;
import com.jeeplus.modules.flowable.service.FlowTaskService;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
@Component("myExecutionListener")
public class MyExecutionListener implements ExecutionListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static FlowTaskService flowTaskService= null;
    private  void init() {
		if(flowTaskService==null){
			init2();
		}
	}
	private synchronized void init2() {
		if(flowTaskService==null){
			flowTaskService= SpringContextHolder.getBean(FlowTaskService.class);
		}
	}
    @SuppressWarnings("rawtypes")
	public void notify(DelegateExecution delegateExecution) {
        String processInstanceId = delegateExecution.getProcessInstanceId();
        try {
        	init();
            HashMap flowMapPid = flowTaskService.getFlowMapPid(processInstanceId);
            System.out.println(flowMapPid.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String eventName = delegateExecution.getEventName();

        if ("start".equals(eventName)) {
            System.out.println("start=========");
        }else if ("end".equals(eventName)) {
            System.out.println("end=========");
        } else if ("take".equals(eventName)) {//连线监听
            System.out.println("take=========");
        }
    }
}
