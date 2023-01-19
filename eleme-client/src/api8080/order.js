import axios02 from "../utils/axios02";
import { Axios } from "axios";
import axios03 from "../utils/axios03";


//将购物车保存至服务器
export function saveBuyCart(shopId,entities,jsonEntities,remarks){
	return axios02.post("/buyCart/check",{
		shopId,
		entities,
		jsonEntities,
		remarks
	})
}

//保存
export function saveOrderInfo(data){
	return axios02.post("/order/save",data)
}

//根据订单id获取到订单
export function getOrderById(id){
	return axios02.get("/order/"+id)
}


//获取订单列表
export function getOrderList(pagenum,size,cid){
	return axios02.get("/order/list/?pagenum="+pagenum+"&size="+size+"&cid="+cid)
}

//支付接口
export function payForOrder(oid){
	return axios02.put("/order/pay/",{
		oid
	})
}

//支付宝支付接口
export function zhifubaoPay(id,totalAmount){
	return axios03.post("/order/alipay",{
		outTradeNo:id+'',
		subject:'test',
		totalAmount:totalAmount,
		body:'test',
	})
}

//获取支付信息
export function getPayInfo(id){
	return axios02.get('/order/payInfo/'+id)
}