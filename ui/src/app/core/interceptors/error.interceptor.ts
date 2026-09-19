import { catchError, finalize, map, Observable, shareReplay, switchMap, tap, throwError } from "rxjs";
import { AuthModel } from "../models/auth.model";
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { AuthService } from "../services/auth.service";
import { Injectable } from "@angular/core";
import { isApiRequest, isPublicAuthEndpoint } from "./jwt.interceptor";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  /** The refresh call currently in flight, shared so parallel 401s trigger exactly one refresh. */
  private refreshInProgress$: Observable<string> | null = null;

  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        // Only an expired/invalid access token (401) on our own API is recoverable.
        // 403 means the user is authenticated but not allowed - refreshing cannot fix that.
        if (err.status !== 401 || !isApiRequest(req.url) || isPublicAuthEndpoint(req.url)) {
          return throwError(() => err);
        }

        if (!localStorage.getItem("refreshToken")) {
          this.authService.logout();
          return throwError(() => err);
        }

        return this.getFreshAccessToken().pipe(
          switchMap(accessToken =>
            next.handle(req.clone({ setHeaders: { Authorization: `Bearer ${accessToken}` } }))
          ),
          catchError(refreshErr => {
            // Refresh token expired / revoked / user gone -> force a clean login.
            this.authService.logout();
            return throwError(() => refreshErr);
          })
        );
      })
    );
  }

  private getFreshAccessToken(): Observable<string> {
    if (!this.refreshInProgress$) {
      this.refreshInProgress$ = this.authService.refresh().pipe(
        tap((res: AuthModel) => {
          localStorage.setItem("accessToken", res.accessToken);
          if (res.refreshToken) {
            localStorage.setItem("refreshToken", res.refreshToken);
          }
          this.authService.me(); // role may have changed since the last login
        }),
        map(res => res.accessToken),
        finalize(() => (this.refreshInProgress$ = null)),
        shareReplay(1)
      );
    }
    return this.refreshInProgress$;
  }
}
