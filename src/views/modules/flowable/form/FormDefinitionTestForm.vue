<template>
  <div>
    <el-dialog :title="title" :close-on-click-modal="false" v-dialogDrag :visible.sync="visible">
      <fm-generate-form :data="options" :remote="remoteFuncs" :value="editData" :remote-option="dynamicData" ref="generateForm">
        <template slot="prItemDetail" slot-scope="scope">
          <!-- 自定义 -->
          <!-- 通过 v-model="scope.model.blank_1565316398399" 绑定数据 -->
          数量：<el-input v-model="scope.model.prItemDetail.qty" @blur.native.capture="refreshPrice" style="width: 80px"></el-input>
          单价：<el-input v-model="scope.model.prItemDetail.price" @blur.native.capture="refreshPrice" style="width: 80px"></el-input>
          总价：<el-input v-if="isNaN(scope.model.prItemDetail.qty * scope.model.prItemDetail.price)" value='0' disabled
            style="width: 80px"></el-input>
          <el-input v-else v-model="scope.model.prItemDetail.qty * scope.model.prItemDetail.price" disabled style="width: 80px"></el-input>
        </template>
      </fm-generate-form>
      <el-button type="primary" @click="handleSubmit">Submit</el-button>
      <span slot="footer" class="dialog-footer">
        <el-button @click="visible = false">{{$t('关闭')}}</el-button>
        <el-button type="primary" @click="visible = false">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
  import validate from '@/utils/validate'
  export default {
    data() {
      return {
        title: '预览表单',
        method: '',
        visible: false,
        loading: false,
        options: {
          list: []
        },
        editData: {},
        remoteFuncs: {},
        dynamicData: {}
      }
    },
    components: {},
    methods: {
      handleSubmit() { // 测试
        debugger
        this.$refs.generateForm.getData().then(data => {
          // Data verification succeeded
          console.log(JSON.stringify(data))
          alert(JSON.stringify(data))
        }).catch(e => {
          // Data verification failed
        })
      },
      init() {
        var id = '57dd665bcb464199a3cad4b22aff36e8'
        this.loading = true
        this.$http({
          url: `/extension/formDefinitionJson/queryById?id=${id}`,
          method: 'get'
        }).then(({
          data
        }) => {
          this.options = JSON.parse(data.formDefinitionJson.json)
          this.visible = true
          this.loading = false
        })
      },
      refreshPrice() {
        var self = this;
        this.$refs.generateForm.getData().then(data => {
          debugger
          var total = 0;
          for (var i = 0; i < data.itemTable.length; i++) {
            if (data.itemTable[i].prItemDetail != null) {
              if (!(validate.isNum(data.itemTable[i].prItemDetail.price) && data.itemTable[i].prItemDetail.price >
                  0)) {
                data.itemTable[i].prItemDetail.price = 0;
              }
              if (!(validate.isNum(data.itemTable[i].prItemDetail.qty) && data.itemTable[i].prItemDetail.qty > 0)) {
                data.itemTable[i].prItemDetail.qty = 0;
              }
              total += data.itemTable[i].prItemDetail.price * data.itemTable[i].prItemDetail.qty;
            }
          }
          data.totalAmount = total;
          self.$refs.generateForm.setData(data);
        }).catch(e => {
          alert(e);
        })
      },
      createForm(id) {
        var id = id || '57dd665bcb464199a3cad4b22aff36e8';
        this.loading = true
        this.$http({
          url: `/extension/formDefinitionJson/queryById?id=${id}`,
          method: 'get'
        }).then(({
          data
        }) => {
          this.options = JSON.parse(data.formDefinitionJson.json)
          this.visible = true
          this.loading = false
        })
      },
      saveForm() {

      }
    }
  }
</script>
