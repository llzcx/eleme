import axios02 from "../utils/axios02";

//获取美食轮播
export function getFoodCarousel(){
	return axios02.get("/shopclass")
} 


//获取商铺列表
export function getShops(pagenum,size,longitude,latitude,key,sortway,shopclassid,fengniao,ping,bao,zhun,xin,onlinepay,piao){
	let str =''
	if(key!=null) str += '&key='+key
	if(sortway!=null) str += '&sortway='+sortway
	if(shopclassid!=null) str += '&shopclassid='+shopclassid
	if(fengniao!=null) str += '&fengniao='+fengniao
	if(ping!=null) str += '&ping='+ping
	if(bao!=null) str += '&bao='+bao
	if(zhun!=null) str += '&zhun='+zhun
	if(xin!=null) str += '&xin='+xin
	if(onlinepay!=null) str += '&onlinepay='+onlinepay
	if(piao!=null) str += '&piao='+piao
	return axios02.get("/business/?"+"pagenum="+pagenum+"&size="+size+"&longitude="+longitude+"&latitude="+latitude+str)
}

//获取商铺列表
export function getShopsByEs(lastId,size,longitude,latitude,key,sortway,shopclassid,fengniao,ping,bao,zhun,xin,onlinepay,piao){
	if(key=="") key = null
	let str =''
	if(key!=null) str += '&key='+key
	if(sortway!=null) str += '&sortway='+sortway
	if(shopclassid!=null) str += '&shopclassid='+shopclassid
	if(fengniao!=null) str += '&fengniao='+fengniao
	if(ping!=null) str += '&ping='+ping
	if(bao!=null) str += '&bao='+bao
	if(zhun!=null) str += '&zhun='+zhun
	if(xin!=null) str += '&xin='+xin
	if(onlinepay!=null) str += '&onlinepay='+onlinepay
	if(piao!=null) str += '&piao='+piao
	if(lastId=null) str += '&lastId='+lastId
	return axios02.get("/business/GoodPerformance?"+"size="+size+"&longitude="+longitude+"&latitude="+latitude+str)
}
//根据商家id获取到所有的分类
export function getCategories(bid){
	return axios02.get("/category/?"+"bid="+bid)
}

//根据分类获取所有的商品
export function getGoods(cid){
	return axios02.get("/goods/?"+"cid="+cid)
}

//根据商家id获取商家
export function getShopById(id){
	return axios02.get("/business/"+id)
}

//根据分类获取到对应的食物列表
export function getFoodsByCId(cid){
	return axios02.get("/cid/{"+cid+"}");
}


//获取分类+食物
export function getMenu(bid){
	return axios02.get("/goods/menu/"+bid);
}

//根据id查询sku的一些信息[用于添加购物车]
export function getSkuInfo(id){
	return axios02.get("/food/goodsInfo/"+id)
}

export function getHotWordList(geohash){

	return axios02.get("/business/getKeyList?geohash="+geohash)
}