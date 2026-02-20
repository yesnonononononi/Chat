<template>
    <AuthLayout :loading = "loading">
        <h2 class="form-title">忘记密码</h2>
        <div class="account-login">
            <div class="form-group">
                <label class="form-label">手机号</label>
                <div class="input-with-btn">
                    <input type="tel" class="form-input" placeholder="请输入手机号" v-model="body.mobile" maxlength="11" />
                    <button class="verify-btn" @click="sendCode" :disabled="countdown > 0">
                        {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
                    </button>
                </div>
            </div>
            <div class="form-group">
                <label class="form-label">验证码</label>
                <input type="text" class="form-input" placeholder="请输入验证码" v-model="body.verifyCode" maxlength="6" />
            </div>
            <div class="form-group">
                <label class="form-label">新密码</label>
                <input type="password" class="form-input" placeholder="请输入新密码" v-model="body.pw" />
            </div>
            <button class="login-btn" @click="forgetPw">提交</button>

            <div class="login-action-group" style="justify-content: center; margin-top: 20px;">
                <router-link to="/login" class="link">返回</router-link>
            </div>
        </div>
    </AuthLayout>
</template>

<script lang="ts" setup>
import { UserApi } from '../api/user';
import { BusinessError } from '../exception/BusinessError';
import { userStore } from '../store/UserStore';
import type { userPwPutDto } from '../types/user';
import { Log } from '../utils/TipUtil';
import { onMounted, reactive, ref, watch, onUnmounted } from 'vue';
import AuthLayout from "../components/AuthLayout.vue";
import router from '@/router';
const loading = ref(false);
const user = userStore();
const body = ref<userPwPutDto>({
    mobile: "",
    verifyCode: "",
    id: "",
    pw: "",
});
const countdown = ref(0);
let timer: ReturnType<typeof setInterval> | null = null;

onMounted(() => {
    
    if (user.userInfo) body.value.id = user.userInfo.id;
})

onUnmounted(() => {
    if (timer) clearInterval(timer);
})

async function sendCode() {
    if (!body.value.mobile) {
        Log.error("请填写手机号");
        return;
    }
    if (!/^1[3-9]\d{9}$/.test(body.value.mobile)) {
        Log.error("手机号格式不正确");
        return;
    }
    if (loading.value || countdown.value > 0) return;
    
    try {
        loading.value = true;
        if (await user.sendVerifyCode(body.value.mobile)) {
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
}

async function forgetPw() {
  
   
    if (!body.value.verifyCode || !body.value.pw || !body.value.mobile) {
        Log.error("请填写完整信息");
        return;
    }
    try {
        if(loading.value)return;
        loading.value = true;
        const res = await UserApi.forgetPw(body.value);
        Log.ok("修改成功");
        router.push("/login");
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            console.error(err);
            Log.error("服务繁忙");
        }
    }finally{
        loading.value = false;

    }

}



</script>

<style scoped>
/* Copied styles from Login.vue/register.vue to match the theme */
.form-title {
    margin-bottom: 32px;
    font-size: 26px;
    font-weight: 600;
    color: #111827;
    letter-spacing: 1px;
}

.account-login {
    display: flex;
    flex-direction: column;
    align-items: center;
}

.form-group {
    width: 100%;
    margin-bottom: 24px;
    text-align: left;
}

.form-label {
    display: block;
    margin-bottom: 8px;
    font-size: 14px;
    color: #374151;
    font-weight: 500;
}

.form-input {
    width: 100%;
    height: 48px;
    padding: 0 20px;
    border: 1px solid #e5e7eb;
    border-radius: 24px;
    background: #f9fafb;
    font-size: 15px;
    color: #1f2937;
    outline: none;
    transition: all 0.3s ease;
}

.form-input:focus {
    background: #fff;
    border-color: #22c55e;
    box-shadow: 0 0 0 4px rgba(34, 197, 94, 0.1);
}

.input-with-btn {
    position: relative;
    display: flex;
    align-items: center;
}

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
}

.verify-btn:hover {
    background: #d1fae5;
    color: #047857;
}

.verify-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}

.login-btn {
    width: 100%;
    height: 48px;
    border: none;
    border-radius: 24px;
    background: #22c55e;
    color: white;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    margin-top: 12px;
    transition: all 0.3s ease;
    box-shadow: 0 4px 12px rgba(34, 197, 94, 0.3);
}

.login-btn:hover {
    background: #16a34a;
    transform: translateY(-1px);
    box-shadow: 0 6px 16px rgba(34, 197, 94, 0.4);
}

.login-action-group {
    width: 100%;
    display: flex;
    justify-content: space-between;
    margin-top: 16px;
    font-size: 14px;
}

.link {
    color: #6b7280;
    text-decoration: none;
    transition: color 0.3s;
}

.link:hover {
    color: #22c55e;
}
</style>