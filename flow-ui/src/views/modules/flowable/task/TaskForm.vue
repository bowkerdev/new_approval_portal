<template>
  <!-- taskFromContainer: 滚动到表单错误信息点 -->
  <div style="height: 100%; display:flex; flex-direction: column;">
    <div id="taskFromContainer"
      style="height: 100%;overflow-y: auto;overflow-x: hidden; flex-grow: 3;"
    >
      <h4 style="text-align:center">{{ title }}</h4>

      <el-tabs type="border-card" v-model="taskSelectedTab">
        <el-tab-pane :label="$i18nMy.t('表单信息')" name="form-first">
          <component id="printForm" :formReadOnly="formReadOnly" v-if="formType === '2'"
            :class="formReadOnly ? 'readonly' : ''" ref="form" :businessId="businessId" :is="form"></component>

          <PreviewForm id="printForm" class="zm-preview-form-wrapper" v-if="formType !== '2'"
            :processDefinitionId="procDefId" :edit="true" :taskFormData="taskFormData" ref="form" />
        </el-tab-pane>
        <!-- <el-tab-pane :label="$i18nMy.t('流程信息')" v-if="procInsId" name="form-second">
          <flow-time-line :historicTaskList="historicTaskList"/>
        </el-tab-pane> -->
        <el-tab-pane :label="$i18nMy.t('流转记录')" v-if="procInsId" name="form-forth">
          <flow-step :historicTaskList="historicTaskList" :parallelRoleMap="parallelRoleMap" :parentForm="'TaskForm'"
            :prTopMgmtLevelMap="prTopMgmtLevelMap" :processInstanceId="procInsId" />
        </el-tab-pane>
        <el-tab-pane :label="$i18nMy.t('流程图')" v-if="procInsId && hasPermission('flow:mytask:flowview')"
          name="form-third">
          <el-card class="box-card" shadow="hover">
            <div slot="header" class="clearfix">
              <span>{{ $i18nMy.t('流程图') }}</span>
            </div>
            <flow-chart ref="chart1" v-if="procInsId" :processInstanceId="procInsId" />
            <flow-chart ref="chart2" v-if="!procInsId" :processDefId="procDefId" />
          </el-card>
        </el-tab-pane>
      </el-tabs>

      <el-card style="margin-top:10px; margin-bottom:66px;" v-if="taskId">
        <el-form size="small" :model="auditForm" ref="auditForm" label-width="120px" style="margin-left: 45px;">
          <el-col :span="16">
            <el-form-item v-if="!procInsId" :label="$i18nMy.t('流程标题')" prop="title" v-show="false">
              <el-input :placeholder="$i18nMy.t('请输入流程标题')" v-model="title">
              </el-input>
            </el-form-item>
            <!-- <flow-step :historicTaskList="historicTaskList" style="margin-left:0px;width:146%"/> -->
            <el-form-item class="updown" v-if="taskId" :label="$i18nMy.t('审批信息')" prop="comment">
              <el-input style="width:146%" type="textarea" :rows="3" maxlength="800"
                :placeholder="$i18nMy.t('长度不超过800')" v-model="auditForm.message">
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="16" v-show="false">
            <el-form-item>
              <el-checkbox v-model="isCC">{{ $i18nMy.t('是否抄送') }}</el-checkbox>
            </el-form-item>
          </el-col>
          <el-col :span="16">
            <el-form-item v-if="isCC" :rules="[{ required: true, message: $i18nMy.t('用户不能为空'), trigger: 'blur' }]" prop="userIds" :label="$i18nMy.t('抄送给')">
              <user-select :value="auditForm.userIds"
                @getValue='(value) => { auditForm.userIds = value }'>></user-select>
            </el-form-item>
          </el-col>
          <el-col :span="16" v-if="false">
            <el-form-item label-width="155px" style="margin-top: 10px;">
              <el-checkbox v-model="isAssign">{{ $i18nMy.t('指定下一步处理者(不设置就使用默认处理人)') }}</el-checkbox>
            </el-form-item>
          </el-col>
          <el-col :span="16" v-if="false">
            <el-form-item label-width="180px" v-if="isAssign" :rules="[{ required: true, message: $i18nMy.t('用户不能为空'), trigger: 'blur' }]" prop="assignee" :label="$i18nMy.t('指定')">
              <user-select :limit="1" :value="auditForm.assignee"
                @getValue='(value) => { auditForm.assignee = value }'>></user-select>
            </el-form-item>
          </el-col>

        </el-form>
      </el-card>
      <task-back-nodes ref="taskBackNodes" @getBackTaskDefKey="back" />
      <user-select-dialog title="选择转办用户" ref="transferUserSelectDialog" :limit="1"
        @doSubmit="selectUsersToTransferTask"></user-select-dialog>
      <user-select-dialog title="选择委派用户" ref="delegateUserSelectDialog" :limit="1"
        @doSubmit="selectUsersToDelateTask"></user-select-dialog>
      <user-select-dialog title="选择加签用户" ref="addSignTaskUserSelectDialog"
        @doSubmit="selectUsersToAddSignTask"></user-select-dialog>
      <el-dialog :title="$i18nMy.t('请输入审批意见')" :visible.sync="dialogFormVisible">
        <el-form :model="docAddForm">
          <el-form-item :label="$i18nMy.t('审批意见')" label-width="200px">
            <el-input type="textarea" v-model="docAddForm.docAddComment" maxlength="800"
              :placeholder="$i18nMy.t('长度不超过800')" auto-complete="off"></el-input>
          </el-form-item>
          <el-form-item :label="$i18nMy.t('资料补充人')" label-width="200px">
            <el-input v-model="docAddForm.selectedAssigneeId" v-show="false"></el-input>
            <el-dropdown trigger="click" ref="selectHisAssignee" placement="bottom-start">
              <span class="el-dropdown-link">
                <span v-html="docAddForm.selectedAssigneeLabel"></span><i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-table size="mini" :stripe="true" :data="hisAssigneeList" style="width: 100%"
                  @row-click="handleRowClicked">
                  <el-table-column prop="step" :label="$i18nMy.t('任务')" width="250">
                  </el-table-column>
                  <el-table-column prop="assignee" :label="$i18nMy.t('执行人')" width="350">
                  </el-table-column>
                  <el-table-column prop="endtime" :label="$i18nMy.t('处理时间')" width="200">
                  </el-table-column>
                </el-table>
              </el-dropdown-menu>
            </el-dropdown>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogFormVisible = false">{{ $i18nMy.t('取消') }}</el-button>
          <el-button type="primary" @click="doDocAdd">{{ $i18nMy.t('确定') }}</el-button>
        </div>
      </el-dialog>
    </div>

    <div class="FlowFormFooter" :class="{ 'full-width': defaultLayout == 'dropdown-top' }" v-if="isButtonShow">
      <template v-for="(button, index) in buttons" >
        <template v-show="button.isHide === '0'">
          <el-button :type="buttonType(button.code)" v-if="button.code !== '_flow_print'" :key="index"
            @click="submit(button, buttons)" v-noMoreClick plain>{{ $i18nMy.t(button.name) }}</el-button>
          <el-button type="primary" v-if="button.code === '_flow_print'" v-print="printObj" :key="index"
            @click="submit(button, buttons)" v-noMoreClick plain>{{ $i18nMy.t(button.name) }}</el-button>
        </template>
      </template>
    </div>
    <div class="FlowFormFooter" :class="{ 'full-width': defaultLayout == 'dropdown-top' }" v-else="isButtonShow">
      <div style="height:40px; color: gray; margin-top: 10px; font-size: 10pt;"> {{ $i18nMy.t('界面正在加载中，请稍后...') }}</div>
    </div>
  </div>

</template>

<script>
// 样式问题
import '@/assets/scss/form-definition.scss'
// import FlowChart from '../modeler/FlowChart'
import UserSelect from '@/components/userSelect'
import PreviewForm from '@/views/modules/flowable/form/GenerateFlowableForm'
import TaskBackNodes from '@/views/modules/flowable/components/TaskBackNodes'
import FlowStep from '@/views/modules/flowable/components/FlowStep'
import FlowTimeLine from '@/views/modules/flowable/components/FlowTimeLine'
import UserSelectDialog from '@/components/userSelect/UserSelectDialog'
const _import = require('@/router/import-' + process.env.NODE_ENV)
export default {
  activated() {
    this.taskSelectedTab = 'form-first'
    if (this.initOk) {
      var initOk = sessionStorage.getItem('instances.initOk')
      if (initOk == null) {
        return
      }
    }
    sessionStorage.removeItem('instances.initOk')
    Object.assign(this.$data, this.$options.data.call(this))
    this.init()
    this.initOk = true
    if (this.formType === '2') { // 读取外置表单
      if (this.formUrl === '/404') {
        this.form = null
        this.$message.info($i18nMy.t('没有关联流程表单!'))
      } else {
        if (this.formUrl === "/oa/pr/form") {  // Jack：此处特殊处理，兼容老数据
          this.formUrl = "/flow/pr/OaPrNewFormAllEdit"
        }
        if (this.formUrl === "/oa/borrowSampleOrderHead/form") {// 特殊处理版衣历史数据
          this.formUrl = '/flow/compatible/bs/borrowSampleOrder'
        }
        this.form = _import(`modules${this.formUrl}`)
      }
    } else { // 读取动态表单
      function _createForm(pThis) {
        pThis.$nextTick(() => {
          if (pThis.formUrl === '/404') {
            pThis.$refs.form.createForm('')
          } else {
            pThis.$refs.form.createForm(pThis.formUrl)
          }
        })
      }
      if (this.status === 'start') {
        // 读取启动表单配置
        this.$http.get('/flowable/form/getStartFormData',
          { params: { processDefinitionId: this.procDefId } }
        ).then(({ data }) => {
          this.taskFormData = data.startFormData
          _createForm(this)
        })
      } else {
        // 读取任务表单配置
        this.$http.get('/flowable/form/getTaskFormData',
          { params: { taskId: this.taskId } }
        ).then(({ data }) => {
          this.taskFormData = data.taskFormData
          _createForm(this)
        })
      }
    }

    // 读取按钮配置
    if (this.status === 'start') {
      if (this.formUrl.indexOf("flow/pr/") > 0) { // 目前只有PR能保存草稿 Jack
        this.buttons = [{ code: '_flow_start', name: '启动', isHide: '0' }, { code: '_flow_save', name: '保存为草稿', isHide: '0' }]
      } else {
        this.buttons = [{ code: '_flow_start', name: '启动', isHide: '0' }]
      }
      this.buttons.push({ "name": "关闭", "code": "_flow_close", "isHide": "0" })
    } else if (this.status === 'reopen') {
      if (this.formUrl.indexOf("flow/pr/") > 0) { // 只对PR流程有效
        this.buttons = [{ code: '_flow_reopen', name: '激活', isHide: '0' }]
      }
      this.buttons.push({ "name": "关闭", "code": "_flow_close", "isHide": "0" })
    } else if (this.procDefKey && this.taskDefKey) {
      // 读取按钮
      this.$http.get('/extension/taskDefExtension/queryByDefIdAndTaskId', {
        params: {
          processDefId: this.procDefKey,
          taskDefId: this.taskDefKey
        }
      }).then(({ data }) => {
        if (data.success) {
          this.buttons = data.taskDefExtension.flowButtonList
          if (this.taskDefKey == 'DocAdd' && this.$store.state.user.id == this.applyUserId) {
            this.buttons.push({ "name": "终止", "code": "_flow_stop", "isHide": "0" })
          }
          this.buttons.push({ "name": "关闭", "code": "_flow_close", "isHide": "0" })
        }
      })
    }

    if (this.status != 'start' && this.formUrl.indexOf("flow/pr/") > 0) {
      let that_ = this
      // 读取并行节点的人员列表
      this.$dictUtils.getSqlDictList('GET_PARALLEL_PR_ROLE_LIST', {}, function (data) {
        for (var index in data) {
          that_.parallelRoleMap[data[index].assignee] = data[index].role
        }
      })
      // top mgmt level 列表，用于控制comment是否可见的权限
      let prTopMgmtLevelList = this.$dictUtils.getDictList('pr_top_mgmt_level')
      for (var index in prTopMgmtLevelList) {
        this.prTopMgmtLevelMap[prTopMgmtLevelList[index].value] = prTopMgmtLevelList[index].label
      }
    }

    if (this.status === 'start' || this.formUrl.indexOf("flow/pr/") < 0 || this.formUrl.indexOf("OaPrNewFormForDoc") > 0 ) {
      this.isButtonShow = true
    }

    // 读取历史任务列表
    if (this.procInsId) {
      this.$http.get(`/flowable/task/historicTaskList?procInsId=${this.procInsId}`).then(({ data }) => {
        this.historicTaskList = data.historicTaskList

        if (this.formUrl.indexOf("flow/pr/") > 0) {
          this.isAfterL13 = false
          for (var index in this.historicTaskList) { // 检查是否大于L13
            if (this.historicTaskList[index].histIns.activityId == 'GroupFA'){
              this.isAfterL13 = true
            } else if (this.historicTaskList[index].histIns.activityId == 'GroupFA_2'){
              this.isAfterL13 = true
              this.isReApproval = true
              break
            }
          }
        }
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
    isAssign(val) {
      if (!val) {
        this.assignee = null
      }
    },
    taskSelectedTab(val) {
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
    handleRowClicked(row, column, event) {
      this.docAddForm.selectedAssigneeLabel = row.step + '&nbsp;&nbsp;|&nbsp;&nbsp;' + row.assignee;
      this.docAddForm.selectedAssigneeId = row.userid;
      this.$refs.selectHisAssignee.hide();
    },

    buttonType(code) {
      return this.$dictUtils.getDictLabel("button_color", code, "primary")
    },
    initChildFrom(query) {
      // query.parentForm = "TaskForm"
      if (this.formType === '2') { // 外置表单
        if (this.form != null && this.$refs.form.init != null) {
          this.$refs.form.init(query, this)
          return
        }
      } else { // 动态表单
        if (this.$refs.form != null && this.$refs.form.init != null) {
          this.$refs.form.init(query)
          return
        }
      }
      setTimeout(() => { this.initChildFrom(query) }, 1000)
    },
    init() {
      this.taskSelectedTab = 'form-first'
      this.procDefId = this.$route.query.procDefId
      this.procDefKey = this.$route.query.procDefKey
      this.formType = this.$route.query.formType
      this.formUrl = this.$route.query.formUrl
      this.taskId = this.$route.query.taskId
      this.taskDefKey = this.$route.query.taskDefKey
      this.status = this.$route.query.status
      this.title = this.$route.query.formTitle
      this.applyUserId = this.$route.query.applyUserId
      this.printObj.popTitle = this.title
      this.businessId = this.$route.query.businessId
      this.procInsId = this.$route.query.procInsId
      this.formReadOnly = this.$route.query.formReadOnly !== undefined && this.$route.query.formReadOnly !== 'false' && this.$route.query.formReadOnly !== false
      this.lastTaskDefKey = this.$route.query.lastTaskDefKey
      this.isCC = false
      this.isAssign = false
      this.auditForm.assignee = null
      this.auditForm.userIds = null
      this.auditForm.message = ''
      let _that = this
      this.$dictUtils.getSqlDictList('GET_FLOW_HIS_ASSIGNEE', { procInsId: this.procInsId, userId: this.$store.state.user.id }, function (data) {
        _that.hisAssigneeList = data
      })
      this.initChildFrom(this.$route.query)
    },
    changeButtons(flag) {
      if (this.taskDefKey == "CeoOffice") {
        let buttons2 = []

        if (flag == "ge5M") {
          buttons2.push({ "name": "To BOD", "code": "_flow_agree", "isHide": "0" })
          for (var index in this.buttons) {
            if (this.buttons[index].code != 'optionA'
              && this.buttons[index].code != 'optionB'
              && this.buttons[index].code != 'optionC'
              && this.buttons[index].code != 'optionD'
              && this.buttons[index].code != 'optionE') {
              buttons2.push(this.buttons[index])
            }
          }
        } else if (flag == "reApproval") {
          for (var index in this.buttons) {
            if (this.buttons[index].code != 'optionB') { // To CEO & CFO & OD
              buttons2.push(this.buttons[index])
            }
          }
        } else {
          for (var index in this.buttons) {
            if (this.buttons[index].code != 'optionD' && this.buttons[index].code != 'optionE') {
              buttons2.push(this.buttons[index])
            }
          }
        }

        this.buttons = buttons2
      }
    },
    cc(data) {
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
    save() {
      this.$refs.form.saveAsDraft((businessTable, businessId) => {
        this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
        this.$router.push('/sys/index')
      })
    },
    // 启动流程
    start(vars) {
      if (this.formType === '2') { // 外置表单
        this.$refs.form.saveForm((businessTable, businessId) => {
          this.$http.post('/flowable/task/start', {
            procDefKey: this.procDefKey,
            businessTable: businessTable,
            businessId: businessId,
            ...vars,
            //title: this.title,  // title用作申请单号，后台生成
            assignee: this.auditForm.assignee
          }).then(({ data }) => {
            if (data.success) {
              this.$message.success(data.msg)
              //this.$utils.sendEmail(data.procInsId, this)
              this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
              //this.$router.push('/flowable/task/TodoList')
              this.$router.push('/sys/index')
              this.cc(data)
            }
          })
        })
      } else { // 动态表单
        this.$refs.form.submitStartFormData({
          processDefinitionId: this.procDefId,
          ...vars,
          //title: this.title, // title用作申请单号，后台生成
          assignee: this.auditForm.assignee
        }, (data) => {
          if (data.success) {
            this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
            //this.$router.push('/flowable/task/TodoList')
            this.$router.push('/sys/index')
            this.cc(data)
          }
        })
      }
    },
    // 同意
    agree(vars) {
      this.commit(vars) // 同意
    },
    // 驳回
    reject() {
      /* this.$confirm($i18nMy.t(`确定驳回流程吗?`), $i18nMy.t('提示'), {
        confirmButtonText: $i18nMy.t('确定'),
        cancelButtonText: $i18nMy.t('取消'),
        type: 'warning'
      }).then(() => { */
      this.$http.post('/flowable/task/backNodes', {
        taskId: this.taskId,
        ...this.auditForm
      }).then(({ data }) => {
        let backNodes = data.backNodes
        if (backNodes.length > 0) {
          let backTaskDefKey = backNodes[backNodes.length - 1].taskDefKey
          this.back(backTaskDefKey)
        }
      })
      //})
    },
    // 驳回到任意节点
    turnBack() {
      this.$refs.taskBackNodes.init(this.taskId)
    },
    // 回退到任意节点
    back(backTaskDefKey) {
      this.$http.post('/flowable/task/back', {
        taskId: this.taskId,
        backTaskDefKey: backTaskDefKey,
        ...this.auditForm
      }).then(({ data }) => {
        if (data.success) {
          this.$message.success(data.msg)
          this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
          //this.$router.push('/flowable/task/TodoList')
          this.$router.push('/sys/index')
          this.cc(data)
        }
      })
    },
    // 回退到发起人
    backToModify() {
      this.$http.post('/flowable/task/back', {
        taskId: this.taskId,
        backTaskDefKey: 'FormModify',
        ...this.auditForm
      }).then(({ data }) => {
        if (data.success) {
          this.$message.success(data.msg)
          this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
          //this.$router.push('/flowable/task/TodoList')
          this.$router.push('/sys/index')
          this.cc(data)
        }
      })
    },
    // 补充资料
    backToDocAdd() {
      this.dialogFormVisible = true
    },
    doDocAdd() {
      if (this.docAddForm.selectedAssigneeId == "") {
        this.$message.warning($i18nMy.t("请选择资料补充人"))
        return
      }
      this.auditForm.assignee = this.docAddForm.selectedAssigneeId
      if (this.auditForm.message == "") {
        this.auditForm.message = this.docAddForm.docAddComment
      } else {
        this.auditForm.message += ", " + this.docAddForm.docAddComment
      }
      this.$http.post('/flowable/task/back', {
        taskId: this.taskId,
        backTaskDefKey: 'DocAdd',
        procInsId: this.procInsId,
        ...this.auditForm
      }).then(({ data }) => {
        if (data.success) {
          this.$message.success(data.msg)
          this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
          this.dialogFormVisible = false
          //this.$router.push('/flowable/task/TodoList')
          this.$router.push('/sys/index')
          this.cc(data)
        }
      })
    },
    // On Hold
    onHold() {
      this.$http.post('/flowable/task/back', {
        taskId: this.taskId,
        backTaskDefKey: this.taskDefKey + '_OnHold',
        procInsId: this.procInsId,
        ...this.auditForm
      }).then(({ data }) => {
        if (data.success) {
          this.$message.success(data.msg)
          this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
          //this.$router.push('/flowable/task/TodoList')
          this.$router.push('/sys/index')
          this.cc(data)
        }
      })
    },
    // 回退到上一审批环节，用于资料补充
    backToLastApprover() {
      this.$refs.form.saveForm((businessTable, businessId) => {
        this.$http.post('/flowable/task/back', {
          taskId: this.taskId,
          backTaskDefKey: this.lastTaskDefKey,
          ...this.auditForm
        }).then(({ data }) => {
          if (data.success) {
            this.$message.success(data.msg)
            this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
            //this.$router.push('/flowable/task/TodoList')
            this.$router.push('/sys/index')
            this.cc(data)
          }
        })
      })
    },
    // 加签
    addMultiInstance() {
      // this.$refs.addSignTaskUserSelectDialog.init()
    },
    selectUsersToAddSignTask(users) {
      let userIds = users.map((user) => {
        return user.id
      }).join(',')
      this.$http.post('/flowable/task/addSignTask', { taskId: this.taskId, userIds: JSON.stringify(userIds), comment: '', flag: false }).then(({ data }) => {
        this.$message.success(data.msg)
      })
    },
    // 减签
    delMultiInstance() {

    },
    // 转办
    transfer() {
      this.$refs.transferUserSelectDialog.init()
    },
    selectUsersToTransferTask(user) {
      this.$http.post('/flowable/task/transfer', { taskId: this.taskId, userId: user[0].id }).then(({ data }) => {
        this.$message.success(data.msg)
        //this.$router.push('/flowable/task/TodoList')
        this.$router.push('/sys/index')
      })
    },
    // 委托
    delegate() {
      this.$refs.delegateUserSelectDialog.init()
    },
    selectUsersToDelateTask(user) {
      this.$http.post('/flowable/task/delegate', { taskId: this.taskId, userId: user[0].id }).then(({ data }) => {
        this.$message.success(data.msg)
        //this.$router.push('/flowable/task/TodoList')
        this.$router.push('/sys/index')
      })
    },
    // 终止
    stop() {
      /* this.$confirm($i18nMy.t(`确定终止流程吗?`), $i18nMy.t('提示'), {
        confirmButtonText: $i18nMy.t('确定'),
        cancelButtonText: $i18nMy.t('取消'),
        type: 'warning'
      }).then(() => { */
      this.$http.post('/flowable/process/stop', { id: this.procInsId, ...this.auditForm }).then(({ data }) => {
        this.$message.success(data.msg)
        //this.$router.push('/flowable/task/TodoList')
        this.$router.push('/sys/index')
      })
      //})
    },
    // 打印
    print() {

    },
    close() {
      this.$common.closeTap(this, this.$route.fullPath);
    },
    // 自定义按钮提交
    commit(vars) {
      if (this.formType === '2') { // 外置表单
        this.$refs.form.saveForm((businessTable, businessId) => {
          this.$http.post('/flowable/task/audit', {
            taskId: this.taskId,
            taskDefKey: this.taskDefKey,
            procInsId: this.procInsId,
            procDefId: this.procDefId,
            businessTable: businessTable,
            businessId: businessId,
            vars: vars,
            comment: this.auditForm,
            assignee: this.auditForm.assignee
          }).then(({ data }) => {
            if (data.success) {
              this.$message.success(data.msg)
              //this.$utils.sendEmail(data.procInsId, this)
              this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
              //this.$router.push('/flowable/task/TodoList')
              this.$router.push('/sys/index')
              this.cc(data)
            }
          })
        })
      } else { // 动态表单
        this.$refs.form.submitTaskFormData(vars, this.procInsId, this.taskId, this.auditForm.assignee, this.auditForm, (data) => {
          if (data.success) {
            this.$store.dispatch('tagsView/delView', { fullPath: this.$route.fullPath })
            //this.$router.push('/flowable/task/TodoList')
            this.$router.push('/sys/index')
            this.cc(data)
          }
        })
      }
    },
    exportData() {
      let sysFlag = this.$dictUtils.getDictValue("sys_config", "sys_flag", "");
      if (sysFlag != "") {
        sysFlag = "_" + sysFlag
      }
      let exportKey = this.procDefKey
      if (exportKey == "pr") {
        exportKey = "prpo"
      }
      this.$utils.syncDownloadPost("FLOW_EXPORT_" + exportKey + sysFlag, { id: this.businessId, procInsId: this.procInsId }, this.$refs.form)
      // this.$utils.syncDownloadPost("FLOW_EXPORT_"+this.procDefKey, {id:this.businessId,procInsId:this.procInsId}, this.$refs.form)
    },
    submit(currentBtn, buttons) {
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

      let cfmMsg = $i18nMy.t(`确定要`) + $i18nMy.t(currentBtn.name) + "?"
      let cfmMsgs = []
      let msgDatas = [], h = this.$createElement
      if (this.$refs.form.getTotalAmount) {
        // this.$message.warning(this.taskDefKey);
        if (this.taskDefKey == 'CeoOffice' && this.$refs.form.getTotalAmount() >= 1000000 && (currentBtn.code == 'optionA' || currentBtn.code == 'optionB')) {
          cfmMsg = $i18nMy.t("总金额大于HK1M，确定要执行") + $i18nMy.t(currentBtn.name) + "?"
          // The total amount of the application exceeds HK$1M, are you sure to continue to the selected option?
        }

        if (this.$refs.form.getOldTotalAmount && this.taskDefKey == 'GroupFA_2' && this.$refs.form.getTotalAmount() != this.$refs.form.getOldTotalAmount()) {
          //var rate = (this.$refs.form.getTotalAmount()/this.$refs.form.getOldTotalAmount()) - 1
          cfmMsg = this.$refs.form.getTotalAmtChangeMsg() + ", " + $i18nMy.t("确定要") + $i18nMy.t(currentBtn.name) + "?"
          cfmMsgs = cfmMsg.split(", ")
        }
      }
      if (cfmMsgs.length == 0){
        msgDatas.push(h('p', null, cfmMsg))
      }else{
        for (var index in cfmMsgs) {
          msgDatas.push(h('p', null, cfmMsgs[index]))
        }
      }

      if (currentBtn.code == '_flow_stop' && this.procDefKey == 'prpo_non_it') {
        msgDatas.push(h('p', null, $i18nMy.t("流程终止操作说明")))
      }

      this.$confirm(h('div', null, msgDatas), $i18nMy.t('提示'), {
        confirmButtonText: $i18nMy.t('是'),
        cancelButtonText: $i18nMy.t('否'),
        type: 'warning'
      }).then(() => {
        switch (currentBtn.code) {
          case '_flow_start': // 自动流程
            this.start(vars)
            break
          case '_flow_reopen': // 已结束的流程重新启动
            this.start(vars)
            break
          case '_flow_save': // 保存草稿
            this.save()
            break
          case '_flow_agree': // 同意
            this.agree(vars)
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
          case '_flow_back_on_hold': // On Hold
            this.onHold()
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
          case '_flow_export':// 导出
            this.exportData()
            break
          case '_flow_close':// 关闭
            this.close()
            break
          default:
            this.commit(vars) // 自定义按钮提交
        }
      })
    }
  },
  data() {
    return {
      initOk: false,
      form: null,
      formType: '',
      formUrl: '',
      taskSelectedTab: 'frist',
      historicTaskList: [],
      hisAssigneeList: [],
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
      applyUserId: '',
      parallelRoleMap: {},
      prTopMgmtLevelMap: {},
      buttons: [],
      isCC: false,
      isButtonShow: false,
      isAssign: false,
      isAfterL13: false,  // pr only
      isReApproval: false,  // pr only
      printObj: {
        id: 'printForm',
        popTitle: '',
        extraCss: '',
        extraHead: '<meta http-equiv="Content-Language" content="zh-cn"/>'
      },
      dialogFormVisible: false,
      docAddForm: {
        selectedAssigneeLabel: $i18nMy.t(`请选择`),
        selectedAssigneeId: '',
        docAddComment: ''
      },
      auditForm: {
        message: '',
        type: '',
        status: '',
        userIds: null,
        assignee: null
      }
    }
  },
  computed: {
    defaultLayout() {
      return this.$store.state.config.defaultLayout
    }
  }
}
</script>
<style scoped lang = "less">
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}

.el-icon-arrow-down {
  font-size: 12px;
}

.updown ::v-deep label {
  float: none !important;
}

.updown ::v-deep label+div {
  float: none !important;
  margin-left: 0px !important;
}

.FlowFormFooter {
  position: static;
  padding-top: 10px;
  height: auto;
  display: flex;
  flex-wrap: wrap;
  flex-shrink: 0;
  flex-grow: 0;
}

.FlowFormFooter button {
  margin-bottom: 10px;
}
</style>
