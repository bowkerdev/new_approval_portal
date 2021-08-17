<template>
  <div class="page">
      <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
        <el-form-item :label="$i18nMy.t('完成时间')" prop="searchDates">
          <el-date-picker
            v-model="searchDates"
            type="daterange"
            size="small"
            align="right"
            value-format="yyyy-MM-dd hh:mm:ss"
            unlink-panels
            range-separator="至"
            start-:placeholder="$i18nMy.t('开始日期')"
            end-:placeholder="$i18nMy.t('结束日期')"
            :picker-options="pickerOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button  type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
          <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
        </el-form-item>
      </el-form>
      <div class="top bg-white">
      <el-row>
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
      </el-row>
        <el-table
          :data="dataList"
          size = "small"
          v-loading="loading"
          height="calc(100% - 80px)"
          @selection-change="selectionChangeHandle"
          class="table">
          <el-table-column
            type="selection"
            header-align="center"
            align="center"
            width="50">
          </el-table-column>
          <el-table-column
            prop="name"
            show-overflow-tooltip=""
            :label="$i18nMy.t('任务')">
            <template slot-scope="scope">
              {{scope.row.name}} 
                 <el-button v-if="scope.row.back" type="warning" size="mini"
                        @click="callback(scope.row)">{{$i18nMy.t('撤销')}}</el-button>
            </template>
          </el-table-column>
          <el-table-column
            prop="vars.title"
            show-overflow-tooltip
            min-width="180px"
            :label="$i18nMy.t('实例标题')">
          </el-table-column>
          <el-table-column
            prop="processDefinitionName"
            :label="$i18nMy.t('流程名称')">
          </el-table-column>
          <el-table-column
            prop="status"
            show-overflow-tooltip
            :label="$i18nMy.t('办理状态')">
            <template slot-scope="scope">
                <el-tag :type="scope.row.level"   effect="dark" size="small">{{scope.row.status}} </el-tag>
             </template>
          </el-table-column>
           <el-table-column
            prop="vars.userName"
            :label="$i18nMy.t('流程发起人')">
          </el-table-column>
          <el-table-column
            prop="currentTask.name"
            show-overflow-tooltip
            :label="$i18nMy.t('当前环节')">
          </el-table-column>
          <el-table-column
            prop="currentTask.assigneeName"
            show-overflow-tooltip
            :label="$i18nMy.t('当前处理人')">
          </el-table-column>
          <el-table-column
            prop="endTime"
            show-overflow-tooltip
            :label="$i18nMy.t('完成时间')">
             <template slot-scope="scope">
              {{scope.row.endTime | formatDate}}
             </template>
          </el-table-column>
          <el-table-column
            fixed="right"
            :key="Math.random()"
            header-align="center"
            align="center"
            width="100"
            :label="$i18nMy.t('操作')">
            <template slot-scope="scope">
              <el-button  type="text" size="small"
                        @click="detail(scope.row)">{{$i18nMy.t('历史')}}</el-button>
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
  </div>
</template>

<script>
  import pick from 'lodash.pick'
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
        dataListSelections: [],
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '最近一个月',
            onClick (picker) {
              const end = new Date()
              const start = new Date()
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
              picker.$emit('pick', [start, end])
            }
          }, {
            text: '最近三个月',
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
          url: '/flowable/task/historic/',
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
      detail (row) {
        this.$http.get('/flowable/task/getTaskDef', {params: {
          taskDefKey: row.taskDefinitionKey,
          procInsId: row.processInstanceId,
          procDefId: row.processDefinitionId
        }}).then(({data}) => {
          if (data.success) {
            this.$router.push({
              path: '/flowable/task/TaskFormDetail',
              query: {readOnly: true, taskId: row.executionId, title: `${row.processDefinitionName}【${row.name}】`, formTitle: `${row.processDefinitionName}`, ...pick(data.flow, 'formType', 'formUrl', 'procDefKey', 'taskDefKey', 'procInsId', 'procDefId', 'taskId', 'status', 'title', 'businessId')}
            })
          }
        })
      },
          // 取回
      callback (row) {
        this.$confirm(`确定撤销该已办任务吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http.post('/flowable/task/callback',
            {'processInstanceId': row.processInstanceId,
              'preTaskDefKey': row.taskDefinitionKey,
              'preTaskId': row.id,
              'currentTaskId': row.currentTask.id,
              'currentTaskDefKey': row.currentTask.taskDefinitionKey
            }).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
                this.refreshList()
              }
            })
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
