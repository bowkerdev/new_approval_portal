<template>
<div>
  <el-dialog
    :title="title"
    fullscreen
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form :model="inputForm" ref="inputForm" v-loading="loading" :class="method==='view'?'readonly':''" :disabled="method==='view'"
             label-width="120px">
           <fm-making-form :bindDataTable="inputForm.autoCreate !=='1'" ref="formDesign" style="height:700px" :data="options" v-if="visible"
             :uploadPath ="`${this.$http.BASE_URL}/sys/file/webupload/upload?uploadPath=/formbuilder`"
             preview :ds="inputForm.dataSource" :tableName="inputForm.tableName" tab-list generate-json clearable>
             
          </fm-making-form>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>{{$i18nMy.t('确定')}}</el-button>
    </span>
  </el-dialog>
</div>
</template>


<script>
  export default {
    data () {
      return {
        title: '',
        method: '',
        visible: false,
        loading: false,
        options: {},
        inputForm: {
          id: '',
          code: '',
          autoCreate: '1',
          dataSource: {
            id: 'master',
            name: '本地数据库',
            enName: 'master',
            dbType: ''
          },
          name: '',
          tableName: '',
          source: '',
          version: '',
          remarks: ''
        }
      }
    },
    components: {
    },
    methods: {
      init (method, id) {
        this.method = method
        this.inputForm.id = id
        if (method === 'add') {
          this.title = `新建数据表单`
        } else if (method === 'edit') {
          this.title = '修改数据表单'
        } else if (method === 'view') {
          this.title = '查看数据表单'
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          if (method === 'add') {
            // this.$refs.formDesign.setJSON({'list': [], 'config': {'labelWidth': 100, 'labelPosition': 'right', 'size': 'small', 'customClass': ''}})
          }
          // this.options = {}
          this.inputForm.name = ''
          this.inputForm.tableName = ''
          this.inputForm.source = ''
          this.inputForm.version = ''
          this.inputForm.remarks = ''
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/form/make/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.form)
              if (this.inputForm.source) {
                this.options = JSON.parse(this.inputForm.source)
                this.$refs.formDesign.setJSON(this.options)
              } else {
                // this.$refs.formDesign.setJSON({'list': [], 'config': {'labelWidth': 100, 'labelPosition': 'right', 'size': 'small', 'customClass': ''}})
              }
              this.loading = false
            })
          }
        })
      },
      handleSubmit () {

      },
      // 表单提交
      doSubmit () {
        this.inputForm.source = JSON.stringify(this.$refs.formDesign.getJSON())
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: `/form/make/saveFormSource`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              if (data && data.success) {
                this.visible = false
                this.$message.success(data.msg)
                this.$emit('refreshDataList')
              }
            })
          }
        })
      }
    }
  }
</script>