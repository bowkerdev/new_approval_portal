<template>
  <div class="page">
      <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
        <el-form-item prop="title">
           <el-input size="small" v-model="searchForm.title" :placeholder="$i18nMy.t('申请单号')" clearable></el-input>
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
            :start-placeholder="$i18nMy.t('开始日期')"
            :end-placeholder="$i18nMy.t('结束日期')"
            :picker-options="pickerOptions">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button  type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
          <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
        </el-form-item>
      </el-form>
      <div class="bg-white top">
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
          v-loading="loading"
          height="calc(100% - 100px)"
          @selection-change="selectionChangeHandle"
          class="table">
         <!-- <el-table-column
            type="selection"
            header-align="center"
            align="center"
            width="50">
          </el-table-column> -->
          <el-table-column
            prop="vars.title"
            show-overflow-tooltip
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
           <el-table-column
            prop="taskName"
            show-overflow-tooltip
            :label="$i18nMy.t('当前环节')">
          </el-table-column>
           <el-table-column
            prop="status"
            show-overflow-tooltip
            :label="$i18nMy.t('流程状态')">
            <template slot-scope="scope">
               {{$i18nMy.t(scope.row.status)}}
            </template>
          </el-table-column>
          <el-table-column
            prop="act.name"
            show-overflow-tooltip
            :label="$i18nMy.t('当前环节')">
          </el-table-column>
          <el-table-column
            prop="act.assigneeName"
            show-overflow-tooltip
            :label="$i18nMy.t('当前处理人')">
          </el-table-column>
          <el-table-column
            prop="startTime"
            show-overflow-tooltip
            :label="$i18nMy.t('开始时间')+' / '+$i18nMy.t('结束时间')">
             <template slot-scope="scope">
              <p>{{scope.row.startTime | formatDate}}</p>
              <p class="text-grey">{{scope.row.endTime | formatDate}}</p>
             </template>
          </el-table-column>

          <el-table-column v-if="false"
            width="150"
            :label="$i18nMy.t('操作')">
            <template slot-scope="scope">
               <el-button  type="text" size="small" @click="detail(scope.row)">{{$i18nMy.t('查看')}}</el-button>
                <!-- <el-dropdown  size="small" style=" margin-left: 10px;">
                    <el-button type="text" size="small">
                          {{$i18nMy.t('更多')}}<i class="el-icon-arrow-down el-icon--right"></i>
                    </el-button>
                    <el-dropdown-menu slot="dropdown" >
                      <el-dropdown-item v-if="scope.row.code === 1" ><el-button type="text"  size="small" @click="urge(scope.row)">催办</el-button></el-dropdown-item>
                      <el-dropdown-item v-if="scope.row.code === 1"><el-button type="text"  size="small" @click="revoke(scope.row)">{{$i18nMy.t('撤销')}}</el-button></el-dropdown-item>
                      <el-dropdown-item v-if="scope.row.code === 3 || scope.row.code === 4"><el-button  type="text" color="red"  size="small" @click="restart(scope.row)">{{$i18nMy.t('编辑')}}</el-button></el-dropdown-item>
                    </el-dropdown-menu>
                </el-dropdown> -->
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
        title="查看流程历史"
        :close-on-click-modal="true"
        :visible.sync="visible"
         v-dialogDrag
        height="600px">

          <iframe :src="processPhotoUrl" frameborder="0" scrolling="auto" width="100%" height="600px"></iframe>
        </el-dialog>
        <urge-form ref="urgeForm"></urge-form>
  </div>
</template>

<script>
  import pick from 'lodash.pick'
  import UrgeForm from './UrgeForm'
  export default {
    data () {
      return {
        searchForm: {
          title: '',
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
        processPhotoUrl: '',
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
    components: {
      UrgeForm
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
          url: '/flowable/task/myApplyed',
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
      trace (row) {
        this.processPhotoUrl = `${this.$http.BASE_URL}/flowable/task/trace/photo/${row.processInstanceId}`
        this.visible = true
      },
      detail (row) {
        this.$http.get('/flowable/task/getTaskDef', {params: {
          procInsId: row.processInstanceId,
          procDefId: row.processDefinitionId
        }}).then(({data}) => {
          if (data.success) {
            this.$router.push({
              path: '/flowable/task/TaskFormDetail',
              query: {parentForm: "TaskFormDetail", readOnly: true, title: 'View:'+row.vars.title, formTitle: row.vars.title, ...pick(data.flow, 'formType', 'formUrl', 'procDefKey', 'taskDefKey', 'procInsId', 'procDefId', 'taskId', 'status', 'title', 'businessId')}
            })
          }
        })
      },
      // 重新填写
      restart (row) {
              // 读取流程表单
        this.$http.get('/flowable/task/getTaskDef', {params: {
          procInsId: row.processInstanceId,
          procDefId: row.processDefinitionId
        }}).then(({data}) => {
          if (data.success) {
            this.$router.push({
              path: '/flowable/task/TaskFormEdit',
              query: {status: 'start', title: row.vars.title, formTitle: row.vars.title, ...pick(data.flow, 'formType', 'formUrl', 'procDefKey', 'taskDefKey', 'procInsId', 'procDefId', 'taskId', 'status', 'title', 'businessId')}
            })
          }
        })
      },
          // 撤销申请
      revoke (row) {
        this.$confirm(`确定要撤销该流程吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http.post('/flowable/process/revokeProcIns', {'id': row.processInstanceId}).then(({data}) => {
            if (data && data.success) {
              this.$message.success(data.msg)
              this.refreshList()
            }
          })
        })
      },
      urge (row) {
        this.$refs.urgeForm.init()
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
<style>
p {
  margin: 0;
  padding: 0;
}

.text-grey {
    color: #999!important;
}
</style>
