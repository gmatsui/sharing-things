import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { LoginService } from './login.service';
import { UserService } from '../common/service/user.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  public loginForm: FormGroup;

  constructor(private _loginService: LoginService, private _userService: UserService) { }

  ngOnInit() {
    this._userService.invalidate();
    this.loginForm = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });
  }

  public login(value) {
    this._loginService.login(value)
      .subscribe(
        token => {
          console.log("OK", token);
          this._userService.storeUser(value.username, token)
        },
        error => {
          console.error("Error", error);
          //TODO manage the error
        }
      )

  }
}
/*
{access_token: "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6IC…5cVBEcvjluvPPxPNSuKsyEj8EGXA1hqVUEvg_POzWimsUJEPw", expires_in: 3600, refresh_expires_in: 3600, refresh_token: "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6IC…WwifQ.XHulH1EoECb3WHQ2XjqZO4GEgkCQhPDNJCPqtMrHArI", token_type: "bearer", …}
access_token: "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJyMnhMUlBaRlJrRy00Rl9SS1BZT21mdXlIMDhDTS1BQVhzUzk1NHoybFhBIn0.eyJqdGkiOiJlMDg3MTRmMS01YWRlLTQyYmYtODA0Mi02Y2U3ZjNjMzJmYjMiLCJleHAiOjE1NjM0OTE4NTgsIm5iZiI6MCwiaWF0IjoxNTYzNDg4MjU4LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjkwODAvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6ImU4OTliYmVhLTFkMjktNDRiOS05NzdlLTE2MmVhNDY1N2ZiNSIsInR5cCI6IkJlYXJlciIsImF6cCI6Im5vbnByb2Qtc2hhcmluZy10aGluZ3MtYWNjb3VudCIsImF1dGhfdGltZSI6MCwic2Vzc2lvbl9zdGF0ZSI6ImJiMjFiY2I1LWJiNmQtNDkzYS1iN2E3LTZiMzEzZWU3YWJjNSIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiaHR0cDovL2xvY2FsaG9zdDo4MDgwIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJhcHBsaWNhdGlvbl91c2VyIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwicHJlZmVycmVkX3VzZXJuYW1lIjoiZ2VyYXJkIiwiZW1haWwiOiJnZXJhcmRvLm1hdHN1aUBnbWFpbC5jb20ifQ.o8j0dU9o5_AlUUUxYC83yOl7YaFUOfg5L2BOVGgIy7Y-9wPgujt58QEKQAUT-cKX9OqBWDlqDwpdxz87IWl9rJ0qO43HBvhni7v39xGpeOw1CScjBq0n7UIulSPos8HEPVDMBsz4BHyxe4LNNsQlAYqlkM8SefZ7peS19BDOosMIGq2X2WdgT54kXaLvWC66MuC8kplBaH7cwVam1bWz2eWC89AIjxiA5u00c11Fe5SptNJEEVZ3M70L3umpoWRV7YAfnh-ItO6jkNcM-dFrbZD_i6vtkP0KaSueY5cVBEcvjluvPPxPNSuKsyEj8EGXA1hqVUEvg_POzWimsUJEPw"
expires_at: 1563491858782
expires_in: 3600
not-before-policy: 0
refresh_expires_in: 3600
refresh_token: "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICI0ZGI0OGUzZi1kNTdjLTRmMzAtYTY2ZC04Mzg3YTgwYTExZTMifQ.eyJqdGkiOiIwOTc0ZDkxOS1lOWRlLTRjYjktOTc4Ni1mOWMzNGFiNjMyODEiLCJleHAiOjE1NjM0OTE4NTgsIm5iZiI6MCwiaWF0IjoxNTYzNDg4MjU4LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjkwODAvYXV0aC9yZWFsbXMvbWFzdGVyIiwiYXVkIjoiaHR0cDovL2xvY2FsaG9zdDo5MDgwL2F1dGgvcmVhbG1zL21hc3RlciIsInN1YiI6ImU4OTliYmVhLTFkMjktNDRiOS05NzdlLTE2MmVhNDY1N2ZiNSIsInR5cCI6IlJlZnJlc2giLCJhenAiOiJub25wcm9kLXNoYXJpbmctdGhpbmdzLWFjY291bnQiLCJhdXRoX3RpbWUiOjAsInNlc3Npb25fc3RhdGUiOiJiYjIxYmNiNS1iYjZkLTQ5M2EtYjdhNy02YjMxM2VlN2FiYzUiLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiYXBwbGljYXRpb25fdXNlciIsIm9mZmxpbmVfYWNjZXNzIiwidW1hX2F1dGhvcml6YXRpb24iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwifQ.XHulH1EoECb3WHQ2XjqZO4GEgkCQhPDNJCPqtMrHArI"
scope: "profile email"
session_state: "bb21bcb5-bb6d-493a-b7a7-6b313ee7abc5"
token_type: "bearer"
*/
