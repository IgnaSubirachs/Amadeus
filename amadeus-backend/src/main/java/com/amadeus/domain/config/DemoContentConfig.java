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

import java.util.List;

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

            Level notesLevel = level(
                    1,
                    "Primeres notes",
                    "Apren a llegir les notes de Do a Si en clau de sol amb moviment conjunt.",
                    "beginner");

            Level rhythmLevel = level(
                    2,
                    "Pols i ritme",
                    "Converteix patrons curts en pulsacio estable abans de tocar-los.",
                    "beginner");

            List<Level> savedLevels = levelRepository.saveAll(List.of(notesLevel, rhythmLevel));
            Level savedNotesLevel = savedLevels.get(0);
            Level savedRhythmLevel = savedLevels.get(1);

            Lesson notesOne = lesson(
                    savedNotesLevel.getId(),
                    1,
                    "Do i Re al pentagrama",
                    "Comenca amb dues notes veines i apren on respiren dins del pentagrama.",
                    "notes",
                    5);
            Lesson notesTwo = lesson(
                    savedNotesLevel.getId(),
                    2,
                    "Mi i Fa: el primer pas",
                    "Afegeix la primera linia i l'espai seguent de la clau de sol.",
                    "notes",
                    6);
            Lesson notesThree = lesson(
                    savedNotesLevel.getId(),
                    3,
                    "Sol, La i Si",
                    "Puja cap al centre del pentagrama i reconeix les notes mes cantables.",
                    "notes",
                    7);
            Lesson notesFour = lesson(
                    savedNotesLevel.getId(),
                    4,
                    "Mini lectura en Do major",
                    "Barreja Do, Re, Mi, Fa, Sol, La i Si com si fossin una petita melodia.",
                    "notes",
                    8);

            Lesson rhythmOne = lesson(
                    savedRhythmLevel.getId(),
                    1,
                    "Negres amb pols constant",
                    "Practica quatre pulsacions regulars sense accelerar.",
                    "ritme",
                    4);
            Lesson rhythmTwo = lesson(
                    savedRhythmLevel.getId(),
                    2,
                    "Blanques i respiracio",
                    "Combina sons de dos temps amb negres per sentir frases mes amples.",
                    "ritme",
                    5);
            Lesson rhythmThree = lesson(
                    savedRhythmLevel.getId(),
                    3,
                    "Silencis curts",
                    "Apren que el silenci tambe forma part del ritme.",
                    "ritme",
                    6);
            Lesson rhythmFour = lesson(
                    savedRhythmLevel.getId(),
                    4,
                    "Frases de quatre pulsacions",
                    "Llegeix patrons una mica mes llargs amb estabilitat i direccio.",
                    "ritme",
                    7);

            List<Lesson> savedLessons = lessonRepository.saveAll(List.of(
                    notesOne,
                    notesTwo,
                    notesThree,
                    notesFour,
                    rhythmOne,
                    rhythmTwo,
                    rhythmThree,
                    rhythmFour));

            Lesson savedNotesOne = savedLessons.get(0);
            Lesson savedNotesTwo = savedLessons.get(1);
            Lesson savedNotesThree = savedLessons.get(2);
            Lesson savedNotesFour = savedLessons.get(3);
            Lesson savedRhythmOne = savedLessons.get(4);
            Lesson savedRhythmTwo = savedLessons.get(5);
            Lesson savedRhythmThree = savedLessons.get(6);
            Lesson savedRhythmFour = savedLessons.get(7);

            exerciseRepository.saveAll(List.of(
                    choice(savedNotesOne.getId(), 1, "easy",
                            "Quina nota veus al pentagrama?", "C4",
                            List.of("Do", "Re", "Mi"),
                            "Do", "Do queda just sota el pentagrama en clau de sol."),
                    note(savedNotesOne.getId(), 2, "easy",
                            "Escriu el nom de la nota marcada.", "D4",
                            "Re", "Re toca la part inferior del pentagrama."),
                    choice(savedNotesOne.getId(), 3, "easy",
                            "Si puges un pas des de Do, arribes a...", "D4",
                            List.of("Re", "Mi", "Fa"),
                            "Re", "Do i Re son notes veines."),

                    choice(savedNotesTwo.getId(), 1, "easy",
                            "Aquesta nota es diu...", "E4",
                            List.of("Re", "Mi", "Fa"),
                            "Mi", "Mi viu a la primera linia de la clau de sol."),
                    note(savedNotesTwo.getId(), 2, "easy",
                            "Quina nota hi ha entre Mi i Sol?", "F4",
                            "Fa", "Quan puges des de Mi, la nota seguent es Fa."),
                    choice(savedNotesTwo.getId(), 3, "easy",
                            "Quina parella puja per graus conjunts?", "F4",
                            List.of("Do-Fa", "Mi-Fa", "Re-Sol"),
                            "Mi-Fa", "Les notes conjuntes estan una al costat de l'altra."),

                    choice(savedNotesThree.getId(), 1, "easy",
                            "Quina nota ocupa la segona linia?", "G4",
                            List.of("Fa", "Sol", "La"),
                            "Sol", "La segona linia de la clau de sol es Sol."),
                    note(savedNotesThree.getId(), 2, "easy",
                            "Escriu la nota situada a l'espai sobre Sol.", "A4",
                            "La", "Despres de Sol, pujant, ve La."),
                    note(savedNotesThree.getId(), 3, "medium",
                            "Identifica la nota de la tercera linia.", "B4",
                            "Si", "La tercera linia de la clau de sol es Si."),

                    choice(savedNotesFour.getId(), 1, "medium",
                            "En una escala de Do major, que ve despres de Si?", "C5",
                            List.of("La", "Do", "Re"),
                            "Do", "Despres de Si tornem a Do en l'octava superior."),
                    note(savedNotesFour.getId(), 2, "medium",
                            "Quina nota tens al quart espai?", "C5",
                            "Do", "El quart espai de la clau de sol es Do agut."),
                    note(savedNotesFour.getId(), 3, "medium",
                            "Llegeix la nota que corona aquesta petita escala.", "D5",
                            "Re", "D5 queda just sobre la linia superior del pentagrama."),

                    rhythm(savedRhythmOne.getId(), 1, "easy",
                            "Escriu el ritme en ordre.",
                            List.of("ta", "ta", "ta", "ta"),
                            "Quatre negres: totes duren un temps."),
                    rhythm(savedRhythmOne.getId(), 2, "easy",
                            "Ara llegeix el mateix pols amb accent final.",
                            List.of("ta", "ta", "ta", "TA"),
                            "La majuscula marca una sensacio d'arribada."),
                    rhythm(savedRhythmOne.getId(), 3, "easy",
                            "Mantingues la pulsacio estable.",
                            List.of("TA", "ta", "ta", "ta"),
                            "No corris despres del primer accent."),

                    rhythm(savedRhythmTwo.getId(), 1, "medium",
                            "Escriu el patro de blanques i negres.",
                            List.of("ta-a", "ta", "ta"),
                            "La blanca aguanta dos temps."),
                    rhythm(savedRhythmTwo.getId(), 2, "medium",
                            "Llegeix aquesta resposta ritmica.",
                            List.of("ta", "ta-a", "ta"),
                            "La blanca al mig fa respirar la frase."),
                    rhythm(savedRhythmTwo.getId(), 3, "medium",
                            "Tanca la frase amb una blanca.",
                            List.of("ta", "ta", "ta-a"),
                            "Els dos ultims temps queden units."),

                    rhythm(savedRhythmThree.getId(), 1, "medium",
                            "Escriu tambe el silenci.",
                            List.of("ta", "sh", "ta", "ta"),
                            "Fes servir sh per indicar un temps de silenci."),
                    rhythm(savedRhythmThree.getId(), 2, "medium",
                            "On cau el silenci?",
                            List.of("sh", "ta", "ta", "ta"),
                            "El primer temps es callat, pero el pols continua."),
                    rhythm(savedRhythmThree.getId(), 3, "medium",
                            "Llegeix sense perdre el centre.",
                            List.of("ta", "ta", "sh", "ta"),
                            "El silenci del tercer temps prepara l'ultim so."),

                    rhythm(savedRhythmFour.getId(), 1, "medium",
                            "Combina durades llargues i curtes.",
                            List.of("ta-a", "ta", "sh"),
                            "Dos temps de so, un temps de so, un de silenci."),
                    rhythm(savedRhythmFour.getId(), 2, "medium",
                            "Llegeix la frase sencera.",
                            List.of("ta", "sh", "ta-a"),
                            "El silenci del segon temps crea impuls cap a la blanca."),
                    rhythm(savedRhythmFour.getId(), 3, "hard",
                            "Ultim repte: mantingues quatre pulsacions clares.",
                            List.of("TA", "ta", "sh", "ta-a"),
                            "Accent, negre, silenci i blanca final.")));
        };
    }

    private static Level level(int orderNumber, String name, String description, String difficulty) {
        return Level.builder()
                .orderNumber(orderNumber)
                .name(name)
                .description(description)
                .difficulty(difficulty)
                .build();
    }

    private static Lesson lesson(
            String levelId,
            int orderNumber,
            String title,
            String description,
            String topic,
            int estimatedMinutes) {
        return Lesson.builder()
                .levelId(levelId)
                .orderNumber(orderNumber)
                .title(title)
                .description(description)
                .musicTheoryTopic(topic)
                .estimatedMinutes(estimatedMinutes)
                .build();
    }

    private static Exercise choice(
            String lessonId,
            int orderNumber,
            String difficulty,
            String question,
            String highlightedNote,
            List<String> options,
            String answer,
            String hint) {
        return Exercise.builder()
                .lessonId(lessonId)
                .type(ExerciseType.MULTIPLE_CHOICE)
                .difficulty(difficulty)
                .orderNumber(orderNumber)
                .questionData("""
                        {"question":"%s","options":%s,"staff":"treble","highlightedNote":"%s","hint":"%s"}
                        """.formatted(question, jsonArray(options), highlightedNote, hint))
                .correctAnswer("""
                        {"answer":"%s"}
                        """.formatted(answer))
                .maxPoints(10)
                .build();
    }

    private static Exercise note(
            String lessonId,
            int orderNumber,
            String difficulty,
            String question,
            String highlightedNote,
            String answer,
            String hint) {
        return Exercise.builder()
                .lessonId(lessonId)
                .type(ExerciseType.NOTE_IDENTIFICATION)
                .difficulty(difficulty)
                .orderNumber(orderNumber)
                .questionData("""
                        {"question":"%s","staff":"treble","highlightedNote":"%s","hint":"%s"}
                        """.formatted(question, highlightedNote, hint))
                .correctAnswer("""
                        {"answer":"%s"}
                        """.formatted(answer))
                .maxPoints(12)
                .build();
    }

    private static Exercise rhythm(
            String lessonId,
            int orderNumber,
            String difficulty,
            String question,
            List<String> pattern,
            String hint) {
        return Exercise.builder()
                .lessonId(lessonId)
                .type(ExerciseType.RHYTHM_READING)
                .difficulty(difficulty)
                .orderNumber(orderNumber)
                .questionData("""
                        {"question":"%s","pattern":%s,"hint":"%s"}
                        """.formatted(question, jsonArray(pattern), hint))
                .correctAnswer("""
                        {"answers":%s}
                        """.formatted(jsonArray(pattern)))
                .maxPoints(15)
                .build();
    }

    private static String jsonArray(List<String> values) {
        return values.stream()
                .map(value -> "\"" + value + "\"")
                .toList()
                .toString();
    }
}
