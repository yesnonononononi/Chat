<template>
    <div class="absolute top-0 z-[100000] w-full h-auto flex items-center justify-between bg-white shadow-md border-b"
        v-if="curMsg">
        <div class="flex items-center gap-2 p-2">
            <img :src="curMsg.icon" alt="" class="w-10 h-10 rounded-full object-cover">
            <div class="overflow-hidden max-w-[200px] md:max-w-md">
                <p class="text-xs md:text-sm font-semibold truncate">{{ curMsg.emitterNick }}</p>
                <p class="text-gray-500 text-xs md:text-sm truncate">{{ curMsg.msg }}</p>
            </div>
        </div>
        <div class="flex items-center gap-4 px-4">
            <span class="text-indigo-500 text-xs md:text-sm font-medium hover:text-indigo-700 cursor-pointer"
                @click="reply(curMsg.emitterId)">回复</span>
            <span class="text-gray-400 text-xs md:text-sm hover:text-gray-600 cursor-pointer"
                @click="close">关闭</span>
        </div>
    </div>

    <div class="absolute top-0 z-[100000] w-full h-auto flex items-center justify-between bg-white shadow-md border-b"
        v-if="curMsgOfGroup">
        <div class="flex items-center gap-2 p-2">
            <img :src="curMsgOfGroup.icon" alt="" class="w-10 h-10 rounded-full object-cover">
            <div class="overflow-hidden max-w-[200px] md:max-w-md">
                <p class="text-xs md:text-sm font-semibold truncate">{{ curMsgOfGroup.nickName }} (群聊)</p>
                <p class="text-gray-500 text-xs md:text-sm truncate">{{ curMsgOfGroup.msg }}</p>
            </div>
        </div>
        <div class="flex items-center gap-4 px-4">
            <span class="text-indigo-500 text-xs md:text-sm font-medium hover:text-indigo-700 cursor-pointer"
                @click="replyGroup(curMsgOfGroup.groupId)">查看</span>
            <span class="text-gray-400 text-xs md:text-sm hover:text-gray-600 cursor-pointer"
                @click="closeGroup">关闭</span>
        </div>
    </div>
</template>

<script  lang="ts" setup>
import { userStore } from '../store/UserStore';
import { computed } from 'vue';
import type { chat, ChatGroup } from '../types/chat';
import router from '../router';

const user = userStore();

const curMsg = computed<chat | null>(() => user.curMsg);
const curMsgOfGroup = computed<ChatGroup | null>(() => user.curMsgOfGroup);

function reply(friendId: string) {
    router.push({
        name: 'chat',
        params: { id: friendId }
    });
    user.curMsg = null;
}

function replyGroup(groupId: string) {
    router.push({
        name: 'group-chat',
        params: { id: groupId }
    });
    user.curMsgOfGroup = null;
}

function close() {
    user.curMsg = null;
}

function closeGroup() {
    user.curMsgOfGroup = null;
}
</script>