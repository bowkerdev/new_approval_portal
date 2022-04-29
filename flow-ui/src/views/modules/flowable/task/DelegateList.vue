<template>
  <div class="page">
      <el-form size="small" :inline="true"  class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
        <el-form-item :label="$i18nMy.t('申请时间')" prop="searchDates">
          <el-date-picker
            v-model="searchDates"
            type="daterange"
            size="small"
            align="right"
            value-format="yyyy-MM-dd hh:mm:ss"
            unlink-panels
            range-separator="~"
            start-:placeholder="$i18nMy.t('开始日期')"
            end-:placeholder="$i18nMy.t('结束日期')"
            :picker-options="pickerOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button  type="primary" @click="refreshList()" size="small" icon="el-icon-search">{{$i18nMy.t('查询')}}</el-button>
          <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">{{$i18nMy.t('重置')}}</el-button>
        </el-form-item>
      </el-form>
      <div class="top bg-white">
      <!-- <el-row>
        <el-button-group class="pull-right">
          <el-tooltip class="item" effect="dark" content="刷新" placement="top">
            <el-button
              type="default"
              size="small"
              icon="el-icon-refresh"
              @click="refreshList">
            </el-button>
          </el-tooltip>
        </el-button-group>
      </el-row> -->
        <el-table
          :data="dataList"
          size = "small"
          height="calc(100% - 120px)"
          v-loading="loading"
          @selection-change="selectionChangeHandle"
          class="table">
          <el-table-column
            type="selection"
            header-align="center"
            align="center"
            width="50">
          </el-table-column>
          <el-table-column
            prop="vars.title"
           show-overflow-tooltip
            :label="$i18nMy.t('申请单号')">
                <template slot-scope="scope">
                  <el-link  type="primary" :underline="false" v-if="scope.row.status === 'todo'"  @click="todo(scope.row)">{{scope.row.vars.title}}</el-link>
                  <span v-else>{{scope.row.vars.title}}</span>
                </template>
          </el-table-column>
          <el-table-column
            prop="processDefinitionName"
            :label="$i18nMy.t('流程名称')">
          </el-table-column>
          <el-table-column
            prop="remarks"
            show-overflow-tooltip
            :label="$i18nMy.t('流程信息')">
          </el-table-column>
           <el-table-column
            prop="task.name"
            :label="$i18nMy.t('当前环节')">
              <template slot-scope="scope">
                <el-tag>{{scope.row.task.name}}</el-tag>
             </template>
          </el-table-column>
          <el-table-column
            prop="task.assignee"
            show-overflow-tooltip
            :label="$i18nMy.t('流程委托人')">
          </el-table-column>
           <el-table-column
            prop="vars.userName"
            :label="$i18nMy.t('流程发起人')">
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
            width="200"
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
       <el-pagination
        @size-change="sizeChangeHandle"
        @current-change="currentChangeHandle"
        :current-page="pageNo"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        :total="total"
        background
        layout="total, sizes, prev, pager, next, jumper">
      </el-pagination>
      </div>
       <el-dialog
        :title="$i18nMy.t('查看进度')"
        :close-on-click-modal="true"
        :visible.sync="visible"
         v-dialogDrag
        width="70%"
        height="600px">

          <flow-chart ref="preview" :processInstanceId="processInstanceId"></flow-chart>
        </el-dialog>
        <user-select ref="userSelect" :limit="1" @doSubmit="selectUsersToTransferTask"></user-select>

  </div>
</template>

<script>
  // import FlowChart from '../modeler/FlowChart'
  import pick from 'lodash.pick'
  import UserSelect from '@/components/userSelect/UserSelectDialog'
  export default {
    data () {
      return {
        searchForm: {
          beginDate: '',
          endDate: ''
        },
        searchDates: '',
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        visible: false,
        currentTask: null,
        dataListSelections: [],
        processInstanceId: '',
        pickerOptions: {
          shortcuts: [{
            text: this.$i18nMy.t('最近一周'),
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: this.$i18nMy.t('最近一个月'),
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: this.$i18nMy.t('最近三个月'),
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
              picker.$emit('pick', [start, end])
            }
          }]
        }
      }
    },
    activated () {
      this.refreshList()
    },
    components: {
      UserSelect
      // FlowChart
    },
    watch: {
      searchDates () {
        if (this.searchDates) {
          this.searchForm.beginDate = this.searchDates[0]
          this.searchForm.endDate = this.searchDates[1]
        } else {
          this.searchForm.beginDate = ''
          this.searchForm.endDate = ''
        }
      }
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/flowable/task/delegate',
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
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageNo = 1
        this.refreshList()
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageNo = val
        this.refreshList()
      },
         // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      claim (row) {
        this.$http.post('/flowable/task/claim', {taskId: row.task.id}).then(({data}) => {
          if (data.success) {
            this.$message.success(data.msg)
            this.refreshList()
          }
        })
      },
      unclaim (row) {
        this.$http.post('/flowable/task/unclaim', {taskId: row.task.id}).then(({data}) => {
          if (data.success) {
            this.$message.success(data.msg)
            this.refreshList()
          }
        })
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
            this.$router.push({
              path: '/flowable/task/TaskForm',
              query: {parentForm: "TaskForm",formTitle: `${row.vars.title}`, title: `审批【${row.task.name || ''}】`, ...pick(data.flow, 'formType', 'formReadOnly', 'formUrl', 'procDefKey', 'taskDefKey', 'procInsId', 'procDefId', 'taskId', 'status', 'title', 'businessId', 'lastTaskDefKey')}
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
      transferTask (row) {
        this.currentTask = row.task
        this.$refs.userSelect.init()
      },
      selectUsersToTransferTask (user) {
        this.$http.post('/flowable/task/delegate', {taskId: this.currentTask.id, userId: user[0].id}).then(({data}) => {
          this.$message.success(data.msg)
          this.refreshList()
        })
      },
      resetSearch () {
        this.searchDates = ''
        this.$refs.searchForm.resetFields()
        this.$nextTick(() => {
          this.refreshList()
        })
      }
    }
  }
</script>
