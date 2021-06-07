<template>
  <div class="page">
     <el-form size="small" :inline="true"  class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
         <el-form-item prop="name">
          <el-input size="small" v-model="searchForm.name" placeholder="组件名称" clearable></el-input>
         </el-form-item>
      <el-form-item>
        <el-button  type="primary" @click="refreshList()" size="small" icon="el-icon-search">查询</el-button>
        <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">重置</el-button>
      </el-form-item>
      </el-form>
      <div class="bg-white top">
      <el-row>
        <el-button v-if="hasPermission('echarts:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
        <el-button v-if="hasPermission('database:datamodel:dataSet:add')" type="primary" size="small" icon="el-icon-coin" @click="toDataSet()">数据源管理</el-button>
        <el-button v-if="hasPermission('echarts:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1">修改</el-button>
        <el-button v-if="hasPermission('echarts:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0">删除
        </el-button>
        <el-button type="default" size="small" icon="el-icon-edit-outline" @click="createMenu()"
         :disabled="dataListSelections.length != 1">创建菜单</el-button>
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
        label="组件名称">
      </el-table-column>
      <el-table-column
        prop="dataSet.name"
        sortable="custom"
        label="数据源">
      </el-table-column>
      <el-table-column
        fixed="right"
        :key="Math.random()"
        header-align="center"
        align="center"
        width="300"
        label="操作">
        <template slot-scope="scope">
         <el-button v-if="hasPermission('echarts:view')" type="text"  icon="el-icon-view" size="small"
                     @click="view(scope.row.id)">查看
          </el-button>
           <el-divider direction="vertical"></el-divider>
          <el-button v-if="hasPermission('echarts:edit')" type="text"  size="small" icon="el-icon-edit"
                     @click="edit(scope.row.id)">修改
          </el-button>
           <el-divider direction="vertical"></el-divider>
          <el-button v-if="hasPermission('echarts:del')" type="text"  size="small" icon="el-icon-delete" @click="del(scope.row.id)">
            删除
          </el-button>
          <el-button v-if="hasPermission('echarts:view')" type="text"  icon="el-icon-view" size="small"
                     @click="generate(scope.row.id)">预览
          </el-button>
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
    <EchartsForm @refreshDataList="refreshList" ref="echartsForm"/>
      <gen-menu-form ref="genMenuForm"></gen-menu-form>
  </div>
</template>

<script>
  import EchartsForm from './EchartsForm'
  import GenMenuForm from './GenMenuForm'
  export default {
    data () {
      return {
        searchForm: {
          name: ''
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
      EchartsForm,
      GenMenuForm
    },
    activated () {
      this.refreshList()
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/echarts/list',
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
        this.$refs.echartsForm.init('add', '')
      },
      // 新增数据源
      toDataSet () {
        this.$router.push('/database/datamodel/DataSetList')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections[0].id
        this.$refs.echartsForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.echartsForm.init('view', id)
      },
      // 预览
      generate (id) {
        this.$router.push(`/echarts/GenerateChart?id=${id}`)
      },
      // 创建菜单
      createMenu () {
        let id = this.dataListSelections[0].id
        this.$refs.genMenuForm.init(id)
      },
      // 删除
      del (id) {
        let ids = id || this.dataListSelections.map(item => {
          return item.id
        }).join(',')
        this.$confirm(`确定删除所选项吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.$http({
            url: '/echarts/delete',
            method: 'delete',
            params: {'ids': ids}
          }).then(({data}) => {
            if (data && data.success) {
              this.$message.success({dangerouslyUseHTMLString: true,
                message: data.msg})
            }
            this.refreshList()
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
