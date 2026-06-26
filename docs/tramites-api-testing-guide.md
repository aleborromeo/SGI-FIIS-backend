# Guía de Pruebas — API de Trámites (SGI-FIIS)

## Índice

1. [Levantar el entorno](#1-levantar-el-entorno)
2. [Usuarios de prueba](#2-usuarios-de-prueba)
3. [Autenticación — obtener token JWT](#3-autenticación--obtener-token-jwt)
4. [Endpoints del módulo de trámites](#4-endpoints-del-módulo-de-trámites)
5. [Flujo feliz — aprobación completa](#5-flujo-feliz--aprobación-completa)
6. [Flujo alternativo — observar y subsanar](#6-flujo-alternativo--observar-y-subsanar)
7. [Flujo de rechazo](#7-flujo-de-rechazo)
8. [Errores esperados](#8-errores-esperados)
9. [Visualización en Swagger UI](#9-visualización-en-swagger-ui)

---

## 1. Levantar el entorno

### Prerequisito: base de datos corriendo

El contenedor de PostgreSQL debe estar activo. Verificar con:

```powershell
docker ps --filter "name=sgifiis-postgres-db"
```

Si no está corriendo:

```powershell
# Desde la raíz del repositorio (SGI-FIIS-backend/)
docker compose up db_fiis
```

### Iniciar el backend

```powershell
cd fiis

# SPRING_FLYWAY_ENABLED=true es necesario para que Flyway
# aplique las migraciones al arrancar por primera vez.
# Una vez aplicadas, el flag sigue siendo necesario para que
# el bean de Flyway no falle al iniciar.
$env:SPRING_FLYWAY_ENABLED = "true"

.\mvnw spring-boot:run
```

**Qué hace este comando:**
- `$env:SPRING_FLYWAY_ENABLED = "true"` — establece una variable de entorno real en el proceso de PowerShell. Spring Boot la lee y activa Flyway.
- `.\mvnw spring-boot:run` — compila y ejecuta la aplicación con el plugin de Maven para Spring Boot.

**Salida esperada en consola (primera ejecución):**
```
Successfully applied 9 migrations to schema "public", now at version v9
Started FiisApplication in X.XXX seconds
```

El servidor queda disponible en `http://localhost:8080`.

---

## 2. Usuarios de prueba

Todos los usuarios tienen contraseña `00000000`. Fueron creados por las migraciones Flyway V1 y V2.

> **Nota:** La migración V5 cambió el email del estudiante de `juan.perez@unas.edu.pe` a `jose.evaristo@unas.edu.pe`.

| Rol | Email | Puede hacer |
|---|---|---|
| `ESTUDIANTE` | `jose.evaristo@unas.edu.pe` | Crear trámites, subsanar |
| `COORDINADOR_GRUPO` | `carlos.ramos@unas.edu.pe` | Aprobar, observar, rechazar |
| `DIRECTOR_INVESTIGACION` | `ana.torres@unas.edu.pe` | Aprobar, observar, rechazar |
| `DECANO` | `luis.mendoza@unas.edu.pe` | Registrar resolución, observar |
| `ADMIN` | `admin@unas.edu.pe` | Gestión de usuarios |

---

## 3. Autenticación — obtener token JWT

### Endpoint

```
POST /api/v1/auth/login
```

### Cuerpo de la solicitud

```json
{
  "email": "jose.evaristo@unas.edu.pe",
  "password": "00000000"
}
```

**Campos:**
- `email` — correo institucional del usuario
- `password` — contraseña en texto plano (el backend la compara contra el hash BCrypt almacenado)

### Comando PowerShell

```powershell
$tokenEst = (Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/auth/login" `
  -Method POST `
  -Body '{"email":"jose.evaristo@unas.edu.pe","password":"00000000"}' `
  -ContentType "application/json").token
```

**Qué hace:**
- `Invoke-RestMethod` — realiza la llamada HTTP y deserializa el JSON de respuesta a un objeto PowerShell automáticamente.
- `.token` — extrae solo el campo `token` de la respuesta y lo guarda en `$tokenEst`.

### Respuesta exitosa

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb3Nl...",
  "type": "Bearer",
  "email": "jose.evaristo@unas.edu.pe",
  "firstNames": "Juan",
  "lastNames": "Perez",
  "roleCode": "ESTUDIANTE",
  "mustChangePassword": true,
  "requiresVerification": false
}
```

**Campos de la respuesta:**
- `token` — JWT firmado con HS512. Expira en 24 horas. Debe enviarse en todas las llamadas protegidas como `Authorization: Bearer <token>`.
- `type` — siempre `"Bearer"`, indica el esquema de autenticación.
- `roleCode` — rol del usuario. El backend usa este valor internamente para el dispatch de acciones en los use cases.
- `mustChangePassword` — si `true`, el usuario debe cambiar su contraseña antes de operar normalmente (en producción). En desarrollo se puede ignorar.

### Cómo usar el token en cada llamada

```powershell
-Headers @{ Authorization = "Bearer $tokenEst" }
```

---

## 4. Endpoints del módulo de trámites

Todos bajo el prefijo `/api/v1/tramites`. Requieren autenticación JWT.

### POST `/api/v1/tramites` — Crear trámite

Crea un nuevo trámite y lo presenta automáticamente al Coordinador de Grupo.

**Quién puede usarlo:** Cualquier usuario autenticado (el solicitante es el usuario del token, no un campo del body).

**Cuerpo:**
```json
{
  "tipoTramite": "PLAN_TESIS",
  "idGrupo": 9,
  "idReferenciaTesis": 1
}
```

**Campos:**
- `tipoTramite` — obligatorio. Valores válidos: `PROYECTO`, `PLAN_TESIS`, `INFORME_AVANCE`.
- `idGrupo` — opcional. ID del grupo de investigación al que pertenece el trámite.
- `idReferenciaProyecto` / `idReferenciaTesis` / `idReferenciaInforme` — **arco excluyente**: exactamente uno debe ser no nulo. Referencia la entidad de origen del trámite.

> `idSolicitante` no se envía en el body — el backend lo extrae del token JWT automáticamente por seguridad.

**Respuesta:** `201 Created` con el trámite en estado `PENDIENTE_COORDINADOR`.

---

### PUT `/api/v1/tramites/{id}/aprobar` — Aprobar trámite

Avanza el trámite al siguiente estado según el rol del ejecutor.

**Sin body.** El rol se extrae del JWT.

| Rol del token | Estado de entrada | Estado de salida |
|---|---|---|
| `COORDINADOR_GRUPO` | `PENDIENTE_COORDINADOR` | `PENDIENTE_DIRECCION` |
| `DIRECTOR_INVESTIGACION` | `PENDIENTE_DIRECCION` | `PENDIENTE_DECANATO` |

**Respuesta:** `200 OK` con el trámite actualizado.

---

### PUT `/api/v1/tramites/{id}/observar` — Observar trámite

Regresa el trámite al solicitante con una observación. El solicitante debe subsanar.

**Cuerpo:**
```json
{
  "textoObservacion": "Falta firma del asesor en el documento"
}
```

**Campos:**
- `textoObservacion` — obligatorio, no puede estar vacío (`@NotBlank`).

| Rol del token | Estado de entrada | Estado de salida |
|---|---|---|
| `COORDINADOR_GRUPO` | `PENDIENTE_COORDINADOR` | `OBSERVADO` |
| `DIRECTOR_INVESTIGACION` | `PENDIENTE_DIRECCION` | `OBSERVADO` |
| `DECANO` | `PENDIENTE_DECANATO` | `OBSERVADO` |

**Respuesta:** `200 OK` con el trámite en estado `OBSERVADO`.

---

### PUT `/api/v1/tramites/{id}/subsanar` — Subsanar trámite

El solicitante original responde a la observación. Solo puede hacerlo el mismo usuario que creó el trámite.

**Cuerpo:**
```json
{
  "detalleSubsanacion": "Se adjunta firma del asesor escaneada"
}
```

**Campos:**
- `detalleSubsanacion` — obligatorio, no puede estar vacío.

**Flujo resultante:** `OBSERVADO` → `SUBSANADO` → `PENDIENTE_COORDINADOR` (doble transición automática).

**Respuesta:** `200 OK` con el trámite de vuelta en `PENDIENTE_COORDINADOR`.

---

### PUT `/api/v1/tramites/{id}/rechazar` — Rechazar trámite

Rechaza el trámite de forma definitiva (estado terminal, no reversible).

**Sin body.** El rol se extrae del JWT.

| Rol del token | Estado de entrada | Estado de salida |
|---|---|---|
| `COORDINADOR_GRUPO` | `PENDIENTE_COORDINADOR` | `RECHAZADO` |
| `DIRECTOR_INVESTIGACION` | `PENDIENTE_DIRECCION` | `RECHAZADO` |

> El Decano no puede rechazar — está excluido por diseño del dominio.

**Respuesta:** `200 OK` con el trámite en estado `RECHAZADO`.

---

### PUT `/api/v1/tramites/{id}/resolucion` — Registrar resolución

Solo el Decano. Finaliza el trámite en dos pasos automáticos: `PENDIENTE_DECANATO` → `APROBADO_CON_RESOLUCION` → `FINALIZADO`.

**Sin body.** El ejecutor es el usuario del token.

**Respuesta:** `200 OK` con el trámite en estado `FINALIZADO`.

---

### GET `/api/v1/tramites/{id}/trazabilidad` — Consultar historial

Devuelve todos los movimientos del trámite en orden cronológico. Cualquier usuario autenticado puede consultarla.

**Sin body.**

**Respuesta:**
```json
[
  {
    "idUsuarioAccion": 2,
    "accion": "PRESENTADO_POR_SOLICITANTE",
    "estadoAnterior": "REGISTRADO",
    "estadoNuevo": "PENDIENTE_COORDINADOR",
    "observacion": null,
    "fechaMovimiento": "2026-06-23T03:30:45.123"
  }
]
```

---

## 5. Flujo feliz — aprobación completa

Ejecutar en orden en PowerShell. El backend debe estar corriendo.

### Paso 1 — Login como Estudiante y crear trámite

```powershell
# Obtener token del Estudiante
$tokenEst = (Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/auth/login" `
  -Method POST `
  -Body '{"email":"jose.evaristo@unas.edu.pe","password":"00000000"}' `
  -ContentType "application/json").token

# Crear el trámite
# - tipoTramite: PLAN_TESIS
# - idGrupo: 9 (grupo de prueba insertado en la DB)
# - idReferenciaTesis: 1 (plan de tesis de prueba insertado en la DB)
$tramite = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/tramites" `
  -Method POST `
  -Body '{"tipoTramite":"PLAN_TESIS","idGrupo":9,"idReferenciaTesis":1}' `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $tokenEst" }

$ID = $tramite.id
Write-Host "Creado: id=$ID estado=$($tramite.estadoActual) revisor=$($tramite.rolRevisorActual)"
# Resultado esperado:
# Creado: id=2 estado=PENDIENTE_COORDINADOR revisor=COORDINADOR_GRUPO
```

### Paso 2 — Coordinador aprueba

```powershell
# Login como Coordinador
$tokenCoord = (Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/auth/login" `
  -Method POST `
  -Body '{"email":"carlos.ramos@unas.edu.pe","password":"00000000"}' `
  -ContentType "application/json").token

# Aprobar — no requiere body
$r = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/tramites/$ID/aprobar" `
  -Method PUT `
  -Headers @{ Authorization = "Bearer $tokenCoord" }

Write-Host "Coordinador aprobó → estado=$($r.estadoActual) próximo_revisor=$($r.rolRevisorActual)"
# Resultado esperado:
# Coordinador aprobó → estado=PENDIENTE_DIRECCION próximo_revisor=DIRECTOR_INVESTIGACION
```

### Paso 3 — Director aprueba

```powershell
# Login como Director
$tokenDir = (Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/auth/login" `
  -Method POST `
  -Body '{"email":"ana.torres@unas.edu.pe","password":"00000000"}' `
  -ContentType "application/json").token

$r = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/tramites/$ID/aprobar" `
  -Method PUT `
  -Headers @{ Authorization = "Bearer $tokenDir" }

Write-Host "Director aprobó → estado=$($r.estadoActual) próximo_revisor=$($r.rolRevisorActual)"
# Resultado esperado:
# Director aprobó → estado=PENDIENTE_DECANATO próximo_revisor=DECANO
```

### Paso 4 — Decano registra resolución

```powershell
# Login como Decano
$tokenDec = (Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/auth/login" `
  -Method POST `
  -Body '{"email":"luis.mendoza@unas.edu.pe","password":"00000000"}' `
  -ContentType "application/json").token

$r = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/tramites/$ID/resolucion" `
  -Method PUT `
  -Headers @{ Authorization = "Bearer $tokenDec" }

Write-Host "Decano finalizó → estado=$($r.estadoActual)"
# Resultado esperado:
# Decano finalizó → estado=FINALIZADO
```

### Paso 5 — Ver trazabilidad completa

```powershell
$traz = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/tramites/$ID/trazabilidad" `
  -Headers @{ Authorization = "Bearer $tokenDec" }

Write-Host "TRAZABILIDAD ($($traz.Count) movimientos):"
$traz | ForEach-Object {
  Write-Host "  [$($_.accion)]  $($_.estadoAnterior) → $($_.estadoNuevo)  (usuario=$($_.idUsuarioAccion))"
}
```

**Resultado esperado:**
```
TRAZABILIDAD (5 movimientos):
  [PRESENTADO_POR_SOLICITANTE]    REGISTRADO             → PENDIENTE_COORDINADOR      (usuario=2)
  [APROBADO_POR_COORDINADOR]      PENDIENTE_COORDINADOR  → PENDIENTE_DIRECCION        (usuario=4)
  [APROBADO_POR_DIRECTOR]         PENDIENTE_DIRECCION    → PENDIENTE_DECANATO         (usuario=5)
  [RESOLUCION_REGISTRADA]         PENDIENTE_DECANATO     → APROBADO_CON_RESOLUCION    (usuario=6)
  [TRAMITE_FINALIZADO]            APROBADO_CON_RESOLUCION → FINALIZADO                (usuario=6)
```

---

## 6. Flujo alternativo — observar y subsanar

Primero crear un **nuevo** trámite (repite el Paso 1). Luego:

```powershell
# Coordinador observa el trámite en PENDIENTE_COORDINADOR
$r = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/tramites/$ID/observar" `
  -Method PUT `
  -Body '{"textoObservacion":"Falta firma del asesor en el documento"}' `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $tokenCoord" }

Write-Host "Observado → estado=$($r.estadoActual)"
Write-Host "Observacion vigente: $($r.observacionActual)"
# Resultado esperado:
# Observado → estado=OBSERVADO
# Observacion vigente: Falta firma del asesor en el documento

# Estudiante subsana (debe ser el mismo que creó el trámite)
$r = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/tramites/$ID/subsanar" `
  -Method PUT `
  -Body '{"detalleSubsanacion":"Se adjunta firma del asesor escaneada"}' `
  -ContentType "application/json" `
  -Headers @{ Authorization = "Bearer $tokenEst" }

Write-Host "Subsanado → estado=$($r.estadoActual)"
# Resultado esperado:
# Subsanado → estado=PENDIENTE_COORDINADOR
# El flujo de aprobación reinicia desde Coordinador
```

---

## 7. Flujo de rechazo

```powershell
# Crear un nuevo trámite primero (repite Paso 1)

# Coordinador rechaza (también puede hacerlo el Director si el trámite
# ya pasó a PENDIENTE_DIRECCION)
$r = Invoke-RestMethod `
  -Uri "http://localhost:8080/api/v1/tramites/$ID/rechazar" `
  -Method PUT `
  -Headers @{ Authorization = "Bearer $tokenCoord" }

Write-Host "Rechazado → estado=$($r.estadoActual)"
# Resultado esperado:
# Rechazado → estado=RECHAZADO (estado terminal, no reversible)
```

---

## 8. Errores esperados

Estos son errores válidos que el sistema debe devolver. Úsalos para verificar que las reglas de negocio están activas.

| Escenario | Cómo reproducirlo | Respuesta esperada |
|---|---|---|
| Rol incorrecto para la acción | Login como ESTUDIANTE, intentar aprobar un trámite | `400` — "El rol [ESTUDIANTE] no puede aprobar trámites" |
| Trámite inexistente | Llamar a `/api/v1/tramites/9999/aprobar` | `404` — "Trámite no encontrado con id: 9999" |
| Arco excluyente inválido | Crear con `idReferenciaProyecto` y `idReferenciaTesis` ambos no nulos | `400` — "El trámite debe referenciar exactamente una entidad origen" |
| Transición inválida | Intentar aprobar un trámite ya FINALIZADO | `400` — "Transición inválida: FINALIZADO → PENDIENTE_DIRECCION" |
| Otro usuario intenta subsanar | Login con un usuario diferente al solicitante, intentar subsanar | `400` — "Solo el solicitante original puede subsanar" |
| Sin token JWT | Llamar sin header `Authorization` | `401 Unauthorized` |
| Decano intenta rechazar | Login como Decano, llamar a `/rechazar` con trámite en `PENDIENTE_DECANATO` | `400` — "El rol [DECANO] no puede rechazar trámites" |

---

## 9. Visualización en Swagger UI

### Acceder

Con el backend corriendo:

```
http://localhost:8080/swagger-ui.html
```

### Seleccionar el grupo de trámites

En el selector desplegable de la esquina superior derecha, elegir **`tramites`**. Se mostrarán los 7 endpoints del módulo.

### Autenticar en Swagger

1. Hacer clic en el botón **Authorize** (candado) en la parte superior derecha.
2. En el campo **bearerAuth**, escribir el token JWT obtenido del login (sin el prefijo `Bearer`).
3. Clic en **Authorize** → **Close**.

Todos los endpoints marcados con candado cerrado ahora usan el token automáticamente.

### Grupos disponibles

| Grupo | Endpoints |
|---|---|
| `tramites` | `/api/v1/tramites/**` — módulo de trámites |
| `auth-users` | `/api/v1/auth/**` y `/api/v1/users/**` |
| `all-apis` | Todos los módulos implementados |

---

## Notas del entorno local

- **Puerto de PostgreSQL:** El contenedor levantado manualmente usa el puerto `5432` en el host (no `5433` como indica el `docker-compose.yml` — ese mapeo aplica solo cuando se levanta con `docker compose up`).
- **Contraseña de DB:** El contenedor manual usa `12345`. El `docker-compose.yml` usa `admin`.
- **Flyway:** Las migraciones se aplican una sola vez. En ejecuciones posteriores, Flyway detecta que ya están aplicadas y no las re-ejecuta.
- **Variable de Flyway:** `$env:SPRING_FLYWAY_ENABLED = "true"` debe estar presente en cada sesión de PowerShell antes de `.\mvnw spring-boot:run`. No persiste entre sesiones.
