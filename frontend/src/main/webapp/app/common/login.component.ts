import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Router, RouterModule }                                    from '@angular/router';

import { PageComponent }    from '../shared/page.component';
import { IconComponent }    from '../shared/icon.component';
import { UserSessionModel } from '../user/user_session.model';

import { AuthService }      from './auth.service';

@Component({
    selector: 'ui-login',
    templateUrl: 'login.html'
})

export class LoginComponent extends PageComponent implements OnInit, AfterViewInit {

    public title:string;

    public email: string       = '';
    public password: string    = '';
    public saveMail: boolean   = false;
    private saveMailKey: string = 'mail';

    public hasError: boolean   = false;
    public onSubmit: boolean   = false;

    @ViewChild('inp_email') inputEmail: ElementRef;
    @ViewChild('inp_pass') inputPass: ElementRef;


    constructor(private authService: AuthService,
                private router: Router){
        super();
    }

    ngOnInit() {
        switch (this.router.url){
            case '/logout':
                this.logout();
                break;
            default:
                let email    = localStorage.getItem(this.saveMailKey);
                if (email) {
                    this.email      = email;
                    this.saveMail   = true;
                }
        }
        this.title = '_L10N_(login.blockName.login)';
    }

    ngAfterViewInit(){
        if (this.inputPass && localStorage.getItem(this.saveMailKey)){
            this.inputPass.nativeElement.focus();
        } else if (this.inputEmail) {
            this.inputEmail.nativeElement.focus();
        }
    }

    login() {
        this.hasError   = false;
        this.onSubmit   = true;
        this.saveMail ? localStorage.setItem(this.saveMailKey, this.email) : localStorage.removeItem(this.saveMailKey);
        this.authService.login(this.email, this.password).then((response) => {
            let user    = new UserSessionModel();
            user.data   = response;

            if (this.authService.isLoggedIn()) {
                this.authService.navigateDefault(user.role, user.accountId);
            } else {
                user.clear();
                this.hasError   = true;
                this.onSubmit   = false;
            }
        }).catch(err => {
            if (err.status  === 401){
                this.hasError   = true;
                this.onSubmit   = false;
            } else {
                this.router.navigate(['/error']);
            }
        });
    }

    logout() {
        this.authService.logout();
        this.router.navigate(['/login']);
    }
}
