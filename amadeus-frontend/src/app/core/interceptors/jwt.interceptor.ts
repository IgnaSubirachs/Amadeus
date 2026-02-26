import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
    const token = localStorage.getItem('amadeus_token');

    // Exclude auth endpoints from interceptor
    const isAuthUrl = req.url.includes('/api/auth');

    if (token && !isAuthUrl) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(req);
};
