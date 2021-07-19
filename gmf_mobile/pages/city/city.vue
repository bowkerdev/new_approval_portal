<template>
	<!-- <view class="my-bg-blue">
	<view class="cu-bar my-bg-blue search fixed" >
		<view class="action">
			<text class="cuIcon-title text-blue"></text>村类型
		</view>
		<view class="action">
			<radio-group @change="getDataList" v-model.trim="inputParam.type">
				<label class="margin-left-sm">
					<radio class="white radio" value="" checked></radio>
					<text class="margin-left-sm">全部</text>
				</label>
				<label class="margin-left-sm">
					<radio class="blue radio" value="s"></radio>
					<text class="margin-left-sm">面上村</text>
				</label>
				<label class="margin-left-sm">
					<radio class="blue radio" value="s"></radio>
					<text class="margin-left-sm">重点村</text>
				</label>
			</radio-group>
		</view>
	</view>
	
	<scroll-view scroll-y class="indexes" 
	 :scroll-with-animation="true" :enable-back-to-top="true"> -->
		<view class="content">
			<uni-table stripe emptyText="暂无更多数据" >
				<!-- 表头行 -->
				<uni-tr>
					<uni-th align="center">乡镇</uni-th>
					<uni-th align="center">任务数</uni-th>
					<uni-th align="center">完成数</uni-th>
					<uni-th align="center">完成率</uni-th>
				</uni-tr>
				<!-- 表格数据行 -->
				<uni-tr v-for="(item,index) in dataList" :key="index">
					<uni-td v-if="item.name == '重点村' || item.name == '面上村' || item.name=='精品村' || item.name == '两线两路'" align="center" style="color:rgb(81, 180, 241)">
						<view @click="gotoTownship(item.name)">
							{{item.name}}
						</view>
					</uni-td>
					<uni-td v-else align="center">
						<view @click="gotoTownship(item.name)">
							{{item.name}}
						</view>
					</uni-td>
					<uni-td align="right">
					<view @click="gotoTownship(item.name)">
						{{item.total}}
					</view>
					</uni-td>
					<uni-td align="right">
					<view @click="gotoTownship(item.name)">
						{{item.finish}}
					</view>
					</uni-td>
					<uni-td align="right">
					<view @click="gotoTownship(item.name)">
						{{item.finishRate}}
					</view>
					</uni-td>
				</uni-tr>
			</uni-table>
		</view>
	<!-- </scroll-view>
	</view> -->
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
					type : '',
					upLevelName : 'xian',
					upLevelValue : '临桂区',
					groupBy : 'xiang',
				},
			}
		},
		onLoad(e) {
			var _that=this;
			uni.setNavigationBarTitle({
				title:_that.inputParam.upLevelValue+'下级乡镇情况'
			});
			uni.$on('update',_that.getDataList)
		},
		onShow(){
			this.getDataList();
		},
		methods: {
			gotoTownship(e){
				if (e=='重点村' || e=='面上村' || e=='精品村' || e=='两线两路') {
					uni.navigateTo({
						url: '/pages/village/village?upLevelValue='+e
					});
				} else {
					uni.navigateTo({
						url: '/pages/township/township?upLevelValue='+e
					});
				}
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
				//_that.$http.get('/dg/dgNongfang/tongji?'+_that.getParam(_that.inputParam))
				_that.$http.get('/database/datamodel/dataSet/getDataByName/XIANG/json?'+_that.getParam(_that.inputParam))
				.then(function(res) {
					console.log(res);
					_that.initDataList(res.data.result);
				}).catch(function(err) {
					// TODO
					console.log(err);
				});
			},
			initDataList(data){
				var _that=this;
				var list = []
				for(var i=0;i<data.length;i++){
					//debugger
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
/* page {
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
	.action {
		color: white;
	} */
</style>
