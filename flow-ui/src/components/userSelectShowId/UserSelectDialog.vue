<template>
  <div>
    <el-dialog
    :title="title"
    width="1440px"
    :close-on-click-modal="false"
    :append-to-body="true"
     v-dialogDrag
     class="userDialog"
    :visible.sync="visible">
    <el-container style="height: 500px">
    <el-container>
      <el-header style="text-align: left; font-size: 12px;height:30px">
        <el-form size="small" :inline="true" ref="searchForm" :model="searchForm" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <el-form-item prop="loginName" style="padding-right: 5px;">
              <el-input size="small" v-model="searchForm.loginName" :placeholder="$i18nMy.t('登录名')" clearable></el-input>
            </el-form-item>
            <el-form-item prop="name" style="padding-right: 5px;">
              <el-input size="small" v-model="searchForm.name" :placeholder="$i18nMy.t('姓名')" clearable></el-input>
            </el-form-item>
            <el-form-item prop="companyEmail" style="padding-right: 5px;">
              <el-input size="small" v-model="searchForm.companyEmail" :placeholder="$i18nMy.t('邮箱')" clearable></el-input>
            </el-form-item>
            <el-form-item style="margin-left: 15px;">
              <el-button type="primary" @click="refreshList()" size="small">查询</el-button>
              <el-button @click="resetSearch()" size="small">重置</el-button>
            </el-form-item>
          </el-form>
      </el-header>

      <el-main>
        <el-table
          :data="dataList"
          v-loading="loading"
          size="small"
          ref="userTable"
          @selection-change="selectionChangeHandle"
          @sort-change="sortChangeHandle"
          height="calc(100% - 40px)"
          style="width: 100%;">
          <el-table-column
            header-align="center"
            align="center"
            v-if="limit <= 1"
            width="30">
              <template slot-scope="scope">
                  <el-radio :label="scope.row.id" :value="dataListAllSelections[0]&&dataListAllSelections[0].id" @change.native="getTemplateRow(scope.$index,scope.row)"><span></span></el-radio>
              </template>
          </el-table-column>
          <el-table-column
            type="selection"
            header-align="center"
            v-if="limit > 1"
            align="center"
            width="50">
          </el-table-column>
          <el-table-column
            prop="loginName"
            header-align="center"
            align="center"
            sortable="custom"
            min-width="90"
            label="登录名">
          </el-table-column>
          <el-table-column
            prop="name"
            header-align="center"
            align="center"
            sortable="custom"
            min-width="90"
            label="姓名">
          </el-table-column>
          <el-table-column
            prop="companyName"
            header-align="center"
            align="center"
            sortable="custom"
            min-width="110"
            label="所属机构">
          </el-table-column>
          <el-table-column
            prop="officeName"
            header-align="center"
            align="center"
            sortable="custom"
            min-width="110"
            label="所属部门">
          </el-table-column>
          <el-table-column
            prop="companyEmail"
            header-align="center"
            align="center"
            sortable="custom"
            min-width="110"
            label="邮箱">
          </el-table-column>
        </el-table>
        <el-pagination
          @size-change="sizeChangeHandle"
          @current-change="currentChangeHandle"
          :current-page="pageNo"
          :page-sizes="[5, 10, 50, 100]"
          :page-size="pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper">
        </el-pagination>
      </el-main>
    </el-container>

    <el-aside width="300px">
      <el-tag
        :key="tag.id"
        v-for="tag in dataListAllSelections"
        closable
        :disable-transitions="false"
        @close="del(tag)">
        {{tag.id}}
      </el-tag>
  </el-aside>
</el-container>
     <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button size="small" type="primary" @click="doSubmit()">{{$i18nMy.t('确定')}}</el-button>
    </span>
    </el-dialog>

  </div>
</template>

<script>
  export default {
    data () {
      return {
        searchForm: {
          loginName: '',
          company: {
            id: ''
          },
          office: {
            id: ''
          },
          name: '',
          companyEmail: ''
        },
        dataListAllSelections: [],   // 所有选中的数据包含跨页数据
        dataListSelections: [],
        idKey: 'id', // 标识列表数据中每一行的唯一键的名称(需要按自己的数据改一下)
        dataList: [],
        pageNo: 1,
        pageSize: 10,
        total: 0,
        orderBy: '',
        loading: false,
        visible: false
      }
    },
    props: {
      selectData: {
        type: Array,
        default: () => { return [] }
      },
      title: {
        type: String,
        default: () => { return '用户选择' }
      },
      limit: {
        type: Number,
        default: 999999
      },
      url :{
        type: String,
        default: () => { return '/sys/user/list' }
      }
    },
    watch: {

    },
    methods: {
      init () {
        this.visible = true
        this.$nextTick(() => {
          this.dataListAllSelections = JSON.parse(JSON.stringify(this.selectData))
          this.resetSearch()
        })
      },
      getTemplateRow (index, row) {                                 // 获取选中数据
        this.dataListSelections = [row]
        this.$nextTick(() => {
          this.changePageCoreRecordData()
        })
      },
      // 设置选中默认值的方法
      setSelectRow () {
        if (!this.dataListAllSelections || this.dataListAllSelections.length <= 0) {
          this.$refs.userTable.clearSelection()
          return
        }
        // 标识当前行的唯一键的名称
        let idKey = this.idKey
        let selectAllIds = []
        this.dataListAllSelections.forEach(row => {
          selectAllIds.push(row[idKey])
        })
        this.$refs.userTable.clearSelection()
        for (var i = 0; i < this.dataList.length; i++) {
          if (selectAllIds.indexOf(this.dataList[i][idKey]) >= 0) {
             // 设置选中，记住table组件需要使用ref="table"
            this.$refs.userTable.toggleRowSelection(this.dataList[i], true)
          }
        }
      },
      // 记忆选择核心方法
      changePageCoreRecordData () {
                // 标识当前行的唯一键的名称
        let idKey = this.idKey
        let that = this
              // 如果总记忆中还没有选择的数据，那么就直接取当前页选中的数据，不需要后面一系列计算
        if (this.dataListAllSelections.length <= 0) {
          this.dataListSelections.forEach(row => {
            that.dataListAllSelections.push(row)
          })
          return
        }
        // 总选择里面的key集合
        let selectAllIds = []
        this.dataListAllSelections.forEach(row => {
          selectAllIds.push(row[idKey])
        })
        let selectIds = []
                // 获取当前页选中的id
        this.dataListSelections.forEach(row => {
          selectIds.push(row[idKey])
                  // 如果总选择里面不包含当前页选中的数据，那么就加入到总选择集合里
          if (selectAllIds.indexOf(row[idKey]) < 0) {
            that.dataListAllSelections.push(row)
          }
        })
        let noSelectIds = []
              // 得到当前页没有选中的id
        this.dataList.forEach(row => {
          if (selectIds.indexOf(row[idKey]) < 0) {
            noSelectIds.push(row[idKey])
          }
        })
        noSelectIds.forEach(id => {
          if (selectAllIds.indexOf(id) >= 0) {
            for (let i = 0; i < that.dataListAllSelections.length; i++) {
              if (that.dataListAllSelections[i][idKey] === id) {
                                // 如果总选择中有未被选中的，那么就删除这条
                that.dataListAllSelections.splice(i, 1)
                break
              }
            }
          }
        })
      },
      del (tag) {
        this.dataListAllSelections.splice(this.dataListAllSelections.indexOf(tag), 1)
        this.$nextTick(() => {
          this.setSelectRow()
        })
      },
      // 获取数据列表
      refreshList () {
        this.loading = true
        this.$http({
          url: this.url,
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
          this.$nextTick(() => {
            this.setSelectRow()
          })
        })
      },
      // 每页数
      sizeChangeHandle (val) {
        this.pageSize = val
        this.pageNo = 1
        this.refreshList()
        this.$nextTick(() => {
          this.changePageCoreRecordData()
        })
      },
      // 当前页
      currentChangeHandle (val) {
        this.pageNo = val
        this.refreshList()
        this.$nextTick(() => {
          this.changePageCoreRecordData()
        })
      },
      // 多选
      selectionChangeHandle (val) {
        debugger
        if(this.limit > 1){
          this.dataListSelections = val
          this.$nextTick(() => {
            this.changePageCoreRecordData()
          })
        }
      },
       // 排序
      sortChangeHandle (obj) {
        if (obj.prop === 'office.name') {
          obj.prop = 'o.name'
        }
        if (obj.prop === 'company.name') {
          obj.prop = 'c.name'
        }
        if (obj.order === 'ascending') {
          this.orderBy = obj.prop + ' asc'
        } else if (obj.order === 'descending') {
          this.orderBy = obj.prop + ' desc'
        } else {
          this.orderBy = ''
        }
        this.refreshList()
      },
      resetSearch () {
        this.searchForm.company.id = ''
        this.searchForm.office.id = ''
        this.$refs.searchForm.resetFields()
        this.refreshList()
      },
      doSubmit () {
        if (this.limit < this.dataListAllSelections.length) {
          this.$message.error(`你最多只能选择${this.limit}个用户`)
          return
        }
        this.visible = false
        this.$emit('doSubmit', this.dataListAllSelections)
      }
    }
  }
</script>
<style lang="scss">
.org {
  height: 100%;
  .el-card__header {
    padding: 10px;
  }
  .el-card__body {
    padding: 10px;
    max-height: 520px;
    overflow: auto;
  }
}
.userDialog{
  .el-dialog__body {
    padding: 10px 0px 0px 10px;
    color: #606266;
    font-size: 14px;
    word-break: break-all;
  }
  .el-main {
    padding: 20px 20px 5px 20px;
    .el-pagination{
      margin-top: 5px;
    }
  }
}
</style>
