import { useWebSocket } from '@vueuse/core';
import { NotifyPlugin } from 'tdesign-vue-next';

import { useNoticeStore, useUserStore } from '@/store';

const clientId = import.meta.env.VITE_CLIENT_ID;

// 初始化socket
export const initWebSocket = (url: string) => {
  if (import.meta.env.VITE_APP_WEBSOCKET === 'false') {
    return;
  }
  url = `${url}?Authorization=Bearer ${useUserStore().token}&clientid=${clientId}`;
  useWebSocket(url, {
    autoReconnect: {
      // 重连最大次数
      retries: 3,
      // 重连间隔
      delay: 1000,
      onFailed() {
        console.log('websocket重连失败');
      },
    },
    heartbeat: {
      message: JSON.stringify({ type: 'ping' }),
      // 发送心跳的间隔
      interval: 10000,
      // 接收到心跳response的超时时间
      pongTimeout: 2000,
    },
    onConnected() {
      console.log('websocket已经连接');
    },
    onDisconnected() {
      console.log('websocket已经断开');
    },
    onMessage: (_, e) => {
      if (e.data.indexOf('ping') > 0) {
        return;
      }
      useNoticeStore().addNotice({
        message: e.data,
        read: false,
        time: new Date().toLocaleString(),
      });
      NotifyPlugin('success', {
        title: '消息',
        content: e.data,
        duration: 5000,
        placement: 'bottom-right',
        closeBtn: true,
      });
    },
  });
};
