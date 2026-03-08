package com.amadeus.domain.config;

import com.amadeus.domain.model.Exercise;
import com.amadeus.domain.model.ExerciseType;
import com.amadeus.domain.model.Lesson;
import com.amadeus.domain.model.Level;
import com.amadeus.domain.repository.ExerciseRepository;
import com.amadeus.domain.repository.LessonRepository;
import com.amadeus.domain.repository.LevelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoContentConfig {

    @Bean
    CommandLineRunner loadDemoContent(
            LevelRepository levelRepository,
            LessonRepository lessonRepository,
            ExerciseRepository exerciseRepository) {
        return args -> {
            if (levelRepository.count() > 0 || lessonRepository.count() > 0 || exerciseRepository.count() > 0) {
                return;
            }

            Level level = Level.builder()
                    .id("level-notes-1")
                    .orderNumber(1)
                    .name("Primeres Notes")
                    .description("Identifica notes basiques i ritmes introductoris.")
                    .difficulty("beginner")
                    .build();

            Lesson lesson = Lesson.builder()
                    .id("lesson-notes-1")
                    .levelId(level.getId())
                    .orderNumber(1)
                    .title("Do, Re i ritme")
                    .description("Una lliço demo per provar el flux de practica.")
                    .musicTheoryTopic("notes")
                    .estimatedMinutes(5)
                    .build();

            Exercise firstExercise = Exercise.builder()
                    .id("exercise-note-1")
                    .lessonId(lesson.getId())
                    .type(ExerciseType.MULTIPLE_CHOICE)
                    .difficulty("easy")
                    .orderNumber(1)
                    .questionData("""
                            {"question":"Quina nota veus?","options":["Do","Re","Mi"],"staff":"treble","highlightedNote":"C4"}
                            """)
                    .correctAnswer("""
                            {"answer":"Do"}
                            """)
                    .maxPoints(10)
                    .build();

            Exercise secondExercise = Exercise.builder()
                    .id("exercise-note-2")
                    .lessonId(lesson.getId())
                    .type(ExerciseType.NOTE_IDENTIFICATION)
                    .difficulty("easy")
                    .orderNumber(2)
                    .questionData("""
                            {"question":"Escriu el nom de la nota marcada.","staff":"treble","highlightedNote":"D4"}
                            """)
                    .correctAnswer("""
                            {"answer":"Re"}
                            """)
                    .maxPoints(10)
                    .build();

            Exercise thirdExercise = Exercise.builder()
                    .id("exercise-rhythm-1")
                    .lessonId(lesson.getId())
                    .type(ExerciseType.RHYTHM_READING)
                    .difficulty("medium")
                    .orderNumber(3)
                    .questionData("""
                            {"question":"Llegeix el ritme en ordre.","pattern":["ta","ta-a","ta"]}
                            """)
                    .correctAnswer("""
                            {"answers":["ta","ta-a","ta"]}
                            """)
                    .maxPoints(15)
                    .build();

            levelRepository.save(level);
            lessonRepository.save(lesson);
            exerciseRepository.save(firstExercise);
            exerciseRepository.save(secondExercise);
            exerciseRepository.save(thirdExercise);
        };
    }
}