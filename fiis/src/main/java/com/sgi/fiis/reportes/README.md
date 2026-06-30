# Módulo de Reportes — SGI-FIIS

## 1. Arquitectura

El módulo implementa **Clean Architecture** con separación estricta en cuatro capas. Las dependencias fluyen únicamente hacia el dominio; la infraestructura implementa los contratos definidos en él.

```
reportes/
├── domain/
│   ├── model/                      # Entidades y DTOs del dominio
│   │   ├── FiltroReporte.java
│   │   ├── PaginatedResponse.java
│   │   ├── ReporteProyecto.java
│   │   ├── ReporteTramite.java
│   │   ├── ReporteResolucion.java
│   │   ├── ReporteInformeAvance.java
│   │   └── TrazabilidadMovimiento.java
│   └── repository/                 # Contratos (interfaces) — Dependency Inversion
│       ├── ReporteRepositoryPort.java
│       └── TrazabilidadRepositoryPort.java
├── application/
│   └── service/                    # Casos de uso
│       ├── ReporteService.java
│       └── TrazabilidadService.java
├── infrastructure/
│   └── persistence/                # Implementaciones JdbcTemplate
│       ├── ReporteRepositoryImpl.java
│       └── TrazabilidadRepositoryImpl.java
└── presentation/
    └── controller/                 # Controladores REST
        ├── ReporteController.java
        └── TrazabilidadController.java
```

**Flujo de dependencias:**
```
presentation → application → domain/repository ← infrastructure
```

---

## 2. API REST

**Base URL:** `/api/reportes`

Todos los parámetros de tipo `Date` deben enviarse en formato ISO `yyyy-MM-dd`.
Todos los endpoints de listado retornan `200 OK` con cuerpo JSON.

---

### `GET /api/reportes/proyectos`

Retorna un listado paginado de proyectos de investigación. Todos los parámetros son opcionales y combinables.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `idGrupo` | `Integer` | Identificador del grupo de investigación |
| `estado` | `String` | Estado del proyecto: `POSTULADO` · `OBSERVADO` · `APROBADO` · `RECHAZADO` · `EN_EJECUCION` · `FINALIZADO` |
| `fechaDesde` | `Date` | Fecha mínima de creación |
| `fechaHasta` | `Date` | Fecha máxima de creación |
| `idInvestigador` | `Integer` | Identificador del responsable del proyecto |
| `idConvocatoria` | `Integer` | Identificador de la convocatoria asociada |
| `page` | `int` | Número de página — default: `0` |
| `size` | `int` | Elementos por página — default: `20`, máximo: `100` |

**Respuesta:**
```json
{
  "data": [{
    "idProyecto": 1,
    "codigoProyecto": "PRY-2024-001",
    "tituloProyecto": "Sistema de Gestión Académica",
    "estadoProyecto": "EN_EJECUCION",
    "nombreGrupo": "GINSOFT",
    "nombreLinea": "Ingeniería de Software",
    "nombreResponsable": "Juan Pérez",
    "tituloConvocatoria": "Convocatoria 2024-I",
    "presupuesto": 15000.00,
    "fechaInicio": "2024-01-15",
    "fechaFin": "2024-12-15",
    "fechaCreacion": "2024-01-10T10:00:00"
  }],
  "total": 42,
  "page": 0,
  "size": 20
}
```

---

### `GET /api/reportes/tramites`

Retorna un listado paginado de trámites registrados en el sistema.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `idGrupo` | `Integer` | Identificador del grupo de investigación |
| `estado` | `String` | Estado actual del trámite |
| `fechaDesde` | `Date` | Fecha mínima de envío |
| `fechaHasta` | `Date` | Fecha máxima de envío |
| `idInvestigador` | `Integer` | Identificador del solicitante |
| `tipoTramite` | `String` | Tipo de trámite: `PROYECTO` · `PLAN_TESIS` · `INFORME_AVANCE` |
| `page` | `int` | Número de página — default: `0` |
| `size` | `int` | Elementos por página — default: `20`, máximo: `100` |

**Respuesta:**
```json
{
  "data": [{
    "idTramite": 5,
    "codigoTramite": "TRM-2024-005",
    "tipoTramite": "PROYECTO",
    "nombreSolicitante": "Juan Pérez",
    "estadoActual": "EN_REVISION",
    "rolRevisorActual": "DIRECTOR",
    "nombreGrupo": "GINSOFT",
    "fechaEnvio": "2024-03-01T09:00:00",
    "fechaActualizacion": "2024-03-05T14:30:00"
  }],
  "total": 18,
  "page": 0,
  "size": 20
}
```

---

### `GET /api/reportes/resoluciones`

Retorna un listado paginado de resoluciones emitidas.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `fechaDesde` | `Date` | Fecha mínima de emisión |
| `fechaHasta` | `Date` | Fecha máxima de emisión |
| `idInvestigador` | `Integer` | Identificador del solicitante del trámite vinculado |
| `tipoTramite` | `String` | Tipo del trámite vinculado a la resolución |
| `page` | `int` | Número de página — default: `0` |
| `size` | `int` | Elementos por página — default: `20`, máximo: `100` |

**Respuesta:**
```json
{
  "data": [{
    "idResolucion": 3,
    "numeroResolucion": "RES-001-2024",
    "fechaEmision": "2024-04-10",
    "asunto": "Aprobación de proyecto de investigación",
    "codigoTramite": "TRM-2024-005",
    "tipoTramite": "PROYECTO",
    "nombreSolicitante": "Juan Pérez",
    "fechaRegistro": "2024-04-10T11:00:00"
  }],
  "total": 7,
  "page": 0,
  "size": 20
}
```

---

### `GET /api/reportes/informes-avance`

Retorna un listado paginado de informes de avance de proyectos.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `idGrupo` | `Integer` | Identificador del grupo de investigación |
| `estado` | `String` | Estado del informe: `PENDIENTE` · `EN_REVISION` · `APROBADO` · `OBSERVADO` · `RECHAZADO` |
| `fechaDesde` | `Date` | Fecha mínima de registro |
| `fechaHasta` | `Date` | Fecha máxima de registro |
| `tipoTramite` | `String` | Tipo de informe: `PARCIAL` · `FINAL` |
| `page` | `int` | Número de página — default: `0` |
| `size` | `int` | Elementos por página — default: `20`, máximo: `100` |

**Respuesta:**
```json
{
  "data": [{
    "idInforme": 2,
    "codigoProyecto": "PRY-2024-001",
    "tituloProyecto": "Sistema de Gestión Académica",
    "tipoInforme": "PARCIAL",
    "periodo": "2024-I",
    "porcentajeAvance": 45.50,
    "estadoInforme": "APROBADO",
    "nombreGrupo": "GINSOFT",
    "fechaRegistro": "2024-06-01T08:00:00"
  }],
  "total": 12,
  "page": 0,
  "size": 20
}
```

---

### `GET /api/reportes/trazabilidad/{idTramite}`

Retorna el historial cronológico completo de movimientos de un trámite, ordenado de forma ascendente por fecha.

| Parámetro | Tipo | Descripción |
|---|---|---|
| `idTramite` | `Integer` | Identificador del trámite *(path variable)* |

**Respuesta:** Retorna `[]` si el trámite no existe o no registra movimientos.

```json
[
  {
    "idMovimiento": 1,
    "idTramite": 5,
    "codigoTramite": "TRM-2024-005",
    "nombreUsuarioAccion": "Ana García",
    "accion": "ENVIO",
    "estadoAnterior": null,
    "estadoNuevo": "EN_REVISION",
    "observacion": "Envío inicial del proyecto",
    "fechaMovimiento": "2024-03-01T09:00:00"
  },
  {
    "idMovimiento": 2,
    "idTramite": 5,
    "codigoTramite": "TRM-2024-005",
    "nombreUsuarioAccion": "Carlos Ramos",
    "accion": "APROBACION",
    "estadoAnterior": "EN_REVISION",
    "estadoNuevo": "APROBADO",
    "observacion": "Proyecto aprobado por el director",
    "fechaMovimiento": "2024-03-05T14:30:00"
  }
]
```
