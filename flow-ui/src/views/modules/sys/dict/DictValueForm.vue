<template>
  <el-dialog
    :title="!inputForm.id ? $i18nMy.t('新增') : $i18nMy.t('修改')"
    :close-on-click-modal="false"
    append-to-body
     v-dialogDrag
    :visible.sync="visible">
    <el-form size="small" :model="inputForm" :rules="dataRule" v-loading="loading" ref="inputForm" @keyup.enter.native="doSubmit()"
             label-width="180px" @submit.native.prevent>
      <el-form-item :label="$i18nMy.t('标签')" prop="label">
        <el-input v-model="inputForm.label" :placeholder="$i18nMy.t('标签')"></el-input>
      </el-form-item>
      <el-form-item :label="$i18nMy.t('键值')" prop="value">
        <el-input type="textarea" v-model="inputForm.value" :placeholder="$i18nMy.t('键值')"    ></el-input>
      </el-form-item>
      <el-form-item :label="$i18nMy.t('排序号')" prop="sort">
        <el-input-number  :step="1" v-model="inputForm.sort" :placeholder="$i18nMy.t('排序号')"></el-input-number>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button size="small" type="primary" @click="doSubmit()" v-noMoreClick>{{$i18nMy.t('确定')}}</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        loading: false,
        inputForm: {
          dictValueId: '',
          dictType: {
            id: ''
          },
          label: '',
          value: '',
          sort: 1
        },
        dataRule: {
          label: [
            {required: true, message: $i18nMy.t('标签不能为空'), trigger: 'blur'}
          ],
          value: [
            {required: true, message: $i18nMy.t('键值不能为空'), trigger: 'blur'}
          ]
        }
      }
    },
    methods: {
      init (method, obj) {
        this.inputForm.dictValueId = obj.dictValueId
        this.inputForm.dictType.id = obj.dictTypeId
        this.visible = true
        this.$nextTick(() => {
          this.$refs['inputForm'].resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.$http({
              url: `/sys/dict/queryDictValue?dictValueId=${this.inputForm.dictValueId}`,
              method: 'get',
              loading: false
            }).then(({data}) => {
              if (data && data.success) {
                this.inputForm = this.recover(this.inputForm, data.dictValue)
                this.inputForm.dictValueId = obj.dictValueId
              }
            })
          }
        })
      },
      // 表单提交
      doSubmit () {
        if (!this.groupId) {
          this.groupWrong = '请选择分组'
        }
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: `/sys/dict/saveDictValue`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              this.loading = false
              if (data && data.success) {
                this.$message.success(data.msg)
                this.visible = false
                this.$emit('refreshDataList')
                this.$dictUtils.refreshDictList()
              }
            })
          }
        })
      }
    }
  }
</script>
