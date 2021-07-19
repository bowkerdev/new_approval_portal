export default {
	isEmpty:function(str){
		return str==null||str.trim()==""
	},
	getDateTime:function(time,type){
		var date = new Date();
		if(null != time && '' != time){
			date = new Date(time)
		}
		var year = date.getFullYear();
		var month = date.getMonth() + 1;
		var day = date.getDate();
		var hour = date.getHours() < 10 ? "0" + date.getHours() : date.getHours();
		var minute = date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes();
		var second = date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
		
		month >= 1 && month <= 9 ? (month = "0" + month) : "";
		day >= 0 && day <= 9 ? (day = "0" + day) : "";
		var timer = '';
		if(null != type && '' != type){
			if("date" == type){
				timer = year + '-' + month + '-' + day
			}
			if("time" == type){
				timer = hour + ':' + minute + ':' + second
			}
			if("datetime" == type){
				timer = year + '-' + month + '-' + day + ' ' + hour + ':' + minute + ':' + second
			}
			
		}
		return timer;
	},
	//2020-06-16 max teng 该时间（date）距离今天的天数
	formatTimeDistance : function(date){
		if(date != null && date != ""){
			var todayTime = new Date().getTime();
			var dateTime = new Date(date).getTime();
			if("NaN" == dateTime.toString()){
				return "";
			}
			var hour=parseInt((todayTime - dateTime) / (3600*1000*1));
			var day=parseInt((todayTime - dateTime) / (3600*1000*24));
			if (hour < 2) {
				return "刚刚";
			} else if (hour < 24) {
				return hour +" 小时前";
			} else{
				return day +" 天前";
			}			
		}else{
			return "";
		}

	}
}
