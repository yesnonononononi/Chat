<template>
    <div class="w-full h-full" v-loading="loading" element-loading-background="rgba(220, 220, 220, 0.5)">
        <div class="w-full h-full" >
            <span class="mt-4 font-bold text-2xl ">群聊管理</span>
            <div
                class="flex items-center justify-between border border-gray-300  rounded-md shadow-md p-4  bg-white mt-8">
                <div>
                    <el-input placeholder="请输入名称" style="width:200px" class="mr-4">
                        <template #suffix>
                            <span class="hover:text-indigo-400 cursor-pointer">搜索</span>
                        </template>
                    </el-input>
                    <el-select placeholder="状态" style="width:100px" @change="search">
                        <el-option label="活跃" value="2"></el-option>
                        <el-option label="禁用" value="3"></el-option>
                    </el-select>
                </div>

            </div>
        </div>
        <div class="border-gray-300 rounded-md bg-white shadow-md p-4 border  mt-8 min-h-96">
            <el-table :data="tableData">
                <el-table-column label="ID" align="center" prop="id"></el-table-column>
                <el-table-column label="名称" align="center" prop="groupName"></el-table-column>
                <el-table-column label="描述" align="center" prop="groupDescription"></el-table-column>
                <el-table-column label="创建人" align="center" prop="creatorName"></el-table-column>
                <el-table-column label="创建时间" align="center" prop="createTime"></el-table-column>
                <el-table-column label="人数" align="center" prop="number"></el-table-column>
                <el-table-column label="操作" align="center" width="200">
                    <template #default="scope">
                        <el-button type="danger" v-if="scope.row.status == 1"
                            @click="handleBan(scope.row)">封禁</el-button>
                        <el-button type="success" v-else @click="handleUnBan(scope.row)">解封</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="state.page"
                :page-sizes="[10, 20, 30, 40]"
                :page-size="state.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="state.total"
                class="mt-2"
                >
            </el-pagination>
        </div>
    </div>

</template>

<script lang="ts" setup>
import {AdminApi} from '../api/admin';
import {GroupStatus} from '../enums/GroupStatus';
import {BusinessError} from '../exception/BusinessError';
import type {Group} from '../types/group';
import {TimeUtil} from '../utils/time';
import {Log} from '../utils/TipUtil';
import {onMounted, ref} from 'vue';

const state = ref({
    page: 1,
    pageSize: 10,
    total: 0,
});
const loading = ref(false);
onMounted(() => {
    queryGroupAll();
});

const tableData = ref<Group[]>();
function search() {
    Log.info("搜索")
}

async function handleBan(group: Group) {
    try {

        if (loading.value) return;
        loading.value = true;
        await AdminApi.banGroup(group.id);
        Log.ok("操作成功");
        group.status = GroupStatus.FORBIDDEN;
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message)
        } else {
            console.error(err); Log.error("操作失败")
        }
    } finally {
        loading.value = false;
    }
}

async function handleUnBan(group: Group) {
    try {
        if (loading.value) return;
        loading.value = true;
        await AdminApi.unBanGroup(group.id);
        Log.ok("操作成功");
        group.status = GroupStatus.NORMAL;
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message)
        } else {
            console.error(err); Log.error("操作失败")
        }
    } finally {
        loading.value = false;
    }

}
async function queryGroupAll() {
    try {
        loading.value = true;
        const res = await AdminApi.queryGroupList(state.value.page, state.value.pageSize);
        const list: Group[] = res.records;
        list.forEach(item => {
            item.createTime = TimeUtil.timestampToTime(item.createTime);
        });
        tableData.value = res.records;
        state.value.total = parseInt(res.total);
    }
    catch (e) {
        if (e instanceof BusinessError) {
            Log.error(e.message)
        }
        else {
            Log.error("获取群聊列表失败")
            console.error(e)
        }
    }finally {
        loading.value = false;
    }
}

function handleSizeChange(val: number) {
    state.value.pageSize = val;
    queryGroupAll();
}

function handleCurrentChange(val: number) {
    state.value.page = val;
    queryGroupAll();
}

</script>

<style scoped></style>