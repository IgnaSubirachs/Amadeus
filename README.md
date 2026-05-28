# Amadeus

Aplicació per aprendre música amb una estètica retro inspirada en Mozart, sessions curtes tipus joc i una mascota guia: **Wolfi Amadeus**.

![Wolfi Amadeus](docs/wolfi.svg)

## Què hi ha ara

- Backend Spring Boot amb model de progrés per usuari, lliçons, exercicis i puntuació.
- Frontend estàtic integrat dins del backend amb una interfície retro i navegació de lliçons.
- Contingut demo carregat automàticament quan la base de dades està buida.
- Wolfi, un guia bitmap estil anys 80 que acompanya l'usuari durant la sessió.

## Experiència actual

- Catàleg de nivell i lliçó demo.
- Sessió interactiva amb exercicis de nota i ritme.
- Correcció server-side de respostes.
- Marcador de progrés i feedback visual.

## Stack

- Java 26
- Spring Boot 3.2.2
- Spring Data JPA
- Spring Security
- PostgreSQL
- Maven
- JUnit 5 + Mockito

## Executar el projecte

1. Crea la base de dades PostgreSQL:

```sql
CREATE DATABASE amadeus_db;
```

2. Revisa credencials a `amadeus-backend/src/main/resources/application.yml`.
3. Entra a `amadeus-backend` i arrenca:

```bash
mvn spring-boot:run
```

4. Obre [http://localhost:8080](http://localhost:8080).

## Tests

Des de `amadeus-backend`:

```bash
mvn test
```

## Demo inclosa

Quan la base està buida, es creen automàticament:

- `level-notes-1`
- `lesson-notes-1`
- `exercise-note-1`
- `exercise-note-2`
- `exercise-rhythm-1`

## Properes millores naturals

- Més emocions i animacions per a Wolfi.
- Més lliçons i branques d'aprenentatge.
- Autenticació real i persistència de perfil.
- Vista de mapa de curs més completa.
