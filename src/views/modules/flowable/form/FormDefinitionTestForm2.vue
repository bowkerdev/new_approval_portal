<template>
  <div>
    <el-dialog :title="title" :close-on-click-modal="false" v-dialogDrag :visible.sync="visible">
      <fm-generate-form :data="options" :remote="remoteFuncs" :value="editData" :remote-option="dynamicData" ref="generateForm">
        <template slot="test2" slot-scope="scope">
          <!-- 自定义 -->
          <!-- 通过 v-model="scope.model.blank_1565316398399" 绑定数据 -->
          宽度：<el-input v-model="scope.model.test2.width" style="width: 100px"></el-input>
          高度：<el-input v-model="scope.model.test2.height" @blur.native.capture="refresh" style="width: 100px"></el-input>
          面积：<el-input v-model="scope.model.test2.width * scope.model.test2.height" style="width: 100px"></el-input>
        </template>

      </fm-generate-form>
      <el-button type="primary" @click="handleSubmit">Submit</el-button>
      <span slot="footer" class="dialog-footer">
        <el-button @click="visible = false">关闭 3</el-button>
        <el-button type="primary" @click="visible = false">确定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
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
      handleSubmit() {
        debugger
        this.$refs.generateForm.getData().then(data => {
          // Data verification succeeded
          alert(JSON.stringify(data))
        }).catch(e => {
          // Data verification failed
        })
      },
      refresh() {
        debugger
        console.log("1111");
      },
      init() {
        var id = '5bd9f28378c1462bbe1a1c59065d8b78'
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
      }
    }
  }
</script>
