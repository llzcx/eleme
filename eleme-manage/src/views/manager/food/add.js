import { getApiUrl } from '@/utils/utils'
import { getToken } from '@/utils/auth'
import {
  addFood, getCategoryByShopId, saveCategory,
  deleteCategory,addSpu
} from '@/api/business/food'
import { getCategory, addCategory, getShopClass } from '@/api/business/shop'

export default {
  data() {
    return {
      restaurant_id: 1,
      categoryForm: {
        categoryList: [],
        categorySelect: '',
        name: '',
        description: ''
      },
      fileMgrUrl: '',
      uploadHeaders: {
        'Authorization': ''
      },
      foodForm: {
        name: '',
        description: '',
        imagePath: '',
        activity: '',
        attributes: [],
      },
      categoryRules: {
        name: [
          { required: true, message: '请选择食品种类', trigger: 'blur' }
        ]
      },
      foodrules: {
        name: [
          { required: true, message: '请输入食品名称', trigger: 'blur' }
        ],
        attributes: [
          { required: true, message: '请选择食品特点', trigger: 'blur' }
        ],
        categoryName: [
          { required: true, message: '请选择食品种类', trigger: 'blur' }
        ],
      },
      attributes: [{
        value: '新',
        label: '新品'
      }, {
        value: '招牌',
        label: '招牌'
      }],
      showAddCategory: false,
      foodSpecs: 'one',
      dialogFormVisible: false,
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
      categoryOptions: [] //种类待选
    }
  },
  created() {
    this.uploadHeaders['Authorization'] = getToken()
    if (this.$route.query.restaurant_id) {
      this.restaurant_id = this.$route.query.restaurant_id
    } else {
      this.restaurant_id = Math.ceil(Math.random() * 10)
      this.$msgbox({
        title: '提示',
        message: '添加食品需要选择一个商铺，先去就去选择商铺吗？',
        showCancelButton: true,
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        beforeClose: (action, instance, done) => {
          if (action === 'confirm') {
            this.$router.push('/business/shop')
            done()
          } else {
            this.$message({
              type: 'info',
              message: '取消'
            })
            done()
          }
        }
      })
    }
    this.initData()
  },
  computed: {
    selectValue: function () {
      // console.log("this:",this.categoryForm.categorySelect)
      return this.categoryOptions[this.categoryForm.categorySelect] || {}
    },
  },
  methods: {
    async initData() {
      let resp = await getCategoryByShopId(this.$route.query.restaurant_id)
      console.log(resp)
      this.categoryOptions = resp.data.data.map((item) => {
        return {
          "label": item['name'],
          "value": item['id'],
        }
      })
      // try {
      //   const result = await getCategory(this.restaurant_id)
      //   if (result.code === 20000) {
      //     result.category_list.map((item, index) => {
      //       item.value = index
      //       item.label = item.name
      //     })
      //     this.categoryForm.categoryList = result.category_list
      //   } else {
      //     console.log(result)
      //   }
      // } catch (err) {
      //   console.log(err)
      // }
    },
    addCategoryFun() {
      this.showAddCategory = !this.showAddCategory
    },
    submitcategoryForm(formName) {
      console.log(this.categoryForm)
      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          console.log(2, this.categoryForm)
          const params = {
            name: this.categoryForm.name,
            description: this.categoryForm.description,
            businessId: this.$route.query.restaurant_id
          }
          try {
            const result = await saveCategory(params)
            if (result.data.success) {
              this.initData()
              this.categoryForm.name = ''
              this.categoryForm.description = ''
              this.showAddCategory = false
              this.$message({
                type: 'success',
                message: '添加成功'
              })
            }
          } catch (err) {
            console.log(err)
          }
        } else {
          this.$notify.error({
            title: '错误',
            message: '请检查输入是否正确',
            offset: 100
          })
          return false
        }
      })
    },
    uploadImg(res, file) {
      console.log(res)
      this.foodForm.imagePath = res.data
    },
    beforeImgUpload(file) {
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
    addspecs() {
      this.foodForm.specs.push({ ...this.specsForm })
      this.specsForm.specs = ''
      this.specsForm.packing_fee = 0
      this.specsForm.price = 20
      this.dialogFormVisible = false
    },
    handleDelete(index) {
      this.foodForm.specs.splice(index, 1)
    },
    tableRowClassName(row, index) {
      if (index === 1) {
        return 'info-row'
      } else if (index === 3) {
        return 'positive-row'
      }
      return ''
    },
    addFood(foodForm) {
      this.$refs[foodForm].validate(async (valid) => {
        if (valid) {

          try {
            const me = this
            const result = await addSpu({
              name: me.foodForm.name,
              description: me.foodForm.description,
              categoryId: me.categoryForm.categorySelect,
              imagePath: me.foodForm.imagePath,
              isSingle:this.foodSpecs==='one'
            })
            if (result.data.success) {
              console.log(result)
              this.$message({
                type: 'success',
                message: '添加成功'
              })
              this.foodForm = {
                name: '',
                description: '',
                imagePath: '',
                activity: '',
                attributes: [],
                packingFee:0,
                price:20,
              }
            } else {
              this.$message({
                type: 'error',
                message: result.message
              })
            }
          } catch (err) {
            console.log(err)
          }
        } else {
          this.$notify.error({
            title: '错误',
            message: '请检查输入是否正确',
            offset: 100
          })
          return false
        }
      })
    },
    //删除分类
    async handleDeleteCategoryTag(categoryId) {
      this.$confirm('确认删除吗?下面的商品会一并删除', '删除商品分类', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then( async () => {
        const resp = await deleteCategory(categoryId)
        if (resp.data.success) {
          this.$message({
            type: 'success',
            message: "删除成功!"
          })
          this.initData()
        } else {
          this.$message({
            type: 'error',
            message: resp.data.errorMsg
          })
        }
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        });
      });

    }
  }
}
