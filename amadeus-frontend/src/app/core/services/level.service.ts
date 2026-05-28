import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface LevelDTO {
    id: string;
    orderNumber: number;
    name: string;
    description: string;
    difficulty?: string;
    isLocked?: boolean;
    imageUrl?: string;
}

@Injectable({
    providedIn: 'root'
})
export class LevelService {
    private apiUrl = `${environment.apiUrl}/levels`;

    constructor(private http: HttpClient) { }

    getAllLevels(): Observable<LevelDTO[]> {
        return this.http.get<LevelDTO[]>(this.apiUrl);
    }

    getLevelById(id: string): Observable<LevelDTO> {
        return this.http.get<LevelDTO>(`${this.apiUrl}/${id}`);
    }
}
