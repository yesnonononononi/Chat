<template>
    <div class="mt-4 w-full" v-loading="loading" element-loading-background="rgba(220, 220, 220, 0.5)">
        <span class="text-xl md:text-2xl font-bold">用户管理</span>
        <div class="w-full h-auto mt-4 flex md:flex-row flex-col items-center gap-2 md:gap-8 p-4 ">
            <div class="">
                <el-input v-model="queryParams.nickName" input-style="width:300px " placeholder="请输入用户昵称"></el-input>
            </div>

            <el-select v-model="queryParams.role" name="type" style="width:240px" class="bg-black " placeholder="角色">
                <el-option label="全部" value=""></el-option>
                <el-option label="管理员" value="admin"></el-option>
                <el-option label="普通用户" value="user"></el-option>
            </el-select>
            <el-select v-model="queryParams.status" style="width:240px" class="bg-neutral-950" placeholder="状态">
                <el-option label="全部" value=""></el-option>
                <el-option label="正常" :value="1"></el-option>
                <el-option label="禁用" :value="0"></el-option>
            </el-select>
            <div>
                <el-button type="primary" @click="handleSearch">搜索</el-button>
                <el-button type="primary" @click="handleReset">重置</el-button>
            </div>

        </div>
        <div class="w-full md:w-full">
            <el-table :data="userInfo" class="rounded-xl">
                <el-table-column prop="id" align="center" label="ID"> </el-table-column>
                <el-table-column prop="nickName" align="center" label="昵称"> </el-table-column>
                <el-table-column prop="gender" align="center" label="性别">
                    <template #default="scope">
                        <span :class="scope.row.gender == 1 ? 'text-blue-400' : 'text-pink-400'">{{
                            getGenderText(scope.row.gender) }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="role" align="center" label="角色">
                    <template #default="scope">
                        <span v-show = "scope.row.role === UserRoleEnum.USER" class="text-green-400">普通用户</span>
                        <span v-show = "scope.row.role === UserRoleEnum.ADMIN" class="text-blue-400">管理员</span>
                        <span v-show = "scope.row.role === UserRoleEnum.SUPER_ADMIN" class="text-yellow-400">超管</span>
                    </template>
                </el-table-column>
                <el-table-column prop="status" align="center" label="状态">
                    <template #default="scope">
                        <span :class="scope.row.status == 1 ? 'text-green-400' : 'text-red-400'">{{
                            getStatusText(scope.row.status) }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="200px">
                    <template #default="scope">
                        <span class="text-red-400 cursor-pointer hover:text-red-700 mr-4"
                            @click="handleBlack(scope.row)" v-if="scope.row.status === 1 &&( scope.row.role === UserRoleEnum.USER || (scope.row.role === UserRoleEnum.ADMIN && user.userInfo?.role === UserRoleEnum.SUPER_ADMIN) && scope.row.role !== UserRoleEnum.SUPER_ADMIN)">禁用</span><!-- 禁用对象不为超管 && ((对象为管理员 && 自己为超管) || 对象为用户 )-->
                        <span class="text-green-400 cursor-pointer hover:text-green-700 mr-4"
                            @click="handleUnblack(scope.row)" v-if="scope.row.status === 0 &&( scope.row.role === UserRoleEnum.USER || (scope.row.role === UserRoleEnum.ADMIN && user.userInfo?.role === UserRoleEnum.SUPER_ADMIN) && scope.row.role !== UserRoleEnum.SUPER_ADMIN)">启用</span>
                        <span class="text-blue-400 cursor-pointer hover:text-blue-700"
                            @click="handleSetAdmin(scope.row)" v-if=" (user.userInfo?.role === UserRoleEnum.SUPER_ADMIN && scope.row.id !== user.userInfo?.id)&&scope.row.role === UserRoleEnum.USER">设为管理员</span>
                    </template><!--自己是超管&&设置对象不能为自己&&设置对象为user -->
                </el-table-column>
            </el-table>
            <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="queryParams.page"
                :page-sizes="[10, 20, 30, 40]"
                :page-size="queryParams.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="queryParams.total"
                class="mt-2"
                >
            </el-pagination>
        </div>
    </div>
</template>
<script lang="ts" setup>
import { userStore } from '@/store/UserStore';
import { AdminApi } from '../api/admin';
import { BusinessError } from '../exception/BusinessError';
import type { userInfo } from '../types/user';
import { Log } from '../utils/TipUtil';
import { computed, onMounted, reactive, ref } from 'vue';
import { UserRoleEnum } from '@/enums/UserRoleEnum';
const loading = ref(false);
const userInfo = ref<userInfo[]>([]);
const user = userStore();
const queryParams = reactive({
    page: 1,
    pageSize: 10,
    total: 0,
    nickName: '',
    role: '',
    status: '',
    hasMore: true
});

onMounted(async () => {
    await queryUserAll();
});

const queryUserAll = async () => {
    try {
        if (loading.value) return;
        loading.value = true;
        const res = await AdminApi.getUsers(queryParams);
        userInfo.value = res.records;
        queryParams.total = parseInt(res.total);
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
            return;
        }
        console.log(err);
        Log.error("获取用户失败");
    } finally {
        loading.value = false;
    }
};

function handleSizeChange(val: number) {
    queryParams.pageSize = val;
    queryUserAll();
}

function handleCurrentChange(val: number) {
    queryParams.page = val;
    queryUserAll();
}

function handleSearch() {
    queryParams.page = 1; // 搜索时重置到第一页
    queryUserAll();
}

function handleReset() {
    queryParams.page = 1;
    queryParams.pageSize = 10;
    queryParams.nickName = '';
    queryParams.role = '';
    queryParams.status = '';
    queryParams.hasMore = true;
    queryUserAll();
}

function getGenderText(gender: number) {
    return gender === 1 ? '男' : gender === 2 ? '女' : '未知';
}

function getStatusText(status: number) {
    return status === 1 ? '正常' : '禁用';
}

const handleBlack = async (user: userInfo) => {
    try {
        if (loading.value) return;
        loading.value = true;
        await AdminApi.blackUser(user.id);
        await queryUserAll();
        Log.ok('禁用成功');
        user.status = 0;
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message)
        } else {
            console.error(err); Log.error("操作失败")
        }
    } finally {
        loading.value = false;
    }

};

const handleUnblack = async (user: userInfo) => {
    try {
        if (loading.value) return;
        loading.value = true;
        await AdminApi.unblackUser(user.id);
        await queryUserAll();
        Log.ok("操作成功");
        user.status = 1;
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message)
        } else {
            console.error(err); Log.error("操作失败")
        }
    } finally {
        loading.value = false;
    }

};

const handleSetAdmin = async (user: userInfo) => {
    try {
        if (loading.value) return;
        loading.value = true;
        await AdminApi.setAdmin(user.id);
        await queryUserAll();
        Log.ok("设置成功");
        user.role = 'admin';
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message)
        } else {
            console.error(err); Log.error("操作失败")
        }
    } finally {
        loading.value = false;
    }
};


</script>
