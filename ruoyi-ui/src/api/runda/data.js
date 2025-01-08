import request from '@/utils/request'

// 查询告警日志列表
export function listData(query) {
  return request({
    url: '/runda/data/list',
    method: 'get',
    params: query
  })
}

// 查询告警日志详细
export function getData(id) {
  return request({
    url: '/runda/data/' + id,
    method: 'get'
  })
}

// 新增告警日志
export function addData(data) {
  return request({
    url: '/runda/data',
    method: 'post',
    data: data
  })
}

// 修改告警日志
export function updateData(data) {
  return request({
    url: '/runda/data',
    method: 'put',
    data: data
  })
}

// 删除告警日志
export function delData(id) {
  return request({
    url: '/runda/data/' + id,
    method: 'delete'
  })
}
