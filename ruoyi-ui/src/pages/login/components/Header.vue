<template>
  <header class="login-header">
    <logo-full-icon class="logo" />
    <div class="operations-container">
      <t-button v-show="false" theme="default" shape="square" variant="text" @click="navToGitHub">
        <logo-github-icon />
      </t-button>
      <t-button v-show="false" theme="default" shape="square" variant="text" @click="navToHelper">
        <help-circle-icon class="icon" />
      </t-button>
      <language-switcher />
      <t-button theme="default" shape="square" variant="text" @click="toggleSettingPanel">
        <setting-icon class="icon" />
      </t-button>
    </div>
  </header>
</template>
<script lang="ts" setup>
import { HelpCircleIcon, LogoGithubIcon, SettingIcon } from 'tdesign-icons-vue-next';

import LogoFullIcon from '@/assets/icons/assets-logo-full.svg?component';
import LanguageSwitcher from '@/components/LanguageSwitcher.vue';
import { useSettingStore } from '@/store';

const settingStore = useSettingStore();

const toggleSettingPanel = () => {
  settingStore.updateConfig({
    showSettingPanel: true,
  });
};

const navToGitHub = () => {
  window.open('https://github.com/tencent/tdesign-vue-next-starter');
};

const navToHelper = () => {
  window.open('http://tdesign.tencent.com/starter/docs/get-started');
};
</script>
<style lang="less" scoped>
.login-header {
  padding: 0 var(--td-comp-paddingLR-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
  backdrop-filter: blur(10px);
  color: var(--td-text-color-primary);
  height: var(--td-comp-size-xxxl);

  .logo {
    width: 178px;
    height: var(--td-comp-size-s);
  }

  .operations-container {
    display: flex;
    align-items: center;

    .t-button {
      margin-left: var(--td-comp-margin-l);
    }
  }
}
</style>
