Auditoría — Módulos grupos_investigacion y lineas_investigacion
Rama: feature/grupos_de_investigacion (autor: Kevin Escally) · Commits: 6114f92 (estructura inicial), 8ade2b3 (feat completo), 5c7a15a (fix seguridad en tests)

1. Arquitectura
Clean Architecture / hexagonal, igual que el resto del backend (users, auth, dashboards):


grupos_investigacion/
  application/{dto, usecase}
  domain/{model, port}
  infrastructure/persistence/
  presentation/{controller, mapper}
lineas_investigacion/   (misma estructura)
Excepciones compartidas nuevas en shared/domain/exception: BusinessException, DuplicateResourceException, ResourceNotFoundException, más GlobalExceptionHandler (@RestControllerAdvice) que ya cubre estas tres + MethodArgumentNotValidException + BadCredentialsException.

2. Modelo de dominio
GrupoInvestigacion: id, codigoGrupo, nombreGrupo, idCoordinadorActual, coordinadorNombres, coordinadorApellidos, esActivo (los dos últimos son campos de enriquecimiento, no persistidos).

Membresia: id, idGrupo, idUsuario, usuarioNombres, usuarioApellidos, usuarioCorreo, esActivo, fechaInicio, fechaFin. Método de dominio retirar() → soft delete (esActivo=false, fechaFin=now()).

LineaInvestigacion: id, nombreLinea, esActiva, fechaCreacion, fechaActualizacion. Métodos activar() / desactivar().

3. Persistencia (JPA + JdbcTemplate híbrido)
Entidad	Tabla	Notas
GrupoInvestigacionEntity	grupos_investigacion	codigo_grupo único, id_coordinador_actual sin FK declarada en la entidad
MembresiaEntity	membresias_grupo	FK lógicas a id_grupo/id_usuario, sin @ManyToOne
LineaInvestigacionEntity	lineas_investigacion	nombre_linea único
GrupoInvestigacionRepositoryAdapter y MembresiaRepositoryAdapter combinan JpaRepository con JdbcTemplate (SQL nativo) para hacer LEFT/INNER JOIN contra la tabla usuarios y traer nombre/apellido/correo del usuario relacionado.
SpringDataLineaRepository.findActivasByIdGrupo ejecuta una query nativa contra una tabla lineas_por_grupo (join grupo↔línea) que no tiene entidad JPA ni migración Flyway en todo el repo.
4. ⚠️ Hallazgo crítico: no existen migraciones Flyway para estos módulos
application.yml tiene flyway.enabled: true y jpa.hibernate.ddl-auto: none. Las migraciones existentes solo llegan a V5__update_user_email.sql (usuarios/auth). No hay ningún V6__...sql que cree grupos_investigacion, membresias_grupo, lineas_investigacion ni lineas_por_grupo. Con la config actual, estos endpoints fallarán en cualquier entorno limpio (CI, DB nueva) por tablas inexistentes. Tampoco hay seed data para los 7 grupos que exige RF-15 (GINSOFT, RESEGTI, GISI, CICO, EAP, MAP, EU) — no aparecen en ningún .sql del repo.

5. Endpoints REST
GrupoInvestigacionController — /api/v1/grupos-investigacion

Método	Ruta	Acción	HTTP éxito
POST	/	Crear grupo	201
GET	/	Listar grupos	200
GET	/{id}	Obtener grupo	200
PATCH	/{id}/coordinador	Asignar coordinador	200
POST	/{id}/miembros	Agregar miembro	201
DELETE	/{id}/miembros/{idUsuario}	Retirar miembro (soft delete)	200
GET	/{id}/miembros	Listar miembros activos	200
GET	/{id}/lineas	Listar líneas asociadas al grupo	200
LineaInvestigacionController — /api/v1/lineas-investigacion

Método	Ruta	Acción	HTTP éxito
POST	/	Registrar línea	201
GET	/?soloActivas=	Listar (todas o solo activas)	200
GET	/{id}	Obtener línea	200
PATCH	/{id}/estado	Activar/desactivar ({"esActiva": bool})	200
6. Reglas de negocio implementadas
Código de grupo único → DuplicateResourceException (409).
Nombre de línea único → DuplicateResourceException (409).
Coordinador debe ser un usuario existente y activo (valida contra tabla usuarios) → si no, BusinessException (400).
Un usuario no puede tener más de una membresía activa simultánea en ningún grupo (existsActivaByUsuario) → BusinessException con referencia explícita a RF-21.
Retiro de miembro es soft delete (conserva historial), coincide con RF-20.
Validaciones Bean Validation: codigoGrupo (@NotBlank, max 20), nombreGrupo/nombreLinea (@NotBlank, max 150), idUsuario (@NotNull).
7. ⚠️ Hallazgo: trazabilidad RF inconsistente entre código y documentación
Los comentarios Javadoc en los controladores no coinciden con la matriz de trazabilidad (docs/matriz_trazabilidad.md):

En código	En matriz de trazabilidad
RF-22 = Crear grupo	RF-22 = Ver miembros activos/coordinador
RF-23 = Listar grupos	RF-23 = Actualizar dashboard del coordinador
RF-19 = Asignar coordinador	RF-19 = Registrar membresía
RF-20 = Agregar/retirar/listar miembro	RF-20 = Retirar miembro (parcialmente correcto)
RF-26 = Listar líneas por grupo	RF-26 = Filtro GINSOFT (Computación/Ing. Software)
Solo el mensaje de error de AsignarMiembroUseCase referencia RF-21 correctamente. Además, RF-16, RF-17, RF-18 (visualizar coordinador, asignar docentes, asignar coordinador) y RF-23 (dashboard del coordinador) no tienen comentario RF asociado en el código aunque sí están cubiertos funcionalmente (RF-16/17/18) o no implementados aún (RF-23, que pertenece al módulo dashboard).

8. ⚠️ Hallazgo: sin restricción de rol en SecurityConfig
SecurityConfig.java solo restringe explícitamente /api/v1/usuarios/** y /api/v1/roles/** a ROLE_ADMIN. Los endpoints de grupos-investigacion y lineas-investigacion caen en .anyRequest().authenticated(), es decir cualquier usuario autenticado (sin importar su rol) puede crear grupos, asignar coordinadores, agregar/retirar miembros y registrar/activar líneas — aunque los requisitos (RF-15 a RF-28) indican que estas operaciones son responsabilidad del Administrador.

9. Pruebas unitarias existentes
Módulo	Con test	Sin test
Grupos — use cases	CrearGrupoUseCase, AsignarCoordinadorUseCase, AsignarMiembroUseCase, RetirarMiembroUseCase	ListarGruposUseCase, ObtenerGrupoUseCase, ListarMiembrosUseCase
Grupos — otros	GrupoInvestigacionControllerTest	GrupoInvestigacionRepositoryAdapter, MembresiaRepositoryAdapter, GrupoInvestigacionMapper
Líneas — use cases	RegistrarLineaUseCase, CambiarEstadoLineaUseCase, ListarLineasUseCase, ObtenerLineaUseCase	ListarLineasPorGrupoUseCase
Líneas — otros	LineaInvestigacionControllerTest	LineaInvestigacionRepositoryAdapter, LineaInvestigacionMapper
El commit 5c7a15a ("fix: seguridad") solo agregó @AutoConfigureMockMvc(addFilters = false) a los dos controller tests para que dejen de pasar por los filtros de seguridad reales durante el test — no es una corrección de lógica de negocio.

10. Resumen de archivos (commit 6114f92, 59 archivos, +2754 líneas)
25 archivos de producción en grupos_investigacion (DTOs, use cases, modelo, puertos, adapters, controller, mapper) + 14 en lineas_investigacion, más 4 clases de excepción/handler compartidas y 10 clases de test, sin tocar application.yml/application.properties salvo un ajuste menor.