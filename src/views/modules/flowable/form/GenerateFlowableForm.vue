<template>
 <div v-loading="loading" style="min-height:50px">
        <fm-generate-form

              :data="options"
              :edit="edit"
              v-if="visible"
              class="readonly"
              :value = "formData"
              ref="generateForm"
            >
        </fm-generate-form>
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
        formData: {},
        dataBindMap: new Map(),
        options: {'list': [], 'config': {'labelWidth': 100, 'labelPosition': 'right', 'size': 'small', 'customClass': ''}}
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
    components: {
    },
    methods: {
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
              this.dataBindMap.set(genList[i].model, genList[i].type)
            }
          }
        }
      },
      createForm (id) {
        if (id) {
          this.loading = true
          this.visible = false
          this.$http({
            url: `/extension/formDefinition/queryByJsonId?jsonId=${id}`,
            method: 'get'
          }).then(({data}) => {
            if (data.success) {
              this.options = JSON.parse(data.formDefinition.formDefinitionJson.json)
              this.dataBindMap.clear()
              this.generateModel(this.options.list)
              setTimeout(() => {
                this.loading = false
                this.visible = true
                this.$nextTick(() => {
                  let disabledArra = []
                  let showArra = []
                  this.taskFormData.forEach((item) => {
                    this.formData[`${item.id}`] = item.value
                    if (item.readable === true) {
                      showArra.push(`${item.id}`)
                    }
                    if (item.writable === false) {
                      disabledArra.push(`${item.id}`)
                    }
                  })
                  let hideArra = this.$refs.generateForm.getDataBindFields().filter((field) => {
                    if (!showArra.includes(field)) {
                      return true
                    }
                  })
                  for (let key in this.formData) {
                    if (this.dataBindMap.get(key) === 'checkbox' ||
                this.dataBindMap.get(key) === 'imgupload' ||
                this.dataBindMap.get(key) === 'table' ||
                this.dataBindMap.get(key) === 'fileupload') {
                      this.formData[key] = JSON.parse(this.formData[key])
                    }
                  }
                  this.$refs.generateForm.hide(hideArra)
                  this.$refs.generateForm.disabled(disabledArra, true)
                  this.$refs.generateForm.setData(this.formData)
                })
              }, 500)
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
      submitStartFormData (vars, callback) {
        console.log('submitStartFormData() vars: ', vars)
        this.$refs.generateForm.getData().then(data => {
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
        }).catch(e => {
        })
      },
      submitTaskFormData (vars, procInsId, taskId, assign, comment, callback) {
        this.$refs.generateForm.getData().then(data => {
          data = {...vars, ...data}
          this.loading = true
          this.$http({
            url: `/flowable/form/submitTaskFormData`,
            method: 'post',
            data: {
              id: this.id,
              procInsId: procInsId,
              taskId: taskId,
              assign: assign,
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
        }).catch(e => {
        })
      }
    }
  }
</script>
