/**
 * Copyright &copy; 2015-2020 <a href="http://www.jeeplus.org/">JeePlus</a> All rights reserved.
 */
package com.jeeplus.modules.flow.pr.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jeeplus.common.json.AjaxJson;
import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.config.properties.JeePlusProperites;
import com.jeeplus.core.web.BaseController;
import com.jeeplus.modules.sys.entity.Office;
import com.jeeplus.modules.sys.service.OfficeService;

/**
 * 机构Controller
 *
 * @author jeeplus
 * @version 2019-5-15
 */
@RestController
@RequestMapping("/pr/office")
public class PrOfficeController extends BaseController {

    @Autowired
    private OfficeService officeService;

    @Autowired
    private JeePlusProperites jeePlusProperites;
 
    /**
     * 获取机构JSON数据。
     *
     * @param extId 排除的ID
     * @param type  类型（1：公司；2：部门/小组/其它）
     * @return
     */ 
    @GetMapping("treeData")
    public AjaxJson treeData(@RequestParam(required = false) String extId, @RequestParam(required = false) String type, @RequestParam(required = false) String parentId, @RequestParam(required = false) String parentCode) {
        List<Office> list = officeService.findList (false);
        if (StringUtils.isEmpty(parentId) && !StringUtils.isEmpty(parentCode)) {
        	Office office = officeService.getByCode(parentCode);
        	if (office!=null) {
        		parentId = office.getId();
        	}else{
        		parentId = "-1";
        	}
    	}
        
        List rootTree = getRootTree (list, extId, type, parentId);
        return AjaxJson.success ().put ("treeData", rootTree);
    }

    private List<Office> getRootTree(List<Office> list, String extId, String type, String parentId) {
        List<Office> offices = Lists.newArrayList ();
        if (StringUtils.isEmpty(parentId)){
        	parentId = "0";
        }
        
        List<Office> rootTrees = officeService.getChildren (parentId);
        for (Office root : rootTrees) {
            if ((StringUtils.isBlank (extId) || (extId != null && !extId.equals (root.getId ()) && root.getParentIds ().indexOf ("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals ("1") ? type.equals (root.getType ()) : true)))
                    && JeePlusProperites.YES.equals (root.getUseable ())) {
            	// 不是被排除节点的子节点
                List<Office> officeList = formatListToTree (root, list, extId, type);
                offices.addAll (officeList);
            }
        }
        return offices;
    }


    public List<Office> formatListToTree(Office root, List<Office> allList, String extId, String type) {
        String rootId = root.getId ();

        // type为2时，是选择部门，因此禁用type为1的公司节点
        if("2".equals(type) && root.getType().equals("1")){
            root.setDisabled(true);
        }else {
            root.setDisabled(false);
        }
        // 最终的树形态
        List<Office> trees = Lists.newArrayList ();

        // 把需要构造树的所有列表, 根据以父id作为key, 整理为列表
        Map<String, List<Office>> treeMap = Maps.newHashMap ();
        for (Office entity : allList) {
            List<Office> offices = treeMap.get (entity.getParent ().getId ());
            if (offices == null) {
                offices = Lists.newLinkedList ();
            }

            if ((StringUtils.isBlank (extId) || (extId != null && !extId.equals (entity.getId ()) && entity.getParentIds ().indexOf ("," + extId + ",") == -1))
                    && (type == null || (type != null && (type.equals ("1") ? type.equals (entity.getType ()) : true)))
                    && JeePlusProperites.YES.equals (entity.getUseable ())) {
                // type为2时，是选择部门，因此禁用type为1的公司节点
                if("2".equals(type) && entity.getType().equals("1")){
                    entity.setDisabled(true);
                }else {
                    entity.setDisabled(false);
                }
                offices.add (entity);
                treeMap.put (entity.getParent ().getId (), offices);
            }
        }

        // 开始递归格式化
        List<Office> children = treeMap.get (rootId);
        if (children != null) {
            for (Office parent : children) {
                formatFillChildren (parent, treeMap);
                trees.add (parent);
            }
        }

        root.setChildren (trees);
        return Lists.newArrayList (root);
    }

    /**
     * 从treeMap中取出子节点填入parent, 并递归此操作
     **/
    private void formatFillChildren(Office parent, Map<String, List<Office>> treeMap) {

        List<Office> children = treeMap.get (parent.getId ());
        parent.setChildren (children);
        if (children != null && !children.isEmpty ()) {
            for (Office child : children) {
                formatFillChildren (child, treeMap);
            }
        }
    }


}
