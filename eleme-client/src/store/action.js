import { getUser } from "@/api/login/login"
import { getAddressList } from '@/api/address/address'
import { GET_USERINFO, GET_ADDRESS, BUY_AGAIN } from './mutation-types.js'
import {updateAddress,deleteAddress,saveAddress,getAddressByCid} from '@/api8080/profile'
export default {
    // 获取用户信息
    async getUserInfo({ commit }) {
        // 通过存储的用户ID获取用户登录信息
        let res = await getUser()
        res = res.data
        // 将用户信息存储至vuex
        commit(GET_USERINFO, res)
    },
    // 获取地址数据
    async getAddress({commit, state}) {
        if(state.addressList.length > 0) return
        let addrRes = await getAddressByCid(state.userInfo.id)
        addrRes = addrRes.data.data
        commit(GET_ADDRESS, addrRes)
    },
    async BuyAgain({commit,state}){
        
    }
}