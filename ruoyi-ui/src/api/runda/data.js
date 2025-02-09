import request from '@/utils/request'

// 查询监测小时报表列表
export function listData(query) {
  return request({
    url: '/runda/data/list',
    method: 'get',
    params: query
  })
}

// 查询监测小时报表详细
export function getData(id) {
  return request({
    url: '/runda/data/' + id,
    method: 'get'
  })
}

// 新增监测小时报表
export function addData(data) {
  return request({
    url: '/runda/data',
    method: 'post',
    data: data
  })
}

// 修改监测小时报表
export function updateData(data) {
  return request({
    url: '/runda/data',
    method: 'put',
    data: data
  })
}

// 删除监测小时报表
export function delData(id) {
  return request({
    url: '/runda/data/' + id,
    method: 'delete'
  })
}
