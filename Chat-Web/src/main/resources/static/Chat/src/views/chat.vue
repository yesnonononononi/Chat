<template>
  <div class="fixed inset-0 h-full bg-gray-100 p-0 m-0 z-[-2]"></div>
  <div class="common-layout fixed w-full h-[100vh]" v-loading="loading"
    element-loading-background="rgba(220, 220, 220, 0.5)">
    <el-container class="h-24">
      <el-header class="flex items-center justify-around bg-gray-100">
        <span class="text-xl md:text-3xl font-bold text-green-400">
          chat
        </span>
        <span @click.prevent="handleCreateGroup" class="text-sm cursor-pointer">创建群聊</span>
        <router-link v-if="user.userInfo?.role ===UserRoleEnum.ADMIN || user.userInfo?.role === UserRoleEnum.SUPER_ADMIN" to="/admin-auth" class="text-sm cursor-pointer">管理中心</router-link>
        <!-- 用户信息 -->
        <div class=" w-10 h-full relative  flex flex-col items-center  justify-center  group cursor-pointer"
          v-if="user.userInfo">
          <img :src="user.userInfo.icon" alt="" class="w-10 h-10 rounded-full object-cover " />
          <div
            class="absolute z-[99999] top-[60px] w-[200px] h-[200px] md:w-[272px] md:h-[300px] opacity-0 pointer-events-none group-hover:opacity-100 group-hover:pointer-events-auto group-hover:-translate-y-2 transition-all duration-700 bg-white  ">
            <div class="h-16 w-full  border-b-2">
              <div class="w-full h-16 flex  justify-between items-center">
                <div class="">
                  <p class="text-sm ml-2">{{ user.userInfo.nickName }}</p>
                </div>
              </div>
            </div>
            <div class="flex justify-between items-center p-2">
              <label for="user-detail" class="text-center cursor-pointer" @click="router.push('/user-info')">
                <div class="w-10 h-10 md:w-12 md:h-12 rounded-full bg-gray-300" id="user-detail">
                  <img class="w-10 h-10 md:w-12 md:h-12  rounded-full"
                    src="https://summit-oss.oss-cn-beijing.aliyuncs.com/icon/%E7%94%A8%E6%88%B7%E8%B4%A6%E5%8F%B7%20user_1177568_%E7%88%B1%E7%BB%99%E7%BD%91_aigei_com.png"
                    alt="">
                </div>
                <span class="text-xs md:text-sm">用户详情</span>
              </label>
              <label for="user-link" class="text-center cursor-pointer" @click="router.push('/user/link')">
                <div class="w-10 h-10 md:w-12 md:h-12 rounded-full bg-gray-300" id="user-link">
                  <img class="w-10 h-10 md:w-12 md:h-12  rounded-full"
                    src="https://summit-oss.oss-cn-beijing.aliyuncs.com/icon/%E7%94%9F%E6%88%90%E5%A5%BD%E5%8F%8B%E5%9B%BE%E6%A0%87.png"
                    alt="">
                </div>
                <span class="text-xs md:text-sm ">好友</span>
              </label>
              <label for="log-out" class="text-center cursor-pointer" @click="loginout">
                <div class="w-10 h-10 md:w-12 md:h-12 rounded-full bg-gray-300" id="log-out">
                  <img class="w-10 h-10 md:w-12 md:h-12  rounded-full"
                    src="https://summit-oss.oss-cn-beijing.aliyuncs.com/icon/%E9%80%80%E5%87%BA_%E7%88%B1%E7%BB%99%E7%BD%91_aigei_com.png"
                    alt="">
                </div>
                <span class="text-xs md:text-sm">退出登录</span>
              </label>
            </div>
          </div>
        </div>
        <!-- 通知 -->
        <div class="relative w-12 h-full flex flex-col items-center justify-center group cursor-pointer">
          <!-- 通知图标 -->
          <el-icon class="h-full" size="24px">
            <Bell />
          </el-icon>
          <!-- 透明桥接层，连接图标和下拉框 -->
          <div class="absolute top-16 w-full h-4"></div>
          <!-- 下拉框 -->
          <div
            class="absolute top-20 left-1/2 -translate-x-1/2 w-48 md:w-64 h-72 rounded-xl z-[100000] pointer-events-none opacity-0 bg-white text-center group-hover:opacity-100 group-hover:pointer-events-auto group-hover:-translate-y-2 transition-all duration-700 shadow-lg border">
            <div class="border-b-2 h-8">
              <span class="text-sm md:text-xl font-bold">消息通知</span>
            </div>
            <el-scrollbar class="w-full flex flex-col overflow-hidden  justify-around cursor-pointer">
              <div v-for="notify in notifications" :key="notify.id"
                class="w-full h-auto max-h-24 p-4 flex justify-between items-center">
                <p class="text-xs md:text-sm font-bold">{{ notify.msg }}</p>
                <div>
                  <p class="text-xs text-gray-400">发布时间:</p>
                  <p class="text-xs   text-gray-400">{{ notify.createTime }}</p>
                </div>

              </div>
            </el-scrollbar>
          </div>

        </div>
      </el-header>
      <el-container>
        <el-aside width="auto" class="relative mt-4  flex  bg-gray-200/30 rounded-xl">
          <el-icon size="24" class="absolute cursor-pointer inset-y-[50%]" v-if="fold" @click="fold = false">
            <Expand />
          </el-icon>
          <div class=" overflow-hidden h-[100vh] transition-all  duration-300 ease-in-out"
            :class="fold ? 'w-0 opacity-0 -translate-x-full' : 'w-[200px] md:w-[300px] opacity-100 translate-x-0'">
            <div class="flex justify-around items-center  w-full h-10 text-center">
              <el-icon class="cursor-pointer">
                <Search />
              </el-icon>
              <div class="border flex">
                <button class="p-1 flex-1" :class="selectfriend ? 'bg-green-300 text-white font-bold' : ''"
                  @click.prevent="queryLinkUser">好友</button>
                <button class="p-1 flex-1" :class="!selectfriend ? 'bg-indigo-300 text-white font-bold' : ''"
                  @click.prevent="queryGroup">群聊</button>
              </div>
              <el-icon class="cursor-pointer" @click="fold = true">
                <Fold />
              </el-icon>
            </div>
            <el-scrollbar class="h-full w-full">
              <div class="h-full w-full" v-if="selectfriend">
                <div v-for="item in search_User" :key="item.linkId" @click="handleChat(item)"
                  class="flex justify-between items-center h-16 md:h-[100px] my-4 text-center group cursor-pointer rounded-[4px] border-gray-300 border-b">
                  <div class="flex items-center">
                    <div class="w-auto h-auto rounded-full">
                      <img :src="item.icon" alt="" class="w-10 h-10 md:w-16 md:h-16   rounded-full" />
                    </div>
                    <div class="ml-4">
                      <p class="mb-2 text-xl font-bold group-hover:text-blue-500">
                        {{ item.nickName || `用户${item.linkId}` }}
                      </p>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="!selectfriend">
                <div v-for="(item) in search_Group" :key="item.id" class="cursor-pointer"
                  @click.prevent="handleChatWithGroup(item.id, item.groupName || '')">
                  <div
                    class="flex justify-between items-center h-16 md:h-[100px] my-4 text-center group cursor-pointer rounded-[4px] border-gray-300 border-b">
                    <div class="flex items-center">
                      <div class="w-auto h-auto rounded-full flex items-center">
                        <img :src="item.icon" alt="" class="w-10 h-10 md:w-16 md:h-16   rounded-full" />
                        <div class="ml-2">
                          <p class="md:max-w-48 max-w-32 whitespace-nowrap overflow-hidden text-ellipsis">{{
                            item.groupName || `未知群聊${item.id}` }}</p>
                          <p
                            class="md:text-sm text-gray-400 mt-2 max-w-32 md:max-w-48 whitespace-nowrap overflow-hidden text-ellipsis">
                            {{ item.groupDescription || '未设置群描述' }}</p>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </el-scrollbar>
          </div>

        </el-aside>
        <el-main class="md:ml-4 h-full  bg-gray-200/50 ">
          <router-view v-if="$route.name !== 'Home'" :key="$route.fullPath"></router-view>
          <EmptyState v-else />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from "vue";
import router from "../router";
import { FriendApi } from "../api/friend";
import { userStore } from "../store/UserStore";
import type { friend_info } from "../types/friend";
import { Ws } from "../utils/Socket/webSocket";
import { msgStore } from "../store/MessageStore";
import type { GroupChatDto } from "../types/group";
import { GroupApi } from "../api/group";
import { ElMain } from "element-plus";
import { Log } from "../utils/TipUtil";
import { BusinessError } from "../exception/BusinessError";
import EmptyState from "../components/EmptyState.vue";
import { SysNoticeApi } from "../api/sysNotice";
import type { data } from "../types/user";
import type { SysNotice } from "../types/sysNotice";
import { TimeUtil } from "../utils/time";
import { UserRoleEnum } from "@/enums/UserRoleEnum";
const search_User = ref<friend_info[]>([]);
const user = userStore();
const msg = msgStore();
const fold = ref(false);
const selectfriend = ref(true);
const ws = Ws.getInstance();
const search_Group = ref<GroupChatDto[]>();
const loading = ref(false);
const notifications = computed(() => msg.getNotifyList() || []);
onMounted(async () => {
  if (user.userInfo) {
    const userId = user.userInfo.id;
    const res:friend_info[]|undefined  = await queryLinkUser();
    if (res) {  //注册会话信息
      res.forEach(eachFriend  => {
        msg.registry(userId, eachFriend.linkId);
      })
 
    await queryNotify();
    fold.value = localStorage.getItem("f") === "true" ? true : false;
  }
}
});


function handleChat(friend_info: friend_info) {
  if (!user.userInfo) return;
  router.push({
    name: 'chat', params: {
      id: friend_info.linkId
    }
  })
}
function handleChatWithGroup(id: string, name: string) {
  if (!user.userInfo) return;
  router.push({
    name: 'group', params: {
      id: id,
      groupName: name
    }
  })
}
function handleCreateGroup() {
  if (!user.userInfo) return;
  router.push({
    name: 'create-group'
  })
}
async function loginout() {
  await user.logout();
  ws.disconnect();
}

async function queryGroup() {
  if (loading.value) return;
  if (!user.userInfo) return;
  try {
    selectfriend.value = false
    const res = await GroupApi.queryGroupByUserID(user.userInfo.id);
    search_Group.value = res;
    return res;
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
async function queryLinkUser(): Promise<friend_info[] | undefined> {
  if (loading.value) return;
  try {
    selectfriend.value = true
    const res = await FriendApi.getLinkList();
    search_User.value = res;
    return res;
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

async function queryNotify() {
  try {
    const res: SysNotice[] = await SysNoticeApi.list();
    res.forEach(eachNotify => {
      if (!eachNotify.createTime) return;
      eachNotify.createTime = TimeUtil.timestampToTime(eachNotify.createTime);
    })
    msg.addNotifyMsgs(res);
  } catch (error) {
    if (error instanceof BusinessError) {
      Log.error(error.message);
      return;
    } else {
      Log.error("未能获取到历史系统消息")
      console.error(error);
    }
  }

}
onUnmounted(() => {
  selectfriend.value = true;
   localStorage.setItem("f", fold.value.toString());
  sessionStorage.setItem("l", JSON.stringify(search_User.value));
})


</script>


<style scoped>
:deep(.el-scrollbar) {
  height: 89%;
}
</style>
