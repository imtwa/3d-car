import request from "@/utils/Request.ts";
import type { CarImage } from '@/api/system/car/type/type'

export function getCarImageList(params?: any) {
  return request({
    url: '/system/car/image/list',
    method: 'get',
    params
  })
}

export function addCarImage(data: CarImage) {
  return request({
    url: '/system/car/image',
    method: 'post',
    data
  })
}

export function updateCarImage(data: CarImage) {
  return request({
    url: '/system/car/image',
    method: 'put',
    data
  })
}

export function deleteCarImage(id: string) {
  return request({
    url: `/system/car/image/${id}`,
    method: 'delete'
  })
}

export function getCarImageDetail(id: string) {
  return request({
    url: `/system/car/image/${id}`,
    method: 'get'
  })
}