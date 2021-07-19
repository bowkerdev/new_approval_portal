/**
 * Copyright © 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flowable.callback.entity;


import com.jeeplus.core.persistence.DataEntity;
import com.jeeplus.common.utils.excel.annotation.ExcelField;

/**
 * 配置设置Entity
 * @author LJ
 * @version 2020-09-25
 */
public class ActCallbackUpperSystemConfig extends DataEntity<ActCallbackUpperSystemConfig> {
	
	private static final long serialVersionUID = 1L;
	private String oaKey;		// 流程标识
	private String url;		// 通知的接口
	private String paramList;		// 需返回的参数列表
	
	public ActCallbackUpperSystemConfig() {
		super();
	}

	public ActCallbackUpperSystemConfig(String id){
		super(id);
	}

	@ExcelField(title="流程标识", align=2, sort=7)
	public String getOaKey() {
		return oaKey;
	}

	public void setOaKey(String oaKey) {
		this.oaKey = oaKey;
	}
	
	@ExcelField(title="通知的接口", align=2, sort=8)
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	@ExcelField(title="需返回的参数列表", align=2, sort=9)
	public String getParamList() {
		return paramList;
	}

	public void setParamList(String paramList) {
		this.paramList = paramList;
	}
	
}