<template>
  <AuthLayout :loading="loading">
    <!-- 表单标题 -->
    <h2 class="form-title">验证码登录</h2>

    <!-- 登录区域 -->
    <div class="account-login">
      <!-- 手机号输入框 + 发送按钮 -->
      <div class="form-group">
        <label class="form-label">手机号</label>
        <div class="input-with-btn">
          <input type="tel" class="form-input" placeholder="请输入手机号" v-model="loginForm.mobile" maxlength="11" />
          <button class="verify-btn" @click="send" :disabled="!canSendCode || countdown > 0">
            {{ countdown > 0 ? `${countdown}s后重发` : "发送验证码" }}
          </button>
        </div>
      </div>

      <!-- 验证码输入框 -->
      <div class="form-group">
        <label class="form-label">验证码</label>
        <input type="text" class="form-input" placeholder="请输入6位验证码" v-model="loginForm.verifyCode" maxlength="6" />
      </div>

      <!-- 登录按钮 -->
      <button class="login-btn" @click="handleSubmit">登录</button>

      <!-- 底部链接 -->
      <div class="login-action-group">
        <div class="phone-login">
          <router-link to="/login" class="link">密码登录</router-link>
        </div>
        <div class="register-link">
          还没有账号？<router-link to="/register" class="link">立即注册</router-link>
        </div>
      </div>
    </div>
  </AuthLayout>
</template>

<script lang="ts" setup>
import {computed, onUnmounted, reactive, ref} from "vue";
import {Log} from "../utils/TipUtil";
import {userStore} from "../store/UserStore";
import AuthLayout from "../components/AuthLayout.vue";

const loading = ref(false);
const user = userStore();

// 表单数据
const loginForm = reactive({ mobile: "", verifyCode: "" });
// 验证码倒计时
const countdown = ref(0);
let timer: ReturnType<typeof setInterval> | null = null;

// 手机号格式校验
const isMobileValid = computed(() => /^1[3-9]\d{9}$/.test(loginForm.mobile));
// 是否可发送验证码
const canSendCode = computed(
  () => isMobileValid.value && countdown.value === 0
);

// 发送验证码
const send = async () => {
  if (loading.value) return;
  try {
    loading.value = true;
    if (!isMobileValid.value) return Log.error("请输入正确手机号");
    // 这里的检查稍微冗余但安全
    if (countdown.value > 0) return;

    if (await user.sendVerifyCode(loginForm.mobile)) {
      loading.value = false;
      // 启动倒计时（60秒）
      countdown.value = 60;
      timer = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
          clearInterval(timer!);
          timer = null;
        }
      }, 1000);
    }
  } finally {
    loading.value = false;
  }

};

// 登录提交
const handleSubmit = async () => {
  try {
    if (loading.value) return;
    loading.value = true;
    if (!isMobileValid.value) return Log.error("请输入正确手机号");
    if (!loginForm.verifyCode) return Log.error("请输入验证码");
    await user.login_sms(loginForm);
  } finally {
    loading.value = false;
  }
}

  // 组件卸载时清除定时器
  onUnmounted(() => timer && clearInterval(timer));
</script>

<style scoped>
/* 3.2 表单标题 */
.form-title {
  margin-bottom: 32px;
  font-size: 20px;
  color: #111827;
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 3.3 账号登录区域 */
.account-login {
  width: 90%;
  margin: 0 auto 30px;
}

.form-group {
  margin-bottom: 22px;
  text-align: left;
}

/* 输入框标签 */
.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #374151;
  font-weight: 500;
  margin-left: 4px;
}

/* 核心：手机号输入框+验证码按钮组合容器 */
.input-with-btn {
  position: relative;
  display: flex;
  align-items: center;
}

/* 输入框 */
.form-input {
  width: 100%;
  height: 48px;
  padding: 0 20px;
  background-color: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 24px;
  font-size: 15px;
  color: #1f2937;
  transition: all 0.3s ease;
  outline: none;
  backdrop-filter: blur(5px);
}

.form-input:hover {
  background-color: rgba(255, 255, 255, 0.95);
}

.form-input:focus {
  background-color: #fff;
  border-color: #22c55e;
  box-shadow: 0 4px 15px rgba(34, 197, 94, 0.15);
  transform: translateY(-1px);
}

.form-input::placeholder {
  color: #9ca3af;
  font-size: 14px;
}

/* 验证码按钮 */
.verify-btn {
  position: absolute;
  right: 6px;
  top: 6px;
  bottom: 6px;
  padding: 0 16px;
  border: none;
  border-radius: 20px;
  background: #e0f2f1;
  color: #059669;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.verify-btn:hover:not(:disabled) {
  background: #d1fae5;
  color: #047857;
}

.verify-btn:disabled {
  background-color: transparent;
  color: #9ca3af;
  cursor: not-allowed;
  opacity: 0.6;
}


.verify-btn:hover:not(:disabled) {
  background-color: #f0fdf4;
  border-color: #16a34a;
  box-shadow: 0 2px 8px rgba(34, 197, 94, 0.1);
}

/* 3.5 登录按钮 */
.login-btn {
  width: 100%;
  height: 48px;
  background-color: #fff;
  border: 1px solid #22c55e;
  border-radius: 8px;
  font-size: 16px;
  color: #22c55e;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 24px;
  /* 增加底部间距 */
}

.login-btn:hover {
  background-color: #f9fafb;
  border-color: #16a34a;
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.1);
}

.login-btn:active {
  transform: scale(0.98);
}

/* 底部链接区域 */
.login-action-group {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
}

.register-link {
  color: #4a5568;
}

.link {
  font-size: 13px;
  color: #22c55e;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.link:hover {
  color: #16a34a;
}
</style>
