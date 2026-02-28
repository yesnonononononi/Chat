<template>
    <div class="w-screen h-screen overflow-hidden box-border" :class="white_page ? 'theme-light' :'theme-dark'" >
        <div class="flex items-center w-full h-full" >
            <div id="left" class="flex flex-col justify-around whitespace-nowrap   transition-all duration-300  h-full " :class="!isFold ? 'w-32 translate-x-0 mx-8 opacity-100' : '-translate-x-full w-0 mx-0 opacity-0'" >
                <span class="text-2xl md:text-3xl text-green-500 font-bold font-glow">chat</span>
                <span class="text-sm md:text-lg  glow-white menu-item " @click="router.push({name:'total-data'})">数据概览</span>
                <span class="text-sm md:text-lg glow-white menu-item " @click="router.push({name:'user-admin'})">用户管理</span>
                <span class="text-sm md:text-lg  glow-white menu-item" @click="router.push({name:'group-admin'})">群聊管理</span>
                <span class="text-sm md:text-lg glow-white menu-item" @click="router.push({name:'system-admin'})">系统管理</span>
                <span class="text-sm md:text-lg glow-white menu-item" @click="router.push({name:'emoji-admin'})">表情管理</span>
                <span class="text-sm md:text-lg glow-white menu-item" @click="router.push({name:'emoji-category-admin'})">表情分类管理</span>
            </div>
            <div id="right" class="flex-1 h-full flex flex-col min-w-0">
                <div class="flex items-center justify-between">
                    <div class="flex items-center">
                        <el-icon color="white" size="16" class="cursor-pointer" @click="isFold = !isFold">
                            <Fold />
                        </el-icon>
                        <span class="font-glow ml-4  text-xs md:text-lg ">
                            <span class="cursor-pointer text-xs md:text-lg" @click="router.push('/')">首页/</span>
                            <span class=" text-xs md:text-lg">管理页/</span>
                            {{ curPage }}
                        </span>
                    </div>
                    <div class="flex items-center">
                        <el-icon v-if="white_page" class="mr-4 cursor-pointer" size="24" color="white" @click="toggleTheme">
                            <Moon /> 
                        </el-icon>  
                        <el-icon v-if="!white_page" class="mr-4 cursor-pointer" size="24" color="yellow" @click="toggleTheme">
                            <Sunny /> 
                        </el-icon>
                        <el-icon color="white" size="16" class="mr-4 cursor-pointer">
                            <Bell />
                        </el-icon>
                        <img :src="user.userInfo?.icon" alt="" class="w-10 h-10 rounded-full mr-4">
                        <span class="font-glow   mr-4">管理员</span>
                    </div>
                </div>
                <el-scrollbar class="flex-1" height="100%" no-resize >
                     <router-view :key="$route.fullPath"></router-view>
                </el-scrollbar>
            </div>
        </div>
    </div>
</template>
<script lang="ts" setup>
import router from '../router';
import {userStore} from '../store/UserStore';
import {ref} from 'vue';

const user = userStore();
const curPage = ref<string>();
const isFold = ref<boolean>(false);

// 主题状态管理
const white_page = ref<boolean>(localStorage.getItem('atl') === 'true');

const toggleTheme = () => {
    white_page.value = !white_page.value;
    localStorage.setItem('atl', String(white_page.value));
};
</script>
