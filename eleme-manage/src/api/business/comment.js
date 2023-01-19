import request from '@/utils/request'

export function getCommentList(params) {
	return request({
	  url: '/comment/shop/',
	  method: 'get',
	  params
	})
  }


  //评论商家
export function addComment(data){
	return request.post("/comment/",data)
}