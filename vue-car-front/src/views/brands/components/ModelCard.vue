<template>
  <div class="model-card" @click="$emit('show-view')">
    <div class="model-image" :style="backgroundImageStyle">
      <div class="model-overlay">
        <el-button type="primary" size="small" class="view-btn">查看详情</el-button>
      </div>
    </div>
    <div class="model-info">
      <h3>{{ model.name }}</h3>
      <p class="price">{{ model.price || '暂无价格' }}</p>
      <p class="description" v-if="model.description">{{ truncateText(model.description, 60) }}</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  model: {
    type: Object,
    required: true
  }
})

defineEmits(['show-view'])

// 截断文本函数，保留指定长度
const truncateText = (text, length) => {
  if (!text) return ''
  return text.length > length ? text.substring(0, length) + '...' : text
}
// 计算属性，用于动态设置背景图
const backgroundImageStyle = computed(() => {
  return {
    'background-image': `url(${props.model.coverImage})`
  }
})
</script>

<style lang="scss" scoped>
.model-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  transition: all 0.3s;
  margin-bottom: 30px;
  cursor: pointer;
  height: 100%;
  display: flex;
  flex-direction: column;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);

    .model-overlay {
      opacity: 1;
    }
  }

  .model-image {
    height: 220px;
    position: relative;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s;
    }

    &:hover img {
      transform: scale(1.05);
    }

    .model-overlay {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.4);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s;
    }

    .view-btn {
      background: #409eff;
      border: none;
      color: white;
      font-weight: 600;
      transition: background 0.3s;
      padding: 12px 24px;

      &:hover {
        background: #337ecc;
      }
    }
  }

  .model-info {
    padding: 20px;
    flex: 1;
    display: flex;
    flex-direction: column;

    h3 {
      font-size: 1.25rem;
      color: #303133;
      margin: 0 0 12px;
      font-weight: 600;
    }

    .price {
      color: #f56c6c;
      font-size: 1.1rem;
      margin-bottom: 15px;
      font-weight: 600;
    }

    .description {
      color: #606266;
      font-size: 0.9rem;
      line-height: 1.5;
      margin: 0;
      flex-grow: 1;
    }
  }
}
</style>
