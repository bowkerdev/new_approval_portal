/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.callback.web;

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
import com.jeeplus.modules.flowable.callback.entity.ActCallbackUpperSystemConfig;
import com.jeeplus.modules.flowable.callback.service.ActCallbackUpperSystemConfigService;
import com.jeeplus.modules.sys.utils.DictUtils;

/**
 * 配置设置Controller
 * @author LJ
 * @version 2020-09-25
 */
@RestController
@RequestMapping(value = "/flowable/callback/actCallbackUpperSystemConfig")
public class ActCallbackUpperSystemConfigController extends BaseController {

	@Autowired
	private ActCallbackUpperSystemConfigService actCallbackUpperSystemConfigService;

	@ModelAttribute
	public ActCallbackUpperSystemConfig get(@RequestParam(required=false) String id) {
		ActCallbackUpperSystemConfig entity = null;
		if (StringUtils.isNotBlank(id)){
			entity = actCallbackUpperSystemConfigService.get(id);
		}
		if (entity == null){
			entity = new ActCallbackUpperSystemConfig();
		}
		return entity;
	}

	/**
	 * 配置设置列表数据
	 */
	@RequiresPermissions("flowable:callback:actCallbackUpperSystemConfig:list")
	@GetMapping("list")
	public AjaxJson list(ActCallbackUpperSystemConfig actCallbackUpperSystemConfig, HttpServletRequest request, HttpServletResponse response) {
		Page<ActCallbackUpperSystemConfig> page = actCallbackUpperSystemConfigService.findPage(new Page<ActCallbackUpperSystemConfig>(request, response), actCallbackUpperSystemConfig);
		return AjaxJson.success().put("page",page);
	}

	/**
	 * 根据Id获取配置设置数据
	 */
	@RequiresPermissions(value={"flowable:callback:actCallbackUpperSystemConfig:view","flowable:callback:actCallbackUpperSystemConfig:add","flowable:callback:actCallbackUpperSystemConfig:edit"},logical=Logical.OR)
	@GetMapping("queryById")
	public AjaxJson queryById(ActCallbackUpperSystemConfig actCallbackUpperSystemConfig) {
		return AjaxJson.success().put("actCallbackUpperSystemConfig", actCallbackUpperSystemConfig);
	}

	/**
	 * 保存配置设置
	 */
	@RequiresPermissions(value={"flowable:callback:actCallbackUpperSystemConfig:add","flowable:callback:actCallbackUpperSystemConfig:edit"},logical=Logical.OR)
	@PostMapping("save")
	public AjaxJson save(ActCallbackUpperSystemConfig actCallbackUpperSystemConfig, Model model) throws Exception{
		/**
		 * 后台hibernate-validation插件校验
		 */
		String errMsg = beanValidator(actCallbackUpperSystemConfig);
		if (StringUtils.isNotBlank(errMsg)){
			return AjaxJson.error(errMsg);
		}
		//新增或编辑表单保存		
		actCallbackUpperSystemConfigService.save(actCallbackUpperSystemConfig);//保存
		return AjaxJson.success(  DictUtils.getLanguageLabel("保存配置设置成功",null));
	}


	/**
	 * 批量删除配置设置
	 */
	@RequiresPermissions("flowable:callback:actCallbackUpperSystemConfig:del")
	@DeleteMapping("delete")
	public AjaxJson delete(String ids) {
		String idArray[] =ids.split(",");
		for(String id : idArray){
			actCallbackUpperSystemConfigService.delete(new ActCallbackUpperSystemConfig(id));
		}
		return AjaxJson.success(DictUtils.getLanguageLabel("删除配置设置成功",null));
	}

	/**
	 * 导出excel文件
	 */
	@RequiresPermissions("flowable:callback:actCallbackUpperSystemConfig:export")
    @GetMapping("export")
    public AjaxJson exportFile(ActCallbackUpperSystemConfig actCallbackUpperSystemConfig, HttpServletRequest request, HttpServletResponse response) {
		try {
            String fileName = "配置设置"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx";
            Page<ActCallbackUpperSystemConfig> page = actCallbackUpperSystemConfigService.findPage(new Page<ActCallbackUpperSystemConfig>(request, response, -1), actCallbackUpperSystemConfig);
    		new ExportExcel("配置设置", ActCallbackUpperSystemConfig.class).setDataList(page.getList()).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error(DictUtils.getLanguageLabel("导出配置设置记录失败！失败信息：",null)+e.getMessage());
		}
    }

	/**
	 * 导入Excel数据

	 */
	@RequiresPermissions("flowable:callback:actCallbackUpperSystemConfig:import")
    @PostMapping("import")
   	public AjaxJson importFile(@RequestParam("file")MultipartFile file, HttpServletResponse response, HttpServletRequest request) {
		try {
			int successNum = 0;
			int failureNum = 0;
			StringBuilder failureMsg = new StringBuilder();
			ImportExcel ei = new ImportExcel(file, 1, 0);
			List<ActCallbackUpperSystemConfig> list = ei.getDataList(ActCallbackUpperSystemConfig.class);
			for (ActCallbackUpperSystemConfig actCallbackUpperSystemConfig : list){
				try{
					actCallbackUpperSystemConfigService.save(actCallbackUpperSystemConfig);
					successNum++;
				}catch(ConstraintViolationException ex){
					failureNum++;
				}catch (Exception ex) {
					failureNum++;
				}
			}
			if (failureNum>0){
			    failureMsg.insert(0,String.format(DictUtils.getLanguageLabel("，失败 %d 条配置设置记录。",""), failureNum));
			}
			return AjaxJson.success(String.format(DictUtils.getLanguageLabel("已成功导入 %d 条配置设置记录%d",""), successNum,failureMsg));
		} catch (Exception e) {
			return AjaxJson.error(DictUtils.getLanguageLabel("导入配置设置失败！失败信息：",null)+e.getMessage());
		}
    }

	/**
	 * 下载导入配置设置数据模板
	 */
	@RequiresPermissions("flowable:callback:actCallbackUpperSystemConfig:import")
    @GetMapping("import/template")
     public AjaxJson importFileTemplate(HttpServletResponse response) {
		try {
            String fileName = DictUtils.getLanguageLabel("配置设置数据导入模板",null)+".xlsx";
    		List<ActCallbackUpperSystemConfig> list = Lists.newArrayList();
    		new ExportExcel(DictUtils.getLanguageLabel("配置设置数据",null), ActCallbackUpperSystemConfig.class, 1).setDataList(list).write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			return AjaxJson.error( DictUtils.getLanguageLabel("导入模板下载失败！失败信息：",null)+e.getMessage());
		}
    }


}