# Matriz de Trazabilidad del Módulo Evaluaciones

## 1. Información general

| Campo                         | Descripción                              |
| ----------------------------- | ---------------------------------------- |
| **Proyecto**                  | Sistema de Gestión de Investigación FIIS |
| **Módulo**                    | Evaluaciones                             |
| **Rama de trabajo**           | `feature/evaluaciones`                   |
| **Arquitectura aplicada**     | Clean Architecture                       |
| **Responsabilidad principal** | Asignación y registro de evaluaciones    |
| **Base de datos**             | PostgreSQL                               |
| **Tabla principal**           | `evaluaciones`                           |

---

## 2. Objetivo de la matriz

La presente matriz tiene como finalidad relacionar los requisitos funcionales del módulo **Evaluaciones** con los componentes implementados en el código fuente, los endpoints REST expuestos y las pruebas realizadas en el entorno Docker.

Esta trazabilidad permite verificar que cada requisito funcional asignado al módulo cuenta con una implementación concreta dentro del sistema.

---

## 3. Requisitos funcionales trazados

| Código | Requisito funcional                                                           |
| ------ | ----------------------------------------------------------------------------- |
| RF-83  | El sistema debe permitir asignar evaluadores a proyectos o documentos.        |
| RF-84  | El evaluador debe poder visualizar proyectos asignados.                       |
| RF-85  | El evaluador debe poder registrar resultados de evaluación.                   |
| RF-86  | El sistema debe registrar observaciones, puntajes o resultados de evaluación. |
| RF-87  | El Director debe poder consultar las evaluaciones realizadas.                 |

---

## 4. Matriz de trazabilidad funcional

| Requisito | Funcionalidad implementada                         | Endpoint                                                | Clase principal                             | Estado       |
| --------- | -------------------------------------------------- | ------------------------------------------------------- | ------------------------------------------- | ------------ |
| RF-83     | Asignación de evaluador a proyecto o plan de tesis | `POST /evaluaciones/asignar`                            | `EvaluacionController`, `EvaluacionService` | Implementado |
| RF-84     | Consulta de evaluaciones asignadas a un evaluador  | `GET /evaluaciones/evaluador/{idEvaluador}`             | `EvaluacionController`, `EvaluacionService` | Implementado |
| RF-85     | Registro del resultado de una evaluación           | `POST /evaluaciones/{idEvaluacion}/resultado`           | `EvaluacionController`, `EvaluacionService` | Implementado |
| RF-86     | Registro de resultado, puntaje y observaciones     | `POST /evaluaciones/{idEvaluacion}/resultado`           | `Evaluacion`, `EvaluacionService`           | Implementado |
| RF-87     | Consulta de evaluaciones realizadas                | `GET /evaluaciones`, `GET /evaluaciones/{idEvaluacion}` | `EvaluacionController`, `EvaluacionService` | Implementado |

---

## 5. Trazabilidad por capas de Clean Architecture

| Requisito | Domain                                        | Application                                                                                       | Infrastructure                                            | Presentation                                                  |
| --------- | --------------------------------------------- | ------------------------------------------------------------------------------------------------- | --------------------------------------------------------- | ------------------------------------------------------------- |
| RF-83     | `Evaluacion.java`                             | `AsignarEvaluadorUseCase`, `EvaluacionService`, `AsignarEvaluadorCommand`                         | `EvaluacionPersistenceAdapter`, `EvaluacionJpaRepository` | `EvaluacionController`, `AsignarEvaluadorRequest`             |
| RF-84     | `Evaluacion.java`                             | `ConsultarEvaluacionesUseCase`, `EvaluacionService`                                               | `EvaluacionPersistenceAdapter`, `EvaluacionJpaRepository` | `EvaluacionController`                                        |
| RF-85     | `Evaluacion.java`, `ResultadoEvaluacion.java` | `RegistrarResultadoEvaluacionUseCase`, `EvaluacionService`, `RegistrarResultadoEvaluacionCommand` | `EvaluacionPersistenceAdapter`, `EvaluacionJpaRepository` | `EvaluacionController`, `RegistrarResultadoEvaluacionRequest` |
| RF-86     | `Evaluacion.java`, `ResultadoEvaluacion.java` | `EvaluacionService`, `EvaluacionResponse`                                                         | `EvaluacionJpaEntity`, `EvaluacionPersistenceMapper`      | `EvaluacionController`                                        |
| RF-87     | `Evaluacion.java`                             | `ConsultarEvaluacionesUseCase`, `EvaluacionService`                                               | `EvaluacionPersistenceAdapter`, `EvaluacionJpaRepository` | `EvaluacionController`                                        |

---

## 6. Trazabilidad de endpoints

| Método | Endpoint                                 | Requisito relacionado | Descripción                                                             |
| ------ | ---------------------------------------- | --------------------- | ----------------------------------------------------------------------- |
| `GET`  | `/evaluaciones`                          | RF-87                 | Permite consultar todas las evaluaciones registradas.                   |
| `GET`  | `/evaluaciones/{idEvaluacion}`           | RF-87                 | Permite consultar una evaluación específica por su identificador.       |
| `GET`  | `/evaluaciones/evaluador/{idEvaluador}`  | RF-84                 | Permite consultar las evaluaciones asignadas a un evaluador.            |
| `POST` | `/evaluaciones/asignar`                  | RF-83                 | Permite asignar un evaluador a un proyecto o plan de tesis.             |
| `POST` | `/evaluaciones/{idEvaluacion}/resultado` | RF-85, RF-86          | Permite registrar resultado, puntaje y observaciones de una evaluación. |

---

## 7. Trazabilidad con la base de datos

| Requisito | Tabla          | Campos utilizados                                                                   |
| --------- | -------------- | ----------------------------------------------------------------------------------- |
| RF-83     | `evaluaciones` | `id_evaluacion`, `id_proyecto`, `id_plan_tesis`, `id_evaluador`, `fecha_asignacion` |
| RF-84     | `evaluaciones` | `id_evaluador`, `id_proyecto`, `id_plan_tesis`, `resultado`, `fecha_evaluacion`     |
| RF-85     | `evaluaciones` | `id_evaluacion`, `id_evaluador`, `resultado`, `fecha_evaluacion`                    |
| RF-86     | `evaluaciones` | `resultado`, `puntaje`, `observaciones`, `fecha_evaluacion`                         |
| RF-87     | `evaluaciones` | Todos los campos principales de consulta                                            |

---

## 8. Reglas de negocio asociadas

| Código   | Regla de negocio                                                                                        | Archivo donde se aplica                                  |
| -------- | ------------------------------------------------------------------------------------------------------- | -------------------------------------------------------- |
| RN-EV-01 | Una evaluación debe estar asociada a un proyecto o a un plan de tesis.                                  | `Evaluacion.java`                                        |
| RN-EV-02 | Una evaluación no puede estar asociada a un proyecto y a un plan de tesis al mismo tiempo.              | `Evaluacion.java`, `EvaluacionService.java`              |
| RN-EV-03 | Toda evaluación debe tener un evaluador asignado.                                                       | `Evaluacion.java`, `EvaluacionService.java`              |
| RN-EV-04 | El puntaje debe estar entre 0 y 100.                                                                    | `Evaluacion.java`                                        |
| RN-EV-05 | Si el resultado es `RECHAZADO`, debe registrarse una observación.                                       | `Evaluacion.java`                                        |
| RN-EV-06 | Si el resultado es `CON_OBSERVACIONES`, debe registrarse una observación.                               | `Evaluacion.java`                                        |
| RN-EV-07 | Un evaluador no puede tener una evaluación pendiente duplicada sobre el mismo proyecto o plan de tesis. | `EvaluacionService.java`, `EvaluacionJpaRepository.java` |
| RN-EV-08 | Solo el evaluador asignado puede registrar el resultado de su evaluación.                               | `EvaluacionService.java`                                 |

---

## 9. Trazabilidad de pruebas realizadas

| Caso de prueba                       | Comando o acción                              | Resultado esperado                                      | Requisito validado  |
| ------------------------------------ | --------------------------------------------- | ------------------------------------------------------- | ------------------- |
| Consultar evaluaciones sin registros | `curl.exe http://localhost:8080/evaluaciones` | Retorna `[]`                                            | RF-87               |
| Asignar evaluador a proyecto         | `POST /evaluaciones/asignar`                  | Retorna evaluación pendiente                            | RF-83               |
| Consultar evaluación por ID          | `GET /evaluaciones/1`                         | Retorna la evaluación registrada                        | RF-87               |
| Consultar evaluaciones por evaluador | `GET /evaluaciones/evaluador/2`               | Retorna evaluaciones asignadas al evaluador             | RF-84               |
| Registrar resultado de evaluación    | `POST /evaluaciones/1/resultado`              | Retorna evaluación con resultado, puntaje y observación | RF-85, RF-86        |
| Verificar persistencia en PostgreSQL | `SELECT * FROM evaluaciones;`                 | La evaluación aparece registrada en la tabla            | RF-83, RF-85, RF-86 |

---

## 10. Evidencia de prueba: asignación de evaluador

Comando utilizado:

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

Interpretación:

La respuesta confirma que se creó una evaluación asociada al proyecto con identificador `1`, asignada al evaluador con identificador `2`. La evaluación queda en estado pendiente porque aún no tiene resultado registrado.

---

## 11. Cobertura actual del módulo

| Aspecto                         | Estado    |
| ------------------------------- | --------- |
| Estructura Clean Architecture   | Cubierto  |
| Modelo de dominio               | Cubierto  |
| Casos de uso                    | Cubierto  |
| Persistencia con JPA            | Cubierto  |
| Endpoints REST                  | Cubierto  |
| Prueba con Docker               | Cubierto  |
| Prueba con PostgreSQL           | Cubierto  |
| Seguridad con JWT               | Pendiente |
| Validación de roles reales      | Pendiente |
| Manejo global de errores        | Pendiente |
| Pruebas unitarias automatizadas | Pendiente |

---

## 12. Conclusión

La matriz de trazabilidad evidencia que los requisitos funcionales asignados al módulo **Evaluaciones** fueron implementados mediante clases, endpoints, reglas de negocio y persistencia en base de datos.

El módulo cumple con su propósito principal: permitir la asignación de evaluadores, la consulta de evaluaciones y el registro de resultados, puntajes y observaciones dentro del Sistema de Gestión de Investigación FIIS.
