<template>
  <a-card title="车型管理" :bordered="false">
    <a-form layout="inline" :model="queryParams">
      <a-form-item label="品牌">
        <a-select
          v-model:value="queryParams.brand"
          placeholder="请选择品牌"
          style="width: 200px"
          allow-clear
        >
          <a-select-option v-for="item in brandList" :key="item.value" :value="item.value">
            {{ item.label }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="状态">
        <a-select
          v-model:value="queryParams.status"
          placeholder="请选择状态"
          style="width: 120px"
          allow-clear
        >
          <a-select-option value="1">上架</a-select-option>
          <a-select-option value="0">下架</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" @click="handleQuery">搜索</a-button>
        <a-button style="margin-left: 8px" @click="resetQuery">重置</a-button>
      </a-form-item>
    </a-form>

    <a-divider />

    <a-space style="margin-bottom: 16px">
      <a-button type="primary" @click="handleAdd">
        <template #icon><plus-outlined /></template>
        新增
      </a-button>
    </a-space>

    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      :loading="loading"
      row-key="id"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-switch
            v-model:checked="record.status"
            checked-children="上架"
            un-checked-children="下架"
            @change="handleStatusChange(record)"
          />
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="handleEdit(record)">编辑</a>
            <a-popconfirm title="确认删除吗?" @confirm="handleDelete(record)">
              <a style="color: #ff4d4f">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <!-- 新增/编辑弹窗 -->
    <a-modal
      v-model:visible="modalVisible"
      :title="modalTitle"
      width="800px"
      :confirm-loading="confirmLoading"
      @ok="handleSubmit"
      @cancel="closeModal"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 18 }"
        :rules="rules"
      >
        <a-form-item label="所属品牌" name="brandId">
          <a-select
            v-model:value="formData.brandId"
            placeholder="请选择品牌"
            style="width: 100%"
          >
            <a-select-option v-for="item in brandList" :key="item.value" :value="item.value">
              {{ item.label }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="车型名称" name="name">
          <a-input v-model:value="formData.name" placeholder="请输入车型名称" />
        </a-form-item>
        <a-form-item label="车型简介" name="description">
          <a-textarea v-model:value="formData.description" placeholder="请输入车型简介" />
        </a-form-item>
        <a-form-item label="车型价格" name="price">
          <a-input-number
            v-model:value="formData.price"
            placeholder="请输入车型价格"
            style="width: 100%"
            :min="0"
            :formatter="value => `¥ ${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
            :parser="value => value.replace(/¥\s?|(,*)/g, '')"
          />
        </a-form-item>
        <a-form-item label="汽车参数" name="params">
          <a-textarea v-model:value="formData.params" placeholder="请输入汽车参数" />
        </a-form-item>
        <a-form-item label="汽车首图" name="coverImage">
          <attachment-upload v-model="formData.coverImage" model="picture" />
        </a-form-item>
        <a-form-item label="汽车介绍图片" name="images">
          <attachment-upload v-model="formData.images" model="picture" multiple />
        </a-form-item>
        <a-form-item label="3D模型文件" name="modelFile">
          <attachment-upload v-model="formData.modelFile" model="dragger" />
        </a-form-item>
      </a-form>
    </a-modal>
  </a-card>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, watch } from 'vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import AttachmentUpload from '@/components/attachment-upload/index.vue'
import { message } from 'ant-design-vue'
import type { TableProps } from 'ant-design-vue'
import { getCarModelList, addCarModel, updateCarModel, deleteCarModel, changeCarModelStatus } from '@/api/system/car/car'
import { getBrandList } from '@/api/system/car/brand'

interface QueryParams {
  brand?: string
  status?: string
  pageNum?: number
  pageSize?: number
}

interface FormData {
  id?: string
  brandId?: string
  name?: string
  description?: string
  price?: number
  params?: string
  coverImage?: string
  images?: string[]
  modelFile?: string
}

const columns = [
  {
    title: '车型名称',
    dataIndex: 'name',
    key: 'name'
  },
  {
    title: '所属品牌',
    dataIndex: 'brandName',
    key: 'brandName'
  },
  {
    title: '价格',
    dataIndex: 'price',
    key: 'price',
    customRender: ({ text }: { text: number }) => `¥${text}`
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status'
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime'
  },
  {
    title: '操作',
    key: 'action',
    width: '150px'
  }
]

const queryParams = reactive<QueryParams>({
  pageNum: 1,
  pageSize: 10
})

const formData = reactive<FormData>({})
const rules = {
  brandId: [{ required: true, message: '请选择品牌', trigger: 'change' }],
  name: [{ required: true, message: '请输入车型名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入车型价格', trigger: 'blur' }],
  coverImage: [{ required: true, message: '请上传汽车首图', trigger: 'change' }]
}

const dataList = ref([])
const brandList = ref([])
const loading = ref(false)
const modalVisible = ref(false)
const modalTitle = ref('')
const confirmLoading = ref(false)
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条`
})

const formRef = ref()

// 获取品牌列表
const getBrands = async () => {
  try {
    const res = await getBrandList({
      pageNum: 1,
      pageSize: 10
    })
    brandList.value = res.data.records.map((item: any) => ({
      label: item.name,
      value: item.id
    }))
  } catch (error) {
    console.error(error)
  }
}

// 获取车型列表
const getList = async () => {
  try {
    loading.value = true
    const res = await getCarModelList(queryParams)
    dataList.value = res.data.list
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置
const resetQuery = () => {
  Object.assign(queryParams, {
    brand: undefined,
    status: undefined,
    pageNum: 1
  })
  getList()
}

// 表格分页变化
const handleTableChange: TableProps['onChange'] = (pagination: any) => {
  queryParams.pageNum = pagination.current
  queryParams.pageSize = pagination.pageSize
  getList()
}

// 新增
const handleAdd = () => {
  modalTitle.value = '新增车型'
  modalVisible.value = true
  Object.assign(formData, {
    id: undefined,
    brandId: undefined,
    name: '',
    description: '',
    price: undefined,
    params: '',
    coverImage: '',
    images: [],
    modelFile: ''
  })
}

// 编辑
const handleEdit = (record: any) => {
  modalTitle.value = '编辑车型'
  modalVisible.value = true
  Object.assign(formData, {
    id: record.id,
    brandId: record.brandId,
    name: record.name,
    description: record.description,
    price: record.price,
    params: record.params,
    coverImage: record.coverImage,
    images: record.images,
    modelFile: record.modelFile
  })
}

// 监听formData
watch(
  () => formData,
  (newValue) => {
    console.log(newValue);
    
  },
  {
    deep: true
  }
)

// 删除
const handleDelete = async (record: any) => {
  try {
    await deleteCarModel(record.id)
    message.success('删除成功')
    getList()
  } catch (error) {
    console.error(error)
  }
}

// 状态切换
const handleStatusChange = async (record: any) => {
  try {
    await changeCarModelStatus({
      id: record.id,
      status: record.status ? '1' : '0'
    })
    message.success('状态修改成功')
  } catch (error) {
    console.error(error)
    record.status = !record.status
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    confirmLoading.value = true
    if (formData.id) {
      await updateCarModel(formData)
      message.success('修改成功')
    } else {
      await addCarModel(formData)
      message.success('新增成功')
    }
    closeModal()
    getList()
  } catch (error) {
    console.error(error)
  } finally {
    confirmLoading.value = false
  }
}

// 关闭弹窗
const closeModal = () => {
  modalVisible.value = false
  formRef.value.resetFields()
}

onMounted(() => {
  getBrands()
  getList()
})
</script>@/api/system/car/car