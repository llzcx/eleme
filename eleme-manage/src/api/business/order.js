import request from '@/utils/request'

export function getList(params) {
  return request({
    url: '/bos/orders',
    method: 'get',
    params
  })
}

export function updateOrderStatus(params) {
  return request({
    url: '/bos/updateOrderStatus',
    method: 'post',
    params
  })
}

export function getOrder(params) {
  return request({
    url: '/bos/getOrder',
    method: 'get',
    params
  })
}



//拉取商家的订单列表
export function getShopOrderList(pagenum,size,key,cid,oid,shopId,statecode){
  let str = "?pagenum="+pagenum+"&size="+size
  if(key!=null) str += "&key="+key
  if(cid!=null) str += "&cid="+cid
  if(oid!=null) str += "&oid="+oid
  if(shopId!=null) str+= "&shopId="+shopId
  if(statecode!=null) str += "&statecode=" +statecode
  return request({
    url: '/order/shopList/'+str,
    method: 'get',
  })
}




// 商家接单 将状态从 正在准备状态 => 正在准备状态
export function orderReceiving(oid){
  return request({
    url: '/order/orderReceiving/'+oid,
    method: 'put',
  })
}

//商家取消订单 将订单状态:已支付状态 => 订单取消 售后状态:退款成功
export function CancelOrderReceiving(oid){
  return request({
    url: '/order/CancelOrderReceiving/'+oid,
    method: 'put',
  })
}

// 商家接单 将状态从 已支付状态 => 正在派送状态
export function orderDispatching(oid){
  return request({
    url: '/order/orderDispatching/'+oid,
    method: 'put',
  })
}

//商家接单 将状态从 正在派送状态 => 订单完成
export function orderFinish(oid){
  return request({
    url: '/order/orderFinish/'+oid,
    method: 'put',
  })
}