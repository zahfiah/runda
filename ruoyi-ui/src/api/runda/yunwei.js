import request from '@/utils/request'

// 查询运维日志列表
export function listYunwei(query) {
  return request({
    url: '/runda/yunwei/list',
    method: 'get',
    params: query
  })
}

// 查询运维日志详细
export function getYunwei(id) {
  return request({
    url: '/runda/yunwei/' + id,
    method: 'get'
  })
}

// 新增运维日志
export function addYunwei(data) {
  return request({
    url: '/runda/yunwei',
    method: 'post',
    data: data
  })
}

// 修改运维日志
export function updateYunwei(data) {
  return request({
    url: '/runda/yunwei',
    method: 'put',
    data: data
  })
}

// 删除运维日志
export function delYunwei(id) {
  return request({
    url: '/runda/yunwei/' + id,
    method: 'delete'
  })
}
