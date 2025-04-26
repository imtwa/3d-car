import request from "@/utils/Request.ts";
import type {PageResponseType} from "@/api/global/Type.ts";
import type {CarUser, CarUserDTO, CarUserVO} from "@/api/system/user/type/CarUser.ts";

// 分页查询前台用户列表
export const queryPage = (data: CarUserDTO) => {
    return request<PageResponseType<CarUserVO>>({
        url: "car/user/page",
        method: "post",
        data: data,
    });
};

// 根据id查询前台用户详情
export const queryById = (id: string) => {
    return request<CarUserDTO>({
        url: "car/user/" + id,
        method: "get"
    });
};

// 新增前台用户
export const save = (data: CarUser) => {
    return request({
        url: 'car/user',
        method: 'post',
        data: data
    });
};

// 修改前台用户状态
export const updateStatus = (id: string, status: string) => {
    return request<string>({
        url: 'car/user/updateStatus/' + id + '/' + status,
        method: 'post'
    });
};

// 根据id集合删除前台用户
export const deleteByIds = (ids: string[]) => {
    return request({
        url: 'car/user',
        method: 'delete',
        data: ids
    });
};

// 重置前台用户密码
export const resetPassword = (userId: string, password: string, passwordRequestKey: string) => {
    return request<string>({
        url: 'car/user/resetPassword',
        method: 'post',
        data: {
            userId: userId,
            password: password,
            passwordRequestKey: passwordRequestKey
        }
    });
};

// 导出前台用户数据
export const exportExcel = (data: CarUserDTO) => {
    return request<string>({
        url: 'car/user/export',
        method: "post",
        data: data,
    });
}; 