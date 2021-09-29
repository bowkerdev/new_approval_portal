<template>
<div>
  <h4 style="text-align:center">{{title}}</h4>
  <!-- <img  v-if="code === 1" class="process-status-img" src="../../../../assets/img/flowable/1.png"/>
  <img  v-if="code === 2" class="process-status-img" src="../../../../assets/img/flowable/2.png"/>
  <img  v-if="code === 3" class="process-status-img" src="../../../../assets/img/flowable/3.png"/>
  <img  v-if="code === 4" class="process-status-img" src="../../../../assets/img/flowable/4.png"/>
  <img  v-if="code === 5" class="process-status-img" src="../../../../assets/img/flowable/5.png"/>
  <img  v-if="code === 6" class="process-status-img" src="../../../../assets/img/flowable/6.png"/> -->
  <el-tabs type="border-card" v-model="selectedTab">
    <el-tab-pane :label="$i18nMy.t('表单信息')" name="form-first">
      <component :formReadOnly="formReadOnly" v-if="formType === '2'"  :class="formReadOnly?'readonly':''" ref="form" :businessId="businessId" :is="form"></component>
      <PreviewForm  class="zm-preview-form-wrapper"  v-if="formType !== '2'"  :processDefinitionId="procDefId" :edit="false" :taskFormData="taskFormData" ref="form"/>
    </el-tab-pane>
    <!-- <el-tab-pane :label="$i18nMy.t('流程信息')" v-if="procInsId"  name="form-second">
       <flow-time-line :historicTaskList="historicTaskList"/>
    </el-tab-pane> -->
    <el-tab-pane :label="$i18nMy.t('流转记录')" v-if="procInsId" name="form-forth">
          <flow-step :historicTaskList="historicTaskList"/>
    </el-tab-pane>
    <el-tab-pane :label="$i18nMy.t('流程图')"  name="form-third">
       <el-card class="box-card"  shadow="hover">
          <div slot="header" class="clearfix">
            <span>{{$i18nMy.t('流程图')}}</span>
          </div>
          <flow-chart ref="chart1" v-if="procInsId" :processInstanceId="procInsId" />
          <flow-chart ref="chart2" v-if="!procInsId" :processDefId="procDefId" />
        </el-card>
    </el-tab-pane>
  </el-tabs>
  <div  style="height:66px ;">-</div>
  <div  class="FlowFormFooter">
    <el-button type="primary" v-if="exportButton!=null" @click="exportData()"  v-noMoreClick plain>{{$i18nMy.t(exportButton.name)}}</el-button>
    <el-button type="primary"  @click="close()"  v-noMoreClick plain>{{$i18nMy.t('关闭')}}</el-button>
  </div>
</div>
</template>

<script>
  // import FlowChart from '../modeler/FlowChart'
  import UserSelect from '@/components/userSelect'
  import PreviewForm from '@/views/modules/flowable/form/GenerateFlowableForm'
  import FlowStep from '@/views/modules/flowable/components/FlowStep'
  import FlowTimeLine from '@/views/modules/flowable/components/FlowTimeLine'
  const _import = require('@/router/import-' + process.env.NODE_ENV)
  export default {
    activated () {
      if(this.initOk){
        var initOk = sessionStorage.getItem('instances.initOk')
        if(initOk==null){
          return
        }
      }
      sessionStorage.removeItem('instances.initOk')
      Object.assign(this.$data, this.$options.data.call(this))
      this.init()
      this.initOk = true
      // 读取流程表单
      if (this.formType === '2') {
        if (this.formUrl === '/404') {
          this.form = null
          this.$message.info('没有关联流程表单!')
        } else {
          if ( this.formUrl === "/oa/pr/form" ) {  // Jack：此处特殊处理，兼容老数据
            this.formUrl = "/flow/pr/OaPrNewForm"
          }
          if(this.formUrl === "/oa/borrowSampleOrderHead/form"){// 特殊处理版衣历史数据
            this.formUrl= '/flow/compatible/bs/borrowSampleOrder'
          }
          this.form = _import(`modules${this.formUrl}`)
        }
      } else {
      // 读取流程表单

        function _createForm(pThis){
          pThis.$nextTick(() => {
            if (pThis.formUrl === '/404') {
              pThis.$refs.form.createForm('')
            } else {
              pThis.$refs.form.createForm(pThis.formUrl)
            }
          })
        }

        this.$http.get('/flowable/form/getHistoryTaskFormData',
              {params: { processInstanceId: this.procInsId, procDefId: this.procDefId, taskDefKey: this.taskDefKey }}
              ).then(({data}) => {
                this.taskFormData = data.taskFormData
                _createForm(this)
              })
      }

      // 读取流程状态
      this.$http.get('/flowable/process/queryProcessStatus',
              {params: { procInsId: this.procInsId, procDefId: this.procDefId }}
              ).then(({data}) => {
                this.code = data.code
              })

      this.$http.get(`/flowable/task/historicTaskList?procInsId=${this.procInsId}`).then(({data}) => {
        this.historicTaskList = data.historicTaskList
      })
    },
    components: {
      UserSelect,
      PreviewForm,
      FlowStep,
      FlowTimeLine
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
      initChildFrom(query){
        if(this.form !=null &&this.$refs.form.init !=null){
          this.$refs.form.init(query)
        }
        else{
          setTimeout(()=>{this.initChildFrom(query)},1000)
        }
      },
      init () {
        this.selectedTab = 'form-first'
        this.procDefId = this.$route.query.procDefId
        this.procDefKey = this.$route.query.procDefKey
        this.formType = this.$route.query.formType
        this.formUrl = this.$route.query.formUrl
        this.taskId = this.$route.query.taskId
        this.taskDefKey = this.$route.query.taskDefKey
        this.status = this.$route.query.status
        this.title = this.$route.query.formTitle
        this.businessId = this.$route.query.businessId
        this.procInsId = this.$route.query.procInsId
        this.formReadOnly = true
        this.initChildFrom(this.$route.query)

        var _pThis=this
        var exportDict=this.$common.find(this.$dictUtils.getDictList('flow_export_config') ,
            function(e){return e.label == _pThis.procDefKey})
        if (exportDict != null && exportDict.value=="true"){
          this.exportButton={name:'电子版'}
        }

        /* this.$http.get('/extension/taskDefExtension/queryByDefIdAndTaskId', {params: {
          processDefId: this.procDefKey,
          taskDefId: this.taskDefKey
        }}).then(({data}) => {
          if (data.success&&data.taskDefExtension!=null&&data.taskDefExtension.flowButtonList.length>0) {
            this.buttons = data.taskDefExtension.flowButtonList
            this.buttons.forEach((btn) => {
              if ((btn.code && btn.code=='_flow_export')) {
                this.button=btn
              }
            })
          }
        }) */
      },
      exportData(){
        this.$utils.syncDownloadPost("FLOW_EXPORT_"+this.procDefKey,{id:this.businessId,procInsId:this.procInsId},this.$refs.form)
      },
      close () {
        this.$common.closeTap(this,this.$route.fullPath);
      }
    },
    data () {
      return {
        buttons:[],
        exportButton:null,
        initOk:false,
        form: null,
        formType: '',
        formUrl: '',
        selectedTab: 'frist',
        historicTaskList: [],
        procDefId: '',
        code: '1',
        procInsId: '',
        readOnly: false,
        procDefKey: '',
        taskId: '',
        taskFormData: [],
        taskDefKey: '',
        status: '',
        title: '',
        businessId: ''
      }
    }
  }
</script>
<style lang="less">
.process-status-img {
    height: 180px;
    position: absolute;
    z-index: 999;
    top: 1px;
    right: 1px;
}
</style>
