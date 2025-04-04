import request from "@/utils/Request.ts";

export interface CarModel {
  id?: string
  brandId?: string
  name?: string
  description?: string
  price?: number
  params?: string
  coverImage?: string
  images?: string[]
  modelFile?: string
  status?: boolean
  createTime?: string
  brandName?: string
}

export interface CarModelQuery {
  brand?: string
  status?: string
  pageNum?: number
  pageSize?: number
}

export function getCarModelList(params: CarModelQuery) {
  return request({
    url: 'car/model/page',
    method: 'post',
    data: params
  })
}

export function addCarModel(data: CarModel) {
  return request({
    url: '/car/model/add',
    method: 'post',
    data
  })
}

export function updateCarModel(data: CarModel) {
  return request({
    url: '/car/model/update',
    method: 'post',
    data
  })
}

export function deleteCarModel(id: string) {
  return request({
    url: `/car/model/delete/${id}`,
    method: 'delete'
  })
}

export function changeCarModelStatus(data: { id: string; status: string }) {
  return request({
    url: '/car/model/changeStatus',
    method: 'post',
    data
  })
}