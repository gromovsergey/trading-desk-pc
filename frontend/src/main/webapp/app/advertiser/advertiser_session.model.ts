import {AdvertiserModel} from './advertiser.model';

export class AdvertiserSessionModel extends AdvertiserModel {

    private _key: string = 'advertiser';
    private storage = sessionStorage;


    public clear(): AdvertiserSessionModel{
        this.storage.removeItem(this._key);
        return this;
    }

    private _getData():AdvertiserModel{
        return JSON.parse(this.storage.getItem(this._key))||{};
    }

    set data(data:AdvertiserModel){
        this.storage.setItem(this._key, JSON.stringify(data));
    }

    get data(): AdvertiserModel{
        return this._getData();
    }

    get id():number {
        return this._getData().id;
    }

    get name():string {
        return this._getData().name;
    }

    get displayStatus():string {
        return this._getData().displayStatus;
    }

    get currencyCode():string {
        return this._getData().currencyCode;
    }

    get countryCode():string {
        return this._getData().countryCode;
    }

    get timeZone():string {
        return this._getData().timeZone;
    }

    public hasData():boolean {
        return Boolean(this.storage.getItem(this._key));
    }
}