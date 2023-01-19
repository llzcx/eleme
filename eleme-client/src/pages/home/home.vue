<template>
    <div>
        <!-- 引入HeadTop组件 -->
        <head-top signin-up="home">
            <!-- vue3具名插槽使用不同于vue2，需要嵌套template -->
            <template #logo>
                <span class="ml-1.5 h-8 leading-8 font-serif text-white">{{ title }}</span>
            </template>
        </head-top>
        <!-- 空出头部组件高度 -->
        <!-- 城市选择 -->
        <nav id="city_nav" class="pt-8 mb-2 bg-gray-50 divide-y border-b-2">
            <div class="px-2 py-1 flex justify-between leading-6 font-serif text-xxs">
                <span>{{completePosition}}</span>
            </div>
            <div class="px-2 py-1 flex justify-between leading-6 text-xxs">
                <span>当前坐标:</span>
                <span>{{newGeohash}}</span>
            </div>
            <!-- 当前定位 -->
            <router-link to="/city/" id="now_city"
                class="px-2 py-1 flex justify-between items-center leading-6">
                <span class="text-blue-400">{{ nowPosition }}</span>
                <svg id="arrow_right" class="w-4 h-4 fill-current text-gray-300">
                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#arrow-right"></use>
                </svg>
            </router-link>
        </nav>
        <!-- 热门城市 -->
        <!-- <nav id="hot_city" class="mb-2 bg-gray-50 border-t-2 divide-y">
            <span class="inline-block leading-6 px-2 py-1 font-serif text-xs">热门城市</span>
            <ul class="flex flex-wrap">
                <router-link tag="li" :to="'/city/' + item.id" v-for="item in hotCityList" :key="item.id" class="w-1/4 h-8 leading-8 text-center font-serif text-xs border border-gray-200 text-blue-400">
                    {{item.name}}
                </router-link>
            </ul>
        </nav> -->
        <!-- 全部城市列表（按照拼音顺序排序） -->
        <!-- <nav id="all_city" class="mb-2 bg-gray-50 border-t-2 divide-y" v-for="(item1, index) in citySortList" :key="index">
            <span v-if="item1=='A'" class="inline-block leading-6 px-2 py-1 font-serif text-xs">{{item1}}&nbsp;(按字母排序)</span>
            <span v-else class="inline-block leading-6 px-2 py-1 font-serif text-xs">{{item1}}</span>
            <ul class="flex flex-wrap">
                <router-link tag="li" :to="'/city/' + item2.id" v-for="item2 in allCityList[item1]" :key="item2.id" class="w-1/4 h-8 leading-8 text-center font-serif text-xs border border-gray-200 truncate ...">
                    {{item2.name}}
                </router-link>
            </ul>
        </nav> -->
    </div>
</template>

<script>
import { onMounted, reactive, toRefs, getCurrentInstance } from 'vue'
import HeadTop from '@/components/HeadTop'
import { guessCity, hotCity, allCity } from '@/api/city/city'
import { regeo } from '@/api8080/gaode'
import { useStore } from 'vuex'
import { getStore, setStore } from '@/config/utils'
export default {
    name: 'Home',
    components: {
        HeadTop
    },
    setup() {
        // 获取全局属性
        // const instance = getCurrentInstance()
        // const _this = instance.appContext.config.globalProperties
        const store = useStore()
        const useMutation = store._mutations
        const state = reactive({
            title: '由高德地图提供支持',
            guess_city: '未知',
            guess_city_id: '',
            hotCityList: [],
            citySortList: [],
            allCityList: [],
            nowPosition: null,
            completePosition:null,
            newGeohash: null,
        });
        onMounted(() => {
            state.nowPosition = getStore('position')
            state.newGeohash = getStore('geohash')
            init()
            regeohandle()
        })
        //初始化 将位置信息和geohash从本地读取
        const init = () => {
            useMutation.SAVE_POSITION[0](getStore('position'))
            useMutation.SAVE_GEOHASH[0](getStore('geohash'))

        }
        //逆编码
        const regeohandle = async () =>{
            let str = ""
            const resp = await regeo(state.newGeohash)
            str += resp.data.regeocode.addressComponent.city
            str += resp.data.regeocode.addressComponent.district
            str += resp.data.regeocode.addressComponent.township
            str += resp.data.regeocode.aois[0].name
            state.completePosition = str
        }


        return {
            ...toRefs(state),
        }
    }
}
</script>

<style>

</style>