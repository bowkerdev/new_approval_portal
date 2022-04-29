<template>
  <div class="page bg-white">
      <el-form size="small" :inline="true"  class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
          <el-form-item prop="title">
                <el-input size="small" v-model="searchForm.title" :placeholder="$i18nMy.t('标题')" clearable></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
      <el-row>
        <el-button  type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0">{{$i18nMy.t('作废')}}</el-button>
      </el-row>
        <el-table
          :data="dataList"
          size = "small"
          height="calc(100% - 180px)"
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
            min-width="180px"
            :label="$i18nMy.t('标题')">
          </el-table-column>
          <el-table-column
            prop="processDefinitionName"
            :label="$i18nMy.t('流程名称')">
          </el-table-column>
           <el-table-column
            prop="status"
            label="当前节点">
            <template slot-scope="scope">
              {{scope.row.taskName}}
             </template>
          </el-table-column>
           <el-table-column
            prop="status"
            label="流程状态">
            <template slot-scope="scope">
                <el-tag  :type="scope.row.level"   effect="dark" size="small">{{scope.row.status}} </el-tag>
             </template>
          </el-table-column>
           <el-table-column
            prop="createBy.name"
            :label="$i18nMy.t('流程发起人')">
          </el-table-column>
         <el-table-column
            fixed="right"
            :key="Math.random()"
            header-align="center"
            align="center"
            width="200"
            :label="$i18nMy.t('操作')">
            <template slot-scope="scope">
              <el-button  type="text" size="small"
                        @click="detail(scope.row)">{{$i18nMy.t('详情')}}</el-button>
              <!-- <el-button  type="text" size="small"
                        @click="trace(scope.row)">{{$i18nMy.t('进度')}}</el-button> -->
              <el-button  type="text" size="small"
                        @click="del(scope.row.id)">{{$i18nMy.t('作废')}}</el-button>
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
       <el-dialog
        :title="$i18nMy.t('查看进度')"
        :close-on-click-modal="true"
        :visible.sync="visible"
         v-dialogDrag
        width="70%"
        height="600px">
          <flow-chart ref="preview" :processInstanceId="processInstanceId"></flow-chart>
        </el-dialog>
  </div>
</template>

<script>
  import pick from 'lodash.pick'
  // import FlowChart from '../modeler/FlowChart'
  export default {
    data () {
      return {
        searchForm: {
          beginDate: '',
          endDate: '',
          title: ''
        },
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        loading: false,
        visible: false,
        dataListSelections: [],
        processInstanceId: ''
      }
    },
    activated () {
      this.refreshList()
    },
    components: {
      // FlowChart
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/flowable/process/runningData',
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
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
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
        this.processInstanceId = row.processInstanceId
        this.visible = true
        this.$nextTick(() => {
          this.$refs.preview.init()
        })
      },
      detail (row) {
        this.$http.get('/flowable/task/getTaskDef', {params: {
          taskDefKey: row.activityId,
          procInsId: row.processInstanceId,
          procDefId: row.processDefinitionId
        }}).then(({data}) => {
          if (data.success) {
            this.$router.push({
              path: '/flowable/task/TaskFormDetail',
              query: {parentForm: "TaskFormDetail", readOnly: true, title: row.vars.title, formTitle: row.vars.title, ...pick(data.flow, 'formType', 'formUrl', 'procDefKey', 'taskDefKey', 'procInsId', 'procDefId', 'taskId', 'status', 'title', 'businessId')}
            })
          }
        })
      },
          // 撤销申请
      del (id) {
        let ids = id || this.dataListSelections.map(item => {
          return item.id
        }).join(',')
        this.$prompt('作废原因', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }).then(({ value }) => {
          this.loading = true
          this.$http.delete('/flowable/process/deleteProcIns', {params: {
            ids: ids,
            reason: value}}).then(({data}) => {
              this.refreshList()
              this.$message.success('作废成功')
            })
          this.loading = false
        })
      }
    }
  }
</script>
