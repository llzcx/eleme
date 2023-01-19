<template>
	<div>
		<div style="background-color: aliceblue;" class="bg-gray-50 chat-out">
			<head-top v-if="ShopDetail != null" :head-title="ShopDetail.name" :go-back='true'></head-top>
			<head-top v-else head-title="null" :go-back='true'></head-top>
			<div class="chat-main">
				<!-- 聊天内容 -->
				<div v-if="loveArray.length" class="content mt-10">
					<ul>
						<li v-if="loveArray.length" v-for="(item, index) in loveArray" :key="index"
							:class="'text-' + item.class" :style="{ '--url': item.url }">
							<span>{{ item.text }}</span>
						</li>
					</ul>
				</div>
				<div v-else>
					暂无聊天内容~
				</div>
			</div>
			<div>
				<section style="height:100px"
					class="fixed left-0 right-0 bottom-0 flex w-full z-50 bg-white text-gray-400">
					<div style="width:300px">
						<van-field size="small" v-model="msgValue" placeholder="请输入发送的内容" />
					</div>
					<van-button @click="sendMsg()" type="success">发送消息</van-button>
				</section>
			</div>
		</div>
	</div>
</template>


<script>
import { reactive, toRefs, onMounted, ref, watch, getCurrentInstance, onUnmounted } from 'vue'
import { useStore } from 'vuex'
import HeadTop from '@/components/HeadTop'
import { getMsgList } from '@/api8080/msg'
import { useRouter } from 'vue-router'
export default {
	components: {
		HeadTop
	},
	setup() {
		const store = useStore()
		const router = useRouter()
		if (!store.state.userInfo) {
			router.push({ path: '/login', replace: true })
		}

		var webSocket = new WebSocket('ws://localhost:8080/boot/websocket/customer' + store.state.userInfo.id)
		webSocket.onopen = function () {
			console.log('webSocket连接创建。。。');
		}
		webSocket.onclose = function () {

		}
		webSocket.onmessage = function (event) {
			console.log("收到消息", event.data)
			const data = event.data
			let el = JSON.parse(data)
			let node = {
				url: 'url(' + el.imagePath + ')',
				text: el.content,
				class: el.customerSender == true ? 'right' : 'left',
				createTime: el.createTime
			}
			state.loveArray.push(node)
			console.log('state.loveArray', state.loveArray)
		}
		webSocket.onerror = function (event) {
			console.log('webSocket连接异常。。。');
		}
		const internalInstance = getCurrentInstance()

		const state = reactive({
			//格式要求{url: 'url(https://ts3.cn.mm.bing.net/th?id=OIP-C.ZkoPhpKfJwsvGmpm8RsragHaFp&w=286&h=218&c=8&rs=1&qlt=90&o=6&dpr=1.7&pid=3.1&rm=2)',class: 'left',text: '你好',}
			loveArray: [],
			actions: '',
			showPopover: true,
			onSelect: '',
			message: true,
			msgValue: '',
			isRefreash: false,
			ShopDetail: null,
		})
		watch(() => state.loveArray, (value) => {
			console.log('监听成功，输出value', value) // 监听成功，输出 { name: 'jake' }
			state.loveArray = value
			// state = value
		})

		onMounted(() => {
			initData()
		})
		onUnmounted(() => {
			webSocket.close();
		});
		const initData = async () => {
			state.ShopDetail = store.state.shopDetail
			console.log('ShopChat:state.ShopDetail', state.ShopDetail)
			let resp = await getMsgList(1, 20, store.state.userInfo.id, state.ShopDetail.id)
			state.loveArray = resp.data.data.rows.map((item) => {
				return {
					"text": item['content'],
					"url": 'url(' + item['imagePath'] + ')',
					"createTime": item['createTime'],
					"class": item['customerSender'] == true ? 'right' : 'left',
				}
			})
		}
		const sendMsg = async () => {
			if (state.ShopDetail == null || store.state.userInfo == null) {
				router.push('/login')
				return
			}
			const data = {
				identity: 'customer',
				businessId: state.ShopDetail.id,
				customerId: store.state.userInfo.id,
				content: state.msgValue,
			}
			webSocket.send(JSON.stringify(data))
			state.msgValue = ''
		}
		const onRefresh = async () => {

		}

		return {
			...toRefs(state),
			sendMsg,
			onRefresh
		}
	}
}
</script>

<style scoped>
.chat-main {
	margin: 0;
	margin-top: 50px;
	padding: 0;
	width: 100%;
	height: 780px;
	overflow: auto;
	font-size: 16px;
	position: relative;
	background-color: #ededed;
	box-shadow: 0 0 10px 3px rgba(0, 0, 0, 0.2);
}

.chat-main .tar .tar_back {
	background-color: #ededed;
	box-shadow: 0px 1px 1px 0px rgba(212, 212, 212, 0.6);
}

.van-popover__wrapper {
	width: 100%;
}

.chat-main ul {
	padding: 20px 45px;
	list-style: none;

}

.chat-main li {
	padding: 10px 10px 10px 10px;
	margin-bottom: 5px;
	position: relative;
	/* 让页面内的文字不可被选中 */
	-webkit-user-select: none;
	-moz-user-select: none;
	-o-user-select: none;
	user-select: none;
}

.chat-main li span {
	display: inline-block;
	border-radius: 7px;
	background: #a6e860;
	padding: 6px 10px 8px 10px;
	max-width: 88%;
	word-wrap: break-word;
}

.chat-main li.text-left span {
	background: white;

}

/* 伪元素类,在文本框前面加上头像 */
.chat-main li.text-left:before {
	content: "";
	width: 38px;
	height: 38px;
	border-radius: 3px;
	display: block;
	background-image: var(--url);
	background-size: 100%;
	position: absolute;
	left: -40px;
	top: 10px;
}

/* 伪元素类 加上气泡框的角 */
.chat-main li.text-left span:after {
	content: "";
	width: 0px;
	height: 0px;
	display: block;
	/* transparent 透明色 */
	border: 7px solid transparent;
	/* 在右边生成一个白色的小三角 */
	border-right: 7px solid white;
	position: absolute;
	left: -3px;
	top: 12px;
}

.text-right {
	text-align: right;
}

.text-right span {
	text-align: left;
}

.chat-main li.text-right:after {
	content: '';
	width: 38px;
	height: 38px;
	border-radius: 3px;
	display: block;
	background-image: var(--url);
	background-size: 100%;
	position: absolute;
	right: -38px;
	top: 10px;
}

.chat-main li.text-right span:after {
	content: "";
	width: 0px;
	height: 0px;
	display: block;
	border: 7px solid transparent;
	border-left: 7px solid #a6e860;
	position: absolute;
	right: -3px;
	top: 11px;
}

.changeButton {
	text-align: center;
}

.chat-main p {
	/* text-align: center; */
	margin-left: 70px;
	font-size: 12px;
	color: rgb(133, 131, 131);
}

.chat-main .button {
	margin: 50px auto;
	border-radius: 8px;
}

.mt-10 {
	margin-top: 10px;
	margin-bottom: 20px;
}

.chat-out {
	height: 780px;
	overflow: auto;
}
</style>