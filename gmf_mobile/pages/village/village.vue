<template>
	<view class="content">
		<uni-table  stripe  emptyText="暂无更多数据" >
			<!-- 表头行 -->
			<uni-tr>
				<uni-th align="center">自然村屯</uni-th>
				<uni-th v-if="inputParam.upLevelValue=='两线两路' || inputParam.upLevelValue=='精品村' || inputParam.upLevelValue=='面上村' || inputParam.upLevelValue=='重点村'" align="center">乡镇</uni-th>
				<uni-th align="center">类型</uni-th>
				<uni-th align="center">任务完成进度</uni-th>
				<!-- <uni-th align="center">完成数</uni-th>
				<uni-th align="center">完成率</uni-th> -->
			</uni-tr>
			<!-- 表格数据行 -->
			<uni-tr v-for="(item,index) in dataList" :key="index">
				<uni-td align="center">
					<view @click="gotoCuntun(item.name)">
						{{item.name}}
					</view>
				</uni-td>
				<uni-td v-if="inputParam.upLevelValue=='两线两路' || inputParam.upLevelValue=='精品村' || inputParam.upLevelValue=='面上村' || inputParam.upLevelValue=='重点村'" align="center">
					<view @click="gotoCuntun(item.name)">
						{{item.xiang}}
					</view>
				</uni-td>
				<uni-td align="center">
				<view @click="gotoCuntun(item.name)">
					{{item.type}}
				</view>
				</uni-td>
				<uni-td align="right">
				<view @click="gotoCuntun(item.name)">
					{{item.finish}} / {{item.total}}  【 {{item.finishRate}} 】
				</view>
				</uni-td>
				<!-- <uni-td align="right">
				<view @click="gotoCuntun(item.name)">
					
				</view>
				</uni-td>
				<uni-td align="right">
				<view @click="gotoCuntun(item.name)">
					{{item.finishRate}}
				</view>
				</uni-td> -->
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
					upLevelName : 'cunwei',
					upLevelValue : '大塘村委',
					groupBy : 'cun',
				},
			}
		},
		onLoad(e) {
			var _that=this;
			_that.inputParam.upLevelValue = (e.upLevelValue || '')
			_that.setTitle();
		},
		onShow(){
			this.setTitle();
			this.getDataList()
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
					title:(_that.inputParam.upLevelValue || '村委')+'下级自然村屯情况'
				})
			},
			gotoCuntun(e){
				uni.navigateTo({
					url: '/pages/cuntun/cuntun?cun='+e
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
				/* console.log(getApp().globalData.ddUserInfo.role_list)
				var rtnList = this.$utils.arrayUtil.findArray(getApp().globalData.ddUserInfo.role_list,function(e){return e.name.indexOf("管理员")>=0})
				if (rtnList!=null && rtnList.length>0){
					console.log("okoko")
				}
				debugger */
				var _that=this;
				//_that.$http.get('dg/dgNongfang/tongji?'+_that.getParam(_that.inputParam))
				var url = '/database/datamodel/dataSet/getDataByName/CUN/json?cunwei='+ _that.inputParam.upLevelValue;
				if (_that.inputParam.upLevelValue=='两线两路') {
					url = '/database/datamodel/dataSet/getDataByName/CUN_LXLL/json';
				} else if (_that.inputParam.upLevelValue=='重点村' || _that.inputParam.upLevelValue=='面上村' || _that.inputParam.upLevelValue=='精品村') {
					url = '/database/datamodel/dataSet/getDataByName/CUN_TYPE/json?type='+_that.inputParam.upLevelValue;
				} else if (_that.inputParam.upLevelValue=='mytask') {
					url = '/database/datamodel/dataSet/getDataByName/CUN_TIANBAO/json?phone='+/* '18977320255' */getApp().globalData.ddUserInfo.mobile;
					//var phone= getApp().globalData.ddUserInfo.mobile;
				}
				 
				_that.$http.get(url)
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
