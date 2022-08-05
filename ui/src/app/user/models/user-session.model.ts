import {LS_USER} from '../../const';

export class UserSessionModel {

  constructor() {
  }

  static clear(): void {
    window.localStorage.removeItem(LS_USER);
  }

  set user(data: UserModel) {
    data.date_token = new Date();
    window.localStorage.setItem(LS_USER, JSON.stringify(data));
  }

  get user(): UserModel {
    return JSON.parse(window.localStorage.getItem(LS_USER)) || null;
  }

  get token(): string {
    return this.user ? this.user.token : null;
  }

  get key(): string {
    return this.user ? this.user.key : null;
  }

  get role(): string {
    return this.user ? this.user.role : null;
  }

  get date_token(): Date {
    return this.user ? new Date(this.user.date_token) : null;
  }

  get accountId(): number {
    return this.user ? this.user.accountId : null;
  }

  public isLogged(): boolean {
    // active 2 hours
    if (this.date_token) {
      const active = (new Date()).getTime() - this.date_token.getTime() < 1000*60*60*2;
      return Boolean(this.token && this.key && active);
    } else {
      return false;
    }
  }

  public isInternal(): boolean {
    return this.role === 'INTERNAL';
  }
}
