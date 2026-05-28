import { Component, OnInit, computed, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { LessonService, LessonDTO, ExerciseDTO } from '../../core/services/lesson.service';
import { UserProgressService } from '../../core/services/user-progress.service';

interface QuestionData {
  question?: string;
  options?: string[];
  staff?: 'treble' | 'bass';
  highlightedNote?: string;
  pattern?: string[];
  hint?: string;
}

interface CorrectAnswerData {
  answer?: string;
  answers?: string[];
}

@Component({
  selector: 'app-lesson',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './lesson.html',
  styleUrls: ['./lesson.scss']
})
export class Lesson implements OnInit {
  lesson = signal<LessonDTO | null>(null);
  exercises = signal<ExerciseDTO[]>([]);

  isLoading = signal(true);
  currentExerciseIndex = signal(0);
  score = signal(0);
  error = signal('');

  userAnswer = signal<string>('');
  showResult = signal(false);
  isCorrect = signal(false);

  currentExercise = computed(() => {
    const list = this.exercises();
    if (list.length === 0 || this.currentExerciseIndex() >= list.length) return null;
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
    this.error.set('');

    this.lessonService.getLessonById(lessonId).subscribe({
      next: (lessonData: LessonDTO) => {
        this.lesson.set(lessonData);
        this.lessonService.getLessonExercises(lessonId).subscribe({
          next: (exercises: ExerciseDTO[]) => {
            this.exercises.set([...exercises].sort((a, b) => a.orderNumber - b.orderNumber));
            this.loadUserProgress(lessonId);
          },
          error: () => this.handleError("No s'han pogut carregar els exercicis")
        });
      },
      error: () => this.handleError("No s'ha pogut carregar la llico")
    });
  }

  loadUserProgress(lessonId: string) {
    this.progressService.getUserProgressForLesson(lessonId).subscribe({
      next: progress => {
        if (progress && !progress.isCompleted) {
          this.currentExerciseIndex.set(progress.currentExerciseIndex ?? 0);
          this.score.set(progress.score ?? 0);
        } else if (progress?.isCompleted) {
          this.currentExerciseIndex.set(this.exercises().length);
          this.score.set(progress.score ?? 0);
        }
        this.isLoading.set(false);
      },
      error: err => {
        if (err.status !== 404) {
          console.error('Error carregant progres:', err);
        }
        this.isLoading.set(false);
      }
    });
  }

  questionData(exercise: ExerciseDTO | null): QuestionData {
    return this.parseJson<QuestionData>(exercise?.questionData, {});
  }

  correctAnswerText(exercise: ExerciseDTO | null): string {
    const answer = this.parseJson<CorrectAnswerData>(exercise?.correctAnswer, {});
    return answer.answer ?? answer.answers?.join(' ') ?? '';
  }

  getOptions(exercise: ExerciseDTO | null): string[] {
    return this.questionData(exercise).options ?? [];
  }

  rhythmPattern(exercise: ExerciseDTO | null): string[] {
    return this.questionData(exercise).pattern ?? [];
  }

  staffNote(exercise: ExerciseDTO | null): string {
    return this.questionData(exercise).highlightedNote ?? '';
  }

  noteTopPercent(note: string): number {
    const positions: Record<string, number> = {
      E4: 80,
      F4: 70,
      G4: 60,
      A4: 50,
      B4: 40,
      C5: 30,
      D5: 20,
      C4: 90,
      D4: 86
    };
    return positions[note.toUpperCase()] ?? 50;
  }

  submitAnswer(selectedAnswer?: string) {
    if (this.showResult()) return;

    const exercise = this.currentExercise();
    if (!exercise) return;

    const finalAnswer = selectedAnswer !== undefined ? selectedAnswer : this.userAnswer();
    const correct = this.isAnswerCorrect(finalAnswer, exercise);

    this.userAnswer.set(finalAnswer);
    this.isCorrect.set(correct);
    this.showResult.set(true);

    if (correct) {
      this.score.update(score => score + exercise.maxPoints);
    }
  }

  nextExercise() {
    this.currentExerciseIndex.update(index => index + 1);
    this.resetExerciseState();
    this.saveProgress();
  }

  saveProgress() {
    const lessonId = this.lesson()?.id;
    if (!lessonId) return;

    this.progressService.recordProgress(lessonId, {
      isCompleted: this.isCompleted(),
      currentExerciseIndex: this.currentExerciseIndex(),
      score: this.score()
    }).subscribe({
      error: err => console.error('Error guardant progres:', err)
    });
  }

  goBack() {
    this.router.navigate(['/dashboard']);
  }

  private isAnswerCorrect(answer: string, exercise: ExerciseDTO): boolean {
    const expected = this.parseJson<CorrectAnswerData>(exercise.correctAnswer, {});

    if (expected.answers) {
      const submitted = answer.split(/[,\s]+/).map(item => this.normalizeAnswer(item)).filter(Boolean);
      return expected.answers.map(item => this.normalizeAnswer(item)).join('|') === submitted.join('|');
    }

    return this.answerVariants(expected.answer ?? '').includes(this.normalizeAnswer(answer));
  }

  private answerVariants(answer: string): string[] {
    const normalized = this.normalizeAnswer(answer);
    const aliases: Record<string, string[]> = {
      do: ['do', 'c'],
      re: ['re', 'd'],
      mi: ['mi', 'e'],
      fa: ['fa', 'f'],
      sol: ['sol', 'g'],
      la: ['la', 'a'],
      si: ['si', 'ti', 'b']
    };

    return aliases[normalized] ?? [normalized];
  }

  private normalizeAnswer(value: string): string {
    return value
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');
  }

  private parseJson<T>(value: string | undefined, fallback: T): T {
    if (!value) return fallback;
    try {
      return JSON.parse(value) as T;
    } catch {
      return fallback;
    }
  }

  private resetExerciseState() {
    this.userAnswer.set('');
    this.showResult.set(false);
    this.isCorrect.set(false);
  }

  private handleError(message: string) {
    this.error.set(message);
    this.isLoading.set(false);
  }
}
