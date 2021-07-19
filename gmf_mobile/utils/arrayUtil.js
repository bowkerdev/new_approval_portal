export default {
	groupBy:function(list,f){
		const groups = {};
		list.forEach(function (o){
			const group = f(o);
		    groups[group] = groups[group] || [];
		    groups[group].push(o);
		});
		return groups;
	},
	findArray:function (list,comparisonFunction){
    		var r=[];
    		var arr=list;
       		for(var i=0;arr.length>i;i++){
       			if(comparisonFunction(arr[i]))r.push(arr[i]);
       		}
       		return r;
    },
    distinct:function (list){
    	var arr = list,i,obj = {},result = [],len = arr.length;
    	for(i = 0; i< arr.length; i++){
    	   if(!obj[arr[i]]){ //如果能查找到，证明数组元素重复了
    	       obj[arr[i]] = 1;
    	       result.push(arr[i]);
    	   }
    	}
    	return result;
    },
	removeEmpty:function (list){
		var okList=[];
		for(var i=0;list.length>i;i++){
			if(list[i]!=null&&list[i]!=''){
				okList.push(list[i])
			}
		}
		return okList;
	}
}
