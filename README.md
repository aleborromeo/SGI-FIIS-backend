<div align="center">

# SGI-FIIS — Sistema de Gestión de Investigación
### Backend API · Facultad de Ingeniería en Informática y Sistemas · UNAS

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker&logoColor=white)](https://docs.docker.com/compose/)
[![SonarQube](https://img.shields.io/badge/SonarQube-Analizado-4E9BCD?logo=sonarqube&logoColor=white)](https://www.sonarsource.com/products/sonarqube/)
[![CI](https://img.shields.io/badge/CI-GitHub_Actions-2088FF?logo=githubactions&logoColor=white)](https://github.com/features/actions)
[![License](https://img.shields.io/badge/Licencia-Académica-lightgrey)](LICENSE)

</div>

---

## Índice

- [Descripción](#descripción)
- [Stack tecnológico](#stack-tecnológico)
- [Arquitectura](#arquitectura)
- [Módulos del sistema](#módulos-del-sistema)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Requisitos previos](#requisitos-previos)
- [Instalación y ejecución](#instalación-y-ejecución)
- [Variables de entorno](#variables-de-entorno)
- [Migraciones de base de datos](#migraciones-de-base-de-datos)
- [Documentación de la API](#documentación-de-la-api)
- [Pruebas](#pruebas)
- [CI/CD](#cicd)
- [Roles del sistema](#roles-del-sistema)
- [Documentación técnica](#documentación-técnica)

---

## Descripción

El **SGI-FIIS** es el backend REST de la plataforma institucional de gestión de investigación para la Facultad de Ingeniería en Informática y Sistemas de la Universidad Nacional Agraria de la Selva (UNAS), Tingo María.

El sistema centraliza y digitaliza los procesos académico-investigativos de la facultad: gestión de convocatorias, proyectos de investigación, planes de tesis, trámites, evaluaciones, resoluciones, informes de avance y reportes de auditoría. Está diseñado para ser consumido por un frontend independiente y soporta autenticación dual: credenciales locales y OAuth 2.0 (Microsoft).

---

## Stack tecnológico

| Componente          | Tecnología                          |
|---------------------|-------------------------------------|
| Lenguaje            | Java 17                             |
| Framework principal | Spring Boot 3.x                     |
| Base de datos       | PostgreSQL 15                       |
| ORM                 | Spring Data JPA / Hibernate         |
| Autenticación       | Spring Security · JWT · OAuth2 (MS) |
| Migraciones BD      | Flyway                              |
| Documentación API   | Springdoc OpenAPI (Swagger UI)      |
| Build tool          | Maven (Wrapper incluido)            |
| Contenedores        | Docker + Docker Compose             |
| Análisis de código  | SonarQube · DeepSource              |
| CI/CD               | GitHub Actions                      |
| Internacionalización| i18n (ES / EN)                      |

---

## Arquitectura

El proyecto implementa **Clean Architecture** (Robert C. Martin), organizada en capas concéntricas donde las dependencias siempre apuntan hacia adentro. El núcleo del negocio es completamente independiente de frameworks, bases de datos y detalles externos.

```
Presentation  →  Application  →  Domain  ←  Infrastructure
   (REST)        (Use Cases)    (Entities)    (JPA / DB)
```

Cada módulo de negocio respeta la misma estructura de cuatro capas:

```
modulo/
├── domain/           ← Entidades de dominio, puertos (interfaces) y eventos. Sin dependencias externas.
├── application/      ← Casos de uso (Use Cases) que orquestan la lógica. Solo conoce Domain.
├── infrastructure/   ← Adaptadores de persistencia JPA, almacenamiento y eventos. Implementa los puertos.
└── presentation/     ← Controllers REST, DTOs HTTP y mappers de presentación.
```

**Beneficios para el equipo:**

| Beneficio | Impacto en SGI-FIIS |
|-----------|---------------------|
| Testabilidad | Cada caso de uso se prueba sin levantar Spring ni la BD |
| Independencia del framework | Lógica de negocio desacoplada de Spring Boot |
| Separación de responsabilidades | Cada integrante trabaja en su módulo sin conflictos |
| Escalabilidad | Nuevos módulos no afectan los existentes |
| Mantenibilidad | Cumple con los RNF de calidad de código del sistema |

---

## Módulos del sistema

| Módulo | Paquete | Descripción |
|--------|---------|-------------|
| **Autenticación** | `auth` | Login local, OAuth2 (Microsoft), JWT, cambio de contraseña, verificación por código |
| **Usuarios** | `users` | CRUD de usuarios, asignación de roles, activación/desactivación |
| **Roles** | `users` | Gestión de roles del sistema |
| **Grupos de Investigación** | `grupos_investigacion` | Creación, coordinadores y membresías |
| **Líneas de Investigación** | `lineas_investigacion` | Registro y cambio de estado por grupo |
| **Convocatorias** | `convocatorias` | Creación y gestión de convocatorias de investigación |
| **Proyectos** | `proyectos` | Registro de proyectos con equipo investigador |
| **Trámites** | `tramites` | Flujo de revisión multi-rol con máquina de estados |
| **Documentación** | `documentacion` | Subida, descarga y desactivación de documentos |
| **Evaluaciones** | `evaluaciones` | Asignación de evaluadores y registro de resultados |
| **Observaciones** | `observations` | Observaciones sobre trámites y subsanaciones |
| **Informes de Avance** | `reportes_progresivos` | Creación, revisión y enmienda de informes |
| **Resoluciones** | `resoluciones` | Emisión y almacenamiento de resoluciones |
| **Reportes** | `reportes` | Reportes de proyectos, trámites, resoluciones e informes |
| **Trazabilidad** | `reportes` | Auditoría de movimientos en el sistema |
| **Dashboards** | `dashboards` | Vistas personalizadas por rol (Admin, Coordinador, Director, Decano, Evaluador, Docente, Estudiante) |
| **Archivos** | `shared` | Controlador centralizado para gestión de archivos |

---

## Estructura del proyecto

```
SGI-FIIS-backend/
├── .env.example                        ← Plantilla de variables de entorno
├── Dockerfile                          ← Imagen de producción
├── docker-compose.yml                  ← Orquestación de servicios (API + PostgreSQL)
├── commitlint.config.cjs               ← Convención de commits (Conventional Commits)
│
├── .github/
│   ├── pull_request_template.md
│   └── workflows/
│       ├── backend-ci.yml              ← Pipeline CI: build, test, SonarQube
│       ├── commit-check.yml            ← Validación de formato de commits
│       ├── docker-validation.yml       ← Validación del Dockerfile
│       ├── flyway-validation.yml       ← Validación de migraciones SQL
│       └── sonar.yml                   ← Análisis estático con SonarQube
│
├── docs/                               ← Documentación del proyecto
│   ├── SistemaGestionInvestigacion_FIIS.md   ← Arquitectura y guía del equipo
│   ├── Sistema de Gestión de Investigación FIIS.md
│   ├── Requisitos del Sistema de Gestión de Investigación FIIS.docx
│   ├── Reporte_Final_Documentacion_Modulos.docx
│   ├── DB_FIIS_Investigacion_pgadmin.sql     ← Esquema de referencia
│   ├── prototipos_sgi_fiis.html              ← Prototipos UI
│   ├── tramites-api-testing-guide.md
│   └── modulo_observaciones/
│       └── documentacion.md
│
└── fiis/                               ← Proyecto Spring Boot
    ├── pom.xml
    ├── docs/
    │   ├── Requisitos_del_sistema.md
    │   ├── Matriz_de_trazabilidad.md
    │   ├── flujos_procesos.md
    │   ├── evaluaciones/
    │   └── images/                     ← Diagramas de arquitectura y casos de uso
    │
    └── src/
        ├── main/
        │   ├── java/com/sgi/fiis/
        │   │   ├── FiisApplication.java
        │   │   ├── auth/               ← Módulo de autenticación
        │   │   ├── users/              ← Módulo de usuarios
        │   │   ├── grupos_investigacion/
        │   │   ├── lineas_investigacion/
        │   │   ├── convocatorias/
        │   │   ├── proyectos/
        │   │   ├── tramites/
        │   │   ├── documentacion/
        │   │   ├── evaluaciones/
        │   │   ├── observations/
        │   │   ├── reportes_progresivos/
        │   │   ├── resoluciones/
        │   │   ├── reportes/
        │   │   ├── dashboards/
        │   │   └── shared/             ← Kernel compartido (excepciones, auditoría, config)
        │   │
        │   └── resources/
        │       ├── application.yml
        │       ├── i18n/               ← Mensajes ES / EN por módulo
        │       └── db/migration/       ← Scripts Flyway (V1 al V11)
        │
        └── test/                       ← Suite completa de pruebas por módulo
```

---

## Requisitos previos

- **Java 17** o superior
- **Maven 3.8+** (o usar el wrapper incluido `./mvnw`)
- **Docker** y **Docker Compose** (para ejecución con contenedores)
- **PostgreSQL 15** (si se ejecuta sin Docker)

---

## Instalación y ejecución

### Opción A — Con Docker Compose (recomendada)

Levanta la API y PostgreSQL con un solo comando:

```bash
# 1. Clonar el repositorio y cambiar a la rama de desarrollo
git clone https://github.com/aleborromeo/SGI-FIIS-backend.git
cd SGI-FIIS-backend
git checkout develop

# 2. Configurar las variables de entorno
cp .env.example .env
# Editar .env con tus credenciales

# 3. Levantar los servicios
docker compose up --build
```

Los servicios quedan disponibles en:
- **API REST:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **PostgreSQL:** `localhost:5433` (mapeado desde el contenedor)

### Opción B — Ejecución local con Maven

```bash
cd fiis

# Compilar y ejecutar pruebas
./mvnw clean verify

# Ejecutar la aplicación
./mvnw spring-boot:run
```

> Las migraciones de base de datos (Flyway) se ejecutan automáticamente al arrancar.

---

## Variables de entorno

Copia `.env.example` a `.env` y completa los valores necesarios:

```env
# Base de datos
DB_HOST=localhost
DB_PORT=5432
DB_NAME=db_fiis_investigacion
DB_USER=postgres
DB_PASSWORD=

# Seguridad y JWT
JWT_SECRET=
APP_FRONTEND_URL=http://localhost:3000

# Correo (SMTP)
SPRING_MAIL_HOST=smtp.gmail.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=sgifiis@gmail.com
SPRING_MAIL_PASSWORD=
APP_MAIL_MOCK=false          # true para entornos de desarrollo sin SMTP
```

> **Importante:** Nunca subas el archivo `.env` al repositorio. Está incluido en `.gitignore`.

---

## Migraciones de base de datos

El proyecto usa **Flyway** para gestión de migraciones. Los scripts se ubican en `fiis/src/main/resources/db/migration/` y se ejecutan en orden automáticamente al iniciar la aplicación.

| Versión | Descripción |
|---------|-------------|
| V1 | Creación de roles y tabla de usuarios |
| V2 | Inserción de usuarios de prueba |
| V3 | Soporte OAuth: columna `provider` en usuarios |
| V4 | Código de verificación de registro |
| V5 | Actualización de emails de usuarios |
| V6 | Tabla de documentos |
| V7 | Tablas restantes del dominio |
| V8 | Tablas de observaciones y subsanaciones |
| V9 | Soporte multilenguaje |
| V10 | Ajustes al esquema de trámites |
| V11 | Datos de prueba para procedimientos |

El esquema de referencia completo está disponible en `docs/DB_FIIS_Investigacion_pgadmin.sql`.

---

## Documentación de la API

Una vez que la aplicación esté corriendo, la documentación interactiva de la API se encuentra en:

```
http://localhost:8080/swagger-ui/index.html
```

La API está protegida con JWT. Para explorar los endpoints en Swagger:

1. Obtener un token via `POST /api/auth/login`
2. Hacer clic en **Authorize** e ingresar `Bearer <token>`

---

## Pruebas

El proyecto cuenta con una suite completa de pruebas unitarias e integración por módulo:

```bash
# Ejecutar todas las pruebas
cd fiis && ./mvnw test

# Ejecutar pruebas con reporte de cobertura
./mvnw verify
```

Las pruebas cubren todas las capas: casos de uso (application), modelos de dominio, adaptadores de persistencia, controllers y mappers.

---

## CI/CD

El repositorio cuenta con pipelines de GitHub Actions que se activan en cada push y pull request hacia `develop`:

| Workflow | Descripción |
|----------|-------------|
| `backend-ci.yml` | Build Maven, ejecución de tests y análisis SonarQube |
| `commit-check.yml` | Validación del formato de commits (Conventional Commits) |
| `docker-validation.yml` | Verifica que el `Dockerfile` construya correctamente |
| `flyway-validation.yml` | Valida la integridad y orden de los scripts de migración |
| `sonar.yml` | Análisis estático de calidad de código |

El proyecto también usa **DeepSource** para análisis continuo de seguridad y buenas prácticas.

---

## Roles del sistema

El sistema implementa control de acceso basado en roles (RBAC):

| Rol | Descripción |
|-----|-------------|
| `ADMIN` | Administrador del sistema. Gestión completa de usuarios y configuración. |
| `DIRECTOR` | Director de la FIIS. Aprueba en el nivel más alto del flujo de trámites. |
| `DECANO` | Decano de la facultad. Firma resoluciones finales. |
| `COORDINADOR` | Coordinador de grupo de investigación. Primera instancia revisora. |
| `EVALUADOR` | Evaluador externo o interno asignado a proyectos. |
| `DOCENTE` | Docente investigador. Crea y gestiona proyectos. |
| `ESTUDIANTE` | Estudiante. Registra planes de tesis y consulta su estado. |
| `INVESTIGADOR` | Investigador activo en grupos y proyectos. |

---

## Documentación técnica

La carpeta `docs/` y `fiis/docs/` contienen documentación completa del sistema:

- **Arquitectura y guía del equipo** → `docs/SistemaGestionInvestigacion_FIIS.md`
- **Requisitos del sistema** → `fiis/docs/Requisitos_del_sistema.md`
- **Flujos de procesos** → `fiis/docs/flujos_procesos.md`
- **Matriz de trazabilidad** → `fiis/docs/Matriz_de_trazabilidad.md`
- **Documentación de evaluaciones** → `fiis/docs/evaluaciones/`
- **Guía de testing de trámites** → `docs/tramites-api-testing-guide.md`
- **Diagramas (arquitectura, casos de uso por rol)** → `fiis/docs/images/`
- **Prototipos de interfaz** → `docs/prototipos_sgi_fiis.html`
- **Reporte final de módulos** → `docs/Reporte_Final_Documentacion_Modulos.docx`

---

<div align="center">

**Universidad Nacional Agraria de la Selva · FIIS · Tingo María, Perú**

</div>
