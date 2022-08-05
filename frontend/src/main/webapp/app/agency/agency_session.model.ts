import {AgencyModel} from "./agency.model";

export class AgencySessionModel extends AgencyModel {

    private _key: string = 'agency';
    private storage = sessionStorage;


    public clear(): AgencySessionModel{
        this.storage.removeItem(this._key);
        return this;
    }

    private _getData(): AgencyModel{
        return JSON.parse(this.storage.getItem(this._key))||{};
    }

    set data(data: AgencyModel){
        this.storage.setItem(this._key, JSON.stringify(data));
    }

    get data(): AgencyModel{
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

    get timeZone():string {
        return this._getData().timeZone;
    }

    public hasData():boolean {
        return Boolean(this.storage.getItem(this._key));
    }
}