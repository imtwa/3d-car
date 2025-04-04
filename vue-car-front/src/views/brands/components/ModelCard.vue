<template>
  <div class="model-card">
    <div class="model-image">
      <img :src="model.image" :alt="model.name" />
    </div>
    <h3>{{ model.name }}</h3>
    <p class="price">{{ model.price }}</p>
    <el-button type="primary" @click="$emit('showView')">查看详情</el-button>
  </div>
</template>

<script setup>
defineProps({
  model: {
    type: Object,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.model-card {
  background: white;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  margin-bottom: 30px;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }

  .model-image {
    height: 180px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;

    img {
      max-width: 100%;
      max-height: 100%;
      object-fit: contain;
    }
  }

  h3 {
    font-size: 1.5rem;
    color: #303133;
    margin: 0 0 15px;
  }

  .price {
    color: #f56c6c;
    font-size: 1.2rem;
    margin-bottom: 20px;
  }

  .el-button {
    width: 120px;
  }
}
</style>
