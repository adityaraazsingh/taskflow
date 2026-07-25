import { CanActivateFn, Router } from "@angular/router";
import { inject } from "@angular/core";

export const authGuard : CanActivateFn =(route, segments)=>{
  const router = inject(Router);

  const isLoggedIn = !!localStorage.getItem('Token'); 

  if (isLoggedIn) {
    return true;
  } else {
    return router.createUrlTree(['/login']);
  }
}