import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {API, LS_USER} from '../../const';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {shareReplay} from "rxjs/operators";

export interface IUser {
  login: string;
  password: string;
}

export interface IUser {
  login: string;
  password: string;
}

@Injectable()
export class LoginService {

  private host = environment.host;

  public constructor(
    private router: Router,
    private http: HttpClient) {
  }

  public isLoggedIn(): boolean {
    return !!window.localStorage.getItem(LS_USER);
  }

  public login(user: IUser): Observable<IUser> {
    return this.http.post<IUser>(this.host + API.auth.login, user).pipe(shareReplay(1));
  }

  public logout(): Observable<any> {
    return this.http.post(this.host + API.auth.logout, {});
  }

  /*
  public navigateDefault(role: string, accountId?: number) {
    if (this.redirectUrl !== '') {
      this.router.navigateByUrl(this.redirectUrl);
      this.redirectUrl = '';
      return;
    }

    if (role === 'INTERNAL') {
      let navigation = sessionStorage.getItem('lu') || '';
      if (navigation === '/login' || navigation === '/logout') {
        navigation = '';
      }
      this.router.navigateByUrl(navigation);
      return;
    }

    if (role === 'PUBLISHER') {
      let navigation = '/report/publisher/' + accountId;
      this.router.navigateByUrl(navigation);
      return;
    }

    this.isAllowedLocal0('agentReport.view')
      .then(function(isAllowed) {
        let navigation = '';
        if (isAllowed) {
          navigation = '/agent-report/total';
        } else if (role === 'AGENCY') {
          navigation = '/agency/' + accountId + '/advertisers';
        } else if (role === 'ADVERTISER') {
          navigation = '/advertiser/' + accountId + '/flights';
        }
        this.router.navigateByUrl(navigation);
      }.bind(this));
  }
   */
}
