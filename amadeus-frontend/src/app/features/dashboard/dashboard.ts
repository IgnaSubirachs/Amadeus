import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { LevelService, LevelDTO } from '../../core/services/level.service';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class Dashboard implements OnInit {
  levels = signal<LevelDTO[]>([]);
  isLoading = signal(true);
  error = signal('');
  username = signal('');

  constructor(
    private levelService: LevelService,
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
        // Sort levels by order number just to be sure
        data.sort((a: LevelDTO, b: LevelDTO) => a.orderNumber - b.orderNumber);
        this.levels.set(data);
        this.isLoading.set(false);
      },
      error: (err: any) => {
        console.error('Error loading levels:', err);
        this.error.set('No se pudieron cargar los niveles');
        this.isLoading.set(false);
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
