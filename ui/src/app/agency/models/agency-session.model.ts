import {SESSION_AGENCY} from '../../const';

export class AgencySessionModel {

  constructor() {
  }

  static clear(): void {
    window.sessionStorage.removeItem(SESSION_AGENCY);
  }

  set data(data: AgencyModel) {
    window.sessionStorage.setItem(SESSION_AGENCY, JSON.stringify(data));
  }

  get data(): AgencyModel {
    return JSON.parse(window.sessionStorage.getItem(SESSION_AGENCY)) || null;
  }

  get id(): number {
    return this.data ? this.data.id : null;
  }

  get name(): string {
    return this.data ? this.data.name : null;
  }

  get displayStatus(): string {
    return this.data ? this.data.displayStatus : null;
  }

  get currencyCode(): string {
    return this.data ? this.data.currencyCode : null;
  }

  get timeZone(): string {
    return this.data ? this.data.timeZone : null;
  }

  hasData(): boolean {
    return Boolean(window.sessionStorage.getItem(SESSION_AGENCY));
  }
}
