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
    return request<CarPostCommentDTO>({
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

// 修改评论状态
export const updateStatus = (id: string, status: string) => {
    return request<string>({
        url: 'car/comment/updateStatus/' + id + '/' + status,
        method: 'post'
    });
};

// 根据id集合删除评论
export const deleteByIds = (ids: string[]) => {
    return request({
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