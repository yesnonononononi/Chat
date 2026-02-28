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

  /**
   * 获取过去多少天的日期列表
   * @param days 天数
   * @param format 格式，默认 'YYYY-MM-DD'
   * @returns 日期字符串列表
   */
  static getPastDays(days: number, format: string = "YYYY-MM-DD"): string[] {
    const result: string[] = [];
    for (let i = days - 1; i >= 0; i--) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      const padZero = (num: number): string => num.toString().padStart(2, "0");
      const year = date.getFullYear();
      const month = padZero(date.getMonth() + 1);
      const day = padZero(date.getDate());
      
      const dateStr = format
        .replace("YYYY", year.toString())
        .replace("MM", month)
        .replace("DD", day);
      result.push(dateStr);
    }
    return result;
  }

   /**
   * 格式化最后消息时间
   * @param dateTimeStr 后端返回的时间字符串，格式如 "2026-02-25 21:45:13.258"
   * @returns 格式化后的字符串
   * 规则：
   * - 今天：显示 HH:mm:ss（例如 "21:45:13"）
   * - 昨天：显示 "昨天"
   * - 本周内（非昨天）：显示星期几（例如 "周三"）
   * - 今年内（非本周）：显示 MM-DD（例如 "02-25"）
   * - 去年及以前：显示 YYYY-MM-DD（例如 "2025-02-25"）
   */
  static formatLastMsgTime(dateTimeStr: string): string {
    if (!dateTimeStr) return '';

    // 解析字符串 "YYYY-MM-DD HH:mm:ss.SSS"
    const match = dateTimeStr.match(/^(\d{4})-(\d{2})-(\d{2}) (\d{2}):(\d{2}):(\d{2})(?:\.(\d{3}))?/);
    if (!match) return dateTimeStr; // 格式不符时原样返回

    const [_, year, month, day, hour, minute, second, millisecond] = match;
    // 构造本地时间的 Date 对象（注意月份从 0 开始）
    if(!(year && month && day && hour && minute && second)){
      return "";
    }
    const msgDate = new Date(
      parseInt(year),
      parseInt(month) - 1,
      parseInt(day),
      parseInt(hour),
      parseInt(minute),
      parseInt(second),
      millisecond ? parseInt(millisecond) : 0
    );

    const now = new Date();

    // 今天的日期（只保留年月日）
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    // 消息日期（只保留年月日）
    const msgDay = new Date(msgDate.getFullYear(), msgDate.getMonth(), msgDate.getDate());

    const diffDays = Math.floor((today.getTime() - msgDay.getTime()) / (24 * 60 * 60 * 1000));

    // 1. 今天
    if (diffDays === 0) {
      const hh = msgDate.getHours().toString().padStart(2, '0');
      const mm = msgDate.getMinutes().toString().padStart(2, '0');
      const ss = msgDate.getSeconds().toString().padStart(2, '0');
      return `${hh}:${mm}:${ss}`;
    }

    // 2. 昨天
    if (diffDays === 1) {
      return '昨天';
    }

    // 3. 判断是否本周内（以周一为一周开始）
    let monday = new Date(today);
    const dayOfWeek = today.getDay(); // 0=周日, 1=周一, ..., 6=周六
    if (dayOfWeek === 0) { // 周日 -> 周一需要减6天
      monday.setDate(today.getDate() - 6);
    } else {
      monday.setDate(today.getDate() - (dayOfWeek - 1));
    }
    monday.setHours(0, 0, 0, 0);

    if (msgDay >= monday) {
      // 本周内：返回星期几（例如 "周三"）
      const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
      return weekdays[msgDate.getDay()] || "";
    }

    // 4. 判断是否今年内
    if (msgDate.getFullYear() === now.getFullYear()) {
      const mm = (msgDate.getMonth() + 1).toString().padStart(2, '0');
      const dd = msgDate.getDate().toString().padStart(2, '0');
      return `${mm}-${dd}`;
    }

    // 5. 去年及以前：返回 YYYY-MM-DD
    const yy = msgDate.getFullYear().toString();
    const mm = (msgDate.getMonth() + 1).toString().padStart(2, '0');
    const dd = msgDate.getDate().toString().padStart(2, '0');
    return `${yy}-${mm}-${dd}`;
  }


  /**
 * 将指定日期格式化为 YYYY-MM 的形式
 * @param date 要格式化的日期（默认当前日期）
 * @returns 格式化后的月份字符串
 */
static formatDateToMonth(date: Date = new Date()): string {
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  return `${year}-${month.toString().padStart(2, '0')}`;
}
 
}
