<template>
  <div class="page" style="height: calc(100% - 76px);">
      <el-form size="small" :inline="true" v-show="isSearchCollapse" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
         <el-form-item prop="cn">
                <el-input size="small" v-model="searchForm.cn" :placeholder="$i18nMy.t('简体中文')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="hk">
                <el-input size="small" v-model="searchForm.hk" :placeholder="$i18nMy.t('繁体中文')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="en">
                <el-input size="small" v-model="searchForm.en" :placeholder="$i18nMy.t('英文')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="vn">
                <el-input size="small" v-model="searchForm.vn" :placeholder="$i18nMy.t('越南文')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="cam">
                <el-input size="small" v-model="searchForm.cam" :placeholder="$i18nMy.t('柬埔寨')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="code">
                <el-input size="small" v-model="searchForm.code" :placeholder="$i18nMy.t('识别码')" clearable></el-input>
         </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
        <!-- 导入导出-->
      <el-form :inline="true" v-show="isImportCollapse"  class="query-form" ref="importForm">
         <el-form-item>
          <el-button type="default" @click="downloadTpl()" size="small">{{$i18nMy.t('下载模板')}}</el-button>
         </el-form-item>
         <el-form-item prop="loginName">
            <el-upload
              class="upload-demo"
              :action="`${this.$http.BASE_URL}/sys/sysSimpleLanguage/import`"
              :on-success="uploadSuccess"
               :show-file-list="true">
              <el-button size="small" type="primary">{{$i18nMy.t('点击上传')}}</el-button>
              <div slot="tip" class="el-upload__tip">{{$i18nMy.t('只允许导入“xls”或“xlsx”格式文件')}}！</div>
            </el-upload>
        </el-form-item>
      </el-form>
      <div class="bg-white top">
      <el-row>
<!--        <el-button v-if="hasPermission('sys:sysSimpleLanguage:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>-->
        <el-button v-if="hasPermission('sys:sysSimpleLanguage:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
        <el-button v-if="hasPermission('sys:sysSimpleLanguage:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}
        </el-button>
        <el-button-group class="pull-right">
            <el-button
              type="default"
              size="small"
              icon="el-icon-search"
              @click="isSearchCollapse = !isSearchCollapse, isImportCollapse=false">
            </el-button>
            <el-button v-if="hasPermission('sys:sysSimpleLanguage:import')" type="default" size="small" icon="el-icon-upload2" :title="$i18nMy.t('导入')" @click="isImportCollapse = !isImportCollapse, isSearchCollapse=false"></el-button>
            <el-button v-if="hasPermission('sys:sysSimpleLanguage:export')" type="default" size="small" icon="el-icon-download" :title="$i18nMy.t('导出')" @click="exportExcel()"></el-button>
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
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
      v-loading="loading"
      size="small"
      @expand-change="detail"
      class="table">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
    <el-table-column
        prop="cn"
        sortable="custom"
        :label="$i18nMy.t('简体中文')">
            <template slot-scope="scope">
              <el-link  type="primary" :underline="false" v-if="hasPermission('sys:sysSimpleLanguage:edit')" @click="edit(scope.row.id)">{{scope.row.cn}}</el-link>
              <el-link  type="primary" :underline="false" v-else-if="hasPermission('sys:sysSimpleLanguage:view')"  @click="view(scope.row.id)">{{scope.row.cn}}</el-link>
              <span v-else>{{scope.row.cn}}</span>
            </template>
      </el-table-column>
    <el-table-column
        prop="hk"
        sortable="custom"
        :label="$i18nMy.t('繁体中文')">
      </el-table-column>
    <el-table-column
        prop="en"
        sortable="custom"
        :label="$i18nMy.t('英文')">
      </el-table-column>
    <el-table-column
        prop="vn"
        sortable="custom"
        :label="$i18nMy.t('越南文')">
      </el-table-column>
    <el-table-column
        prop="cam"
        sortable="custom"
        :label="$i18nMy.t('柬埔寨')">
      </el-table-column>
    <el-table-column
        prop="code"
        sortable="custom"
        :label="$i18nMy.t('识别码')">
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        width="200"
        :label="$i18nMy.t('操作')">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('sys:sysSimpleLanguage:view')" type="text" icon="el-icon-view" size="small" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('sys:sysSimpleLanguage:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('sys:sysSimpleLanguage:del')" type="text"  icon="el-icon-delete" size="small" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>
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
    <SysSimpleLanguageForm  ref="sysSimpleLanguageForm" @refreshDataList="refreshList"></SysSimpleLanguageForm>
  </div>
</template>

<script>
  import SysSimpleLanguageForm from './SysSimpleLanguageForm'
  export default {
    name: 'sys-language-SysSimpleLanguageList',
    data () {
      return {
        searchForm: {
          cn: '',
          hk: '',
          en: '',
          vn: '',
          cam: '',
          code: ''
        },
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        orderBy: '',
        dataListSelections: [],
        isSearchCollapse: true,
        isImportCollapse: false,
        loading: false
      }
    },
    components: {
      SysSimpleLanguageForm
    },
    activated () {
      this.refreshList()
    },

    methods: {
      // 获取数据列表
      refreshList () {
        let _self = this
        _self.loading = true
        _self.$http({
          url: '/sys/sysSimpleLanguage/list',
          method: 'get',
          params: {
            'pageNo': _self.pageNo,
            'pageSize': _self.pageSize,
            'orderBy': _self.orderBy,
            ..._self.searchForm
          }
        }).then(({data}) => {
          debugger
          if (data && data.success) {
            _self.dataList = data.page.list
            console.log(_self.dataList)
            _self.total = data.page.count
            _self.loading = false
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
        this.$refs.sysSimpleLanguageForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.sysSimpleLanguageForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.sysSimpleLanguageForm.init('view', id)
      },
      // 删除
      del (id) {
        let ids = id || this.dataListSelections.map(item => {
          return item.id
        }).join(',')
         // eslint-disable-next-line no-undef
        this.$confirm($i18nMy.t('确定删除所选项吗') + '?', $i18nMy.t('提示'), {
        // eslint-disable-next-line no-undef
          confirmButtonText: $i18nMy.t('确定'),
          // eslint-disable-next-line no-undef
          cancelButtonText: $i18nMy.t('取消'),
          type: 'warning'
        }).then(() => {
          this.loading = true
          this.$http({
            url: '/sys/sysSimpleLanguage/delete',
            method: 'delete',
            params: {'ids': ids}
          }).then(({data}) => {
            if (data && data.success) {
              this.$message.success(data.msg)
              this.refreshList()
            }
            this.loading = false
          })
        })
      },
     // 查看详情
      detail (row) {
        this.$http.get(`/sys/sysSimpleLanguage/queryById?id=${row.id}`).then(({data}) => {
          this.dataList.forEach((item, index) => {
            if (item.id === row.id) {
            }
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
        this.$utils.download('/sys/sysSimpleLanguage/import/template')
      },
      exportExcel () {
        this.$utils.download('/sys/sysSimpleLanguage/export')
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>
