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
                        <span :class=" total.todayNewUser >0 ?'text-green-400':'text-red-400'">
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
                    <p class="text-white font-bold">{{ total.totalIncome || 0 }}</p>
                    <p class="text-white font-bold mt-4 text-sm md:text-lg">收入总数</p>
                    <p class="text-white font-bold mt-4 text-sm md:text-lg">较昨日
                        <span :class=" total.todayNewIncome >0 ?'text-green-400':'text-red-600'">
                            {{ total.todayNewIncome }}
                        </span>
                    </p>
                </div>
            </div>
            <div class="bg-green-500 w-48 h-24 md:w-96 md:h-36 rounded-xl flex items-center">
                <el-icon size="36" class="ml-4">
                    <CopyDocument />
                </el-icon>
                <div class="ml-10">
                    <p class="text-white font-bold">{{ total.totalOrder || 0 }}</p>
                    <p class="text-white font-bold mt-4 text-sm md:text-lg">订单总数</p>
                    <p class="text-white font-bold mt-4 text-sm md:text-lg">较昨日
                           <span :class=" total.todayNewOrder >0 ?'text-green-400':'text-red-400'">
                            {{ total.todayNewOrder }}
                        </span>
                    </p>
                </div>
            </div>

        </div>
        <div class="flex flex-col md:flex-row items-center w-full  h-[500px] mt-8">
            <div class="w-full  md:w-[60%] h-full mt-8">
                <div class="flex justify-between w-full mr-2">
                    <span class="font-bold ml-2">销售趋势</span>
                    <div class="flex border border-gray-400">
                        <button class="mr-4 cursor-pointer " :class="dataOfDay == 7 ? 'bg-blue-400' : ''"
                            @click="dataOfDay = 7">最近7日</button>
                        <button class="mr-4 cursor-pointer" :class="dataOfDay == 30 ? 'bg-blue-400' : ''"
                            @click="dataOfDay = 30">最近30日</button>
                        <button class="cursor-pointer" :class="dataOfDay == 90 ? 'bg-blue-400' : ''"
                            @click="dataOfDay = 90">最近90日</button>
                    </div>
                </div>
                <div class="flex w-full h-full mt-4">
                    <EchartsChart :option="total_user"></EchartsChart>
                </div>
            </div>
            <div class=" mt-8 md:w-[40%] w-full h-full text-center">
                <div class="w-full h-full">
                    <span class="font-bold">
                        流水占比
                    </span>
                    <div class="w-full h-full">
                        <EchartsChart :option="total_recharge"></EchartsChart>
                    </div>
                </div>
            </div>
        </div>
       <!--  <div class="w-[50vw] h-auto mt-4">
            <div class="flex justify-between itesm-center">
                <span>最新订单</span>
                <span>查看更多</span>
            </div>
            <div class="w-full">

            </div>
        </div> -->
    </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue';
import { BarChart, LineChart } from 'echarts/charts'

import type { BarSeriesOption, EChartsOption } from 'echarts/types/dist/shared';
import EchartsChart from './EchartsChart.vue';
import { AdminApi } from '../api/admin';
import { BusinessError } from '../exception/BusinessError';
import { Log } from '../utils/TipUtil';
import type { WorkSpace } from '../types/WorkSpace';
const total = ref<WorkSpace>({
    todayNewIncome: 0,
    todayNewUser: 0,
    todayNewOrder: 0,
    totalIncome: 0,
    totalOrder: 0,
    totalUser: 0,

})
const total_recharge: any = {
    series: [
        {

            type: 'pie',
            center: ['50%', '50%'],
            radius: ['60%'],
            data: [
                {
                    value: 335,
                    name: '直接访问'
                },
                {
                    value: 234,
                    name: '联盟广告'
                },
                {
                    value: 1548,
                    name: '搜索引擎'
                }
            ],
            label: {
                show: true,
                formatter: '{b}:{c} ({d}%)',
                fontSize: 12,
                color: 'green'
            },
            labelLine: {
                show: true,
                length: 15,
                length2: 10
            }
        }
    ]
}
const total_user = ref<any>({
    xAxis: {
        type: 'category',
        data: []
    },
    yAxis: {
        type: 'value'
    },
    series: [
        {
            data: [],
            type: 'line'
        }
    ]
});
const dataOfDay = ref<number>(7);  //7 30 90


async function getAllData() {
    try {
        const res: any = await AdminApi.getLatestWorkSpaceData();
        if (res) {
            const data = res;
            total_user.value.xAxis.data = Object.keys(data);
            total_user.value.series[0].data = Object.values(data);
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


async function getNewUserByRange() { }

onMounted(() => {
    getAllData();
})

watch(dataOfDay, () => {
    getNewUserByRange();
})
</script>