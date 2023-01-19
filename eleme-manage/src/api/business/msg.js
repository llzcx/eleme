import request from '@/utils/request'


//获取消息列表
export function getMsgList(pagenum, size, cid, bid) {
	let str = "?pagenum=" + pagenum + "&size=" + size + "&cid=" + cid + "&bid=" + bid
	return request({
		url: "/customerBusinessMsg/list" + str,
		method: 'get',
	})
}


//获取聊天对象
export function getChatObjectList(bid){
	return request({
		url: "/customerBusinessMsg/customer/list/" + bid,
		method: 'get',
	})
}