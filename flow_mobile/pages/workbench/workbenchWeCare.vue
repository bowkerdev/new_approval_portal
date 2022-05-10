<template>
	<view>
		<cu-custom bgColor="bg-blue" backUrl="/pages/login/login" :isBack="!hasSSOTokenFlag">
			<block slot="backText">{{$i18nMy.t('返回')}}</block> 
			<block slot="content" >{{$i18nMy.t('工作中心')}}</block>
		</cu-custom>
		<!-- <swiper class="screen-swiper square-dot bg-blue"  :indicator-dots="true" :circular="true"
		 :autoplay="true" interval="2000" duration="500">
			<swiper-item v-for="(item,index) in swiperList"  :key="index">
				<image :src="item.url" mode="aspectFill" v-if="item.type=='image'"></image>
				<video :src="item.url" autoplay loop muted :show-play-btn="false" :controls="false" objectFit="cover" v-if="item.type=='video'"></video>
			</swiper-item>
		</swiper> -->
		<view class="grid col-3 padding-sm" style="margin-top: -7px;padding-left: 0px;padding-right: 0px;">
			<view  @tap="toTodoList"  class="padding-sm" style="padding-left: 10px;">
				<navigator hover-class="none" url="/pages/workbench/workbenchWeCare">
					<view class="padding radius text-center shadow-blur bg-white" style="width: 100%;height: 100%;">
						<view>
							<text class="lg font-size-35 text-blue cuIcon-list"></text>
							<view class="margin-top-sm text-Abc" style="font-size: 15px;">{{$i18nMy.t('待办任务')}}</view>
						</view>
						<view class="cu-tag badge">{{todoTag}}</view>
					</view>
				</navigator>
				
			</view>
			<view  @tap="toHistoryList"  class="padding-sm" style="padding-left: 0px;">
				<navigator hover-class="none" url="/pages/workbench/workbenchWeCare" >
					<view class="padding radius text-center shadow-blur bg-white" style="width: 100%;height: 100%;">
						<view>
							<text class="lg font-size-35 text-blue cuIcon-time"></text>
							<view class="margin-top-sm text-Abc" style="font-size: 15px;">{{$i18nMy.t('已办任务')}}</view>
						</view>
						<view class="cu-tag badge">{{historicTag}}</view>
					</view>
				</navigator>
			</view>
		</view>
		<view class="cu-tabbar-height"></view>
	</view>
</template>

<script>
	import moment from 'moment'
	import {mapState} from 'vuex'
	export default {
		data() {
			return {
				todoTag:0,
				historicTag:0,
				cardCur: 0,
				dataList: [],
				categoryList: [],
				processMap: new Map(),
				swiperList: [{
					id: 0,
					type: 'image',
					url: '/static/swiper/1.svg'
				}, {
					id: 1,
					type: 'image',
					url: '/static/swiper/2.svg'
				}, {
					id: 2,
					type: 'image',
					url: '/static/swiper/3.svg'
				}, {
					id: 3,
					type: 'image',
					url: '/static/swiper/4.svg'
				}, {
					id: 4,
					type: 'image',
					url: '/static/swiper/5.svg'
				}, {
					id: 5,
					type: 'image',
					url: '/static/swiper/6.svg'
				}, {
					id: 6,
					type: 'image',
					url: '/static/swiper/7.svg'
				}],
				dotStyle: false,
				towerStart: 0,
				direction: ''
			};
		}, 
		computed: mapState({
			 username: (state) => state.user.username,
			 hasSSOTokenFlag: (state) => state.user.hasSSOTokenFlag
		}),
	    async mounted() {
			this.getTag();
			/* let res = await this.$http.get('/app/extension/actCategory/treeData')
			let {data} = await this.$http.get('/app/flowable/process/list')
			this.processMap = new Map()
			res.data.treeData.forEach((item)=>{
				this.processMap.set(item.name, [])
			})
			let list = data.page.list
			list.forEach((item)=>{
				if(this.processMap.has(item.category)){
					let list = this.processMap.get(item.category)
					list.push(item)
				}else{
					this.processMap.set(item.category, [item])
				}
			})
			
			for(let [key,value] of this.processMap){
			    console.log(key,value);
			} */
		},
		activated() {
			this.getTag();
		},
		methods: {
			getTag(){
				let _that = this;
				_that.$http.get('/app/flowable/task/todo', {
					pageNo: 10,
					pageSize: 1,
					status: '1',
					procDefKey: 'app_flow_list',
					title: ''
					
				}).then(({data})=>{
					_that.todoTag = data.page.count || 0
				});
				_that.$http.get('/app/flowable/task/historic', {
					pageNo: 10,
					pageSize: 1,
					status: '1',					
					procDefKey: 'app_flow_list',
					title: ''
					
				}).then(({data})=>{
					_that.historicTag = data.page.count || 0
				})
			}, 
			toApplyList () {
				uni.navigateTo({
				   url: '/pages/workbench/task/ApplyList'
				})
			},
			toTodoList () {
				uni.navigateTo({
				    url: '/pages/workbench/task/TodoList'
				})
			},
			toHistoryList () {
				uni.navigateTo({
				    url: '/pages/workbench/task/HistoryList'
				})
			},
			toFlowCopyList () {
				uni.navigateTo({
				    url: '/pages/workbench/task/FlowCopyList'
				})
			},
			start (row) {
				  // 读取流程表单
				this.$http.get('/app/flowable/task/getTaskDef', {
				  procDefId: row.id,
				  status: 'start'
				}).then(({data}) => {
				  if (data.success) {
					  let processTitle = `${this.username} 在 ${moment(new Date()).format('YYYY-MM-DD HH:mm')} 发起了 [${row.name}]`
					  let query = {procDefId: row.id, procDefKey: row.key, status: 'start', title: `发起流程【${row.name}】`, formType: data.flow.formType, formUrl: data.flow.formUrl, formTitle: processTitle}
					  uni.navigateTo({
					     url: '/pages/workbench/task/TaskForm?flow='+JSON.stringify(query)
					  })
				  }
				})
		  }
		}
	}
</script>

<style>
  .cu-list.card-menu {
      overflow: hidden;
      margin-right: 5px;
      margin-left: 5px;
      border-radius: 7px;
  }
  .cu-list.card-menu.margin-top-20 {
      margin-top: -20px;
  }
  .cu-list.menu>.cu-item .content>uni-view:first-child {
      display: -webkit-box;
      display: -webkit-flex;
      display: flex;
      -webkit-box-align: center;
      /* -webkit-align-items: center; */
      /* align-items: center; */
      display: inline-block;
      margin-right: 5px;
      width: 1.6em;
      text-align: center;
  }
  .font-size-35{
	 font-size: 35px!important;
  }
  .circle-button{
	 width: 44px;
	 height: 44px;
	 border-radius: 18px;
	 padding-top: 4px;
  }
  .circle-button-box{
	  width: 25%;
	  margin-top: 10px;
	  display: -webkit-box;
	  display: -webkit-flex;
	  display: flex;
	  -webkit-box-orient: vertical;
	  -webkit-box-direction: normal;
	  -webkit-flex-direction: column;
	  flex-direction: column;
	  -webkit-box-align: center;
	  -webkit-align-items: center;
	  align-items: center;
	  -webkit-box-pack: center;
	  -webkit-justify-content: center;
	  justify-content: center;
	  box-sizing: border-box;
  }
  .font-b {
	  vertical-align: middle;
	  font-size: 18px;
	  font-weight: 500;
	  color: #3a3a3a;
  }
</style>
