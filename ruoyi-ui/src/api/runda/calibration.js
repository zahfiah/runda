import request from '@/utils/request'

// 查询数据校准管理列表
export function listCalibration(query) {
  return request({
    url: '/runda/calibration/list',
    method: 'get',
    params: query
  })
}

// 查询数据校准管理详细
export function getCalibration(id) {
  return request({
    url: '/runda/calibration/' + id,
    method: 'get'
  })
}

// 新增数据校准管理
export function addCalibration(data) {
  return request({
    url: '/runda/calibration',
    method: 'post',
    data: data
  })
}

// 修改数据校准管理
export function updateCalibration(data) {
  return request({
    url: '/runda/calibration',
    method: 'put',
    data: data
  })
}

// 删除数据校准管理
export function delCalibration(id) {
  return request({
    url: '/runda/calibration/' + id,
    method: 'delete'
  })
}
