import {AdvertiserModel} from './advertiser.model';

export class AdvertiserSessionModel extends AdvertiserModel {

  private advertiserKey = 'advertiser';
  private storage = sessionStorage;

  set data(data: AdvertiserModel) {
    this.storage.setItem(this.advertiserKey, JSON.stringify(data));
  }

  get data(): AdvertiserModel {
    return this._getData();
  }

  get id(): number {
    return this._getData().id;
  }

  get name(): string {
    return this._getData().name;
  }

  get displayStatus(): string {
    return this._getData().displayStatus;
  }

  get currencyCode(): string {
    return this._getData().currencyCode;
  }

  get countryCode(): string {
    return this._getData().countryCode;
  }

  get timeZone(): string {
    return this._getData().timeZone;
  }

  hasData(): boolean {
    return Boolean(this.storage.getItem(this.advertiserKey));
  }

  clear(): AdvertiserSessionModel {
    this.storage.removeItem(this.advertiserKey);
    return this;
  }

  private _getData(): AdvertiserModel {
    return JSON.parse(this.storage.getItem(this.advertiserKey)) || {};
  }
}
