import axios from "axios";
import { ElMessage } from "element-plus";
import router from "../router";


//创建axios实例化对象
const request = axios.create({
  baseURL: "/api",
  timeout: 600000,
});


//axios的请求 request拦截器 - 获取localStorage中的token，添加token请求头
request.interceptors.request.use(
  (config) => {
    // console.log(config);
    //获取token
    const loginUser = JSON.parse(localStorage.getItem("loginUser"));  //把字符串转为JSON对象
    //判断token是否存在
    if (loginUser && loginUser.token) {
      //添加token请求头
      config.headers.token = loginUser.token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
}
)


//axios响应拦截器
request.interceptors.response.use(
  (response) => {
    // console.log(response);
    return response.data;
  },
  (error) => {
    if(error.response.status === 401){  //全等
      //提示信息
      ElMessage.error("登录超时，请重新登陆");
      //跳转登录页面
      router.push("/login");

    }
    return Promise.reject(error);
  }
);

export default request;