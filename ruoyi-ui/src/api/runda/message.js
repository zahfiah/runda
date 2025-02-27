import request from '@/utils/request'

// 查询消息通知列表
export function listMessage(query) {
  return request({
    url: '/runda/message/list',
    method: 'get',
    params: query
  })
}

// 查询消息通知详细
export function getMessage(id) {
  return request({
    url: '/runda/message/' + id,
    method: 'get'
  })
}

// 新增消息通知
export function addMessage(data) {
  return request({
    url: '/runda/message',
    method: 'post',
    data: data
  })
}

// 修改消息通知
export function updateMessage(data) {
  return request({
    url: '/runda/message',
    method: 'put',
    data: data
  })
}

// 删除消息通知
export function delMessage(id) {
  return request({
    url: '/runda/message/' + id,
    method: 'delete'
  })
}

//更新已读
export function updateRead(data) {
  return request({
    url: '/runda/message/updateRead',
    method: 'put',
    data: data
  })
}
//统计未读
export function isRead(query) {
  return request({
    url: '/runda/message/isRead',
    method: 'get',
    params: query
  })
}
