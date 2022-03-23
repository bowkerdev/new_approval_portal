/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.common.email;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.utils.GetBpToken;
import com.jeeplus.common.utils.net.HttpUtil;
import com.jeeplus.modules.flowable.service.FlowTaskService;

/**
 * 发邮件
 *
 * @author Jack
 * @version 2022-03-12
 */
 
 public class SendEmailThread implements Runnable{ 
    	private String procInstId = null;  
    	private String msgKeyId = null;
		public SendEmailThread( String procInstId, String msgKeyId) {
			super();
			this.procInstId = procInstId; //"1930a4d4-6329-11ec-ac04-00090ffe0001";
			this.msgKeyId = msgKeyId;
		}

		public void run() {
			try {
	    	//String msgKeyId = flowTaskService.getMsgId(procInstId) ;//"386b6a0024784ba8b7d5d4dad60e9a65"; // ID是common tool上配置的邮件发送任务ID
	    	if (StringUtils.isEmpty(msgKeyId)) {
	    		return;
	    	}
	    	
	    	String url = "https://commontools.bowkerasia.com/zhimitool/msg/msgPushConfig/sendImmediately";
	    	JSONObject commontoolsJson = new JSONObject();
	    	JSONObject param = new JSONObject();
	    	param.put("procInstId", procInstId); // "9220f440-48e0-11ec-a087-342eb7b6edfa"
	    	commontoolsJson.put("param", param.toJSONString());
	    	commontoolsJson.put("id", msgKeyId);
	    	commontoolsJson.put("sendTo", "");
	    	Map<String, String> headers = new HashMap<>();
	    	headers.put("Content-Type", "application/json");
	    	headers.put("token",GetBpToken.getToken());
	    	headers.put("tokenType","bowker_baseportal");
	    	
	    	String sendResult; 
				sendResult = HttpUtil.post(url, commontoolsJson.toJSONString(), headers);
				JSONObject sendResultJsonObject = (JSONObject)JSONObject.parse(sendResult);
		    	if (!sendResultJsonObject.getBooleanValue("success")){
		    		throw new RuntimeException("Some errors appeared during notify common tools, please try again!");
		        }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
        }  
    }  

     
/*    public void sendEmail(HttpServletRequest request, String procInstId) throws Exception {
    	String url = "https://commontools.bowkerasia.com/zhimitool/msg/msgPushConfig/sendImmediately";
    	String id = "386b6a0024784ba8b7d5d4dad60e9a65"; // ID是common tool上配置的邮件发送任务ID
    	
    	DictUtils.getDictValue("commontools_url", "sys_config", "https://commontools.bowkerasia.com/zhimitool/msg/msgPushConfig/sendImmediately");
    	
    	JSONObject commontoolsJson = new JSONObject();
    	JSONObject param = new JSONObject();
    	param.put("procInstId", procInstId); 
    	commontoolsJson.put("param", param.toJSONString());
    	commontoolsJson.put("id", id);
    	commontoolsJson.put("sendTo", "");
    	Map<String, String> headers = new HashMap<>();
    	headers.put("Content-Type", "application/json");
    	headers.put("token",request.getHeader("ssoToken"));
    	headers.put("tokenType",request.getHeader("ssoTokenType"));
    	
    	String sendResult = HttpUtil.post(url, commontoolsJson.toJSONString(), headers);
    	JSONObject sendResultJsonObject = (JSONObject)JSONObject.parse(sendResult);
    	if (!sendResultJsonObject.getBooleanValue("success")){
    		throw new RuntimeException("Some errors appeared during notify common tools, please try again!");
        }
    } 
*/
 
