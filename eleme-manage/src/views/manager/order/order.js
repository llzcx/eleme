import {
  getList, updateOrderStatus, getShopOrderList,
  orderFinish, CancelOrderReceiving, orderReceiving, orderDispatching
} from '@/api/business/order'
import { getResturantDetail } from '@/api/business/shop'
import { getUserInfo } from '@/api/business/user'
import { getAddressById } from '@/api/business/address'
import permission from '@/directive/permission/index.js'
import store from '@/store'
export default {
  directives: { permission },
  data() {
    return {
      listQuery: {
        pagenum: 1,
        size: 20,
        key: null,
        cid: null,
        oid: null,
        shopId: null,
        StateOptions:[
          {
            label:'已支付',
            value:101
          },
          {
            label:'正在制作',
            value:203
          },
          {
            label:'正在派送',
            value:202
          },
          {
            label:'交易完成',
            value:304
          }
        ],
        selectedState:null,
      },
      total: 0,
      list: null,
      listLoading: true,
      selRow: {},
      expandRowData: [],
      selectTrackOrder:null,
      trackOrderShow:false,
      trackActive:0,
    }
  },
  computed: {

  },
  created() {
    this.init()
  },
  
  methods: {
    init() {
      this.fetchData()
    },
    fetchData() {
      this.list = []
      this.listLoading = true
      console.log("selectedState",this.listQuery.selectedState)
      getShopOrderList(this.listQuery.pagenum, this.listQuery.size,null,
        this.listQuery.cid, this.listQuery.oid, this.listQuery.shopId
        ,this.listQuery.selectedState).then(response => {
          let list = response.data.data.rows
          console.log(response)
          list.forEach((item, index) => {
            item.index = index
            //处理精度
            item.totalPrice = item.totalPrice.toFixed(2)
            item.expectedTime = item.expectedTime===null?'立即送达':item.expectedTime
          })
          this.list = list
          this.listLoading = false
          this.total = response.data.data.totalCount
        })
    },
    search() {
      this.fetchData()
    },
    reset() {
      this.listQuery = {
        pagenum: 1,
        size: 20,
        key: null,
        cid: null,
        oid: null,
        shopId: null,
        StateOptions:[
          {
            label:'已支付',
            value:101
          },
          {
            label:'正在制作',
            value:203
          },
          {
            label:'正在派送',
            value:202
          },
          {
            label:'交易完成',
            value:304
          }
        ],
        selectedState:null
      }
      this.fetchData()
    },
    async expandRow(row, status) {
      if (status.length > 0) {
        this.expandRowData = []
        const restaurant = await getResturantDetail(row.restaurant_id);
        const userInfo = await getUserInfo(row.user_id);
        const addressInfo = await getAddressById(row.address_id);
        this.list.splice(row.index, 1, {
          ...row, ...{
            restaurant_name: restaurant.data.name,
            restaurant_address: restaurant.data.address,
            address: addressInfo.data.address,
            user_name: userInfo.data.username
          }
        });
        this.expandRowData.push(row.index);
      } else {
        this.expandRowData = []
      }


    },
    getRowKeys(row) {
      return row.index
    },
    handleFilter() {
      this.listQuery.pagenum = 1
      this.getList()
    },
    fetchNext() {
      this.listQuery.pagenum = this.listQuery.pagenum + 1
      this.fetchData()
    },
    fetchPrev() {
      this.listQuery.pagenum = this.listQuery.pagenum - 1
      this.fetchData()
    },
    fetchPage(pagenum) {
      this.listQuery.pagenum = pagenum
      this.fetchData()
    },
    changeSize(size) {
      this.listQuery.size = size
      this.fetchData()
    },
    handleUpdateOrderStatus(row, status) {
      this.$confirm('确定该操作?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        updateOrderStatus({ id: row.id, status: status }).then(response => {
          this.$message({
            message: '操作成功',
            type: 'sucess'
          })
          this.fetchData()
        })
      }).catch(() => {
      })
    },
    handleViewOrder(row) {
      this.$router.push({ path: '/business/orderdetail', query: { id: row.id } })
    },
    clear() {
      this.$confirm('确定清空数据?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        clear().then(response => {
          this.$message({
            message: '清空成功',
            type: 'sucess'
          })
          this.fetchData()
        })
      }).catch(() => {
      })
    },
    // 商家接单 将状态从 已支付状态 => 正在准备状态
    handleToReceiving(id) {
      this.$confirm('确定接下该订单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        orderReceiving(id).then(resp => {
          this.$message({
            message: '操作成功',
            type: 'sucess'
          })
          this.fetchData()
        })
      }).catch(() => {
      })
    },
    //商家取消订单 将订单状态:已支付状态 => 订单取消 售后状态:退款成功
    handleToCancelReceiving(id) {
      this.$confirm('确定取消该客户的订单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        CancelOrderReceiving(id).then(resp => {
          this.$message({
            message: '操作成功',
            type: 'sucess'
          })
          this.fetchData()
        })
      }).catch(() => {
      })
    },
    // 商家接单 将状态从 正在准备状态 => 正在派送
    handleToDispatch(id) {
      this.$confirm('确定派送该订单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        orderDispatching(id).then(resp => {
          this.$message({
            message: '操作成功',
            type: 'sucess'
          })
          this.fetchData()
        })
      }).catch(() => {
      })
    },
    //商家接单 将状态从 正在派送状态 => 订单完成
    handleToFinish(id) {
      this.$confirm('确定送达该订单?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        orderFinish(id).then(resp => {
          this.$message({
            message: '操作成功',
            type: 'sucess'
          })
          this.fetchData()
        })
      }).catch(() => {
      })
    },
    handleOrderTrack(row){
      this.selectTrackOrder = row
      this.trackOrderShow = true
    },
    getStateValue(order){
      let num = 0
      if(order){
        if(order.createTime){
          num++ 
          if(order.payTime){
            num++
            if(order.cancelTime || order.timeoutTime){
              num++
            }else if(order.receivingTime){
              num++
              if(order.dispatchTime){
                num++
                if(order.arrivalTime){
                  num++
                  if(order.finishTime){
                    num++
                  }
                }
              }
            }
          }
        }
      }
      return num
    }

  }
}
