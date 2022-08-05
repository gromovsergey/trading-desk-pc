import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService}    from './common.service';
import {UserSessionModel} from '../user/user_session.model';

@Injectable()
export class AuthService extends CommonService{
    private redirectUrl: string = '';

    public constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public isLoggedIn(path?: string):boolean {
        if (path) {
            this.redirectUrl = path;
        }
        let userModel   = new UserSessionModel();
        return userModel.isLogged();
    }

    public login(login: string, password: string): Promise<any>{
        return this.httpPost(this.api.auth.login, {
            login:      login,
            password:   password
        }, {}, true);
    }

    public logout(){
        this.httpPost(this.api.auth.logout, {});
    }

    public navigateDefault(role: string, accountId?: number){
        if (this.redirectUrl!=='') {
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
            .then(function (isAllowed) {
                let navigation = '';
                if (isAllowed) {
                    navigation = '/agentreport/total';
                } else if (role === 'AGENCY') {
                    navigation = '/agency/' + accountId + '/advertisers';
                } else if (role === 'ADVERTISER') {
                    navigation = '/advertiser/' + accountId + '/flights';
                }
                this.router.navigateByUrl(navigation);
            }.bind(this));
    }
}