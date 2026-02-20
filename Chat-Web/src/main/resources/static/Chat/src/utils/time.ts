export class TimeUtil {
  /**
   * TS 时间戳转时间格式
   * @param timestamp 时间戳（支持秒/毫秒，如 1739223600 或 1739223600000）
   * @param format 输出格式，默认 'YYYY-MM-DD HH:mm:ss'
   * @returns 格式化后的时间字符串
   */
  static timestampToTime(
    timestamp: string,
    format: string = "YYYY-MM-DD HH:mm:ss",
  ): string {
    // 第一步：处理时间戳（判断是秒还是毫秒，JS Date 需要毫秒级）
    let ts = parseInt(timestamp) ;
    // 如果时间戳长度是10位，说明是秒级，转成毫秒级
    if (ts.toString().length === 10) {
      ts = ts * 1000;
    }

    // 第二步：创建 Date 对象
    const date = new Date(ts);

    // 第三步：定义补零函数（处理 1→01 这类情况）
    const padZero = (num: number): string => num.toString().padStart(2, "0");

    // 第四步：解析年、月、日、时、分、秒
    const year = date.getFullYear();
    const month = padZero(date.getMonth() + 1); // 月份从 0 开始，需 +1
    const day = padZero(date.getDate());
    const hours = padZero(date.getHours());
    const minutes = padZero(date.getMinutes());
    const seconds = padZero(date.getSeconds());

    // 第五步：替换格式字符串中的占位符
    return format
      .replace("YYYY", year.toString())
      .replace("MM", month)
      .replace("DD", day)
      .replace("HH", hours)
      .replace("mm", minutes)
      .replace("ss", seconds);
  }
}
