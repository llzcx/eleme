import axios02 from "../utils/axios02";

export function addNewAddress(data) {
    return axios02.post('/address/save', data)
}

// 删除地址
export function deleteAddress(addressid) {
    return axios02.delete('/address/'+addressid)
}

//根据用户id获取收获地址列表
export function getAddressListByCId(cid){
    return axios02.get("/address/"+cid)
}

//更新地址信息
export function updateAddress(data){
    return axios02.put('/address/update', data)
}


//更新默认的id
export function setDefault(id){
    return axios02.put("/address/setDefault/"+id)
}