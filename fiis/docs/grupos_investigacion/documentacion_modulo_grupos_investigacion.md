# Documentación del Módulo Grupos de Investigación

## 1. Información general del módulo

| Campo                      | Descripción                                        |
| --------------------------- | --------------------------------------------------- |
| **Proyecto**                | Sistema de Gestión de Investigación FIIS             |
| **Módulo**                  | Grupos de Investigación (`grupos_investigacion`)     |
| **Rama de trabajo**         | `feature/lineas_de_investigacion` (base histórica: `feature/grupos_de_investigacion`) |
| **Arquitectura aplicada**   | Clean Architecture                                   |
| **Base de datos**           | PostgreSQL                                           |
| **Framework**               | Spring Boot                                          |
| **Paquete raíz**            | `com.sgi.fiis.grupos_investigacion`                  |
| **Prefijo de endpoints**    | `/api/v1/research-groups`                            |

---

## 2. Descripción general

El módulo **Grupos de Investigación** administra los grupos institucionales de investigación de la FIIS (por ejemplo GINSOFT, RESEGTI, GISI, CICO, EAP, MAP y EU), sus coordinadores y sus miembros (docentes investigadores). Permite crear grupos, asignar un coordinador, agregar y retirar miembros, y consultar tanto los grupos como las líneas de investigación asociadas a cada uno.

El módulo está construido bajo **Clean Architecture**, separando el modelo de dominio, los casos de uso, la persistencia (JPA + `JdbcTemplate`) y los controladores REST. Colabora además con el módulo `lineas_investigacion` (para listar líneas asociadas a un grupo) y es consumido por otros módulos del sistema, como `proyectos` (validación de existencia de grupo) y `documentos` (autorización de descarga de archivos por coordinador de grupo).

---

## 3. Objetivo del módulo

Automatizar la gestión de los grupos de investigación dentro del SGI-FIIS, permitiendo controlar:

* Qué grupos de investigación existen y si están activos.
* Quién es el coordinador actual de cada grupo.
* Qué docentes investigadores integran cada grupo (membresía activa e historial).
* Que un docente no pertenezca a más de un grupo activo a la vez.
* Qué líneas de investigación están asociadas a cada grupo.

---

## 4. Alcance funcional

El módulo implementa las siguientes funcionalidades:

* Crear un grupo de investigación (código único + nombre).
* Listar todos los grupos de investigación registrados.
* Consultar un grupo por su identificador.
* Asignar un coordinador a un grupo (debe ser un usuario activo con rol `COORDINADOR_GRUPO`).
* Agregar un miembro (docente investigador) a un grupo.
* Retirar un miembro de un grupo (soft delete, conserva historial).
* Listar los miembros activos de un grupo.
* Listar las líneas de investigación activas asociadas a un grupo.

---

## 5. Requisitos funcionales relacionados

El módulo se encuentra relacionado con los requisitos funcionales **RF-15 al RF-23** (`docs/requisitos_sistema.md`).

| Código  | Requisito funcional                                                                                              | Implementación en el módulo                                                              |
| ------- | ------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------ |
| RF-15   | El sistema debe permitir visualizar los grupos de investigación registrados (GINSOFT, RESEGTI, GISI, CICO, EAP, MAP, EU). | `GET /api/v1/research-groups` — datos sembrados en `V7__create_remaining_tables.sql`.     |
| RF-16   | El administrador debe poder consultar el coordinador asignado a cada grupo.                                        | `GET /api/v1/research-groups` y `GET /api/v1/research-groups/{id}` incluyen `currentCoordinatorId`, `coordinatorFirstNames`, `coordinatorLastNames`. |
| RF-17   | El administrador debe poder asignar docentes investigadores a un grupo.                                            | `POST /api/v1/research-groups/{id}/members`.                                              |
| RF-18   | El administrador debe poder asignar un coordinador a cada grupo.                                                   | `PATCH /api/v1/research-groups/{id}/coordinator`.                                          |
| RF-19   | El sistema debe registrar la membresía activa de cada docente en la tabla de miembros del grupo.                   | `AssignMemberUseCase` crea una `Membership` con `active=true` y `startDate`.               |
| RF-20   | El sistema debe permitir retirar o desactivar miembros de un grupo sin eliminar su historial.                      | `DELETE /api/v1/research-groups/{id}/members/{userId}` → soft delete (`Membership.remove()`). |
| RF-21   | El sistema debe impedir que un usuario tenga más de una membresía activa en distintos grupos.                      | `AssignMemberUseCase` valida `existsActiveByUser`; además existe el índice único parcial `uq_membresias_activas` en base de datos. |
| RF-22   | El sistema debe mostrar en cada grupo los miembros activos y el coordinador actual.                                | `GET /api/v1/research-groups/{id}/members` + campos de coordinador en la respuesta del grupo. |
| RF-23   | El sistema debe actualizar automáticamente el dashboard del coordinador según el grupo al que pertenece.           | Cubierto por el módulo `dashboards` (`/api/v1/dashboard/coordinator/**`), que consulta la membresía activa expuesta por este módulo. |

> **Nota de trazabilidad:** los comentarios Javadoc de `ResearchGroupController` (por ejemplo `RF-22: Create research group`, `RF-23: List research groups`, `RF-19: Assign coordinator`, `RF-20: Add/Remove member`, `RF-26: List research lines`) usan una numeración distinta a la de `docs/requisitos_sistema.md`. La tabla anterior refleja la numeración oficial de los requisitos; se recomienda alinear los comentarios del código en una futura limpieza.

---

## 6. Arquitectura aplicada

El módulo sigue el enfoque de **Clean Architecture**, separando responsabilidades en cuatro capas:

```text
grupos_investigacion
├── application
├── domain
├── infrastructure
└── presentation
```

Esta separación evita el acoplamiento directo entre los controladores REST y la base de datos, y mantiene aislada la lógica de negocio de los detalles técnicos de persistencia.

---

## 7. Estructura del módulo

```text
grupos_investigacion
├── application
│   ├── dto
│   │   ├── AssignCoordinatorRequestDto.java
│   │   ├── AssignMemberRequestDto.java
│   │   ├── MembershipResponseDto.java
│   │   ├── ResearchGroupRequestDto.java
│   │   └── ResearchGroupResponseDto.java
│   └── usecase
│       ├── AssignCoordinatorUseCase.java
│       ├── AssignMemberUseCase.java
│       ├── CreateGroupUseCase.java
│       ├── GetGroupUseCase.java
│       ├── ListGroupsUseCase.java
│       ├── ListMembersUseCase.java
│       └── RemoveMemberUseCase.java
│
├── domain
│   ├── model
│   │   ├── Membership.java
│   │   └── ResearchGroup.java
│   └── port
│       ├── MembershipRepositoryPort.java
│       └── ResearchGroupRepositoryPort.java
│
├── infrastructure
│   └── persistence
│       ├── GroupMembershipEntity.java            (uso cruzado, ver sección 8.3)
│       ├── GroupMembershipJpaRepository.java     (uso cruzado, ver sección 8.3)
│       ├── MembershipEntity.java
│       ├── MembershipRepositoryAdapter.java
│       ├── ResearchGroupEntity.java
│       ├── ResearchGroupJpaRepository.java       (uso cruzado, ver sección 8.3)
│       ├── ResearchGroupRepositoryAdapter.java
│       ├── SpringDataMembershipRepository.java
│       └── SpringDataResearchGroupRepository.java
│
└── presentation
    ├── controller
    │   └── ResearchGroupController.java
    └── mapper
        └── ResearchGroupMapper.java
```

---

## 8. Descripción de capas

### 8.1. Capa Domain

Contiene el núcleo del módulo: los modelos de negocio y los puertos de salida.

Archivos principales:

```text
domain/model/ResearchGroup.java
domain/model/Membership.java
domain/port/ResearchGroupRepositoryPort.java
domain/port/MembershipRepositoryPort.java
```

Responsabilidades:

* Representar el modelo de grupo de investigación (`id`, `groupCode`, `groupName`, `currentCoordinatorId`, datos enriquecidos del coordinador, `active`).
* Representar el modelo de membresía (`id`, `groupId`, `userId`, datos enriquecidos del usuario, `active`, `startDate`, `endDate`) e implementar el comportamiento `remove()` (soft delete).
* Declarar los puertos de salida (`ResearchGroupRepositoryPort`, `MembershipRepositoryPort`) que la capa de aplicación consume sin depender de JPA.

### 8.2. Capa Application

Contiene los casos de uso del módulo, cada uno como un `@Service` independiente (sin una interfaz de "puerto de entrada" explícita, a diferencia del módulo Evaluaciones).

Archivos principales:

```text
application/usecase/CreateGroupUseCase.java
application/usecase/ListGroupsUseCase.java
application/usecase/GetGroupUseCase.java
application/usecase/AssignCoordinatorUseCase.java
application/usecase/AssignMemberUseCase.java
application/usecase/RemoveMemberUseCase.java
application/usecase/ListMembersUseCase.java
application/dto/ResearchGroupRequestDto.java
application/dto/ResearchGroupResponseDto.java
application/dto/AssignCoordinatorRequestDto.java
application/dto/AssignMemberRequestDto.java
application/dto/MembershipResponseDto.java
```

Responsabilidades:

* Crear grupos validando unicidad del código (`existsByCode`).
* Asignar coordinador validando que el usuario exista, esté activo y tenga el rol `COORDINADOR_GRUPO`.
* Agregar miembros validando rol `DOCENTE_INVESTIGADOR` y la regla RF-21 (una sola membresía activa).
* Retirar miembros (soft delete) y listar miembros activos de un grupo.
* Listar y consultar grupos.

### 8.3. Capa Infrastructure

Implementa la persistencia combinando **Spring Data JPA** y **`JdbcTemplate`** (SQL nativo) para enriquecer las respuestas con datos de la tabla `usuarios` (nombres, apellidos, correo, validación de rol).

Archivos principales:

```text
infrastructure/persistence/ResearchGroupEntity.java
infrastructure/persistence/MembershipEntity.java
infrastructure/persistence/SpringDataResearchGroupRepository.java
infrastructure/persistence/SpringDataMembershipRepository.java
infrastructure/persistence/ResearchGroupRepositoryAdapter.java
infrastructure/persistence/MembershipRepositoryAdapter.java
```

Responsabilidades:

* Mapear las tablas `grupos_investigacion` y `membresias_grupo`.
* Implementar `ResearchGroupRepositoryPort` y `MembershipRepositoryPort` combinando `JpaRepository` con consultas SQL nativas (`JOIN` contra `usuarios` y `roles`/`usuarios_roles`) para resolver nombre/apellido/correo del coordinador o del miembro, y para validar el rol institucional del usuario.

**Uso cruzado desde otros módulos.** Además de los adaptadores propios del módulo, existen dos artefactos JPA adicionales reutilizados por otros módulos del sistema:

| Artefacto                         | Consumido por                                                                 | Propósito                                                                                  |
| ---------------------------------- | -------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------- |
| `ResearchGroupJpaRepository`       | `proyectos/infrastructure/persistence/SaveProjectAdapter.java`                   | Verificar la existencia del grupo de investigación al registrar un proyecto.                  |
| `GroupMembershipEntity` / `GroupMembershipJpaRepository` | `shared/presentation/controller/FileController.java`      | Determinar si el usuario autenticado (rol `COORDINADOR_GRUPO`) tiene una membresía activa en el mismo grupo que el proyecto dueño del documento, para autorizar la descarga del archivo. |

`GroupMembershipEntity` mapea la misma tabla `membresias_grupo` que `MembershipEntity`, pero con una relación `@ManyToOne` hacia `ResearchGroupEntity` (en vez de un `Integer groupId` plano), lo cual permite a `FileController` navegar directamente hasta el grupo del proyecto.

### 8.4. Capa Presentation

Expone los endpoints REST del módulo.

Archivos principales:

```text
presentation/controller/ResearchGroupController.java
presentation/mapper/ResearchGroupMapper.java
```

Responsabilidades:

* Recibir solicitudes HTTP y validarlas (`@Valid`).
* Delegar la ejecución a los casos de uso correspondientes.
* Convertir entre DTOs y modelo de dominio mediante `ResearchGroupMapper`.
* Aplicar autorización a nivel de método con `@PreAuthorize` (`hasRole('ADMIN')` / `isAuthenticated()`), reforzada también a nivel de filtro HTTP en `SecurityConfig`.
* Delegar en `ListResearchLinesByGroupUseCase` (del módulo `lineas_investigacion`) para exponer `GET /{id}/lines`.

---

## 9. Reglas de negocio implementadas

| N.º | Regla de negocio                                                                                                      |
| --- | ------------------------------------------------------------------------------------------------------------------------ |
| 1   | El código de un grupo (`groupCode`) debe ser único → `DuplicateResourceException` (409).                                  |
| 2   | Todo grupo nuevo se crea activo (`active = true`).                                                                        |
| 3   | Solo puede asignarse como coordinador un usuario existente, activo y con rol `COORDINADOR_GRUPO` → `BusinessException` (400). |
| 4   | Solo puede agregarse como miembro un usuario existente, activo y con rol `DOCENTE_INVESTIGADOR` → `BusinessException` (400). |
| 5   | Un usuario no puede tener más de una membresía activa simultánea en ningún grupo (RF-21) → `BusinessException` (400) + índice único parcial `uq_membresias_activas` en base de datos. |
| 6   | El retiro de un miembro es un soft delete: se marca `active = false` y se registra `endDate`, conservando el historial (RF-20). |
| 7   | Consultar un grupo, listar sus miembros o listar sus líneas requiere que el grupo exista → `ResourceNotFoundException` (404). |
| 8   | Crear grupos, asignar coordinador, agregar y retirar miembros están restringidos al rol `ADMIN` (`@PreAuthorize` + `SecurityConfig`); las operaciones de lectura solo requieren estar autenticado. |

---

## 10. Endpoints implementados

Controlador: `ResearchGroupController` — base `/api/v1/research-groups`.

| Método   | Endpoint                          | Descripción                                              | Autorización            |
| -------- | ---------------------------------- | ----------------------------------------------------------- | -------------------------- |
| `POST`   | `/`                                 | Crea un grupo de investigación.                              | `hasRole('ADMIN')`         |
| `GET`    | `/`                                 | Lista todos los grupos de investigación.                     | Autenticado                |
| `GET`    | `/{id}`                             | Obtiene un grupo por su identificador.                        | Autenticado                |
| `PATCH`  | `/{id}/coordinator`                 | Asigna un coordinador al grupo.                                | `hasRole('ADMIN')`         |
| `POST`   | `/{id}/members`                     | Agrega un miembro (docente investigador) al grupo.             | `hasRole('ADMIN')`         |
| `DELETE` | `/{id}/members/{userId}`            | Retira un miembro del grupo (soft delete).                     | `hasRole('ADMIN')`         |
| `GET`    | `/{id}/members`                     | Lista los miembros activos del grupo.                          | Autenticado                |
| `GET`    | `/{id}/lines`                       | Lista las líneas de investigación activas asociadas al grupo.  | Autenticado                |

Todas las rutas de escritura (`POST`, `PATCH`, `DELETE`) están además restringidas a `ROLE_ADMIN` a nivel de filtro HTTP en `SecurityConfig` (`auth/infrastructure/security/SecurityConfig.java`), como segunda capa de defensa independiente del `@PreAuthorize` del controlador.

---

## 11. Tablas utilizadas

### `grupos_investigacion`

| Campo                     | Descripción                                             |
| --------------------------- | ---------------------------------------------------------- |
| `id_grupo`                  | Identificador único del grupo (PK).                         |
| `codigo_grupo`               | Código único del grupo (p. ej. `GINSOFT`).                  |
| `nombre_grupo`               | Nombre descriptivo del grupo.                                |
| `id_coordinador_actual`      | FK a `usuarios.id_usuario`, coordinador vigente (nullable).  |
| `es_activo`                  | Indica si el grupo está activo.                              |

### `membresias_grupo`

| Campo             | Descripción                                                       |
| ------------------- | --------------------------------------------------------------------- |
| `id_membresia`      | Identificador único de la membresía (PK).                             |
| `id_grupo`          | FK a `grupos_investigacion.id_grupo`.                                  |
| `id_usuario`        | FK a `usuarios.id_usuario`.                                            |
| `es_activo`         | Indica si la membresía está vigente.                                   |
| `fecha_inicio`      | Fecha en que inició la membresía.                                       |
| `fecha_fin`         | Fecha en que terminó la membresía (null mientras está activa).         |

Restricción relevante: `CREATE UNIQUE INDEX uq_membresias_activas ON membresias_grupo(id_usuario) WHERE es_activo = TRUE;` — implementa a nivel de base de datos la regla RF-21.

Ambas tablas, junto con `lineas_investigacion` y `lineas_por_grupo`, se crean y siembran en la migración Flyway `fiis/src/main/resources/db/migration/V7__create_remaining_tables.sql`, que incluye los 7 grupos institucionales (GINSOFT, RESEGTI, GISI, CICO, EAP, MAP, EU) exigidos por RF-15.

---

## 12. Pruebas unitarias existentes

| Módulo               | Con test                                                                                          | Sin test                          |
| ----------------------- | ------------------------------------------------------------------------------------------------------ | ------------------------------------- |
| Casos de uso            | `CreateGroupUseCaseTest`, `AssignCoordinatorUseCaseTest`, `AssignMemberUseCaseTest`, `RemoveMemberUseCaseTest`, `GetGroupUseCaseTest`, `ListGroupsUseCaseTest`, `ListMembersUseCaseTest` | —                                      |
| Persistencia            | `ResearchGroupRepositoryAdapterTest`, `MembershipRepositoryAdapterTest`                                  | —                                      |
| Presentación            | `ResearchGroupControllerTest`, `ResearchGroupMapperTest`                                                 | —                                      |

Todos los casos de uso, adaptadores de persistencia, el controlador y el mapper del módulo cuentan con pruebas unitarias en `fiis/src/test/java/com/sgi/fiis/grupos_investigacion/`.

---

## 13. Conclusión

El módulo **Grupos de Investigación** permite administrar los grupos institucionales de la FIIS, sus coordinadores y sus membresías, respetando los principios de Clean Architecture y las reglas de negocio definidas en RF-15 a RF-23. Su persistencia combina JPA con `JdbcTemplate` para resolver datos enriquecidos de usuario sin acoplar el dominio a detalles de la base de datos, y expone puntos de integración reutilizados por los módulos `proyectos` y `documentos`.

---

## 14. Decisiones técnicas tomadas

| Decisión                                                                 | Justificación                                                                                                  |
| --------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------- |
| Separar el módulo en capas de Clean Architecture                            | Mantiene separado el dominio, los casos de uso, la persistencia y los controladores REST.                       |
| Un `@Service` por caso de uso, sin puerto de entrada explícito               | Simplifica la estructura del módulo respecto a un esquema `UseCase` interfaz + `Service` implementación.        |
| Combinar `JpaRepository` con `JdbcTemplate`                                  | Permite enriquecer `ResearchGroup` y `Membership` con nombre/apellido/correo del usuario relacionado sin declarar `@ManyToOne` en las entidades JPA principales. |
| Índice único parcial `uq_membresias_activas`                                | Refuerza a nivel de base de datos la regla RF-21, además de la validación en `AssignMemberUseCase`.             |
| Reutilizar `ResearchGroupJpaRepository` y `GroupMembershipEntity` desde otros módulos | Evita duplicar el mapeo JPA de `grupos_investigacion`/`membresias_grupo` en los módulos `proyectos` y `documentos`. |
| Retiro de miembro como soft delete                                          | Conserva el historial de membresías, requerido por RF-20.                                                       |

---

## 15. Mejoras futuras

* Alinear los comentarios `RF-XX` del código (`ResearchGroupController`) con la numeración oficial de `docs/requisitos_sistema.md` (ver nota de la sección 5).
* Unificar `MembershipEntity`/`SpringDataMembershipRepository` con `GroupMembershipEntity`/`GroupMembershipJpaRepository` en una sola representación JPA de `membresias_grupo`, para evitar mantener dos mapeos de la misma tabla.
* Agregar un endpoint para desactivar un grupo completo (actualmente solo existe `es_activo` a nivel de columna, sin caso de uso que lo modifique).
* Agregar paginación en `GET /api/v1/research-groups` y en el listado de miembros, pensando en un número creciente de grupos y docentes.
* Agregar pruebas de integración end-to-end (contenedor Docker + Postgres) que documenten explícitamente el flujo crear grupo → asignar coordinador → agregar miembro → retirar miembro, similar a lo documentado para el módulo Evaluaciones.

---

## 16. Estado actual del módulo

El módulo Grupos de Investigación se encuentra en estado funcional. Actualmente permite:

* Crear, listar y consultar grupos de investigación.
* Asignar coordinadores validando rol institucional.
* Agregar y retirar miembros respetando la regla de membresía única activa (RF-21).
* Listar miembros activos y líneas de investigación asociadas a un grupo.
* Persistir la información en PostgreSQL mediante migraciones Flyway (`V7__create_remaining_tables.sql`), con los 7 grupos institucionales y sus líneas ya sembrados.
* Restringir las operaciones de escritura al rol `ADMIN`, tanto a nivel de filtro HTTP (`SecurityConfig`) como de método (`@PreAuthorize`).
