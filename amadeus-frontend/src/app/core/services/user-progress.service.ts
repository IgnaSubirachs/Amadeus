import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface UserProgressDTO {
    id: string;
    userId: string;
    lessonId: string;
    isCompleted: boolean;
    currentExerciseIndex: number;
    score: number;
}

export interface RecordProgressRequest {
    isCompleted: boolean;
    currentExerciseIndex: number;
    score: number;
}

@Injectable({
    providedIn: 'root'
})
export class UserProgressService {
    private apiUrl = `${environment.apiUrl}/progress`;

    constructor(private http: HttpClient) { }

    getUserProgressForLesson(lessonId: string): Observable<UserProgressDTO> {
        return this.http.get<UserProgressDTO>(`${this.apiUrl}/lesson/${lessonId}`);
    }

    recordProgress(lessonId: string, request: RecordProgressRequest): Observable<UserProgressDTO> {
        return this.http.post<UserProgressDTO>(`${this.apiUrl}/lesson/${lessonId}`, request);
    }
}
