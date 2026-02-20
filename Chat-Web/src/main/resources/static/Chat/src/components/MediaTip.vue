<template>
    <div class="absolute top-0 z-[100000] w-full h-auto flex items-center justify-between bg-white shadow-md border-b" v-if="isSend">
        <div class=" flex items-center gap-2 p-2">
            <img :src="user?.icon" alt="" class="w-10 h-10 rounded-full object-cover">
            <div>
                <p class="text-xs md:text-sm font-semibold">{{ user?.nickName }}</p>
                <p class="text-gray-500 text-xs md:text-sm">向你发来了视频聊天</p>
            </div>
        </div>
        <div class="flex items-center gap-8 px-4" v-if="!isCancel">
            <span class="text-indigo-500 text-xs md:text-sm font-medium hover:text-indigo-700 cursor-pointer"
                @click="accept">接受</span>
            <span class="text-gray-400 text-xs md:text-sm hover:text-gray-600 cursor-pointer"
                @click="reject">拒绝</span>
        </div>
        <span class="text-gray-400 text-xs md:text-sm px-4" v-if="isCancel">已取消</span>
    </div>
</template>
<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue';
import type { MediaApplyDTO } from '../types/media';
import { MediaWs } from '../utils/Socket/MediaWs';
import { Ws } from '../utils/Socket/webSocket';
import { MediaApi } from '../api/media';
import router from '../router';
import { BusinessError } from '../exception/BusinessError';
import { Log } from '../utils/TipUtil';
import { userStore } from '../store/UserStore';
const isSend = ref<boolean>(false);  //是否收到视频聊天
const isCancel = ref<boolean>(false);
const user = ref<MediaApplyDTO>()
const user_me = userStore();
let media: MediaWs | null = null;

function receiveListen(data: MediaApplyDTO) {
    isSend.value = true;
    user.value = data;
}

function acceptListen(data: any) {
    isSend.value = false;
}

function rejectListen(data: any) {
    isCancel.value = true;
    setTimeout(() => { isSend.value = false }, 2000)
}

function cancelListen(data: any) {
    //对方取消视频聊天
    console.log("对方取消了视频聊天");
    isCancel.value = true;
    isSend.value = false;
    setTimeout(() => { isCancel.value = false }, 2000)
}

function initMediaListeners() {
    if (media) {
        MediaWs.stopListen();
    }
    const ws = Ws.getInstance();
    media = new MediaWs(ws);
    media.init(receiveListen, acceptListen, rejectListen, cancelListen);
}

onMounted(async () => {
    initMediaListeners();
});

watch(() => user_me.isLogin, (newVal) => {
    if (newVal) {
        setTimeout(() => {
            initMediaListeners();
        }, 500);
    }
});

onUnmounted(() => {
    MediaWs.stopListen();
})

async function accept() {
    if (!user.value?.userId || !user_me.userInfo) return;
    try {
        await MediaApi.accept(user.value.userId);
        const roomName = user.value.userId + user_me.userInfo.id  //我接受,房名为发送者id+接受者id
        if (roomName) {
            router.push({
                name: "video-call",
                params: {
                    id: user.value.userId,
                    roomName: roomName,
                    role: 'receiver'
                }
            })
            isSend.value = false;
        }
    } catch (error) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        }
        console.error(error);
    }
}

async function reject() {
    if (!user.value?.userId) return;
    try {
        await MediaApi.reject(user.value.userId);
        isSend.value = false;
    } catch (error) {
        console.error(error);
    }
}
</script>