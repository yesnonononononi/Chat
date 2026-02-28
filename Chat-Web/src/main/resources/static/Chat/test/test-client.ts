import {WebSocketClient} from "../src/utils/WebSocket/websocket.ts";
import readline from "node:readline";
import {stdin as input, stdout as output} from "node:process";

const client = new WebSocketClient("ws://localhost:8080/ws/message", 5);

const rl = readline.createInterface({ input, output });

function menu() {
  console.log("1 连接");
  console.log("2 发送消息");
  console.log("3 发送心跳");
  console.log("4 关闭连接");
  console.log("5 查看状态");
  console.log("6 退出");
  rl.setPrompt("> ");
  rl.prompt();
}

async function autoTest() {
  const ws = client.connect("ws://localhost:8080/ws/message");
  if (!ws) {
    console.error("连接初始化失败");
    return;
  }
  const testMsg = { type: "data", msg: "自动测试消息1" };
  await client.send(testMsg, true);
  setTimeout(async () => {
    client.close();
    const offlineMsg = { type: "data", msg: "自动断连后消息2" };
    await client.send(offlineMsg, true);
  }, 8000);
  const timer = setInterval(() => {
    const state = client.getLinkState();
    const map: Record<number, string> = {
      0: "CONNECTING",
      1: "OPEN",
      2: "CLOSING",
      3: "CLOSED",
    };
    console.log(`${map[state] || "UNKNOWN"}（重连次数：${client.currentRestartNum}）`);
  }, 2000);
  setTimeout(() => {
    clearInterval(timer);
    try {
      client.close();
    } catch {}
    process.exit(0);
  }, 35000);
}

const isAuto = process.argv.includes("--auto");
if (!isAuto) {
  menu();
}

rl.on("line", async (line) => {
  const choice = line.trim();
  if (choice === "1") {
    const ws = client.connect("ws://localhost:8080/ws/message");
    if (!ws) {
      console.error("连接初始化失败");
    }
  } else if (choice === "2") {
    rl.question("输入消息: ", async (text) => {
      await client.send({ type: "data", msg: text }, true);
      rl.setPrompt("> ");
      rl.prompt();
    });
    return;
  } else if (choice === "3") {
    await client.send({ type: "notice", msg: "ping" }, false);
  } else if (choice === "4") {
    client.close();
  } else if (choice === "5") {
    const state = client.getLinkState();
    const map: Record<number, string> = {
      0: "CONNECTING",
      1: "OPEN",
      2: "CLOSING",
      3: "CLOSED",
    };
    console.log(`${map[state] || "UNKNOWN"}（重连次数：${client.currentRestartNum}）`);
  } else if (choice === "6") {
    rl.close();
    process.exit(0);
  } else {
    console.log("无效指令");
  }
  rl.setPrompt("> ");
  rl.prompt();
});

rl.on("close", () => {
  try {
    client.close();
  } catch {}
});

if (isAuto) {
  autoTest();
}
