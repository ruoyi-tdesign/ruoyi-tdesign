<template>
  <t-popup expand-animation placement="bottom-right" trigger="click">
    <template #content>
      <div class="header-msg">
        <div class="header-msg-top">
          <p>{{ t('layout.notice.title') }}</p>
          <t-button
            v-if="unreadMsg.length > 0"
            class="clear-btn"
            variant="text"
            theme="primary"
            @click="setRead('all')"
          >
            {{ t('layout.notice.clear') }}
          </t-button>
        </div>
        <t-list v-if="unreadMsg.length > 0" class="narrow-scrollbar" style="max-height: 366px" :split="false">
          <t-list-item v-for="(item, index) in unreadMsg" :key="index">
            <div>
              <p class="msg-content">{{ item.message }}</p>
              <p class="msg-type">{{ getType(item.message) }}</p>
            </div>
            <p class="msg-time">{{ item.time }}</p>
            <template #action>
              <t-button size="small" variant="outline" @click="setRead('radio', item)">
                {{ t('layout.notice.setRead') }}
              </t-button>
            </template>
          </t-list-item>
        </t-list>

        <div v-else class="empty-list">
          <img :src="Nothing" alt="空" />
          <p>{{ t('layout.notice.empty') }}</p>
        </div>
        <div v-if="unreadMsg.length > 0" class="header-msg-bottom">
          <t-button class="header-msg-bottom-link" variant="text" theme="default" block @click="goDetail">
            {{ t('layout.notice.viewAll') }}
          </t-button>
        </div>
      </div>
    </template>
    <t-badge :count="unreadMsg.length" :offset="[4, 4]">
      <t-button theme="default" shape="square" variant="text">
        <mail-icon />
      </t-button>
    </t-badge>
  </t-popup>
</template>

<script lang="ts" setup>
import { storeToRefs } from 'pinia';
import { MailIcon } from 'tdesign-icons-vue-next';
import { onBeforeUnmount, onMounted } from 'vue';
import { useRouter } from 'vue-router';

import Nothing from '@/assets/images/nothing.png';
import { t } from '@/locales';
import { useNoticeStore, useSSE } from '@/store';
import type { NoticeItem } from '@/types/interface';
import { initWebSocket } from '@/utils/websocket';

onMounted(() => {
  const protocol = window.location.protocol === 'https:' ? 'wss://' : 'ws://';
  const baseApi = import.meta.env.VITE_APP_BASE_API;
  let address = `${protocol}${window.location.host}${baseApi}`;
  if (baseApi.startsWith('http')) {
    address = baseApi.replace('http', 'ws');
  }
  initWebSocket(`${address}/resource/websocket`);
});

onMounted(() => {
  useSSE().execute(`${import.meta.env.VITE_APP_BASE_API}/resource/sse/connect/login`);
});

onBeforeUnmount(() => {
  useSSE().close();
});

const router = useRouter();
const store = useNoticeStore();
const { readAll } = store;
const { unreadMsg } = storeToRefs(store);

const setRead = (type: string, item?: NoticeItem) => {
  if (type === 'all') {
    readAll();
  } else {
    item.read = true;
  }
};

function getType(content: string) {
  if (content) {
    const array = content.match(/^\[(\S+)].*/);
    return array.at(1);
  }
  return '';
}

const goDetail = () => {
  router.push('/system/notice');
};
</script>

<style lang="less" scoped>
.header-msg {
  width: 400px;
  // height: 440px;
  margin: calc(0px - var(--td-comp-paddingTB-xs)) calc(0px - var(--td-comp-paddingLR-s));

  .empty-list {
    // height: calc(100% - 120px);
    text-align: center;
    padding: var(--td-comp-paddingTB-xxl) 0;
    font: var(--td-font-body-medium);
    color: var(--td-text-color-secondary);

    img {
      width: var(--td-comp-size-xxxxl);
    }

    p {
      margin-top: var(--td-comp-margin-xs);
    }
  }

  &-top {
    position: relative;
    font: var(--td-font-title-medium);
    color: var(--td-text-color-primary);
    text-align: left;
    padding: var(--td-comp-paddingTB-l) var(--td-comp-paddingLR-xl) 0;
    display: flex;
    align-items: center;
    justify-content: space-between;

    .clear-btn {
      right: calc(var(--td-comp-paddingTB-l) - var(--td-comp-paddingLR-xl));
    }
  }

  &-bottom {
    align-items: center;
    display: flex;
    justify-content: center;
    padding: var(--td-comp-paddingTB-s) var(--td-comp-paddingLR-s);
    border-top: 1px solid var(--td-component-stroke);

    &-link {
      text-decoration: none;
      cursor: pointer;
      color: var(--td-text-color-placeholder);
    }
  }

  .t-list {
    height: calc(100% - 104px);
    padding: var(--td-comp-margin-s) var(--td-comp-margin-s);
  }

  .t-list-item {
    overflow: hidden;
    width: 100%;
    padding: var(--td-comp-paddingTB-l) var(--td-comp-paddingLR-l);
    border-radius: var(--td-radius-default);
    font: var(--td-font-body-medium);
    color: var(--td-text-color-primary);
    cursor: pointer;
    transition: background-color 0.2s linear;

    &:hover {
      background-color: var(--td-bg-color-container-hover);

      .msg-content {
        color: var(--td-brand-color);
      }

      .t-list-item__action {
        button {
          bottom: var(--td-comp-margin-l);
          opacity: 1;
        }
      }

      .msg-time {
        bottom: -6px;
        opacity: 0;
      }
    }

    .msg-content {
      margin-bottom: var(--td-comp-margin-s);
    }

    .msg-type {
      color: var(--td-text-color-secondary);
    }

    .t-list-item__action {
      button {
        opacity: 0;
        position: absolute;
        right: var(--td-comp-margin-xxl);
        bottom: -6px;
      }
    }

    .msg-time {
      transition: all 0.2s ease;
      opacity: 1;
      position: absolute;
      right: var(--td-comp-margin-xxl);
      bottom: var(--td-comp-margin-l);
      color: var(--td-text-color-secondary);
    }
  }
}
</style>
