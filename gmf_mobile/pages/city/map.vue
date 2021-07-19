<template>
	<div class="content">
		<div class="title-1">临桂区整体完成情况：{{valueAll}} / {{subtotalAll}}</div>
		<div class="title-2">完成率：{{finishRateAll}}</div>
		<div id="echartMap"   @click="gotoCity()" style="width: '100%';height: 500px" />
		
		<view class="padding " style="display: flex;align-items: flex-end;justify-content: center;height: calc(100% - 650px);">
			<button class="cu-btn bg-grey lg margin" @click="gotoMyTask()">我的任务</button>
			<button class="cu-btn bg-green lg margin" @click="gotoCity()">所有乡镇</button>
		</view>
		<!-- 
		<div class="title-2"  @click="gotoCity()" style=" display: flex;align-items: flex-end;justify-content: center;padding-bottom: 40px;height: calc(100% - 650px);">
			<div class="title-3">查看乡镇详细 >></div> <br>
			<div class="title-3">查看我的任务 >></div>
		</div> -->
	</div>
</template>

<script>
	import * as echarts from 'echarts';
	import * as linguiJson from '@/common/lingui.js'; /*chart.min.js为在线定制*/
	var myChart = null;
	export default {
		data() {
			return {
				dataList: [],
				subtotalAll: 0,
				valueAll: 0,
				finishRateAll:'0.00%',
				inputParam: {
					upLevelName: 'xian',
					upLevelValue: '临桂区',
					groupBy: 'xiang',
				},
			};
		},
		//钩子函数  不了解的话 建议看看 vue的生命周期
		mounted() {
			this.getDataList();
		},
		onShow(){
			this.getDataList();
		},
		methods: {
			gotoCity(){
				uni.navigateTo({
					url: '/pages/city/city'
				});
			},
			gotoMyTask(){
				uni.navigateTo({
					url: '/pages/village/village?upLevelValue=mytask'
				});
			},
			gotoTownship(e) {
				uni.navigateTo({
					url: '/pages/township/township?upLevelValue=' + e
				});
			},
			getParam(param) {
				if (null == param) {
					return '';
				}
				var paramArr = []
				for (var key in param) {
					paramArr.push(key + "=" + param[key]);
				}
				return paramArr.join("&");
			},
			getDataList() {
				var _that = this;
				_that.$http.get('/dg/dgNongfang/tongji?'+_that.getParam(_that.inputParam))
				.then(function(res) {
					console.log(res);
					_that.initDataList(res.data.list);
					_that.mapEchartsInit();
				}).catch(function(err) {
					// TODO
					console.log(err);
				});
			},
			initDataList(data) {
				var _that = this;
				var list = _that.initList(data);
				var dataList = _that.mapTemplateData();
				var subtotalAll = 0;
				var valueAll = 0;
			
				for(var i=0;i<dataList.length;i++){
					dataList[i].subtotal = 0;
					dataList[i].value = 0;
					dataList[i].rate = '0.00%';
					for(var j=0;j<list.length;j++){
						if(dataList[i].name == list[j].name){
							dataList[i].subtotal = list[j].subtotal;
							dataList[i].value = list[j].value; 
							dataList[i].rate = dataList[i].subtotal > 0? parseFloat((dataList[i].value * 100) / dataList[i].subtotal).toFixed(2) + ' %' : '0.00 %';
							break
						}
					}
					subtotalAll += dataList[i].subtotal;
					valueAll += dataList[i].value;
				}
				_that.finishRateAll = subtotalAll > 0? parseFloat((valueAll * 100) / subtotalAll).toFixed(2) + ' %' : '0.00 %';
				_that.subtotalAll = subtotalAll;
				_that.valueAll = valueAll;
				_that.dataList = dataList;
			},

			mapEchartsInit() {
				let _that = this
				_that.$nextTick(() => {
					var data = _that.dataList
					var center = _that.mapCenterData()
					var center2 = _that.mapCenterData()
					var lineData = data.map(item => {
						return {
							coords: [
								center[item.name],
								[center[item.name][0], center[item.name][1] + item.value * 0.0001]
							]
						}
					})
					var scatterData = data.map(item => {
						return [center[item.name][0], center[item.name][1] + item.value * 0.0001]
					})
					var scatterData2 = data.map(item => {
						return center[item.name]
					})
					var scatterData3 = data.map(item => {
						return center2[item.name].concat(item.name)
					})

					myChart = echarts.init(document.getElementById('echartMap'));
					var mapData = _that.getMapData();
					echarts.registerMap('js2', mapData, {});
					echarts.registerMap('js', mapData, {});
					var option = {
						backgroundColor: '#192239',
						geo: [{
							map: 'js',
							aspectScale: 1,
							roam: false, //是否允许缩放
							zoom: 1.12, //默认显示级别
							layoutSize: '100%',
							layoutCenter: ['42%', '50%'],
							itemStyle: {
								areaColor: 'transparent',
								borderColor: '#37C1FD',
								borderWidth: 1,
							},
							emphasis: {
								itemStyle: {
									areaColor: '#192e6b'
								},
								label: {
									show: 0,
									color: '#fff'
								}
							},
							zlevel: 3,
						}, {
							map: 'js2',
							aspectScale: 1,
							roam: false, //是否允许缩放
							zoom: 1.12, //默认显示级别
							layoutSize: '100%',
							layoutCenter: ['42%', '50%'],
							itemStyle: {
								areaColor: {
									type: 'radial',
									x: 0.6,
									y: 0.5,
									r: 0.8,
									colorStops: [{
										offset: 0,
										color: '#56CCFB' // 0% 处的颜色
									}, {
										offset: 1,
										color: '#0160AD' // 100% 处的颜色
									}],
									global: false // 缺省为 false
								},
								borderColor: '#37C1FD',
								borderWidth: 1,
							},
							zlevel: 2,
							silent: true,
						}, {
							map: 'js2',
							aspectScale: 1,
							roam: false, //是否允许缩放
							zoom: 1.12, //默认显示级别
							layoutSize: '100%',
							layoutCenter: ['42%', '50.5%'],
							itemStyle: {
								areaColor: '#005DDC',
								borderColor: '#329BF5',
								borderWidth: 1,
							},
							zlevel: 1,
							silent: true,
						}, ],
						series: [{
							type: 'lines',
							zlevel: 5,
							effect: {
								show: false,
								period: 4, //箭头指向速度，值越小速度越快
								trailLength: 0.02, //特效尾迹长度[0,1]值越大，尾迹越长重
								symbol: 'arrow', //箭头图标
								symbolSize: 5, //图标大小
							},
							lineStyle: {
								width: 15, //尾迹线条宽度
								color: {
									type: 'linear',
									x: 0,
									y: 0,
									x2: 1,
									y2: 0,
									colorStops: [{
										offset: 0,
										color: '#F09B0A' // 0% 处的颜色
									}, {
										offset: 0.2,
										color: '#F09B0A' // 0% 处的颜色
									}, {
										offset: 0.5,
										color: '#FEF03B' // 0% 处的颜色
									}, {
										offset: 0.7,
										color: '#FEF03B' // 0% 处的颜色
									}, {
										offset: 1,
										color: '#F09B0A' // 100% 处的颜色
									}],
									global: false // 缺省为 false
								},
								opacity: 1, //尾迹线条透明度
								curveness: 0 //尾迹线条曲直度
							},
							label: {
								show: 0,
								position: 'end',
								formatter: '245'
							},
							silent: true,
							data: lineData
						}, {
							type: 'scatter',
							coordinateSystem: 'geo',
							geoIndex: 0,
							zlevel: 5,
							label: {
								show: !0,
								position: 'right',
								formatter: params =>   data[params.dataIndex].rate +'\n'
									+data[params.dataIndex].value +
									' / ' + data[params.dataIndex].subtotal,
								padding: [4, 8],
								backgroundColor: '#003F5E',
								borderRadius: 5,
								borderColor: '#67F0EF',
								borderWidth: 1,
								color: '#67F0EF'
							},
							symbol: 'circle',
							symbolSize: [15, 8],
							itemStyle: {
								color: '#FEF03B',
								opacity: 1
							},
							silent: true,
							data: scatterData
						}, {
							type: 'scatter',
							coordinateSystem: 'geo',
							geoIndex: 0,
							zlevel: 5,
							symbol: 'circle',
							symbolSize: [15, 8],
							itemStyle: {
								color: '#F09B0A',
								opacity: 1,
								shadowColor: '#000',
								shadowBlur: 5,
								shadowOffsetY: 2,
							},
							silent: true,
							data: scatterData2
						}, {
							type: 'effectScatter',
							coordinateSystem: 'geo',
							geoIndex: 0,
							symbol: 'circle',
							symbolSize: 4,
							showEffectOn: 'render',
							rippleEffect: {
								brushType: 'fill',
								scale: 10
							},
							hoverAnimation: true,
							label: {
								formatter: p => p.data[2],
								position: 'bottom',
								color: '#fff',
								fontSize: 14,
								distance: 10,
								show: !0,
							},
							itemStyle: {
								color: '#FEF134',
							},
							zlevel: 6,
							data: scatterData3
						}]
					};
					myChart.on('click', function(params) {
						var city = params.name;
						_that.gotoTownship(city)
					});
					myChart.setOption(option);

				})
			},
			
			initList(data){
				var list = [];
				for (var i = 0; i < data.length; i++) {
					var tmp = {
						name: '',
						subtotal: 0,
						value: 0
					}
					tmp.name = data[i].name;
					tmp.subtotal = parseInt(data[i].total || 0);
					tmp.value = parseInt(data[i].finish || 0);
					list.push(tmp);
				}
				return list
			},
			getMapData() {
				let _that = this;
				var mapData = {};
				mapData = linguiJson.data
				return mapData;
			},
			mapCenterData(){
				return {
					"茶洞镇": [109.915668, 25.240459],
					"黄沙瑶族乡": [109.906307, 25.476524],
					"会仙镇": [110.216462, 25.066061],
					"两江镇": [110.024121, 25.199255],
					"临桂镇": [110.179752, 25.275615],
					"六塘镇": [110.292636, 25.005594],
					"南边山镇": [110.230037, 24.891612],
					"宛田瑶族乡": [110.058802, 25.61523],
					"五通镇": [110.035869, 25.365575],
					"四塘镇": [110.132541, 25.161606],
					"中庸镇": [110.083073, 25.478576]
				}
			},
			mapTemplateData() {
				return [
					{name: '茶洞镇',subtotal: 1000,value: (Math.random() * 100 + 500).toFixed(0)},
					{name: '黄沙瑶族乡',subtotal: 1000,value: (Math.random() * 100 + 500).toFixed(0)},
					{name: '会仙镇',subtotal: 1000,value: (Math.random() * 100 + 300).toFixed(0)},
					{name: '两江镇',subtotal: 1000,value: (Math.random() * 100 + 300).toFixed(0)},
					{name: '临桂镇',subtotal: 1000,value: (Math.random() * 100 + 300).toFixed(0)},
					{name: '六塘镇',subtotal: 1000,value: (Math.random() * 100 + 300).toFixed(0)},
					{name: '南边山镇',subtotal: 1000,value: (Math.random() * 100 + 100).toFixed(0)},
					{name: '宛田瑶族乡',subtotal: 1000,value: (Math.random() * 100 + 100).toFixed(0)},
					{name: '五通镇',subtotal: 1000,value: (Math.random() * 100 + 100).toFixed(0)},
					{name: '四塘镇',subtotal: 1000,value: (Math.random() * 100 + 100).toFixed(0)},
					{name: '中庸镇',subtotal: 1000,value: (Math.random() * 100 + 100).toFixed(0)}
				]
			}
		},

	}
</script>

<style>
	.content{
		background: #192239 !important;
	}
	#echartMap div {
		height: 100%;
	}

	.title-1 {
		background-color: #192239;
		padding-top: 20px;
		font-size: 40rpx;
		color: white;
		text-align: center;
	}

	.title-2 {
		background-color: #192239;
		padding-top: 5px;
		font-size: 40rpx;
		color: white;
		text-align: center;
	}
	
	.title-3 {
		position: relative;
		top: -40rpx;
	}
</style>
