<template>
  <div class="brands">
    <div class="brand-header">
      <h1>品牌专区</h1>
      <p class="subtitle">选择您感兴趣的汽车品牌，探索更多车型</p>
    </div>

    <div class="brand-list">
      <el-row :gutter="40">
        <el-col v-for="brand in brands" :key="brand.id" :xs="24" :sm="12" :md="12" :lg="12">
          <brand-card :brand="brand" @click="router.push(`/brands/${brand.id}`)" />
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import BrandCard from '@/views/brands/components/BrandCard.vue'

const router = useRouter()

// 固定展示奔驰和奥迪两个品牌，不需要进行请求
const brands = ref([
  {
    id: 1,
    name: '奔驰',
    description: '德国豪华汽车品牌，以卓越工艺和创新科技著称',
    logo: 'https://assets.oneweb.mercedes-benz.com.cn/global-header/img/icon_wev_benz@2x.png'
  },
  {
    id: 2,
    name: '奥迪',
    description: '德国高端汽车制造商，以先进技术和精湛设计闻名',
    logo: 'https://pic.616pic.com/ys_bnew_img/00/04/08/yk2MlNzhYS.jpg'
  }
])
</script>

<style lang="scss" scoped>
.brands {
  padding: 20px 40px;
  max-width: 1200px;
  margin: 0 auto;
  background-color: #f5f5f5;

  .brand-header {
    text-align: center;
    margin-bottom: 20px;

    h1 {
      font-size: 3rem;
      color: #303133;
      margin-bottom: 20px;
      font-weight: 600;
    }

    .subtitle {
      color: #606266;
      font-size: 1.4rem;
      max-width: 700px;
      margin: 0 auto;
      line-height: 1.6;
    }
  }

  .brand-list {
    padding: 8px 0;

    .el-row {
      margin-bottom: 8px;
    }

    .el-col {
      margin-bottom: 8px;
      height: 100%;
    }
  }
}
</style>
