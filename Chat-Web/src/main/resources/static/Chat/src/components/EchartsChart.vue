<template>
    <!-- 图表容器，必须设置宽高 -->
    <div ref="chartRef" class="echarts-container" :style="{ width, height }"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch } from 'vue';
// 按需引入 ECharts 核心及所需模块
import * as echarts from 'echarts/core';
import { BarChart, LineChart, PieChart } from 'echarts/charts';
import {
    TitleComponent,
    TooltipComponent,
    GridComponent,
    DatasetComponent,
    TransformComponent,
} from 'echarts/components';
import { LabelLayout, UniversalTransition } from 'echarts/features';
import { CanvasRenderer } from 'echarts/renderers';
import type {
    BarSeriesOption,
    LineSeriesOption,
    PieSeriesOption
} from 'echarts/charts';
import type {
    TitleComponentOption,
    TooltipComponentOption,
    GridComponentOption,
    DatasetComponentOption
} from 'echarts/components';
import type { ComposeOption } from 'echarts/core';


// 注册 ECharts 组件（只注册需要的，减小体积）
echarts.use([
    TitleComponent,
    TooltipComponent,
    GridComponent,
    DatasetComponent,
    TransformComponent,
    BarChart,
    LineChart,
    LabelLayout,
    PieChart,
    UniversalTransition,
    CanvasRenderer
]);

// 定义 Option 类型
type ECOption = ComposeOption<
    | BarSeriesOption
    | LineSeriesOption
    | TitleComponentOption
    | TooltipComponentOption
    | GridComponentOption
    |PieSeriesOption
    | DatasetComponentOption
>;

// 接收父组件传参
const props = defineProps({
    // 图表配置项
    option: {
        type: Object as () => ECOption,
        required: true
    },
    // 图表宽度，默认100%
    width: {
        type: String,
        default: '100%'
    },
    // 图表高度，默认100%
    height: {
        type: String,
        default: '100%'
    }
});

// 图表实例和DOM引用
const chartRef = ref<HTMLDivElement | null>(null);
let chartInstance: echarts.ECharts | null = null;

// 初始化图表
const initChart = () => {
    if (!chartRef.value) return;
    // 创建图表实例
    chartInstance = echarts.init(chartRef.value);
    // 设置配置项
    chartInstance.setOption(props.option);
    // 适配窗口大小变化
    window.addEventListener('resize', resizeChart);
};

// 调整图表大小
const resizeChart = () => {
    chartInstance?.resize();
};

// 监听配置项变化，更新图表
watch(
    () => props.option,
    (newOption) => {
        chartInstance?.setOption(newOption);
    },
    { deep: true }
);

// 挂载时初始化
onMounted(() => {
    initChart();
});

// 卸载时销毁实例，防止内存泄漏
onUnmounted(() => {
    window.removeEventListener('resize', resizeChart);
    chartInstance?.dispose();
    chartInstance = null;
});
</script>

<style scoped>
.echarts-container {
    display: block;
}
</style>