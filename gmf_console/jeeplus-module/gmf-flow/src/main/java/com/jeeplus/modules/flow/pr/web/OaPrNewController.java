/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.pr.web;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.jeeplus.modules.flow.pr.entity.OaPrNew;
import com.jeeplus.modules.flow.pr.service.OaPrNewService;

/**
 * PR申请单Controller
 * @author zhimi
 * @version 2021-06-29
 */
@RestController
@RequestMapping(value = "/flow/pr/oaPrNew")
public class OaPrNewController extends BaseController {

	@Autowired
	private OaPrNewService oaPrNewService;

	@ModelAttribute
	public OaPrNew get(@RequestParam(required=false) String id) {
		OaPrNew entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = oaPrNewService.get(id);
		}
		if (entity == null){
			entity = new OaPrNew();
		}
		return entity;
	}

	/**
	 * PR申请单列表数据
	 */
	@GetMapping("list")
	public AjaxJson list(OaPrNew oaPrNew, HttpServletRequest request, HttpServletResponse response) {
		Page<OaPrNew> page = oaPrNewService.findPage(new Page<OaPrNew>(request, response), oaPrNew);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取PR申请单数据
	 */
	@GetMapping("queryById")
	public AjaxJson queryById(OaPrNew oaPrNew) {
		return AjaxJson.success().put("oaPrNew", oaPrNew);
	}

	/**
	 * 保存PR申请单
	 */
	@PostMapping("save")
	public AjaxJson save(OaPrNew oaPrNew, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(oaPrNew);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑保存
		oaPrNewService.save(oaPrNew);//保存
		return AjaxJson.success("保存PR申请单成功").put("businessTable", "oa_pr_new").put("businessId", oaPrNew.getId());
	}


	/**
	 * 批量删除PR申请单
	 */
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			oaPrNewService.delete(new OaPrNew(id));
		}
		return AjaxJson.success("删除PR申请单成功");
	}


}