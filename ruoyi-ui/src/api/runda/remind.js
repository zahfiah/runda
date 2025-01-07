import request from '@/utils/request'

// 查询告警设置列表
export function listRemind(query) {
  return request({
    url: '/runda/remind/list',
    method: 'get',
    params: query
  })
}

// 查询告警设置详细
export function getRemind(id) {
  return request({
    url: '/runda/remind/' + id,
    method: 'get'
  })
}

// 新增告警设置
export function addRemind(data) {
  return request({
    url: '/runda/remind',
    method: 'post',
    data: data
  })
}

// 修改告警设置
export function updateRemind(data) {
  return request({
    url: '/runda/remind',
    method: 'put',
    data: data
  })
}

// 删除告警设置
export function delRemind(id) {
  return request({
    url: '/runda/remind/' + id,
    method: 'delete'
  })
}
