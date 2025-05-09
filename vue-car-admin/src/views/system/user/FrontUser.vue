<template>
  <div>
    <a-flex :gap="16" vertical>
      <!--      筛选条件-->
      <a-card :style="{border: 'none'}" :body-style="{'padding-bottom': '0'}">
        <a-form :colon="false">
          <a-row :gutter="16">
            <a-col>
              <a-form-item label="用户名">
                <a-input placeholder="请输入用户名" v-model:value="userQuery.username" allowClear/>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="昵称">
                <a-input placeholder="请输入昵称" v-model:value="userQuery.nickname" allowClear/>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="状态">
                <a-select placeholder="请选择 " v-model:value="userQuery.status" allowClear>
                  <a-select-option :value="item.value" v-for="item in sys_status">{{item.label}}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col>
              <a-form-item label="创建时间">
                <a-range-picker allowClear v-model:value="userQuery.createTimeList"/>
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
      <!--    表格-->
      <a-table :columns="userColumn"
               :data-source="userList"
               :loading="queryLoading"
               :pagination="false"
               :row-selection="userRowSelectionType"
               row-class-name="hover-cursor-pointer"
               :custom-row="handleRowClick"
               row-key="id"
               :scroll="{x: 1200}"
      >
        <template #title>
          <a-flex :gap="8" wrap="wrap" >
            <a-button type="primary" @click="() => { resetForm(); handleModelStatus('新增前台用户'); }">
              <template #icon>
                <PlusOutlined />
              </template>
              新 增
            </a-button>
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
          <template v-if="column.key === 'createTime' || column.key === 'lastLoginTime'">
            {{text ? dayjs(text).format('YYYY-MM-DD HH:mm') : '-'}}
          </template>
          <template v-if="column.key === 'registerType'">
            <dict-tag :dict-data-option="user_register_type" :dict-data-value="text"></dict-tag>
          </template>
          <template v-if="column.key === 'status'">
            <a-switch v-model:checked="record.statusIsNormal"
                      @change="(checked: boolean | string | number, event: MouseEvent) => handleUpdateStatus(event,record.id, text)"
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
            <a-button type="link" size="small" @click="(event: MouseEvent) => getUserInfo(event, record.id)">
              <template #icon>
                <EditOutlined />
              </template>
              编辑
            </a-button>
            <a-divider type="vertical"/>
            <a-button type="link" size="small" @click="(event: MouseEvent) => handleOpenResetPasswordModel(event, record)">
              <template #icon>
                <KeyOutlined />
              </template>
              重置密码
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
            <a-pagination v-model:current="userQuery.pageNum"
                          v-model:page-size="userQuery.pageSize"
                          show-size-changer
                          :total="userTotal"
                          :show-total="(total:number) => `共 ${total} 条`"
                          @change="initPage"
            />
          </a-flex>
        </template>
      </a-table>
    </a-flex>

    <!-- 用户编辑弹窗 -->
    <a-modal v-model:open="modalActive.open" @cancel="resetForm" :afterClose="resetForm" @ok="handleSubmit">
      <template #title>
        <div style="margin-bottom: 24px">
          <a-typography-title :level="4">{{ modalActive.title }}</a-typography-title>
        </div>
      </template>
      <a-form ref="formRef" :rules="userRules" :model="carUserDTO" :label-col="{ span: 4 }" :colon="false">
        <a-form-item label="用户名" name="username" :wrapper-col="{ span: 16 }">
          <a-input v-model:value="carUserDTO.username" placeholder="用于用户登陆" :maxlength="30" show-count allowClear/>
        </a-form-item>
        <a-form-item label="密码" name="password" :wrapper-col="{ span: 16 }" v-if="!carUserDTO.id">
          <a-input-password v-model:value="carUserDTO.password" placeholder="请输入密码" allowClear/>
        </a-form-item>
        <a-form-item label="状态">
          <a-radio-group v-model:value="carUserDTO.status">
            <a-radio :value="item.value" v-for="item in sys_status">{{ item.label }}</a-radio>
          </a-radio-group>
        </a-form-item>
        <a-form-item label="备注" :wrapper-col="{ span: 16 }">
          <a-textarea v-model:value="carUserDTO.remark" placeholder="请输入备注信息" :rows="3" :maxlength="200" show-count allowClear/>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 重置密码弹窗 -->
    <a-modal v-model:open="resetPasswordModelActive" 
             title="重置密码" 
             @ok="handleResetPassword" 
             :confirmLoading="resetPasswordLoading">
      <a-form ref="resetPasswordFormRef" :rules="resetPasswordRules" :model="resetPasswordForm">
        <a-form-item label="新密码" name="password">
          <a-input-password v-model:value="resetPasswordForm.password" allowClear/>
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password v-model:value="resetPasswordForm.confirmPassword" allowClear/>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, toRefs, onMounted } from 'vue';
import dayjs from 'dayjs';
import type { Rule } from 'ant-design-vue/es/form';
import type { TableProps } from 'ant-design-vue';
import DictTag from '@/components/dict-tag/index.vue';
import { 
  DeleteOutlined, 
  SearchOutlined, 
  RedoOutlined, 
  EditOutlined, 
  PlusOutlined, 
  KeyOutlined, 
  ExportOutlined 
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { CarUser, CarUserDTO, CarUserVO } from '@/api/system/user/type/CarUser';
import * as carUserApi from '@/api/system/user/CarUser';

// 字典数据
const user_gender = [
  { value: '0', label: '男' },
  { value: '1', label: '女' },
  { value: '2', label: '保密' }
];

const sys_status = [
  { value: '0', label: '正常' },
  { value: '1', label: '停用' }
];

const user_register_type = [
  { value: '0', label: '管理员新增' },
  { value: '1', label: '用户自助注册' }
];

// 表格列定义
const userColumn = [
  { title: '用户名', dataIndex: 'username', key: 'username', width: 120 },
  // { title: '昵称', dataIndex: 'nickname', key: 'nickname', width: 120 },
  // { title: '性别', dataIndex: 'gender', key: 'gender', width: 80, 
  //   customRender: ({text}: {text: string}) => 
  //     user_gender.find(item => item.value === text)?.label || '保密' 
  // },
  // { title: '手机号码', dataIndex: 'phoneNumber', key: 'phoneNumber', width: 130 },
  // { title: '邮箱', dataIndex: 'email', key: 'email', width: 180 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
  { title: '注册类型', dataIndex: 'registerType', key: 'registerType', width: 120 },
  { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 160 },
  { title: '最后登录时间', dataIndex: 'lastLoginTime', key: 'lastLoginTime', width: 160 },
  { title: '操作', key: 'action', fixed: 'right', width: 220 }
];

// 查询条件和数据列表
const queryLoading = ref(false);
const userQuery = reactive<CarUserDTO>({
  pageNum: 1,
  pageSize: 10
});
const userList = ref<CarUserVO[]>([]);
const userTotal = ref(0);

// 选择行相关
const selectedIds = ref<string[]>([]);
const openDeletePopconfirm = ref(false);

// 表单验证规则
const userRules: Record<string, Rule[]> = {
  username: [
    { required: true, message: '请输入用户名' },
    { min: 3, max: 30, message: '用户名长度必须在3-30之间' },
    { pattern: /^[a-zA-Z0-9_-]+$/, message: '用户名只能包含字母、数字、下划线和短横线' }
  ],
  password: [
    { required: true, message: '请输入密码' },
    { min: 6, max: 20, message: '密码长度必须在6-20之间' }
  ]
};

// 表单数据
const carUserDTO = reactive<CarUserDTO>({
  registerType: '0', // 管理员新增
  status: '0', // 默认正常状态
  gender: '2' // 默认保密
});

// 弹窗状态
const modalActive = reactive({
  open: false,
  title: '',
  closeModal: () => {
    modalActive.open = false;
  }
});

// 重置密码相关
const resetPasswordModelActive = ref(false);
const resetPasswordLoading = ref(false);
const resetPasswordForm = reactive({
  password: '',
  confirmPassword: '',
  userId: ''
});
const resetPasswordRules: Record<string, Rule[]> = {
  password: [
    { required: true, message: '请输入新密码' },
    { min: 6, max: 20, message: '密码长度必须在6-20之间' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    { validator: validateConfirmPassword }
  ]
};
const resetPasswordFormRef = ref();

// 初始化
onMounted(() => {
  initPage();
});

// 表格行选择配置
const userRowSelectionType: TableProps['rowSelection'] = {
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
    if (userQuery.createTimeList && userQuery.createTimeList.length === 2) {
      userQuery.createTimeStart = userQuery.createTimeList[0]?.format('YYYY-MM-DD 00:00:00');
      userQuery.createTimeEnd = userQuery.createTimeList[1]?.format('YYYY-MM-DD 23:59:59');
    } else {
      userQuery.createTimeStart = undefined;
      userQuery.createTimeEnd = undefined;
    }
    
    const res = await carUserApi.queryPage(userQuery);
    if (res && res.data && res.data.records) {
      userList.value = res.data.records.map(item => ({
        ...item,
        statusIsNormal: item.status === '0',
        updateStatusLoading: false
      }));
      userTotal.value = res.data.total || 0;
    }
  } catch (error) {
    console.error('查询前台用户失败', error);
    message.error('查询前台用户失败');
  } finally {
    queryLoading.value = false;
  }
};

// 重置查询条件
const resetPage = () => {
  Object.assign(userQuery, {
    username: undefined,
    nickname: undefined,
    status: undefined,
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
    
    await carUserApi.deleteByIds(ids);
    message.success('删除成功');
    
    // 刷新数据
    handleQueryPage();
    // 清空选择
    selectedIds.value = [];
  } catch (error) {
    console.error('删除前台用户失败', error);
    message.error('删除前台用户失败');
  } finally {
    closePopconfirm();
  }
};

// 处理修改状态
const handleUpdateStatus = async (event: MouseEvent, id: string, currentStatus: string) => {
  event.stopPropagation();
  try {
    const newStatus = currentStatus === '0' ? '1' : '0';
    await carUserApi.updateStatus(id, newStatus);
    message.success('状态修改成功');
    
    // 刷新列表
    handleQueryPage();
  } catch (error) {
    console.error('修改状态失败', error);
    message.error('修改状态失败');
    
    // 回滚状态
    const user = userList.value.find(item => item.id === id);
    if (user) {
      user.statusIsNormal = !user.statusIsNormal;
      user.updateStatusLoading = false;
    }
  }
};

// 打开编辑弹窗
const handleModelStatus = (title: string) => {
  resetPasswordModelActive.value = false;
  modalActive.open = true;
  modalActive.title = title;
};

// 获取用户信息
const getUserInfo = async (event: MouseEvent, id: string) => {
  event.stopPropagation();
  try {
    resetForm();
    
    const res = await carUserApi.queryById(id);
    if (res) {
      Object.assign(carUserDTO, res);
      handleModelStatus('编辑前台用户');
    }
  } catch (error) {
    console.error('获取用户信息失败', error);
    message.error('获取用户信息失败');
  }
};

// 提交表单
const formRef = ref();
const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    
    await carUserApi.save(carUserDTO);
    message.success(carUserDTO.id ? '修改成功' : '新增成功');
    
    // 关闭弹窗
    modalActive.open = false;
    // 刷新数据
    handleQueryPage();
  } catch (error) {
    console.error('保存前台用户失败', error);
    message.error('保存失败，请检查表单');
  }
};

// 重置表单
const resetForm = () => {
  formRef.value?.resetFields();
  Object.assign(carUserDTO, {
    id: undefined,
    username: undefined,
    password: undefined,
    status: '0',
    remark: undefined,
    registerType: '0'
  });
};

// 点击行操作
const handleRowClick = (record: CarUserVO) => {
  return {
    onClick: () => {
      getUserInfo(new MouseEvent('click'), record.id as string);
    }
  };
};

// 打开重置密码弹窗
const handleOpenResetPasswordModel = (event: MouseEvent, record: CarUserVO) => {
  event.stopPropagation();
  // 确保编辑弹窗关闭
  modalActive.open = false;
  
  resetPasswordForm.userId = record.id as string;
  resetPasswordForm.password = '';
  resetPasswordForm.confirmPassword = '';
  resetPasswordModelActive.value = true;
};

// 确认密码验证
function validateConfirmPassword(rule: Rule, value: string) {
  if (value && value !== resetPasswordForm.password) {
    return Promise.reject('两次输入的密码不一致');
  }
  return Promise.resolve();
}

// 重置密码操作
const handleResetPassword = async () => {
  try {
    await resetPasswordFormRef.value.validate();
    resetPasswordLoading.value = true;
    
    // 确保userId是数字类型转换
    const userId = resetPasswordForm.userId ? Number(resetPasswordForm.userId) : undefined;
    if (!userId) {
      message.error('用户ID无效');
      return;
    }
    
    await carUserApi.resetPassword(
      userId,
      resetPasswordForm.password,
      ''  // 密码加密请求key，如果后端需要可以加上
    );
    
    message.success('密码重置成功');
    resetPasswordModelActive.value = false;
  } catch (error) {
    console.error('重置密码失败', error);
    message.error('重置密码失败');
  } finally {
    resetPasswordLoading.value = false;
  }
};

// 导出Excel
const handleExportExcel = async () => {
  try {
    // 处理日期范围查询
    if (userQuery.createTimeList && userQuery.createTimeList.length === 2) {
      userQuery.createTimeStart = userQuery.createTimeList[0]?.format('YYYY-MM-DD 00:00:00');
      userQuery.createTimeEnd = userQuery.createTimeList[1]?.format('YYYY-MM-DD 23:59:59');
    } else {
      userQuery.createTimeStart = undefined;
      userQuery.createTimeEnd = undefined;
    }
    
    const res = await carUserApi.exportExcel(userQuery);
    if (!res) {
      message.error('导出失败');
      return;
    }
    
    // 创建下载链接
    const blob = res instanceof Blob ? res : new Blob([res]);
    const downloadLink = document.createElement('a');
    const url = window.URL.createObjectURL(blob);
    downloadLink.href = url;
    downloadLink.download = '前台用户数据.xlsx';
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