import {LANG_DEFAULT, LANG_STRINGS_EN, LANG_STRINGS_RU, LS_LANG} from '../../const';

export class L10nStatic {
  static translate(value: string, lang?: string): string {
    const langKey = lang || L10nStatic.getLang();
    const langObject = langKey === 'ru' ? LANG_STRINGS_RU : LANG_STRINGS_EN;

    return langObject[value] || value;
  }

  static getLang(): string {
    return window.localStorage.getItem(LS_LANG) || LANG_DEFAULT;
  }

  static getLocale(): string {
    return L10nStatic.getLang() === 'ru' ? 'ru-RU' : 'en-US';
  }
}
