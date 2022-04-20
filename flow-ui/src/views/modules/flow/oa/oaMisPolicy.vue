<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 36px;">
    <el-form size="small" :model="inputForm" ref="inputForm" v-loading="loading" :disabled="formReadOnly"
             label-width="140px" style="width: calc(100% - 25px);">
      <el-row :gutter="15">
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('区域')" prop="site" :rules="[]">
            <el-select v-model="inputForm.site" :placeholder="$i18nMy.t('请选择区域')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictList('apply_site_code')" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('部门')" prop="department" :rules="[]">
            <el-select @change="selectDepartment" v-model="inputForm.department" :placeholder="$i18nMy.t('请选择部门')" style="width: 100%;">
              <el-option v-for="item in departmentDataList" :key="item.department" :label="item.department" :value="item">
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
              <user-select :limit='1' :value="scope.row.userId" @getValue='(value,name,data) => {scope.row.userId=value;scope.row.name = data[0].name;scope.row.email = data[0].email;}'>
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
        <el-table :data="itemsPolicyDataList" size="small" :border="true"  style="margin-top: 20px">
          <el-table-column prop="items"  show-overflow-tooltip  label="项目"></el-table-column>
          <el-table-column prop="policy"  show-overflow-tooltip  label="政策"></el-table-column>
          <el-table-column prop="states"  show-overflow-tooltip  label="是否开通">
            <template slot-scope="scope">
              <el-radio v-model="scope.row.states" label="1">YES</el-radio>
              <el-radio v-model="scope.row.states" label="">NO</el-radio>
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip  label="需求描述">
            <template slot-scope="scope">
              <el-input v-model="scope.row.requirements" :placeholder="$i18nMy.t('请填写需求描述')"></el-input>
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
          site:'',
          department:''
        },
        departmentDataList:[],
        userDataList:[],
        itemsPolicyDataList:[],

      }
    },
    props: {
      formReadOnly: {
        type: Boolean,
        default: false
      }
    },
    created() {},
    components: {
      UserSelect
    },
    activated () {

    },
    methods: {
      init(query) {
        console.log(query)
        this.misPolicyConfigList()
      },
      selectDepartment(e){
        console.log(e)
        var filterArr = ['id','createBy','createDate','department','updateBy','updateDate']
        var data = []
        for(var key in e){
          if(filterArr.indexOf(key) == -1){
            var obj  = {department:JSON.parse(JSON.stringify(e)),key:key,items:this.$i18nMy.t(key),policy:e[key],states:'',requirements:''}
            data.push(obj)
          }
        }
        this.itemsPolicyDataList = data
      },
      misPolicyConfigList () {
        this.loading = true
        this.$http({
          url: '/flow/oa/mispolicy/oaMisPolicyConfig/list',
          method: 'get',
          params: {
            'pageNo': 1,
            'pageSize': -1,
            'orderBy': 'department'
          }
        }).then(({data}) => {
          this.loading = false
          if (data && data.success) {
            this.departmentDataList = data.page.list
          }
        })
      },
      addUser(){
        var obj = {userId:'',name:'',email:''}
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
        return true;
      },
      //表单提交
      saveForm(callBack) {
        if(!this.validInputForm()){
          this.$message.warning($i18nMy.t('信息录入不完整'))
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
