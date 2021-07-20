<template>
  <div class="page">
      <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small">查询</el-button>
            <el-button @click="resetSearch()" size="small">重置</el-button>
          </el-form-item>
      </el-form>
        <!-- 导入导出-->
        <el-dialog  title="导入Excel" :visible.sync="isImportCollapse">
          <el-form :inline="true" v-show="isImportCollapse"  class="query-form" ref="importForm">
             <el-form-item>
              <el-button type="default" @click="downloadTpl()" size="small">下载模板</el-button>
             </el-form-item>
             <el-form-item prop="loginName">
                <el-upload
                  class="upload-demo"
                  :action="`${this.$http.BASE_URL}/flow/pr/oaPrNew/import`"
                  :on-success="uploadSuccess"
                   :show-file-list="true">
                  <el-button size="small" type="primary">点击上传</el-button>
                  <div slot="tip" class="el-upload__tip">只允许导入“xls”或“xlsx”格式文件！</div>
                </el-upload>
            </el-form-item>
          </el-form>
      </el-dialog>
      <div class="bg-white top">
      <el-row>
        <el-button v-if="hasPermission('flow:pr:oaPrNew:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
        <el-button v-if="hasPermission('flow:pr:oaPrNew:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>修改</el-button>
        <el-button v-if="hasPermission('flow:pr:oaPrNew:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>删除
        </el-button>
        <el-button-group class="pull-right">
            <el-button v-if="hasPermission('flow:pr:oaPrNew:import')" type="default" size="small" icon="el-icon-upload2" title="导入" @click="isImportCollapse = !isImportCollapse, isSearchCollapse=false"></el-button>
            <el-button v-if="hasPermission('flow:pr:oaPrNew:export')" type="default" size="small" icon="el-icon-download" title="导出" @click="exportExcel()"></el-button>
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
      height="calc(100% - 80px)"
      @expand-change="detail"
      class="table">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column type="expand" >
      <template slot-scope="scope">
        <el-table
            size="small"
            :data="scope.row.detailInfoList"
            style="width: 100%">
          <el-table-column  prop="item" show-overflow-tooltip :label="$i18nMy.t('物品')">
          </el-table-column>
          <el-table-column  prop="brandName" show-overflow-tooltip :label="$i18nMy.t('品牌')">
          </el-table-column>
          <el-table-column  prop="modelNo" show-overflow-tooltip :label="$i18nMy.t('型号')">
          </el-table-column>
          <el-table-column  prop="quantity" show-overflow-tooltip :label="$i18nMy.t('数量')">
          </el-table-column>
          <el-table-column  prop="uom" show-overflow-tooltip :label="$i18nMy.t('单位')">
          </el-table-column>
        </el-table>
      </template>
      </el-table-column>
    <el-table-column
        prop="applicationNo"
        show-overflow-tooltip
        sortable="custom"
        label="申请单号">
            <template slot-scope="scope">
              <el-link  type="primary" :underline="false" v-if="hasPermission('flow:pr:oaPrNew:edit')" @click="edit(scope.row.id)">{{scope.row.applicationNo}}</el-link>
              <el-link  type="primary" :underline="false" v-else-if="hasPermission('flow:pr:oaPrNew:view')"  @click="view(scope.row.id)">{{scope.row.applicationNo}}</el-link>
              <span v-else>{{scope.row.applicationNo}}</span>
            </template>
      </el-table-column>
    <el-table-column
        prop="projectName"
        show-overflow-tooltip
        sortable="custom"
        label="项目名称">
      </el-table-column>
    <el-table-column
        prop="applySiteCode"
        show-overflow-tooltip
        sortable="custom"
        label="采购地区">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("apply_site_code", scope.row.applySiteCode, '-') }}
        </template>
      </el-table-column>
      <el-table-column
        prop="requesterDepartment.name"
        show-overflow-tooltip
        sortable="custom"
        label="请求者部门">
      </el-table-column>
    <el-table-column
        prop="expenseType"
        show-overflow-tooltip
        sortable="custom"
        label="费用类型">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("expense_type", scope.row.expenseType, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="costCenter"
        show-overflow-tooltip
        sortable="custom"
        label="成本中心">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("cost_center", scope.row.costCenter, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="assetGroup"
        show-overflow-tooltip
        sortable="custom"
        label="固定资产类型">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("asset_group", scope.row.assetGroup, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="flow.taskName"
        show-overflow-tooltip
        sortable="custom"
        label="当前环节">
      </el-table-column>
    <el-table-column
        prop="approvedDate"
        show-overflow-tooltip
        sortable="custom"
        label="状态日期">
      </el-table-column>
    <el-table-column
        prop="requestRiority"
        show-overflow-tooltip
        sortable="custom"
        label="申购优先级">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("request_priority", scope.row.requestRiority, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="contractCurrency"
        show-overflow-tooltip
        sortable="custom"
        label="合同币种">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("pr_currency", scope.row.contractCurrency, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="totalContractAmount"
        show-overflow-tooltip
        sortable="custom"
        label="合同总价">
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        fixed="right"
        :key="Math.random()"
        width="200"
        label="操作">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('flow:pr:oaPrNew:view')" type="text" icon="el-icon-view" size="small" @click="view(scope.row.id)">查看</el-button>
          <el-button v-if="hasPermission('flow:pr:oaPrNew:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">修改</el-button>
          <el-button v-if="hasPermission('flow:pr:oaPrNew:del')" type="text"  icon="el-icon-delete" size="small" @click="del(scope.row.id)">删除</el-button>
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
    <el-dialog
      title="详情"
      :close-on-click-modal="false"
       v-dialogDrag
       width="1100px"
      :visible.sync="visible">
      <OaPrNewForm  ref="oaPrNewForm" ></OaPrNewForm>
    </el-dialog>

  </div>
</template>

<script>
  import OaPrNewForm from './OaPrNewForm'
  export default {
    data () {
      return {
        visible:false,
        searchForm: {
        },
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
      OaPrNewForm
    },
    activated () {
      this.refreshList()
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/flow/pr/oaPrNew/list',
          method: 'get',
          params: {
            'pageNo': this.pageNo,
            'pageSize': this.pageSize,
            'orderBy': this.orderBy,
            ...this.searchForm
          }
        }).then(({data}) => {
          if (data && data.success) {
            debugger
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
        //this.$refs.oaPrNewForm.init('add', '')
      },
      // 修改
      edit (id) {
        debugger
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        let query={businessId:id}
        this.visible=true
        this.$nextTick(() => {
          this.$refs.oaPrNewForm.init(query)
        })
      },
      // 查看
      view (id) {
        debugger
        let query={businessId:id}
        this.visible=true
        this.$nextTick(() => {
          this.$refs.oaPrNewForm.init(query)
        })
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
            url: '/flow/pr/oaPrNew/delete',
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
        debugger
        if(!this.$common.isEmpty(row.detailInfo)){
          row.detailInfoList=JSON.parse(row.detailInfo)
        }
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
        this.$utils.download('/flow/pr/oaPrNew/import/template')
      },
      exportExcel () {
        let params = {
          ...this.searchForm
        }
        this.$utils.download('/flow/pr/oaPrNew/export', params)
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>
