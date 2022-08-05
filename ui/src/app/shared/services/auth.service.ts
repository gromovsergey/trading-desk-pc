import {Injectable} from '@angular/core';
import {LS_USER} from '../../const';

@Injectable()
export class AuthService {

  get user(): User {
    return JSON.parse(window.localStorage.getItem(LS_USER));
  }

  get isLoggedIn(): boolean {
    return !!this.user;
  }

  public constructor() {
  }
}
