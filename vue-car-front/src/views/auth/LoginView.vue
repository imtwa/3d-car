<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>{{ isRegister ? '注册' : '登录' }}</h2>
      </div>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="0"
        class="login-form"
        @keyup.enter="handleSubmit"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="用户名" 
            :prefix-icon="User" 
            @blur="isRegister && checkUsernameExist()"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <el-form-item v-if="isRegister" prop="confirmPassword">
          <el-input
            v-model="form.confirmPassword"
            type="password"
            placeholder="确认密码"
            :prefix-icon="Lock"
            show-password
          />
        </el-form-item>
        <div class="login-btn">
          <el-button type="primary" :loading="loading" @click="handleSubmit">
            {{ isRegister ? '注册' : '登录' }}
          </el-button>
        </div>
        <div class="login-options">
          <el-link type="primary" @click="toggleMode">
            {{ isRegister ? '已有账号？去登录' : '没有账号？去注册' }}
          </el-link>
        </div>
      </el-form>
    </div>
    <tianai-captcha ref="captchaRef" @success="handleCaptchaSuccess" />
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'
import TianaiCaptcha from '@/components/tianai-captcha/index.vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const captchaRef = ref(null)
const loading = ref(false)
const isRegister = ref(false)

const form = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

// 验证用户名是否存在
const checkUsernameExist = async () => {
  if (form.username && form.username.length >= 3) {
    const isAvailable = await userStore.checkUsername(form.username)
    if (!isAvailable) {
      ElMessage.warning('该用户名已被使用')
      return false
    }
    return true
  }
  return false
}

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

const rules = computed(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9@.]+$/, message: '用户名只允许大小写英文、数字、@、.', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  ...(isRegister.value
    ? {
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validatePass, trigger: 'blur' }
        ]
      }
    : {})
}))

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async valid => {
    if (valid) {
      if (isRegister.value) {
        // 注册时先检查用户名是否可用
        const usernameValid = await checkUsernameExist()
        if (!usernameValid) return
      }
      
      // 显示滑块验证码
      if (captchaRef.value) {
        captchaRef.value.show()
      }
    }
  })
}

const handleCaptchaSuccess = async (captchaId) => {
  loading.value = true
  try {
    if (isRegister.value) {
      // 注册
      const result = await userStore.register({
        username: form.username,
        password: form.password,
        confirmPassword: form.confirmPassword,
        captchaId
      })
      
      if (result.success) {
        ElMessage.success('注册成功，请登录')
        isRegister.value = false
        form.password = ''
        form.confirmPassword = ''
      } else {
        ElMessage.error(result.message || '注册失败')
        if (captchaRef.value) {
          captchaRef.value.refresh()
        }
      }
    } else {
      // 登录
      const success = await userStore.login({
        username: form.username,
        password: form.password,
        captchaId
      })
      
      if (success) {
        ElMessage.success('登录成功')
        router.push('/')
      } else {
        if (captchaRef.value) {
          captchaRef.value.refresh()
        }
      }
    }
  } finally {
    loading.value = false
  }
}

const toggleMode = () => {
  isRegister.value = !isRegister.value
  form.password = ''
  form.confirmPassword = ''
  if (formRef.value) {
    formRef.value.clearValidate()
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #8bc6ec 0%, #9599e2 100%);

  .login-box {
    width: 400px;
    padding: 40px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  }

  .login-header {
    text-align: center;
    margin-bottom: 30px;

    h2 {
      margin: 0;
      color: #303133;
      font-size: 24px;
    }
  }

  .login-form {
    .login-btn {
      width: 100%;
      margin-top: 20px;

      .el-button {
        width: 100%;
      }
    }
  }

  .login-options {
    margin-top: 15px;
    text-align: center;
  }
}
</style>
