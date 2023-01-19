import { getApiUrl } from '@/utils/utils'
import { getToken } from '@/utils/auth'
import {
	getMsgList, getChatObjectList
} from '@/api/business/msg'
import store from '@/store'
export default {
	data() {
		return {
			loveArray: [],
			chatObject: [],
			form: null,
			selectObject: null,
			inputValue: '',
			webSocket:null,
		}
	},
	created() {
		this.initData()
	},
	computed: {

	},
	methods: {
		async initData() {
			const me = this
			//先用商家的账号连接一下websocket
			this.webSocket = new WebSocket('ws://localhost:8080/boot/websocket/business'+store.getters.shopInfo.id)
			this.webSocket.onopen = function () {
				console.log('webSocket连接创建中...');
			}
			this.webSocket.onclose = function () {

			}
			this.webSocket.onmessage = function (event) {
				console.log("收到消息", event.data)
				const data = event.data
				let el = JSON.parse(data)
				let node = {
					url: 'url(' + el.imagePath + ')',
					text: el.content,
					class: el.customerSender === false ? 'right' : 'left',
					createTime: el.createTime
				}
				console.log('改变前:this.loveArray', me.loveArray)
				me.loveArray.push(node)
				console.log('this.loveArray', me.loveArray)
			}
			this.webSocket.onerror = function (event) {
				console.log('webSocket连接异常。。。');
			}
			//获取可以聊天的对象
			console.log('shopInfo[获取可以聊天的对象]',store.getters.shopInfo)
			let resp1 = await getChatObjectList(store.getters.shopInfo.id)
			this.chatObject = resp1.data.data.map((item) => {
				return {
					'name': item['name'],
					'img': item['avatar'],
					'id': item['id'],
				}
			})

		},
		//用户点击事件
		async pullData(item) {
			this.loveArray = []
			console.log("点击", item.id)
			this.form = item
			let resp = await getMsgList(1, 20, item.id, store.getters.shopInfo.id)
			this.loveArray = resp.data.data.rows.map((item) => {
				return {
					"text": item['content'],
					"url": 'url(' + item['imagePath'] + ')',
					"createTime": item['createTime'],
					"class": item['customerSender'] == false ? 'right' : 'left',
				}
			})
		},
		//点击发送事件
		sendMsg() {
			if (this.form==null) {
				this.$message({
					type: 'error',
					message: '未选择发送人'
				})
				return
			}
			const data = {
				identity: 'business',
				businessId: store.getters.shopInfo.id,
				customerId: this.form.id,
				content: this.inputValue,
			}
			this.webSocket.send(JSON.stringify(data))
		}
	}

}



