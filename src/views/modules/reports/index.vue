<template>
<div>
 <el-row>
 <el-button  type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
 </el-row>
  <el-table
    class="table"
    size = "medium"
    :data="dataList">
     <el-table-column
      type="index"
      width="50">
    </el-table-column>
    <el-table-column
      prop="name"
      :label="{{$i18nMy.t('报表名')">
    </el-table-column>
    <el-table-column
      prop="updateDate"
      :label="{{$i18nMy.t('更新日期')">
      <template slot-scope="scope">
         {{moment(scope.row.updateDate).format('YYYY-MM-DD HH:mm:ss')}}
      </template>
    </el-table-column>
    <el-table-column :label="{{$i18nMy.t('操作')">
      <template slot-scope="scope">
         <el-button
          size="mini"
          type="text"
          @click="pre(scope.row)" >{{$i18nMy.t('预览')}}</el-button>
           <el-divider direction="vertical"></el-divider>
        <el-button
          size="mini"
          type="text"
          @click="design(scope.row)" >{{$i18nMy.t('设计')}}</el-button>
           <el-divider direction="vertical"></el-divider>
        <el-button
          size="mini"
          type="text"
          @click="del(scope.row)" >{{$i18nMy.t('删除')}}</el-button>
           <el-divider direction="vertical"></el-divider>
           <el-button
          size="mini"
          type="text"
          @click="download(scope.row)" >{{$i18nMy.t('导出')}}</el-button>
      </template>
    </el-table-column>
  </el-table>
</div>
</template>
<script>
export default {
  data () {
    return {
      dataList: []
    }
  },
  activated () {
    this.refreshList()
  },
  methods: {
      // 获取数据列表
    refreshList () {
      this.loading = true
      this.$http({
        url: '/reports/list',
        method: 'get'
      }).then(({data}) => {
        if (data && data.success) {
          this.dataList = data.reports
          this.loading = false
        }
      })
    },
    add () {
      this.$router.push({path: '/ureport/designer', query: {title: '新建报表', iframeUrl: `${process.env.VUE_APP_SERVER_URL}/ureport/designer`}})
    },
    pre (report) {
      this.$router.push({path: `/ureport/preview`, query: {title: '预览表表', iframeUrl: `${process.env.VUE_APP_SERVER_URL}/ureport/preview?_u=file:${report.name}`}})
    },
    design (report) {
      this.$router.push({path: `/ureport/designer`, query: {iframeUrl: `${process.env.VUE_APP_SERVER_URL}/ureport/designer?_u=file:${report.name}`, title: '编辑报表'}})
    },
  // 删除
    del (report) {
      this.$confirm(`确定删除所选项吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.loading = true
        this.$http({
          url: '/reports/delete',
          method: 'delete',
          params: {'id': `file:${report.name}`}
        }).then(({data}) => {
          this.loading = false
          if (data && data.success) {
            this.$message.success(data.msg)
            this.refreshList()
          }
        })
      })
    },
    download (report) {
      window.open(`${this.$http.BASE_URL}/ureport/word?_u=file:${report.name}`)
    }

  }
}
</script>