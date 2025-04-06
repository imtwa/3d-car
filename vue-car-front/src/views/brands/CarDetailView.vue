<template>
  <div class="car-detail-view">
    <!-- 全屏图片滚动区域 -->
    <div class="fullscreen-scroll" ref="scrollContainer">
      <div
        v-for="(image, index) in carImages"
        :key="index"
        class="image-section"
        :id="`image-section-${index}`"
      >
        <div class="image-container">
          <img
            :src="image.url"
            :alt="`${carData.name} - 图片 ${index + 1}`"
            class="fullscreen-image"
          />

          <!-- 图片上的文字描述 -->
          <div class="image-text" v-if="image.text">
            <h2>{{ image.text.title || '' }}</h2>
            <p>{{ image.text.description || '' }}</p>
          </div>
        </div>
      </div>

      <!-- 车型参数区域 (最后一屏) -->
      <div class="image-section specs-section" id="specs-section">
        <div class="specs-container">
          <h2 class="specs-title">车型参数</h2>

          <div class="specs-grid">
            <div class="spec-item" v-if="carData.parameters?.acceleration">
              <div class="spec-value">{{ carData.parameters.acceleration }} <small>s</small></div>
              <div class="spec-label">百公里加速</div>
            </div>
            <div class="spec-item" v-if="carData.parameters?.displacement">
              <div class="spec-value">
                {{ carData.parameters.displacement }} <small>cm<sup>3</sup></small>
              </div>
              <div class="spec-label">排量</div>
            </div>
            <div class="spec-item" v-if="carData.parameters?.power">
              <div class="spec-value">{{ carData.parameters.power }} <small>kW</small></div>
              <div class="spec-label">功率</div>
            </div>
            <div class="spec-item">
              <div class="spec-value">{{ formatPrice(carData.price) }}</div>
              <div class="spec-label">售价</div>
            </div>
          </div>

          <div class="car-description">
            <h3>车型简介</h3>
            <p>{{ carData.description }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 滚动提示 -->
    <div class="scroll-indicator" v-if="currentSectionIndex < totalSections - 1">
      <span>向下滚动查看更多</span>
      <el-icon><ArrowDown /></el-icon>
    </div>

    <!-- 分页指示器 -->
    <div class="pagination-indicator">
      <div
        v-for="n in totalSections"
        :key="n"
        class="pagination-dot"
        :class="{ active: currentSectionIndex === n - 1 }"
        @click="scrollToSection(n - 1)"
      ></div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElLoading, ElMessage } from 'element-plus'
import { ArrowLeft, ArrowDown } from '@element-plus/icons-vue'
import { getCarModelSelectId } from '@/api/car'
import { queryAttachmentInfoByIds } from '@/api/AttachmentStorage'

const route = useRoute()
const carId = route.params.id

// 滚动相关
const scrollContainer = ref(null)
const currentSectionIndex = ref(0)
const isScrolling = ref(false)

// 车型数据
const carData = ref({})

// 图片数据
const carImages = ref([])

// 总区块数 (图片 + 参数页)
const totalSections = computed(() => carImages.value.length + 1) // +1 是参数页

// 格式化价格
const formatPrice = price => {
  if (!price) return '暂无价格'

  // 如果是数字范围 (如 "300000-400000")
  if (price.includes('-')) {
    const [min, max] = price.split('-')
    return `${(Number(min) / 10000).toFixed(2)}-${(Number(max) / 10000).toFixed(2)}万`
  }

  try {
    const num = parseFloat(price)
    if (isNaN(num)) return price

    // 大于100万显示为xx万
    if (num >= 1000000) {
      return `${(num / 10000).toFixed(2)}万`
    }

    // 否则显示原始价格
    return price
  } catch (e) {
    return price
  }
}

// 获取车型详情数据
const fetchCarDetail = async () => {
  const loading = ElLoading.service({
    target: '.car-detail-view',
    text: '加载中...',
    background: 'rgba(0, 0, 0, 0.7)'
  })

  try {
    const res = await getCarModelSelectId(carId)
    carData.value = res.data || {}

    // 解析 parameters JSON 字符串为对象
    if (typeof carData.value.parameters === 'string') {
      try {
        // 处理 JSON 中的单引号
        const fixedJson = carData.value.parameters
        carData.value.parameters = JSON.parse(fixedJson)
      } catch (e) {
        console.error('解析参数失败:', e)
      }
    }
    // 获取所有相关图片
    const allImageIds = [...(carData.value.imageIds || []), carData.value.coverImageId].filter(Boolean) // 过滤空值

    if (allImageIds.length > 0) {
      
      // 通过API获取图片URL
      const imageResult = await queryAttachmentInfoByIds(allImageIds)
      const images = imageResult.data || []

      // 处理封面图片
      // if (carData.coverImageId && images.length > 0) {
      //   const coverImage = images.find(img => img.id === carData.coverImageId)
      //   if (coverImage) {
      //     carImages.value.push({
      //       id: coverImage.id,
      //       url: '/api' + coverImage.path,
      //       text: (carData.parameters?.imageTxt && carData.parameters.imageTxt[0]) || null
      //     })
      //   }
      // }

      // 处理其他图片
      if (carData.value.imageIds && carData.value.imageIds.length > 0) {
        carData.value.imageIds.forEach((imageId, index) => {
          const image = images.find(img => img.id === imageId)
          if (image) {
            carImages.value.push({
              id: image.id,
              url: '/api' + image.path,
              text: (carData.value.parameters?.imageTxt && carData.value.parameters.imageTxt[index]) || null
            })
          }
        })
      }
    }
  } catch (error) {
    console.error('获取车型详情失败', error)
    ElMessage.error('获取车型详情失败')
    useMockImages()
  } finally {
    loading.close()
  }
}

// 滚动到指定区块
const scrollToSection = index => {
  if (isScrolling.value) return
  isScrolling.value = true

  let targetElement
  if (index < carImages.value.length) {
    targetElement = document.getElementById(`image-section-${index}`)
  } else {
    targetElement = document.getElementById('specs-section')
  }

  if (targetElement) {
    targetElement.scrollIntoView({ behavior: 'smooth' })
    currentSectionIndex.value = index

    // 重置滚动锁定
    setTimeout(() => {
      isScrolling.value = false
    }, 1000)
  }
}

// 监听滚动事件以更新当前区块索引
const handleScroll = () => {
  if (!scrollContainer.value || isScrolling.value) return

  const sections = Array.from(document.querySelectorAll('.image-section'))
  const containerTop = scrollContainer.value.scrollTop
  const containerHeight = scrollContainer.value.clientHeight

  // 找出当前在视窗中的区块
  for (let i = 0; i < sections.length; i++) {
    const section = sections[i]
    const sectionTop = section.offsetTop

    // 如果区块的顶部在视窗内或刚好在视窗顶部以上一点
    if (sectionTop <= containerTop + containerHeight / 3) {
      currentSectionIndex.value = i
    }
  }
}

// 添加触摸滑动支持
let touchStartY = 0
const handleTouchStart = e => {
  touchStartY = e.touches[0].clientY
}

const handleTouchEnd = e => {
  const touchEndY = e.changedTouches[0].clientY
  const deltaY = touchEndY - touchStartY

  // 如果滑动距离足够大
  if (Math.abs(deltaY) > 50) {
    if (deltaY < 0 && currentSectionIndex.value < totalSections.value - 1) {
      // 向上滑动，前往下一部分
      scrollToSection(currentSectionIndex.value + 1)
    } else if (deltaY > 0 && currentSectionIndex.value > 0) {
      // 向下滑动，前往上一部分
      scrollToSection(currentSectionIndex.value - 1)
    }
  }
}

// 设置键盘导航
const handleKeyDown = e => {
  if (e.key === 'ArrowDown' || e.key === 'PageDown') {
    if (currentSectionIndex.value < totalSections.value - 1) {
      scrollToSection(currentSectionIndex.value + 1)
    }
    e.preventDefault()
  } else if (e.key === 'ArrowUp' || e.key === 'PageUp') {
    if (currentSectionIndex.value > 0) {
      scrollToSection(currentSectionIndex.value - 1)
    }
    e.preventDefault()
  }
}

// 生命周期钩子
onMounted(() => {
  fetchCarDetail()

  // 添加事件监听器
  if (scrollContainer.value) {
    scrollContainer.value.addEventListener('scroll', handleScroll)
    scrollContainer.value.addEventListener('touchstart', handleTouchStart)
    scrollContainer.value.addEventListener('touchend', handleTouchEnd)
  }

  window.addEventListener('keydown', handleKeyDown)
})

onBeforeUnmount(() => {
  // 移除事件监听器
  if (scrollContainer.value) {
    scrollContainer.value.removeEventListener('scroll', handleScroll)
    scrollContainer.value.removeEventListener('touchstart', handleTouchStart)
    scrollContainer.value.removeEventListener('touchend', handleTouchEnd)
  }

  window.removeEventListener('keydown', handleKeyDown)
})
</script>

<style lang="scss" scoped>
.car-detail-view {
  position: relative;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background-color: #000;
  color: #fff;

  // 返回按钮
  .back-button {
    position: fixed;
    top: 20px;
    left: 20px;
    z-index: 100;
    display: flex;
    align-items: center;
    gap: 5px;
    padding: 8px 15px;
    background-color: rgba(0, 0, 0, 0.6);
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;

    &:hover {
      background-color: rgba(0, 0, 0, 0.8);
    }

    .el-icon {
      font-size: 16px;
    }
  }

  // 车型标题
  .car-header {
    position: fixed;
    top: 20px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 100;
    text-align: center;

    h1 {
      font-size: 2.5rem;
      font-weight: 600;
      margin: 0;
      letter-spacing: 1px;

      @media (max-width: 768px) {
        font-size: 1.8rem;
      }
    }

    .brand-name {
      font-size: 1.2rem;
      opacity: 0.8;
      margin: 5px 0 0;

      @media (max-width: 768px) {
        font-size: 1rem;
      }
    }
  }

  // 全屏滚动容器
  .fullscreen-scroll {
    height: 100vh;
    overflow-y: auto;
    scroll-snap-type: y mandatory;

    // 隐藏滚动条
    &::-webkit-scrollbar {
      display: none;
    }

    scrollbar-width: none;
    -ms-overflow-style: none;

    // 单个图片区域
    .image-section {
      height: 100vh;
      width: 100%;
      scroll-snap-align: start;
      position: relative;

      .image-container {
        height: 100%;
        width: 100%;
        display: flex;
        justify-content: center;
        align-items: center;
        position: relative;

        // 全屏图片
        .fullscreen-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
          object-position: center;
        }

        // 图片上的文字
        .image-text {
          position: absolute;
          top: 8%;
          left: 40px;
          max-width: 500px;
          text-align: left;
          padding: 20px;
          border-radius: 4px;

          @media (max-width: 768px) {
            top: 100px;
            left: 20px;
            right: 20px;
            max-width: unset;
          }

          h2 {
            font-size: 2rem;
            font-weight: 600;
            margin: 0 0 15px;

            @media (max-width: 768px) {
              font-size: 1.5rem;
            }
          }

          p {
            font-size: 1.1rem;
            line-height: 1.6;
            opacity: 0.9;
            margin: 0;

            @media (max-width: 768px) {
              font-size: 0.9rem;
            }
          }
        }
      }

      // 参数区域特殊样式
      &.specs-section {
        background: linear-gradient(135deg, #1a1a1a 0%, #000 100%);
        display: flex;
        justify-content: center;
        align-items: center;

        .specs-container {
          max-width: 900px;
          width: 90%;
          padding: 40px;

          .specs-title {
            font-size: 2.5rem;
            font-weight: 600;
            margin: 0 0 40px;
            text-align: center;

            @media (max-width: 768px) {
              font-size: 1.8rem;
              margin-bottom: 30px;
            }
          }

          .specs-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 30px;
            margin-bottom: 50px;

            @media (max-width: 576px) {
              grid-template-columns: repeat(2, 1fr);
              gap: 20px;
            }

            .spec-item {
              text-align: center;

              .spec-value {
                font-size: 2rem;
                font-weight: 600;
                margin-bottom: 10px;

                small {
                  font-size: 1rem;
                  font-weight: normal;
                }

                @media (max-width: 768px) {
                  font-size: 1.5rem;
                }
              }

              .spec-label {
                font-size: 1rem;
                opacity: 0.7;
              }
            }
          }

          .car-description {
            text-align: center;

            h3 {
              font-size: 1.5rem;
              margin-bottom: 15px;

              @media (max-width: 768px) {
                font-size: 1.2rem;
              }
            }

            p {
              font-size: 1.1rem;
              line-height: 1.7;
              opacity: 0.8;

              @media (max-width: 768px) {
                font-size: 0.9rem;
                line-height: 1.6;
              }
            }
          }
        }
      }
    }
  }

  // 滚动提示
  .scroll-indicator {
    position: fixed;
    bottom: 30px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    opacity: 0.7;
    animation: pulse 2s infinite;
    z-index: 90;

    span {
      font-size: 0.9rem;
      letter-spacing: 1px;
    }

    .el-icon {
      font-size: 20px;
    }

    @keyframes pulse {
      0% {
        transform: translate(-50%, 0);
      }
      50% {
        transform: translate(-50%, 10px);
      }
      100% {
        transform: translate(-50%, 0);
      }
    }
  }

  // 分页指示器
  .pagination-indicator {
    position: fixed;
    right: 30px;
    top: 50%;
    transform: translateY(-50%);
    display: flex;
    flex-direction: column;
    gap: 15px;
    z-index: 90;

    .pagination-dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.3);
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background-color: rgba(255, 255, 255, 0.8);
      }

      &.active {
        background-color: #fff;
        transform: scale(1.2);
      }
    }
  }
}
</style>
