import request from '@/utils/request'

// 查询监测站点管理列表
export function listStation(query) {
  return request({
    url: '/runda/station/list',
    method: 'get',
    params: query
  })
}

// 查询监测站点管理详细
export function getStation(id) {
  return request({
    url: '/runda/station/' + id,
    method: 'get'
  })
}

// 新增监测站点管理
export function addStation(data) {
  return request({
    url: '/runda/station',
    method: 'post',
    data: data
  })
}

//新增站点消息发送
export function sendStation(data) {
  return request({
    url: '/runda/station/send',
    method: 'post',
    data: data
  })
}

// 修改监测站点管理
export function updateStation(data) {
  return request({
    url: '/runda/station',
    method: 'put',
    data: data
  })
}

// 删除监测站点管理
export function delStation(id) {
  return request({
    url: '/runda/station/' + id,
    method: 'delete'
  })
}
