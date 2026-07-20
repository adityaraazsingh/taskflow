import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";

@Injectable({
    providedIn:'root'
})

export class JwtInterceptor implements HttpInterceptor {

    intercept(request : HttpRequest<unknown>, next: HttpHandler):Observable<HttpEvent<unknown>>{
        const localToken = localStorage.getItem('Token');
        console.log("Attaching Token from local storage : " + localToken);
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

// import { HttpInterceptorFn } from '@angular/common/http';

// export const JwtInterceptor: HttpInterceptorFn = (req, next) => {
  
//   if (req.url.includes('/api/users')) {
//     return next(req);
//   }

//   const savedToken = window.localStorage.getItem('Token');
//   console.log("Attaching token to http Request : ", savedToken)

//   let token: string | null = null;

//   if (savedToken) {
//     const parsed = JSON.parse(savedToken);
//     token = parsed.token.token;
//   }

//   if (token) {
//     const reqWithHeader = req.clone({
//       headers: req.headers.set('Authorization', `Bearer ${token}`),
//     });

//     return next(reqWithHeader);
//   }

//   return next(req);
// };