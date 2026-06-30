# Sistema de Gestión de Investigación FIIS

## Documentación de Arquitectura — Clean Architecture

> **Propósito de este documento:** Explicar al equipo la arquitectura elegida, su estructura de carpetas, y cómo se verá en Visual Studio Code a través de un módulo completo de ejemplo.

---

## 1. ¿Por qué Clean Architecture?

Clean Architecture (Robert C. Martin) organiza el código en capas concéntricas donde **las dependencias siempre apuntan hacia adentro**. El núcleo del sistema (las reglas de negocio) no depende de ningún framework, base de datos ni detalle externo.

### Principio fundamental

```
Presentation  →  Application  →  Domain  ←  Infrastructure
   (REST)        (Use Cases)    (Entities)    (JPA / DB)
```

- **Domain** no importa nada externo. Es el corazón.
- **Application** orquesta los casos de uso. Solo conoce Domain.
- **Infrastructure** implementa los puertos (repositorios, persistencia). Conoce Domain.
- **Presentation** expone la API REST. Conoce Application.

### Beneficios para nuestro proyecto

| Beneficio                                 | Por qué importa en FIIS                                                       |
| ----------------------------------------- | ----------------------------------------------------------------------------- |
| **Testabilidad**                          | Cada caso de uso se prueba sin levantar Spring ni la BD                       |
| **Independencia del framework**           | Podemos cambiar de Spring Boot sin tocar la lógica de negocio                 |
| **Separación clara de responsabilidades** | Cada integrante trabaja en su módulo sin pisar al otro                        |
| **Escalabilidad**                         | Agregar nuevos módulos (evaluaciones, reportes) no afecta los existentes      |
| **Mantenibilidad**                        | El código sigue las reglas RNF-26, RNF-27, RNF-29 del documento de requisitos |

---

## 2. Stack tecnológico

| Componente        | Tecnología                  |
| ----------------- | --------------------------- |
| Lenguaje          | Java 17                     |
| Framework         | Spring Boot 3.x             |
| Base de datos     | PostgreSQL                  |
| ORM               | Spring Data JPA / Hibernate |
| Autenticación     | Spring Security + JWT       |
| Documentación API | Springdoc OpenAPI (Swagger) |
| Build tool        | Maven                       |

---

## 3. Estructura general del proyecto

Esta es la vista completa del proyecto en VS Code, con **todos los módulos** del sistema:

```
sgifiis/                                         ← Raíz del proyecto
│
├── pom.xml
├── README.md
│
└── src/
    └── main/
        ├── java/
        │   └── pe/unas/fiis/sgifiis/
        │       │
        │       ├── shared/                      ← Shared Kernel (compartido)
        │       │   ├── domain/
        │       │   │   └── valueobjects/        ← VOs reutilizables
        │       │   └── infrastructure/
        │       │       └── persistence/
        │       │
        │       ├── auth/                        ← [junior]   Autenticación y roles
        │       ├── users/                       ← [junior]   Gestión de usuarios
        │       ├── researchgroups/              ← [kevin]    Grupos de investigación
        │       ├── researchlines/               ← [kevin]    Líneas de investigación
        │       ├── convocatorias/               ← [amy]      Convocatorias
        │       ├── projects/                    ← [amy]      Proyectos de investigación
        │       ├── thesis/                      ← (por asignar) Planes de tesis
        │       ├── tramites/                    ← [marco]    Flujo de trámites
        │       ├── documents/                   ← [Ale]      Gestión documental
        │       ├── progressreports/             ← [jorge]    Informes de avance
        │       ├── resolutions/                 ← [sebas]    Resoluciones
        │       ├── evaluations/                 ← [dairon]   Evaluaciones
        │       ├── observations/                ← [nilver]   Observaciones y subsanaciones
        │       ├── dashboards/                  ← [sergio]   Dashboard por rol
        │       ├── reports/                     ← [mese]     Reportes y auditoría
        │       └── SgifiisApplication.java
        │
        └── resources/
            ├── application.yml
            └── db/migration/                    ← Scripts SQL (Flyway)
```

---

## 4. Módulo de ejemplo completo: `tramites`

> **Responsable:** marco  
> **Requisitos cubiertos:** RF-54 al RF-64, RN-04, RN-07

Este módulo gestiona el **flujo de revisión**: Docente/Estudiante → Coordinador → Director → Decano.

---

### 4.1 Estructura de carpetas en VS Code

Así se verá el módulo `tramites/` al desplegarlo en el explorador de VS Code:

```
📁 tramites/
│
├── 📁 domain/
│   │
│   ├── 📁 model/
│   │   ├── 📄 Tramite.java               ← Entidad de dominio (pura, sin anotaciones JPA)
│   │   ├── 📄 TramiteMovimiento.java     ← Value Object / entidad de auditoría
│   │   ├── 📄 TramiteEstado.java         ← Enum: POSTULADO, OBSERVADO, APROBADO, RECHAZADO...
│   │   └── 📄 TramiteTipo.java           ← Enum: PROYECTO, PLAN_TESIS, INFORME, RESOLUCION
│   │
│   ├── 📁 port/
│   │   ├── 📄 TramiteRepository.java     ← Interfaz (puerto de salida)
│   │   └── 📄 TramiteEventPublisher.java ← Interfaz para publicar eventos de dominio
│   │
│   └── 📁 event/
│       ├── 📄 TramiteDerivadoEvent.java  ← Evento: trámite pasó al siguiente revisor
│       ├── 📄 TramiteObservadoEvent.java ← Evento: trámite fue observado
│       └── 📄 TramiteAprobadoEvent.java  ← Evento: trámite aprobado
│
├── 📁 application/
│   │
│   ├── 📁 usecase/
│   │   ├── 📄 DerivaTramiteUseCase.java       ← Derivar al siguiente rol revisor
│   │   ├── 📄 AprobaTramiteUseCase.java        ← Aprobar un trámite
│   │   ├── 📄 ObservaTramiteUseCase.java       ← Registrar observación
│   │   ├── 📄 RechazaTramiteUseCase.java       ← Rechazar trámite
│   │   ├── 📄 SubsanaTramiteUseCase.java       ← Levantar observación
│   │   ├── 📄 ConsultaTramiteUseCase.java      ← Consultar trámite y trazabilidad
│   │   └── 📄 ListaTramitesPorRolUseCase.java  ← Listar trámites según rol del usuario
│   │
│   └── 📁 dto/
│       ├── 📄 TramiteRequestDTO.java      ← Datos de entrada (crear/actualizar trámite)
│       ├── 📄 TramiteResponseDTO.java     ← Datos de salida
│       ├── 📄 ObservacionRequestDTO.java  ← Datos para registrar observación
│       └── 📄 TramiteMovimientoDTO.java   ← Para mostrar la trazabilidad
│
├── 📁 infrastructure/
│   │
│   ├── 📁 persistence/
│   │   ├── 📄 TramiteJpaEntity.java            ← Entidad JPA (con @Entity, @Table, etc.)
│   │   ├── 📄 TramiteMovimientoJpaEntity.java  ← Entidad JPA para movimientos
│   │   ├── 📄 TramiteJpaRepository.java        ← Extiende JpaRepository<>
│   │   ├── 📄 TramiteRepositoryImpl.java       ← Implementa TramiteRepository (del domain)
│   │   └── 📄 TramiteMapper.java               ← Convierte entre JpaEntity ↔ Domain Model
│   │
│   └── 📁 event/
│       └── 📄 TramiteEventPublisherImpl.java   ← Implementa TramiteEventPublisher
│
└── 📁 presentation/
    │
    ├── 📁 controller/
    │   └── 📄 TramiteController.java       ← @RestController con los endpoints REST
    │
    └── 📁 mapper/
        └── 📄 TramiteWebMapper.java        ← Convierte DTO ↔ Request/Response HTTP
```

---

### 4.2 Código de cada capa

#### `domain/model/Tramite.java` — Entidad de dominio

```java
// Sin anotaciones JPA, sin Spring. Pura lógica de negocio.
package pe.unas.fiis.sgifiis.tramites.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Tramite {

    private final UUID id;
    private String codigo;
    private TramiteTipo tipo;
    private TramiteEstado estado;
    private UUID solicitanteId;
    private String rolRevisorActual;   // "COORDINADOR", "DIRECTOR", "DECANO"
    private String observacionActual;
    private LocalDateTime fechaEnvio;
    private LocalDateTime fechaActualizacion;

    // Constructor, getters, lógica de negocio...

    public void derivarA(String nuevoRolRevisor) {
        // RN-07: Si el Director observa un plan, retorna al Coordinador
        this.rolRevisorActual = nuevoRolRevisor;
        this.estado = TramiteEstado.EN_REVISION;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void observar(String observacion) {
        this.observacionActual = observacion;
        this.estado = TramiteEstado.OBSERVADO;
        this.fechaActualizacion = LocalDateTime.now();
    }

    public void aprobar() {
        this.estado = TramiteEstado.APROBADO;
        this.observacionActual = null;
        this.fechaActualizacion = LocalDateTime.now();
    }
}
```

---

#### `domain/port/TramiteRepository.java` — Puerto de salida

```java
// Interfaz definida en Domain. Infrastructure la implementa.
package pe.unas.fiis.sgifiis.tramites.domain.port;

import pe.unas.fiis.sgifiis.tramites.domain.model.Tramite;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TramiteRepository {
    Tramite save(Tramite tramite);
    Optional<Tramite> findById(UUID id);
    List<Tramite> findByRolRevisorActual(String rol);
    List<Tramite> findBySolicitanteId(UUID solicitanteId);
    // RF-57: coordinador solo ve los de su grupo
    List<Tramite> findByCoordinadorGrupoId(UUID grupoId);
}
```

---

#### `application/usecase/ObservaTramiteUseCase.java` — Caso de uso

```java
//  Orquesta la lógica. Solo conoce Domain. Sin Spring MVC.
package pe.unas.fiis.sgifiis.tramites.application.usecase;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.unas.fiis.sgifiis.tramites.application.dto.ObservacionRequestDTO;
import pe.unas.fiis.sgifiis.tramites.domain.event.TramiteObservadoEvent;
import pe.unas.fiis.sgifiis.tramites.domain.model.Tramite;
import pe.unas.fiis.sgifiis.tramites.domain.port.TramiteEventPublisher;
import pe.unas.fiis.sgifiis.tramites.domain.port.TramiteRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ObservaTramiteUseCase {

    private final TramiteRepository tramiteRepository;
    private final TramiteEventPublisher eventPublisher;

    @Transactional
    public Tramite execute(UUID tramiteId, ObservacionRequestDTO dto) {
        Tramite tramite = tramiteRepository.findById(tramiteId)
            .orElseThrow(() -> new IllegalArgumentException("Trámite no encontrado"));

        tramite.observar(dto.getObservacion());
        Tramite guardado = tramiteRepository.save(tramite);

        // RF-61: registrar la última observación
        // RF-63: actualizar estado y rol revisor
        eventPublisher.publish(new TramiteObservadoEvent(guardado.getId(), dto.getObservacion()));

        return guardado;
    }
}
```

---

#### `infrastructure/persistence/TramiteJpaEntity.java` — Entidad JPA

```java
//  Las anotaciones de BD viven aquí, lejos del dominio.
package pe.unas.fiis.sgifiis.tramites.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tramites")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TramiteJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String tipo;          // PROYECTO, PLAN_TESIS, INFORME

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String estado;        // POSTULADO, EN_REVISION, OBSERVADO, APROBADO, RECHAZADO

    @Column(name = "solicitante_id", nullable = false)
    private UUID solicitanteId;

    @Column(name = "rol_revisor_actual")
    private String rolRevisorActual;

    @Column(name = "observacion_actual", columnDefinition = "TEXT")
    private String observacionActual;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;
}
```

---

#### `infrastructure/persistence/TramiteRepositoryImpl.java` — Implementación del puerto

```java
//  Conecta el Domain con JPA. La única clase que conoce ambos mundos.
package pe.unas.fiis.sgifiis.tramites.infrastructure.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pe.unas.fiis.sgifiis.tramites.domain.model.Tramite;
import pe.unas.fiis.sgifiis.tramites.domain.port.TramiteRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TramiteRepositoryImpl implements TramiteRepository {

    private final TramiteJpaRepository jpaRepository;
    private final TramiteMapper mapper;

    @Override
    public Tramite save(Tramite tramite) {
        TramiteJpaEntity entity = mapper.toJpa(tramite);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Tramite> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Tramite> findByRolRevisorActual(String rol) {
        return jpaRepository.findByRolRevisorActual(rol)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Tramite> findBySolicitanteId(UUID solicitanteId) {
        return jpaRepository.findBySolicitanteId(solicitanteId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public List<Tramite> findByCoordinadorGrupoId(UUID grupoId) {
        return jpaRepository.findByCoordinadorGrupoId(grupoId)
            .stream().map(mapper::toDomain).collect(Collectors.toList());
    }
}
```

---

#### `presentation/controller/TramiteController.java` — Endpoints REST

```java
//  Solo maneja HTTP. Delega todo a los casos de uso.
package pe.unas.fiis.sgifiis.tramites.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.unas.fiis.sgifiis.tramites.application.dto.*;
import pe.unas.fiis.sgifiis.tramites.application.usecase.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tramites")
@RequiredArgsConstructor
public class TramiteController {

    private final ObservaTramiteUseCase observaTramiteUseCase;
    private final AprobaTramiteUseCase aprobaTramiteUseCase;
    private final DerivaTramiteUseCase derivaTramiteUseCase;
    private final ConsultaTramiteUseCase consultaTramiteUseCase;
    private final ListaTramitesPorRolUseCase listaTramitesPorRolUseCase;

    // RF-57: Coordinador lista trámites pendientes de su grupo
    @GetMapping("/pendientes")
    @PreAuthorize("hasAnyRole('COORDINADOR_GRUPO','DIRECTOR_INVESTIGACION','DECANO')")
    public ResponseEntity<?> listarPendientes() {
        return ResponseEntity.ok(listaTramitesPorRolUseCase.execute());
    }

    // RF-60: Aprobar trámite
    @PatchMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyRole('COORDINADOR_GRUPO','DIRECTOR_INVESTIGACION','DECANO')")
    public ResponseEntity<?> aprobar(@PathVariable UUID id) {
        return ResponseEntity.ok(aprobaTramiteUseCase.execute(id));
    }

    // RF-60: Observar trámite — RF-61: registra última observación
    @PatchMapping("/{id}/observar")
    @PreAuthorize("hasAnyRole('COORDINADOR_GRUPO','DIRECTOR_INVESTIGACION')")
    public ResponseEntity<?> observar(
            @PathVariable UUID id,
            @RequestBody ObservacionRequestDTO dto) {
        return ResponseEntity.ok(observaTramiteUseCase.execute(id, dto));
    }

    // RF-56: Derivar trámite al siguiente revisor
    @PatchMapping("/{id}/derivar")
    @PreAuthorize("hasAnyRole('COORDINADOR_GRUPO','DIRECTOR_INVESTIGACION')")
    public ResponseEntity<?> derivar(
            @PathVariable UUID id,
            @RequestBody DerivacionRequestDTO dto) {
        return ResponseEntity.ok(derivaTramiteUseCase.execute(id, dto));
    }

    // RF-64: Trazabilidad completa del trámite
    @GetMapping("/{id}/trazabilidad")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> trazabilidad(@PathVariable UUID id) {
        return ResponseEntity.ok(consultaTramiteUseCase.execute(id));
    }
}
```

---

## 5. Flujo de dependencias en el módulo (diagrama)

```
┌─────────────────────────────────────────────────────┐
│                   PRESENTATION                       │
│   TramiteController                                  │
│   (HTTP In/Out, @RestController, Seguridad)          │
└────────────────────┬────────────────────────────────┘
                     │ usa DTOs
┌────────────────────▼────────────────────────────────┐
│                   APPLICATION                        │
│   ObservaTramiteUseCase                              │
│   AprobaTramiteUseCase                               │
│   DerivaTramiteUseCase        ← Casos de uso         │
│   (Lógica de orquestación, transacciones)            │
└──────────┬───────────────────────────────────────────┘
           │ usa interfaces del Domain
┌──────────▼───────────────────────────────────────────┐
│                     DOMAIN                            │
│   Tramite.java  (entidad pura)                        │
│   TramiteRepository  (puerto, interfaz)               │
│   TramiteEstado, TramiteTipo  (enums)                 │
│   TramiteObservadoEvent  (evento de dominio)          │
│   ← NO depende de nada externo →                      │
└──────────────────────────────────────────────────────┘
           ▲ implementa las interfaces
┌──────────┴───────────────────────────────────────────┐
│                 INFRASTRUCTURE                        │
│   TramiteJpaEntity  (@Entity JPA)                     │
│   TramiteRepositoryImpl  (Spring Data JPA)            │
│   TramiteMapper  (Domain ↔ JPA)                       │
│   TramiteEventPublisherImpl  (Spring Events)          │
└──────────────────────────────────────────────────────┘
```

**Regla de oro:** Las flechas de dependencia NUNCA apuntan hacia afuera del Domain. El Domain no sabe que Spring Boot existe.

---

## 6. Distribución de módulos por integrante

| Integrante | Módulo                          | Paquete Java                        |
| ---------- | ------------------------------- | ----------------------------------- |
| junior     | Autenticación, Usuarios y Roles | `auth/`, `users/`                   |
| kevin      | Grupos e Investigación, Líneas  | `researchgroups/`, `researchlines/` |
| amy        | Convocatorias y Proyectos       | `convocatorias/`, `projects/`       |
| marco      | Flujo de Trámites               | `tramites/`                         |
| Ale        | Gestión Documental              | `documents/`                        |
| jorge      | Informes de Avance              | `progressreports/`                  |
| nilver     | Observaciones y Subsanaciones   | `observations/`                     |
| mese       | Reportes y Auditoría            | `reports/`                          |
| sebas      | Resoluciones                    | `resolutions/`                      |
| dairon     | Evaluaciones                    | `evaluations/`                      |
| sergio     | Dashboard por Rol               | `dashboards/`                       |

---

## 7. Convenciones que todo el equipo debe seguir

### Nomenclatura de clases

| Capa           | Sufijo                    | Ejemplo                 |
| -------------- | ------------------------- | ----------------------- |
| Domain entity  | _(sin sufijo)_            | `Tramite`               |
| Domain port    | `Repository`, `Publisher` | `TramiteRepository`     |
| Domain event   | `Event`                   | `TramiteObservadoEvent` |
| Use case       | `UseCase`                 | `ObservaTramiteUseCase` |
| DTO entrada    | `RequestDTO`              | `ObservacionRequestDTO` |
| DTO salida     | `ResponseDTO`             | `TramiteResponseDTO`    |
| JPA entity     | `JpaEntity`               | `TramiteJpaEntity`      |
| JPA repo       | `JpaRepository`           | `TramiteJpaRepository`  |
| Implementación | `Impl`                    | `TramiteRepositoryImpl` |
| Controller     | `Controller`              | `TramiteController`     |
| Mapper         | `Mapper`                  | `TramiteMapper`         |

### Reglas obligatorias

1. **Nunca importar clases JPA en el Domain.** Si ves `import jakarta.persistence` en `/domain/`, hay un error.
2. **Nunca importar el Controller desde un UseCase.** El flujo es unidireccional.
3. **Un UseCase = una responsabilidad.** No crear `TramiteService` con 20 métodos.
4. **Los endpoints llevan `@PreAuthorize`** con el rol correspondiente según RF-04.
5. **Cada módulo tiene su propio `Mapper`** para aislar la conversión Domain ↔ JPA.

---

## 8. Estructura del script SQL base (Flyway)

Los scripts de base de datos van en `src/main/resources/db/migration/` con el formato:

```
V1__create_users.sql
V2__create_research_groups.sql
V3__create_tramites.sql
V4__create_tramite_movimientos.sql
...
```

Ejemplo para la tabla `tramites`:

```sql
-- V3__create_tramites.sql
CREATE TABLE tramites (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    codigo              VARCHAR(20) NOT NULL UNIQUE,
    tipo                VARCHAR(30) NOT NULL,  -- PROYECTO, PLAN_TESIS, INFORME, RESOLUCION
    estado              VARCHAR(30) NOT NULL,  -- POSTULADO, EN_REVISION, OBSERVADO, APROBADO, RECHAZADO
    solicitante_id      UUID NOT NULL REFERENCES users(id),
    rol_revisor_actual  VARCHAR(40),
    observacion_actual  TEXT,
    fecha_envio         TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_actualizacion TIMESTAMP
);

CREATE TABLE tramite_movimientos (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tramite_id      UUID NOT NULL REFERENCES tramites(id),
    usuario_id      UUID NOT NULL REFERENCES users(id),
    accion          VARCHAR(30) NOT NULL,   -- RF-100: APROBADO, OBSERVADO, RECHAZADO...
    estado_anterior VARCHAR(30),
    estado_nuevo    VARCHAR(30),
    observacion     TEXT,
    fecha           TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

_Documento preparado para el equipo de desarrollo — Sistema de Gestión de Investigación FIIS_  
_Facultad de Ingeniería en Informática y Sistemas — UNAS_
