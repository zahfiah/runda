import request from '@/utils/request'

// 查询大气数据查询212列表
export function listQuery212(query) {
  return request({
    url: '/runda/query212/list',
    method: 'get',
    params: query
  })
}

// 查询大气数据查询212详细
export function getQuery212(id) {
  return request({
    url: '/runda/query212/' + id,
    method: 'get'
  })
}

// 新增大气数据查询212
export function addQuery212(data) {
  return request({
    url: '/runda/query212',
    method: 'post',
    data: data
  })
}

// 修改大气数据查询212
export function updateQuery212(data) {
  return request({
    url: '/runda/query212',
    method: 'put',
    data: data
  })
}

// 删除大气数据查询212
export function delQuery212(id) {
  return request({
    url: '/runda/query212/' + id,
    method: 'delete'
  })
}
