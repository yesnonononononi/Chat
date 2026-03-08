<template>
  <div class="user-detail-page" v-loading="loading" element-loading-background="rgba(220, 220, 220, 0.5)">
    <!-- 背景层 -->
    <div class="bg-layer" :style="{ backgroundImage: `url(${bgImage})` }"></div>

    <!-- 核心内容容器 -->
    <div class="user-container" v-if="tempUserInfo">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">用户详情</h1>
        <el-button link type="success" @click="router.back()">
          <el-icon class="mr-1">
            <Back />
          </el-icon> 返回列表
        </el-button>
      </div>

      <!-- 头部卡片：头像与基础信息 -->
      <div class="user-header-card">
        <div class="avatar-wrapper">
          <el-avatar :size="100" :src="tempUserInfo.icon" class="user-avatar" shape="circle" />
          <!-- 编辑模式下的头像上传按钮 -->
          <div class="avatar-edit-mask" v-if="isEditing" @click="triggerFileSelect">
            <el-icon>
              <Camera />
            </el-icon>
            <input type="file" ref="avatarInputRef" class="hidden-input" accept="image/png,image/jpeg,image/jpg"
              @change="handleAvatarUpload" />
          </div>
        </div>

        <div class="user-basic-info">
          <!-- 昵称 -->
          <div class="nickname-section">
            <h2 v-if="!isEditing" class="user-nickname">
              {{ tempUserInfo.nickName || "未设置昵称" }}
            </h2>
            <el-input v-else v-model="tempUserInfo.nickName" placeholder="请输入昵称" class="nickname-input" maxlength="20"
              show-word-limit />
          </div>

          <!-- 签名/爱好 -->
          <div class="signature-section">
            <p v-if="!isEditing" class="user-signature">
              {{ tempUserInfo.hobby || "这个人很懒，什么都没留下~" }}
            </p>
            <el-input v-else v-model="tempUserInfo.hobby" placeholder="请输入个性签名/爱好" class="signature-input" />
          </div>

          <!-- 标签组 -->
          <div class="user-tags">
            <el-tag type="info" effect="plain" round size="small">注册于 {{ formatTime(tempUserInfo.createTime) }}</el-tag>
            <el-tag type="info" effect="plain" round size="small">ip: {{ tempUserInfo.ip || '未知' }}</el-tag>
          </div>
        </div>
      </div>

      <!-- 账号详细信息 -->
      <div class="account-info-card">
        <div class="card-header">
          <span class="card-title">账号信息</span>
          <el-tag v-if="tempUserInfo.status === 1" type="success" size="small" effect="plain">状态正常</el-tag>
          <el-tag v-else type="danger" size="small" effect="plain">账户异常</el-tag>
        </div>

        <el-form :model="tempUserInfo" label-width="70px" label-position="left" class="info-form">
          <el-row :gutter="20">
            <el-col :xs="24" :sm="12">
              <el-form-item label="手机号">
                <span class="info-text">{{ tempUserInfo.mobile || "未绑定" }}</span>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="邮箱">
                <span v-if="!isEditing" class="info-text">{{ tempUserInfo.email || "未绑定" }}</span>
                <el-input v-else v-model="tempUserInfo.email" placeholder="请输入邮箱" />
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="性别">
                <span v-if="!isEditing" class="info-text">
                  {{ tempUserInfo.gender === 1 ? "男" : tempUserInfo.gender === 2 ? "女" : "未设置" }}
                </span>
                <el-select v-else v-model="tempUserInfo.gender" placeholder="请选择性别">
                  <el-option label="男" :value="1" />
                  <el-option label="女" :value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :xs="24" :sm="12">
              <el-form-item label="年龄">
                <span v-if="!isEditing" class="info-text">{{ tempUserInfo.age || "未设置" }}</span>
                <el-input-number v-else v-model="tempUserInfo.age" :min="0" :max="120" controls-position="right"
                  class="w-full" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <!-- 底部操作栏 -->
      <div class="action-bar">
        <template v-if="!isEditing">
          <el-button type="primary" plain :icon="Edit" @click="enterEditMode">编辑资料</el-button>
          <el-button type="success" plain :icon="Key" @click="handleChangePwd">修改密码</el-button>
          <el-popconfirm title="确定要注销当前账号吗？此操作不可恢复" @confirm="handleDelete" confirm-button-text="确认注销"
            cancel-button-text="取消" confirm-button-type="danger">
            <template #reference>
              <el-button type="danger" plain :icon="Delete">注销账号</el-button>
            </template>
          </el-popconfirm>
        </template>
        <template v-else>
          <el-button @click="cancelEdit">取消</el-button>
          <el-button type="primary" :loading="loading" @click="saveProfile">保存修改</el-button>
        </template>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import {BusinessError} from "../exception/BusinessError";
import {onMounted, ref} from "vue";
import {userStore} from "../store/UserStore";
import {format} from "date-fns";
import {getUser} from "../utils/UserInfo";
import {Log} from "../utils/TipUtil";
import router from "../router/index";
import {UserApi} from "../api/user";
import type {userInfo} from "../types/user";
import {cloneDeep} from "lodash";
import {Delete, Edit, Key} from "@element-plus/icons-vue";
// 状态管理
const store = userStore();
const isEditing = ref(false);

const tempUserInfo = ref<userInfo | null>(null);
const avatarInputRef = ref<HTMLInputElement | null>(null);
const bgImage = ref("");
const defaultAvatar = "https://picsum.photos/id/64/200/200";
const loading = ref(false);
// 初始化
onMounted(() => {
  if (!store.isLogin || !store.userInfo) {
    Log.error("未获取到用户信息");
    router.push("/login");
    return;
  }
  tempUserInfo.value = cloneDeep(store.userInfo);
});

// 工具函数
const formatTime = (time: string | number | Date | undefined) => {
  if (!time) return "未知";
  return format(new Date(time), "yyyy-MM-dd");
};

// 操作逻辑
const enterEditMode = () => {
  isEditing.value = true;
};

const cancelEdit = () => {
  isEditing.value = false;
  if (store.userInfo) {
    tempUserInfo.value = cloneDeep(store.userInfo);
  }
};

const saveProfile = async () => {
  if (!tempUserInfo.value) return;
  if (loading.value) return;
  loading.value = true;
  try {

    // 简单的校验
    if (!tempUserInfo.value.id) {
      const uid = getUser();
      if (uid) tempUserInfo.value.id = uid;
      else throw new Error("用户ID丢失");
    }

    await UserApi.putUserInfoByInfo(tempUserInfo.value);
    Log.ok("保存成功");

    // 更新本地状态
    store.userInfo = cloneDeep(tempUserInfo.value);
    isEditing.value = false;
  } catch (error: any) {
    if (error instanceof BusinessError) {
      Log.error(error.message);
    } else {
      Log.error("服务繁忙");
      console.error(error);
    }
  } finally {
    loading.value = false;
  }
};

const handleChangePwd = () => {
  router.push({
    name:"reset-pw"
  })
};

const handleDelete = async () => {
  
  const userID = store.userInfo?.id;
  if (!userID) { console.log(userID);return; }
  try {
    await UserApi.deleteUser(userID);
    Log.ok("用户已成功注销");
    store.logout();
  } catch (error: any) {
    if (error instanceof BusinessError) {
      Log.error(error.message);
    } else {
      Log.error("服务繁忙");
      console.error(error);
    }
  }
};

// 头像上传逻辑
const triggerFileSelect = () => {
  avatarInputRef.value?.click();
};

const handleAvatarUpload = async (e: Event) => {
  const target = e.target as HTMLInputElement;
  const file = target.files?.[0];
  if (loading.value) return;


  if (!file) return;

  if (!file.type.startsWith("image/")) {
    Log.error("请选择图片文件");
    return;
  }

  if (file.size > 4 * 1024 * 1024) {
    Log.error("图片大小不能超过4MB");
    return;
  }

  const formData = new FormData();
  formData.append("file", file);

  try {
    loading.value = true;
    const url = await UserApi.uploadAvatar(formData);
    Log.ok("头像上传成功");
    loading.value = false;
    if (tempUserInfo.value) {
      tempUserInfo.value.icon = url;
    }

  } catch (error: any) {
    if (error instanceof BusinessError) {
      Log.error(error.message);
    } else {
      Log.error("服务繁忙");
      console.error(error);
    }
  } finally {
    if (avatarInputRef.value) avatarInputRef.value.value = "";
    loading.value = false;
  }
};
</script>

<style scoped>
.user-detail-page {
  position: relative;
  min-height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  padding: 40px 20px;
  background-color: #f3f4f6;
  overflow-y: auto;
}

.bg-layer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  z-index: 0;
  backdrop-filter: blur(20px);
}

.user-container {
  position: relative;
  z-index: 1;
  width: 100%;
  max-width: 700px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
  padding: 30px;
  display: flex;
  flex-direction: column;
  gap: 24px;
  border: 1px solid rgba(34, 197, 94, 0.1);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.user-header-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(to bottom, #ffffff, #f9fafb);
  border-radius: 12px;
  border: 1px solid #e5e7eb;
}

.avatar-wrapper {
  position: relative;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  border: 4px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  width: 100%;
  height: 100%;
}

.avatar-edit-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}

.avatar-wrapper:hover .avatar-edit-mask {
  opacity: 1;
}

.hidden-input {
  display: none;
}

.user-basic-info {
  width: 100%;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: center;
}

.nickname-section {
  width: 100%;
  display: flex;
  justify-content: center;
}

.user-nickname {
  font-size: 22px;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.nickname-input {
  width: 200px;
}

.signature-section {
  width: 100%;
  display: flex;
  justify-content: center;
}

.user-signature {
  color: #6b7280;
  font-size: 14px;
  margin: 0;
}

.signature-input {
  width: 300px;
}

.user-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
}

/* Account Info Card */
.account-info-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #e5e7eb;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.info-text {
  color: #111827;
  font-weight: 500;
}

.info-form :deep(.el-form-item__label) {
  color: #6b7280;
}

/* Action Bar */
.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}

/* Mobile Adaptation */
@media (max-width: 640px) {
  .user-detail-page {
    padding: 0;
  }

  .user-container {
    border-radius: 0;
    min-height: 100vh;
    border: none;
  }

  .signature-input,
  .nickname-input {
    width: 100%;
  }

  .action-bar {
    flex-direction: column-reverse;
  }

  .action-bar .el-button {
    width: 100%;
    margin-left: 0 !important;
    margin-bottom: 8px;
  }
}
</style>
