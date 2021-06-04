<template>
  <div class="page bg-white">
    <el-button-group>
      <el-button v-if="hasPermission('sys:office:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新增')}}</el-button>
    </el-button-group>
    <el-treetable 
      :data="dataList"
      isBigData
      isTreeTable
      expanded
      size="small"
      v-loading = "loading"
      row-key="id"
      class="table treetable">
      <el-treetable-column
        prop="name"
        header-align="center"
        show-overflow-tooltip
        align="left"
        width="200"
        :label="$i18nMy.t('机构名称')">
        <template slot-scope="scope">
          <el-link  type="primary" :underline="false" v-if="hasPermission('sys:office:edit')" @click="edit(scope.row.id)">{{scope.row.name}}</el-link>
          <el-link  type="primary" :underline="false" v-else-if="hasPermission('sys:office:view')"  @click="view(scope.row.id)">{{scope.row.name}}</el-link>
          <span v-else>{{scope.row.name}}</span>
        </template>
      </el-treetable-column>
      <el-treetable-column
        prop="code"
        width="150"
        :label="$i18nMy.t('机构编码')">
      </el-treetable-column>
      <el-treetable-column
        width="200"
        prop="grade"
        :label="$i18nMy.t('机构类型')">
        <template slot-scope="scope">
          {{$dictUtils.getDictLabel("sys_office_grade", scope.row.grade)}}
        </template>
      </el-treetable-column>
      <el-treetable-column
        prop="area.name"
        width="200"
        :label="$i18nMy.t('归属区域')">
      </el-treetable-column>
      <el-treetable-column
        prop="useable"
        :label="$i18nMy.t('是否可用')">
        <template slot-scope="scope">
          <el-tag :type="scope.row.useable == '0' ? 'danger' : 'success'">
            {{ $dictUtils.getDictLabel("yes_no", scope.row.useable, '否') }}
          </el-tag>
        </template>
      </el-treetable-column>
      <el-treetable-column
        fixed="right"
        :key="Math.random()"
        header-align="center"
        align="center"
        width="300"
        :label="$i18nMy.t('操作')">
        <template slot-scope="scope">
          <el-button v-if="hasPermission('sys:office:view')" type="text" icon="el-icon-view" size="small" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('sys:office:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('sys:office:del')" type="text" icon="el-icon-delete" size="small" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
          <el-button v-if="hasPermission('sys:office:add')" type="text" icon="el-icon-circle-plus-outline" size="small" @click="addChild(scope.row.id, scope.row.name)">{{$i18nMy.t('添加下级机构')}}</el-button>
        </template>
      </el-treetable-column>
    </el-treetable >
    <!-- 弹窗, 新增 / 修改 -->
    <OfficeForm ref="officeForm" @refreshDataList="refreshList"></OfficeForm>
  </div>
</template>

<script>
  import OfficeForm from './OfficeForm'

  export default {
    name: 'sys-office-OfficeList',
    data () {
      return {
        loading: false,
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
          this.loading = false
        })
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
        this.$confirm(`确定删除该记录吗?`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/sys/office/delete',
            method: 'delete',
            params: {id: id}
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
