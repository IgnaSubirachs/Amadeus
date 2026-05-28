import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

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

    constructor(
        private http: HttpClient,
        private authService: AuthService
    ) { }

    getUserProgressForLesson(lessonId: string): Observable<UserProgressDTO> {
        return this.http.get<UserProgressDTO>(`${this.apiUrl}/user/${this.currentUserId()}/lesson/${lessonId}`);
    }

    recordProgress(lessonId: string, request: RecordProgressRequest): Observable<UserProgressDTO> {
        return this.http.post<UserProgressDTO>(this.apiUrl, {
            userId: this.currentUserId(),
            lessonId,
            score: request.score,
            currentExerciseIndex: request.currentExerciseIndex,
            isCompleted: request.isCompleted
        });
    }

    private currentUserId(): string {
        const user = this.authService.currentUser();
        if (!user) {
            throw new Error('No authenticated user available for progress tracking');
        }
        return user.id;
    }
}
