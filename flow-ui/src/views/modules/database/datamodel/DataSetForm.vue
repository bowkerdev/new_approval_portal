<template>
<div class="el-scrollbar__wrap overflow-auto wrap-white padding-20">
  <div class="el-scrollbar__view">
      <el-row :gutter="20" v-loading="loading">
        <el-col :span="24">
           <el-row :gutter="20">
              <el-col :span="12"><h3>{{$i18nMy.t('数据模型配置')}}</h3></el-col>
               <el-col :span="12" style="text-align:right">
                  <el-button v-if="$route.query.method!='view'" type="primary" @click="doSubmit" size="small">{{$i18nMy.t('保存数据源')}}</el-button>
                  <el-button type="info" size="small" @click="goBack">{{$i18nMy.t('返回')}}</el-button>
                </el-col>
           </el-row>
        </el-col>
        <el-col :span="12">
          <el-form size="small"  :model="inputForm" ref="inputForm" v-loading="loading" label-width="150px">
            <el-form-item :label="$i18nMy.t('目标数据库')" prop="db.id"
                :rules="[{required: true, message:'目标数据库不能为空', trigger:'blur'}]">
                  <SelectTree ref="db"
                    :props="{
                        value: 'id',    // ID字段名
                        label: 'label',         // 显示名称
                        children: 'children'    // 子级字段名
                    }"
                    url="/database/datalink/dataSource/treeData2"
                    :value="inputForm.db.id"  :clearable="true"  :accordion="true"
                    @getValue="(value) => {inputForm.db.id=value}"/>
            </el-form-item>
            <el-form-item label="组别" prop="group" :rules="[{required: true, message:'组别', trigger:'blur'} ]">
                <el-select v-model="inputForm.group" :placeholder="$i18nMy.t('请选择')"  style="width: 100%;">
                    <el-option  v-for="item in $dictUtils.getDictList('DATASOURCE_GROUP')"
                      :key="item.value" :label="item.label" :value="item.value">
                    </el-option>
                </el-select>
            </el-form-item>

            <el-form-item :label="$i18nMy.t('数据源名称')" prop="name"
              :rules="[{required: true, message:'数据源名称不能为空', trigger:'blur'}]">
                <el-input v-model="inputForm.name" :placeholder="$i18nMy.t('请填写数据源名称,并且遵循【类型_组别_名称】格式命名')"     ></el-input>
            </el-form-item>
                <el-form-item label="sql语句" prop="sqlcmd"
                  :rules="[{required: true, message:'sql语句不能为空', trigger:'blur'}]">
                <editor v-model="inputForm.sqlcmd" @init="editorInitSQL" lang="sql" height="200px" style="border: 1px solid #d9d9d9;"></editor>
            </el-form-item>
          </el-form>
        </el-col>
        <el-col :span="12">
           <el-table
            :data="paramForm.tableData"
            height="calc(100% - 300px)">
             <el-table-column
              type="index"
              width="50">
            </el-table-column>
            <el-table-column
              prop="field"
              :label="$i18nMy.t('参数名')">
               <template slot-scope="scope">
                <el-input v-model="scope.row.field" :placeholder="$i18nMy.t('请输入内容')"></el-input>
              </template>
            </el-table-column>
            <el-table-column
              prop="defaultValue"
              :label="$i18nMy.t('默认值')">
               <template slot-scope="scope">
                <el-input v-model="scope.row.defaultValue" :placeholder="$i18nMy.t('请输入内容')"></el-input>
              </template>
            </el-table-column>
            <el-table-column
              fixed="right"
              :key="Math.random()"
              :label="$i18nMy.t('操作')">
              <template slot-scope="scope">
                <el-button
                  @click.native.prevent="deleteRow(scope.$index, paramForm.tableData)"
                  type="text"
                  size="small">{{$i18nMy.t('移除')}}</el-button>
              </template>
            </el-table-column>
          </el-table>
           <el-button type="primary" size="small" @click="addRow" style="margin-top:10px;margin-bottom:10px">{{$i18nMy.t('增加参数')}}</el-button>
           <el-alert
            title="说明：" :closable="false" type="success">
            <template slot='title'>
                    <div class="iconSize">SQL中添加参数的方式：格式：{#参数名#}，'{#参数名#}'，#{参数名}:</div>
                    <div class="iconSize">示例，入参id=1时，三种方式：</div>
                    <div class="iconSize">1、select * from table where 1=1 {#id#}，sql将变成</div>
                    <div class="iconSize">select * from table where 1=1 and id = 1</div>
                    <div class="iconSize">2、select * from table where 1=1 '{#id#}'，sql将变成</div>
                    <div class="iconSize">select * from table where 1=1 and id = '1'</div>
                    <div class="iconSize">3、select * from table where id='#{id}'，sql将变成</div>
                    <div class="iconSize">select * from table where id='1'</div>
            </template>

          </el-alert>
          <el-button type="primary" @click="doPreviewTable" size="small" style="margin-top:10px;margin-bottom:10px"><i class="fa fa-eye"></i> 解析</el-button>
          <el-button type="primary" @click="doPreviewData" size="small" style="margin-top:10px;margin-bottom:10px"> <i class="fa fa-eye"></i> 预览数据</el-button>
        </el-col>
        <el-col :span="24">
          <h3>{{$i18nMy.t('数据源列配置')}}</h3>
           <el-table
            :data="columnForm.columnList"
            style="width: 100%">
            <el-table-column
              prop="name"
              :label="$i18nMy.t('字段名')"
              >
            </el-table-column>
            <el-table-column
              prop="type"
              :label="$i18nMy.t('类型')"
              >
            </el-table-column>
            <el-table-column
              prop="label"
              :label="$i18nMy.t('标签')">
              <template slot-scope="scope">
                <el-input v-model="scope.row.label"></el-input>
              </template>
            </el-table-column>
             <el-table-column>
                 <template slot="header">
                    <el-checkbox v-model="columnForm.isNeedAll" :indeterminate="isIndeterminate"  @change="handleCheckAllChange">参与分析</el-checkbox>
                </template>
                <template slot-scope="scope">
                     <el-checkbox v-model="scope.row.isNeed" @change="handleCheckedNeedChange"></el-checkbox>
                </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>


    <el-dialog title="数据预览" :visible.sync="previewVisible"  v-dialogDrag>
     <el-tabs v-if="previewVisible" type="border-card">
        <el-tab-pane label="HTML">
              <el-table
                :data="previewData.json"
                style="height:500px; width: 100%">
                <el-table-column
                  v-for="(value, key, index) in previewData.column"
                  :key = "index"
                  :prop="key"
                  :label="key"
                  width="180">
                </el-table-column>
              </el-table>
        </el-tab-pane>
        <el-tab-pane label="JSON">
            <editor v-model="previewData.json_str" @init="editorInitJSON" lang="json" theme="twilight" width="100%" height="500px"></editor>
        </el-tab-pane>
        <el-tab-pane label="XML">
            <editor v-model="previewData.xml" @init="editorInitXML" lang="xml" theme="twilight" width="100%" height="500px"></editor>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
</div>
</div>
</template>

<script>
  import omit from 'lodash.omit'
  import SelectTree from '@/components/treeSelect/treeSelect.vue'
  export default {
    data () {
      return {
        title: '',
        method: '',
        loading: false,
        previewVisible: false,
        isIndeterminate: false,
        inputForm: {
          id: '',
          db: {
            id: ''
          },
          name: '',
          group:'',
          sqlcmd: ''
        },
        paramForm: {
          tableData: []
        },
        previewData: {
          column: [],
          html: '',
          json: [],
          json_str: '',
          xml: ''
        },
        columnForm: {
          isNeedAll: true,
          columnList: []
        }
      }
    },
    activated () {
      this.inputForm.id = this.$route.query.id
      if(this.inputForm.id !=null){
        this.$http({
          url: `/database/datamodel/dataSet/queryById?id=${this.inputForm.id}`,
          method: 'get'
        }).then(({data}) => {
          this.inputForm = this.recover(this.inputForm, data.dataSet)
          this.columnForm.columnList = data.dataSet.columnList
          this.paramForm.tableData = data.dataSet.params
        })
      }
    },
    components: {
      SelectTree,
      editor: require('vue2-ace-editor')
    },
    methods: {
      deleteRow (index, rows) {
        rows.splice(index, 1)
      },
      addRow () {
        this.paramForm.tableData.push({field: '', defaultValue: ''})
      },
      doPreviewData: function () {
        let fields = this.paramForm.tableData.map((row) => {
          return row.field
        })
        let defaultValues = this.paramForm.tableData.map((row) => {
          return row.defaultValue
        })
        defaultValues.push('')
        this.$http({
          url: '/database/datamodel/dataSet/exec',
          method: 'post',
          headers: {arrayFormat: 'brackets'},
          data: {
            db: this.inputForm.db.id,
            sql: this.inputForm.sqlcmd,
            field: fields,
            defaultValue: defaultValues
          }
        }).then(({data}) => {
          this.previewData.json = data.json
          this.previewData.json_str = JSON.stringify(data.json)
          this.previewData.xml = data.xml
          this.previewData.column = data.json[0] || { }
          this.previewVisible = true
        })
      },
      doPreviewTable () {
        let fields = this.paramForm.tableData.map((row) => {
          return row.field
        })
        let defaultValues = this.paramForm.tableData.map((row) => {
          return row.defaultValue
        })
        defaultValues.push('')
        this.$http({
          url: '/database/datamodel/dataSet/getMeta',
          method: 'post',
          headers: {arrayFormat: 'brackets'},
          data: {
            db: this.inputForm.db.id,
            sql: this.inputForm.sqlcmd,
            field: fields,
            defaultValue: defaultValues
          }
        }).then(({data}) => {
          this.columnForm.isNeedAll = true
          data.columnList.forEach((column) => {
            column.isNeed = true
          })
          this.columnForm.columnList = JSON.parse(JSON.stringify(data.columnList)).map((column) => {
            return this.columnForm.columnList.filter((oldColumn) => {
              return oldColumn.name === column.name
            })[0] || column
          })
        })
      },
      handleCheckAllChange (val) {
        this.columnForm.columnList.forEach((column) => {
          column.isNeed = val
        })
        this.columnForm.columnList = JSON.parse(JSON.stringify(this.columnForm.columnList))
        this.isIndeterminate = false
      },
      handleCheckedNeedChange (val) {
        let trueCount = 0
        let falseCount = 0
        this.columnForm.columnList.forEach((column) => {
          if (column.isNeed) {
            trueCount++
          } else {
            falseCount++
          }
        })
        this.isIndeterminate = trueCount > 0 && falseCount > 0
        this.columnForm.isNeedAll = trueCount > 0
      },
      editorInitXML: function () {
        require('brace/ext/language_tools') // language extension prerequsite...
        require('brace/mode/xml')
        require('brace/theme/twilight')
      },
      editorInitSQL: function () {
        require('brace/ext/language_tools') // language extension prerequsite...
        require('brace/mode/sql')
        require('brace/theme/chrome')
      },
      editorInitJSON: function () {
        require('brace/ext/language_tools') // language extension prerequsite...
        require('brace/mode/json')
        require('brace/theme/twilight')
      },
      // 表单提交
      doSubmit () {
        this.$refs['inputForm'].validate((valid) => {
          if (valid) {
            if('' === (this.inputForm.name || '')){
              this.$message.warning("数据源名称不能为空")
              return ;
            }
            if(!this.$common.regExpEnNumberUnderscore(this.inputForm.name)){
              this.$message.warning("只能输入由数字、26个英文字母或者下划线组成的字符串")
              return ;
            }
            if('_' === this.inputForm.name.substring(0,1)){
              this.$message.warning("首字符不能为下划线")
              return ;
            }
            if(this.$common.regExpNumber(this.inputForm.name.substring(0,1))){
              this.$message.warning("首字符不能为数字")
              return ;
            }
            this.inputForm.name = this.inputForm.name.toUpperCase();
            this.loading = true
            let params = []
            let columnList = []
            this.paramForm.tableData.forEach((item, index) => {
              item.sort = index
              params.push(omit(item, 'id'))
            })
            this.columnForm.columnList.forEach((item, index) => {
              item.sort = index
              columnList.push(omit(item, 'id'))
            })
            this.$http({
              url: `/database/datamodel/dataSet/save`,
              method: 'post',
              data: {
                ...this.inputForm,
                params: params,
                columnList: columnList

              }
            }).then(({data}) => {
              if (data && data.success) {
                this.loading = false
                this.goBack()
              }
            })
          }
        })
      },
      goBack () {
        this.$store.dispatch('tagsView/delView', {fullPath: this.$route.fullPath})
        this.$router.push('/database/datamodel/DataSetList')
      }
    }
  }
</script>
