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
            if (genList[i].options.dataBind) {//
              this.dataBindMap.set(genList[i].model,
                {type:genList[i].type,defaultType:genList[i].options.defaultType})
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
      setHtmlParam(json){
        if(json instanceof Array){
          for(var i=0;i<json.length;i++){
            if(json[i].columns !=null && json[i].columns instanceof Array ){
              this.setHtmlParam(json[i].columns)
            }
            else{
              this.setHtmlParam(json[i])
            }
          }
        }
        else{
          if(json.type == 'html'){
            var html=json.options.defaultValue
            var ssoToken = this.$cookie.get(process.env.VUE_APP_SSO_TYPE+'_token')||""

            var tmp=this.$dictUtils.getDictValue("sit_test","testToken","")
            if(tmp !=""){
              ssoToken = tmp
            }

            html=html.replaceAll("#{"+"ssoType"+"}",ssoToken)
            for (let i=0; this.taskFormData.length > i;i++) {
              html=html.replaceAll("#{"+this.taskFormData[i].id+"}",this.taskFormData[i].value)
            }
            var obj=this.taskFormData.find(function(e){return e.id==json.model})
            obj.value = html
          }
          if(json.rows !=null){
            this.setHtmlParam(json.rows)
          }
          else if(json.list !=null){
            this.setHtmlParam(json.list)
          }
          else if(json.tableColumns !=null){
            this.setHtmlParam(json.tableColumns)
          }
        }
        return json
      },
      init(){

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
              // eslint-disable-next-line no-undef
              this.options = this.DynamicFormLanguage.simpleLanguageFrom(JSON.parse(json),this.taskFormData)
              this.setHtmlParam(this.options)
              this.dataBindMap.clear()
              this.generateModel(this.options.list)
              this.$nextTick(() => {
                this.loading = false
                this.visible = true
                this.$nextTick(() => {
                  let disabledArra = []
                  let showArra = []
                  setTimeout(()=>{
                    // 国际化序号 ,临时办法
                    var tables= document.getElementsByClassName("el-table__fixed-header-wrapper")
                    for(var i=0;i<tables.length;i++){
                      var th= tables[i].getElementsByTagName("th")
                      for(var j=0;j<th.length;j++){
                        if("序号"==th[j].textContent){
                          th[j].innerHTML ='<div class="cell">'+this.$i18nMy.t('序号')+'</div>'
                        }
                      }
                    }
                  },2000)
                  this.taskFormData.forEach((item) => {
                    if (item.value !== undefined) {
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
                    let dataField = this.dataBindMap.get(key)
                    let dataFieldType = dataField.type
                    if (dataFieldType && (dataFieldType === 'checkbox' ||
                        dataFieldType === 'imgupload' ||
                        dataFieldType === 'table' ||
                        (dataFieldType === 'blank' && dataField.defaultType !='String' ) ||
                        dataFieldType === 'select' ||
                        dataFieldType === 'fileupload')) {
                      if (this.formData[key] && typeof this.formData[key] === 'string' &&
                           ( this.formData[key].indexOf("{")==0 && this.formData[key].lastIndexOf("}")==this.formData[key].length -1 )||
                           ( this.formData[key].indexOf("[")==0 && this.formData[key].lastIndexOf("]")==this.formData[key].length -1 )) {
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
        }).catch(e => {this.visible = true;this.loading = false})
      },
      submitTaskFormData(vars, procInsId, taskId, assign, comment, callback) {
        this.$refs.generateForm.getData().then(data => {
          this.submitTaskFormDataToBackend(data, vars, procInsId, taskId, assign, comment, callback)
        }).catch(e => {this.visible = true;this.loading = false})
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
