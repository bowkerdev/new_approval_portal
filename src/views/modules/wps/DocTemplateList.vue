<template>
    <div class="jp-common-layout page">
      <div class="jp-common-layout-left">
        <div class="jp-common-title"> 
          <el-row :gutter="5">
            <el-col :span="20">
              <el-input
                placeholder="输入关键字进行过滤"
                size="small"
                v-model="filterText">
              </el-input>
            </el-col>
            <el-col :span="4">
                <el-button type="primary" @click="addTreeNode" size="small" icon="el-icon-plus" circle></el-button>
            </el-col>
          </el-row>
        </div>
        <div class="jp-common-el-tree-scrollbar el-scrollbar">
        <div class="el-scrollbar__wrap">
          <div class="el-scrollbar__view">
          <el-tree
            class="filter-tree"
            :data="docCategoryTreeData"
            :props="{
              value: 'id', // ID字段名
              label: 'name', // 显示名称
              children: 'children' // 子级字段名
            }"
            default-expand-all
            :filter-node-method="filterNode"
            :expand-on-click-node="false"
            @node-click="handleNodeClick"
            ref="docCategoryTree"
          >
            <span class="custom-tree-node" slot-scope="{ node, data }">
              <span>{{ node.label }}</span>
              <span>
                <el-button
                  type="text"
                  class="tree-item-button"
                  icon="el-icon-plus"
                  @click="() => addChildTreeNode(data)"
                >
                </el-button>
                <el-button
                  type="text"
                  class="tree-item-button"
                  icon="el-icon-edit-outline"
                  @click="() => editTreeNode(data)"
                >
                </el-button>
                <el-button type="text" class="tree-item-button" icon="el-icon-delete" @click="() => delTreeNode(data)">
                </el-button>
              </span>
            </span>
          </el-tree>
         </div>
        </div>
      </div>
    </div>
    <div class="jp-common-layout-center jp-flex-main">
      <el-form :inline="true" size="small" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
            <el-form-item prop="category.id">
              <SelectTree
                ref="category"
                :props="{
                  value: 'id', // ID字段名
                  label: 'name', // 显示名称
                  children: 'children' // 子级字段名
                }"
                size="small"
                url="/wps/docCategory/treeData"
                :value="searchForm.category.id"
                :clearable="true"
                :accordion="true"
                @getValue="
                  value => {
                    searchForm.category.id = value
                  }
                "
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="refreshList()" size="small">查询</el-button>
              <el-button @click="resetSearch()" size="small">重置</el-button>
            </el-form-item>
          </el-form>
        <div class="bg-white top">
          <el-row>
  
          <el-dropdown trigger="click" @command="createFile">
              <el-button size="small" type="primary" icon="el-icon-plus" class="e-b">新建</el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="w"><svg class="icon" aria-hidden="true"><use xlink:href="#icon-word1"/></svg><span style="text-align: center">文字文档</span></el-dropdown-item>
                <el-dropdown-item command="s"><svg class="icon" aria-hidden="true"><use xlink:href="#icon-excel1"/></svg><span style="text-align: center">表格文档</span></el-dropdown-item>
                <el-dropdown-item command="p"><svg class="icon" aria-hidden="true"><use xlink:href="#icon-ppt4"/></svg><span style="text-align: center">演示文档</span></el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
            <el-button
              v-if="hasPermission('wps:docTemplate:add')"
              type="primary"
              size="small"
              plain
              icon="el-icon-upload"
              @click="add()"
              >上传</el-button
            >
            <el-button
              v-if="hasPermission('wps:docTemplate:edit')"
              type="warning"
              size="small"
              icon="el-icon-edit-outline"
              @click="edit()"
              :disabled="dataListSelections.length != 1"
              plain
              >修改</el-button
            >
            <el-button
              v-if="hasPermission('wps:docTemplate:del')"
              type="danger"
              size="small"
              icon="el-icon-delete"
              @click="del()"
              :disabled="dataListSelections.length <= 0"
              plain
              >删除
            </el-button>
            <el-button-group class="pull-right">
              <el-button type="default" size="small" icon="el-icon-refresh" @click="refreshList"> </el-button>
            </el-button-group>
          </el-row>
          <el-table
            :data="dataList"
            size="small"
            height="calc(100% - 80px)"
            @selection-change="selectionChangeHandle"
            @sort-change="sortChangeHandle"
            v-loading="loading"
            class="table"
          >
            <el-table-column type="selection" size="medium" header-align="center" align="center" width="50">
            </el-table-column>
            <el-table-column prop="name" show-overflow-tooltip sortable="custom" label="名称">
              <template slot-scope="scope">
                <el-link
                  type="primary"
                  :underline="false"
                  v-if="hasPermission('wps:docTemplate:edit')"
                  @click="edit(scope.row.id)"
                  >{{ scope.row.name }}</el-link
                >
                <el-link
                  type="primary"
                  :underline="false"
                  v-else-if="hasPermission('wps:docTemplate:view')"
                  @click="view(scope.row.id)"
                  >{{ scope.row.name }}</el-link
                >
                <span v-else>{{ scope.row.name }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="path" show-overflow-tooltip sortable="custom" label="文件">
              <template slot-scope="scope" v-if="scope.row.path">
                <el-link
                  type="primary"
                  :underline="false"
                  v-if="hasPermission('wps:docTemplate:edit')"
                  @click="design(scope.row)"
                  >{{ decodeURIComponent(scope.row.path.substring(scope.row.path.lastIndexOf('/') + 1)) }}</el-link
                >
              </template>
            </el-table-column>
            <!-- <el-table-column prop="version" show-overflow-tooltip sortable="custom" label="版本"> </el-table-column> -->
            <el-table-column prop="category.name" show-overflow-tooltip  label="类型">
            </el-table-column>
            <el-table-column prop="remarks" show-overflow-tooltip sortable="custom" label="备注信息"> </el-table-column>
            <el-table-column header-align="center" align="center" :key="Math.random()" fixed="right" width="300" label="操作">
              <template slot-scope="scope">
                <el-button
                  v-if="hasPermission('wps:docTemplate:view')"
                  type="text"
                  icon="el-icon-download"
                  size="small"
                  @click="download(scope.row.path)"
                  >下载</el-button
                >
                <el-button
                  v-if="hasPermission('wps:docTemplate:edit')"
                  type="text"
                  icon="el-icon-edit"
                  size="small"
                  @click="edit(scope.row.id)"
                  >修改</el-button
                >
                <el-button
                  v-if="hasPermission('wps:docTemplate:del')"
                  type="text"
                  icon="el-icon-delete"
                  size="small"
                  @click="del(scope.row.id)"
                  >删除</el-button
                >
                <el-button
                  v-if="hasPermission('wps:docTemplate:edit')"
                  type="text"
                  icon="el-icon-edit"
                  size="small"
                  @click="design(scope.row)"
                  >编辑内容</el-button
                >
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
            layout="total, sizes, prev, pager, next, jumper"
          >
          </el-pagination>
    </div>
    <!-- 弹窗, 新增 / 修改 -->
    <DocTemplateForm ref="docTemplateForm" @refreshDataList="refreshList"></DocTemplateForm>
    <DocCategoryForm ref="docCategoryForm" @refreshTree="refreshTree"></DocCategoryForm>
    <el-dialog
      title="编辑文档"
      :close-on-click-modal="true"
      v-dialogDrag
      fullscreen
      class="word"
      style="padding:0px"
      :visible.sync="visible"
    >
      <iframe v-if="visible" :src="wpsUrl" frameborder="0" scrolling="auto" height="1000px" width="100%"></iframe>
    </el-dialog>
      </div>
    </div>
</template>

<script>
import DocTemplateForm from './DocTemplateForm'
import DocCategoryForm from './DocCategoryForm'
import SelectTree from '@/components/treeSelect/treeSelect.vue'
export default {
  data () {
    return {
      searchForm: {
        category: {
          id: ''
        }
      },
      filterText: '',
      visible: false,
      docCategoryTreeData: [],
      selectDocCategoryName: '',
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
    SelectTree,
    DocTemplateForm,
    DocCategoryForm
  },
  activated () {
    this.refreshTree()
    this.refreshList()
  },
  computed: {
    contentViewHeight () {
      let height = this.$store.state.common.documentClientHeight - 122
      return { minHeight: height + 'px' }
    }
  },
  watch: {
    filterText (val) {
      this.$refs.docCategoryTree.filter(val)
    }
  },
  methods: {
    filterNode (value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },
    refreshTree () {
      this.$http({
        url: `/wps/docCategory/treeData`,
        method: 'get'
      }).then(({ data }) => {
        this.docCategoryTreeData = data.treeData
      })
    },
    handleNodeClick (data) {
      this.searchForm.category.id = data.id
      this.selectDocCategoryName = '已选: ' + data.name
      this.refreshList()
    },
    handleNodeClose () {
      this.searchForm.category.id = ''
      this.selectDocCategoryName = ''
      this.refreshList()
    },
    addChildTreeNode (node) {
      this.$refs.docCategoryForm.init('addChild', { id: '', parent: { id: node.id, name: node.name } })
    },
    // 新增
    addTreeNode () {
      this.$refs.docCategoryForm.init('add', { id: '', parent: { id: '', name: '' } })
    },
    design (row) {
      this.wpsUrl = `${process.env.VUE_APP_SERVER_URL}/weboffice/index?filename=${row.path}&fileid=${new Date().getTime()}`
      this.visible = true
    },
    // 修改
    editTreeNode (data) {
      this.$refs.docCategoryForm.init('edit', { id: data.id, parent: { id: '', name: '' } })
    },
    delTreeNode (data) {
      this.$confirm(`确定删除所选项吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        this.$http({
          url: '/wps/docCategory/delete',
          method: 'delete',
          params: { id: data.id }
        }).then(({ data }) => {
          if (data && data.success) {
            this.$message.success(data.msg)
            this.loading = false
            this.refreshTree()
            this.refreshList()
          }
        })
      })
    },
    // 获取数据列表
    refreshList () {
      this.loading = true
      this.$http({
        url: '/wps/docTemplate/list',
        method: 'get',
        params: {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          orderBy: this.orderBy,
          ...this.searchForm
        }
      }).then(({ data }) => {
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
      this.$refs.docTemplateForm.init('add', '')
    },
    // 修改
    edit (id) {
      id =
        id ||
        this.dataListSelections.map(item => {
          return item.id
        })[0]
      this.$refs.docTemplateForm.init('edit', id)
    },
    // 查看
    view (id) {
      this.$refs.docTemplateForm.init('view', id)
    },
    // 删除
    del (id) {
      let ids =
        id ||
        this.dataListSelections
          .map(item => {
            return item.id
          })
          .join(',')
      this.$confirm(`确定删除所选项吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        this.$http({
          url: '/wps/docTemplate/delete',
          method: 'delete',
          params: { ids: ids }
        }).then(({ data }) => {
          this.loading = false
          if (data && data.success) {
            this.$message.success(data.msg)
            this.refreshList()
          }
        })
      })
    },
    createFile (type) {
      this.$http({
        url: '/weboffice/new/url',
        method: 'get',
        params: { type: type }
      }).then(({ data }) => {
        window.open(data.url, '_blank')
      })
    },
    // 导入成功
    uploadSuccess (res, file) {
      if (res.success) {
        this.$message.success({ dangerouslyUseHTMLString: true, message: res.msg })
      } else {
        this.$message.error(res.msg)
      }
    },
    // 下载模板
    download (path) {
      window.open(path, '_blank')
    },
    exportExcel () {
      this.$utils.download('/wps/docTemplate/export')
    },
    resetSearch () {
      this.$refs.searchForm.resetFields()
      this.selectDocCategoryName = ''
      this.refreshList()
    }
  }
}
</script>

<style lang="scss">
  .el-message-box{
    margin-top: -15% !important;
  }
  .dbConfirmButton {
    background-color: #f56c6c !important;
    border-color: #f56c6c !important;
  }
  .word{
    .el-dialog__body{
      padding: 0px;
    }
  }
</style>
<style scoped>
  .dbFile{
    padding-top: 2%;
    padding-left: 4%;
  }
  .e-b{
    margin-right: 10px;
  }

  .el-dropdown + .el-dropdown {
    margin-left: 15px;
  }
  .el-icon-arrow-down {
    font-size: 12px;
  }
  .icon {
    width: 1.15em;
    height: 1.15em;
    vertical-align: -0.15em;
    fill: currentColor;
    overflow: hidden;
    margin: 0 8px 0 0;
  }
</style>
