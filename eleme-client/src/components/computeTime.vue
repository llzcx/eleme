<template>
    <div>
        <span v-if="countTime >= 0" class="text-xxs text-orange-500 border border-orange-500 p-1 rounded" @click="gotoPay">{{remaining}}</span>
        <span v-else class="text-xxs text-orange-500 border border-orange-500 p-1 rounded">{{"订单超时"}}</span>
        <van-dialog v-model:show="showAlert">
            <template #default>
                <div class="flex flex-col items-center">
                    <van-icon size="82" class="m-2 text-yellow-400" name="warning-o" />
                    <span class="mb-2">{{alertText}}</span>
                </div>
            </template>
            <template #footer>
                <div class="bg-green-400">
                    <button class="w-full h-6 text-white" @click="showAlert=false">确认</button>
                </div>
            </template>
        </van-dialog>
    </div>
</template>

<script>
import { reactive, toRefs, onMounted, computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
export default {
    name: 'ComputeTime',
    props: ['time','order'],
    setup(props) {
    
        const router = useRouter()
        const store = useStore()
        const numTime = computed(() => {    // 处理订单返回时间
            // if(props.time.toString().indexOf('分钟') !== -1) {
            //     return parseInt(props.time)*60
            // }else{
            //     return parseInt(props.time);
            // }
            //获取当前日期
            const end_date = new Date();
            //获得传来的日期       
            const sta_date = new Date(props.time); 
            //获得日期差的秒数 返回
            return (end_date-sta_date)/1000;
        })
        const remaining = computed(() => {  // 格式化时间
            // let min = parseInt(state.countTime)/60;
            // let sec = parseInt(state.countTime)%60;
            let min = parseInt(state.countTime/60)
            let sec = parseInt(state.countTime%60)
            if(10 <= min < 10) {
                min = '0' + min
            }
            if(0 <= sec < 10) {
                sec = '0' + sec
            }
            return '去支付（还剩' + min + '分' + sec + '秒）'
        })
        const state = reactive({
            countTime: 600,
            showAlert: false,
            alertText: null,
            timer: null
        })
        onMounted(() => {
            state.countTime -= numTime.value    // 同步最新时间
            remainingTime()
        })
        const remainingTime = () => {
            clearInterval(state.timer)
            state.timer = setInterval(() => {
                state.countTime--
                if(state.countTime <= 0) {
                    clearInterval(state.timer)
                    // state.showAlert = true
                    // state.alertText = '支付超时'
                }
            }, 1000)
        }
        const gotoPay = () => {
            router.push({path:'/order/payment',query:{orderId:props.order.id}})
        }
        return {
            numTime,
            remaining,
            gotoPay,
            ...toRefs(state)
        }
    },

}
</script>

<style scoped>

</style>