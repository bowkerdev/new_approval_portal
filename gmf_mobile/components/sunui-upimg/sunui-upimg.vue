<template>
	<view class="sunui-uploader-bd">
		<view class="sunui-uploader-files">
			<block v-for="(item, index) in upload_before_list" :key="index">
				<view class="sunui-uploader-file" :class="[item.upload_percent < 100 ? 'sunui-uploader-file-status' : '']" @click="previewImage(index)">
					<!-- step1.这里修改服务器返回字段 ！！！ -->
					<image class="sunui-uploader-img" :style="upload_img_wh" :src="item.path" mode="aspectFill" />
					<view class="sunui-img-removeicon right" @click.stop="removeImage(index)" v-show="upimg_move">×</view>
					<view class="sunui-loader-filecontent" v-if="item.upload_percent < 100">{{ item.upload_percent }}%</view>
				</view>
			</block>
			<view v-show="upload_len < upload_count" hover-class="sunui-uploader-hover" class="sunui-uploader-inputbox" @click="chooseImage" :style="upload_img_wh">
				<view><image src="../../static/img/picIcon.png" class="picIcon"></view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			upload_len: 0,
			upload_cache: [],
			upload_cache_list: [],
			upload_before_list: [],
			header :{}
		};
	},
	name: 'sunui-upimg',
	props: {
		// 服务器url
		url: {
			type: String,
			default: 'https://a3.dns06.net.cn/app/index.php?i=2&c=entry&a=wxapp&do=Upload_qiniu_b&m=jzwx_a'
		},
		// 上传样式宽高
		upload_img_wh: {
			type: String,
			default: 'width:162rpx;height:162rpx;'
		},
		// 上传数量
		upload_count: {
			type: [Number, String],
			default: 9
		},
		// 是否自动上传? 可以先用变量为false然后再改为true即为手动上传
		upload_auto: {
			type: Boolean,
			default: true
		},
		// 是否显示删除
		upimg_move: {
			type: Boolean,
			default: true
		},
		// 服务器预览图片
		upimg_preview: {
			type: Array,
			default: () => {
				return [];
			}
		},
		// 服务器返回预览(看服务器卡顿情况设定)
		upimg_delaytime: {
			type: [Number, String],
			default: 1000
		}
	},
	async created() {
		let _self = this;
		setTimeout(() => {
			_self.upload_before_list = _self.upload_before_list.concat(_self.upimg_preview);
			_self.upload_len = _self.upload_before_list.length;
			_self.upimg_preview.map(item => {
				// step2.这里修改服务器返回字段 ！！！
				_self.upload_cache_list.push(item.path);
			});
			_self.emit();
		}, _self.upimg_delaytime);
	},
	methods: {
		upImage(paths,header) {
			let _self = this;
			const promises = paths.map(function(path) {
				return promisify(upload)({
					url: _self.url,
					path: path,
					name: 'file',
					header : header,
					_self: _self
				});
			});
			Promise.all(promises)
				.then(function(data) {
					_self.upload_cache_list.push(...data);
					_self.emit();
				})
				.catch(function(res) {
					_self.removeImage(0);
					uni.showToast({
						title: `上传失败，请重试`,
						icon: 'none'
					});
				});
		},
		chooseImage() {
			let _self = this;
			uni.chooseImage({
				count: _self.upload_count - _self.upload_before_list.length,
				sizeType: ['compressed'], // , 'original'
				sourceType: ['album', 'camera'],
				success: function(res) {
					if(res.tempFiles ==null ){
						var tmpList=[]
						for(var i=0;res.filePaths.length>i ;i++){
							tmpList.push({upload_percent:0,path:res.filePaths[i]}) 
						}
						res.tempFiles= tmpList;
					}
					for (let i = 0, len = res.tempFiles.length; i < len; i++) {
						res.tempFiles[i]['upload_percent'] = 0;
						_self.upload_before_list.push(res.tempFiles[i]);
					}
					_self.upload_cache = res.tempFilePaths;
					_self.upload(_self.upload_auto);
				},
				fail: function(err) {
					console.log(err);
				}
			});
		},
		async upload(upload_auto) {
			let _self = this;
			upload_auto ? await _self.upImage(_self.upload_cache,_self.header) : console.warn(`传输参数:this.$refs.xx.upload(true)才可上传,默认false`);
		},
		previewImage(idx) {
			let _self = this;
			let preview = [];
			for (let i = 0, len = _self.upload_before_list.length; i < len; i++) {
				// step3.这里修改服务器返回字段 ！！！
				preview.push(_self.upload_before_list[i].path);
			}
			uni.previewImage({
				current: idx,
				urls: preview
			});
		},
		setDefaut(url) {
			this.upload_len=1;
			this.upload_before_list=[{upload_percent:100,path:url}];
		},
		setHeader(header) {
			this.header=header;
		},
		removeImage(idx) {
			let _self = this;
			_self.upload_before_list.splice(idx, 1);
			_self.upload_cache_list.splice(idx, 1);
			_self.upload_len = _self.upload_before_list.length;
			_self.emit('removeImage');
		},
		emit(opt) {
			let _self = this;
			if(opt=='removeImage'||(_self.upload_cache_list.length>0&&_self.upload_cache_list[0]!=null)){
				_self.$emit('change', _self.upload_cache_list);
			}
			
		}
	}
};

const promisify = api => {
	return function(options, ...params) {
		return new Promise(function(resolve, reject) {
			api(
				Object.assign({}, options, {
					success: resolve,
					fail: reject
				}),
				...params
			);
		});
	};
};

const upload = function(options) {
	let url = options.url,
		_self = options._self,
		path = options.path,
		name = options.name,
		// data = options.data,
		header = options.header ||{},
		success = options.success,
		progress = options.progress,
		fail = options.fail;
	uni.showLoading({title: `正在上传...`});
	const uploadTask =uni.uploadFile ({
		url: url,
		filePath: path,
		name: name,
		header : header,
		fileType: "image",
		success: function(res) {
			uni.hideLoading();
			for (let i = 0, len = _self.upload_before_list.length; i < len; i++) {
				_self.upload_before_list[i]['upload_percent'] = 100;
			}
			_self.upload_before_list = _self.upload_before_list.slice()
			_self.upload_len = _self.upload_before_list.length;
			try {
				//Tip : 切记->主要修改这里图片的返回值为真实返回路径!!! 详情见示例
				console.log(JSON.stringify(res));
				if(typeof res.data === 'string'){
					var tmp=JSON.parse(res.data)
				}
				else{
					var tmp=res.data
				}
				if(tmp.info != null){
					var data = tmp.info;
				}
				else{
					var data = tmp
				}
			} catch (e) {
				console.log(e.message);
				throw (e, data);
			}
			// 根据自己的返回数据做相应判断,服务器返回200即代表成功请求
			console.log("200");
			if (res.statusCode == 200) {
				if (success) {
					success(data);
				}
			} else {
				if (fail) {
					fail(data);
				}
			}
		},
		fail: function(res) {
			uni.hideLoading();
			if (fail) {
				fail(res);
			}
		}
	});
	uploadTask.onProgressUpdate(async function(res) {
		try{
			for (let i = 0, len = _self.upload_before_list.length; i < len; i++) {
				console.log("upload_percent 1 : " + _self.upload_before_list[i]['upload_percent'])
				if(_self.upload_before_list[i]['upload_percent'] <100){
					_self.upload_before_list[i]['upload_percent'] = await res.progress;
				}
				console.log("upload_percent 2 : " + _self.upload_before_list[i]['upload_percent'])
			}
			_self.upload_before_list = _self.upload_before_list.slice()
			_self.upload_len = _self.upload_before_list.length;
		}catch(e){
			console.log(e)
		}
	});
};
</script>

<style lang="scss">
@charset "UTF-8";
.picIcon{
	width: 40px;
	height: 40px;
}
.sunui-uploader-img {
	display: block;
}

.sunui-uploader-input {
	position: absolute;
	z-index: 1;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	opacity: 0;
}

.sunui-uploader-inputbox {
	position: relative;
	margin-bottom: 16rpx;
	box-sizing: border-box;
	background-color: #ededed;
	display: flex;
	flex-wrap: wrap;
	align-items: center;
	justify-content: center;
}

.sunui-img-removeicon {
	position: absolute;
	color: #fff;
	width: 40upx;
	height: 40upx;
	line-height: 40upx;
	z-index: 2;
	text-align: center;
	background-color: #e54d42;

	&.right {
		top: 0;
		right: 0;
	}
}

.sunui-uploader-file {
	position: relative;
	margin-right: 16rpx;
	margin-bottom: 16rpx;
}

.sunui-uploader-file-status:before {
	content: ' ';
	position: absolute;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	background-color: rgba(0, 0, 0, 0.5);
}

.sunui-loader-filecontent {
	position: absolute;
	top: 50%;
	left: 50%;
	transform: translate(-50%, -50%);
	color: #fff;
	z-index: 9;
}

.sunui-uploader-bd {
	padding: 26rpx;
	margin: 0;
}

.sunui-uploader-files {
	display: flex;
	flex-wrap: wrap;
}

.sunui-uploader-file:nth-child(4n + 0) {
	margin-right: 0;
}

.sunui-uploader-inputbox > view {
	text-align: center;
}

.sunui-uploader-file-status:after {
	content: ' ';
	position: absolute;
	top: 0;
	right: 0;
	bottom: 0;
	left: 0;
	background-color: rgba(0, 0, 0, 0.5);
}

.sunui-uploader-hover {
	box-shadow: 0 0 0 #e5e5e5;
	background: #e5e5e5;
}
</style>
