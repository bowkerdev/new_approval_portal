<template>
	<view class="my-bg-blue">
	<view class="cu-bar my-bg-blue search fixed" >
		<view class="search-form round">
			<text class="cuIcon-search"></text>
			<input type="text" placeholder="请输入农户姓名" confirm-type="search" v-model.trim="inputParam.hu" ></input>
		</view>
		<view class="action">
			<button class="cu-btn bg-gradual-green shadow-blur round" style="width: 60px;" @tap="getDataList">搜索</button>
		</view>
	</view>
	<scroll-view scroll-y class="indexes" 
	 :scroll-with-animation="true" :enable-back-to-top="true">
		<view class="content">
			<uni-table stripe  emptyText="暂无更多数据" >
				<!-- 表头行 -->
				<uni-tr>
					<uni-th align="center">农户</uni-th>
					<uni-th align="center">电话</uni-th>
					<uni-th align="center">改造进度</uni-th>
					<uni-th align="center">操作</uni-th>
				</uni-tr>
				<!-- 表格数据行 -->
				<uni-tr v-for="(item,index) in dataList" :key="index">
					<uni-td align="center" width="100">{{item.hu}}</uni-td>
					<uni-td align="center" width="100">{{item.phone}}</uni-td>
					<uni-td align="center">
						<div v-if="item.isFinish == '1'">
							<span style="color:#09BB07">已上报</span>
						</div>
						<div v-else>
							-
						</div>
					</uni-td>
					<uni-td align="center">
						<view @click="gotoOperating(item)" style="font-size: 14px;color: rgb(81, 180, 241)">
							填报
						</view>
					</uni-td>
				</uni-tr>
			</uni-table>
		</view>
    </scroll-view>
	</view>
</template>

<script>
	import uniTable from '@/components/uni-table/uni-table.vue';
	import uniTr from '@/components/uni-tr/uni-tr.vue';
	import uniTh from '@/components/uni-th/uni-th.vue';
	import uniTd from '@/components/uni-td/uni-td.vue';
	export default {
		components: {
		  uniTable,
		  uniTr,
		  uniTh,
		  uniTd
		},
		data() {
			return {
				dataList:[],
				inputParam:{
					hu: "",
					cun:'刘村',
					pageSize: 1000
				},
			}
		},
		onLoad(e) {
			var _that=this;
			_that.inputParam.cun = (e.cun || '')
			_that.setTitle();
		},
		onShow(){
			this.setTitle();
			this.getDataList();
		},
		onReady() {
		    // 页面加载完成
			var _that=this;
			_that.setTitle();
		},
		methods: {
			setTitle(){
				var _that=this;
				uni.setNavigationBarTitle({
					title:(_that.inputParam.cun || '自然村屯')+'下级农户情况'
				})
			},
			gotoOperating(e){
				uni.removeStorageSync("farmerHu")
				uni.setStorageSync("farmerHu",e)
				uni.navigateTo({
					url: '/pages/farmer/farmer'
				});
			},
			getParam(param){
				if(null == param){
					return '';
				}
				var paramArr = []
				for(var key in param){
					paramArr.push(key+"="+param[key]);
				}
				return paramArr.join("&");
			},
			getDataList(){
				var _that=this;
				_that.$http.get('/dg/dgNongfang/list?'+_that.getParam(_that.inputParam))
				.then(function(res) {
					console.log(res);
					_that.initDataList(res.data.page.list);
				}).catch(function(err) {
					// TODO
					console.log(err);
				});
			},
			initDataList(data){
				var _that=this;
				var list = []
				for(var i=0;i<data.length;i++){
					var tmp = data[i];
					if(!tmp.finish) tmp.finish = 0;
					tmp.finishRate = parseFloat((tmp.finish * 100) / tmp.total).toFixed(2) + ' %' 
					list.push(tmp);
				}
				_that.dataList = list;
			},
		}
	}
</script>

<style>
.uni-table-th{
	font-weight: bold;
}

	page {
		padding-top: 100upx;
		background-color: #192239 !important; 
	}

	.indexes {
		position: relative;
	}

	.indexBar {
		position: fixed;
		right: 0px;
		bottom: 0px;
		padding: 20upx 20upx 20upx 60upx;
		display: flex;
		align-items: center;
	}

	.indexBar .indexBar-box {
		width: 40upx;
		height: auto;
		background: #fff;
		display: flex;
		flex-direction: column;
		box-shadow: 0 0 20upx rgba(0, 0, 0, 0.1);
		border-radius: 10upx;
	}

	.indexBar-item {
		flex: 1;
		width: 40upx;
		height: 40upx;
		display: flex;
		align-items: center;
		justify-content: center;
		font-size: 24upx;
		color: #888;
	}

	movable-view.indexBar-item {
		width: 40upx;
		height: 40upx;
		z-index: 9;
		position: relative;
	}

	movable-view.indexBar-item::before {
		content: "";
		display: block;
		position: absolute;
		left: 0;
		top: 10upx;
		height: 20upx;
		width: 4upx;
		background-color: #f37b1d;
	}
	.my-bg-blue {
		background-color: #192239;
	}
	.indexToast {
		position: fixed;
		top: 0;
		right: 80upx;
		bottom: 0;
		background: rgba(0, 0, 0, 0.5);
		width: 100upx;
		height: 100upx;
		border-radius: 10upx;
		margin: auto;
		color: #fff;
		line-height: 100upx;
		text-align: center;
		font-size: 48upx;
	}
</style>
