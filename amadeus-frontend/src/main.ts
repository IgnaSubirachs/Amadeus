import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

bootstrapApplication(App, appConfig)
  .catch((err) => {
    const errorDiv = document.getElementById('error-overlay');
    if (errorDiv) {
      errorDiv.innerHTML += `<p><strong>BOOTSTRAP ERROR:</strong> ${err.message || err}<br/>${err.stack || ''}</p>`;
    }
    console.error(err);
  });
