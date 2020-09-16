<template>
  <fm-generate-form v-if="visible" :data="options" :edit="edit" class="readonly" :value="formData"
    ref="generateForm">
    <template slot="prItemDetail" slot-scope="scope">
      数量：<el-input v-model="scope.model.prItemDetail.qty" @blur.native.capture="refreshPrice" style="width: 80px"></el-input>
      单价：<el-input v-model="scope.model.prItemDetail.price" @blur.native.capture="refreshPrice" style="width: 80px"></el-input>
      总价：<el-input v-if="isNaN(scope.model.prItemDetail.qty * scope.model.prItemDetail.price)" value='0' disabled style="width: 80px"></el-input>
      <el-input v-else v-model="scope.model.prItemDetail.qty * scope.model.prItemDetail.price" disabled style="width: 80px"></el-input>
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
      refreshPrice() {
        debugger
        var self = this
        this.$refs.generateForm.getData().then(data => {
          var total = 0
          for (var i = 0; i < data.itemTable.length; i++) {
            if (data.itemTable[i].prItemDetail != null) {
              if (!(validate.isNum(data.itemTable[i].prItemDetail.price) && data.itemTable[i].prItemDetail.price >
                  0)) {
                data.itemTable[i].prItemDetail.price = 0
              }
              data.itemTable[i].prItemDetail.price = parseFloat(data.itemTable[i].prItemDetail.price)
              if (!(validate.isNum(data.itemTable[i].prItemDetail.qty) && data.itemTable[i].prItemDetail.qty > 0)) {
                data.itemTable[i].prItemDetail.qty = 0
              }
              data.itemTable[i].prItemDetail.qty = parseFloat(data.itemTable[i].prItemDetail.qty)
              total += data.itemTable[i].prItemDetail.price * data.itemTable[i].prItemDetail.qty
              data.itemTable[i].prItemDetailTotal=data.itemTable[i].prItemDetail.price * data.itemTable[i].prItemDetail.qty
            }
          }
          data.totalAmount = total
          self.$refs.generateForm.setData(data)
        }).catch(e => {
          alert(e)
        })
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
              if (!showArra.includes(field)) {
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
