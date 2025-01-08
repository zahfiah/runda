import request from '@/utils/request'

// 查询短信记录列表
export function listInfo(query) {
  return request({
    url: '/runda/info/list',
    method: 'get',
    params: query
  })
}

// 查询短信记录详细
export function getInfo(id) {
  return request({
    url: '/runda/info/' + id,
    method: 'get'
  })
}

// 新增短信记录
export function addInfo(data) {
  return request({
    url: '/runda/info',
    method: 'post',
    data: data
  })
}

// 修改短信记录
export function updateInfo(data) {
  return request({
    url: '/runda/info',
    method: 'put',
    data: data
  })
}

// 删除短信记录
export function delInfo(id) {
  return request({
    url: '/runda/info/' + id,
    method: 'delete'
  })
}
