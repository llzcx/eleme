import {
	addWord, deleteWord, getWordList
} from '@/api/business/word'
import { getResturantDetail } from '@/api/business/shop'
import { getUserInfo } from '@/api/business/user'
import { getAddressById } from '@/api/business/address'
import store from '@/store'
export default {
	data() {
		return {
			List: [],
			inputVisible:false,
			inputValue:'',
		}
	},
	computed: {

	},
	created() {
		this.init()
	},

	methods: {
		async init() {
			const resp = await getWordList()
			this.List = resp.data.data
		},
		async handleClose(tag) {
			const resp = await deleteWord(tag)
			if(resp.data.success){
				this.List.splice(this.List.indexOf(tag), 1)
				this.$message({
					message: '删除成功',
					type: 'sucess'
				})
			}else{
				this.$message({
					message: '删除失败',
					type: 'sucess'
				})
			}
		},
		showInput() {
			this.inputVisible = true;
			this.$nextTick(_ => {
				this.$refs.saveTagInput.$refs.input.focus();
			});
		},

		async handleInputConfirm() {
			const inputValue = this.inputValue
			const resp = await addWord(inputValue)
			if(resp.data.success){
				if (inputValue) {
					this.List.push(inputValue);
				}
				this.inputVisible = false;
				this.inputValue = '';
				this.$message({
					message: '添加成功',
					type: 'sucess'
				})
			}else{
				this.$message({
					message: '添加失败',
					type: 'sucess'
				})
			}
		}
	}
}
