 /* eslint-disable */
// @ts-nocheck
const STORAGE_USER_KEY = 'STORAGE_USER_KEY'
// const STORAGE_CARTLIST_KEY = 'STORAGE_CARTLIST_KEY'
// const STORAGE_QUERYMYLIST_KEY = 'STORAGE_QUERYMYLIST_KEY'
// import { Toast } from 'mint-ui';
import axios from 'axios'
const prefix = 'http://testapi.gu-dao.cn';
// import authService from '@/api/authService.js'
//定义一些常量
var x_PI = 3.14159265358979324 * 3000.0 / 180.0;
var PI = 3.1415926535897932384626;
var a = 6378245.0;
var ee = 0.00669342162296594323;

export default {
  traverseTree: function(tree,callBack){ //遍历树  获取id数组
    for(var i in tree){
      callBack(tree[i])
      if(tree[i].children){
        this.traverseTree(tree[i].children,callBack);
      }
    }
  },
  distinct : function (list){
  	var arr = list,i,obj = {},result = [],len = arr.length;
  	for(i = 0; i< arr.length; i++){
  	   if(!obj[arr[i]]){ //如果能查找到，证明数组元素重复了
  		   obj[arr[i]] = 1;
  		   result.push(arr[i]);
  	   }
  	}
  	return result;
  },
  find:function (list,comparisonFunction){
    var arr=list;
    for(var i=0;arr.length>i;i++){
      if(comparisonFunction(arr[i]))return arr[i];
    }
  },
  findArray:function (list,comparisonFunction){
    var r=[];
    var arr=list;
    for(var i=0;arr.length>i;i++){
      if(comparisonFunction(arr[i]))r.push(arr[i]);
    }
    return r;
  },
  /**
   * 判断时间数组是否有重叠时间
   * times格式要求：[{start:'15:00',end:'15:59'}]
   * @param times 时间数组
   * @param startField 开始时间属性
   * @param endField 结束时间属性
   * @param type 数据类型：time | string
   * @return string
   * @author max teng
   * @version 2021-03-15
   * */
  timesIsOverlapping(times,startField,endField,type){
    if(this.isEmpty(startField)){
      return 'Error:startField is empty'
    }
    if(this.isEmpty(endField)){
      return 'Error:endField is empty'
    }
    if(this.isEmpty(type)){
      return 'Error:type is empty'
    }
    if(times instanceof Array){
      if(times.length > 1){
        var timeArr = []
        for(var i=0;i<times.length;i++){

          var tmpTime = {start:'',end:''}
          if('string' === type){
            tmpTime.start = times[i][startField]
            tmpTime.end = times[i][endField]
          }
          if('time' === type){
            var startTime = new Date(times[i][startField])
            var endTime = new Date(times[i][endField])
            var sh = startTime.getHours()
            var sm = startTime.getMinutes()
            var eh = endTime.getHours()
            var em = endTime.getMinutes()
            tmpTime.start = (sh < 10?'0'+sh:sh)+":"+(sm < 10?'0'+sm:sm)
            tmpTime.end = (eh < 10?'0'+eh:eh)+":"+(em < 10?'0'+em:em)
          }

          var tmpStart = {time:0,type:''}
          tmpStart.id = new Date('2021-03-15 '+tmpTime.start+':00').getTime()
          tmpStart.type = 0
          tmpStart.start = new Date('2021-03-15 '+tmpTime.start+':00').getTime()
          tmpStart.end = new Date('2021-03-15 '+tmpTime.end+':00').getTime()
          tmpStart.startString = tmpTime.start
          tmpStart.endString = tmpTime.end
          timeArr.push(tmpStart)

          var tmpEnd = {time:0,type:''}
          tmpEnd.id = new Date('2021-03-15 '+tmpTime.end+':00').getTime()
          tmpEnd.type = 1
          tmpEnd.start = new Date('2021-03-15 '+tmpTime.start+':00').getTime()
          tmpEnd.end = new Date('2021-03-15 '+tmpTime.end+':00').getTime()
          tmpEnd.startString = tmpTime.start
          tmpEnd.endString = tmpTime.end
          timeArr.push(tmpEnd)
        }
        timeArr = timeArr.sort(function (a,b){return a.id - b.id})
        for(var i=0;i<= (timeArr.length / 2);i++){
          var start = timeArr[i*2]
          var end = timeArr[i*2 + 1]
          if(!(1 === (start.type + end.type) && start.end === end.end && start.start === end.start) ){
            return 'Error:【'+start.startString+'-'+start.endString+'】【'+end.startString+'-'+end.endString+'】 time overlap!';
          }
        }
      }
    }
    return '';
  },
  /**
   * 获取月份所有的星期天
   * @param date
   * @param m
   * @param dayNum
   * @returns {[]}
   * @author max teng
   * @version 2021-03-15
   */
  getMonthSunday(date,m,dayNum){
    var monthResult = []
    for (var d = 1; d <= dayNum; d++) {
      date.setMonth(m - 1, d)
      // 返回表示星期的某一天的数字  0 --> 周日
      let day = date.getDay()
      if (0 === day) {
        let month = date.getMonth() + 1
        let day = date.getDate()
        if (month < 10) {
          month = '0' + month
        }
        if (day < 10) {
          day = '0' + day
        }
        var tmp = {}
        tmp.id = date.getFullYear() + '-' + month + '-' + day
        tmp.date = new Date(tmp.id)
        monthResult.push(tmp)
      }
    }
    return monthResult
  },
  /**
   * 获取月份所有天数
   * @param year
   * @param m
   * @returns {number}
   * @author max teng
   * @version 2021-03-15
   */
  getMonthDayNum(year,m){
    var dayNum = 0
    switch (m) {
      case 1:
      case 3:
      case 5:
      case 7:
      case 8:
      case 10:
      case 12:
        dayNum = 31
        break
      case 4:
      case 6:
      case 9:
      case 11:
        dayNum = 30
        break
      case 2:
        // eslint-disable-next-line no-mixed-operators
        if (year % 4 === 0 && year % 100 !== 0 || year % 400 === 0) {
          dayNum = 29
        } else {
          dayNum = 28
        }
        break
    }
    return dayNum
  },
  /**
   * 获取一整年或者某个月的的周日所对应的日期
   * @param t 日期,如2021-03-16
   * @param type 类型 year|month
   * @return [{}]
   * @author max teng
   * @version 2021-03-02
   */
  getAllSunday(t,type) {
    let time = t.replace(/-/g, ':')
    time = time.split(':')
    let date = new Date(time[0], (time[1] - 1), time[2])
    let year = date.getFullYear()
    let month = date.getMonth() + 1
    // eslint-disable-next-line one-var
    let d_index, dayNum = ''
    let result = []
    var monthResult = []
    if('year' === type){
      for (var m_index = 1; m_index <= 12; m_index++) {
        dayNum = this.getMonthDayNum(year,m_index)
        monthResult = this.getMonthSunday(date,m_index,dayNum)
        for(d_index=0;d_index<monthResult.length;d_index++){
          result.push(monthResult[d_index])
        }
      }
    }
    if('month' === type){
      dayNum = this.getMonthDayNum(year,month)
      monthResult = this.getMonthSunday(date,month,dayNum)
      for(d_index=0;d_index<monthResult.length;d_index++){
        result.push(monthResult[d_index])
      }
    }
    console.log(result)
    return result
  },
  /**
   * 对级联数据进行分组
   * @param data 要分组数据
   * @param type 属性，要以哪个属性进行分组
   * @return [{}]
   * @author max teng
   * @version 2020-03-17
   */
  initOriginalCascadeData(data){
    let _that = this
    var factoryObj = _that.getCascadeGroupData(data, 'factory')
    var list = []
    for(var factoryKey in factoryObj){
      var factoryMap = {type:'factory',value:'',label:'',children:[]}
      factoryMap.value = factoryKey
      factoryMap.label = factoryKey
      var buildingObj = _that.getCascadeGroupData(factoryObj[factoryKey], 'building')
      for(var buildingKey in buildingObj){
        var buildingMap = {type:'building',value:'',label:'',children:[]}
        buildingMap.value = buildingKey
        buildingMap.label = buildingKey
        var floorObj = _that.getCascadeGroupData(buildingObj[buildingKey], 'floor')
        for(var floorKey in floorObj){
          var floorMap = {type:'floor',value:'',label:'',children:[]}
          floorMap.value = floorKey
          floorMap.label = floorKey
          for(var i=0;i<floorObj[floorKey].length;i++){
            var lineMap = {type:'line',value:'',label:''}
            lineMap.value = floorObj[floorKey][i].line_id
            lineMap.label = floorObj[floorKey][i].line_no
            floorMap.children.push(lineMap)
          }
          buildingMap.children.push(floorMap)
        }
        factoryMap.children.push(buildingMap)
      }
      list.push(factoryMap)
    }
    console.log(list)
    return list
  },
  /**
   * 获取子树数据
   * @param data
   * @param value
   * @return {[]}
   */
  getCascadeChildrenData(data,value){
    let _that = this
    var list=[]
    if(!_that.isEmpty(value)){
      for(var i=0;i< data.length;i++){
        if(value === data[i].value){
          list = data[i].children
          break
        }
      }
    }
    return list
  },
  /**
   * 分组
   * @param data
   * @param type
   * @return {{}}
   */
  getCascadeGroupData (data, type) {
    let _that = this
    return _that.groupBy(data, function(e) { return [e[type]] })
  },
  unique:function(list,compare) {
    if(list==null||list.length==0){
      return []
    }
    list.sort()
    var temp=[list[0]]
    for(var i = 1; i < list.length; i++){
      if(compare!=null){
        var isAdd = true
        for(var j=0;j< temp.length;j++){
          if(!compare(list[i],temp[j])){
            isAdd = false
            break
          }
        }
        if(isAdd){
          temp.push(list[i])
        }
      }
      else{
        var isAdd = true
        for(var j=0;j< temp.length;j++){
          if(list[i] === temp[j]){
            isAdd = false
            break
          }
        }
        if(isAdd){
          temp.push(list[i]);
        }
      }
    }
    return temp;
  },
  max:function(list, f) {
    var max ;var rd;
    list.forEach(function (o){
      var data=f(o);
      if(max==null){
        max =data;
        rd=o;
      }
      else{
        if(max<data){
          max=data;
          rd=o;
        }
      }
    });
    return rd;
  },

  min:function(list, f) {
    var min ;var rd;
    list.forEach(function (o){
      var data=f(o);
      if(min==null){
        min =data;
        rd=o;
      }
      else{
        if(min>data){
          min=data;
          rd=o;
        }
      }
    });
    return rd;
  },
  groupBy:function(list,f){
    const groups = {};
    list.forEach(function (o){
      const group = f(o);
      groups[group] = groups[group] || [];
      groups[group].push(o);
    });
    return groups;
  },
  // 获取
  getLocal(key = STORAGE_USER_KEY) {
    // console.log('get local operation')
    return JSON.parse(window.localStorage.getItem(key))
  },
  // 设置用
  /**
   * @param {any} res
   */
  setLocal(res, key = STORAGE_USER_KEY, isSaveOldData = false) {
    //第三个参数是true的话,会增加数据而不是重新设置,res必须是数组
    if (isSaveOldData) {
      if (this.getLocal(key)) {
        let oldData = this.getLocal(key);
        return window.localStorage.setItem(key, JSON.stringify(oldData.concat(res)))
      }
    }
    return window.localStorage.setItem(key, JSON.stringify(res))
  },
  /**
   * @param {any[]} obj
   */
  deepClone(obj) {
    var o;
    if (typeof obj == "object") {
      if (obj === null) {
        o = null;
      } else {
        if (obj instanceof Array) {
          o = [];
          for (var i = 0, len = obj.length; i < len; i++) {
            o.push(this.deepClone(obj[i]));
          }
        } else {
          o = {};
          for (var j in obj) {
            o[j] = this.deepClone(obj[j]);
          }
        }
      }
    } else {
      o = obj;
    }
    return o;
  },
  getGuid() {
    function S4() {
      return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
    }
    return (S4() + S4() + "-" + S4() + "-" + S4() + "-" + S4() + "-" + S4() + S4() + S4());
  },
  /**
   * @param {string} value
   */
  isEmpty(value) {
    let result = false;
    if (value == null || value == undefined) {
      result = true;
    }
    if (typeof value == 'string' && (value.replace(/\s+/g, "") == "" || value == "")) {
      result = true;
    }
    if (typeof value == "object" && value instanceof Array && value.length === 0) {
      result = true;
    }
    return result;
  },
  /**
   * @param {any} time
   */
  formatTime(time) {
    //   格式：yyyy-MM-dd hh:mm:ss
    let date = new Date(Number(time));
    let Y = date.getFullYear() + '-';
    let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    let D = date.getDate() < 10 ? '0' + date.getDate() + ' ' : date.getDate() + ' ';
    let h = date.getHours() < 10 ? '0' + date.getHours() + ':' : date.getHours() + ':';
    let m = date.getMinutes() < 10 ? '0' + date.getMinutes() + ':' : date.getMinutes() + ':';
    let s = date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds();
    return Y + M + D + h + m + s;
  },
  /**
   * @param {any} time
   */
  formatDate(time) {
    //   格式：yyyy-MM-dd
    let date = new Date(Number(time));
    let Y = date.getFullYear() + '-';
    let M = (date.getMonth() + 1 < 10 ? '0' + (date.getMonth() + 1) : date.getMonth() + 1) + '-';
    let D = date.getDate() < 10 ? '0' + date.getDate() + ' ' : date.getDate() + ' ';
    return Y + M + D;
  },

  /**
    * 字符串转数组
    * @param {*} value
    * @returns
    */
  stringToArray(value) {
    if (this.isEmpty(value)) return []
    if (typeof value !== 'string') return []
    if (value.indexOf(",") < 0) return [value]
    let array = value.split(",");
    return array
  },
  /**
   * 数组转字符串
   * @param {*} value
   * @returns
   */
  arrayToString(value) {
    if (typeof value == "object" && value instanceof Array) {
      if (value.length == 0) return ""
      let str = '';
      value.forEach(element => {
        str += element + ',';
      })
      return str.substr(0, str.length - 1);
    } else {
      return ""
    }
  },


  /**
   * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
   * 即谷歌、高德 转 百度
   * @param {number} lng
   * @param {number} lat
   * @returns {*[]}
   */
  gcj02tobd09(lng, lat) {
    var z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * x_PI);
    var theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * x_PI);
    var bd_lng = z * Math.cos(theta) + 0.0065;
    var bd_lat = z * Math.sin(theta) + 0.006;
    return [bd_lng, bd_lat]
  },
  /**
   * @param {string} value
   */
  isPhone(value) {
    var myreg = /^[1][3,4,5,7,8][0-9]{9}$/;
    if (!myreg.test(value)) {
      return false;
    } else {
      return true;
    }
  },
  /**
   * @param {string} value
   */
  isIDCardNo(value) {
    var myreg = /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$|^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/;
    if (!myreg.test(value)) {
      return false;
    } else {
      return true;
    }
  },
  getUrlParam(paramName,urlStr){
    var url = urlStr||window.location.href ;             //获取当前url
    var tmp=url.split('?');                //获取?之后的参数字符串
    if(tmp.length>1){
      var cs =  tmp[1]; 
    }
    else{
       return null; 
    }
    var cs_arr = cs.split('&');                    //参数字符串分割为数组
    var cs={};
    for(var i=0;i<cs_arr.length;i++){         //遍历数组，拿到json对象
      cs[cs_arr[i].split('=')[0]] = cs_arr[i].split('=')[1]
    }
    return cs[paramName];     
  },

  filterApplyOption (searchForm,optionList){
    try{
      //清空选项后缀列表
      var filterModeList = ['_eq','_ne','_like','_not_like','_in','_not_in','_ge','_le','_null','_not_null']
      for(var prop in optionList){
        var param = searchForm;
        var option = optionList[prop].option;
        //递归遍历最里层prop
        var ary = prop.split('.');
        for(var index = 0; index < ary.length - 1 ;index++){
          param = param[ary[index]];
        }
        var originalProp = ary[ary.length - 1];
        //清空前选项
        for(var index = 0; index < filterModeList.length; index++){
          delete param[originalProp+filterModeList[index]];
        }
        //设置选项
        if (option.filterMode == '_between') {
          if (option.inputValue) {
            param[ary[ary.length - 1]+'_ge'] = option.inputValue;
          }
          if(option.subInputValue){
            param[ary[ary.length - 1]+'_le'] = option.subInputValue;
          }
        } else if (['_in','_not_in'].indexOf(option.filterMode)>-1) {
          param[ary[ary.length - 1]+option.filterMode] = option.optionList.map(function(arrayElement){
            return arrayElement.inputValue;
          }).join(',');
        } else {
          param[ary[ary.length - 1]+option.filterMode] = option.inputValue;
        }
      }
    }catch(e){

    }
    return searchForm;
  }
}
