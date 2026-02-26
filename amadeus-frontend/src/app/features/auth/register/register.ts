import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class Register {
  email = signal('');
  username = signal('');
  password = signal('');

  isLoading = signal(false);
  errorMessage = signal('');

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  onSubmit() {
    if (!this.email() || !this.username() || !this.password()) {
      this.errorMessage.set('Por favor completa todos los campos requeridos');
      return;
    }

    if (this.password().length < 8) {
      this.errorMessage.set('La contraseña debe tener al menos 8 caracteres');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');

    this.authService.register({
      email: this.email(),
      username: this.username(),
      password: this.password()
    }).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.isLoading.set(false);
        // Display backend validation error if any
        if (err.error && typeof err.error === 'object') {
          const firstError = Object.values(err.error)[0] as string;
          this.errorMessage.set(firstError || 'Error al registrar tu cuenta. Intenta con otro correo o usuario.');
        } else {
          this.errorMessage.set('Error en el servidor al registrar cuenta.');
        }
      }
    });
  }
}
