<template>
  <div class="page">
      <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
         <!-- 搜索框-->
		     <el-form-item prop="db.id">
				  <el-select v-model="searchForm.db.id" :placeholder="$i18nMy.t('目标数据库')" clearable>
				   <el-option v-for="item in dbList" :key="item.id"
				     :label="item.name" :value="item.id" >
				   </el-option>
				  </el-select>
		     </el-form-item>
         <el-form-item prop="group">
            <el-select v-model="searchForm.group" clearable  :placeholder="$i18nMy.t('组别')" size="small">
              <el-option
                v-for="item in $dictUtils.getDictList('DATASOURCE_GROUP')"
                :key="item.value"  :label="item.label"  :value="item.value">
              </el-option>
            </el-select>
         </el-form-item>
		     <el-form-item prop="name">
          <el-input size="small" v-model="searchForm.name" :placeholder="$i18nMy.t('数据源名称')" clearable></el-input>
		     </el-form-item>
          <el-form-item>
            <el-button  type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
      <div class="top bg-white">
      <el-row>
        <el-button v-if="hasPermission('database:datamodel:dataSet:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('database:datamodel:dataSet:edit')" type="success" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
        <el-button v-if="hasPermission('database:datamodel:dataSet:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}</el-button>
        <el-button-group class="pull-right">
          <el-tooltip class="item" effect="dark" content="刷新" placement="top">
            <el-button type="default" size="small" icon="el-icon-refresh" @click="refreshList"></el-button>
          </el-tooltip>
        </el-button-group>
      </el-row>
    <el-table
      :data="dataList"
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
      v-loading="loading"
      height="calc(100% - 80px)"
      size = "small"
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
        :label="$i18nMy.t('数据源名称')">
        <template slot-scope="scope">
          <el-link  type="primary" :underline="false"  v-if="hasPermission('database:datamodel:dataSet:edit')" @click="edit(scope.row.id)">{{scope.row.name}}</el-link>
          <el-link  type="primary" :underline="false"  v-else-if="hasPermission('database:datamodel:dataSet:view')"  @click="view(scope.row.id)">{{scope.row.name}}</el-link>
          <span v-else>{{scope.row.name}}</span>
        </template>
      </el-table-column>

      <el-table-column
          prop="group"
          show-overflow-tooltip
          label="组别">
          <template slot-scope="scope">
                {{ $dictUtils.getDictLabel("DATASOURCE_GROUP", scope.row.group, '-') }}
          </template>
        </el-table-column>

	  <el-table-column
        prop="db.name"
        sortable="custom"
        :label="$i18nMy.t('目标数据库')">
      </el-table-column>
	  <el-table-column
        prop="sqlcmd"
        sortable="custom"
        show-overflow-tooltip
        label="sql语句">
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        width="300"
        :label="$i18nMy.t('操作')">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('database:datamodel:dataSet:view')" type="text" icon="el-icon-view" size="mini" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('database:datamodel:dataSet:edit')" type="text" icon="el-icon-edit" size="mini" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button type="text" size="mini" icon="el-icon-coin" @click="getDbInterface(scope.row.id,scope.row.name)">{{$i18nMy.t('获取数据接口')}}</el-button>
          <el-button v-if="hasPermission('database:datamodel:dataSet:del')" type="text" size="mini" icon="el-icon-delete" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
    <el-dialog title="数据接口" :visible.sync="dialogInterfaceVisible"  v-dialogDrag>
      <el-table :data="interfaceTable">
        <el-table-column property="type" width="120px" :label="$i18nMy.t('接口格式')"></el-table-column>
        <el-table-column property="url" :label="$i18nMy.t('接口地址')"></el-table-column>
        <el-table-column property="url2" label="接口地址2"></el-table-column>
      </el-table>
      <br>
      <h4> 接口传递参数说明：/接口url?参数名=参数值, 如果不传递参数，则使用默认值</h4>
    </el-dialog>
  </div>
</template>

<script>
  export default {
    data () {
      return {
        searchForm: {
          db: {
            id: '',
            name: ''
          },
          group:'',
          name: ''
        },
        dbList:[],
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        orderBy: '',
        dataListSelections: [],
        isImportCollapse: false,
        dialogInterfaceVisible: false,
        interfaceTable: [],
        loading: false
      }
    },
    activated () {
      let _that=this
      this.$dictUtils.getSqlDictList('DATASOURCE_DB_LIST',{},function(data){
        _that.dbList = data
        _that.refreshList()
      })
    },

    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/database/datamodel/dataSet/list',
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
        this.$router.push('/database/datamodel/DataSetForm')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$router.push({path: `/database/datamodel/DataSetForm`, query: {id: id, title: '数据模型配置'}})
      },
      // 查看
      view (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$router.push({path: `/database/datamodel/DataSetForm`, query: {method: 'view', id: id, title: '数据模型配置'}})
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
            url: '/database/datamodel/dataSet/delete',
            method: 'delete',
            params: {'ids': ids}
          }).then(({data}) => {
            if (data && data.success) {
              this.$message.success(data.msg)
              this.loading = false
              this.refreshList()
            }
          })
        })
      },
      // 导入成功
      getDbInterface (id,name) {
        this.dialogInterfaceVisible = true
        this.interfaceTable = []
        this.interfaceTable.push({type: 'JSON', url: `/database/datamodel/dataSet/getData/${id}/json`,
        url2:`/database/datamodel/dataSet/getDataByName/${name}/json`})
        this.interfaceTable.push({type: 'XML', url: `/database/datamodel/dataSet/getData/${id}/xml`,
        url2:`/database/datamodel/dataSet/getDataByName/${name}/xml`})
        this.interfaceTable.push({type: 'TABLE', url: `/database/datamodel/dataSet/getData/${id}/html`,
        url2:`/database/datamodel/dataSet/getDataByName/${name}/html`})
        this.interfaceTable.push({type: 'PAGE', url: `/database/datamodel/dataSet/getPageDataByName/${name}`,
        url2:`/database/datamodel/dataSet/getPageDataByName/${name}`})
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>
