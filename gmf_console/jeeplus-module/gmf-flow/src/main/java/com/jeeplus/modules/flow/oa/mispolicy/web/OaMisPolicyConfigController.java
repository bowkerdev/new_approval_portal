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
import com.jeeplus.modules.flow.oa.mispolicy.entity.OaMisPolicyConfig;
import com.jeeplus.modules.flow.oa.mispolicy.service.OaMisPolicyConfigService;

/**
 * MIS PolicyController
 * @author zhimi
 * @version 2022-04-19
 */
@RestController
@RequestMapping(value = "/flow/oa/mispolicy/oaMisPolicyConfig")
public class OaMisPolicyConfigController extends BaseController {

	@Autowired
	private OaMisPolicyConfigService oaMisPolicyConfigService;

	@ModelAttribute
	public OaMisPolicyConfig get(@RequestParam(required=false) String id) {
		OaMisPolicyConfig entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = oaMisPolicyConfigService.get(id);
		}
		if (entity == null){
			entity = new OaMisPolicyConfig();
		}
		return entity;
	}

	/**
	 * MIS Policy列表数据
	 */
	/*@RequiresPermissions("oa:mispolicy:oaMisPolicyConfig:list")*/
	@GetMapping("list")
	public AjaxJson list(OaMisPolicyConfig oaMisPolicyConfig, HttpServletRequest request, HttpServletResponse response) {
		Page<OaMisPolicyConfig> page = oaMisPolicyConfigService.findPage(new Page<OaMisPolicyConfig>(request, response), oaMisPolicyConfig);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取MIS Policy数据
	 */
	/*@RequiresPermissions(value={"oa:mispolicy:oaMisPolicyConfig:view","oa:mispolicy:oaMisPolicyConfig:add","oa:mispolicy:oaMisPolicyConfig:edit"},logical=Logical.OR)*/
	@GetMapping("queryById")
	public AjaxJson queryById(OaMisPolicyConfig oaMisPolicyConfig) {
		return AjaxJson.success().put("oaMisPolicyConfig", oaMisPolicyConfig);
	}

	/**
	 * 保存MIS Policy
	 */
	/*@RequiresPermissions(value={"oa:mispolicy:oaMisPolicyConfig:add","oa:mispolicy:oaMisPolicyConfig:edit"},logical=Logical.OR)*/
	@PostMapping("save")
	public AjaxJson save(OaMisPolicyConfig oaMisPolicyConfig, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(oaMisPolicyConfig);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存
		oaMisPolicyConfigService.save(oaMisPolicyConfig);//保存
		return AjaxJson.success("保存MIS Policy成功");
	}


	/**
	 * 批量删除MIS Policy
	 */
	/*@RequiresPermissions("oa:mispolicy:oaMisPolicyConfig:del")*/
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			oaMisPolicyConfigService.delete(new OaMisPolicyConfig(id));
		}
		return AjaxJson.success("删除MIS Policy成功");
	}

	/**
	 * 导出excel文件
	 */
	/*@RequiresPermissions("oa:mispolicy:oaMisPolicyConfig:export")*/
    @GetMapping("export")
    public AjaxJson exportFile(OaMisPolicyConfig oaMisPolicyConfig, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "MIS Policy"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<OaMisPolicyConfig> page = oaMisPolicyConfigService.findPage(new Page<OaMisPolicyConfig>(request, response, -1), oaMisPolicyConfig);
    		new ExportExcel("MIS Policy", OaMisPolicyConfig.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error("导出MIS Policy记录失败！失败信息："+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	/*@RequiresPermissions("oa:mispolicy:oaMisPolicyConfig:import")*/
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<OaMisPolicyConfig> list = ei.getDataList(OaMisPolicyConfig.class);
			for (OaMisPolicyConfig oaMisPolicyConfig : list){
				try{
					oaMisPolicyConfigService.save(oaMisPolicyConfig);
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
	/*@RequiresPermissions("oa:mispolicy:oaMisPolicyConfig:import")*/
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = "MIS Policy数据导入模板.xlsx";
    		List<OaMisPolicyConfig> list = Lists.newArrayList();
    		new ExportExcel("MIS Policy数据", OaMisPolicyConfig.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( "导入模板下载失败！失败信息："+e.getMessage());
		}
    }


}