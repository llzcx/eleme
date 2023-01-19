<template>
    <div class="absolute top-0 right-0 bottom-0 right-0 w-full bg-gray-200">
        <head-top head-title="商家详情" :go-back='true'></head-top>
        <!-- 商家详情 -->
        <section v-if="shopDetail" class="pt-8">
            <!-- 活动与属性 -->
            <div class="mt-2 bg-white">
                <span class="flex items-center p-1 text-xs border-b">活动与属性</span>
                <div class="text-gray-500">
                    <!-- 满减 -->
                    <!-- <ul class="px-1 text-xxs">
                        <li v-for="item in shopDetail.activities" :key="item.id" class="my-2">
                            <span class="mr-2 text-white" :style='{backgroundColor: "#" + item.icon_color}'>{{item.icon_name}}</span>
                            <span>{{item.description}}(APP专享)</span>
                        </li>
                    </ul> -->
                    <!-- 保障 -->
                    <ul class="px-1 text-xxs">
                        <div class="my-2">
                            <span v-if="shopDetail.bao" class="mr-2 text-white" :style='{backgroundColor: "rgb(153, 153, 153)"}'>保</span>
                            <span>已加入“外卖保”计划，食品安全有保障(APP专享)</span>
                        </div>
                        <div v-if="shopDetail.zhun" class="my-2">
                            <span class="mr-2 text-white" :style='{backgroundColor: "rgb(87, 169, 255)"}'>准</span>
                            <span>准时必达，超时秒赔(APP专享)</span>
                        </div>
                        <div v-if="shopDetail.piao" class="my-2">
                            <span class="mr-2 text-white" :style='{backgroundColor: "rgb(153, 153, 153)"}'>票</span>
                            <span>该商家支持开发票，请在下单时填写好发票抬头(APP专享)</span>
                        </div>
                    </ul>
                </div>
            </div>
            <!-- 食品监督安全公示 -->
            <div class="mt-2 bg-white">
                <span class="flex items-center p-1 text-xs border-b">食品监督安全公示</span>
                <div class="flex p-2">
                    <svg width="2rem" height="2rem" v-if="true">
                        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#res-well"></use>
                    </svg>
                    <svg width="2rem" height="2rem" v-else>
                        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#res-bad"></use>
                    </svg>
                    <div class="flex flex-col ml-2 justify-between text-gray-500">
                        <div class="text-xxs">
                            <span>监督检查结果：</span>
                            <span :class="true ? 'text-green-500' : 'text-red-500'">{{true ? '良好' : '差'}}</span>
                        </div>
                        <span class="text-xxs">检查日期：{{'一个月前'}}</span>
                    </div>
                </div>
            </div>
            <!-- 商家信息 -->
            <div class="mt-2 bg-white">
                <span class="flex items-center p-1 text-xs border-b">商家信息</span>
                <div class="text-xxs text-gray-500">
                    <div class="border-b px-1 py-2">{{shopDetail.name}}</div>
                    <div class="border-b px-1 py-2">地址：{{shopDetail.address}}</div>
                    <div class="border-b px-1 py-2">营业时间：[{{(shopDetail.startTime+'/'+shopDetail.endTime)}}]</div>
                    <div class="border-b px-1 py-2 flex justify-between">
                        <span>营业执照</span>
                        <van-icon name="arrow" />
                    </div>
                    <div class="border-b px-1 py-2 flex justify-between">
                        <span>餐饮服务特许证</span>
                        <van-icon name="arrow" />
                    </div>
                </div>
            </div>
        </section>
    </div>
</template>

<script>
import { reactive, toRefs, onMounted } from 'vue'
import { useStore } from 'vuex'
import HeadTop from '@/components/HeadTop'
export default {
    components: {
        HeadTop
    },
    setup() {
        let store = useStore()
        const state = reactive({
            shopDetail: null
        })
        onMounted(() => {
            console.log('[in shopDetail page]state.shopDetail',state.shopDetail)
            state.shopDetail = store.state.shopDetail
        })
        return {
            ...toRefs(state)
        }
    }
}
</script>

<style scoped>

</style>