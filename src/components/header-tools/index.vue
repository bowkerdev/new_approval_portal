<template>
  <el-popover :placement="placement" :width="width" v-model="visible" class="header-filter">
    <div class="header-tools-order">
      <div @click="sortOrder('ascending')"><i class="el-icon-top"></i>&nbsp;升序</div>
      <div @click="sortOrder('descending')"><i class="el-icon-bottom"></i>&nbsp;降序</div>
      <div @click="sortOrder(null)"><i class="el-icon-close"></i>&nbsp;取消排序</div>
    </div>
    <h3 v-text="label"></h3>
    <div class="header-tools-body">
      <div v-for="(option,optionIndex) in optionList">
        <div class="container-flex-space-between">
          <el-select class="filter-mode-select" v-model="option.filterMode">
            <el-option key="_eq" value="_eq" label="等于"></el-option>
            <el-option key="_ne" value="_ne" label="不等于"></el-option>
            <el-option key="_like" value="_like" label="包含"></el-option>
            <el-option key="_not_like" value="_not_like" label="不包含"></el-option>
            <el-option key="_in" value="_in" label="在之中"></el-option>
            <el-option key="_not_in" value="_not_in" label="不在之中"></el-option>
            <el-option key="_ge" value="_ge" label="大于等于"></el-option>
            <el-option key="_le" value="_le" label="小于等于"></el-option>
            <el-option key="_null" value="_null" label="为空"></el-option>
            <el-option key="_not_null" value="_not_null" label="不为空"></el-option>
          </el-select>
          <span>
            <i @click="addFilter(optionIndex)" class="el-icon-circle-plus"></i>
            <i v-if="optionList.length > 1" @click="removeFilter(optionIndex)" class="el-icon-error"></i>
          </span>
        </div>
        <el-input v-if="option.filterMode != '_null' && option.filterMode != '_not_null'" v-model="option.inputValue" size="mini" clearable />
      </div>
    </div>
    <div class="header-tools-footer container-flex-space-between">
      <el-button size="small" @click="visible = false">{{$i18nMy.t('关闭')}}</el-button>
      <el-button size="small" type="primary" @click="doSubmit()">{{$i18nMy.t('确定')}}</el-button>
    </div>
    <span slot="reference" class="header-tools-title">
      <span :class="{'has-search':hasFilter()}" v-text="label"></span><span><span class="caret-wrapper"><i class="sort-caret ascending"></i><i class="sort-caret descending"></i></span><i class="el-icon-search" :class="{'has-search':hasFilter()}"></i></span>
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
      label: String,
      prop: String
    },
    data () {
      return {
        visible: false,
        optionList:[{
          inputValue:'',
          filterMode:'_eq'
        }]
      }
    },
    mounted : function() {
      this.$emit('regist-component', this);
    },
    methods: {
      addFilter (optionIndex) {
        this.optionList.splice(optionIndex+1,0,{
          inputValue:'',
          filterMode:'_eq'
        })
      },
      removeFilter (optionIndex) {
        this.optionList.splice(optionIndex,1);
      },
      hasFilter () {
        if(this.optionList && this.optionList.length){
          for(var i = 0; i < this.optionList.length; i++){
            if (this.optionList[i].inputValue) {
              return true;
            }
          }
        }
        return false;
      },
      doSubmit () {
        var option = {
          prop: this.prop,
          optionList: this.optionList
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
        this.optionList = [{
          inputValue:'',
          filterMode:'_eq'
        }]
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
    max-height: 300px;
    overflow: auto;
  }

  .header-tools-body >>>i{
    color: #66b1ff;
    margin-left: 10px;
    font-size: 16px;
  }
</style>
