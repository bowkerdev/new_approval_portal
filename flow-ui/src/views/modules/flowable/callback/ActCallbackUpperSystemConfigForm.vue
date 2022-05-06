<template>
<div>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form :model="inputForm" size="small" ref="inputForm" v-loading="loading" :class="method==='view'?'readonly':''"  :disabled="method==='view'"
             label-width="120px">
      <el-row  :gutter="15">
        <el-col :span="12">
            <el-form-item label="流程标识" prop="oaKey"
                :rules="[
                 ]">
              <el-input v-model="inputForm.oaKey" :placeholder="$i18nMy.t('请填写流程标识')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="通知的接口" prop="url"
                :rules="[
                 ]">
              <el-input v-model="inputForm.url" :placeholder="$i18nMy.t('请填写通知的接口')"     ></el-input>
           </el-form-item>
        </el-col>
        <text>
          {"procDef":{"name":"ReplacementApproval流程","processDefinitionId":"ReplacementApproval:7:380187af-c79d-11ec-b55b-00163e0661bd","version":7,"processDefinitionKey":"ReplacementApproval"},"vars":{"createTime":"2022-05-06 10:26:35","applicationNo":"RPTBVNBF1202205000001","remark":"543","details":"[{\"colorName\":\"DARK BLUE\",\"reason\":\"CCC\",\"ccc\":\"B\",\"updateByName\":\"zhimi-support\",\"seqNo\":\"66\",\"qty2\":17.4,\"unit1\":\"YARD\",\"unit2\":\"KG\",\"qty1\":47,\"configId\":\"08Q002\",\"id\":6,\"barcode\":\"F0084~#BADF18-067130~V751550O321~66~14226978\",\"brand\":\"ADIDAS\",\"createByName\":\"zhimi-support\",\"batch\":\"V751550-O321\",\"mpo\":\"#BADF18-067130\",\"size\":\"0\",\"status\":\"PENDING\",\"supplierCode\":\"F0084\",\"updateBy\":24461,\"productNo\":[\"1 x 1 RIB\"],\"productType\":\"KNITTED FABRIC\",\"supplierName\":\"PROMAX TEXTILE (VIETNAM) CO., LTD.\",\"materialType\":\"FABRIC\",\"styleNo\":\"S1906GHTT004\",\"specification\":\"61.0\",\"updateTime\":1651804039659,\"createBy\":24461,\"productCode\":\"60021923\",\"createTime\":1651804039659,\"stockId\":2884475,\"colorCode\":\"043A\",\"taskId\":4},{\"colorName\":\"BLACK\",\"reason\":\"CCC\",\"ccc\":\"B\",\"updateByName\":\"zhimi-support\",\"seqNo\":\"120\",\"qty2\":9.93,\"unit1\":\"YARD\",\"unit2\":\"KG\",\"qty1\":39,\"id\":7,\"barcode\":\"F0084~#BADF18-067403~V751460A711~120~14217708-1\",\"brand\":\"ADIDAS\",\"createByName\":\"zhimi-support\",\"batch\":\"V751460-A711\",\"mpo\":\"#BADF18-067403\",\"size\":\"0\",\"status\":\"PENDING\",\"supplierCode\":\"F0084\",\"updateBy\":24461,\"productNo\":[\"1 x 1 RIB\"],\"productType\":\"KNITTED FABRIC\",\"supplierName\":\"PROMAX TEXTILE (VIETNAM) CO., LTD.\",\"materialType\":\"FABRIC\",\"styleNo\":\"F1706GHTM111\",\"specification\":\"70.0\",\"updateTime\":1651804039659,\"createBy\":24461,\"productCode\":\"62696410\",\"createTime\":1651804039659,\"stockId\":2885023,\"colorCode\":\"095A\",\"taskId\":4},{\"colorName\":\"BLACK\",\"reason\":\"CCC\",\"ccc\":\"C\",\"updateByName\":\"zhimi-support\",\"seqNo\":\"13\",\"qty2\":8.18,\"unit1\":\"YARD\",\"unit2\":\"KG\",\"qty1\":54,\"configId\":\"08Q002\",\"id\":8,\"barcode\":\"F0084~#BADF18-073595~V817080A35~13~14238700\",\"brand\":\"ADIDAS\",\"createByName\":\"zhimi-support\",\"batch\":\"V817080-A35\",\"mpo\":\"#BADF18-073595\",\"size\":\"0\",\"status\":\"PENDING\",\"supplierCode\":\"F0084\",\"updateBy\":24461,\"productNo\":\"[DOUBLEWEAVE]\",\"productType\":\"WOVEN\",\"supplierName\":\"PROMAX TEXTILE (VIETNAM) CO., LTD.\",\"materialType\":\"FABRIC\",\"styleNo\":\"S1954MC281WVN\",\"specification\":\"56.0\",\"updateTime\":1651804039659,\"createBy\":24461,\"productCode\":\"70005695\",\"createTime\":1651804039659,\"stockId\":2898901,\"colorCode\":\"095A\",\"taskId\":4},{\"colorName\":\"BLACK\",\"reason\":\"CCC\",\"ccc\":\"C\",\"updateByName\":\"zhimi-support\",\"seqNo\":\"A2269-1\",\"qty2\":0.61,\"unit1\":\"YARD\",\"unit2\":\"KG\",\"qty1\":2,\"configId\":\"01Z003\",\"id\":9,\"barcode\":\"1NWV~#BADF18-073151~2~LOT #95 - 84 (J18B1497)~A2269-1~20190121\",\"brand\":\"ADIDAS\",\"createByName\":\"zhimi-support\",\"batch\":\"LOT #95 - 84 (J18B1497)\",\"mpo\":\"#BADF18-073151\",\"size\":\"0\",\"status\":\"PENDING\",\"supplierCode\":\"1NWV\",\"updateBy\":24461,\"productNo\":\"[SINGLE JERSEY]\",\"productType\":\"KNITTED FABRIC\",\"supplierName\":\"NEW WIDE(VIETNAM) ENTERPRISE CO.,LTD.\",\"materialType\":\"FABRIC\",\"styleNo\":\"F1925W311\",\"specification\":\"60.0\",\"updateTime\":1651804039659,\"createBy\":24461,\"productCode\":\"62732531\",\"createTime\":1651804039659,\"stockId\":2901425,\"colorCode\":\"095A\",\"taskId\":4}]","userName":"zhimi-support","title":"RPTBVNBF1202205000001","applyUserId":"b32fc3d6aae34e9590575ea00f22972d","applicant":"zhimi-support"},"upstreamSystemUrl":"http://192.168.2.108:8080/wms/portal/replacementinventory/approvalCallBack","procIns":{"processInstanceId":"163d048c-cce4-11ec-b55b-00163e0661bd","code":1,"comments":[{"level":"success","commentType":"COMMENT__flow_agree","fullMessage":"同意_FLOW_ACTION_","message":"","type":"_flow_agree","status":"同意"}],"currentTask":{"executionId":"163d048f-cce4-11ec-b55b-00163e0661bd","taskDefinitionKey":"firstApproval","assigneeName":"zhimi-support","assigneeLoginName":"zhimi","name":"第一次审批","assignee":"b32fc3d6aae34e9590575ea00f22972d","id":"163d2bb1-cce4-11ec-b55b-00163e0661bd"},"startTime":1651804054000,"status":"运行中"}}s
        </text>
        <el-col :span="24">
            <el-form-item label="需返回的参数处理js" prop="getParamJs"
                :rules="[
                 ]">
          <el-input type="textarea" v-model="inputForm.getParamJs" placeholder="请填写需返回的参数处理js"     ></el-input>
           </el-form-item>
        </el-col>
        </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button size="small" @click="visible = false">{{$i18nMy.t("关闭")}}</el-button>
      <el-button size="small" type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>{{$i18nMy.t("确定")}}</el-button>
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
        inputForm: {
          id: '',
          oaKey: '',
          url: '',
          getParamJs: ''
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
          this.title = $i18nMy.t(`新建配置设置`)
        } else if (method === 'edit') {
          this.title = $i18nMy.t('修改配置设置')
        } else if (method === 'view') {
          this.title = $i18nMy.t('查看配置设置')
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/flowable/callback/actCallbackUpperSystemConfig/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.actCallbackUpperSystemConfig)
              this.loading = false
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
              url: `/flowable/callback/actCallbackUpperSystemConfig/save`,
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
