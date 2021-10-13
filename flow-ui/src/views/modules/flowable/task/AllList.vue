<template>
  <div class="page">
      <el-form size="small" :inline="true"  class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
        <el-form-item prop="title">
           <el-input size="small" v-model="searchForm.title" :placeholder="$i18nMy.t('申请单号')" clearable></el-input>
        </el-form-item>
        <el-form-item prop="processDefinitionKey">
           <el-input size="small" v-model="searchForm.processDefinitionKey" :placeholder="$i18nMy.t('流程名称')" clearable></el-input>
        </el-form-item>
        <el-form-item prop="requestRiority">
           <el-select size="small" v-model="searchForm.status" :placeholder="$i18nMy.t('流程状态')"  style="width: 100%;">
             <el-option
               v-for="item in $dictUtils.getDictList('flow_status')"
               :key="item.value"
               :label="item.label"
               :value="item.value">
             </el-option>
           </el-select>
        </el-form-item>
        <el-form-item prop="searchDates">
          <el-date-picker
            v-model="searchDates"
            type="daterange"
            size="small"
            align="right"
            value-format="yyyy-MM-dd hh:mm:ss"
            unlink-panels
            range-separator="-"
            :start-placeholder="$i18nMy.t('开始申请时间')"
            :end-placeholder="$i18nMy.t('结束申请时间')"
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
            prop="vars.title"
           show-overflow-tooltip
            :label="$i18nMy.t('申请单号')">
                <template slot-scope="scope">
                  <el-link  type="primary" :underline="false"   @click="detail(scope.row)">{{scope.row.vars.title}}</el-link>
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
           <!-- <el-table-column
            prop="task.name"
            :label="$i18nMy.t('当前环节')">
              <template slot-scope="scope">
                <el-tag>{{scope.row.task.name}}</el-tag>
             </template>
          </el-table-column> -->
           <el-table-column
            prop="vars.userName"
            :label="$i18nMy.t('流程发起人')">
          </el-table-column>
          <el-table-column
            prop="task.name"
            :label="$i18nMy.t('当前环节')">
          </el-table-column>
          <el-table-column
            prop="task.assigneeName"
            show-overflow-tooltip
            :label="$i18nMy.t('当前处理人')">
          </el-table-column>
          <el-table-column
            prop="status"
            :label="$i18nMy.t('流程状态')">
            <template slot-scope="scope">
             {{$i18nMy.t(scope.row.status)}}
            </template>
          </el-table-column>
          <el-table-column
            prop="task.createTime"
            show-overflow-tooltip
            :label="$i18nMy.t('申请时间')">
             <template slot-scope="scope">
              {{scope.row.task.createTime | formatDate}}
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
          title: '',
          processDefinitionKey: '',
          status: '',
          startTime: '',
          endTime: ''
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
          this.searchForm.startTime = this.searchDates[0]
          this.searchForm.endTime = this.searchDates[1]
        } else {
          this.searchForm.startTime = ''
          this.searchForm.endTime = ''
        }
      }
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/flowable/task/allList',
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
      detail (row) {
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
              path: '/flowable/task/TaskFormDetail',
              query: {readOnly: true, formTitle: row.vars.title, title: row.vars.title, ...pick(data.flow, 'formType', 'formUrl', 'procDefKey', 'taskDefKey', 'procInsId', 'procDefId', 'taskId', 'status', 'title', 'businessId')}
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
