<template>
	<view>
		<cu-custom bgColor="bg-blue" backUrl="/pages/apps/mail/inbox" :isBack="true">
			<block slot="backText">{{$i18nMy.t('返回')}}</block>
			<block slot="content"> {{mailBox.mail.title}}</block>
		</cu-custom>
		<view class="cu-bar bg-white solid-bottom">
			<view class="mail grid col-1  margin-top-sm action " >
				<view class=" text-gray text-sm">
					<text class="title padding-right-xs" >{{$i18nMy.t('发件人：')}}</text>{{mailBox.sender.name}}
				</view>
				<view class=" text-gray text-sm">
					<text class="title  padding-right-xs" >{{$i18nMy.t('收件人：')}}</text>{{mailBox.receiverNames}}
				</view>
				<view class=" text-gray text-sm">
					<text class="title  padding-right-xs" >{{$i18nMy.t('时间：')}}</text>{{mailBox.sendtime}}
				</view>			
			</view>
		</view>

		<view class="padding bg-white">
			<view class="text-left padding">
				<view v-html="mailBox.mail.content"></view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		data() {
			return {
				mailBox: {
					mail: {
						title: ''
					},
					sender: {
						name: ''
					}
				}
			}
		},
		onLoad: function (option) {
		    this.$http.get('/app/mailBox/queryById?id='+option.id).then(({data})=>{
				this.mailBox = data.mailBox
			});
		},
		methods: {
			
		}
	}
</script>

<style>
  .mail .title {
  	min-width: calc(4em + 0px);
	text-align: right; 
	display: inline-block
  }
</style>
