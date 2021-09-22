import Vue from 'vue'
import VueI18n from 'vue-i18n'
import http from '@/utils/httpRequest'

var i18nMy = {}

if(window.$i18nMy == null){
  window.$i18nMy = i18nMy
}
i18nMy.initFlag == ""
i18nMy.langData = {}
i18nMy.t = function t(key) {
  var res=$i18nMy.langData[key]
  if(res !=null){
    return res;
  }
  var values = [],
    len = arguments.length - 1;
  while (len-- > 0) values[len] = arguments[len + 1];

  var languageData = localStorage.getItem('languageData') || "{}"
  languageData = JSON.parse(languageData);

  if(window.translationedList == null){
    window.translationedList = []
  }
  if (window.$i18nMy.initFlag == "OK"&&
  key != null && key != '' && languageData[key] == null &&
  window.translationedList.indexOf(key) < 0) {
    console.log("后台国际化："+key)
    window.translationedList.push(key);
    http({
      url: '/sys/sysSimpleLanguage/save/automation',
      method: 'post',
      headers:{'Content-Type':'application/json'},
      data :{code : key}
    }).then(({
      data
    }) => {}).catch((e) => {})
  }
  if(languageData[key] !=null){
    Vue.config.lang=localStorage.getItem('lang')||'en-US'
    switch (Vue.config.lang){
      case 'en-US':window.$i18nMy.langData[key]=languageData[key].en; break;
      case 'zh-CN':window.$i18nMy.langData[key]=languageData[key].cn; break;
      case 'zh-TW':window.$i18nMy.langData[key]=languageData[key].hk; break;
      case 'vn':window.$i18nMy.langData[key]=languageData[key].vn; break;
      case 'cam':window.$i18nMy.langData[key]=languageData[key].cam; break;
      default: break;
    }
  }
  return window.$i18nMy.langData[key]||key

};
i18nMy.getFlowableI18n = function(){
  switch (Vue.config.lang){
    case 'en-US':
    case 'vn':
    case 'cam':{
      return {}
    }; break;
    case 'zh-TW':
    case 'zh-CN':{
      return {
          "Activate the create/remove space tool": "启动创建/删除空间工具",
          "Activate the global connect tool": "启动全局连接工具",
          "Activate the hand tool": "启动手动工具",
          "Activate the lasso tool": "启动 Lasso 工具",
          "Ad-hoc": "Ad-hoc子流程",
          "Add Lane above": "添加到通道之上",
          "Add Lane below": "添加到通道之下",
          "Append compensation activity": "追加补偿活动",
          "Append {type}": "追加 {type}",
          "Business Rule Task": "规则任务",
          "Call Activity": "引用流程",
          "Cancel Boundary Event": "取消边界事件",
          "Cancel End Event": "结束取消事件",
          "Change type": "更改类型",
          "Collapsed Pool": "折叠池",
          "Compensation Boundary Event": "补偿边界事件",
          "Compensation End Event": "结束补偿事件",
          "Compensation Intermediate Throw Event": "中间补偿抛出事件",
          "Compensation Start Event": "补偿启动事件",
          "Complex Gateway": "复杂网关",
          "Conditional Boundary Event (non-interrupting)": "条件边界事件 (非中断)",
          "Conditional Boundary Event": "条件边界事件",
          "Conditional Intermediate Catch Event": "中间条件捕获事件",
          "Conditional Start Event (non-interrupting)": "条件启动事件 (非中断)",
          "Conditional Start Event": "条件启动事件",
          "Connect using Association": "文本关联",
          "Connect using DataInputAssociation": "数据关联",
          "Connect using Sequence/MessageFlow or Association": "消息关联",
          "Create IntermediateThrowEvent/BoundaryEvent": "创建中间抛出/边界事件",
          "Create Pool/Participant": "创建池/参与者",
          "Create expanded SubProcess": "创建可折叠子流程",
          "Create {type}": "创建 {type}",
          "Divide into three Lanes": "分成三条通道",
          "Divide into two Lanes": "分成两条通道",
          "End Event": "结束事件",
          "Error Boundary Event": "错误边界事件",
          "Error End Event": "结束错误事件",
          "Error Start Event": "错误启动事件",
          "Escalation Boundary Event (non-interrupting)": "升级边界事件 (非中断)",
          "Escalation Boundary Event": "升级边界事件",
          "Escalation End Event": "结束升级事件",
          "Escalation Intermediate Throw Event": "中间升级抛出事件",
          "Escalation Start Event (non-interrupting)": "升级启动事件 (非中断)",
          "Escalation Start Event": "升级启动事件",
          "Event Sub Process": "事件子流程",
          "Event based Gateway": "事件网关",
          "Exclusive Gateway": "独占网关",
          "Expanded Pool": "展开池",
          "Inclusive Gateway": "包容网关",
          "Intermediate Throw Event": "中间抛出事件",
          "Link Intermediate Catch Event": "中间链接捕获事件",
          "Link Intermediate Throw Event": "中间链接抛出事件",
          "Loop": "循环",
          "Manual Task": "手动任务",
          "Message Boundary Event (non-interrupting)": "消息边界事件 (非中断)",
          "Message Boundary Event": "消息边界事件",
          "Message End Event": "结束消息事件",
          "Message Intermediate Catch Event": "中间消息捕获事件",
          "Message Intermediate Throw Event": "中间消息抛出事件",
          "Message Start Event (non-interrupting)": "消息启动事件 (非中断)",
          "Message Start Event": "消息启动事件",
          "Parallel Gateway": "并行网关",
          "Parallel Multi Instance": "并行多实例",
          "Receive Task": "接受任务",
          "Remove": "移除",
          "Script Task": "脚本任务",
          "Send Task": "发送任务",
          "Sequential Multi Instance": "串行多实例",
          "Service Task": "服务任务",
          "Signal Boundary Event (non-interrupting)": "信号边界事件 (非中断)",
          "Signal Boundary Event": "信号边界事件",
          "Signal End Event": "结束信号事件",
          "Signal Intermediate Catch Event": "中间信号捕获事件",
          "Signal Intermediate Throw Event": "中间信号抛出事件",
          "Signal Start Event (non-interrupting)": "信号启动事件 (非中断)",
          "Signal Start Event": "信号启动事件",
          "Start Event": "开始事件",
          "Sub Process (collapsed)": "可折叠子流程",
          "Sub Process (expanded)": "可展开子流程",
          "Sub Process": "子流程",
          "Task": "任务",
          "Terminate End Event": "终止边界事件",
          "Timer Boundary Event (non-interrupting)": "定时边界事件 (非中断)",
          "Timer Boundary Event": "定时边界事件",
          "Timer Intermediate Catch Event": "中间定时捕获事件",
          "Timer Start Event (non-interrupting)": "定时启动事件 (非中断)",
          "Timer Start Event": "定时启动事件",
          "Transaction": "事务",
          "User Task": "用户任务",
          "already rendered {element}": "{element} 已呈现",
          "diagram not part of bpmn:Definitions": "图表不是 bpmn:Definitions 的一部分",
          "element required": "需要元素",
          "element {element} referenced by {referenced}#{property} not yet drawn": "元素 {element} 的引用 {referenced}#{property} 尚未绘制",
          "failed to import {element}": "{element} 导入失败",
          "flow elements must be children of pools/participants": "元素必须是池/参与者的子级",
          "more than {count} child lanes": "超过 {count} 条通道",
          "no diagram to display": "没有要显示的图表",
          "no parent for {element} in {parent}": "在 {element} 中没有父元素 {parent}",
          "no process or collaboration to display": "没有可显示的流程或协作",
          "no shape type specified": "未指定形状类型",
          "out of bounds release": "越界释放"
        };
    }; break;
    default: {
      return {}
    }
  }
}

http({
  url: '/sys/sysSimpleLanguage/getAll',
  method: 'get'
}).then(({
  data
}) => {
  if (data && data.success) {
    localStorage.setItem('languageData', JSON.stringify(data.lanMap || '{}'))
    var m = window.$setI18n(localStorage.getItem('lang'))
    window.$i18nMy.initFlag="OK"
  }
}).catch((e) => {
  console.log(e.message)
})

export default i18nMy
