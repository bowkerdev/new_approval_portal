<template>
<div>
  <el-dialog
    :title="title"
    width="90%"
    v-dialogDrag
    :close-on-click-modal="false"
    :visible.sync="visible">

      <el-tabs type="border-card" v-model="taskSelectedTab">
        <el-tab-pane label="表单信息" name="form-first">
          <component :formReadOnly="formReadOnly" v-if="formType === '2'" :class="formReadOnly?'readonly':''"  ref="form" :businessId="businessId" :is="form"></component>

          <PreviewForm   v-if="formType !== '2'"  :processDefinitionId="procDefId" :edit="true" :taskFormData="taskFormData" ref="form"/>
        </el-tab-pane>
        <el-tab-pane label="流程信息" v-if="procInsId"  name="form-second">
           <el-card class="box-card"  shadow="hover">
              <div slot="header" class="clearfix">
                <span>流程信息</span>
              </div>
              <el-timeline :reverse="true" v-if="histoicFlowList.length">
                  <el-timeline-item color="#3f9eff" :key="index" v-for="(act, index) in histoicFlowList"  :timestamp="moment(act.histIns.endTime).format('YYYY-MM-DD')" placement="top">
                    <el-card>
                      <h4>{{act.histIns.activityName}}</h4>
                      <p>{{act.assigneeName}} : {{act.comment}}</p>
                      <p>开始时间 : {{moment(act.histIns.startTime).format('YYYY-MM-DD HH:mm:ss')}}</p>
                      <p>结束时间 : {{moment(act.histIns.endTime).format('YYYY-MM-DD HH:mm:ss')}}</p>
                      <p>用时 : {{act.durationTime || '0秒'}}</p>
                    </el-card>
                  </el-timeline-item>
              </el-timeline>
            </el-card>
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
              <el-card class="box-card"  shadow="hover" style="margin-top:5px">
          <div slot="header" class="clearfix">
            <span>流转记录</span>
          </div>
          <el-steps :active="histoicFlowList.length">
            <el-step :key="index" v-for="(act, index) in histoicFlowList" :title="act.histIns.activityName" finish-status="success"  :description="(act.assigneeName||'') +'    '   + moment(act.histIns.endTime).format('YYYY-MM-DD HH:mm:ss')"></el-step>
          </el-steps>
          <el-table
           border
          :data="histoicFlowList"
          style="width: 100%">
          <el-table-column
            prop="histIns.activityName"
            label="执行环节"
            width="180">
          </el-table-column>
          <el-table-column
            prop="assigneeName"
            label="执行人"
            width="180">
          </el-table-column>
          <el-table-column
            prop="histIns.startTime"
            label="开始时间">
            <template slot-scope="scope">
              {{scope.row.histIns.startTime | formatDate}}
            </template>
          </el-table-column>
           <el-table-column
            prop="histIns.endTime"
            label="结束时间">
            <template slot-scope="scope">
              {{scope.row.histIns.endTime | formatDate}}
            </template>
          </el-table-column>
           <el-table-column
            prop="comment"
            label="审批意见">
          </el-table-column>
           <el-table-column
            prop="durationTime"
            label="任务历时">
            <template slot-scope="scope">
              {{scope.row.durationTime || '0秒'}}
            </template>
          </el-table-column>
        </el-table>
        </el-card>
         </el-tab-pane>
      </el-tabs>

      <el-card style="margin-top:10px" v-if="!procInsId || taskId">
          <el-form :model="auditForm"   ref="auditForm" label-width="120px">
            <el-col :span="16">
              <el-form-item  v-if="!procInsId"  label="流程标题" prop="title">
                <el-input
                  placeholder="请输入流程标题"
                  v-model="title">
                </el-input>
            </el-form-item>
            <el-form-item  v-if="taskId"  label="审批信息" prop="comment"
                           :rules="[ {required: true, message:'审批意见不能为空', trigger:'blur'}]">
                <el-input
                  type="textarea"
                  :rows="3"
                  placeholder="请输入审批意见"
                  v-model="auditForm.comment">
                </el-input>
            </el-form-item>
          </el-col>
<!--          <el-col :span="16">-->
<!--            <el-form-item>-->
<!--              <el-checkbox v-model="isCC">是否抄送</el-checkbox>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
<!--          <el-col :span="16">-->
<!--            <el-form-item v-if="isCC"  :rules="[-->
<!--            {required: true, message: '用户不能为空', trigger: 'blur'}-->
<!--          ]"  prop="userIds" label="抄送给">-->
<!--              <user-select :value="auditForm.userIds"  @getValue='(value) => {auditForm.userIds=value}'>></user-select>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
<!--          <el-col :span="16">-->
<!--            <el-form-item>-->
<!--              <el-checkbox v-model="isAssign">指定下一步处理者(不设置就使用默认处理人)</el-checkbox>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
<!--          <el-col :span="16">-->
<!--            <el-form-item v-if="isAssign"  :rules="[-->
<!--            {required: true, message: '用户不能为空', trigger: 'blur'}-->
<!--          ]"  prop="assign" label="指定">-->
<!--              <user-select :limit="1" :value="auditForm.assign"  @getValue='(value) => {auditForm.assign=value}'>></user-select>-->
<!--            </el-form-item>-->
<!--          </el-col>-->
          <el-col :span="16">
            <el-form-item>
                <el-button type="primary" v-show="button.isHide === '0'" v-for="(button, index) in buttons" :key="index" @click="submit(button, buttons)"  v-noMoreClick>{{button.name}}</el-button>
            </el-form-item>
          </el-col>
         </el-form>
      </el-card>

<!--      <span slot="footer" class="dialog-footer">-->
<!--        <el-button type="primary"  @click="visible = false">关闭</el-button>-->
<!--      </span>-->

  </el-dialog>
</div>
</template>

<script>
  // import FlowChart from '../modeler/FlowChart'
  import UserSelect from '@/components/userSelect'
  import PreviewForm from '@/views/modules/flowable/form/GenerateFlowableForm'
  const _import = require('@/router/import-' + process.env.NODE_ENV)
  export default {
    data () {
      return {
        // 下面2个参数是针对dialog添加的
        visible: false,
        loading: false,
        form: null,
        formType: '',
        formUrl: '',
        taskSelectedTab: 'frist',
        histoicFlowList: [],
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
        auditForm: {
          comment: '',
          userIds: null,
          assign: null
        }
      }
    },
    components: {
      UserSelect,
      PreviewForm
      // FlowChart
    },
    watch: {
      isAssign (val) {
        if (!val) {
          this.assign = null
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
      init (flowData) {
        this.loading = true

        console.log('传递参数2：', flowData)
        this.taskSelectedTab = 'form-first'
        this.procDefId = flowData.procDefId
        this.procDefKey = flowData.procDefKey
        this.formType = flowData.formType
        this.formUrl = flowData.formUrl
        this.taskId = flowData.taskId
        this.taskDefKey = flowData.taskDefKey
        this.status = flowData.status
        this.title = flowData.formTitle
        this.businessId = flowData.businessId
        this.procInsId = flowData.procInsId
        this.formReadOnly = flowData.formReadOnly !== undefined && flowData.formReadOnly !== 'false' && flowData.formReadOnly !== false
        this.isCC = false
        this.isAssign = false
        this.auditForm.assign = null
        this.auditForm.userIds = null
        this.auditForm.comment = ''

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
          this.buttons = [{id: 'start', name: '启动', isHide: '0'}]
        } else if (this.procDefKey && this.taskDefKey) {
          // 读取按钮
          this.$http.get('/extension/taskDefExtension/queryByDefIdAndTaskId', {params: {
            processDefId: this.procDefKey,
            taskDefId: this.taskDefKey
          }}).then(({data}) => {
            console.log('btn', data)
            if (data.success) {
              this.buttons = data.taskDefExtension.flowButtonList
            }
          })
        }
        // 读取历史任务列表
        this.$http.get(`/flowable/task/histoicFlowList?procInsId=${this.procInsId}`).then(({data}) => {
          this.histoicFlowList = data.histoicFlowList
        })

        console.log('this.$refs-加载完毕： ', this.$refs)

        this.visible = true
        this.loading = false
        console.log('11')
      },
      // 流程抄送
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
      submit (currentBtn, buttons) {
        let vars = {}
        buttons.forEach((btn) => {
          vars[btn.code] = false
        })
        vars[currentBtn.code] = true
        vars.title = this.title
        vars.assign = this.auditForm.assign
        // console.log('流程变量：', vars)
        if (currentBtn.id === 'start') {
          if (this.formType === '2') {
            // console.log('this.$refs： ', this.$refs)
            this.$refs.form.saveForm((businessTable, businessId) => {
              this.$http.post('/flowable/task/start', {
                procDefKey: this.procDefKey,
                businessTable: businessTable,
                businessId: businessId,
                ...vars,
                title: this.title,
                assign: this.auditForm.assign
              }).then(({data}) => {
                if (data.success) {
                  this.$message.success(data.msg)
                  this.$emit('refreshDataList')
                  this.cc(data)
                }
              })
            })
          } else {
            this.$refs.form.submitStartFormData({
              processDefinitionId: this.procDefId,
              ...vars,
              title: this.title,
              assign: this.auditForm.assign
            }, (data) => {
              if (data.success) {
                this.$message.success(data.msg)
                this.$emit('refreshDataList')
                this.cc(data)
              }
            })
          }
        } else {
          if (this.formType === '2') {
            this.$refs['auditForm'].validate((valid) => {
              if (valid) {
                this.$refs.form.saveForm((businessTable, businessId) => {
                  this.$http.post('/flowable/task/audit', {
                    taskId: this.taskId,
                    taskDefKey: this.taskDefKey,
                    procInsId: this.procInsId,
                    procDefId: this.procDefId,
                    vars: vars,
                    comment: this.auditForm.comment,
                    assign: this.auditForm.assign
                  }).then(({data}) => {
                    if (data.success) {
                      this.$message.success(data.msg)
                      this.visible = false
                      this.$message.success(data.msg)
                      this.$emit('refreshDataList')
                      this.cc(data)
                    }
                  })
                })
              }
            })
          } else {
            this.$refs.form.submitTaskFormData(vars, this.procInsId, this.taskId, this.auditForm.assign, this.auditForm.comment, this.taskId, (data) => {
              if (data.success) {
                this.$message.success(data.msg)
                this.$emit('refreshDataList')
                this.cc(data)
              }
            })
          }
        }
      }
    }

  }
</script>
