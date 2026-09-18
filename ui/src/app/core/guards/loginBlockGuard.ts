import { CanActivateFn, Router } from '@angular/router';

export const loginBlockGuard: CanActivateFn = (route, state) => {
  const isLoggedIn = !!localStorage.getItem('accessToken'); // or use your auth service
  const router = new Router();
  
  if (isLoggedIn) {
    return router.parseUrl('/dashboard');
  }
  return true; // allow access if not logged in
};
