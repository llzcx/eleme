<template>
    <div class="pt-8">
        <head-top head-title="搜索" :go-back="true"></head-top>
        <!-- 搜索输入框 -->
        <form class="p-2 bg-gray-50 h-12 flex text-xxs" @submit.prevent>
            <input type="search" class="px-2 border flex-grow" placeholder="请输入商家或店铺名称" v-model="searchValue">
            <button class="bg-blue-400 flex-1 ml-2 text-white rounded" @click="search()">提交</button>
        </form>
        <section class="text-xxs">
            <span class="block text-md text-gray-500 m-2">全城热搜:{{ cityName }}</span>
            <div>
                <div class="top-10">
                    <ul>
                        <li :key="index" v-for="item,index in HotData" @click="gotoFood(item.word)"><a>{{item.word+"      |热度:"+item.num+"w"}}</a></li>
                    </ul>
                </div>
            </div>
        </section>
        <!-- 有数据 展示搜索结果列表 -->
        <section v-if="restrauantList.length" class="text-xxs">
            <span class="block text-md text-gray-500 m-2">商家</span>
            <ul class="bg-gray-50">
                <li v-for="item in restrauantList" :key="item.id" class="flex border-t-2 border-b-2"
                    @click="goToShop(item)">
                    <!-- 左侧 图片 -->
                    <div id="search_item_left" class="flex items-center p-2">
                        <van-image width="64" height="64" :src="item.imagePath"></van-image>
                    </div>
                    <!-- 右侧 详情 -->
                    <div id="search_item_right" class="flex flex-col justify-around flex-1 text-xs">
                        <span class="flex items-center">
                            {{ item.name }}
                            <svg xmlns="http://www.w3.org/2000/svg" version="1.1" width="24" height="14" class="ml-2">
                                <polygon points="0,14 4,0 24,0 20,14" style="fill:none;stroke:#FF6000;stroke-width:1" />
                                <line x1="1.5" y1="12" x2="20" y2="12" style="stroke:#FF6000;stroke-width:1.5" />
                                <text x="3.5" y="9" style="fill:#FF6000;font-size:9;font-weight:bold;">支付</text>
                            </svg>
                        </span>
                        <span>月销 {{ item.recentOrderNum }} 单</span>
                        <span>{{ item.delivery_fee || item.floatMinimumOrderAmount }} 元起送 / 距离{{ item.distance + "m"
                        }}</span>
                    </div>
                </li>
            </ul>
        </section>
        <!-- 默认显示历史记录 -->
        <section v-if="searchHistory.length && showHistory" class="text-xxs">
            <span class="block text-md text-gray-500 m-2">搜索历史</span>
            <ul class="bg-gray-50">
                <li v-for="(item, index) in searchHistory" :key="index"
                    class="flex justify-between items-center border-b-2 mx-2 h-12">
                    <div class="flex-grow" @click="search(item)">{{ item }}</div>
                    <van-icon name="cross" class="w-8 text-center" @click="delHistory(index)"></van-icon>
                </li>
            </ul>
            <button class="mt-2 h-8 w-full bg-white" @click="clearAll">清空搜索历史</button>
        </section>
        <!-- 没有搜索记录显示空状态 -->
        <van-empty v-if="isEmpty" class="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 "
            image="search" description="暂无搜索结果" />
        <!-- tabbar -->
        <Footer class="mt-12"></Footer>
    </div>
</template>

<script>
import { onMounted, reactive, toRefs } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { searchRestaurant } from '@/api/msite/msite'
import { getShops, getShopsByEs,getHotWordList } from '@/api8080/msite'
import { getCity } from '@/api8080/gaode'
import { imgBaseUrl } from '@/config/env'
import { getStore, setStore, removeStore } from '@/config/utils'
import HeadTop from '@/components/HeadTop'
import Footer from '@/components/Footer'
export default {
    name: 'Search',
    components: {
        HeadTop,
        Footer
    },
    setup() {
        const route = useRoute()
        let router = useRouter()
        const state = reactive({
            geohash: '',
            searchValue: '',    // 搜索内容
            restrauantList: [],
            searchHistory: [],
            imgBaseUrl,
            showHistory: true,  // 是否显示历史记录，默认显示
            isEmpty: false, // 搜索结果为空
            pagenum: 1,
            lastId: null,
            HotData:[],
            cityName:'',
        })
        onMounted(() => {
            state.geohash = route.params.geohash
            // 如果存在历史搜索记录，默认展示
            if (getStore('searchHistory')) {
                state.searchHistory = JSON.parse(getStore('searchHistory'))
            }
            initData()
        })
        const initData = async () => {
            const resp = await getHotWordList(state.geohash)
            if(resp.data && resp.data.success){
                state.HotData = resp.data.data
            }else{
                console.log("拉取本城市热点key失败")
            }
            if(state.geohash){
                const resp1 = await getCity(state.geohash)
                state.cityName = resp1.data.data
            }
        }
        const search = async (historyVal) => {
            if (historyVal) {
                state.searchValue = historyVal
            } else if (!state.searchValue) {
                return
            }
            // 隐藏搜索历史记录
            state.showHistory = false
            let longitude = state.geohash.substring(0, state.geohash.indexOf(','))
            let latitude = state.geohash.substring(state.geohash.indexOf(',') + 1)
            console.log("longitude", longitude)
            console.log("latitude", longitude)
            let dataList = await getShopsByEs(state.lastId, 20, Number(longitude), Number(latitude), state.searchValue, null, null, null, null, null, null, null, null, null)
            if (dataList.data.success) {
                state.lastId = dataList.data.data.lastId
            }
            state.restrauantList = dataList.data.data.rows
            state.isEmpty = !state.restrauantList.length
            // 进入下一页面前先进行查重判断，如果存在相同的历史记录则不做重复存储，不存在则存储
            let history = getStore('searchHistory')
            if (history) {
                let checkRepeat = false
                state.searchHistory = JSON.parse(history)
                state.searchHistory.forEach(item => {
                    if (item == state.searchValue) {
                        checkRepeat = true
                    }
                })
                if (!checkRepeat) {
                    state.searchHistory.push(state.searchValue)
                }
            } else {
                state.searchHistory.push(state.searchValue)
            }
            setStore('searchHistory', state.searchHistory)
        }
        const delHistory = (index) => {
            state.searchHistory.splice(index, 1)
            setStore('searchHistory', state.searchHistory)
        }
        const clearAll = () => {
            state.searchHistory = []
            setStore('searchHistory', state.searchHistory)
        }
        const goToShop = (item) => {
            router.push({ path: '/shop', query: { geohash: state.geohash, id: item.id } })
        }
        const gotoFood = (value)=>{
            router.push({ path: '/food', query: { geohash: state.geohash, key:value } })
        }
        return {
            ...toRefs(state),
            search,
            clearAll,
            delHistory,
            goToShop,
            gotoFood
        }
    }
}
</script>

<style scoped>
.van-empty>>>.van-empty__description {
    padding: 0;
}

.top-10 a {
    color: #525C66;
    text-decoration: none;
}

.top-10 {
    float: left;
    width: 390px;
    background: #fff;
    border: 1px solid #FFF;
    box-shadow: #d0d0d0 1px 1px 10px 0px;
}

.top-10 ul {
    counter-reset: section;
}

.top-10 li {
    float: left;
    width: 260px;
    border-bottom: 1px solid #b8c2cc;
    line-height: 46px;
    height: 46px;
    overflow: hidden;
    color: #525C66;
    font-size: 14px;

}

.top-10 li:before {
    counter-increment: section;
    content: counter(section);
    display: inline-block;
    padding: 0 12px;
    margin-right: 10px;
    height: 18px;
    line-height: 18px;
    background: #b8c2cc;
    color: #fff;
    border-radius: 3px;
    font-size: 9px
}

.top-10 li:nth-child(1):before {
    background: #e12525
}

.top-10 li:nth-child(2):before {
    background: #d6820c
}

.top-10 li:nth-child(3):before {
    background: #eeda57
}

.top-10 #rank-more {
    float: left;
    width: 100%;
    margin-top: 8px;
    text-align: center;
}

.top-10 .span-h7 {
    font-size: 14px;
    color: #B8C2CC;
    font-weight: 300;
}
</style>