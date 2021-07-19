<template>
<div>
  <el-dialog
    :title="title"
    :close-on-click-modal="false"
     v-dialogDrag
    :visible.sync="visible">
    <el-form :model="inputForm" ref="inputForm" v-loading="loading" :class="method==='view'?'readonly':''"  :disabled="method==='view'"
             label-width="120px">
      <el-row  :gutter="15">
        <el-col :span="12">
            <el-form-item label="公司" prop="company.id"
                :rules="[
                 ]">
          <!-- <SelectTree 
                      ref="company"
                      :props="{
                          value: 'id',             // ID字段名
                          label: 'name',         // 显示名称
                          children: 'children'    // 子级字段名
                        }"

                      url="/sys/office/treeData?type=1&parentId=bowker"
                      :value="inputForm.company.id"
                      :clearable="true"
                      :accordion="true"
                      @getValue="(value) => {inputForm.company.id=value}"/> -->
               <el-select v-model="inputForm.company.id" :placeholder="$i18nMy.t('公司')" @change="siteChange" clearable>
                <el-option v-for="item in siteList" :key="item.value"
                  :label="item.label" :value="item.value" >
                </el-option>
               </el-select>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="部门" prop="department.id"
                :rules="[
                 ]">
          <SelectTree
                      ref="department"
                      v-if="ifSiteChange"
                      :props="{
                          value: 'id',             // ID字段名
                          label: 'name',         // 显示名称
                          children: 'children'    // 子级字段名
                        }"

                      :url="`/sys/office/treeData?type=2&parentId=${inputForm.company.id}`"
                      :value="inputForm.department.id"
                      :clearable="true"
                      :accordion="true"
                      @getValue="(value) => {inputForm.department.id=value}"/>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="流程标识" prop="procDefKey"
                :rules="[
                 ]">
              <el-input v-model="inputForm.procDefKey" placeholder="请填写流程标识"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="组标识" prop="userGroup"
                :rules="[
                 ]">
              <el-input v-model="inputForm.userGroup" placeholder="请填写组标识"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="组标识辅助码" prop="userGroupCode"
                :rules="[
                 ]">
              <el-input v-model="inputForm.userGroupCode" placeholder="请填写组标识辅助码"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="用户" prop="user.id"
                :rules="[
                 ]">
                <user-select :limit='1' :value="inputForm.user.id" @getValue='(value) => {inputForm.user.id=value}'></user-select>
           </el-form-item>
        </el-col>
        </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>确定</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  import UserSelect from '@/components/userSelect'
  export default {
    data () {
      return {
        ifSiteChange: true,
        siteList:[],
        title: '',
        method: '',
        visible: false,
        loading: false,
        inputForm: {
          id: '',
          company: {
            id: ''
          },
          department: {
            id: ''
          },
          procDefKey: '',
          userGroup: '',
          userGroupCode: '',
          user: {
            id: ''
          }
        }
      }
    },
    components: {
      SelectTree,
      UserSelect
    },
    activated () {
      let _that=this
      this.$dictUtils.getSqlDictList('GET_APPLY_SITE',{},function(data){
        _that.siteList = data
      })
    },
    methods: {
      siteChange(){
        debugger
        this.ifSiteChange = false;
        this.$nextTick(() => {
          this.ifSiteChange = true;
        })
      },
      init (method, id) {
        this.method = method
        this.inputForm.id = id
        if (method === 'add') {
          this.title = $i18nMy.t('新建流程用户组设置')
        } else if (method === 'edit') {
          this.title = $i18nMy.t('修改流程用户组设置')
        } else if (method === 'view') {
          this.title = $i18nMy.t('查看流程用户组设置')
        }
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/flowable/wf/wfUserGroup/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.wfUserGroup)
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
              url: `/flowable/wf/wfUserGroup/save`,
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
