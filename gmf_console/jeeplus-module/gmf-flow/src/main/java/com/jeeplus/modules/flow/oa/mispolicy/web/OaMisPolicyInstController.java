/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.oa.mispolicy.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jeeplus.modules.flow.oa.mispolicy.service.OaMisPolicyInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.flow.oa.mispolicy.entity.OaMisPolicyInst;

/**
 * MIS PolicyController
 * @author zhimi
 * @version 2022-04-20
 */
@RestController
@RequestMapping(value = "/flow/oa/mispolicy/oaMisPolicyInst")
public class OaMisPolicyInstController extends BaseController {

	@Autowired
	private OaMisPolicyInstService oaMisPolicyInstService;

	@ModelAttribute
	public OaMisPolicyInst get(@RequestParam(required=false) String id) {
		OaMisPolicyInst entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = oaMisPolicyInstService.get(id);
		}
		if (entity == null){
			entity = new OaMisPolicyInst();
		}
		return entity;
	}

	/**
	 * MIS Policy列表数据
	 */
	@GetMapping("list")
	public AjaxJson list(OaMisPolicyInst oaMisPolicyInst, HttpServletRequest request, HttpServletResponse response) {
		Page<OaMisPolicyInst> page = oaMisPolicyInstService.findPage(new Page<OaMisPolicyInst>(request, response), oaMisPolicyInst);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取MIS Policy数据
	 */
	@GetMapping("queryById")
	public AjaxJson queryById(OaMisPolicyInst oaMisPolicyInst) {
		return AjaxJson.success().put("oaMisPolicyInst", oaMisPolicyInst);
	}

	/**
	 * 保存MIS Policy
	 */
	@PostMapping("save")
	public AjaxJson save(OaMisPolicyInst oaMisPolicyInst, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(oaMisPolicyInst);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑保存
		oaMisPolicyInstService.save(oaMisPolicyInst);//保存
		return AjaxJson.success("保存MIS Policy成功").put("businessTable", "oa_mis_policy_inst").put("businessId", oaMisPolicyInst.getId());
	}


	/**
	 * 批量删除MIS Policy
	 */
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			oaMisPolicyInstService.delete(new OaMisPolicyInst(id));
		}
		return AjaxJson.success("删除MIS Policy成功");
	}


}