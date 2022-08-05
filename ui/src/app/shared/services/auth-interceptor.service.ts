import {Injectable} from '@angular/core';
import {HttpEvent, HttpInterceptor, HttpHandler, HttpRequest} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {AuthService} from './auth.service';
import {catchError} from 'rxjs/operators';
import {UserSessionModel} from '../../user/models/user-session.model';
import {MatSnackBar} from '@angular/material/snack-bar';
import {L10nStatic} from '../static/l10n.static';
import {Router} from '@angular/router';

@Injectable()
export class AuthInterceptorService implements HttpInterceptor {

  userSession = new UserSessionModel();
  constructor(private authService: AuthService,
              private router: Router,
              private snackBar: MatSnackBar
  ) {
    this.userSession = new UserSessionModel();
  }

  getAuthorizationToken(): string {
    const user = this.authService.user;
    return user ? `${user.token}:${user.key}` : '';
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', this.getAuthorizationToken())
    });
    // if(!this.userSession.isLogged()){
    //   this.router.navigateByUrl('/login').then();
    // }
    // send cloned request with header to the next handler.
    return next.handle(authReq).pipe(
      catchError((err) => {
        if (err.status === 401) {
          UserSessionModel.clear();
          this.router.navigateByUrl('/login');
        }
        // this.snackBar.open(`Error ${err.status}: ${err.statusText}`, L10nStatic.translate('button.close'), {
        //   duration: 3000
        // });

        return throwError(err);
      })
    );
  }
}
