<template>
<div>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form size="small" :model="inputForm" ref="inputForm" :class="method==='view'?'readonly':''" :disabled="method==='view'" v-loading="loading" @keyup.enter.native="doSubmit()"
             label-width="120px" @submit.native.prevent>
      <el-row  :gutter="15">
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('名称')" prop="name"
                :rules="[
                  {required: true, message:'名称不能为空', trigger:'blur'}
                 ]">
			        <el-input v-model="inputForm.name" :placeholder="$i18nMy.t('请填写名称')"></el-input>
	         </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('监听器类型')" prop="listenerType"
                :rules="[
                 {required: true, message:'监听器类型不能为空', trigger:'blur'}
                 ]">
                    <el-radio-group v-model="inputForm.listenerType">
                          <el-radio v-model="listenerType" label="1">{{$i18nMy.t('执行监听器')}}</el-radio>
                          <el-radio v-model="listenerType" label="2">{{$i18nMy.t('任务监听器')}}</el-radio>
                    </el-radio-group>
	         </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('事件')" prop="event"
                :rules="[
                 {required: true, message:'事件不能为空', trigger:'blur'}
                 ]">
		            <el-select v-model="inputForm.event" :placeholder="$i18nMy.t('请选择')"  style="width: 100%;">
                          <el-option
                            v-for="item in events"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value">
                          </el-option>
                      </el-select>
	         </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('值类型')" prop="valueType"
                :rules="[
                 {required: true, message:'值类型不能为空', trigger:'blur'}
                 ]">
                    <el-radio-group v-model="inputForm.valueType">
                        <el-radio v-model="valueType" label="1">{{$i18nMy.t('类')}}</el-radio>
                        <el-radio v-model="valueType" label="2">{{$i18nMy.t('表达式')}}</el-radio>
                        <el-radio v-model="valueType" label="3">{{$i18nMy.t('委托表达式')}}</el-radio>
                    </el-radio-group>
	         </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item :label="valueLabel" prop="value"
                :rules="[
                 {required: true, message:'值不能为空', trigger:'blur'}
                 ]">
			        <el-input v-model="inputForm.value" :placeholder="$i18nMy.t('请填写值')"     ></el-input>
	         </el-form-item>
        </el-col>
        </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button size="small" type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>{{$i18nMy.t('确定')}}</el-button>
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
        listenerTab: '0',
        valueLabel: '类',
        events: [{label: 'start', value: 'start'}, {label: 'take', value: 'take'}, {label: 'end', value: 'end'}],
        inputForm: {
          id: '',
          name: '',
          listenerType: '1',
          event: '',
          valueType: '1',
          value: ''
        }
      }
    },
    watch: {
      'inputForm.listenerType' (val) {
        if (val === '1') {
          this.inputForm.event = ''
          this.events = [{label: 'start', value: 'start'}, {label: 'take', value: 'take'}, {label: 'end', value: 'end'}]
        } else {
          this.inputForm.event = ''
          this.events = [{label: 'start', value: 'start'}, {label: 'assignment', value: 'assignment'}, {label: 'complete', value: 'complete'}, {label: 'delete', value: 'delete'}]
        }
      },
      'inputForm.valueType' (val) {
        if (val === '1') {
          this.valueLabel = '类'
        } else if (val === '2') {
          this.valueLabel = '表达式'
        } else {
          this.valueLabel = '委托表达式'
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
          this.title = $i18nMy.t('新建监听器')
        } else if (method === 'edit') {
          this.title = $i18nMy.t('修改监听器')
        } else if (method === 'view') {
          this.title = $i18nMy.t('查看监听器')
        }
        this.visible = true
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          this.listenerTab = '0'
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.$http({
              url: `/extension/listener/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.listener)
            })
          }
        })
      },
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: `/extension/listener/save`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              this.loading = false
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