# Matriz de Trazabilidad de Requisitos
## Sistema de Gestión de Investigación FIIS

---

## 1. Leyenda de prioridad

| Prioridad | Significado |
|---|---|
| **Alta** | Requisito indispensable para el funcionamiento del sistema. |
| **Media** | Requisito importante para mejorar la gestión y control. |
| **Baja** | Requisito complementario o de mejora futura. |

---

## 2. Matriz de trazabilidad de requisitos funcionales

| ID | Módulo | Actor principal | Caso de uso asociado | Prioridad | Criterio de prueba |
|---|---|---|---|---|---|
| **RF-01** | Autenticación | Todos los usuarios | Iniciar sesión | Alta | El usuario ingresa correo y contraseña válidos y accede al sistema. |
| **RF-02** | Autenticación | Todos los usuarios | Validar estado de usuario | Alta | Un usuario inactivo no debe poder ingresar al sistema. |
| **RF-03** | Dashboard | Todos los usuarios | Redireccionar por rol | Alta | Al iniciar sesión, cada rol visualiza su dashboard correspondiente. |
| **RF-04** | Seguridad / Roles | Todos los usuarios | Controlar acceso por rol | Alta | Un usuario no puede ingresar a módulos no autorizados. |
| **RF-05** | Autenticación | Todos los usuarios | Cerrar sesión | Alta | El usuario cierra sesión y no puede volver sin autenticarse. |
| **RF-06** | Seguridad | Todos los usuarios | Cambiar contraseña inicial | Media | Si `must_change_password` está activo, el sistema solicita cambio de contraseña. |
| **RF-07** | Gestión de usuarios | Administrador | Registrar usuario | Alta | El administrador registra un usuario nuevo correctamente. |
| **RF-08** | Gestión de usuarios | Administrador | Editar usuario | Alta | El administrador actualiza datos personales del usuario. |
| **RF-09** | Gestión de usuarios | Administrador | Asignar rol | Alta | El usuario queda registrado con el rol seleccionado. |
| **RF-10** | Gestión de usuarios | Administrador | Generar correo institucional | Media | Si el correo está vacío, se genera con formato `nombre.apellido@unas.edu.pe`. |
| **RF-11** | Gestión de usuarios | Administrador | Activar/desactivar usuario | Alta | Un usuario cambia entre estado ACTIVO e INACTIVO. |
| **RF-12** | Gestión de usuarios | Administrador | Reiniciar contraseña | Media | El sistema genera o restablece la contraseña inicial del usuario. |
| **RF-13** | Gestión de usuarios | Administrador | Proteger usuario propio | Alta | El administrador no puede eliminar su propia cuenta. |
| **RF-14** | Gestión de usuarios | Administrador | Buscar usuarios | Media | El sistema filtra usuarios por nombre, correo, DNI, rol o estado. |
| **RF-15** | Grupos de investigación | Administrador | Listar grupos | Alta | Se visualizan GINSOFT, RESEGTI, GISI, CICO, EAP, MAP y EU. |
| **RF-16** | Grupos de investigación | Administrador | Consultar coordinador | Alta | Cada grupo muestra su coordinador actual o indica que no tiene. |
| **RF-17** | Grupos de investigación | Administrador | Asignar docente a grupo | Alta | Un docente investigador queda registrado como miembro activo del grupo. |
| **RF-18** | Grupos de investigación | Administrador | Asignar coordinador de grupo | Alta | El coordinador queda registrado en `research_groups.coordinator_user_id`. |
| **RF-19** | Grupos de investigación | Administrador | Registrar membresía | Alta | La membresía se guarda en `research_group_members`. |
| **RF-20** | Grupos de investigación | Administrador | Retirar miembro | Media | El miembro queda inactivo sin eliminar su historial. |
| **RF-21** | Grupos de investigación | Administrador | Validar membresía única | Alta | Un usuario no debe tener más de un grupo activo. |
| **RF-22** | Grupos de investigación | Administrador | Ver miembros activos | Alta | La vista del grupo muestra sus miembros activos. |
| **RF-23** | Dashboard coordinador | Coordinador de Grupo | Actualizar dashboard por grupo | Alta | El coordinador visualiza solo datos de su grupo. |
| **RF-24** | Líneas de investigación | Administrador | Registrar línea | Media | Se registra una línea de investigación activa. |
| **RF-25** | Postulación de proyecto | Docente Investigador | Seleccionar línea | Alta | El formulario muestra el campo “Línea de investigación”. |
| **RF-26** | Postulación de proyecto | Docente Investigador | Filtrar líneas GINSOFT | Alta | Solo aparecen “Computacion” e “Ingenieria de software”. |
| **RF-27** | Líneas de investigación | Administrador | Activar/desactivar línea | Media | Una línea inactiva no debe mostrarse en formularios. |
| **RF-28** | Postulación de proyecto | Docente Investigador | Ocultar líneas inactivas | Alta | El selector muestra únicamente líneas activas. |
| **RF-29** | Convocatorias | Director / Administrador | Registrar convocatoria | Alta | Se registra una convocatoria de investigación. |
| **RF-30** | Convocatorias | Director / Administrador | Definir periodo de postulación | Alta | La convocatoria almacena fechas de inicio y fin. |
| **RF-31** | Convocatorias | Director / Administrador | Cambiar estado de convocatoria | Alta | La convocatoria puede estar ABIERTA, CERRADA o FINALIZADA. |
| **RF-32** | Convocatorias | Docente Investigador | Ver convocatorias abiertas | Alta | El docente visualiza solo convocatorias vigentes. |
| **RF-33** | Proyectos | Docente Investigador | Crear proyecto desde convocatoria | Alta | El docente registra un proyecto desde una convocatoria activa. |
| **RF-34** | Convocatorias | Docente Investigador | Bloquear convocatoria cerrada | Alta | El sistema impide registrar proyectos en convocatorias cerradas. |
| **RF-35** | Proyectos | Docente Investigador | Registrar proyecto | Alta | El docente registra un nuevo proyecto de investigación. |
| **RF-36** | Proyectos | Docente Investigador | Completar datos del proyecto | Alta | El sistema guarda título, objetivo, línea, presupuesto y fechas. |
| **RF-37** | Documentos | Docente Investigador | Adjuntar archivo de proyecto | Alta | El sistema acepta PDF, DOC o DOCX. |
| **RF-38** | Proyectos | Sistema | Generar código de proyecto | Media | El proyecto recibe un código automático. |
| **RF-39** | Proyectos | Sistema | Vincular proyecto a investigador | Alta | El proyecto se asocia al investigador autenticado. |
| **RF-40** | Trámites | Sistema | Crear trámite de postulación | Alta | Al registrar el proyecto, se crea el trámite correspondiente. |
| **RF-41** | Trámites | Sistema | Enviar a Coordinador | Alta | El trámite queda en estado `PENDIENTE_COORDINADOR`. |
| **RF-42** | Proyectos | Docente Investigador | Consultar mis proyectos | Alta | El docente visualiza solo sus proyectos. |
| **RF-43** | Seguridad / Proyectos | Docente Investigador | Restringir proyectos ajenos | Alta | El sistema devuelve 403 si intenta ver proyectos de otro docente. |
| **RF-44** | Proyectos | Todos según rol | Ver estado del proyecto | Media | El sistema muestra el estado actual del proyecto. |
| **RF-45** | Planes de tesis | Estudiante | Registrar plan de tesis | Alta | El estudiante registra un plan de tesis. |
| **RF-46** | Planes de tesis | Sistema | Asociar plan al estudiante | Alta | El plan queda vinculado al usuario solicitante. |
| **RF-47** | Trámites de tesis | Sistema | Enviar plan al Coordinador | Alta | El trámite queda pendiente de revisión por Coordinador. |
| **RF-48** | Trámites de tesis | Coordinador | Revisar plan de tesis | Alta | El Coordinador aprueba, observa o rechaza el plan. |
| **RF-49** | Trámites de tesis | Director | Revisar plan derivado | Alta | El Director revisa el plan enviado por el Coordinador. |
| **RF-50** | Trámites de tesis | Director / Coordinador | Devolver observación al Coordinador | Alta | La observación del Director retorna al Coordinador, no directamente al estudiante. |
| **RF-51** | Trámites de tesis | Estudiante | Subsanar observación | Alta | El estudiante adjunta o registra subsanación. |
| **RF-52** | Resoluciones | Decano | Registrar resolución de plan | Alta | El Decano registra resolución del plan aprobado. |
| **RF-53** | Tesis | Estudiante | Gestionar informe de tesis | Media | El sistema considera el informe de tesis como producto del tesista. |
| **RF-54** | Trámites | Sistema | Registrar trámite | Alta | Todo trámite posee tipo, código, solicitante, estado y revisor. |
| **RF-55** | Trámites | Sistema | Registrar datos mínimos del trámite | Alta | El trámite almacena código, tipo, solicitante, estado, revisor y fecha. |
| **RF-56** | Workflow | Coordinador / Director / Decano | Derivar trámite | Alta | El trámite cambia de revisor y estado según el flujo. |
| **RF-57** | Trámites | Coordinador | Revisar trámites del grupo | Alta | El Coordinador ve solo trámites de su grupo. |
| **RF-58** | Trámites | Director | Revisar trámites de Dirección | Alta | El Director visualiza trámites pendientes de su revisión. |
| **RF-59** | Trámites | Decano | Revisar trámites para resolución | Alta | El Decano visualiza trámites pendientes de resolución. |
| **RF-60** | Trámites | Coordinador / Director / Decano | Aprobar, observar o rechazar | Alta | La acción actualiza el estado del trámite. |
| **RF-61** | Trámites | Revisores | Registrar observación | Alta | La última observación queda almacenada. |
| **RF-62** | Trámites | Solicitante | Levantar observación | Alta | El solicitante subsana y reenvía el trámite. |
| **RF-63** | Workflow | Sistema | Actualizar estado y revisor | Alta | Cada acción actualiza estado y rol revisor. |
| **RF-64** | Trazabilidad | Todos según permiso | Ver trazabilidad | Alta | Se visualizan los movimientos del trámite. |
| **RF-65** | Documentos | Todos según rol | Cargar documentos | Alta | El usuario carga documentos asociados a trámites o proyectos. |
| **RF-66** | Documentos | Sistema | Registrar metadatos de archivo | Alta | Se almacena ruta, nombre, tipo, tamaño, usuario y fecha. |
| **RF-67** | Documentos | Todos según permiso | Ver o descargar documentos | Alta | Solo usuarios autorizados pueden descargar documentos. |
| **RF-68** | Seguridad documental | Todos según rol | Restringir documentos ajenos | Alta | El sistema bloquea descargas sin permiso. |
| **RF-69** | Documentos | Solicitante | Adjuntar subsanación | Alta | El usuario adjunta archivo de subsanación. |
| **RF-70** | Informes | Docente Investigador | Registrar informe de avance | Alta | El docente registra informe de proyecto aprobado o en ejecución. |
| **RF-71** | Informes | Docente Investigador | Completar datos de informe | Alta | Se guarda periodo, avance, logros, dificultades y recomendaciones. |
| **RF-72** | Informes | Docente Investigador | Adjuntar archivo de informe | Alta | El informe acepta archivo PDF, DOC o DOCX. |
| **RF-73** | Trámites de informes | Sistema | Crear trámite de informe | Alta | Cada informe genera trámite de revisión. |
| **RF-74** | Trámites de informes | Coordinador | Derivar informe al Director | Alta | El informe pasa de Coordinador a Director. |
| **RF-75** | Trámites de informes | Director | Revisar informe | Alta | El Director aprueba, observa o rechaza informe. |
| **RF-76** | Informes | Docente Investigador | Levantar observación de informe | Alta | El docente reenvía informe subsanado. |
| **RF-77** | Informes | Docente Investigador / Director | Ver historial de informes | Media | El sistema lista informes por proyecto. |
| **RF-78** | Resoluciones | Decano | Registrar resolución | Alta | El Decano registra resolución asociada a trámite aprobado. |
| **RF-79** | Resoluciones | Decano | Adjuntar archivo de resolución | Alta | Se registra número, fecha, asunto y archivo. |
| **RF-80** | Resoluciones | Sistema | Asociar resolución | Alta | La resolución se vincula al proyecto o plan de tesis. |
| **RF-81** | Resoluciones / Workflow | Sistema | Actualizar estado con resolución | Alta | El trámite cambia a aprobado con resolución. |
| **RF-82** | Resoluciones | Docente / Estudiante | Descargar resolución | Media | El solicitante visualiza o descarga su resolución. |
| **RF-83** | Evaluaciones | Director | Asignar evaluadores | Media | El Director asigna evaluadores a proyectos o documentos. |
| **RF-84** | Evaluaciones | Evaluador | Ver proyectos asignados | Media | El evaluador visualiza solo proyectos asignados. |
| **RF-85** | Evaluaciones | Evaluador | Registrar evaluación | Media | El evaluador registra resultado de evaluación. |
| **RF-86** | Evaluaciones | Evaluador | Registrar observaciones o puntajes | Media | La evaluación almacena resultado y observaciones. |
| **RF-87** | Evaluaciones | Director | Consultar evaluaciones | Media | El Director revisa evaluaciones registradas. |
| **RF-88** | Dashboard | Administrador | Ver dashboard general | Alta | El administrador ve indicadores institucionales. |
| **RF-89** | Dashboard | Director | Ver dashboard institucional | Alta | El Director ve proyectos, grupos, trámites y convocatorias. |
| **RF-90** | Dashboard | Coordinador | Ver dashboard de grupo | Alta | El Coordinador visualiza solo su grupo. |
| **RF-91** | Dashboard | Coordinador | Filtrar dashboard por grupo | Alta | El dashboard no muestra datos de otros grupos. |
| **RF-92** | Dashboard | Docente Investigador | Ver dashboard docente | Alta | El docente ve sus proyectos, documentos, trámites e informes. |
| **RF-93** | Dashboard | Evaluador | Ver dashboard evaluador | Media | El evaluador ve proyectos y evaluaciones asignadas. |
| **RF-94** | Reportes | Director / Administrador | Generar reportes institucionales | Media | El sistema genera reportes de proyectos, trámites e informes. |
| **RF-95** | Reportes | Director / Administrador | Filtrar reportes | Media | Los reportes se filtran por grupo, estado, fecha o investigador. |
| **RF-96** | Auditoría | Sistema | Registrar movimientos | Alta | Cada movimiento de trámite queda registrado. |
| **RF-97** | Auditoría | Sistema | Registrar usuario y acción | Alta | Se registra usuario, acción, fecha y cambio de estado. |
| **RF-98** | Auditoría | Sistema | Registrar observaciones | Alta | Las observaciones quedan asociadas al movimiento. |
| **RF-99** | Auditoría | Usuarios autorizados | Consultar trazabilidad | Alta | Se visualiza el historial completo del trámite. |
| **RF-100** | Auditoría | Sistema | Auditar acciones sensibles | Alta | Se registran creación, edición, aprobación, rechazo y resolución. |

---

## 3. Matriz de trazabilidad de requisitos no funcionales

| ID | Categoría | Módulo relacionado | Actor impactado | Prioridad | Criterio de prueba |
|---|---|---|---|---|---|
| **RNF-01** | Seguridad | Autenticación | Todos | Alta | Ningún módulo interno debe accederse sin iniciar sesión. |
| **RNF-02** | Seguridad | Roles y permisos | Todos | Alta | Un usuario no puede acceder a rutas fuera de su rol. |
| **RNF-03** | Seguridad | Proyectos / Documentos | Todos | Alta | El sistema bloquea información ajena al usuario. |
| **RNF-04** | Seguridad | Usuarios | Todos | Alta | Las contraseñas deben almacenarse hasheadas. |
| **RNF-05** | Seguridad | Formularios | Todos | Alta | Los formularios POST deben usar token CSRF. |
| **RNF-06** | Seguridad | Formularios / BD | Todos | Alta | Las entradas deben validarse antes de guardar. |
| **RNF-07** | Seguridad | Documentos | Todos | Alta | Solo se aceptan archivos permitidos por extensión y tamaño. |
| **RNF-08** | Seguridad | Documentos | Todos | Alta | No se debe descargar un archivo sin validar permisos. |
| **RNF-09** | Seguridad / Auditoría | Trámites | Sistema | Alta | Las acciones críticas quedan registradas. |
| **RNF-10** | Seguridad | Gestión de usuarios | Administrador | Alta | Un usuario inactivo no puede acceder al sistema. |
| **RNF-11** | Usabilidad | Interfaz | Todos | Media | Las pantallas deben mostrar información clara. |
| **RNF-12** | Usabilidad | Formularios | Todos | Media | El sistema muestra mensajes de éxito, error o advertencia. |
| **RNF-13** | Usabilidad | Formularios | Todos | Media | Los campos obligatorios deben identificarse. |
| **RNF-14** | Usabilidad | Menú por rol | Todos | Alta | Cada rol visualiza solo opciones relevantes. |
| **RNF-15** | Usabilidad | Listados | Todos | Media | Los listados deben permitir búsqueda o filtros. |
| **RNF-16** | Usabilidad | Interfaz web | Todos | Media | La interfaz debe ser responsiva. |
| **RNF-17** | Rendimiento | Consultas | Todos | Media | Las consultas frecuentes deben responder en tiempo razonable. |
| **RNF-18** | Rendimiento | Listados | Todos | Media | Los listados deben estar paginados. |
| **RNF-19** | Rendimiento / Seguridad | Consultas por rol | Todos | Alta | Las consultas deben filtrar por usuario, rol o grupo. |
| **RNF-20** | Rendimiento | Documentos | Todos | Media | No se deben cargar archivos pesados en vistas principales. |
| **RNF-21** | Disponibilidad | Sistema | Todos | Media | El sistema debe estar disponible en horario institucional. |
| **RNF-22** | Confiabilidad | Trámites | Todos | Alta | La información se conserva aunque el trámite sea observado o rechazado. |
| **RNF-23** | Confiabilidad | Operaciones críticas | Sistema | Alta | Las operaciones críticas deben ejecutarse con transacciones. |
| **RNF-24** | Confiabilidad | Manejo de errores | Todos | Media | Los errores deben mostrarse de forma controlada. |
| **RNF-25** | Recuperación | Base de datos / Archivos | Administrador | Alta | Debe existir procedimiento de respaldo y restauración. |
| **RNF-26** | Mantenibilidad | Arquitectura | Desarrollador | Media | El sistema debe estar organizado modularmente. |
| **RNF-27** | Mantenibilidad | Código fuente | Desarrollador | Media | Se deben separar controladores, modelos, vistas y servicios. |
| **RNF-28** | Mantenibilidad | Rutas y módulos | Desarrollador | Media | La nomenclatura debe ser consistente. |
| **RNF-29** | Mantenibilidad | Configuración | Administrador / Desarrollador | Media | El sistema debe permitir agregar roles o estados con bajo impacto. |
| **RNF-30** | Mantenibilidad | Código fuente | Desarrollador | Media | El código debe facilitar corrección y ampliación. |
| **RNF-31** | Escalabilidad | Grupos | Administrador | Media | El sistema debe permitir agregar nuevos grupos. |
| **RNF-32** | Escalabilidad | Líneas de investigación | Administrador | Media | El sistema debe permitir agregar nuevas líneas. |
| **RNF-33** | Escalabilidad | Trámites | Administrador / Director | Media | El sistema debe permitir nuevos tipos de trámite. |
| **RNF-34** | Escalabilidad | Base de datos | Todos | Media | El sistema debe soportar crecimiento de usuarios y documentos. |
| **RNF-35** | Escalabilidad | Módulos futuros | Administrador | Baja | Debe permitir módulos futuros de indicadores o producción científica. |
| **RNF-36** | Integridad | Trámites | Sistema | Alta | Todo trámite debe tener solicitante válido. |
| **RNF-37** | Integridad | Proyectos | Sistema | Alta | Todo proyecto debe tener responsable o investigador asociado. |
| **RNF-38** | Integridad | Usuarios | Administrador | Alta | No deben existir correos duplicados. |
| **RNF-39** | Integridad | Roles / Grupos | Administrador | Alta | No debe haber inconsistencias entre usuarios, roles y grupos. |
| **RNF-40** | Integridad | Base de datos | Sistema | Media | Las entidades principales deben registrar fechas de creación y actualización. |
| **RNF-41** | Compatibilidad | Plataforma web | Todos | Alta | El sistema debe ejecutarse vía navegador web. |
| **RNF-42** | Compatibilidad | Navegadores | Todos | Media | Debe funcionar en Chrome, Firefox y Edge. |
| **RNF-43** | Compatibilidad | Infraestructura FIIS | Administrador | Media | Debe desplegarse en la infraestructura definida. |
| **RNF-44** | Compatibilidad | Base de datos | Desarrollador | Alta | El sistema debe funcionar con PostgreSQL. |
| **RNF-45** | Compatibilidad | Servidor | Administrador | Alta | Debe ejecutarse en Linux, Nginx y PHP-FPM. |
| **RNF-46** | Auditoría | Trámites | Sistema | Alta | El sistema conserva historial de movimientos. |
| **RNF-47** | Auditoría | Usuarios | Sistema | Alta | Debe identificarse qué usuario realizó cada acción. |
| **RNF-48** | Auditoría | Trámites | Usuarios autorizados | Alta | Deben consultarse estados anteriores y fechas. |
| **RNF-49** | Auditoría | Grupos | Administrador | Media | Debe conservarse historial de membresías. |
| **RNF-50** | Auditoría | Grupos | Administrador | Media | Debe diferenciar miembros activos e inactivos. |
| **RNF-51** | Respaldo | Base de datos | Administrador | Alta | Deben existir respaldos periódicos. |
| **RNF-52** | Respaldo | Archivos | Administrador | Alta | Los archivos cargados deben respaldarse. |
| **RNF-53** | Recuperación | Sistema | Administrador | Alta | Debe existir procedimiento de restauración. |
| **RNF-54** | Seguridad de respaldo | Respaldos | Administrador | Alta | Los respaldos deben almacenarse en ubicación segura. |

---

## 4. Matriz de trazabilidad por casos de uso principales

| Código CU | Caso de uso | Requisitos relacionados | Actor principal | Prioridad |
|---|---|---|---|---|
| **CU-01** | Iniciar sesión | RF-01, RF-02, RF-03, RNF-01, RNF-04 | Todos | Alta |
| **CU-02** | Gestionar usuarios | RF-07 a RF-14, RNF-10, RNF-38 | Administrador | Alta |
| **CU-03** | Gestionar grupos de investigación | RF-15 a RF-23, RNF-31, RNF-49, RNF-50 | Administrador | Alta |
| **CU-04** | Gestionar líneas de investigación | RF-24 a RF-28, RNF-32 | Administrador | Media |
| **CU-05** | Gestionar convocatorias | RF-29 a RF-34 | Director / Administrador | Alta |
| **CU-06** | Registrar proyecto de investigación | RF-35 a RF-44, RNF-36, RNF-37 | Docente Investigador | Alta |
| **CU-07** | Revisar proyecto por Coordinador | RF-54 a RF-64 | Coordinador | Alta |
| **CU-08** | Revisar proyecto por Director | RF-56, RF-58, RF-60, RF-63, RF-64 | Director | Alta |
| **CU-09** | Registrar resolución | RF-78 a RF-82 | Decano | Alta |
| **CU-10** | Registrar plan de tesis | RF-45 a RF-53 | Estudiante | Alta |
| **CU-11** | Subsanar observaciones | RF-51, RF-62, RF-69, RF-76 | Estudiante / Docente | Alta |
| **CU-12** | Registrar informe de avance | RF-70 a RF-77 | Docente Investigador | Alta |
| **CU-13** | Gestionar documentos | RF-65 a RF-69, RNF-07, RNF-08 | Todos según rol | Alta |
| **CU-14** | Gestionar evaluaciones | RF-83 a RF-87 | Director / Evaluador | Media |
| **CU-15** | Consultar dashboards | RF-88 a RF-93 | Todos según rol | Alta |
| **CU-16** | Generar reportes | RF-94, RF-95 | Director / Administrador | Media |
| **CU-17** | Consultar auditoría y trazabilidad | RF-96 a RF-100, RNF-46 a RNF-50 | Director / Administrador | Alta |

---

## 5. Priorización para la siguiente fase de construcción

| Fase | Módulo | Requisitos principales | Prioridad |
|---|---|---|---|
| **Fase 1** | Autenticación, usuarios y roles | RF-01 a RF-14 | Alta |
| **Fase 1** | Grupos de investigación y miembros | RF-15 a RF-23 | Alta |
| **Fase 1** | Dashboard por rol | RF-88 a RF-93 | Alta |
| **Fase 2** | Convocatorias y proyectos | RF-29 a RF-44 | Alta |
| **Fase 2** | Gestión documental | RF-65 a RF-69 | Alta |
| **Fase 3** | Flujo de trámites | RF-54 a RF-64 | Alta |
| **Fase 3** | Observaciones y subsanaciones | RF-51, RF-62, RF-69, RF-76 | Alta |
| **Fase 4** | Resoluciones | RF-78 a RF-82 | Alta |
| **Fase 4** | Informes de avance | RF-70 a RF-77 | Alta |
| **Fase 5** | Evaluaciones | RF-83 a RF-87 | Media |
| **Fase 5** | Reportes y auditoría | RF-94 a RF-100 | Media/Alta |

---

## 6. Criterios generales de validación del sistema

| Código | Criterio de validación |
|---|---|
| **CV-01** | Cada rol debe visualizar únicamente los módulos autorizados. |
| **CV-02** | Un docente investigador debe poder registrar proyectos y consultar solo sus propios registros. |
| **CV-03** | Un coordinador debe visualizar solo proyectos, trámites y miembros de su grupo. |
| **CV-04** | Un estudiante debe gestionar únicamente sus planes de tesis. |
| **CV-05** | El flujo Coordinador → Director → Decano debe actualizar correctamente los estados. |
| **CV-06** | Toda observación debe quedar registrada y visible en la trazabilidad. |
| **CV-07** | Todo documento cargado debe conservar su nombre, ruta, usuario y fecha. |
| **CV-08** | Toda resolución debe quedar vinculada al trámite correspondiente. |
| **CV-09** | Los dashboards deben mostrar datos filtrados según el rol. |
| **CV-10** | El sistema debe impedir accesos no autorizados mediante rutas directas. |
