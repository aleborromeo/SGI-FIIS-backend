# Documentación del Módulo Líneas de Investigación

## 1. Información general del módulo

| Campo                      | Descripción                                        |
| --------------------------- | ----------------------------------------------------- |
| **Proyecto**                | Sistema de Gestión de Investigación FIIS               |
| **Módulo**                  | Líneas de Investigación (`lineas_investigacion`)       |
| **Rama de trabajo**         | `feature/lineas_de_investigacion`                      |
| **Arquitectura aplicada**   | Clean Architecture                                     |
| **Base de datos**           | PostgreSQL                                             |
| **Framework**               | Spring Boot                                            |
| **Paquete raíz**            | `com.sgi.fiis.lineas_investigacion`                    |
| **Prefijo de endpoints**    | `/api/v1/research-lines`                               |

---

## 2. Descripción general

El módulo **Líneas de Investigación** administra el catálogo de líneas de investigación de la FIIS (por ejemplo "Computacion", "Ingenieria de software", "Ciberseguridad y Auditoria de TI", etc.), su estado (activa/inactiva) y su asociación con los grupos de investigación. Estas líneas se muestran en los formularios de postulación de proyectos y planes de tesis.

El módulo está construido bajo **Clean Architecture**, separando el modelo de dominio, los casos de uso, la persistencia (Spring Data JPA) y los controladores REST. Colabora estrechamente con el módulo `grupos_investigacion`: `ResearchGroupController` expone `GET /api/v1/research-groups/{id}/lines` delegando en `ListResearchLinesByGroupUseCase` de este módulo.

---

## 3. Objetivo del módulo

Automatizar la gestión del catálogo de líneas de investigación dentro del SGI-FIIS, permitiendo controlar:

* Qué líneas de investigación existen y si están activas.
* Qué líneas están asociadas a cada grupo de investigación.
* Que solo se muestren líneas activas en los formularios de registro de proyectos/planes de tesis.

---

## 4. Alcance funcional

El módulo implementa las siguientes funcionalidades:

* Registrar una línea de investigación (nombre único).
* Listar todas las líneas de investigación, o únicamente las activas (`onlyActive=true`).
* Consultar una línea de investigación por su identificador.
* Activar o desactivar una línea de investigación.
* Listar las líneas activas asociadas a un grupo de investigación específico.

---

## 5. Requisitos funcionales relacionados

El módulo se encuentra relacionado con los requisitos funcionales **RF-24 al RF-28** (`docs/requisitos_sistema.md`).

| Código  | Requisito funcional                                                                                              | Implementación en el módulo                                                              |
| ------- | ------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------ |
| RF-24   | El sistema debe permitir registrar líneas de investigación asociadas a la facultad o programa académico.           | `POST /api/v1/research-lines` (`RegisterResearchLineUseCase`).                              |
| RF-25   | El formulario de postulación debe mostrar el campo "Línea de investigación".                                       | `GET /api/v1/research-lines?onlyActive=true` provee las opciones activas para el formulario. |
| RF-26   | Para el grupo GINSOFT, el sistema debe mostrar únicamente las líneas "Computacion" e "Ingenieria de software".      | `GET /api/v1/research-groups/{id}/lines` (`ListResearchLinesByGroupUseCase` + tabla `lineas_por_grupo`, sembrada en `V7__create_remaining_tables.sql`). |
| RF-27   | El sistema debe permitir activar o desactivar líneas de investigación.                                             | `PATCH /api/v1/research-lines/{id}/status` (`ChangeResearchLineStatusUseCase`).             |
| RF-28   | El sistema debe evitar mostrar líneas inactivas en los formularios de registro de proyectos.                       | `findAllActive()` / `findActiveByGroup()` filtran por `es_activa = true` a nivel de consulta. |

> **Nota de trazabilidad:** el comentario Javadoc `GET /` en `ResearchLineController` referencia `RF-25, RF-27, RF-28` para el listado general, mientras que el registro usa `RF-24` y el cambio de estado usa `RF-27`; esta numeración coincide razonablemente con `docs/requisitos_sistema.md`, a diferencia del módulo `grupos_investigacion` donde sí existe una discrepancia (ver su documentación).

---

## 6. Arquitectura aplicada

El módulo sigue el enfoque de **Clean Architecture**, separando responsabilidades en cuatro capas:

```text
lineas_investigacion
├── application
├── domain
├── infrastructure
└── presentation
```

Esta separación evita el acoplamiento directo entre los controladores REST y la base de datos, y mantiene aislada la lógica de negocio de los detalles técnicos de persistencia.

---

## 7. Estructura del módulo

```text
lineas_investigacion
├── application
│   ├── dto
│   │   ├── ResearchLineRequestDto.java
│   │   └── ResearchLineResponseDto.java
│   └── usecase
│       ├── ChangeResearchLineStatusUseCase.java
│       ├── GetResearchLineUseCase.java
│       ├── ListResearchLinesByGroupUseCase.java
│       ├── ListResearchLinesUseCase.java
│       └── RegisterResearchLineUseCase.java
│
├── domain
│   ├── model
│   │   └── ResearchLine.java
│   └── port
│       └── ResearchLineRepositoryPort.java
│
├── infrastructure
│   └── persistence
│       ├── ResearchLineEntity.java
│       ├── ResearchLineRepositoryAdapter.java
│       └── SpringDataResearchLineRepository.java
│
└── presentation
    ├── controller
    │   └── ResearchLineController.java
    └── mapper
        └── ResearchLineMapper.java
```

---

## 8. Descripción de capas

### 8.1. Capa Domain

Contiene el núcleo del módulo: el modelo de negocio y el puerto de salida.

Archivos principales:

```text
domain/model/ResearchLine.java
domain/port/ResearchLineRepositoryPort.java
```

Responsabilidades:

* Representar el modelo de línea de investigación (`id`, `lineName`, `active`, `createdAt`, `updatedAt`).
* Implementar el comportamiento de dominio `activate()` / `deactivate()`, que además actualiza `updatedAt`.
* Declarar el puerto de salida `ResearchLineRepositoryPort` que la capa de aplicación consume sin depender de JPA.

### 8.2. Capa Application

Contiene los casos de uso del módulo, cada uno como un `@Service` independiente.

Archivos principales:

```text
application/usecase/RegisterResearchLineUseCase.java
application/usecase/ListResearchLinesUseCase.java
application/usecase/GetResearchLineUseCase.java
application/usecase/ChangeResearchLineStatusUseCase.java
application/usecase/ListResearchLinesByGroupUseCase.java
application/dto/ResearchLineRequestDto.java
application/dto/ResearchLineResponseDto.java
```

Responsabilidades:

* Registrar líneas validando unicidad del nombre (`existsByName`) y fijando `active=true`, `createdAt`/`updatedAt` al momento de creación.
* Listar todas las líneas o solo las activas, según el parámetro `onlyActive`.
* Consultar una línea por identificador.
* Activar/desactivar una línea delegando en el comportamiento de dominio `ResearchLine.activate()`/`deactivate()`.
* Listar las líneas activas asociadas a un grupo (usado desde `ResearchGroupController`).

### 8.3. Capa Infrastructure

Implementa la persistencia mediante **Spring Data JPA**, incluyendo una consulta nativa para resolver la relación N a N entre grupos y líneas.

Archivos principales:

```text
infrastructure/persistence/ResearchLineEntity.java
infrastructure/persistence/SpringDataResearchLineRepository.java
infrastructure/persistence/ResearchLineRepositoryAdapter.java
```

Responsabilidades:

* Mapear la tabla `lineas_investigacion`.
* Implementar `ResearchLineRepositoryPort` mediante `SpringDataResearchLineRepository`.
* Resolver `findActiveByGroupId` mediante una consulta nativa (`@Query(nativeQuery = true)`) que hace `INNER JOIN` entre `lineas_investigacion` y la tabla intermedia `lineas_por_grupo` (definida y sembrada en `V7__create_remaining_tables.sql`, sin entidad JPA propia — se accede solo vía SQL nativo).

### 8.4. Capa Presentation

Expone los endpoints REST del módulo.

Archivos principales:

```text
presentation/controller/ResearchLineController.java
presentation/mapper/ResearchLineMapper.java
```

Responsabilidades:

* Recibir solicitudes HTTP y validarlas (`@Valid`).
* Delegar la ejecución a los casos de uso correspondientes.
* Convertir entre DTOs y modelo de dominio mediante `ResearchLineMapper`.
* Aplicar autorización a nivel de método con `@PreAuthorize` (`hasRole('ADMIN')` / `isAuthenticated()`), reforzada también a nivel de filtro HTTP en `SecurityConfig`.

---

## 9. Reglas de negocio implementadas

| N.º | Regla de negocio                                                                                                |
| --- | -------------------------------------------------------------------------------------------------------------------- |
| 1   | El nombre de una línea (`lineName`) debe ser único → `DuplicateResourceException` (409).                              |
| 2   | Toda línea nueva se registra activa (`active = true`), con `createdAt` y `updatedAt` fijados al momento de creación.  |
| 3   | Activar/desactivar una línea actualiza `updatedAt` mediante el método de dominio `ResearchLine.activate()`/`deactivate()`. |
| 4   | Consultar o cambiar el estado de una línea inexistente lanza `ResourceNotFoundException` (404).                        |
| 5   | El listado puede filtrarse para excluir líneas inactivas (`onlyActive=true`), evitando que se muestren en formularios de registro (RF-28). |
| 6   | El listado de líneas por grupo (`findActiveByGroup`) solo devuelve líneas activas, aunque la asociación grupo-línea en `lineas_por_grupo` exista independientemente del estado. |
| 7   | Registrar líneas y cambiar su estado están restringidos al rol `ADMIN` (`@PreAuthorize` + `SecurityConfig`); las operaciones de lectura solo requieren estar autenticado. |

---

## 10. Endpoints implementados

Controlador: `ResearchLineController` — base `/api/v1/research-lines`.

| Método   | Endpoint            | Descripción                                                          | Autorización            |
| -------- | --------------------- | ------------------------------------------------------------------------ | -------------------------- |
| `POST`   | `/`                    | Registra una línea de investigación.                                       | `hasRole('ADMIN')`         |
| `GET`    | `/?onlyActive=`        | Lista líneas de investigación (todas por defecto, o solo activas).         | Autenticado                |
| `GET`    | `/{id}`                | Obtiene una línea por su identificador.                                     | Autenticado                |
| `PATCH`  | `/{id}/status`         | Activa o desactiva una línea (`{"active": true|false}`).                    | `hasRole('ADMIN')`         |

Endpoint adicional expuesto desde el módulo `grupos_investigacion`, delegando en este módulo:

| Método | Endpoint                              | Descripción                                              | Autorización |
| ------ | ---------------------------------------- | ------------------------------------------------------------- | --------------- |
| `GET`  | `/api/v1/research-groups/{id}/lines`      | Lista las líneas de investigación activas de un grupo (RF-26). | Autenticado     |

Todas las rutas de escritura (`POST`, `PATCH`) están además restringidas a `ROLE_ADMIN` a nivel de filtro HTTP en `SecurityConfig` (`auth/infrastructure/security/SecurityConfig.java`), como segunda capa de defensa independiente del `@PreAuthorize` del controlador.

---

## 11. Tablas utilizadas

### `lineas_investigacion`

| Campo                  | Descripción                                        |
| ------------------------ | ------------------------------------------------------ |
| `id_linea`                | Identificador único de la línea (PK).                    |
| `nombre_linea`             | Nombre único de la línea de investigación.               |
| `es_activa`                | Indica si la línea está activa.                          |
| `fecha_creacion`           | Fecha de creación del registro.                          |
| `fecha_actualizacion`      | Fecha de la última actualización de estado.              |

### `lineas_por_grupo` (tabla intermedia, sin entidad JPA propia)

| Campo       | Descripción                                                  |
| ------------- | ----------------------------------------------------------------- |
| `id_grupo`     | FK a `grupos_investigacion.id_grupo`.                              |
| `id_linea`     | FK a `lineas_investigacion.id_linea`.                              |

Clave primaria compuesta (`id_grupo`, `id_linea`). Solo se accede mediante la consulta nativa `findActiveByGroupId` de `SpringDataResearchLineRepository`.

Ambas tablas se crean y siembran en la migración Flyway `fiis/src/main/resources/db/migration/V7__create_remaining_tables.sql`, que incluye 6 líneas base ("Computacion", "Ingenieria de software", "Ciberseguridad y Auditoria de TI", "Ciencia de Datos e Inteligencia Artificial", "Redes y Telecomunicaciones", "Gestion de Tecnologias de Informacion") y su asociación inicial con el grupo GINSOFT, en línea con RF-26.

---

## 12. Pruebas unitarias existentes

| Módulo               | Con test                                                                                            | Sin test |
| ----------------------- | ---------------------------------------------------------------------------------------------------- | ---------- |
| Casos de uso            | `RegisterResearchLineUseCaseTest`, `ChangeResearchLineStatusUseCaseTest`, `GetResearchLineUseCaseTest`, `ListResearchLinesUseCaseTest`, `ListResearchLinesByGroupUseCaseTest` | —          |
| Persistencia            | `ResearchLineRepositoryAdapterTest`                                                                     | —          |
| Presentación            | `ResearchLineControllerTest`, `ResearchLineMapperTest`                                                   | —          |

Todos los casos de uso, el adaptador de persistencia, el controlador y el mapper del módulo cuentan con pruebas unitarias en `fiis/src/test/java/com/sgi/fiis/lineas_investigacion/`.

---

## 13. Conclusión

El módulo **Líneas de Investigación** permite administrar el catálogo de líneas de investigación de la FIIS y su asociación con los grupos institucionales, respetando los principios de Clean Architecture y las reglas de negocio definidas en RF-24 a RF-28. Su diseño mantiene el dominio independiente de JPA y expone un punto de integración (`ListResearchLinesByGroupUseCase`) reutilizado por el módulo `grupos_investigacion` para resolver RF-26.

---

## 14. Decisiones técnicas tomadas

| Decisión                                                              | Justificación                                                                                             |
| -------------------------------------------------------------------------| --------------------------------------------------------------------------------------------------------- |
| Separar el módulo en capas de Clean Architecture                         | Mantiene separado el dominio, los casos de uso, la persistencia y los controladores REST.                  |
| Un `@Service` por caso de uso, sin puerto de entrada explícito            | Simplifica la estructura del módulo respecto a un esquema `UseCase` interfaz + `Service` implementación.   |
| Encapsular `activate()`/`deactivate()` en el modelo de dominio            | Evita duplicar la lógica de actualización de `updatedAt` en cada caso de uso que cambia el estado.         |
| Consulta nativa (`@Query(nativeQuery = true)`) para `lineas_por_grupo`   | Evita crear una entidad JPA adicional solo para una tabla de asociación N a N de solo lectura.              |
| Exponer `GET /{id}/lines` desde `ResearchGroupController` en lugar de `ResearchLineController` | El recurso "líneas de un grupo" se modela como sub-recurso de `research-groups`, manteniendo la coherencia REST. |

---

## 15. Mejoras futuras

* Agregar un endpoint para gestionar la asociación grupo-línea (`lineas_por_grupo`) desde este módulo, en lugar de depender únicamente de los datos sembrados por la migración Flyway.
* Agregar una entidad JPA para `lineas_por_grupo` si en el futuro se necesita más que una consulta de lectura (por ejemplo, asociar/desasociar líneas de un grupo vía API).
* Agregar paginación en `GET /api/v1/research-lines` si el catálogo de líneas crece significativamente.
* Agregar pruebas de integración end-to-end (contenedor Docker + Postgres) que documenten explícitamente el flujo registrar línea → activar/desactivar → listar por grupo, similar a lo documentado para el módulo Evaluaciones.
* Validar en `ChangeResearchLineStatusUseCase` el impacto de desactivar una línea que ya esté referenciada por proyectos o planes de tesis activos.

---

## 16. Estado actual del módulo

El módulo Líneas de Investigación se encuentra en estado funcional. Actualmente permite:

* Registrar, listar (todas o solo activas) y consultar líneas de investigación.
* Activar o desactivar líneas, respetando la regla de no mostrar líneas inactivas en formularios (RF-28).
* Listar las líneas activas asociadas a un grupo de investigación específico (RF-26), consumido por `grupos_investigacion`.
* Persistir la información en PostgreSQL mediante migraciones Flyway (`V7__create_remaining_tables.sql`), con las líneas base y su asociación a GINSOFT ya sembradas.
* Restringir las operaciones de escritura al rol `ADMIN`, tanto a nivel de filtro HTTP (`SecurityConfig`) como de método (`@PreAuthorize`).
