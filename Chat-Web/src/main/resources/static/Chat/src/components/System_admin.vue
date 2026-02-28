<template>
    <div class="w-full h-full" v-loading="loading" element-loading-background="rgba(220, 220, 220, 0.5)">
        <div class="w-full h-full">
            <span class="font-bold text-2xl mt-4">系统管理</span>
        </div>
        <div class="mt-8">
            <div class="flex items-center justify-between m-2">
                <span class="text-gray-400 ">发布系统通知</span>
                <span class=" right-4 cursor-pointer font-glow hover:text-indigo-500" @click="handlePublish">发布</span>
            </div>
            <div class="min-h-96  shadow-md rounded-xl mt-4">
                <el-input type="textarea" class=" w-full   h-full p-2 border-none  " resize="none" placeholder="请输入内容"
                    maxlength="200" input-style="height:300px" v-model="notify">
                </el-input>
            </div>
        </div>
    </div>

</template>
<script lang="ts" setup>

import {SysNoticeApi} from '../api/sysNotice';
import {BusinessError} from '../exception/BusinessError';
import {userStore} from '../store/UserStore';
import type {SysNoticeDto} from '../types/sysNotice';
import {Log} from '../utils/TipUtil';
import {ref} from 'vue';

const user = userStore();
const loading = ref(false);
const notify = ref<string>('');

function handlePublish() {
    if (notify.value.trim().length == 0) {
        Log.warn("请输入内容");
        return;
    }
    try {
        const body: SysNoticeDto = {
            msg: notify.value,
            publisherId: user.userInfo?.id,
        }
        SysNoticeApi.publish(body);
        Log.ok("发布成功");
        notify.value = '';
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
            return;
        }
        Log.error("发布失败");
        console.error(err);
    }

}

</script>

<style scoped>
:deep(.el-textarea__inner) {
    background-color: var(--theme-bg);

    min-height: 300px;
}
 
</style>