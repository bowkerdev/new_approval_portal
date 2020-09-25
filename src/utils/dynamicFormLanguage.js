import _i18nMy from '@/utils/i18n2'
export default {
  isValueNull(value) {
    if (value == null) {
      return true
    }
    if (value == undefined) {
      return true
    }
    return false
  },
  isNotNullArray(arr) {
    if (!this.isValueNull(arr)) {
      if (Array.isArray(arr)) {
        return true
      }
    }
    return false
  },
  getLanguage(value) {
    return _i18nMy.t(value)
  },
  /**
   * @param columns 格式要求：[{list:[{name:''}]}]
   * */
  simpleLanguageGridFrom(columns) {
    if (this.isNotNullArray(columns)) {
      for (let j = 0; j < columns.length; j++) {
        var columnList = columns[j].list
        if (this.isNotNullArray(columnList)) {
          for (let k = 0; k < columnList.length; k++) {
            var obj = columnList[k]
            if (!this.isValueNull(obj.name)) {
              // eslint-disable-next-line no-undef
              obj.name = this.getLanguage(obj.name)
            }
          }
        }
      }
    }
  },
  /**
   * @param tableColumns 格式要求：[{name:''}]
   * */
  simpleLanguageTableFrom(tableColumns) {
    if (this.isNotNullArray(tableColumns)) {
      for (let j = 0; j < tableColumns.length; j++) {
        var obj = tableColumns[j]
        if (!this.isValueNull(obj.name)) {
          // eslint-disable-next-line no-undef
          obj.name = this.getLanguage(obj.name)
        }
      }
    }
  },
  /**
   * @param options 格式要求：{options:[{value:''}]}
   * */
  simpleLanguageRadioFrom(options) {
    if (!this.isValueNull(options)) {
      var optionList = options.options
      if (this.isNotNullArray(optionList)) {
        for (let i = 0; i < optionList.length; i++) {
          var obj = optionList[i]
          if (!this.isValueNull(obj.value)) {
            // eslint-disable-next-line no-undef
            obj.value = this.getLanguage(obj.value)
          }
        }
      }
    }
  },
  /**
   * @param obj 格式要求：{name:''}
   * */
  simpleLanguageNameFrom(obj) {
    if (!this.isValueNull(obj.name)) {
      // eslint-disable-next-line no-undef
      obj.name = this.getLanguage(obj.name)
    }
  },
  /**
   * @param json 格式要求：{list:[{type:''}]}
   * */
  simpleLanguageFrom(json) {
    if (Object.prototype.toString.call(json) === '[object Object]') {
      var list = json.list
      if (this.isNotNullArray(list)) {
        for (let i = 0; i < list.length; i++) {
          var type = list[i].type
          switch (type) {
            case 'grid' :this.simpleLanguageGridFrom(list[i].columns); break
            case 'table' :this.simpleLanguageTableFrom(list[i].tableColumns); break
            case 'radio' :this.simpleLanguageRadioFrom(list[i].options); break
            case 'divider' :
            case 'fileupload':
            case 'textarea' : this.simpleLanguageNameFrom(list[i]); break
            default: break
          }
        }
      }
    }
    return json
  }
}
