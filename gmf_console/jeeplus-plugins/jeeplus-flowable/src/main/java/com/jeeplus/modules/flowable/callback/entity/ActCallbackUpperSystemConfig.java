/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.callback.entity;


import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;
import lombok.Data;

/**
 * *Entity
 * @author LJ
 * @version 2021-08-13
 */
@Data
public class ActCallbackUpperSystemConfig extends DataEntity<ActCallbackUpperSystemConfig> {
	
	private static final long serialVersionUID = 1L;
	@ExcelField(title="流程标识", align=2, sort=7)
	private String oaKey;		// 流程标识
	@ExcelField(title="通知的接口", align=2, sort=8)
	private String url;		// 通知的接口
	@ExcelField(title="需返回的参数列表", align=2, sort=9)
	private String getParamJs;		// 需返回的参数列表
	
	public ActCallbackUpperSystemConfig() {
		super();
	}
	
	public ActCallbackUpperSystemConfig(String id){
		super(id);
	}
}