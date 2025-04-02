<template>
  <el-dialog
    v-model="dialogVisible"
    title="欢迎登录"
    width="400px"
    :close-on-click-modal="false"
    :show-close="false"
  >
    <div class="login-title">
      <h2>欢迎登录</h2>
      <div v-if="enableRegister">
        <el-text>没有账户？</el-text>
        <el-link type="primary" @click="switchToRegister">快速注册</el-link>
      </div>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleSubmit">
      <el-form-item prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="form.password"
          type="password"
          show-password
          placeholder="请输入密码"
          :prefix-icon="Lock"
        />
      </el-form-item>
      <el-form-item>
        <div class="login-options">
          <el-checkbox v-model="rememberMe">记住账号</el-checkbox>
          <el-link type="primary" :underline="false">忘记密码？</el-link>
        </div>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading" class="submit-btn"
          >登录</el-button
        >
      </el-form-item>
    </el-form>
    <tianai-captcha ref="captchaRef" @success="handleCaptchaSuccess" />
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import TianaiCaptcha from './tianai-captcha/index.vue'

const props = defineProps({
  modelValue: Boolean
})

const emit = defineEmits(['update:modelValue', 'login-success', 'switch-to-register'])

const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)
const captchaRef = ref(null)
const rememberMe = ref(false)
const enableRegister = ref(true) // 启用注册功能

const dialogVisible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async valid => {
    if (valid) {
      if (captchaRef.value) {
        captchaRef.value.show()
      }
    }
  })
}

const handleCaptchaSuccess = async captchaId => {
  loading.value = true
  try {
    const success = await userStore.login({
      username: form.username,
      password: form.password,
      captchaId
    })
    if (success) {
      ElMessage.success('登录成功')
      dialogVisible.value = false
      emit('login-success')

      // 如果勾选了记住账号，保存用户名到本地存储
      if (rememberMe.value) {
        localStorage.setItem('rememberedUsername', form.username)
      } else {
        localStorage.removeItem('rememberedUsername')
      }
    } else {
      ElMessage.error('登录失败')
      if (captchaRef.value) {
        captchaRef.value.refresh()
      }
    }
  } finally {
    loading.value = false
  }
}

const switchToRegister = () => {
  dialogVisible.value = false
  emit('switch-to-register')
}

// 初始化时检查是否有记住的用户名
const initRememberedUsername = () => {
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  if (rememberedUsername) {
    form.username = rememberedUsername
    rememberMe.value = true
  }
}

// 组件挂载时初始化
initRememberedUsername()
</script>

<style scoped>
.login-title {
  text-align: center;
  margin-bottom: 24px;
}

.login-title h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.submit-btn {
  width: 100%;
}
</style>
