<template>
  <div class="page">
      <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
         <el-form-item prop="name">
                <el-input size="small" v-model="searchForm.name" :placeholder="$i18nMy.t('表单名称')" clearable></el-input>
		     </el-form-item>
            <!-- 搜索框-->
          <el-form-item>
            <el-button  type="primary" @click="refreshList()" size="small" icon="el-icon-search">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
      <div class="bg-white top">
      <el-row>
        <el-button v-if="hasPermission('form:make:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
         <el-button v-if="hasPermission('form:make:add')" type="primary" size="small" icon="el-icon-edit" @click="design()"
          :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('设计')}}</el-button>
        <el-button v-if="hasPermission('form:make:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
        <el-button v-if="hasPermission('form:make:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}</el-button>
        </el-button>
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
        prop="name"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('表单名称')">
          <template slot-scope="scope">
          <el-link  type="primary" :underline="false" v-if="hasPermission('form:make:edit')" @click="edit(scope.row.id)">{{scope.row.name}}</el-link>
          <el-link  type="primary" :underline="false" v-else-if="hasPermission('form:make:view')"  @click="preview(scope.row.id)">{{scope.row.name}}</el-link>
          <span v-else>{{scope.row.name}}</span>
        </template>
      </el-table-column>
	  <el-table-column
        prop="tableName"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('表名')">
      </el-table-column>
	  <el-table-column
        prop="code"
        show-overflow-tooltip
        sortable="custom"
        label="表单key">
      </el-table-column>
      <el-table-column
        prop="dataSource.name"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('所属数据库')">
      </el-table-column>
	  <el-table-column
        prop="version"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('版本号')">
        <template slot-scope="scope">
            {{'V ' + scope.row.version + '.0'}}
        </template>
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        fixed="right"
        :key="Math.random()"
        width="300"
        :label="$i18nMy.t('操作')">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('form:make:edit')" type="text" icon="el-icon-edit" size="mini" @click="design(scope.row.id)">{{$i18nMy.t('设计')}}</el-button>
          <el-button v-if="hasPermission('form:make:del')" type="text" size="mini" icon="el-icon-delete"  @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
          <el-button v-if="hasPermission('form:make:view')" type="text" size="mini" icon="el-icon-view"  @click="preview(scope.row)">{{$i18nMy.t('预览')}}</el-button>
          <el-button v-if="hasPermission('form:make:deploy')" type="text" size="mini" icon="el-icon-s-promotion"  @click="release(scope.row)">{{$i18nMy.t('发布')}}</el-button>
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
    <BasicForm  ref="basicForm" @refreshDataList="refreshList"></BasicForm>
    <MakeForm  ref="makeForm" @refreshDataList="refreshList"></MakeForm>
    <MenuForm ref="menuForm" @refreshDataList="refreshList"></MenuForm>
  </div>
</template>

<script>
  import BasicForm from './BasicForm'
  import MakeForm from './MakeForm'
  import MenuForm from './MenuForm'
  import DataRuleList from './DataRuleList'
  export default {
    name: 'form-MakeFormList',
    data () {
      return {
        searchForm: {
        },
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        orderBy: '',
        dataRuleTitle: '',
        dataListSelections: [],
        rightVisible: false,
        isImportCollapse: false,
        loading: false
      }
    },
    components: {
      MakeForm,
      BasicForm,
      MenuForm,
      DataRuleList
    },
    activated () {
      this.refreshList()
    },

    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/form/make/list',
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
        if (obj.prop === 'dataSource.name') {
          obj.prop = 'db.name'
        }
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
        this.$refs.basicForm.init('add', '')
      },
      // 设计
      design (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.makeForm.init('edit', id)
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.basicForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.makeForm.init('view', id)
      },
         // 发布
      release (row) {
        if (!row.source) {
          this.$message.warning('请先设计表单!')
          return
        }
        this.$refs.menuForm.init(row)
      },
       // 查看
      preview (row) {
        if (!row.source) {
          this.$message.warning('请先设计表单!')
          return
        }
        this.$http({
          url: `/form/make/queryById?id=${row.id}`,
          method: 'get'
        }).then(({data}) => {
          if (data.form.source) {
            this.options = JSON.parse(data.form.source)
          } else {
            this.options = {'list': [], 'config': {'labelWidth': 100, 'labelPosition': 'right', 'size': 'small', 'customClass': ''}}
          }
          this.$router.push({path: '/form/generateList', query: {title: row.name, id: row.id, previewMode: true}})
        })
      },
      // 删除
      del (id) {
        let ids = id || this.dataListSelections.map(item => {
          return item.id
        }).join(',')
        this.$confirm($i18nMy.t('确定删除所选项吗') + '?', $i18nMy.t('提示'), {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/form/make/delete',
            method: 'delete',
            params: {'ids': ids}
          }).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.$message.success(data.msg)
              this.refreshList()
            }
          })
        })
      },
      showRight (row) {
        this.rightVisible = true
        this.$nextTick(() => {
          this.$refs.dataRuleList.form = row
          this.$refs.dataRuleList.refreshList(row.id)
          this.dataRuleTitle = row.name
        })
      },
      closeRight () {
        this.rightVisible = false
      },
      // 入成功
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
        this.$utils.download('/form/make/import/template')
      },
      exportExcel () {
        this.$utils.download('/form/make/export')
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>