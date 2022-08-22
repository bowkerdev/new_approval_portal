<template>
	<view>
		<form>
			<view class="cu-form-group"  :class="{'margin-top': index===0, 'flex-col': ['table'].indexOf(item.type) > -1,
				'html-group': item.type == 'html', 'table-type': item.type == 'table', 'textarea-group': item.type == 'textarea' }" 
				v-if="isShow(item)" v-for="(item,index) in formData" :key="index"
			>
				<view class="title" v-if="item.type == 'html'" >
					{{ $i18nMy.t(getHtmlText(item.options.defaultValue)) }}
				</view>
				<view class="title" v-if="item.options.hideLabel !== true">
					<text class="red-color " v-if="item.options.required">* </text> {{ $i18nMy.t(item.name)}}
				</view>
				<template v-if="item.type=='input' && ('hidden' in item.options) && !item.options.hidden">				
					<!-- 普通输入框 -->
					<input  class="cu-form-content"  :placeholder='item.placeholder' :disabled="!item.writable"  v-model="item.value"></input>
				</template>	
				<template v-if="(item.type=='textarea' || item.type=='editor')  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 多行文本输入框 -->
					<textarea maxlength="-1"  :placeholder='item.placeholder' :disabled="!item.writable"   v-model="item.value"></textarea>
				</template>	
				<template v-if="item.type=='number'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 普通输入框 -->
					<input type="number" :placeholder='item.placeholder' :disabled="!item.writable"   v-model="item.value"></input>
				</template>	
				<template v-if="item.type=='radio'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 单选框 -->
				
					<jp-radio-group  v-if="item.options.remote === 3"  v-model="item.value">
						<label v-for="(option, index) in $dictUtils.getDictList(item.options.dictType)">
							<view>
								{{option.label}}
								<radio class='blue radio' :disabled="!item.writable" :value="option.value"  :class="item.value==option.value?'checked':''" :checked="item.value==option.value?true:false" ></radio>
							</view>
						</label>
					</jp-radio-group>
					<jp-radio-group  v-else-if="item.options.showLabel"  v-model="item.value">
						<label v-for="(option, index) in item.options.options">
							<view>
								{{option.label}}
								<radio class='blue radio' :disabled="!item.writable" :value="option.value"  :class="item.value==option.value?'checked':''" :checked="item.value==option.value?true:false" ></radio>
							</view>
						</label>
					</jp-radio-group>
					<jp-radio-group  v-else  v-model="item.value">
						<label v-for="(option, index) in item.options.options">
							<view>
								{{option.value}}
								<radio class='blue radio' :disabled="!item.writable"  :value="option.value"  :checked="item.value==option.value?true:false" ></radio>
							</view>
						</label>
					</jp-radio-group>
				</template>	
				<template v-if="item.type=='checkbox'  && ('hidden' in item.options) && !item.options.hidden">
					<jp-checkbox-group v-if="item.options.remote === 3" v-model="item.value">
						<label v-for="(option, index) in $dictUtils.getDictList(item.options.dictType)">
							<view>
								{{option.label || option.value}}
								<checkbox :disabled="!item.writable"  class="blue" :value="option.value"  :class="(','+item.value+',').indexOf(','+option.value+',')>=0?'checked':''" :checked="(','+item.value+',').indexOf(','+option.value+',')>=0" ></checkbox>
							</view>
						</label>
					</jp-checkbox-group>
					
					<jp-checkbox-group v-else-if="item.options.showLabel" v-model="item.value">
						<label v-for="(option, index) in item.options.options">
							<view>
								{{option.label}}
								<checkbox :disabled="!item.writable"  class="blue" :value="option.value"  :class="(','+item.value+',').indexOf(','+option.value+',')>=0?'checked':''" :checked="(','+item.value+',').indexOf(','+option.value+',')>=0" ></checkbox>
							</view>
						</label>
					</jp-checkbox-group>
					<jp-checkbox-group v-else v-model="item.value">
						<label v-for="(option, index) in item.options.options">
							<view>
								{{option.value}}
								<checkbox  class="blue" :disabled="!item.writable"  :value="option.value"  :class="(','+item.value+',').indexOf(','+option.value+',')>=0?'checked':''" :checked="(','+item.value+',').indexOf(','+option.value+',')>=0" ></checkbox>
							</view>
						</label>
					</jp-checkbox-group>
				</template>	
				<template v-if="item.type=='time'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 时间控件 -->
					<jp-datetime-picker  v-model="item.value" :placeholder='item.placeholder' :disabled="!item.writable" mode="time"></jp-datetime-picker>
				</template>	
				<template v-if="item.type=='date'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 日期控件 -->
					<jp-datetime-picker  v-model="item.value" :placeholder='item.placeholder' :disabled="!item.writable" mode="date"></jp-datetime-picker>
				</template>	
				<template v-if="item.type=='rate'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 评分 -->
					<uni-rate :size="18":disabled="!item.writable" :allowHalf="item.options.allowHalf" v-model="item.value" :max="item.options.max"/>
				</template>	
				<template v-if="item.type=='color'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 颜色选择框 -->
					<jp-color-picker :disabled="!item.writable" v-model="item.value" ></jp-color-picker>
				</template>	
				<template v-if="item.type=='select'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 选择框 -->
					<jp-picker :disabled="!item.writable" v-if="item.options.remote === 3" v-model="item.value" :range="$dictUtils.getDictList(item.options.dictType)">
					</jp-picker>
					<jp-picker :disabled="!item.writable" v-else-if="item.options.showLabel" v-model="item.value" :range="item.options.options">
					</jp-picker>
					<jp-picker :disabled="!item.writable" v-else v-model="item.value" rangeKey="value" :range="item.options.options">
					</jp-picker>
				</template>	
				<template v-if="item.type=='switch'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 开关 -->
					<jp-switch :placeholder='item.placeholder' :disabled="!item.writable"   v-model="item.value"></jp-switch>
				</template>	
				<template v-if="item.type=='slider'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 滑块 -->
					<jp-slider  :placeholder='item.placeholder' :disabled="!item.writable"   v-model="item.value"></jp-slider>
				</template>	
				<template v-if="item.type=='text'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 普通输入框 -->
					<text>{{item.value}}</text>
				</template>
				<template v-if="(item.type=='imgupload' || item.type=='fileupload')  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 图片上传 -->
					<jp-image-upload :disabled="!item.writable"   v-model="item.value"></jp-image-upload>
				</template>	
				<template v-if="item.type=='user'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 用户选择框 -->
					<user-select  :disabled="!item.writable"   v-model="item.value"></user-select>
				</template>	
				<template v-if="item.type=='office'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 机构选择框 -->
					<jp-office-select :disabled="!item.writable"   v-model="item.value"></jp-office-select>
				</template>	
				<template v-if="item.type=='area'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 普通输入框 -->
					<jp-area-select :disabled="!item.writable"  v-model="item.value"></jp-area-select>
				</template>	
				<template v-if="item.type=='dict'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 字典 -->
					<jp-picker :disabled="!item.writable"  v-model="item.value" :range="$dictUtils.getDictList(item.options.dictType)"></jp-picker>
				</template>	
				<template v-if="item.type=='table'  && ('hidden' in item.options) && !item.options.hidden">
					<!-- 表格数据以对象形式展示 -->
					<view class="table-row-card-list">
						<view v-for="(tableRow, tableRowIndex) in item.value" :key="tableRowIndex" class="table-row-card-show">
							<view class="table-row-card-body">
								<view v-for="col in item.tableColumns" :key="col['model']" class="table-row-info-item">
									<view class="table-row-info-key">{{ $i18nMy.t(col['name']) }}</view>
									<view class="table-row-info-value">{{ tableRow[col['model']] || '-' }}</view>
								</view>
							</view>
							<view class="table-row-card-btns" v-if="item.writable">
								<view class="card-btn del-btn" 
									@click="removeTableRow(item.value, tableRowIndex)"
								>{{$i18nMy.t('删除')}}</view>
								<view class="card-btn modify-btn" @click="modifyTableRow(tableRow, item)">
									{{$i18nMy.t('修改')}}
								</view>
							</view>
						</view>
					</view>
					
					<view class="add-table-row-btn" @click="addTableRow(item.tableColumns, item)"
						 v-if="item.writable"
					>
						{{$i18nMy.t('新增')}}
					</view>
					
				</template>	
			</view>
		</form>
		<table-row-edit-form ref="tableRowEditForm" />
	</view>
</template>

<script>
	import TableRowEditForm from './components/table-row-edit.vue'
	import { getSqlDictList } from '@/api/dataset.js'
	
	export default {
		name: 'activeForm',
		components: { TableRowEditForm },
		data() {
			return {
				hasFetchDict: false
			}
		},
		watch:{
			formData:{
				handler (val) {
					console.log(this.formData)
					if (!(!this.hasFetchDict && val && val.length)) { return }
					// extra cut获取特殊reason,action,prevention选项值
					if(this.procDefKey == 'extra_cut_approval') {
						uni.showLoading({ mask: true })
						// key:接口数据对应的类型， value: 配置的model
						const match = { "qaReason": "reason", "qaAction": "action", "qaPrevention": "prevention" }
						const typeArr = Object.values(match)
						const list = {} // { reason: [], action: [], prevention: [] }
						typeArr.forEach(key => list[key] = [])
						// 找到目标配置
						const targetArr = this.formData.filter(item => item['model'] == 'qaRapList' && item['type'] == 'table')
						
						getSqlDictList('GET_EXTRA_CUT_QA_PULL_DOWN', {})
							.then(({data}) => {
								const { success, result } = data
								if (success && result && result.length) {
									// 分类存放查回来的值
									result.forEach(item => {
										const type = item['type']
										if (typeArr.indexOf(type) == -1) { return }
										list[type].push({ value: item['value'], label: item['value'] })
									})
								}
							})
							.catch(err => {
								console.log('GET_EXTRA_CUT_QA_PULL_DOWN err')
							})
							.finally(() => {
								uni.hideLoading()
								this.hasFetchDict = true
								this.resetSelectOption(match, targetArr, list)
							})

					}
				},
				immediate: true,
				deep: false
			}
		},
		props: {
			formData:{
				type:Array,
				default:function(){
					return []
				}
			},
			procDefKey: {
				type: String,
				default: undefined
			}
		},
		methods: {
			// 
			isShow(config) {
				const readable = config['readable']
				const model = config['model']
				// extra_cut_approval: qaRecordDataLabel,qaRapList仅在工厂为BCA的时候显示
				if (this.procDefKey == 'extra_cut_approval' && readable 
					&& ['qaRecordDataLabel', 'qaRapList'].indexOf(model) > -1
				) {
					// 获取工厂: applySiteCode
					const targetList = this.formData.filter(item => item['model'] === 'applySiteCode')
					const siteCode = targetList.length? targetList[0]['value']: undefined
					return siteCode === 'BCA'
				}
				return readable
			},
			// 重置下拉选项
			// match = { "qaReason": "reason", "qaAction": "action", "qaPrevention": "prevention" }
			// list = { reason: [], action: [], prevention: [] }
			resetSelectOption(match, targetArr, list) {
				// 替换选项
				if (!(targetArr && targetArr.length)) { return }
				const colArr = targetArr[0]['tableColumns']
				colArr.forEach(item => {
					if (!item['options']) { item['options'] = {} }
					const modelId = item['model'] //"qaReason" , "qaAction" , "qaPrevention"
					item['options']['options'] = list[match[modelId]]
				})
			},
			// table删除行
			removeTableRow(tableRowArr, tableRowIndex) {
				tableRowArr.splice(tableRowIndex, 1)
			},
			// 编辑表格行
			modifyTableRow(tableRow, item, mode) {
				this.$refs.tableRowEditForm.init('modify', tableRow, item)
			},
			addTableRow(config, item) {
				var tableRow = {}
				config.forEach(item => {
					tableRow[item['model']] = ''
				})
				this.$refs.tableRowEditForm.init('add', tableRow, item)
			},
			// 获取html
			getHtmlText(str) {
				str = str || ''
				return str.replace(/\<[^>]*\>(([^<])*)/g, function(reg, name) {
					return name;
				}).replace(/\r|\n/g, '').replace(/\s+/g, ' ')
			}
		}
	}
</script>

<style scoped>
	.cu-form-group input {
		flex: none;
		text-align: right;
		color: #000;
	}
	.cu-form-group .title {
		height: auto;
		line-height: 1.2;
		padding-top: 12px;
		padding-bottom: 12px; 
		font-size: 26upx;
		color: #a8a8a8; 
	}
	.cu-form-group.html-group.margin-top,
	.cu-form-group .title ,
	.cu-form-group .cu-form-content{
		font-size: 26upx;
	}
</style>

<style lang="scss" scoped>
.uni-list-cell {
    justify-content: flex-start
}
.cu-form-group{
	min-height: auto;
	padding-left: 10px;
	padding-right: 10px;
	
	&.html-group + & {
		border: none;
	}
	&.html-group.margin-top {
		padding-top: 20px;
		padding-bottom: 20px;
		display: flex;
		justify-content: center;
		align-items: center;
		font-size: 32upx;
	}
	&.html-group {
		padding-top: 16px;
		padding-bottom: 16px;
		font-size: 30upx;
		font-weight: bold;
	}
	
	&.table-type {
		padding-left: 0;
		padding-right: 0;
		background-color: transparent;
	}
	
	&.textarea-group {
		align-items: flex-start;
	}
  uni-checkbox-group{
		text-align: right;
	}
	uni-radio-group {
		text-align: right;
	}
	uni-checkbox, uni-radio {
		position: relative;
		margin-top: 7px;
		margin-left:7px;
		margin-bottom: 7px;
	}
	uni-textarea {
		margin-top: 12px;
		text-align: right;
	}
	
	&.flex-col {
		flex-direction: column;
		align-items: flex-start;
		width: 100%;
	}
	
	.table-row-card-list {
		width: 100%;
		border-top: 1px dashed #c4c4c4;
	}
	.table-row-card-show {
		margin-bottom: 10px;
		padding: 10px 0;
		border-bottom: 1px dashed #c4c4c4;
	}
	.table-row-card-body {
		padding-bottom: 10px;
	}
	.table-row-info-item {
		padding: 2px 10px;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}
	.table-row-info-key {
		color: #a8a8a8;
		font-size: 26upx;
	}
	.table-row-info-value {
		margin-top: 2px;
		max-width: 60%;
		text-align: right;
		color: #000;
		font-size: 24upx;
		flex-shrink: 0;
	}
	.table-row-card-btns {
		display: flex;
		justify-content: center;
		
		.card-btn {
			margin-top: 10px;
			flex-grow: 1;
			text-align: center;
			
			&.modify-btn {
				color: #E6A23C;
			}
			&.del-btn {
				color: #e6122e;
			}
		}
			
		.card-btn + .card-btn {
			border-left: 1px dashed #c4c4c4;
		}
	}
	.add-table-row-btn {
		padding: 10px;
		width: 100%;
		color: #0081FF;
		text-align: center;
		
	}
}
</style>