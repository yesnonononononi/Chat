<template>
  <AuthLayout :loading="loading">
    <!-- 2.2 表单标题 -->
    <h2 class="form-title">账号登录</h2>

    <!-- 2.3 账号密码登录区域（居中对齐） -->
    <div class="account-login">
      <div class="form-group">
        <label class="form-label">手机号</label>
        <input type="text" class="form-input" placeholder="请输入手机号" maxlength="11" v-model="loginData.mobile" />
      </div>
      <div class="form-group">
        <label class="form-label">密码</label>
        <input type="password" class="form-input" placeholder="请输入密码" :value="loginData.pw" @input="loginData.pw = ($event.target as HTMLInputElement).value" />
      </div>

      <!-- 新增：登录操作区（手机号登录-左 / 忘记密码-右，对立布局） -->
      <div class="login-action-group">
        <!-- 手机号登录按钮（表单左下方） -->
        <div class="phone-login">
          <router-link to="/phone-login" class="link">手机号登录</router-link>
        </div>
        <!-- 忘记密码（右对齐，保持原有样式） -->
        <div class="forgot-password">
          <router-link to="/forget-pw" class="link">忘记密码？</router-link>
        </div>
      </div>

      <!-- 登录按钮（偏白色，突出交互） -->
      <button class="login-btn" @click="commit">登录</button>
    </div>

    <!-- 2.4 注册入口（底部居中，与表单整体协调） -->
    <div class="register-link">
      还没有账号？<router-link to="/register" class="link">立即注册</router-link>
    </div>
  </AuthLayout>
  <div class="w-full absolute bottom-[20px] text-center">
    <a href="https://beian.miit.gov.cn/" target="_blank" class="font-bold text-indigo-300 hover:underline">
        湘ICP备2026006047号
    </a>
</div>
</template>

<script lang="ts" setup>
import type {body} from "../types/auth";
import {computed, onMounted, reactive, ref} from "vue";
import {Log} from "../utils/TipUtil";
import {userStore} from "../store/UserStore";
import router from "../router";
import {BusinessError} from "@/exception/BusinessError";
import {RsaUtil} from "@/utils/RsaUtil";
import AuthLayout from "../components/AuthLayout.vue";

const loading = ref(false);
const user = userStore();
const loginData = reactive<body>({
  mobile: "",
  pw: "",
});

onMounted(() => {
  if (user && user.isLogin && user.userInfo && user.token) router.push("/");
});

const isMobileValid = computed(() =>
  /^1[3-9]\d{9}$/.test(String(loginData.mobile))
);
async function commit() {
  if (loading.value) return;
  try {
    loading.value = true;
    if (!isMobileValid.value) return Log.error("手机号格式错误");
    const submitData = { ...loginData };
    submitData.pw = await RsaUtil.encrypt(submitData.pw);  //公钥加密密码
    await user.login(submitData);
    loading.value = false;
  } catch (error) {
    if (error instanceof BusinessError) {
      Log.error(error.message);
    }
    else {
      Log.error("服务繁忙");
      console.error(error);
    }
  }
  finally {
    loading.value = false;

  }

}
</script>

<style scoped>
/* 3.2 表单标题：颜色柔和，与偏绿背景协调 */
.form-title {
  margin-bottom: 32px;
  font-size: 20px;
  color: #111827;
  /* 更深的灰黑色，提升质感 */
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 3.3 账号登录区域：确保内部元素居中对齐 */
.account-login {
  width: 90%;
  /* 控制输入框区域宽度，避免过宽 */
  margin: 0 auto 30px;
  /* 水平居中，与注册入口间距 */
}

.form-group {
  margin-bottom: 22px;
  text-align: left;
  /* 标签居左，符合用户输入习惯 */
}

/* 输入框标签：颜色清晰，不突兀 */
.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #374151;
  /* 加深标签颜色，提升可读性 */
  font-weight: 500;
  margin-left: 4px;
  /* 微调对齐 */
}

/* 输入框：偏白色，边界清晰 */
.form-input {
  width: 100%;
  height: 48px;
  padding: 0 20px;
  background-color: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 24px;
  font-size: 15px;
  color: #1f2937;
  /* 输入文字颜色加深 */
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
  /* 占位符颜色调整 */
  font-size: 14px;
}


/* 新增：登录操作区（控制左右对立布局） */
.login-action-group {
  display: flex;
  justify-content: space-between;
  /* 左右两端对齐 */
  align-items: center;
  margin-bottom: 24px;
  /* 与原忘记密码的间距保持一致 */
}

/* 手机号登录（左下方，样式与忘记密码统一） */
.phone-login .link {
  font-weight: 500;
}

/* 忘记密码（右对齐，保持原有样式） */
.forgot-password {
  margin-bottom: 0;
  /* 取消原单独margin，由父容器统一控制 */
}

.link {
  font-size: 13px;
  color: #22c55e;
  /* 偏绿色链接，呼应页面主题 */
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s;
}

.link:hover {
  color: #16a34a;
  /*  hover时加深绿色，反馈清晰 */
}

/* 3.5 登录按钮：偏白色，突出交互 */
.login-btn {
  width: 100%;
  height: 48px;
  /* 纯白背景：与表单风格统一 */
  background-color: #fff;
  border: 1px solid #22c55e;
  /* 绿色边框，突出按钮 */
  border-radius: 8px;
  font-size: 16px;
  color: #22c55e;
  /* 绿色文字，呼应主题 */
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.login-btn:hover {
  background-color: #f9fafb;
  border-color: #16a34a;
  box-shadow: 0 4px 12px rgba(34, 197, 94, 0.1);
}

.login-btn:active {
  transform: scale(0.98);
}

/* 3.6 注册入口：颜色柔和，与整体协调 */
.register-link {
  font-size: 14px;
  color: #4a5568;
}
</style>
