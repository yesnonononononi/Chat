<template>

    <div class="flex justify-center  h-[100vh]" v-loading="loading"
        element-loading-background="rgba(220, 220, 220, 0.5)">
        <el-icon size="24" class="cursor-pointer" @click="router.push('/user/link')">
            <Back />
        </el-icon>
        <div class="w-full bg-white rounded-xl m-4 text-center">
            <div class="h-full mt-8 flex flex-col items-center">
                <div class="mb-8 border border-gray-500  text-center">
                    <button class="w-[50%] h-full  text-xl cursor-pointer" @click="getFriendApply"
                        :class="{ 'bg-green-400': friendApply }"> 好友申请 </button>
                    <button class="w-[50%] h-full  text-xl cursor-pointer" @click="getGroupApply"
                        :class="{ 'bg-indigo-400': !friendApply }"> 群聊申请 </button>
                </div>
                <el-scrollbar class="flex w-full h-[500px] flex-col items-center gap-y-4 ">
                    <div v-if="friendApply && applicationList.length >= 1">
                        <div v-for="item in applicationList" :key="item.id" @click="getUserInfo(item.applicantId)"
                            class="flex items-center justify-between w-72 h-20 mb-4 bg-gray-300/50 rounded-xl">
                            <span class="flex items-center">
                                <img :src="item.icon" alt="" class="w-12 h-12 rounded-full bg-white ml-4">
                                <div class="ml-2 ">
                                    <p class="text-sm ">{{ item.applicantNick }}</p>
                                    <p class="text-xs">{{ item.applyReason || '' }}</p>
                                </div>
                            </span>
                            <div v-if="item.status === 0" class="relative z-10 flex">
                                <el-button type="success" @click="ackApply(item)" class="text-xs md:text-sm" >同意</el-button>
                                <el-button type="danger" @click="rejectApply(item)" class="text-xs md:text-sm mr-2">拒绝</el-button>
                            </div>
                            <div v-if="item.status == 1" class="relative z-10 mr-4">
                                <el-icon color="green"><Select /></el-icon>
                            </div>
                            <div v-if="item.status == 2" class="relative z-10 mr-4">
                                <span class="text-xs md:text-sm text-gray-400 ">已拒绝</span>
                            </div>
                        </div>
                    </div>
                    <div v-if="!friendApply && groupApplicationList.length >= 1">
                        <div v-for="item in groupApplicationList" :key="item.id" @click="getUserInfo(item.userId)"
                            class="flex items-center justify-between w-72 h-20 mb-4 bg-gray-300/50 rounded-xl">
                            <span class="flex items-center">
                                <img :src="item.groupIcon" alt="" class="w-12 h-12 rounded-full bg-white ml-4" />
                                <div>
                                    <p class="text-sm ">{{ item.groupName }}</p>
                                    <p class="text-xs">{{ item.applicationReason || '' }}</p>
                                </div>
                            </span>
                            <span v-if="item.status === 'pending'" class="mr-2">待处理</span>
                            <span v-if="item.status === 'rejected'" class="mr-2 flex flex-col items-end">
                                <span>已拒绝</span>
                                <span class="text-xs text-blue-400 cursor-pointer hover:underline" @click.stop="viewReason(item.rejectionReason)">查看拒绝原因</span>
                            </span>
                            <span v-if="item.status === 'approved'" class="mr-2">已同意</span>
                        </div>
                    </div>

                </el-scrollbar>
            </div>
        </div>

        <el-dialog v-model="viewReasonDialogVisible" title="拒绝原因" width="30%">
            <span>{{ currentReason }}</span>
            <template #footer>
                <span class="dialog-footer">
                    <el-button type="primary" @click="viewReasonDialogVisible = false">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>



</template>
<script lang="ts" setup>
import type {page} from '../types/user';
import {type userInfo} from '../types/user';
import type {friend_apply} from '../types/friend';
import {onMounted, ref} from 'vue';
import {Log} from '../utils/TipUtil';
import router from '../router';
import {UserApi} from '../api/user';
import {FriendApi} from '../api/friend';
import {userStore} from '../store/UserStore';
import {BusinessError} from '../exception/BusinessError';
import {GroupApi} from '../api/group';
import type {GroupApplicationVO} from '../types/group';

const user_me = userStore();
const userInfo = ref<userInfo>();
const applicationList = ref<friend_apply[]>([]);
const groupApplicationList = ref<GroupApplicationVO[]>([]);
const friendApply = ref<boolean>(true);
const loading = ref(false);
const viewReasonDialogVisible = ref(false);
const currentReason = ref("");

function viewReason(reason: string | undefined) {
    currentReason.value = reason || "管理员未填写拒绝理由";
    viewReasonDialogVisible.value = true;
}

onMounted(() => {
 
    (async () => {
        getFriendApply();
    })();
})
async function getFriendApply() {
    if (loading.value) return;
    applicationList.value.length = 0;
    friendApply.value = true;
    try {
        loading.value = true;
        const res: page<friend_apply[]> = await FriendApi.queryApplication();
        if (res && res.records.length > 0) {
            applicationList.value = res.records;
        }
        loading.value = false;
    } catch (error: any) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("获取用户信息时错误", error);
        }
    } finally {
        loading.value = false;
    }
}

async function getGroupApply() {
    if (loading.value) return;
    friendApply.value = false;
    groupApplicationList.value.length = 0;
    try {
        loading.value = true;
        const res: GroupApplicationVO[] = await GroupApi.listGroupApplyByUser();
        groupApplicationList.value = res;
        loading.value = false;
    } catch (error: any) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("获取用户信息时错误", error);
        }
    } finally {
        loading.value = false;
    }
}

async function getUserInfo(user_id: string) {
    if (!user_id) {
        Log.error("未获取到用户信息");
        return;
    }
    if (loading.value) return;
    try {
        loading.value = true;
        const res = await UserApi.getUserInfoById(user_id);
        userInfo.value = res;
        loading.value = false;
    } catch (error: any) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error(error);
        }
    } finally {
        loading.value = false;
    }
}

async function ackApply(user: friend_apply) {
    try {
        if (loading.value) return;
        if (!user) {
            Log.error("未获取到用户信息");
            setTimeout(() => router.push('/login'), 1500);
            return;
        }

        loading.value = true;
        await FriendApi.ackApplication(user);
        Log.ok("操作成功");
        user.status = 1;
        loading.value = false;
    } catch (error: any) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("确认用户信息时错误", error);
        }
    } finally {
        loading.value = false;
    }
}
async function rejectApply(user: friend_apply) {
    try {
        if (!user) {
            Log.error("未获取到用户信息");
            setTimeout(() => router.push('/login'), 1500);
            return;
        }
        if (loading.value) return;
        loading.value = true;
        await FriendApi.rejectApplication(user);
        Log.ok("操作成功");
        user.status = 2;
        loading.value = false;
    } catch (error: any) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("确认用户信息时错误", error);
        }
    } finally {
        loading.value = false;
    }
}
</script>