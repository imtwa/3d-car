<template>
  <div>
    <a-flex :gap="16" vertical>
      <!-- 筛选条件 -->
      <a-card :style="{border: 'none'}" :body-style="{'padding-bottom': '0'}">
        <a-form :colon="false">
          <a-row :gutter="16">
            <a-col>
              <a-form-item label="评论内容">
                <a-input placeholder="请输入评论内容" v-model:value="commentQuery.content" allowClear/>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="帖子标题">
                <a-input placeholder="请输入帖子标题" v-model:value="commentQuery.postTitle" allowClear/>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="用户名">
                <a-input placeholder="请输入用户名" v-model:value="commentQuery.username" allowClear/>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="状态">
                <a-select placeholder="请选择" v-model:value="commentQuery.status" allowClear>
                  <a-select-option :value="item.value" v-for="item in sys_status">{{item.label}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="创建时间">
                <a-range-picker allowClear v-model:value="commentQuery.createTimeList"/>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item>
                <a-space size="small">
                  <a-button type="primary" @click="handleQueryPage" :loading="queryLoading">
                    <template #icon>
                      <SearchOutlined />
                    </template>
                    查 询
                  </a-button>
                  <a-button @click="resetPage" :loading="queryLoading">
                    <template #icon>
                      <RedoOutlined />
                    </template>
                    重 置
                  </a-button>
                </a-space>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </a-card>
      
      <!-- 评论表格 -->
      <a-table :columns="commentColumn"
               :data-source="commentList"
               :loading="queryLoading"
               :pagination="false"
               :row-selection="commentRowSelectionType"
               row-key="id"
               :scroll="{x: 1200}"
      >
        <template #title>
          <a-flex :gap="8" wrap="wrap" >
            <a-popconfirm title="删除后不可恢复，是否删除？"
                          :open="openDeletePopconfirm"
                          ok-text="确 定"
                          cancel-text="取 消"
                          @confirm="handleDelete(undefined)"
                          @cancel="closePopconfirm"
                          @open-change="(open: boolean) => !open ? closePopconfirm(): ''"
            >
              <a-button danger @click="openPopconfirm">
                <template #icon>
                  <DeleteOutlined />
                </template>
                删 除
                <span v-if="selectedIds && selectedIds.length > 0" style="margin-left: 4px"> {{selectedIds.length}} 项</span>
              </a-button>
            </a-popconfirm>
            <a-button ghost type="primary" @click="handleExportExcel">
              <template #icon>
                <ExportOutlined />
              </template>
              导出
            </a-button>
          </a-flex>
        </template>
        <template #bodyCell="{column,record,text}">
          <template v-if="column.key === 'content'">
            <a-tooltip :title="text" placement="topLeft">
              <span>{{ text }}</span>
            </a-tooltip>
          </template>
          <template v-if="column.key === 'postTitle'">
            <a-tooltip :title="text" placement="topLeft">
              <span>{{ text }}</span>
            </a-tooltip>
          </template>
          <template v-if="column.key === 'createTime'">
            {{text ? dayjs(text).format('YYYY-MM-DD HH:mm') : '-'}}
          </template>
          <template v-if="column.key === 'status'">
            <a-switch v-model:checked="record.statusIsNormal"
                      @change="(checked: boolean | string | number, event: MouseEvent) => handleUpdateStatus(event, record.id, text)"
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
            <a-button type="link" size="small" @click="(event: MouseEvent) => viewPost(event, record.postId)">
              <template #icon>
                <EyeOutlined />
              </template>
              查看帖子
            </a-button>
            <a-divider type="vertical"/>
            <a-popconfirm title="删除后不可恢复，是否删除？"
                          placement="bottomRight"
                          ok-text="确 定"
                          cancel-text="取 消"
                          @confirm="handleDelete(record.id)"
            >
              <a-button type="link" danger size="small" @click="(event: MouseEvent) => event.stopPropagation()">
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
                          @change="initPage"
            />
          </a-flex>
        </template>
      </a-table>
    </a-flex>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import dayjs from 'dayjs';
import type { TableProps } from 'ant-design-vue';
import { 
  DeleteOutlined, 
  SearchOutlined, 
  RedoOutlined, 
  EyeOutlined, 
  ExportOutlined 
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { CarPostCommentDTO, CarPostCommentVO } from '@/api/system/forum/type/CarPost';
import * as carCommentApi from '@/api/system/forum/CarComment';
import { useRoute, useRouter } from 'vue-router';

// 字典数据
const sys_status = [
  { value: '0', label: '正常' },
  { value: '1', label: '停用' }
];

// 表格列定义
const commentColumn = [
  { title: '评论内容', dataIndex: 'content', key: 'content', width: 200, ellipsis: true },
  { title: '帖子标题', dataIndex: 'postTitle', key: 'postTitle', width: 200, ellipsis: true },
  { title: '评论用户', dataIndex: 'username', key: 'username', width: 120 },
  // { title: '回复对象', dataIndex: 'parentUsername', key: 'parentUsername', width: 120 },
  { title: '点赞次数', dataIndex: 'likeCount', key: 'likeCount', width: 100 },
  // { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '评论时间', dataIndex: 'createTime', key: 'createTime', width: 160 },
  { title: '操作', key: 'action', width: 180, fixed: 'right' }
];

// 查询条件和数据列表
const queryLoading = ref(false);
const commentQuery = reactive<CarPostCommentDTO>({
  pageNum: 1,
  pageSize: 10
});
const commentList = ref<CarPostCommentVO[]>([]);
const commentTotal = ref(0);

// 选择行相关
const selectedIds = ref<string[]>([]);
const openDeletePopconfirm = ref(false);

const route = useRoute();
const router = useRouter();

// 初始化
onMounted(() => {
  // 检查是否有查询参数
  if (route.query.postId) {
    commentQuery.postId = route.query.postId as string;
    commentQuery.postTitle = route.query.postTitle as string;
  }
  initPage();
});

// 表格行选择配置
const commentRowSelectionType: TableProps['rowSelection'] = {
  onChange: (selectedRowKeys: any[], selectedRows: any[]) => {
    selectedIds.value = selectedRowKeys as string[];
  }
};

// 初始化数据
const initPage = async () => {
  await handleQueryPage();
};

// 查询数据
const handleQueryPage = async () => {
  try {
    queryLoading.value = true;
    
    // 处理日期范围查询
    if (commentQuery.createTimeList && commentQuery.createTimeList.length === 2) {
      commentQuery.createTimeStart = commentQuery.createTimeList[0]?.format('YYYY-MM-DD 00:00:00');
      commentQuery.createTimeEnd = commentQuery.createTimeList[1]?.format('YYYY-MM-DD 23:59:59');
    } else {
      commentQuery.createTimeStart = undefined;
      commentQuery.createTimeEnd = undefined;
    }
    
    const res = await carCommentApi.queryPage(commentQuery);
    if (res && res.data && res.data.records) {
      commentList.value = res.data.records.map(item => ({
        ...item,
        statusIsNormal: item.status === '0',
        updateStatusLoading: false
      }));
      commentTotal.value = res.data.total || 0;
    }
  } catch (error) {
    console.error('查询评论列表失败', error);
    message.error('查询评论列表失败');
  } finally {
    queryLoading.value = false;
  }
};

// 重置查询条件
const resetPage = () => {
  Object.assign(commentQuery, {
    content: undefined,
    postTitle: undefined,
    username: undefined,
    status: undefined,
    createTimeList: undefined,
    createTimeStart: undefined,
    createTimeEnd: undefined,
    postId: undefined,
    pageNum: 1,
    pageSize: 10
  });
  handleQueryPage();
};

// 打开确认删除弹窗
const openPopconfirm = () => {
  if (selectedIds.value.length === 0) {
    message.warning('请至少选择一条记录');
    return;
  }
  openDeletePopconfirm.value = true;
};

// 关闭确认删除弹窗
const closePopconfirm = () => {
  openDeletePopconfirm.value = false;
};

// 处理删除操作
const handleDelete = async (id?: string) => {
  try {
    if (id) {
      // 删除单个评论
      await carCommentApi.deleteById(id);
    } else {
      // 批量删除评论
      const ids = selectedIds.value;
      if (!ids || ids.length === 0) {
        message.warning('请选择要删除的记录');
        return;
      }
      
      await carCommentApi.deleteByIds(ids);
    }
    message.success('删除成功');
    
    // 刷新数据
    handleQueryPage();
    // 清空选择
    selectedIds.value = [];
  } catch (error) {
    console.error('删除评论失败', error);
    message.error('删除评论失败');
  } finally {
    closePopconfirm();
  }
};

// 处理修改状态
const handleUpdateStatus = async (event: MouseEvent, id: string, currentStatus: string) => {
  event.stopPropagation();
  try {
    const newStatus = currentStatus === '0' ? '1' : '0';
    await carCommentApi.updateStatus(id, newStatus);
    message.success('状态修改成功');
    
    // 刷新列表
    handleQueryPage();
  } catch (error) {
    console.error('修改状态失败', error);
    message.error('修改状态失败');
    
    // 回滚状态
    const comment = commentList.value.find(item => item.id === id);
    if (comment) {
      comment.statusIsNormal = !comment.statusIsNormal;
      comment.updateStatusLoading = false;
    }
  }
};

// 查看帖子
const viewPost = async (event: MouseEvent, postId?: string) => {
  event.stopPropagation();
  if (!postId) {
    message.warning('帖子ID不存在');
    return;
  }
  
  // 跳转到帖子管理页面
  router.push({
    path: '/system/forum',
    query: {
      postId: postId
    }
  });
};

// 导出Excel
const handleExportExcel = async () => {
  try {
    // 处理日期范围查询
    if (commentQuery.createTimeList && commentQuery.createTimeList.length === 2) {
      commentQuery.createTimeStart = commentQuery.createTimeList[0]?.format('YYYY-MM-DD 00:00:00');
      commentQuery.createTimeEnd = commentQuery.createTimeList[1]?.format('YYYY-MM-DD 23:59:59');
    } else {
      commentQuery.createTimeStart = undefined;
      commentQuery.createTimeEnd = undefined;
    }
    
    const res = await carCommentApi.exportExcel(commentQuery);
    if (!res) {
      message.error('导出失败');
      return;
    }
    
    // 创建下载链接
    const blob = res instanceof Blob ? res : new Blob([res]);
    const downloadLink = document.createElement('a');
    const url = window.URL.createObjectURL(blob);
    downloadLink.href = url;
    downloadLink.download = '评论数据.xlsx';
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
    window.URL.revokeObjectURL(url);
    
    message.success('导出成功');
  } catch (error) {
    console.error('导出失败', error);
    message.error('导出失败');
  }
};
</script> 