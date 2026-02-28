<template>
  <AuthLayout :loading="loading">
    <!-- 2.2 表单标题 -->
    <h2 class="form-title">账号注册</h2>

    <!-- 2.3 注册表单区域（居中对齐） -->
    <div class="account-register">
      <!-- 手机号输入框（带发送验证码按钮） -->
      <div class="form-group">
        <label class="form-label">手机号</label>
        <div class="input-with-btn">
          <input type="tel" class="form-input" placeholder="请输入手机号" v-model="registerData.mobile" maxlength="11"
            @input="handleMobileInput" />
          <button class="verify-btn" @click="sendVerifyCode" :disabled="!isMobileValid || isCounting">
            {{ isCounting ? `${countDown}秒后重发` : "发送验证码" }}
          </button>
        </div>
      </div>

      <!-- 验证码输入框 -->
      <div class="form-group">
        <label class="form-label">验证码</label>
        <input type="text" class="form-input" placeholder="请输入6位验证码" v-model="registerData.verifyCode" maxlength="6" />
      </div>

      <!-- 密码输入框（可选） -->
      <div class="form-group">
        <label class="form-label">设置密码（可选）</label>
        <input type="password" class="form-input" placeholder="6-18位字母/数字/符号组合" v-model="registerData.pw" />
      </div>

      <!-- 昵称输入框（可选） -->
      <div class="form-group">
        <label class="form-label">设置昵称（可选）</label>
        <input type="text" class="form-input" placeholder="请输入昵称" v-model="registerData.nickName" maxlength="12" />
      </div>

      <!-- 功能按钮组（注册+清空） -->
      <div class="btn-group">
        <button class="register-btn" @click="commitRegister">注册</button>
      </div>
    </div>

    <!-- 2.4 登录跳转入口（底部居中） -->
    <div class="login-link">
      已有账号？<router-link to="/login" class="link">去登录</router-link>
    </div>
  </AuthLayout>
</template>

<script lang="ts" setup>
import {onUnmounted, reactive, ref} from "vue";
import type {register} from "../types/register";
import {Log} from "../utils/TipUtil";
import {userStore} from "../store/UserStore";
import {RsaUtil} from "@/utils/RsaUtil";
import AuthLayout from "../components/AuthLayout.vue";

const loading = ref(false);
const user = userStore();
// 注册表单数据
const registerData = reactive<register>({
  mobile: "",
  verifyCode: "",
  pw: "",
  nickName: "",
  icon: "",
});

// 验证码倒计时相关状态
const isCounting = ref(false); // 是否正在倒计时
const countDown = ref(60); // 倒计时秒数
const isMobileValid = ref(false); // 手机号格式是否合法
let countDownTimer: ReturnType<typeof setInterval> | null = null;

// 监听手机号输入，验证格式（11位数字）
const handleMobileInput = (e: Event) => {
  const input = e.target as HTMLInputElement;
  const mobile = input.value.trim();
  // 正则验证手机号格式
  isMobileValid.value = /^1[3-9]\d{9}$/.test(mobile);
};

// 倒计时逻辑
const startCountDown = () => {
  isCounting.value = true;
  countDown.value = 60;

  // 清除已有定时器（防止重复触发）
  if (countDownTimer) clearInterval(countDownTimer);

  countDownTimer = setInterval(() => {
    countDown.value--;
    if (countDown.value <= 0) {
      clearInterval(countDownTimer!);
      isCounting.value = false;
    }
  }, 1000);
};

async function sendVerifyCode() {
  if (loading.value) return;
  try {
    loading.value = true;
    if (await user.sendVerifyCode(registerData.mobile)) {
      startCountDown();
    }
  } finally {
    loading.value = false;
  }

}

// 提交注册逻辑
const commitRegister = async () => {
  if (loading.value) return;
  try {
    loading.value = true;
    // 基础表单验证
    if (!registerData.mobile) return Log.warn("请输入手机号");
    if (!isMobileValid.value) return Log.warn("请输入正确的手机号");
    if (!registerData.verifyCode) return Log.warn("请输入验证码");
    if (registerData.verifyCode.length !== 6) return Log.warn("请输入6位验证码");

    // 构造提交数据，对密码进行加密，避免修改原响应式对象导致二次加密问题
    const submitData = { ...registerData };
    if (submitData.pw) {
      submitData.pw = await RsaUtil.encrypt(submitData.pw);
    }

    await user.register(submitData);
  } finally {
    loading.value = false;
  }
};

// 组件卸载时清除定时器（防止内存泄漏）
onUnmounted(() => {
  if (countDownTimer) clearInterval(countDownTimer);
});

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

/* 3.3 注册表单区域：基础布局与登录页一致 */
.account-register {
  width: 90%;
  margin: 0 auto 30px;
}

.form-group {
  margin-bottom: 22px;
  text-align: left;
}

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

/* 输入框：所有输入框样式统一（包括手机号输入框） */
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

/* 验证码按钮：风格与输入框协调，尺寸匹配 */
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

.verify-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.verify-btn:hover:not(:disabled) {
  background: #d1fae5;
  color: #047857;
}

.avatar-uploader .avatar {
  width: 50px;
  height: 50px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 50px;
  height: 50px;
  text-align: center;
}

/* 功能按钮组：注册+清空按钮横向排列 */
.btn-group {
  display: flex;
  gap: 12px;
  margin-top: 10px;
}

/* 注册按钮：主按钮风格，与登录按钮一致 */
.register-btn {
  flex: 1;
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
}

.register-btn:hover {
  background-color: #f9fafb;
  border-color: #16a34a;
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.1);
}

.register-btn:active {
  transform: scale(0.98);
}

/* 清空按钮：次要按钮风格，与注册按钮区分 */
.reset-btn {
  flex: 1;
  height: 48px;
  background-color: #f9fafb;
  border: 1px solid #d1d5db;
  border-radius: 8px;
  font-size: 16px;
  color: #4b5563;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.reset-btn:hover {
  background-color: #f3f4f6;
  border-color: #9ca3af;
}

.reset-btn:active {
  transform: scale(0.98);
}

/* 3.4 登录跳转入口：与登录页注册入口风格统一 */
.login-link {
  font-size: 14px;
  color: #4a5568;
}

.link {
  font-size: 14px;
  /* 与登录入口文字尺寸统一 */
  color: #22c55e;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.link:hover {
  color: #16a34a;
}
</style>
