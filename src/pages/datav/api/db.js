import request from '@/utils/httpRequest'
export const getList = () => {
  return request({
    url: '/database/datalink/dataSource/treeData2',
    method: 'get'
  })
}

export const dynamicSql = (data) => {
  return request({
    url: '/datav/dataScreen/sqlQuery',
    method: 'post',
    data: JSON.parse(data)
  })
}
