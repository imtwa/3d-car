import request from "@/utils/Request.ts";
import type {PageResponseType} from "@/api/global/Type.ts";
import type {CarPostComment, CarPostCommentDTO, CarPostCommentVO} from "./type/CarPost.ts";

// 分页查询评论列表
export const queryPage = (data: CarPostCommentDTO) => {
    return request<PageResponseType<CarPostCommentVO>>({
        url: "car/comment/page",
        method: "post",
        data: data,
    });
};

// 根据id查询评论详情
export const queryById = (id: string) => {
    return request<CarPostCommentVO>({
        url: "car/comment/" + id,
        method: "get"
    });
};

// 根据帖子ID查询评论列表
export const queryByPostId = (postId: string, data: CarPostCommentDTO) => {
    return request<PageResponseType<CarPostCommentVO>>({
        url: "car/comment/post/" + postId,
        method: "post",
        data: data
    });
};

// 新增评论
export const addComment = (data: CarPostComment) => {
    return request<boolean>({
        url: "car/comment/add",
        method: "post",
        data: data
    });
};

// 修改评论
export const updateComment = (data: CarPostComment) => {
    return request<number>({
        url: "car/comment/update",
        method: "post",
        data: data
    });
};

// 修改评论状态
export const updateStatus = (id: string, status: string) => {
    return request<number>({
        url: 'car/comment/updateStatus/' + id + '/' + status,
        method: 'post'
    });
};

// 删除单个评论
export const deleteById = (id: string) => {
    return request<number>({
        url: 'car/comment/' + id,
        method: 'delete'
    });
};

// 批量删除评论
export const deleteByIds = (ids: string[]) => {
    return request<number>({
        url: 'car/comment',
        method: 'delete',
        data: ids
    });
};

// 导出评论数据
export const exportExcel = (data: CarPostCommentDTO) => {
    return request({
        url: 'car/comment/export',
        method: "post",
        data: data,
        responseType: 'blob'
    });
}; 