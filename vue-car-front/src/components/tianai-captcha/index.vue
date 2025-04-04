<template>
  <div>
    <div id="lihua-tian-captcha" class="tian-captcha"/>
    <div class="mask" v-if="showMask" @click.stop></div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

// 请求url前缀
const baseURL = '/api'
// 是否打开遮罩
const showMask = ref(false)

// 抛出的方法：校验成功/校验失败/刷新/关闭
const emit = defineEmits(['success', 'fail', 'refresh', 'close'])

// 向外部抛出函数
defineExpose({
  // 显示验证码
  show: () => {
    load()
  },
  // 关闭验证码
  close: () => {
    destroy()
  },
  // 刷新验证码
  refresh: () => {
    if (window.tac) {
      window.tac.reloadCaptcha()
    }
  }
})

// 验证码配置
const captchaConfig = {
  // 请求验证码接口
  requestCaptchaDataUrl: baseURL + "/captcha/get",
  // 验证验证码接口
  validCaptchaUrl: baseURL + "/captcha/check",
  // 绑定div
  bindEl: "#lihua-tian-captcha",
  // 验证成功回调
  validSuccess: (res, c, tac) => {
    emit('success', res.data.id)
    showMask.value = false
    tac.destroyWindow()
  },
  // 验证失败回调
  validFail: (res, c, tac) => {
    emit('fail', res)
    tac.reloadCaptcha()
  },
  // 刷新按钮回调事件
  btnRefreshFun: (el, tac) => {
    emit('refresh')
    tac.reloadCaptcha()
  },
  // 关闭按钮回调事件
  btnCloseFun: (el, tac) => {
    emit('close')
    showMask.value = false
    tac.destroyWindow()
  }
}

// 主题样式
const style = {
  // 按钮样式
  btnUrl: "/tac/images/btn.png",
  // 背景样式
  bgUrl: null,
  // logo地址
  logoUrl: null,
  // 滑块槽背景颜色
  moveTrackMaskBgColor: "#f7b645",
  // 滑块槽边框颜色
  moveTrackMaskBorderColor: "#ef9c0d"
}

// 加载验证码
const load = () => {
  showMask.value = true
  window.initTAC("tac", captchaConfig, style).then((tac) => {
    // 初始化验证码
    tac.init();
  })
}

// 销毁验证码
const destroy = () => {
  if (window.tac) {
    window.tac.destroyWindow()
  }
  showMask.value = false
}
</script>

<style scoped>
.tian-captcha {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 1000;
}

.mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
}

/* 滑块验证码样式 */
:deep(#tianai-captcha-parent) {
  border-radius: 4px;
  overflow: hidden;
}

:deep(#tianai-captcha-slider-move-track-font) {
  color: #fff;
}

:deep(#tianai-captcha .content .tianai-captcha-tips.tianai-captcha-tips-success) {
  color: #67c23a;
}

:deep(#tianai-captcha .content .tianai-captcha-tips.tianai-captcha-tips-error) {
  color: #f56c6c;
}
</style>