import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { Router } from '@angular/router';

@Injectable()
export class UserService {
  constructor(private _cookieService: CookieService, private _router: Router) {}

  storeUser(username: string, token: any) {
    const {access_token, refresh_token, expires_in, refresh_expires_in} = token;
    const expireDate = new Date().getTime() + (1000 * expires_in);
    const refreshExpireDate = new Date().getTime() + (1000 * refresh_expires_in);

    this._cookieService.set("logged_username", username, expireDate);//Todo fetch the user info store in localstore
    this._cookieService.set("access_token", access_token, expireDate);
    this._cookieService.set("refresh_token", access_token, refreshExpireDate);
    this._router.navigate(['/home']);
  }

  invalidate() {
    this._cookieService.delete('logged_username');
    this._cookieService.delete('access_token');
    this._cookieService.delete('refresh_token');
    this._router.navigate(['/']);
  }

  isLogged(){
    return this._cookieService.check('access_token');
  }
}
