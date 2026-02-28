<template>
    <div class="w-full h-[100vh] text-center">
        <div class="flex justify-around items-center border-b-2 border-gray-300 w-full ">
            <span @click.prevent="router.back()" class="cursor-pointer"><el-icon size="24"><Back/></el-icon></span>
            <span class="m-auto">发布群公告</span>
            <span class="text-xs md:text-xl hover:text-indigo-400 cursor-pointer" @click.prevent="putGroup">发布</span>
        </div>
        <div class="w-auto h-auto p-2">
        <el-input type="textarea" maxlength="200" minlength="1" class="mt-4"  input-style="background-color: #FFFFFF; height:400px; "  show-word-limit resize = "none" v-model="content" /> 
        </div>
    </div>
</template>

<script lang="ts" setup>
import {Log} from "../utils/TipUtil";
import {GroupApi} from "../api/group";
import {ref} from "vue";
import {useRoute} from "vue-router";
import router from "../router";
import {BusinessError} from "../exception/BusinessError";

const content = ref("");
const route = useRoute();
function putGroup() {
    const groupId = route.params.id as string;
    if(!groupId){
        Log.error("未获取到群聊信息");
        return;
    }
    GroupApi.publishGroupNotice({
        id:"",
        groupId: groupId,
        content: content.value.trim()
    }).then(res => {
        Log.ok("发布成功");
        router.back();
    }).catch(err => {
        if(err instanceof BusinessError)Log.error(  err.message  );
        else console.error(err);
    });
}
</script>