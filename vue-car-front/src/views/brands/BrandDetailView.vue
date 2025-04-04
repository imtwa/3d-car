<template>
  <div class="brand-detail">
    <!-- 品牌信息头部 -->
    <div class="brand-intro">
      <img :src="brand?.logo" :alt="brand?.name" class="brand-logo" />
      <div class="brand-info">
        <h1>{{ brand?.name }}</h1>
        <p class="description">{{ brand?.description }}</p>
        <div class="brand-stats">
          <div class="stat-item">
            <span class="stat-value">{{ brand?.models?.length || 0 }}</span>
            <span class="stat-label">车型数量</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ brand?.foundYear || '1926' }}</span>
            <span class="stat-label">创立年份</span>
          </div>
          <div class="stat-item">
            <span class="stat-value">{{ brand?.country || '德国' }}</span>
            <span class="stat-label">原产国</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 车型展示列表 -->
    <div class="model-list">
      <h2>车型展示</h2>
      <div v-if="!brand?.models || brand?.models.length === 0" class="empty-models">
        <el-empty description="暂无车型数据" />
      </div>
      <el-row :gutter="30" v-else>
        <el-col v-for="model in brand?.models" :key="model.id" :xs="24" :sm="12" :md="8" :lg="8">
          <model-card :model="model" @showView="showView(model)" />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { brandApi } from '@/api/brand'
import { ElMessage, ElLoading } from 'element-plus'
import { Star } from '@element-plus/icons-vue'
import ModelCard from '@/views/brands/components/ModelCard.vue'

const route = useRoute()
const router = useRouter()
const brand = ref(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  const loadingInstance = ElLoading.service({
    target: '.brand-detail',
    text: '加载品牌信息中...',
    background: 'rgba(255, 255, 255, 0.7)'
  })

  try {
    // const res = await brandApi.getBrandDetail(route.params.id)
    const res = {}
    brand.value = { id: '1', models: [] }

    // 如果没有模拟数据，添加一些模拟数据用于展示
    if (!brand.value.models || brand.value.models.length === 0) {
      if (brand.value.id === '1') {
        // 假设id为1是奔驰
        brand.value.models = [
          {
            id: '101',
            name: '奔驰 C级',
            price: '31.98-48.60万',
            image:
              'https://img2.baidu.com/it/u=2442436610,2127871497&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333'
          },
          {
            id: '102',
            name: '奔驰 E级',
            price: '42.98-62.98万',
            image:
              'https://img1.baidu.com/it/u=3257632527,1396293257&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333'
          },
          {
            id: '103',
            name: '奔驰 S级',
            price: '93.80-165.80万',
            image:
              'https://img0.baidu.com/it/u=3224648955,1193166630&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333'
          }
        ]
        brand.value.features = [
          {
            title: '豪华舒适',
            description: '奔驰以卓越的舒适性和豪华内饰著称，为驾乘者提供极致体验'
          },
          { title: '安全科技', description: '领先的安全技术和驾驶辅助系统，确保驾乘安全' },
          { title: '精工制造', description: '德国工艺精神的代表，每一个细节都彰显品质' }
        ]
        brand.value.history =
          '梅赛德斯-奔驰是世界上最著名的汽车品牌之一，拥有超过130年的历史。1886年，卡尔·本茨发明了世界上第一辆汽车，开创了汽车工业的先河。多年来，奔驰一直致力于创新和卓越，成为豪华汽车的代名词。'
        brand.value.foundYear = '1886'
        brand.value.country = '德国'
      } else if (brand.value.id === '2') {
        // 假设id为2是奥迪
        brand.value.models = [
          {
            id: '201',
            name: '奥迪 A4L',
            price: '30.98-39.68万',
            image:
              'https://img2.baidu.com/it/u=3519522381,1341358140&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333'
          },
          {
            id: '202',
            name: '奥迪 A6L',
            price: '41.98-65.38万',
            image:
              'https://img0.baidu.com/it/u=2900496193,3583187074&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333'
          },
          {
            id: '203',
            name: '奥迪 Q5L',
            price: '39.68-48.38万',
            image:
              'https://img1.baidu.com/it/u=3789867991,1930019998&fm=253&fmt=auto&app=138&f=JPEG?w=500&h=333'
          }
        ]
        brand.value.features = [
          { title: '科技前沿', description: '奥迪以尖端科技和创新设计著称，引领汽车技术发展' },
          { title: '卓越性能', description: 'quattro四驱系统和高效动力总成，提供出色驾驶体验' },
          { title: '精致设计', description: '简约而不简单的设计语言，彰显现代豪华气质' }
        ]
        brand.value.history =
          '奥迪汽车公司成立于1909年，由August Horch创立。"奥迪"一词源自拉丁语"audi"，意为"听"，象征着奥迪对声音和音乐的高品质追求。多年来，奥迪一直致力于创新和卓越，成为豪华汽车的领导者之一。'
      }
    }
    loading.value = false
    // 关闭加载动画
    loadingInstance.close()
  } catch (error) {
    ElMessage.error('获取品牌信息失败')
    loading.value = false
    // 关闭加载动画
    loadingInstance.close()
  }
})
</script>

<style lang="scss" scoped>
.brand-detail {
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
  position: relative;
  min-height: 500px;

  @media (max-width: 768px) {
    padding: 20px 15px;
  }

  h2 {
    font-size: 2rem;
    color: #303133;
    margin: 50px 0 30px;
    position: relative;
    padding-bottom: 15px;

    &:after {
      content: '';
      position: absolute;
      bottom: 0;
      left: 0;
      width: 60px;
      height: 3px;
      background-color: #3498db;
    }

    @media (max-width: 768px) {
      font-size: 1.5rem;
      margin: 30px 0 20px;
    }
  }

  .brand-intro {
    display: flex;
    align-items: center;
    margin-bottom: 60px;
    padding: 40px;
    background: #f5f7fa;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

    @media (max-width: 768px) {
      flex-direction: column;
      text-align: center;
      padding: 30px 20px;
    }

    .brand-logo {
      height: 100px;
      margin-right: 40px;
      object-fit: contain;

      @media (max-width: 768px) {
        margin-right: 0;
        margin-bottom: 20px;
      }
    }

    .brand-info {
      flex: 1;

      h1 {
        font-size: 2.5rem;
        color: #303133;
        margin: 0 0 20px;

        @media (max-width: 768px) {
          font-size: 2rem;
        }
      }

      .description {
        font-size: 1.2rem;
        color: #606266;
        margin: 0 0 20px;
        line-height: 1.6;

        @media (max-width: 768px) {
          font-size: 1rem;
        }
      }

      .brand-stats {
        display: flex;
        margin-top: 20px;

        @media (max-width: 768px) {
          justify-content: center;
        }

        .stat-item {
          margin-right: 40px;
          text-align: center;

          &:last-child {
            margin-right: 0;
          }

          .stat-value {
            display: block;
            font-size: 1.8rem;
            font-weight: bold;
            color: #3498db;
          }

          .stat-label {
            display: block;
            font-size: 0.9rem;
            color: #909399;
            margin-top: 5px;
          }
        }
      }
    }
  }

  .brand-history {
    background: white;
    border-radius: 12px;
    padding: 30px;
    margin-bottom: 60px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);

    p {
      color: #606266;
      line-height: 1.8;
      font-size: 1.1rem;
      text-align: justify;

      @media (max-width: 768px) {
        font-size: 1rem;
      }
    }
  }

  .model-list {
    margin-bottom: 60px;

    .empty-models {
      padding: 40px 0;
    }
  }

  .brand-features {
    margin-bottom: 60px;

    .feature-card {
      background: white;
      border-radius: 12px;
      padding: 30px;
      text-align: center;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
      height: 100%;
      transition: transform 0.3s;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
      }

      .feature-icon {
        font-size: 2.5rem;
        color: #3498db;
        margin-bottom: 20px;
      }

      h3 {
        font-size: 1.3rem;
        color: #303133;
        margin: 0 0 15px;
      }

      p {
        color: #606266;
        line-height: 1.6;
        margin: 0;
      }
    }
  }
}
</style>
