/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.wf.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.jeeplus.common.utils.DateUtils;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.core.persistence.Page;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.common.utils.excel.ExportExcel;
import com.jeeplus.common.utils.excel.ImportExcel;
import com.jeeplus.modules.flowable.wf.entity.WfDelegate;
import com.jeeplus.modules.flowable.wf.service.WfDelegateService;

/**
 * 流程委托设置Controller
 * @author Jack
 * @version 2021-07-01
 */
@RestController
@RequestMapping(value = "/flowable/wf/wfDelegate")
public class WfDelegateController extends BaseController {

	@Autowired
	private WfDelegateService wfDelegateService;

	@ModelAttribute
	public WfDelegate get(@RequestParam(required=false) String id) {
		WfDelegate entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wfDelegateService.get(id);
		}
		if (entity == null){
			entity = new WfDelegate();
		}
		return entity;
	}

	/**
	 * 流程委托设置列表数据
	 */
	@RequiresPermissions("flowable:wf:wfDelegate:list")
	@GetMapping("list")
	public AjaxJson list(WfDelegate wfDelegate, HttpServletRequest request, HttpServletResponse response) {
		Page<WfDelegate> page = wfDelegateService.findPage(new Page<WfDelegate>(request, response), wfDelegate);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取流程委托设置数据
	 */
	@RequiresPermissions(value={"flowable:wf:wfDelegate:view","flowable:wf:wfDelegate:add","flowable:wf:wfDelegate:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(WfDelegate wfDelegate) {
		return AjaxJson.success().put("wfDelegate", wfDelegate);
	}

	/**
	 * 保存流程委托设置
	 */
	@RequiresPermissions(value={"flowable:wf:wfDelegate:add","flowable:wf:wfDelegate:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(WfDelegate wfDelegate, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(wfDelegate);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		wfDelegateService.save(wfDelegate);//保存
		return AjaxJson.success("保存成功");
	}


	/**
	 * 批量删除流程委托设置
	 */
	@RequiresPermissions("flowable:wf:wfDelegate:del")
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			wfDelegateService.delete(new WfDelegate(id));
		}
		return AjaxJson.success("删除成功");
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("flowable:wf:wfDelegate:export")
    @GetMapping("export")
    public AjaxJson exportFile(WfDelegate wfDelegate, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "流程委托设置"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<WfDelegate> page = wfDelegateService.findPage(new Page<WfDelegate>(request, response, -1), wfDelegate);
    		new ExportExcel("流程委托设置", WfDelegate.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出流程委托设置记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("flowable:wf:wfDelegate:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<WfDelegate> list = ei.getDataList(WfDelegate.class);
			for (WfDelegate wfDelegate : list){
				try{
					wfDelegateService.save(wfDelegate);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入流程委托设置数据模板
	 */
	@RequiresPermissions("flowable:wf:wfDelegate:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "流程委托设置数据导入模板.xlsx";
    		List<WfDelegate> list = Lists.newArrayList();
    		new ExportExcel("流程委托设置数据", WfDelegate.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}