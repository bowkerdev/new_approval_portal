<template>
  <el-popover :placement="placement" :width="width" v-model="visible">
    <div class="header-tools-order">
      <div @click="sortOrder('ascending')"><i class="el-icon-top"></i>&nbsp;升序</div>
      <div @click="sortOrder('descending')"><i class="el-icon-bottom"></i>&nbsp;降序</div>
      <div @click="sortOrder(null)"><i class="el-icon-close"></i>&nbsp;取消排序</div>
    </div>
    <h3 v-text="label"></h3>
    <div class="header-tools-body">
      <el-select class="filter-mode-select" v-model="option.filterMode">
        <el-option key="" value="" label="等于"></el-option>
        <el-option key="_ne" value="_ne" label="不等于"></el-option>
        <el-option key="_like" value="_like" label="包含"></el-option>
        <el-option key="_not_like" value="_not_like" label="不包含"></el-option>
        <!-- <el-option key="_in" value="_in" label="在之中"></el-option>
        <el-option key="_not_in" value="_not_in" label="不在之中"></el-option> -->
        <el-option key="_ge" value="_ge" label="大于等于"></el-option>
        <el-option key="_le" value="_le" label="小于等于"></el-option>
        <el-option key="_null" value="_null" label="为空"></el-option>
        <el-option key="_not_null" value="_not_null" label="不为空"></el-option>
      </el-select>
      <el-input v-model="option.inputValue" size="mini" clearable />
    </div>
    <div class="header-tools-footer container-flex-space-between">
      <el-button size="small" @click="visible = false">关闭</el-button>
      <el-button size="small" type="primary" @click="doSubmit()">确定</el-button>
    </div>
    <span slot="reference" class="header-tools-title">
      <span :class="{'has-search':option.inputValue}" v-text="label"></span><span><span class="caret-wrapper"><i class="sort-caret ascending"></i><i class="sort-caret descending"></i></span><i class="el-icon-search" :class="{'has-search':option.inputValue}"></i></span>
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
        default: '200'
      },
      label: String,
      prop: String
    },
    data () {
      return {
        visible: false,
        option:{
          inputValue:'',
          filterMode:''
        }
      }
    },
    methods: {
      doSubmit () {
        this.option.prop = this.prop;
        this.$emit('apply-option', this.option)
        this.visible = false;
      },
      sortOrder (sort) {
        this.option.prop = this.prop;
        this.option.sort = sort;
        this.$emit('sort-order', this.option);
        this.visible = false;
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
    width: 80px;
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
</style>
