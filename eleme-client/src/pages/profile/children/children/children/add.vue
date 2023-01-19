<template>
    <div class="absolute top-0 right-0 bottom-0 right-0 w-full bg-gray-200 z-50">
        <head-top head-title="新增地址" :go-back='true'></head-top>
        <section class="bg-white mt-10">
            <div class="px-2 pt-2 text-xxs">
                <van-form @submit="onSubmit">
                    <van-cell-group inset>
                        <van-field v-model="name" name="姓名" label="姓名" placeholder="您的名字"
                            :rules="[{ required: true, message: '请填写姓名' }]" />
                        <van-field disabled="true" @click="gotoAddDetail()" v-model="area" name="地址" label="地址"
                            placeholder="小区/写字楼/街道等" :rules="[{ required: true, message: '请填写地址' }]" />
                        <van-field disabled="true" v-model="geohash" name="经纬度" label="经纬度" />
                        <van-field v-model="detailAddress" name="详细地址" label="详细地址" placeholder="详细地址"
                            :rules="[{ required: true, message: '请填写详细地址' }]" />
                        <van-field type="tel" v-model="phone" name="电话" label="电话" placeholder="您的联系电话"
                            :rules="[{ required: true, message: '请填写电话' }]" />
                        <van-field name="radio" label="性别">
                            <template #input>
                                <van-radio-group v-model="sex" direction="horizontal">
                                    <van-radio name="先生">先生</van-radio>
                                    <van-radio name="女士">女士</van-radio>
                                </van-radio-group>
                            </template>
                        </van-field>
                        <van-field name="radio" label="标签">
                            <template #input>
                                <van-radio-group v-model="tag" direction="horizontal">
                                    <van-radio name="家">家</van-radio>
                                    <van-radio name="学校">学校</van-radio>
                                </van-radio-group>
                            </template>
                        </van-field>
                        <van-field name="switch" label="是否为默认地址">
                            <template #input>
                                <van-switch v-model="isDefault" size="20" />
                            </template>
                        </van-field>
                    </van-cell-group>
                    <div style="margin: 16px;">
                        <van-button @click.prevent="addAddress" round block type="primary" native-type="submit">
                            新增地址
                        </van-button>

                    </div>
                </van-form>
            </div>
        </section>

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
import { computed, reactive, toRefs } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'
import HeadTop from '@/components/HeadTop'
import { updateAddress, deleteAddress, addNewAddress } from '@/api8080/address'
export default {
    components: {
        HeadTop
    },
    setup() {
        const store = useStore()
        const router = useRouter()
        const useMutation = store._mutations
        const btnStatus = computed(() => {
            if (state.name && area && state.detailAddress && state.phone && state.sex && state.tag ) {
                return true
            } else {
                return false
            }
        })
        const area = computed(() => {
            return store.state.addAddress
        })
        const geohash = computed(() => {
            return store.state.addgeohash
        })
        const state = reactive({
            name: '',   // 姓名
            // area: '',   // 地区
            address: '',    // 地址
            detailAddress: '',
            lon: '123', //经度
            lat: '12',//纬度
            phone: '',  // 联系方式
            backup_phone: '',    // 备用联系方式
            warn: false,
            addressWarnText: '',
            phoneWarnText: '',
            showAlert: false,
            alertText: '',
            sex: '先生',
            tag: '家',
            // checked:'',
            isDefault: false,
        })
        const handleWarn = (type) => {
            if (type == 'address') {
                if (state.address.length == 0) {
                    state.warn = true
                    state.addressWarnText = '请填写详细送餐地址'
                }
                else if (state.address.length > 0 && state.address.length <= 2) {
                    state.warn = true
                    state.addressWarnText = '送餐地址太短无法识别'
                }
                else {
                    state.warn = false
                    state.addressWarnText = ''
                }
            }
            else if (type == 'phone') {
                if ((/^[1][358][0-9]{9}$/).test(state.phone)) {
                    state.warn = false
                } else if (state.phone == '') {
                    state.warn = true
                    state.phoneWarnText = '手机号不能为空'
                } else {
                    state.warn = true
                    state.phoneWarnText = '请输入正确的手机号'
                }
            }
        }
        const addAddress = async () => {
            if (btnStatus) {
                const data = {
                    name: state.name,
                    cid: store.state.userInfo.id,
                    phone: state.phone,
                    geohash: geohash.value,
                    address:area.value,
                    specificAddress: state.detailAddress,
                    isDefault: state.isDefault,
                    tag: state.tag,
                    sex: state.sex=='先生'?true:false,
                }
                let res = await addNewAddress(data);

                if (!res.data.success) {
                    state.showAlert = true
                    state.alertText = res.data.errorMsg
                } else {
                    useMutation.ADD_ADDRESS[0](data)
                    router.go(-1)
                }
            } else {
                return
            }
        }
        const gotoAddDetail = () => {
            router.push({ path: '/profile/info/address/add/addDetail', query: {} })
        }
        return {
            ...toRefs(state),
            btnStatus,
            area,
            handleWarn,
            addAddress,
            gotoAddDetail,
            geohash
        }
    }
}
</script>

<style scoped>

</style>