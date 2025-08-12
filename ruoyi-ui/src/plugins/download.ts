import axios from 'axios';
// @ts-ignore
import { saveAs } from 'file-saver';
import { LoadingPlugin, MessagePlugin } from 'tdesign-vue-next';

import { useUserStore } from '@/store';
import errorCode from '@/utils/errorCode';
import { blobValidate, getVisitUrl } from '@/utils/ruoyi';

const baseURL = import.meta.env.VITE_APP_BASE_API;

export default {
  file(downloadUrl: string, filename?: string) {
    const { token } = useUserStore();
    const url = getVisitUrl(downloadUrl);
    const downloadLoadingInstance = LoadingPlugin({
      text: '正在下载数据，请稍候',
      attach: 'body',
      fullscreen: true,
      zIndex: 99999,
    });
    axios({
      method: 'get',
      url,
      responseType: 'blob',
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        const isBlob = blobValidate(res.data);
        if (isBlob) {
          // 如果未指定文件名，则从响应头解析
          if (!filename && res.headers) {
            // 尝试从download-filename头获取
            if (res.headers['download-filename']) {
              filename = decodeURIComponent(res.headers['download-filename']);
            }
            // 尝试从content-disposition头获取
            else if (res.headers['content-disposition']) {
              const disposition = decodeURIComponent(res.headers['content-disposition']);
              const match = disposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
              if (match && match[1]) {
                filename = match[1].replace(/['"]/g, '');
              }
            }
          }

          // 尝试获取正确的MIME类型
          let mimeType = res.headers['content-type'] || 'application/octet-stream';
          // 修复可能的MIME类型错误
          if (filename.endsWith('.csv')) mimeType = 'text/csv';
          if (filename.endsWith('.json')) mimeType = 'application/json';

          // 创建Blob对象
          const blob = new Blob([res.data], { type: mimeType });
          this.saveAs(blob, filename);
        } else {
          this.printErrMsg(res.data);
        }
      })
      .catch((r) => {
        console.error(r);
        MessagePlugin.error('下载文件出现错误，请联系管理员！');
      })
      .finally(() => downloadLoadingInstance.hide());
  },
  oss(ossId: number | string) {
    const { token } = useUserStore();
    const url = `${baseURL}/resource/oss/download/${ossId}`;
    const downloadLoadingInstance = LoadingPlugin({
      text: '正在下载数据，请稍候',
      attach: 'body',
      fullscreen: true,
      zIndex: 99999,
    });
    axios({
      method: 'get',
      url,
      responseType: 'blob',
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        const isBlob = blobValidate(res.data);
        if (isBlob) {
          const blob = new Blob([res.data], { type: 'application/octet-stream' });
          this.saveAs(blob, decodeURI(res.headers['download-filename']));
        } else {
          this.printErrMsg(res.data);
        }
      })
      .catch((r) => {
        console.error(r);
        MessagePlugin.error('下载文件出现错误，请联系管理员！');
      })
      .finally(() => downloadLoadingInstance.hide());
  },
  zip(url: string, name: string) {
    const { token } = useUserStore();
    url = baseURL + url;
    const downloadLoadingInstance = LoadingPlugin({
      text: '正在下载数据，请稍候',
      attach: 'body',
      fullscreen: true,
    });
    axios({
      method: 'get',
      url,
      responseType: 'blob',
      headers: {
        Authorization: `Bearer ${token}`,
        datasource: localStorage.getItem('dataName'),
      },
    })
      .then((res) => {
        const isBlob = blobValidate(res.data);
        if (isBlob) {
          const blob = new Blob([res.data], { type: 'application/zip' });
          this.saveAs(blob, name);
        } else {
          this.printErrMsg(res.data);
        }
      })
      .catch((r) => {
        console.error(r);
        MessagePlugin.error('下载文件出现错误，请联系管理员！');
      })
      .finally(() => downloadLoadingInstance.hide());
  },
  saveAs(text: any, name: any, opts?: any) {
    saveAs(text, name, opts);
  },
  async printErrMsg(data: { text: () => any }) {
    const resText = await data.text();
    const rspObj = JSON.parse(resText);
    // @ts-ignore
    const errMsg = errorCode[rspObj.code] || rspObj.msg || errorCode.default;
    await MessagePlugin.error(errMsg);
  },
};
