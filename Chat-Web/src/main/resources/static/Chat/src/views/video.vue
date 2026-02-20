<template>
    <div class="w-full h-[100vh]">

        <div
            class="relative flex flex-col w-[100%] md:w-[50%] h-[100vh] m-auto items-center justify-center overflow-hidden">
            <video id="remote" ref="remoteVideo" class="w-full aspect-video h-full bg-gray-400 object-cover" playsinline
                autoplay></video>
            <video id="local" ref="localVideo"
                class="absolute aspect-video top-0 right-0 w-[40%] h-[35%] bg-gray-200 z-10 object-cover" playsinline
                autoplay></video>
            <span v-show="!state.isAccepted && role === 'initiator'"
                class="absolute   text-sm md:text-xl font-bold text-green-400">等待对方接听...</span>
            <div class="absolute bottom-0 m-auto my-24 flex items-center justify-around gap-4 "
                v-show="state.isConnected">
                <div class="text-red-500 font-bold cursor-pointer" @click="leaveRoom">挂断</div>
                <div class="text-gray-500 font-bold cursor-pointer" @click="turnCamera">{{ state.openCamera ? '关闭摄像头'
                    : '开启摄像头' }}</div>
                <div class="text-gray-500 font-bold cursor-pointer" @click="muted">{{ state.muted ? '声音' : '静音' }}</div>
            </div>
            <!-- 等待中显示的取消按钮 -->
            <div class="absolute bottom-0 m-auto my-24 flex items-center justify-around gap-4"
                v-show="!state.isConnected">
                <div class="text-red-500 font-bold cursor-pointer" @click="cancelCall">取消</div>
            </div>
        </div>
    </div>

</template>

<script lang="ts" setup>
import router from '../router';
import { userStore } from '../store/UserStore';
import { onMounted, ref, watch, toRef, onUnmounted, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { BusinessError } from '../exception/BusinessError';
import { Log } from '../utils/TipUtil';
import { MediaApi } from '../api/media';
import { Room, RoomEvent } from "livekit-client";
import type { MediaApplyDTO } from '../types/media';
import { MediaWs } from '../utils/Socket/MediaWs';
import { Ws } from '../utils/Socket/webSocket';

const state = ref({
    openCamera: true,
    openMicrophone: true,
    isConnected: false,
    isAccepted: false,
    muted: false,

})
const remoteVideo = ref<HTMLVideoElement | null>();
const localVideo = ref<HTMLVideoElement | null>();
const URL = "wss://demo-mr43kxju.livekit.cloud";
const route = useRoute();
const user = userStore();
const friendId = route.params.id as string;
let room: Room | null = null;
const token = ref<string>();
let media = null;
const role = route.params.role as string;
const roomName = route.params.roomName as string;
onMounted(async () => {
    if (!user.userInfo || !user.isLogin || !user.token) router.push('/login');
    await initRoom(roomName);
    await nextTick();
    if (role=== "initiator") {
        media = new MediaWs(Ws.getInstance());
        media.initListenAccept(onAccept);
        media.initListenReject(onReject);
        media.initListenCancel(onRemoteCancel);
        await callFriend();
    }

});

function onRemoteCancel() {
    Log.warn("对方已挂断");
    if (room) {
        room.disconnect();
        room = null;
    }
    setTimeout(() => {
        router.back();
    }, 1500);
}

async function onAccept(receiveId: string) {
    Log.ok("对方已接听");
    await initRoom(roomName);
}

function onReject(emitterId: string) {
    Log.warn("对方拒绝了您的通话请求");
    setTimeout(() => {
        router.back();
    }, 1500);
}

async function callFriend() {
    if (!user.userInfo) return;
    try {
        const dto: MediaApplyDTO = {
            receiverId: friendId,
            nickName: user.userInfo.nickName,
            icon: user.userInfo.icon,
            userId: user.userInfo.id
        };
        await MediaApi.send(dto);
        Log.info("已发送视频请求，等待对方接听...");

        // 监听对方响应
        const ws = user.getWs();
        const socket = ws?.getSocket();

        if (socket) {

        }
    } catch (error) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("呼叫失败");
            console.error(error);
        }
        setTimeout(() => router.back(), 1500);
    }
}

async function cancelCall() {
    try {
        await MediaApi.cancel(friendId);
        Log.info("已取消视频通话");
        router.back();
    } catch (error) {
        console.error(error);
        router.back();
    }
}

async function initRoom(roomName: string) {
    if (room) return;
    room = new Room();
    try {
        token.value = await getToken(roomName);
        if (!token.value) {
            Log.error("服务繁忙,请重试");
            console.log("获取token失败");
            return;
        }
        Log.info("操作成功");
        await startListen(room)
        await room.connect(
            URL,
            token.value
        );
    } catch (err) {
        Log.error("连接房间失败");
        console.error(err);
    }

}

async function turnCamera() {
    try {
        if (!room) return;
        await room.localParticipant.setCameraEnabled(!state.value.openCamera);
        state.value.openCamera = !state.value.openCamera;
        Log.ok("已关闭摄像头");
    } catch (error) {
        Log.error("关闭摄像头失败");
        console.error(error);

    }
}

async function enableCamara() {
    if (!room || !localVideo.value) { console.log("获取本地视频失败"); return; };

    try {
        await room.localParticipant.enableCameraAndMicrophone();
        room.localParticipant.videoTrackPublications.forEach(pub => {
            const track = pub.track;
            if (!track || !localVideo.value) { console.log("未获取到track"); return; }
            track.attach(localVideo.value);
        })
    }
    catch (err) {
        Log.error("开启摄像头和麦克风失败");
        console.error(err);
    }

}

async function muted() {
    if (!room) return;
    state.value.muted = !state.value.muted;
    room.localParticipant.setMicrophoneEnabled(!state.value.muted);
}

async function getToken(roomName: string) {
    if (!user.userInfo) return;
    try {
        const body = {
            userId: user.userInfo.id,
            userName: user.userInfo.nickName,
            roomName: roomName
        }
        const token = await MediaApi.getMediaToken(body);
        return token;

    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error(err);
        }
    }

}


async function leaveRoom() {
    if (!room) return;
    try {
        // 主动挂断，通知对方
        await MediaApi.cancel(friendId);

        room.disconnect();
        Log.ok("已离开房间");

        room = null;
        state.value.isConnected = false;
        state.value.isAccepted = false;
        if (localVideo.value) localVideo.value.srcObject = null;
        if (remoteVideo.value) remoteVideo.value.srcObject = null;
        router.back();

    } catch (err) {
        Log.error("离开房间失败");
        console.error(err);
    }

}


async function startListen(room: Room) {
    //监听轨道发布,等价于告诉你有媒体流,但此时不能直接获取,当远端有人发布视频轨道时，我自动订阅
    room.on(RoomEvent.TrackPublished, async (track) => {
        if (track.kind === 'video') {
            Log.primary("视频通话");
            track.setSubscribed(true);  //订阅轨道
        }
    });
    //监听轨道订阅,告诉你可以获取媒体流了 当我成功订阅远端视频后 把视频流绑定到 remoteVideo
    room.on(RoomEvent.TrackSubscribed, (track, publication, participant) => {
        if (track.kind === 'video' && remoteVideo.value) {
            track.attach(remoteVideo.value);
        }
    })

    //监听用户加入房间
    room.on(RoomEvent.ParticipantConnected, (participant) => {
        Log.info(`${participant.identity}已加入聊天`);
        state.value.isAccepted = true;
        participant.videoTrackPublications.forEach(async (sub) => {   //videoTrackPublications: 本地轨道列表
            sub.setSubscribed(true);
        })
    })

    //监听连接成功
    room.on(RoomEvent.Connected, async () => {
        Log.ok("连接成功");
        state.value.isConnected = true;
        await enableCamara();
    })

    //监听连接断开
    room.on(RoomEvent.Disconnected, () => {
        Log.info("已断开连接")
    });
}

onUnmounted(() => {
    if (room) {
        room.disconnect();
        room = null;
    }
    MediaWs.removeListenAccept();
    MediaWs.removeListenReject();
    MediaWs.removeListenCancel();


})

</script>