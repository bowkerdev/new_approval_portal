<template>
	<view class="content">
		<uni-table  stripe  emptyText="暂无更多数据" >
			<!-- 表头行 -->
			<uni-tr>
				<uni-th align="center">村委</uni-th>
				<uni-th align="center">任务数</uni-th>
				<uni-th align="center">完成数</uni-th>
				<uni-th align="center">完成率</uni-th>
			</uni-tr>
			<!-- 表格数据行 -->
			<uni-tr v-for="(item,index) in dataList" :key="index">
				<uni-td align="center">
					<view @click="gotoVillage(item.name)">
						{{item.name}}
					</view>
				</uni-td>
				<uni-td align="right">
				<view @click="gotoVillage(item.name)">
					{{item.total}}
				</view>
				</uni-td>
				<uni-td align="right">
				<view @click="gotoVillage(item.name)">
					{{item.finish}}
				</view>
				</uni-td>
				<uni-td align="right">
				<view @click="gotoVillage(item.name)">
					{{item.finishRate}}
				</view>
				</uni-td>
			</uni-tr>
		</uni-table>
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
					upLevelName : 'xiang',
					upLevelValue : '五通镇',
					groupBy : 'cunwei',
				},
			}
		},
		onLoad(e) {
			var _that=this;
			_that.inputParam.upLevelValue = (e.upLevelValue || '')
			_that.setTitle();
		},
		onShow(){
			var _that=this;
			this.setTitle();
			_that.getDataList()
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
					title:(_that.inputParam.upLevelValue || '乡镇')+'下级村委情况'
				})
			},
			gotoVillage(e){
				uni.navigateTo({
					url: '/pages/village/village?upLevelValue='+e
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
				_that.$http.get('dg/dgNongfang/tongji?'+_that.getParam(_that.inputParam))
				.then(function(res) {
					console.log(res);
					_that.initDataList(res.data.list);
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

</style>
