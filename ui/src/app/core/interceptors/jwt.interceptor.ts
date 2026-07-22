import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
    providedIn:'root'
})

export class JwtInterceptor implements HttpInterceptor {

    intercept(request : HttpRequest<unknown>, next: HttpHandler):Observable<HttpEvent<unknown>>{
        const localToken = localStorage.getItem('Token');
        if(localToken){
            request = request.clone({
                setHeaders: {
                    Authorization: `Bearer ${localToken}`
                }
            });
        }
        return next.handle(request);
    }

}