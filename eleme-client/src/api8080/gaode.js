import axios02 from "../utils/axios02";

//根据关键字获取位置
export function getTipsByKey(key){
	return axios02.get("/gaoDe/getInfoByKey",{
        params: {
            keyword:key
        }
    })
}

export function regeo(geohash){
	return axios02.get("/gaoDe/regeo",{
        params: {
            key:'666cf07e826b62760407bf8068855003',
            geohash
        }
    })
}

export function getCity(geohash){
    return axios02.get("/gaoDe/city",{
        params: {
            geohash
        }
    })
}
