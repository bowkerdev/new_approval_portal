import _i18nMy from '@/utils/i18n2'
export default {
  dealHtml(html){
    var copy=html
    html=html.replace(/\n/g,"").replace(/\t/g,"").replace(/ /g,"");
    var array = html.match(/>[^\x00-\xff]+</g);
    if(array!=null){
      for(var i=0;i<array.length;i++){
        html=html.replaceAll(array[i],">"+_i18nMy.t(array[i].replace("<","").replace(">",""))+"<")
      }
      return html
    }
    else{
     return copy
    }
  },
  setValue(taskFormData,obj ){
    if(taskFormData !=null){
      var obj2=taskFormData.find(function(e){return e.id==obj.model})
      if(obj2!=null){
        obj2.value = obj.options.defaultValue
      }
      else{
        debugger
      }
    }
  },
  dealObj(obj,taskFormData){
    if(obj.options!=null&&!obj.options.hidden){
      console.log(obj.type+" label："+obj.name)
      switch(obj.type){
        case 'html':{
          obj.options.defaultValue = this.dealHtml(obj.options.defaultValue);
          this.setValue(taskFormData,obj )
          break ;
        }
        case 'text':{
          obj.options.defaultValue = _i18nMy.t(obj.options.defaultValue);
          this.setValue(taskFormData,obj )
          break ;
        }
        case 'radio':
        case 'date':
        case 'textarea':
        case 'input':
        case 'imgupload':
        case 'fileupload':{
          if(obj.name!=null&&obj.name!=''){
            obj.name = _i18nMy.t(obj.name);
          }
          break ;
        }
        case 'td':break ;
        default :
          break ;
      }
    }
  },
  /**
   * @param
   * */
  simpleLanguageFrom(json,taskFormData) {
    if(json instanceof Array){
      for(var i=0;i<json.length;i++){
        if(json[i].columns !=null && json[i].columns instanceof Array ){
          this.simpleLanguageFrom(json[i].columns,taskFormData)
        }
        else{
          this.simpleLanguageFrom(json[i],taskFormData)
        }
      }
    }
    else{
      this.dealObj(json,taskFormData)
      if(json.rows !=null){
        this.simpleLanguageFrom(json.rows,taskFormData)
      }
      else if(json.list !=null){
        this.simpleLanguageFrom(json.list,taskFormData)
      }
      else if(json.tableColumns !=null){
        this.simpleLanguageFrom(json.tableColumns,taskFormData)
      }
    }
    return json
  }
}
