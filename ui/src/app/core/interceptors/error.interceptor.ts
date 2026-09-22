import {
  catchError,
  finalize,
  map,
  Observable,
  shareReplay,
  switchMap,
  tap,
  throwError
} from "rxjs";

import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from "@angular/common/http";

import { Injectable } from "@angular/core";
import { MatSnackBar } from "@angular/material/snack-bar";

import { AuthService } from "../services/auth.service";
import { AuthModel } from "../models/auth.model";
import { ApiError } from "../enums/ApiError";

import { isApiRequest, isPublicAuthEndpoint } from "./jwt.interceptor";

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {

  /** Ensures only ONE refresh call happens even if multiple 401s occur */
  private refreshInProgress$: Observable<string> | null = null;

  constructor(
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    return next.handle(req).pipe(
      catchError((err: HttpErrorResponse) => {
        // 1. HANDLE 401 (TOKEN REFRESH)
        if (
          err.status === 401 &&
          isApiRequest(req.url) &&
          !isPublicAuthEndpoint(req.url)
        ) {
          if (!localStorage.getItem("refreshToken")) {
            this.authService.logout();
            return throwError(() => err);
          }
          return this.getFreshAccessToken().pipe(
            switchMap(accessToken =>
              next.handle(
                req.clone({
                  setHeaders: { Authorization: `Bearer ${accessToken}` }
                })
              )
            ),
            catchError(refreshErr => {
              this.authService.logout();
              return throwError(() => refreshErr);
            })
          );
        }

        // 2. HANDLE STRUCTURED ApiError
        let apiError: ApiError | null = null;

        if (err.error && err.error.errorCode) {
          apiError = err.error as ApiError;
        }

        // 3. SHOW USER-FRIENDLY MESSAGE
        const message = apiError
          ? `${apiError.title} — ${apiError.detail}\nRef: ${apiError.traceId}`
          : (err.error?.message || "Something went wrong");

        this.snackBar.open(message, "Close", {
          duration: 5000
        });
        
        // 4. PROPAGATE ERROR
        return throwError(() => apiError || err);
      })
    );
  }

  //  TOKEN REFRESH LOGIC
  private getFreshAccessToken(): Observable<string> {

    if (!this.refreshInProgress$) {
      this.refreshInProgress$ = this.authService.refresh().pipe(
        tap((res: AuthModel) => {
          localStorage.setItem("accessToken", res.accessToken);
          if (res.refreshToken) {
            localStorage.setItem("refreshToken", res.refreshToken);
          }
          // refresh user context
          this.authService.me();
        }),
        map(res => res.accessToken),
        finalize(() => (this.refreshInProgress$ = null)),
        shareReplay(1)
      );
    }

    return this.refreshInProgress$;
  }
}