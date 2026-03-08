const state = {
  userId: localStorage.getItem("amadeusUserId") || `guest-${Math.random().toString(36).slice(2, 8)}`,
  course: null,
  currentLesson: null,
  currentSession: null,
  selectedAnswers: {},
};

localStorage.setItem("amadeusUserId", state.userId);
document.getElementById("user-id").textContent = state.userId;

const guideLines = {
  idle: "Benvingut. Comencem per una lliço curta i fem sonar el pentagrama.",
  loading: "Obrint el llibre de partitures...",
  started: "Perfecte. Escolta la pregunta, mira el pentagrama i respon sense por.",
  correct: "Això si que desafina poc. Seguim.",
  wrong: "No passa res. Torna-hi: la teoria entra millor amb ritme.",
  complete: "Bravo. Has acabat la sessio demo d'Amadeus."
};

function setGuideLine(key) {
  document.getElementById("guide-line").textContent = guideLines[key] || key;
}

function drawGuide() {
  const canvas = document.getElementById("amadeus-guide");
  const ctx = canvas.getContext("2d");
  const scale = 10;
  const palette = {
    T: "transparent",
    B: "#0b1222",
    W: "#f6efdb",
    G: "#f4d8a7",
    O: "#ffb347",
    C: "#72e2c3",
    R: "#a44d34"
  };

  const frameA = [
    "TTTTTWWWWWWTTTT",
    "TTTTWWWWWWWWTTT",
    "TTTWWWWWWWWWWTT",
    "TTWWBBBBBBBBWWT",
    "TTWBBBBBBBBBBW T".replace(/ /g, ""),
    "TWWBGGGGGGGGBWW",
    "TWBWGGGBBGGWBWT",
    "TWBWGGGGGGGWBWT",
    "TWBWGBWWWWGBBWT",
    "TWWBGGGGGGGGBWT",
    "TTWBGGOOOGGGBWT",
    "TTWWGGGGGGGWWTT",
    "TTTWRRRRRRRWTTT",
    "TTTWCRRCCRWTTTT",
    "TTTWBCCCCBWTTTT",
    "TTTTWBBBBWTTTTT"
  ];

  const frameB = [
    "TTTTTWWWWWWTTTT",
    "TTTTWWWWWWWWTTT",
    "TTTWWWWWWWWWWTT",
    "TTWWBBBBBBBBWWT",
    "TTWBBBBBBBBBBWT",
    "TWWBGGGGGGGGBWW",
    "TWBWGGBTTBGWBWT",
    "TWBWGGGGGGGWBWT",
    "TWBWGBWWWWGBBWT",
    "TWWBGGGGGGGGBWT",
    "TTWBGGOOOGGGBWT",
    "TTWWGGGGGGGWWTT",
    "TTTWRRRRRRRWTTT",
    "TTTWCRRCCRWTTTT",
    "TTTWBCCCCBWTTTT",
    "TTTTWBBBBWTTTTT"
  ];

  const frames = [frameA, frameB];
  let index = 0;

  const render = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height);
    frames[index].forEach((row, y) => {
      row.split("").forEach((pixel, x) => {
        const color = palette[pixel];
        if (!color || color === "transparent") return;
        ctx.fillStyle = color;
        ctx.fillRect(x * scale, y * scale, scale, scale);
      });
    });
  };

  render();
  setInterval(() => {
    index = (index + 1) % frames.length;
    render();
  }, 700);
}

async function fetchJSON(url, options) {
  const response = await fetch(url, options);
  if (!response.ok) {
    const payload = await response.json().catch(() => ({}));
    throw new Error(payload.error || "Error inesperat");
  }
  return response.json();
}

function renderCatalog() {
  const catalog = document.getElementById("catalog");
  if (!state.course?.levels?.length) {
    catalog.innerHTML = '<div class="catalog-item"><p>No hi ha contingut disponible.</p></div>';
    return;
  }

  catalog.innerHTML = state.course.levels.map(level => `
    <article class="catalog-item">
      <p class="panel-kicker">Nivell ${level.orderNumber}</p>
      <h3>${level.name}</h3>
      <p>${level.description || "Sense descripcio."}</p>
      ${level.lessons.map(lesson => `
        <div class="catalog-item" style="margin-top:12px;">
          <h4>${lesson.orderNumber}. ${lesson.title}</h4>
          <p>${lesson.description || "Sessio curta"}</p>
          <p>${lesson.exerciseCount} exercicis · ${lesson.estimatedMinutes || 5} min</p>
          <button class="lesson-button" data-lesson-id="${lesson.id}">Comencar lliço</button>
        </div>
      `).join("")}
    </article>
  `).join("");

  catalog.querySelectorAll(".lesson-button").forEach(button => {
    button.addEventListener("click", () => startLesson(button.dataset.lessonId));
  });
}

function updateScoreboard(summary) {
  document.getElementById("score-value").textContent = summary?.totalScore ?? 0;
  document.getElementById("exercise-value").textContent = `${summary?.currentExerciseIndex ?? 0} / ${summary?.totalExercises ?? 0}`;
  document.getElementById("completion-value").textContent = summary?.completed ? "Completada" : "En curs";
}

function renderLesson(session) {
  state.currentSession = session;
  state.selectedAnswers = {};
  document.getElementById("lesson-title").textContent = session.title;
  document.getElementById("lesson-meta").innerHTML = `
    <span>Tema: ${session.topic}</span>
    <span>Durada: ${session.estimatedMinutes || 5} min</span>
    <span>Exercicis: ${session.exercises.length}</span>
  `;
  updateScoreboard({
    currentExerciseIndex: session.currentExerciseIndex,
    totalExercises: session.exercises.length,
    totalScore: session.totalScore,
    completed: session.completed
  });

  const area = document.getElementById("exercise-area");
  area.classList.remove("empty-state");
  area.innerHTML = session.exercises.map(exercise => renderExerciseCard(exercise)).join("");

  area.querySelectorAll(".option-button").forEach(button => {
    button.addEventListener("click", () => {
      const { exerciseId, value } = button.dataset;
      state.selectedAnswers[exerciseId] = value;
      area.querySelectorAll(`[data-exercise-id="${exerciseId}"]`).forEach(node => node.classList.toggle("active", node.dataset.value === value));
    });
  });

  area.querySelectorAll(".submit-button").forEach(button => {
    button.addEventListener("click", () => submitAnswer(button.dataset.exerciseId));
  });

  setGuideLine("started");
}

function renderExerciseCard(exercise) {
  const data = JSON.parse(exercise.questionData);
  const noteHint = data.highlightedNote || data.pattern?.join(" · ") || data.staff || "";

  let inputMarkup = "";
  if (exercise.type === "MULTIPLE_CHOICE") {
    inputMarkup = `
      <div class="options-grid">
        ${data.options.map(option => `<button class="option-button" data-exercise-id="${exercise.id}" data-value="${option}">${option}</button>`).join("")}
      </div>
    `;
  } else if (exercise.type === "NOTE_IDENTIFICATION") {
    inputMarkup = `
      <input class="answer-input" id="input-${exercise.id}" placeholder="Escriu la nota, ex: Re" />
      <button class="submit-button" data-exercise-id="${exercise.id}">Enviar resposta</button>
    `;
  } else {
    inputMarkup = `
      <input class="answer-input" id="input-${exercise.id}" placeholder='Escriu JSON array, ex: ["ta","ta-a","ta"]' />
      <button class="submit-button" data-exercise-id="${exercise.id}">Enviar ritme</button>
    `;
  }

  if (exercise.type === "MULTIPLE_CHOICE") {
    inputMarkup += `<button class="submit-button" data-exercise-id="${exercise.id}">Confirmar eleccio</button>`;
  }

  return `
    <article class="exercise-card">
      <p class="panel-kicker">${exercise.type.replaceAll("_", " ")}</p>
      <h3>${data.question}</h3>
      <p>${noteHint}</p>
      ${inputMarkup}
    </article>
  `;
}

async function startLesson(lessonId) {
  try {
    setGuideLine("loading");
    const session = await fetchJSON(`/api/users/${state.userId}/lessons/${lessonId}/start`, { method: "POST" });
    state.currentLesson = lessonId;
    renderLesson(session);
    document.getElementById("feedback").textContent = "Sessio iniciada. Respon el primer exercici.";
    document.getElementById("feedback").className = "feedback-card";
  } catch (error) {
    document.getElementById("feedback").textContent = error.message;
    document.getElementById("feedback").className = "feedback-card bad";
  }
}

async function submitAnswer(exerciseId) {
  try {
    const input = document.getElementById(`input-${exerciseId}`);
    const submittedAnswer = state.selectedAnswers[exerciseId] || input?.value?.trim();
    if (!submittedAnswer) {
      throw new Error("Escriu o selecciona una resposta abans d'enviar.");
    }

    const summary = await fetchJSON(`/api/users/${state.userId}/lessons/${state.currentLesson}/answer`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ exerciseId, submittedAnswer })
    });

    updateScoreboard(summary);
    const feedback = document.getElementById("feedback");
    feedback.textContent = summary.answerCorrect
      ? `Correcte. +${summary.awardedPoints} punts.`
      : "Incorrecte. Repassa la pista i torna-hi.";
    feedback.className = `feedback-card ${summary.answerCorrect ? "good" : "bad"}`;
    setGuideLine(summary.completed ? "complete" : summary.answerCorrect ? "correct" : "wrong");
  } catch (error) {
    document.getElementById("feedback").textContent = error.message;
    document.getElementById("feedback").className = "feedback-card bad";
  }
}

async function boot() {
  drawGuide();
  try {
    setGuideLine("loading");
    state.course = await fetchJSON("/api/catalog/tree");
    renderCatalog();
    setGuideLine("idle");
  } catch (error) {
    document.getElementById("catalog").innerHTML = `<div class="catalog-item"><p>${error.message}</p></div>`;
    document.getElementById("feedback").textContent = "No s'ha pogut carregar el cataleg.";
    document.getElementById("feedback").className = "feedback-card bad";
  }
}

boot();