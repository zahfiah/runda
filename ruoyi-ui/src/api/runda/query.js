import request from '@/utils/request'

// 查询数据查询列表
export function listQuery(query) {
  return request({
    url: '/runda/query/list',
    method: 'get',
    params: query
  })
}

// 查询数据查询详细
export function getQuery(id) {
  return request({
    url: '/runda/query/' + id,
    method: 'get'
  })
}

// 新增数据查询
export function addQuery(data) {
  return request({
    url: '/runda/query',
    method: 'post',
    data: data
  })
}

// 修改数据查询
export function updateQuery(data) {
  return request({
    url: '/runda/query',
    method: 'put',
    data: data
  })
}

// 删除数据查询
export function delQuery(id) {
  return request({
    url: '/runda/query/' + id,
    method: 'delete'
  })
}
