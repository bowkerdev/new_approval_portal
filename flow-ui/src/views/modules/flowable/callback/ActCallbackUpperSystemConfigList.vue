<template>
  <div>
      <el-form :inline="true" v-show="isSearchCollapse" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
         <el-form-item prop="oaKey">
                <el-input size="small" v-model="searchForm.oaKey" :placeholder="$i18nMy.t('流程标识')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="url">
                <el-input size="small" v-model="searchForm.url" :placeholder="$i18nMy.t('通知的接口')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="paramList">
                <el-input size="small" v-model="searchForm.paramList" :placeholder="$i18nMy.t('需返回的参数列表')" clearable></el-input>
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
              :action="`${this.$http.BASE_URL}/flowable/callback/actCallbackUpperSystemConfig/import`"
              :on-success="uploadSuccess"
               :show-file-list="true">
              <el-button size="small" type="primary">{{$i18nMy.t('点击上传')}}</el-button>
              <div slot="tip" class="el-upload__tip">{{$i18nMy.t('只允许导入“xls”或“xlsx”格式文件！')}}</div>
            </el-upload>
        </el-form-item>
      </el-form>
      <el-row>
        <el-button v-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
        <el-button v-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}
        </el-button>
        <el-button-group class="pull-right">
            <el-button
              type="default"
              size="small"
              icon="el-icon-search"
              @click="isSearchCollapse = !isSearchCollapse, isImportCollapse=false">
            </el-button>
            <el-button v-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:import')" type="default" size="small" icon="el-icon-upload2" :title="$i18nMy.t('导入')" @click="isImportCollapse = !isImportCollapse, isSearchCollapse=false"></el-button>
            <el-button v-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:export')" type="default" size="small" icon="el-icon-download" :title="$i18nMy.t('导出')" @click="exportExcel()"></el-button>
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
        prop="oaKey"
        show-overflow-tooltip
        sortable="custom"
        label="流程标识">
            <template slot-scope="scope">
              <el-link  type="primary" :underline="false" v-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:edit')" @click="edit(scope.row.id)">{{scope.row.oaKey}}</el-link>
              <el-link  type="primary" :underline="false" v-else-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:view')"  @click="view(scope.row.id)">{{scope.row.oaKey}}</el-link>
              <span v-else>{{scope.row.oaKey}}</span>
            </template>
      </el-table-column>
    <el-table-column
        prop="url"
        show-overflow-tooltip
        sortable="custom"
        label="通知的接口">
      </el-table-column>
    <el-table-column
        prop="paramList"
        show-overflow-tooltip
        sortable="custom"
        label="需返回的参数列表">
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        fixed="right"
        width="200"
        label="操作">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:view')" type="text" icon="el-icon-view" size="small" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('flowable:callback:actCallbackUpperSystemConfig:del')" type="text"  icon="el-icon-delete" size="small" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
    <ActCallbackUpperSystemConfigForm  ref="actCallbackUpperSystemConfigForm" @refreshDataList="refreshList"></ActCallbackUpperSystemConfigForm>
  </div>
</template>

<script>
  import ActCallbackUpperSystemConfigForm from './ActCallbackUpperSystemConfigForm'
  export default {
    data () {
      return {
        searchForm: {
          oaKey: '',
          url: '',
          paramList: ''
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
      ActCallbackUpperSystemConfigForm
    },
    activated () {
      this.refreshList()
    },

    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/flowable/callback/actCallbackUpperSystemConfig/list',
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
        this.$refs.actCallbackUpperSystemConfigForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.actCallbackUpperSystemConfigForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.actCallbackUpperSystemConfigForm.init('view', id)
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
            url: '/flowable/callback/actCallbackUpperSystemConfig/delete',
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
        this.$utils.download('/flowable/callback/actCallbackUpperSystemConfig/import/template')
      },
      exportExcel () {
        this.$utils.download('/flowable/callback/actCallbackUpperSystemConfig/export')
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>