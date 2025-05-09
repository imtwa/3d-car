<template>
  <div class="rich-text-editor">
    <el-form-item :label="label" :prop="prop">
      <div class="editor-container">
        <el-input
          type="textarea"
          v-model="content"
          :placeholder="placeholder"
          :maxlength="maxLength"
          :rows="8"
          show-word-limit
          @input="updateContent"
        />
      </div>
    </el-form-item>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    default: '内容'
  },
  prop: {
    type: String,
    default: 'content'
  },
  placeholder: {
    type: String,
    default: '请输入内容...'
  },
  maxLength: {
    type: Number,
    default: 5000
  }
})

const emit = defineEmits(['update:modelValue'])

const content = ref(props.modelValue)

// 监听modelValue变化
watch(
  () => props.modelValue,
  newValue => {
    content.value = newValue
  }
)

// 更新内容
const updateContent = () => {
  emit('update:modelValue', content.value)
}
</script>

<style lang="scss" scoped>
.rich-text-editor {
  .editor-container {
    width: 100%;
  }
}
</style>
