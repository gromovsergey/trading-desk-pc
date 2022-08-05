import {Component, OnInit, ViewChild, ElementRef} from '@angular/core';
import {FormControl, FormGroup, Validators, FormBuilder} from '@angular/forms';
import {LoginService} from '../../services/login.service';
import {LS_EMAIL, SESSION_LAST_URL} from '../../../const';
import {tap} from 'rxjs/operators';
import {AuthService} from '../../../shared/services/auth.service';
import {Router} from '@angular/router';
import {UserSessionModel} from '../../../user/models/user-session.model';
import {environment} from "../../../../environments/environment";

@Component({
  selector: 'ui-login',
  styleUrls: ['./login.component.scss'],
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
  @ViewChild('password', {static: false}) password: ElementRef;
  public hostName: 'Default' | 'Genius' | 'Pharmatic';
  public submitted: boolean;
  public hasError: boolean;
  public saveEmail: string;
  public loginForm: FormGroup;
  private errorMessage: string;
  private sessionModel: UserSessionModel;

  constructor(
      private formBuilder: FormBuilder,
      private router: Router,
      private loginService: LoginService) {
    this.saveEmail = window.localStorage.getItem(LS_EMAIL);
    this.sessionModel = new UserSessionModel();
    this.hostName = 'Default';
  }

  ngOnInit(): void {
    this.initForm();
    this.isAuth();

    switch (window.location.protocol + '//' + window.location.hostname) {
      case environment.hostGenius:
        this.hostName = 'Genius';
        break;
      case environment.hostPharmatic:
        this.hostName = 'Pharmatic';
        break;
    }
  }

  get redirectUrl(): string {
    return sessionStorage.getItem(SESSION_LAST_URL) || 'defaultPath';
  }

  public onSubmit(): void {
    let userInfo = {
      login: this.loginForm.get('email').value,
      password: this.loginForm.get('password').value
    }

    this.loginForm.get('saveEmail').value ?
        window.localStorage.setItem(LS_EMAIL, this.loginForm.get('email').value) :
        window.localStorage.removeItem(LS_EMAIL);

    this.loginService.login(userInfo).pipe(tap(() => {
      if (window.sessionStorage.getItem('prevEmail') && window.sessionStorage.getItem('prevEmail') !== this.loginForm.get('email').value) {
        window.sessionStorage.removeItem('lu');
        window.sessionStorage.setItem('prevEmail', this.loginForm.get('email').value);
      } else {
        window.sessionStorage.setItem('prevEmail', this.loginForm.get('email').value);
      }
      this.submitted = true;
      this.hasError = false;
    })).subscribe({
      next: (res) => {
        this.sessionModel.user = res as any;
        this.router.navigateByUrl(this.redirectUrl).then();
      },
      error: (error) => {
        this.hasError = true;
        this.submitted = false;
        this.errorMessage = error;
      },
      complete: () => {}
    });
  }

  private initForm(): void {
    this.loginForm = this.formBuilder.group({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required]),
      saveEmail: new FormControl('')
    });
  }

  private isAuth(): void {
    if (this.sessionModel.isLogged()) {
      this.router.navigateByUrl(this.redirectUrl).catch();
    }

    if (this.saveEmail) {
      this.loginForm.get('email').setValue(this.saveEmail);
      this.loginForm.get('saveEmail').setValue(true);
      window.setTimeout(() => {
        this.password.nativeElement.focus();
      });
    }
  }


}
