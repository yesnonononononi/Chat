<template>
    <div class="common-layout " v-loading="loading" element-loading-background="rgba(220, 220, 220, 0.5)">
        <el-container class="h-full">
            <el-header class="">
                <div class="flex justify-between items-center h-[9vh] border-b border-gray-400 relative z-[100]">
                    <el-icon @click="closeView" class="cursor-pointer">
                        <ArrowLeftBold />
                    </el-icon>
                    <div class="flex items-center gap-1">
                        <span class="max-w-48 whitespace-nowrap overflow-hidden text-ellipsis">{{ friend.nickName || ``
                        }}</span>
                    </div>
                    <el-icon @click.prevent="getMore" class="cursor-pointer">
                        <MoreFilled />
                    </el-icon>
                </div>

            </el-header>

            <el-main style="padding:2px">
                <div class="h-[55vh] mt-1">
                    <el-scrollbar class="w-full flex flex-col" ref="wrapEl" @scroll="handleScroll">
                        <p v-if="isLoading">正在加载中...</p>
                        <div v-for="item in msgList" :key="item.msgId" class="mb-6 ">
                            <!-- 好友 -->
                            <div v-if="user.userInfo && item.emitterId != user.userInfo.id"
                                class="flex items-center max-w-full break-all break-words" >

                                <img :src="friend.icon" class="w-10 h-10 md:w-12 md:h-12 cursor-pointer rounded-xl" @click.prevent="getMore" alt="">

                                <div class="mx-4">
                                    <!-- <p class="mb-2 text-sm text-gray-400">{{ friend.nickName || '' }}</p> -->
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
                                        class="border h-auto border-gray-500/50   inline-block mr-2 break-words break-all">
                                        <p class="text-xs md:text-sm mx-2">{{ item.msg }}</p>
                                        <p class="text-[12px] text-gray-500">{{ item.status == serverMsgCode.READ ? '已读'
                                            :
                                            '未读' }}</p>
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
                        <el-icon size="24" class="hover:text-indigo-400 cursor-pointer" @click="call_video"><VideoCamera /></el-icon> 
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

<script lang="ts" setup name="chatView">
import { Log } from '../utils/TipUtil';
import type { chat } from '../types/chat';
import { ref, onUnmounted, computed, onMounted, nextTick, watch } from 'vue'
import type { userInfo, page } from '../types/user';
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
import { MediaApi } from '../api/media';
const user_msg_input = ref<string>("");
const msg = msgStore();
const user = userStore();
const route = useRoute();
let ws = user.getWs();
const wrapEl = ref<InstanceType<typeof ElScrollbar>>();
const isLoading = ref(false);
const friendId = route.params.id as string;
const loading = ref(false);
// 计算当前会话ID
const currentSessionId = computed(() => {
    if (user.userInfo && friendId) {
        return msg.getSessionKey(user.userInfo.id, friendId);
    }
    return "";
});

// 获取当前会话对象（响应式）
const currentSession = computed(() => msg.getSession(currentSessionId.value));

// 使用 computed 直接映射 Store 中的消息列表
const msgList = computed(() => currentSession.value?.msgList || []);

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
    if(user.userInfo)dto.value.dto.emitterId = user.userInfo.id;

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
    const ws = user.getWs();
    ws?.onOneByOneMsgACK((data: MsgACKOfServer) => {
        if (!data) return;
        const msgId = data.msgId;
        const msgCode = data.msgCode;
        console.log("收到消息回调", data)

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
    const ws = user.getWs();
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

        //对信息进行排序_升序
        ChatService.sortMsgs(historyMsgList.records);

        //插入到消息序列之前  old->new --- [%s  old->new] 以此保证消息是从old向new排列
        msg.addMsgs(currentSessionId.value, historyMsgList.records);
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
    return res;
}
function send(event?: Event) {
    try {
        if (user_msg_input.value.trim().length !== 0 && user.userInfo) {
            if(user_msg_input.value.length > 200 ){
                Log.error("信息长度不能大于200个字");
                return;
            }
            if (!ws) ws = user.getWs();
            if (!ws) return;
            if (event) {
                //阻止默认换行
                event.preventDefault();
            }
            const m: chat = {
                msg: user_msg_input.value,
                msgId: ChatService.generateUUID(),  //消息id由前端生成
                receiveId: friend.value.id,
                emitterId: user.userInfo.id,
                emitterNick:user.userInfo.nickName,
                icon:user.userInfo.icon,
                status: null,
                sendTime: Date.now()  //发送时间由服务端构造后发送给接收者并持久化
            }

            let isSend = ws.sendOfPrivate(m, (ackData: MsgAck) => {
                console.log("收到回执", ackData)
                if (!ackData) return;

                if (ackData.success) {
                    // m.msgId = ackData.msgId; //前端生成ID后，不需要再从ack获取
                    console.log("消息发送成功");
                } else {
                    Log.error(ackData.errorMsg || "发送失败");
                }
            });

            console.log("发送消息", m);

            //清空输入框
            user_msg_input.value = '';

            //向会话信息表放入信息
            if (isSend) msg.addMsg(currentSessionId.value, m);

            ChatService.scrollToBottom(wrapEl.value);
        } else {
            Log.error("未获取到用户信息或未输入任何信息")
        }
    } catch (error) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("发送出现异常", error);
        }
    }
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
// Ctrl+Enter 手动换行
function handleCtrlEnter(event: Event) {
    ChatService.handleCtrlEnter(event, user_msg_input);
}

function getMore() {
    router.push({
        name: 'user-introduce',
        params: { id: friend.value.id }
    })
}
async function call_video() {
    try {

        if (!user.userInfo) return;
        router.push({
            name:'video-call'
            ,params:{
                id:friend.value.id,
                roomName:user.userInfo.id+friend.value.id,
                role:"initiator"
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

</script>

<style scoped>
:deep(.el-scrollbar){
    height: 100% !important;
}
</style>