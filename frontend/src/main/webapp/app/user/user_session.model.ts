import {UserModel} from "./user.model";

export class UserSessionModel {

    private _key: string = 'user';
    private storage = localStorage;


    public clear():UserSessionModel{
        this.storage.removeItem(this._key);
        return this;
    }

    private _getData():UserModel{
        return JSON.parse(this.storage.getItem(this._key))||{};
    }

    set data(data:UserModel){
        this.storage.setItem(this._key, JSON.stringify(data));
    }

    get token():string{
        return this._getData().token || null;
    }

    get key():string{
        return this._getData().key || null;
    }

    get role():string {
        return this._getData().role || null;
    }

    get accountId():number {
        return this._getData().accountId || null;
    }

    public isLogged(): boolean{
        return Boolean(this.token && this.key);
    }

    public isInternal(){
        return this.role === 'INTERNAL';
    }
}