<template>
  <div class="brands-page">
    <div class="hero-section">
      <div class="hero-content">
        <h1>品牌专区</h1>
        <p class="subtitle">探索豪华汽车品牌，发现您的梦想座驾</p>
      </div>
    </div>

    <div class="brands-container">
      <div class="section-header">
        <h2>热门品牌</h2>
        <p>选择您感兴趣的汽车品牌，了解更多车型信息</p>
      </div>

      <div class="brand-list">
        <el-row :gutter="30">
          <el-col
            v-for="brand in brands"
            :key="brand.id"
            :xs="24"
            :sm="12"
            :md="12"
            :lg="12"
            class="brand-col"
          >
            <div class="brand-card" @click="navigateToBrand(brand)">
              <div class="brand-logo">
                <img :src="brand.logo" :alt="brand.name" />
              </div>
              <div class="brand-info">
                <h3>{{ brand.name }}</h3>
                <p class="description">{{ brand.description }}</p>
                <div class="stats">
                  <div class="stat-item">
                    <span class="label">创立年份</span>
                    <span class="value">{{ brand.foundYear || '--' }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="label">原产国</span>
                    <span class="value">{{ brand.country || '--' }}</span>
                  </div>
                </div>
                <el-button type="primary" class="view-btn">查看品牌车型</el-button>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'

const router = useRouter()
const brands = ref([])
const loading = ref(false)

// 获取品牌列表
async function fetchBrands() {
  // loading.value = true
  // const loadingInstance = ElLoading.service({
  //   target: '.brands-page',
  //   text: '加载品牌信息中...',
  //   background: 'rgba(255, 255, 255, 0.7)'
  // })

  // 使用模拟数据
  useMockData()
  // loading.value = false
  // loadingInstance.close()
}

// 使用模拟数据
function useMockData() {
  brands.value = [
    {
      id: 1,
      name: '奔驰',
      description: '德国豪华汽车品牌，以卓越工艺和创新科技著称，拥有130多年历史的汽车制造商',
      logo: 'https://assets.oneweb.mercedes-benz.com.cn/global-header/img/icon_wev_benz@2x.png',
      foundYear: '1886',
      country: '德国'
    },
    {
      id: 2,
      name: '奥迪',
      description: '德国高端汽车制造商，以先进技术和精湛设计闻名，quattro四驱系统是其标志性技术',
      logo: 'https://pic.616pic.com/ys_bnew_img/00/04/08/yk2MlNzhYS.jpg',
      foundYear: '1909',
      country: '德国'
    }
  ]
}

// 导航到品牌详情页
function navigateToBrand(brand) {
  router.push(`/brands/${brand.id}`)
}

// 在组件挂载时获取数据
onMounted(fetchBrands)
</script>

<style lang="scss" scoped>
.brands-page {
  min-height: 100vh;
  background-color: #f8f9fa;

  .hero-section {
    height: 300px;
    background-image:
      linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)),
      url('https://img2.baidu.com/it/u=1124612787,2278927168&fm=253&fmt=auto&app=138&f=JPEG?w=889&h=500');
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

  .brands-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px 60px;

    .section-header {
      text-align: center;
      margin-bottom: 40px;

      h2 {
        font-size: 2.2rem;
        color: #303133;
        margin-bottom: 15px;
        font-weight: 600;

        @media (max-width: 768px) {
          font-size: 1.8rem;
        }
      }

      p {
        font-size: 1.1rem;
        color: #606266;
        max-width: 700px;
        margin: 0 auto;
        line-height: 1.6;

        @media (max-width: 768px) {
          font-size: 1rem;
        }
      }
    }

    .brand-list {
      .brand-col {
        margin-bottom: 30px;
      }

      .brand-card {
        display: flex;
        background: white;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
        transition: all 0.3s;
        height: 100%;
        cursor: pointer;

        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
        }

        @media (max-width: 768px) {
          flex-direction: column;
        }

        .brand-logo {
          width: 200px;
          padding: 30px;
          display: flex;
          align-items: center;
          justify-content: center;
          background-color: #f8f9fa;

          @media (max-width: 768px) {
            width: 100%;
            height: 150px;
            padding: 20px;
          }

          img {
            max-width: 100%;
            max-height: 100%;
            object-fit: contain;
          }
        }

        .brand-info {
          flex: 1;
          padding: 30px;
          display: flex;
          flex-direction: column;

          h3 {
            font-size: 1.8rem;
            color: #303133;
            margin: 0 0 15px;
            font-weight: 600;

            @media (max-width: 768px) {
              font-size: 1.5rem;
            }
          }

          .description {
            font-size: 1rem;
            color: #606266;
            margin-bottom: 20px;
            line-height: 1.6;
            flex-grow: 1;
          }

          .stats {
            display: flex;
            margin-bottom: 25px;

            .stat-item {
              margin-right: 30px;

              &:last-child {
                margin-right: 0;
              }

              .label {
                display: block;
                font-size: 0.9rem;
                color: #909399;
                margin-bottom: 5px;
              }

              .value {
                display: block;
                font-size: 1.1rem;
                color: #303133;
                font-weight: 600;
              }
            }
          }

          .view-btn {
            align-self: flex-start;
            padding: 12px 24px;
            font-weight: 600;

            @media (max-width: 768px) {
              align-self: center;
            }
          }
        }
      }
    }
  }
}
</style>
