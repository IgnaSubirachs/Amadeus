import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { LevelService, LevelDTO } from '../../core/services/level.service';
import { AuthService } from '../../core/services/auth.service';
import { LessonDTO, LessonService } from '../../core/services/lesson.service';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard implements OnInit {
  levels = signal<LevelDTO[]>([]);
  lessonsByLevel = signal<Record<string, LessonDTO[]>>({});
  isLoading = signal(true);
  error = signal('');
  username = signal('');

  constructor(
    private levelService: LevelService,
    private lessonService: LessonService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    const user = this.authService.currentUser();
    if (user) {
      this.username.set(user.username);
    }
    this.loadLevels();
  }

  loadLevels() {
    this.isLoading.set(true);
    this.levelService.getAllLevels().subscribe({
      next: (data: LevelDTO[]) => {
        data.sort((a: LevelDTO, b: LevelDTO) => a.orderNumber - b.orderNumber);
        this.levels.set(data);
        this.loadLessonsForLevels(data);
      },
      error: (err: any) => {
        console.error('Error loading levels:', err);
        this.error.set("No s'han pogut carregar els nivells");
        this.isLoading.set(false);
      }
    });
  }

  private loadLessonsForLevels(levels: LevelDTO[]) {
    if (levels.length === 0) {
      this.lessonsByLevel.set({});
      this.isLoading.set(false);
      return;
    }

    const requests = levels.map(level =>
      this.lessonService.getLessonsByLevel(level.id).pipe(
        catchError((err: any) => {
          console.error(`Error loading lessons for level ${level.id}:`, err);
          return of([]);
        })
      )
    );

    forkJoin(requests).subscribe({
      next: lessonGroups => {
        const lessonsByLevel = lessonGroups.reduce<Record<string, LessonDTO[]>>((acc, lessons, index) => {
          acc[levels[index].id] = [...lessons].sort((a, b) => a.orderNumber - b.orderNumber);
          return acc;
        }, {});

        this.lessonsByLevel.set(lessonsByLevel);
        this.isLoading.set(false);
      },
      error: (err: any) => {
        console.error('Error loading lessons:', err);
        this.error.set("No s'han pogut carregar les llicons");
        this.isLoading.set(false);
      }
    });
  }

  getLessons(levelId: string): LessonDTO[] {
    return this.lessonsByLevel()[levelId] ?? [];
  }

  difficulty(level: LevelDTO): string {
    return level.difficulty ?? 'beginner';
  }

  isLocked(level: LevelDTO): boolean {
    return level.isLocked ?? false;
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
