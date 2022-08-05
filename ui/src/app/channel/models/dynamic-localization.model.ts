export class DynamicLocalizationModel {
  key = '';
  value = '';
  lang = '';

  constructor(lang?: string) {
    if (lang) {
      this.lang = lang;
    }
  }
}
