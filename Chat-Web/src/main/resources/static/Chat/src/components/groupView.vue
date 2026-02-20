<template>
    <div class="common-layout" v-loading="loading"
        element-loading-background="rgba(220, 220, 220, 0.5)">
        <el-container>
            <el-header height="6vh">
                <div class="flex justify-between items-center w-full h-[6vh] p-1 border-b-2">
                    <el-icon class="cursor-pointer" @click.prevent="router.push('/')">
                        <Back />
                    </el-icon>
                    <span class="max-w-48 whitespace-nowrap overflow-hidden text-ellipsis">{{ groupName || `` }}</span>
                    <el-icon class="cursor-pointer" @click.prevent="getMoreInfo">
                        <MoreFilled />
                    </el-icon>
                </div>
            </el-header>
            <el-main style="padding:2px">
                <div class=" h-[50vh] mr-2">
                    <el-scrollbar class="w-full flex flex-col " ref="wrapEl" @scroll="handleScroll">
                        <p v-if="isLoading">正在加载中...</p>
                        <div v-for="item in msgList" :key="item.msgId" class="mb-6 ">
                            <!-- 群成员 -->
                            <div v-if="user.userInfo && item.emitterId != user.userInfo.id" @click.prevent="toIntroduce(item.emitterId)" class="flex items-center cursor-pointer">

                                <img :src="item.icon" class="w-10 h-10 md:w-12 md:h-12 rounded-xl" alt="">

                                <div class="mx-4">
                                    <p class="mb-2 text-sm text-gray-400">{{ item.nickName || '' }}</p>
                                    <div
                                        class="border h-auto border-gray-500/50 rounded-sm inline-block max-w-64 break-words">
                                        <p class="text-xs md:text-sm mx-2">{{ item.msg }}</p>
                                    </div>

                                </div>
                            </div>
                            <!-- 自己 -->
                            <div v-else class="">
                                <div class="flex justify-end items-center">
                                    <div
                                        class="border h-auto border-gray-500/50 max-w-[50%] inline-block mr-2 break-words">
                                        <p class="text-xs md:text-sm mx-2">{{ item.msg }}</p>

                                    </div>

                                    <img :src="user.userInfo.icon" class="w-10 h-10 md:w-12 md:h-12 rounded-xl"
                                        v-if="user.userInfo" alt="">

                                </div>

                            </div>
                        </div>

                    </el-scrollbar>
                </div>

            </el-main>
            <el-footer>
                <div class="relative border-t-2 h-[20vh] md:h-[27vh] ">
                    <div id="tool" class="w-full flex items-center gap-x-4 jutify-between">
                       <el-icon size="24" class="hover:text-indigo-400 cursor-pointer"><Star /></el-icon>
                        <el-icon size="24" class="hover:text-indigo-400 cursor-pointer"><VideoCamera /></el-icon> 
                    </div>
                    <el-input v-model="user_msg_input" type="textarea" class=" w-full h-full  p-2" resize="none"
                        :disabled="false" :input-style="{ backgroundColor: '#ECEEF1', height: '100%' }" :maxlength="200"
                        show-word-limit :rows=8 @keydown.enter.exact.prevent="send($event)"
                        @keydown.ctrl.enter="handleCtrlEnter" />
                    <div class="absolute bottom-4 right-4 cursor-pointer" @click="send">
                        <el-icon class="" size="24">
                            <Right />
                        </el-icon>
                    </div>
                </div>
            </el-footer>
        </el-container>
    </div>
</template>
<script setup lang="ts">
import { GroupApi } from '../api/group';
import { Ws, type MsgAck } from '../utils/Socket/webSocket';
import { msgStore } from '../store/MessageStore';
import { userStore } from '../store/UserStore';
import { computed, nextTick, onMounted, onUnmounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import type { Group } from '../types/group';
import { Log } from '../utils/TipUtil';
import { ref, type Ref } from 'vue';
import router from '../router';
import { ChatService } from '../services/ChatService';
import type { ChatGroup } from '../types/chat';
import type { ElScrollbar } from 'element-plus';
import { GroupMsgType } from '../enums/GroupMsgType';
import { BusinessError } from '../exception/BusinessError';

const wrapEl = ref<InstanceType<typeof ElScrollbar>>();
const user_msg_input = ref("");
const msg = msgStore();
const user = userStore();
const ws = user.getWs();
const route = useRoute();
const groupId = route.params.id as string;
const group = ref<Group>();
const currentSessionId = computed(() => { { return msg.getSessionKey(user?.userInfo?.id || "", groupId) } return ""; });
const msgList = computed(() => { if (currentSessionId.value) return msg.getGroupMsgList(currentSessionId.value) });
const isLoading = ref(false);
const currentSession = computed(() => { if (currentSessionId.value) return msg.getSession(currentSessionId.value) });
const groupName = route.params.groupName as string;
const loading = ref(false);
onMounted(async () => {
    if (!groupId) {
        Log.error("未获取到群聊基本信息");
        router.push("/");
    }
    loading.value = true;
    try {
        if (!groupName) {
            const res = await GroupApi.queryGroupById(groupId).catch(err => {
                if (err instanceof BusinessError) {
                    Log.error(err.message);
                } else {
                    Log.error("服务繁忙");
                    console.error("未能成功获取群聊基本信息", err);
                }
            })
            group.value = res;
        }
        if (!currentSession.value) {
            // 尝试自动注册会话（应对刷新页面等场景）
            if (user.userInfo && groupId) {
                msg.registry(user.userInfo.id, groupId);
            }

            // 再次检查会话是否存在
            if (!msg.getSession(currentSessionId.value || "")) {
                Log.error("未获取到当前会话信息");
                setTimeout(() => router.push("/"));
                return;
            }
        }

        // 强制重置会话状态，确保每次进入都重新拉取最新消息（解决本地状态滞后问题）
        msg.resetSession(currentSessionId.value);
        msg.setSessionOpen(currentSessionId.value, true);


        await loadHistoryMsgs();

        nextTick(() => {
            ChatService.scrollToBottom(wrapEl.value);
        })
    } catch (err) {
        Log.error("未获取到群聊基本信息");
        router.push("/");
    } finally {
        loading.value = false;
    }
})

// 监听消息列表变化，自动滚动到底部
watch(msgList, () => {
    if (isLoading.value) return;
    nextTick(() => {
        ChatService.scrollToBottom(wrapEl.value);
    });
}, { deep: true });

function send(e: Event) {
    if (!user.userInfo || !user.userInfo.id) return;
    try {
        const message: ChatGroup = {
            groupId: groupId,
            msg: user_msg_input.value,
            msgId: ChatService.generateUUID(), //消息id由前端生成
            emitterId: user.userInfo.id,
            messageType: GroupMsgType.TEXT,
            createTime: Date.now(),
            icon: user.userInfo.icon,
            nickName: user.userInfo.nickName
        }
        const isSend = ws?.sendOfGroup(message, (msgAck: MsgAck) => {
            if (msgAck.success) {
                // message.msgId = msgAck.msgId; //前端生成ID后，不需要再从ack获取
                console.log("消息发送成功");
            } else {
                Log.error(msgAck.errorMsg || "发送失败");
                console.warn(msgAck.errorMsg);
            }
        })
        if (isSend && currentSessionId.value) {
            msg.addGroupMsg(currentSessionId.value, message);
            ChatService.scrollToBottom(wrapEl.value);
            Log.ok("发送群聊消息成功");
            user_msg_input.value = "";
            return;
        }
        console.log("未能进入消息列表" + isSend, +currentSessionId.value)
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error("发送群聊消息失败", err);
        }
    }
}
function handleCtrlEnter(event: Event) {
    ChatService.handleCtrlEnter(event, user_msg_input);
}
const handleScroll = (params: { scrollLeft: number; scrollTop: number }) => {
    ChatService.handleScroll(params, isLoading, wrapEl, handleToTherShold);
}
//刷新最新消息
async function handleToTherShold() {
    ChatService.handleToTherShold(isLoading, wrapEl, loadHistoryMsgs);
}

async function loadHistoryMsgs() {
    if (!currentSession.value || !currentSessionId.value) return;
    if (!currentSession.value.hasMore) return;

    try {
        const data = await GroupApi.queryGroupMsgById(groupId, currentSession.value.page, currentSession.value.pageSize);
        if (data && data.records && data.records.length > 0) {
            msg.addGroupMsgs(currentSessionId.value, ChatService.sortGroupMsgs(data.records));
            currentSession.value.page++;
            if (data.records.length < currentSession.value.pageSize) {
                currentSession.value.hasMore = false;
            }
        } else {
            currentSession.value.hasMore = false;
        }
    } catch (error) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("加载群聊历史消息失败", error);
        }
        isLoading.value = false;
    }
}

function getMoreInfo() {
    router.push({
        name: "group-detail",
        params: {
            id: groupId
        }
    })
}

function toIntroduce(id:string){
    router.push({
        name: "user-introduce",
        params: {
            id: id
        }
    })
}

onUnmounted(() => {
    if (currentSessionId.value && currentSession.value) {
        msg.setSessionOpen(currentSessionId.value, false);
        currentSession.value.page = 1;
        currentSession.value.pageSize = 10;
        currentSession.value.hasMore = true;
        isLoading.value = false;
    }
});


</script>
