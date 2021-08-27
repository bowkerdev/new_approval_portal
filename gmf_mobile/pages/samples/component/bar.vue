<template>
	<view>
		<cu-custom bgColor="bg-gradual-pink" :isBack="true"><block slot="backText">{{$i18nMy.t('返回')}}</block><block slot="content">{{$i18nMy.t('操作条')}}</block></cu-custom>
		<view class="cu-bar bg-white margin-top">
			<view class="action">
				<text class="cuIcon-title text-green"></text>
				<text>{{$i18nMy.t('底部操作条')}}</text>
			</view>
		</view>
		<view class="box">
			<view class="cu-bar tabbar bg-white">
				<view class="action">
					<view class="cuIcon-cu-image">
						<image src="/static/tabbar/basics_cur.png"></image>
					</view>
					<view class="text-green">{{$i18nMy.t('元素')}}</view>
				</view>
				<view class="action">
					<view class="cuIcon-cu-image">
						<image src="/static/tabbar/component.png"></image>
					</view>
					<view class="text-gray">{{$i18nMy.t('组件')}}</view>
				</view>
				<view class="action">
					<view class="cuIcon-cu-image">
						<image src="/static/tabbar/plugin.png"></image>
						<view class="cu-tag badge">99</view>
					</view>
					<view class="text-gray">{{$i18nMy.t('扩展')}}</view>
				</view>
				<view class="action">
					<view class="cuIcon-cu-image">
						<image src="/static/tabbar/about.png"></image>
						<view class="cu-tag badge"></view>
					</view>
					<view class="text-gray">{{$i18nMy.t('关于')}}</view>
				</view>
			</view>
			<view class="cu-bar tabbar margin-bottom-xl bg-black">
				<view class="action text-orange">
					<view class="cuIcon-homefill"></view> 首页
				</view>
				<view class="action text-gray">
					<view class="cuIcon-similar"></view> 分类
				</view>
				<view class="action text-gray">
					<view class="cuIcon-recharge"></view>
					积分
				</view>
				<view class="action text-gray">
					<view class="cuIcon-cart">
						<view class="cu-tag badge">99</view>
					</view>
					购物车
				</view>
				<view class="action text-gray">
					<view class="cuIcon-my">
						<view class="cu-tag badge"></view>
					</view>
					我的
				</view>
			</view>
			<view class="cu-bar tabbar margin-bottom-xl bg-white">
				<view class="action text-green">
					<view class="cuIcon-homefill"></view> 首页
				</view>
				<view class="action text-gray">
					<view class="cuIcon-similar"></view> 分类
				</view>
				<view class="action text-gray add-action">
					<button class="cu-btn cuIcon-add bg-green shadow"></button>
					发布
				</view>
				<view class="action text-gray">
					<view class="cuIcon-cart">
						<view class="cu-tag badge">99</view>
					</view>
					购物车
				</view>
				<view class="action text-gray">
					<view class="cuIcon-my">
						<view class="cu-tag badge"></view>
					</view>
					我的
				</view>
			</view>
			<view class="cu-bar tabbar bg-black">
				<view class="action text-green">
					<view class="cuIcon-homefill"></view> 首页
				</view>
				<view class="action text-gray">
					<view class="cuIcon-similar"></view> 分类
				</view>
				<view class="action text-gray add-action">
					<button class="cu-btn cuIcon-add bg-green shadow"></button>
					发布
				</view>
				<view class="action text-gray">
					<view class="cuIcon-cart">
						<view class="cu-tag badge">99</view>
					</view>
					购物车
				</view>
				<view class="action text-gray">
					<view class="cuIcon-my">
						<view class="cu-tag badge"></view>
					</view>
					我的
				</view>
			</view>

			<view class="cu-bar bg-white tabbar border shop">
				<button class="action" open-type="contact">
					<view class="cuIcon-service text-green">
						<view class="cu-tag badge"></view>
					</view>
					客服
				</button>
				<view class="action text-orange">
					<view class="cuIcon-favorfill"></view> 已收藏
				</view>
				<view class="action">
					<view class="cuIcon-cart">
						<view class="cu-tag badge">99</view>
					</view>
					购物车
				</view>
				<view class="bg-red submit">{{$i18nMy.t('立即订购')}}</view>
			</view>

			<view class="cu-bar bg-white tabbar border shop">
				<button class="action" open-type="contact">
					<view class="cuIcon-service text-green">
						<view class="cu-tag badge"></view>
					</view>
					客服
				</button>
				<view class="action">
					<view class="cuIcon-cart">
						<view class="cu-tag badge">99</view>
					</view>
					购物车
				</view>
				<view class="bg-orange submit">{{$i18nMy.t('加入购物车')}}</view>
				<view class="bg-red submit">{{$i18nMy.t('立即订购')}}</view>
			</view>

			<view class="cu-bar bg-white tabbar border shop">
				<button class="action" open-type="contact">
					<view class="cuIcon-service text-green">
						<view class="cu-tag badge"></view>
					</view>
					客服
				</button>
				<view class="action">
					<view class=" cuIcon-shop"></view> 店铺
				</view>
				<view class="action">
					<view class="cuIcon-cart">
						<view class="cu-tag badge">99</view>
					</view>
					购物车
				</view>
				<view class="btn-group">
					<button class="cu-btn bg-red round shadow-blur">{{$i18nMy.t('立即订购')}}</button>
				</view>
			</view>
			<view class="cu-bar bg-white tabbar border shop">
				<button class="action" open-type="contact">
					<view class="cuIcon-service text-green">
						<view class="cu-tag badge"></view>
					</view> 客服
				</button>
				<view class="action">
					<view class="cuIcon-cart">
						<view class="cu-tag badge">99</view>
					</view>
					购物车
				</view>
				<view class="btn-group">
					<button class="cu-btn bg-orange round shadow-blur">{{$i18nMy.t('加入购物车')}}</button>
					<button class="cu-btn bg-red round shadow-blur">{{$i18nMy.t('立即订购')}}</button>
				</view>
			</view>
		</view>

		<view class="cu-bar bg-white">
			<view class="action">
				<text class="cuIcon-title text-green"></text>
				<text>{{$i18nMy.t('标题操作条')}}</text>
			</view>
		</view>
		<view class="box" v-if="false">
			<view class="cu-bar justify-center bg-white">
				<view class="action border-title">
					<text class="text-xl text-bold">{{$i18nMy.t('关于我们')}}</text>
					<text class="bg-grey" style="width:2rem"></text>
					<!-- 底部样式 last-child选择器-->
				</view>
			</view>
			<view class="cu-bar justify-center bg-white">
				<view class="action border-title">
					<text class="text-xl text-bold text-blue">{{$i18nMy.t('关于我们')}}</text>
					<text class="bg-gradual-blue" style="width:3rem"></text>
				</view>
			</view>
			<view class="cu-bar justify-center bg-white">
				<view class="action sub-title">
					<text class="text-xl text-bold text-green">{{$i18nMy.t('关于我们')}}</text>
					<text class="bg-green" style="width:2rem"></text>
					<!-- last-child选择器-->
				</view>
			</view>
			<view class="cu-bar justify-center bg-white">
				<view class="action sub-title">
					<text class="text-xl text-bold text-blue">{{$i18nMy.t('关于我们')}}</text>
					<text class="text-ABC text-blue">about</text>
					<!-- last-child选择器-->
				</view>
			</view>
		</view>
		<view class="box">
			<view class="cu-bar bg-white">
				<view class="action border-title">
					<text class="text-xl text-bold">{{$i18nMy.t('关于我们')}}</text>
					<text class="bg-grey" style="width:2rem"></text>
					<!-- 底部样式 last-child选择器-->
				</view>
			</view>
			<view class="cu-bar bg-white">
				<view class="action border-title">
					<text class="text-xl text-bold text-blue">{{$i18nMy.t('关于我们')}}</text>
					<text class="bg-gradual-blue" style="width:3rem"></text>
				</view>
			</view>
			<view class="cu-bar bg-white">
				<view class="action sub-title">
					<text class="text-xl text-bold text-green">{{$i18nMy.t('关于我们')}}</text>
					<text class="bg-green"></text>
					<!-- last-child选择器-->
				</view>
			</view>
			<view class="cu-bar bg-white">
				<view class="action sub-title">
					<text class="text-xl text-bold text-blue">{{$i18nMy.t('关于我们')}}</text>
					<text class="text-ABC text-blue">about</text>
					<!-- last-child选择器-->
				</view>
			</view>
			<view class="cu-bar bg-white">
				<view class="action title-style-3">
					<text class="text-xl text-bold">{{$i18nMy.t('关于我们')}}</text>
					<text class="text-Abc text-gray self-end margin-left-sm">about</text>
				</view>
			</view>
			<view class="cu-bar bg-white">
				<view class="action">
					<text class="cuIcon-title text-green"></text>
					<text class="text-xl text-bold">{{$i18nMy.t('关于我们')}}</text>
				</view>
			</view>
			<view class="cu-bar bg-white">
				<view class="action">
					<text class="cuIcon-titles text-green"></text>
					<text class="text-xl text-bold">{{$i18nMy.t('关于我们')}}</text>
				</view>
			</view>
		</view>

		<view class="cu-bar bg-white">
			<view class="action">
				<text class="cuIcon-title text-green"></text>
				<text>{{$i18nMy.t('顶部操作条')}}</text>
			</view>
		</view>
		<view class="box">
			<view class="cu-bar bg-white">
				<view class="action">
					<text class="cuIcon-back text-gray"></text> 返回
				</view>
				<view class="content text-bold">
					操作条
				</view>
			</view>
			<view class="cu-bar bg-white">
				<view class="action">
					<text class="cuIcon-homefill text-gray"></text> 首页
				</view>
				<view class="content text-bold">
					鲜亮的高饱和色彩，专注视觉的小程序组件库
				</view>
				<view class="action">
					<text class="cuIcon-cardboardfill text-grey"></text>
					<text class="cuIcon-recordfill text-red"></text>
				</view>
			</view>
			<view class="cu-bar bg-blue">
				<view class="action">
					<text class="cuIcon-close"></text> 关闭
				</view>
				<view class="content text-bold">
					海蓝
				</view>
			</view>
			<view class="cu-bar bg-black search">
				<view class="cu-avatar round" style="background-image:url(https://ossweb-img.qq.com/images/lol/web201310/skin/big91012.jpg);"></view>
				<view class="content">
					ColorUI
				</view>
				<view class="action">
					<text class="cuIcon-more"></text>
				</view>
			</view>
		</view>


		<view class="cu-bar bg-white">
			<view class="action">
				<text class="cuIcon-title text-green"></text>
				<text>{{$i18nMy.t('搜索操作条')}}</text>
			</view>
		</view>
		<view class="box">
			<view class="cu-bar search bg-white">
				<view class="search-form round">
					<text class="cuIcon-search"></text>
					<input @focus="InputFocus" @blur="InputBlur" :adjust-position="false" type="text" :placeholder="$i18nMy.t('搜索图片、文章、视频')" confirm-type="search"></input>
				</view>
				<view class="action">
					<button class="cu-btn bg-green shadow-blur round">{{$i18nMy.t('搜索')}}</button>
				</view>
			</view>
			<view class="cu-bar search bg-white">
				<view class="cu-avatar round" style="background-image:url(https://ossweb-img.qq.com/images/lol/web201310/skin/big11010.jpg"></view>
				<view class="search-form round">
					<text class="cuIcon-search"></text>
					<input @focus="InputFocus" @blur="InputBlur" :adjust-position="false" type="text" :placeholder="$i18nMy.t('搜索图片、文章、视频')" confirm-type="search"></input>
				</view>
				<view class="action">
					<text>{{$i18nMy.t('广州')}}</text>
					<text class="cuIcon-triangledownfill"></text>
				</view>
			</view>
			<view class="cu-bar bg-red search">
				<view class="cu-avatar round" style="background-image:url(https://ossweb-img.qq.com/images/lol/web201310/skin/big114004.jpg);"></view>
				<view class="search-form radius">
					<text class="cuIcon-search"></text>
					<input @focus="InputFocus" @blur="InputBlur" :adjust-position="false" type="text" :placeholder="$i18nMy.t('搜索图片、文章、视频')" confirm-type="search"></input>
				</view>
				<view class="action">
					<text>{{$i18nMy.t('广州')}}</text>
					<text class="cuIcon-triangledownfill"></text>
				</view>
			</view>
			<view class="cu-bar bg-cyan search">
				<view class="search-form radius">
					<text class="cuIcon-search"></text>
					<input @focus="InputFocus" @blur="InputBlur" :adjust-position="false" type="text" :placeholder="$i18nMy.t('搜索图片、文章、视频')" confirm-type="search"></input>
				</view>
				<view class="action">
					<text class="cuIcon-close"></text>
					<text>{{$i18nMy.t('取消')}}</text>
				</view>
			</view>
		</view>

		<view class="cu-bar bg-white">
			<view class="action">
				<text class="cuIcon-title text-green"></text>
				<text>{{$i18nMy.t('操作条按钮组')}}</text>
			</view>
		</view>

		<view class="box">
			<view class="cu-bar btn-group">
				<button class="cu-btn bg-green shadow-blur round lg">{{$i18nMy.t('保存')}}</button>
			</view>
			<view class="cu-bar btn-group">
				<button class="cu-btn bg-green shadow-blur">{{$i18nMy.t('保存')}}</button>
				<button class="cu-btn text-green line-green shadow">{{$i18nMy.t('上传')}}</button>
			</view>
			<view class="cu-bar btn-group">
				<button class="cu-btn bg-green shadow-blur round">{{$i18nMy.t('保存')}}</button>
				<button class="cu-btn bg-blue shadow-blur round">{{$i18nMy.t('提交')}}</button>
			</view>
		</view>


		<view class="cu-bar bg-white">
			<view class="action">
				<text class="cuIcon-title text-green"></text>
				<text>{{$i18nMy.t('输入操作条')}}</text>
			</view>
		</view>
		<view class="box">
			<view class="cu-bar input">
				<view class="action">
					<text class="cuIcon-sound text-grey"></text>
				</view>
				<input @focus="InputFocus" @blur="InputBlur" :adjust-position="false" class="solid-bottom" :focus="false" maxlength="300" cursor-spacing="10"></input>
				<view class="action">
					<text class="cuIcon-emojifill text-grey"></text>
				</view>
				<button class="cu-btn bg-green shadow-blur">{{$i18nMy.t('发送')}}</button>
			</view>

			<view class="cu-bar input">
				<view class="cu-avatar round" style="background-image:url(https://ossweb-img.qq.com/images/lol/web201310/skin/big91012.jpg);"></view>
				<view class="action">
					<text class="cuIcon-roundaddfill text-grey"></text>
				</view>
				<input @focus="InputFocus" @blur="InputBlur" :adjust-position="false" class="solid-bottom" maxlength="300" cursor-spacing="10"></input>
				<view class="action">
					<text class="cuIcon-emojifill text-grey"></text>
				</view>
				<button class="cu-btn bg-green shadow-blur">{{$i18nMy.t('发送')}}</button>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				InputBottom: 0
			};
		},
		methods: {
			InputFocus(e) {
				this.InputBottom = e.detail.height
			},
			InputBlur(e) {
				this.InputBottom = 0
			}
		}
	}
</script>

<style>
	.box {
		margin: 20upx 0;
	}

	.box view.cu-bar {
		margin-top: 20upx;
	}
</style>
