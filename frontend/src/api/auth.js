import request from '../api/request'

// 注册
export const register = (data) => {
  return request.post('/auth/register', data)
}

export function login(data) {
  return request.post('/api/auth/login', data)
}