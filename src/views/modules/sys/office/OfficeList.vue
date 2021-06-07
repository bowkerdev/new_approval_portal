<template>
  <div class="page">
    <el-form size="small" :inline="true"  class="query-form" ref="searchForm" :model="searchForm"  @keyup.enter.native="refreshList()" @submit.native.prevent>
        <el-form-item prop="name">
          <el-input size="small"  v-model="searchForm.name"   :placeholder="$i18nMy.t('机构名称')"   clearable></el-input>
        </el-form-item>
      <el-form-item>
        <el-button  type="primary" @click="refreshList()" size="small" icon="el-icon-search">{{$i18nMy.t('查询')}}</el-button>
        <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">{{$i18nMy.t('重置')}}</el-button>
      </el-form-item>
    </el-form>
    <div class="bg-white top">
    <vxe-toolbar :refresh="{query: refreshList}" export print custom>
      <template #buttons>
        <el-row>
          <el-button v-if="hasPermission('sys:office:add')" type="primary" icon="el-icon-plus" size="small" @click="add()">{{$i18nMy.t('新增')}}</el-button>
          <el-button v-if="hasPermission('sys:office:del')" :disabled="$refs.xTree && $refs.xTree.getCheckboxRecords().length === 0" type="danger" size="small" icon="el-icon-delete" @click="del()">{{$i18nMy.t('删除')}}</el-button>
        </el-row>
      </template>
    </vxe-toolbar>
    <div style="height: calc(100% - 50px);">
      <vxe-table
        resizable
        ref="xTree"
        border="inner"
        auto-resize
        height="auto"
        row-id="id"
        size="small"
        show-header-overflow
        show-overflow
        highlight-hover-row
        :print-config="{}"
        :export-config="{}"
        :tree-config="{expandAll: true}"
        :loading="loading"
        :checkbox-config="{labelField: ''}"
        :data="dataList">
        <vxe-table-column type="checkbox" width="40px"> </vxe-table-column>
        <vxe-table-column  :title="$i18nMy.t('机构名称')"  field="name"  align="left" tree-node >
          <template slot-scope="scope">
            <el-link  type="primary" :underline="false" v-if="hasPermission('sys:office:edit')" @click="edit(scope.row.id)">{{scope.row.name}}</el-link>
            <el-link  type="primary" :underline="false" v-else-if="hasPermission('sys:office:view')"  @click="view(scope.row.id)">{{scope.row.name}}</el-link>
            <span v-else>{{scope.row.name}}</span>
          </template>
        </vxe-table-column>
        <vxe-table-column  :title="$i18nMy.t('机构编码')" field="code" align="center"></vxe-table-column>
        <vxe-table-column  :title="$i18nMy.t('机构类型')" field="grade" align="center">
          <template slot-scope="scope">
            {{$dictUtils.getDictLabel("sys_office_grade", scope.row.grade)}}
          </template>
        </vxe-table-column>
         <vxe-table-column  :title="$i18nMy.t('归属区域')" field="area.name" align="center"></vxe-table-column>
        <vxe-table-column  :title="$i18nMy.t('是否可用')" field="useable" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.useable == '0' ? 'danger' : 'success'">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.useable, '否') }}
            </el-tag>
          </template>
        </vxe-table-column>
        <vxe-table-column :title="$i18nMy.t('操作')" width="300px" fixed="right" align="center">
          <template slot-scope="scope">
            <el-button v-if="hasPermission('sys:office:view')" type="text" icon="el-icon-view" size="small" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
            <el-button v-if="hasPermission('sys:office:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
            <el-button v-if="hasPermission('sys:office:del')" type="text" icon="el-icon-delete" size="small" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
            <el-button v-if="hasPermission('sys:office:add')" type="text" icon="el-icon-circle-plus-outline" size="small" @click="addChild(scope.row.id, scope.row.name)">{{$i18nMy.t('添加下级机构')}}</el-button>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    </div>
    <!-- 弹窗, 新增 / 修改 -->
    <OfficeForm ref="officeForm" @refreshDataList="refreshList"></OfficeForm>
  </div>
</template>

<script>
  import OfficeForm from './OfficeForm'
  import XEUtils from 'xe-utils'
  export default {
    data () {
      return {
        loading: false,
        searchForm: {
          name: ''
        },
        dataList: []
      }
    },
    components: {
      OfficeForm
    },
    activated () {
      this.refreshList()
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/sys/office/treeData',
          method: 'get'
        }).then(({data}) => {
          this.dataList = data.treeData
          this.handleSearch()
          this.loading = false
        })
      },
      handleSearch () {
        let filterName = XEUtils.toValueString(this.searchForm.name).trim()
        if (filterName) {
          let options = { children: 'children' }
          let searchProps = ['name']
          this.dataList = XEUtils.searchTree(this.dataList, item => searchProps.some(key => XEUtils.toValueString(item[key]).indexOf(filterName) > -1), options)
          this.$nextTick(() => {
            this.$refs.xTree.setAllTreeExpand(true)
          })
        }
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      },
       // 新增下级
      addChild (id, name) {
        this.$refs.officeForm.init('addChild', {id: '', parent: {id: id, name: name}})
      },
       // 新增
      add () {
        this.$refs.officeForm.init('add', {id: '', parent: {id: '', name: ''}})
      },
      // 修改
      edit (id) {
        this.$refs.officeForm.init('edit', {id: id, parent: {id: '', name: ''}})
      },
      // 查看
      view (id) {
        this.$refs.officeForm.init('view', {id: id, parent: {id: '', name: ''}})
      },
      // 删除
      del (id) {
        let ids = id || this.$refs.xTree.getCheckboxRecords().map(item => {
          return item.id
        }).join(',')
        this.$confirm($i18nMy.t('确定删除该记录吗?'), $i18nMy.t('提示'), {
          confirmButtonText: $i18nMy.t('确定'),
          cancelButtonText: $i18nMy.t('取消'),
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/sys/office/delete',
            method: 'delete',
            params: {ids: ids}
          }).then(({data}) => {
            this.loading = false
            if (data && data.success) {
              this.$message({
                message: data.msg,
                type: 'success',
                duration: 1500
              })
              this.refreshList()
            }
          })
        })
      }
    }
  }
</script>
