<template>
  <div class="page" style="height: calc(100% - 26px);">
      <!--      max teng 2021年2月25日 15:19:00-->
      <!-- <el-row style="background-color: #9cc9ec;padding: 15px;margin-bottom: 5px;">
        <el-col><font style="color: red;">*</font>为确保系统统一性，字典配置请遵循以下规则：字典类型只能使用英文小写字母，数字，下划线，并以英文字母开头命名；</el-col>
      </el-row> -->
      <el-form size="small" :inline="true"  class="query-form" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
         <el-form-item prop="type">
          <el-input size="small" v-model="searchForm.type" :disabled="true" :placeholder="$i18nMy.t('类型')" clearable></el-input>
        </el-form-item>
        <el-form-item prop="description">
          <el-input size="small" v-model="searchForm.description" :placeholder="$i18nMy.t('描述')" clearable></el-input>
        </el-form-item>
        <el-form-item>
          <el-button  type="primary" @click="refreshList()" size="small" icon="el-icon-search">{{$i18nMy.t('查询')}}</el-button>
          <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">{{$i18nMy.t('重置')}}</el-button>
        </el-form-item>

      </el-form>
      <div class="bg-white top" style="overflow: auto;">
        <el-table
          :data="dataList"
          v-loading="loading"
          size="small"
          @selection-change="selectionChangeHandle"
          @sort-change="sortChangeHandle"
          class="table">
        <el-table-column
          type="selection"
          header-align="center"
          align="center"
          width="50">
        </el-table-column>
        <el-table-column
          prop="type"
          sortable="custom"
          :label="$i18nMy.t('类型')">
         <template slot-scope="scope">
          <el-link  type="primary" :underline="false" v-if="hasPermission('sys:dict:edit')" @click="edit(scope.row.id)">{{scope.row.type}}</el-link>
          <el-link  type="primary" :underline="false" v-else-if="hasPermission('sys:dict:view')"  @click="view(scope.row.id)">{{scope.row.type}}</el-link>
          <span v-else>{{scope.row.type}}</span>
        </template>
        </el-table-column>
        <el-table-column
          prop="description"
          :label="$i18nMy.t('描述')">
        </el-table-column>
        <el-table-column
          fixed="right"
          header-align="center"
          align="center"
          width="450"
          :label="$i18nMy.t('操作')">
          <template slot-scope="scope">
             <el-button v-if="hasPermission('sys:dict:view')" type="text" size="small"
                      @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
            <el-divider direction="vertical"></el-divider>

            <el-button v-if="hasPermission('sys:dict:edit')" type="text" size="small" @click="showRight(scope.row)">{{$i18nMy.t('管理键值')}}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 弹窗, 新增 / 修改 -->
    <dict-type-form  ref="dictTypeForm" @refreshDataList="refreshList"></dict-type-form>
    <el-drawer
      size = "700px"
      :title="`数据字典值列表，所属类型: ${this.dictTypeTitle}`"
      :visible.sync="rightVisible"
      direction="rtl">
      <dict-value-list  :dict-type-title="dictTypeTitle" ref="dictValueList" @closeRight="closeRight"></dict-value-list>
    </el-drawer>
    </div>
</template>

<script>
  import DictTypeForm from './DictTypeForm'
  import DictValueList from './DictValueList'

export default {
    name: 'sys-dict-DictTypeList',
    data () {
      return {
        searchForm: {
          type: '',
          description: ''
        },
        dataList: [],
        pageNo: 1,
        pageSize: 99999,
        total: 0,
        orderBy: '',
        dataListSelections: [],
        dictTypeTitle: '',
        paramType: '',
        rightVisible: false,
        loading: false
      }
    },
    components: {
      DictTypeForm,
      DictValueList
    },
    activated () {
      this.paramType=this.$route.query.type;
      this.searchForm.type=this.$route.query.type;
      this.refreshList()
    },
    methods: {
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: "/database/datamodel/dataSet/getDataByName/"+this.paramType+"/json",
          method: 'get',
          params: {
            description: this.searchForm.description
          }
        }).then(({data}) => {
          if (data && data.success) {
            this.dataList = data.result
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
        this.$refs.dictTypeForm.init('add', '')
      },
      // 修改
      edit (id) {
        id = id || this.dataListSelections.map(item => {
          return item.id
        })[0]
        this.$refs.dictTypeForm.init('edit', id)
      },
      // 查看
      view (id) {
        this.$refs.dictTypeForm.init('view', id)
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
          this.$http({
            url: '/sys/dict/delete',
            method: 'delete',
            params: {'ids': ids}
          }).then(({data}) => {
            if (data && data.success) {
              this.$message.success(data.msg)
              this.refreshList()
              this.$dictUtils.refreshDictList()
            }
          })
        })
      },
      resetSearch () {
        this.$refs.searchForm.resetFields()
        this.searchForm.type=this.$route.query.type;
        this.refreshList()
      },
      showRight (row) {
        this.rightVisible = true
        this.$nextTick(() => {
          this.$refs.dictValueList.refreshList(row.id)
          this.dictTypeTitle = row.type
        })
      },
      closeRight () {
        this.rightVisible = false
      }
    }
  }
</script>
