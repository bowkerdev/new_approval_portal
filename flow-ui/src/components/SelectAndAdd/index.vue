<script>
import { isFunction } from 'lodash'
import request from '@/utils/httpRequest'
export default {
  name: 'SelectAndAdd',
  model: {
    prop: 'value',
    event: 'update'
  },
  props: {
    title: {
      type: String,
      default: 'Select'
    },
    columns: {
      type: Array,
      required: true
    },
    fields: {
      type: Array,
      default: () => []
    },
    queryUrl: {
      type: String,
      required: true
    },
    method: {
      type: String,
      default: 'GET'
    },
    valueKey: {
      type: String,
      required: true
    },
    value: {
      type: String,
      required: true
    },
    placeholder: {
      type: String,
      default: ''
    },
    disabled: {
      type: Boolean,
      default: false
    },
    onlySelect: {
      type: Boolean,
      default: false
    },
    params: {
      type: Object,
      default: null 
    },
    openAddFormFn: {
      type: Function,
      default: null
    }
  },
  data() {
    return {
      visible: false,
      loading: true,
      searchForm: {},
      tableData: [],
      pageNo: 1,
      pageSize: 20,
      total: 0
    }
  },
  created() {
    // 动态生成 searchForm
    if (this.fields && !Object.keys(this.searchForm).length) {
      this.fields.forEach(el => {
        const prop = el.prop
        if(!(prop in this.searchForm)) {
          this.$set(this.searchForm, prop, '')
        }
      })
    }
  },
  methods: {
    open() {
      this.visible = true
      this.$refs.searchForm? this.reset(): this.fetchList()
    },
    search() {
      this.pageNo = 1
      this.fetchList()
    },
    reset() {
      this.pageNo = 1
      this.$refs.searchForm.resetFields()
      this.fetchList()
    },
    // 点击选择表格行，触发
    selectHandle(currentRow) {
      if(!currentRow) { return }
      this.$emit('update', currentRow[this.valueKey])
      this.$emit('confirm', currentRow)
      this.$nextTick(() => {
        this.visible = false
      })
    },
    sizeChangeHandle(val) {
      this.pageSize = val
      this.pageNo = 1
      this.fetchList()
    },
    currentChangeHandle(val) {
      this.pageNo = val
      this.fetchList()
    },
    fetchList() {
      this.loading = true
      const datakey = this.method.toUpperCase() === 'POST'? 'data': 'params'
      const attachParams = this.params || {}
      request({
        url: this.queryUrl,
        method: this.method, 
        [datakey]: {
          pageNo: this.pageNo,
          pageSize: this.pageSize,
          ...this.searchForm,
          ...attachParams
        }
      }).then(({data}) => {
        if (data && data.success && data.page && data.page.list) {
          this.tableData = data.page.list
          this.count = data.page.count
        }
      }).catch(() => {
        this.tableData = []
        this.count = 0
      }).finally(() => {
        this.loading = false
      })
    }
  },
  render() {
    let addEntryFlag = isFunction(this.openAddFormFn)
    const loadingDirectives = [{name: 'loading', value: this.loading, modifiers: { fullscreen: false } }]
    // el-form-item 自适应
    const layout = { xs: 24, sm: 24, md: 12, lg: 8 }
    return (
      <div class="select-and-add-wrap">
        <el-input
          maxlength={this.maxlength}
          vModel={this.value}
          readonly={true}
          vOn:input={this.inputEmit}
        />
        <el-button 
          type="primary" plain
          icon="el-icon-document-add"
          class="open-dialog-btn"
          disabled={this.disabled}
          vOn:click={this.open} 
        />
        <el-dialog
          customClass="select-and-add-dialog"
          title={this.title}
          closeOnPressEscape={true}
          closeOnClickModal={false}
          appendToBody={true}
          visible={this.visible}
          on={{ "update:visible": () => (this.visible = false) }}
        >
          
          <div class="search-body-wrap">
            <el-form
              ref="searchForm"
              labelPosition="top"
              labelWidth="120px"
              {...{ props: { model: this.searchForm } }}
            >
              <el-row>
                {this.fields &&
                  this.fields.map(el => {
                    return (
                      <el-col {...{props: {...layout}}}>
                        <el-form-item
                          key={el.prop}
                          label={this.$i18nMy.t(el.label)}
                          prop={el.prop}
                        >
                          <el-input
                            maxlength={this.maxInputLength}
                            clearable={true}
                            vModel={this.searchForm[el.prop]}
                          />
                        </el-form-item>
                      </el-col>
                    )
                  })
                }
                <el-col {...{props: {...layout}}}>
                  <el-form-item
                    label="-"
                  >
                    <el-button type="primary" vOn:click={this.search}>
                      { this.$i18nMy.t("查询") }
                    </el-button>
                    <el-button vOn:click={this.reset}>
                      { this.$i18nMy.t("重置") }
                    </el-button>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>

            {addEntryFlag &&
              (
                <div style="margin-bottom: 10px;">
                  <el-button type="primary" 
                    icon="el-icon-document-add"
                    vOn:click={() => {this.openAddFormFn()}}
                  >{this.$i18nMy.t("新增记录")}</el-button>
                </div>
              )
            }
            

            <el-table
              ref="table"
              border={true}
              height={300}
              data={this.tableData}
              highlightCurrentRow={true}
              {...{ directives: loadingDirectives }}
              vOn:current-change={this.selectHandle}
            >
              {this.columns && 
                this.columns.map(col => {
                  return (
                    <el-table-column
                      headerAlign="center"
                      align="center"
                      minWidth="100px"
                      showOverflowTooltip={true}
                      key={col.prop}
                      prop={col.prop}
                      label={col.label}
                    />
                  )
                })
              }
            </el-table>
            <el-pagination
              class="pagination"
              background={true}
              currentPage={this.pageNo}
              pageSizes={[20, 50, 100]}
              pageSize={this.pageSize}
              pagerCount={5}
              total={this.count}
              layout="total, sizes, prev, pager, next"
              vOn:size-change={this.sizeChangeHandle}
              vOn:current-change={this.currentChangeHandle}
            />
          </div>
          
          <span slot="footer" class="dialog-footer">
            <el-button vOn:click={() => {this.visible = false}}>
              {this.$i18nMy.t("关闭")}
            </el-button>
          </span>
        </el-dialog>
      </div>
    )
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/scss/dialog-search-select.scss';
</style>
