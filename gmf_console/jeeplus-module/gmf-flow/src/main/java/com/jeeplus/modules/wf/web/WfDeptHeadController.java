/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.wf.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.utils.DictUtils;
import com.jeeplus.modules.wf.entity.WfDeptHead;
import com.jeeplus.modules.wf.service.WfDeptHeadService;

/**
 * Department Head SettingController
 * @author zhimi
 * @version 2022-11-18
 */
@RestController
@RequestMapping(value = "/wf/wfDeptHead")
public class WfDeptHeadController extends BaseController {

	@Autowired
	private WfDeptHeadService wfDeptHeadService;

	@ModelAttribute
	public WfDeptHead get(@RequestParam(required=false) String id) {
		WfDeptHead entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wfDeptHeadService.get(id);
		}
		if (entity == null){
			entity = new WfDeptHead();
		}
		return entity;
	}

	/**
	 * Department Head Setting列表数据
	 */
	@RequiresPermissions("wf:wfDeptHead:list")
	@GetMapping("list")
	public AjaxJson list(WfDeptHead wfDeptHead, HttpServletRequest request, HttpServletResponse response) {
		Page<WfDeptHead> page = wfDeptHeadService.findPage(new Page<WfDeptHead>(request, response), wfDeptHead);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取Department Head Setting数据
	 */
	@RequiresPermissions(value={"wf:wfDeptHead:view","wf:wfDeptHead:add","wf:wfDeptHead:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(WfDeptHead wfDeptHead) {
		return AjaxJson.success().put("wfDeptHead", wfDeptHead);
	}

	/**
	 * 保存Department Head Setting
	 */
	@RequiresPermissions(value={"wf:wfDeptHead:add","wf:wfDeptHead:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(WfDeptHead wfDeptHead, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(wfDeptHead);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		wfDeptHeadService.save(wfDeptHead);//保存
		return AjaxJson.success(DictUtils.getLanguageLabel("保存成功",null));
	}


	/**
	 * 批量删除Department Head Setting
	 */
	@RequiresPermissions("wf:wfDeptHead:del")
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			wfDeptHeadService.delete(new WfDeptHead(id));
		}
		return AjaxJson.success(DictUtils.getLanguageLabel("删除成功",null));
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("wf:wfDeptHead:export")
    @GetMapping("export")
    public AjaxJson exportFile(WfDeptHead wfDeptHead, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "Department Head Setting"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<WfDeptHead> page = wfDeptHeadService.findPage(new Page<WfDeptHead>(request, response, -1), wfDeptHead);
    		new ExportExcel("Department Head Setting", WfDeptHead.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error(DictUtils.getLanguageLabel("导出失败信息：",null)+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("wf:wfDeptHead:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<WfDeptHead> list = ei.getDataList(WfDeptHead.class);
			for (WfDeptHead wfDeptHead : list){
				try{
					wfDeptHeadService.save(wfDeptHead);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
			    failureMsg.insert(0,String.format(DictUtils.getLanguageLabel("，失败 %d 条记录。",""), failureNum));
			}
			return AjaxJson.success(String.format(DictUtils.getLanguageLabel("已成功导入 %d 条记录%d",""), successNum,failureMsg));
		} catch (Exception e) {
			return AjaxJson.error(DictUtils.getLanguageLabel("导入失败！失败信息：",null)+e.getMessage());
		}
    }

	/**
	 * 下载导入Department Head Setting数据模板
	 */
	@RequiresPermissions("wf:wfDeptHead:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "Department Head Setting Data Template.xlsx";
    		List<WfDeptHead> list = Lists.newArrayList();
    		new ExportExcel("Department Head Setting Data", WfDeptHead.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error(DictUtils.getLanguageLabel("导入模板下载失败！失败信息：",null)+e.getMessage());
		}
    }


}