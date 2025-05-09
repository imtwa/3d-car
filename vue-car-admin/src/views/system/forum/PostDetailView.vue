<template>
  <div>
    <a-flex :gap="16" vertical>
      <!-- 返回按钮 -->
      <a-card :style="{border: 'none'}" :body-style="{'padding-bottom': '0'}">
        <a-flex justify="space-between" align="center">
          <a-button @click="goBack">
            <template #icon>
              <ArrowLeftOutlined />
            </template>
            返回帖子列表
          </a-button>
          <a-space>
            <a-popconfirm 
              title="删除后不可恢复，是否删除？"
              ok-text="确 定"
              cancel-text="取 消"
              @confirm="handleDelete"
            >
              <a-button danger>
                <template #icon>
                  <DeleteOutlined />
                </template>
                删除帖子
              </a-button>
            </a-popconfirm>
            <a-button type="primary" @click="handleUpdateStatus">
              <template #icon>
                <CheckOutlined v-if="postDetail.status === '1'" />
                <StopOutlined v-else />
              </template>
              {{ postDetail.status === '0' ? '停用帖子' : '启用帖子' }}
            </a-button>
            <a-button type="primary" @click="handleUpdateTop">
              <template #icon>
                <VerticalAlignTopOutlined v-if="postDetail.isTop === '0'" />
                <VerticalAlignBottomOutlined v-else />
              </template>
              {{ postDetail.isTop === '0' ? '置顶帖子' : '取消置顶' }}
            </a-button>
          </a-space>
        </a-flex>
      </a-card>

      <!-- 帖子详情 -->
      <a-card :style="{border: 'none'}" :loading="loading">
        <a-flex justify="space-between" align="flex-start">
          <a-space direction="vertical" style="max-width: 80%;">
            <a-typography-title :level="3">{{ postDetail.title }}</a-typography-title>
            <a-space>
              <a-tag color="blue" v-if="postDetail.isTop === '1'">置顶</a-tag>
              <a-tag color="green" v-if="postDetail.status === '0'">正常</a-tag>
              <a-tag color="red" v-else>停用</a-tag>
              <a-typography-text>
                <UserOutlined /> {{ postDetail.user?.username || '-' }}
              </a-typography-text>
              <a-typography-text>
                <ClockCircleOutlined /> {{ postDetail.createTime ? dayjs(postDetail.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}
              </a-typography-text>
              <a-typography-text>
                <EyeOutlined /> 浏览: {{ postDetail.viewCount || 0 }}
              </a-typography-text>
              <a-typography-text>
                <LikeOutlined /> 点赞: {{ postDetail.likeCount || 0 }}
              </a-typography-text>
              <a-typography-text>
                <CommentOutlined /> 评论: {{ postDetail.commentCount || 0 }}
              </a-typography-text>
            </a-space>
          </a-space>
        </a-flex>
        
        <a-divider />
        
        <div class="post-content" v-html="postDetail.content" style="margin-bottom: 24px;"></div>
        
        <a-divider orientation="left">评论列表</a-divider>

        <!-- 评论列表 -->
        <a-table 
          :columns="commentColumn"
          :data-source="commentList"
          :loading="commentLoading"
          :pagination="false"
          row-key="id"
        >
          <template #bodyCell="{column,record,text}">
            <template v-if="column.key === 'createTime'">
              {{text ? dayjs(text).format('YYYY-MM-DD HH:mm') : '-'}}
            </template>
            <template v-if="column.key === 'status'">
              <a-switch v-model:checked="record.statusIsNormal"
                        @change="(checked: boolean | string | number, event: MouseEvent) => handleUpdateCommentStatus(event, record.id, text)"
                        @click="(checked: boolean | string | number, event: MouseEvent) => { event.stopPropagation(); record.updateStatusLoading = true }"
                        :loading="record.updateStatusLoading">
                <template #checkedChildren>
                  {{sys_status.filter(item => item.value === text)[0]?.label}}
                </template>
                <template #unCheckedChildren>
                  {{sys_status.filter(item => item.value === text)[0]?.label}}
                </template>
              </a-switch>
            </template>
            <template v-if="column.key === 'action'">
              <a-popconfirm title="删除后不可恢复，是否删除？"
                          placement="bottomRight"
                          ok-text="确 定"
                          cancel-text="取 消"
                          @confirm="handleDeleteComment(record.id)"
              >
                <a-button type="link" danger size="small">
                  <template #icon>
                    <DeleteOutlined />
                  </template>
                  删除
                </a-button>
              </a-popconfirm>
            </template>
          </template>
          <template #footer>
            <a-flex justify="flex-end">
              <a-pagination v-model:current="commentQuery.pageNum"
                          v-model:page-size="commentQuery.pageSize"
                          show-size-changer
                          :total="commentTotal"
                          :show-total="(total:number) => `共 ${total} 条`"
                          @change="loadComments"
              />
            </a-flex>
          </template>
        </a-table>
      </a-card>
    </a-flex>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import dayjs from 'dayjs';
import { 
  ArrowLeftOutlined, 
  DeleteOutlined, 
  CheckOutlined, 
  StopOutlined, 
  VerticalAlignTopOutlined, 
  VerticalAlignBottomOutlined,
  UserOutlined,
  ClockCircleOutlined,
  EyeOutlined,
  LikeOutlined,
  CommentOutlined
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { CarPost, CarPostCommentDTO, CarPostCommentVO } from '@/api/system/forum/type/CarPost';
import * as carPostApi from '@/api/system/forum/CarPost';
import * as carCommentApi from '@/api/system/forum/CarComment';

const route = useRoute();
const router = useRouter();

// 字典数据
const sys_status = [
  { value: '0', label: '正常' },
  { value: '1', label: '停用' }
];

// 评论列表列定义
const commentColumn = [
  { title: '评论内容', dataIndex: 'content', key: 'content', width: 300, ellipsis: true },
  { title: '评论用户', dataIndex: 'username', key: 'username', width: 120 },
  { title: '回复对象', dataIndex: 'parentUsername', key: 'parentUsername', width: 120 },
  { title: '点赞次数', dataIndex: 'likeCount', key: 'likeCount', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '评论时间', dataIndex: 'createTime', key: 'createTime', width: 160 },
  { title: '操作', key: 'action', width: 120 }
];

// 状态定义
const loading = ref(false);
const commentLoading = ref(false);
const postDetail = reactive<CarPost>({});
const postId = ref(route.params.id as string);

// 评论相关数据
const commentQuery = reactive<CarPostCommentDTO>({
  pageNum: 1,
  pageSize: 10,
  postId: postId.value
});
const commentList = ref<CarPostCommentVO[]>([]);
const commentTotal = ref(0);

// 初始化
onMounted(() => {
  getPostDetail();
  loadComments();
});

// 返回帖子列表
const goBack = () => {
  router.push('/system/forum');
};

// 获取帖子详情
const getPostDetail = async () => {
  loading.value = true;
  try {
    const res = await carPostApi.queryById(postId.value);
    if (res) {
      Object.assign(postDetail, res);
    }
  } catch (error) {
    console.error('获取帖子详情失败', error);
    message.error('获取帖子详情失败');
  } finally {
    loading.value = false;
  }
};

// 加载评论
const loadComments = async () => {
  try {
    commentLoading.value = true;
    
    if (!postId.value) {
      return;
    }
    
    const res = await carCommentApi.queryByPostId(postId.value, commentQuery);
    if (res && res.data && res.data.records) {
      commentList.value = res.data.records.map(item => ({
        ...item,
        statusIsNormal: item.status === '0',
        updateStatusLoading: false
      }));
      commentTotal.value = res.data.total || 0;
    }
  } catch (error) {
    console.error('加载评论失败', error);
    message.error('加载评论失败');
  } finally {
    commentLoading.value = false;
  }
};

// 删除帖子
const handleDelete = async () => {
  try {
    await carPostApi.deleteByIds([postId.value]);
    message.success('删除成功');
    router.push('/system/forum');
  } catch (error) {
    console.error('删除帖子失败', error);
    message.error('删除帖子失败');
  }
};

// 修改帖子状态
const handleUpdateStatus = async () => {
  try {
    const newStatus = postDetail.status === '0' ? '1' : '0';
    await carPostApi.updateStatus(postId.value, newStatus);
    message.success('状态修改成功');
    
    // 刷新详情
    getPostDetail();
  } catch (error) {
    console.error('修改状态失败', error);
    message.error('修改状态失败');
  }
};

// 修改置顶状态
const handleUpdateTop = async () => {
  try {
    const newIsTop = postDetail.isTop === '0' ? '1' : '0';
    await carPostApi.updateTop(postId.value, newIsTop);
    message.success('置顶状态修改成功');
    
    // 刷新详情
    getPostDetail();
  } catch (error) {
    console.error('修改置顶状态失败', error);
    message.error('修改置顶状态失败');
  }
};

// 修改评论状态
const handleUpdateCommentStatus = async (event: MouseEvent, id: string, currentStatus: string) => {
  try {
    const newStatus = currentStatus === '0' ? '1' : '0';
    await carCommentApi.updateStatus(id, newStatus);
    message.success('状态修改成功');
    
    // 刷新列表
    await loadComments();
  } catch (error) {
    console.error('修改评论状态失败', error);
    message.error('修改评论状态失败');
    
    // 回滚状态
    const comment = commentList.value.find(item => item.id === id);
    if (comment) {
      comment.statusIsNormal = !comment.statusIsNormal;
      comment.updateStatusLoading = false;
    }
  }
};

// 删除评论
const handleDeleteComment = async (id: string) => {
  try {
    if (!id) {
      message.warning('请选择要删除的记录');
      return;
    }
    
    await carCommentApi.deleteByIds([id]);
    message.success('删除成功');
    
    // 刷新评论列表
    await loadComments();
    
    // 刷新帖子详情以更新评论数
    await getPostDetail();
  } catch (error) {
    console.error('删除评论失败', error);
    message.error('删除评论失败');
  }
};
</script>

<style scoped>
.post-content {
  line-height: 1.8;
  font-size: 14px;
}
</style> 