<template>
	<view class="content" style="height: auto;">
		<view class="card content-card">
			<form @submit="save">
				<view class="cu-form-group">
					<view class="title">户主姓名：</view>
					<input :disabled="isEdit" placeholder="请填写户主姓名" name="mobile" v-model="inputForm.hu"  ></input>
				</view>
				<view class="cu-form-group">
					<view class="title">电话号码：</view>
					<input placeholder="请填写电话号码" name="mobile" v-model="inputForm.phone"  ></input>
				</view>
				<view class="cu-form-group">
					<view class="title">身份证号：</view>
					<input placeholder="请填写电话号码" name="mobile" v-model="inputForm.idno"  ></input>
				</view>
				<view class="cu-form-group">
					<view class="title">开户行：</view>
					<input placeholder="请填写电话号码" name="mobile" v-model="inputForm.bank"  ></input>
				</view>
				<view class="cu-form-group">
					<view class="title">银行卡号：</view>
					<input placeholder="请填写电话号码" name="mobile" v-model="inputForm.bankno"  ></input>
				</view>
				<view class="cu-form-group">
					<view class="title">房屋面积：</view>
					<input placeholder="请填写房屋面积" name="mobile" v-model="inputForm.mianji"  ></input>
					<view class="cu-capsule radius">
						<view class="line-blue">
							㎡
						</view>
					</view>
				</view>
				<view class="cu-form-group" style="display: none;">
					<view class="title">农户属性：</view>
					<input placeholder="请填写农户属性" name="mobile" v-model="inputForm.attr"  ></input>
				</view>
				<view class="cu-form-group" style="display: none;">
					<view class="title">改造方式：</view>
					<input placeholder="请填写改造方式" name="mobile" v-model="inputForm.changeWay"  ></input>
				</view>
				<view class="cu-bar">
					<view class="action">
						改造前风貌图片上传
					</view>
				</view>
				<view class="cu-form-group">
					<view class="grid col-12 grid-square" >
						<sunui-upcos :upload_count="1" :upimg_preview="preViewBefore"
						:url="upImgCos.upImgUrl"  upload_img_wh="width:250px;height:140px;"
						@change="upCosDataBefore"  ref="uImageBefore">
						</sunui-upcos>
					</view>
				</view>	
				<view class="cu-bar">
					<view class="action">
						改造后风貌图片上传
					</view>
				</view>
				<view class="cu-form-group">
					<view class="grid col-12 grid-square" >
						<sunui-upcos :upload_count="1" :upimg_preview="preViewAfter" 
						:url="upImgCos.upImgUrl"  upload_img_wh="width:250px;height:140px;"
						@change="upCosDataAfter"  ref="uImageAfter">
						</sunui-upcos>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="grid col-12 grid-square" >
						<sunui-upcos :upload_count="1" :upimg_preview="preViewAfter2" 
						:url="upImgCos.upImgUrl"  upload_img_wh="width:250px;height:140px;"
						@change="upCosDataAfter2"  ref="uImageAfter2">
						</sunui-upcos>
					</view>
				</view>
				<view class="cu-form-group">
					<view class="title">提交人：</view>
					<input placeholder="请填写提交人姓名" name="mobile" v-model="inputForm.submitter"  ></input>
				</view>
				<view class="cu-form-group">
					<view class="title">审核人：</view>
					<input placeholder="请填写审核人姓名" name="mobile" v-model="inputForm.approver"  ></input>
				</view>
				<view class="cu-form-group">
					<view class="title">联系电话：</view>
					<input placeholder="请填写提交人联系电话" name="mobile" v-model="inputForm.submitterPhone"  ></input>
				</view>
				<view class="cu-form-group">
					<view class="title">备注：</view>
					<textarea placeholder="请填写备注" name="remarks" v-model="inputForm.remarks"  ></textarea>
				</view>
				<view class="uni-btn-v cn-form-button" >
					<button class="button" type="default"  hover-class="hover" formType="submit" >提&nbsp;&nbsp;&nbsp;&nbsp;交</button>
				</view>
				<view class="cu-form-group" style="min-height: 50rpx;border: 0px !important;"></view>
			</form>
		</view>
	</view>
</template>

<script>
	import uniTable from '@/components/uni-table/uni-table.vue';
	import uniTr from '@/components/uni-tr/uni-tr.vue';
	import uniTh from '@/components/uni-th/uni-th.vue';
	import uniTd from '@/components/uni-td/uni-td.vue';
	import Vue from 'vue';
	import sunUiCos from '@/components/sunui-upimg/sunui-upimg.vue';	
	export default {
		components: {
		  uniTable,
		  uniTr,
		  uniTh,
		  uniTd,
		  'sunui-upcos': sunUiCos
		},
		data() {
			return {
				isEdit:false,
				inputForm:{
					hu:'黎树荣',
					phone:'',
					mianji:'',
					attr:'',
					changeWay:'',
					before:'',
					after:'',
					after2:'', 
					idno:'',
					bank:'',
					bankno:'',
					submitter:'',
					submitterPhone:'',
					approver:'',
					remarks:''
				},
				preViewBefore:[], // 如果需填图片的默认值，请填这里
				preViewAfter:[],
				preViewAfter2:[],
				upImgCos: {// /digital 
					upImgUrl:(getApp().globalData.apiUrl+'api/file/obs/uploadObsFile')
				},
			}
		},
		onLoad(e) {
			var _that=this;
			let farmerHu = uni.getStorageSync("farmerHu");
			if(null != farmerHu){
				_that.inputForm = farmerHu;
				uni.removeStorageSync("farmerHu");
			}
			_that.isEdit = _that.inputForm.hu !== '';
			_that.setTitle();
			setTimeout(function(){
				_that.$refs["uImageBefore"].setHeader({token:getApp().globalData.token.token})
				if(_that.inputForm.before!=""&&_that.inputForm.before!=null){
					_that.$refs["uImageBefore"].setDefaut(_that.inputForm.before);
				}
				_that.$refs["uImageAfter"].setHeader({token:getApp().globalData.token.token})
				if(_that.inputForm.after!=""&&_that.inputForm.after!=null){
					_that.$refs["uImageAfter"].setDefaut(_that.inputForm.after);
				}
				_that.$refs["uImageAfter2"].setHeader({token:getApp().globalData.token.token})
				if(_that.inputForm.after2!=""&&_that.inputForm.after2!=null){
					_that.$refs["uImageAfter2"].setDefaut(_that.inputForm.after2);
				}
			},500)

		},
		onShow(){
			this.setTitle();
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
					title:(_that.inputForm.hu || '农户')+'改造情况填报'
				});
			},
			upCosDataAfter(e){
				if(e.length > 0){
					this.inputForm.after  =e[0].url
				}
				else{
					this.inputForm.after = "";
				}
			},
			upCosDataAfter2(e){
				if(e.length > 0){
					this.inputForm.after2  =e[0].url
				}
				else{
					this.inputForm.after2 = "";
				}
			},
			upCosDataBefore(e){
				if(e.length > 0){
					this.inputForm.before  = e[0].url
				}
				else{
					this.inputForm.before = "";
				}
			},
			save(){
				var _that=this;
				var tmp=JSON.parse(JSON.stringify(_that.inputForm))
				tmp.createBy=null;tmp.updateBy=null;
				_that.$http.post('/dg/dgNongfang/save',tmp)
				.then(function(res) {
					console.log(res);
					uni.showModal({
						title:"系统提示",
						content:"保存成功！",
						showCancel:false,
						success: (res) => {
							uni.navigateBack();
						}
					})
				}).catch(function(err) {
					// TODO
					console.log(err);
					uni.showModal({
						title:"系统提示",
						content:"保存失败！错误信息："+err.msg,
						showCancel:false,
						success: (res) => {
					
						}
					})
				});
			},
		}
	}
</script>

<style>
	/deep/ .sunsin_width {
		width: 400upx !important;
		height: 110upx !important;
		line-height: 160upx !important;
	}
	.cu-form-group input{
		text-align: right;
		color:#fff;
	}
	.title{
		width: 110px;
	}
	.button{
		margin-top: 40upx;
		background-color: rgb(81, 180, 241);
		color: #ffffff;
		margin-right:40rpx;
		margin-left:40rpx;
		width: calc(100% - 80rpx);
	}

	html,
	body,
	uni-page-body,
	.card,
	.cu-form-group,
	.cn-form-button,
	.cu-bar{
		background-color: #192239;
		color:#fff
	}
	.cu-bar,
	.cn-form-button,
	.cu-form-group+.cu-form-group{
		border-top: 0.5px solid rgb(71, 156, 231);
	}
</style>
