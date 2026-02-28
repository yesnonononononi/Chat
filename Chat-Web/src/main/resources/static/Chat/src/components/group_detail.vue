<template>
    <div class="flex flex-col  w-full gap-y-2  h-[100vh]" v-loading="loading"
        element-loading-background="rgba(220, 220, 220, 0.5)">
        <div class="flex">
            <el-icon size="24" class="cursor-pointer" @click="() => { $router.back() }">
                <Back />
            </el-icon>
        </div>
        <span>
            <img :src="group?.icon" alt="" class="w-16 h-16 rounded-full mt-4">
            <el-upload v-if="user.userInfo && group?.creatorId == user.userInfo.id"
                class="text-xs md:text-sm cursor-pointer hover:font-bold text-gray-400" :on-success="upload"
                :auto-upload="false" accept="image/*" :file-list="fileList" :limit="1" :disabled="loading"
                :on-change="handleFileSelected">
                <template #trigger>更改头像</template>
            </el-upload>
        </span>
        <span class="text-xs md:text-sm text-gray-500 max-w-96 whitespace-nowrap overflow-hidden text-ellipsis">群聊名称 :
            {{ group?.groupName }}</span>
        <span class="text-xs md:text-sm text-gray-500">活跃度: <span class=" text-gray-400"
                :class="{ 'text-green-300': group?.status == 1 }">{{ group?.status == 1 ? '高' : '低' }}</span></span>
        <el-collapse class="w-full" style="background-color:#ECEEF1;" accordion>
            <el-collapse-item title="群聊描述" name="1" class="" style="background-color: #ECEEF1;">
                <p class="el-collapse-item text-xs md:text-sm text-gray-400">{{ group?.groupDescription ||
                    "群主很懒,尚未设置群聊描述" }}</p>
            </el-collapse-item>
            <el-collapse-item title="群成员">
                <p class="text-xs md:text-sm text-gray-400">人数: {{ group?.number || '未知' }}</p>
                <div class="flex items-center gap-2 m-2" v-if="group_members.length >= 1">
                    <div v-for="(member) in group_members" :key="member.id" class="cursor-pointer"
                        @click.prevent="getUserMoreInfo(member.userId)">
                        <el-popover trigger="contextmenu" placement="top" :width=50
                            :disabled="member.role === GroupMember.ADMIN || member.role === GroupMember.OWNER">
                            <template #reference>
                                <div class="w-full h-full text-center">
                                    <img class="mr-2 w-10 h-10 rounded-full" :src="member.avatar" />
                                    <p class="text-xs text-gray-400 max-w-10 truncate">
                                        {{ member.role !== GroupMember.MEMBER ? '' : member.nickName }}
                                        <span class="text-yellow-300"
                                            v-if="member.role === GroupMember.ADMIN">@管理员</span>
                                        <span class="text-yellow-300"
                                            v-if="member.role === GroupMember.OWNER">@群主</span>
                                    </p>
                                </div>

                            </template>
                            <div class="w-auto flex flex-col items-center text-center gap-2 " v-show="member.role === GroupMember.MEMBER ||(user.userInfo?.role === GroupMember.ADMIN && member.role !== GroupMember.OWNER )|| user.userInfo?.role === GroupMember.OWNER">
                                <span
                                    class="border-b border-gray-400 w-full cursor-pointer hover:text-red-400" @click="kickUser(member)">踢出群聊</span>
                                <span class="w-full cursor-pointer hover:text-red-400" @click="muteUser(member)">{{ member.status === GroupStatus.FORBIDDEN ? '解禁' :'禁言' }}</span>
                            </div>
                        </el-popover>

                    </div>
                </div>

            </el-collapse-item>
            <el-collapse-item title="群聊申请" v-if="user.userInfo && group?.creatorId == user.userInfo.id">
                <div v-for="(apply) in group_apply" :key="apply.id" class="flex flex-col items-center gap-2 mt-2">
                    <div class="flex w-full items-center justify-between">
                        <div class="flex items-center gap-2">
                            <img :src="apply.applicantIcon" alt="" class="w-12 h-12  rounded-full">
                            <span>
                                <p>{{ apply.applicantNick }}</p>
                                <p>{{ apply.applicationReason || "" }}</p>
                            </span>
                        </div>

                        <div class="" v-if="apply.status === GroupMemberStatusEnum.PENDING">
                            <el-button type="primary" @click="approveGroupApply(apply)">同意</el-button>
                            <el-button type="danger" @click="handleRejectClick(apply)">拒绝</el-button>
                        </div>
                        <span class="text-xs md:text-sm text-gray-400 mr-2"
                            v-else-if="apply.status === GroupMemberStatusEnum.APPROVE">已批准</span>
                        <span class="text-xs md:text-sm text-gray-400 mr-2" v-else>已拒绝</span>
                    </div>
                </div>
            </el-collapse-item>
            <el-collapse-item title="群聊公告">
                <el-scrollbar>
                    <div v-for="(item, index) in group_notify" :key="index" class="mb-2 border">
                        <div class="flex justify-between items-center">
                            <img :src="item.publisherAvatar" class="w-6 h-6 rounded-full">
                            <div>
                                <p class="text-xs md:text-sm text-gray-400 mb-2">发布者:{{ item.publisherName }}</p>
                                <p class="text-xs md:text-sm text-gray-400 mb-2 ">发布于:{{
                                    TimeUtil.timestampToTime(item.createTime) }}</p>
                                <span class="text-xs cursor-pointer hover:text-red-500 text-gray-400"
                                    @click.prevent="delNotify(item.id)"
                                    v-if="item.publisherId == user.userInfo?.id || user.userInfo?.id === group?.creatorId">删除</span>
                            </div>
                        </div>
                        <span class="text-xs md:text-sm text-gray-400 mr-2">{{ item.content || '暂未设置任何群公告' }}</span>
                    </div>
                </el-scrollbar>
            </el-collapse-item>
            <el-collapse-item title="群聊设置" v-if="user.userInfo && group?.creatorId == user.userInfo.id">
                <div class="flex justify-between items-center">
                    <span>群聊公告</span>
                    <span @click.prevent="setupGroupNotify"
                        class="text-xs md:text-sm text-gray-400 hover:text-indigo-400 cursor-pointer mt-4">发布</span>
                </div>
                <div class="relative flex justify-between items-center">
                    <span>群聊描述</span>
                    <span
                        class="absolute right-0 text-xs md:text-sm text-gray-400 hover:text-indigo-400 cursor-pointer mt-4 transition-all duration-300 ease-out"
                        :class="editDescription
                            ? 'opacity-0 scale-75 pointer-events-none'
                            : 'opacity-100 scale-100 '
                            " @click.prevent='editDes'>
                        更改
                    </span>
                    <el-input type="text" placeholder="在此输入新群描述" size="default" style="width: 200px"
                        v-model="newDesciption" class="absolute inset-0 transition-all duration-300 ease-out" :class="{
                            'opacity-100  scale-100  ': editDescription,
                            ' opacity-0 scale-95 pointer-events-none ': !editDescription
                        }">
                        <template #suffix>
                            <span @click.prevent="putDescription" class="cursor-pointer hover:text-indigo-400">保存</span>
                            <span @click.prevent="editDescription = false"
                                class="cursor-pointer hover:text-indigo-400 ml-2">取消</span>
                        </template>
                    </el-input>
                </div>
                <div class="flex justify-between items-center">
                    <span>群聊管理</span>
                    <span class="text-xs md:text-sm text-gray-400 hover:text-indigo-400 cursor-pointer mt-4">设置</span>
                </div>
                <div class="flex justify-between items-center">
                    <span>群聊权限</span>
                    <span class="text-xs md:text-sm text-gray-400 hover:text-indigo-400 cursor-pointer mt-4">设置</span>
                </div>
            </el-collapse-item>
        </el-collapse>

        <el-dialog v-model="rejectDialogVisible" title="拒绝申请" width="30%">
            <el-input v-model="rejectReason" placeholder="请输入拒绝理由" type="textarea" :rows="3" />
            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="rejectDialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="confirmReject">确定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>

</template>
<script setup lang="ts">
import {nextTick, onMounted, ref} from "vue";
import {GroupApi} from "../api/group";
import type {GroupApplicationVO, GroupChatVO, GroupMemberVO, groupNoticeVO, putGroupDto} from "../types/group";
import {useRoute} from "vue-router";
import {Log} from "../utils/TipUtil";
import {BusinessError} from "../exception/BusinessError";
import router from "../router";
import {userStore} from "../store/UserStore";
import {GroupMemberStatusEnum} from "../enums/GroupMemberStatusEnum";
import {GroupMember} from "../enums/GroupMember";
import {TimeUtil} from "../utils/time";
import {FileUtil} from "../utils/FIle";
import {uploadFile} from "../api/common";
import type {data} from "../types/user";
import {GroupStatus} from "../enums/GroupStatus";

const route = useRoute();
const user = userStore();
const fileList = ref<any[]>();
const groupId = route.params.id as string;
const group = ref<GroupChatVO>();
const group_members = ref<GroupMemberVO[]>([]);
const group_apply = ref<GroupApplicationVO[]>([]);
const group_notify = ref<groupNoticeVO[]>();
const loading = ref(false);
const editDescription = ref<boolean>(false);
const newDesciption = ref<any>("");
// 拒绝相关
const rejectDialogVisible = ref(false);
const rejectReason = ref("");
const currentRejectApp = ref<GroupApplicationVO | null>(null);

onMounted(async () => {
    if (!user.userInfo || !user.userInfo.id || !user.isLogin) {
        router.push("/login");
    }
    try {
        loading.value = true;
        await GroupApi.queryGroupById(groupId).then(res => {
            group.value = res;
        }).catch(err => {
            if (err instanceof BusinessError) {
                Log.error(err.message);
            } else {
                Log.error("服务繁忙");
                console.error("未能成功获取群聊基本信息", err);
            }
        });
        queryMembers();
        queryNotify();
        if (group.value && user.userInfo && user.userInfo.id === group.value.creatorId) {
            queryGroupApplication();
        }
    } finally {
        loading.value = false;

    }
})

function putDescription() {
    if (newDesciption.value.trim().length === 0) {
        Log.error("请输入群描述");
        return;
    }
    try {
        if (loading.value) return;
        loading.value = true;
        GroupApi.putGroup({
            id: groupId,
            creatorId: user.userInfo?.id,
            groupDescription: newDesciption.value
        }).then(res => {
            Log.ok("操作成功");
            editDescription.value = false;
            newDesciption.value = "";
            if (group.value) group.value.groupDescription = newDesciption.value;
        }).catch(err => {
            if (err instanceof BusinessError) {
                Log.error(err.message);
            } else {
                Log.error("服务繁忙");
                console.error("未能成功修改群描述", err);
            }
        })
    } finally {
        loading.value = false;
    }
}

function queryMembers() {
    GroupApi.queryMemberByGroupId(groupId).then(res => {
        group_members.value = res.records;
    }).catch(err => {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error("未能成功获取群聊成员列表", err);
        }
    })
}

function queryGroupApplication() {
    GroupApi.listGroupApplyByGroup(groupId).then(res => {
        group_apply.value = res;
    }).catch(err => {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error(err);
        }
    })
}

function getUserMoreInfo(user_id: string) {
    console.log(user_id)
    router.push({
        name: "user-introduce",
        params : { id: user_id }
    })
}

function approveGroupApply(apply: GroupApplicationVO) {
    if (!user.userInfo) return;
    GroupApi.approveGroupApply({
        id: apply.id,
        groupId: apply.groupId,
        applicantId: apply.applicantId,
        processedBy: user.userInfo.id
    }).then(() => {
        Log.ok("操作成功");
        apply.status = GroupMemberStatusEnum.APPROVE;
    }).catch((err) => {
        if (err instanceof BusinessError) {
            Log.error(err.message);
            return;
        }
        console.error(err);
        Log.error("服务繁忙");
    })
}

function setupGroupNotify() {
    router.push({
        name: "group-notify",
        params: { id: groupId }
    })
}

function queryNotify() {
    GroupApi.queryNotify(groupId).then(res => {
        group_notify.value = res;
    }).catch(err => {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error("未能成功获取群聊公告", err);
        }
    })
}

async function delNotify(notifyID: string) {
    try {
        if (loading.value) return;
        loading.value = true;
        await GroupApi.delNotify(groupId, notifyID)
        Log.ok("操作成功");
        if (group_notify.value) group_notify.value = group_notify.value.filter(item => item.id !== notifyID);
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error("未能成功删除群聊", err);
        }
    }
    finally {
        loading.value = false;
    }


}
function handleRejectClick(apply: GroupApplicationVO) {
    currentRejectApp.value = apply;
    rejectDialogVisible.value = true;
    rejectReason.value = "";
}

function confirmReject() {
    if (!currentRejectApp.value) return;
    
    rejectApplication(currentRejectApp.value, rejectReason.value);
    rejectDialogVisible.value = false;
}

function rejectApplication(apply: GroupApplicationVO, reason: string) {
    if (!user.userInfo) {
        Log.error("请先登录");
        return;
    }
    GroupApi.rejectGroupApply({
        id: apply.id,
        applicantId: apply.applicantId,
        groupId: apply.groupId,
        processedBy: user.userInfo.id,
        rejectionReason: reason
    }).then(() => {
        Log.ok("操作成功");
        apply.status = GroupMemberStatusEnum.REJECT;
    }).catch((err) => {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            console.error("操作失败", err);
            Log.error("服务繁忙");
        }

    })
}


async function handleFileSelected(file: any) {
    if (file.status !== 'ready' || !file.raw) return;
    await upload(file.raw);
}

async function upload(file: File) {
    try {
        if (loading.value || !file) return;
        loading.value = true;
        const f = await FileUtil.beforeValidate(file);
        const res: data<string> = await uploadFile(file);
        await GroupApi.putGroup({ id: groupId, icon: res.data })
        if (file && group.value) {
            group.value.icon = res.data;
        }
    } catch (err) {
        if (err instanceof Error) Log.error(err.message);
        else {
            Log.error("服务繁忙");
            console.error("上传失败", err);
        }
    } finally {
        loading.value = false;
        fileList.value = [];
    }

}

async function editDes() {
    editDescription.value = true;
    await nextTick();
    newDesciption.value?.focus();
}



async function muteUser(user: GroupMemberVO) {
    try {
        if (!user.id || loading.value) return;
        loading.value = true;
        const newStatus = user.status === GroupStatus.FORBIDDEN ? GroupStatus.NORMAL : GroupStatus.FORBIDDEN;
        const body: putGroupDto = {
            userId: user.userId,
            groupId: groupId,
            status: newStatus,
            memberId: user.id
        }
        await GroupApi.banMember(body);
        Log.ok("操作成功");
        user.status = newStatus;
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error("未能成功修改群成员状态", err);
        }
    } finally {
        loading.value = false;
    }
}

async function kickUser(user: GroupMemberVO) {
    try {
        if (!user.id || loading.value) return;
        loading.value = true;
        const body: putGroupDto = {
            userId: user.userId,
            groupId: groupId,
            status: GroupStatus.NORMAL,
            memberId: user.id
        }
        await GroupApi.delMember(body);
        Log.ok("操作成功");
        group_members.value = group_members.value.filter(item => item.id !== user.id);
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error("服务繁忙");
            console.error("踢出操作未能成功", err);
        }
    } finally {
        loading.value = false;
    }
}


</script>
<style scoped>
::v-deep .el-collapse-item .el-collapse-item__header {
    background-color: #ECEEF1;
}

::v-deep .el-collapse-item__wrap {
    background-color: #ECEEF1;
}
</style>