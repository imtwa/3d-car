import request from './request'

// 获取帖子列表
export const getForumPosts = params => {
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
export const getForumPostDetail = id => {
  return request({
    url: `/car/post/${id}`,
    method: 'get'
  })
}

// 创建帖子
export const createForumPost = data => {
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
    url: `/car/post/remove/${id}`,
    method: 'post',
    params: { userId }
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

// 获取帖子评论 (获取的是帖子详情，包含评论)
export const getPostComments = (postId, params) => {
  return request({
    url: `/car/post/${postId}`,
    method: 'get',
    params
  })
}

// 获取指定帖子的评论列表 (专用于获取评论)
export const getPostCommentsList = (postId, params) => {
  return request({
    url: `/car/post/comments/${postId}`,
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
export const uploadPostImage = formData => {
  return request({
    url: '/system/attachment/storage/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 收藏帖子
export const collectForumPost = (postId, userId) => {
  return request({
    url: '/car/post/collection/collect',
    method: 'post',
    params: {
      postId,
      userId
    }
  })
}

// 取消收藏帖子
export const uncollectForumPost = (postId, userId) => {
  return request({
    url: '/car/post/collection/uncollect',
    method: 'post',
    params: {
      postId,
      userId
    }
  })
}

// 检查用户是否已收藏帖子
export const checkPostCollected = (postId, userId) => {
  return request({
    url: '/car/post/collection/check',
    method: 'get',
    params: {
      postId,
      userId
    }
  })
}

// 获取用户收藏的帖子
export const getUserCollections = (userId, pageNum = 1, pageSize = 10) => {
  return request({
    url: `/car/post/collection/user/${userId}`,
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}

// 获取用户发布的帖子
export const getUserPosts = (userId, pageNum = 1, pageSize = 10) => {
  return request({
    url: `/car/post/user/posts/${userId}`,
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}

// 获取用户的评论
export const getUserComments = (userId, pageNum = 1, pageSize = 10) => {
  return request({
    url: `/car/post/user/comments/${userId}`,
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}
