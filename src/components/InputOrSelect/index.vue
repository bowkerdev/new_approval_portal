<script>
import { debounce } from 'lodash'
import request from '@/utils/httpRequest'
export default {
  name: 'InputOrSelect',
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
      default: () => null
    },
    maxlength: {
      type: Number,
      default: 128
    },
    maxInputLength: {
      type: Number,
      default: 128
    }
  },
  data() {
    return {
      inputVal: '',
      visible: false,
      loading: true,
      searchForm: {},
      tableData: [],
      pageNo: 1,
      pageSize: 20,
      total: 0
    }
  },
  watch: {
    inputVal: debounce(function (val) {
      (val !== this.value) && this.$emit('update', val)
    }, 680),
    value: {
      handler(val) {
        (val !== this.inputVal) && (this.inputVal = val)
      }
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
    // 手输入，触发事件
    inputEmit(val) {
      this.$emit('inputCb', val)
    },
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
    clear() {
      this.inputVal = ''
      this.$emit('clear')
    },
    // 点击选择表格行，触发
    selectHandle(currentRow) {
      if(!currentRow) { return }
      this.inputVal = currentRow[this.valueKey]
      this.$emit('confirm', currentRow)
      this.$nextTick(() => {
        this.visible = false
      })
    },
    selectRow(row = undefined) {
      this.$refs.table.setCurrentRow(row)
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
    const loadingDirectives = [{name: 'loading', value: this.loading, modifiers: { fullscreen: false } }]
    // el-form-item 自适应
    const layout = { xs: 24, sm: 24, md: 12, lg: 8 }
    return (
      <div class="input-select-wrap">
        <el-input
          maxlength={this.maxlength}
          vModel={this.inputVal}
          disabled={this.disabled || this.onlySelect}
          vOn:input={this.inputEmit}
        />
        <el-button 
          type="primary" plain
          icon="el-icon-s-operation"
          class="open-dialog-btn"
          disabled={this.disabled}
          vOn:click={this.open} 
        />
        <el-dialog
          customClass="input-select-dialog"
          title={this.title}
          closeOnPressEscape={true}
          closeOnClickModal={false}
          appendToBody={true}
          visible={this.visible}
          on={{ "update:visible": () => (this.visible = false) }}
        >
          <el-scrollbar ref="scrollContainer">
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

            <el-table
              ref="table"
              border={true}
              height={400}
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
              total={this.count}
              layout="total, sizes, prev, pager, next"
              vOn:size-change={this.sizeChangeHandle}
              vOn:current-change={this.currentChangeHandle}
            />
          </el-scrollbar>
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

<style lang="scss">
.input-select-wrap {
  position: relative;

  & > .el-input input {
    padding-right: 45px;
  }

  .open-dialog-btn {
    position: absolute;
    right: 0;
  }
}
.input-select-dialog {
  display: flex;
  flex-direction: column;

  & .el-dialog__body {
    flex-grow: 1;
  }

  .el-scrollbar {
    padding: 0 10px;
    height: 100%;
    // width: calc(100% - 20px);
  }
  .el-form--label-top .el-form-item__label {
    padding-bottom: 0;
  }

  .el-form {
    .el-col {
      padding: 0 7px;
    }
    .el-form-item__content .el-button {
      vertical-align: bottom;
    }
  }

  .el-table--enable-row-hover .el-table__body tr:hover>td {
    background-color: #a2a2c4;
    color: #fff;
  }

  .pagination {
    margin-top: 20px;
  }
}
</style>
