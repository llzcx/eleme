import request from '@/utils/request'

//根据关键字获取位置
export function getTipsByKey(key){
	return request.get("/gaoDe/getInfoByKey",{
        params: {
            keyword:key
        }
    })
}

export function regeo(geohash){
	return request.get("/gaoDe/regeo",{
        params: {
            key:'666cf07e826b62760407bf8068855003',
            geohash
        }
    })
}
