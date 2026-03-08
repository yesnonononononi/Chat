<template>
    <div class="w-full h-[100vh] flex flex-col items-center justify-center ">
        <el-input type="text" v-model="input" style="width: 200px;" class="w-[200px] mb-4">
            <template #suffix>
                <el-button class="text-gray-400 hover:text-indigo-400 " style="border:none" @click="get">发送</el-button>
            </template>
        </el-input>

            <textarea class="min-w-[400px] min-h-[300px] shadow-md" v-model="text"></textarea>
    </div>

</template>

<script lang="ts" setup>
import {AiApi} from '@/api/Ai';
import {BusinessError} from '@/exception/BusinessError';
import {AiWs} from '@/utils/Socket/AiWs';
import {Ws} from '@/utils/Socket/webSocket';
import {Log} from '@/utils/TipUtil';
import {ref} from 'vue';

const text = ref<string>("");
const input = ref("");
async function get() {
    try {
        const res = await AiApi.chat({
            message: input.value,
            modelName: 'qwen-plus'
        })
        const ws = new AiWs(Ws.getInstance());
        ws.listenStream(data => {
            if (data !== "RESPONSE" && data !== "ERROR" && data !== "END") {
                text.value += data;
            } 
            if (data === "END") {
                ws.endListenStream();
            } else if (data === "ERROR") {
                ws.endListenStream();
                console.error("请求错误")
            }
            else {
                console.log("开始")
            }
        })
    } catch (e) {
        if(e instanceof BusinessError){
            Log.error(e.message);
            return;
        }
        console.error(e)
    }

} 
</script>