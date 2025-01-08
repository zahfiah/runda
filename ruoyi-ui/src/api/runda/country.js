import request from '@/utils/request'

// 查询国控数据查询列表
export function listCountry(query) {
  return request({
    url: '/runda/country/list',
    method: 'get',
    params: query
  })
}

// 查询国控数据查询详细
export function getCountry(id) {
  return request({
    url: '/runda/country/' + id,
    method: 'get'
  })
}

// 新增国控数据查询
export function addCountry(data) {
  return request({
    url: '/runda/country',
    method: 'post',
    data: data
  })
}

// 修改国控数据查询
export function updateCountry(data) {
  return request({
    url: '/runda/country',
    method: 'put',
    data: data
  })
}

// 删除国控数据查询
export function delCountry(id) {
  return request({
    url: '/runda/country/' + id,
    method: 'delete'
  })
}
