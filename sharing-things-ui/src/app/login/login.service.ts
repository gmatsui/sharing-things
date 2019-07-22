import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@environment';

@Injectable()
export class LoginService {
  private _loginUrl: string;
  constructor(private _http: HttpClient) {
    this._loginUrl = `${environment.loginUrl}`;
  }

  login({username, password}) {
    const requestBody = {username, password};
    return this._http.post(this._loginUrl, requestBody);
  }
}
