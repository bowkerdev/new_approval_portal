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
import com.jeeplus.modules.flowable.wf.entity.WfUserGroup;
import com.jeeplus.modules.flowable.wf.service.WfUserGroupService;

/**
 * 流程用户组设置Controller
 * @author zhimi
 * @version 2020-09-22
 */
@RestController
@RequestMapping(value = "/flowable/wf/wfUserGroup")
public class WfUserGroupController extends BaseController {

	@Autowired
	private WfUserGroupService wfUserGroupService;

	@ModelAttribute
	public WfUserGroup get(@RequestParam(required=false) String id) {
		WfUserGroup entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = wfUserGroupService.get(id);
		}
		if (entity == null){
			entity = new WfUserGroup();
		}
		return entity;
	}

	/**
	 * 流程用户组设置列表数据
	 */
	@RequiresPermissions("flowable:wf:wfUserGroup:list")
	@GetMapping("list")
	public AjaxJson list(WfUserGroup wfUserGroup, HttpServletRequest request, HttpServletResponse response) {
		Page<WfUserGroup> page = wfUserGroupService.findPage(new Page<WfUserGroup>(request, response), wfUserGroup);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取流程用户组设置数据
	 */
	@RequiresPermissions(value={"flowable:wf:wfUserGroup:view","flowable:wf:wfUserGroup:add","flowable:wf:wfUserGroup:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(WfUserGroup wfUserGroup) {
		return AjaxJson.success().put("wfUserGroup", wfUserGroup);
	}

	/**
	 * 保存流程用户组设置
	 */
	@RequiresPermissions(value={"flowable:wf:wfUserGroup:add","flowable:wf:wfUserGroup:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(WfUserGroup wfUserGroup, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(wfUserGroup);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		wfUserGroupService.save(wfUserGroup);//保存
		return AjaxJson.success("保存流程用户组设置成功");
	}


	/**
	 * 批量删除流程用户组设置
	 */
	@RequiresPermissions("flowable:wf:wfUserGroup:del")
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			wfUserGroupService.delete(new WfUserGroup(id));
		}
		return AjaxJson.success("删除流程用户组设置成功");
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("flowable:wf:wfUserGroup:export")
    @GetMapping("export")
    public AjaxJson exportFile(WfUserGroup wfUserGroup, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "流程用户组设置"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<WfUserGroup> page = wfUserGroupService.findPage(new Page<WfUserGroup>(request, response, -1), wfUserGroup);
    		new ExportExcel("流程用户组设置", WfUserGroup.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出流程用户组设置记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("flowable:wf:wfUserGroup:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<WfUserGroup> list = ei.getDataList(WfUserGroup.class);
			for (WfUserGroup wfUserGroup : list){
				try{
					wfUserGroupService.save(wfUserGroup);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条流程用户组设置记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条流程用户组设置记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入流程用户组设置失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入流程用户组设置数据模板
	 */
	@RequiresPermissions("flowable:wf:wfUserGroup:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "流程用户组设置数据导入模板.xlsx";
    		List<WfUserGroup> list = Lists.newArrayList();
    		new ExportExcel("流程用户组设置数据", WfUserGroup.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}