<template>
  <div>
      <el-form :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
         <el-form-item prop="company.id">
            <!-- <SelectTree
                  ref="company.id"
                  :props="{
                      value: 'id',             // ID字段名
                      label: 'name',         // 显示名称
                      children: 'children'    // 子级字段名
                    }"
                   size="small"
                  :url="`/sys/office/treeData?type=1`"
                  :value="searchForm.company.id"
                  :clearable="true"
                  :accordion="true"
                  @getValue="(value) => {searchForm.company.id=value}"/> -->
             <el-select size="small" v-model="searchForm.company.id" :placeholder="$i18nMy.t('所有公司')" @change="siteChange" clearable>
              <el-option v-for="item in siteList" :key="item.value"
                :label="item.label" :value="item.value" >
              </el-option>
             </el-select>
         </el-form-item>
         <el-form-item prop="department.id">
            <SelectTree
                  :placeholder="$i18nMy.t('所有部门')"
                  v-if="ifSiteChange"
                  ref="department.id"
                  :props="{
                      value: 'id',             // ID字段名
                      label: 'name',         // 显示名称
                      children: 'children'    // 子级字段名
                    }"
                   size="small"
                   :url="`/sys/office/treeData?type=2&parentId=${searchForm.company.id}`"
                  :value="searchForm.department.id"
                  :clearable="true"
                  :accordion="true"
                  @getValue="(value) => {searchForm.department.id=value}"/>
         </el-form-item>
         <el-form-item prop="procDefKey">
                <!-- <el-input size="small" v-model="searchForm.procDefKey" :placeholder="$i18nMy.t('流程标识')" clearable></el-input> -->
                <el-select size="small" v-model="searchForm.procDefKey" :placeholder="$i18nMy.t('流程标识')" clearable allow-create filterable>
                 <el-option v-for="item in procDefKeyList" :key="item.value"
                   :label="item.label" :value="item.value" >
                 </el-option>
                </el-select>
         </el-form-item>
         <el-form-item prop="userGroup">
                <!-- <el-input size="small" v-model="searchForm.userGroup" :placeholder="$i18nMy.t('组标识')" clearable></el-input> -->
                <el-select size="small" v-model="searchForm.userGroup" :placeholder="$i18nMy.t('组标识')" clearable allow-create filterable>
                 <el-option v-for="item in userGroupList" :key="item.value"
                   :label="item.label" :value="item.value" >
                 </el-option>
                </el-select>
         </el-form-item>
         <el-form-item prop="userGroupCode">
                <el-input size="small" v-model="searchForm.userGroupCode" :placeholder="$i18nMy.t('组标识辅助码')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="user.id">
            <user-select :placeholder="$i18nMy.t('用户')"  :limit='1' size="small" :value="searchForm.user.id" @getValue='(value) => {searchForm.user.id=value}'></user-select>
         </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small" icon="el-icon-search">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
        <!-- 导入导出-->

      <div class="bg-white top">
      <el-row>
        <el-button v-if="hasPermission('flowable:wf:wfUserGroup:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('flowable:wf:wfUserGroup:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}
        </el-button>

      </el-row>
    <el-table
      :data="dataList"
      border
      size="medium"
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
      v-loading="loading"
      class="table">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
        prop="company.name"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('公司')">
            <template slot-scope="scope">
              <el-link  type="primary" :underline="false" v-if="hasPermission('flowable:wf:wfUserGroup:edit')" @click="edit(scope.row.id)">{{scope.row.company.name||scope.row.company.id||"ALL"}}</el-link>
              <el-link  type="primary" :underline="false" v-else-if="hasPermission('flowable:wf:wfUserGroup:view')"  @click="view(scope.row.id)">{{scope.row.company.name||scope.row.company.id||"ALL"}}</el-link>
              <span v-else>
                {{scope.row.company.name||scope.row.company.id||"ALL"}}212121
              </span>
            </template>
      </el-table-column>
      <el-table-column
        prop="department.name"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('部门')">
        <template slot-scope="scope">
          {{scope.row.department.name||"ALL"}}
        </template>
      </el-table-column>
    <el-table-column
        prop="procDefKey"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('流程标识')">
      </el-table-column>
    <el-table-column
        prop="userGroup"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('组标识')">
      </el-table-column>
    <el-table-column
        prop="userGroupCode"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('组标识辅助码')">
      </el-table-column>
      <el-table-column
        prop="user.name"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('用户')">
        <template slot-scope="scope">
          {{scope.row.user.loginName}} : {{scope.row.user.name}}
        </template>
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        fixed="right"
        width="200"
        :label="$i18nMy.t('操作')">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('flowable:wf:wfUserGroup:view')" type="text" icon="el-icon-view" size="small" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('flowable:wf:wfUserGroup:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('flowable:wf:wfUserGroup:del')" type="text"  icon="el-icon-delete" size="small" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
    <WfUserGroupForm  ref="wfUserGroupForm" @refreshDataList="refreshList"></WfUserGroupForm>
  </div>
</template>

<script>
  import WfUserGroupForm from './WfUserGroupForm'
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  import UserSelect from '@/components/userSelect'
  export default {
    data () {
      return {
        ifSiteChange: true,
        siteList:[],
        procDefKeyList:[],
        userGroupList:[],
        searchForm: {
          company: {
            id: ''
          },
          department: {
            id: ''
          },
          procDefKey: '',
          userGroup: '',
          user: {
            id: ''
          }
        },
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
      UserSelect,
      WfUserGroupForm
    },
    activated () {
      let _that=this
      this.$dictUtils.getSqlDictList('GET_APPLY_SITE',{},function(data){
        _that.siteList = data
      })
      this.$dictUtils.getSqlDictList('GET_procDefKey_LIST',{},function(data){
        _that.procDefKeyList = data
      })
      this.$dictUtils.getSqlDictList('GET_userGroup_LIST',{},function(data){
        _that.userGroupList = data
      })
      this.refreshList()
    },

    methods: {
      siteChange(){
        this.ifSiteChange = false;
        this.$nextTick(() => {
          this.ifSiteChange = true;
        })
      },
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/flowable/wf/wfUserGroup/list',
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
        this.$refs.wfUserGroupForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.wfUserGroupForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.wfUserGroupForm.init('view', id)
      },
      // 删除
      del (id) {
        let ids = id || this.dataListSelections.map(item => {
          return item.id
        }).join(',')
        this.$confirm($i18nMy.t('确定删除所选项吗') + '?', $i18nMy.t('提示'), {
          confirmButtonText: $i18nMy.t('确定'),
          cancelButtonText: $i18nMy.t('取消'),
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/flowable/wf/wfUserGroup/delete',
            method: 'delete',
            params: {'ids': ids}
          }).then(({data}) => {
            if (data && data.success) {
              this.$message.success($i18nMy.t(data.msg))
              this.refreshList()
            }
            this.loading = false
          })
        })
      },
      // 导入成功
      uploadSuccess (res, file) {
        if (res.success) {
          this.$message.success({dangerouslyUseHTMLString: true,
            message: res.msg})
        } else {
          this.$message.error(res.msg)
        }
      },
      // 下载模板
      downloadTpl () {
        this.$utils.download('/flowable/wf/wfUserGroup/import/template')
      },
      exportExcel () {
        this.$utils.download('/flowable/wf/wfUserGroup/export')
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>
