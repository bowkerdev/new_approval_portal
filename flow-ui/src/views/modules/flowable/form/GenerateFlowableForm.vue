<template>
  <div v-loading="loading" style="min-height:50px">
    <component v-if="visible&&isCustom == true" ref="generateForm" :is="form"></component>
    <fm-generate-form v-if="visible&&isCustom == false" :data="options" :edit="edit" class="readonly" :value="formData"
      ref="generateForm">
    </fm-generate-form>
  </div>
</template>

<script>
  const _import = require('@/router/import-' + process.env.NODE_ENV)
  export default {
    data() {
      return {
        form: null,
        isCustom: false,
        title: '',
        method: '',
        visible: false,
        loading: false,
        formData: {},
        dataBindMap: new Map(),
        options: {
          'list': [],
          'config': {
            'labelWidth': 100,
            'labelPosition': 'right',
            'size': 'small',
            'customClass': ''
          }
        }
      }
    },
    props: {
      processDefinitionId: {
        type: String,
        required: true
      },
      taskFormData: {
        type: Array,
        required: false,
        default: []
      },
      edit: {
        type: Boolean,
        default: true
      }
    },
    components: {},
    methods: {
      generateModel(genList) {
        for (let i = 0; i < genList.length; i++) {
          if (genList[i].type === 'grid') {
            genList[i].columns.forEach(item => {
              this.generateModel(item.list)
            })
          } else if (genList[i].type === 'tabs') {
            genList[i].tabs.forEach(item => {
              this.generateModel(item.list)
            })
          } else if (genList[i].type === 'report') {
            genList[i].rows.forEach(row => {
              row.columns.forEach(column => {
                this.generateModel(column.list)
              })
            })
          } else {
            // 处理老版本没有dataBind值的情况，默认绑定数据
            if (genList[i].options.dataBind) {
              this.dataBindMap.set(genList[i].model, genList[i].type)
            }
          }
        }
      },
      createCustomForm(showArra,disabledArra){
        if(this.$refs.generateForm==null||this.$refs.generateForm.createForm ==null){
          setTimeout(() => { this.createCustomForm(showArra,disabledArra)},1000)
        }
        else{
          this.$refs.generateForm.createForm(this.options, this.formData, showArra, disabledArra, this.edit)
        }
      },
      createForm (id) {
        if (id) {
          this.loading = true
          this.visible = false
          this.formData = {}
          this.$http({
            url: `/extension/formDefinition/queryByJsonId?jsonId=${id}`,
            method: 'get'
          }).then(({data}) => {
            if (data.success) {
              var json = data.formDefinition.formDefinitionJson.json
              if (json.indexOf('"type":"blank"') >= 0 || json.indexOf('"type": "blank"') >= 0) {
                this.isCustom = true
                this.form = _import(`modules/flowable/custom/${data.formDefinition.name}`)
              }
              debugger
              // eslint-disable-next-line no-undef
              this.options = this.DynamicFormLanguage.simpleLanguageFrom(JSON.parse(json))
              this.dataBindMap.clear()
              this.generateModel(this.options.list)
              this.$nextTick(() => {
                this.loading = false
                this.visible = true
                this.$nextTick(() => {
                  let disabledArra = []
                  let showArra = []
                  this.taskFormData.forEach((item) => {
                    if (item.value != null) {
                      this.formData[`${item.id}`] = item.value
                    }
                    if (item.readable === true) {
                      showArra.push(`${item.id}`)
                    }
                    if (item.writable === false) {
                      disabledArra.push(`${item.id}`)
                    }
                  })
                  for (let key in this.formData) {
                    let dataFieldType = this.dataBindMap.get(key)
                    console.log(dataFieldType)
                    if (dataFieldType && (dataFieldType === 'checkbox' ||
                        dataFieldType === 'imgupload' ||
                        dataFieldType === 'table' ||
                        dataFieldType === 'blank' ||
                        (dataFieldType === 'select' && dataField.options.multiple) ||
                        dataFieldType === 'fileupload')) {
                      if (this.formData[key] && typeof this.formData[key] === 'string') {
                        this.formData[key] = JSON.parse(this.formData[key])
                      } else if (!this.formData[key]) {
                        this.formData[key] = []
                      }
                    }
                    if (dataFieldType && (dataFieldType === 'number' ||
                          (dataFieldType &&
                           dataFieldType.dataType === 'number'))) {
                      if (this.formData[key] !== undefined && this.formData[key] !== '' && typeof this.formData[key] === 'string') {
                        this.formData[key] = JSON.parse(this.formData[key])
                      }
                    }
                  }
                  if (!this.isCustom) {
                    let hideArra = this.$refs.generateForm.getDataBindFields().filter((field) => {
                      if (!showArra.includes(field)) {
                        return true
                      }
                    })
                    this.$refs.generateForm.hide(hideArra)
                    this.$refs.generateForm.disabled(disabledArra, true)
                    this.$refs.generateForm.setData(this.formData)
                  } else {
                    debugger
                    this.createCustomForm(showArra,disabledArra)
                  }
                })
              })
            } else {
              this.loading = false
              this.visible = true
            }
          })
        } else {
          this.$message.info('没有关联流程表单!')
          this.visible = true
        }
      },
      submitStartFormData(vars, callback) {
        this.$refs.generateForm.getData().then(data => {
          this.submitStartFormDataToBackend(data, vars, callback)
        }).catch(e => {})
      },
      submitTaskFormData(vars, procInsId, taskId, assign, comment, callback) {
        this.$refs.generateForm.getData().then(data => {
          this.submitTaskFormDataToBackend(data, vars, procInsId, taskId, assign, comment, callback)
        }).catch(e => {})
      },
      submitStartFormDataToBackend(data, vars, callback) {
        this.loading = true
        this.$http({
          url: `/flowable/form/submitStartFormData`,
          method: 'post',
          data: {
            id: this.id,
            ...vars,
            data: JSON.stringify(data)
          }
        }).then(({data}) => {
          if (data && data.success) {
            this.visible = false
            this.loading = false
            this.$message.success(data.msg)
            callback(data)
          }
        })
      },
      submitTaskFormDataToBackend(data, vars, procInsId, taskId, assignee, comment, callback) {
        data = {...vars, ...data}
        this.loading = true
        this.$http({
          url: `/flowable/form/submitTaskFormData`,
          method: 'post',
          data: {
            id: this.id,
            procInsId: procInsId,
            taskId: taskId,
            assignee: assignee,
            comment: comment,
            data: JSON.stringify(data)
          }
        }).then(({data}) => {
          if (data && data.success) {
            this.visible = false
            this.loading = false
            this.$message.success(data.msg)
            callback(data)
          }
        })
      }
    }
  }
</script>
