import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {CommonService} from './common.service';
import {UserSessionModel} from '../../user/models/user-session.model';

@Injectable()
export class AuthService extends CommonService {
  private redirectUrl = '';

  constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  isLoggedIn(path?: string): boolean {
    if (path) {
      this.redirectUrl = path;
    }
    return new UserSessionModel().isLogged();
  }

  login(login: string, password: string): Promise<any> {
    return this.httpPost(this.api.auth.login, {
      login,
      password
    }, {});
  }

  logout(): void {
    this.httpPost(this.api.auth.logout).catch(err => console.error(err));
  }

  navigateDefault(role: string, accountId?: number): void {
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
      const navigation = '/report/publisher/' + accountId;
      this.router.navigateByUrl(navigation);
      return;
    }

    this.isAllowedLocal0('agentReport.view')
      .then((isAllowed) => {
        let navigation = '';
        if (isAllowed) {
          navigation = '/agent-report/total';
        } else if (role === 'AGENCY') {
          navigation = '/agency/' + accountId + '/advertisers';
        } else if (role === 'ADVERTISER') {
          navigation = '/advertiser/' + accountId + '/flights';
        }
        this.router.navigateByUrl(navigation);
      });
  }
}
