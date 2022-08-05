export class DynamicLocalizationModel {
    public key: string = '';
    public value: string = '';
    public lang: string = '';

    constructor(lang?: string) {
        if (lang) {
            this.lang = lang;
        }
    }
}