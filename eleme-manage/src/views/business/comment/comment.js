
import { getCommentList, addComment } from '@/api/business/comment'
import store from '@/store'
import ElImageViewer from 'element-ui/packages/image/src/image-viewer'
// this.$route.query 拿参数
// store.getters.shopInfo.id 拿vuex
export default {
	components: { ElImageViewer },
	data() {
		return {
			StateOptions: [
				{
					label: '已回复',
					value: true
				},
				{
					label: '未回复',
					value: false
				},
			],
			selectStateOptions: null,
			ImageOptions: [
				{
					label: '有图片',
					value: true
				},
				{
					label: '无图片',
					value: false
				},
			],
			selectImageOptions: null,
			params: {
				pagenum: 1,
				size: 20,
				shopId: null,
				mingRating: null,
				haveImg: false,
				cid: null,
				haveReply: null,
			},
			total: 0,
			commentList: [],
			listLoading: false,
			srcList: [],
			replyComment: false,
			replyValue: '',
			SelectedComment: null,

		}
	},
	watch: {
		//普通的watch监听
		selectStateOptions(newValue, oldValue) {
			console.log('selectStateOptions->newValue',newValue)
			if(newValue == null) {
				this.params.haveReply = null
			}else{
				this.params.haveReply = newValue[0]
			}
			
		},
		selectImageOptions(newValue, oldValue) {
			console.log('selectImageOptions->newValue',newValue)
			if(newValue == null) {
				this.params.haveReply = null
			}else{
				this.params.haveImg = newValue[0]
			}
		}
	},
	created() {
		this.initData()
	},
	computed: {

	},
	methods: {
		initData() {
			this.params.shopId = store.getters.shopInfo.id
			this.get()
		},
		async get() {
			const da = this.params
			console.table(da)
			this.listLoading = true
			const resp = await getCommentList(this.params)
			console.log('resp.data', resp.data)
			if (resp.data.success) {
				this.total = resp.data.data.totalCount
				this.commentList = resp.data.data.rows
			} else {

				this.commentList = []
			}
			this.listLoading = false
		},
		getRowKeys(row) {
			return row.index
		},
		handleViewImgList(item) {

		},
		handleReply(item) {
			this.replyComment = true
			this.SelectedComment = item
			this.replyValue = ''

		},
		handleViewOrder(row) {
			this.$router.push({ path: '/business/orderdetail', query: { id: row.orderId } })
		},
		async confirmReply() {
			const data = {
				content: this.replyValue,
				shopId: store.getters.shopInfo.id,
				customerId: this.SelectedComment.customerId,
				orderId: this.SelectedComment.orderId,
				isCustomer: false,
				parent: this.SelectedComment.id,
			}
			addComment(data).then(resp => {
				if (resp.data.success) {
					this.$message({
						message: '回复成功',
						type: 'success'
					})
					this.replyValue = ''
					this.replyComment = false
					this.get()
				} else {
					this.$message({
						message: resp.data.errorMsg,
						type: 'error'
					})
				}
			})
		},
		fetchNext() {
			this.params.pagenum = this.params.pagenum + 1
			this.get()
		},
		fetchPrev() {
			this.params.pagenum = this.params.pagenum - 1
			this.get()
		},
		changeSize(limit) {
			this.params.size = limit
			this.get()
		},
		search() {
			this.get()
		},
		reset() {
			this.params = {
				pagenum: 1,
				size: 20,
				shopId: null,
				mingRating: null,
				haveImg: false,
				cid: null,
				haveReply: null,
			},
			this.params.shopId = store.getters.shopInfo.id
			this.selectImageOptions = null
			this.selectStateOptions = null
			this.get()
		},
		fetchPage(pagenum) {
			this.params.pagenum = pagenum
			this.get()
		},
	},
}