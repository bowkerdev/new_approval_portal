<template>
	<view class="edit-form-popup-mask" v-show="show">
		<view class="edit-form-popup">
			<view class="edit-form-title">
				<view class="popup-btn confirm-btn" @click="confirm">{{$i18nMy.t('确定')}}</view>
				<view class="popup-title">{{title}}</view>
				<view class="popup-btn close-btn" @click="hide">{{$i18nMy.t('取消')}}</view>
			</view>
			<view class="edit-form">
				<view class="form-item" v-for="fieldItem in item.tableColumns" :key="fieldItem.model">
					<view class="label-key">{{ $i18nMy.t(fieldItem.name) }}</view>
					<template v-if="fieldItem.type=='input'">
						<input class="input-wrap" v-model="editObj[fieldItem.model]" :placeholder="$i18nMy.t('请输入')"/>
					</template>
					<!-- 选择框 -->
					<template v-if="fieldItem.type=='select'  && ('hidden' in fieldItem.options) && !fieldItem.options.hidden">
						<jp-picker class="input-wrap" v-if="fieldItem.options.remote === 3" 
							v-model="editObj[fieldItem['model']]" 
							:range="$dictUtils.getDictList(fieldItem.options.dictType)"
						/>
						<jp-picker class="input-wrap" v-else-if="fieldItem.options.showLabel" 
							v-model="editObj[fieldItem['model']]"
							:range="fieldItem.options.options"
						/>
						<jp-picker class="input-wrap" v-else v-model="editObj[fieldItem['model']]" 
							rangeKey="value" 
							:range="fieldItem.options.options"
						/>
					</template>	
					<template v-if="fieldItem.type=='text'">
						<input class="input-wrap" disabled :placeholder="$i18nMy.t('文本，不可编辑')"/>
					</template>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	export default {
		name: 'TableRowEditForm',
		data() {
			return {
				editObj: {},
				show: false,
				mode: '',
				title: '',
				
				item: {},
				staticObj: {}
			}
		},
		methods: {
			init(mode, editObj, item) {
				this.mode = mode;
				this.editObj = editObj;
				this.staticObj = JSON.parse(JSON.stringify(editObj))
				this.item = item || {}
				this.title = this.$i18nMy.t(mode=='add'? '新增': '修改')
				
				this.show = true
			},
			hide() {
				var thisVue = this;
				Object.keys(this.editObj).forEach(key => {
					thisVue.editObj[key] = thisVue.staticObj[key]
				})
				this.show = false
			},
			confirm() {
				if (this.mode === 'add') {
					this.item.value.push(this.editObj)
					this.$forceUpdate()
				}
				this.show = false
			}
		}
	}
</script>

<style scoped>
	.edit-form-popup-mask {
		position: fixed;
		bottom: 0;
		right: 0;
		top: 0;
		left: 0;
		z-index: 998;
		background-color: rgba(0, 0, 0, .7);
	}
	.edit-form-popup {
		padding: 10px;
		max-height: 80vh;
		position: absolute;
		bottom: 0;
		right: 0;
		left: 0;
		display: flex;
		flex-direction: column;
		background-color: #fff;
	}
	.edit-form-title {
		padding-bottom: 10px;
		display: flex;
		justify-content: space-between;
		align-items: center;
		border-bottom: 1px solid #eee;
	}
	.edit-form {
		padding-top: 10px;
		padding-bottom: 10px;
		flex-grow: 1;
		overflow-y: auto;
	}
	.popup-btn {
		padding: 10px;
	}
	.form-item {
		padding: 10px 0;
		display: flex;
		justify-content: flex-start;
	}
	.form-item + .form-item  {
		border-top: 1px solid #eee;
	}
	.label-key {
		padding-right: 10px;
		color: #a8a8a8;
		font-size: 26upx;
	}
	.input-wrap {
		flex-grow: 1;
		text-align: right;
		font-size: 24upx;
		color: #000;
	}
	.popup-btn {
		font-weight: bold;
	}
	.popup-title {
		font-weight: bold;
	}
	.popup-btn.confirm-btn {
		color: #0081FF;
	}
	.popup-btn.close-btn {
		color: #b5b5b0;
	}
</style>

