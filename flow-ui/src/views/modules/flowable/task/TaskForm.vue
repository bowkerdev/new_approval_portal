<template>
<div style="height: 100%;overflow-y: auto;overflow-x: hidden;">
  <h4 style="text-align:center">{{title}}</h4>

  <el-tabs type="border-card" v-model="taskSelectedTab">
    <el-tab-pane :label="$i18nMy.t('表单信息')" name="form-first">
      <component id="printForm" :formReadOnly="formReadOnly" v-if="formType === '2'" :class="formReadOnly?'readonly':''"  ref="form" :businessId="businessId" :is="form"></component>

      <PreviewForm  id="printForm"   v-if="formType !== '2'"  :processDefinitionId="procDefId" :edit="true" :taskFormData="taskFormData" ref="form"/>
    </el-tab-pane>
    <el-tab-pane :label="$i18nMy.t('流程信息')" v-if="procInsId" name="form-second">
      <flow-time-line :historicTaskList="historicTaskList"/>
    </el-tab-pane>
    <el-tab-pane :label="$i18nMy.t('流程图')"  v-if="procInsId" name="form-third">
       <el-card class="box-card"  shadow="hover">
          <div slot="header" class="clearfix">
            <span>{{$i18nMy.t('流程图')}}</span>
          </div>
          <flow-chart ref="chart1" v-if="procInsId" :processInstanceId="procInsId" />
          <flow-chart ref="chart2" v-if="!procInsId" :processDefId="procDefId" />
        </el-card>
    </el-tab-pane>
    <el-tab-pane :label="$i18nMy.t('流转记录')" v-if="procInsId" name="form-forth">
          <flow-step :historicTaskList="historicTaskList"/>
     </el-tab-pane>
  </el-tabs>



<el-card style="margin-top:10px; margin-bottom:66px" v-if="!procInsId || taskId">
    <el-form size="small" :model="auditForm"   ref="auditForm" label-width="120px">
      <el-col :span="16">
        <el-form-item  v-if="!procInsId"  :label="$i18nMy.t('流程标题')" prop="title" v-show="false">
          <el-input
            :placeholder="$i18nMy.t('请输入流程标题')"
            v-model="title">
          </el-input>
        </el-form-item>
      <el-form-item label-width="220px"  v-if="taskId"  :label="$i18nMy.t('审批信息')" prop="comment" >
          <el-input
            type="textarea"
            :rows="3"
            :placeholder="$i18nMy.t('请输入审批意见')"
            v-model="auditForm.message">
          </el-input>
      </el-form-item>
    </el-col>
    <el-col :span="16" v-show="false">
      <el-form-item>
        <el-checkbox v-model="isCC">{{$i18nMy.t('是否抄送')}}</el-checkbox>
      </el-form-item>
    </el-col>
    <el-col :span="16">
      <el-form-item v-if="isCC"  :rules="[
              {required: true, message: $i18nMy.t('用户不能为空'), trigger: 'blur'}
            ]"  prop="userIds" :label="$i18nMy.t('抄送给')">
            <user-select :value="auditForm.userIds"  @getValue='(value) => {auditForm.userIds=value}'>></user-select>
      </el-form-item>
    </el-col>
    <el-col :span="16">
      <el-form-item label-width="220px" style="margin-top: 30px;">
        <el-checkbox v-model="isAssign">{{$i18nMy.t('指定下一步处理者(不设置就使用默认处理人)')}}</el-checkbox>
      </el-form-item>
    </el-col>
    <el-col :span="16">
      <el-form-item label-width="220px" v-if="isAssign"  :rules="[
              {required: true, message: $i18nMy.t('用户不能为空'), trigger: 'blur'}
            ]"  prop="assignee" :label="$i18nMy.t('指定')">
            <user-select :limit="1" :value="auditForm.assignee"  @getValue='(value) => {auditForm.assignee=value}'>></user-select>
      </el-form-item>
    </el-col>

   </el-form>
</el-card>

<div class="FlowFormFooter">
  <template v-for="(button, index) in buttons">
      <template  v-show="button.isHide === '0'">
        <el-button type="primary"  v-if="button.code !== '_flow_print'"  :key="index" @click="submit(button, buttons)"  v-noMoreClick plain>{{button.name}}</el-button>
        <el-button type="primary" v-if="button.code === '_flow_print'" v-print="printObj" :key="index" @click="submit(button, buttons)"  v-noMoreClick plain>{{button.name}}</el-button>
      </template>
  </template>
</div>
<task-back-nodes ref="taskBackNodes" @getBackTaskDefKey="back"/>
<user-select-dialog title="选择转办用户" ref="transferUserSelectDialog" :limit="1" @doSubmit="selectUsersToTransferTask"></user-select-dialog>
<user-select-dialog title="选择委派用户" ref="delegateUserSelectDialog" :limit="1" @doSubmit="selectUsersToDelateTask"></user-select-dialog>
<user-select-dialog title="选择加签用户" ref="addSignTaskUserSelectDialog" @doSubmit="selectUsersToAddSignTask"></user-select-dialog>
</div>
</template>

<script>
  // import FlowChart from '../modeler/FlowChart'
  import UserSelect from '@/components/userSelect'
  import PreviewForm from '@/views/modules/flowable/form/GenerateFlowableForm'
  import TaskBackNodes from '@/views/modules/flowable/components/TaskBackNodes'
  import FlowStep from '@/views/modules/flowable/components/FlowStep'
  import FlowTimeLine from '@/views/modules/flowable/components/FlowTimeLine'
  import UserSelectDialog from '@/components/userSelect/UserSelectDialog'
  const _import = require('@/router/import-' + process.env.NODE_ENV)
  export default {
    activated () {
      if(this.$route.query.taskId != this.taskId ){
        Object.assign(this.$data, this.$options.data.call(this))
      }
      this.init()
      if (this.formType === '2') { // 读取外置表单
        if (this.formUrl === '/404') {
          this.form = null
          this.$message.info($i18nMy.t('没有关联流程表单!'))
        } else {
          this.form = _import(`modules${this.formUrl}`)
        }
      } else { // 读取动态表单
        this.$nextTick(() => {
          if (this.formUrl === '/404') {
            this.$refs.form.createForm('')
          } else {
            this.$refs.form.createForm(this.formUrl)
          }
        })
        if (this.status === 'start') {
          // 读取启动表单配置
          this.$http.get('/flowable/form/getStartFormData',
              {params: {processDefinitionId: this.procDefId}}
              ).then(({data}) => {
                this.taskFormData = data.startFormData
              })
        } else {
          // 读取任务表单配置
          this.$http.get('/flowable/form/getTaskFormData',
              {params: {taskId: this.taskId}}
              ).then(({data}) => {
                this.taskFormData = data.taskFormData
              })
        }
      }
       // 读取按钮配置
      if (this.status === 'start') {
        this.buttons = [{code: '_flow_start', name: $i18nMy.t('启动'), isHide: '0'},{code: '_flow_save', name: $i18nMy.t('保存为草稿'), isHide: '0'}]
      } else if (this.procDefKey && this.taskDefKey) {
        // 读取按钮
        this.$http.get('/extension/taskDefExtension/queryByDefIdAndTaskId', {params: {
          processDefId: this.procDefKey,
          taskDefId: this.taskDefKey
        }}).then(({data}) => {
          if (data.success) {
            this.buttons = data.taskDefExtension.flowButtonList
          }
        })
      }
    // 读取历史任务列表
      if (this.procInsId) {
        this.$http.get(`/flowable/task/historicTaskList?procInsId=${this.procInsId}`).then(({data}) => {
          this.historicTaskList = data.historicTaskList
        })
      }
    },
    components: {
      UserSelect,
      UserSelectDialog,
      PreviewForm,
      TaskBackNodes,
      FlowStep,
      FlowTimeLine
    },
    watch: {
      isAssign (val) {
        if (!val) {
          this.assignee = null
        }
      },
      taskSelectedTab (val) {
        if (val === 'form-third') {
          if (this.procInsId) {
            this.$refs.chart1.init()
          } else {
            this.$refs.chart2.init()
          }
        }
      }
    },
    methods: {
      initChildFrom(query){
        if(this.form !=null &&this.$refs.form.init !=null){
          this.$refs.form.init(query)
        }
        else{
          setTimeout(()=>{this.initChildFrom(query)},1000)
        }
      },
      init () {
        this.taskSelectedTab = 'form-first'
        this.procDefId = this.$route.query.procDefId
        this.procDefKey = this.$route.query.procDefKey
        this.formType = this.$route.query.formType
        this.formUrl = this.$route.query.formUrl
        this.taskId = this.$route.query.taskId
        this.taskDefKey = this.$route.query.taskDefKey
        this.status = this.$route.query.status
        this.title = this.$route.query.formTitle
        this.printObj.popTitle = this.title
        debugger
        this.businessId = this.$route.query.businessId
        this.procInsId = this.$route.query.procInsId
        this.formReadOnly = this.$route.query.formReadOnly !== undefined && this.$route.query.formReadOnly !== 'false' && this.$route.query.formReadOnly !== false
        this.lastTaskDefKey = this.$route.query.lastTaskDefKey
        this.isCC = false
        this.isAssign = false
        this.auditForm.assignee = null
        this.auditForm.userIds = null
        this.auditForm.message = ''
        this.initChildFrom(this.$route.query)
      },
      cc (data) {
        if (this.isCC && this.auditForm.userIds) {
          this.$refs['auditForm'].validate((valid) => {
            if (valid) {
              this.$http.post('/extension/flowCopy/save', {
                userIds: this.auditForm.userIds,
                procDefId: this.procDefId,
                procInsId: data.procInsId,
                procDefName: '',
                procInsName: this.title,
                taskName: ''
              })
            }
          })
        }
      },
      // 暂存草稿
      save () {
        this.$refs.form.saveForm((businessTable, businessId) => {

        })
      },
      // 启动流程
      start (vars) {
        if (this.formType === '2') { // 外置表单
          this.$refs.form.saveForm((businessTable, businessId) => {
            this.$http.post('/flowable/task/start', {
              procDefKey: this.procDefKey,
              businessTable: businessTable,
              businessId: businessId,
              ...vars,
              title: this.title,
              assignee: this.auditForm.assignee
            }).then(({data}) => {
              if (data.success) {
                this.$message.success(data.msg)
                this.$store.dispatch('tagsView/delView', {fullPath: this.$route.fullPath})
                this.$router.push('/flowable/task/TodoList')
                this.cc(data)
              }
            })
          })
        } else { // 动态表单
          this.$refs.form.submitStartFormData({
            processDefinitionId: this.procDefId,
            ...vars,
            title: this.title,
            assignee: this.auditForm.assignee
          }, (data) => {
            if (data.success) {
              this.$store.dispatch('tagsView/delView', {fullPath: this.$route.fullPath})
              this.$router.push('/flowable/task/TodoList')
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
        this.$confirm($i18nMy.t(`确定驳回流程吗?`), $i18nMy.t('提示'), {
          confirmButtonText: $i18nMy.t('确定'),
          cancelButtonText: $i18nMy.t('取消'),
          type: 'warning'
        }).then(() => {
          this.$http.post('/flowable/task/backNodes', {
            taskId: this.taskId,
            ...this.auditForm}).then(({data}) => {
              let backNodes = data.backNodes
              if (backNodes.length > 0) {
                let backTaskDefKey = backNodes[backNodes.length - 1].taskDefKey
                this.back(backTaskDefKey)
              }
            })
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
            this.$message.success(data.msg)
            this.$store.dispatch('tagsView/delView', {fullPath: this.$route.fullPath})
            this.$router.push('/flowable/task/TodoList')
            this.cc(data)
          }
        })
      },
      // 回退到发起人
      backToModify () {
        this.$http.post('/flowable/task/back', {
          taskId: this.taskId,
          backTaskDefKey: 'FormModify',
          ...this.auditForm
        }).then(({data}) => {
          if (data.success) {
            this.$message.success(data.msg)
            this.$store.dispatch('tagsView/delView', {fullPath: this.$route.fullPath})
            this.$router.push('/flowable/task/TodoList')
            this.cc(data)
          }
        })
      },
      // 补充资料
      backToDocAdd () {
        this.$refs.form.saveForm((businessTable, businessId) => {
          this.$http.post('/flowable/task/back', {
            taskId: this.taskId,
            backTaskDefKey: 'DocAdd',
            ...this.auditForm
          }).then(({data}) => {
            if (data.success) {
              this.$message.success(data.msg)
              this.$store.dispatch('tagsView/delView', {fullPath: this.$route.fullPath})
              this.$router.push('/flowable/task/TodoList')
              this.cc(data)
            }
          })
        })
      },
      // 回退到上一审批环节，用于资料补充
      backToLastApprover () {
        this.$http.post('/flowable/task/back', {
          taskId: this.taskId,
          backTaskDefKey: this.lastTaskDefKey,
          ...this.auditForm
        }).then(({data}) => {
          if (data.success) {
            this.$message.success(data.msg)
            this.$store.dispatch('tagsView/delView', {fullPath: this.$route.fullPath})
            this.$router.push('/flowable/task/TodoList')
            this.cc(data)
          }
        })
      },
      // 加签
      addMultiInstance () {
        // this.$refs.addSignTaskUserSelectDialog.init()
      },
      selectUsersToAddSignTask (users) {
        let userIds = users.map((user) => {
          return user.id
        }).join(',')
        this.$http.post('/flowable/task/addSignTask', {taskId: this.taskId, userIds: JSON.stringify(userIds), comment: '', flag: false}).then(({data}) => {
          this.$message.success(data.msg)
        })
      },
      // 减签
      delMultiInstance () {

      },
      // 转办
      transfer () {
        this.$refs.transferUserSelectDialog.init()
      },
      selectUsersToTransferTask (user) {
        this.$http.post('/flowable/task/transfer', {taskId: this.taskId, userId: user[0].id}).then(({data}) => {
          this.$message.success(data.msg)
          this.$router.push('/flowable/task/TodoList')
        })
      },
      // 委托
      delegate () {
        this.$refs.delegateUserSelectDialog.init()
      },
      selectUsersToDelateTask (user) {
        this.$http.post('/flowable/task/delegate', {taskId: this.taskId, userId: user[0].id}).then(({data}) => {
          this.$message.success(data.msg)
          this.$router.push('/flowable/task/TodoList')
        })
      },
      // 终止
      stop () {
        this.$confirm($i18nMy.t(`确定终止流程吗?`), $i18nMy.t('提示'), {
          confirmButtonText: $i18nMy.t('确定'),
          cancelButtonText: $i18nMy.t('取消'),
          type: 'warning'
        }).then(() => {
          this.$http.post('/flowable/process/stop', {id: this.procInsId, ...this.auditForm}).then(({data}) => {
            this.$message.success(data.msg)
            this.$router.push('/flowable/task/TodoList')
          })
        })
      },
      // 打印
      print () {

      },
      // 自定义按钮提交
      commit (vars) {
        if (this.formType === '2') { // 外置表单
          this.$refs.form.saveForm((businessTable, businessId) => {
            this.$http.post('/flowable/task/audit', {
              taskId: this.taskId,
              taskDefKey: this.taskDefKey,
              procInsId: this.procInsId,
              procDefId: this.procDefId,
              vars: vars,
              comment: this.auditForm,
              assignee: this.auditForm.assignee
            }).then(({data}) => {
              if (data.success) {
                this.$message.success(data.msg)
                this.$store.dispatch('tagsView/delView', {fullPath: this.$route.fullPath})
                this.$router.push('/flowable/task/TodoList')
                this.cc(data)
              }
            })
          })
        } else { // 动态表单
          this.$refs.form.submitTaskFormData(vars, this.procInsId, this.taskId, this.auditForm.assignee, this.auditForm, (data) => {
            if (data.success) {
              this.$store.dispatch('tagsView/delView', {fullPath: this.$route.fullPath})
              this.$router.push('/flowable/task/TodoList')
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
        switch (currentBtn.code) {
          case '_flow_start': // 自动流程
            this.start(vars)
            break
          case '_flow_save': // 保存草稿
            this.save()
            break
          case '_flow_agree': // 同意
            this.agree()
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
          case '_flow_back_last_approver': // 回到上一审批人
            this.backToLastApprover()
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
            this.commit(vars) // 自定义按钮提交
        }
      }
    },
    data () {
      return {
        form: null,
        formType: '',
        formUrl: '',
        taskSelectedTab: 'frist',
        historicTaskList: [],
        procDefId: '',
        procInsId: '',
        lastTaskDefKey: '',
        formReadOnly: false,
        procDefKey: '',
        taskId: '',
        taskFormData: [],
        taskDefKey: '',
        status: '',
        title: '',
        businessId: '',
        buttons: [],
        isCC: false,
        isAssign: false,
        printObj: {
          id: 'printForm',
          popTitle: '',
          extraCss: '',
          extraHead: '<meta http-equiv="Content-Language" content="zh-cn"/>'
        },
        auditForm: {
          message: '',
          type: '',
          status: '',
          userIds: null,
          assignee: null
        }
      }
    }
  }
</script>
