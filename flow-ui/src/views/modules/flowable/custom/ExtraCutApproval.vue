<template>
<fm-generate-form v-if="visible" :data="options" :remote-option="dynamicData" :edit="edit" :value="formData"
    ref="generateForm">
    <template slot="qaRecordData" slot-scope="" >
    </template>

  </fm-generate-form>
</template>

<!-- 
1.控制QA RAP显示
2.给QA RAP赋值
 -->
<script>
import Table from '../../../../pages/datav/option/components/table.vue'
  export default {
    data() {
      return {
        qaPullDownList:[],
        dynamicData: {
          qa_reason_options : [], // 单选框组 option data
          qa_action_options : [], // 单选框组 option data
          qa_prevention_options : [], // 单选框组 option data
        },
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
    created() {
    },
    mounted(){
        let _that = this
        this.$dictUtils.getSqlDictList('GET_EXTRA_CUT_QA_PULL_DOWN', {}, function (data) {
          _that.qaPullDownList = data
          if (_that.qaPullDownList && _that.qaPullDownList.length > 0) {
            for (var i = 0; i < _that.qaPullDownList.length; i++) {
               var item = {label:_that.qaPullDownList[i].value, value:_that.qaPullDownList[i].value};
              switch(_that.qaPullDownList[i].type){
                case 'action':
                  _that.dynamicData.qa_action_options.push(item);
                  break
                case 'reason':
                   _that.dynamicData.qa_reason_options.push(item);
                  break
                case 'prevention':
                   _that.dynamicData.qa_prevention_options.push(item);
                  break
              }
            }
          }
        })
    },
    components: {Table},
    methods: {
      test(info) {
        console.log(info)
      },
      // 20220628Ryder为了业务加了一个特殊判断 固定代码，不可以改 start
      createForm(options, formData, showArra, disabledArra, edit) {
        // debugger
        this.options = options
        formData.applicantDate = this.$common.parseTime(formData.applicantDate,'{y}-{m}-{d}')
        formData.expectedDate = this.$common.parseTime(formData.expectedDate,'{y}-{m}-{d} {h}:{i}:{s}')
        // formData.applicantDate = new Date(formData.applicantDate).pattern("yyyy-MM-dd");
        // formData.expectedDate = new Date(formData.expectedDate).pattern("yyyy-MM-dd HH:mm:ss");
        this.formData = formData
        this.visible = true
        this.edit = edit
        this.$nextTick(() => {
          let hideArra = this.$refs.generateForm.getDataBindFields().filter((field) => {
            if (showArra!=null&&!showArra.includes(field)) {
              return true
            }
          })
          //加一个特殊业务判断
          if(formData.applySiteCode != 'BCA'){
            hideArra.push('qaRecordDataLabel');
            hideArra.push('qaRapList');
          }
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
.extra-cut-approval-self-table {
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
