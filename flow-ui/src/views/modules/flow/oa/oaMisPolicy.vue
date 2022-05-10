<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 36px;">
    <el-form size="small" :model="inputForm" ref="inputForm" v-loading="loading" :disabled="formReadOnly"
             label-width="140px" style="width: calc(100% - 25px);">
      <el-row :gutter="15">
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('申请单号')" label-width="150px" prop="applicationNo" :rules="[]">
            <el-input v-model="inputForm.applicationNo" :disabled='true' :placeholder="$i18nMy.t('系统自动生成')"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('申请人')" label-width="220px" prop="createBy.name" :rules="[]">
            <el-input v-model="inputForm.createBy.name" :disabled='true' :placeholder="$i18nMy.t('申请人')"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('申请时间')" label-width="150px" prop="createDate" :rules="[]">
            <el-input v-model="inputForm.createDate" :disabled='true' :placeholder="$i18nMy.t('申请时间')"></el-input>
          </el-form-item>
        </el-col>
        <el-col  :span="12">
          <el-form-item :label="$i18nMy.t('申请人部门')" label-width="220px" prop="createByOffice.name"
                        :rules="[  ]">
            <el-input v-model="inputForm.createByOffice.name" :disabled='true' :placeholder="$i18nMy.t('申请人部门')"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('区域')" label-width="150px" prop="site" :rules="[]">
            <el-select :disabled="(businessId || '') != ''" v-model="inputForm.site" :placeholder="$i18nMy.t('请选择区域')" style="width: 100%;">
              <el-option v-for="item in $dictUtils.getDictList('apply_site_code')" :key="item.value" :label="item.value" :value="item.value">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('部门')" label-width="220px" prop="department" :rules="[]">
            <el-select :disabled="(businessId || '') != ''" @change="selectDepartment" v-model="inputForm.department" :placeholder="$i18nMy.t('请选择部门')" style="width: 100%;">
              <el-option v-for="item in departmentDataList" :key="item.department" :label="item.department" :value="item.department">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="24">
          <p style="text-align: left;margin: 20px 0px 10px 0px;font-size: 16px;font-weight: bold;">
              {{$i18nMy.t('需求人员列表')}}
          </p>
        </el-col>
        <el-button :disabled="(businessId || '') != ''" size="mini" icon="el-icon-circle-plus" @click="addUser()">{{$i18nMy.t('新增')}}</el-button>
        <el-table :data="userDataList" size="small" :border="true" style="margin-top: 5px">
          <el-table-column show-overflow-tooltip :label="$i18nMy.t('员工号')">
            <template  slot-scope="scope">
              <user-select v-if="scope.row.userid == ''" :disabled="(businessId || '') != ''" :value="scope.row.userid" @getValue='(value,name,data) => { selectUser(scope.$index,scope.row,data)}'></user-select>
              <user-select v-else :limit="1" :disabled="(businessId || '') != ''" :value="scope.row.userid" @getValue='(value,name,data) => { selectUser(scope.$index,scope.row,data)}'></user-select>
            </template>
          </el-table-column>
          <el-table-column prop="name"  show-overflow-tooltip  :label="$i18nMy.t('名字')"></el-table-column>
          <el-table-column prop="email"  show-overflow-tooltip  :label="$i18nMy.t('AD邮箱')"></el-table-column>
          <el-table-column align="center" :label="$i18nMy.t('操作')">
            <template slot-scope="scope">
              <el-button
                :disabled="(businessId || '') != ''"
                size="mini"
                type="danger"
                icon="el-icon-delete-solid"
                @click="deleteUser(scope.$index, scope.row)">{{$i18nMy.t('删除')}}</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-row>
      <el-row :gutter="0">
        <el-col :span="24">
          <p style="text-align: left;margin: 20px 0px 10px 0px;font-size: 16px;font-weight: bold;">
              {{$i18nMy.t('IT权限开通需求')}}
          </p>
        </el-col>
        <el-table ref="itemsPolicyTable" :data="itemsPolicyDataList" size="small" :border="true"  style="margin-top: 20px">
          <el-table-column prop="items"  show-overflow-tooltip  :label="$i18nMy.t('IT权限')"></el-table-column>
          <el-table-column prop="policy"  show-overflow-tooltip  :label="$i18nMy.t('政策')"></el-table-column>
          <el-table-column prop="states"  show-overflow-tooltip  :label="$i18nMy.t('是否开通')">
            <template slot-scope="scope">
              <el-radio  :disabled="(businessId || '') != ''" v-model="scope.row.states" @change="(e) => {if(scope.row.states == '1'){scope.row.requirements=''}}" label="1">{{$i18nMy.t('是')}}</el-radio>
              <el-radio :disabled="(businessId || '') != ''" v-model="scope.row.states" @change="(e) => {scope.row.requirements=''}" label="0">{{$i18nMy.t('否')}}</el-radio>
            </template>
          </el-table-column>
          <el-table-column show-overflow-tooltip  :label="$i18nMy.t('需求描述')">
            <template slot-scope="scope">
              <el-input v-model="scope.row.requirements"
                        :class="(scope.row.states == '1'&& (scope.row.requirements || '') == ''?'border-red':'')"
                        :disabled="!(scope.row.states == '1' && (businessId || '') == '')"
                        ></el-input>
              <br>
              <span v-if="scope.row.states == '1'&& (scope.row.requirements || '') == ''" style="color: red;">{{$i18nMy.t('需求描述不能为空')}}</span>
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
          applicationNo:'',
          createBy:{id:this.$store.state.user.id, name: this.$store.state.user.name},
          createDate:this.$common.formatTime(new Date()),
          createByOffice: {
            id: this.$store.state.user.office.id,
            name: this.$store.state.user.office.name
          },
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
      selectUser(index,row,data){
        var _that = this
        var userDataList = JSON.parse(JSON.stringify(_that.userDataList))
        _that.userDataList.length = 0
        userDataList.splice(index,1)
        for(var i=0;i<data.length;i++){
          var tmp = {userid:'',name:'',email:''}
          tmp.userid=data[i].id;
          tmp.name = data[i].name;
          tmp.email = data[i].email;
          userDataList.push(tmp)
        }
        _that.userDataList = userDataList
        console.log(_that.userDataList)
      },
      selectDepartment(value){
        this.initDepartment(value,null)
      },
      initDepartment(department,oaMisPolicyInst){
        console.log(department)
        var filterArr = ['id','createBy','createDate','department','updateBy','updateDate','remarks','delFlag']
        var data = []
        var obj = this.$common.find(this.departmentDataList,function (e){return e.department == department})
        for(var key in obj){
          if(filterArr.indexOf(key) == -1){
            var tmp  = {department:JSON.parse(JSON.stringify(obj)),key:key,items:this.$i18nMy.t(key),policy:obj[key],states:'0',requirements:''}
            if(null != oaMisPolicyInst){
              tmp.requirements = oaMisPolicyInst[key] || ''
              if('' != tmp.requirements){
                tmp.states='1'
              }
            }
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
            _that.businessId = query.businessId
            _that.loading = true
            _that.inputForm.id = query.businessId
            _that.$nextTick(() => {
              _that.$refs.inputForm.resetFields()
              _that.$http({
                url: `/flow/oa/mispolicy/oaMisPolicyInst/queryById?id=${_that.inputForm.id}`,
                method: 'get'
              }).then(({data}) => {
                _that.inputForm = _that.recover(_that.inputForm, data.oaMisPolicyInst)
                _that.initDepartment(_that.inputForm.department,data.oaMisPolicyInst)
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
