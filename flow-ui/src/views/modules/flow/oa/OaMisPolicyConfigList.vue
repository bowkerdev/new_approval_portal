<template>
    <div class="page">
      <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
         <el-form-item prop="department">
                <el-input size="small" v-model="searchForm.department" placeholder="department" clearable></el-input>
         </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small" icon="el-icon-search">查询</el-button>
            <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">重置</el-button>
          </el-form-item>
      </el-form>
        <!-- 导入导出-->
      <el-dialog  title="导入Excel" :visible.sync="isImportCollapse">
          <el-form size="small" :inline="true" v-show="isImportCollapse"  ref="importForm">
             <el-form-item>
              <el-button type="default" @click="downloadTpl()" size="small">下载模板</el-button>
             </el-form-item>
             <el-form-item prop="loginName">
                <el-upload
                  class="upload-demo"
                  :action="`${this.$http.BASE_URL}/flow/oa/mispolicy/oaMisPolicyConfig/import`"
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
        <el-button v-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">新建</el-button>
        <el-button v-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>修改</el-button>
        <el-button v-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>删除
        </el-button>
        <el-button-group class="pull-right">
            <el-button v-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:import')" type="default" size="small" icon="el-icon-upload2" title="导入" @click="isImportCollapse = !isImportCollapse"></el-button>
            <el-button v-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:export')" type="default" size="small" icon="el-icon-download" title="导出" @click="exportExcel()"></el-button>
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
       height="calc(100% - 80px)"
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
        prop="department"
        show-overflow-tooltip
        sortable="custom"
        label="department">
            <template slot-scope="scope">
              <el-link  type="primary" :underline="false" v-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:edit')" @click="edit(scope.row.id)">{{scope.row.department}}</el-link>
              <el-link  type="primary" :underline="false" v-else-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:view')"  @click="view(scope.row.id)">{{scope.row.department}}</el-link>
              <span v-else>{{scope.row.department}}</span>
            </template>
      </el-table-column>
    <el-table-column
        prop="hardware"
        show-overflow-tooltip
        sortable="custom"
        label="hardware">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.hardware, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="lanConnection"
        show-overflow-tooltip
        sortable="custom"
        label="lan_connection">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.lanConnection, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="wifiOffice"
        show-overflow-tooltip
        sortable="custom"
        label="wifi_office">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.wifiOffice, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="wifiGuest"
        show-overflow-tooltip
        sortable="custom"
        label="wifi_guest">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.wifiGuest, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="wifiOthers"
        show-overflow-tooltip
        sortable="custom"
        label="wifi_others">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.wifiOthers, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="vpn"
        show-overflow-tooltip
        sortable="custom"
        label="vpn">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.vpn, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="usbDrive"
        show-overflow-tooltip
        sortable="custom"
        label="usb_drive">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.usbDrive, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="shareDrive"
        show-overflow-tooltip
        sortable="custom"
        label="share_drive">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.shareDrive, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="msTeamDrive"
        show-overflow-tooltip
        sortable="custom"
        label="ms_team_drive">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.msTeamDrive, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="threerdPartyCloudDrive"
        show-overflow-tooltip
        sortable="custom"
        label="threerd_party_cloud_drive">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.threerdPartyCloudDrive, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="internet"
        show-overflow-tooltip
        sortable="custom"
        label="internet">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.internet, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="instantMassager"
        show-overflow-tooltip
        sortable="custom"
        label="instant_massager">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.instantMassager, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="cloudTransfer"
        show-overflow-tooltip
        sortable="custom"
        label="cloud_transfer">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.cloudTransfer, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="oneDrivePersonal"
        show-overflow-tooltip
        sortable="custom"
        label="one_drive_personal">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.oneDrivePersonal, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="msTeamMessage"
        show-overflow-tooltip
        sortable="custom"
        label="ms_team_message">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.msTeamMessage, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="softwareInstallation"
        show-overflow-tooltip
        sortable="custom"
        label="software_installation">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.softwareInstallation, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="msEmailToExternalAddress"
        show-overflow-tooltip
        sortable="custom"
        label="ms_email_to_external_address">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.msEmailToExternalAddress, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="mfa"
        show-overflow-tooltip
        sortable="custom"
        label="mfa">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.mfa, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="mobileEmail"
        show-overflow-tooltip
        sortable="custom"
        label="mobile_email">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("yes_no", scope.row.mobileEmail, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="updateDate"
        show-overflow-tooltip
        sortable="custom"
        label="更新时间">
      </el-table-column>
    <el-table-column
        prop="remarks"
        show-overflow-tooltip
        sortable="custom"
        label="备注信息">
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        fixed="right"
        :key="Math.random()"
        width="200"
        label="操作">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:view')" type="text" icon="el-icon-view" size="small" @click="view(scope.row.id)">查看</el-button>
          <el-button v-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">修改</el-button>
          <el-button v-if="hasPermission('oa:mispolicy:oaMisPolicyConfig:del')" type="text"  icon="el-icon-delete" size="small" @click="del(scope.row.id)">删除</el-button>
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
    <OaMisPolicyConfigForm  ref="oaMisPolicyConfigForm" @refreshDataList="refreshList"></OaMisPolicyConfigForm>
  </div>
</template>

<script>
  import OaMisPolicyConfigForm from './OaMisPolicyConfigForm'
  export default {
    data () {
      return {
        searchForm: {
          department: ''
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
      OaMisPolicyConfigForm
    },
    activated () {
      this.refreshList()
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: '/flow/oa/mispolicy/oaMisPolicyConfig/list',
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
        this.$refs.oaMisPolicyConfigForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.oaMisPolicyConfigForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.oaMisPolicyConfigForm.init('view', id)
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
            url: '/flow/oa/mispolicy/oaMisPolicyConfig/delete',
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
        this.$utils.download('/flow/oa/mispolicy/oaMisPolicyConfig/import/template')
      },
      exportExcel () {
        let params = {
          ...this.searchForm
        }
        this.$utils.download('/flow/oa/mispolicy/oaMisPolicyConfig/export', params)
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.refreshList()
      }
    }
  }
</script>
