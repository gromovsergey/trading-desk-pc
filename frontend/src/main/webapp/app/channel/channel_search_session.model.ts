export class ChannelSearchSessionModel {
    private _key: string = 'channelsSearch';
    private storage = sessionStorage;

    public clear() {
        this.storage.removeItem(this._key);
    }

    public setData(data: ChannelSearchModel) {
        this.storage.setItem(this._key, JSON.stringify(data));
    }

    public getData(): ChannelSearchModel {
        return JSON.parse(this.storage.getItem(this._key)) || {};
    }

    public hasData():boolean {
        return Boolean(this.storage.getItem(this._key));
    }
}

export class ChannelSearchModel {
    public name: string = '';
    public accountId: string = '';
    public channelType: string = '';
    public visibility: string = '';
}