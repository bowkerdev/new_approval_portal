<template>
	<view>
		<cu-custom bgColor="bg-gradual-blue" :isBack="true"><block slot="backText">{{$i18nMy.t('返回')}}</block><block slot="content">{{$i18nMy.t('边框阴影')}}</block></cu-custom>
		<view class="cu-bar bg-white solid-bottom">
			<view class="action">
				<text class="cuIcon-title text-blue"></text>{{$i18nMy.t('边框')}}</view>
			<view class="action">
				<switch class="sm" @change="SetSize" :class="size?'checked':''"></switch>
			</view>
		</view>
		<view class="padding bg-white text-center">
			<view class="padding" :class="size?'solids':'solid'">{{$i18nMy.t('四周')}}</view>
			<view class="padding margin-top" :class="size?'solids-top':'solid-top'">{{$i18nMy.t('上')}}</view>
			<view class="padding margin-top" :class="size?'solids-right':'solid-right'">{{$i18nMy.t('右')}}</view>
			<view class="padding margin-top" :class="size?'solids-bottom':'solid-bottom'">{{$i18nMy.t('下')}}</view>
			<view class="padding margin-top" :class="size?'solids-left':'solid-left'">{{$i18nMy.t('左')}}</view>
		</view>
		<view class="cu-bar bg-white margin-top">
			<view class="action">
				<text class="cuIcon-title text-blue"></text>{{$i18nMy.t('阴影')}}</view>
		</view>
		<view class="padding text-center">
			<view class="padding-xl radius shadow bg-white">{{$i18nMy.t('默认阴影')}}</view>
			<view class="padding-xl radius shadow bg-blue margin-top">{{$i18nMy.t('根据背景颜色而改变的阴影')}}</view>
			<view class="padding-xl radius shadow shadow-lg bg-white margin-top">{{$i18nMy.t('长阴影')}}</view>
			<view class="padding-xl radius shadow shadow-lg bg-blue margin-top">{{$i18nMy.t('长阴影')}}</view>
			<view class="padding-xl radius shadow-warp bg-white margin-top">{{$i18nMy.t('翘边阴影')}}</view>
			<view class="padding-xl radius shadow-blur bg-red margin-top bg-img" style="background-image:url(https://ossweb-img.qq.com/images/lol/web201310/skin/big91005.jpg);">
				<view>{{$i18nMy.t('根据背景图而改变的阴影')}}</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				size: false
			};
		},
		methods: {
			SetSize(e) {
				this.size = e.detail.value
			}
		}
	}
</script>

<style>

</style>
