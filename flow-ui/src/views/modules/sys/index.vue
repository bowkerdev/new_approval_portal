<template>
  <div class="el-scrollbar__wrap">
  <div class="el-scrollbar__view">
    <el-row :gutter="10" style="margin-bottom: 10px">
      <el-col :lg="24" :md="24" :sm="24" :xs="24">

          <el-row>
            <el-col :span="6" v-for="data in dataList2" :key="data.id">
              <el-card class="box-card" v-if="hasPermission(data.key)" style="margin:5px 10px 5px 0px">
                  <div class="actCard" @click="start(data)">
                    <!-- <img src='@/assets/img/Scheme.png'/> -->
                    <div class="yuan1" :class="getRandomColor()">{{data.name.substring(0,1)}}</div>
                    <el-button class="task-name" type="text" :title="data.name+' '+data.version">{{$i18nMy.t('新建')}} {{$i18nMy.t(data.name)}}</el-button>
                  </div>
                </el-card>
            </el-col>
            <el-col :span="6" v-for="data in menuList" :key="data.id">
              <el-card class="box-card" v-if="hasPermission(data.permission)" style="margin:5px 10px 5px 0px">
                  <div class="actCard" @click="toTargetPage(data.url)">
                    <div class="yuan1" :class="getRandomColor()">{{data.name.substring(0,1)}}</div>
                    <el-button class="task-name" type="text" :title="data.name">{{$i18nMy.t(data.name)}}</el-button>
                  </div>
                </el-card>
            </el-col>
          </el-row>
      </el-col>

    </el-row>
    <el-row :gutter="10" style="margin-bottom: 10px">
      <el-col
        :lg="24" :md="24" :sm="24" :xs="24"
      >
        <el-card
          :header="$i18nMy.t('我的待办')"
        >
          <el-table
              :data="dataList"
              size = "small"
              v-loading="loading"
              height="245px"
              class="table my-table-margin">
              <el-table-column
                prop="vars.request_riority"
                width="60px"
                show-overflow-tooltip
                :label="$i18nMy.t('优先级')">
                    <template slot-scope="scope">
                      <el-tooltip class="item" effect="dark" :content="$dictUtils.getDictLabel('request_priority', scope.row.vars.request_riority, scope.row.vars.request_riority)" placement="top">
                        <i v-if="scope.row.vars.request_riority==='high'" style="color: #ff0004;" class="el-icon-warning"></i>
                        <i v-if="scope.row.vars.request_riority==='medium'" style="color: #ffce38;" class="el-icon-warning"></i>
                        <i v-if="scope.row.vars.request_riority==='low'" style="color: #1eb163;" class="el-icon-warning"></i>
                      </el-tooltip>
                    </template>
              </el-table-column>
              <el-table-column
                prop="vars.title"
                width="200px"
               show-overflow-tooltip
                :label="$i18nMy.t('申请单号')">
                    <template slot-scope="scope">
                      <el-link  type="primary" :underline="false" @click="todo(scope.row)">{{scope.row.vars.title}}</el-link>
                    </template>
              </el-table-column>
              <el-table-column
                width="150px"
                prop="processDefinitionName"
                show-overflow-tooltip
                :label="$i18nMy.t('流程名称')">
              </el-table-column>
              <el-table-column
                prop="remarks"
                show-overflow-tooltip
                :label="$i18nMy.t('流程信息')">
                <template slot-scope="scope">
                    {{scope.row.remarks || scope.row.vars.description}}
                </template>
              </el-table-column>
              <el-table-column
                prop="var.total_contract_amount"
                show-overflow-tooltip
                align="right"
                width="120px"
                :label="$i18nMy.t('总金额')">
                <template slot-scope="scope">
                  {{$common.toThousands(scope.row.vars.total_vat_contract_amount || scope.row.vars.total_contract_amount || scope.row.vars.priceAmount)}}
                </template>
              </el-table-column>
              <el-table-column
                prop="var.contract_currency"
                show-overflow-tooltip
                width="120px"
                :label="$i18nMy.t('币种')">
                <template slot-scope="scope">
                    {{scope.row.vars.contract_currency || scope.row.vars.currency}}
                </template>
              </el-table-column>
               <el-table-column
                width="180px"
                prop="applyUserName"
                show-overflow-tooltip
                :label="$i18nMy.t('流程发起人')">
                 <template slot-scope="scope">
                   {{scope.row.applyUserName || scope.row.vars.userName}}
                 </template>
              </el-table-column>
              <el-table-column
               width="120px"
                prop="task.name"
                show-overflow-tooltip
                :label="$i18nMy.t('当前环节')">
                <template slot-scope="scope">
                  {{$i18nMy.t(scope.row.task.name)}}
                </template>
              </el-table-column>
              <el-table-column
                width="180px"
                prop="task.assigneeName"
                show-overflow-tooltip
                :label="$i18nMy.t('当前处理人')">
              </el-table-column>
              <el-table-column
                width="150px"
                prop="task.createTime"
                show-overflow-tooltip
                :label="$i18nMy.t('申请时间')">
                 <template slot-scope="scope">
                  {{scope.row.task.createTime | formatDate}}
                 </template>
              </el-table-column>
              <el-table-column
                fixed="right"
                :key="Math.random()"
                header-align="center"
                align="center"
                v-if="false"
                width="150"
                :label="$i18nMy.t('操作')">
                <template slot-scope="scope">
                  <!-- <el-button v-if="scope.row.status === 'claim'" type="text" size="small" @click="claim(scope.row)">{{$i18nMy.t('签收任务')}}</el-button> -->
                  <el-button type="text" size="small" @click="todo(scope.row)">{{$i18nMy.t('办理')}}</el-button>
                  <!--  <el-button v-if="scope.row.status === 'todo'" type="text" size="small" @click="transferTask(scope.row)">{{$i18nMy.t('委派')}}</el-button> -->
                  <!--  <el-button v-if="scope.row.claimTime" type="text" size="small" @click="unclaim(scope.row)">{{$i18nMy.t('取消签收')}}</el-button> -->
                  <el-button  type="text" size="small" @click="trace(scope.row)">{{$i18nMy.t('进度')}}</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-row>
              <el-button-group class="pull-right" style="margin-top: 15px;">
                <el-tooltip class="item" effect="dark" content="More" placement="top">
                  <el-button type="text" size="small" @click="toTodoList">{{$i18nMy.t('更多')}}</el-button>
                </el-tooltip>
              </el-button-group>
            </el-row>
            <el-dialog
             :title="$i18nMy.t('查看进度')"
             :close-on-click-modal="true"
             :visible.sync="visible"
              v-dialogDrag
             width="70%"
             height="600px">
                <flow-chart ref="preview" :processInstanceId="processInstanceId"></flow-chart>
             </el-dialog>
        </el-card>
        </el-col>
        <el-col
          :lg="12" :md="12" :sm="12" :xs="12"
        >
          <el-card v-show="false"
            :header="$i18nMy.t('委托待办')"
          >
            <el-table
                :data="delegateList"
                size = "small"
                v-loading="loading"
                height="245px"
                class="table my-table-margin">
                <el-table-column
                  prop="vars.title"
                  width="180px"
                 show-overflow-tooltip
                  :label="$i18nMy.t('申请单号')">
                      <template slot-scope="scope">
                        <el-link  type="primary" :underline="false" v-if="scope.row.status === 'todo'"  @click="todo(scope.row)">{{scope.row.vars.title}}</el-link>
                        <span v-else>{{scope.row.vars.title}}</span>
                      </template>
                </el-table-column>
                <el-table-column
                  prop="processDefinitionName"
                  show-overflow-tooltip
                  :label="$i18nMy.t('流程名称')">
                </el-table-column>
                <el-table-column
                  prop="remarks"
                  show-overflow-tooltip
                  :label="$i18nMy.t('流程信息')">
                  <template slot-scope="scope">
                      {{scope.row.remarks || scope.row.vars.description}}
                  </template>
                </el-table-column>
                 <el-table-column
                  prop="task.name"
                  show-overflow-tooltip
                  :label="$i18nMy.t('当前环节')">
                   <template slot-scope="scope">
                     {{$i18nMy.t(scope.row.task.name)}}
                   </template>
                </el-table-column>
                 <el-table-column
                  prop="task.assignee"
                  show-overflow-tooltip
                  :label="$i18nMy.t('流程委托人')">
                </el-table-column>
                <el-table-column
                  prop="task.createTime"
                  show-overflow-tooltip
                  :label="$i18nMy.t('申请时间')">
                   <template slot-scope="scope">
                    {{scope.row.task.createTime | formatDate}}
                   </template>
                </el-table-column>
                <el-table-column
                  fixed="right"
                  :key="Math.random()"
                  header-align="center"
                  align="center"
                  v-if="false"
                  width="150"
                  :label="$i18nMy.t('操作')">
                  <template slot-scope="scope">
                    <!-- <el-button v-if="scope.row.status === 'claim'" type="text" size="small" @click="claim(scope.row)">{{$i18nMy.t('签收任务')}}</el-button> -->
                    <el-button type="text" size="small" @click="todo(scope.row)">{{$i18nMy.t('办理')}}</el-button>
                    <!--  <el-button v-if="scope.row.status === 'todo'" type="text" size="small" @click="transferTask(scope.row)">{{$i18nMy.t('委派')}}</el-button> -->
                    <!--  <el-button v-if="scope.row.claimTime" type="text" size="small" @click="unclaim(scope.row)">{{$i18nMy.t('取消签收')}}</el-button> -->
                    <el-button  type="text" size="small" @click="trace(scope.row)">{{$i18nMy.t('进度')}}</el-button>
                  </template>
                </el-table-column>
              </el-table>
              <el-row>
                <el-button-group class="pull-right" style="margin-top: 15px;">
                  <el-tooltip class="item" effect="dark" content="More" placement="top">
                    <el-button type="text" size="small" @click="toDelegateList">{{$i18nMy.t('更多')}}</el-button>
                  </el-tooltip>
                </el-button-group>
              </el-row>
              <el-dialog
               :title="$i18nMy.t('查看进度')"
               :close-on-click-modal="true"
               :visible.sync="visible"
                v-dialogDrag
               width="70%"
               height="600px">
                  <flow-chart ref="preview" :processInstanceId="processInstanceId"></flow-chart>
               </el-dialog>
          </el-card>
          </el-col>
        </el-row>
        <el-row :gutter="10" style="margin-bottom: 10px">
          <el-col
            :lg="12" :md="12" :sm="12" :xs="12"
          >
        <el-card
          :header="$i18nMy.t('已办任务')"
        >
            <el-table
              :data="dataList1"
              size = "small"
              v-loading="loading1"
              height="245px"
              class="table my-table-margin">

              <el-table-column
                prop="vars.title"
                show-overflow-tooltip
                width="180px"
                :label="$i18nMy.t('申请单号')">
                <template slot-scope="scope">
                  <el-link  type="primary" :underline="false" @click="detail(scope.row)">{{scope.row.vars.title}}</el-link>
                </template>
              </el-table-column>
              <el-table-column
                prop="processDefinitionName"
                show-overflow-tooltip
                :label="$i18nMy.t('流程名称')">
              </el-table-column>
              <el-table-column
                prop="remarks"
                show-overflow-tooltip
                :label="$i18nMy.t('流程信息')">
                <template slot-scope="scope">
                    {{scope.row.remarks || scope.row.vars.description}}
                </template>
              </el-table-column>
              <el-table-column
                prop="name"
                show-overflow-tooltip=""
                :label="$i18nMy.t('我的经办')">
                <template slot-scope="scope">
                  {{scope.row.name}}
                     <!-- <el-button v-if="scope.row.back" type="warning" size="mini"
                            @click="callback(scope.row)">{{$i18nMy.t('撤销')}}</el-button> -->
                </template>
              </el-table-column>
              <!-- <el-table-column
                prop="vars.userName"
                :label="$i18nMy.t('流程发起人')">
              </el-table-column> -->

              <!-- <el-table-column
                prop="status"
                show-overflow-tooltip
                :label="$i18nMy.t('办理状态')">
              </el-table-column> -->
              <el-table-column
                prop="endTime"
                show-overflow-tooltip
                :label="$i18nMy.t('处理时间')">
                 <template slot-scope="scope">
                  {{scope.row.endTime | formatDate}}
                 </template>
              </el-table-column>
              <!-- <el-table-column
                prop="currentTask.name"
                show-overflow-tooltip
                :label="$i18nMy.t('当前环节')">
              </el-table-column> -->
              <el-table-column
                prop="status"
                show-overflow-tooltip
                :label="$i18nMy.t('流程状态')">
                <template slot-scope="scope">
                 {{$i18nMy.t(scope.row.status)}}
                </template>
              </el-table-column>
              <el-table-column
                prop="currentTask.assigneeName"
                show-overflow-tooltip
                :label="$i18nMy.t('当前处理人')">
              </el-table-column>
              <el-table-column
                fixed="right"
                header-align="center"
                align="center"
                v-if="false"
                width="100"
                :label="$i18nMy.t('操作')">
                <template slot-scope="scope">
                  <el-button  type="text" size="small"
                            @click="detail(scope.row)">{{$i18nMy.t('查看')}}</el-button>
                </template>
              </el-table-column>
            </el-table>
            <el-row>
              <el-button-group class="pull-right" style="margin-top: 15px;">
                <el-tooltip class="item" effect="dark" content="More" placement="top">
                  <el-button type="text" size="small" @click="toHistoryList">{{$i18nMy.t('更多')}}</el-button>
                </el-tooltip>
              </el-button-group>
            </el-row>
        </el-card>
      </el-col>
      <el-col
        :lg="12" :md="12" :sm="12" :xs="12"
      >
        <el-card
          :header="$i18nMy.t('我的申请')"
        >
            <el-table
              :data="dataList3"
              size = "small"
              v-loading="loading3"
              height="245px"
              class="table my-table-margin">
              <el-table-column
                prop="vars.title"
                show-overflow-tooltip
                width="180px"
                :label="$i18nMy.t('申请单号')">
                <template slot-scope="scope">
                  <el-link  type="primary" :underline="false" @click="detail(scope.row)">{{scope.row.vars.title}}</el-link>
                </template>
              </el-table-column>
              <el-table-column
                prop="processDefinitionName"
                show-overflow-tooltip
                :label="$i18nMy.t('流程名称')">
              </el-table-column>
              <el-table-column
                prop="remarks"
                show-overflow-tooltip
                :label="$i18nMy.t('流程信息')">
              </el-table-column>
               <!-- <el-table-column
                prop="act.name"
                show-overflow-tooltip
                :label="$i18nMy.t('当前环节')">
              </el-table-column> -->
              <el-table-column
                prop="status"
                show-overflow-tooltip
                :label="$i18nMy.t('流程状态')">
                <template slot-scope="scope">
                 {{$i18nMy.t(scope.row.status)}}
                </template>
              </el-table-column>
              <el-table-column
                prop="act.assigneeName"
                show-overflow-tooltip
                :label="$i18nMy.t('当前处理人')">
              </el-table-column>
               <!-- <el-table-column
                prop="status"
                show-overflow-tooltip
                :label="$i18nMy.t('办理状态')">
              </el-table-column> -->
              <el-table-column
                prop="startTime"
                show-overflow-tooltip
                :label="$i18nMy.t('申请时间')">
                 <template slot-scope="scope">
                  {{scope.row.startTime | formatDate}}
                 <!-- <p class="text-grey">{{scope.row.endTime | formatDate}}</p> -->
                 </template>
              </el-table-column>

              <el-table-column
                fixed="right"
                header-align="center"
                align="center"
                width="100"
                v-if="false"
                :label="$i18nMy.t('操作')">
                <template slot-scope="scope">
                   <el-button  type="text" size="small" @click="detail(scope.row)">{{$i18nMy.t('查看')}}</el-button>
                    <!-- <el-dropdown  size="small" style=" margin-left: 10px;">
                        <el-button type="text" size="small">
                              更多<i class="el-icon-arrow-down el-icon--right"></i>
                        </el-button>
                        <el-dropdown-menu slot="dropdown" >
                          <el-dropdown-item v-if="scope.row.code === 1" ><el-button type="text"  size="small" @click="urge(scope.row)">催办</el-button></el-dropdown-item>
                          <el-dropdown-item v-if="scope.row.code === 1"><el-button type="text"  size="small" @click="revoke(scope.row)">撤销</el-button></el-dropdown-item>
                          <el-dropdown-item v-if="scope.row.code === 3 || scope.row.code === 4"><el-button  type="text" color="red"  size="small" @click="restart(scope.row)">编辑</el-button></el-dropdown-item>
                        </el-dropdown-menu>
                    </el-dropdown> -->
                </template>
              </el-table-column>
            </el-table>
            <el-row>
              <el-button-group class="pull-left" style="margin-top: 15px;">
                <el-tooltip class="item" effect="dark" content="More" placement="top">
                  <el-button type="text" size="small" @click="toDraftList">{{$i18nMy.t('我的草稿')}}</el-button>
                </el-tooltip>
              </el-button-group>
              <el-button-group class="pull-right" style="margin-top: 15px;">
                <el-tooltip class="item" effect="dark" content="More" placement="top">
                  <el-button type="text" size="small" @click="toApplyList">{{$i18nMy.t('更多')}}</el-button>
                </el-tooltip>
              </el-button-group>
            </el-row>
        </el-card>
      </el-col>
    </el-row>
  </div>
  </div>
</template>

<script>
import pick from 'lodash.pick'
import Vue from 'vue'
import * as numeral from 'numeral'
const yuan = (val) => `&yen; ${numeral(val).format('0,0')}`

export default Vue.extend({
  data () {
    return {
      targetTime: new Date().getTime() + 3900000,
      searchForm: {
        beginDate: '',
        endDate: ''
      },
      searchDates: '',

      menuList: [
        {
      		id: "PR_enquiry",
      		"url": "/flow/pr/OaPrNewList",
      		"name": "Enquiry for PR applications",
          "permission": "flow:pr:oaPrNew:list"
      	},
        {
        	id: "Application_enquiry",
        	"url": "/flowable/task/AllList",
        	"name": "Enquiry for ALL applicatons",
          "permission": "flow:task:allList"
        },
        {
        	id: "PR_draft",
        	"url": "/flow/pr/OaPrNewList?isDraft=1&title=我的草稿",
        	"name": "My Draft",
          "permission": "flow:pr:oaPrNew:list:draft"
        }
      ],

      delegateList: [],
      pageNo3: 1,
      pageSize3: 5,
      total3: 0,
      loading3: false,

      dataList: [],
      pageNo: 1,
      pageSize: 5,
      total: 0,
      loading: false,
      dataList1: [],
      total1: 0,
      loading1: false,
      dataList2: [],
      total2: 0,
      loading2: false,
      dataList3: [],
      total3: 0,
      loading3: false,
      visible: false,
      currentTask: null,
      dataListSelections: [],
      processInstanceId: ''
    }
  },
  components: {
  },
  activated () {
    this.refreshDelegateList()
    this.refreshList()
    this.refreshList1()
    this.refreshList2()
    this.refreshList3()
  },
  computed: {
    tags () {
      return {
        'list|100': [
          {
            name: '@city',
            'value|1-100': 150,
            'type|0-2': 1
          }
        ]
      }
    },
    userName () {
      return JSON.parse(localStorage.getItem('user')).name
    }
  },
  mounted () {

  },
  methods: {
    refreshList3 () {
      this.loading3 = true
      this.$http({
        url: '/flowable/task/myApplyed',
        method: 'get',
        params: {
          'pageNo': this.pageNo,
          'pageSize': this.pageSize,
          ...this.searchForm
        }
      }).then(({data}) => {
        if (data && data.success) {
          this.dataList3 = data.page.list
          this.total3 = data.page.count
          this.loading3 = false
        }
      })
    },
    refreshList2 () {
      this.loading2 = true
      this.$http({
        url: '/flowable/process/list',
        method: 'get',
        params: {
          'pageNo': 1,
          'pageSize': 1000,
          'category': 'Self Service',
          ...this.searchForm
        }
      }).then(({data}) => {
        if (data && data.success) {
          this.dataList2 = data.page.list
          this.total2 = data.page.count
          this.loading2 = false
        }
      })
    },
    refreshList1 () {
      this.loading1 = true
      this.$http({
        url: '/flowable/task/historic/',
        method: 'get',
        params: {
          'pageNo': this.pageNo,
          'pageSize': this.pageSize,
          ...this.searchForm
        }
      }).then(({data}) => {
        if (data && data.success) {
          this.dataList1 = data.page.list
          this.total1 = data.page.count
          this.loading1 = false
        }
      })
    },
    refreshList () {
      this.loading = true
      this.$http({
        url: '/flowable/task/todo',
        method: 'get',
        params: {
          'pageNo': this.pageNo,
          'pageSize': this.pageSize,
          ...this.searchForm
        }
      }).then(({data}) => {
        if (data && data.success) {
          this.dataList = data.page.list
          this.total = data.page.count
          this.loading = false
        }
      })
    },
    refreshDelegateList () {
      this.loading = true
      this.$http({
        url: '/flowable/task/delegate',
        method: 'get',
        params: {
          'pageNo': this.pageNo3,
          'pageSize': this.pageSize3,
          ...this.searchForm
        }
      }).then(({data}) => {
        if (data && data.success) {
          this.delegateList = data.page.list
          this.total3 = data.page.count
          this.loading3 = false
        }
      })
    },
    toTargetPage (targetUrl) {
      this.$router.push(targetUrl)
    },
    toTodoList () {
      this.$router.push(`/flowable/task/TodoList`)
    },
    toDelegateList () {
      this.$router.push(`/flowable/task/DelegateList`)
    },
    toHistoryList () {
      this.$router.push(`/flowable/task/HistoryList`)
    },
    toApplyList () {
      this.$router.push(`/flowable/task/ApplyList`)
    },
    toDraftList () {
      this.$router.push(`/flow/pr/OaPrNewList?isDraft=1&title=我的草稿`)
    },

    todo (row) {
      this.$http.get('/flowable/task/getTaskDef', {params: {
        taskId: row.task.id,
        taskName: row.task.name,
        taskDefKey: row.task.taskDefinitionKey,
        procInsId: row.task.processInstanceId,
        procDefId: row.task.processDefinitionId,
        procDefKey: row.task.processDefKey,
        status: row.status
      }}).then(({data}) => {
        if (data.success) {
          var param={parentForm: "TaskForm", formTitle: `${row.vars.title}`, title: `Do:${row.vars.title}`, applyUserId: `${row.vars.applyUserId}`,
            ...pick(data.flow, 'formType', 'formReadOnly', 'formUrl', 'procDefKey', 'taskDefKey', 'procInsId', 'procDefId', 'taskId', 'status', 'title', 'businessId', 'lastTaskDefKey')}
          this.$router.push({
            path: '/flowable/task/TaskForm',
            query: param
          })
        }
      })
    },
    trace (row) {
      this.processInstanceId = row.task.processInstanceId
      this.visible = true
      this.$nextTick(() => {
        this.$refs.preview.init()
      })
    },
    detail (row) {
      //debugger
      this.$http.get('/flowable/task/getTaskDef', {params: {
        taskDefKey: 'ITChecker', // row.task.taskDefinitionKey,
        procInsId: row.processInstanceId,
        procDefId: row.processDefinitionId
      }}).then(({data}) => {
        if (data.success) {
          this.$router.push({
            path: '/flowable/task/TaskFormDetail',
            query: {parentForm: "TaskFormDetail", readOnly: true, taskId: row.executionId, title: 'View:'+`${row.vars.title}`, formTitle: `${row.vars.title}`, processStatus: `${row.status}`, ...pick(data.flow, 'formType', 'formUrl', 'procDefKey', 'taskDefKey', 'procInsId', 'procDefId', 'taskId', 'status', 'title', 'businessId')}
          })
        }
      })
    },
    start (row) {
      // 读取流程表单
      let tabTitle = $i18nMy.t('发起流程') + '：' + $i18nMy.t(`${row.name}`)
      let processTitle = ''//`${this.userName} start process : ${row.name}  ${this.moment(new Date()).format('YYYY-MM-DD HH:mm')} `
      this.$http.get('/flowable/task/getTaskDef', {params: {
        procDefId: row.id,
        status: 'start'
      }}).then(({data}) => {
        if (data.success) {
          this.$router.push({
            path: '/flowable/task/TaskForm',
            query: {parentForm: "TaskForm",procDefId: row.id, procDefKey: row.key, status: 'start', title: tabTitle, formType: data.flow.formType, formUrl: data.flow.formUrl, formTitle: processTitle}
          })
        }
      })
    },
    yuan (value) {
      return yuan(value)
    },
    numeral (value) {
      return numeral(value)
    },
    defaultFormatter (value) {
      return defaultFormatter(value)
    },
    getRandomColor() {
     return "yuan-color-" + (Math.floor(Math.random() * (5)) + 1)
    }
  }
})
</script>

<style lang="scss" scoped>
@import '@/assets/scss/theme.scss';
.el-table--border::after, .el-table--group::after, .el-table::before {
    content: '';
    position: absolute;
    background-color: #ffffff;
    z-index: 1;
}
.my-table-margin {
    margin-top: -15px;
}
.func-text {
  margin-top: 10px;
}
.yuan{
  background-color: #1c3f95;
  font-size: 25px;
  color: #ffffff;
  width: 120px;
  height: 120px;
  border-radius: 60px;
  text-align: center;
  margin:0 auto;
  line-height: 120px;
}
.yuan1{
  background-color: #1c3f95;
  font-size: 16px;
  color: #ffffff;
  width: 40px;
  height: 40px;
  border-radius: 20px;
  text-align: center;
  line-height: 40px;
  float: left;
}
.task-name {
  color:#010407;
  margin-left:5px;
  width: calc(100% - 45px);
  text-overflow:ellipsis;
  overflow:hidden;
  white-space:nowrap;
  text-align:left;
  line-height: 16px;
  font-size: 14px;
}
.yuan-color-1 {
  background-color: #2f6dfd;
}
.yuan-color-2 {
  background-color: #5e87f9;
}
.yuan-color-3 {
  background-color: #5e3fd8;
}
.yuan-color-4 {
  background-color: #1ea0cf;
}
.yuan-color-5 {
  background-color: #2f6bf9;
}
.map-chart {
  padding-top: 24px;
  height: 457px;
  text-align: center;
  img {
    display: inline-block;
    max-width: 100%;
    max-height: 437px;
  }
}

.pie-card {
 .pie-stat {
    font-size: 24px;
  }
}

@media screen and (max-width: $screen-lg) {
  .map-chart {
    height: auto;
  }
}

</style>
