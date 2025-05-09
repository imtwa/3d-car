<template>
  <el-dialog
    v-model="dialogVisible"
    title="用户注册"
    width="400px"
    :close-on-click-modal="false"
    :show-close="false"
  >
    <div class="register-title">
      <h2>欢迎注册</h2>
      <div>
        <el-text>已有账户？</el-text>
        <el-link type="primary" @click="handleLogin">前往登录</el-link>
      </div>
    </div>
    <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleSubmit">
      <el-form-item prop="username">
        <el-input
          v-model="form.username"
          placeholder="请输入用户名"
          :prefix-icon="User"
          @blur="checkUsernameExist"
        />
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
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="form.confirmPassword"
          type="password"
          show-password
          placeholder="请再次输入密码"
          :prefix-icon="Lock"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSubmit" :loading="loading" class="submit-btn"
          >注册</el-button
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

const emit = defineEmits(['update:modelValue', 'register-success', 'switch-to-login'])

const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)
const captchaRef = ref(null)

const dialogVisible = computed({
  get: () => props.modelValue,
  set: value => emit('update:modelValue', value)
})

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

// 验证两次密码是否一致
const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 验证用户名是否存在
const checkUsernameExist = async () => {
  if (form.username && form.username.length >= 3) {
    const isAvailable = await userStore.checkUsername(form.username)

    if (isAvailable) {
      ElMessage.warning('该用户名已被使用')
      return false
    }
    return true
  }
  return false
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9@.]+$/, message: '用户名只允许大小写英文、数字、@、.', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ]
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async valid => {
    if (valid) {
      const usernameValid = await checkUsernameExist()
      if (!usernameValid) return

      if (captchaRef.value) {
        captchaRef.value.show()
      }
    }
  })
}

const handleCaptchaSuccess = async captchaId => {
  loading.value = true
  try {
    const result = await userStore.register({
      username: form.username,
      password: form.password,
      confirmPassword: form.confirmPassword,
      captchaId
    })

    if (result.success) {
      ElMessage.success('注册成功，请登录')
      dialogVisible.value = false
      emit('register-success', form.username)
    } else {
      ElMessage.error(result.message || '注册失败')
      if (captchaRef.value) {
        captchaRef.value.refresh()
      }
    }
  } finally {
    loading.value = false
  }
}

const handleLogin = () => {
  dialogVisible.value = false
  emit('switch-to-login')
}
</script>

<style scoped>
.register-title {
  text-align: center;
  margin-bottom: 24px;
}

.register-title h2 {
  margin: 0 0 8px;
  font-size: 24px;
  color: var(--el-text-color-primary);
}

.submit-btn {
  width: 100%;
}
</style>
