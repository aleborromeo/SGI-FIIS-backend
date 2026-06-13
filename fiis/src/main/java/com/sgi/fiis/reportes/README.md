# Módulo: Reportes

## Estructura de Capas (Clean Architecture)

```
reportes/
├── presentation/          ← Controllers REST (entrada HTTP)
│   ├── ReporteController.java
│   └── TrazabilidadController.java
├── application/           ← Servicios (casos de uso)
│   ├── ReporteService.java
│   └── TrazabilidadService.java
├── domain/                ← Entidades y DTOs
│   ├── FiltroReporte.java
│   ├── PaginatedResponse.java
│   ├── ReporteProyecto.java
│   ├── ReporteTramite.java
│   ├── ReporteResolucion.java
│   ├── ReporteInformeAvance.java
│   └── TrazabilidadMovimiento.java
└── infrastructure/
    └── persistence/       ← Repositorios (acceso a BD con JdbcTemplate)
        ├── ReporteRepository.java
        └── TrazabilidadRepository.java
```

---

## API REST

Base URL: `/api/reportes`

---

### GET `/api/reportes/proyectos`

Reporte de proyectos de investigación con filtros combinables.

**Query Parameters (todos opcionales):**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `idGrupo` | Integer | ID del grupo de investigación |
| `estado` | String | `POSTULADO`, `OBSERVADO`, `APROBADO`, `RECHAZADO`, `EN_EJECUCION`, `FINALIZADO` |
| `fechaDesde` | Date `yyyy-MM-dd` | Fecha mínima de creación |
| `fechaHasta` | Date `yyyy-MM-dd` | Fecha máxima de creación |
| `idInvestigador` | Integer | ID del responsable del proyecto |
| `idConvocatoria` | Integer | ID de la convocatoria asociada |
| `page` | int | Página (default: `0`) |
| `size` | int | Elementos por página (default: `20`, máx: `100`) |

**Respuesta `200 OK`:**
```json
{
  "data": [
    {
      "idProyecto": 1,
      "codigoProyecto": "PRY-2024-001",
      "tituloProyecto": "...",
      "estadoProyecto": "EN_EJECUCION",
      "nombreGrupo": "...",
      "nombreLinea": "...",
      "nombreResponsable": "...",
      "tituloConvocatoria": "...",
      "presupuesto": 15000.00,
      "fechaInicio": "2024-01-15",
      "fechaFin": "2024-12-15",
      "fechaCreacion": "2024-01-10T10:00:00"
    }
  ],
  "total": 42,
  "page": 0,
  "size": 20
}
```

---

### GET `/api/reportes/tramites`

Reporte de trámites con filtros combinables.

**Query Parameters (todos opcionales):**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `idGrupo` | Integer | ID del grupo de investigación |
| `estado` | String | Estado actual del trámite |
| `fechaDesde` | Date `yyyy-MM-dd` | Fecha mínima de envío |
| `fechaHasta` | Date `yyyy-MM-dd` | Fecha máxima de envío |
| `idInvestigador` | Integer | ID del solicitante |
| `tipoTramite` | String | `PROYECTO`, `PLAN_TESIS`, `INFORME_AVANCE` |
| `page` | int | Página (default: `0`) |
| `size` | int | Elementos por página (default: `20`, máx: `100`) |

**Respuesta `200 OK`:**
```json
{
  "data": [
    {
      "idTramite": 5,
      "codigoTramite": "TRM-2024-005",
      "tipoTramite": "PROYECTO",
      "nombreSolicitante": "Juan Pérez",
      "estadoActual": "EN_REVISION",
      "rolRevisorActual": "DIRECTOR",
      "nombreGrupo": "...",
      "fechaEnvio": "2024-03-01T09:00:00",
      "fechaActualizacion": "2024-03-05T14:30:00"
    }
  ],
  "total": 18,
  "page": 0,
  "size": 20
}
```

---

### GET `/api/reportes/resoluciones`

Reporte de resoluciones emitidas con filtros combinables.

**Query Parameters (todos opcionales):**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `fechaDesde` | Date `yyyy-MM-dd` | Fecha mínima de emisión |
| `fechaHasta` | Date `yyyy-MM-dd` | Fecha máxima de emisión |
| `idInvestigador` | Integer | ID del solicitante del trámite vinculado |
| `tipoTramite` | String | Tipo del trámite vinculado |
| `page` | int | Página (default: `0`) |
| `size` | int | Elementos por página (default: `20`, máx: `100`) |

**Respuesta `200 OK`:**
```json
{
  "data": [
    {
      "idResolucion": 3,
      "numeroResolucion": "RES-001-2024",
      "fechaEmision": "2024-04-10",
      "asunto": "Aprobación de proyecto de investigación",
      "codigoTramite": "TRM-2024-005",
      "tipoTramite": "PROYECTO",
      "nombreSolicitante": "Juan Pérez",
      "fechaRegistro": "2024-04-10T11:00:00"
    }
  ],
  "total": 7,
  "page": 0,
  "size": 20
}
```

---

### GET `/api/reportes/informes-avance`

Reporte de informes de avance de proyectos con filtros combinables.

**Query Parameters (todos opcionales):**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `idGrupo` | Integer | ID del grupo de investigación |
| `estado` | String | `PENDIENTE`, `EN_REVISION`, `APROBADO`, `OBSERVADO`, `RECHAZADO` |
| `fechaDesde` | Date `yyyy-MM-dd` | Fecha mínima de registro |
| `fechaHasta` | Date `yyyy-MM-dd` | Fecha máxima de registro |
| `tipoTramite` | String | Tipo de informe: `PARCIAL` o `FINAL` |
| `page` | int | Página (default: `0`) |
| `size` | int | Elementos por página (default: `20`, máx: `100`) |

**Respuesta `200 OK`:**
```json
{
  "data": [
    {
      "idInforme": 2,
      "codigoProyecto": "PRY-2024-001",
      "tituloProyecto": "...",
      "tipoInforme": "PARCIAL",
      "periodo": "2024-I",
      "porcentajeAvance": 45.50,
      "estadoInforme": "APROBADO",
      "nombreGrupo": "...",
      "fechaRegistro": "2024-06-01T08:00:00"
    }
  ],
  "total": 12,
  "page": 0,
  "size": 20
}
```

---

### GET `/api/reportes/trazabilidad/{idTramite}`

Historial cronológico completo de movimientos de un trámite.

**Path Parameter:**

| Parámetro | Tipo | Descripción |
|---|---|---|
| `idTramite` | Integer | ID del trámite a consultar |

**Respuesta `200 OK`:**
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

> Devuelve `[]` si el trámite no existe o no tiene movimientos registrados.
