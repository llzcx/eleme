<template>
    <div class="mt-8 mb-12">
        <head-top head-title="确认订单" :go-back="true" signin-up='confirmOrder'></head-top>
        <section v-if="chooseAddress" class="address_contsiner">
            <!-- id: orderData.cart.id, sig: orderData.sig -->
            <router-link :to='{ path: "/confirmOrder/chooseAddress", query: {} }' class="h-full flex items-center">
                <div class="ml-2 flex flex-1 items-center">
                    <van-icon name="location-o" />
                    <div>
                        <div class="ml-2">
                            <span class="text-1xs font-bold">{{ chooseAddress.name }}</span>
                            <span class="text-xxs ml-1">{{ chooseAddress.sex == 1 ? '先生' : '女士' }}</span>
                            <span class="text-xxs ml-1">{{ chooseAddress.phone }}</span>
                        </div>
                        <div class="ml-2">
                            <span v-if="chooseAddress.tag" class="text-xxs bg_tag text-white px-1 mr-2 rounded"
                                :style="{ backgroundColor: iconColor(chooseAddress.tag) }">{{ chooseAddress.tag }}</span>
                            <span class="text-xxs text-gray-400">{{ chooseAddress.specificAddress }}</span>
                        </div>
                    </div>
                </div>
                <van-icon class="mr-1 text-gray-400" name="arrow" />
            </router-link>
        </section>
        <section v-else class="address_contsiner">
            <router-link :to='{ path: "/profile/info/address/add", query: {} }' class="h-full flex items-center">
                <span class="text-xxs text-gray-600">您现在还没有添加任何地址,点击前往添加地址</span>
            </router-link>
        </section>
        <div class="flex justify-between mt-2 px-4 bg-white border-l-4 border-blue-400 py-4 text-1xs">
            <span class="font-bold">送达时间</span>
            <div class="flex flex-col items-end">
                <span class="mb-2 text-blue-400">尽快送达 ｜ 预计 {{ "10" }}</span>
                <span class="w-12 bg-blue-400 text-white rounded-sm text-center text-xxs"
                    v-if="shopInfo && shopInfo.deliveryMode">蜂鸟专送</span>
            </div>
        </div>
        <div class="mt-2 bg-white text-1xs px-2 divide-y">
            <div class="flex justify-between py-2">
                <span>支付方式</span>
                <div class="text-gray-400">
                    <span>在线支付</span>
                    <van-icon name="arrow" />
                </div>
            </div>
            <div class="flex justify-between text-gray-400 py-2">
                <span>红包</span>
                <span>暂时只支持在饿了么APP中使用</span>
            </div>
        </div>
        <div v-if="shopInfo != null" class="mt-2 bg-white px-2 divide-y">
            <div v-if="true" class="flex items-center py-2">
                <van-image width="1.2rem" height="1.2rem" :src="shopInfo.imagePath"></van-image>
                <span class="text-1xs ml-2">{{ shopInfo.name }}</span>
            </div>
            <ul>
                <div v-if="skuCartList != null">
                    <li v-for="sku in skuCartList" :key="sku.skuId" class="text-1xs flex justify-between py-1">
                        <span class="text-gray-500">{{ sku.name }}</span>
                        <span class="text-gray-500">{{ getspecsStrList(sku) }}</span>
                        <div class="flex justify-between w-20">
                            <span v-if="sku.num > 0" class="text-orange-500">x {{ sku.num }}</span>
                            <span v-else></span>
                            <span class="text-gray-500">¥{{ sku.price }}</span>
                        </div>
                    </li>
                </div>
                <div class="text-1xs flex justify-between py-1 text-gray-500">
                    <span>配送费</span>
                    <div>
                        <span></span>
                        <span>¥ {{ shopInfo.floatDeliveryFee || 0 }}</span>
                    </div>
                </div>
                <div class="text-1xs flex justify-between py-1 text-gray-500">
                    <span>总打包费</span>
                    <div>
                        <span></span>
                        <span>¥ {{ totalPackingFee || 0 }}</span>
                    </div>
                </div>
                <div v-if="true" class="text-1xs flex justify-between py-1 text-gray-500">
                    <span>{{ "总价" }}</span>
                    <div>
                        <span></span>
                        <span>¥ {{ totalPrice }}</span>
                    </div>
                </div>
            </ul>
            <div class="flex justify-between text-1xs py-1 text-gray-500">
                <span>订单¥{{ totalPrice }}</span>
                <span class="text-orange-400">待支付 ¥{{ totalPrice }}</span>
            </div>
        </div>
        <div class="mt-2 bg-white px-2 text-gray-400 text-1xs divide-y">
            <router-link :to='{ path: "/confirmOrder/remark", query: {} }' class="flex justify-between py-2">
                <span class="text-gray-500">订单备注</span>
                <div class="flex-1 flex justify-between items-center text-right ml-6">
                    <span v-if="remarks" class="inline-block w-40 truncate ...">{{ remarks }}</span>
                    <span v-else class="inline-block w-40 truncate ...">{{ '口味、偏好等' }}</span>
                    <van-icon name="arrow" />
                </div>
            </router-link>
            <div class="flex justify-between py-2">
                <span class="text-gray-500">发票抬头</span>
                <div>
                    <span>不需要开发票</span>
                    <van-icon name="arrow" />
                </div>
            </div>
        </div>
        <div v-if="true"
            class="fixed flex justify-between items-center bottom-0 w-full h-8 bg-gray-800 text-white text-1xs">
            <span class="ml-2">待支付 ¥{{ totalPrice }}</span>
            <span class="flex items-center bg-green-400 h-full px-4" @click="confirmOrder">确认下单</span>
        </div>
        <van-dialog v-model:show="showAlert">
            <template #default>
                <div class="flex flex-col items-center">
                    <van-icon size="82" class="m-2 text-yellow-400" name="warning-o" />
                    <span class="mb-2">{{ alertText }}</span>
                </div>
            </template>
            <template #footer>
                <div class="bg-green-400">
                    <button class="w-full h-6 text-white" @click="showAlert = false">确认</button>
                </div>
            </template>
        </van-dialog>
        <transition name="router-slid" mode="out-in">
            <router-view></router-view>
        </transition>
    </div>
</template>

<script>
import { reactive, toRefs, onMounted, computed, watch, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import { checkout, placeOrders } from '@/api/order/order'
import { imgBaseUrl } from '@/config/env'
import { getAddressListByCId } from '@/api8080/address'
import { getShopById } from '@/api8080/msite'
import { saveBuyCart, saveOrderInfo } from '@/api8080/order'
import HeadTop from '@/components/HeadTop'
export default {
    components: {
        HeadTop
    },
    setup() {
        const store = useStore()
        const useMutation = store._mutations
        const route = useRoute()
        const router = useRouter()
        const state = reactive({
            geohash: '',
            shopId: null,
            shopCart: null, // 当前订单购物车信息
            orderData: null,    // 订单信息
            chooseAddress: null,
            imgBaseUrl,
            showAlert: false,   // 提示框显隐
            alertText: null, // 提示文本
            skuCartList: [],
            shopInfo: null,
            remarks: null,
        })
        // 获取路由传递参数
        state.geohash = route.query.geohash
        state.shopId = Number(route.query.shopId)
        // 初始化购物车
        useMutation.INIT_BUYCART[0]()
        // 保存shopId
        useMutation.SAVE_SHOPID[0](state.shopId)
        // 获取当前店铺id对应的购物车信息
        state.shopCart = store.state.cartList[state.shopId]
        state.skuCartList = store.state.skuCartList;
        watch(() => [store.state.chooseAddress], (val1) => {
            state.chooseAddress = store.state.chooseAddress
        })
        watch(() => [store.state.inputText], (val1) => {
            state.remarks = store.state.inputText
        })
        const totalPrice = computed(() => {
            if (state.skuCartList == null) return 0;
            let num = 0;
            console.log('state.skuCartList', state.skuCartList)
            state.skuCartList.forEach((item) => {
                const fee = item.packingFee == null ? 0 : item.packingFee
                num += item.price * item.num + fee
            })
            const fleeFee = (state.shopInfo == undefined || state.shopInfo.floatDeliveryFee == undefined) ? 0 : state.shopInfo.floatDeliveryFee
            num += fleeFee
            return num.toFixed(2)
        })
        const totalPackingFee = computed(() => {
            if (state.skuCartList == null) return 0;
            let num = 0;
            console.log('state.skuCartList', state.skuCartList)
            state.skuCartList.forEach((item) => {
                const fee = item.packingFee == null ? 0 : item.packingFee
                num += fee
            })
            return num.toFixed(2)
        })
        const getspecsStrList = (sku) => {
            let str = "";
            if (sku.specsList instanceof Array) {
                console.log("数组")
                sku.specsList.forEach((item) => {
                    str += item + " ";
                })
            }
            return str;
        }

        onMounted(() => {
            if (state.geohash) {
                console.log("yes")
                initData()
            } else {
                console.log("error")
            }
        })
        const getshop = async () => {
            const shopResp = await getShopById(state.shopId);
            console.log(shopResp.data.data)
            state.shopInfo = shopResp.data.data;
        }
        const initData = async () => {
            initAddress()
            getshop();
        }
        const initAddress = async () => {
            state.remarks = store.state.inputText
            // if(store.state.userInfo && store.state.userInfo.id) {
            if (!store.state.userInfo) {
                router.push({ path: '/login', query: {} })
            }
            const addressResp = await getAddressListByCId(store.state.userInfo.id)
            if (addressResp.data.data instanceof Array && addressResp.data.data.length) {
                // 获取当前默认地址
                addressResp.data.data.forEach(item => {
                    if (item.isDefault) {
                        useMutation.CHOOSE_ADDRESS[0]({ address: item, index: 0 })
                    }
                })

            }
            // }
        }
        const confirmOrder = async () => {    // 订单确认
            const listData = state.skuCartList.map(item => {
                return {
                    "skuId": item['skuId'],
                    "num": item['num']
                }
            })
            const data = {
                list: listData,
                addressId: state.chooseAddress.id,
                customerId: store.state.userInfo.id,
                shopId: state.shopId,
                remarks: state.remarks,
            }
            let OrderResp = await saveOrderInfo(data)
            // useMutation.ORDER_SUCCESS[0](OrderResp.data.data)
            router.push({ path: '/confirmOrder/payment', query: { orderId: OrderResp.data.data }, replace: true })
        }
        const iconColor = (name) => {
            switch (name) {
                case '公司': return '#4cd964';
                case '学校': return '#3190e8';
            }
        }
        return {
            ...toRefs(state),
            initData,
            iconColor,
            confirmOrder,
            totalPrice,
            getshop,
            getspecsStrList,
            totalPackingFee
        }
    }
}
</script>

<style scoped>
.address_contsiner {
    height: 3.5rem;
    background: url(@/assets/image/address_bottom.png) left bottom repeat-x;
    background-color: #fff;
    background-size: auto .12rem;
}

.bg_tag {
    background-color: #ff5722;
}

.router-slid-enter-active,
.router-slid-leave-active {
    transition: all .4s;
}

.router-slid-enter-from,
.router-slid-leave-active {
    transform: translate3d(2rem, 0, 0);
    opacity: 0;
}
</style>