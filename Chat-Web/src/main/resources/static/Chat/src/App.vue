<script setup lang="ts">
import {onMounted, onUnmounted, watch} from 'vue';
import MediaTip from './components/MediaTip.vue';
import MessageTip from './components/MessageTip.vue';
import {mediaStore} from './store/MediaStore';
import {userStore} from './store/UserStore';

const media = mediaStore();
const user =userStore();
const initGlobalListen = () => { 
  if(user){
    media.initMediaListener();
  }
};

watch(()=>user.isLogin,(newVal:any)=>{
  if(newVal){
    setTimeout(()=>{
     media.initMediaListener();
    },500)
  }else{
    media.removeMediaListener();
  }
})

onMounted(() => {
  initGlobalListen();
});
onUnmounted(() => {
  media.removeMediaListener();
});
</script>

<template>
  <div class="app">
    <router-link to="/"></router-link>
   <router-link to="/login"></router-link>
  </div>
  <router-view :key="$route.fullPath"></router-view>
  <MediaTip />
  <MessageTip />
</template>



<style>
*{
  margin:0;
  padding:0;
  box-sizing: border-box;
}
 
</style>
