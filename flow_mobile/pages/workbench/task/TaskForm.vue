<template>
	<view>
		<cu-custom bgColor="bg-blue" :isBack="true" >
			<block slot="backText" @click="returnPrev()">{{$i18nMy.t('返回')}}</block>
			<block slot="content">{{title}}</block>
		</cu-custom>
		<!-- 菜单 -->
		<view class="bg-white nav fixed flex text-center" :style="[{top:CustomBar + 'px'}]">
			<view class="cu-item flex-sub" :class="0==tabIndex?'text-blue cur':''" @tap="tabSelect" data-id="0">
				{{$i18nMy.t('表单信息')}}
			</view>
			<view class="cu-item flex-sub"  v-if="procInsId" :class="2==tabIndex?'text-blue cur':''" @tap="tabSelect" data-id="2">
				{{$i18nMy.t('流转记录')}}
			</view>
			<view class="cu-item flex-sub" :class="1==tabIndex?'text-blue cur':''" @tap="tabSelect" data-id="1">
				{{$i18nMy.t('流程图')}}
			</view>
		</view>
		<view v-show="0 === tabIndex">
			<scroll-view scroll-y class="page">
			<view class="margin-top" :class="notBackgroundColor?'':' bg-white '">
				  <component :formReadOnly="formReadOnly" :class="formReadOnly?'readonly':''"  ref="form" :businessId="businessId" :is="form"></component>
				  <PreviewForm :formData="formData"  v-if="formType !== '2'"  
						:processDefinitionId="procDefId" :procDefKey="procDefKey" 
						:edit="true" ref="form"
					></PreviewForm>
			</view>
			<view class=" bg-white margin-top" v-if="!procInsId || taskId">
				<form @submit="formSubmit">
				<view class="cu-list menu">
					<!-- <view class="cu-form-group" v-if="!procInsId" >
						<view class="title">
							<text class="red-color">* </text> {{$i18nMy.t('标题')}}
						</view>
						<input :placeholder="$i18nMy.t('请输入流程标题')" maxlength="200" v-model="title" name="title"></input>
					</view> -->
					<view class="cu-form-group"  v-if="taskId"> 
						<view style="width: 100%;height: 100%;">
							<view class="form-textarea-title wd-100">{{$i18nMy.t('审批意见')}}</view>
							<view>
								<textarea class="form-textarea-content" style="background-color: #fff4f4;" maxlength="1000"  v-model="auditForm.message" name="message" :placeholder="$i18nMy.t('请输入审批意见')"></textarea>
							</view>
						</view>
					</view>
					<!-- <view class="cu-form-group">
						<view class="title">{{$i18nMy.t('是否抄送')}}</view>
						<jp-switch v-model="isCC" ></jp-switch>
					</view>
					<view class="cu-form-group"  v-if="isCC">
						<view class="title">{{$i18nMy.t('抄送给')}}</view>
						<user-select  v-model="auditForm.userIds" ></user-select>
					</view>
					<view class="cu-form-group">
						<view class="title">{{$i18nMy.t('指定下一步处理者')}}</view>
						<jp-switch v-model="isAssign"></jp-switch>
					</view>
					<view class="cu-form-group"  v-if="isAssign">
						<view class="title">{{$i18nMy.t('指定')}}</view>
						<user-select v-model="auditForm.assignee"  ></user-select>
					</view> -->
						<user-select-dialog title="选择转办用户" ref="transferUserSelectDialog" :showRadio="true" :showCheckBox="false" @doSubmit="selectUsersToTransferTask"></user-select-dialog>
						<user-select-dialog title="选择委派用户" ref="delegateUserSelectDialog" :showRadio="true" :showCheckBox="false" @doSubmit="selectUsersToDelateTask"></user-select-dialog>
						<user-select-dialog title="选择加签用户" ref="addSignTaskUserSelectDialog"  @doSubmit="selectUsersToAddSignTask"></user-select-dialog>
						<task-back-nodes ref="taskBackNodes" @getBackTaskDefKey="back"></task-back-nodes>
					</view>
					
					<view class="bottom-wrap flex">
						<view class="flex-sub"  v-show="button.isHide === '0'"
						 v-for="(button, index) in buttons" :key="index" >
							<button style="height: 50px; line-height: 1.2; font-size:14px;"
							class="block buttonBox flex_align_certer flex_vlign_certer" @click="submit(button, buttons)">{{button.name}}</button>
						</view>
					</view>
				</form>
			</view>
			<view class="cu-tabbar-height"></view>
			</scroll-view>
		</view>
		<view v-show="1 === tabIndex">
			<view class="padding bg-white margin-top">
				<flow-chart ref="chart1" v-if="procInsId" :processInstanceId="procInsId" />
				<flow-chart ref="chart2" v-if="!procInsId" :processDefId="procDefId" />
			</view>
		</view>
		<view v-show="2 === tabIndex">
			<view class="padding bg-white margin-top">
				<view class="cu-timeline" :key="index" v-for="(act, index) in historicTaskList">
					<view class="cu-time">{{act.histIns.startTime |formatDate('MM-DD')}}</view>
					<view class="cu-item text-gray">
						<view class="content">
							<view class="cu-capsule radius">
								<view class="cu-tag bg-grey">{{act.histIns.activityName}}</view>
								<!-- <view class="cu-tag line-cyan">{{act.histIns.activityName}}</view> -->
							</view>
							
							<view class="margin-top">
								{{$i18nMy.t('审批人')}} ：{{act.assigneeName}}
							</view>
							<view class="margin-top">
								{{$i18nMy.t('办理状态')}} ：<font style="font-weight: bold; color: #0081FF;">{{$i18nMy.t(act.comment.status)}}</font> 
							</view>
							<view class="margin-top">
								{{$i18nMy.t('审批意见')}} ： {{act.comment.message}}
							</view>
							<view class="margin-top">
								{{$i18nMy.t('开始时间')}} : {{act.histIns.startTime |formatDate}}
							</view>
							<view class="margin-top">
								{{$i18nMy.t('结束时间')}} : {{act.histIns.endTime |formatDate}}
							</view>
							<view class="margin-top">
								{{$i18nMy.t('任务历时')}} : {{act.durationTime || '0 s'}}
							</view>
						</view>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
	/* import userSelect from '@/components/user-select/user-select.vue' */
	import userSelectDialog from '@/components/user-select/user-select-dialog.vue'
	import PreviewForm from '../form/GenerateFlowableForm'
	import TaskBackNodes from './components/TaskBackNodes.vue'
	import TestActivitiLeaveForm from '@/pages/test/activiti/TestActivitiLeaveForm.vue'
	import PrAppForm from '@/pages/workbench/form/PrAppForm'
	import moment from 'moment'
	var  graceChecker = require("@/common/graceChecker.js");
	
	import mixFlow from '@/mixins/flowable.js'
	import { original_key } from '@/mixins/flowable.js'
	
	export default {
		mixins: [mixFlow],
		onLoad: function (option) {
		    this.flow = JSON.parse(decodeURIComponent(option.flow));
			this.procDefId = this.flow.procDefId
			this.procDefKey = this.flow.procDefKey
			this.formType = this.flow.formType
			this.formUrl = this.flow.formUrl
			this.taskId = this.flow.taskId
			this.taskDefKey = this.flow.taskDefKey
			this.status = this.flow.status
			this.title = this.flow.formTitle
			this.businessId = this.flow.businessId
			this.procInsId = this.flow.procInsId
			this.formReadOnly = this.flow.formReadOnly !== undefined && this.flow.formReadOnly !== 'false' && this.flow.formReadOnly !== false
			this.isCC = false
			this.isAssign = false
			this.auditForm.assignee = null
			this.auditForm.userIds = null
			this.auditForm.message = ''
		},
		async mounted () {
			  if (this.formType === '2') { //外置表单
				if (this.formUrl === '/404') {
				  this.form = null
				  uni.showToast({ title: '没有关联流程表单!', icon: "none" });
				} else {
				  // uniapp 不支持动态组件，所以通过名称匹配决定调用的表单组件
				  if(this.formUrl.indexOf("OaPrNewForm")>-1 || this.formUrl.indexOf("/oa/pr/form")>-1){
					  this.notBackgroundColor = true;
					  this.formReadOnly = true;
					  this.form = PrAppForm;
				  } else {
					  uni.showToast({ title: '没有关联流程表单!', icon: "none" });
				  }
				    
				}
			  } else { // 动态表单
			  // 读取流程表单
				if (this.formUrl === '/404') {
				   uni.showToast({ title: '没有关联流程表单!', icon: "none" });
				} else {
				  let {data} = await this.$http.get('/app/flowable/form/getMobileForm?formDefinitionJsonId='+this.formUrl);
				 // 初始化动态表单
				  data.formStructure.forEach((item)=>{ //挂载 writable，readable,value 属性，是为了触发对这三个属性的监听
					  item.writable = true
					  item.readable = true
					  if(this.isObjectValue(item)){
						  item.value = null
					  }else{
						  item.value = ''
					  }
					  let input = JSON.parse(JSON.stringify(item))
					  this.formData.push(input)
				  })
				  if (this.status === 'start') {
				    // 读取启动表单配置
				    let {data} = await this.$http.get('/app/flowable/form/getStartFormData',{processDefinitionId: this.procDefId})
				    this.setData(data.startFormData, 'start')
				  } else {
				    // 读取任务表单配置
				    let {data} = await this.$http.get('/app/flowable/form/getTaskFormData',{taskId: this.taskId})
				    this.setData(data.taskFormData, 'audit')
				  }
				}
			  }
			   // 读取按钮配置
			  if (this.status === 'start') {
				this.buttons = [{code: '_flow_start', name: $i18nMy.t('启动'), isHide: '0'}]
			  } else if (this.procDefKey && this.taskDefKey) {
				// 读取按钮
				this.$http.get('/app/extension/taskDefExtension/queryByDefIdAndTaskId',  {
				  processDefId: this.procDefKey,
				  taskDefId: this.taskDefKey
				}).then(({data}) => {
				  if (data.success) {
					this.buttons = data.taskDefExtension.flowButtonList 
					var deleteIdx = -1
					for(var i=0;i<this.buttons.length;i++){
						if (this.buttons[i].code=="_flow_export") { //app暂不支持导出
							deleteIdx = i
						}else{
							this.buttons[i].name= $i18nMy.t(this.buttons[i].name)
						}
					}
					if (deleteIdx >= 0) {
						this.buttons.splice(deleteIdx,1)
					}
				  }
				})
			  }
			// 读取历史任务列表
			  this.$http.get(`/app/flowable/task/historicTaskList?procInsId=${this.procInsId}`).then(({data}) => {
				this.historicTaskList = data.historicTaskList.reverse()
			  })
		},
		components:{
		  //userSelect,
		  userSelectDialog,
		  TestActivitiLeaveForm,
		  PrAppForm,
		  TaskBackNodes,
		  PreviewForm
		},
		onBackPress(options) {
			console.log("onBackPress:"+options.from)			
		    this.returnPrev();
		    return true;
		  },
		data() {
			return {
				notBackgroundColor:false,
				flow: null,
				tabIndex: 0,
				form: null,
				formType: '',
				formUrl: '',
				taskSelectedTab: 'frist',
				historicTaskList: [],
				procDefId: '',
				procInsId: '',
				formReadOnly: false,
				procDefKey: '',
				taskId: '',
				formData: [],
				taskDefKey: '',
				status: '',
				title: '',
				businessId: '',
				buttons: [],
				isCC: false,
				isAssign: false,
				auditForm: {
				  message: '',
				  type: '',
				  status: '',
				  userIds: null,
				  assignee: null
				}
			}
		},
		methods:{
			returnPrev(){
				console.log("returnPrev");
				uni.redirectTo({
				    url: '/pages/workbench/task/TodoList'
				});
			},
			tabSelect (e) {
				  if (this.procInsId) {
					this.$refs.chart1.init()
				  } else {
					this.$refs.chart2.init()
				  }
				this.tabIndex = parseInt(e.currentTarget.dataset.id);
			},
			// 为任务表单赋值
			setData (taskFormData, status) {
				this.formData.forEach((input)=>{
					let item = taskFormData.filter((item)=>{
						if(input.model === item.id){
							return true
						}else{
							return false
						}
					})[0]
					if(item){
						if(status === 'start'){
							this.isFixParam(input)
							if(this.isObjectValue(input)){
								  if(input.options.defaultValue && typeof input.options.defaultValue=== 'string'){
									  input.value =  JSON.parse(input.options.defaultValue)
								  }else{
									  input.value =  input.options.defaultValue
								  } 
							}else{
								 let value = input.options.defaultValue || ''
								  // 时间戳转换成格式化时间显示
								 input.value = this.mix_transfterVal(input['model'], value)
								 
							}
						}else{
							if(this.isObjectValue(input)){
								  if(item.value && typeof item.value=== 'string'){
									   input.value =  JSON.parse(item.value)
								   }else {
									   input.value = item.value
								   }
							   }else{
									// 时间戳转换成格式化时间显示
									input.value = this.mix_transfterVal(input['model'], item.value)
							  }
						}
						
						input.readable = item.readable
						input.writable = item.writable
					}else{
						input.readable = false
					}
				})
			},
			// 默认参数赋值替换 ${user.name}...
			isFixParam (input) {
			   if(/^[$][{].*[}]$/.test(input.options.defaultValue)){
				  let params = input.options.defaultValue.substring(2, input.options.defaultValue.length-1)
				  if(params === "new Date()"){
					input.options.defaultValue = moment(new Date()).format('YYYY-MM-DD HH:mm:ss')
				  }else{
					let value = {user: this.$store.state.user.userInfo}
					params.split('.').forEach((param)=>{
					  value = value?.[param]
					})
					input.options.defaultValue = value
				  }
				}
			},
			// 判断数据类型是否是非String类型
			isObjectValue (input) {
				if(input.type === 'checkbox' ||
				   input.type === 'slider' ||
				   input.type === 'switch' ||
				   input.type === 'rate' ||
				   input.type === 'imgupload' ||
				   input.type === 'select' && input.options.multiple ||
				   input.type === 'fileupload' ||
				   input.type === 'table'){
					   return true
				  }
				  return false
			},
			// 抄送
			cc (data) {
				if (this.isCC && this.auditForm.userIds) {
					  this.$http.post('/app/extension/flowCopy/save', {
						userIds: this.auditForm.userIds,
						procDefId: this.procDefId,
						procInsId: data.procInsId,
						procDefName: '',
						procInsName: this.title,
						taskName: ''
					  })
				}
			  },
			// 暂存草稿
			 save () {
	  
			 },
			// 启动流程
			start (vars) {
			  if (this.formType === '2') { // 外置表单启动
				this.$refs.form.saveForm((businessTable, businessId) => {
				  this.$http.post('/app/flowable/task/start', {
					procDefKey: this.procDefKey,
					businessTable: businessTable,
					businessId: businessId,
					...vars,
					title: this.title,
					assignee: this.auditForm.assignee
				  }).then(({data}) => {
					if (data.success) {
					  uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
					  setTimeout(function(){
					  	uni.navigateTo({
					  		url: '/pages/workbench/task/TodoList'
					  	})
					  },1000)
					  this.cc(data)
					}
				  })
				})
			  } else { //动态表单启动
				this.$refs.form.submitStartFormData({
				  processDefinitionId: this.procDefId,
				  ...vars,
				  title: this.title,
				  assignee: this.auditForm.assignee
				}, (data) => {
				  if (data.success) {
					uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
					setTimeout(function(){
						uni.navigateTo({
							url: '/pages/workbench/task/TodoList'
						})
					},1000)
					this.cc(data)
				  }
				})
			  }
			},
			// 同意
			agree (vars) {
			  this.commit(vars) // 同意
			},
			// 驳回
			reject () {
				uni.showLoading()
				uni.showModal({
				    title: '提示',
				    content: '确定驳回流程吗？',
				    success: (res) => {
				        if (res.confirm) {
				            this.$http.post('/flowable/task/backNodes', {
				              taskId: this.taskId,
				              ...this.auditForm}).then(({data}) => {
				            	let backNodes = data.backNodes
				            	if (backNodes.length > 0) {
				            	  let backTaskDefKey = backNodes[backNodes.length - 1].taskDefKey
				            	  this.back(backTaskDefKey)
				            	}
				             })
				        } else if (res.cancel) {
				           uni.hideLoading()
				        }
				    }
				});
			},
			// 回退到发起人
			backToModify () {
			  this.$http.post('/flowable/task/back', {
			    taskId: this.taskId,
			    backTaskDefKey: 'FormModify',
			    ...this.auditForm
			  }).then(({data}) => {
			    if (data.success) {
			      uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
			      setTimeout(function(){
			      	uni.navigateTo({
			      		url: '/pages/workbench/task/TodoList'
			      	})
			      },1000)
			      this.cc(data)
			    }
			  })
			},
			// 补充资料
			backToDocAdd () {
			    this.$http.post('/flowable/task/back', {
			      taskId: this.taskId,
			      backTaskDefKey: 'DocAdd',
			      ...this.auditForm
			    }).then(({data}) => {
			      if (data.success) {
			        uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
			        setTimeout(function(){
			        	uni.navigateTo({
			        		url: '/pages/workbench/task/TodoList'
			        	})
			        },1000)
			        this.cc(data)
			      }
			    })
			},
			// 驳回到任意节点
			turnBack () {
			  this.$refs.taskBackNodes.init(this.taskId)
			},
			// 回退到任意节点
			back (backTaskDefKey) {
			  this.$http.post('/flowable/task/back', {
				taskId: this.taskId,
				backTaskDefKey: backTaskDefKey,
				...this.auditForm
			  }).then(({data}) => {
				if (data.success) {
				  uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
				  uni.navigateTo({
				  	url: '/pages/workbench/task/TodoList'
				  })
				  this.cc(data)
				}
			  })
			},
			// 加签
			addMultiInstance () {
			  // this.$refs.addSignTaskUserSelectDialog.showModal()
			},
			selectUsersToAddSignTask (users) {
			  let userIds = users.map((user) => {
				return user.id
			  }).join(',')
			  this.$http.post('/flowable/task/addSignTask', {taskId: this.taskId, userIds: JSON.stringify(userIds), message: '', flag: false}).then(({data}) => {
				uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
			  })
			},
			// 减签
			delMultiInstance () {
	  
			},
			// 转办
			transfer () {
			  this.$refs.transferUserSelectDialog.showModal()
			},
			selectUsersToTransferTask (userId) {
			  if(!userId){
				  uni.showToast({ title: '没有选择任何用户!', icon: "none" });
				  return
			  }
			  this.$http.post('/flowable/task/transfer', {taskId: this.taskId, userId: userId}).then(({data}) => {
				uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
				setTimeout(function(){
					uni.navigateTo({
						url: '/pages/workbench/task/TodoList'
					})
				},1000)
			  })
			},
			// 委托
			delegate () {
			  this.$refs.delegateUserSelectDialog.showModal()
			},
			selectUsersToDelateTask (userId) {
			  if(!userId){
				  uni.showToast({ title: '没有选择任何用户!', icon: "none" });
				  return
			  }
			  this.$http.post('/flowable/task/delegate', {taskId: this.taskId, userId: userId}).then(({data}) => {
				uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
				setTimeout(function(){
					uni.navigateTo({
						url: '/pages/workbench/task/TodoList'
					})
				},1000)
			  })
			},
			// 终止
			stop () {
				uni.showLoading()
				uni.showModal({
				    title: '提示',
				    content: '确定终止流程吗？',
				    success: (res) => {
				        if (res.confirm) {
				            this.$http.post('/flowable/process/stop', {id: this.procInsId, ...this.auditForm}).then(({data}) => {
				              uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
				              uni.navigateTo({
				              	url: '/pages/workbench/task/TodoList'
				              })
				            })
				        } else if (res.cancel) {
				           uni.hideLoading()
				        }
				    }
				});
			},
			// 打印
			print () {
	  
			},
			// 自定义按钮提交
			commit (vars) {
				//定义表单规则
				 var rule = [
					/* {name:"message", checkType : "notnull", checkRule:"",  errorMsg:"审批意见不能为空!"} */
				 ];
				 //进行表单检查
				 var formData = this.auditForm;
				 var checkRes = graceChecker.check(formData, rule);
				 if(!checkRes){
				  uni.showToast({ title: graceChecker.error, icon: "none" });
				  return;
				}
				 if (this.formType === '2') { //外置表单审批
				  //this.$refs.form.saveForm((businessTable, businessId) => {
					  this.$http.post('/app/flowable/task/audit', {
						taskId: this.taskId,
						taskDefKey: this.taskDefKey,
						procInsId: this.procInsId,
						procDefId: this.procDefId,
						vars: vars,
						comment: this.auditForm,
						assignee: this.auditForm.assignee
					  }).then(({data}) => {
						if (data.success) {
							uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" }); 
							setTimeout(function(){
								uni.navigateTo({
									url: '/pages/workbench/task/TodoList'
								})
							},1000)
							this.cc(data)
						}
					  })
				//})
				} else { // 动态表单启动
					this.$refs.form.submitTaskFormData(vars, this.procInsId, this.taskId, this.auditForm.assignee, this.auditForm, (data) => {
					  if (data.success) {
						uni.showToast({ title: $i18nMy.t('处理成功'), icon: "success" });
						setTimeout(function(){
							uni.navigateTo({
								url: '/pages/workbench/task/TodoList'
							})
						},1000)
						//抄送
						this.cc(data)
					  }
					})
				}
			},
			submit (currentBtn, buttons) {
			  let vars = {} // 存储流程变量
	  
			  // 把当前操作对应的自定义按钮(以_flow_开头的是系统按钮，排除在外）的编码，存储为对应的流程变量，值设置为true，其余自定义按钮编码对应的流程变量值为false。
			  buttons.forEach((btn) => {
				if (btn.code && !btn.code.startsWith('_flow_')) {
				  vars[btn.code] = false
				}
			  })
			  if (currentBtn.code && !currentBtn.code.startsWith('_flow_')) {
				vars[currentBtn.code] = true
			  }
			  vars.title = this.title // 标题
			  vars.assignee = this.auditForm.assignee // 指定的下一步骤处理人
			  this.auditForm.type = currentBtn.code // 提交类型
			  this.auditForm.status = currentBtn.name // 按钮文字
				var thisVue = this;
			  switch (currentBtn.code) {
				case '_flow_start': // 自动流程
				  this.start(vars)
				  break
				case '_flow_save': // 保存草稿
				  this.save()
				  break
				case '_flow_agree': // 同意
					uni.showModal({
						title: $i18nMy.t('确认同意？'),
						confirmText: $i18nMy.t('确定'),
						cancelText: $i18nMy.t('取消'),
						content: '',
						success: function (res) {
							if (res.confirm) {
								thisVue.agree(vars)
							} else if (res.cancel) {
							}
						}
					});
				  break
				case '_flow_reject': // 驳回
				  this.reject()
				  break
				case '_flow_back': // 驳回到任意步骤
				  this.turnBack()
				  break
				case '_flow_back_modify': // 驳回到申请人
				  this.backToModify()
				  break
				case '_flow_back_doc_add': // 资料补充
				  this.backToDocAdd()
				  break
				case '_flow_add_multi_instance': // 加签
				  this.addMultiInstance()
				  break
				case '_flow_del_multi_instance': // 减签
				  this.delMultiInstance()
				  break
				case '_flow_transfer': // 转办
				  this.transfer()
				  break
				case '_flow_delegate':// 外派
				  this.delegate()
				  break
				case '_flow_stop':// 终止
				  this.stop()
				  break
				case '_flow_print':// 打印
				  this.print()
				  break
				default:
					if (currentBtn.code == 'disagree') {
						uni.showModal({
							title: $i18nMy.t('确认不同意？'),
							confirmText: $i18nMy.t('确定'),
							cancelText: $i18nMy.t('取消'),
							content: '',
							success: function (res) {
								if (res.confirm) {
									thisVue.commit(vars)
								} else if (res.cancel) {
								}
							}
						});
					} else {
						this.commit(vars) // 自定义按钮提交
					}
					
				  
			  }
			}
		}
	}
</script>

<style scoped > 
	page {
		padding-top: 45px;
	}
	.content {
		font-size: 12px;
	}
	/* .cu-form-group .title {
		min-width: calc(4em + 40px);
	} */
	uni-view:nth-child(1n+1)>uni-button.buttonBox{
		background:#0081ff;
		color: #FFFFFF;
	}
	uni-view:nth-child(1n+2)>uni-button.buttonBox{
		background:#f37b1d;
		color: #FFFFFF;
	}
	uni-button:nth-child(1n+3).buttonBox{
		background:#8dc63f;
		color: #FFFFFF;
	}
	uni-button:nth-child(1n+4).buttonBox{
		background:#1cbbb4;
		color: #FFFFFF;
	}
	uni-button:nth-child(1n+5).buttonBox{
		background:#a5673f;
		color: #FFFFFF;
	}
	.margin-top {
	    margin-top: 10px;
	} 
	.form-textarea-title {
		font-size: 12px;
		margin-top: 15px;
		margin-bottom: -10px;
	}
	.form-textarea-title,
	.form-textarea-content{
		font-size: 26upx;
	}
	/deep/.uni-scroll-view-content{
	   transform:none !important;
	   transition:none !important;
	}
	
</style>
