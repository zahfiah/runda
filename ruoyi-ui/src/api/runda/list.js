import request from '@/utils/request'

// 查询单位名录列表
export function listList(query) {
  return request({
    url: '/runda/list/list',
    method: 'get',
    params: query
  })
}

// 查询单位名录详细
export function getList(id) {
  return request({
    url: '/runda/list/' + id,
    method: 'get'
  })
}

// 新增单位名录
export function addList(data) {
  return request({
    url: '/runda/list',
    method: 'post',
    data: data
  })
}

// 修改单位名录
export function updateList(data) {
  return request({
    url: '/runda/list',
    method: 'put',
    data: data
  })
}

// 删除单位名录
export function delList(id) {
  return request({
    url: '/runda/list/' + id,
    method: 'delete'
  })
}
