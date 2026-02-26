import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { LessonService, LessonDTO, ExerciseDTO } from '../../core/services/lesson.service';
import { UserProgressService, UserProgressDTO } from '../../core/services/user-progress.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-lesson',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './lesson.html',
  styleUrls: ['./lesson.scss']
})
export class Lesson implements OnInit {
  lesson = signal<LessonDTO | null>(null);
  exercises = signal<ExerciseDTO[]>([]);

  // State
  isLoading = signal(true);
  currentExerciseIndex = signal(0);
  score = signal(0);
  error = signal('');

  // Interactive Exercise State
  userAnswer = signal<string>('');
  showResult = signal(false);
  isCorrect = signal(false);

  // Computed Values
  currentExercise = computed(() => {
    const list = this.exercises();
    if (list.length === 0) return null;
    return list[this.currentExerciseIndex()];
  });

  progressPercentage = computed(() => {
    const total = this.exercises().length;
    if (total === 0) return 0;
    return (this.currentExerciseIndex() / total) * 100;
  });

  isCompleted = computed(() => {
    const total = this.exercises().length;
    return total > 0 && this.currentExerciseIndex() >= total;
  });

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private lessonService: LessonService,
    private progressService: UserProgressService
  ) { }

  ngOnInit(): void {
    const lessonId = this.route.snapshot.paramMap.get('id');
    if (lessonId) {
      this.loadLessonData(lessonId);
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  loadLessonData(lessonId: string) {
    this.isLoading.set(true);

    // 1. Fetch Lesson Data
    this.lessonService.getLessonById(lessonId).subscribe({
      next: (lessonData: LessonDTO) => {
        this.lesson.set(lessonData);

        // 2. Fetch Exercises for the lesson
        this.lessonService.getLessonExercises(lessonId).subscribe({
          next: (exs: ExerciseDTO[]) => {
            exs.sort((a: ExerciseDTO, b: ExerciseDTO) => a.orderNumber - b.orderNumber);
            this.exercises.set(exs);

            // 3. Load Progress (Resuming learning)
            this.loadUserProgress(lessonId);
          },
          error: (err: any) => this.handleError('Error cargando ejercicios')
        });
      },
      error: (err: any) => this.handleError('Error cargando la lección')
    });
  }

  loadUserProgress(lessonId: string) {
    this.progressService.getUserProgressForLesson(lessonId).subscribe({
      next: (progress: any) => {
        if (progress && !progress.isCompleted) {
          // Resume from where the user left off
          this.currentExerciseIndex.set(progress.currentExerciseIndex);
          this.score.set(progress.score);
        } else if (progress && progress.isCompleted) {
          // Already completed, just view the summary
          this.currentExerciseIndex.set(this.exercises().length);
          this.score.set(progress.score);
        }
        this.isLoading.set(false);
      },
      error: (err: any) => {
        // If 404, it means it's a new attempt. Just ignore the 404 error
        if (err.status !== 404) {
          console.error('Error cargando progreso:', err);
        }
        this.isLoading.set(false);
      }
    });
  }

  // Parses the stringified options array returned from the backend (if the question has options)
  getOptions(exercise: ExerciseDTO | null): any[] {
    if (!exercise || !exercise.optionsData) return [];
    try {
      return JSON.parse(exercise.optionsData);
    } catch {
      return [];
    }
  }

  submitAnswer(selectedAnswer?: string) {
    if (this.showResult()) return; // Already answered

    const exercise = this.currentExercise();
    if (!exercise) return;

    const finalAnswer = selectedAnswer !== undefined ? selectedAnswer : this.userAnswer();

    // Check answer natively considering the JSON shape might be slightly different in format, lowercasing both checks
    const correct = finalAnswer.trim().toLowerCase() === exercise.correctAnswer.trim().toLowerCase();

    this.isCorrect.set(correct);
    this.showResult.set(true);

    if (correct) {
      this.score.update(s => s + exercise.points);
    }
  }

  nextExercise() {
    this.currentExerciseIndex.update(i => i + 1);
    this.resetExerciseState();
    this.saveProgress();
  }

  private resetExerciseState() {
    this.userAnswer.set('');
    this.showResult.set(false);
    this.isCorrect.set(false);
  }

  saveProgress() {
    const lessonId = this.lesson()?.id;
    if (!lessonId) return;

    const request = {
      isCompleted: this.isCompleted(),
      currentExerciseIndex: this.currentExerciseIndex(),
      score: this.score()
    };

    this.progressService.recordProgress(lessonId, request).subscribe({
      error: (err: any) => console.error('Error guardando progreso:', err)
    });
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }

  private handleError(msg: string) {
    this.error.set(msg);
    this.isLoading.set(false);
  }
}
