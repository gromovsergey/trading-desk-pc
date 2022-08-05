import {Injectable} from '@angular/core';
import {Http}       from '@angular/http';
import {Router}     from '@angular/router';

import {CommonService} from '../common/common.service';
import {UserModel}     from './user.model';


@Injectable()
export class UserService extends CommonService {

    public constructor(public router: Router, public http: Http){
        super(router, http);
    }

    public getProfile(authToken: string): Promise<UserModel>{
        return this.httpGet(this.api.user.profile);
    }

    public getUserRoles(accountId: number): Promise<any>{
        return this.httpGet(this.api.user.role, {
            accountId: accountId
        });
    }

    public getAccountUsers(accountId: number): Promise<any>{
        return this.httpGet(this.api.user.getAll, {
            accountId: accountId
        });
    }

    public getAdvertisersByUser(userId: number): Promise<any>{
        return this.httpGet(this.api.user.advertiser, {
            userId: userId
        });
    }

    public getAdvertisersByAgency(accountId: number): Promise<any>{
        return this.httpGet(this.api.user.advertiser, {
            accountId: accountId
        });
    }

    public getUserById(userId: number): Promise<any>{
        return this.httpGet(this.api.user.get, {
            userId: userId
        });
    }

    public save(user: any): Promise<any>{
        return this.httpPost(this.api.user.get, user);
    }

    public update(user: any): Promise<any>{
        return this.httpPut(this.api.user.get, user);
    }

    public statusChange(userId: number, name: string): Promise<any>{
        return this.httpPut(this.api.user.status, {}, {
            userId: userId,
            name: name
        });
    }

    public passwordChange(oldPassword: string, newPassword: string, confirmNewPassword: string): Promise<any>{
        return this.httpPut(this.api.user.changePassword, {
            oldPassword: oldPassword,
            newPassword: newPassword,
            confirmNewPassword: confirmNewPassword
        });
    }
}