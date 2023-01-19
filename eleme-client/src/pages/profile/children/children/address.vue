<template>
    <div class="absolute top-0 right-0 bottom-0 right-0 w-full bg-gray-200 z-50">
        <head-top head-title="编辑地址" :go-back='true'>
            <template #edit>
                <span class="absolute right-2 top-1/2 -translate-y-1/2 text-1xs text-white" @click="handleEdit">
                    {{ editText }}
                </span>
            </template>
        </head-top>
        <section class="mt-10 bg-white">
            <ul v-if="List">
                <li v-for="(item, index) in List" :key="index" class="border-b"
                    :class="current == index ? 'bg-yellow-100' : ''" @click="changeAddress(index)">
                    <div class="flex justify-between items-center px-2">
                        <div class="address-item">
                            <div class="address-item-left">
                                <div class="address-item-left-top">
                                    <span class="shouhuoren">{{ item.name }}</span>
                                    <span class="shouhuoren-sex">{{ (item.sex == true ? "先生" : "女士") }}</span>
                                    <span class="shouhuoren-phone">{{item.phone}}</span>
                                    <div class="address-default" v-if="item.isDefault">默认</div>
                                </div>
                                <div class="address-item-left-bottom">
                                    <span class="addressA">{{ item.address+''+item.specificAddress}}</span>
                                </div>
                            </div>
                            <div class="address-item-right">

                            </div>
                        </div>
                        <div class="flex flex-col">


                            <!-- <span class="text-xxs mb-1">电话:{{item.phone}}</span> -->
                        </div>
                        <van-icon v-if="showDelete" class="text-gray-400" name="cross"
                            @click="delAddress(item, index)" />
                        <button v-else class="setDef" @click="setDef(item)">设为默认</button>
                    </div>
                </li>
            </ul>
            <router-link to="/profile/info/address/add"
            class="flex justify-between items-center bg-white text-1xs mt-2 px-2 py-1">
            <span>新增地址</span>
            <van-icon class="text-gray-500" name="arrow" />
        </router-link>
        </section>

        <transition name="router-slid" mode="out-in">
            <router-view></router-view>
        </transition>
    </div>
</template>

<script>
import { reactive, toRefs, onMounted, watch, computed } from 'vue'
import { useStore } from 'vuex'
// import { deleteAddress } from '@/api/address/address'
import HeadTop from '@/components/HeadTop'
import { addNewAddress, deleteAddress, getAddressListByCId, updateAddress,setDefault } from '@/api8080/address'
export default {
    components: {
        HeadTop
    },
    setup() {
        const store = useStore()
        const useAction = store._actions
        const state = reactive({
            current: 0, // 当前选中地址索引
            editText: '编辑',
            showDelete: false,
            List: [],
            disabledList: [],
            chosenAddressId: null,
        })
        onMounted(() => {
    
            initData()
        })
        const initData = () => {
            if (store.state.userInfo && store.state.userInfo.id) {
                //调用异步方法action 存储地址列表到vuex的storage当中
                // useAction.getAddress[0]()
                getAddressLisst()
            }
        }
        const getAddressLisst = () => {
            getAddressListByCId(store.state.userInfo.id).then(resp => {
                state.List = resp.data.data
                console.log("地址列表:", state.List)
            })
        }
        const changeAddress = (index) => {
            state.current = index
        }
        const handleEdit = () => {
            if (state.editText === '编辑') {
                state.editText = '完成'
                state.showDelete = true
            } else {
                state.editText = '编辑'
                state.showDelete = false
            }
        }
        const delAddress = async (item, index) => {
            if (store.state.userInfo && store.state.userInfo.id) {
                await deleteAddress(item.id)
                state.List.splice(index, 1)
            }
        }
        const onAdd = () => {

        }
        const onEdit = () => {

        }
        const setDef = (item)=>{
            setDefault(item.id).then(resp=>{
                if(resp.data.success){
                    // this.$notify('修改成功!');
                    // Notify({ type: 'success', message:  });
                }
            })
            initData()
        }
        return {
            ...toRefs(state),
            changeAddress,
            handleEdit,
            delAddress,
            onAdd,
            onEdit,
            setDef
        }
    }
}
</script>

<style scoped>
.router-slid-enter-active,
.router-slid-leave-active {
    transition: all .4s;
}

.router-slid-enter-from,
.router-slid-leave-active {
    transform: translate3d(2rem, 0, 0);
    opacity: 0;
}


.address-item {
    display: flex;
}

.address-item-left {
    flex: 8;
}

.address-item-left-top {
    display: flex;
}

.shouhuoren {
    color: rgb(0, 0, 0);
    font-weight: 500;
    font-size: 17px;
    white-space:nowrap;
}
.shouhuoren::before{
    content: "收货人:";
    
}
.shouhuoren-sex{
    white-space:nowrap;
    color: rgb(0, 0, 0);
    font-weight: 500;
    font-size: 17px;
    margin-left: 10px;

}
.address-default{
    content: "默认";
    background-color: rgb(77, 190, 231);
    white-space:nowrap;
    color: white;
    font-size: 12px;
    width: 30px;
    height: 17px;
    margin-left: 20px;
    margin-top: 4px;
    border-radius: 8px;
    text-align: center;
}
.shouhuoren-phone{
    color: rgb(0, 0, 0);
    font-weight: 500;
    font-size: 17px;
    margin-left: 10px;
}
.address-default{

}
.address-item-left-bottom {

}
.addressA {
    width: 270px;
    color: rgb(0, 0, 0);
    font-weight: 300;
    font-size: 17px;
    white-space:nowrap;
    text-overflow:ellipsis;
    overflow:hidden;
    display: inline-block;
}
.addressA::before{
    content: "地址:";
}

.address-item-right {
    flex: 2;
}
.setDef{
    border-radius: 8px;
    font-weight: 550;
    color: aliceblue;
    font-size: 5px;
    background-color: rgb(89, 162, 219);
}
</style>