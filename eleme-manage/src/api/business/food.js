import request from '@/utils/request'

/**
 * 添加食品
 */

export function addFood(params) {
  return request({
    url: '/shopping/addfood',
    method: 'post',
    params
  })
}
/**
 * 获取食品列表
 */
export function getFoods(pagenum,size,key,categoryId,id,shopId) {
  let str = "?pagenum="+pagenum+"&size="+size
  if(key != null) str += "&key=" + key
  if(categoryId != null) str += "&categoryId=" + categoryId
  if(id != null) str += "&id=" + id
  if(shopId != null) str += "&shopId=" + shopId
  return request({
    url: '/goods/list/'+str,
    method: 'get'
  })
}

/**
 * 更新食品信息
 */

export function updateFood(params) {
  return request({
    url: '/shopping/v2/updatefood',
    method: 'post',
    params
  })
}

/**
 * 删除食品
 */

export function deleteFood(id) {
  return request({
    url: '/shopping/v2/food/' + id,
    method: 'delete'
  })
}

/**
 * 审核商铺
 * @param params
 */
export function auditFood(params) {
  return request({
    url: '/shopping/auditFood',
    method: 'post',
    params
  })
}

//获取一个商家的菜单分类列表
export function getShopMenu(shopId){
  return request({
    url: '/category/?bid='+shopId,
    method: 'get',
  })
}

//更新非规格商品
export function updateSinlge(data){
  return request({
    url: '/goods/single',
    method: 'put',
    data:data
  })
}
//更新规格商品
export function updateNotSingleSepcsSpu(data){
  return request({
    url:'/goods/notSingle',
    method:'put',
    data
  })
}

//添加规格分类
export function addSpecsCategory(data){
  return request({
    url: '/specs/addCategories',
    method: 'post',
    data:data
  })
}


//添加规格分类下的规格
export function addspecs(data){
  return request({
    url: '/specs/addSpecs',
    method: 'post',
    data:data
  })
}


//根据商品id获取到这个spu的所有规格分类
export function getSpecsCategoryList(id){
  return request({
    url: '/specs/list/'+id,
    method: 'get',
  })
}

//获取规格分类及其下的规格
export function getTreeSpecs(gid){
  return request({
    url: '/specs/categoryAndSpecsList/'+gid,
    method: 'get',
  })
}

//添加sku
export function addSku(sku){
  return request({
    url: '/food',
    method: 'post',
    data:sku
  })
}

//获取sku列表 [spuId]
export function getSkuList(spuId){
  return request({
    url: '/goods/'+spuId,
    method: 'get',
  })
}

//删除sku
export function removeSku(skuId){
  return request({
    url: '/food/'+skuId,
    method: 'delete'
  })
}

//删除spu
export function removeSpu(gid){
  return request({
    url: '/goods',
    method: 'delete',
    data:{
      gid
    }
    
  })
}

//获取一个商家所有的分类
export function getCategoryByShopId(shopId){
  return request({
    url: '/category/?bid='+shopId,
    method: 'get',
    
  })
}

//删除分类
export function deleteCategory(categoryId){
  return request({
    url: '/category/',
    method: 'delete',
    data:{
      categoryId
    }
  })
}


//保存一个新的商家分类
export function saveCategory(data){
  return request({
    url: '/category/',
    method: 'post',
    data:data
  })
}

//添加一个商品
export function addSpu(data){
  return request({
    url: '/goods/',
    method: 'post',
    data:data
  })
}

//删除一个规格
export function deleteSpecs(specsId){
  return request({
    url: '/specs/specs/'+specsId,
    method: 'delete',
  })
}


//删除一个规格分类
export function deleteSpecsCategory(specsCategoryId){
  return request({
    url: '/specs/deleteSpecsCategory/'+specsCategoryId,
    method: 'delete',
  })
}

//上架商品
export function PutOnShelves(gid){
  return request({
    url: '/goods/PutOnShelves',
    method: 'put',
    data:{
      gid
    }
  })
}


//下架商品
export function LowerShelf(gid){
  return request({
    url: '/goods/LowerShelf/',
    method: 'put',
    data:{
      gid
    }
  })
}


