<template>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form size="small" :model="inputForm" :rules="dataRule" ref="inputForm" @keyup.enter.native="doSubmit()"
             label-width="120px" v-loading="loading" :class="method==='view'?'readonly':''" :disabled="method==='view'" @submit.native.prevent>
    <el-row :gutter="15">
        <el-col :span="12">
           <el-form-item :label="$i18nMy.t('登录名')" prop="loginName" >
              <el-input v-model="inputForm.loginName" maxlength="50" placeholder="" :disabled="true"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item :label="$i18nMy.t('姓名')" prop="name" >
              <el-input v-model="inputForm.name" maxlength="50" placeholder="" :disabled="true"></el-input>
            </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="company.id" :label="$i18nMy.t('公司')" >
            <SelectTree
              :disabled="true"
              ref="companyTree"
              :props="{
                  value: 'id',             // ID字段名
                  label: 'name',         // 显示名称
                  children: 'children'    // 子级字段名
                }"
              :url="`/sys/office/treeData?type=1&&extId=${this.inputForm.id}`"
              :value="inputForm.company.id"
              :clearable="true"
              :accordion="true"
              @getValue="(value) => {inputForm.company.id=value}"/>
          </el-form-item>
        </el-col>
        <el-col :span="12">
           <el-form-item prop="office.id" :label="$i18nMy.t('部门')" >
            <SelectTree
              :disabled="true"
              ref="officeTree"
              :props="{
                  value: 'id',             // ID字段名
                  label: 'name',         // 显示名称
                  children: 'children'    // 子级字段名
                }"

              :url="`/sys/office/treeData?type=2&&extId=${this.inputForm.id}`"
              :value="inputForm.office.id"
              :clearable="true"
              :accordion="true"
              @getValue="(value) => {inputForm.office.id=value}"/>
          </el-form-item>
        </el-col>

        <el-col :span="24">
          <el-form-item :label="$i18nMy.t('角色')" prop="roleIdList">
              <el-select v-model="inputForm.roleIdList" style="width:100%" multiple :placeholder="$i18nMy.t('请选择')">
                <el-option
                  v-for="role in roleList"
                  :key="role.id"
                  :label="role.name"
                  :value="role.id">
                </el-option>
              </el-select>
              <!-- <el-checkbox-group
                v-model="inputForm.roleIdList">
                <el-checkbox v-for="role in roleList" :label="role.id" :key="role.id">{{role.name}}</el-checkbox>
              </el-checkbox-group> -->
          </el-form-item>
        </el-col>
    </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>{{$i18nMy.t('确定')}}</el-button>
    </span>
  </el-dialog>
</template>

<script>
import SelectTree from '@/components/treeSelect/treeSelect.vue'
export default {
  data () {
    var validatePass2 = (rule, value, callback) => {
      if (value !== this.inputForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      visible: false,
      loading: false,
      sso: true,
      title: '',
      method: '',
      roleList: [],
      postList: [],
      inputForm: {
        id: '',
        company: { // 归属公司
          id: '',
          name: ''
        },
        office: {
          id: '',
          name: ''
        },
        post: {
          id: '',
          name: ''
        },
        roleIdList: [],
        loginName: '', // 登录名
        no: '', // 工号
        name: '', // 姓名
        email: '', // 邮箱
        phone: '', // 电话
        mobile: '', // 手机
        loginIp: '', // 最后登陆IP
        loginDate: '', // 最后登陆日期
        loginFlag: '1', // 是否允许登陆
        photo: '', // 头像
        qrCode: '', // 二维码
        oldLoginName: '', // 原登录名
        newPassword: '', // 新密码
        confirmNewPassword: '',
        sign: '', // 签名
        oldLoginIp: '', // 上次登陆IP
        oldLoginDate: '', // 上次登陆日期,
        remarks: '' // 备注

      },
      dataRule: {
        'company.id': [{required: true, message: $i18nMy.t('公司不能为空'), trigger: 'blur'}],
        'office.id': [{required: true, message: $i18nMy.t('部门不能为空'), trigger: 'blur'}],
        name: [{required: true, message: $i18nMy.t('姓名不能为空'), trigger: 'blur'}],
        loginName: [{required: true, message: $i18nMy.t('登录名不能为空'), trigger: 'blur'}],
        roleIdList: [{required: true, message: $i18nMy.t('角色不能为空'), trigger: 'blur'}],
        confirmNewPassword: [{validator: validatePass2, trigger: 'blur'}]
      }
    }
  },
  components: {
    SelectTree
  },
  methods: {
    init (method, id) {
      this.sso = process.env.VUE_APP_SSO_LOGIN==='true'?true:false;
      this.method = method
      this.inputForm.id = id
      if (method === 'add') {
        this.title = $i18nMy.t('新建用户')
      } else if (method === 'edit') {
        this.title = $i18nMy.t('修改用户')
      } else if (method === 'view') {
        this.title = $i18nMy.t('查看用户')
      }
      this.visible = true
      this.$nextTick(() => {
        this.$refs.inputForm.resetFields()
        this.inputForm.oldLoinName = ''
        this.$http({
          url: `/sys/role/list?pageNo=1&pageSize=-1`,
          method: 'get'
        }).then(({data}) => {
          this.roleList = data.page.list
        })
        /* this.$http({
          url: `/sys/post/list?pageNo=1&pageSize=-1&status=1`,
          method: 'get'
        }).then(({data}) => {
          this.postList = data.page.list
        }) */

        if (method === 'edit' || method === 'view') { // 修改或者查看
          this.$http({
            url: `/sys/user/queryById?id=${this.inputForm.id}`,
            method: 'get'
          }).then(({data}) => {
            this.inputForm = this.recover(this.inputForm, data.user)
            this.inputForm.oldLoginName = this.inputForm.loginName
          })
        }
      })
    },

    handleAvatarSuccess (res, file) {
      this.inputForm.photo = res.url
    },

    beforeAvatarUpload (file) {
      const isJPG = file.type.indexOf('image/') >= 0
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像只能是图片格式!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
        return false
      }
      return true
    },
      // 表单提交
    doSubmit () {
      this.$refs['inputForm'].validate((valid) => {
        if (valid) {
          this.loading = true
          if(this.inputForm.no == ''){
            this.inputForm.no=this.inputForm.loginName;
          }
          this.$http({
            url: `/sys/user/save`,
            method: 'post',
            headers: {arrayFormat: 'repeat'},
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
<style scoped>
.avatar{
  height: 100px;
}
</style>
