import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from "../../environment";

/** Endpoints that are public and must not receive a (possibly stale) access token. */
export const PUBLIC_AUTH_ENDPOINTS = ['/auth/login', '/auth/refresh', '/users/signup'];

export function isApiRequest(url: string): boolean {
    return url.startsWith(environment.apiUrl);
}

export function isPublicAuthEndpoint(url: string): boolean {
    return PUBLIC_AUTH_ENDPOINTS.some(endpoint => url.startsWith(`${environment.apiUrl}${endpoint}`));
}

@Injectable({
    providedIn:'root'
})

export class JwtInterceptor implements HttpInterceptor {

    intercept(request : HttpRequest<unknown>, next: HttpHandler):Observable<HttpEvent<unknown>>{
        const localToken = localStorage.getItem('accessToken');
        if(localToken && isApiRequest(request.url) && !isPublicAuthEndpoint(request.url)){
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${localToken}`
                }
            });
        }
        return next.handle(request);
    }

}