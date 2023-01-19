import axios02 from "../utils/axios02";

export function getMsgList(pagenum,size,cid,bid){
	let str = "?pagenum="+pagenum+"&size="+size+"&cid="+cid+"&bid="+bid
	return axios02.get("/customerBusinessMsg/list"+str)
}