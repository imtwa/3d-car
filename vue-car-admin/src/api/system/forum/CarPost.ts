import request from "@/utils/Request.ts";
import type {PageResponseType} from "@/api/global/Type.ts";
import type {CarPost, CarPostDTO, CarPostVO} from "./type/CarPost.ts";

// 分页查询帖子列表
export const queryPage = (data: CarPostDTO) => {
    return request<PageResponseType<CarPostVO>>({
        url: "car/post/page",
        method: "post",
        data: data,
    });
};

// 根据id查询帖子详情
export const queryById = (id: string) => {
    return request<CarPostDTO>({
        url: "car/post/" + id,
        method: "get"
    });
};

// 新增或修改帖子
export const save = (data: CarPost) => {
    return request({
        url: 'car/post',
        method: 'post',
        data: data
    });
};

// 修改帖子状态
export const updateStatus = (id: string, status: string) => {
    return request<string>({
        url: 'car/post/updateStatus/' + id + '/' + status,
        method: 'post'
    });
};

// 修改帖子置顶状态
export const updateTop = (id: string, isTop: string) => {
    return request<string>({
        url: 'car/post/updateTop/' + id + '/' + isTop,
        method: 'post'
    });
};

// 根据id集合删除帖子
export const deleteByIds = (ids: string[]) => {
    return request({
        url: 'car/post',
        method: 'delete',
        data: ids
    });
};

// 导出帖子数据
export const exportExcel = (data: CarPostDTO) => {
    return request({
        url: 'car/post/export',
        method: "post",
        data: data,
        responseType: 'blob'
    });
}; 