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
              class="filter-tree jp-common-el-tree"
              :render-content="renderContent"
              :data="officeTreeData"
              :props="{
                    value: 'id',             // ID字段名
                    label: 'name',         // 显示名称
                    children: 'children'    // 子级字段名
                  }"
              node-key="id"
              default-expand-all
              :filter-node-method="filterNode"
              :expand-on-click-node="false"
              highlight-current
              @node-click="handleNodeClick"
              ref="officeTree">
            </el-tree>
        </div>
      </div>
      </div>
      </div>
      <div class="jp-common-layout-center jp-flex-main">
     <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
         <el-form-item prop="loginName">
          <el-input size="small" v-model="searchForm.loginName" :placeholder="$i18nMy.t('登录名')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="name">
           <el-input size="small" v-model="searchForm.name" :placeholder="$i18nMy.t('姓名')" clearable></el-input>
        </el-form-item>
      <el-form-item>
        <el-button  type="primary" @click="refreshList()" size="small" icon="el-icon-search">{{$i18nMy.t('查询')}}</el-button>
        <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">{{$i18nMy.t('重置')}}</el-button>
      </el-form-item>
      </el-form>
      <el-dialog  title="导入Excel" :visible.sync="isImportCollapse">
        <el-form size="small" :inline="true" ref="importForm">
          <el-form-item>
          <el-button  type="default" @click="downloadTpl()" size="small">{{$i18nMy.t('下载模板')}}</el-button>
          </el-form-item>
          <el-form-item prop="loginName">
              <el-upload
                class="upload-demo"
                :action="`${this.$http.BASE_URL}/sys/user/import`"
                :on-success="handleSuccess"
                :before-upload="beforeUpload"
                :show-file-list="true">
              <el-button size="small" type="primary">{{$i18nMy.t('点击上传')}}</el-button>
                <div slot="tip" class="el-upload__tip">只允许导入“xls”或“xlsx”格式文件！</div>
              </el-upload>
          </el-form-item>
        </el-form>
      </el-dialog>
    <div class="bg-white top">
      <el-row>
        <el-button v-if="hasPermission('sys:user:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('sys:user:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
      <el-button v-if="hasPermission('sys:user:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}
      </el-button>
        <el-button-group class="pull-right">
          <el-tooltip class="item" effect="dark" content="导入" placement="top">
              <el-button v-if="hasPermission('sys:user:import')" type="default" size="small" icon="el-icon-upload2" title="导入" @click="isImportCollapse = !isImportCollapse"></el-button>
          </el-tooltip>
          <el-tooltip class="item" effect="dark" content="导出" placement="top">
            <el-button v-if="hasPermission('sys:user:export')" type="default" size="small" icon="el-icon-download" title="导出" @click="exportExcel()"></el-button>
          </el-tooltip>
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
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
      size="small"
      height="calc(100% - 80px)"
      class="table">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="photo"
        header-align="center"
        align="center"
         min-width="80"
        :label="$i18nMy.t('头像')">
        <template slot-scope="scope">
          <img :src="scope.row.photo === ''?'/static/img/avatar.png':scope.row.photo" style="height:35px"/>
        </template>
      </el-table-column>
      <el-table-column
        prop="loginName"
        sortable="custom"
        min-width="100"
        show-overflow-tooltip
        :label="$i18nMy.t('登录名')">
        <template slot-scope="scope">
          <el-link  type="primary" :underline="false" v-if="hasPermission('sys:user:edit')" @click="edit(scope.row.id)">{{scope.row.loginName}}</el-link>
          <el-link  type="primary" :underline="false" v-else-if="hasPermission('sys:user:view')"  @click="view(scope.row.id)">{{scope.row.loginName}}</el-link>
          <span v-else>{{scope.row.loginName}}</span>
        </template>
      </el-table-column>
      <el-table-column
        prop="name"
        sortable="custom"
        min-width="100"
        show-overflow-tooltip
        :label="$i18nMy.t('用户名')">
      </el-table-column>
      <el-table-column
        prop="company.name"
        sortable="custom"
        min-width="120"
        show-overflow-tooltip
        :label="$i18nMy.t('公司')">
      </el-table-column>
      <el-table-column
        prop="office.name"
        sortable="custom"
         min-width="120"
         show-overflow-tooltip
        :label="$i18nMy.t('部门')">
      </el-table-column>
      <el-table-column
        prop="post.name"
        sortable="custom"
        show-overflow-tooltip
        min-width="80"
        :label="$i18nMy.t('岗位')">
      </el-table-column>
      <el-table-column
        prop="loginFlag"
        :label="$i18nMy.t('状态')">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.loginFlag === '1'" size="small" type="success">{{$i18nMy.t('正常')}}</el-tag>
          <el-tag v-else-if="scope.row.loginFlag === '0'" size="small" type="danger">{{$i18nMy.t('禁用')}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column
        fixed="right"
        :key="Math.random()"
        header-align="center"
        align="center"
        width="200"
        :label="$i18nMy.t('操作')">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('sys:user:view')" type="text" size="small" icon="el-icon-view" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('sys:user:edit')" type="text" size="small" icon="el-icon-edit" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('sys:user:del')" type="text" size="small" icon="el-icon-delete" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
    <user-form  ref="userForm" @refreshDataList="refreshList"></user-form>
    </div>
    </div>
</template>

<script>
  import UserForm from './UserForm'

  export default {
    name: 'sys-user-UserList',
    data () {
      return {
        searchForm: {
          loginName: '',
          name: '',
          company: {
            id: ''
          },
          office: {
            id: ''
          }
        },
        isShow: true,
        filterText: '',
        dataList: [],
        officeTreeData: [],
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
      UserForm
    },
    activated () {
      this.refreshTree()
      this.refreshList()
    },
    watch: {
      filterText (val) {
        this.$refs.officeTree.filter(val)
      }
    },
    methods: {
      filterNode (value, data) {
        if (!value) return true
        return data.name.indexOf(value) !== -1
      },
      renderContent (h, { node, data, store }) {
        return (
              <span class="custom-tree-node">
                {
                  data.type === '1' ? <i class="fa fa-sitemap"></i> : <i class="fa fa-users"></i>
                }
                <span class="text">{node.label}</span>
              </span>
        )
      },
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/sys/user/list',
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
      refreshTree () {
        this.$http({
          url: `/sys/office/treeData`,
          method: 'get'
        }).then(({data}) => {
          this.officeTreeData = data.treeData
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
        if (obj.prop === 'office.name') {
          obj.prop = 'o.name'
        }
        if (obj.prop === 'company.name') {
          obj.prop = 'c.name'
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
        this.$refs.userForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.userForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.userForm.init('view', id)
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
          this.loading = true
          this.$http({
            url: '/sys/user/delete',
            method: 'delete',
            params: {'ids': ids}
          }).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.$message.success({dangerouslyUseHTMLString: true,
                message: data.msg})
              this.refreshList()
            }
          })
        })
      },
      // 导入
      importExcel () {

      },
      // 下载模板
      downloadTpl () {
        this.$utils.download('/sys/user/import/template')
      },
      handleSuccess (res, file) {
        if (res.success) {
          this.$message.success({dangerouslyUseHTMLString: true,
            message: res.msg})
        } else {
          this.$message.error(res.msg)
        }
      },
      handleNodeClick (data) {
        if (data.type === '1') {
          this.searchForm.company.id = data.id
          this.searchForm.office.id = ''
        } else {
          this.searchForm.company.id = ''
          this.searchForm.office.id = data.id
        }
        this.refreshList()
      },
      beforeUpload (file) {
        const isExcel = file.name.indexOf('.xls') >= 0
        const isLt2M = file.size / 1024 / 1024 < 10

        if (!isExcel) {
          this.$message.error('只能上传excel文件!')
          return false
        }
        if (!isLt2M) {
          this.$message.error('上传头像图片大小不能超过 10MB!')
          return false
        }
        return true
      },
      exportExcel () {
        this.$utils.download('/sys/user/export')
      },
      resetSearch () {
        this.searchForm.company.id = ''
        this.searchForm.office.id = ''
        this.filterText = ''
        this.$refs.officeTree.setCurrentKey(null)
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>
<style lang="scss">
.el-card__body {
    overflow: auto;
}
</style>
