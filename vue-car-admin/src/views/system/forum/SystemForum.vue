<template>
  <div>
    <a-flex :gap="16" vertical>
      <!-- 筛选条件 -->
      <a-card :style="{border: 'none'}" :body-style="{'padding-bottom': '0'}">
        <a-form :colon="false">
          <a-row :gutter="16">
            <a-col>
              <a-form-item label="标题">
                <a-input placeholder="请输入帖子标题" v-model:value="postQuery.title" allowClear/>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="用户名">
                <a-input placeholder="请输入用户名" v-model:value="postQuery.username" allowClear/>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="状态">
                <a-select placeholder="请选择" v-model:value="postQuery.status" allowClear>
                  <a-select-option :value="item.value" v-for="item in sys_status">{{item.label}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="置顶">
                <a-select placeholder="请选择" v-model:value="postQuery.isTop" allowClear>
                  <a-select-option :value="item.value" v-for="item in top_status">{{item.label}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="创建时间">
                <a-range-picker allowClear v-model:value="postQuery.createTimeList"/>
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
      
      <!-- 帖子表格 -->
      <a-table :columns="postColumn"
               :data-source="postList"
               :loading="queryLoading"
               :pagination="false"
               :row-selection="postRowSelectionType"
               row-class-name="hover-cursor-pointer"
               :custom-row="handleRowClick"
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
          <template v-if="column.key === 'title'">
            <a-tooltip :title="record.content" placement="topLeft">
              <span>{{ text }}</span>
            </a-tooltip>
          </template>
          <template v-if="column.key === 'username'">
            {{ record.user?.username || '-' }}
          </template>
          <template v-if="column.key === 'createTime' || column.key === 'updateTime'">
            {{text ? dayjs(text).format('YYYY-MM-DD HH:mm') : '-'}}
          </template>
          <template v-if="column.key === 'isTop'">
            <a-switch v-model:checked="record.isTopFlag"
                      @change="(checked: boolean | string | number, event: MouseEvent) => handleUpdateTop(event, record.id, text)"
                      @click="(checked: boolean | string | number, event: MouseEvent) => { event.stopPropagation(); record.updateTopLoading = true }"
                      :loading="record.updateTopLoading">
              <template #checkedChildren>
                {{top_status.filter(item => item.value === (typeof text === 'number' ? text.toString() : text))[0]?.label}}
              </template>
              <template #unCheckedChildren>
                {{top_status.filter(item => item.value === (typeof text === 'number' ? text.toString() : text))[0]?.label}}
              </template>
            </a-switch>
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
            <a-button type="link" size="small" @click="(event: MouseEvent) => navigateToPostDetail(event, record.id)">
              <template #icon>
                <EyeOutlined />
              </template>
              查看
            </a-button>
            <a-divider type="vertical"/>
            <a-button type="link" size="small" @click="(event: MouseEvent) => navigateToCommentManagement(event, record)">
              <template #icon>
                <CommentOutlined />
              </template>
              评论
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
            <a-pagination v-model:current="postQuery.pageNum"
                          v-model:page-size="postQuery.pageSize"
                          show-size-changer
                          :total="postTotal"
                          :show-total="(total:number) => `共 ${total} 条`"
                          @change="initPage"
            />
          </a-flex>
        </template>
      </a-table>
    </a-flex>

    <!-- 帖子详情弹窗 -->
    <a-modal v-model:open="postDetailModalActive" width="800px" :footer="null">
      <template #title>
        <div>帖子详情</div>
      </template>
      <div class="post-detail-table">
        <table style="width: 100%; border-collapse: collapse;">
          <tr>
            <td style="width: 20%; padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">标题</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">{{ postDetail.title }}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">发布者</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">{{ postDetail.username || postDetail.user?.username }}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">内容</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">
              <div v-html="postDetail.content" style="max-height: 300px; overflow-y: auto;"></div>
            </td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">浏览次数</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">{{ postDetail.viewCount }}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">点赞次数</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">{{ postDetail.likeCount }}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">评论次数</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">{{ postDetail.commentCount }}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">是否置顶</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">
              {{ postDetail.isTop === '1' || Number(postDetail.isTop) === 1 ? '是' : '否' }}
            </td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">状态</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">{{ postDetail.status === '0' ? '正常' : '停用' }}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">创建时间</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">{{ postDetail.createTime ? dayjs(postDetail.createTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}</td>
          </tr>
          <tr>
            <td style="padding: 8px; border: 1px solid #f0f0f0; background: #fafafa; font-weight: bold;">最后更新时间</td>
            <td style="padding: 8px; border: 1px solid #f0f0f0;">{{ postDetail.updateTime ? dayjs(postDetail.updateTime).format('YYYY-MM-DD HH:mm:ss') : '-' }}</td>
          </tr>
        </table>
      </div>
    </a-modal>
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
  CommentOutlined, 
  ExportOutlined 
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { CarPost, CarPostDTO, CarPostVO } from '@/api/system/forum/type/CarPost';
import * as carPostApi from '@/api/system/forum/CarPost';
import { useRouter } from 'vue-router';

// 字典数据
const sys_status = [
  { value: '0', label: '正常' },
  { value: '1', label: '停用' }
];

const top_status = [
  { value: '0', label: '普通' },
  { value: '1', label: '置顶' }
];

// 表格列定义
const postColumn = [
  { title: '标题', dataIndex: 'title', key: 'title', width: 200, ellipsis: true },
  { title: '发布用户', dataIndex: 'username', key: 'username', width: 120 },
  { title: '浏览次数', dataIndex: 'viewCount', key: 'viewCount', width: 100 },
  { title: '点赞次数', dataIndex: 'likeCount', key: 'likeCount', width: 100 },
  { title: '评论次数', dataIndex: 'commentCount', key: 'commentCount', width: 100 },
  { title: '置顶', dataIndex: 'isTop', key: 'isTop', width: 100 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 160 },
  { title: '操作', key: 'action', fixed: 'right', width: 240 }
];

// 查询条件和数据列表
const queryLoading = ref(false);
const postQuery = reactive<CarPostDTO>({
  pageNum: 1,
  pageSize: 10
});
const postList = ref<CarPostVO[]>([]);
const postTotal = ref(0);

// 选择行相关
const selectedIds = ref<string[]>([]);
const openDeletePopconfirm = ref(false);

// 帖子详情相关
const postDetailModalActive = ref(false);
const postDetail = reactive<CarPost>({});

const router = useRouter();

// 初始化
onMounted(() => {
  initPage();
});

// 表格行选择配置
const postRowSelectionType: TableProps['rowSelection'] = {
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
    if (postQuery.createTimeList && postQuery.createTimeList.length === 2) {
      postQuery.createTimeStart = postQuery.createTimeList[0]?.format('YYYY-MM-DD 00:00:00');
      postQuery.createTimeEnd = postQuery.createTimeList[1]?.format('YYYY-MM-DD 23:59:59');
    } else {
      postQuery.createTimeStart = undefined;
      postQuery.createTimeEnd = undefined;
    }
    
    const res = await carPostApi.queryPage(postQuery);
    if (res && res.data && res.data.records) {
      postList.value = res.data.records.map(item => {
        // 确保isTop和status是字符串类型
        const isTop = item.isTop?.toString() || '0';
        const status = item.status?.toString() || '0';
        
        return {
          ...item,
          isTop: isTop,
          status: status,
          statusIsNormal: status === '0',
          updateStatusLoading: false,
          isTopFlag: isTop === '1' || Number(item.isTop) === 1,
          updateTopLoading: false
        };
      });
      postTotal.value = res.data.total || 0;
    }
  } catch (error) {
    console.error('查询帖子列表失败', error);
    message.error('查询帖子列表失败');
  } finally {
    queryLoading.value = false;
  }
};

// 重置查询条件
const resetPage = () => {
  Object.assign(postQuery, {
    title: undefined,
    username: undefined,
    status: undefined,
    isTop: undefined,
    createTimeList: undefined,
    createTimeStart: undefined,
    createTimeEnd: undefined,
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
    const ids = id ? [id] : selectedIds.value;
    if (!ids || ids.length === 0) {
      message.warning('请选择要删除的记录');
      return;
    }
    
    await carPostApi.deleteByIds(ids);
    message.success('删除成功');
    
    // 刷新数据
    handleQueryPage();
    // 清空选择
    selectedIds.value = [];
  } catch (error) {
    console.error('删除帖子失败', error);
    message.error('删除帖子失败');
  } finally {
    closePopconfirm();
  }
};

// 处理修改状态
const handleUpdateStatus = async (event: MouseEvent, id: string, currentStatus: string) => {
  event.stopPropagation();
  try {
    const newStatus = currentStatus === '0' ? '1' : '0';
    await carPostApi.updateStatus(id, newStatus);
    message.success('状态修改成功');
    
    // 刷新列表
    handleQueryPage();
  } catch (error) {
    console.error('修改状态失败', error);
    message.error('修改状态失败');
    
    // 回滚状态
    const post = postList.value.find(item => item.id === id);
    if (post) {
      post.statusIsNormal = !post.statusIsNormal;
      post.updateStatusLoading = false;
    }
  }
};

// 处理修改置顶状态
const handleUpdateTop = async (event: MouseEvent, id: string, currentIsTop: string) => {
  event.stopPropagation();
  try {
    // 调用后端toggle-top接口，不需要传递当前状态
    await carPostApi.updateTop(id);
    message.success('置顶状态修改成功');
    
    // 刷新列表
    handleQueryPage();
  } catch (error) {
    console.error('修改置顶状态失败', error);
    message.error('修改置顶状态失败');
    
    // 回滚状态
    const post = postList.value.find(item => item.id === id);
    if (post) {
      post.isTopFlag = !post.isTopFlag;
      post.updateTopLoading = false;
    }
  }
};

// 点击行操作
const handleRowClick = (record: CarPostVO) => {
  return {
    onClick: () => {
      navigateToPostDetail(new MouseEvent('click'), record.id as string);
    }
  };
};

// 导航到帖子详情页
const navigateToPostDetail = async (event: MouseEvent, id: string) => {
  event.stopPropagation();
  try {
    // 先清空之前的详情数据，重置为空对象
    for (const key in postDetail) {
      if (Object.prototype.hasOwnProperty.call(postDetail, key)) {
        delete (postDetail as any)[key];
      }
    }
    
    // 根据行数据直接获取详情，避免额外请求
    const rowData = postList.value.find(item => item.id === id);
    if (rowData) {
      // 直接使用列表中的数据
      Object.assign(postDetail, rowData);
      postDetailModalActive.value = true;
      console.log('从行数据加载详情:', postDetail);
    } else {
      // 如果找不到行数据，再从API获取
      const res = await carPostApi.queryById(id);
      if (res) {
        Object.assign(postDetail, res);
        postDetailModalActive.value = true;
        console.log('从API加载详情:', postDetail);
      } else {
        message.error('获取帖子详情失败');
      }
    }
  } catch (error) {
    console.error('获取帖子详情失败', error);
    message.error('获取帖子详情失败');
  }
};

// 导航到评论管理页面
const navigateToCommentManagement = (event: MouseEvent, record: CarPostVO) => {
  event.stopPropagation();
  router.push({
    path: '/system/comment',
    query: {
      postId: record.id,
      postTitle: record.title
    }
  });
};

// 导出Excel
const handleExportExcel = async () => {
  try {
    // 处理日期范围查询
    if (postQuery.createTimeList && postQuery.createTimeList.length === 2) {
      postQuery.createTimeStart = postQuery.createTimeList[0]?.format('YYYY-MM-DD 00:00:00');
      postQuery.createTimeEnd = postQuery.createTimeList[1]?.format('YYYY-MM-DD 23:59:59');
    } else {
      postQuery.createTimeStart = undefined;
      postQuery.createTimeEnd = undefined;
    }
    
    const res = await carPostApi.exportExcel(postQuery);
    if (!res) {
      message.error('导出失败');
      return;
    }
    
    // 创建下载链接
    const blob = res instanceof Blob ? res : new Blob([res]);
    const downloadLink = document.createElement('a');
    const url = window.URL.createObjectURL(blob);
    downloadLink.href = url;
    downloadLink.download = '帖子数据.xlsx';
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
