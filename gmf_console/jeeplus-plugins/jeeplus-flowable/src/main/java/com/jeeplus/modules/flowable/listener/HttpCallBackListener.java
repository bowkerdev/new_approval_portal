package com.jeeplus.modules.flowable.listener;

import com.alibaba.fastjson.JSON;
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
import java.util.Optional;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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

	
	private String getToken(String tokenKey) {
		HttpServletRequest request=Servlets.getRequest();
		String token0 = request.getParameter(tokenKey);
        String token1 = request.getHeader(tokenKey);
        String token2 = CookieUtils.getCookie(request, tokenKey);
        if(StringUtils.isNotBlank(token0)){
            return token0;
        }
        if(StringUtils.isNotBlank(token1)){
            return token1;
        }
        if(StringUtils.isNotBlank(token2)){
            return token2;
        }
        return null;
	}
	private String getAdminToken() {
        User user = UserUtils.getByLoginName("admin");
        if(user !=null){
        	String token = JWTUtil.createAccessToken(user.getLoginName(), user.getPassword());
        	return token;
        }
        return null;
	}
	private void setHeader(Map<String, String> headers) {
		headers.put("Content-Type","application/json");
    	String token=getToken("bowker_baseportal_token");
    	if(StringUtils.isNoneBlank(token)){
    		headers.put(JWTUtil.TOKEN,token );
    		headers.put("Authorization","Bearer "+token );
    		headers.put(JWTUtil.TOKENTYPE,"bowker_baseportal" );
    	}
    	else{
    		token = getToken(JWTUtil.TOKEN);
    		if(StringUtils.isNoneBlank(token)){
    			headers.put(JWTUtil.TOKEN,token);
    		}
    		else{
    			headers.put(JWTUtil.TOKEN,getAdminToken());
    		}
    	}
	}
	private String  runJs(String script,String json) throws NoSuchMethodException, ScriptException {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
		engine.eval(script);
		Invocable invoke = (Invocable) engine;
		String resultString = (String) invoke.invokeFunction(
				"getParams",json);
		return resultString;
	}
    @SuppressWarnings({  "rawtypes" })
	public void notify(DelegateExecution delegateExecution) {
    	Date d=new Date();
    	AsynHttpLog log=new AsynHttpLog();
        String processInstanceId = delegateExecution.getProcessInstanceId();
        try {
        	init();
            HashMap flowMap = flowTaskService.getFlowMapPid(processInstanceId);
            JSONObject json=(JSONObject) JSON.toJSON(flowMap);
            log.setParam(json.toJSONString());
            if(flowMap.size()>0){
                String assigneeId =json.getJSONObject("procIns").getJSONObject("currentTask").getString("assignee");
                User assigneeObj= UserUtils.get(assigneeId);
                json.getJSONObject("procIns").getJSONObject("currentTask").put("assigneeLoginName", assigneeObj.getLoginName());
                json.getJSONObject("procIns").getJSONObject("currentTask").put("assigneeName", assigneeObj.getName());
                String processDefinitionKey=json.getJSONObject("procDef").getString("processDefinitionKey");
                List<ActCallbackUpperSystemConfig> list=actCallbackUpperSystemConfigService.findListByOaKey(processDefinitionKey);
                
                ActCallbackUpperSystemConfig c=null;
                if(list.size()>1){
                	String taskDefinitionKey=json.getJSONObject("procIns").getJSONObject("currentTask").getString("taskDefinitionKey");
                	Optional<ActCallbackUpperSystemConfig> tmp=list.stream()
                			.filter(t -> t.getOaKey().endsWith(processDefinitionKey+":"+taskDefinitionKey)).findFirst();
                	if(tmp.isPresent()){ // : 后为空，为整个流程通用，不为空，则是指定
                		c=tmp.get();
                	}
                	else{
                		c=list.stream()
                    		.filter(t -> t.getOaKey().endsWith(processDefinitionKey+":")).findFirst().get();
                	}
                }
                else if(list.size() ==1){
                	c=list.get(0);
                }
                if(c!=null) json.put("upstreamSystemUrl", c.getUrl());
                String jsonString=json.toString();
                log.setParam(jsonString);
                if(c!=null){
                	Map<String, String> headers=new HashMap<>();
                	this.setHeader(headers);
                	if(StringUtils.isNoneBlank(c.getGetParamJs())){
                		jsonString = this.runJs(c.getGetParamJs(),jsonString);
                		JSONObject tmp=JSON.parseObject(jsonString);
                		if(StringUtils.isNoneBlank(tmp.getString("upstreamSystemUrl"))
                		  &&!c.getUrl().equals(tmp.getString("upstreamSystemUrl"))){
                			c.setUrl(tmp.getString("upstreamSystemUrl"));
                		}            		
                	}
                	log.setParam(jsonString);
                	String res=HttpUtil.post(c.getUrl(), jsonString, headers);
                	log.setReturnString(res);
                	log.setExecTime(new Date().getTime()-d.getTime());
                }
            }
            
            asynHttpLogService.save(log);
        } catch (Exception e) {
            e.printStackTrace();
            log.setReturnString(e.getMessage());
        	log.setExecTime(new Date().getTime()-d.getTime());
        	asynHttpLogService.save(log);
        	throw new RuntimeException(e);
        }
        
    }
}
