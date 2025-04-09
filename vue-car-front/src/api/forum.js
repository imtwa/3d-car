import request from './request'

// 获取帖子列表
export const getForumPosts = (params) => {
  return request({
    url: '/car/post/page',
    method: 'post',
    data: params
  })
}

// 获取置顶帖子列表
export const getPinnedPosts = (limit = 5, userId) => {
  return request({
    url: `/car/post/pinned/${limit}`,
    method: 'get',
    params: { userId }
  })
}

// 获取帖子详情
export const getForumPostDetail = (id) => {
  return request({
    url: `/car/post/${id}`,
    method: 'get'
  })
}

// 创建帖子
export const createForumPost = (data) => {
  return request({
    url: '/car/post/add',
    method: 'post',
    data
  })
}

// 更新帖子
export const updateForumPost = (id, data) => {
  return request({
    url: '/car/post/update',
    method: 'post',
    data: {
      ...data,
      id
    }
  })
}

// 删除帖子
export const deleteForumPost = (id, userId) => {
  return request({
    url: `/car/post/${id}`,
    method: 'delete'
  })
}

// 点赞帖子
export const likeForumPost = (id, userId) => {
  return request({
    url: `/car/post/like/${id}`,
    method: 'post',
    params: { userId }
  })
}

// 取消点赞
export const unlikeForumPost = (id, userId) => {
  return request({
    url: `/car/post/like/${id}`,
    method: 'post',
    params: { userId }
  })
}

// 获取帖子评论
export const getPostComments = (postId, params) => {
  return request({
    url: `/car/post/${postId}`,
    method: 'get',
    params
  })
}

// 添加评论
export const addComment = (postId, data) => {
  return request({
    url: '/car/post/comment/add',
    method: 'post',
    data: {
      ...data,
      postId
    }
  })
}

// 删除评论
export const deleteComment = (commentId, userId) => {
  return request({
    url: `/car/post/comment/${commentId}`,
    method: 'delete',
    params: { userId }
  })
}

// 置顶/取消置顶帖子
export const pinForumPost = (id, userId) => {
  return request({
    url: `/car/post/toggle-top/${id}`,
    method: 'post'
  })
}

// 取消置顶
export const unpinForumPost = (id, userId) => {
  return request({
    url: `/car/post/toggle-top/${id}`,
    method: 'post'
  })
}

// 上传帖子图片
export const uploadPostImage = (formData) => {
  return request({
    url: '/system/attachment/storage/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}