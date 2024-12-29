import request from '@/utils/request'

// 查询设备列表
export function listJianceshebei(query) {
  return request({
    url: '/runda/jianceshebei/list',
    method: 'get',
    params: query
  })
}

// 查询设备详细
export function getJianceshebei(id) {
  return request({
    url: '/runda/jianceshebei/' + id,
    method: 'get'
  })
}

// 新增设备
export function addJianceshebei(data) {
  return request({
    url: '/runda/jianceshebei',
    method: 'post',
    data: data
  })
}

// 修改设备
export function updateJianceshebei(data) {
  return request({
    url: '/runda/jianceshebei',
    method: 'put',
    data: data
  })
}

// 删除设备
export function delJianceshebei(id) {
  return request({
    url: '/runda/jianceshebei/' + id,
    method: 'delete'
  })
}
