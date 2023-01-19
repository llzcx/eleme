import request from '@/utils/request'

export function login(userType,username, password) {
  return request({
    url: '/manager/login',
    method: 'post',
    data:{
      username,
      password,
      userType
    }
  })
}

export function getInfo(token) {
  return request({
    url: '/manager/check',
    method: 'post',
    data:{
      token
    }
  })
}

//登出
export function logout(token) {
  console.log('logout')
  return request({
    url: '/manager/loginOut',
    method: 'post',
    data:{
      token
    }
  })
}

export function updatePwd(params) {
  return request({
    url: '/account/updatePwd',
    method: 'post',
    params
  })
}
