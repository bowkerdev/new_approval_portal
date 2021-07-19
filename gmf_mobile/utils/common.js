export default {
	/**
	* @param {string} value
	* @author max teng
	* @version 2021-03-18
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
	toCurrentPage:function(isRegisted){
		let currentPage = uni.getStorageSync("currentPage");
		if(isRegisted){
			if(currentPage!=null&&currentPage!=''){
				uni.removeStorageSync("currentPage");
				let currentPageList = getCurrentPages();
				var j=0;
				for(var i=currentPageList.length - 1;i>=0;i--,j++){
					if(("/"+currentPageList[i].route) == currentPage){
						break;
					}
				} 
				uni.navigateBack({
				    delta: j
				});
			}
			else{
				/* uni.switchTab({
					url:"/pages/center/center"
				}) */
			}
		}
		else{
			/* uni.navigateTo({
				url: '/pages/wxlogin/wxlogin'
			}); */
		}
	},	
	colorList:[{"title":"嫣红","name":"red","color":"#e54d42"},{"title":"桔橙","name":"orange","color":"#f37b1d"},{"title":"明黄","name":"yellow","color":"#fbbd08"},{"title":"橄榄","name":"olive","color":"#8dc63f"},{"title":"森绿","name":"green","color":"#39b54a"},{"title":"天青","name":"cyan","color":"#1cbbb4"},{"title":"海蓝","name":"blue","color":"#0081ff"},{"title":"姹紫","name":"purple","color":"#6739b6"},{"title":"木槿","name":"mauve","color":"#9c26b0"},{"title":"桃粉","name":"pink","color":"#e03997"},{"title":"棕褐","name":"brown","color":"#a5673f"},{"title":"玄灰","name":"grey","color":"#8799a3"},{"title":"草灰","name":"gray","color":"#aaaaaa"},{"title":"墨黑","name":"black","color":"#333333"},{"title":"雅白","name":"white","color":"#ffffff"}],
	colorList2:[{"title":"橄榄","name":"olive","color":"#8dc63f"},{"title":"明黄","name":"yellow","color":"#fbbd08"},{"title":"天青","name":"cyan","color":"#1cbbb4"},{"title":"桃粉","name":"pink","color":"#e03997"},{"title":"木槿","name":"mauve","color":"#9c26b0"}],
	objectToQueryString(obj) {
	  return Object.keys(obj).map(function (key) {
	    return "".concat(encodeURIComponent(key), "=").concat(encodeURIComponent(obj[key]));
	  }).join('&');
	},
	recover (target, source) {
	  if (target === undefined || target === null) { throw new TypeError('Cannot convert first argument to object') }
	  var to = Object(target)
	  if (source === undefined || source === null) { return to }
	  var keysArray = Object.keys(Object(target))
	  for (var nextIndex = 0, len = keysArray.length; nextIndex < len; nextIndex++) {
	    var nextKey = keysArray[nextIndex]
	    var desc = Object.getOwnPropertyDescriptor(target, nextKey)
	    if (desc !== undefined && desc.enumerable) {
	      if (to.hasOwnProperty(nextKey)) {
	        if (to[nextKey] instanceof Array) {
	          to[nextKey] = source[nextKey]
	        } else if (to[nextKey] instanceof Object) {
	          recover(to[nextKey], source[nextKey])
	        } else if (source[nextKey] !== undefined) {
	          to[nextKey] = source[nextKey]
	        } else if (typeof (to[nextKey]) === 'string') {
	          to[nextKey] = ''
	        } else {
	          to[nextKey] = undefined
	        }
	      }
	    }
	  }
	  return to
	}
}
