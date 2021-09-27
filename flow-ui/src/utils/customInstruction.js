import Vue from 'vue'

/**
 * [handleInput 在输入阶段的处理逻辑]
 * @param  {[DOM]} ele   [当前指令操作的dom对象]
 * @param  {[虚拟节点]} vnode [当前指令渲染的虚拟节点]
 * @param  {[指令信息]} rel   [当前指令的所有指令信息]
 * @param  {[校验类型]} type  [输入阶段的校验类型]
 *      "number": 仅支持输入数字
 *      "float": 仅支持数字和小数点
 */
function handleInput(ele,vnode,rel){
  let rule;
  switch(true) {
    case rel.modifiers.float: // 浮点型
      rule = /[^\d\.]/g; break;
    default: //默认仅支持输入数字
      rule = /\D/g;
  }
  let val = ele.value.replace(rule,"");
  if(val !=  ele.value){
    let maxLen = vnode.data.attrs && vnode.data.attrs['max-len'] ? vnode.data.attrs['max-len'] :0;
    if(maxLen>0){val = val.substr(0,maxLen)}
    var currObj=rel.value
    var expression= vnode.data.model.expression.substr(vnode.data.model.expression.indexOf(".")+1)
//    if(vnode.elm!=null&&vnode.elm.children!=null&&vnode.elm.children.length >0){
//      vnode.elm.children[0].value=val
//    }
    currObj[expression] = val
  }
}

Vue.directive('only-num', {
  inserted (el,binding,vnode) {
      let ele = el.tagName === 'INPUT' ? el : el.querySelector('input')
      ele.oninput = function() {
        //获取相关的指令配置信息
        let rel = vnode.data.directives.filter(item =>{
          return item.name === "only-num"
        })[0]
        vnode.context.$nextTick(()=>{
          handleInput(ele,vnode,rel)
        })
      }
  }
});
