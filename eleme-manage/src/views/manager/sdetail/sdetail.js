import {getApiUrl} from '@/utils/utils'
import {getToken} from '@/utils/auth'
import {cityGuess} from '@/api/getData'
import permission from '@/directive/permission/index.js'
import {getTipsByKey,regeo} from '@/api/business/gapde'
import store from '@/store'
import {
  getResturants,
  getResturantsCount,
  foodCategory,
  updateResturant,
  stopResturant,
  auditResturant,
  searchplace,
  deleteResturant,
  getShopById,
  getShopClass,
  updateShopInfo
} from '@/api/business/shop'

export default {
  directives: {permission},
  data() {
    return {
      checkStateStr:'审核未通过',
      shopInfo:{},
      city: {},
      testNum:10,
      testStr:"测试字符串",
      count: 0,
      fileMgrUrl: '',
      uploadHeaders: {
        'Authorization': ''
      },
      tableData: [],
      currentPage: 1,
      selectTable: {},
      dialogFormVisible: false,
      categoryOptions: [],
      selectedCategory: [],
      address: {},
      total: 0,
      stateList: [
        {label: '审核中', value: '0'},
        {label: '审核通过', value: '1'},
        {label: '审核拒绝', value: '-1'},
      ],
      audit: {show: false, auditRemark: ''},
      listQuery: {
        page: 1,
        limit: 20,
        name: undefined,
        state: ''
      },
    }

  },
  created() {
    console.log("12")
    this.initData()
    // this.uploadHeaders['Authorization'] = getToken()


  },

  methods: {
    async initData() {
      //获取商家信息(登录以后可以得到)
     
      this.shopInfo = store.getters.shopInfo;
      const resp = await getShopById(store.getters.shopInfo.id);
      this.shopInfo = resp.data.data;
      this.selectTable = this.shopInfo
      if(this.selectTable.checkPass===-1){
        this.checkStateStr = '未审核'
      }else if(this.selectTable.checkPass===0){
        this.checkStateStr = '审核未通过'
      }else if(this.selectTable.checkPass===1){
        this.checkStateStr = '审核已通过'
      }

      const resp1 = await getShopClass();

      this.categoryOptions = resp1.data.data.map((item) => {
        if(item['id']==this.shopInfo.shopClassId) {
          this.selectedCategory = []
          this.selectedCategory.push(item['id'])
        }
        return {
            "label": item['name'],
            "value": item['id'],
        }
      })
      // try {
      //   cityGuess().then(response => {
      //     this.city = response.data
      //     getResturantsCount().then(response2 => {
      //       this.count = response2.count
      //       // this.fetchData()
      //     })
      //   })
      // } catch (err) {
      //   console.log('获取数据失败', err)
      // }
    },
    
    async getCategory() {
      try {
        const response = await foodCategory()
        const categories = response.data
        categories.forEach(item => {
          if (item.sub_categories.length) {
            const addnew = {
              value: item.name,
              label: item.name,
              children: []
            }
            item.sub_categories.forEach((subitem, index) => {
              if (index === 0) {
                return
              }
              addnew.children.push({
                value: subitem.name,
                label: subitem.name
              })
            })
            this.categoryOptions.push(addnew)
          }
        })
      } catch (err) {
        console.log('获取商铺种类失败', err)
      }
    },
    async fetchData() {
      getResturants(this.listQuery.page,this.listQuery.limit).then(response => {
        const restaurants = response.data.data.rows
        this.total = response.data.total
        this.tableData = []
        restaurants.forEach(item => {
          const tableData = {}
          tableData.name = item.name
          tableData.address = item.address
          tableData.description = item.description
          tableData.id = item.id
          tableData.phone = item.phone
          tableData.rating = item.rating
          tableData.recent_order_num = item.recent_order_num
          tableData.category = item.category
          tableData.image_path = item.image_path
          tableData.state = item.state
          tableData.stateStr = item.stateStr
          tableData.auditRemark = item.auditRemark
          tableData.disabled = item.disabled
          tableData.unliquidatedAmount = item.unliquidatedAmount
          tableData.totalAmount = item.totalAmount
          tableData.platform_rate = item.platform_rate
          this.selectTable = tableData
          this.selectTable.image = ''
          this.address.address = tableData.address

          this.selectedCategory = tableData.category.split('/')
          if (!this.categoryOptions.length) {
            this.getCategory()
          }
        })
      })
    },

    search() {
      this.fetchData()
    },
    addFood( row) {
      this.$router.push({path: '/business/food/add', query: {restaurant_id: row.id}})
    },
    viewFood( row) {
      this.$router.push({path: '/business/food', query: {restaurant_id: row.id}})
    },
    async querySearchAsync(queryString, cb) {
      // if (queryString) {
      //   try {
      //     const cityList = await searchplace(this.city.id, queryString)
      //     if (cityList instanceof Array) {
      //       cityList.map(item => {
      //         item.value = item.address
      //         return item
      //       })
      //       cb(cityList)
      //     }
      //   } catch (err) {
      //     console.log(err)
      //   }
      // }
    },
    addressSelect(vale) {
      const {address, latitude, longitude} = vale
      this.address = {address, latitude, longitude}
    },
    handleServiceAvatarScucess(res, file) {
      this.selectTable.imagePath = res.data
    },
    beforeAvatarUpload(file) {
      const isRightType = (file.type === 'image/jpeg') || (file.type === 'image/png')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isRightType) {
        this.$message.error('上传头像图片只能是 JPG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isRightType && isLt2M
    },
    async updateShop() {
      try {
        updateShopInfo({
          id:store.getters.shopInfo.id,
          name:this.selectTable.name,
          address:this.selectTable.address,
          phone:this.selectTable.phone,
          latitude:this.selectTable.latitude,
          longitude:this.selectTable.longitude,
          imagePath:this.selectTable.imagePath,
          floatDeliveryFee:this.selectTable.floatDeliveryFee,
          floatMinimumOrderAmount:this.selectTable.floatMinimumOrderAmount,
          description:this.selectTable.description,
          startTime:this.selectTable.startTime,
          endTime:this.selectTable.endTime,
          shopClassId:this.selectedCategory[0],
        }).then(response => {
          if(response.data.success){
            this.$message({
              type: 'success',
              message: '更新店铺信息成功'
            })
          }else{
            this.$message({
              type: 'error',
              message: '更新店铺信息失败'
            })            
          }
          this.getResturants()
        })

      } catch (err) {
        console.log('更新餐馆信息失败', err)
        this.$message({
          type: 'error',
          message: err
        })
      }
    }
  }
}
