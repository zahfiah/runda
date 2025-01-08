import request from '@/utils/request'

// 查询监测设备管理设备列表
export function listDevice(query) {
  return request({
    url: '/runda/device/list',
    method: 'get',
    params: query
  })
}

// 查询监测设备管理设备详细
export function getDevice(id) {
  return request({
    url: '/runda/device/' + id,
    method: 'get'
  })
}

// 新增监测设备管理设备
export function addDevice(data) {
  return request({
    url: '/runda/device',
    method: 'post',
    data: data
  })
}

// 修改监测设备管理设备
export function updateDevice(data) {
  return request({
    url: '/runda/device',
    method: 'put',
    data: data
  })
}

// 删除监测设备管理设备
export function delDevice(id) {
  return request({
    url: '/runda/device/' + id,
    method: 'delete'
  })
}
