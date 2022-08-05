import { Component, OnInit } from '@angular/core';
import { RouterModule }      from '@angular/router';

import { LoadingComponent } from '../shared/loading.component';
import { CellComponent }    from '../shared/cell.component';
import { PanelComponent }   from '../shared/panel.component';
import { UserSessionModel } from './user_session.model';
import { UserService }      from './user.service';
import { UserModel }        from './user.model';

@Component({
    selector: 'ui-my-settings',
    templateUrl: 'my_settings.html'
})

export class MySettingsComponent implements OnInit {

    public wait: boolean       = true;
    private sessionUserData: UserSessionModel = new UserSessionModel();
    private userSettings: UserModel = new UserModel();
    private oldPassword: string;
    private newPassword: string;
    private confirmNewPassword: string;
    private notificationMessage: string;
    private waitSubmit: boolean = false;

    private errors  = {
        oldPassword: null,
        newPassword: null,
        confirmNewPassword: null
    };

    constructor(private userService: UserService){}

    ngOnInit(){
        this.userService
            .getProfile(this.sessionUserData.token)
            .then(foundUser => {
                Object.assign(this.userSettings, foundUser);
                this.wait = false;
            });
    }

    private doSubmit(e: any){
        e.preventDefault();
        if (this.waitSubmit) return;
        this.notificationMessage = null;

        this.waitSubmit = true;
        this.userService.passwordChange(this.oldPassword, this.newPassword, this.confirmNewPassword)
            .then(res   => {
                this.oldPassword = null;
                this.newPassword = null;
                this.confirmNewPassword = null;

                this.errors.oldPassword = null;
                this.errors.newPassword = null;
                this.errors.confirmNewPassword = null;

                this.notificationMessage = '_L10N_(agencyAccount.notification.user.passwordChangeSucceeded)';
                this.waitSubmit = false;
            })
            .catch(e => {
                this.waitSubmit = false;
                this.errors = e.json();
            });

    }
}
