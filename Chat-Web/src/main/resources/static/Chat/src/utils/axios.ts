import axios from "axios";
import router from "@/router";
import { Log } from "./TipUtil";
import {  getUserToken } from "./UserInfo";
import { msgStore } from "@/store/MessageStore";
import { userStore } from "@/store/UserStore";
import { RsaUtil } from "./RsaUtil";

//配置基础路径(axios实例)
const request = axios.create({
  baseURL: "/api",
  timeout: 5000,
  headers: {
    "Content-Type": "application/json",
  },
});

//请求拦截器,发送请求做统一处理
request.interceptors.request.use(
  (config: any) => {
    const token = getUserToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error("请求拦截器错误: ", error);
    return Promise.reject(error); //抛出错误,让组件捕获
  },
);

//响应拦截器
request.interceptors.response.use(
  async (response: any) => {
    const data = response.data;
    // 传入请求URL，支持白名单逻辑
    await RsaUtil.checkSign(data, response.config.url);
    //返回响应体的数据
    return data;
  },
  (error) => {
    console.error("响应拦截器错误: ", error);
    if (error.response) {
      switch (error.response.status) {
        case 401:
          userStore().end();
          msgStore().removeAllLocalState();
          break;
        case 403:
          Log.warn("无权限访问");
          setTimeout(() => {
            router.back();
          }, 2000);
        case 500:
          break;
      }
    } else {
      Log.error("网络错误");
    }
    return Promise.reject(error);
  },
);

//导出实例
export default request;
