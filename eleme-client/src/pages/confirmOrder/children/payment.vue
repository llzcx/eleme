<template>
    <div class="fixed top-0 right-0 bottom-0 right-0 bg-gray-200 w-full z-50">
        <head-top head-title="在线支付" :go-back="true"></head-top>
        <section class="flex flex-col mt-8 bg-white p-4 text-center">
			<span v-if="payDetail" class="text-xl">待支付:{{payDetail.totalPrice}}元</span>
            <span v-else class="text-xl">{{error}}</span>
			<span>{{"  "}}</span>
			<span>{{"  "}}</span>
            <span class="text-gray-400 text-1xs">剩余支付时间</span>
            <span class="text-xl">{{ remaining }}</span>
        </section>
        <span class="inline-block text-gray-500 text-1xs ml-2 my-1">选择支付方式</span>
        <section class="bg-white pl-2 divide-y text-1xs text-gray-500">
            <div class="flex justify-between items-center py-2">
                <div class="flex items-center">
                    <img class="w-8 h-8" src="@/assets/image/zhifubao.png" />
                    <span class="ml-2">支付宝</span>
                </div>
                <svg class="w-4 h-4 mr-2" @click="payWay = 1" :style="payWay == 1 ? 'fill: #4cd964' : 'fill: #cccccc'">
                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#select"></use>
                </svg>
            </div>
            <div class="flex justify-between items-center py-2">
                <div class="flex items-center">
                    <svg class="w-8 h-8">
                        <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#weixin"></use>
                    </svg>
                    <span class="ml-2">微信</span>
                </div>
                <svg class="w-4 h-4 mr-2" @click="payWay = 2" :style="payWay == 2 ? 'fill: #4cd964' : 'fill: #cccccc'">
                    <use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#select"></use>
                </svg>
            </div>
        </section>
        <div class="mt-4 mx-2">
            <button class="w-full bg-green-400 py-1 rounded text-white" @click="confrimPay">确认支付</button>
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
                    <button class="w-full h-6 text-white" @click="handleConfirm">确认</button>
                </div>
            </template>
        </van-dialog>
    </div>
</template>

<script>
import { reactive, toRefs, onMounted, computed, onUnmounted } from 'vue'
import { payForOrder, zhifubaoPay, getPayInfo } from '@/api8080/order'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import HeadTop from '@/components/HeadTop'
import { Dialog } from 'vant'
export default {
    components: {
        HeadTop
    },
    setup() {
        const store = useStore()
        const router = useRouter()
        const route = useRoute()
        const useMutation = store._mutations
        const state = reactive({
            payDetail: {
				totalPrice:0,
			},    //  付款信息
            showAlert: false,
            alertText: null,
            countNum: 600,  // 倒计时
            payWay: 1, //付款方式
            gotoOrder: false,
            confirmAlert: true,
        })

        const initData = async () => {
            //检查是否已经支付完成

            //拉取剩余时间
            let resp = await getPayInfo(route.query.orderId)
            
            if(!resp.data.success || resp.data.data.orderState != 102){
                router.push(-1)
            }
			console.table(resp.data.data)
            state.payDetail = resp.data.data
            console.log("resp.data.data", resp.data.data)
            let time = resp.data.data.createTime
            //获取当前日期
            const end_date = new Date();
            //获得传来的日期       
            const sta_date = new Date(time);
            //获得日期差的秒数 返回
            state.countNum = 600 - parseInt((end_date - sta_date) / 1000);
            console.log('sta_date', sta_date)
            console.log('end_date', end_date)
            console.log('state.countNum', state.countNum)

        }
        initData()
        // 模拟支付完成，清空当前店铺对应购物车数据结构
        if (store.state.shopid) {
            useMutation.CLEAR_CART[0](store.state.shopid)
        }
        onMounted(() => {
            remainingTime() // 倒计时
        })
        onUnmounted(() => {
            clearInterval(state.timer)
        })
        const remainingTime = () => {
            clearInterval(state.timer)
            state.timer = setInterval(() => {
                state.countNum--
                if (state.countNum <= 0) {
                    clearInterval(state.timer)
                    state.showAlert = true
                    state.alertText = '支付已超时'
                    router.push({path:"/order",replace:true})
                }
            }, 1000)
        }

        const remaining = computed(() => {
            let min = parseInt(state.countNum / 60)
            if (min < 10) {
                min = '0' + min
            }
            let sec = parseInt(state.countNum % 60)
            if (sec < 10) {
                sec = '0' + sec
            }
            return '00: ' + min + ' : ' + sec
        })
        const confrimPay = () => {  // 确认订单
            state.showAlert = true
            // state.alertText = '当前环境无法支付，请打开官方APP进行付款'
            // const resp = await zhifubaoPay(store.state.orderMessage.id, state.payDetail.totalPrice)
            let str = ""
            str += "&outTradeNo=" + route.query.orderId
            str += "&subject=" + 'subject-str'
            str += "&totalAmount=" + state.payDetail.totalPrice
            str += "&body=" + "body-str"
            window.open('localhost:8080/boot/pay.html?' + str);
            // setTimeout(()=>{
            // tempwindow.location='localhost:8080/boot/pay.html?'+str;

            // }, 1000);
            // let form = document.createElement('form');
            // form.action = 'localhost:8080/boot/order/alipay';
            // form.target = '_blank';

            // form.method = 'POST';

            // document.body.appendChild(form);
            // form.submit();

            const beforeClose = (action) =>
                new Promise((resolve) => {
                    if (action === 'confirm') {
                            state.showAlert = true
                            state.alertText = '支付成功!'
                            resolve(true)
                            router.push({path:"/order",replace:true})
                    } else {
                        state.showAlert = true
                        state.alertText = '请尽快完成支付!'
                        router.push({path:"/order",replace:true})
                        resolve(true);
                    }
                });
            Dialog.confirm({
                title: '提示',
                message: '是否已经完成了支付?',
                beforeClose,
            });

            // if (resp.data.success) {
            //     state.alertText = '支付成功!'
            //     state.gotoOrder = true
            // } else {
            //     state.alertText = '支付失败!'
            // }

        }
        const handleConfirm = () => {
            state.showAlert = false
            if (state.gotoOrder) {
                router.push({path:"/order",replace:true})
            }
        }
        const beforeClose = (action) =>
            new Promise((resolve) => {
                setTimeout(() => {
                    if (action === 'confirm') {
                        resolve(true);
                    } else {
                        // 拦截取消操作
                        // resolve(false);
                    }
                }, 1000);
            });

        return {
            ...toRefs(state),
            remaining,
            confrimPay,
            handleConfirm,
            beforeClose
        }
    }
}
</script>

<style scoped>

</style>