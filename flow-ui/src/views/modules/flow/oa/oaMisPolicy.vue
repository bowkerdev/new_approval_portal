<template>
  <div style="height: 100%;overflow-y: auto;overflow-x: hidden; margin-left: 36px;">
    <el-form size="small" :model="inputForm" ref="inputForm" v-loading="loading" :disabled="formReadOnly"
             label-width="140px" style="width: calc(100% - 25px);">
      <el-row :gutter="15">
        <el-col :span="12">
          <el-form-item :label="$i18nMy.t('部门')" prop="department" :rules="[]">
            <el-input v-model="inputForm.department" placeholder=""></el-input>
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
          <el-table-column prop="requirements"  show-overflow-tooltip  label="需求描述"></el-table-column>
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
          department:''
        },
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
    methods: {
      test(e){
        console.log(e)
      },
      addUser(){
        var obj = {userId:'',name:'',email:''}
        this.userDataList.push(obj)
      },
      deleteUser(index,row){

      },
      //表单提交
      saveForm(callBack) {},
      // 表单草稿提交
      saveAsDraft(callBack) {}
    }
  }
</script>
