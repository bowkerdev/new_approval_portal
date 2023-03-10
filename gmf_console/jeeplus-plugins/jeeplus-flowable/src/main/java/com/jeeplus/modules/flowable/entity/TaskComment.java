package com.jeeplus.modules.flowable.entity;

import com.jeeplus.common.utils.StringUtils;
import com.jeeplus.modules.flowable.constant.FlowableConstant;
import com.jeeplus.modules.flowable.vo.ActionType;
import lombok.Data;


@Data
public class TaskComment {

    private String type; // 操作按钮编码
    private String commentType; //历史记录的类型
    private String status;        // 操作按钮文字
    private String message;    // 任务意见
    private String fullMessage; // status + '_FLOW_ACTION_' + message
    private String level; //显示级别 info danger success

    public static final String prefix = "COMMENT_";
    public String getFullMessage(){
        return this.status + FlowableConstant.FLOW_ACTION+this.message;
    }

    public void setFullMessage(String fullMessage){
        if(StringUtils.isBlank (fullMessage) || !fullMessage.contains (FlowableConstant.FLOW_ACTION)){
            this.setMessage (fullMessage);
        }else{
          String[] list =  fullMessage.split (FlowableConstant.FLOW_ACTION);
          if(list.length > 1){
              this.setStatus (list[0]);
              this.setMessage (list[1]);
          }else if(list.length > 0){
              this.setStatus(list[0]);
              this.setMessage ("");
          }else{
        	  this.setStatus("");
              this.setMessage("");
          }
        }

    }
    public void setType(String type){
        this.type = type;
        this.level = ActionType.getLevel (type);
    }
    public void setCommentType(String commentType){
        if(commentType.startsWith (prefix)){
            this.type = commentType.substring (prefix.length ());
            this.level = ActionType.getLevel (type);
        }

    }
    public String getCommentType(){
        return prefix+type;
    }
}
