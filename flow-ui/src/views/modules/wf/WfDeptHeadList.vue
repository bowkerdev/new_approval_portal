<template>
    <div class="page">
      <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
         <el-form-item prop="site">
                  <el-select v-model="searchForm.site" :placeholder="$i18nMy.t('Site')" size="small" style="width: 100%;">
                    <el-option
                      v-for="item in $dictUtils.getDictList('apply_site_code')"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
         </el-form-item>
         <el-form-item prop="dept">
                <el-input size="small" v-model="searchForm.dept" :placeholder="$i18nMy.t('Department')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="approver.id">
            <user-select :limit='1' size="small" :placeholder="$i18nMy.t('Approver')" :value="searchForm.approver.id" @getValue='(value) => {searchForm.approver.id=value}'></user-select>
         </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
        <!-- 导入导出-->
      <el-dialog title="Import" :visible.sync="isImportCollapse">
          <el-form size="small" :inline="true" v-show="isImportCollapse"  ref="importForm">
             <el-form-item>
              <el-button type="default" @click="downloadTpl()" size="small">{{$i18nMy.t('下载模板')}}</el-button>
             </el-form-item>
             <el-form-item prop="loginName">
                <el-upload
                  class="upload-demo"
                  :action="`${this.$http.BASE_URL}/wf/wfDeptHead/import`"
                  :on-success="uploadSuccess"
                   :show-file-list="true">
                  <el-button size="small" type="primary">{{$i18nMy.t('点击上传')}}</el-button>
                  <div slot="tip" class="el-upload__tip">{{$i18nMy.t('只允许导入“xls”或“xlsx”格式文件！')}}</div>
                </el-upload>
            </el-form-item>
          </el-form>
      </el-dialog>
      <div class="bg-white top">
      <el-row>
        <el-button v-if="hasPermission('wf:wfDeptHead:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('wf:wfDeptHead:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
        <el-button v-if="hasPermission('wf:wfDeptHead:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}
        </el-button>
        <el-button-group class="pull-right">
            <el-button v-if="hasPermission('wf:wfDeptHead:import')" type="default" size="small" icon="el-icon-upload2" :title="$i18nMy.t('导入')" @click="isImportCollapse = !isImportCollapse"></el-button>
            <el-button v-if="hasPermission('wf:wfDeptHead:export')" type="default" size="small" icon="el-icon-download" :title="$i18nMy.t('导出')" @click="exportExcel()"></el-button>
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
       size="small"
       height="calc(100% - 80px)"
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
        prop="site"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('Site')">
        <template slot-scope="scope">
              <el-link  type="primary" :underline="false" v-if="hasPermission('wf:wfDeptHead:edit')" @click="edit(scope.row.id)"> {{ $dictUtils.getDictLabel("apply_site_code", scope.row.site, '-') }}</el-link>
              <el-link  type="primary" :underline="false" v-else-if="hasPermission('wf:wfDeptHead:view')"  @click="view(scope.row.id)"> {{ $dictUtils.getDictLabel("apply_site_code", scope.row.site, '-') }}</el-link>
              <span v-else>{{ $dictUtils.getDictLabel("apply_site_code", scope.row.site, '-') }}</span>
        </template>
      </el-table-column>
    <el-table-column
        prop="dept"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('Department')">
      </el-table-column>
      <el-table-column
        prop="approver.name"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('Approver')">
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        fixed="right"
        :key="Math.random()"
        width="200"
        :label="$i18nMy.t('操作')">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('wf:wfDeptHead:view')" type="text" icon="el-icon-view" size="small" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('wf:wfDeptHead:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('wf:wfDeptHead:del')" type="text"  icon="el-icon-delete" size="small" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
        <!-- 弹窗, 新增 / 修改 -->
    <WfDeptHeadForm  ref="wfDeptHeadForm" @refreshDataList="refreshList"></WfDeptHeadForm>
  </div>
</template>

<script>
  import WfDeptHeadForm from './WfDeptHeadForm'
  import UserSelect from '@/components/userSelect'
  export default {
    data () {
      return {
        searchForm: {
          site: '',
          dept: '',
          approver: {
            id: ''
          }
        },
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        orderBy: '',
        dataListSelections: [],
        isImportCollapse: false,
        loading: false
      }
    },
    components: {
      UserSelect,
      WfDeptHeadForm
    },
    activated () {
      this.refreshList()
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/wf/wfDeptHead/list',
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
        this.$refs.wfDeptHeadForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.wfDeptHeadForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.wfDeptHeadForm.init('view', id)
      },
      // 删除
      del (id) {
        let ids = id || this.dataListSelections.map(item => {
          return item.id
        }).join(',')
        this.$confirm($i18nMy.t('确定删除所选项吗') + '?', $i18nMy.t('提示'), {
          confirmButtonText: $i18nMy.t('确定'),
          cancelButtonText: $i18nMy.t('取消'),
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/wf/wfDeptHead/delete',
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
        this.$utils.download('/wf/wfDeptHead/import/template')
      },
      exportExcel () {
        let params = {
          ...this.searchForm
        }
        this.$utils.download('/wf/wfDeptHead/export', params)
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>
