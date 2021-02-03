<template>
    <div class="jp-common-layout page">
      <div class="jp-common-layout-left">
        <div class="jp-common-title"> 
          <el-input
          :placeholder="$i18nMy.t('输入关键字进行过滤')"
          size="small"
          clearable
          v-model="filterText">
        </el-input>
        </div>
      <div class="jp-common-el-tree-scrollbar el-scrollbar">
      <div class="el-scrollbar__wrap">
        <div class="el-scrollbar__view">
      <el-tree
         class="filter-tree"
        :data="databaseTreeData"
        :props="{
              value: 'id',             // ID字段名
              label: 'label',         // 显示名称
              children: 'children'    // 子级字段名
            }"
        default-expand-all
        :filter-node-method="filterNode"
        :expand-on-click-node="false"
        node-key="id"
        highlight-current
        @node-click="handleNodeClick"
        ref="databaseTree">
      </el-tree>
        </div>
      </div>
      </div>
      </div>
      <div class="jp-common-layout-center jp-flex-main">
     <el-form size="small" :inline="true"  class="query-form"  ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
         <el-form-item prop="name">
           <el-input size="small" v-model="searchForm.name" :placeholder="$i18nMy.t('表名')" clearable></el-input>
        </el-form-item>
      <el-form-item>
        <el-button  type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
        <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
      </el-form-item>
      </el-form>
    <div class="bg-white top">
    <el-table
      :data="dataList"
      v-loading="loading"
      height="calc(100% - 20px)"
      size = "small"
      @selection-change="selectionChangeHandle"
      style="width: 100%;">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>>
      <el-table-column
        prop="name"
        sortable
        show-overflow-tooltip
        :label="$i18nMy.t('表名')">
         <template slot-scope="scope">
          <el-link type="primary" :underline="false" @click="view(scope.row)">{{scope.row.name}}</el-link>
        </template>
      </el-table-column>
      <el-table-column
        prop="schema"
        sortable
        show-overflow-tooltip
        :label="$i18nMy.t('模式')">
      </el-table-column>
       <el-table-column
        prop="catalog"
        sortable
        show-overflow-tooltip
        :label="$i18nMy.t('数据库')">
      </el-table-column>
      <el-table-column
        prop="Description"
        sortable
        show-overflow-tooltip
        :label="$i18nMy.t('说明')">
      </el-table-column>
      <el-table-column 
      align="center"
      :label="$i18nMy.t('操作')">
        <template slot-scope="scope">
          <el-button
            icon="el-icon-view" type="text" size="small"
            @click="view(scope.row)">{{$i18nMy.t('查看数据')}}</el-button>
        </template>
    </el-table-column>
    </el-table>
    <el-alert
      :title = "`总共${dataList.length}条记录`"
      type="success"
      :closable="false"/>
    </div>
    </div>

    <el-dialog title="查看数据" width="80%" v-if="viewDataVisible"  v-dialogDrag :visible.sync="viewDataVisible">
      <el-table size="small" :data="viewData.dataList">
        <el-table-column v-for="column in viewData.columnList" show-overflow-tooltip :key="column.name" :property="column.name" :label="column.description || column.name" ></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    data () {
      return {
        searchForm: {
          name: '',
          dataSourceId: ''
        },
        filterText: '',
        viewDataVisible: false,
        dataList: [],
        databaseTreeData: [],
        orderBy: '',
        dataListSelections: [],
        loading: false,
        dataSourceId: '',
        viewData: {
          columnList: [],
          dataList: []
        }
      }
    },
    watch: {
      filterText (val) {
        this.$refs.databaseTree.filter(val)
      }
    },
    activated () {
      this.refreshTree()
      if (this.searchForm.dataSourceId !== '') {
        this.refreshList()
        this.$nextTick(() => {
          this.$refs.databaseTree.setCurrentKey(this.searchForm.dataSourceId)
        })
      }
    },
    methods: {
      filterNode (value, data) {
        if (!value) return true
        return data.label.indexOf(value) !== -1
      },
      // 获取数据列表
      refreshList () {
        if (!this.searchForm.dataSourceId) {
          this.$message.warning('请选择一个数据库!')
          return
        }
        this.loading = true
        this.$http({
          url: '/database/table/list',
          method: 'get',
          params: {
            ...this.searchForm
          }
        }).then(({data}) => {
          if (data && data.success) {
            let results = data.data.rows.filter((row) => {
              if (row.name.indexOf(this.searchForm.name) >= 0) {
                return row
              }
            })
            this.dataList = results
            this.loading = false
          } else {
            this.dataList = []
            this.loading = false
          }
        }).catch(({data}) => {
          this.dataList = []
          this.loading = false
        })
      },
      refreshTree () {
        this.$http({
          url: `/database/datalink/dataSource/treeData2`,
          method: 'get'
        }).then(({data}) => {
          this.databaseTreeData = data.treeData
        })
      },
      // 多选
      selectionChangeHandle (val) {
        this.dataListSelections = val
      },
      handleNodeClick (data) {
        if (data.type === 'db') {
          this.searchForm.dataSourceId = data.id
          this.dataSourceId = data.id
          this.refreshList()
        }
      },
      handleNodeClose () {
        this.searchForm.dataSourceId = ''
        this.dataSourceId = ''
        this.dataList = []
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      },
      view (row) {
        this.viewDataVisible = true
        this.$http.get(`/database/table/executeSql/${row.name}?dataSourceId=${this.dataSourceId}`).then(({data}) => {
          if (data.success) {
            this.viewData.columnList = data.columns
            this.viewData.dataList = data.list
          }
        })
      },
      doSubmit () {
        let names = this.dataListSelections.map((item) => {
          return item.name
        }).join(',')
        this.$http({
          url: '/gen/genTable/saveTableFromDB',
          method: 'post',
          data: {
            name: names,
            'dataSource.id': this.dataSourceId
          },
          loading: true,
          loadingText: '正在导入数据库...'
        }).then(({data}) => {
          if (data.success) {
            this.$message.success({
              message: data.msg,
              dangerouslyUseHTMLString: true
            })
            this.$emit('refreshDataList')
            this.visible = false
          }
        })
      }
    }
  }
</script>
