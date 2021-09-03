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
  dealGeneralCtl(obj){
    if(obj.name!=null){
      obj.name=_i18nMy.t(obj.name)
    }
  },
  dealObj(obj){
    if(obj.options!=null&&!obj.options.hidden){
      switch(obj.type){
        case 'html':obj.options.defaultValue = this.dealHtml(obj.options.defaultValue); break ;
        case 'radio':
        case 'date':
        case 'textarea':
        case 'input':this.dealGeneralCtl(obj); break ;
        case 'td':break ;
        default :
          debugger;
          break ;
      }
    }
  },
  /**
   * @param
   * */
  simpleLanguageFrom(json) {
    if(json instanceof Array){
      for(var i=0;i<json.length;i++){
        if(json[i].columns !=null && json[i].columns instanceof Array ){
          this.simpleLanguageFrom(json[i].columns)
        }
        else{
          this.simpleLanguageFrom(json[i])
        }
      }
    }
    else{
      this.dealObj(json)
      if(json.rows !=null){
        this.simpleLanguageFrom(json.rows)
      }
      else if(json.list !=null){
        this.simpleLanguageFrom(json.list)
      }
      else if(json.tableColumns !=null){
        this.simpleLanguageFrom(json.tableColumns)
      }
    }
    return json
  }
}
