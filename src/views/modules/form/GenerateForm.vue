<template>
  <div>
    <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
        <fm-generate-form 
            v-if="visible"
              :edit="method !== 'view'"
              :data="options" 
              :value="initData" 
              :class="method==='view'?'readonly':''"
              ref="generateForm"
            >
        </fm-generate-form>
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
        beanId: '',
        visible: false,
        initData: {},
        dataBindMap: new Map(),
        options: {list: []}
      }
    },
    created () {
      // this.createForm()
    },
    activated () {
      this.createForm()
    },
    components: {
    },
    methods: {
      init (method, formId, beanId) {
        this.method = method
        this.beanId = beanId
        this.initData = {}
        if (method === 'add') {
          this.title = `新建`
        } else if (method === 'edit') {
          this.title = '修改'
        } else if (method === 'view') {
          this.title = '查看'
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.generateForm.reset()
          if (beanId) {
            this.loading = true
            this.$http({
              url: `/form/generate/queryById?formId=${formId}&id=${beanId}`,
              method: 'get'
            }).then(({data}) => {
              this.loading = false
              for (let key in data.obj) {
                let dataField = this.dataBindMap.get(key)
                if (dataField && (dataField['type'] === 'checkbox' ||
                dataField['type'] === 'imgupload' ||
                dataField['type'] === 'table' ||
                (dataField['type'] === 'select' && dataField.options.multiple) ||
                dataField['type'] === 'fileupload')) {
                  if (data.obj[key] !== undefined) {
                    data.obj[key] = JSON.parse(data.obj[key])
                  } else {
                    data.obj[key] = []
                  }
                }
              }
              this.initData = data.obj
            })
          }
        })
      },
      generateModel (genList) {
        for (let i = 0; i < genList.length; i++) {
          if (genList[i].type === 'grid') {
            genList[i].columns.forEach(item => {
              this.generateModel(item.list)
            })
          } else if (genList[i].type === 'tabs') {
            genList[i].tabs.forEach(item => {
              this.generateModel(item.list)
            })
          } else {
            // 处理老版本没有dataBind值的情况，默认绑定数据
            if (genList[i].options.dataBind) {
              this.dataBindMap.set(genList[i].model, genList[i])
            }
          }
        }
      },
      createForm () {
        this.$http({
          url: `/form/make/queryById?id=${this.$route.query.id}`,
          method: 'get'
        }).then(({data}) => {
          if (data.form.source) {
            this.options = JSON.parse(data.form.source)
          } else {
            this.options = {'list': [], 'config': {'labelWidth': 100, 'labelPosition': 'right', 'size': 'small', 'customClass': ''}}
          }
          this.dataBindMap.clear()
          this.generateModel(this.options.list)
        })
      },

      // 表单提交
      doSubmit () {
        // 自定义js ，保存前执行
        if (this.options.config.eventType === '1') {
          // eslint-disable-next-line no-new-func
          new Function(`return ${this.options.config.customJs}`)()
        }
  
        this.$refs.generateForm.getData().then(data => {
          if (this.beanId) {
            data.id = this.beanId
          }
          this.$http({
            url: `/form/generate/save`,
            method: 'post',
            data: {formId: this.$route.query.id, data: JSON.stringify(data)}
          }).then(({data}) => {
            if (data && data.success) {
              this.visible = false
              this.$message.success(data.msg)
              this.$emit('refreshDataList')
            }
            // 自定义js 保存后执行
            if (this.options.config.eventType === '2') {
          // eslint-disable-next-line no-new-func
              new Function(`return ${this.options.config.customJs}`)()
            }
          })
        }).catch(e => {
        })
      }
    }
  }
</script>