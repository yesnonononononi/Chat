<template>
    <div class="w-full h-[100vh]" v-loading="loading" element-loading-background="rgba(220, 220, 220, 0.5)">
        <div class="w-full h-full flex flex-col justify-center items-center">
            <div class="flex flex-col items-center justify-center gap-4 shadow-md p-8" >
                <span>重置密码</span>
                <el-input v-model="oldPw" type="" placeholder="请输入旧密码"></el-input>
                <el-input v-model="newPw" type="password" placeholder="请输入新密码"></el-input>
                <el-input v-model="confirmPw" type="password" placeholder="确认新密码"></el-input>
                <div  >
                    <span class="text-sm text-green-300 hover:text-green-500 cursor-pointer" @click="forgetPw">忘记密码?</span>
                </div>
                <button class="bg-green-400 shadow-md w-24 h-8 rounded-xl" @click.prevent="reset">提交</button>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { UserApi } from '../api/user';
import { userStore } from '../store/UserStore';
import { ref } from 'vue'
import type { userPwPutDto } from '../types/user';
import { Log, log } from '../utils/TipUtil';
import { BusinessError } from '../exception/BusinessError';
import router from '@/router';
const loading = ref(false);
const oldPw = ref('');
const newPw = ref('');
const confirmPw = ref('');
const user = userStore();
async function reset() {
    if (!oldPw.value || !newPw.value) {
        Log.error("请填写完整信息");
        return;
    }
    if (newPw.value !== confirmPw.value) {
        Log.error("密码不一致");
        return;
    }
    try {
        if (!user.userInfo || loading.value) {
            return;
        }
        loading.value = true;
        const body: userPwPutDto = {
            oldPw: oldPw.value,
            pw: newPw.value,
            id: user.userInfo.id
        }
        await UserApi.putPw(body);
        Log.ok("修改成功");
        newPw.value = '';
        oldPw.value = '';
        confirmPw.value = '';
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            console.error(err);
            Log.error("修改失败");
        }
    } finally {
        loading.value = false;
    }

}

function forgetPw(){
    Log.primary("找回密码后点击返回即可");
    router.push({
        name:"forget-pw"
    })
}
</script>