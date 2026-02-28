<template>
  <div class="cursor"></div>
  <div class="login-page"   v-loading="loading" element-loading-background="rgba(0, 0, 0, 0.7)">
    <div class="bg-layer">
      <div @click="toggleBack" class="toggle-btn" id="toggle">
        <button type="button" class="btn" aria-label="切换主题">
          <el-icon style="border: none; cursor: pointer">
            <Switch />
          </el-icon>
        </button>
      </div>
    </div>

    <!-- 2. 偏白色表单容器（整体居中） -->
    <div class="login-form" ref="loginFormRef">
      <slot></slot>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {nextTick, onMounted, onUnmounted, ref} from "vue";

defineProps<{
  loading?: boolean;
}>();

let isWhite = true;
let isChange = false;
const toggle = ref<HTMLElement | null>(null);
const loginFormRef = ref<HTMLElement | null>(null);

onMounted(async () => {
  toggle.value = document.querySelector("#toggle");
  
  // 使用 nextTick 确保 DOM 已渲染，并通过 ref 操作类名
  await nextTick();
  if (loginFormRef.value) {
    // 稍微延迟以触发 CSS transition
    setTimeout(() => {
      loginFormRef.value?.classList.add('fade-in');
    }, 100);
  }
  
  document.addEventListener("mousemove", handleMouseMove);
});

onUnmounted(() => {
  document.removeEventListener("mousemove", handleMouseMove);
});

function toggleBack() {
  isWhite = !isWhite;
  if (!isWhite) {
    document.documentElement.style.setProperty(
      "--page-image",
      "url(https://img-s.msn.cn/tenant/amp/entityid/BB1msDMO?q=60&m=6&f=jpg&u=t)"
    );
  }
  if (isWhite)
    document.documentElement.style.setProperty(
      "--page-image",
      "url(https://img-s.msn.cn/tenant/amp/entityid/AAOEa6N?q=60&m=6&f=jpg&u=t)"
    );
}

const handleMouseMove = (e: MouseEvent) => {
  if (isChange) return;
  isChange = true;
  requestAnimationFrame(() => {
    const x = (e.clientX / window.innerWidth) * 100;
    const y = (e.clientY / window.innerHeight) * 100;
    document.documentElement.style.setProperty("--cursor-x", `${x}%`);
    document.documentElement.style.setProperty("--cursor-y", `${y}%`);
    isChange = false;
  });
};
</script>

<style>
:root {
  --cursor-x: 50%;
  --cursor-y: 50%;
  /* 定义默认背景图（你的预期默认图片） */
  --page-image: url(https://img-s.msn.cn/tenant/amp/entityid/BB1msDMO?q=60&m=6&f=jpg&u=t);
}
</style>

<style scoped>
/* 全局基础样式：统一字体+清零默认边距 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
    "Helvetica Neue", Arial, "Noto Sans", sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

.cursor {
  position: fixed;
  inset: 0;
  z-index: 1;
  pointer-events: none;
  background: radial-gradient(circle 100px at var(--cursor-x) var(--cursor-y),
      #deeae2 0%,
      transparent 100%);
  transition: opacity 0.3s ease;
}

/* 1. 页面容器：确保表单垂直+水平居中 */
.login-page {
  position: fixed;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  display: flex;
  justify-content: center;
  /* 水平居中 */
  align-items: center;
  /* 垂直居中 */
  /* filter: blur(0.3px);  移除导致文字模糊的滤镜 */
}


.bg-layer {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: background-image 0.8s ease, opacity 0.8s ease;
  opacity: 1;
  /* 默认显示 */
  background-size: cover;
  background-position: center;
  /* 引用CSS变量 */
  background-image: var(--page-image);
  /* 混合模式：让背景图与底色融合，避免过于突兀 */
  background-blend-mode: overlay;
  z-index: -10;
}

/* 2. 切换按钮（右上角） */
.toggle-btn {
  position: absolute;
  top: 20px;
  right: 20px;
  z-index: 100;
  cursor: pointer;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.toggle-btn:hover {
  background: rgba(255, 255, 255, 0.4);
  transform: scale(1.1);
}

.btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #fff;
  cursor: pointer;
}

/* 3. 表单容器：偏白色+整体居中+柔和阴影 */
.login-form {
  position: relative;
  z-index: 20;
  width: 90%;
  /* 移动端默认宽度 */
  max-width: 360px;
  /* PC端最大宽度 */
  padding: 45px 35px;
  background-color: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(10px);
  border-radius: 14px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.08);
  text-align: center;
  /* 初始状态：透明且稍微下沉，配合动画 */
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.8s ease, transform 0.8s ease;
}

/* 进场动画类 */
.login-form.fade-in {
  opacity: 1;
  transform: translateY(0);
}

.login-form::after {
  position: absolute;
  z-index: -1;
  inset: -1px;
  filter: blur(5px);
  border-radius: inherit;
  animation: login-form-border 5s infinite ease-in-out alternate;
  content: "";
}

@keyframes login-form-border {
  0% {
    box-shadow: 0 0 3px red;
  }

  50% {
    box-shadow: 0 0 4px yellow;
  }

  100% {
    box-shadow: 0 0 5px greenyellow;
  }
}

/* 移动端适配 */
@media (max-width: 480px) {
  .login-form {
    padding: 30px 20px;
    /* 减小内边距 */
  }
}
</style>
