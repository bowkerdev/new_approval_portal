<template>
  <div class="page" style="height: calc(100% - 40px);">
      <el-form size="small" :inline="true" class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
         <el-form-item prop="applicationNo">
                <el-input size="small" v-model="searchForm.applicationNo" :placeholder="$i18nMy.t('申请单号')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="projectName">
                <el-input size="small" v-model="searchForm.projectName" :placeholder="$i18nMy.t('项目名称')" clearable></el-input>
         </el-form-item>
         <el-form-item prop="applySiteCode">
                  <el-select size="small" v-model="searchForm.applySiteCode" :placeholder="$i18nMy.t('采购地区')" @change="siteChange" style="width: 100%;">
                    <el-option
                      v-for="item in $dictUtils.getDictList('apply_site_code')"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
         </el-form-item>
         <el-form-item prop="requesterDepartment.id">
            <SelectTree
                  ref="requesterDepartment.id"
                  v-if="ifSiteChange"
                  :props="{
                      value: 'id',             // ID字段名
                      label: 'name',         // 显示名称
                      children: 'children'    // 子级字段名
                    }"
                  size="small"
                  :placeholder="$i18nMy.t('请求者部门')"
                  :url="`/sys/office/treeData?type=2&parentCode=${searchForm.applySiteCode}`"
                  :value="searchForm.requesterDepartment.id"
                  :clearable="true"
                  :accordion="true"
                  @getValue="(value) => {searchForm.requesterDepartment.id=value}"/>
         </el-form-item>
         <el-form-item prop="expenseType">
                  <el-select size="small" v-model="searchForm.expenseType" :placeholder="$i18nMy.t('费用类型')"  style="width: 100%;">
                    <el-option
                      v-for="item in $dictUtils.getDictList('expense_type')"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
         </el-form-item>
         <el-form-item prop="requestRiority">
                  <el-select size="small" v-model="searchForm.requestRiority" :placeholder="$i18nMy.t('申购优先级')"  style="width: 100%;">
                    <el-option
                      v-for="item in $dictUtils.getDictList('request_priority')"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value">
                    </el-option>
                  </el-select>
         </el-form-item>
         <el-form-item prop="createDate">
               <el-date-picker
                    v-model="searchForm.createDate"
                    type="daterange"
                    size="small"
                    align="right"
                    value-format="yyyy-MM-dd hh:mm:ss"
                    unlink-panels
                    range-separator="~"
                    :start-placeholder="$i18nMy.t('开始申请时间')"
                    :end-placeholder="$i18nMy.t('结束申请时间')">
                 </el-date-picker>
         </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
      </el-form>
        <!-- 导入导出-->
        <el-dialog  title="导入Excel" :visible.sync="isImportCollapse">
          <el-form :inline="true" v-show="isImportCollapse"  class="query-form" ref="importForm">
             <el-form-item>
              <el-button type="default" @click="downloadTpl()" size="small">{{$i18nMy.t('下载模板')}}</el-button>
             </el-form-item>
             <el-form-item prop="loginName">
                <el-upload
                  class="upload-demo"
                  :action="`${this.$http.BASE_URL}/flow/pr/oaPrNew/import`"
                  :on-success="uploadSuccess"
                   :show-file-list="true">
                  <el-button size="small" type="primary">{{$i18nMy.t('点击上传')}}</el-button>
                  <div slot="tip" class="el-upload__tip">只允许导入“xls”或“xlsx”格式文件！</div>
                </el-upload>
            </el-form-item>
          </el-form>
      </el-dialog>
      <div class="bg-white top">
      <el-row v-show="false">
        <el-button v-if="hasPermission('flow:pr:oaPrNew:add')" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
        <el-button v-if="hasPermission('flow:pr:oaPrNew:edit')" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
         :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
        <el-button v-if="hasPermission('flow:pr:oaPrNew:del')" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                  :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}
        </el-button>
        <el-button-group class="pull-right">
            <!-- <el-button v-if="hasPermission('flow:pr:oaPrNew:import')" type="default" size="small" icon="el-icon-upload2" title="导入" @click="isImportCollapse = !isImportCollapse, isSearchCollapse=false"></el-button>
            <el-button v-if="hasPermission('flow:pr:oaPrNew:export')" type="default" size="small" icon="el-icon-download" title="导出" @click="exportExcel()"></el-button> -->
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
      height="calc(100% - 120px)"
      @expand-change="detail"
      class="table">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column type="expand" >
      <template slot-scope="scope" >
        <el-table
            border
            size="small"
            :data="scope.row.detailInfoList"
            style="margin-top:-21px;" >
          <el-table-column  prop="item" show-overflow-tooltip :label="$i18nMy.t('物品')">
          </el-table-column>
          <el-table-column  prop="brandName" show-overflow-tooltip :label="$i18nMy.t('品牌')">
          </el-table-column>
          <el-table-column  prop="modelNo" show-overflow-tooltip :label="$i18nMy.t('型号')">
          </el-table-column>
          <el-table-column  prop="quantity" align="right" show-overflow-tooltip :label="$i18nMy.t('数量')">
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
        width="200"
        :label="$i18nMy.t('申请单号')">
            <template slot-scope="scope">
              <el-link v-if="!$common.isEmpty(scope.row.procInsId)"  type="primary" :underline="false" @click="flowDetail(scope.row)">{{scope.row.applicationNo}}</el-link>
              <el-link v-if="$common.isEmpty(scope.row.procInsId)"  type="primary" :underline="false" @click="start(scope.row)">{{scope.row.applicationNo}}</el-link>
            </template>
      </el-table-column>
    <el-table-column
        prop="projectName"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('项目名称')">
      </el-table-column>
    <el-table-column
        prop="applySiteCode"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('采购地区')">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("apply_site_code", scope.row.applySiteCode, '-') }}
        </template>
      </el-table-column>
      <el-table-column
        prop="requesterDepartment.name"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('请求者部门')">
      </el-table-column>
    <el-table-column
        prop="expenseType"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('费用类型')">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("expense_type", scope.row.expenseType, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="costCenter"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('成本中心')">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("cost_center", scope.row.costCenter, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="assetGroup"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('固定资产类型')">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("asset_group", scope.row.assetGroup, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="flow.taskName"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('当前环节')">
      </el-table-column>
    <el-table-column
        prop="createDate"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('申请时间')">
      </el-table-column>
    <el-table-column
        prop="requestRiority"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('申购优先级')">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("request_priority", scope.row.requestRiority, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="contractCurrency"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('合同币种')">
        <template slot-scope="scope">
              {{ $dictUtils.getDictLabel("pr_currency", scope.row.contractCurrency, '-') }}
        </template>
      </el-table-column>
    <el-table-column
        prop="totalContractAmount"
        show-overflow-tooltip
        sortable="custom"
        :label="$i18nMy.t('合同总价')">
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        fixed="right"
        :key="Math.random()"
        width="200"
        :label="$i18nMy.t('操作')">
        <template  slot-scope="scope">
          <el-button v-if="hasPermission('flow:pr:oaPrNew:view')" type="text" icon="el-icon-view" size="small" @click="flowDetail(scope.row)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="hasPermission('flow:pr:oaPrNew:edit')" type="text" icon="el-icon-edit" size="small" @click="copyToStart(scope.row)">{{$i18nMy.t('复制申请单')}}</el-button>
          <!-- <el-button v-if="hasPermission('flow:pr:oaPrNew:edit')" type="text" icon="el-icon-edit" size="small" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="hasPermission('flow:pr:oaPrNew:del')" type="text"  icon="el-icon-delete" size="small" @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button> -->
        </template>
      </el-table-column> -->
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
  import pick from 'lodash.pick'
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  export default {
    data () {
      return {
        ifSiteChange: true,
        visible:false,
        searchForm: {
          isDraft: '',
          createDate: '',
          applicationNo: '',
          projectName: '',
          applySiteCode: '',
          requesterDepartment: {
            id: ''
          },
          expenseType: '',
          requestRiority: ''
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
      SelectTree,
      OaPrNewForm
    },
    activated () {
      var _that=this;

      _that.searchForm.isDraft = (_that.$route.query.isDraft || '')
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
          url: '/flow/pr/oaPrNew/list',
          method: 'get',
          params: {
            'pageNo': this.pageNo,
            'pageSize': this.pageSize,
            'orderBy': this.orderBy,
            beginCreateDate: this.searchForm.createDate[0],
            endCreateDate: this.searchForm.createDate[1],
            ...this.lodash.omit(this.searchForm, 'createDate')
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

      start (param) {
        let row = {id: param.flow.procDefId, name: "PRPO", key: "prpo"}
        // 读取流程表单
        let tabTitle = $i18nMy.t('发起流程') + '：' + $i18nMy.t(`${row.name}`)
        let processTitle = `Start Process : ${row.name}  ${this.moment(new Date()).format('YYYY-MM-DD HH:mm')} `

        this.$http.get('/flowable/task/getTaskDef', {params: {
          procDefId: row.id,
          status: 'start'
        }}).then(({data}) => {
          if (data.success) {
            this.$router.push({
              path: '/flowable/task/TaskForm',
              query: {procDefId: row.id, procDefKey: row.key, status: 'start', title: tabTitle, formType: data.flow.formType, formUrl: data.flow.formUrl, formTitle: processTitle, businessId: param.id}
            })
          }
        })
      },
      copyToStart (param) {
        let row = {id: param.flow.procDefId, name: "PRPO", key: "prpo"}
        // 读取流程表单
        let tabTitle = $i18nMy.t('发起流程') + '：' + $i18nMy.t(`${row.name}`)
        let processTitle = `Start Process : ${row.name}  ${this.moment(new Date()).format('YYYY-MM-DD HH:mm')} `

        this.$http.get('/flowable/task/getTaskDef', {params: {
          procDefId: row.id,
          status: 'start'
        }}).then(({data}) => {
          if (data.success) {
            this.$router.push({
              path: '/flowable/task/TaskForm',
              query: {procDefId: row.id, procDefKey: row.key, status: 'start', title: tabTitle, formType: data.flow.formType, formUrl: data.flow.formUrl, formTitle: processTitle, businessId: param.id+"__copy"}
            })
          }
        })
      },
      // 新增
      add () {
        //this.$refs.oaPrNewForm.init('add', '')
      },
      // 修改
      edit (id) {
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
        let query={businessId:id}
        this.visible=true
        this.$nextTick(() => {
          this.$refs.oaPrNewForm.init(query)
        })
      },
      flowDetail (row) {
        this.$http.get('/flowable/task/getTaskDef', {params: {
          procInsId: row.procInsId,
          procDefId: row.flow.procDefId,
        }}).then(({data}) => {
          data.flow.taskDefKey ='ITChecker' // 指定这个流程 保证有导出按钮
          if (data.success) {
            this.$router.push({
              path: '/flowable/task/TaskFormDetail',
              query: {readOnly: true, title: row.applicationNo, formTitle: row.applicationNo, ...pick(data.flow, 'formType', 'formUrl', 'procDefKey', 'taskDefKey', 'procInsId', 'procDefId', 'taskId', 'status', 'title', 'businessId')}
            })
          }
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
          beginCreateDate: this.searchForm.createDate[0],
          endCreateDate: this.searchForm.createDate[1],
          ...this.lodash.omit(this.searchForm, 'createDate')
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
