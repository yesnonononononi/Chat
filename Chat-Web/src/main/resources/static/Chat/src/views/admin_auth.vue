<template>
    <div class="w-full h-[100vh] flex flex-col justify-center items-center">
        <div
            class="min-w-64 md:min-w-96 min-h-64 border border-gray-300 rounded-md p-4 shadow-md flex items-center gap-4 animate-breathe">
            <div v-if="!isAuth || !isFail"
                class="w-16 h-16 rounded-full border border-gray-500 border-b-transparent animate-spin">
            </div>
            <div>
                <p>{{ !isFail ? (isAuth ? `认证成功,即将跳转 ${time}s` : '认证中,请稍后...') : '验证失败' }}</p>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import router from '../router';
import { AdminApi } from '../api/admin';
import { onMounted, ref } from 'vue';
import { Log } from '../utils/TipUtil';
const isAuth = ref(false);
const isFail = ref(false);
const time = ref(5);
onMounted(async () => {
    handleAuthAndRedirect();
})



// 封装跳转逻辑
const handleAuthAndRedirect = async () => {

    try {
        // 执行认证请求
        isAuth.value = await AdminApi.auth() as boolean;

        // 认证成功后延迟跳转
        if (isAuth.value) {
            sessionStorage.setItem('isAdminAuth', 'true'); // 设置认证标志
            const interval = setInterval(() => {
                time.value--;
            },1000)

            setTimeout(() => {
                router.push({
                    name: 'admin'
                }).catch(err => {
                    // 捕获跳转异常（比如已经在目标页面）
                    console.error('跳转失败:', err);
                    Log.error("跳转失败")
                }).finally(() => {
                    clearInterval(interval);
                })
            }, 5000);
        } else {
            isFail.value = true;
        }
    } catch (error) {
        console.error('认证请求出错:', error);
        isAuth.value = false;
    }
};


</script>