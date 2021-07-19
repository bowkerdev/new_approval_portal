<template>
  <div class="page">
    <el-form size="small" :inline="true"   v-if="showSearchBtn" class="query-form" ref="searchForm" :model="params" @keyup.enter.native="refreshList()" @submit.native.prevent>
            <!-- 搜索框-->
		     <el-form-item  v-for="(option, index) in dataBindFields.filter((field)=>{
           return field.isSearch})"
          :key="index"
          :prop="option.model">
            <user-select 
                v-if="option.type === 'user'"
                size="small"
                v-model="params[`${option.model}`]"
                @getValue='(value, label) => {params[`${option.model}`]= value}'>
            </user-select>
            <SelectTree 
             v-else-if="option.type === 'office'"
             size="small"
             v-model="params[`${option.model}`]"
              :props="{
                  value: 'id',             // ID字段名
                  label: 'name',         // 显示名称
                  children: 'children'    // 子级字段名
                }"
               
              :url="`/sys/office/treeData`"
              :clearable="true" 
              :accordion="true"
              @getValue="(value) => {params[`${option.model}`]= value}"/>
            <SelectTree 
                 v-else-if="option.type === 'area'"
                 size="small"
                 v-model="params[`${option.model}`]"
                :props="{
                    value: 'id',             // ID字段名
                    label: 'name',         // 显示名称
                    children: 'children'    // 子级字段名
                  }"
                :url="`/sys/area/treeData`"
                :clearable="true" 
                :accordion="true"
                @getValue="(value) => {params[`${option.model}`]= value}"/>

             <el-select  v-else-if="option.type === 'dict'"  size="small"  v-model="params[`${option.model}`]">
                <el-option
                  v-for="item in $dictUtils.getDictList(`${option.options.dictType}`)"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value">
                </el-option>
              </el-select>
      <!-- <el-rate
        v-else-if="option.type === 'rate'"
        v-model="params[`${option.model}`]"
      ></el-rate> -->
      <!-- <el-color-picker 
        v-else-if="option.type === 'color'"
        size="small"
        v-model="params[`${option.model}`]"
      ></el-color-picker> -->
 
      <!-- <el-select
        v-else-if="option.type === 'select'"
        v-model="params[`${option.model}`]"
      >
        <el-option v-for="item in (options.remote ? remoteOptions : options.options)" :key="item.value" :value="item.value" :label="options.showLabel || options.remote?item.label:item.value"></el-option>
      </el-select> -->

      <!-- <el-switch
        v-else-if="option.type === 'switch'"
        size = "small"
        v-model="params[`${option.model}`]"
      >
      </el-switch> -->
        <!-- <el-slider 
          v-else-if="option.type === 'slider'"
          size="small"
          v-model="params[`${option.model}`]"
        ></el-slider> -->
          <el-time-picker 
            v-else-if="option.type === 'time'"
            size="small"
            :placeholder="option.name"
            v-model="params[`${option.model}`]">
          </el-time-picker>
          <el-date-picker
            v-else-if="option.type === 'date'"
            size="small"
            :placeholder="option.name"
            v-model="params[`${option.model}`]">
        </el-date-picker>
          <el-input  v-else
            size="small"
            :placeholder="option.name"
            v-model="params[`${option.model}`]"
          ></el-input>
		     </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="refreshList()" size="small" icon="el-icon-search">{{$i18nMy.t('查询')}}</el-button>
            <el-button @click="resetSearch()" size="small" icon="el-icon-refresh-right">{{$i18nMy.t('重置')}}</el-button>
          </el-form-item>
    </el-form>
          <!-- 导入导出-->
    <el-dialog  :title="$i18nMy.t('导入Excel')" :visible.sync="isImportCollapse">
      <el-form size="small" :inline="true" class="query-form" ref="importForm">
         <el-form-item>
          <el-button type="default" @click="downloadTpl()" size="small">{{$i18nMy.t('下载模板')}}</el-button>
         </el-form-item>
         <el-form-item prop="loginName">
            <el-upload
              class="upload-demo"
              :action="`${this.$http.BASE_URL}/form/generate/import`"
              :on-success="uploadSuccess"
              :data="{
                formId: this.$route.query.id
              }"
               :show-file-list="true">
              <el-button size="small" type="primary">{{$i18nMy.t('点击上传')}}</el-button>
              <div slot="tip" class="el-upload__tip">只允许导入“xls”或“xlsx”格式文件！</div>
            </el-upload>
        </el-form-item>
      </el-form>
    </el-dialog>
  <div class="bg-white" :style="showSearchBtn? 'height:calc(100% - 76px)': 'height:100%'">
    <el-row>
      <el-button v-if="$route.query.previewMode || hasPermission(`form:${tableName}:add`)" type="primary" size="small" icon="el-icon-plus" @click="add()">{{$i18nMy.t('新建')}}</el-button>
      <el-button v-if="$route.query.previewMode || hasPermission(`form:${tableName}:edit`)" type="warning" size="small" icon="el-icon-edit-outline" @click="edit()"
        :disabled="dataListSelections.length != 1" plain>{{$i18nMy.t('修改')}}</el-button>
      <el-button v-if="$route.query.previewMode || hasPermission(`form:${tableName}:del`)" type="danger"   size="small" icon="el-icon-delete" @click="del()"
                :disabled="dataListSelections.length <= 0" plain>{{$i18nMy.t('删除')}}</el-button>
      <el-button :disabled="dataListSelections.length != 1" v-for="(item, index) in options.config.actions.filter((action)=>{return action.position.indexOf('1') > -1 && ($route.query.previewMode || !action.auth || hasPermission(action.auth))})" :key="index"  size="small" icon="el-icon-link"  @click="go(item)">
        {{item.name}}
      </el-button>
      <el-button-group class="pull-right">
          <el-button v-if="$route.query.previewMode || hasPermission(`form:${tableName}:import`)" type="default" size="small" icon="el-icon-upload2" title="导入" @click="isImportCollapse = !isImportCollapse"></el-button>
          <el-button v-if="$route.query.previewMode || hasPermission(`form:${tableName}:export`)" type="default" size="small" icon="el-icon-download" title="导出" @click="exportExcel()"></el-button>
          <el-button
            type="default"
            size="small"
            icon="el-icon-refresh"
            @click="refreshList">
          </el-button>
      </el-button-group>
    </el-row>
    <el-table
      :data="dataList"
      size="small"
      @selection-change="selectionChangeHandle"
      @sort-change="sortChangeHandle"
      height="calc(100% - 80px)"
      v-loading="loading"
      class="table">
      <el-table-column
        type="selection"
        header-align="center"
        align="center"
        width="50">
      </el-table-column>
      <el-table-column
       v-for="(option, index) in dataBindFields.filter((item)=>{
         return item.isShow
         })"
        :key="index"
        :prop="option.model"
        show-overflow-tooltip
        :sortable="option.isSort?'custom':false"
        :label="option.name">
        <template  slot-scope="scope">
         <div v-if="option.type === 'html'"  v-html="scope.row[`${option.model}`]"></div>
         <div v-else-if="option.type === 'color'"><el-color-picker disabled v-model="scope.row[`${option.model}`]"></el-color-picker></div>
         <div v-else-if="option.type === 'editor'"><p v-html="$utils.unescapeHTML(scope.row[`${option.model}`] || '')"></p></div>
         <div v-else-if="option.type === 'imgupload'">
            <el-image
              style="height: 50px;width:50px;margin-right:10px;"
              :src="item.url" v-for="(item, index) in JSON.parse(scope.row[`${option.model}`] || '[]')" :key="index"
              :preview-src-list="JSON.parse(scope.row[`${option.model}`]).map((item)=>{return item.url})">
            </el-image>
         </div>
         <div v-else-if="option.type === 'fileupload'">
              <a :href="item.url" target="_blank" :key="index" v-for="(item, index) in JSON.parse(scope.row[`${option.model}`] || '[]')">
                {{decodeURIComponent(item.url.substring(item.url.lastIndexOf("/")+1))}}
            </a>
         </div>
          <div v-else-if="option.type === 'dict'">
                  {{$dictUtils.getDictLabel(`${option.options.dictType}`, scope.row[`${option.model}`])}}
          </div>
          <div v-else-if="option.type === 'selectTree'">
                  <TreeSelectText 
             size="small"
             v-model="scope.row[`${option.model}`]"
              :props="{
                  value: 'id',             // ID字段名
                  label: 'name',         // 显示名称
                  children: 'children'    // 子级字段名
                }"
               
              :url="option.options.dataUrl"
              :clearable="true" 
              :accordion="true"
             />
          </div>
           <div v-else-if="option.type === 'checkbox' && option.options.remote === 3">
                 
                    <span :key="index" v-for="(item, index) in JSON.parse(scope.row[`${option.model}`] || '[]')">
                      {{$dictUtils.getDictLabel(`${option.options.dictType}`, item)}} 
                      <span v-if="index+1 !==  JSON.parse(scope.row[`${option.model}`] || '[]').length">
                        |
                      </span>
                    </span>
          </div>
          
           <div v-else-if="option.type === 'select' && option.options.remote === 3 && option.options.multiple">
                    <span :key="index" v-for="(item, index) in JSON.parse(scope.row[`${option.model}`] || '[]')">
                        {{$dictUtils.getDictLabel(`${option.options.dictType}`, item)}}
                         <span v-if="index+1 !==  JSON.parse(scope.row[`${option.model}`] || '[]').length">
                          |
                        </span>
                    </span>
          </div>
          <div v-else-if="option.type === 'select' && option.options.remote === 3 && !option.options.multiple">
                  {{$dictUtils.getDictLabel(`${option.options.dictType}`, scope.row[`${option.model}`])}}
          </div>

          <div v-else-if="option.type === 'radio' && option.options.remote === 3">
                  {{$dictUtils.getDictLabel(`${option.options.dictType}`, scope.row[`${option.model}`])}}
          </div>
         <div v-else>
             <div v-if="index === 0">
                <el-link  type="primary" :underline="false" v-if="$route.query.previewMode || hasPermission(`form:${tableName}:edit`)" @click="edit(scope.row.id)">{{scope.row[`${option.model}`] || ''}} </el-link>
                <el-link  type="primary" :underline="false" v-else-if="$route.query.previewMode || hasPermission(`form:${tableName}:view`)"  @click="view(scope.row.id)">{{scope.row[`${option.model}`] || ''}} </el-link>
                <span v-else>{{scope.row[`${option.model}`] === undefined ? '' : scope.row[`${option.model}`]}} </span>
             </div>
             <span v-else>{{scope.row[`${option.model}`] === undefined ? '' : scope.row[`${option.model}`]}} </span>
          </div>
        </template>
      </el-table-column>
      <el-table-column
        header-align="center"
        align="center"
        fixed="right"
        :key="Math.random()"
        width="200"
        :label="$i18nMy.t('操作')">
        <template  slot-scope="scope">
          <el-button v-if="$route.query.previewMode || hasPermission(`form:${tableName}:view`)" type="text" icon="el-icon-view" size="mini" @click="view(scope.row.id)">{{$i18nMy.t('查看')}}</el-button>
          <el-button v-if="$route.query.previewMode || hasPermission(`form:${tableName}:edit`)" type="text" icon="el-icon-edit" size="mini" @click="edit(scope.row.id)">{{$i18nMy.t('修改')}}</el-button>
          <el-button v-if="$route.query.previewMode || hasPermission(`form:${tableName}:del`)" type="text" size="mini" icon="el-icon-delete"  @click="del(scope.row.id)">{{$i18nMy.t('删除')}}</el-button>

         <el-button v-for="(item, index) in options.config.actions.filter((action)=>{return action.position.indexOf('2') > -1 && ($route.query.previewMode || !action.auth || hasPermission(action.auth))})" :key="index"  type="text" size="mini" icon="el-icon-link"  @click="go(item, scope.row)">
            {{item.name}}
          </el-button>

        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="sizeChangeHandle"
      @current-change="currentChangeHandle"
      :current-page="pageNo"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pageSize"
      :total="total"
      background
      layout="total, sizes, prev, pager, next, jumper">
    </el-pagination>
         <!-- 弹窗, 新增 / 修改 -->
    <GenerateForm ref="previewForm" @refreshDataList="refreshList"></GenerateForm>
  </div>
</div>
</template>
<script>
   import GenerateForm from './GenerateForm'
   import UserSelect from '@/components/userSelect'
   import SelectTree from '@/components/treeSelect/treeSelect.vue'
   import TreeSelectText from './TreeSelectText.vue'
   import {isURL} from '@/utils/validate'
   export default {
     data () {
       return {
         page: {
           currentPage: 1,
           total: 0,
           pageSize: 10
         },
         params: {

         },
         title: '',
         tableName: '',
         dataList: [],
         options: {config: { actions: [] }, list: []},
         dataListSelections: [],
         dataBindFields: [],
         pageNo: 1,
         pageSize: 10,
         total: 0,
         orderBy: '',
         isImportCollapse: false,
         loading: false
       }
     },
     activated () {
       this.$http({
         url: `/form/make/queryById?id=${this.$route.query.id}`,
         method: 'get'
       }).then(({data}) => {
         if (data.form.source) {
           this.options = JSON.parse(data.form.source)
         } else {
          //  this.options = {'list': [], 'config': {'labelWidth': 100, 'labelPosition': 'right', 'size': 'small', 'customClass': ''}}
         }
         this.tableName = data.form.tableName
         this.dataBindFields = []
         this.generateModel(this.options.list)
         this.dataBindFields.forEach((option) => {
           if (option.isSearch) {
             this.$set(this.params, option.model, undefined)
           }
         })
         this.refreshList()
       })
     },
     components: {
       UserSelect,
       SelectTree,
       TreeSelectText,
       GenerateForm
     },
     computed: {
       showSearchBtn () {
         let searchs = this.dataBindFields.filter((field) => {
           return field.isSearch
         })
         return searchs.length > 0
       }
     },
     methods: {
       generateModel (genList) {
         for (let i = 0; i < genList.length; i++) {
           if (genList[i].type === 'grid') {
             genList[i].columns.forEach(item => {
               this.generateModel(item.list)
             })
           } else if (genList[i].type === 'tabs') {
             genList[i].tabs.forEach(item => {
               this.generateModel(item.list)
             })
           } else if (genList[i].type === 'report') {
             genList[i].rows.forEach(row => {
               row.columns.forEach(column => {
                 this.generateModel(column.list)
               })
             })
           } else {
            // 处理老版本没有dataBind值的情况，默认绑定数据
             if (genList[i].options.dataBind) {
               this.dataBindFields.push({'model': genList[i].model,
                 'options': genList[i].options,
                 'name': genList[i].name,
                 'type': genList[i].type,
                 'isShow': genList[i].options.isShow,
                 'isSort': genList[i].options.isSort,
                 'isSearch': genList[i].options.isSearch})
             }
           }
         }
         return this.dataBindFields
       },
       selectionChange (list) {
         this.selections = list
       },
       refreshList () {
         this.loading = true
         this.$http({
           url: `/form/generate/list`,
           method: 'post',
           data: {
             formId: this.$route.query.id,
             params: JSON.stringify(this.params),
             'orderBy': this.orderBy,
             pageNo: this.pageNo,
             pageSize: this.pageSize
           }
         }).then(({data}) => {
           this.dataList = data.page.list
           this.total = data.page.count
           this.loading = false
         })
       },
       // 跳转
       go (item, row) {
         row = row || this.dataListSelections.map(item => {
           return item.id
         })[0]
         let keyValue = item.paramValue
         if (/^[$]{.*}$/.test(item.paramValue)) {
           let keyName = item.paramValue.match(/[$]{[a-zA-Z0-9]*[.]+(\S*)}/)[1]
           keyValue = row[keyName]
         }
         if (isURL(item.link)) {
           this.$router.push({path: '/form/explorer', query: {title: item.name, iframeUrl: `${item.link}?${item.paramKey}=${keyValue}`}})
         } else {
           this.$router.push({path: `${item.link}?${item.paramKey}=${keyValue}`, query: {title: item.name}})
         }
       },
        // 新增
       add () {
         this.$refs.previewForm.init('add', `${this.$route.query.id}`)
       },
        // 编辑
       edit (id) {
         id = id || this.dataListSelections.map(item => {
           return item.id
         })[0]
         this.$refs.previewForm.init('edit', `${this.$route.query.id}`, id)
       },
        // 每页数
       sizeChangeHandle (val) {
         this.pageSize = val
         this.pageNo = 1
         this.refreshList()
       },
      // 当前页
       currentChangeHandle (val) {
         this.pageNo = val
         this.refreshList()
       },
      // 多选
       selectionChangeHandle (val) {
         this.dataListSelections = val
       },

    // 排序
       sortChangeHandle (obj) {
         if (obj.order === 'ascending') {
           this.orderBy = obj.prop + ' asc'
         } else if (obj.order === 'descending') {
           this.orderBy = obj.prop + ' desc'
         } else {
           this.orderBy = ''
         }
         this.refreshList()
       },
           // 查看
       view (id) {
         this.$refs.previewForm.init('view', `${this.$route.query.id}`, id)
       },
       del (id) {
         let ids = id || this.dataListSelections.map(item => {
           return item.id
         }).join(',')
         this.$confirm($i18nMy.t('确定删除所选项吗') + '?', $i18nMy.t('提示'), {
           confirmButtonText: '确定',
           cancelButtonText: '取消',
           type: 'warning'
         }).then(() => {
           this.loading = true
           this.$http({
             url: '/form/generate/delete',
             method: 'delete',
             params: {formId: this.$route.query.id, 'ids': ids}
           }).then(({data}) => {
             this.loading = false
             if (data && data.success) {
               this.$message.success(data.msg)
               this.refreshList()
             }
           })
         })
       },
          // 导入成功
       uploadSuccess (res, file) {
         if (res.success) {
           this.$message.success({dangerouslyUseHTMLString: true,
             message: res.msg})
           this.refreshList()
         } else {
           this.$message.error(res.msg)
         }
       },
      // 下载模板
       downloadTpl () {
         this.$http({
           method: 'post',
           url: '/form/generate/import/template',
           data: {
             formId: this.$route.query.id
           },
           responseType: 'blob'
         }).then(response => {
           if (!response) {
             return
           }
           let link = document.createElement('a')
           link.href = window.URL.createObjectURL(new Blob([response.data]))
           link.target = '_blank'
           let filename = response.headers['content-disposition']
           link.download = decodeURI(filename)
           document.body.appendChild(link)
           link.click()
           document.body.removeChild(link)
         // eslint-disable-next-line handle-callback-err
         }).catch((error) => {

         })
       },
       exportExcel () {
         this.$http({
           method: 'post',
           url: '/form/generate/export',
           data: {
             formId: this.$route.query.id,
             params: JSON.stringify(this.params),
             'orderBy': this.orderBy
           },
           responseType: 'blob'
         }).then(response => {
           if (!response) {
             return
           }
           let link = document.createElement('a')
           link.href = window.URL.createObjectURL(new Blob([response.data]))
           link.target = '_blank'
           let filename = response.headers['content-disposition']
           link.download = decodeURI(filename)
           document.body.appendChild(link)
           link.click()
           document.body.removeChild(link)
         // eslint-disable-next-line handle-callback-err
         }).catch((error) => {

         })
       },
       resetSearch () {
         this.$refs.searchForm.resetFields()
         this.refreshList()
       }
     }
}
</script>