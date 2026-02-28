<template>
    <div class="common-layout " v-loading="loading" element-loading-background="rgba(220, 220, 220, 0.5)">
        <el-container class="h-full">
            <el-header class="relative z-[4] border-b">
                <div class="flex justify-between items-center min-h-[70px]   border-gray-400 relative z-[4]">
                    <el-icon @click="closeView" class="cursor-pointer">
                        <ArrowLeftBold />
                    </el-icon>
                    <div class="flex items-center gap-1">
                        <span class="max-w-48 whitespace-nowrap overflow-hidden text-ellipsis"
                            :class="isAi ? 'text-green-400' : ''">
                            {{ friend.nickName || ``
                            }}
                        </span>
                    </div>
                    <el-icon @click.prevent="getMore" class="cursor-pointer">
                        <MoreFilled />
                    </el-icon>
                </div>

            </el-header>

            <el-main style="padding:2px">
                <div class="h-[50vh] mt-1">
                    <el-scrollbar class="w-full flex flex-col" ref="wrapEl" @scroll="handleScroll">
                        <p v-if="isLoading">正在加载中...</p>
                        <div v-for="item in msgList" :key="item.msgId" class="mb-6 ">
                            <!-- 好友 -->
                            <div v-if="user.userInfo && item.emitterId != user.userInfo.id"
                                class="flex items-start max-w-full break-all break-words ">

                                <img :src="friend.icon" class="w-10 h-10 md:w-12 md:h-12 cursor-pointer rounded-xl"
                                    @click.prevent="getMore" alt="">

                                <div class="mx-4 glass-base ">

                                    <div class="h-auto mt-2  border-gray-500/50 rounded-sm inline-block max-w-64 md:max-w-full break-words"
                                        :class="item.type !== MsgType.TEXT ? 'border-none' : 'border'">
                                        <p v-if="item.type === MsgType.TEXT" class="text-xs  md:text-sm mx-2">
                                            {{
                                                item.msg
                                            }}</p>
                                        <p v-if="item.type === MsgType.AI" class="text-xs  md:text-sm mx-2"
                                            v-html="item.msg">
                                        </p>
                                        <img v-if="item.type === MsgType.EMOJI" :src="item.emoji?.url"
                                            :alt="item.emoji?.content" :width="item.emoji?.width"
                                            @click.prevent="() => { }" :height="item.emoji?.height"
                                            class=" pointer-events-none user-drag-none select-none object-cover max-w-32 max-h-32 text-xs md:text-sm mx-2" />
                                    </div>

                                </div>
                            </div>
                            <!-- 自己 -->
                            <div v-else class="">
                                <div class="flex justify-end items-start">
                                    <span class="w-2 h-2 rounded-full bg-red-500 mr-4"
                                        v-if="item.status === serverMsgCode.FAIL"></span>
                                    <el-popover placement="top" :width="60" trigger="contextmenu">
                                        <div style="text-align: center; margin: 0"
                                            v-if="item.sendTime && Date.now() - item.sendTime < MAX_WHIHWRAWN_TIME">
                                            <el-button size="small" type="danger" @click="recall(item)">撤回</el-button>
                                        </div>
                                        <template #reference>
                                            <div class=" h-auto   inline-block mr-2 break-words break-all cursor-pointer"
                                                :class="item.type !== MsgType.TEXT ? 'border-none' : 'border'">
                                                <p class="text-xs md:text-sm mx-2" v-if="item.type === MsgType.TEXT">{{
                                                    item.msg }}</p>
                                                <img v-if="item.type === MsgType.EMOJI" :src="item.emoji?.url"
                                                    :alt="item.emoji?.content" :width="item.emoji?.width"
                                                    @click.prevent="() => { }" :height="item.emoji?.height"
                                                    class=" pointer-events-none user-drag-none select-none max-w-32 max-h-32 object-cover">
                                                <p class="text-[12px] text-gray-500">{{ item.status ==
                                                    serverMsgCode.READ ? '已读'
                                                    :
                                                    '未读' }}</p>
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
                <ChatInput v-model="user_msg_input" :show-video-call="!isAi" @send="isAi ? sendForAi() : send()"
                    @video-call="call_video" />
            </el-footer>


        </el-container>
    </div>
</template>

<script lang="ts" setup name="chatView">
import { Log } from '../utils/TipUtil';
import type { chat } from '../types/chat';
import { computed, nextTick, onMounted, onUnmounted, reactive, ref, watch, type WatchHandle } from 'vue'
import type { page, userInfo } from '../types/user';
import { userStore } from '../store/UserStore';
import { msgStore } from '../store/MessageStore';
import { Ws, type MsgAck } from '../utils/Socket/webSocket';
import { useRoute } from 'vue-router';
import { serverMsgCode } from '../enums/server-callback';
import router from '../router';
import { UserApi } from '../api/user';
import { MsgApi } from '../api/msg';
import type { historyPage } from '../types/msg';
import type { ElScrollbar } from 'element-plus';
import { ChatService } from '../services/ChatService';
import type { MsgACKOfServer } from '../types/msg-ack';
import { BusinessError } from '../exception/BusinessError';
import { socketStore } from '../store/SocketStore';
import ChatInput from './ChatInput.vue';
import { MsgType } from '../enums/GroupMsgType';

import type { EmojiVO } from '../types/emoji';
import { emojiStore } from '../store/EmojiStore';
import { aiInfo } from '@/types/Ai';
import { AiApi } from '@/api/Ai';
import { model } from '@/enums/AiEnum';
import { AiWs } from '@/utils/Socket/AiWs';
import { resStatus } from '@/enums/AiResStatus';
import { MdUtil } from '@/utils/md';
const MAX_WHIHWRAWN_TIME = 1000 * 60 * 5;
const user_msg_input = ref<string>("");
const msg = msgStore();
const user = userStore();
const emojis = emojiStore();
const socket = socketStore();
const route = useRoute();
let ws = socket.getWs();
const wrapEl = ref<InstanceType<typeof ElScrollbar>>();
const isLoading = ref(false);
const friendId = route.params.id as string;
const loading = ref(false);
const isAi = ref(false);
// AI 流式回复状态
const isAiStreaming = ref(false);

let aiReplyMsg: chat | null = null;
// 计算当前会话ID
const currentSessionId = computed(() => {
    if (user.userInfo && friendId) {
        return msg.getSessionKey(user.userInfo.id, friendId);
    }
    return "";
});

const parseMd = async (msg: string) => {
    return await MdUtil.parse(msg);
}

// 获取当前会话对象（响应式）
const currentSession = computed(() => msg.getSession(currentSessionId.value));

// 使用 computed 直接映射 Store 中的消息列表
const msgList = computed(() => currentSession.value?.msgList.privateMsgList || []);

// 用户好友信息
const friend = ref<userInfo>({
    icon: "",
    nickName: "",
    mobile: "",
    id: ""
});

//获取历史消息
const dto = ref<historyPage>({
    dto: {
        emitterId: "",
        receiveId: "",
        msg: "__MSG__",
        status: 0,
        msgId: "",
        sendTime: 0
    },
    page: 0,
    pageSize: 0
})


onMounted(async () => {
    if (!currentSession.value) {

        // 尝试自动注册会话（应对刷新页面等场景）
        if (user.userInfo && friendId) {
            msg.registry(user.userInfo.id, friendId);
        }

        // 再次检查会话是否存在
        if (!msg.getSession(currentSessionId.value)) {
            Log.error("未获取到当前会话信息");
            setTimeout(() => router.push("/"));
            return;
        }
    }

    // 强制重置会话状态，确保每次进入都重新拉取最新消息（解决本地状态滞后问题）
    msg.resetSession(currentSessionId.value);
    msg.setSessionOpen(currentSessionId.value, true);

    // 初始化分页参数
    if (currentSession.value) currentSession.value.page = 1;

    //获取聊天历史_构造消息体
    if (user.userInfo) dto.value.dto.emitterId = user.userInfo.id;

    //获取好友信息
    try {

        loading.value = true;

        const res: userInfo | undefined = await getFriendInfo();
        if (!res) {
            Log.error("未获取到好友信息")
            router.push("/");
            return;
        }
        //将好友信息保存
        friend.value = res;
        //获取历史聊天记录,构造消息体
        dto.value.dto.receiveId = friend.value.id;
        //标记消息为已读
        await MsgApi.readMsg(friend.value.id);
        await loadHistoryMsgs();

        //获取滚动条响应式对象
        nextTick(() => { ChatService.scrollToBottom(wrapEl.value); })

    } catch (error) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error(error);
        }
        return;
    } finally {
        loading.value = false;
    }

    //监听已读未读消息
    const ws = socket.getWs();
    ws?.onOneByOneMsgACK((data: MsgACKOfServer) => {
        if (!data) return;
        const msgId = data.msgId;
        const msgCode = data.msgCode;


        if (msgId === "DEFAULT_READ_ALL") {
            msg.updateAllMsgStatus(currentSessionId.value, msgCode);
        } else {
            // 使用 Store action 更新状态，利用倒序查找优化性能
            msg.updateMsgStatus(currentSessionId.value, msgId, msgCode);
        }
    })

})

// 监听消息列表变化，自动滚动到底部
watch(msgList, () => {
    if (isLoading.value) return;
    nextTick(() => {
        ChatService.scrollToBottom(wrapEl.value);
    });
}, { deep: true });

// 离开组件时清空当前会话消息，避免占用内存和缓存不一致问题
onUnmounted(() => {
    if (currentSessionId.value) {
        msg.setSessionOpen(currentSessionId.value, false);
        msg.resetSession(currentSessionId.value);  //清空本地缓存
    }
    const ws = socket.getWs();
    ws?.removeMsgACKListener();
})
async function loadHistoryMsgs() {
    if (!currentSession.value || !currentSession.value.hasMore) return;
    dto.value.page = currentSession.value.page++;
    dto.value.pageSize = currentSession.value.pageSize;

    try {
        //发送聊天记录获取请求__返回desc,降序
        const historyMsgList: page<chat[]> = await MsgApi.getHistoryByDto(dto.value);

        if (historyMsgList.records.length == 0 || historyMsgList.records.length !== currentSession.value.pageSize) {
            currentSession.value.hasMore = false;
        }
        let list = historyMsgList.records;
        //对信息进行排序_升序
        ChatService.sortMsgs(list);
        await ChatService.handleEmoji(list);
        await ChatService.handleMd(list);
        //插入到消息序列之前  old->new --- [%s  old->new] 以此保证消息是从old向new排列
        msg.addMsgs(currentSessionId.value, list);
    } catch (error) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("加载历史消息失败", error);
        }
    }
}
async function getFriendInfo() {

    friend.value.id = route.params.id as string;
    const res: userInfo = await UserApi.getUserInfoById(friend.value.id);
    if (res.id === aiInfo.id) {
        isAi.value = true;
    }
    return res;
}
/**
 * 获取有效的发送内容
 * @param message - 可选的直接消息内容（如表情URL）
 * @returns 优先返回 message，否则返回输入框内容 user_msg_input
 */
function getEffectiveContent(message?: string, msg?: string): string {
    return message || msg || user_msg_input.value;
}

/**
 * 校验输入内容的合法性
 * @param content - 待校验的消息内容
 * @returns boolean - 校验通过返回 true，否则 false
 */
function validateInput(content: string): boolean {
    if (!user.userInfo) {
        Log.error("未获取到用户信息");
        return false;
    }
    if (!content.trim()) {
        Log.error("未输入任何信息");
        return false;
    }
    if (content.length > 200) {
        Log.error("信息长度不能大于200个字");
        return false;
    }
    return true;
}

/**
 * 构造聊天消息对象
 * @param content - 消息内容
 * @param type - 消息类型 (TEXT, EMOJI 等)
 * @returns chat - 构造好的消息对象
 * @throws BusinessError - 如果用户信息缺失
 */
function createChatMessage(content: string, type: MsgType, emitterId: string, receiveId: string, nickName: string, icon: string): chat {
    if (!user.userInfo) {
        throw new BusinessError("未找到用户信息");
    }
    return {
        msg: content,
        msgId: ChatService.generateUUID(),
        receiveId: receiveId,
        emitterId: emitterId,
        emitterNick: nickName,
        icon: icon,
        status: null,
        type: type,
        sendTime: Date.now(),

    };
}

/**
 * 处理 WebSocket 发送回执
 * @param ackData - 服务器返回的确认数据
 * @param m - 当前发送的消息对象引用
 */
function handleMessageCallback(ackData: MsgAck, m: chat) {
    console.log("收到回执", ackData);
    if (!ackData) return;

    if (ackData.success) {
        if (ackData.msgId) m.msgId = ackData.msgId;
        if (ackData.sendTime) m.sendTime = ackData.sendTime;
        console.log("消息发送成功");
    } else {
        Log.error(ackData.errorMsg || "发送失败");
        m.status = serverMsgCode.FAIL;
        console.log(m.status);
    }
}

/**
 * 发送消息到 WebSocket
 * @param m - 要发送的消息对象
 * @returns boolean - 如果 WebSocket 连接正常且发送调用成功返回 true，否则 false
 */
function sendToSocket(m: chat) {
    if (!ws) ws = socket.getWs();
    if (!ws) return false;

    return ws.sendOfPrivate(m, (ackData: MsgAck) => handleMessageCallback(ackData, m));
}

/**
 * 发送消息主函数
 * 负责协调输入校验、消息构造、Socket发送及UI更新
 
 * @param msgType - 消息类型，默认为 TEXT
 * @param message - 直接指定的消息内容（如表情），若未提供则使用输入框内容
 */
async function send(msgType: MsgType = MsgType.TEXT, Emoji?: EmojiVO) {
    try {

        const content = getEffectiveContent(Emoji?.id || user_msg_input.value);


        if (!content.trim() && !Emoji) {
            if (user_msg_input.value.length === 0) Log.error("未输入任何信息");
            return;
        }

        if (!validateInput(content) || !user.userInfo) return;

        const m = createChatMessage(content, msgType, user.userInfo.id, friend.value.id, user.userInfo.nickName, user.userInfo.icon);



        if (!Emoji) {
            user_msg_input.value = '';
        }

        const isSent = sendToSocket(m);

        if (isSent && msg) {
            if (m.type === MsgType.EMOJI) {
                const res = await mapEmoji(m.msg);
                if (res) m.emoji = res;
            }
            msg.addMsg(currentSessionId.value, m);
        }

        ChatService.scrollToBottom(wrapEl.value);

    } catch (error) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("发送出现异常", error);
        }
    }
}

async function sendForAi() {
    if (!user.userInfo) return;
    if (!user_msg_input.value || user_msg_input.value.length === 0) {
        Log.info("请输入内容");
        return;
    }
    // 先把用户问题发出去
    const userMessage: chat = createChatMessage(
        user_msg_input.value,
        MsgType.TEXT,
        user.userInfo.id,
        friend.value.id,
        user.userInfo.nickName,
        user.userInfo.icon
    );
    msg.addMsg(currentSessionId.value, userMessage);
    sendToSocket(userMessage);


    // 为 AI 回复做准备：占位
    aiReplyMsg = null;
    isAiStreaming.value = true;
    let res: AiWs | null = null;
    try {
        //1, 开启监听
        res = startStreamListen();
        if (!res) {
            Log.error("AI 响应异常");
            return;
        }
        //2,开启流式,发送问题
        await AiApi.chat({
            message: user_msg_input.value,
            modelName: model.QWEN
        });
        //3, 清空输入框
        user_msg_input.value = "";

    } catch (error) {
        isAiStreaming.value = false;
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("发送异常");
            console.error("发送出现异常", error);
        }
    }
}

function startStreamListen() {
    //开启监听
    const wsClient = new AiWs(Ws.getInstance());
    //0.5,开启监听之前先停止之前可能存在的监听
    wsClient.endListenStream();

    wsClient.listenStream(async (data: string) => {
        if (data === resStatus.DEFAULT_RES_START_STREAM) {
            // 流式开始：创建一条空内容的 AI 消息占位
            aiReplyMsg = createChatMessage(
                "",
                MsgType.AI,
                friend.value.id,
                user.userInfo!.id,
                friend.value.nickName,
                friend.value.icon
            );
            //直接追加消息
            if (aiReplyMsg) msg.addMsg(currentSessionId.value, aiReplyMsg);

            return;
        }
        if (data === resStatus.DEFAULT_END_STREAM) {
            // 流式结束
            isAiStreaming.value = false;
            wsClient.endListenStream();
            let lastMsg = msgList.value[msgList.value.length - 1];
            if(lastMsg)lastMsg.msg = await  parseMd(lastMsg.msg);
            return;
        }
        if (data === resStatus.DEFAULT_ERROR_START_STREAM) {
            // 出错
            isAiStreaming.value = false;
            wsClient.endListenStream();
            Log.error("AI 助手回答失败，请稍后重试");

            return;
        }
        // 正常内容增量追加
        if (msgList.value.length > 0 && aiReplyMsg) {
            const lastMsg = msgList.value[msgList.value.length - 1];
            if (lastMsg && lastMsg.msg !== undefined) {

                lastMsg.msg += data;
            }
        }
    });
    return wsClient;

}

async function mapEmoji(msg: string) {
    return emojis.queryEmojiById(msg);
}
function closeView() {
    router.push("/");
    if (currentSession.value) currentSession.value.hasMore = true;
}

const handleScroll = (params: { scrollLeft: number; scrollTop: number }) => {
    ChatService.handleScroll(params, isLoading, wrapEl, handleToTherShold);
}
//刷新最新消息
async function handleToTherShold() {
    ChatService.handleToTherShold(isLoading, wrapEl, loadHistoryMsgs);
}

function getMore() {
    router.push({
        name: 'user-introduce',
        params: { id: friend.value.id }
    })
}
async function call_video() {
    try {

        if (!user.userInfo || isAi.value) return;
        router.push({
            name: 'video-call'
            , params: {
                id: friend.value.id,
                roomName: user.userInfo.id + friend.value.id,
                role: "initiator"
            }
        })
        Log.ok("已发送视频请求")
    } catch (e) {
        if (e instanceof BusinessError) {
            Log.error(e.message);
        } else {
            Log.error("服务繁忙");
            console.error(e);
        }
    }

}

const recall = async (item: chat) => {
    try {
        if (isAi.value) return;
        await MsgApi.withdrawnMsg(item);
        Log.ok("撤回成功");
        const index = msgList.value.findIndex((each) => each.msgId === item.msgId)
        if (index > -1) msgList.value.splice(index, 1)
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