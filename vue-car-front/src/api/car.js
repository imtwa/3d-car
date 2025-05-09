import request from './request'

export function getCarModelList(data) {
  return request({
    url: 'car/model/page',
    method: 'post',
    data
  })
}

export function getCarModelSelectId(id) {
  return request({
    url: `car/model/selectId/${id}`,
    method: 'get'
  })
}
