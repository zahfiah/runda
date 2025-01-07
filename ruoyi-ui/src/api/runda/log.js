import request from '@/utils/request'

// 查询接口日志列表
export function listLog(query) {
  return request({
    url: '/runda/log/list',
    method: 'get',
    params: query
  })
}

// 查询接口日志详细
export function getLog(id) {
  return request({
    url: '/runda/log/' + id,
    method: 'get'
  })
}

// 新增接口日志
export function addLog(data) {
  return request({
    url: '/runda/log',
    method: 'post',
    data: data
  })
}

// 修改接口日志
export function updateLog(data) {
  return request({
    url: '/runda/log',
    method: 'put',
    data: data
  })
}

// 删除接口日志
export function delLog(id) {
  return request({
    url: '/runda/log/' + id,
    method: 'delete'
  })
}
