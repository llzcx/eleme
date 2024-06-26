import {getApiUrl} from '@/utils/utils'
import {getToken} from '@/utils/auth'
import {cityGuess} from '@/api/getData'
import permission from '@/directive/permission/index.js'
import {
  getResturants,
  getResturantsCount,
  foodCategory,
  updateResturant,
  stopResturant,
  auditResturant,
  searchplace,
  deleteResturant,
  check
} from '@/api/business/shop'

export default {
  directives: {permission},
  data() {
    return {
      // city: {},
      // count: 0,
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
        {label:"请选择",value:null },
        {label: '审核中', value: '-1'},
        {label: '审核通过', value: '1'},
        {label: '审核拒绝', value: '0'},
      ],
      audit: {show: false, auditRemark: ''},
      listQuery: {
        page: 1,
        limit: 20,
        name: null,
        state: null
      }
    }
  },
  created() {
    this.initData()
    this.uploadHeaders['Authorization'] = getToken()
  },

  methods: {
    async initData() {
      this.fetchData()
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
      getResturants(this.listQuery.page,this.listQuery.limit,this.listQuery.name,this.listQuery.state).then(response => {
        const restaurants = response.data.data.rows
        this.total = response.data.data.totalCount
        this.tableData = []
        restaurants.forEach(item => {
          const tableData = {}
          tableData.name = item.name
          tableData.address = item.address
          tableData.description = item.description
          tableData.id = item.id
          tableData.phone = item.phone
          tableData.rating = item.rating
          tableData.recentOrderNum = item.RecentOrderNum
          tableData.category = item.category
          tableData.imagePath = item.imagePath
          tableData.state = item.state
          tableData.stateStr = item.stateStr
          tableData.auditRemark = ''
          tableData.disabled = ''
          tableData.unliquidatedAmount = ''
          tableData.totalAmount = ''
          tableData.isInSchool = item.isInSchool + ''
          tableData.deactivate = item.deactivate
          tableData.platform_rate = ''
          tableData.checkPass = item.checkPass
          tableData.deactivate = item.deactivate
          this.tableData.push(tableData)
          this.shopDetail = tableData
        })
      })
    },

    search() {
      this.fetchData()
    },
    reset() {
      this.listQuery.name = null
      this.listQuery.state = null
      this.page = 1
      this.fetchData()
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`)
    },
    fetchNext() {
      this.listQuery.page = this.listQuery.page + 1
      this.fetchData()
    },
    fetchPrev() {
      this.listQuery.page = this.listQuery.page - 1
      this.fetchData()
    },
    fetchPage(page) {
      this.listQuery.page = page
      this.fetchData()
    },
    changeSize(limit) {
      this.listQuery.limit = limit
      this.fetchData()
    },
    handleStop(index, row, disabled) {
      let me = this
      this.$confirm('确认' + (disabled == 1 ? '停用商铺' : '启用商铺') + '?', {
        confirmButtonText: '确认',
        cancelButtonText: '取消'
      }).then(({value}) => {
        const deactivate = disabled === 1?true:false;
        stopResturant(row.id, deactivate).then(response => {
          this.$message({
            type: 'success',
            message: (disabled == 1 ? '停用商铺' : '启用商铺') + '成功'
          })
          me.fetchData()
        })
      }).catch(({value}) => {
      });

    },
    handleAuditConfirm(state) {
      let ste = true;
      if (state == 0) {
        if (this.audit.auditRemark == '') {
          ste =false;
          this.$message({
            type: 'warning',
            message: '请输入拒绝原因'
          })
          return
        }
      }
      auditResturant(this.selectTable.id,ste,this.audit.auditRemark).then(response => {
        this.$message({
          type: 'success',
          message: '审核成功'
        })
        this.fetchData()
      })
      this.audit.show = false
    },
    handleAudit(index, row) {
      this.audit.show = true
      this.selectTable = row
    },
    handleCheck(index, row) {
      if(row.unliquidatedAmount=='' || row.unliquidatedAmount=='0'){
        this.$message({
          type: 'warning',
          message: '没有可结算的交易额'
        })
        return ;
      }
      let me = this
      this.$confirm('待结算金额' + row.unliquidatedAmount + '元,确认结算?', {
        confirmButtonText: '确认',
        cancelButtonText: '取消'
      }).then(({value}) => {
        check({id: row.id}).then(response => {
          this.$message({
            type: 'success',
            message: '结算成功'
          })
          me.fetchData()
        })
      }).catch(({value}) => {
      });
    },
    handleEdit(index, row) {
      console.log('row', row)
      this.selectTable = row
      this.selectTable.image = getApiUrl() + '/file/getImgStream?fileName=' + row.image_path
      this.address.address = row.address
      this.dialogFormVisible = true
      this.selectedCategory = row.category.split('/')
      if (!this.categoryOptions.length) {
        this.getCategory()
      }
    },
    viewFood(index, row) {
      this.$router.push({path: '/Mbusiness/Mfood', query: {restaurant_id: row.id}})
    },
    async handleDelete(index, row) {
      try {
        const res = await deleteResturant(row.id)
        if (res.status == 1) {
          this.$message({
            type: 'success',
            message: '删除店铺成功'
          })
          this.tableData.splice(index, 1)
        } else {
          throw new Error(res.message)
        }
      } catch (err) {
        this.$message({
          type: 'error',
          message: err.message
        })
        console.log('删除店铺失败')
      }
    },
    async querySearchAsync(queryString, cb) {
      if (queryString) {
        try {
          const cityList = await searchplace(this.city.id, queryString)
          if (cityList instanceof Array) {
            cityList.map(item => {
              item.value = item.address
              return item
            })
            cb(cityList)
          }
        } catch (err) {
          console.log(err)
        }
      }
    },
    addressSelect(vale) {
      const {address, latitude, longitude} = vale
      this.address = {address, latitude, longitude}
    },
    handleServiceAvatarScucess(res, file) {
      this.selectTable.image_path = res.data.realFileName
      this.selectTable.image = getApiUrl() + '/file/getImgStream?fileName=' + res.data.realFileName

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
      this.dialogFormVisible = false
      try {
        Object.assign(this.selectTable, this.address)
        this.selectTable.category = this.selectedCategory.join('/')
        updateResturant(this.selectTable).then(response => {
          this.$message({
            type: 'success',
            message: '更新店铺信息成功'
          })
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
