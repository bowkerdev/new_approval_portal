<!--占锐改装的弹窗展示流程历史详情-->
<template>
<div>
  <el-dialog
    :title="title"
    v-dialogDrag
    width="1100px"
    :close-on-click-modal="false"
    :visible.sync="visible">

<!--    <h4 style="text-align:center">{{title}}</h4>-->

    <el-tabs  v-model="selectedTab">
      <el-tab-pane label="表单信息" name="form-first">

        <component :formReadOnly="formReadOnly" v-if="formType === '2'"  :class="formReadOnly?'readonly':''" ref="form" :businessId="businessId" :is="form"></component>
        <PreviewForm   v-if="formType !== '2'"  :processDefinitionId="procDefId" :edit="false" :taskFormData="taskFormData" ref="form"/>

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

    <span slot="footer" class="dialog-footer">
      <el-button type="primary"  @click="visible = false">关闭</el-button>
    </span>
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
        title: '',
        method: '',
        visible: false,
        loading: false,
        // 以下是传递过来的参数
        form: null,
        selectedTab: 'form-first',
        formType: '',
        formUrl: '',
        histoicFlowList: [],
        procDefId: '',
        procInsId: '',
        readOnly: false,
        procDefKey: '',
        taskId: '',
        taskFormData: [],
        taskDefKey: '',
        status: '',
        businessId: ''
      }
    },
    components: {
      UserSelect,
      PreviewForm
      // FlowChart
    },
    watch: {
      selectedTab (val) {
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
      init (formTitle, flowData) {
        // console.log('页面传递过来的参数flowData： ', flowData)
        this.selectedTab = 'form-first'
        this.title = formTitle
        this.procDefId = flowData.procDefId
        this.procDefKey = flowData.procDefKey
        this.formType = flowData.formType
        this.formUrl = flowData.formUrl
        this.taskId = flowData.taskId
        this.taskDefKey = flowData.taskDefKey
        this.status = flowData.status
        this.businessId = flowData.businessId
        this.procInsId = flowData.procInsId
        // 占锐：看是否传递过来了formReadOnly 参数 ，用于设置能否修改表单
        if (flowData.formReadOnly) {
          this.formReadOnly = true
        } else {
          this.formReadOnly = flowData.formReadOnly
        }

        this.loading = true

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

        this.$http.get(`/flowable/task/histoicFlowList?procInsId=${this.procInsId}`).then(({data}) => {
          this.histoicFlowList = data.histoicFlowList
        })

        this.visible = true
        this.loading = false
      }
    }
  }
</script>
