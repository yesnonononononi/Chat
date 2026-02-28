<template>
    <div class="w-full h-[100vh]" v-loading="loading" element-loading-background="rgba(220, 220, 220, 0.5)">
        <div class="w-full flex flex-col justify-center border-b-4 border-gray-300">
            <div class="flex justify-between items-center w-full">
                <el-icon @click.prevent="router.back()" class="cursor-pointer">
                    <Back />
                </el-icon>
                <el-icon @click="getMore" class="cursor-pointer">
                    <More />
                </el-icon>
            </div>
            <div class="w-auto p-2 h-auto flex  my-8">
                <img :src="userInfo?.icon" alt="" class="w-24 h-24 rounded-xl">
                <div class="ml-8 flex flex-col justify-around ">
                    <div class="text-xl font-bold flex items-center gap-2 flex-wrap">
                        {{ userInfo?.nickName }}
                        <span class="w-2 h-2 rounded-full" v-if="userInfo?.gender"
                            :class="userInfo?.gender == 1 ? 'bg-blue-500' : 'bg-pink-500'"></span>
                        <span v-if="userInfo?.role === 'admin'"
                            class="px-2 py-0.5 text-xs rounded-full bg-gradient-to-r from-yellow-500 to-yellow-600 text-white font-bold shadow-sm scale-75 origin-left">管理员</span>
                        <span v-if="userInfo?.role === 'super_admin'"
                            class="px-2 py-0.5 text-xs rounded-full bg-gradient-to-r from-purple-600 via-fuchsia-500 to-pink-500 text-white font-bold shadow-md flex items-center gap-1 border border-yellow-200/50 scale-75 origin-left md:scale-90">
                            <el-icon class="text-yellow-300">
                                <Trophy />
                            </el-icon>
                            超级管理员
                        </span>
                    </div>
                    <div class="text-sm text-gray-400">爱好:{{ userInfo?.hobby || '未展示任何爱好' }}</div>
                    <div class="text-sm text-gray-400">年龄:{{ userInfo?.age || '未知' }}</div>
                </div>
            </div>
        </div>
        <div v-if="isLink">
            <div class="w-full h-auto text-center p-4 border-b-2 border-gray-300 cursor-pointer"
                @click.prevent="toChat">
                <span class="text-xs md:text-sm text-indigo-400">发消息</span>
            </div>
            <div class="w-full h-auto text-center p-4 border-b-2 border-gray-300 cursor-pointer" @click="toVideo">
                <span class="text-xs md:text-sm text-indigo-400">视频通话</span>
            </div>
            <div>
                <div class="w-full h-auto text-center p-4 border-b-2 border-gray-300 cursor-pointer" @click="delLink">
                    <span class="text-xs md:text-sm text-red-400">删除好友</span>
                </div>
            </div>
        </div>
        <div v-else-if="userInfo?.id !== user_me.userInfo?.id">
            <div class="w-full h-auto text-center p-4 border-b-2 border-gray-300 cursor-pointer" @click="dialogVisible = true">
                <span class="text-xs md:text-sm text-indigo-400">添加好友</span>
            </div>
        </div>

        <!-- 添加好友弹窗 -->
        <el-dialog v-model="dialogVisible" title="添加好友" width="80%" :before-close="handleClose">
            <el-input v-model="applyReason" placeholder="请输入申请理由" type="textarea" :rows="3" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="sendApplication">发送申请</el-button>
                </span>
            </template>
        </el-dialog>

    </div>

</template>

<script lang="ts" setup>
import {onMounted, ref} from 'vue';
import router from '../router';
import {useRoute} from 'vue-router';
import {userStore} from '../store/UserStore';
import {UserApi} from '../api/user';
import type {userInfo} from '../types/user';
import {Log} from '../utils/TipUtil';
import {BusinessError} from '../exception/BusinessError';
import {FriendApi} from '../api/friend';
import type {friend_apply_dto} from '../types/friend';

const route = useRoute();
const user_me = userStore();
const userId = route.params.id as string;
const userInfo = ref<userInfo>();
const loading = ref(false);
const isLink = ref(false);
const dialogVisible = ref(false);
const applyReason = ref('');

const handleClose = (done: () => void) => {
    done();
}

async function sendApplication() {
    if (loading.value) return;
    try {
        loading.value = true;
        if (!userInfo.value?.id || !user_me.userInfo) {
            Log.error('未获取到用户信息');
            return;
        }
        const body: friend_apply_dto = {
            id: "",
            applyReason: applyReason.value,
            recipientId: userInfo.value.id,
            applicantId: user_me.userInfo.id
        }
        await FriendApi.sendApplication(body);
        Log.ok("申请已发送");
        dialogVisible.value = false;
        applyReason.value = "";
    } catch (error: any) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("发送好友请求遇到问题", error);
        }
    } finally {
        loading.value = false;
    }
}
onMounted(async () => {
    if (!user_me.isLogin || !user_me.token) router.push('/login');
    await queryUserById();
    await hasLink();
})
async function queryUserById() {
    try {
        if (loading.value) return;
        loading.value = true;
        if (!userId) return;
        const res = await UserApi.getUserInfoById(userId);
        userInfo.value = res;
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            console.error(err);
            Log.error("服务繁忙");
        }
    } finally {
        loading.value = false;
    }

}

async function delLink() {
    try {
        if (loading.value) return;
        loading.value = true;
        if (!userInfo.value?.id) return;
        await FriendApi.delLink({
            userID: user_me.userInfo?.id,
            linkID: userInfo.value.id
        });
        Log.ok("删除成功");
        isLink.value = false;
        // 更新本地存储
        const link = sessionStorage.getItem('l');
        if (link) {
            const users = JSON.parse(link) as { linkId: string, nickName: string }[];
            const newUsers = users.filter(u => u.linkId !== userInfo.value?.id);
            sessionStorage.setItem('l', JSON.stringify(newUsers));
        }
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error(err);
        }
    } finally {
        loading.value = false;
    }
}

 

function getMore() {
    router.push({
        name: 'user_more',
        params: { id: userId },
    });
}

function toChat() {
    router.push({
        name: 'chat',
        params: { id: userId },
    });
}

function toVideo() {
    try {

        if (!user_me.userInfo || !userInfo.value) return;
        router.push({
            name: 'video-call'
            , params: {
                id: userInfo.value.id,
                roomName: user_me.userInfo.id + userInfo.value.id,
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

async function hasLink() {
    try {
        loading.value = true;
        const link = sessionStorage.getItem('l') as string;

        const user = JSON.parse(link) as { linkId: string, nickName: string }[];
        user.forEach(element => {

            if (element.linkId === userId && element.nickName === userInfo.value?.nickName) {
                isLink.value = true;
            }
        });
    } catch (e) {
        if (e instanceof BusinessError) {
            Log.error(e.message);
        } else {
            Log.error("服务繁忙");
            console.error(e);
        }
    } finally {
        loading.value = false;
    }
}
</script>