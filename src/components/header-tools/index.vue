<template>
  <el-popover :placement="placement" :width="width" v-model="visible" class="header-filter">
    <div class="header-tools-order">
      <div @click="sortOrder('ascending')" class="AZ-anime"><i class="el-icon-top"></i>&nbsp;{{$i18nMy.t('升序')}}</div>
      <div @click="sortOrder('descending')" class="AZ-anime"><i class="el-icon-bottom"></i>&nbsp;{{$i18nMy.t('降序')}}</div>
      <div @click="sortOrder(null)" class="AZ-anime"><i class="el-icon-close"></i>&nbsp;{{$i18nMy.t('取消排序')}}</div>
    </div>
    <h3 v-text="label"></h3>
    <div class="header-tools-body">
      <div>
        <el-select class="filter-mode-select" v-model="option.filterMode">
          <el-option v-for="operation in operationList" :key="operation.value" :value="operation.value" :label="operation.label"></el-option>
        </el-select>
      </div>
      <el-input v-if="['_null','_not_null','_in','_not_in'].indexOf(option.filterMode)<0" v-model="option.inputValue" size="mini" clearable />
      <el-input v-if="['_between'].indexOf(option.filterMode)>-1" v-model="option.subInputValue" size="mini" clearable />
      <div class="option-list-input container-flex-space-between" v-if="['_in','_not_in'].indexOf(option.filterMode)>-1" v-for="(op,opIndex) in option.optionList">
        <el-input v-model="op.inputValue" @paste.native.capture.prevent="onPaste($event,opIndex)" size="mini" clearable />
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
      <span :class="{'has-search':hasFilter()}" v-text="label"></span><span><span class="caret-wrapper"><i class="sort-caret ascending"></i><i class="sort-caret descending"></i></span><i class="el-icon-arrow-down AZ-anime" :class="{'has-search':hasFilter()}"></i></span>
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

<style scoped>
  h3{
    margin: 15px 0px 5px 0px;
  }

  .header-tools-title{
    display: flex;
    align-items: baseline;
    justify-content: space-between;
  }

  .filter-mode-select{
    width: 100px;
  }

  .filter-mode-select >>>.el-input .el-input__inner{
    padding-left: 0px;
    border: none;
  }

  .header-tools-footer{
    margin-top: 10px;
  }

  .header-tools-order{
    border-bottom: 1px solid #EBEEF5;
    padding-bottom: 10px;
  }

  .header-tools-order >>>div{
    padding: 5px 0px;
    border-radius: 4px;
    cursor: pointer;
  }

  .header-tools-order >>>div:hover{
    background-color: #66b1ff;
    color: white;
  }

  .has-search{
    color: #409EFF;
  }

  .header-tools-body{
    max-height: 320px;
    overflow: auto;
    scrollbar-width: none; /* firefox */
    -ms-overflow-style: none; /* IE 10+ */
  }

  .header-tools-body::-webkit-scrollbar {
    display: none; /* Chrome Safari */
  }

  .header-tools-body >>>i{
    color: #66b1ff;
    margin-left: 10px;
    font-size: 16px;
  }

  .el-table .sort-caret{
    border-top-color: transparent;
    border-bottom-color: transparent;
  }

  .header-tools-title >>>.el-icon-arrow-down{
    opacity: 0;
  }

  .header-tools-title:hover >>>.el-icon-arrow-down{
    opacity: 1;
  }

  .el-input+.el-input,
  .option-list-input+.option-list-input{
    margin-top:10px;
  }
</style>
