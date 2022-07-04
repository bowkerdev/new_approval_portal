<template>
  <fm-generate-form v-if="visible" :data="options" :edit="edit" class="readonly" :value="formData" ref="generateForm">
    <template slot="qaRecordData" slot-scope="scope">
      <el-row :gutter="20">
        <el-col :span="8">
          <div>{{$i18nMy.t('Reason')}}</div>
        </el-col>
        <el-col :span="8">
          <div>{{$i18nMy.t('Action')}}</div>
        </el-col>
        <el-col :span="8">
          <div>{{$i18nMy.t('Prevention')}}</div>
        </el-col>
      </el-row>      
      <el-row :gutter="20">
        <el-col :span="8">
          <el-select class="mySelect" size="mini" v-model="scope.model.qaRecordData.qaReason" :placeholder="$i18nMy.t('请选择')"  style="">
                <el-option v-for="item in qaReasonList"
                  :key="item"
                  :label="item"
                  :value="item"
                ></el-option>
              </el-select> 
        </el-col>
        <el-col :span="8">
                <el-select class="mySelect" size="mini" v-model="scope.model.qaRecordData.qaAction" :placeholder="$i18nMy.t('请选择')"  style="">
                <el-option v-for="item in qaActionList"
                  :key="item"
                  :label="item"
                  :value="item"
                ></el-option>
              </el-select> 
        </el-col>
        <el-col :span="8">
                <el-select class="mySelect" size="mini" v-model="scope.model.qaRecordData.qaPrevention" :placeholder="$i18nMy.t('请选择')"  style="">
                <el-option v-for="item in qaPreventionList"
                  :key="item"
                  :label="item"
                  :value="item"
                ></el-option>
              </el-select> 
        </el-col>
      </el-row>
    </template>
  </fm-generate-form>
</template>

<!-- 
1.控制QA RAP显示
2.给QA RAP赋值
 -->
<script>
import Table from '../../../../pages/datav/option/components/table.vue'
  export default {
    data() {
      return {
        qaReasonList:[],
        qaActionList:[],
        qaPreventionList:[],
        qaPullDownList:[],
        // 固定代码，不可以改 start
        options: {
          list: []
        },
        visible: false,
        edit: true,
        formData: {}
        // 固定代码，不可以改 end
      }
    },
    created() {
    },
    mounted(){
        let _that = this
        this.$dictUtils.getSqlDictList('GET_EXTRA_CUT_QA_PULL_DOWN', {}, function (data) {
          _that.qaPullDownList = data
          if (_that.qaPullDownList && _that.qaPullDownList.length > 0) {
            for (var i = 0; i < _that.qaPullDownList.length; i++) {
              switch(_that.qaPullDownList[i].type){
                case 'action':
                  _that.qaActionList.push(_that.qaPullDownList[i].value);
                  break
                case 'reason':
                  _that.qaReasonList.push(_that.qaPullDownList[i].value);
                  break
                case 'prevention':
                  _that.qaPreventionList.push(_that.qaPullDownList[i].value);
                  break
              }
            }
          }
        })
    },
    components: {Table},
    methods: {
      test(info) {
        console.log(info)
      },
      // 20220628Ryder为了业务加了一个特殊判断 固定代码，不可以改 start
      createForm(options, formData, showArra, disabledArra, edit) {
        debugger
        this.options = options
        this.formData = formData
        this.visible = true
        this.edit = edit
        this.$nextTick(() => {
          let hideArra = this.$refs.generateForm.getDataBindFields().filter((field) => {
            if (showArra!=null&&!showArra.includes(field)) {
              return true
            }
          })
          //加一个特殊业务判断
          if(formData.applySiteCode != 'BCA'){
            hideArra.push('qaRecordDataLabel');
            hideArra.push('qaRecordData');
          }
          this.$refs.generateForm.hide(hideArra)
          this.$refs.generateForm.disabled(disabledArra, true)
          if (JSON.stringify(this.formData) != '{}') {
            this.$refs.generateForm.setData(this.formData)
            }
        })
      },
      getData() {
        return this.$refs.generateForm.getData()
      }
      // 固定代码，不可以改 end
    }
  }
</script>

<style lang="scss">

</style>
