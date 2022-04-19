/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.oa.mispolicy.web;

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
import com.jeeplus.modules.flow.oa.mispolicy.entity.OaMisPolicyInst;
import com.jeeplus.modules.flow.oa.mispolicy.service.OaMisPolicyInstService;

/**
 * MIS PolicyController
 * @author zhimi
 * @version 2022-04-19
 */
@RestController
@RequestMapping(value = "/oa/mispolicy/oaMisPolicyInst")
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
	@RequiresPermissions("oa:mispolicy:oaMisPolicyInst:list")
	@GetMapping("list")
	public AjaxJson list(OaMisPolicyInst oaMisPolicyInst, HttpServletRequest request, HttpServletResponse response) {
		Page<OaMisPolicyInst> page = oaMisPolicyInstService.findPage(new Page<OaMisPolicyInst>(request, response), oaMisPolicyInst);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取MIS Policy数据
	 */
	@RequiresPermissions(value={"oa:mispolicy:oaMisPolicyInst:view","oa:mispolicy:oaMisPolicyInst:add","oa:mispolicy:oaMisPolicyInst:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(OaMisPolicyInst oaMisPolicyInst) {
		return AjaxJson.success().put("oaMisPolicyInst", oaMisPolicyInst);
	}

	/**
	 * 保存MIS Policy
	 */
	@RequiresPermissions(value={"oa:mispolicy:oaMisPolicyInst:add","oa:mispolicy:oaMisPolicyInst:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(OaMisPolicyInst oaMisPolicyInst, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(oaMisPolicyInst);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		oaMisPolicyInstService.save(oaMisPolicyInst);//保存
		return AjaxJson.success("保存MIS Policy成功");
	}


	/**
	 * 批量删除MIS Policy
	 */
	@RequiresPermissions("oa:mispolicy:oaMisPolicyInst:del")
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			oaMisPolicyInstService.delete(new OaMisPolicyInst(id));
		}
		return AjaxJson.success("删除MIS Policy成功");
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("oa:mispolicy:oaMisPolicyInst:export")
    @GetMapping("export")
    public AjaxJson exportFile(OaMisPolicyInst oaMisPolicyInst, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "MIS Policy"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<OaMisPolicyInst> page = oaMisPolicyInstService.findPage(new Page<OaMisPolicyInst>(request, response, -1), oaMisPolicyInst);
    		new ExportExcel("MIS Policy", OaMisPolicyInst.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出MIS Policy记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("oa:mispolicy:oaMisPolicyInst:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<OaMisPolicyInst> list = ei.getDataList(OaMisPolicyInst.class);
			for (OaMisPolicyInst oaMisPolicyInst : list){
				try{
					oaMisPolicyInstService.save(oaMisPolicyInst);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
				failureMsg.insert(0, "，失败 "+failureNum+" 条MIS Policy记录。");
			}
			return AjaxJson.success( "已成功导入 "+successNum+" 条MIS Policy记录"+failureMsg);
		} catch (Exception e) {
			return AjaxJson.error("导入MIS Policy失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 下载导入MIS Policy数据模板
	 */
	@RequiresPermissions("oa:mispolicy:oaMisPolicyInst:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "MIS Policy数据导入模板.xlsx";
    		List<OaMisPolicyInst> list = Lists.newArrayList();
    		new ExportExcel("MIS Policy数据", OaMisPolicyInst.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}