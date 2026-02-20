<template>

    <div class="w-auto  flex-1 h-[100vh] m-4 border shadow " v-loading="loading"
        element-loading-background="rgba(220, 220, 220, 0.5)">
        <el-icon class="text-4xl text-green-400 m-2 cursor-pointer" size="24" @click.prevent="router.push('/')">
            <Back />
        </el-icon>

        <el-form class="m-4">
            <el-form-item label="群聊头像">
                <el-upload
                    class="relative overflow-hidden hover:border-gray-400 group w-32 h-32 md:w-48 md:h-48 border cursor-pointer flex flex-col items-center justify-center"
                    action="" :show-file-list="false" :on-success="handleAvatarSuccess"
                    :before-upload="beforeAvatarUpload">
                    <img v-if="form.icon" :src="form.icon" class="w-48 h-48 block" />
                    <el-icon v-else class="text-center group-hover:text-blue-300" size="48">
                        <Plus />
                    </el-icon>
                </el-upload>
            </el-form-item>
            <el-form-item label="群组名称">
                <el-input v-model="form.groupName" class="relative z-10 max-w-32 md:max-w-64" type="text"
                    show-word-limit maxlength="20" />
            </el-form-item>
            <el-form-item label="群组描述">
                <el-input v-model="form.groupDescription" resize="none" maxlength="50" type="textarea"
                    class="border h-full" show-word-limit />
            </el-form-item>
            <el-button type="primary" @click.prevent="submit">创建</el-button>
        </el-form>
    </div>

</template>
<script setup lang="ts">
import { Log } from "../utils/TipUtil";
import { type GroupChatDto } from "../types/group";
import { onMounted, ref } from "vue";
import { userStore } from "../store/UserStore";
import router from "../router";
import { GroupApi } from "../api/group";
import { uploadFile } from "../api/common";
import { BusinessError } from "../exception/BusinessError";
const avatar = ref();
const user = userStore();
const loading = ref(false);
const form = ref<GroupChatDto>({
    id: "",
    groupName: "",
    groupDescription: "",
    creatorId: "",
    icon: "",

})
onMounted(() => {
  

   
        const userId = user.userInfo?.id;
        form.value.creatorId = userId;
  
}
);
function handleAvatarSuccess(res: any, file: any) {
    form.value.icon = res.url;
}
function beforeAvatarUpload(file: any) {
    const isJPG = file.type === "image/jpeg";
    const isLt2M = file.size / 1024 / 1024 < 2;
    if (!isJPG) {
        Log.error("上传头像图片只能是 JPG 格式!");
    }
    if (!isLt2M) {
        Log.error("上传头像图片大小不能超过 2MB!");
    }
    avatar.value = file;
    form.value.icon = URL.createObjectURL(file);
}

const newLocal = "https://summit-oss.oss-cn-beijing.aliyuncs.com/icon/%E7%94%9F%E6%88%90%E5%A5%BD%E5%8F%8B%E5%9B%BE%E6%A0%87%20%281%29.png";
async function submit() {
    if (loading.value) return;
    try {
        loading.value = true;

        if (!avatar.value) form.value.icon = newLocal;
        
            const res = await uploadFile(avatar.value).catch((err) => {
                if (err instanceof BusinessError) {
                    Log.error(err.message);
                } else {
                    Log.error("服务繁忙");
                    console.error("头像上传失败", err);
                }
              
            }); 
            if(res)form.value.icon = res.data as string;
            else form.value.icon = newLocal;

        try {
            await GroupApi.addGroup(form.value);
            loading.value = false;
            Log.ok("创建成功");
            router.push("/");
        } catch (err) {
            if (err instanceof BusinessError) {
                Log.error(err.message);
            } else {
                Log.error("服务繁忙");
                console.error(err);
            }
        }
    } finally {
        loading.value = false;
    }

}
</script>