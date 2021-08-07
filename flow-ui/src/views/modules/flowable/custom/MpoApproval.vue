<template>
  <fm-generate-form v-if="visible" :data="options" :edit="edit" class="readonly" :value="formData"
    ref="generateForm">
    <template slot="summaryPurchases" slot-scope="scope" >
      <el-table :data="scope.model.summaryPurchases" size="small" height="auto"
      class="table" :header-cell-style="rowClass">
        <el-table-column prop="summaryType"  show-overflow-tooltip  label=""></el-table-column>
        <el-table-column  align="center" :label="$i18nMy.t('扣存')" >
          <template>
            <el-table-column prop="deductionQty"  show-overflow-tooltip :label="$i18nMy.t('数量')"></el-table-column>
            <el-table-column prop="deductionUsd"  show-overflow-tooltip label="USD"></el-table-column>
            <el-table-column prop="deductionCny"  show-overflow-tooltip label="CNY"></el-table-column>
            <el-table-column prop="deductionHkd"  show-overflow-tooltip label="HKD"></el-table-column>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$i18nMy.t('新采购单')" >
          <template>
            <el-table-column prop="newQty"  show-overflow-tooltip :label="$i18nMy.t('数量')"></el-table-column>
            <el-table-column prop="newUsd"  show-overflow-tooltip label="USD"></el-table-column>
            <el-table-column prop="newCny"  show-overflow-tooltip label="CNY"></el-table-column>
            <el-table-column prop="newHkd"  show-overflow-tooltip label="HKD"></el-table-column>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$i18nMy.t('对比结果（成本报表）')" >
          <template>
            <el-table-column prop="numberMpoHigherCostSheet"  show-overflow-tooltip :label="$i18nMy.t('价格高于成本报表的采购单数量')"></el-table-column>
            <el-table-column prop="numberOfMpoWithoutUnitPrice"  show-overflow-tooltip :label="$i18nMy.t('缺少单价的采购单数量')"></el-table-column>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <template slot="detailsPurchases" slot-scope="scope" >
      <el-table :data="scope.model.detailsPurchases" size="small" height="auto"
      class="table" :header-cell-style="rowClass2" >
        <el-table-column prop="detailsType"  show-overflow-tooltip  label="类型"></el-table-column>
        <el-table-column prop="requestDate"  show-overflow-tooltip  label="提出日期"></el-table-column>
        <el-table-column prop="reason"  show-overflow-tooltip  label="原因"></el-table-column>
        <el-table-column prop="orderCategory"  show-overflow-tooltip  label="订单种类"></el-table-column>
        <el-table-column prop="factory"  show-overflow-tooltip  label="工厂"></el-table-column>
        <el-table-column  align="center" :label="$i18nMy.t('扣存')" >
          <template>
            <el-table-column prop="deductionQty"  show-overflow-tooltip :label="$i18nMy.t('数量')"></el-table-column>
            <el-table-column prop="deductionUsd"  show-overflow-tooltip label="USD"></el-table-column>
            <el-table-column prop="deductionCny"  show-overflow-tooltip label="CNY"></el-table-column>
            <el-table-column prop="deductionHkd"  show-overflow-tooltip label="HKD"></el-table-column>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$i18nMy.t('新采购单')" >
          <template>
            <el-table-column prop="newQty"  show-overflow-tooltip :label="$i18nMy.t('数量')"></el-table-column>
            <el-table-column prop="newUsd"  show-overflow-tooltip label="USD"></el-table-column>
            <el-table-column prop="newCny"  show-overflow-tooltip label="CNY"></el-table-column>
            <el-table-column prop="newHkd"  show-overflow-tooltip label="HKD"></el-table-column>
          </template>
        </el-table-column>
        <el-table-column align="center" :label="$i18nMy.t('对比结果（成本报表）')" >
          <template>
            <el-table-column prop="numberMpoHigherCostSheet"  show-overflow-tooltip :label="$i18nMy.t('价格高于成本报表的采购单数量')"></el-table-column>
            <el-table-column prop="numberOfMpoWithoutUnitPrice"  show-overflow-tooltip :label="$i18nMy.t('缺少单价的采购单数量')"></el-table-column>
          </template>
        </el-table-column>
      </el-table>
    </template>
  </fm-generate-form>
</template>

<script>
  import validate from '@/utils/validate'
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
    components: {},
    methods: {
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
      rowClass2({ row, rowIndex,columnIndex}) {
        if(columnIndex<=4&&rowIndex==0){
          return 'background:#F5F7FA'
        }
        else if((rowIndex==0 && columnIndex == 5)||(rowIndex==1 && columnIndex<=3)) {
          return 'background:#ffe699'
        }
        else if((rowIndex==0 && columnIndex == 6)||(rowIndex==1 && columnIndex<=7)) {
          return 'background:#c6e0b4'
        }
        else{
          return 'background:#fce4d6'
        }
      },
      // 固定代码，不可以改 start
      createForm(options, formData, showArra, disabledArra, edit) {
        this.options = options
        this.formData = formData
        this.visible = true
        this.edit = edit
        setTimeout(() => {
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
        }, 500)
      },
      getData() {
        return this.$refs.generateForm.getData()
      }
      // 固定代码，不可以改 end
    }
  }
</script>
<style >

</style>
