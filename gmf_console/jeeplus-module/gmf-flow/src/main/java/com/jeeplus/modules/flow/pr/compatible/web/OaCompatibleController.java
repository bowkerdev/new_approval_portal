/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.pr.compatible.web;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.runtime.ProcessInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.net.HttpUtil;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.User;
import com.jeeplus.modules.sys.security.util.JWTUtil;
import com.jeeplus.modules.sys.utils.DictUtils;
import com.jeeplus.modules.sys.utils.UserUtils;

/**
 * PR申请单Controller
 * @author zhimi
 * @version 2021-06-29
 */
@RestController
@RequestMapping(value = "/flow/compatible")
public class OaCompatibleController extends BaseController {
    @Autowired
    private RuntimeService runtimeService;
	private void changeBorrowSampleDate(JSONObject res,String key,SimpleDateFormat sdf) {
		if(res.containsKey(key)&&res.get(key)!=null){
			res.put(key,sdf.format(res.getDate(key)));
		}
	}
	@RequestMapping(value = "borrowsample/saveAjax" ,method = RequestMethod.POST)
	@ResponseBody
	public AjaxJson borrowsample(@RequestBody BorrowSampleOrderHead borrowSampleOrderHead) throws Exception{
		ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery().includeProcessVariables().orderByStartTime().desc();
		processInstanceQuery.variableValueLike("orderId", borrowSampleOrderHead.getOrderId().toString());
        List<ProcessInstance> runningList = processInstanceQuery.list();
        if(runningList.size()!=0){
        	throw new RuntimeException("订单已经提交");
        }
		
		AjaxJson r=new AjaxJson();
		User u =UserUtils.getByUserName(borrowSampleOrderHead.getCreateByLoginName());
		String token=JWTUtil.createAccessToken(u.getLoginName(), u.getPassword());
		String host=DictUtils.getDictValue("thisHost", "flowCompatible", "http://localhost:8082/approval");
		String borrowsampleFlowId=DictUtils.getDictValue("borrowsampleFlowId", "flow_compatible", "sample_approve");
		Map<String,String> headers =new HashMap<>();
		headers.put(JWTUtil.TOKEN, token);
		Map<String, String> params =new HashMap<>();
		params.put("processDefinitionId", borrowsampleFlowId);
		params.put("title", borrowSampleOrderHead.getOrderNo());	
		JSONObject res=(JSONObject) JSON.toJSON(borrowSampleOrderHead);
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		changeBorrowSampleDate(res,"approveTime",sdf);
		changeBorrowSampleDate(res,"createTime",sdf);
		changeBorrowSampleDate(res,"updateTime",sdf);
		changeBorrowSampleDate(res,"estimateReturnedTime",sdf);
		JSONArray jsons= res.getJSONArray("borrowSampleOrderLineList");
		for(int i=0;i<jsons.size();i++){
			changeBorrowSampleDate(jsons.getJSONObject(i),"updateTime",sdf);
			changeBorrowSampleDate(jsons.getJSONObject(i),"createTime",sdf);
		}
		params.put("data",res.toJSONString());	
		String src=HttpUtil.post(host+"/flowable/form/submitStartFormData", params, headers);
		r.setMsg(src);
		return r;
	}

	@RequestMapping(value = "borrowsample/endApproveAjax" ,method = RequestMethod.GET)
	@ResponseBody
	public AjaxJson endApproveAjax(String orderIds) throws Exception{
		AjaxJson r=new AjaxJson();
		try{
			User u =UserUtils.getByUserName("admin");
			String token=JWTUtil.createAccessToken(u.getLoginName(), u.getPassword());
			String host=DictUtils.getDictValue("thisHost", "flowCompatible", "http://localhost:8082/approval");
			Map<String,String> headers =new HashMap<>();
			headers.put(JWTUtil.TOKEN, token);
			
			String [] tmp=orderIds.split(",");
			for(String orderId:tmp){
				ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery().includeProcessVariables().orderByStartTime().desc();
				processInstanceQuery.variableValueLike("orderId", orderId);
		        List<ProcessInstance> runningList = processInstanceQuery.list();
		        for(ProcessInstance p:runningList){
		        	Map<String, String> params =new HashMap<>();
		        	params.put("id", p.getId());
		        	params.put("message", "wms 终止");
		        	HttpUtil.post(host+"/flowable/process/stop", params, headers);
		        }
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return r;
	}
	
}