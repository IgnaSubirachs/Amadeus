import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

export interface LessonDTO {
    id: string;
    levelId: string;
    orderNumber: number;
    title: string;
    description: string;
    content: string;
    musicTheoryTopic: string;
    estimatedMinutes: number;
}

export interface ExerciseDTO {
    id: string;
    lessonId: string;
    orderNumber: number;
    type: string;
    difficulty: string;
    questionData: string;
    correctAnswer: string;
    maxPoints: number;
}

@Injectable({
    providedIn: 'root'
})
export class LessonService {
    private apiUrl = `${environment.apiUrl}/lessons`;

    constructor(private http: HttpClient) { }

    getLessonsByLevel(levelId: string): Observable<LessonDTO[]> {
        return this.http.get<LessonDTO[]>(`${this.apiUrl}/level/${levelId}`);
    }

    getLessonById(id: string): Observable<LessonDTO> {
        return this.http.get<LessonDTO>(`${this.apiUrl}/${id}`);
    }

    getLessonExercises(lessonId: string): Observable<ExerciseDTO[]> {
        return this.http.get<ExerciseDTO[]>(`${environment.apiUrl}/exercises/lesson/${lessonId}`);
    }
}
