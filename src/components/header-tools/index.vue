<template>
  <el-popover :placement="placement" :width="width" v-model="visible" :style="{ '--defaultTheme': defaultTheme}">
    <div class="header-tools-order" :style="{ '--defaultTheme': defaultTheme}">
      <div @click="sortOrder('ascending')" class="order-item AZ-anime"><i class="el-icon-top"></i>&nbsp;{{$i18nMy.t('升序')}}</div>
      <div @click="sortOrder('descending')" class="order-item AZ-anime"><i class="el-icon-bottom"></i>&nbsp;{{$i18nMy.t('降序')}}</div>
      <div @click="sortOrder(null)" class="order-item AZ-anime"><i class="el-icon-close"></i>&nbsp;{{$i18nMy.t('取消排序')}}</div>
    </div>
    <h3 v-text="label"></h3>
    <div class="header-tools-body">
      <div>
        <el-select class="filter-mode-select" v-model="option.filterMode">
          <el-option v-for="operation in operationList" :key="operation.value" :value="operation.value" :label="operation.label"></el-option>
        </el-select>
      </div>
      <el-input v-if="['_null','_not_null','_in','_not_in'].indexOf(option.filterMode)<0" v-model="option.inputValue" clearable />
      <el-input v-if="['_between'].indexOf(option.filterMode)>-1" v-model="option.subInputValue" clearable />
      <div class="option-list-input container-flex-space-between" v-if="['_in','_not_in'].indexOf(option.filterMode)>-1" v-for="(op,opIndex) in option.optionList">
        <el-input v-model="op.inputValue" @paste.native.capture.prevent="onPaste($event,opIndex)" clearable />
        <div class="container-flex-space-between">
          <i @click="addFilter(opIndex)" class="el-icon-circle-plus"></i>
          <i v-if="option.optionList.length > 1" @click="removeFilter(opIndex)" class="el-icon-error"></i>
        </div>
      </div>
    </div>
    <div class="header-tools-footer container-flex-space-between">
      <el-button size="small" @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button size="small" type="primary" @click="doSubmit()">{{$i18nMy.t('确定')}}</el-button>
    </div>
    <span slot="reference" class="header-tools-title">
      <span :class="{'has-search':hasFilter()}" v-text="label"></span>
      <span style="white-space: nowrap;">
        <span class="caret-wrapper">
          <i class="sort-caret ascending"></i>
          <i class="sort-caret descending"></i>
        </span>
        <i class="fa fa-filter AZ-anime" :class="{'has-search':hasFilter()}"></i>
      </span>
    </span>
  </el-popover>
</template>

<script>
  export default {
    name: 'header-tools',
    props: {
      placement: {
        type: String,
        default: 'bottom'
      },
      width: {
        type: String,
        default: '240'
      },
      dataType: {
        type: String,
        default: 'string'
      },
      label: String,
      prop: String
    },
    data () {
      return {
        visible: false,
        option:{
          inputValue:'',
          subInputValue:'',
          filterMode:'_eq',
          optionList:[{
            inputValue:''
          }]
        },
        operationList:[],
        stringOperation:[
          {value:'_eq',label:$i18nMy.t('正好是')},
          {value:'_ne',label:$i18nMy.t('不是')},
          {value:'_like',label:$i18nMy.t('包含')},
          {value:'_not_like',label:$i18nMy.t('不包含')},
          {value:'_in',label:$i18nMy.t('在之中')},
          {value:'_not_in',label:$i18nMy.t('不在之中')},
          {value:'_null',label:$i18nMy.t('为空')},
          {value:'_not_null',label:$i18nMy.t('不为空')}
        ],
        numberOperation:[
          {value:'_eq',label:$i18nMy.t('等于')},
          {value:'_ne',label:$i18nMy.t('不等于')},
          {value:'_ge',label:$i18nMy.t('大于等于')},
          {value:'_le',label:$i18nMy.t('小于等于')},
          {value:'_between',label:$i18nMy.t('介于以下项之间')},
          {value:'_null',label:$i18nMy.t('为空')},
          {value:'_not_null',label:$i18nMy.t('不为空')}
        ],
        dateOperation:[
          {value:'_eq',label:$i18nMy.t('正好是')},
          {value:'_ne',label:$i18nMy.t('不是')},
          {value:'_ge',label:$i18nMy.t('晚于')},
          {value:'_le',label:$i18nMy.t('早于')},
          {value:'_between',label:$i18nMy.t('介于以下时间之间')},
          {value:'_null',label:$i18nMy.t('为空')},
          {value:'_not_null',label:$i18nMy.t('不为空')}
        ]
      }
    },
    mounted : function() {
      this.$emit('regist-component', this);
      this.operationList =
      this.dataType == 'string'? this.stringOperation :
      this.dataType == 'number'? this.numberOperation :
      this.dataType == 'date'? this.dateOperation :
      this.stringOperation;
    },
    computed: {
      defaultTheme () {
        return this.$store.state.config.defaultTheme
      }
    },
    methods: {
      addFilter (optionIndex) {
        this.option.optionList.splice(optionIndex+1,0,{
          inputValue:'',
          filterMode:'_eq'
        })
      },
      removeFilter (optionIndex) {
        this.option.optionList.splice(optionIndex,1);
      },
      hasFilter () {
        if(this.option.inputValue || this.option.subInputValue){
          return true;
        }else{
          if(this.option.optionList && this.option.optionList.length){
            for(var i = 0; i < this.option.optionList.length; i++){
              if (this.option.optionList[i].inputValue) {
                return true;
              }
            }
          }
          return false;
        }
      },
      doSubmit () {
        var option = {
          prop: this.prop,
          option: this.option
        };
        this.$emit('apply-option', option)
        this.visible = false;
      },
      sortOrder (sort) {
        var option = {
          prop: this.prop,
          sort: sort
        };
        this.$emit('sort-order', option);
        this.visible = false;
      },
      clearFilter () {
        this.option = {
          inputValue:'',
          subInputValue:'',
          filterMode:'_eq',
          optionList:[{
            inputValue:''
          }]
        }
      },
      onPaste(e,index){
        var _this = this;
        var rows = e.clipboardData.getData('text').split('\n');
        //去除最后一位空位
        if (!rows[rows.length-1]) {
          rows.splice(rows.length-1,1);
        }
        //去重
        var obj = {};
        var resultRows = []
        rows.forEach(function(row){
          if(!obj[row]){
            resultRows.push(row);
          }
          obj[row]=true;
        });
        if(resultRows && resultRows.length){
          resultRows.forEach(function(row,rowIndex){
            if (_this.option.optionList.length<=index+rowIndex) {
              _this.option.optionList.push({inputValue:row})
            }else{
              _this.option.optionList[index+rowIndex].inputValue = row;
            }
          });
        }
      }
    }
  }
</script>

<style lang="scss" scoped>
@import '@/assets/scss/element-variables.scss';
  h3{
    margin: 10px 0;
  }

  .header-tools-title{
    display: flex;
    align-items: center;
    justify-content: space-between;
    line-height: 1.1;
    white-space: normal;
    word-break: break-word;
  }

  .header-tools-title>span:first-child{
    display:-webkit-box;
    overflow:hidden;
    white-space:normal !important;
    text-overflow:ellipsis;
    word-wrap:break-word;
    -webkit-line-clamp:2;
    -webkit-box-orient:vertical;
  }

  .header-tools-title i{
    cursor: pointer;
  }

  .filter-mode-select{
    width: 100px;
  }

  .filter-mode-select ::v-deep .el-input .el-input__inner{
    padding-left: 0px;
    border: none;
  }

  .header-tools-footer{
    margin-top: 10px;
  }

  .header-tools-order {
    border-bottom: 1px solid #EBEEF5;
    padding-bottom: 10px;
    font-size: $--font-size-small;

    .order-item {
      padding-top: 3px;
      padding-bottom: 3px;
    }

    ::v-deep div{
      padding: 5px 0px;
      border-radius: 4px;
      cursor: pointer;
    }

    ::v-deep div:hover{
      background-color: var(--defaultTheme);
      color: white;
    }

  }

  .has-search{
    color: var(--defaultTheme);
  }

  .header-tools-body{
    max-height: 320px;
    overflow: auto;
    scrollbar-width: none; /* firefox */
    -ms-overflow-style: none; /* IE 10+ */

    ::v-deep i {
      color: var(--defaultTheme);
      margin-left: 10px;
      font-size: 16px;
    }
  }

  .header-tools-body::-webkit-scrollbar {
    display: none; /* Chrome Safari */
  }

  .el-table .header-tools-title .caret-wrapper .sort-caret{
    border-top-color: transparent;
    border-bottom-color: transparent;
  }
  .el-table .ascending .header-tools-title .sort-caret.ascending {
    border-bottom-color: var(--defaultTheme);
  }
  .el-table .descending .header-tools-title .sort-caret.descending {
    border-top-color: var(--defaultTheme);
  }

  .el-input+.el-input,
  .option-list-input+.option-list-input{
    margin-top: 4px;
  }

  .el-select-dropdown__item {
    font-size: $--font-size-small;
  }
</style>
