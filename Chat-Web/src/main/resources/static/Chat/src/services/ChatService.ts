import {nextTick, type Ref} from "vue";
import type {chat, ChatGroup} from "../types/chat";
import type {ElScrollbar} from "element-plus";
import {throttle} from "lodash";
import {Log} from "../utils/TipUtil";
import {BusinessError} from "../exception/BusinessError";
import {emojiStore} from "@/store/EmojiStore";
import {MsgType} from "@/enums/GroupMsgType";
import {MdUtil} from "@/utils/md";

/**
 * 聊天通用业务逻辑服务类
 */
export class ChatService {
  /**
   * 消息排序（升序）- 私聊
   * @param historyMsgList 消息列表
   */
  static sortMsgs(historyMsgList: chat[]) {
    historyMsgList.sort((a, b) => (a.sendTime || 0) - (b.sendTime || 0));
    return historyMsgList;
  }

  /**
   * 消息排序（升序）- 群聊
   * @param historyMsgList 消息列表
   */
  static sortGroupMsgs(historyMsgList: ChatGroup[]) {
    historyMsgList.sort((a, b) => (a.createTime || 0) - (b.createTime || 0));
    return historyMsgList;
  }

  /**
   * 生成Snowflake-like的数字ID (兼容BIGINT)
   * 格式: 13位时间戳 + 6位随机数 (共19位)
   * @returns string (因为JS Number精度问题，必须返回字符串)
   */
  static generateUUID(): string {
    const timestamp = Date.now().toString(); // 13 digits
    const random = Math.floor(Math.random() * 1000000)
      .toString()
      .padStart(6, "0"); // 6 digits
    return timestamp + random;
  }

  /**
   * Ctrl+Enter 换行处理
   * @param event 键盘事件
   * @param inputVal 输入框绑定的响应式变量
   */
  static handleCtrlEnter(event: Event, inputVal: Ref<string>) {
    event.preventDefault(); // 阻止默认行为
    // 给输入框添加换行符
    inputVal.value += "\n";
    // 强制更新输入框光标位置
    nextTick(() => {
      const target = event.target as HTMLTextAreaElement | HTMLInputElement;
      if (target) {
        target.focus();
        // 尝试设置光标位置，部分 input 类型可能不支持
        try {
          if (target.setSelectionRange) {
            target.setSelectionRange(target.value.length, target.value.length);
          }
        } catch (e) {
          // 忽略不支持 setSelectionRange 的情况
        }
      }
    });
  }

  /**
   * 滚动到底部
   * @param wrapEl Element Plus Scrollbar 实例引用
   */
  static scrollToBottom(wrapEl: any) {
    if (!wrapEl) return;
    // 兼容 Element Plus Scrollbar 的 wrapRef 或 wrap$ 属性
    const wrap = wrapEl.wrapRef || wrapEl;
    if (!wrap || !wrap.scrollTo) return;

    // 确保 DOM 更新后再滚动
    nextTick(() => {
      wrap.scrollTo({
        top: wrap.scrollHeight,
      });
    });
  }

  /**
   * 滚动事件处理（防抖）
   */
  static handleScroll = throttle(
    (
      params: { scrollLeft: number; scrollTop: number },
      isLoading: Ref<boolean>,
      wrapEl: Ref<InstanceType<typeof ElScrollbar> | undefined>,
      handleToTherShold: () => void,
    ) => {
      if (isLoading.value) return;

      try {
        if (wrapEl.value) {
          const wrap = wrapEl.value.wrapRef;
          if (!wrap) return;

          const HEIGHT = wrap.clientHeight; //视口高度
          const HEIGHT_ALL = wrap.scrollHeight; //容器总高度

          if (HEIGHT === 0 || HEIGHT_ALL === 0) return;

          const WRAPABLE_HEIGHT = HEIGHT_ALL - HEIGHT; //可滚动高度

          //计算百分比
          if (!HEIGHT) return;

          const rate = (params.scrollTop / WRAPABLE_HEIGHT) * 100; //占比

          if (rate !== undefined && rate < 30) {
            handleToTherShold();
          }
        }
      } catch (error) {
        if (error instanceof BusinessError) {
          Log.error(error.message);
        } else {
          Log.error("服务繁忙");
          console.error(error);
        }
        isLoading.value = false;
      }
    },
    200,
  );

  /**
   * 触发加载阈值处理
   */
  static async handleToTherShold(
    isLoading: Ref<boolean>,
    wrapEl: Ref<InstanceType<typeof ElScrollbar> | undefined>,
    loadHistoryMsgs: () => Promise<void>,
  ) {
    if (isLoading.value || !wrapEl.value) return;

    isLoading.value = true; // 立即设置加载状态，防止重复触发

    const wrap = wrapEl.value.wrapRef;
    if (!wrap) {
      isLoading.value = false;
      return;
    }
    const oldHeight = wrap.scrollHeight;
    const oldTop = wrap.scrollTop;

    try {
      await loadHistoryMsgs();
      //新高度 = 旧高度+页面新增的高度
      // 使用 nextTick 确保 DOM 更新后再设置滚动位置
      nextTick(() => {
        if (wrap) {
          wrap.scrollTop = oldTop + (wrap.scrollHeight - oldHeight);
        }
      });
    } catch (error) {
      if (error instanceof BusinessError) {
        Log.error(error.message);
      } else {
        Log.error("服务繁忙");
        console.error("加载历史消息失败", error);
      }
    } finally {
      isLoading.value = false;
    }
  }

  /**
   * 处理表情
   * @param msg 消息列表
   */
  static async handleEmoji(msg?: chat[], msgOfGroup?: ChatGroup[]) {
    const emojis = emojiStore();

    let list: string[] = [];
    let data = msg || msgOfGroup;

    if (!data) return;
    data.forEach((item) => {
      if (item.type === MsgType.EMOJI) list.push(item.msg);
    });
    const res = await emojis.queryEmojis(list);
    data.map((item) => {
      if (item.type === MsgType.EMOJI)
        item.emoji = res.find((emoji) => emoji.id === item.msg);
    });
  }

  static async handleMd(msg?: chat[], msgOfGroup?: ChatGroup[]) {
    const list = msg || msgOfGroup || [];
    for (const msg of list) {
      if (msg.type === MsgType.AI && msg.msg) {
        msg.msg = await MdUtil.parse(msg.msg);
      }
    }
  }
}
