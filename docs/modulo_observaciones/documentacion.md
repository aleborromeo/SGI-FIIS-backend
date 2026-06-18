# Módulo de Observaciones y Subsanaciones

El módulo de **Observaciones y Subsanaciones** es un componente crítico del Sistema de Gestión de Investigación FIIS. Se encarga de gestionar el ciclo de vida de las observaciones registradas por los revisores (Coordinador, Director, Decano) sobre los trámites de investigación y las subsanaciones presentadas por los solicitantes.

Este módulo cumple con los requisitos funcionales **RF-60 a RF-64, RF-69, RF-76** y reglas de negocio **RN-07, RN-08**.

---

## 1. Arquitectura del Módulo

El módulo sigue los principios de la **Arquitectura Hexagonal (Puertos y Adaptadores)** para garantizar el desacoplamiento de la lógica de negocio y la infraestructura:

```
[ Capa de Presentación (Controller) ]
                │
                ▼
  [ Capa de Aplicación (DTOs, Use Cases) ]
        │                       │
        ▼                       ▼
  [ Puertos (Interfaces) ] ◄── [ Núcleo del Dominio (Modelos, Enums, Reglas) ]
        ▲
        │
[ Capa de Infraestructura (Adapters, Mappers, JPA, DB) ]
```

### Estructura de Paquetes (`com.sgi.fiis.observaciones`)

* **`domain`**:
  * **`model`**: Contiene los modelos de dominio enriquecidos (`Observacion.java`, `Subsanacion.java`) y enums (`TipoObservacion.java`, `ObservacionEstado.java`).
  * **`port`**: Define los puertos de salida para persistencia (`ObservacionRepository.java`, `SubsanacionRepository.java`).
  * **`exception`**: Excepciones de negocio (`ObservacionNotFoundException`, `SubsanacionInvalidaException`).
* **`application`**:
  * **`dto`**: DTOs de petición y respuesta (e.g., `ObservacionRequestDTO`, `SubsanacionResponseDTO`).
  * **`usecase`**: Clases de servicio enfocadas en casos de uso específicos (`RegistraObservacionUseCase`, `RegistraSubsanacionUseCase`, `ListaObservacionesPorTramiteUseCase`, etc.).
* **`infrastructure`**:
  * **`persistence`**: Entidades JPA, repositorios Spring Data y adaptadores concretos que implementan los puertos de dominio.
  * **`mapper`**: Mapeadores para transformar entre entidades de base de datos (JPA) y entidades de negocio (Dominio).
* **`presentation`**:
  * **`controller`**: Controlador REST expuesto (`ObservacionController.java`) y manejadores de excepciones a nivel HTTP.

---

## 2. Modelo de Datos (Esquema de Base de Datos)

El esquema de base de datos PostgreSQL está diseñado con integridad referencial restrictiva y optimización de consultas a través de índices estratégicos.

### Tabla: `observaciones`
Almacena el detalle de cada observación registrada por un revisor sobre un trámite.

| Columna | Tipo de Datos | Modificadores | Descripción |
| :--- | :--- | :--- | :--- |
| `id_observacion` | `SERIAL` | `PRIMARY KEY` | Identificador único de la observación. |
| `id_tramite` | `INT` | `NOT NULL` | Referencia al trámite observado (`FK` -> `tramites`). |
| `id_revisor` | `INT` | `NOT NULL` | Referencia al revisor que la creó (`FK` -> `usuarios`). |
| `tipo_observacion` | `VARCHAR(30)` | `NOT NULL` | Categoría de la observación (`chk_tipo_observacion`). |
| `descripcion` | `TEXT` | `NOT NULL` | Detalle textual de la observación. |
| `estado_observacion` | `VARCHAR(20)` | `DEFAULT 'PENDIENTE' NOT NULL` | Estado del ciclo de vida (`chk_estado_observacion`). |
| `rol_revisor` | `VARCHAR(50)` | `NOT NULL` | Rol del usuario en el momento de revisar. |
| `fecha_registro` | `TIMESTAMP` | `DEFAULT NOW() NOT NULL` | Fecha de creación. |
| `fecha_actualizacion` | `TIMESTAMP` | `DEFAULT NOW() NOT NULL` | Fecha de última modificación (Auditoría RNF-40). |

* **Constraints de validación (CHECKs)**:
  * `chk_tipo_observacion`: `tipo_observacion IN ('TECNICA', 'DOCUMENTAL', 'PRESUPUESTAL', 'FORMATO')`
  * `chk_estado_observacion`: `estado_observacion IN ('PENDIENTE', 'SUBSANADA', 'VIGENTE')`

---

### Tabla: `subsanaciones`
Almacena las subsanaciones y levantamiento de observaciones enviadas por los solicitantes.

| Columna | Tipo de Datos | Modificadores | Descripción |
| :--- | :--- | :--- | :--- |
| `id_subsanacion` | `SERIAL` | `PRIMARY KEY` | Identificador único de la subsanación. |
| `id_observacion` | `INT` | `NOT NULL` | Referencia a la observación levantada (`FK` -> `observaciones`). |
| `id_solicitante` | `INT` | `NOT NULL` | Referencia al usuario solicitante (`FK` -> `usuarios`). |
| `descripcion` | `TEXT` | `NOT NULL` | Explicación detallada de cómo se levanta la observación. |
| `id_documento_adjunto` | `INT` | `NULL` | Referencia a archivo adjunto de prueba (`FK` -> `documentos`). |
| `fecha_registro` | `TIMESTAMP` | `DEFAULT NOW() NOT NULL` | Fecha de creación. |
| `fecha_actualizacion` | `TIMESTAMP` | `DEFAULT NOW() NOT NULL` | Fecha de modificación (Auditoría RNF-40). |

---

### Índices Estratégicos (Rendimiento RNF-17)
* `ix_observaciones_tramite`: Optimiza la búsqueda de historial de observaciones por trámite.
* `ix_observaciones_estado`: Agiliza la carga de bandejas de pendientes de subsanar.
* `ix_subsanaciones_observacion`: Relaciona rápidamente una observación con sus correspondientes subsanaciones.
* `ix_observaciones_tramite_estado` *(Compuesto)*: Indexa conjuntamente `(id_tramite, estado_observacion)` para búsquedas frecuentes de estados específicos de trámites.

---

## 3. Reglas de Negocio Clave

1. **Ciclo de Subsanación (Levantamiento)**:
   * Una observación nueva inicia en estado `PENDIENTE`.
   * El solicitante puede registrar una subsanación únicamente sobre observaciones que estén en estado `PENDIENTE` (validado mediante `esSubsanable()`).
   * Al registrar con éxito una subsanación, la observación pasa automáticamente a estado `SUBSANADA` y se actualiza la `fecha_actualizacion` (Auditoría RNF-40).

2. **Flujo de Retorno (Workflow)**:
   * Si un trámite recibe observaciones, el sistema determina dinámicamente a qué rol previo debe retornar para que sea modificado, de acuerdo con la jerarquía:
     * `DECANO` ➔ retorna a `DIRECTOR_INVESTIGACION`
     * `DIRECTOR_INVESTIGACION` ➔ retorna a `COORDINADOR_GRUPO`
     * `COORDINADOR_GRUPO` ➔ retorna al `SOLICITANTE` (retorna `null` en el cálculo de rol jerárquico).

---

## 4. Endpoints de la API REST

Todos los endpoints tienen el prefijo `/api/observaciones`:

| Método | Endpoint | Descripción | Cuerpo de Petición (Request JSON) | Respuesta Exitosa (200 / 201) |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/observaciones` | Registra una nueva observación sobre un trámite. | `ObservacionRequestDTO` | `ObservacionResponseDTO` |
| **POST** | `/api/observaciones/{id}/subsanar` | Presenta una subsanación para levantar la observación `{id}`. | `SubsanacionRequestDTO` | `SubsanacionResponseDTO` |
| **GET** | `/api/observaciones/{id}` | Obtiene el detalle de la observación `{id}`. | *(Ninguno)* | `ObservacionResponseDTO` |
| **GET** | `/api/observaciones/tramite/{tramiteId}` | Lista todas las observaciones asociadas al trámite `{tramiteId}`. | *(Ninguno)* | `List<ObservacionResponseDTO>` |
| **GET** | `/api/observaciones/{id}/subsanaciones` | Lista todas las subsanaciones cargadas para la observación `{id}`. | *(Ninguno)* | `List<SubsanacionResponseDTO>` |
