<template>
    <div class="common-layout h-[100vh]" v-loading="loading" element-loading-background="rgba(0, 0, 0, 0.7)">
        <el-container>
            <el-header class="flex items-center justify-between  ">
                <el-icon @click.prevent="router.push('/')" class="cursor-pointer">
                    <Back />
                </el-icon>
                <div class="m-auto">
                    <div class="border   flex">
                        <button class="p-1 flex-1" :class="queryPerson ? 'bg-black text-white font-bold' : ''"
                            @click.prevent="queryPerson = true">找人</button>
                        <button class="p-1 flex-1" :class="!queryPerson ? 'bg-black text-white font-bold' : ''"
                            @click.prevent="queryPerson = false">找群</button>
                    </div>
                </div>
                <div>
                    <el-button type="success" @click="router.push('/apply')">
                        申请
                    </el-button>
                </div>
            </el-header>
            <el-container >
                <el-main>
                    <div>
                        <div class="w-full">
                            <el-input v-model="input" placeholder="请输入群聊/用户名" class="rounded-2xl"
                                @keyup.enter="queryPerson ? search(input) : searchGroup(input)">
                                <template #suffix>
                                    <el-icon size="24px" class="mr-4 cursor-pointer" color="green"
                                        @click="queryPerson ? search(input) : searchGroup(input)">
                                        <Search />
                                    </el-icon>
                                </template>
                            </el-input>
                        </div>
                        <div class="w-full mt-4">
                            <el-scrollbar class="flex w-full h-[500px] flex-col  gap-y-4 ">

                                <div class=" w-full  h-20 mb-4  rounded-xl" v-if="queryPerson">
                                    <div v-for="item in searchList" :key="item.id"
                                        class="flex items-center justify-between">
                                        <div class="ml-4 flex item-center ">
                                            <img :src="item.icon" class="w-16 h-16 rounded-full" alt="">
                                            <div class=" ml-4">
                                                <p class="">{{ item.nickName || '未命名' }}</p>
                                                <div class="flex mt-2 ">
                                                    <span v-if="item.gender === 1"
                                                        class="text-xs text-center  border mr-2 border-blue-300 bg-blue-300/50 rounded-[6px] p-[2px]">男</span>
                                                    <span v-if="item.gender === 2"
                                                        class="text-xs text-center  border mr-2  border-pink-300 bg-pink-300/50 rounded-[6px] p-[2px]">女</span>
                                                    <span class="text-xs md:text-sm text-gray-300" v-if="item.hobby">{{
                                                        item.hobby }}</span>
                                                </div>

                                            </div>
                                        </div>
                                        <div
                                            class="w-auto h-auto border-2 py-[1px] px-[4px]  border-gray-300 rounded-xl cursor-pointer">
                                            <span class="text-xs md:text-sm" @click="sendApplication(item.id)">{{ '添加'
                                                }}</span>
                                        </div>
                                    </div>
                                </div>
                                <div class="w-full flex items-center justify-between  h-20 mb-4  rounded-xl"
                                    v-if="!queryPerson">
                                    <div v-for="item in searchListOfGroup" :key="item.groupId"
                                        class="flex item-center w-full justify-between ">
                                        <div class="ml-4 flex item-center">
                                            <img :src="item.icon" class="w-12 h-12" alt="">
                                            <div class=" flex flex-col item-center justify-around ml-4">
                                                <p class="">{{ item.groupName || '未命名' }}</p>
                                                <span class="text-xs md:text-sm text-gray-400"
                                                    v-if="item.groupDescription">{{
                                                    item.groupDescription }}</span>
                                            </div>
                                        </div>

                                        <div
                                            class="w-auto  border-2 p-2  border-gray-300 rounded-xl cursor-pointer text-center">
                                            <span class="text-xs md:text-sm"
                                                @click="sendApplicationGroup(item.id)">申请加入</span>
                                        </div>
                                    </div>
                                </div>


                            </el-scrollbar>
                        </div>

                    </div>

                </el-main>
            </el-container>
        </el-container>
    </div>
</template>
<script setup lang="ts">;
import { FriendApi } from '../api/friend';
import { UserApi } from '../api/user';
import { GroupApi } from '../api/group';
import { type search_user, type userInfo, type data, type page } from '../types/user';
import type { friend_apply, friend_apply_dto } from '../types/friend';
import { onMounted, ref } from 'vue';
import { Log } from '../utils/TipUtil';
import { userStore } from '../store/UserStore';
import router from '../router';
import { Search } from '@element-plus/icons-vue';
import type { GroupApplicationDTO, GroupChatDto } from '../types/group';
import { BusinessError } from '../exception/BusinessError';

const input = ref<string>('');
const queryPerson = ref<boolean>(true);
const searchList = ref<search_user[]>([]);
const searchListOfGroup = ref<GroupChatDto[]>([]);
const userInfo = ref<userInfo>();
const loading = ref<boolean>(false);
const user_me = userStore();
 

async function search(nickName: string) {
    if (loading.value) return;
    loading.value = true;
    try {
        const res: any = await UserApi.getUserInfoByNick(nickName);
        searchList.value = res;
        loading.value = false;
    } catch (error) {
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

async function searchGroup(groupName: string) {
    if (loading.value) return;
    try {
        loading.value = true;
        const res = await GroupApi.queryGroupByName(groupName, 1, 10);
        console.log("搜索群组结果", res);
        searchListOfGroup.value = res.records;
        loading.value = false;
    } catch (error) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("搜索群组出错", error);
        }
    } finally {
        loading.value = false;
    }
}
async function sendApplication(user_id: string) {
    if (loading.value) return;
    try {
        loading.value = true;
        if (!user_id || !user_me.userInfo) { Log.error('未获取到用户信息'); return; }
        const body: friend_apply_dto = {
            id: "",
            applyReason: "",
            recipientId: user_id,
            applicantId: user_me.userInfo.id
        }
        await FriendApi.sendApplication(body);
        loading.value = false;
        Log.ok("操作成功");

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
async function sendApplicationGroup(groupId: string) {
    if (loading.value) return;
    try {
        loading.value = true;
        const body: GroupApplicationDTO = {
            groupId: groupId,
            applicationReason: "",
            applicantId: user_me.userInfo?.id,
        }
        await GroupApi.applyGroup(body);
        loading.value = false;
        Log.ok("操作成功");
    } catch (error: any) {
        if (error instanceof BusinessError) {
            Log.error(error.message);
        } else {
            Log.error("服务繁忙");
            console.error("发送群组请求遇到问题", error);
        }
    } finally {
        loading.value = false;
    }
}

</script>
