<template>
  <div>
      <el-form :inline="true" v-show="isSearchCollapse" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
         <el-form-item prop="url">
                <el-input size="small" v-model="searchForm.url" placeholder="接口名称" clearable></el-input>
         </el-form-item>
         <el-form-item prop="isSuccee">
                <el-input size="small" v-model="searchForm.isSuccee" placeholder="是否正确执行" clearable></el-input>
         </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询'})}</el-button>
            <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
        <!-- 导入导出-->
      <el-form :inline="true" v-show="isImportCollapse"  class="query-form" ref="importForm">
         <el-form-item>
          <el-button type="default" @click="downloadTpl()" size="small">{{$i18nMy.t('下载模板')}}</el-button>
         </el-form-item>
         <el-form-item prop="loginName">
            <el-upload
              class="upload-demo"
              :action="`${this.$http.BASE_URL}/log/asynHttpLog/import`"
              :on-success="uploadSuccess"
               :show-file-list="true">
              <el-button size="small" type="primary">{{$i18nMy.t('点击上传')}}</el-button>
              <div slot="tip" class="el-upload__tip">{{$i18nMy.t('只允许导入“xls”或“xlsx”格式文件！')}}</div>
            </el-upload>
        </el-form-item>
      </el-form>
      <el-row>
        <el-button v-if="hasPermission('log:asynHttpLog:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('log:asynHttpLog:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
        <el-button v-if="hasPermission('log:asynHttpLog:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}
        </el-button>
        <el-button-group class="pull-right">
            <el-button
              type="default"
              size="small"
              icon="el-icon-search"
              @click="isSearchCollapse = !isSearchCollapse, isImportCollapse=false">
            </el-button>
            <el-button v-if="hasPermission('log:asynHttpLog:import')" type="default" size="small" icon="el-icon-upload2" :title="$i18nMy.t('导入')" @click="isImportCollapse = !isImportCollapse, isSearchCollapse=false"></el-button>
            <el-button v-if="hasPermission('log:asynHttpLog:export')" type="default" size="small" icon="el-icon-download" :title="$i18nMy.t('导出')" @click="exportExcel()"></el-button>
            <el-button
              type="default"
              size="small"
              icon="el-icon-refresh"
              @click="refreshList">
            </el-button>
        </el-button-group>
      </el-row>
    <el-table
      :data="dataList"
      border
      size="medium"
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
      v-loading="loading"
      class="table">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
    <el-table-column
        prop="url"
        show-overflow-tooltip
        sortable="custom"
        label="接口名称">
            <template slot-scope="scope">
              <el-link  type="primary" :underline="false" v-if="hasPermission('log:asynHttpLog:edit')" @click="edit(scope.row.id)">{{scope.row.url}}</el-link>
              <el-link  type="primary" :underline="false" v-else-if="hasPermission('log:asynHttpLog:view')"  @click="view(scope.row.id)">{{scope.row.url}}</el-link>
              <span v-else>{{scope.row.url}}</span>
            </template>
      </el-table-column>
    <el-table-column
        prop="isSuccee"
        show-overflow-tooltip
        sortable="custom"
        label="是否正确执行">
      </el-table-column>
    <el-table-column
        prop="execTime"
        show-overflow-tooltip
        sortable="custom"
        label="执行时间">
      </el-table-column>
    <el-table-column
        prop="param"
        show-overflow-tooltip
        sortable="custom"
        label="入参">
      </el-table-column>
    <el-table-column
        prop="returnString"
        show-overflow-tooltip
        sortable="custom"
        label="返回结果">
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        fixed="right"
        width="200"
        label="操作">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('log:asynHttpLog:view')" type="text" icon="el-icon-view" size="small" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('log:asynHttpLog:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('log:asynHttpLog:del')" type="text"  icon="el-icon-delete" size="small" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
        <!-- 弹窗, 新增 / 修改 -->
    <AsynHttpLogForm  ref="asynHttpLogForm" @refreshDataList="refreshList"></AsynHttpLogForm>
  </div>
</template>

<script>
  import AsynHttpLogForm from './AsynHttpLogForm'
  export default {
    data () {
      return {
        searchForm: {
          url: '',
          isSuccee: ''
        },
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        orderBy: '',
        dataListSelections: [],
        isSearchCollapse: false,
        isImportCollapse: false,
        loading: false
      }
    },
    components: {
      AsynHttpLogForm
    },
    activated () {
      this.refreshList()
    },

    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/log/asynHttpLog/list',
          method: 'get',
          params: {
            'pageNo': this.pageNo,
            'pageSize': this.pageSize,
            'orderBy': this.orderBy,
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

    // 排序
      sortChangeHandle (obj) {
        if (obj.order === 'ascending') {
          this.orderBy = obj.prop + ' asc'
        } else if (obj.order === 'descending') {
          this.orderBy = obj.prop + ' desc'
        } else {
          this.orderBy = ''
        }
        this.refreshList()
      },
      // 新增
      add () {
        this.$refs.asynHttpLogForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.asynHttpLogForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.asynHttpLogForm.init('view', id)
      },
      // 删除
      del (id) {
        let ids = id || this.dataListSelections.map(item => {
          return item.id
        }).join(',')
        this.$confirm($i18nMy.t(`确定删除所选项吗?`), $i18nMy.t('提示'), {
          confirmButtonText: $i18nMy.t('确定'),
          cancelButtonText: $i18nMy.t('取消'),
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/log/asynHttpLog/delete',
            method: 'delete',
            params: {'ids': ids}
          }).then(({data}) => {
            if (data && data.success) {
              this.$message.success(data.msg)
              this.refreshList()
            }
            this.loading = false
          })
        })
      },
      // 导入成功
      uploadSuccess (res, file) {
        if (res.success) {
          this.$message.success({dangerouslyUseHTMLString: true,
            message: res.msg})
        } else {
          this.$message.error(res.msg)
        }
      },
      // 下载模板
      downloadTpl () {
        this.$utils.download('/log/asynHttpLog/import/template')
      },
      exportExcel () {
        this.$utils.download('/log/asynHttpLog/export')
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>