<template>
<div>
  <el-dialog
    title="创建菜单"
    :close-on-click-modal="false"
     v-dialogDrag
     width="1000px"
    :visible.sync="visible">
    <el-form :model="inputForm"  ref="inputForm" @keyup.enter.native="doSubmit()"
             label-width="120px" @submit.native.prevent>
        <el-form-item :label="{{$i18nMy.t('上级菜单')" prop="parent.id" :rules="[{required: true, message: '请选择上级菜单', trigger: 'blur'}]">
             <SelectTree 
             ref="menuParentTree"
             :props="{
                value: 'id',             // ID字段名
                label: 'name',         // 显示名称
                children: 'children'    // 子级字段名
              }"
            :data="menuList"
            :value="inputForm.parent.id"
            :clearable="true" 
            :accordion="true"
            @getValue="(value) => {inputForm.parent.id=value}"/>
        </el-form-item>
        <el-form-item :label="{{$i18nMy.t('名称')" prop="name" :rules="[{required: true, message: '名称不能为空', trigger: 'blur'}]">
            <el-input maxlength="200" v-model="inputForm.name" 
                  :placeholder="{{$i18nMy.t('菜单名称')"></el-input>
        </el-form-item>
        <el-form-item v-if="inputForm.type !== '2'" :label="{{$i18nMy.t('菜单图标')" prop="icon">
            <el-input v-model="inputForm.icon" @focus="selectIcon" clearable :readonly="true" style="width:100%" :placeholder="{{$i18nMy.t('菜单图标名称')"></el-input>
        </el-form-item>
        <data-rule-list  ref="dataRuleList" :form="form"></data-rule-list>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button type="primary" @click="doSubmit()">{{$i18nMy.t('确定')}}</el-button>
    </span>
  </el-dialog>
  <Icon ref="icon" @getValue="value => inputForm.icon = value"></Icon>
  
</div>
</template>

<script>
  import Icon from '@/components/icon'
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  import DataRuleList from './DataRuleList'
  export default {
    data () {
      return {
        visible: false,
        menuList: [],
        form: {},
        menuListTreeProps: {
          label: 'name',
          children: 'children'
        },
        inputForm: {
          parent: {
            id: ''
          },
          name: '',
          id: '',
          icon: '',
          dataRuleList: [],
          formId: ''
        }
      }
    },
    components: {
      SelectTree,
      DataRuleList,
      Icon
    },
    methods: {
      init (form) {
        this.form = form
        this.inputForm.name = form.name
        this.inputForm.formId = form.id
        this.$http({
          url: `/sys/menu/treeData`,
          method: 'get'
        }).then(({data}) => {
          this.menuList = data.treeData
        })
        this.visible = true
        this.$nextTick(() => {
          this.$refs['inputForm'].resetFields()
          this.$refs.dataRuleList.dataRuleList = []
          this.$refs.menuParentTree.clearHandle()
        })
      },
       // 图标选中
      iconActiveHandle (iconName) {
        this.inputForm.icon = iconName
      },
      selectIcon () {
        this.$refs.icon.visible = true
      },
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.inputForm.dataRuleList = this.$refs.dataRuleList.dataRuleList
            this.$http.post('/form/make/createMenu',
               this.inputForm
            ).then(({data}) => {
              if (data && data.success) {
                this.$message.success({dangerouslyUseHTMLString: true,
                  message: data.msg})
                this.visible = false
              }
            })
          }
        })
      }
    }
  }
</script>