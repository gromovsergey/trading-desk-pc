import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Router} from '@angular/router';
import {API} from '../../const';
import {CommonService} from '../../common/services/common.service';


@Injectable()
export class UserService extends CommonService {

  public constructor(public router: Router, public http: HttpClient) {
    super(router, http);
  }

  public getProfile(): Promise<UserModel> {
    return this.httpGet(API.user.profile);
  }

  public getUserRoles(accountId: number): Promise<any> {
    return this.httpGet(API.user.role, {accountId: accountId.toString()});
  }

  public getAccountUsers(accountId: number): Promise<any> {
    return this.httpGet(API.user.getAll, {
      accountId: accountId.toString()
    });
  }

  public getAdvertisersByUser(userId: number): Promise<any> {
    return this.httpGet(API.user.advertiser, {
      userId: userId.toString()
    });
  }

  public getAdvertisersByAgency(accountId: number): Promise<any> {
    return this.httpGet(API.user.advertiser, {
      accountId: accountId.toString()
    });
  }

  public getUserById(userId: number): Promise<any> {
    return this.httpGet(API.user.get, {
      userId: userId.toString()
    });
  }

  public save(user: any): Promise<any> {
    return this.httpPost(API.user.get, user);
  }

  public update(user: any): Promise<any> {
    return this.httpPut(API.user.get, user);
  }

  public statusChange(userId: number, name: string): Promise<any> {
    return this.httpPut(API.user.status, {}, {
      name, userId: userId.toString()
    });
  }

  public passwordChange(oldPassword: string, newPassword: string, confirmNewPassword: string): Promise<any> {
    return this.httpPut(API.user.changePassword, {
      oldPassword,
      newPassword,
      confirmNewPassword
    });
  }
}
