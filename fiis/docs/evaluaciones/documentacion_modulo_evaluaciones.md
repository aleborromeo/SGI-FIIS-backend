# Documentación del Módulo Evaluaciones

## 1. Información general del módulo

| Campo                     | Descripción                                   |
| ------------------------- | --------------------------------------------- |
| **Proyecto**              | Sistema de Gestión de Investigación FIIS      |
| **Módulo**                | Evaluaciones                                  |
| **Rama de trabajo**       | `feature/evaluaciones`                        |
| **Arquitectura aplicada** | Clean Architecture                            |
| **Base de datos**         | PostgreSQL                                    |
| **Framework**             | Spring Boot                                   |
| **Contenedores usados**   | `sgifiis-backend-api` y `sgifiis-postgres-db` |

---

## 2. Descripción general

El módulo **Evaluaciones** forma parte del Sistema de Gestión de Investigación FIIS. Su finalidad es gestionar el proceso mediante el cual un proyecto de investigación, plan de tesis o documento académico es asignado a un evaluador para su revisión.

Este módulo permite registrar asignaciones de evaluación, consultar evaluaciones asignadas, registrar resultados, puntajes y observaciones, manteniendo una estructura organizada bajo el enfoque de **Clean Architecture**.

---

## 3. Objetivo del módulo

Automatizar la asignación y registro de evaluaciones dentro del SGI-FIIS, permitiendo controlar:

* Qué usuario realiza la evaluación.
* Qué proyecto o plan de tesis es evaluado.
* Cuál fue el resultado de la evaluación.
* Qué puntaje fue asignado.
* Qué observaciones fueron registradas.
* En qué fecha se asignó la evaluación.
* En qué fecha se emitió el resultado.

---

## 4. Alcance funcional

El módulo implementa las siguientes funcionalidades:

* Asignar evaluadores a proyectos o planes de tesis.
* Registrar resultados de evaluación.
* Registrar puntajes de evaluación.
* Registrar observaciones asociadas a la evaluación.
* Consultar todas las evaluaciones registradas.
* Consultar una evaluación por su identificador.
* Consultar evaluaciones asignadas a un evaluador específico.

---

## 5. Requisitos funcionales relacionados

El módulo se encuentra relacionado con los requisitos funcionales **RF-83 al RF-87**.

| Código | Requisito funcional                                                           | Implementación en el módulo                                              |
| ------ | ----------------------------------------------------------------------------- | ------------------------------------------------------------------------ |
| RF-83  | El sistema debe permitir asignar evaluadores a proyectos o documentos.        | Se implementó el endpoint `POST /evaluaciones/asignar`.                  |
| RF-84  | El evaluador debe poder visualizar proyectos asignados.                       | Se implementó el endpoint `GET /evaluaciones/evaluador/{idEvaluador}`.   |
| RF-85  | El evaluador debe poder registrar resultados de evaluación.                   | Se implementó el endpoint `POST /evaluaciones/{idEvaluacion}/resultado`. |
| RF-86  | El sistema debe registrar observaciones, puntajes o resultados de evaluación. | Se registran los campos `resultado`, `puntaje` y `observaciones`.        |
| RF-87  | El Director debe poder consultar las evaluaciones realizadas.                 | Se implementaron endpoints de consulta general y por identificador.      |

---

## 6. Arquitectura aplicada

El módulo fue implementado siguiendo el enfoque de **Clean Architecture**, separando responsabilidades en cuatro capas principales:

```text
evaluaciones
├── application
├── domain
├── infrastructure
└── presentation
```

Esta estructura evita el acoplamiento directo entre los controladores y la base de datos. Además, permite mantener separada la lógica de negocio, los casos de uso, la persistencia y la exposición de endpoints REST.

---

## 7. Estructura del módulo

```text
evaluaciones
├── application
│   ├── dto
│   │   ├── command
│   │   │   ├── AsignarEvaluadorCommand.java
│   │   │   └── RegistrarResultadoEvaluacionCommand.java
│   │   └── response
│   │       └── EvaluacionResponse.java
│   ├── ports
│   │   └── in
│   │       ├── AsignarEvaluadorUseCase.java
│   │       ├── ConsultarEvaluacionesUseCase.java
│   │       └── RegistrarResultadoEvaluacionUseCase.java
│   └── service
│       └── EvaluacionService.java
│
├── domain
│   ├── enums
│   │   └── ResultadoEvaluacion.java
│   ├── exception
│   │   └── EvaluacionException.java
│   ├── model
│   │   └── Evaluacion.java
│   └── ports
│       └── out
│           └── EvaluacionRepositoryPort.java
│
├── infrastructure
│   └── persistence
│       ├── adapter
│       │   └── EvaluacionPersistenceAdapter.java
│       ├── entity
│       │   └── EvaluacionJpaEntity.java
│       ├── mapper
│       │   └── EvaluacionPersistenceMapper.java
│       └── repository
│           └── EvaluacionJpaRepository.java
│
└── presentation
    ├── controller
    │   └── EvaluacionController.java
    └── dto
        ├── AsignarEvaluadorRequest.java
        └── RegistrarResultadoEvaluacionRequest.java
```

---

## 8. Descripción de capas

### 8.1. Capa Domain

La capa **Domain** contiene el núcleo del módulo. Aquí se ubican las reglas de negocio principales y el modelo puro de evaluación.

Archivos principales:

```text
domain/model/Evaluacion.java
domain/enums/ResultadoEvaluacion.java
domain/exception/EvaluacionException.java
domain/ports/out/EvaluacionRepositoryPort.java
```

Responsabilidades:

* Representar el modelo de evaluación.
* Definir los resultados permitidos.
* Validar reglas propias del negocio.
* Declarar el puerto de salida para persistencia.

---

### 8.2. Capa Application

La capa **Application** contiene los casos de uso del módulo. Coordina las operaciones principales sin depender directamente de JPA ni de detalles técnicos de infraestructura.

Archivos principales:

```text
application/service/EvaluacionService.java
application/ports/in/AsignarEvaluadorUseCase.java
application/ports/in/RegistrarResultadoEvaluacionUseCase.java
application/ports/in/ConsultarEvaluacionesUseCase.java
application/dto/command/AsignarEvaluadorCommand.java
application/dto/command/RegistrarResultadoEvaluacionCommand.java
application/dto/response/EvaluacionResponse.java
```

Responsabilidades:

* Asignar evaluadores.
* Registrar resultados de evaluación.
* Consultar evaluaciones.
* Validar solicitudes antes de enviarlas al dominio.
* Convertir el modelo de dominio en respuestas para la capa de presentación.

---

### 8.3. Capa Infrastructure

La capa **Infrastructure** contiene la implementación técnica de persistencia usando Spring Data JPA.

Archivos principales:

```text
infrastructure/persistence/entity/EvaluacionJpaEntity.java
infrastructure/persistence/repository/EvaluacionJpaRepository.java
infrastructure/persistence/mapper/EvaluacionPersistenceMapper.java
infrastructure/persistence/adapter/EvaluacionPersistenceAdapter.java
```

Responsabilidades:

* Mapear la tabla `evaluaciones`.
* Implementar el acceso a datos mediante JPA.
* Convertir entre entidad JPA y modelo de dominio.
* Implementar el puerto `EvaluacionRepositoryPort`.

---

### 8.4. Capa Presentation

La capa **Presentation** expone los endpoints REST del módulo.

Archivos principales:

```text
presentation/controller/EvaluacionController.java
presentation/dto/AsignarEvaluadorRequest.java
presentation/dto/RegistrarResultadoEvaluacionRequest.java
```

Responsabilidades:

* Recibir solicitudes HTTP.
* Convertir los request en commands.
* Delegar la ejecución a los casos de uso.
* Devolver respuestas HTTP al cliente.

---

## 9. Reglas de negocio implementadas

El módulo considera las siguientes reglas:

| N.º | Regla de negocio                                                                                        |
| --- | ------------------------------------------------------------------------------------------------------- |
| 1   | Una evaluación debe estar asociada a un proyecto o a un plan de tesis.                                  |
| 2   | Una evaluación no puede estar asociada a un proyecto y a un plan de tesis al mismo tiempo.              |
| 3   | Toda evaluación debe tener un evaluador asignado.                                                       |
| 4   | El puntaje debe estar entre 0 y 100.                                                                    |
| 5   | Si el resultado es `RECHAZADO`, se deben registrar observaciones.                                       |
| 6   | Si el resultado es `CON_OBSERVACIONES`, se deben registrar observaciones.                               |
| 7   | Un evaluador no puede tener una evaluación pendiente duplicada sobre el mismo proyecto o plan de tesis. |
| 8   | Solo el evaluador asignado puede registrar el resultado de su evaluación.                               |

---

## 10. Resultados permitidos

El módulo maneja los siguientes resultados de evaluación:

```text
APROBADO
RECHAZADO
CON_OBSERVACIONES
```

Estos valores se encuentran definidos en el enum:

```text
ResultadoEvaluacion.java
```

---

## 11. Endpoints implementados

| Método | Endpoint                                 | Descripción                                        |
| ------ | ---------------------------------------- | -------------------------------------------------- |
| `GET`  | `/evaluaciones`                          | Lista todas las evaluaciones registradas.          |
| `GET`  | `/evaluaciones/{idEvaluacion}`           | Busca una evaluación por su identificador.         |
| `GET`  | `/evaluaciones/evaluador/{idEvaluador}`  | Lista evaluaciones asignadas a un evaluador.       |
| `POST` | `/evaluaciones/asignar`                  | Asigna un evaluador a un proyecto o plan de tesis. |
| `POST` | `/evaluaciones/{idEvaluacion}/resultado` | Registra el resultado de una evaluación.           |

---

## 12. Tabla utilizada

El módulo utiliza la tabla principal:

```text
evaluaciones
```

Campos principales:

| Campo              | Descripción                                |
| ------------------ | ------------------------------------------ |
| `id_evaluacion`    | Identificador único de la evaluación.      |
| `id_proyecto`      | Identificador del proyecto evaluado.       |
| `id_plan_tesis`    | Identificador del plan de tesis evaluado.  |
| `id_evaluador`     | Identificador del usuario evaluador.       |
| `resultado`        | Resultado emitido por el evaluador.        |
| `puntaje`          | Puntaje asignado en la evaluación.         |
| `observaciones`    | Comentarios u observaciones del evaluador. |
| `fecha_asignacion` | Fecha en que se asignó la evaluación.      |
| `fecha_evaluacion` | Fecha en que se registró el resultado.     |

---

## 13. Pruebas en entorno Docker

El módulo fue probado usando los siguientes contenedores:

| Contenedor            | Descripción                         |
| --------------------- | ----------------------------------- |
| `sgifiis-backend-api` | Contenedor del backend Spring Boot. |
| `sgifiis-postgres-db` | Contenedor de PostgreSQL.           |

Se verificó que ambos contenedores estuvieran activos:

```powershell
docker compose ps
```

Resultado esperado:

```text
sgifiis-backend-api   Up   8080
sgifiis-postgres-db   Up   5432
```

---

## 14. Prueba: listar evaluaciones

Comando ejecutado:

```powershell
curl.exe http://localhost:8080/evaluaciones
```

Respuesta inicial:

```json
[]
```

Esta respuesta indica que el endpoint funciona correctamente, aunque inicialmente no existían evaluaciones registradas.

---

## 15. Prueba: asignar evaluador

Para evitar problemas de comillas en PowerShell, se utilizó un archivo JSON local.

Archivo usado:

```text
fiis/local/evaluaciones/payloads/body-evaluacion.json
```

Contenido del archivo:

```json
{
  "idProyecto": 1,
  "idPlanTesis": null,
  "idEvaluador": 2
}
```

Comando ejecutado:

```powershell
curl.exe -X POST "http://localhost:8080/evaluaciones/asignar" `
-H "Content-Type: application/json" `
--data-binary "@fiis/local/evaluaciones/payloads/body-evaluacion.json"
```

Respuesta obtenida:

```json
{
  "idEvaluacion": 1,
  "idProyecto": 1,
  "idPlanTesis": null,
  "idEvaluador": 2,
  "resultado": null,
  "puntaje": null,
  "observaciones": null,
  "fechaAsignacion": "2026-06-12T16:20:51.440956549",
  "fechaEvaluacion": null,
  "pendiente": true
}
```

La respuesta confirma que la evaluación fue registrada correctamente como pendiente.

---

## 16. Prueba: registrar resultado de evaluación

Archivo usado:

```text
fiis/local/evaluaciones/payloads/body-resultado.json
```

Contenido del archivo:

```json
{
  "idEvaluador": 2,
  "resultado": "APROBADO",
  "puntaje": 90,
  "observaciones": "El proyecto cumple con los criterios establecidos."
}
```

Comando sugerido:

```powershell
curl.exe -X POST "http://localhost:8080/evaluaciones/1/resultado" `
-H "Content-Type: application/json" `
--data-binary "@fiis/local/evaluaciones/payloads/body-resultado.json"
```

Respuesta esperada:

```json
{
  "idEvaluacion": 1,
  "idProyecto": 1,
  "idPlanTesis": null,
  "idEvaluador": 2,
  "resultado": "APROBADO",
  "puntaje": 90,
  "observaciones": "El proyecto cumple con los criterios establecidos.",
  "fechaAsignacion": "2026-06-12T16:20:51.440956549",
  "fechaEvaluacion": "2026-06-12T16:25:00",
  "pendiente": false
}
```

---

## 17. Verificación en PostgreSQL

Para verificar la persistencia de datos en PostgreSQL, se ingresó al contenedor de base de datos:

```powershell
docker exec -it sgifiis-postgres-db psql -U postgres -d db_fiis_investigacion
```

Consulta ejecutada:

```sql
SELECT *
FROM evaluaciones;
```

Esta consulta permite comprobar que la evaluación fue registrada correctamente en la tabla `evaluaciones`.

---

## 18. Evidencia de funcionamiento

Se comprobó que:

* El backend se ejecuta correctamente en el puerto `8080`.
* PostgreSQL se ejecuta correctamente en el puerto `5432`.
* La tabla `evaluaciones` existe en la base de datos.
* El endpoint `GET /evaluaciones` responde correctamente.
* El endpoint `POST /evaluaciones/asignar` registra una evaluación pendiente.
* La evaluación queda persistida en PostgreSQL.
* El módulo respeta la separación de responsabilidades definida por Clean Architecture.

---

## 19. Conclusión

El módulo **Evaluaciones** permite gestionar la asignación y registro de evaluaciones dentro del SGI-FIIS. Su implementación respeta los principios de Clean Architecture, separando dominio, aplicación, infraestructura y presentación.

Esta organización reduce el acoplamiento, mejora la mantenibilidad del código y facilita futuras ampliaciones, como integración con autenticación JWT, filtros por estado de evaluación, reportes por evaluador, trazabilidad avanzada y validaciones por roles institucionales.

## 20. Decisiones técnicas tomadas

Durante la implementación del módulo Evaluaciones se tomaron las siguientes decisiones técnicas:

| Decisión                                              | Justificación                                                                                      |
| ----------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| Separar el módulo en capas de Clean Architecture      | Permite mantener separado el dominio, los casos de uso, la persistencia y los controladores REST.  |
| Usar un modelo de dominio independiente de JPA        | Evita que la lógica de negocio dependa directamente de anotaciones o detalles de base de datos.    |
| Usar `EvaluacionRepositoryPort` como puerto de salida | Permite que la capa de aplicación dependa de una abstracción y no directamente de Spring Data JPA. |
| Usar `EvaluacionJpaEntity` solo en infraestructura    | Mantiene aislada la representación de la tabla `evaluaciones`.                                     |
| Usar `Command` y `Response` en la capa application    | Facilita la comunicación entre capas sin exponer directamente entidades internas.                  |
| Usar archivos JSON para pruebas con PowerShell        | Evita errores de comillas al enviar cuerpos JSON mediante `curl.exe`.                              |

---

## 21. Problemas encontrados y solución aplicada

Durante las pruebas del módulo se identificaron algunos inconvenientes técnicos:

| Problema                                        | Causa                                                                 | Solución                                                                                       |
| ----------------------------------------------- | --------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| El endpoint `GET /evaluaciones` respondía `[]`  | La tabla `evaluaciones` estaba vacía.                                 | Se verificó que el endpoint funcionaba correctamente y luego se creó una evaluación de prueba. |
| Error al usar `curl` en PowerShell              | PowerShell interpreta `curl` como alias de `Invoke-WebRequest`.       | Se utilizó `curl.exe` para ejecutar el comando real de curl.                                   |
| Error `400 Bad Request` al enviar JSON con `-d` | El cuerpo JSON no se enviaba correctamente por problemas de comillas. | Se creó un archivo `body-evaluacion.json` y se envió con `--data-binary`.                      |
| Error de conexión a `localhost:8080`            | El contenedor del backend estaba detenido.                            | Se levantó nuevamente el servicio `api_fiis` con Docker Compose.                               |
| No se podía consultar PostgreSQL con `db_fiis`  | El servicio de base de datos no estaba activo inicialmente.           | Se verificó el nombre real del contenedor y se usó `sgifiis-postgres-db`.                      |

---

## 22. Mejoras futuras

El módulo Evaluaciones puede ampliarse en futuras iteraciones con las siguientes mejoras:

* Agregar manejo global de excepciones para devolver errores en formato JSON más claro.
* Agregar filtros para listar evaluaciones por estado: pendientes, aprobadas, rechazadas o con observaciones.
* Integrar el módulo con autenticación JWT para obtener el evaluador desde el usuario autenticado.
* Validar que el usuario asignado tenga realmente el rol de evaluador.
* Agregar trazabilidad de cambios cuando una evaluación sea registrada o modificada.
* Agregar endpoints para consultar evaluaciones por proyecto o por plan de tesis.
* Evitar la modificación de una evaluación que ya tenga resultado registrado.
* Agregar pruebas unitarias para la capa de dominio y aplicación.
* Agregar pruebas de integración para los endpoints REST.
* Agregar paginación en los listados cuando existan muchas evaluaciones.

---

## 23. Estado actual del módulo

El módulo Evaluaciones se encuentra en estado funcional inicial. Actualmente permite:

* Consultar evaluaciones registradas.
* Asignar evaluadores a proyectos o planes de tesis.
* Registrar resultados de evaluación.
* Guardar puntajes y observaciones.
* Persistir la información en PostgreSQL.
* Ejecutarse correctamente dentro del entorno Docker del proyecto.

El módulo queda preparado para integrarse posteriormente con seguridad, roles institucionales, trazabilidad y reportes.

