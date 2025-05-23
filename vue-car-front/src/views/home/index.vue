<template>
  <div class="home">
    <div class="home-content">
      <!-- 英雄区域 -->
      <div class="hero">
        <h1>探索汽车的未来</h1>
        <p>通过3D技术和虚拟现实，体验前所未有的汽车展示方式</p>
        <el-button type="primary" size="large" @click="router.push('/brands')">开始探索</el-button>
      </div>

      <!-- 特色功能区域 -->
      <div class="features">
        <el-row :gutter="40">
          <el-col :xs="24" :sm="8">
            <div class="feature-card">
              <div class="feature-icon">🔍</div>
              <h3>3D展示</h3>
              <p>360°全方位展示各类汽车型号，让您足不出户即可感受真实车型的每个细节。</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="feature-card">
              <div class="feature-icon">📱</div>
              <h3>AR体验</h3>
              <p>创新的增强现实技术，让您能够在真实环境中预览汽车效果，带来身临其境的互动体验。</p>
            </div>
          </el-col>
          <el-col :xs="24" :sm="8">
            <div class="feature-card">
              <div class="feature-icon">💬</div>
              <h3>社区交流</h3>
              <p>加入我们的汽车爱好者社区，分享您的用车体验，获取最新的汽车资讯和专业建议。</p>
            </div>
          </el-col>
        </el-row>
      </div>

      <!-- 热门车型区域 -->
      <div class="hot-models">
        <h2 class="section-title">热门车型</h2>
        <el-carousel
          :interval="4000"
          type="card"
          height="320px"
          indicator-position="outside"
          arrow="always"
        >
          <el-carousel-item v-for="(item, index) in hotModels" :key="index">
            <div class="model-card carousel-card">
              <img class="model-image" :src="item.image">{{ item.name }}</img>
              <div class="model-info">
                <!-- <h3>{{ item.name }}</h3>
                <p>{{ item.description }}</p> -->
                <!-- <el-button type="primary" @click="router.push(item.link)">查看详情</el-button> -->
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ref } from 'vue'

const router = useRouter()

const hotModels = ref([
  {
    name: 'Audi A8L',
    description: '豪华轿车的典范之作',
    image: './img/1.jpg',
    link: '/models/1'
  },
  {
    name: 'Audi A6L',
    description: '科技与优雅的完美结合',
    image: 'img/2.jpg',
    link: '/models/2'
  },
  {
    name: '奔驰 GLC',
    description: '豪华SUV的新标杆',
    image: 'img/3.jpg',
    link: '/models/3'
  }
])
</script>

<style lang="scss" scoped>
.home {
  min-height: 100vh;
  margin: 0 auto;
  background-color: #f5f5f5;
  color: #333;
  line-height: 1.6;

  .home-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 20px;

    // 英雄区域样式
    .hero {
      background-image:
        linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)),
        url('https://images.unsplash.com/photo-1503376780353-7e6692767b70?ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80');
      background-size: cover;
      background-position: center;
      color: white;
      text-align: center;
      padding: 100px 20px;
      border-radius: 8px;
      margin-bottom: 40px;

      h1 {
        font-size: 48px;
        margin-bottom: 20px;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
      }

      p {
        font-size: 20px;
        margin-bottom: 30px;
        max-width: 700px;
        margin-left: auto;
        margin-right: auto;
      }

      .el-button {
        padding: 12px 24px;
        font-size: 16px;
        &.el-button--primary {
          background-color: #3498db;
          border-color: #3498db;
          &:hover {
            background-color: #2980b9;
            border-color: #2980b9;
          }
        }
      }
    }

    // 特色功能区域样式
    .features {
      margin: 40px 0;
      background-color: white;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
      padding: 30px;

      .feature-card {
        background-color: white;
        border-radius: 8px;
        padding: 30px;
        text-align: center;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        transition: transform 0.3s;

        &:hover {
          transform: translateY(-5px);
        }

        .feature-icon {
          font-size: 40px;
          color: #3498db;
          margin-bottom: 20px;
        }

        h3 {
          font-size: 20px;
          margin-bottom: 15px;
          color: #333;
        }

        p {
          color: #666;
          line-height: 1.6;
        }
      }
    }

    // 热门车型区域样式
    .hot-models {
      margin: 40px 0;
      background-color: white;
      border-radius: 8px;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
      padding: 30px;

      .section-title {
        font-size: 24px;
        margin-bottom: 20px;
        color: #333;
        border-bottom: 2px solid #3498db;
        padding-bottom: 10px;
      }

      // 轮播图样式
      .el-carousel {
        margin-top: 30px;

        .el-carousel__indicators {
          margin-top: 15px;
        }

        .el-carousel__arrow {
          background-color: rgba(52, 152, 219, 0.7);
          &:hover {
            background-color: rgba(41, 128, 185, 0.9);
          }
        }
      }

      .carousel-card {
        height: 100%;
        margin-bottom: 0;
      }

      .model-card {
        background-color: white;
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
        transition: transform 0.3s;
        cursor: pointer;
        margin-bottom: 20px;

        &:hover {
          transform: translateY(-5px);
        }

        .model-image {
          height: 300px;
          background-color: #f0f0f0;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 18px;
          color: #666;
        }

        .model-info {
          padding: 20px;

          h3 {
            margin-bottom: 10px;
            color: #333;
          }

          p {
            color: #666;
            margin-bottom: 15px;
          }

          .el-button {
            background-color: #3498db;
            border-color: #3498db;
            &:hover {
              background-color: #2980b9;
              border-color: #2980b9;
            }
          }
        }
      }
    }
  }
}
</style>
