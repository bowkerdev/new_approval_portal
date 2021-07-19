export default {
	sysError:function(code,position){
		uni.showToast({
			icon: 'none',
			title: '系统错误,'+(code||''),
			position:position==null?'bottom':position,
			duration: 2000,
		});
	},
	toast:function(title,position){
		uni.showToast({
			icon: 'none',
			title: title,
			position:position==null?'bottom':position,
			duration: 2000,
		});
	},
	toastDefault:function(title){
		uni.showToast({
			icon: 'none',
			title: title,
			duration: 2000,
		});
	},
	loading:function(title){
		uni.showLoading({
			title:title
		})
	},
	hideLoading:function(title){
		uni.hideLoading()
	}
}
