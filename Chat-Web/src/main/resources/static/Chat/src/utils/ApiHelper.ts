import { type data } from "@/types/user";
import { type AxiosResponse } from "axios";
import { BusinessError } from "@/exception/BusinessError";

export class ApiHelper {
  /**
   * 处理 API 响应，直接返回数据或抛出异常
   * @param promise Axios 请求 Promise
   * @returns 解析后的数据 data.data
   */
  static async handle<T>(promise: Promise<any>): Promise<T> {

      const res = await promise;
      if (res.code === 1) {
        return res.data;
      } else {
        throw new BusinessError(res.msg || "服务繁忙,操作未能成功", res.code);
      }

  }
}
