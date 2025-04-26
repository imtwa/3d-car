<template>
  <div class="ar">
    <div class="hero-section">
      <div class="hero-content">
        <h1>3D汽车展示</h1>
        <p class="subtitle">通过3D技术和虚拟现实，体验前所未有的汽车展示方式</p>
      </div>
    </div>

    <div class="models-container">
      <div class="section-header">
        <h2>热门车型</h2>
        <p>选择您感兴趣的汽车型号，体验3D虚拟展示</p>
      </div>

      <!-- 搜索框 -->
      <div class="search-container">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索车型名称"
          class="search-input"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #suffix>
            <el-button :icon="Search" circle @click="handleSearch" />
          </template>
        </el-input>
      </div>

      <div class="model-list" v-loading="loading">
        <el-row :gutter="30">
          <el-col 
            v-for="model in paginatedModels" 
            :key="model.id" 
            :xs="24" 
            :sm="12" 
            :md="8" 
            :lg="6"
            class="model-col"
          >
            <div class="model-card" @click="viewCarModel(model)">
              <div class="model-image" :style="{ backgroundImage: `url(${model.coverImage || '/car/default-car.jpg'})` }">
                <div class="model-overlay">
                  <el-button type="primary" size="small" class="view-btn">3D展示</el-button>
                </div>
              </div>
              <div class="model-info">
                <h3>{{ model.name }}</h3>
                <p class="description">{{ truncateText(model.description || '暂无描述', 60) }}</p>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- 无数据提示 -->
        <div v-if="paginatedModels.length === 0 && !loading" class="empty-result">
          <el-empty :description="searchKeyword ? '没有找到符合搜索条件的车型' : '暂无车型数据'" />
        </div>
      </div>

      <!-- 分页器 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[12, 24, 36, 48]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          background
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElLoading, ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getCarModelList } from '@/api/car'
import { queryAttachmentInfoByIds } from '@/api/AttachmentStorage'

const router = useRouter()

// 状态变量
const loading = ref(false)
const carModels = ref([])
const dialogVisible = ref(false)
const currentModel = ref(null)

// 分页相关
const pageNum = ref(1)
const pageSize = ref(12)
const total = ref(0)

// 搜索相关
const searchKeyword = ref('')
const searchTimeout = ref(null)
const allCarModels = ref([]) // 存储所有车型数据，用于客户端过滤

// 计算当前页显示的车型
const paginatedModels = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  const end = start + pageSize.value
  return carModels.value.slice(start, end)
})

// 获取车型列表数据
const fetchCarModels = async () => {
  loading.value = true
  try {
    // 请求所有数据，后续在客户端进行分页
    const params = {
      pageNum: 1,
      pageSize: 100 // 请求较大数量，确保能获取所有数据
    }
    const res = await getCarModelList(params)
    if (res.data) {
      if (res.data.records && res.data.records.length > 0) {
        // 提取所有coverImageId
        const coverImageIds = res.data.records
          .map(item => item.coverImageId)
          .filter(id => id) // 过滤掉空值
        
        // 如果有coverImageId，获取对应的图片URL
        if (coverImageIds.length > 0) {
          try {
            const coverImagesRes = await queryAttachmentInfoByIds(coverImageIds)
            // 创建id到URL的映射
            const coverImageMap = {}
            if (coverImagesRes.data) {
              coverImagesRes.data.forEach(img => {
                coverImageMap[img.id] = '/api' + img.path
              })
            }
            
            // 将图片URL添加到车型数据中
            const modelsWithImages = res.data.records.map(item => ({
              ...item,
              coverImage: item.coverImageId ? coverImageMap[item.coverImageId] : null
            }))
            
            // 保存所有获取的数据
            allCarModels.value = modelsWithImages
            
            // 应用客户端搜索过滤
            applySearchFilter()
          } catch (imgError) {
            console.error('获取图片信息失败:', imgError)
            allCarModels.value = res.data.records
            applySearchFilter()
          }
        } else {
          allCarModels.value = res.data.records
          applySearchFilter()
        }
      } else {
        allCarModels.value = []
        carModels.value = []
      }
    }
  } catch (error) {
    console.error('获取车型列表失败:', error)
    ElMessage.error('获取车型列表失败')
    allCarModels.value = []
    carModels.value = []
  } finally {
    loading.value = false
  }
}

// 客户端搜索过滤
const applySearchFilter = () => {
  if (!searchKeyword.value.trim()) {
    carModels.value = allCarModels.value
  } else {
    const keyword = searchKeyword.value.trim().toLowerCase()
    carModels.value = allCarModels.value.filter(model => 
      model.name && model.name.toLowerCase().includes(keyword)
    )
  }
  
  // 更新总数和重置页码
  total.value = carModels.value.length
  pageNum.value = 1
}

// 查看车型3D展示
const viewCarModel = (model) => {
  if (!model) return
  // 在新标签页中打开carAR页面
  window.open(`/carAR/${model.id}`, '_blank')
}

// 截断文本函数，保留指定长度
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}

// 处理页码变化 - 客户端分页
const handleCurrentChange = (val) => {
  pageNum.value = val
  // 客户端分页，不需要重新获取数据
}

// 处理每页数量变化 - 客户端分页
const handleSizeChange = (val) => {
  pageSize.value = val
  pageNum.value = 1 // 重置为第一页
  // 客户端分页，不需要重新获取数据
}

// 处理搜索
const handleSearch = () => {
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  
  searchTimeout.value = setTimeout(() => {
    pageNum.value = 1 // 重置为第一页
    applySearchFilter()
  }, 300)
}

// 监听搜索关键词变化
watch(searchKeyword, (newVal, oldVal) => {
  if (newVal !== oldVal) {
    handleSearch()
  }
})

// 生命周期钩子
onMounted(() => {
  fetchCarModels()
})
</script>

<style lang="scss" scoped>
.ar {
  min-height: 100vh;
  background-color: #1a1a1a;
  color: #fff;

  // 英雄区域样式
  .hero-section {
    height: 300px;
    background-image: linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)), url('https://images.unsplash.com/photo-1503376780353-7e6692767b70?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80');
    background-size: cover;
    background-position: center;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 50px;

    .hero-content {
      text-align: center;
      color: #fff;

      h1 {
        font-size: 3rem;
        margin-bottom: 20px;
        font-weight: 600;
        text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);

        @media (max-width: 768px) {
          font-size: 2.2rem;
        }
      }

      .subtitle {
        font-size: 1.3rem;
        max-width: 600px;
        margin: 0 auto;
        line-height: 1.6;
        text-shadow: 0 2px 8px rgba(0, 0, 0, 0.5);

        @media (max-width: 768px) {
          font-size: 1.1rem;
          padding: 0 20px;
        }
      }
    }
  }

  // 车型列表容器
  .models-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 60px;

    .section-header {
      text-align: center;
      margin-bottom: 40px;

      h2 {
        font-size: 32px;
        margin-bottom: 10px;
        color: #3498db;
      }

      p {
        font-size: 18px;
        color: #ccc;
        max-width: 600px;
        margin: 0 auto;
      }
    }

    // 搜索框样式
    .search-container {
      margin-bottom: 30px;
      max-width: 500px;
      margin-left: auto;
      margin-right: auto;

      .search-input {
        :deep(.el-input__wrapper) {
          background-color: #2c2c2c;
          box-shadow: 0 0 0 1px #444;
          border-radius: 8px;

          &:hover, &:focus {
            box-shadow: 0 0 0 1px #3498db;
          }

          .el-input__inner {
            color: #fff;
            
            &::placeholder {
              color: #999;
            }
          }
        }
      }
    }

    .model-list {
      min-height: 300px;

      .model-col {
        margin-bottom: 30px;
      }

      .model-card {
        background: #2c2c2c;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
        transition: all 0.3s;
        height: 100%;
        display: flex;
        flex-direction: column;
        cursor: pointer;

        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.3);

          .model-overlay {
            opacity: 1;
          }
        }

        .model-image {
          height: 200px;
          background-size: cover;
          background-position: center;
          position: relative;

          .model-overlay {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s;

            .view-btn {
              background-color: #3498db;
              border: none;
              font-weight: bold;
              padding: 10px 20px;

              &:hover {
                background-color: #2980b9;
              }
            }
          }
        }

        .model-info {
          padding: 15px 20px;
          flex: 1;
          display: flex;
          flex-direction: column;
          background-color: #eee;

          h3 {
            font-size: 18px;
            margin-bottom: 10px;
            color: #333;
          }

          .description {
            color: #555;
            font-size: 14px;
            line-height: 1.5;
            flex: 1;
          }
        }
      }

      // 无数据提示样式
      .empty-result {
        padding: 40px 0;
        text-align: center;

        :deep(.el-empty__description) {
          color: #ccc;
        }
      }
    }

    // 分页容器样式
    .pagination-container {
      margin-top: 30px;
      display: flex;
      justify-content: center;

      :deep(.el-pagination) {
        --el-pagination-bg-color: #2c2c2c;
        --el-pagination-text-color: #ccc;
        --el-pagination-button-color: #ccc;
        --el-pagination-button-bg-color: #2c2c2c;
        --el-pagination-button-disabled-color: #606266;
        --el-pagination-button-disabled-bg-color: #2c2c2c;
        --el-pagination-hover-color: #3498db;

        .el-pagination__total,
        .el-pagination__sizes .el-select .el-input .el-input__inner,
        .el-pagination__jump {
          color: #ccc;
        }

        .el-pager li {
          background-color: #2c2c2c;
          color: #ccc;

          &.is-active {
            color: #fff;
            background-color: #3498db;
          }

          &:hover:not(.is-active) {
            color: #3498db;
          }
        }
      }
    }
  }

  // 3D展示对话框
  :deep(.model-dialog) {
    .el-dialog__header {
      background: #1a1a1a;
      color: white;
      padding: 15px 20px;

      .el-dialog__title {
        color: white;
        font-weight: bold;
      }
    }

    .el-dialog__body {
      padding: 0;
    }

    .ar-display {
      width: 100%;
      height: 70vh;
      background: #1a1a1a;
      position: relative;

      .placeholder {
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 24px;
        color: #909399;
      }
    }
  }
}
</style>
