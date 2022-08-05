import { Injectable }          from '@angular/core';
import { CanActivate, CanActivateChild, CanLoad,
         Router, ActivatedRouteSnapshot, RouterStateSnapshot, Route } from '@angular/router';
import { AuthService }         from './auth.service';

@Injectable()
export class AuthGuard implements CanActivate, CanActivateChild, CanLoad {

    constructor(private authService: AuthService, private router: Router) {}

    canActivate(route: ActivatedRouteSnapshot|Route, state?: RouterStateSnapshot): boolean {
        if (this.authService.isLoggedIn(state && state.url)) {
            return true;
        }
        this.router.navigate(['/login']);
        return false;
    }
    
    canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
        return this.canActivate(route, state);
    }
    
    canLoad(route: Route): boolean {
        return this.canActivate(route);
    }
}