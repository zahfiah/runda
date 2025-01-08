import request from '@/utils/request'

// 查询手动矫正列表
export function listCheck(query) {
  return request({
    url: '/runda/check/list',
    method: 'get',
    params: query
  })
}

// 查询手动矫正详细
export function getCheck(id) {
  return request({
    url: '/runda/check/' + id,
    method: 'get'
  })
}

// 新增手动矫正
export function addCheck(data) {
  return request({
    url: '/runda/check',
    method: 'post',
    data: data
  })
}

// 修改手动矫正
export function updateCheck(data) {
  return request({
    url: '/runda/check',
    method: 'put',
    data: data
  })
}

// 删除手动矫正
export function delCheck(id) {
  return request({
    url: '/runda/check/' + id,
    method: 'delete'
  })
}
