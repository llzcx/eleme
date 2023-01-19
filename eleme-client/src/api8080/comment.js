import axios02 from "../utils/axios02";


//评论商家
export function addComment(data){
	return axios02.post("/comment/",data)
}


export function getCommentList(params){
	return axios02({
		method:'get',
		url:"/comment/customer/",
		params
	})
}

export function getShopCommentDetail(shopId){
	return axios02.get("/comment/totalityRating/"+shopId)
}

