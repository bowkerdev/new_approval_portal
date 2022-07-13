package com.jeeplus.modules.flowable.listener;

import com.alibaba.fastjson.JSON;
import com.jeeplus.common.utils.SpringContextHolder;
import com.jeeplus.modules.flowable.service.FlowTaskService;

import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import java.util.HashMap;
@Component("myTaskListener")
public class MyTaskListener implements TaskListener {
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
	@Override
	public void notify(DelegateTask delegateTask) {
		 String processInstanceId = delegateTask.getProcessInstanceId();
		 String eventName = delegateTask.getEventName();
	        try {
	        	init();
	            HashMap flowMapPid = flowTaskService.getFlowMapPid(processInstanceId,eventName);
	            System.out.println(JSON.toJSONString(flowMapPid));
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        if ("start".equals(eventName)) {
	            System.out.println("start=========");
	        }else if ("end".equals(eventName)) {
	            System.out.println("end=========");
	        } else if ("take".equals(eventName)) {//连线监听
	            System.out.println("take=========");
	        }
		
	}
}
