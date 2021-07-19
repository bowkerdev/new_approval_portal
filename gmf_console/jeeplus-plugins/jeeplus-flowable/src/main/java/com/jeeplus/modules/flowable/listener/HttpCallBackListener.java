package com.jeeplus.modules.flowable.listener;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.utils.CookieUtils;
import com.jeeplus.common.utils.SpringContextHolder;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.net.HttpUtil;
import com.jeeplus.config.web.Servlets;
import com.jeeplus.modules.flowable.callback.entity.ActCallbackUpperSystemConfig;
import com.jeeplus.modules.flowable.callback.service.ActCallbackUpperSystemConfigService;
import com.jeeplus.modules.flowable.service.FlowTaskService;
import com.jeeplus.modules.log.entity.AsynHttpLog;
import com.jeeplus.modules.log.service.AsynHttpLogService;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.security.util.JWTUtil;
import com.jeeplus.modules.sys.utils.UserUtils;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
@Component("httpCallBackListener")
public class HttpCallBackListener implements ExecutionListener {
	private static final long serialVersionUID = 1L;
	private static FlowTaskService flowTaskService=null;
	private static ActCallbackUpperSystemConfigService actCallbackUpperSystemConfigService= null;
	private static AsynHttpLogService asynHttpLogService= null;
	private  void init() {
		if(flowTaskService==null||actCallbackUpperSystemConfigService==null||asynHttpLogService==null){
			init2();
		}
	}
	private synchronized void init2() {
		if(flowTaskService==null){
			flowTaskService= SpringContextHolder.getBean(FlowTaskService.class);
		}
		if(actCallbackUpperSystemConfigService==null){
			actCallbackUpperSystemConfigService= SpringContextHolder.getBean(ActCallbackUpperSystemConfigService.class);
		}
		if(asynHttpLogService==null){
			asynHttpLogService= SpringContextHolder.getBean(AsynHttpLogService.class);
		}
	}
	@SuppressWarnings("rawtypes")
	private JSONObject getMapData(HashMap map,String param) {
		String[] tmps=param.split("\\.");
		HashMap copyMap=map;
		int i=0;
		JSONObject json=new JSONObject();
		for(;tmps.length > i+1;i++){
			String tmp = tmps[i];
			copyMap=(HashMap) copyMap.get(tmp);
			if(copyMap == null){
				break;
			}
		}
		if(copyMap == null){
			return new JSONObject();
		}
		json.put(tmps[i], copyMap.get(tmps[i]));
		return json;
	}
	
	@SuppressWarnings("rawtypes")
	private JSONObject getReturnJson(String paramList,HashMap flowMapPid) {
		String[] tmps=paramList.split(";");
		JSONObject json=new JSONObject();
		for(String tmp:tmps){
			json.putAll(getMapData(flowMapPid,tmp));
		}
		return json;
	}
	
	private String getToken() {
		HttpServletRequest request=Servlets.getRequest();
		String token0 = request.getParameter(JWTUtil.TOKEN);
        String token1 = request.getHeader(JWTUtil.TOKEN);
        String token2 = CookieUtils.getCookie(request, JWTUtil.TOKEN);
        if(StringUtils.isNotBlank(token0)){
            return token0;
        }
        if(StringUtils.isNotBlank(token1)){
            return token1;
        }
        if(StringUtils.isNotBlank(token2)){
            return token2;
        }
        User user = UserUtils.getByLoginName("admin");
        if(user !=null){
        	String token = JWTUtil.createAccessToken(user.getLoginName(), user.getPassword());
        	return token;
        }
        return null;
	}
	
    @SuppressWarnings({  "rawtypes" })
	public void notify(DelegateExecution delegateExecution) {
    	Date d=new Date();
    	AsynHttpLog log=new AsynHttpLog();
        String processInstanceId = delegateExecution.getProcessInstanceId();
        try {
        	init();
            HashMap flowMapPid = flowTaskService.getFlowMapPid(processInstanceId);
            List<ActCallbackUpperSystemConfig> list=actCallbackUpperSystemConfigService.findListByOaKey((String)((Map)flowMapPid.get("procDef")).get("processDefinitionKey"));
            if(list.size()==1){
            	Map<String, String> headers=new HashMap<>();
            	headers.put("Content-Type","application/json");
            	headers.put(JWTUtil.TOKEN, getToken());
            	String json=this.getReturnJson(list.get(0).getParamList(),flowMapPid).toJSONString();
            	log.setParam(json);
            	String res=HttpUtil.post(list.get(0).getUrl(), json, headers);
            	log.setReturnString(res);
            	log.setExecTime(new Date().getTime()-d.getTime());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            log.setReturnString(e.getMessage());
        	log.setExecTime(new Date().getTime()-d.getTime());
        }
        asynHttpLogService.save(log);
    }
}
