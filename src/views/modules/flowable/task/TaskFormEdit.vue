<template>
<div>
  <h4 style="text-align:center">{{title}}</h4>

  <el-tabs type="border-card" v-model="taskSelectedTab">
    <el-tab-pane label="表单信息" name="form-first">
      <component id="printForm" :formReadOnly="formReadOnly" v-if="formType === '2'" :class="formReadOnly?'readonly':''"  ref="form" :businessId="businessId" :is="form"></component>
      
      <PreviewForm  id="printForm"   v-if="formType !== '2'"  :processDefinitionId="procDefId" :edit="true" :taskFormData="taskFormData" ref="form"/>
    </el-tab-pane>
    <el-tab-pane label="流程信息" v-if="procInsId"  name="form-second">
       <flow-time-line :historicTaskList="historicTaskList"/>
    </el-tab-pane>
    <el-tab-pane label="流程图"  name="form-third">
       <el-card class="box-card"  shadow="hover">
          <div slot="header" class="clearfix">
            <span>流程图</span>
          </div>
          <flow-chart ref="chart1" v-if="procInsId" :processInstanceId="procInsId" />
          <flow-chart ref="chart2" v-if="!procInsId" :processDefId="procDefId" />
        </el-card>
    </el-tab-pane>
    <el-tab-pane label="流转记录" v-if="procInsId" name="form-forth">
           <flow-step :historicTaskList="historicTaskList"/>
     </el-tab-pane>
  </el-tabs>
<el-card style="margin-top:10px">
    <el-form size="small" :model="auditForm"   ref="auditForm" label-width="120px">
      <el-col :span="16">
        <el-form-item  v-if="!procInsId"  label="流程标题" prop="title">
          <el-input
            placeholder="请输入流程标题"
            v-model="title">
          </el-input>
      </el-form-item>
      <el-form-item  v-if="taskId"  label="审批信息" prop="message">
          <el-input
            type="textarea"
            :rows="3"
            placeholder="请输入审批意见"
            v-model="auditForm.message">
          </el-input>
      </el-form-item>
    </el-col>
    <el-col :span="16">
      <el-form-item>
        <el-checkbox v-model="isCC">是否抄送</el-checkbox>
      </el-form-item>
    </el-col>
    <el-col :span="16">
      <el-form-item v-if="isCC"  :rules="[
              {required: true, message: '用户不能为空', trigger: 'blur'}
            ]"  prop="userIds" label="抄送给">
            <user-select :value="auditForm.userIds"  @getValue='(value) => {auditForm.userIds=value}'>></user-select>
      </el-form-item>
    </el-col>
    <el-col :span="16">
      <el-form-item>
        <el-checkbox v-model="isAssign">指定下一步处理者(不设置就使用默认处理人)</el-checkbox>
      </el-form-item>
    </el-col>
    <el-col :span="16">
      <el-form-item v-if="isAssign"  :rules="[
              {required: true, message: '用户不能为空', trigger: 'blur'}
            ]"  prop="assignee" label="指定">
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
<user-select-dialog ref="transferUserSelectDialog" :limit="1" @doSubmit="selectUsersToTransferTask"></user-select-dialog>
<user-select-dialog ref="delegateUserSelectDialog" :limit="1" @doSubmit="selectUsersToDelateTask"></user-select-dialog>
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
      this.init()
          // 读取流程表单
      if (this.formType === '2') {
        if (this.formUrl === '/404') {
          this.form = null
          this.$message.info('没有关联流程表单!')
        } else {
          this.form = _import(`modules${this.formUrl}`)
        }
      } else {
      // 读取流程表单
        if (this.formUrl === '/404') {
          this.$refs.form.createForm('')
        } else {
          this.$refs.form.createForm(this.formUrl)
        }

        this.$http.get('/flowable/form/getHistoryTaskFormData',
              {params: { processInstanceId: this.procInsId, procDefId: this.procDefId, taskDefKey: this.taskDefKey }}
              ).then(({data}) => {
                this.taskFormData = data.taskFormData
              })
      }
       // 读取按钮配置
      if (this.status === 'start') {
        this.buttons = [{code: '_flow_start', name: '启动', isHide: '0'}]
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
      this.$http.get(`/flowable/task/historicTaskList?procInsId=${this.procInsId}`).then(({data}) => {
        this.historicTaskList = data.historicTaskList
      })
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
        this.businessId = this.$route.query.businessId
        this.procInsId = this.$route.query.procInsId
        this.formReadOnly = this.$route.query.formReadOnly !== undefined && this.$route.query.formReadOnly !== 'false' && this.$route.query.formReadOnly !== false
        this.isCC = false
        this.isAssign = false
        this.auditForm.assignee = null
        this.auditForm.userIds = null
        this.auditForm.message = ''
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
        this.$confirm(`确定驳回流程吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
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
      // 加签
      addMultiInstance () {

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
        this.$confirm(`确定终止流程吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
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