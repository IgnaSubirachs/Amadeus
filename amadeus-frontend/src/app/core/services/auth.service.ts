import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, tap } from 'rxjs';

export interface UserDTO {
    id: string;
    email: string;
    username: string;
    profileImage?: string;
}

export interface AuthResponse {
    token: string;
    user: UserDTO;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = `${environment.apiUrl}/auth`;

    // Using signals for reactive state management
    currentUser = signal<UserDTO | null>(null);
    isAuthenticated = signal<boolean>(false);

    constructor(private http: HttpClient) {
        this.checkInitialState();
    }

    private checkInitialState() {
        const token = localStorage.getItem('amadeus_token');
        const userStr = localStorage.getItem('amadeus_user');

        if (token && userStr) {
            this.currentUser.set(JSON.parse(userStr));
            this.isAuthenticated.set(true);
        }
    }

    login(credentials: any): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
            tap(response => this.handleAuthSuccess(response))
        );
    }

    register(userData: any): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/register`, userData).pipe(
            tap(response => this.handleAuthSuccess(response))
        );
    }

    logout() {
        localStorage.removeItem('amadeus_token');
        localStorage.removeItem('amadeus_user');
        this.currentUser.set(null);
        this.isAuthenticated.set(false);
    }

    getToken(): string | null {
        return localStorage.getItem('amadeus_token');
    }

    private handleAuthSuccess(response: AuthResponse) {
        localStorage.setItem('amadeus_token', response.token);
        localStorage.setItem('amadeus_user', JSON.stringify(response.user));
        this.currentUser.set(response.user);
        this.isAuthenticated.set(true);
    }
}
