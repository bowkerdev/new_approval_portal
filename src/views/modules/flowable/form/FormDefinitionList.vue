<template>
    <div class="jp-common-layout page">
      <div class="jp-common-layout-left">
        <div class="jp-common-title"> 
          <el-row :gutter="5">
            <el-col :span="20">
              <el-input
                :placeholder="$i18nMy.t('输入关键字进行过滤')"
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
                :data="formCategoryTreeData"
                :props="{
                      value: 'id',             // ID字段名
                      label: 'name',         // 显示名称
                      children: 'children'    // 子级字段名
                    }"
                default-expand-all
                :filter-node-method="filterNode"
                :expand-on-click-node="false"
                highlight-current
                node-key="id"
                @node-click="handleNodeClick"
                ref="formCategoryTree">
                  <span class="custom-tree-node" slot-scope="{ node, data}">
                    <span>{{ node.label }}</span>
                    <span>
                      <el-button type="text" class="tree-item-button" icon="el-icon-plus" @click="() => addChildTreeNode(data)">
                      </el-button>
                        <el-button type="text" class="tree-item-button" icon="el-icon-edit-outline" @click="() => editTreeNode(data)">
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
      <el-form size="small" :inline="true"  class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
		     <el-form-item prop="category.id">
		          <SelectTree
                      ref="category"
                      :props="{
                          value: 'id',             // ID字段名
                          label: 'name',         // 显示名称
                          children: 'children'    // 子级字段名
                        }"
                      size="small"
                      url="/extension/formCategory/treeData"
                      :value="searchForm.category.id"
                      :clearable="true"
                      :accordion="true"
                      @getValue="(value) => {searchForm.category.id=value}"/>
		     </el-form-item>
		     <el-form-item prop="name">
                <el-input size="small" v-model="searchForm.name" :placeholder="$i18nMy.t('表单名称')" clearable></el-input>
		     </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
      <div class="bg-white top">
      <el-row>
        <el-button v-if="hasPermission('extension:formDefinition:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('extension:formDefinition:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
        <el-button v-if="hasPermission('extension:formDefinition:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}</el-button>
        <el-button-group class="pull-right">
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
        @selection-change="selectionChangeHandle"
        @sort-change="sortChangeHandle"
        height="calc(100% - 80px)"
        v-loading="loading"
        class="table">
        <el-table-column
          type="selection"
          size="small"
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
            <el-link  type="primary" :underline="false" v-if="hasPermission('extension:formDefinition:edit')" @click="edit(scope.row.id)">{{scope.row.name}}</el-link>
            <el-link  type="primary" :underline="false" v-else-if="hasPermission('extension:formDefinition:view')"  @click="view(scope.row.id)">{{scope.row.name}}</el-link>
            <span v-else>{{scope.row.name}}</span>
          </template>
      </el-table-column>
      <el-table-column
          prop="category.name"
          show-overflow-tooltip
        :label="$i18nMy.t('分类')">
        </el-table-column>
      <el-table-column
          prop="formDefinitionJson.version"
          show-overflow-tooltip
        :label="$i18nMy.t('版本号')">
      </el-table-column>
      <el-table-column
          prop="formDefinitionJson.status"
          show-overflow-tooltip
        :label="$i18nMy.t('状态')">
          <template slot-scope="scope">
          <el-tag v-if="scope.row.formDefinitionJson.status === '1'" size="small" type="success">{{$i18nMy.t('已发布')}}</el-tag>
          <el-tag v-else size="small" type="danger">{{$i18nMy.t('未发布')}}</el-tag>
          </template>
        </el-table-column>
      <el-table-column
          prop="formDefinitionJson.isPrimary"
          show-overflow-tooltip
        :label="$i18nMy.t('是否主版本')">
          <template slot-scope="scope">
          <el-tag v-if="scope.row.formDefinitionJson.isPrimary === '1'" size="small" type="success">{{$i18nMy.t('主版本')}}</el-tag>
          <el-tag v-else size="small" type="danger">{{$i18nMy.t('非主版本')}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          header-align="center"
          align="center"
          fixed="right"
          :key="Math.random()"
          width="250"
        :label="$i18nMy.t('操作')">
          <template  slot-scope="scope">
          <el-button v-if="hasPermission('extension:formDefinition:view')" type="text" icon="el-icon-view" size="small" @click="showDesignForm(scope.row.id, scope.row.formDefinitionJson.id)">{{$i18nMy.t('设计')}}</el-button>
          <el-button v-if="hasPermission('extension:formDefinition:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('extension:formDefinition:edit')" type="text" icon="el-icon-edit" size="small" @click="manage(scope.row.id)">{{$i18nMy.t('版本管理')}}</el-button>
          <el-button v-if="hasPermission('extension:formDefinition:del')" type="text"  icon="el-icon-delete" size="small" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
      <FormDefinitionForm  ref="formDefinitionForm" @showDesignForm="showDesignForm" @refreshDataList="refreshList"></FormDefinitionForm>
      <FormCategoryForm  ref="formCategoryForm"  @refreshTree="refreshTree"></FormCategoryForm>
      <DesignForm  ref="designForm" @refreshDataList="refreshList"></DesignForm>
      </div>
  </div>
  </div>
</template>

<script>
  import FormDefinitionForm from './FormDefinitionForm'
  import FormCategoryForm from './FormCategoryForm'
  import DesignForm from './MakeFlowableForm'
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  export default {
    name: 'flowable-form-FormDefinitionList',
    data () {
      return {
        searchForm: {
          category: {
            id: ''
          },
          name: ''
        },
        filterText: '',
        formCategoryTreeData: [],
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
      SelectTree,
      FormDefinitionForm,
      FormCategoryForm,
      DesignForm
    },
    activated () {
      this.refreshTree()
      this.refreshList()
    },
    watch: {
      filterText (val) {
        this.$refs.formCategoryTree.filter(val)
      }
    },
    methods: {
      filterNode (value, data) {
        if (!value) return true
        return data.name.indexOf(value) !== -1
      },
      refreshTree () {
        this.$http({
          url: `/extension/formCategory/treeData`,
          method: 'get'
        }).then(({data}) => {
          this.formCategoryTreeData = data.treeData
        })
      },
      handleNodeClick (data) {
        this.searchForm.category.id = data.id
        this.refreshList()
      },
      addChildTreeNode (node) {
        this.$refs.formCategoryForm.init('addChild', {id: '', parent: {id: node.id, name: node.name}})
      },
       // 新增
      addTreeNode () {
        this.$refs.formCategoryForm.init('add', {id: '', parent: {id: '', name: ''}})
      },
      // 修改
      editTreeNode (data) {
        this.$refs.formCategoryForm.init('edit', {id: data.id, parent: {id: '', name: ''}})
      },
      delTreeNode (data) {
        this.$confirm(`确定删除所选项吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/extension/formCategory/delete',
            method: 'delete',
            params: {'id': data.id}
          }).then(({data}) => {
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
          url: '/extension/formDefinition/list',
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
        this.$refs.formDefinitionForm.init('add', '')
      },
            // 新增
      showDesignForm (id, jsonId) {
        this.$refs.designForm.init(id, jsonId)
      },
      manage (id) {
        this.$router.push(`/flowable/form/FormDefinitionJsonList?id=${id}`)
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.formDefinitionForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.formDefinitionForm.init('view', id)
      },
      // 删除
      del (id) {
        let ids = id || this.dataListSelections.map(item => {
          return item.id
        }).join(',')
        this.$confirm(`确定删除所选流程表单以及级联的表单版本吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/extension/formDefinition/delete',
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
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.filterText = ''
        this.$refs.formCategoryTree.setCurrentKey(null)
        this.refreshList()
      }
    }
  }
</script>
