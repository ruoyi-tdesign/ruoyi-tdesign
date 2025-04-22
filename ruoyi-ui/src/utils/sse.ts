import { useEventSource } from '@vueuse/core';
import { NotifyPlugin } from 'tdesign-vue-next';

import { useNoticeStore, useUserStore } from '@/store';

// 初始化
export const initSSE = (url: string) => {
  if (import.meta.env.VITE_APP_SSE === 'false') {
    return;
  }

  url = `${url}?Authorization=Bearer ${useUserStore().token}&clientid=${import.meta.env.VITE_CLIENT_ID}`;
  const { data, error, close } = useEventSource(url, [], {
    autoReconnect: {
      retries: 10,
      delay: 3000,
      onFailed() {
        console.log('Failed to connect after 10 retries');
      },
    },
  });

  watch(error, () => {
    console.log('SSE connection error:', error.value);
    error.value = null;
  });

  watch(data, () => {
    if (!data.value) return;
    useNoticeStore().addNotice({
      message: data.value,
      read: false,
      time: new Date().toLocaleString(),
    });
    NotifyPlugin('success', {
      title: '消息',
      content: data.value,
      theme: 'success',
      duration: 3000,
      placement: 'bottom-right',
      closeBtn: true,
    });
    data.value = null;
  });
  return {
    close() {
      close();
    },
  };
};
