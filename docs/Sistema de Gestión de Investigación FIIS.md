# Sistema de Gestión de Investigación FIIS
## Requisitos del Sistema y Matriz de Trazabilidad Unificada

---

## 1. Descripción General del Sistema
El **Sistema de Gestión de Investigación FIIS** tiene como propósito automatizar, controlar y dar seguimiento a los procesos académicos y administrativos relacionados con la investigación en la Facultad de Ingeniería en Informática y Sistemas. 

El sistema permite gestionar usuarios, grupos de investigación, convocatorias, proyectos de investigación, planes de tesis, informes de avance, documentos, observaciones, resoluciones, evaluaciones y trazabilidad de los trámites. Contempla la participación de distintos roles: Administrador, Estudiante/Tesista, Docente Investigador, Coordinador de Grupo de Investigación, Director de Investigación, Decano y Evaluador.

---

## 2. Actores del Sistema

| Código | Actor | Descripción |
| :--- | :--- | :--- |
| **ACT-01** | Administrador | Usuario responsable de gestionar usuarios, roles, grupos de investigación, miembros y configuración general del sistema. |
| **ACT-02** | Estudiante/Tesista | Usuario que registra, consulta y subsana planes de tesis o informes de tesis. |
| **ACT-03** | Docente Investigador | Usuario que registra proyectos de investigación, presenta documentos e informes de avance. |
| **ACT-04** | Coordinador de Grupo | Usuario responsable de revisar trámites de docentes o estudiantes pertenecientes a su grupo de investigación. |
| **ACT-05** | Director de Investigación | Usuario responsable de revisar, aprobar, observar o derivar trámites hacia el Decano. |
| **ACT-06** | Decano | Usuario responsable de emitir o registrar resoluciones asociadas a proyectos o planes de tesis. |
| **ACT-07** | Evaluador | Usuario responsable de evaluar proyectos o documentos asignados. |

---

## 3. Requisitos Funcionales (RF)

### 3.1. Gestión de Autenticación y Acceso
* **RF-01:** El sistema debe permitir el inicio de sesión mediante correo institucional y contraseña.
* **RF-02:** El sistema debe validar que el usuario se encuentre activo antes de permitir el ingreso.
* **RF-03:** El sistema debe redirigir al usuario a un dashboard según su rol.
* **RF-04:** El sistema debe restringir el acceso a módulos según el rol asignado.
* **RF-05:** El sistema debe permitir cerrar sesión de forma segura.
* **RF-06:** El sistema debe permitir obligar al usuario a cambiar su contraseña inicial cuando corresponda.

### 3.2. Gestión de Usuarios
* **RF-07:** El administrador debe poder registrar usuarios del sistema.
* **RF-08:** El administrador debe poder editar datos personales del usuario: DNI, nombres, apellidos, correo, teléfono, rol y estado.
* **RF-09:** El administrador debe poder asignar roles: ADMIN, ESTUDIANTE, DOCENTE_INVESTIGADOR, COORDINADOR_GRUPO, DIRECTOR_INVESTIGACION, DECANO y EVALUADOR.
* **RF-10:** El sistema debe generar automáticamente el correo institucional bajo el formato `primer_nombre.primer_apellido@unas.edu.pe` cuando no se ingrese un correo manualmente.
* **RF-11:** El administrador debe poder activar o desactivar usuarios.
* **RF-12:** El administrador debe poder reiniciar la contraseña de un usuario.
* **RF-13:** El sistema debe evitar que el administrador elimine su propio usuario.
* **RF-14:** El sistema debe permitir buscar usuarios por nombre, correo, DNI, rol o estado.

### 3.3. Gestión de Grupos de Investigación
* **RF-15:** El sistema debe permitir visualizar los grupos de investigación registrados: GINSOFT, RESEGTI, GISI, CICO, EAP, MAP y EU.
* **RF-16:** El administrador debe poder consultar el coordinador asignado a cada grupo de investigación.
* **RF-17:** El administrador debe poder asignar docentes investigadores a un grupo de investigación.
* **RF-18:** El administrador debe poder asignar un coordinador a cada grupo de investigación.
* **RF-19:** El sistema debe registrar la membresía activa de cada docente en la tabla de miembros del grupo.
* **RF-20:** El sistema debe permitir retirar o desactivar miembros de un grupo sin eliminar su historial.
* **RF-21:** El sistema debe impedir que un usuario tenga más de una membresía activa en distintos grupos, salvo que se establezca una regla institucional diferente.
* **RF-22:** El sistema debe mostrar en cada grupo los miembros activos y el coordinador actual.
* **RF-23:** El sistema debe actualizar automáticamente el dashboard del coordinador según el grupo al que pertenece.

### 3.4. Gestión de Líneas de Investigación
* **RF-24:** El sistema debe permitir registrar líneas de investigación asociadas a la facultad o programa académico.
* **RF-25:** El formulario de postulación debe mostrar el campo “Línea de investigación”.
* **RF-26:** Para el grupo GINSOFT, el sistema debe mostrar únicamente las líneas “Computación” e “Ingeniería de software”, según la configuración actual.
* **RF-27:** El sistema debe permitir activar o desactivar líneas de investigación.
* **RF-28:** El sistema debe evitar mostrar líneas inactivas en los formularios de registro de proyectos.

### 3.5. Gestión de Convocatorias
* **RF-29:** El sistema debe permitir registrar convocatorias de investigación.
* **RF-30:** El sistema debe permitir definir fechas de inicio y fin de postulación.
* **RF-31:** El sistema debe permitir definir si una convocatoria está abierta, cerrada o finalizada.
* **RF-32:** El docente investigador debe poder visualizar convocatorias abiertas.
* **RF-33:** El docente investigador debe poder registrar un proyecto desde una convocatoria activa.
* **RF-34:** El sistema debe impedir registrar proyectos en convocatorias cerradas.

### 3.6. Gestión de Proyectos de Investigación
* **RF-35:** El docente investigador debe poder registrar un proyecto de investigación.
* **RF-36:** El sistema debe registrar información general del proyecto: título, resumen, objetivo general, línea de investigación, presupuesto, fechas de inicio y fin, equipo de trabajo y lugar de ejecución.
* **RF-37:** El sistema debe permitir adjuntar el archivo del proyecto en formato PDF, DOC o DOCX.
* **RF-38:** El sistema debe generar automáticamente un código de proyecto.
* **RF-39:** El sistema debe vincular el proyecto al docente investigador que lo registra.
* **RF-40:** El sistema debe registrar automáticamente un trámite de postulación del proyecto.
* **RF-41:** El sistema debe enviar el proyecto al Coordinador de Grupo para revisión inicial.
* **RF-42:** El docente investigador debe poder consultar sus proyectos registrados.
* **RF-43:** El sistema debe impedir que un docente visualice proyectos que no le pertenecen.
* **RF-44:** El sistema debe mostrar el estado del proyecto: postulado, observado, aprobado, rechazado, en ejecución o finalizado.

### 3.7. Gestión de Planes de Tesis
* **RF-45:** El estudiante debe poder registrar un plan de tesis.
* **RF-46:** El sistema debe asociar el plan de tesis al estudiante solicitante.
* **RF-47:** El sistema debe enviar el plan de tesis al Coordinador de Grupo para revisión.
* **RF-48:** El Coordinador debe poder aprobar, observar o rechazar el plan de tesis.
* **RF-49:** El Director de Investigación debe poder revisar el plan de tesis derivado por el Coordinador.
* **RF-50:** Si el Director observa un plan de tesis, el sistema debe devolverlo al Coordinador de Grupo, y el Coordinador debe comunicar la observación al estudiante.
* **RF-51:** El estudiante debe poder subsanar observaciones del plan de tesis.
* **RF-52:** El Decano debe poder registrar la resolución correspondiente al plan de tesis aprobado.
* **RF-53:** El producto final asociado al tesista debe considerar el informe de tesis.

### 3.8. Gestión de Trámites y Flujo de Revisión
* **RF-54:** El sistema debe registrar trámites asociados a proyectos, planes de tesis, informes y resoluciones.
* **RF-55:** Todo trámite debe tener código, tipo, solicitante, estado actual, rol revisor y fecha de envío.
* **RF-56:** El sistema debe permitir derivar trámites entre Coordinador, Director y Decano según el flujo establecido.
* **RF-57:** El Coordinador debe poder revisar trámites pendientes de su grupo.
* **RF-58:** El Director debe poder revisar trámites pendientes de Dirección de Investigación.
* **RF-59:** El Decano debe poder revisar trámites pendientes de resolución.
* **RF-60:** El sistema debe permitir aprobar, observar o rechazar trámites.
* **RF-61:** El sistema debe registrar la última observación realizada sobre un trámite.
* **RF-62:** El sistema debe permitir levantar o subsanar observaciones.
* **RF-63:** El sistema debe actualizar el estado y rol revisor del trámite según cada acción realizada.
* **RF-64:** El sistema debe mostrar la trazabilidad completa de cada trámite.

### 3.9. Gestión de Documentos
* **RF-65:** El sistema debe permitir cargar documentos asociados a proyectos, trámites, planes de tesis e informes.
* **RF-66:** El sistema debe almacenar datos del archivo: ruta, nombre original, tipo, tamaño, usuario que subió el archivo y fecha de carga.
* **RF-67:** El sistema debe permitir visualizar y descargar documentos según permisos del usuario.
* **RF-68:** El sistema debe impedir que un usuario acceda a documentos que no le corresponden.
* **RF-69:** El sistema debe permitir adjuntar documentos de subsanación.

### 3.10. Gestión de Informes de Avance
* **RF-70:** El docente investigador debe poder registrar informes de avance de proyectos aprobados o en ejecución.
* **RF-71:** El sistema debe registrar tipo de informe, periodo, porcentaje de avance, logros, dificultades y recomendaciones.
* **RF-72:** El sistema debe permitir adjuntar un archivo de informe.
* **RF-73:** El sistema debe generar un trámite de revisión para cada informe enviado.
* **RF-74:** El Coordinador debe revisar el informe y derivarlo al Director.
* **RF-75:** El Director debe aprobar, observar o rechazar el informe.
* **RF-76:** El docente debe poder levantar observaciones del informe.
* **RF-77:** El sistema debe mostrar el historial de informes presentados por proyecto.

### 3.11. Gestión de Resoluciones
* **RF-78:** El Decano debe poder registrar resoluciones asociadas a trámites aprobados.
* **RF-79:** El sistema debe permitir registrar número, fecha, asunto y archivo de resolución.
* **RF-80:** El sistema debe asociar la resolución con el proyecto o plan de tesis correspondiente.
* **RF-81:** El sistema debe actualizar el estado del trámite a aprobado con resolución cuando corresponda.
* **RF-82:** El docente o estudiante debe poder visualizar y descargar la resolución asociada a su trámite.

### 3.12. Gestión de Evaluaciones
* **RF-83:** El sistema debe permitir asignar evaluadores a proyectos o documentos.
* **RF-84:** El evaluador debe poder visualizar proyectos asignados.
* **RF-85:** El evaluador debe poder registrar resultados de evaluación.
* **RF-86:** El sistema debe registrar observaciones, puntajes o resultados de evaluación según el diseño del módulo.
* **RF-87:** El Director debe poder consultar las evaluaciones realizadas.

### 3.13. Dashboards y Reportes
* **RF-88:** El sistema debe mostrar un dashboard general para el Administrador.
* **RF-89:** El sistema debe mostrar un dashboard institucional para el Director de Investigación.
* **RF-90:** El sistema debe mostrar un dashboard específico para cada Coordinador de Grupo.
* **RF-91:** El dashboard del Coordinador debe mostrar solo miembros, proyectos, trámites e informes de su grupo.
* **RF-92:** El sistema debe mostrar un dashboard para el Docente Investigador con sus proyectos, documentos, trámites e informes.
* **RF-93:** El sistema debe mostrar un dashboard para el Evaluador con proyectos y evaluaciones asignadas.
* **RF-94:** El sistema debe permitir generar reportes institucionales de proyectos, trámites, resoluciones e informes.
* **RF-95:** El sistema debe permitir filtrar reportes por estado, grupo, fecha, tipo de trámite, investigador o convocatoria.

### 3.14. Auditoría y Trazabilidad
* **RF-96:** El sistema debe registrar movimientos de cada trámite.
* **RF-97:** El sistema debe registrar usuario, acción, fecha, estado anterior y estado nuevo de cada movimiento.
* **RF-98:** El sistema debe registrar observaciones realizadas durante la revisión.
* **RF-99:** El sistema debe permitir consultar la trazabilidad del trámite.
* **RF-100:** El sistema debe registrar acciones sensibles como creación, edición, eliminación, aprobación, observación, rechazo y emisión de resolución.

---

## 4. Requisitos No Funcionales (RNF)

### 4.1. Seguridad
* **RNF-01:** El sistema debe implementar autenticación obligatoria para todos los módulos internos.
* **RNF-02:** El sistema debe aplicar control de acceso basado en roles.
* **RNF-03:** El sistema debe impedir que un usuario acceda a información de otro usuario sin autorización.
* **RNF-04:** Las contraseñas deben almacenarse usando hashing seguro.
* **RNF-05:** El sistema debe proteger formularios mediante tokens CSRF.
* **RNF-06:** El sistema debe validar todos los datos de entrada para prevenir inyección SQL, carga de archivos maliciosos y manipulación de parámetros.
* **RNF-07:** El sistema debe restringir los tipos de archivos permitidos a formatos definidos como PDF, DOC y DOCX.
* **RNF-08:** El sistema debe impedir acceso directo a archivos protegidos sin validación de permisos.
* **RNF-09:** El sistema debe registrar acciones críticas para auditoría.
* **RNF-10:** El sistema debe permitir desactivar usuarios que ya no deben ingresar.

### 4.2. Usabilidad
* **RNF-11:** La interfaz debe ser clara, ordenada y comprensible para usuarios administrativos, docentes y estudiantes.
* **RNF-12:** El sistema debe mostrar mensajes de éxito, error y advertencia después de cada acción relevante.
* **RNF-13:** Los formularios deben indicar los campos obligatorios.
* **RNF-14:** Los módulos deben estar organizados por rol para evitar confusión.
* **RNF-15:** El sistema debe permitir realizar buscas y filtros en listados principales.
* **RNF-16:** El diseño debe ser responsivo y poder visualizarse en computadoras, tablets y dispositivos móviles.

### 4.3. Rendimiento
* **RNF-17:** El sistema debe responder a consultas comunes en un tiempo razonable para el usuario.
* **RNF-18:** Los listados deben usar paginación para evitar sobrecarga de datos.
* **RNF-19:** Las consultas deben filtrar información según el usuario autenticado y su rol.
* **RNF-20:** El sistema debe evitar cargar documentos pesados innecesariamente en vistas principales.

### 4.4. Disponibilidad y Confiabilidad
* **RNF-21:** El sistema debe estar disponible durante el horario institucional de uso.
* **RNF-22:** El sistema debe conservar los datos de trámites, documentos y movimientos aun cuando un trámite sea observado o rechazado.
* **RNF-23:** El sistema debe evitar pérdida de datos durante operaciones críticas mediante transacciones.
* **RNF-24:** El sistema debe manejar errores internos mostrando mensajes controlados al usuario.
* **RNF-25:** El sistema debe permitir recuperación ante fallos mediante respaldos de base de datos y archivos.

### 4.5. Mantenibilidad
* **RNF-26:** El sistema debe estar organizado mediante una arquitectura modular.
* **RNF-27:** El código debe separar controladores, modelos, vistas, servicios y reglas de validación.
* **RNF-28:** Los nombres de rutas, métodos y vistas deben ser consistentes con el rol o módulo correspondiente.
* **RNF-29:** El sistema debe permitir agregar nuevos roles, estados o tipos de trámite con bajo impacto.
* **RNF-30:** El sistema debe facilitar la corrección de errores y ampliación de funcionalidades.

### 4.6. Escalabilidad
* **RNF-31:** El sistema debe permitir incorporar nuevos grupos de investigación.
* **RNF-32:** El sistema debe permitir registrar nuevas líneas de investigación.
* **RNF-33:** El sistema debe permitir agregar nuevos tipos de trámites.
* **RNF-34:** El sistema debe soportar el crecimiento progresivo de usuarios, proyectos, documentos e informes.
* **RNF-35:** El sistema debe permitir separar módulos futuros como repositorio de producción científica, indicadores o integración con sistemas externos.

### 4.7. Integridad de Datos
* **RNF-36:** El sistema debe garantizar que cada trámite tenga un solicitante válido.
* **RNF-37:** El sistema debe garantizar que cada proyecto esté asociado a un responsable o investigador.
* **RNF-38:** El sistema debe evitar duplicidad de correos institucionales.
* **RNF-39:** El sistema debe evitar inconsistencias entre usuarios, roles, grupos y membresías.
* **RNF-40:** El sistema debe registrar fechas de creación y actualización en las entidades principales.

### 4.8. Compatibilidad
* **RNF-41:** El sistema debe ejecutarse en entorno web.
* **RNF-42:** El sistema debe ser compatible con navegadores modernos como Google Chrome, Mozilla Firefox y Microsoft Edge.
* **RNF-43:** El sistema debe funcionar adecuadamente sobre la infraestructura definida para la FIIS.
* **RNF-44:** El sistema debe ser compatible con base de datos PostgreSQL.
* **RNF-45:** El sistema debe permitir despliegue en servidor Linux con Nginx y PHP-FPM.

### 4.9. Auditoría y Trazabilidad (No Funcional)
* **RNF-46:** El sistema debe conservar historial de movimientos de trámites.
* **RNF-47:** El sistema debe permitir identificar qué usuario realizó cada acción.
* **RNF-48:** El sistema debe permitir consultar fechas y estados anteriores de un trámite.
* **RNF-49:** El sistema debe conservar historial de membresías de grupos de investigación.
* **RNF-50:** El sistema debe permitir diferenciar miembros activos e inactivos.

### 4.10. Respaldo y Recuperación
* **RNF-51:** La base de datos debe ser respaldada periódicamente.
* **RNF-52:** Los archivos cargados deben respaldarse junto con la base de datos.
* **RNF-53:** El sistema debe contar con procedimientos de restauración ante fallos.
* **RNF-54:** Los respaldos deben almacenarse en una ubicación segura.

---

## 5. Reglas de Negocio Principales (RN)

| Código | Regla de Negocio |
| :--- | :--- |
| **RN-01** | Cada usuario debe tener un rol principal asignado. |
| **RN-02** | Cada docente investigador debe pertenecer a un grupo de investigación. |
| **RN-03** | Cada grupo de investigación debe tener un coordinador asignado. |
| **RN-04** | Un coordinador solo debe revisar trámites correspondientes a su grupo. |
| **RN-05** | Un docente investigador solo debe visualizar sus proyectos, documentos e informes. |
| **RN-06** | Un estudiante solo debe visualizar sus planes de tesis y trámites. |
| **RN-07** | Si el Director observa un plan de tesis, este debe retornar al Coordinador, no directamente al estudiante. |
| **RN-08** | El Coordinador es responsable de comunicar al estudiante las observaciones del Director sobre el plan de tesis. |
| **RN-09** | El Decano registra la resolución final cuando el trámite ha sido aprobado. |
| **RN-10** | Los documentos asociados a un trámite no deben eliminarse físicamente sin autorización administrativa. |
| **RN-11** | Las líneas de investigación visibles en la postulación deben estar activas. |
| **RN-12** | Para el grupo GINSOFT, las líneas visibles deben ser “Computación” e “Ingeniería de software”. |

---

## 6. Matrices de Trazabilidad de Requisitos

### 6.1. Leyenda de Prioridad
* **Alta:** Requisito indispensable para el funcionamiento del sistema.
* **Media:** Requisito importante para mejorar la gestión y control.
* **Baja:** Requisito complementario o de mejora futura.

### 6.2. Matriz de Trazabilidad de Requisitos Funcionales

| ID | Módulo | Actor Principal | Caso de Uso Asociado | Prioridad | Criterio de Prueba |
| :--- | :--- | :--- | :--- | :--- | :--- |
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
| **RF-26** | Postulación de proyecto | Docente Investigador | Filtrar líneas GINSOFT | Alta | Solo aparecen “Computación” e “Ingeniería de software”. |
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
| **RF-41** | Trámites | Sistema | Enviar a Coordinador | Alta | El trámite queda en estado PENDIENTE_COORDINADOR. |
| **RF-42** | Proyectos | Docente Investigador | Consultar mis proyectos | Alta | El docente visualiza solo sus proyectos. |
| **RF-43** | Seguridad / Proyectos | Docente Investigador | Restringir proyectos ajenos | Alta | El sistema devuelve 403 si intenta ver proyectos de otro docente. |
| **RF-44** | Proyectos | Todos según rol | Ver estado del proyecto | Media | El sistema muestra el estado actual del proyecto. |
| **RF-45** | Planes de tesis | Estudiante | Registrar plan de tesis | Alta | El estudiante registra un plan de tesis. |
| **RF-46** | Planes de tesis | Sistema | Asociar plan al estudiante | Alta | El plan queda vinculado al usuario solicitante. |
| **RF-47** | Trámites de tesis | Sistema | Enviar plan al Coordinador | Alta | El trámite queda pendiente de revisión por Coordinador. |
| **RF-48** | Trámites de tesis | Coordinador | Revisar plan de tesis | Alta | El Coordinador aprueba, observa o rechaza el plan. |
| **RF-49** | Trámites de tesis | Director | Revisar plan derivado | Alta | El Director revisa el plan enviado por el Coordinador. |
| **RF-50** | Trámites de tesis | Director / Coordinador | Devolver observación a Coordinador | Alta | La observación del Director retorna al Coordinador, no directamente al estudiante. |
| **RF-51** | Trámites de tesis | Estudiante | Subsanar observación | Alta | El estudiante adjunta o registra subsanación. |
| **RF-52** | Resoluciones | Decano | Registrar resolución de plan | Alta | El Decano registra resolución del plan aprobado. |
| **RF-53** | Tesis | Estudiante | Gestionar informe de tesis | Media | El sistema considera el informe de tesis como producto del tesista. |
| **RF-54** | Trámites | Sistema | Registrar trámite | Alta | Todo trámite posee tipo, código, solicitante, estado y revisor. |
| **RF-55** | Trámites | Sistema | Registrar datos mínimos trámite | Alta | El trámite almacena código, tipo, solicitante, estado, revisor y fecha. |
| **RF-56** | Workflow | Coordinador/Director/Decano | Derivar trámite | Alta | El trámite cambia de revisor y estado según el flujo. |
| **RF-57** | Trámites | Coordinador | Revisar trámites del grupo | Alta | El Coordinador ve solo trámites de su grupo. |
| **RF-58** | Trámites | Director | Revisar trámites de Dirección | Alta | El Director visualiza trámites pendientes de su revisión. |
| **RF-59** | Trámites | Decano | Revisar trámites para resolución | Alta | El Decano visualiza trámites pendientes de resolución. |
| **RF-60** | Trámites | Coordinador/Director/Decano | "Aprobar, observar o rechazar" | Alta | La acción actualiza el estado del trámite. |
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
| **RF-77** | Informes | Docente / Director | Ver historial de informes | Media | El sistema lista informes por proyecto. |
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
| **RF-100**| Auditoría | Sistema | Auditar acciones sensibles | Alta | Se registran creación, edición, aprobación, rechazo y resolución. |

### 6.3. Matriz de Trazabilidad de Requisitos No Funcionales

| ID | Categoría | Módulo Relacionado | Actor Impactado | Prioridad | Criterio de Prueba |
| :--- | :--- | :--- | :--- | :--- | :--- |
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

## 7. Casos de Uso Principales (CU)

| Código CU | Caso de Uso | Requisitos Relacionados | Actor Principal | Prioridad |
| :--- | :--- | :--- | :--- | :--- |
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

## 8. Planificación y Priorización de Fases de Construcción

De acuerdo al alcance funcional y la matriz de prioridades, se establece el siguiente orden secuencial para el desarrollo del sistema:

### Fase 1: Base del Sistema y Organización
* **Módulos:** Autenticación, usuarios, roles, grupos de investigación, miembros y dashboard por rol.
* **Requisitos implicados:** RF-01 a RF-23 y RF-88 a RF-93.
* **Prioridad:** Alta.

### Fase 2: Convocatorias y Postulación
* **Módulos:** Convocatorias, proyectos y gestión documental.
* **Requisitos implicados:** RF-29 a RF-44 y RF-65 a RF-69.
* **Prioridad:** Alta.

### Fase 3: Core de Flujos y Trámites
* **Módulos:** Flujo de trámites, gestión de observaciones y subsanaciones.
* **Requisitos implicados:** RF-54 a RF-64, RF-51, RF-62, RF-69 y RF-76.
* **Prioridad:** Alta.

### Fase 4: Cierre Administrativo y Seguimiento
* **Módulos:** Resoluciones e informes de avance.
* **Requisitos implicados:** RF-78 a RF-82 y RF-70 a RF-77.
* **Prioridad:** Alta.

### Fase 5: Evaluaciones y Control Final
* **Módulos:** Evaluaciones, reportes institucionales y auditoría.
* **Requisitos implicados:** RF-83 a RF-87 y RF-94 a RF-100.
* **Prioridad:** Media / Alta.

---

## 9. Criterios de Aceptación y Validación General

Para dar por aceptados los componentes del sistema, se deben cumplir las siguientes directrices generales:

### 9.1 Criterios de Aceptación (CA)
* **CA-01:** Un usuario debe poder ingresar solo si tiene credenciales válidas y estado activo.
* **CA-02:** Un usuario debe ver únicamente los módulos permitidos por su rol.
* **CA-03:** Un docente investigador debe poder registrar un proyecto y enviarlo al Coordinador.
* **CA-04:** El Coordinador debe poder aprobar, observar o rechazar trámites de su grupo.
* **CA-05:** El Director debe poder revisar trámites derivados por el Coordinador.
* **CA-06:** El Decano debe poder registrar resoluciones de trámites aprobados.
* **CA-07:** El sistema debe registrar la trazabilidad de cada trámite.
* **CA-08:** El Administrador debe poder asignar docentes y coordinadores a grupos de investigación.
* **CA-09:** El dashboard del Coordinador debe mostrar solo información de su grupo.
* **CA-10:** El sistema debe impedir que un usuario visualice información que no le pertenece.

### 9.2 Criterios de Validación (CV)
* **CV-01:** Cada rol debe visualizar únicamente los módulos autorizados.
* **CV-02:** Un docente investigador debe poder registrar proyectos y consultar solo sus propios registros.
* **CV-03:** Un coordinador debe visualizar solo proyectos, trámites y miembros de su grupo.
* **CV-04:** Un estudiante debe gestionar únicamente sus planes de tesis.
* **CV-05:** El flujo del trámite debe respetar el orden jerárquico establecido y actualizar correctamente los estados.
* **CV-06:** Toda observación debe ser registrada y visible para el solicitante en el historial del trámite.
* **CV-07:** Los documentos cargados deben mantener la integridad de sus metadatos mínimos.
* **CV-08:** Las resoluciones emitidas deben quedar vinculadas unívocamente al trámite correspondiente.