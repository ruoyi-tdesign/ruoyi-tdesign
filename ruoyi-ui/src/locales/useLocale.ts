import { useLocalStorage } from '@vueuse/core';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

import { t } from '@/locales';
import { i18n, langCode, localeConfigKey } from '@/locales/index';
import message from '@/plugins/modal';

export function useLocale() {
  const { locale } = useI18n({ useScope: 'global' });
  function changeLocale(lang: string) {
    // 如果切换的语言不在对应语言文件里则默认为简体中文
    if (!langCode.includes(lang)) {
      lang = 'zh_CN';
    }

    locale.value = lang;
    useLocalStorage(localeConfigKey, 'zh_CN').value = lang;
    message.msgSuccess(t('constants.switchLanguage'));
  }

  const getComponentsLocale = computed(() => {
    // @ts-expect-error 应该有值
    return i18n.global.getLocaleMessage(locale.value).componentsLocale;
  });

  return {
    changeLocale,
    getComponentsLocale,
    locale,
  };
}
