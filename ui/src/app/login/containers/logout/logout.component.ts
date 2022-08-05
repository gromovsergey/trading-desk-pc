import {Component, OnInit} from '@angular/core';
import {LoginService} from '../../services/login.service';
import {LS_USER, SESSION_LAST_URL} from '../../../const';
import {Router} from '@angular/router';
import {AuthService} from "../../../shared/services/auth.service";

@Component({
  selector: 'ui-logout',
  template: `{{'blockName.logout' | translate}}`
})

export class LogoutComponent implements OnInit {

  constructor(
      private authService: AuthService,
      private router: Router,
      private loginService: LoginService) {
  }

  ngOnInit(): void {
    //this.loginService.logout().subscribe();
    window.localStorage.removeItem(LS_USER);
    this.router.navigateByUrl('/login');
  }
}
