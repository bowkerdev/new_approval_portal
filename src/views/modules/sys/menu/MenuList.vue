<template>
  <div class="page">
    <el-form size="small" :inline="true"  class="query-form" ref="searchForm" :model="searchForm"  @keyup.enter.native="refreshList()" @submit.native.prevent>
         <el-form-item prop="name">
          <el-input size="small"  v-model="searchForm.name"   :placeholder="$i18nMy.t('菜单名称')"   clearable></el-input>
        </el-form-item>
      <el-form-item>
        <el-button  type="primary" @click="refreshList()" size="small" icon="el-icon-search">{{$i18nMy.t('查询')}}</el-button>
        <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">{{$i18nMy.t('重置')}}</el-button>
      </el-form-item>
    </el-form>
    <div class="bg-white top">
    <vxe-toolbar :refresh="{query: refreshList}" export print custom>
      <template #buttons>
        <el-button v-if="hasPermission('sys:menu:add')" type="primary" icon="el-icon-plus" size="small" @click="add()">{{$i18nMy.t('新增')}}</el-button>
        <el-button v-if="hasPermission('sys:menu:del')" :disabled="$refs.xTree && $refs.xTree.getCheckboxRecords().length === 0" type="danger" size="small" icon="el-icon-delete" @click="del()">{{$i18nMy.t('删除')}}</el-button>
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
        :tree-config="{}"
        :loading="loading"
        :checkbox-config="{checkStrictly: true}"
        :data="dataList">
        <vxe-table-column type="checkbox"  width="40px">
        </vxe-table-column>
        <vxe-table-column  :title="$i18nMy.t('名称')" field="name" align="left" tree-node>
          <template slot-scope="scope">
              <el-link  type="primary" :underline="false" v-if="hasPermission('sys:menu:edit')" @click="edit(scope.row.id)">{{scope.row.name}}</el-link>
              <el-link  type="primary" :underline="false" v-else-if="hasPermission('sys:menu:view')"  @click="view(scope.row.id)">{{scope.row.name}}</el-link>
              <span v-else>{{scope.row.name}}</span>
            </template>
        </vxe-table-column>
        <vxe-table-column  :title="$i18nMy.t('图标')" field="icon" align="center">
            <template slot-scope="scope">
              <i :class="scope.row.icon"></i>
            </template>
        </vxe-table-column>
        <vxe-table-column  :title="$i18nMy.t('类型')" field="type" align="center">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.type === '0'" size="small">{{$i18nMy.t('目录')}}</el-tag>
                <el-tag v-else-if="scope.row.type === '1'" size="small" type="success">{{$i18nMy.t('菜单')}}</el-tag>
                <el-tag v-else-if="scope.row.type === '2'" size="small" type="info">{{$i18nMy.t('按钮')}}</el-tag>
                <el-tag v-else-if="scope.row.type === '3'" size="small" type="info">{{$i18nMy.t('路由')}}</el-tag>
              </template>
        </vxe-table-column>
        <vxe-table-column :title="$i18nMy.t('排序号')" field="sort" width="170px" align="center">
            <template slot-scope="scope">
                  <el-input-number size="small" v-model="scope.row.sort" @change="sortChange(scope.row)" :step="30"  :min="0" :max="10000" :label="$i18nMy.t('描述文字')"></el-input-number>
                </template>
        </vxe-table-column>
        <vxe-table-column :title="$i18nMy.t('是否显示')" field="isShow" align="center">
          <template slot-scope="scope">
                    <el-switch
                      v-model="scope.row.isShow"
                      v-if="scope.row.type === '0' || scope.row.type === '1'"
                      @change="statusChange(scope.row)"
                      active-color="#13ce66"
                      inactive-color="#ff4949"
                      active-value="1"
                      inactive-value="0">
                    </el-switch>
                </template>
        </vxe-table-column>
        <vxe-table-column  :title="$i18nMy.t('菜单路由')" field="href" ></vxe-table-column>
        <vxe-table-column  :title="$i18nMy.t('权限标志')" field="permission"></vxe-table-column>
        <vxe-table-column :title="$i18nMy.t('操作')" width="150px" fixed="right" align="center">
          <template slot-scope="scope">
                  <el-button  type="text" size="small" @click="showRight(scope.row)">{{$i18nMy.t('数据规则')}}</el-button>
                  <el-divider direction="vertical"></el-divider>
                  <el-dropdown size="small"  @command="handleCommand">
                    <span class="el-dropdown-link">
                      {{$i18nMy.t('更多')}}<i class="el-icon-arrow-down el-icon--right"></i>
                    </span>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item  v-if="hasPermission('sys:menu:view')" :command="{method:'view', id:scope.row.id}">
                      {{$i18nMy.t('查看')}}
                      </el-dropdown-item>
                      <el-dropdown-item v-if="hasPermission('sys:menu:edit')" :command="{method:'edit', id:scope.row.id}">
                        {{$i18nMy.t('修改')}}
                      </el-dropdown-item>
                      <el-dropdown-item v-if="hasPermission('sys:menu:del')" :command="{method:'del', id:scope.row.id}">
                        {{$i18nMy.t('删除')}}
                      </el-dropdown-item>
                      <el-dropdown-item v-if="hasPermission('sys:menu:add')" :command="{method:'addChild', row:scope.row}">
                        {{$i18nMy.t('添加下级菜单')}}
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
          </template>
      </vxe-table-column>
      </vxe-table>
    </div>
    </div>
    <el-drawer
      size = "700px"
      :title="`数据规则列表，所属菜单: ${this.dataRuleTitle}`"
      :visible.sync="rightVisible"
      direction="rtl">
      <data-rule-list  ref="dataRuleList" @closeRight="closeRight"></data-rule-list>
    </el-drawer>

    <!-- 弹窗, 新增 / 修改 -->
    <menu-form ref="menuForm"  @refreshDataList="refreshList"></menu-form>
  </div>
</template>

<script>
  import MenuForm from './MenuForm'
  import DataRuleList from './DataRuleList'
  import XEUtils from 'xe-utils'
  export default {
    data () {
      return {
        rightVisible: false,
        loading: false,
        dataRuleTitle: '',
        searchForm: {
          name: ''
        },
        dataList: []
      }
    },
    components: {
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
          url: '/sys/menu/treeData2',
          method: 'get'
        }).then(({data}) => {
          this.loading = false
          this.dataList = data.treeData
          this.handleSearch()
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
      addChild (row) {
        this.$refs.menuForm.init('addChild', {id: '', parent: {id: row.id, name: name}})
      },
      // 新增
      add () {
        this.$refs.menuForm.init('add', {id: '', parent: {id: '', name: ''}})
      },
      // 修改
      edit (id) {
        this.$refs.menuForm.init('edit', {id: id, parent: {id: '', name: ''}})
      },
      // 查看
      view (id) {
        this.$refs.menuForm.init('view', {id: id, parent: {id: '', name: ''}})
      },
      handleCommand (command) {
        if (command.method === 'view') {
          this.view(command.id)
        } else if (command.method === 'edit') {
          this.edit(command.id)
        } else if (command.method === 'del') {
          this.del(command.id)
        } else if (command.method === 'addChild') {
          this.addChild(command.row)
        }
      },
      // 删除
      del (id) {
        let ids = id || this.$refs.xTree.getCheckboxRecords().map(item => {
          return item.id
        }).join(',')
        this.$confirm($i18nMy.t('确定删除该条记录吗?'), $i18nMy.t('提示'), {
          confirmButtonText: $i18nMy.t('确定'),
          cancelButtonText: $i18nMy.t('取消'),
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: `/sys/menu/delete`,
            method: 'delete',
            params: {'ids': ids}
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
      },
      statusChange (row) {
        this.loading = true
        this.$http({
          url: `/sys/menu/save`,
          method: 'post',
          data: {id: row.id, isShow: row.isShow}
        }).then(({data}) => {
          if (data && data.success) {
            this.$message({
              message: data.msg,
              type: 'success',
              duration: 1500
            })
          }
          this.loading = false
          this.refreshList()
        })
      },
      sortChange (row) {
        this.loading = true
        this.$http({
          url: `/sys/menu/save`,
          method: 'post',
          data: {id: row.id, sort: row.sort}
        }).then(({data}) => {
          if (data && data.success) {
            this.$message({
              message: data.msg,
              type: 'success',
              duration: 1500
            })
          }
          this.loading = false
          this.refreshList()
        })
      },
      showRight (row) {
        this.rightVisible = true
        this.$nextTick(() => {
          this.$refs.dataRuleList.refreshList(row.id)
          this.dataRuleTitle = row.name
        })
      },
      closeRight () {
        this.rightVisible = false
      }
    }
  }
</script>
