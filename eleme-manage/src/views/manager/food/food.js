import { getApiUrl } from '@/utils/utils'
import { getToken } from '@/utils/auth'
import permission from '@/directive/permission/index.js'
import {
  getFoods, updateFood, deleteFood, auditFood,
  getShopMenu, updateSinlge, addSpecsCategory, addspecs
  , getSpecsCategoryList, getTreeSpecs, addSku, removeSku,
  removeSpu, getCategoryByShopId, deleteSpecs, deleteSpecsCategory,
  PutOnShelves,LowerShelf,updateNotSingleSepcsSpu,updateSingleSepcsSpu
} from '@/api/business/food'
import { getResturantDetail, getMenuById, getMenu } from '@/api/business/shop'
import { Loading } from 'element-ui'
import store from '@/store'

export default {
  directives: { permission },
  data() {
    return {
      fileMgrUrl: 'http://127.0.0.1:8080/boot/customer/image',
      uploadHeaders: {
        'Authorization': ''
      },
      restaurant_id: null,
      city: {},
      offset: 0,
      limit: 20,
      count: 0,
      tableData: [],
      currentPage: 1,
      selectTable: {},
      dialogFormVisible: false,
      dialogFormVisibleSingle: false,
      menuOptions: [],
      selectMenu: {},
      selectIndex: null,
      //添加规格分类时的表单
      addSpecsCategoryForm: {
        goodId: null,
        name: '',
        specsName: '',
        specsDescription: ''
      },
      //添加规格时需要的表单
      addSpecsForm: {
        name: '',
        description: '',
        specsCategoryId: null,
        goodId: null,
      },
      specsForm: {
        specs: '',
        packing_fee: 0,
        price: 20
      },
      specsFormrules: {
        specs: [
          { required: true, message: '请输入规格', trigger: 'blur' }
        ]
      },
      AddspecsCategoryVisible: false,
      AddspecsDetailVisible: false,
      specsFormVisible: false,
      expendRow: [],
      total: 0,
      stateList: [
        { label: '审核中', value: '0' },
        { label: '审核通过', value: '1' },
        { label: '审核拒绝', value: '-1' },
      ],
      audit: { show: false, auditRemark: '' },
      listQuery: {
        page: 1,
        limit: 20,
        key: null,
        categoryId: null,
        id: null,
        shopId: null,
      },
      specsCategoryOptions: [],
      selectSpecsCategoryMenu: [],
      selectSpecsCategoryIndex: null,
      AddSkuDetailVisible: false,
      skuForm: {
        specsList: '',
        price: 0,
        packingFee: 0,
        goodsId: null
      },
      specsTreeOption: [],//树
      selectedSpecsArr: [],//选择的规格数组
      specsTags: [],//规格标签
      specsInfo: {},//规格信息
      SkuListVisible: false,
      SkuList: [],
      SpecsListVisible: false,//规格列表展示与否
      selectedCategory: null,
    }
  },
  created() {
    this.initData()
    this.fileMgrUrl = getApiUrl() + '/file'
    this.uploadHeaders['Authorization'] = getToken()
  },
  computed: {
    specs: function () {
      let specs = []
      if (this.selectTable.skuList) {
        this.selectTable.skuList.forEach(item => {
          let str = "";
          item.specsValue.forEach(s => {
            str += s + " "
          })
          specs.push({
            id:item.skuId,
            specsList: str,
            packingFee: item.packingFee,
            price: item.price
          })
        })
      }
      return specs
    }
  },
  methods: {
    search() {
      this.fetchData()
    },
    reset() {
      this.listQuery.page = 1
      this.listQuery.size = 20
      this.listQuery.key = null
      this.listQuery.categoryId = null
      this.listQuery.id = null
      this.listQuery.shopId = null
      this.selectedCategory = null
      this.fetchData()
    },
    async initData() {
      this.fetchData()
    },
    //商家提供的食品分类
    async getMenu() {
      this.menuOptions = []
      try {
        console.log("本此要获取id为此的分类列表:", this.selectTable.shopId)
        const menuResponse = await getShopMenu(this.selectTable.shopId)
        const menu = menuResponse.data.data
        menu.forEach((item, index) => {
          this.menuOptions.push({
            label: item.name,
            value: item.id,
            index
          })
        })
      } catch (err) {
        console.log('获取食品种类失败', err)
      }
    },
    //获取到某个商品的规格分类列表
    async getSpecsCategoryList(id) {
      this.specsCategoryOptions = []
      let resp = await getSpecsCategoryList(id)
      resp.data.data.forEach((item, index) => {
        this.specsCategoryOptions.push({
          label: item.name,
          value: item.id,
          index
        })
      })
    },
    async fetchData() {
      //获取食物分类[从输入框里面获取]
      this.listQuery.categoryId = this.selectedCategory
      this.listQuery.shopId = null;
      getFoods(
        this.listQuery.page,
        this.listQuery.limit,
        this.listQuery.key,
        this.listQuery.categoryId,
        this.listQuery.id,
        this.listQuery.shopId
      ).then(response => {
        this.tableData = []
        this.total = response.data.data.totalCount
        this.tableData = response.data.data.rows;
      })
    },
    tableRowClassName(row, index) {
      if (index === 1) {
        return 'info-row'
      } else if (index === 3) {
        return 'positive-row'
      }
      return ''
    },
    //异步添加规格函数
    async addspecs() {
      this.addSpecsForm.goodId = this.selectTable.id
      this.addSpecsForm.specsCategoryId = this.selectSpecsCategoryMenu.value
      let resp = await addspecs(this.addSpecsForm)
      if (resp.data.success) {
        this.$message({
          type: 'success',
          message: '添加规格成功'
        })
      } else {

      }
    },
    async addspecsCategory() {
      this.addSpecsCategoryForm.goodId = this.selectTable.id
      console.table(this.addSpecsCategoryForm)
      let resp = await addSpecsCategory(this.addSpecsCategoryForm)
      if (resp.data.success) {
        this.$message({
          type: 'success',
          message: '添加成功'
        })
        this.fetchData()
      } else {
        this.$message({
          type: 'error',
          message: '添加失败'
        })
        this.fetchData()
      }
    },
    //用于处理选择列表值的改变 [select -> tag]
    handleSelectSpecsChange(value) {
      let category_id = value[0];
      let specs_id = value[1];
      let name = ""
      //拿到规格id以后去存的地方找name
      this.specsInfo.forEach(item => {
        item.specsList.forEach(specs => {
          if (specs_id === specs.id) {
            name = specs.name;
          }
        })
      })
      let obj = {
        name: name,
        type: 'success',
        id: specs_id,
        categoryId: category_id
      }
      //往标签option里面放东西
      this.addItemToTags(obj)
      //更新标签
      let str = "#"
      this.specsTags.forEach(item => {
        str += item.id + "#"
      })
      this.skuForm.specsList = str
      console.log(value)
    },
    async deleteSpecs(index,item) {
      console.log("item:",item)
      this.specs.splice(index, 1)
      //去数据库删除
      let resp = await removeSku(item.id)
      if(resp.data.success){
        this.$message({
          type: 'success',
          message: '删除规格组合成功!'
        })
      }
    },
    handleSizeChange(val) {
      console.log(`每页 ${val} 条`)
    },
    handleCurrentChange(val) {
      this.currentPage = val
      this.offset = (val - 1) * this.limit
      this.fetchData()
    },
    expand(row, status) {
      if (status) {
        this.getSelectItemData(row)
      } else {
        const index = this.expendRow.indexOf(row.index)
        this.expendRow.splice(index, 1)
      }
    },
    handleAudit(row) {
      this.audit.show = true
      this.selectTable = row
    },
    handleAuditConfirm(state) {

      if (state == "-1") {
        if (this.audit.auditRemark == '') {
          this.$message({
            type: 'warning',
            message: '请输入拒绝原因'
          })
          return
        }
      }
      const params = { item_id: this.selectTable.item_id, state: state, auditRemark: this.audit.auditRemark }

      auditFood(params).then(response => {
        this.$message({
          type: 'success',
          message: '审核成功'
        })
        this.fetchData()
      })
      this.audit.show = false
    },
    /**
     * 处理规格商品编辑
     * @param {*} row 
     */
    async handleEdit(row) {
      //初始化增加sku事件
      this.specsTreeOption = [],//树
        this.selectedSpecsArr = [],//选择的规格数组
        this.specsTags = [],//规格标签
        this.specsInfo = {},//规格信息

        //设置选中的行
        this.selectTable = row
      this.getSelectItemData(row, 'edit')
      //开启可见
      this.dialogFormVisible = true
      //将规格列表的数据加载进入到表格当中 [在计算属性当中已经为我们实现]
      //接下来就是生成规格树了
      let rasp = await getTreeSpecs(this.selectTable.id)
      //暂时保存一下这个选中的spu的规格信息
      this.specsInfo = rasp.data.data
      //遍历这个对象 将它放在option当中
      rasp.data.data.forEach(item => {
        let temp = []
        item.specsList.forEach(specsitem => {
          temp.push({
            value: specsitem.id,
            label: specsitem.name
          })
        })
        this.specsTreeOption.push({
          value: item.id,
          label: item.name,
          children: temp
        })
      })
      //将规格树转换成 规格选择框
      this.specsInfo.forEach(item => {
        if (item.specsList.length > 0) {
          let tmp = item.specsList[0]
          this.specsTags.push({
            name: tmp.name,
            type: 'success',
            id: tmp.id,
            categoryId: item.id
          })
        }

      })
      //更新表单中的goodId
      this.skuForm.goodsId = row.id

    },
    /**
     * 处理非规格商品编辑
     * @param {*} row 
     */
    handleSingleEdit(row) {
      this.selectTable = row
      this.getSelectItemDataSingle(row, 'edit')
      this.dialogFormVisibleSingle = true
    },
    //处理规格商品编辑子方法
    async getSelectItemData(row, type) {
      this.menuOptions = []
      this.selectMenu = {}
      this.selectIndex = null
      this.selectTable = row
      await this.getMenu()
      const me  = this
      this.menuOptions.forEach((item,index)=>{
        if(item.value==row.categoryId){
          me.handleSelect(index)
        }
      })
    },
    //处理非规格商品子方法
    async getSelectItemDataSingle(row, type) {
      this.menuOptions = []
      this.selectMenu = {}
      this.selectIndex = null
      this.selectTable = row
      await this.getMenu()
      const me  = this
      this.menuOptions.forEach((item,index)=>{
        if(item.id==row.categoryId){
          me.handleSelect(index)
        }
      })
    },
    //添加规格分类 对于一个spu
    handAddspecs(row) {
      this.selectTable = row
      //将规格列表先清空
      this.specsCategoryOptions = []
      this.selectSpecsCategoryIndex = ''
      this.getSpecsCategoryList(row.id)
      this.selectSpecsCategoryMenu = ''
      this.AddspecsDetailVisible = true
    },
    //添加规格分类
    handAddspecsCategory(row) {
      this.selectTable = row
      this.AddspecsCategoryVisible = true
    },
    //添加规格商品sku 的触发按钮
    handAddSku(row) {
      this.selectTable = row
      this.AddSkuDetailVisible = true
    },
    //往标签规格数组里面放东西{}
    addItemToTags(tagSmall) {
      let idex = -1;
      //遍历数组 查找与之匹配的位置
      this.specsTags.forEach((item, index) => {
        if (item.categoryId === tagSmall.categoryId) {
          idex = index;
        }
      })
      if (idex != -1) {
        this.specsTags[idex] = tagSmall
        return true
      } else {
        return false
      }
    },
    handleSelect(index) {
      this.selectIndex = index
      this.selectMenu = this.menuOptions[index]
    },
    handleSpecsCategorySelect(index) {
      this.selectSpecsCategoryIndex = index
      this.selectSpecsCategoryMenu = this.specsCategoryOptions[index]
    },
    async handleDelete(index, row) {
      try {
        removeSpu(row.id).then(response => {
          this.$message({
            type: 'success',
            message: '删除食品成功'
          })
          //从列表中删除
          this.fetchData()
        })
      } catch (err) {
        this.$message({
          type: 'error',
          message: err.message
        })
        console.log('删除食品失败')
      }
    },
    handleServiceAvatarScucess(res, file) {
      if (res.status === 1) {
        this.selectTable.imagePath = res.imagePath
      } else {
        this.$message.error('上传图片失败！')
      }
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

    handleBeforeUpload() {
      this.loadingInstance = Loading.service({
        lock: true,
        text: this.$t('common.uploading'),
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
    },
    handleUploadSuccess(response, raw) {
      this.selectTable.imagePath = response.data;
      this.loadingInstance.close()
      if (response.success) {
        this.selectTable.imagePath = response.data;
      } else {
        this.$message({
          message: this.$t('common.uploadError'),
          type: 'error'
        })
      }
    },
    async updateSingleFood() {
      this.dialogFormVisible = false

      // const subData = { new_category_id: this.selectMenu.value, specs: this.specs }
      // const postData = { ...this.selectTable, ...subData }

      const me = this
      updateSinlge({
        name: me.selectTable.name,
        goodsId: me.selectTable.id,
        descript: me.selectTable.description,
        categoryId: me.selectMenu.value,
        price: me.selectTable.price,
        packingFee: me.selectTable.packingFee,
        imagePath: me.selectTable.imagePath,
      }).then(response => {
        this.fetchData()
        this.$message({
          type: 'success',
          message: '更新食品信息成功'
        })
      }).catch(() => {
        this.$message({
          type: 'error',
          message: err.message
        })
      })
      this.dialogFormVisible = false
      this.fetchData()
    },
    //更新规格商品
    async updateNotSinlgeSpu() {
      const me = this
        const data = {
          goodsId:me.selectTable.id,
          name:me.selectTable.name,
          description:me.selectTable.description,
          categoryId:me.selectMenu.value,
          imagePath:me.selectTable.imagePath,
        }
        const resp = await updateNotSingleSepcsSpu(data)
        if(resp.data.success){
          this.$message({
            type: 'success',
            message: '更新成功!'
          })
        }else{
          this.$message({
            type: 'error',
            message: '更新失败!'
          })
        }
        this.dialogFormVisible = false
        this.fetchData()
    },
    //添加sku商品
    async addSkuFood() {
      this.dialogFormVisible = false
      // const subData = { new_category_id: this.selectMenu.value, specs: this.specs }
      // const postData = { ...this.selectTable, ...subData }
      const me = this
      addSku(this.skuForm).then(response => {
        this.fetchData()
        this.$message({
          type: 'success',
          message: '添加规格食品成功'
        })
      }).catch(() => {
        this.$message({
          type: 'error',
          message: err.message
        })
      })
      this.AddSkuDetailVisible = false
    },
    //打开sku列表
    async OpenSkuList() {

    },
    //删除sku
    async deleteSku(row) {
      removeSku(row.id).then(response => {
        this.fetchData()
        this.$message({
          type: 'success',
          message: '删除sku成功!'
        })

      }).catch(() => {
        this.$message({
          type: 'error',
          message: err.message
        })
      })
    },
    //将商品展示/隐藏给前端[按钮时间]
    async handShowToFront(row) {

    },
    //查看规格列表
    async handSpecsList(row) {
      this.selectTable = row
      let rasp = await getTreeSpecs(this.selectTable.id)
      //暂时保存一下这个选中的spu的规格信息
      this.specsInfo = rasp.data.data
      this.SpecsListVisible = true

    },
    async deleteSpecsRequest(row) {
      let resp = await deleteSpecs(row.id)
      console.log(resp)
      if (resp.data.success) {
        this.$message({
          type: 'success',
          message: '删除规格成功!'
        })
        this.fetchData()
      } else {
        this.$message({
          type: 'error',
          message: resp.data.errorMsg
        })
      }
    },
    //删除规格
    async deleteASpecs(row) {
      this.$confirm('删除规格会导致含有此规格的所有规格商品被删除,确认删除吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        center: true
      }).then(async () => {
        this.deleteSpecsRequest(row);
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    //上架商品
    async PutOnShelves(row) {
      PutOnShelves(row.id).then(response => {
        if (response.data.success) {
          this.$message({
            type: 'success',
            message: '上架成功'
          })
          this.fetchData()
        } else {
          this.$message({
            type: 'error',
            message: response.data.errorMsg
          })
        }
      })
    },
    //下架商品
    async LowerShelf(row) {
      LowerShelf(row.id).then(response => {
        if (response.data.success) {
          this.$message({
            type: 'success',
            message: '成功下架'
          })
          this.fetchData()
        } else {
          this.$message({
            type: 'error',
            message: response.data.errorMsg
          })
        }
      })
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
    }
  }
}
