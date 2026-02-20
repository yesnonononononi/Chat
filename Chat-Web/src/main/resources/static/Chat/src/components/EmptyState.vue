<template>
  <div class="w-full h-full flex flex-col items-center justify-center text-gray-400 select-none">
    <div class="mb-6 opacity-20">
      <svg width="200" height="200" viewBox="0 0 200 200" fill="none" xmlns="http://www.w3.org/2000/svg">
        <circle cx="100" cy="100" r="80" stroke="currentColor" stroke-width="8" />
        <path d="M70 85C70 85 85 75 100 75C115 75 130 85 130 85" stroke="currentColor" stroke-width="8" stroke-linecap="round" />
        <circle cx="75" cy="110" r="10" fill="currentColor" />
        <circle cx="125" cy="110" r="10" fill="currentColor" />
        <path d="M80 145C80 145 90 155 100 155C110 155 120 145 120 145" stroke="currentColor" stroke-width="8" stroke-linecap="round" />
      </svg>
    </div>
    <h2 class="text-2xl font-bold mb-2 tracking-widest text-green-400/60">CHAT SYSTEM</h2>
    <p class="text-sm">选择左侧好友或群聊开始交流</p>
    <div class="mt-12 flex gap-4 text-xs opacity-50">
      <div class="flex items-center gap-1">
        <div class="w-2 h-2 rounded-full bg-green-400"></div>
        <span>随时沟通</span>
      </div>
      <div class="flex items-center gap-1">
        <div class="w-2 h-2 rounded-full bg-blue-400"></div>
        <span>安全加密</span>
      </div>
      <div class="flex items-center gap-1">
        <div class="w-2 h-2 rounded-full bg-indigo-400"></div>
        <span>视频通话</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
</style>
