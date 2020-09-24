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
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('表单名称')" prop="name"
                :rules="[
                  {required: true, message:'表单名称不能为空', trigger:'blur'}
                 ]">
              <el-input v-model="inputForm.name" :placeholder="$i18nMy.t('请填写表单名称')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item label="表单key" prop="code"
                :rules="[
                  {required: true, message:'表单key不能为空', trigger:'blur'},
                  { validator: validateNoExist, trigger: 'blur' }
                 ]">
              <el-input v-model="inputForm.code" :placeholder="$i18nMy.t('请填写表单编码')"     ></el-input>
           </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('所属数据库')"  prop="dataSource.id"  :rules="[{required: true, message: '所属数据库不能为空', trigger: 'blur'}]">
              <SelectTree
              ref="dataSourceTree"
              :props="{
                  value: 'id',             // ID字段名
                  label: 'label',         // 显示名称
                  children: 'children'    // 子级字段名
                }"
              :data="dataSourceTree"
              :value="inputForm.dataSource.id"
              :label="inputForm.dataSource.name"
              :clearable="true"
              :accordion="true"
              :disabled="method !== 'add'"
              @getValue="(id, label, node) => {inputForm.dataSource.id=id, inputForm.dataSource.enName =node.enName, inputForm.dataSource.dbType=node?node.dbType:''}"/>
            </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('是否自动建表')" prop="autoCreate"
                :rules="[
                  {required: true, message:'是否自动建表不能为空', trigger:'blur'}
                 ]">
                    <el-radio-group :disabled="method !== 'add'" @change="changeAutoCreate" v-model="inputForm.autoCreate">
                        <el-radio v-for="item in $dictUtils.getDictList('yes_no')" :label="item.value" :key="item.id">{{item.label}}</el-radio>
                    </el-radio-group>
           </el-form-item>
        </el-col>
        <el-col :span="24">
            <el-form-item  v-if="inputForm.autoCreate === '0'" :label="$i18nMy.t('表名')" prop="tableName"
                  :rules="[
                  {required: true, message:'表名不能为空', trigger:'blur'}
                 ]">
                <el-select :disabled="method !== 'add'" v-model="inputForm.tableName" style="width:100%" filterable :placeholder="$i18nMy.t('请选择表')">
                  <el-option
                    v-for="item in dataTable"
                    :key="item.name"
                    :label="item.nameAndComments"
                    :value="item.name">
                  </el-option>
                </el-select>
           </el-form-item>

         <el-form-item  v-else :label="$i18nMy.t('表名')" prop="tableName"
         :rules="[
                   {validator: validateValid, trigger:'blur'},
                  {required: true, message:'表名不能为空', trigger:'blur'},
                  { validator: validateTableExist, trigger: 'blur' }
                 ]">
                  <el-input :disabled="method !== 'add'" v-model="inputForm.tableName" :placeholder="$i18nMy.t('请输入表名')" >
                     <el-button :disabled="method !== 'add'" @click="createTableName" slot="append">{{$i18nMy.t('生成随机表名')}}</el-button>
                  </el-input>
           </el-form-item>

        </el-col>
        <el-col :span="24">
            <el-form-item :label="$i18nMy.t('备注信息')" prop="remarks"
                :rules="[
                 ]">
          <el-input type="textarea" v-model="inputForm.remarks" :placeholder="$i18nMy.t('请填写备注信息')"     ></el-input>
           </el-form-item>
        </el-col>
        </el-row>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button type="primary" v-if="method != 'view'" @click="doSubmit()" v-noMoreClick>{{$i18nMy.t('确定')}}</el-button>
    </span>
  </el-dialog>
</div>
</template>

<script>
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  export default {
    data () {
      return {
        title: '',
        method: '',
        visible: false,
        loading: false,
        dataSourceTree: [],
        dataTable: [],
        oldCode: '',
        oldTableName: '',
        inputForm: {
          id: '',
          code: '',
          autoCreate: '1',
          dataSource: {
            id: 'master',
            name: '本地数据库',
            enName: 'master',
            dbType: ''
          },
          name: '',
          tableName: '',
          remarks: ''
        }
      }
    },
    components: {
      SelectTree
    },
    watch: {
      'inputForm.dataSource.id': {
        handler (newVal) {
          this.$http({
            url: `/form/make/getTableList?dataSource.enName=${this.inputForm.dataSource.enName}`,
            method: 'get'
          }).then(({data}) => {
            this.dataTable = data.rows
          })
        },
        immediate: true,
        deep: false
      },
      dataTable (val) {
        if (this.inputForm.autoCreate === '0') {
          var list = val.filter((table) => {
            return table.name === this.inputForm.tableName
          })
          if (list.length === 0) {
            this.inputForm.tableName = ''
          }
        }
      }

    },
    methods: {
      init (method, id) {
        this.method = method
        this.inputForm.id = id
        if (method === 'add') {
          this.title = `新建表单`
        } else if (method === 'edit') {
          this.title = '修改表单'
        } else if (method === 'view') {
          this.title = '查看表单'
        }
        this.$http.get(`/database/datalink/dataSource/treeData2`).then(({data}) => {
          this.dataSourceTree = data.treeData
        })
        this.visible = true
        this.loading = false
        this.$nextTick(() => {
          this.$refs.inputForm.resetFields()
          if (method === 'edit' || method === 'view') { // 修改或者查看
            this.loading = true
            this.$http({
              url: `/form/make/queryById?id=${this.inputForm.id}`,
              method: 'get'
            }).then(({data}) => {
              this.inputForm = this.recover(this.inputForm, data.form)
              this.oldCode = this.inputForm.code
              this.oldTableName = this.inputForm.tableName
              this.loading = false
            })
          }
        })
      },
      changeAutoCreate () {
        this.inputForm.tableName = ''
      },
      createTableName () {
        this.inputForm.tableName = 'jp_form_' + new Date().getTime()
      },
      validateNoExist  (rule, value, callback) {
        if (this.method === 'edit' && this.inputForm.code === this.oldCode) {
          callback()
        }
        this.$http.get('/form/make/validateKey?key=' + value).then(({data}) => {
          if (data.noExist) {
            callback()
          } else {
            callback(new Error('表单key已经存在'))
          }
        })
      },
      validateTableExist  (rule, value, callback) {
        if (this.method === 'edit' && this.inputForm.tableName === this.oldTableName) {
          callback()
        }
        this.$http.get(`/form/make/validateTableExist?name=${value}&dataSource.enName=${this.inputForm.dataSource.enName}`).then(({data}) => {
          if (data.noExist) {
            callback()
          } else {
            callback(new Error('表在数据库中已经存在'))
          }
        })
      },
      validateValid (rule, value, callback) {
        if (value && !/^[A-Za-z]+[A-Za-z0-9_-]*$/.test(value)) {
          callback(new Error('请输入合法的表名，只能包含A-Za-z0-9_-这些字符，且必须以英文字符开头!'))
        } else {
          callback()
        }
      },
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            this.loading = true
            this.$http({
              url: `/form/make/saveBasicInfo`,
              method: 'post',
              data: this.inputForm
            }).then(({data}) => {
              if (data && data.success) {
                this.visible = false
                this.$message.success(data.msg)
                this.$emit('refreshDataList')
              }
              this.loading = false
            })
          }
        })
      }
    }
  }
</script>