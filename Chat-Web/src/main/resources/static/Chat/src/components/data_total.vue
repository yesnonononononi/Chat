<template>
    <div class="w-full  h-full box-border">
        <div class="flex justify-between items-center w-full p-2 mt-8">
            <span class="text-xl font-bold ">数据概览</span>
            <span class="mr-4 glow-white" @click="getAllData">刷新数据</span>
        </div>
        <div class="flex flex-col md:flex-row  justify-around items-center mt-8 gap-8">
            <div class="bg-indigo-500 w-48 h-24 md:w-96 md:h-36 rounded-xl flex items-center">
                <el-icon size="36" class="ml-4">
                    <User />
                </el-icon>
                <div class="ml-10">
                    <p class="text-white font-bold">{{ total.totalUser || 0 }}</p>
                    <p class="text-white font-bold mt-4 text-sm md:text-lg">用户总数</p>
                    <p class="text-white font-bold mt-4 text-sm md:text-lg">
                        较昨日
                        <span :class="total.todayNewUser > 0 ? 'text-green-400' : 'text-red-400'">
                            {{ total.todayNewUser }}
                        </span>
                    </p>
                </div>
            </div>
            <div class="bg-pink-300 w-48 h-24 md:w-96 md:h-36 rounded-xl flex items-center">
                <el-icon size="36" class="ml-4">
                    <CopyDocument />
                </el-icon>
                <div class="ml-10">
                    <p class="text-white font-bold">{{ total.totalMsg || 0 }}</p>
                    <p class="text-white font-bold mt-4 text-sm md:text-lg">消息总数</p>
                    <p class="text-white font-bold mt-4 text-sm md:text-lg">较昨日
                        <span :class="total.todayNewMsg > 0 ? 'text-green-400' : 'text-red-600'">
                            {{ total.todayNewMsg }}
                        </span>
                    </p>
                </div>
            </div>
            <div class="bg-green-500 w-48 h-24 md:w-96 md:h-36 rounded-xl flex items-center">
                <el-icon size="36" class="ml-4">
                    <Position />
                </el-icon>
                <div class="ml-10">
                    <p class="text-white font-bold">{{ total.onlineUser || 0 }}</p>
                    <p class="text-white font-bold mt-4 text-sm md:text-lg">在线用户数</p>

                </div>
            </div>

        </div>
        <div class="flex justify-between w-full mt-4">
            <span class="font-bold ml-2">趋势折线图</span>
            <div class="flex border border-gray-400 mr-8">
                <button class="mr-4 cursor-pointer " :class="dataOfDay == 7 ? 'bg-blue-400' : ''"
                    @click="dataOfDay = 7">最近7日</button>
                <button class="mr-4 cursor-pointer" :class="dataOfDay == 30 ? 'bg-blue-400' : ''"
                    @click="dataOfDay = 30">最近30日</button>
                <button class="cursor-pointer" :class="dataOfDay == 90 ? 'bg-blue-400' : ''"
                    @click="dataOfDay = 90">最近90日</button>
            </div>
        </div>
        <div class="flex flex-col md:flex-row justify-center  items-center  w-[400px] md:w-full h-[500px] mt-8">
            <div class="flex w-full md:w-1/2 h-full mt-4">
                <EchartsChart :option="total_user"></EchartsChart>
            </div>
            <div class=" mt-8 w-full  md:w-1/2 h-full text-center">
                <EchartsChart :option="actice_user"></EchartsChart>
            </div>
        </div>
        <div class="p-2">
            <span class="text-lg font-bold">排行榜</span>
        </div>
        <div class="h-[400px] w-full flex items-center md:flex-row flex-col">
            <div class="w-full md:w-1/2 h-full mt-8 mb-4  flex flex-col items-center justify-center">

                <div class="w-full    shadow-md p-4">
                    <span class="font-glow mt-8 ">
                        日活跃度排行榜
                        <span class="  text-green-300">
                            TOP10
                        </span>
                    </span>
                    <el-scrollbar height="350px">
                        <div v-for="(item, index) in dayCount" :key="index"
                            class="w-full h-auto flex justify-between items-center mt-4  ">
                            <div class="flex">
                                <img :src="item.icon" class="w-12 h-12 rounded-full" alt="">
                                <div>
                                    <p class="mb-2 ml-2">{{ item.nickName }}</p>
                                    <p class="ml-2 text-xs">活跃度: {{ item.active }}</p>
                                </div>
                            </div>
                            <span class="mr-4" :class="[
                                index === 0 ? 'font-glow font-bold text-yellow-400 text-xl' : '',
                                index === 1 ? 'font-glow text-gray-200' : '',
                                index === 2 ? 'font-glow text-orange-500/70' : '',
                            ]">
                                {{ index + 1 }}
                            </span>

                        </div>
                    </el-scrollbar>
                </div>
            </div>
            <div class="w-full md:w-1/2 h-full mt-8 mb-4 flex flex-col items-center justify-center">

                <div class="w-full  shadow-md p-4">
                    <span class="font-glow">周活跃度排行榜
                        <span class="  text-green-300">
                            TOP10
                        </span>
                    </span>
                    <el-scrollbar height="350px">

                        <div v-for="(item, index) in weekCount" :key="index"
                            class="w-full h-auto flex justify-between items-center mt-4  ">
                            <div class="flex">
                                <img :src="item.icon" class="w-12 h-12 rounded-full" alt="">
                                <div>
                                    <p class="mb-2 ml-2">{{ item.nickName }}</p>
                                    <p class="ml-2 text-xs">活跃度: {{ item.totalActive }}</p>
                                </div>
                            </div>
                            <span class="mr-4" :class="[
                                index === 0 ? 'font-glow font-bold text-yellow-400 text-xl' : '',
                                index === 1 ? 'font-glow text-gray-200' : '',
                                index === 2 ? 'font-glow text-orange-500/70' : '',
                            ]">
                                {{ index + 1 }}
                            </span>

                        </div>
                    </el-scrollbar>
                </div>
            </div>
            <div class="w-full md:w-1/2 h-full mt-8 mb-4 flex flex-col items-center justify-center">

                <div class="w-full  shadow-md p-4">
                    <span class="font-glow">月活跃度排行榜
                        <span class="  text-green-300">
                            TOP10
                        </span>
                    </span>
                    <el-scrollbar height="350px">
                        <div v-for="(item, index) in monthCount" :key="index"
                            class="w-full h-auto flex justify-between items-center mt-4  ">
                            <div class="flex">
                                <img :src="item.icon" class="w-12 h-12 rounded-full" alt="">
                                <div>
                                    <p class="mb-2 ml-2">{{ item.nickName }}</p>
                                    <p class="ml-2 text-xs">活跃度: {{ item.totalActive }}</p>
                                </div>
                            </div>
                            <span class="mr-4" :class="[
                                index === 0 ? 'font-glow font-bold text-yellow-400 text-xl' : '',
                                index === 1 ? 'font-glow text-gray-200' : '',
                                index === 2 ? 'font-glow text-orange-500/70' : '',
                            ]">
                                {{ index + 1 }}
                            </span>

                        </div>
                    </el-scrollbar>
                </div>
            </div>
            <div class="w-full md:w-1/2 h-full mt-8 mb-4 flex flex-col items-center justify-center">

                <div class="w-full   shadow-md p-4">
                    <span class="font-glow">总活跃度排行榜
                        <span class="  text-green-300">
                            TOP10
                        </span>
                    </span>
                    <el-scrollbar height="350px">
                        <div v-for="(item, index) in allCount" :key="index"
                            class="w-full h-auto flex justify-between items-center mt-4  ">
                            <div class="flex">
                                <img :src="item.icon" class="w-12 h-12 rounded-full" alt="">
                                <div>
                                    <p class="mb-2 ml-2">{{ item.nickName }}</p>
                                    <p class="ml-2 text-xs">活跃度: {{ item.totalActive }}</p>
                                </div>
                            </div>
                            <span class="mr-4" :class="[
                                index === 0 ? 'font-glow font-bold text-yellow-400 text-xl' : '',
                                index === 1 ? 'font-glow text-gray-200' : '',
                                index === 2 ? 'font-glow text-orange-500/70' : '',
                            ]">
                                {{ index + 1 }}
                            </span>

                        </div>
                    </el-scrollbar>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts" setup>
import { onMounted, ref, watch } from 'vue';
import EchartsChart from './EchartsChart.vue';
import { AdminApi } from '../api/admin';
import { BusinessError } from '../exception/BusinessError';
import { Log } from '../utils/TipUtil';
import { type userActiveVO, type UserTotalActiveVO, type WorkSpace } from '../types/WorkSpace';
import { TimeUtil } from '../utils/time';
import { da } from 'element-plus/es/locale/index.mjs';
import type { List } from 'echarts/core';
import { get } from 'lodash';
import { Position } from '@element-plus/icons-vue';
const dataOfDay = ref<number>(7);  //7 30 90

const total = ref<WorkSpace>({
    onlineUser: 0,
    todayNewUser: 0,
    todayNewMsg: 0,
    totalMsg: 0,
    totalUser: 0,
    date: ''
})

/** 总用户/新消息图表配置 */
const total_user = ref<any>({
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        data: ['每日新用户', '新消息']
    },
    xAxis: {
        type: 'category',
        data: [],
        axisLabel: {
            interval: 0,
            // 可选：旋转标签避免重叠
            rotate: 30
        }
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name: '每日新用户',
            data: [],
            type: 'line',
            itemStyle: {
                color: '#409EFF'
            }
        },
        {
            name: '新消息',
            data: [],
            type: 'line',
            itemStyle: {
                color: '#67C23A'
            }
        },
    ]
});

/** 活跃用户图表配置  */
const actice_user = ref<any>({
    tooltip: {
        trigger: 'axis'
    },
    legend: {
        data: ['活跃用户']
    },
    xAxis: {
        type: 'category',
        data: [],
        axisLabel: {
            interval: 0,
            rotate: 30
        }
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            name: '活跃用户',
            data: [],
            type: 'line'
        }
    ]
});

const dayCount = ref<userActiveVO[]>([]);
const weekCount = ref<UserTotalActiveVO[]>([]);
const allCount = ref<UserTotalActiveVO[]>([]);
const monthCount = ref<UserTotalActiveVO[]>([]);
// ====================== 公共工具函数（提取重复逻辑，减少冗余） ======================
/**
 * 设置ECharts X轴标签间隔（复用逻辑）
 * @param chartConfig 图表配置对象
 * @param dates 日期列表
 * @param dayCount 天数
 */
const setXAxisInterval = (chartConfig: any, dates: string[], dayCount: number) => {
    if (dayCount >= 30) {
        chartConfig.xAxis.axisLabel.interval = (index: number) => {
            const total = dates.length;
            const step = Math.ceil(total / 8); // 最多显示8个标签，避免拥挤
            return index === 0 || index === total - 1 || index % step === 0;
        };
    } else {
        chartConfig.xAxis.axisLabel.interval = 0; // 全部显示
    }
    chartConfig.xAxis.data = dates; // 统一设置X轴日期数据
};

// ====================== 核心业务函数 ======================
/**
 * 获取活跃用户数据
 */
async function queryActive() {
    const dates = TimeUtil.getPastDays(dataOfDay.value);
    try {
        // 类型断言为UserActive数组，适配返回值
        const result: { date: string, active: number }[] = await AdminApi.getUserActiveByDate(dates);
        if (!result) {
            Log.error('获取活跃用户数据失败');
            return;
        }
        const res: { date: string, active: number }[] = result;
        // 1. 设置X轴间隔和日期
        setXAxisInterval(actice_user.value, dates, dataOfDay.value);
        // 2. 处理数据（无数据时设为空数组）
        let acticeList: number[] = [];
        dates.forEach(date => {
            const result = res.find(each => each.date === date);
            acticeList.push(result?.active || 0);
        })
        actice_user.value.series[0].data = acticeList;

    } catch (err) {
        // 错误处理（保留你的原逻辑）
        if (err instanceof BusinessError) {
            Log.error(err.message);
        } else {
            Log.error('获取活跃用户数据失败');
            console.error(err);
        }
        // 异常时重置X轴
        actice_user.value.xAxis.axisLabel.interval = 0;
    }
}

/**
 * 获取新用户/新消息数据
 */
async function getNewUserByRange() {
    try {
        const dates = TimeUtil.getPastDays(dataOfDay.value);
        const res: WorkSpace[] = await AdminApi.getWorkSpaceDataByDate(dates);

        const newUserList: number[] = [];
        const newMsgList: number[] = [];

        // 1. 设置X轴间隔和日期
        setXAxisInterval(total_user.value, dates, dataOfDay.value);
        // 2. 遍历日期补全数据（无数据时填0）
        dates.forEach(date => {
            const result = res?.find(workspace => workspace.date === date);
            newUserList.push(result?.todayNewUser || 0);
            newMsgList.push(result?.todayNewMsg || 0);
        });
        // 3. 更新图表数据
        total_user.value.series[0].data = newUserList;
        total_user.value.series[1].data = newMsgList;


    } catch (err) {
        console.error(err);
        Log.error("获取趋势数据失败");
    }
}



async function getAllData() {
    try {
        const res: any = await AdminApi.getLatestWorkSpaceData();
        if (res) {
            const data = res;
            total.value = data;
        }
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        }
        else {
            Log.error('获取数据失败');
            console.error(err);
        }
    }

}


async function getUserActiveForDayTop10() {
    try {
        const res: userActiveVO[] = await AdminApi.getUserActive();
        res.sort((a, b) => b.active - a.active).splice(10)
        if (res) dayCount.value = res;
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        }
        else {
            Log.error('获取数据失败');
            console.error(err);
        }
        return [];
    }
}
async function getALLUserActiveForTop10() {
    try {
        const res: UserTotalActiveVO[] = await AdminApi.getUserAllActiveTOPN(10);
        if (res) {
            allCount.value = res;
        }
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        }
        else {
            Log.error('获取总用户活跃数据失败');
            console.error(err);
        }
        return [];
    }
}

async function getUserActiveForWeekTop10() {
    try {
        const res: UserTotalActiveVO[] = await AdminApi.getWeeklyUserActiveTop10()
        res.sort((a, b) => b.totalActive - a.totalActive).splice(7)
        if (res) weekCount.value = res;
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        }
        else {
            Log.error('获取数据失败');
            console.error(err);
        }
    }
}
async function getUserActiveForMonthTop10(month?: string) {
    try {
        const date = new Date();
        if (!month) {
            month = (date.getMonth()+1).toString();
        };
        const body = date.getFullYear().toString() + '-' + month.padStart(2, '0')
        const res: UserTotalActiveVO[] = await AdminApi.getMonthlyUserActiveTopN(10, body)
        res.sort((a, b) => b.totalActive - a.totalActive).splice(7)
        if (res) monthCount.value = res;
    } catch (err) {
        if (err instanceof BusinessError) {
            Log.error(err.message);
        }
        else {
            Log.error('获取数据失败');
            console.error(err);
        }
    }
}



watch(() => dataOfDay.value, () => {
    query();
})



onMounted(async () => {
    getAllData();
    query();
    getUserActiveForDayTop10();
    getALLUserActiveForTop10();
    getUserActiveForWeekTop10();
    getUserActiveForMonthTop10();
})

function query() {
    getNewUserByRange();
    queryActive();
}

</script>