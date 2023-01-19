<template>
	<div class="fixed top-0 right-0 bottom-0 right-0 bg-gray-200 w-full z-50">
		<head-top head-title="评论商品" :go-back="true"></head-top>
		<div class="mt-10">
			<van-form @submit="onSubmit">
			<van-field v-model="submit.content" rows="20" autosize label="评论" type="textarea" maxlength="600"
				placeholder="请输入评论" show-word-limit />
			<van-field name="uploader" label="图片上传">
				<template #input>
					<van-uploader v-model="imageList" :after-read="afterRead" />
				</template>
			</van-field>
			<van-field name="rate" label="评分">
				<template #input>
					<van-rate v-model="submit.rating" />
				</template>
			</van-field>
			<div style="margin: 16px;">
				<van-button round block type="primary" native-type="submit">
					提交
				</van-button>
			</div>
		</van-form>
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
import { payForOrder, zhifubaoPay, getPayInfo } from '@/api8080/order'
import { addComment, getCommentList } from '@/api8080/comment'
import HeadTop from '@/components/HeadTop'
import { useStore } from 'vuex'
import { useRoute, useRouter } from 'vue-router'
import { reactive, toRefs, onMounted, onUnmounted } from 'vue'
import Axios from 'axios'
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
			imageList:[],
			submit: {
				imgList: [
					// { url: 'https://unpkg.com/@vant/assets/leaf.jpeg' },
					// Uploader 根据文件后缀来判断是否为图片文件
					// 如果图片 URL 中不包含类型信息，可以添加 isImage 标记来声明
				],
				content: '',
				shopId: '',
				customerId: '',
				orderId: '',
				content: '',
				rating: '',
				isCustomer:true,
			},
			showAlert:false,
			alertText:'',

		})
		onMounted(() => {
			initData()
		})
		onUnmounted(() => {

		})
		const initData = () => {
			console.log(store.state.userInfo)
			if (store.state.userInfo != null)
				state.submit.customerId = store.state.userInfo.id
			state.submit.orderId = route.query.orderId
		}
		const onSubmit = () => {
			addComment(state.submit).then(resp=>{
				if(resp.data.success){
					state.showAlert = true
					state.alertText = "评论成功"
					router.push({replace:true,path:'/order'})
				}
			})
		}
		const afterRead = async (file) => {
			if(file==null) {
				console.log("file is null")
				return;
			}
			let data = new FormData()
			data.append('file', file.file)
			console.table(data.get('file'))
			try {
				let response = await Axios({
					url: 'http://127.0.0.1:8080/boot/customer/image',
					method: 'POST',
					headers: { 'content-type': 'multipart/form-data' },
					data
				})
				console.log('response.data',response.data)
				let res = response.data
				if (res.success) {
					state.submit.imgList.push(res.data)
				}
			} catch (err) {
				state.showAlert = true
				state.alertText = '上传失败'
				throw new Error(err)
			}
		}
		const handleConfirm = ()=>{
			state.showAlert = false
            if (state.gotoOrder) {
                router.push('/order')
            }
		}
		return {
			...toRefs(state),
			onSubmit,
			afterRead,
			handleConfirm
		}
	}

}
</script>