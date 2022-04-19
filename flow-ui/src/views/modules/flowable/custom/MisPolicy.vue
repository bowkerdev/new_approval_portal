<template>
  <fm-generate-form @on-user-change="onUserChange" v-if="visible" :data="options" :edit="edit" class="readonly" :value="formData"
    ref="generateForm">
    <template slot="misPolicyInst" slot-scope="scope" >
      <el-button type="primary" icon="el-icon-arrow-left" @click="test">测试</el-button>
      <el-table :data="scope.model.misPolicyInst" size="small" height="auto"
      class="table mpo-approval-self-table" >
        <el-table-column prop="items"  show-overflow-tooltip  label="项目"></el-table-column>
        <el-table-column prop="policy"  show-overflow-tooltip  label="政策"></el-table-column>
        <el-table-column prop="states"  show-overflow-tooltip  label="是否开通">
          <template slot-scope="scope">
            <el-radio v-model="scope.row.states" label="1">YES</el-radio>
            <el-radio v-model="scope.row.states" label="">NO</el-radio>
          </template>
        </el-table-column>
        <el-table-column prop="requirements"  show-overflow-tooltip  label="需求描述"></el-table-column>
      </el-table>
    </template>

  </fm-generate-form>
</template>

<script>
  export default {
    data() {
      return {
        // 固定代码，不可以改 start
        options: {
          list: []
        },
        visible: false,
        edit: true,
        formData: {}
        // 固定代码，不可以改 end
      }
    },
    created() {},
    components: {

    },
    methods: {
      test(e){
        console.log(e)
        debugger
      },
      onUserChange(e){
        console.log(e)
        debugger
      },
      rowClass({ row, rowIndex,columnIndex}) {

        if(columnIndex==0&&rowIndex==0){
          return 'background:#F5F7FA'
        }
        else if((rowIndex==0 && columnIndex==1)||(rowIndex==1 && columnIndex<=3)) {
          return 'background:#ffe699'
        }
        else if((rowIndex==0 && columnIndex==2)||(rowIndex==1 && columnIndex<=7)) {
          return 'background:#c6e0b4'
        }
        else{
          return 'background:#fce4d6'
        }
      },
      // 固定代码，不可以改 start
      createForm(options, formData, showArra, disabledArra, edit) {
        debugger
        this.options = options
        this.formData = formData
        this.visible = true
        this.edit = edit
        this.$nextTick(() => {
          let hideArra = this.$refs.generateForm.getDataBindFields().filter((field) => {
            if (showArra!=null&&!showArra.includes(field)) {
              return true
            }
          })
          this.$refs.generateForm.hide(hideArra)
          this.$refs.generateForm.disabled(disabledArra, true)
          if (JSON.stringify(this.formData) != '{}') {
            this.$refs.generateForm.setData(this.formData)
          }
        })
      },
      getData() {
        return this.$refs.generateForm.getData()
      }
      // 固定代码，不可以改 end
    }
  }
</script>

<style lang="scss">
.mpo-approval-self-table {
  &.el-table td,
  &.el-table th.is-leaf,
  &.el-table--border td,
  &.el-table--border th,
  &.el-table--border,
  .el-table--group,
  & .el-table__body-wrapper .el-table--border.is-scrolling-left~.el-table__fixed {
    border-color: rgb(153, 153, 153);
  }

  & th{
    color: #606266;
  }

  &.el-table--border {
    border-right-style: solid;
    border-right-width: 1px;
    border-bottom-style: solid;
    border-bottom-width: 1px;
  }

  &.el-table th .cell {
    white-space: initial;
    word-break: break-word;
  }
}

</style>
