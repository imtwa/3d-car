<template>
  <div class="brand-detail">
    <!-- 顶部导航 -->
    <div class="top-navigation">
      <!-- 车型选择器 -->
      <div class="model-selector" v-if="allModels.length > 0">
        <div
          v-for="(model, index) in allModels"
          :key="model.id"
          class="model-tab"
          :class="{ active: currentModelIndex === index }"
          @click="selectModel(index)"
        >
          {{ model.name }}
        </div>
      </div>
    </div>

    <!-- 全屏轮播图 -->
    <div class="fullscreen-carousel">
      <el-carousel
        :interval="5000"
        arrow="always"
        indicator-position="none"
        height="calc(100vh - 120px)"
        @change="handleCarouselChange"
        ref="carousel"
        :initial-index="0"
      >
        <el-carousel-item v-for="(model, index) in allModels" :key="model.id">
          <div class="carousel-content">
            <div class="image-container">
              <img :src="model.coverImageUrl" :alt="model.name" class="model-image" />
            </div>

            <!-- 车型信息上方覆盖层 - 显示车型名称 -->
            <div class="model-info-overlay top">
              <div class="model-name-container">
                <h1>{{ model.name }}</h1>
              </div>
            </div>

            <!-- 车型信息下方覆盖层 - 显示参数和按钮 -->
            <div class="model-info-overlay bottom">
              <div class="model-details">
                <div class="model-specs" v-if="model.parameters">
                  <div class="spec-item">
                    <span class="spec-value">{{ model.parameters?.acceleration || '6.2' }} s</span>
                    <span class="spec-label">最快百公里加速</span>
                  </div>
                  <div class="spec-item">
                    <span class="spec-value"
                      >{{ model.parameters?.displacement || '1984' }} cm<sup>3</sup></span
                    >
                    <span class="spec-label">最大排量容量</span>
                  </div>
                  <div class="spec-item">
                    <span class="spec-value">{{ model.parameters?.power || '195' }} kW</span>
                    <span class="spec-label">最大输出功率</span>
                  </div>
                </div>

                <div class="action-buttons">
                  <el-button type="primary" class="detail-btn" @click="showModelDetail(model)"
                    >查看详情</el-button
                  >
                  <el-button type="default" class="virtual-btn" @click="showVirtualDisplay(model)"
                    >虚拟展示</el-button
                  >
                </div>
              </div>
            </div>
      </div>
        </el-carousel-item>
      </el-carousel>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCarModelList } from '@/api/car'
import { queryAttachmentInfoByIds } from '@/api/AttachmentStorage'
import { ElMessage, ElLoading } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(false)

// 轮播图引用
const carousel = ref(null)

// 所有车型列表
const allModels = ref([])

// 当前选中的车型索引
const currentModelIndex = ref(0)

// 选择车型
function selectModel(index) {
  currentModelIndex.value = index
  // 手动控制轮播图切换到对应索引
  if (carousel.value) {
    carousel.value.setActiveItem(index)
  }
}

// 处理轮播图切换
function handleCarouselChange(index) {
  // 更新当前选中的车型索引
  currentModelIndex.value = index
  console.log('当前车型索引:', index)
}

// 获取图片URL
function getImageUrl(image) {
  // 如果image已经是完整URL，则直接返回
  if (image && (image.startsWith('http://') || image.startsWith('https://'))) {
    return image
  }
  // 否则拼接基础URL
  return image || 'https://via.placeholder.com/800x450?text=暂无图片'
}

// 显示车型详情
function showModelDetail(model) {
  if (!model) return
  router.push(`/cars/${model.id}`)
}

// 显示虚拟展示
function showVirtualDisplay(model) {
  if (!model) return
  ElMessage.info(`即将进入${model.name}的虚拟展示`)
  // 这里可以添加跳转到虚拟展示页面的逻辑
}

// 获取品牌详情和车型列表
async function fetchBrandData() {
  loading.value = true
  const loadingInstance = ElLoading.service({
    target: '.brand-detail',
    text: '加载品牌信息中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })

  useMockData()

  try {
    const brandId = route.params.id

    // 从真实API获取数据
    const res = await getCarModelList({
      pageNum: 1,
      pageSize: 100,
      brandId
    })
    const models = res.data?.records || []

    // 如果有真实数据，直接使用
    if (models.length > 0) {
      // 收集所有封面图片ID
      const coverImageIds = models.map(model => model.coverImageId)

      // 一次性获取所有封面图片的URL
      const coverImagesRes = await queryAttachmentInfoByIds(coverImageIds)
      const coverImages = coverImagesRes.data || []

      // 创建一个映射，以便快速查找封面图片URL
      const coverImageMap = coverImages.reduce((acc, img) => {
        acc[img.id] = '/api' +  img.path
        return acc
      }, {})

      // 为每个模型添加封面图片URL
      allModels.value = models.map(model => ({
        ...model,
        coverImageUrl: coverImageMap[model.coverImageId] || '' // 如果没有找到URL，可以提供一个默认值或空字符串
      }))
    }

  } catch (error) {
    console.error('获取品牌信息失败:', error)
    ElMessage.error('获取品牌信息失败')
  } finally {
    loading.value = false
    loadingInstance.close()
  }
}

// 不再需要按分类组织车型数据

// 使用模拟数据
function useMockData() {
  // 模拟车型数据 - 平铺展示所有车型
  allModels.value = [
    {
      id: '101',
      name: 'A3',
      description: '豪华紧凑型轿车',
      price: '28.98-33.65万',
      category: '轿车',
      parameters: {
        acceleration: '7.7',
        displacement: '1984',
        power: '150'
      },
      images: [
        'https://www.audi.cn/content/dam/OneWeb/faw_vw/model_finder/faw_vw/model_filter/model_filter_car_image/A5-Sportback_MY2023.png'
      ]
    }
  ]
}

// 在组件挂载时获取数据
onMounted(fetchBrandData)

// 当路由参数改变时重新获取数据
watch(() => route.params.id, fetchBrandData)
</script>

<style lang="scss" scoped>
.brand-detail {
  position: relative;
  background-color: #000;
  color: #fff;
  overflow: hidden;

  @media (max-width: 768px) {
    min-height: calc(100vh - 60px);
  }

  // 全屏轮播图样式
  .fullscreen-carousel {
    position: relative;
    height: calc(100vh - 140px);

    // 自定义导航按钮样式
    .custom-nav-buttons {
      position: absolute;
      top: 50%;
      width: 100%;
      transform: translateY(-50%);
      z-index: 1000;
      display: flex;
      justify-content: space-between;
      padding: 0 20%;
      pointer-events: none; // 避免干扰轮播图内容的交互

      .nav-button {
        width: 50px;
        height: 50px;
        background-color: rgba(255, 255, 255, 0.9);
        border-radius: 50%;
    display: flex;
    align-items: center;
        justify-content: center;
        cursor: pointer;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        pointer-events: auto; // 确保按钮可以接收点击事件
        color: #000;
        font-size: 24px;
        transition: background-color 0.3s;

        &:hover {
          background-color: #ffffff;
        }

        &.next {
          border: 2px solid #ff0000; // 添加红色边框以匹配用户图片中的红框
        }
      }
    }

    .el-carousel {
      height: 100%;

      .el-carousel__arrow {
        background-color: rgba(255, 255, 255, 0.9);
        color: #000;
        font-size: 24px;
        width: 50px;
        height: 50px;
        z-index: 999; /* 进一步提高z-index确保按钮在最上层 */
        border-radius: 50%;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        pointer-events: auto; /* 确保按钮可以接收点击事件 */
        opacity: 1 !important; /* 确保按钮始终可见 */

        &--left {
          left: 20%;
        }

        &--right {
          right: 20%;
        }

        &:hover {
          background-color: #ffffff;
          cursor: pointer; /* 确保鼠标悬停时显示为指针 */
        }
      }

      .carousel-content {
        position: relative;
        height: 100%;

        .image-container {
          height: 100%;
          width: 100%;
          overflow: hidden;

          .model-image {
            max-width: 70%;
            max-height: 70%;
            width: auto;
            height: auto;
            object-fit: contain;
            object-position: center;
            transition: transform 0.5s;
            margin: 0 auto;
            display: block;
          }

          /* 确保图片容器相对定位，以便按钮可以相对于它定位 */
          &:hover .model-image {
            transform: scale(1.05);
          }
        }

        // 车型信息覆盖层 - 基础样式
        .model-info-overlay {
          position: absolute;
          left: 0;
          width: 100%;
          color: white;
          text-align: center;

          @media (max-width: 768px) {
            padding: 20px;
          }

          // 上方覆盖层 - 显示车型名称
          &.top {
            top: 0;
            padding: 30px;
            background: linear-gradient(
              to bottom,
              rgba(0, 0, 0, 0.8) 0%,
              rgba(0, 0, 0, 0.4) 30%,
              rgba(0, 0, 0, 0) 100%
            );

            .model-name-container {
              h1 {
                font-size: 3rem;
                font-weight: 600;
                margin: 0;
                text-align: center;

                @media (max-width: 768px) {
                  font-size: 2rem;
                }
              }
            }
          }

          // 下方覆盖层 - 显示参数和按钮
          &.bottom {
            bottom: 0;
            padding: 40px;
            background: linear-gradient(
              to top,
              rgba(0, 0, 0, 0.8) 0%,
              rgba(0, 0, 0, 0.4) 50%,
              rgba(0, 0, 0, 0) 100%
            );
            display: flex;
            justify-content: center;

            .model-details {
              max-width: 800px;

              .model-description {
                font-size: 1.1rem;
                margin: 0 0 15px;
                opacity: 0.9;

                @media (max-width: 768px) {
                  font-size: 0.9rem;
                }
              }

              .model-price {
                font-size: 1.3rem;
                font-weight: 600;
                margin: 0 0 20px;

                @media (max-width: 768px) {
                  font-size: 1.1rem;
                  margin: 0 0 15px;
                }
              }

              .model-specs {
                display: flex;
                gap: 40px;
                margin-bottom: 30px;
                justify-content: center;

                @media (max-width: 768px) {
                  gap: 20px;
                  flex-wrap: wrap;
                  margin-bottom: 20px;
                }

                .spec-item {
                  display: flex;
                  flex-direction: column;

                  .spec-value {
                    font-size: 1.5rem;
                    font-weight: 600;
                    margin-bottom: 5px;

                    @media (max-width: 768px) {
                      font-size: 1.2rem;
                    }
                  }

                  .spec-label {
            font-size: 0.9rem;
                    opacity: 0.7;
                  }
                }
              }

              .action-buttons {
                display: flex;
                gap: 15px;
                justify-content: center;

                @media (max-width: 768px) {
                  flex-direction: column;
                  gap: 10px;
                }

                .el-button {
                  min-width: 120px;
                  border-radius: 0;
                  font-weight: 500;

                  &.detail-btn {
                    background-color: white;
                    color: black;
                    border: none;

                    &:hover {
                      background-color: rgba(255, 255, 255, 0.9);
                    }
                  }

                  &.virtual-btn {
                    background-color: transparent;
                    color: white;
                    border: 1px solid white;

                    &:hover {
                      background-color: rgba(255, 255, 255, 0.1);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 顶部导航样式
.top-navigation {
  position: relative;
  z-index: 10;
  padding: 20px 40px;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  flex-direction: column;
  gap: 15px;
  align-items: center;

  @media (max-width: 768px) {
    padding: 15px;
  }

  // 车型分类选择器
  .category-selector {
    display: flex;
    gap: 30px;
    overflow-x: auto;
    padding-bottom: 10px;

    &::-webkit-scrollbar {
      height: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background-color: rgba(255, 255, 255, 0.3);
    }

    .category-tab {
      font-size: 1.1rem;
      font-weight: 500;
      padding: 5px 0;
      cursor: pointer;
      white-space: nowrap;
      position: relative;
      color: rgba(255, 255, 255, 0.7);
      transition: color 0.3s;

      &:hover {
        color: white;
      }

      &.active {
        color: white;

        &:after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 0;
          width: 100%;
          height: 2px;
          background-color: white;
        }
      }
    }
  }

  // 车型选择器
  .model-selector {
    display: flex;
    gap: 20px;
    overflow-x: auto;
    padding-bottom: 5px;
    justify-content: center;

    &::-webkit-scrollbar {
      height: 2px;
    }

    &::-webkit-scrollbar-thumb {
      background-color: rgba(255, 255, 255, 0.3);
    }

    .model-tab {
      font-size: 1rem;
      padding: 5px 0;
      cursor: pointer;
      white-space: nowrap;
      position: relative;
      color: rgba(255, 255, 255, 0.6);
      transition: color 0.3s;

      &:hover {
        color: white;
      }

      &.active {
        color: white;
        font-weight: 500;

        &:after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 0;
          width: 100%;
          height: 2px;
          background-color: white;
        }
      }
    }
  }
}
</style>
