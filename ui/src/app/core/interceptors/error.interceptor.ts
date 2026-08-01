import { catchError, Observable, switchMap, throwError } from "rxjs";
import { AuthModel } from "../models/auth.model";
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { AuthService } from "../services/auth.service";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService, private router : Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError(err => {
        if (err.status === 401 || err.status === 403) {
          const refreshToken = localStorage.getItem("refreshToken");
          if (refreshToken) {
            return this.authService.refresh().pipe(
              switchMap((res: AuthModel) => {
                console.log("Token refreshed successfully:", res);
                // Save new access token (and refresh token if backend issues a new one)
                localStorage.setItem("accessToken", res.accessToken);
                if (res.refreshToken) {
                  console.log("New refresh token received:", res.refreshToken);
                  localStorage.setItem("refreshToken", res.refreshToken);
                }

                const newReq = req.clone({
                  setHeaders: { Authorization: `Bearer ${res.accessToken}` }
                });
                return next.handle(newReq);
              }),
              catchError(refreshErr => {
                // If refresh also fails → force logout
                localStorage.clear();
                this.router.navigate(['/login']);
                return throwError(() => refreshErr);
              })
            );
          }
        }
        return throwError(() => err);
      })
    );
  }
}
