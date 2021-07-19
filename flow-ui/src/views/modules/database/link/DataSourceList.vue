<template>
  <div class="page">
     <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
         <el-form-item prop="name">
          <el-input size="small" v-model="searchForm.name" :placeholder="$i18nMy.t('连接名称')" clearable></el-input>
         </el-form-item>
      <el-form-item>
        <el-button  type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
        <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
      </el-form-item>
      </el-form>
      <div class="top bg-white">
      <el-row>
        <el-button v-if="hasPermission('database:datalink:dataSource:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('database:datalink:dataSource:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
        <el-button v-if="hasPermission('database:datalink:dataSource:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}</el-button>
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
      v-loading="loading"
      size = "small"
      height="calc(100% - 80px)"
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
        :label="$i18nMy.t('连接名称')">
        <template slot-scope="scope">
          <el-link  type="primary" :underline="false" v-if="hasPermission('database:datalink:dataSource:edit')" @click="edit(scope.row.id)">{{scope.row.name}}</el-link>
          <el-link  type="primary" :underline="false" v-else-if="hasPermission('database:datalink:dataSource:view')"  @click="view(scope.row.id)">{{scope.row.name}}</el-link>
          <span v-else>{{scope.row.name}}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="enName"
        :label="$i18nMy.t('连接英文名')">
      </el-table-column>
      <el-table-column
        prop="type"
        :label="$i18nMy.t('数据库类型')">
        <template slot-scope="scope">
            {{ $dictUtils.getDictLabel("db_type", scope.row.type, '-') }}
        </template>
      </el-table-column>
      <el-table-column
        prop="host"
        :label="$i18nMy.t('主机地址')">
      </el-table-column>
      <el-table-column
        prop="port"
        :label="$i18nMy.t('端口')">
      </el-table-column>
      <el-table-column
        prop="dbname"
        :label="$i18nMy.t('数据库名')">
      </el-table-column>
      <el-table-column
        prop="username"
        :label="$i18nMy.t('数据库用户名')">
      </el-table-column>
      <el-table-column
        fixed="right"
        :key="Math.random()"
        header-align="center"
        align="center"
        width="200"
        :label="$i18nMy.t('操作')">
        <template slot-scope="scope">
         <el-button v-if="hasPermission('database:datalink:dataSource:view')" type="text"  icon="el-icon-view" size="small" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('database:datalink:dataSource:edit')" type="text"  size="small" icon="el-icon-edit" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('database:datalink:dataSource:del')" type="text"  size="small" icon="el-icon-delete" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
    <data-source-form  ref="dataSourceForm" @refreshDataList="refreshList"></data-source-form>
  </div>
</template>

<script>
  import DataSourceForm from './DataSourceForm'
  export default {
    data () {
      return {
        searchForm: {
          name: ''
        },
        filterText: '',
        dataList: [],
        officeTreeData: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        orderBy: '',
        dataListSelections: [],
        isImportCollapse: false,
        loading: false,
        selectOfficeName: ''
      }
    },
    components: {
      DataSourceForm
    },
    activated () {
      this.refreshList()
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/database/datalink/dataSource/list',
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
        this.$refs.dataSourceForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.dataSourceForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.dataSourceForm.init('view', id)
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
          this.$http({
            url: '/database/datalink/dataSource/delete',
            method: 'delete',
            params: {'ids': ids}
          }).then(({data}) => {
            if (data && data.success) {
              this.$message.success({dangerouslyUseHTMLString: true,
                message: data.msg})
              this.refreshList()
            }
          })
        }).catch(() => {
        })
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>
