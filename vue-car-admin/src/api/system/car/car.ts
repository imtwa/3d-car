import request from "@/utils/Request.ts";

export interface CarModel {
  id?: string;
  brandId?: string;
  name?: string;
  description?: string;
  price?: string;
  parameters?: string;
  coverImage?: string;
  images?: string[];
  modelUrl?: string;
  modelAttachmentId?: string; // 模型文件附件ID
  status?: string;
  createTime?: string;
  updateTime?: string;
  delFlag?: string;
}

export interface CarModelQuery {
  brandId?: string;
  status?: string;
  pageNum?: number;
  pageSize?: number;
}

export function getCarModelList(params: CarModelQuery) {
  return request({
    url: "car/model/page",
    method: "post",
    data: params,
  });
}

export function addCarModel(data: CarModel) {
  return request({
    url: "/car/model/add",
    method: "post",
    data: {
      ...data,
      // 确保imageIds是数组格式传递给后端
      imageIds: Array.isArray(data.imageIds) ? data.imageIds : (data.imageIds ? data.imageIds.toString().split(",").filter(Boolean) : [])
    },
  });
}

export function updateCarModel(data: CarModel) {
  return request({
    url: "/car/model/update",
    method: "post",
    data: {
      ...data,
      // 确保imageIds是数组格式传递给后端
      imageIds: Array.isArray(data.imageIds) ? data.imageIds : (data.imageIds ? data.imageIds.toString().split(",").filter(Boolean) : [])
    },
  });
}

export function deleteCarModel(id: string) {
  return request({
    url: `/car/model/delete/${id}`,
    method: "delete",
  });
}

export function changeCarModelStatus(data: { id: string; status: string }) {
  return request({
    url: "/car/model/changeStatus",
    method: "post",
    data,
  });
}
