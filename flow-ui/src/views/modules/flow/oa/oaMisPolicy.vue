<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 36px;">
    <el-form size="small" :model="inputForm" ref="inputForm" v-loading="loading" :disabled="formReadOnly"
             label-width="140px" style="width: calc(100% - 25px);">
      <el-row :gutter="15">
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('区域')" prop="site" :rules="[]">
            <el-select v-model="inputForm.site" :placeholder="$i18nMy.t('请选择区域')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictList('apply_site_code')" :key="item.value" :label="item.value" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('部门')" prop="department" :rules="[]">
            <el-select @change="selectDepartment" v-model="inputForm.department" :placeholder="$i18nMy.t('请选择部门')" style="width: 100%;">
              <el-option v-for="item in departmentDataList" :key="item.department" :label="item.department" :value="item.department">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-button size="mini" icon="el-icon-circle-plus" @click="addUser()">Add</el-button>
        <el-table :data="userDataList" size="small" :border="true" style="margin-top: 5px">
          <el-table-column show-overflow-tooltip  label="工号">
            <template  slot-scope="scope">
              <user-select :limit='1' :value="scope.row.userid" @getValue='(value,name,data) => {scope.row.userid=value;scope.row.name = data[0].name;scope.row.email = data[0].email;}'>
              </user-select>
            </template>
          </el-table-column>
          <el-table-column prop="name"  show-overflow-tooltip  label="名字"></el-table-column>
          <el-table-column prop="email"  show-overflow-tooltip  label="AD邮箱"></el-table-column>
          <el-table-column align="center" label="操作">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="danger"
                icon="el-icon-delete-solid"
                @click="deleteUser(scope.$index, scope.row)">Delete</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-row>
      <el-row :gutter="0">
        <el-table ref="itemsPolicyTable" :data="itemsPolicyDataList" size="small" :border="true"  style="margin-top: 20px">
          <el-table-column prop="items"  show-overflow-tooltip  label="项目"></el-table-column>
          <el-table-column prop="policy"  show-overflow-tooltip  label="政策"></el-table-column>
          <el-table-column prop="states"  show-overflow-tooltip  label="是否开通">
            <template slot-scope="scope">
              <el-radio v-model="scope.row.states" @change="(e) => {if(scope.row.states == '1'){scope.row.requirements=''}}" label="1">YES</el-radio>
              <el-radio v-model="scope.row.states" label="0">NO</el-radio>
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip  label="需求描述">
            <template slot-scope="scope">
              <el-input v-model="scope.row.requirements"
                        :class="(scope.row.states == '1'&& (scope.row.requirements || '') == ''?'border-red':'')"
                        :disabled="scope.row.states == '0'"
                        :placeholder="$i18nMy.t('请填写需求描述')"></el-input>
              <br>
              <span v-if="scope.row.states == '1'&& (scope.row.requirements || '') == ''" style="color: red;">需求描述不能为空</span>
            </template>
          </el-table-column>
        </el-table>
      </el-row>
    </el-form>
  </div>
</template>

<script>
  import UserSelect from '@/components/userSelect'
  export default {
    data() {
      return {
        loading:false,
        inputForm:{
          id:'',
          site:'',
          department:''
        },
        departmentDataList:[],
        userDataList:[],
        itemsPolicyDataList:[],

      }
    },
    props: {
      businessId: {
        type: String,
        default: ''
      },
      formReadOnly: {
        type: Boolean,
        default: false
      }
    },
    created() {},
    components: {
      UserSelect
    },
    watch: {

    },
    methods: {
      init (query) {
        this.misPolicyConfigList(query)
      },
      selectDepartment(value){
        console.log(value)
        var filterArr = ['id','createBy','createDate','department','updateBy','updateDate']
        var data = []
        var obj = this.$common.find(this.departmentDataList,function (e){return e.department == value})
        for(var key in obj){
          if(filterArr.indexOf(key) == -1){
            var tmp  = {department:JSON.parse(JSON.stringify(obj)),key:key,items:this.$i18nMy.t(key),policy:obj[key],states:'0',requirements:''}
            data.push(tmp)
          }
        }
        this.itemsPolicyDataList = data
      },
      misPolicyConfigList (query) {
        var _that = this
        _that.loading = true
        _that.$http({
          url: '/flow/oa/mispolicy/oaMisPolicyConfig/list',
          method: 'get',
          params: {
            'pageNo': 1,
            'pageSize': -1,
            'orderBy': 'department'
          }
        }).then(({data}) => {
          _that.loading = false
          if (data && data.success) {
            _that.departmentDataList = data.page.list
          }
          if (query&&query.businessId) {
            _that.loading = true
            _that.inputForm.id = query.businessId
            _that.$nextTick(() => {
              _that.$refs.inputForm.resetFields()
              _that.$http({
                url: `/flow/oa/mispolicy/oaMisPolicyInst/queryById?id=${_that.inputForm.id}`,
                method: 'get'
              }).then(({data}) => {
                _that.inputForm = _that.recover(_that.inputForm, data.oaMisPolicyInst)
                _that.selectDepartment(_that.inputForm.department)
                if(null != data.oaMisPolicyInstUserids){
                  for(var i=0;i<data.oaMisPolicyInstUserids.length;i++){
                    data.oaMisPolicyInstUserids[i].userid = data.oaMisPolicyInstUserids[i].id
                  }
                  _that.userDataList = data.oaMisPolicyInstUserids
                }
                _that.loading = false
              })
            })
          }
        })
      },
      addUser(){
        var obj = {userid:'',name:'',email:''}
        this.userDataList.push(obj)
      },
      deleteUser(index,row){
        this.userDataList.splice(index,1)
      },
      setSaveData(){
        var obj = JSON.parse(JSON.stringify(this.inputForm));
        obj.userids = this.userDataList.map(o => {return o.userid}).join(',')
        for(var i in this.itemsPolicyDataList){
          obj[this.itemsPolicyDataList[i].key] = this.itemsPolicyDataList[i].requirements
        }
        return obj;
      },
      validInputForm(){
        if('' == (this.inputForm.site)){
          return false;
        }
        if('' == (this.inputForm.department)){
          return false;
        }
        if(this.userDataList != null && this.userDataList.length == 0){
          return false;
        }
        for(var i in this.itemsPolicyDataList){
          this.itemsPolicyDataList[i].error = ''
          if(this.itemsPolicyDataList[i].states == '1' && (this.itemsPolicyDataList[i].requirements || '') == ''){
            return false;
          }
        }
        return true;
      },
      //表单提交
      saveForm(callBack) {
        if(!this.validInputForm()){
          this.$message.warning($i18nMy.t('信息录入不完整'))
          return ;
        }
        this.loading = true
        this.$http({
          url: `/flow/oa/mispolicy/oaMisPolicyInst/save`,
          method: 'post',
          data: this.setSaveData()
        }).then(({data}) => {
          this.loading = false
          if (data && data.success) {
            this.$message.success(data.msg)
            callBack(data.businessTable, data.businessId)
          }
          else{
            this.$message.error(data.msg)
          }
        })
      },
      // 表单草稿提交
      saveAsDraft(callBack) {
        if(!this.validInputForm()){
          this.$message.warning($i18nMy.t('信息录入不完整'))
          return ;
        }
        this.loading = true
        this.$http({
          url: `/flow/oa/mispolicy/oaMisPolicyInst/save`,
          method: 'post',
          data: this.setSaveData()
        }).then(({data}) => {
          this.loading = false
          if (data && data.success) {
            this.$message.success(data.msg)
            callBack(data.businessTable, data.businessId)
          }
          else{
            this.$message.error(data.msg)
          }
        })
      }
    }
  }
</script>
<style lang="scss">
.border-red .el-input__inner{
  border: 2px red solid;
}
</style>
