import request from './request'

// 根据路径查询文件信息，用于附件组件数据回显
export const queryAttachmentInfoByIds = (ids) => {
    return request({
        url: "system/attachment/storage/info",
        method: "post",
        data: ids
    })
}

// 获取文件下载链接
export const getDownloadURL = (id, expireTime) => {
    return request({
        url: `system/attachment/storage/url/${id}`,
        method: "get",
        params: {
            expireTime: expireTime
        }
    })
}

// 公开附件下载
export const publicAttachmentDownload = (id) => {
    return request({
        url: `system/attachment/storage/download/p/${id}`,
        method: 'get',
        responseType: 'blob'
    })
}