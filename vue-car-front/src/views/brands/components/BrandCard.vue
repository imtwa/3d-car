<template>
  <div class="brand-card" @click="$emit('click')">
    <div class="brand-logo">
      <img :src="brand.logo" :alt="brand.name" />
    </div>
    <div class="brand-info">
      <h3>{{ brand.name }}</h3>
      <p>{{ brand.description }}</p>
      <el-button type="primary" class="view-more-btn">查看更多</el-button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  brand: {
    type: Object,
    required: true
  }
})
</script>

<style lang="scss" scoped>
.brand-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
  cursor: pointer;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
  height: 100%;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  }

  .brand-logo {
    padding: 40px;
    background: #f5f7fa;
    text-align: center;
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;

    img {
      height: 120px;
      max-width: 80%;
      object-fit: contain;
    }
  }

  .brand-info {
    padding: 30px;
    text-align: center;

    h3 {
      margin: 0 0 15px;
      color: #303133;
      font-size: 1.8rem;
      font-weight: 600;
    }

    p {
      color: #606266;
      font-size: 1rem;
      line-height: 1.6;
      margin-bottom: 20px;
    }

    .view-more-btn {
      margin-top: 8px;
      width: 120px;
      font-size: 1rem;
    }
  }
}
</style>
