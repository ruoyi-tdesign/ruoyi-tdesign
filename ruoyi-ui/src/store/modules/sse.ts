import { initSSE } from '@/utils/sse';

export const useSSE = defineStore('sse', () => {
  const closeRef = ref();

  function execute(url: string) {
    const { close } = initSSE(url);
    closeRef.value = close;
  }

  async function close() {
    closeRef.value?.();
    closeRef.value = null;
  }

  return {
    close,
    execute,
  };
});
