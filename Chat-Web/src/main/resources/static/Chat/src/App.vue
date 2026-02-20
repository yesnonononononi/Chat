<script setup lang="ts">
import MediaTip from './components/MediaTip.vue';
import MessageTip from './components/MessageTip.vue';
 
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
