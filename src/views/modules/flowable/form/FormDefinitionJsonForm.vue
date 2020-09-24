<template>
<div>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
         <fm-generate-form 
              :data="options" 
              ref="generateForm"
              v-if="visible"
            >
        </fm-generate-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button type="primary" @click="visible = false">{{$i18nMy.t('确定')}}</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
  export default {
    data () {
      return {
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
      init (id) {
        this.loading = true
        this.$http({
          url: `/extension/formDefinitionJson/queryById?id=${id}`,
          method: 'get'
        }).then(({data}) => {
          this.options = JSON.parse(data.formDefinitionJson.json)
          this.visible = true
          this.loading = false
        })
      }
    }
  }
</script>