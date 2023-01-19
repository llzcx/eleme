import { getOrder, getShopOrderList } from '@/api/business/order'
import { getResturantDetail } from '@/api/business/shop'
import { getUserInfo } from '@/api/business/user'
import { getAddressById } from '@/api/business/address'
import { getApiUrl } from '@/utils/utils'
export default {
  data() {
    return {
      form: { order: { id: '' }, user: { id: '', name: '' }, address: { name: '', tel: '', addressDetail: '' }, orderItems: [] },
      apiUrl: '',
      expressList: [],
      shipping: {
        show: false,
        id: '',
        idExpress: '',
        shippingSn: ''
      }
    }
  },
  created() {
    console.log(this.$route.query.id)
    const id = this.$route.query.id
    console.log('id', id)
    this.init(id)
  },
  methods: {
    init(id) {
      this.fetchData(id)
    },
    fetchData(id) {
      getShopOrderList(1, 20, null, null,id, null).then(response => {
        if(response.data.data.rows.length > 0){
          this.form = response.data.data.rows[0]
        }else{
          this.form = {}
        }
      }
      )
    },
    openSendOutForm() {
      // if (this.expressList.length == 0) {
      //   expressApi.queryAll().then(response => {
      //     this.expressList = response.data
      //   })
      // }
      // this.shipping.show = true
    },
    sendOut() {

    },
    formatPrice(price) {
      if (price) {
        return '￥' + price.toFixed (2)
      }
      return ''
    },
    printOrder() {
      this.$print(this.$refs.print)
    }

  }
}
