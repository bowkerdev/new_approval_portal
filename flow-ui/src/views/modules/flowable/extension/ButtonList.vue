<template>
  <div class="page">
      <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
		     <el-form-item prop="name">
                <el-input size="small" v-model="searchForm.name" :placeholder="$i18nMy.t('名称')" clearable></el-input>
		     </el-form-item>
		     <el-form-item prop="code">
                <el-input size="small" v-model="searchForm.code" :placeholder="$i18nMy.t('编码')" clearable></el-input>
		     </el-form-item>
          <el-form-item>
            <el-button  type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
      <div class="top bg-white">
      <el-row>
        <el-button v-if="hasPermission('extension:button:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('extension:button:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1">{{$i18nMy.t('修改')}}</el-button>
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
      height="calc(100% - 80px)"
      v-loading="loading"
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
      class="table">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
	  <el-table-column
        prop="name"
        sortable="custom"
        :label="$i18nMy.t('名称')">
        <template slot-scope="scope">
          <el-link  type="primary" :underline="false" v-if="hasPermission('extension:button:edit')" @click="edit(scope.row.id)">{{scope.row.name}}</el-link>
          <el-link  type="primary" :underline="false" v-else-if="hasPermission('extension:button:view')"  @click="view(scope.row.id)">{{scope.row.name}}</el-link>
          <span v-else>{{scope.row.name}}</span>
        </template>
      </el-table-column>
	  <el-table-column
        prop="code"
        sortable="custom"
        :label="$i18nMy.t('编码')">
      </el-table-column>
	  <el-table-column
        prop="sort"
        sortable="custom"
        :label="$i18nMy.t('排序')">
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        width="200"
        :label="$i18nMy.t('操作')">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('extension:button:view')" type="text" icon="el-icon-view" size="mini" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('extension:button:edit')" type="text" icon="el-icon-edit" size="mini" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('extension:button:del')" type="text" size="mini" icon="el-icon-delete" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
    <ButtonForm  ref="buttonForm" @refreshDataList="refreshList"></ButtonForm>
  </div>
  </div>
</template>

<script>
  import ButtonForm from './ButtonForm'
  export default {
    data () {
      return {
        searchForm: {
          name: '',
          code: ''
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
      ButtonForm
    },
    activated () {
      this.refreshList()
    },

    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/extension/button/list',
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
        this.$refs.buttonForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.buttonForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.buttonForm.init('view', id)
      },
      isDefaultButton (code) {
        let defaultButtons = ['_flow_save', '_flow_agree', '_flow_reject', '_flow_back', '_flow_add_multi_instance', '_flow_del_multi_instance', '_flow_transfer', '_flow_delegate', '_flow_stop', '_flow_print']
        return defaultButtons.filter((val) => {
          return code === val
        }).length > 0
      },
      // 删除
      del (row) {
        if (this.isDefaultButton(row.code)) {
          this.$message.error(`不能删除预设按钮【${row.name}】`)
          return
        }
        this.$confirm($i18nMy.t('确定删除所选项吗') + '?', $i18nMy.t('提示'), {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/extension/button/delete',
            method: 'delete',
            params: {'ids': row.id}
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
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>