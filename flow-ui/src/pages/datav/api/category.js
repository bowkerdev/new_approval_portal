import request from '@/utils/httpRequest'
export const getList = (params) => request({
  url: '/datav/dataScreenCategory/treeData',
  method: 'get',
  params: params
});