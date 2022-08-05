export class ChannelSearchSessionModel {
  private storageKey = 'channelsSearch';
  private storage = sessionStorage;

  clear(): void {
    this.storage.removeItem(this.storageKey);
  }

  setData(data: ChannelSearchModel): void {
    this.storage.setItem(this.storageKey, JSON.stringify(data));
  }

  getData(): ChannelSearchModel {
    return JSON.parse(this.storage.getItem(this.storageKey)) || {};
  }

  hasData(): boolean {
    return Boolean(this.storage.getItem(this.storageKey));
  }
}
