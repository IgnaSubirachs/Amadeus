# 🐳 Docker - Configuración de PostgreSQL

## 📦 ¿Qué incluye?

Este `docker-compose.yml` levanta:

1. **PostgreSQL 16** - Base de datos principal
2. **pgAdmin** - Interfaz web para gestionar la BD (opcional)

---

## 🚀 Comandos Básicos

### Iniciar PostgreSQL:
```bash
docker-compose up -d
```
- `-d` = modo detached (segundo plano)
- La base de datos estará en: `localhost:5432`
- Credenciales:
  - User: `postgres`
  - Password: `postgres`
  - Database: `amadeus_db`

### Ver logs:
```bash
docker-compose logs -f postgres
```

### Detener contenedores:
```bash
docker-compose down
```

### Detener Y BORRAR datos (reset total):
```bash
docker-compose down -v
```
⚠️ Esto borra todos los datos de la BD

---

## 🌐 pgAdmin (Interfaz Gráfica)

Si quieres gestionar la base de datos visualmente:

1. **Acceder:** http://localhost:5050
2. **Login:**
   - Email: `admin@amadeus.com`
   - Password: `admin`

3. **Conectar a PostgreSQL:**
   - Host: `postgres` (nombre del servicio en Docker)
   - Port: `5432`
   - Username: `postgres`
   - Password: `postgres`

---

## 🔧 Integración con Spring Boot

Tu `application.yml` ya está configurado correctamente:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/amadeus_db
    username: postgres
    password: postgres
```

**¿Por qué `localhost`?**
- Spring Boot corre en tu máquina (NO en Docker)
- PostgreSQL expone el puerto 5432 a tu máquina
- Desde la perspectiva de Spring Boot → la BD está en `localhost:5432`

---

## 🎯 Workflow Completo

### 1. Iniciar base de datos:
```bash
cd amadeus-backend
docker-compose up -d
```

### 2. Verificar que está corriendo:
```bash
docker ps
```
Deberías ver: `amadeus-postgres` y `amadeus-pgadmin`

### 3. Ejecutar Spring Boot:
```bash
mvn spring-boot:run
```

### 4. Al terminar de trabajar:
```bash
docker-compose down
```
(Los datos persisten en el volumen `postgres_data`)

---

## 📊 Persistencia de Datos

Los datos se guardan en un **volumen Docker**:
- Nombre: `postgres_data`
- Ubicación: Gestionado por Docker
- **Persistente:** Sobrevive a `docker-compose down`

Ver volúmenes:
```bash
docker volume ls
```

---

## 🐛 Troubleshooting

### Error: Puerto 5432 ocupado
Otro PostgreSQL está corriendo en tu máquina.

**Solución 1:** Cambiar puerto en `docker-compose.yml`:
```yaml
ports:
  - "5433:5432"  # Usar 5433 en lugar de 5432
```
Y en `application.yml`:
```yaml
url: jdbc:postgresql://localhost:5433/amadeus_db
```

**Solución 2:** Detener PostgreSQL local:
```bash
# Windows (PowerShell como administrador)
net stop postgresql-x64-14
```

### Cannot connect to database
1. Verificar que el contenedor está corriendo: `docker ps`
2. Ver logs: `docker-compose logs postgres`
3. Verificar credenciales en `application.yml`

---

## 🎓 Resumen

| Acción | Comando |
|--------|---------|
| Iniciar BD | `docker-compose up -d` |
| Ver estado | `docker ps` |
| Ver logs | `docker-compose logs -f postgres` |
| Detener | `docker-compose down` |
| Reset completo | `docker-compose down -v` |
| Acceder pgAdmin | http://localhost:5050 |

---

## ✅ Próximo Paso

Una vez `docker-compose up -d` esté corriendo:

```bash
mvn spring-boot:run
```

Spring Boot se conectará automáticamente a PostgreSQL en Docker.
