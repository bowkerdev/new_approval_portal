<template>
  <div style="background:url('/static/img/bg1.png') no-repeat; background-size:100%  100%; height:100%; background-attachment:inherit;">
	<!-- <div style="text-align:center">
		 <img src="/static/img/bg1.png" width="100%" height="100vh" >
	</div> -->
<!-- 	<div style="margin-top:10px;text-align:center">
		<font color="#666666" size="6pt">Welcome to Development Landscape</font>
	</div> -->
  </div>
</template>

<script>
export default {
  data() {
    return {
      tableHeight:"600",
      tableColumnList: {
        inventory:[
          {value: 'warehouse_name', label: '仓库名称',align:'left'},
          {value: 'product_type', label: '库存类型',align:'left'},
          {value: 'product_no', label: '产品类型',align:'left'},
          {value: 'sku', label: '库存单元SKU',align:'right'},
          {value: 'qty', label: '库存数',align:'right'}
        ],
        inventoryInOut:[
          {value: 'warehouse_name', label: '仓库名称',align:'left'},
          {value: 'product_type', label: '库存类型',align:'left'},
          {value: 'product_no', label: '产品类型',align:'left'},
          {value: 'in_m_qty', label: '本月入库',align:'right'},
          {value: 'out_m_qty', label: '本月出库',align:'right'},
          {value: 'in_d_qty', label: '本日入库',align:'right'},
          {value: 'out_d_qty', label: '本日出库',align:'right'},
        ],
      },
      searchForm: {
        inventory:{
          orderBy: '',
          updateTime:''
        },
        inventoryInOut:{
          orderBy: '',
          updateTime:''
        }
      },
      dataList:{
        inventory:[],
        inventoryInOut:[]
      } ,
      loading: false
    }
  },
  components: {
  },
  created() {

  },

  methods: {
    // 获取数据列表
  }
}
</script>
<style scoped>
.el-table th{
  text-align: center !important;
}
 .float-left{
   float: left;
 }
 .width-50{
   width:calc(50% - 5px);
 }
 .pd-l-5{
   padding-left: 5px;
 }
 .pd-r-5{
   padding-right: 5px;
 }
 .update-time{
   text-align: right;
   padding-right: 10px;
 }

</style>
