import request from '@/utils/httpRequest'
export const getList = (params) => request({
  url: '/datav/dataMap/list',
  method: 'get',
  params: params
});
