import request from "@/utils/Request.ts";

// 获取品牌列表
export const getBrandList = (params?: any) => {
  return request({
    url: '/car/brand/page',
    method: 'post',
    data:params
  })
}

// 新增品牌
export const addBrand = (data: any) => {
  return request({
    url: '/car/brand',
    method: 'post',
    data
  })
}

// 修改品牌
export const updateBrand = (data: any) => {
  return request({
    url: '/car/brand',
    method: 'put',
    data
  })
}

// 删除品牌
export const deleteBrand = (id: string) => {
  return request({
    url: `/car/brand/${id}`,
    method: 'delete'
  })
}