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
        <el-form-item label-width="220px" :label="$i18nMy.t('department')" prop="department" :rules="[{required: true, message:$i18nMy.t('请填写部门'), trigger:'blur'}]">
          <el-input v-model="inputForm.department"  :placeholder="$i18nMy.t('department')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('hardware')" prop="hardware" :rules="[]">
          <el-input v-model="inputForm.hardware"  :placeholder="$i18nMy.t('hardware')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('lanConnection')" prop="lanConnection" :rules="[]">
          <el-input v-model="inputForm.lanConnection"  :placeholder="$i18nMy.t('lanConnection')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('wifiOffice')" prop="wifiOffice" :rules="[]">
          <el-input v-model="inputForm.wifiOffice"  :placeholder="$i18nMy.t('wifiOffice')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('wifiGuest')" prop="wifiGuest" :rules="[]">
          <el-input v-model="inputForm.wifiGuest"  :placeholder="$i18nMy.t('wifiGuest')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('wifiOthers')" prop="wifiOthers" :rules="[]">
          <el-input v-model="inputForm.wifiOthers"  :placeholder="$i18nMy.t('wifiOthers')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('vpn')" prop="vpn" :rules="[]">
          <el-input v-model="inputForm.vpn"  :placeholder="$i18nMy.t('vpn')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('usbDrive')" prop="usbDrive" :rules="[]">
          <el-input v-model="inputForm.usbDrive"  :placeholder="$i18nMy.t('usbDrive')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('shareDrive')" prop="shareDrive" :rules="[]">
          <el-input v-model="inputForm.shareDrive"  :placeholder="$i18nMy.t('shareDrive')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('msTeamDrive')" prop="msTeamDrive" :rules="[]">
          <el-input v-model="inputForm.msTeamDrive"  :placeholder="$i18nMy.t('msTeamDrive')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('threerdPartyCloudDrive')" prop="threerdPartyCloudDrive" :rules="[]">
          <el-input v-model="inputForm.threerdPartyCloudDrive"  :placeholder="$i18nMy.t('threerdPartyCloudDrive')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('internet')" prop="internet" :rules="[]">
          <el-input v-model="inputForm.internet"  :placeholder="$i18nMy.t('internet')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('instantMassager')" prop="instantMassager" :rules="[]">
          <el-input v-model="inputForm.instantMassager"  :placeholder="$i18nMy.t('instantMassager')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('cloudTransfer')" prop="cloudTransfer" :rules="[]">
          <el-input v-model="inputForm.cloudTransfer"  :placeholder="$i18nMy.t('cloudTransfer')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('oneDrivePersonal')" prop="oneDrivePersonal" :rules="[]">
          <el-input v-model="inputForm.oneDrivePersonal"  :placeholder="$i18nMy.t('oneDrivePersonal')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('msTeamMessage')" prop="msTeamMessage" :rules="[]">
          <el-input v-model="inputForm.msTeamMessage"  :placeholder="$i18nMy.t('msTeamMessage')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('softwareInstallation')" prop="softwareInstallation" :rules="[]">
          <el-input v-model="inputForm.softwareInstallation"  :placeholder="$i18nMy.t('softwareInstallation')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('msEmailToExternalAddress')" prop="msEmailToExternalAddress" :rules="[]">
          <el-input v-model="inputForm.msEmailToExternalAddress"  :placeholder="$i18nMy.t('msEmailToExternalAddress')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('mfa')" prop="mfa" :rules="[]">
          <el-input v-model="inputForm.mfa"  :placeholder="$i18nMy.t('mfa')"></el-input>
        </el-form-item>
        </el-col>
        <el-col :span="12">
        <el-form-item label-width="220px" :label="$i18nMy.t('mobileEmail')" prop="mobileEmail" :rules="[]">
          <el-input v-model="inputForm.mobileEmail"  :placeholder="$i18nMy.t('mobileEmail')"></el-input>
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
        inputForm: {
          id: '',
          procDefKey: '',
          createBy:{id:'', name: ''},
          createDate:'',
          department:'',
          hardware:'',
          lanConnection:'',
          wifiOffice:'',
          wifiGuest:'',
          wifiOthers:'',
          vpn:'',
          usbDrive:'',
          shareDrive:'',
          msTeamDrive:'',
          threerdPartyCloudDrive:'',
          internet:'',
          instantMassager:'',
          cloudTransfer:'',
          oneDrivePersonal:'',
          msTeamMessage:'',
          softwareInstallation:'',
          msEmailToExternalAddress:'',
          mfa:'',
          mobileEmail:'',
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
          this.title = $i18nMy.t('新建')
        } else if (method === 'edit') {
          this.title = $i18nMy.t('修改')
        } else if (method === 'view') {
          this.title = $i18nMy.t('查看')
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/flow/oa/mispolicy/oaMisPolicyConfig/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.oaMisPolicyConfig)
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
              url: `/flow/oa/mispolicy/oaMisPolicyConfig/save`,
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
