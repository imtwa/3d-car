<template>
  <div>
    <div v-if="loading || error" class="car-ar-container">
      <div v-if="loading" class="loading-container">
        <el-loading :fullscreen="true" />
      </div>
      <div v-else-if="error" class="error-container">
        <el-result icon="error" :title="error" sub-title="请检查车型信息或返回首页">
          <template #extra>
            <el-button type="primary" @click="router.push('/')">返回首页</el-button>
          </template>
        </el-result>
      </div>
    </div>
    <component v-else-if="carModel" :is="modelComponent" :modelId="carModel.modelAttachmentId" />
  </div>
</template>

<script setup>
import { ref, shallowRef, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { defineAsyncComponent } from 'vue'
import { getCarModelSelectId } from '@/api/car'
import { ElLoading } from 'element-plus'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const error = ref('')
const carModel = ref(null) // 添加 carModel ref
const modelComponent = shallowRef(null)

// 获取车型信息并加载对应的3D模型组件
async function loadCarModel() {
  try {
    const carId = route.params.id
    if (!carId) {
      throw new Error('未找到车型ID')
    }

    // 获取车型信息
    const res = await getCarModelSelectId(carId)
    carModel.value = res.data

    if (!carModel.value || !carModel.value.modelVue) {
      throw new Error('未找到车型信息或3D模型')
    }

    // 动态导入3D模型组件
    const componentPath = carModel.value.modelVue.replace('@/', '')
    modelComponent.value = defineAsyncComponent({
      loader: () => import(`./components/${componentPath}.vue`),
      loadingComponent: ElLoading,
      errorComponent: {
        template: '<div class="error-message">加载3D模型失败</div>'
      },
      delay: 200,
      timeout: 30000
    })
  } catch (err) {
    error.value = err.message || '加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadCarModel()
})
</script>

<style scoped>
.car-ar-container {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f5f5;
}

.loading-container,
.error-container {
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.error-message {
  color: #f56c6c;
  font-size: 16px;
  text-align: center;
}
</style>
