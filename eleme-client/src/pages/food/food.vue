<template>
    <div class="mt-8">
        <head-top :head-title="headTitle" :go-back="true"></head-top>
        <div style="margin-top: 30px;">
            <!-- 搜索输入框 -->
            <form class="p-2 bg-gray-50 h-12 flex text-xxs" @submit.prevent>
                <input type="search" class="px-2 border flex-grow" placeholder="请输入关键词" v-model="searchValue">
                <button class="bg-blue-400 flex-1 ml-2 text-white rounded" @click="search()">提交</button>
            </form>
        </div>
        <van-config-provider :theme-vars="themeVars">
            <!-- 下拉二级筛选菜单 -->
            <van-dropdown-menu class="fixed w-full z-10" active-color="#3190e8">

                <!-- 食品分类 -->
                <van-dropdown-item :options="category" v-model="restaurant_category_id" :title="foodTitle" ref="food"
                    @open="chooseType('food')" @close="resetTitle">
                </van-dropdown-item>
                <!-- 排序 -->
                <van-dropdown-item title="排序" v-model="sortValue" :options="sortTypeList" @change="changeSort" />
                <!-- 筛选 -->
                <van-dropdown-item title="筛选" ref="filter">
                    <!-- 配送方式 -->
                    <div class="m-4 text-xs">
                        <span class="text-1xs">配送方式</span>
                        <div>
                            <van-checkbox-group v-model="deliveryChecked" direction="horizontal"
                                @change="selectDelivery">
                                <van-checkbox v-for="(item, index) in delivery" :key="index" :name="item"
                                    class="flex items-center pl-2 w-1/3 h-8 border-2 text-xxs"
                                    :class="delivery_mode !== item.id ? '' : 'border-blue-300'">
                                    <template #icon>
                                        <svg class="w-4 h-4">
                                            <use xmlns:xlink="http://www.w3.org/1999/xlink"
                                                :xlink:href="delivery_mode === item.id ? '#selected' : '#fengniao'">
                                            </use>
                                        </svg>
                                    </template>
                                    {{ item.text }}
                                </van-checkbox>
                            </van-checkbox-group>
                        </div>
                    </div>
                    <!-- 商家属性 -->
                    <div class="m-4 text-xs">
                        <span class="text-1xs">商家属性（可以多选）</span>
                        <div>
                            <van-checkbox-group v-model="activityChecked" direction="horizontal"
                                @change="selectActivity">
                                <van-checkbox v-for="(item, index) in activity" :key="index" :name="item"
                                    class="flex pl-2 w-1/3 h-8 border-2 text-xxs"
                                    :class="support_ids.indexOf(item.id) === -1 ? '' : 'border-blue-300'">
                                    <template #icon>
                                        <svg v-if="support_ids.indexOf(item.id) !== -1" class="w-4 h-4 mb-2 text-xxs">
                                            <use xmlns:xlink="http://www.w3.org/1999/xlink" :xlink:href="'#selected'">
                                            </use>
                                        </svg>
                                        <div v-else
                                            class="flex justify-center items-center w-4 h-4 text-xxs border-2 rounded"
                                            :style="{ color: '#' + item.icon_color, borderColor: '#' + item.icon_color }">
                                            {{ item.icon_name }}</div>
                                    </template>
                                    {{ item.name }}
                                </van-checkbox>
                            </van-checkbox-group>
                        </div>
                    </div>
                    <footer class="flex bg-gray-200 p-1">
                        <div class="flex-1 flex justify-center m-2">
                            <van-button class="w-full" type="danger" @click="clearSelect">清空</van-button>
                        </div>
                        <div class="flex-1 flex justify-center m-2">
                            <van-button class="w-full" type="success" @click="confirmSelect">
                                <template #default>
                                    <div>
                                        确定
                                        <span v-show="checkSum">({{ checkSum }})</span>
                                    </div>
                                </template>
                            </van-button>
                        </div>
                    </footer>
                </van-dropdown-item>
            </van-dropdown-menu>
        </van-config-provider>

        <shop-list class="pt-10" v-if="geohash" :geohash="geohash" :restaurantCategoryId="restaurant_category_id"
            :restaurantCategoryIds="restaurant_category_ids" :sortByType="sortByType" :deliveryMode="delivery_mode == 1"
            :supportIds="support_ids" :confirmSelect="confirmStatus" :keyValue="searchValue"></shop-list>
    </div>
</template>

<script>
import { toRefs, ref, reactive, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useStore } from 'vuex'
import { msiteAddress, foodCategory, foodDelivery, foodActivity } from '@/api/msite/msite'
import { getFoodCarousel } from '@/api8080/msite'
import HeadTop from '@/components/HeadTop'
import ShopList from '@/components/ShopList'
export default {
    name: 'Food',
    components: {
        HeadTop,
        ShopList
    },
    setup() {
        const route = useRoute()
        const store = useStore()
        const useMutation = store._mutations
        const themeVars = {
            treeSelectItemActiveColor: '#3190e8',
            sidebarSelectedBorderColor: '#3190e8',
            dropdownMenuHeight: '2rem'
        }
        const food = ref(null)
        const filter = ref(null)
        const state = reactive({
            geohash: '',
            headTitle: '',
            foodTitle: '',
            restaurant_category_id: '', // 食品类型ID
            restaurant_category_ids: '',    // 筛选类型ID
            sortValue: 0,   // 选中排序
            activeId: 1,
            activeIndex: 0,
            sortByType: null,   // 数据以何种方式进行排序
            deliveryChecked: [],    // 筛选配送方式
            activityChecked: [],    // 筛选商家活动
            category: [],
            delivery: [{ "color": "57A9FF", "id": 1, "is_solid": true, "text": "蜂鸟专送", "__v": 0 }],
            activity: [{
                "description": "已加入“外卖保”计划，食品安全有保障",
                "icon_color": "999999",
                "icon_name": "保",
                "id": 7,
                "name": "外卖保",
                "ranking_weight": 6,
                "__v": 0
            },
            {
                "description": "可使用支付宝、微信、手机QQ进行在线支付",
                "icon_color": "FF4E00",
                "icon_name": "付",
                "id": 3,
                "name": "在线支付",
                "ranking_weight": 2,
                "__v": 0
            },
            {
                "description": "准时必达，超时秒赔",
                "icon_color": "57A9FF",
                "icon_name": "准",
                "id": 9,
                "name": "准时达",
                "ranking_weight": 1,
                "__v": 0
            },
            {
                "description": "该商家支持开发票，请在下单时填写好发票抬头",
                "icon_color": "999999",
                "icon_name": "票",
                "id": 4,
                "name": "开发票",
                "ranking_weight": 3,
                "__v": 0
            },
            {
                "description": "",
                "icon_color": "3FBDE6",
                "icon_name": "品",
                "id": 8,
                "name": "品牌商家",
                "ranking_weight": 7,
                "__v": 0
            },
            {
                "description": "新店",
                "icon_color": "E8842D",
                "icon_name": "新",
                "id": 5,
                "name": "新店",
                "ranking_weight": 4,
                "__v": 0
            }],
            delivery_mode: null, // 选中的配送方式
            support_ids: [],    // 选中的商家活动（多选）
            sortTypeList: [ // 排序方式列表
                { text: '智能排序', value: 1, sortType: 1 },
                { text: '距离最近', value: 2, sortType: 2 },
                { text: '销量最高', value: 3, sortType: 3 },
                { text: '起送价最低', value: 4, sortType: 4 },
                { text: '配送速度最快', value: 5, sortType: 5 },
                { text: '评分最高', value: 6, sortType: 6 },
            ],
            checkSum: 0,    // 多选项总和
            confirmStatus: false,    // 是否确认选择
            searchValue: '',//搜索关键词
        })
        watch(() => [state.delivery_mode, state.support_ids], ([newDelivery, newSupport], [oldDelivery, oldSupport]) => {
            if (oldDelivery !== newDelivery || oldSupport !== newSupport) {
                let delivery_mode
                if (!newDelivery) {
                    delivery_mode = 0
                } else {
                    delivery_mode = 1
                }
                console.log()
                state.checkSum = delivery_mode + state.support_ids.length
            }
        })
        //每次选择完分类以后进行一次监听 对head和title进行赋值
        watch(() => [state.restaurant_category_id], (newValue, oldValue) => {
            console.log("category", state.category)
            console.log('newValue', newValue)
            if (!newValue[0]) {
                console.log("new value is null")
                state.foodTitle = '无分类'
                state.headTitle = '无分类'
            } else if (newValue != oldValue && state.category != [] && state.category != undefined && state.category != null) {
                state.category.forEach(item => {
                    if (item.value == newValue) {
                        state.foodTitle = item.text
                        state.headTitle = state.foodTitle
                    }
                })
            }
            state.confirmStatus = !state.confirmStatus
        })
        onMounted(() => {
            initData()
        })
        const initData = async () => {    // 数据初始化
            if(route.query.key )state.searchValue = route.query.key
            state.geohash = route.query.geohash
            state.headTitle = route.query.title
            state.foodTitle = state.headTitle
            if (route.query.restaurant_category_id) {
                state.restaurant_category_id = route.query.restaurant_category_id
            } else {
                state.restaurant_category_id = null
            }
            let category = await getFoodCarousel()
            category = category.data.data
            category.push({
                name: '无分类',
                id: null,
            })
            state.category = category.map((item) => {
                return {
                    text: item['name'],
                    value: item['id']
                }
            })
            if (state.restaurant_category_id) {
                console.log("restaurant_category_id", state.restaurant_category_id)
                console.log("state.category", state.category)
                state.category.forEach(item => {
                    if (item.value == state.restaurant_category_id) {
                        state.foodTitle = item.text
                        state.headTitle = state.foodTitle
                        console.log("success title")
                    }
                })
            } else {
                state.foodTitle = '无分类'
                state.headTitle = '无分类'
                console.log("success title")
            }
        }
        const chooseType = (type) => {  // 选择分类
            if (type == 'food') {
                state.foodTitle = '分类'
            } else {
                state.foodTitle = state.headTitle
            }
        }
        const resetTitle = () => {  // 关闭下拉菜单同时重置标题
            state.foodTitle = state.headTitle
        }
        const selectFoodType = (item) => {  // 获取所选食品分类信息
            console.log(item)
            state.restaurant_category_ids = item.id
            state.foodTitle = state.headTitle = item.name
            food.value.toggle()
        }
        const changeSort = (val) => {   // 获取排序方式
            state.sortByType = state.sortTypeList[val].sortType
            console.log('state.sortByType', state.sortByType)
        }
        const selectDelivery = (item) => {
            if (item.length) {
                state.delivery_mode = item[0].id
            } else {
                state.delivery_mode = null
            }
        }
        const selectActivity = (item) => {
            console.log(item)
            let support_ids = []
            if (item.length) {
                item.forEach(i => {
                    support_ids.push(i.id)
                })
            } else {
                support_ids = []
            }
            state.support_ids = Array.from(new Set(support_ids))
        }
        const clearSelect = () => { // 重置数据选中状态
            state.delivery_mode = null
            state.support_ids = []
            state.deliveryChecked = [],
                state.activityChecked = [],
                state.checkSum = 0
        }
        const confirmSelect = () => {   // 提交筛选条件
            state.confirmStatus = !state.confirmStatus
            filter.value.toggle()

        }
        const search = () => {
            state.confirmStatus = !state.confirmStatus
        }
        return {
            ...toRefs(state),
            food,
            filter,
            themeVars,
            chooseType,
            resetTitle,
            changeSort,
            selectFoodType,
            selectDelivery,
            selectActivity,
            clearSelect,
            confirmSelect,
            search
        }
    }
}
</script>

<style scoped>
.van-checkbox--horizontal {
    margin-right: 0px !important;
}

.van-checkbox__icon {
    height: 0 !important;
}
</style>