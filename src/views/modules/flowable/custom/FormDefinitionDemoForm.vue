<template>
  <div>
    test:
    <fm-generate-form v-if="visible" :data="options" :edit="edit" class="readonly" :value="formData"
      ref="generateForm">
      <template slot="test1" slot-scope="scope">
        宽度：<el-input v-model="scope.model.test1.width" @blur.native.capture="refreshPrice" style="width: 100px"></el-input>
        高度：<el-input v-model="scope.model.test1.height" @blur.native.capture="refreshPrice" style="width: 100px"></el-input>
        面积：<el-input v-model="scope.model.test1.width * scope.model.test1.height" style="width: 100px"></el-input>
      </template>
    </fm-generate-form>
  </div>

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
        var self = this
        this.$refs.generateForm.getData().then(data => {
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
