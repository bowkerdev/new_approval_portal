<template>
<div>
  <el-dialog :title="title" :close-on-click-modal="false"  v-dialogDrag :visible.sync="visible">
    <component v-if="visible&&isCustom == true" ref="generateForm" :is="form"></component>
    <fm-generate-form v-if="visible&&isCustom == false" :data="options"  ref="generateForm"></fm-generate-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button size="small" type="primary" @click="visible = false">{{$i18nMy.t('确定')}}</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
  const _import = require('@/router/import-' + process.env.NODE_ENV)
  export default {
    data () {
      return {
        form: null,
        isCustom: false,
        title: '预览表单',
        method: '',
        visible: false,
        loading: false,
        options: {list: []}
      }
    },
    components: {
    },
    methods: {
      initGenerateForm(){
        if(this.$refs.generateForm==null||this.$refs.generateForm.createForm ==null){
          setTimeout(this.initGenerateForm,1000)
        }
        else{
          this.$refs.generateForm.createForm(this.options, {}, null, [], true)
        }
      },
      init (id) {
        this.loading = true
        this.$http({
          url: `/extension/formDefinitionJson/queryById?id=${id}`,
          method: 'get'
        }).then(({data}) => {
          this.options = JSON.parse(data.formDefinitionJson.json)
          var json = data.formDefinitionJson.json
          if (json.indexOf('"type":"blank"') >= 0 || json.indexOf('"type": "blank"') >= 0) {
            this.isCustom = true
            this.form = _import(`modules/flowable/custom/${data.formDefinitionJson.name}`)
            this.$nextTick(() => {
              this.initGenerateForm();
            })
          }
          this.visible = true
          this.loading = false
        })
      }
    }
  }
</script>