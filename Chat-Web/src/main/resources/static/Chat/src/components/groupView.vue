<template>
    <div class="common-layout" v-loading="loading" element-loading-background="rgba(220, 220, 220, 0.5)">
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
                            <div v-if="user.userInfo && item.emitterId != user.userInfo.id"
                                class="flex items-start cursor-pointer">

                                <img :src="item.icon" class="w-10 h-10 md:w-12 md:h-12 rounded-xl"
                                    @click.prevent="toIntroduce(item.emitterId)" alt="">

                                <div class="mx-4">
                                    <p class="mb-2 text-sm text-gray-400">{{ item.nickName || '' }}</p>
                                    <div class="border h-auto border-gray-500/50 rounded-sm inline-block max-w-64 break-words"
                                        :class="item.type !== MsgType.TEXT ? 'border-none' : 'border'">
                                        <p v-if="item.type === MsgType.TEXT" class="text-xs md:text-sm mx-2">{{
                                            item.msg }}</p>
                                        <img v-else-if="item.type === MsgType.IMAGE || item.type === MsgType.EMOJI"
                                            :src="item.emoji?.url" :width="item.emoji?.width"
                                            :height="item.emoji?.height" :alt="item.emoji?.content"
                                            class="max-h-32 max-w-32 pointer-events-none user-drag-none select-none object-cover ">
                                    </div>

                                </div>
                            </div>
                            <!-- 自己 -->
                            <div v-else class="">
                                <div class="flex justify-end items-start">
                                    <el-popover placement="top" :width="60" trigger="contextmenu">
                                        <div style="text-align: center; margin: 0"
                                            v-if="item.createTime && Date.now() - item.createTime < MAX_WHIHWRAWN_TIME">
                                            <el-button size="small" type="danger" link
                                                @click="recall(item)">撤回</el-button>
                                        </div>
                                        <template #reference>
                                            <div class="border mt-1  border-gray-500/50  inline-block mr-2 break-words cursor-pointer"
                                                :class="item.type !== MsgType.TEXT ? 'border-none' : 'border'">
                                                <p v-if="item.type === MsgType.TEXT" class="text-xs  md:text-sm mx-2">{{
                                                    item.msg }}
                                                </p>
                                                <img v-else-if="item.type === MsgType.IMAGE || item.type === MsgType.EMOJI"
                                                    :src="item.emoji?.url" :width="item.emoji?.width"
                                                    :height="item.emoji?.height" :alt="item.emoji?.content"
                                                    class=" max-h-32 max-w-32 pointer-events-none user-drag-none select-none object-cover ">
                                            </div>
                                        </template>
                                    </el-popover>

                                    <img :src="user.userInfo.icon" class="w-10 h-10 md:w-12 md:h-12 rounded-xl"
                                        v-if="user.userInfo" alt="">
                                </div>

                            </div>
                        </div>

                    </el-scrollbar>
                </div>

            </el-main>
            <el-footer>
                <ChatInput 
                    v-model="user_msg_input" 
                    :show-video-call="false"
                    @send="send"
                />
            </el-footer>
        </el-container>
    </div>
</template>
<script setup lang="ts">
import {GroupApi} from '../api/group';
import {type MsgAck} from '../utils/Socket/webSocket';
import {msgStore} from '../store/MessageStore';
import {userStore} from '../store/UserStore';
import {socketStore} from '../store/SocketStore';
import {computed, nextTick, onMounted, onUnmounted, ref, watch} from 'vue';
import {useRoute} from 'vue-router';
import type {Group, GroupMessageVO} from '../types/group';
import {Log} from '../utils/TipUtil';
import router from '../router';
import {ChatService} from '../services/ChatService';
import type {ChatGroup} from '../types/chat';
import type {ElScrollbar} from 'element-plus';
import {MsgType} from '../enums/GroupMsgType';
import {BusinessError} from '../exception/BusinessError';
import type {EmojiVO} from '../types/emoji';
import ChatInput from './ChatInput.vue';

const wrapEl = ref<InstanceType<typeof ElScrollbar>>();
const user_msg_input = ref("");
const msg = msgStore();
const user = userStore();
const socket = socketStore();
const ws = socket.getWs();
const route = useRoute();
const groupId = route.params.id as string;
const group = ref<Group>();
const currentSessionId = computed(() => { { return msg.getSessionKey(user?.userInfo?.id || "", groupId) } });
const msgData = computed(() => { if (currentSessionId.value) return msg.getGroupMsgList(currentSessionId.value) });
const msgList = computed(() => msgData.value ? msgData.value.groupMsgList : []);
const isLoading = ref(false);
const currentSession = computed(() => { if (currentSessionId.value) return msg.getSession(currentSessionId.value) });
const groupName = route.params.groupName as string;
const loading = ref(false);
const MAX_WHIHWRAWN_TIME = 1000 * 60 * 5;

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

function send(type: MsgType = MsgType.TEXT, emoji?: EmojiVO) {
    if (!user.userInfo || !user.userInfo.id) return;
    try {
        validateInput(user_msg_input.value, type, emoji);
        const message = createMessage(emoji, type);
        if (!message) return;
        if (type === MsgType.EMOJI && typeof emoji !== 'string') message.emoji = emoji;
        const isSend = ws?.sendOfGroup(message, (msgAck: MsgAck) => {
            if (msgAck.success) {
                if (msgAck.msgId) {
                    message.msgId = msgAck.msgId;
                }
                if (msgAck.sendTime) {
                    message.createTime = msgAck.sendTime;
                }
            } else {
                Log.error(msgAck.errorMsg || "发送失败");
                console.warn(msgAck.errorMsg);
            }
        })
        const is = handleIsSend(isSend, message);
        if (!is) console.log("未能进入消息列表" + isSend, +currentSessionId.value)
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error("发送群聊消息失败", err);
        }
    }
}

/**
 * 创建群聊消息对象
 * @param content - 待发送的消息内容
 * @param type - 消息类型
 * @returns ChatGroup - 创建的消息对象
 */
function createMessage(emoji?: EmojiVO | string, type: MsgType = MsgType.TEXT) {
    if (!user.userInfo) return;
    const message: ChatGroup = {
        groupId: groupId,
        msg: (emoji && (typeof emoji === 'string' ? emoji : emoji.id)) || user_msg_input.value,
        msgId: ChatService.generateUUID(), //消息id由前端生成
        emitterId: user.userInfo.id,
        type: type || MsgType.TEXT,
        createTime: Date.now(),
        icon: user.userInfo.icon,
        nickName: user.userInfo.nickName,
    }
    return message;
}
/**
 * 校验输入内容的合法性
 * @param content - 待校验的消息内容
 * @returns boolean - 校验通过返回 true，否则 false
 */
function validateInput(content: string, type: MsgType = MsgType.TEXT, emoji?: EmojiVO) {

    switch (type) {
        case MsgType.TEXT:
            if ((!content && content.trim() === "" && !emoji && !user_msg_input.value && user_msg_input.value.trim() === "") || (user_msg_input.value.length > 200 || content.length > 200 )) {
                throw new BusinessError("信息输入长度不能大于200个字符");
            }
            break;
        case MsgType.EMOJI:
            if (!emoji) {
                throw new BusinessError("请选择表情");
            }
            break;
    }
}

function handleIsSend(isSend: boolean | undefined, message: ChatGroup) {
    if (isSend && currentSessionId.value) {
        msg.addGroupMsg(currentSessionId.value, message);
        ChatService.scrollToBottom(wrapEl.value);
        user_msg_input.value = "";
        return true;
    }
    return false;
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
            let list = data.records;
            console.log("历史消息", list);
            list = ChatService.sortGroupMsgs(list);
            console.log("处理后的历史消息", list);
            await ChatService.handleEmoji(undefined, list);
            console.log("处理后的历史消息1", list);
            msg.addGroupMsgs(currentSessionId.value, list);
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

function toIntroduce(id: string) {
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

const recall = async (item: ChatGroup) => {
    try {
        const vo: GroupMessageVO = {
            msgId: item.msgId,
            groupId: item.groupId,
            emitterId: item.emitterId,
            msg: item.msg
        };
        await GroupApi.withdrawnGroupMsg(vo);
        Log.ok("撤回成功");
    } catch (e: any) {
        if (e instanceof BusinessError) Log.error(e.message);
        else {
            Log.error("撤回失败");
            console.error(e);
        }
    }
}
</script>

<style scoped>
:deep(.el-scrollbar) {
    height: 100% !important;
}

:deep(.el-footer) {
    height: 100%;
}
</style>
