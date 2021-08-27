<template>
	<view>
		<cu-custom bgColor="bg-gradual-blue" :isBack="true"><block slot="backText">{{$i18nMy.t('返回')}}</block><block slot="content">{{$i18nMy.t('按钮')}}</block></cu-custom>
		<view class="cu-bar bg-white solid-bottom">
			<view class="action">
				<text class="cuIcon-title text-blue"></text>{{$i18nMy.t('按钮形状')}}</view>
			<view class="action">
				<navigator class="action" url="design" hover-class="none">
					<text class="cuIcon-skinfill"></text>
					<text class="text-df">{{$i18nMy.t('设计')}}</text>
				</navigator>
			</view>
		</view>
		<view class="padding flex flex-wrap justify-between align-center bg-white">
			<button class="cu-btn">{{$i18nMy.t('默认')}}</button>
			<button class="cu-btn round">{{$i18nMy.t('圆角')}}</button>
			<button class="cu-btn cuIcon">
				<text class="cuIcon-emojifill"></text>
			</button>
		</view>
		<view class="cu-bar margin-top bg-white solid-bottom">
			<view class="action">
				<text class="cuIcon-title text-blue"></text>{{$i18nMy.t('按钮尺寸')}}</view>
		</view>
		<view class="padding flex flex-wrap justify-between align-center bg-white">
			<button class="cu-btn round sm">{{$i18nMy.t('小尺寸')}}</button>
			<button class="cu-btn round">{{$i18nMy.t('默认')}}</button>
			<button class="cu-btn round lg">{{$i18nMy.t('大尺寸')}}</button>
		</view>
		<view class="cu-bar margin-top bg-white">
			<view class="action">
				<text class="cuIcon-title text-blue"></text>{{$i18nMy.t('按钮颜色')}}</view>
			<view class="action">
				<text class="text-df margin-right-sm">{{$i18nMy.t('阴影')}}</text>
				<switch @change="SetShadow" :class="shadow?'checked':''" color="#39B54A"></switch>
			</view>
		</view>
		<view class="grid col-5 padding-sm">
			<view class="margin-tb-sm text-center" v-for="(item,index) in ColorList" :key="index">
				<button class="cu-btn round" :class="['bg-' + item.name , shadow?'shadow':'']">{{item.title}}</button>
			</view>
		</view>
		<view class="cu-bar margin-top bg-white">
			<view class="action">
				<text class="cuIcon-title text-blue"></text>{{$i18nMy.t('镂空按钮')}}</view>
			<view class="action">
				<radio-group @change="SetBorderSize">
					<label class="margin-left-sm">
						<radio class="blue radio" value="" checked></radio>
						<text class="margin-left-sm">{{$i18nMy.t('小')}}</text>
					</label>
					<label class="margin-left-sm">
						<radio class="blue radio" value="s"></radio>
						<text class="margin-left-sm">{{$i18nMy.t('大')}}</text>
					</label>
				</radio-group>
			</view>
		</view>
		<view class="grid col-5 padding-sm">
			<view class="margin-tb-sm text-center" v-for="(item,index) in ColorList" :key="index" v-if="item.name!='white'">
				<button class="cu-btn round" :class="[bordersize?'lines-' + item.name:'line-' + item.name, shadow?'shadow':'']">{{item.title}}</button>
			</view>
		</view>
		<view class="cu-bar margin-top bg-white">
			<view class="action">
				<text class="cuIcon-title text-blue"></text>{{$i18nMy.t('块状按钮')}}</view>
		</view>
		<view class="padding flex flex-direction">
			<button class="cu-btn bg-grey lg">{{$i18nMy.t('玄灰')}}</button>
			<button class="cu-btn bg-red margin-tb-sm lg">{{$i18nMy.t('嫣红')}}</button>
		</view>
		<view class="cu-bar margin-top bg-white">
			<view class="action">
				<text class="cuIcon-title text-blue"></text>{{$i18nMy.t('无效状态')}}</view>
		</view>
		<view class="padding">
			<button class="cu-btn block bg-blue margin-tb-sm lg" disabled type="">{{$i18nMy.t('无效状态')}}</button>
			<button class="cu-btn block line-blue margin-tb-sm lg" disabled>{{$i18nMy.t('无效状态')}}</button>
		</view>
		<view class="cu-bar margin-top bg-white">
			<view class="action">
				<text class="cuIcon-title text-blue"></text>{{$i18nMy.t('按钮加图标')}}</view>
		</view>
		<view class="padding-xl">
			<button class="cu-btn block line-orange lg">
				<text class="cuIcon-upload"></text> 图标</button>
			<button class="cu-btn block bg-blue margin-tb-sm lg">
				<text class="cuIcon-loading2 cuIconfont-spin"></text> 加载</button>
			<button class="cu-btn block bg-black margin-tb-sm lg" loading> 原生加载</button>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				ColorList: this.ColorList,
				shadow: false,
				bordersize: ''
			};
		},
		methods: {
			SetShadow(e) {
				this.shadow = e.detail.value
			},
			SetBorderSize(e) {
				this.bordersize = e.detail.value
			}
		}
	}
</script>

<style>

</style>
