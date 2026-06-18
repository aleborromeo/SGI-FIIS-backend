# Requisitos Funcionales y No Funcionales del Sistema de Gestión de Investigación FIIS

## 1. Descripción general del sistema
El Sistema de Gestión de Investigación FIIS tiene como propósito automatizar, controlar y dar seguimiento a los procesos académicos y administrativos relacionados con la investigación en la Facultad de Ingeniería en Informática y Sistemas. El sistema permite gestionar usuarios, grupos de investigación, convocatorias, proyectos de investigación, planes de tesis, informes de avance, documentos, observaciones, resoluciones, evaluaciones y trazabilidad de los trámites.

El sistema contempla la participación de distintos roles: Administrador, Estudiante/Tesista, Docente Investigador, Coordinador de Grupo de Investigación, Director de Investigación, Decano y Evaluador.

---

## 2. Actores del sistema

| Código | Actor | Descripción |
|---|---|---|
| **ACT-01** | Administrador | Usuario responsable de gestionar usuarios, roles, grupos de investigación, miembros y configuración general del sistema. |
| **ACT-02** | Estudiante/Tesista | Usuario que registra, consulta y subsana planes de tesis o informes de tesis. |
| **ACT-03** | Docente Investigador | Usuario que registra proyectos de investigación, presenta documentos e informes de avance. |
| **ACT-04** | Coordinador de Grupo | Usuario responsable de revisar trámites de docentes o estudiantes pertenecientes a su grupo de investigación. |
| **ACT-05** | Director de Investigación | Usuario responsable de revisar, aprobar, observar o derivar trámites hacia el Decano. |
| **ACT-06** | Decano | Usuario responsable de emitir o registrar resoluciones asociadas a proyectos o planes de tesis. |
| **ACT-07** | Evaluador | Usuario responsable de evaluar proyectos o documentos asignados. |

---

## 3. Requisitos funcionales

### 3.1. Gestión de autenticación y acceso
| Código | Requisito funcional |
|---|---|
| **RF-01** | El sistema debe permitir el inicio de sesión mediante correo institucional y contraseña. |
| **RF-02** | El sistema debe validar que el usuario se encuentre activo antes de permitir el ingreso. |
| **RF-03** | El sistema debe redirigir al usuario a un dashboard según su rol. |
| **RF-04** | El sistema debe restringir el acceso a módulos según el rol asignado. |
| **RF-05** | El sistema debe permitir cerrar sesión de forma segura. |
| **RF-06** | El sistema debe permitir obligar al usuario a cambiar su contraseña inicial cuando corresponda. |

### 3.2. Gestión de usuarios
| Código | Requisito funcional |
|---|---|
| **RF-07** | El administrador debe poder registrar usuarios del sistema. |
| **RF-08** | El administrador debe poder editar datos personales del usuario: DNI, nombres, apellidos, correo, teléfono, rol y estado. |
| **RF-09** | El administrador debe poder asignar roles: ADMIN, ESTUDIANTE, DOCENTE_INVESTIGADOR, COORDINADOR_GRUPO, DIRECTOR_INVESTIGACION, DECANO y EVALUADOR. |
| **RF-10** | El sistema debe generar automáticamente el correo institucional bajo el formato primer_nombre.primer_apellido@unas.edu.pe cuando no se ingrese un correo manualmente. |
| **RF-11** | El administrador debe poder activar o desactivar usuarios. |
| **RF-12** | El administrador debe poder reiniciar la contraseña de un usuario. |
| **RF-13** | El sistema debe evitar que el administrador elimine su propio usuario. |
| **RF-14** | El sistema debe permitir buscar usuarios por nombre, correo, DNI, rol o estado. |

### 3.3. Gestión de grupos de investigación
| Código | Requisito funcional |
|---|---|
| **RF-15** | El sistema debe permitir visualizar los grupos de investigación registrados: GINSOFT, RESEGTI, GISI, CICO, EAP, MAP y EU. |
| **RF-16** | El administrador debe poder consultar el coordinador asignado a cada grupo de investigación. |
| **RF-17** | El administrador debe poder asignar docentes investigadores a un grupo de investigación. |
| **RF-18** | El administrador debe poder asignar un coordinador a cada grupo de investigación. |
| **RF-19** | El sistema debe registrar la membresía activa de cada docente en la tabla de miembros del grupo. |
| **RF-20** | El sistema debe permitir retirar o desactivar miembros de un grupo sin eliminar su historial. |
| **RF-21** | El sistema debe impedir que un usuario tenga más de una membresía activa en distintos grupos, salvo que se establezca una regla institucional diferente. |
| **RF-22** | El sistema debe mostrar en cada grupo los miembros activos y el coordinador actual. |
| **RF-23** | El sistema debe actualizar automáticamente el dashboard del coordinador según el grupo al que pertenece. |

### 3.4. Gestión de líneas de investigación
| Código | Requisito funcional |
|---|---|
| **RF-24** | El sistema debe permitir registrar líneas de investigación asociadas a la facultad o programa académico. |
| **RF-25** | El formulario de postulación debe mostrar el campo “Línea de investigación”. |
| **RF-26** | Para el grupo GINSOFT, el sistema debe mostrar únicamente las líneas “Computacion” e “Ingenieria de software”, según la configuración actual. |
| **RF-27** | El sistema debe permitir activar o desactivar líneas de investigación. |
| **RF-28** | El sistema debe evitar mostrar líneas inactivas en los formularios de registro de proyectos. |

### 3.5. Gestión de convocatorias
| Código | Requisito funcional |
|---|---|
| **RF-29** | El sistema debe permitir registrar convocatorias de investigación. |
| **RF-30** | El sistema debe permitir definir fechas de inicio y fin de postulación. |
| **RF-31** | El sistema debe permitir definir si una convocatoria está abierta, cerrada o finalizada. |
| **RF-32** | El docente investigador debe poder visualizar convocatorias abiertas. |
| **RF-33** | El docente investigador debe poder registrar un proyecto desde una convocatoria activa. |
| **RF-34** | El sistema debe impedir registrar proyectos en convocatorias cerradas. |

### 3.6. Gestión de proyectos de investigación
| Código | Requisito funcional |
|---|---|
| **RF-35** | El docente investigador debe poder registrar un proyecto de investigación. |
| **RF-36** | El sistema debe registrar información general del proyecto: título, resumen, objetivo general, línea de investigación, presupuesto, fechas de inicio y fin, equipo de trabajo y lugar de ejecución. |
| **RF-37** | El sistema debe permitir adjuntar el archivo del proyecto en formato PDF, DOC o DOCX. |
| **RF-38** | El sistema debe generar automáticamente un código de proyecto. |
| **RF-39** | El sistema debe vincular el proyecto al docente investigador que lo registra. |
| **RF-40** | El sistema debe registrar automáticamente un trámite de postulación del proyecto. |
| **RF-41** | El sistema debe enviar el proyecto al Coordinador de Grupo para revisión inicial. |
| **RF-42** | El docente investigador debe poder consultar sus proyectos registrados. |
| **RF-43** | El sistema debe impedir que un docente visualice proyectos que no le pertenecen. |
| **RF-44** | El sistema debe mostrar el estado del proyecto: postulado, observado, aprobado, rechazado, en ejecución o finalizado. |

### 3.7. Gestión de planes de tesis
| Código | Requisito funcional |
|---|---|
| **RF-45** | El estudiante debe poder registrar un plan de tesis. |
| **RF-46** | El sistema debe asociar el plan de tesis al estudiante solicitante. |
| **RF-47** | El sistema debe enviar el plan de tesis al Coordinador de Grupo para revisión. |
| **RF-48** | El Coordinador debe poder aprobar, observar o rechazar el plan de tesis. |
| **RF-49** | El Director de Investigación debe poder revisar el plan de tesis derivado por el Coordinador. |
| **RF-50** | Si el Director observa un plan de tesis, el sistema debe devolverlo al Coordinador de Grupo, y el Coordinador debe comunicar la observación al estudiante. |
| **RF-51** | El estudiante debe poder subsanar observaciones del plan de tesis. |
| **RF-52** | El Decano debe poder registrar la resolución correspondiente al plan de tesis aprobado. |
| **RF-53** | El producto final asociado al tesista debe considerar el informe de tesis. |

### 3.8. Gestión de trámites y flujo de revisión
| Código | Requisito funcional |
|---|---|
| **RF-54** | El sistema debe registrar trámites asociados a proyectos, planes de tesis, informes y resoluciones. |
| **RF-55** | Todo trámite debe tener código, tipo, solicitante, estado actual, rol revisor y fecha de envío. |
| **RF-56** | El sistema debe permitir derivar trámites entre Coordinador, Director y Decano según el flujo establecido. |
| **RF-57** | El Coordinador debe poder revisar trámites pendientes de su grupo. |
| **RF-58** | El Director debe poder revisar trámites pendientes de Dirección de Investigación. |
| **RF-59** | El Decano debe poder revisar trámites pendientes de resolución. |
| **RF-60** | El sistema debe permitir aprobar, observar o rechazar trámites. |
| **RF-61** | El sistema debe registrar la última observación realizada sobre un trámite. |
| **RF-62** | El sistema debe permitir levantar o subsanar observaciones. |
| **RF-63** | El sistema debe actualizar el estado y rol revisor del trámite según cada acción realizada. |
| **RF-64** | El sistema debe mostrar la trazabilidad completa de cada trámite. |

### 3.9. Gestión de documentos
| Código | Requisito funcional |
|---|---|
| **RF-65** | El sistema debe permitir cargar documentos asociados a proyectos, trámites, planes de tesis e informes. |
| **RF-66** | El sistema debe almacenar datos del archivo: ruta, nombre original, tipo, tamaño, usuario que subió el archivo y fecha de carga. |
| **RF-67** | El sistema debe permitir visualizar y descargar documentos según permisos del usuario. |
| **RF-68** | El sistema debe impedir que un usuario acceda a documentos que no le corresponden. |
| **RF-69** | El sistema debe permitir adjuntar documentos de subsanación. |

### 3.10. Gestión de informes de avance
| Código | Requisito funcional |
|---|---|
| **RF-70** | El docente investigador debe poder registrar informes de avance de proyectos aprobados o en ejecución. |
| **RF-71** | El sistema debe registrar tipo de informe, periodo, porcentaje de avance, logros, dificultades y recomendaciones. |
| **RF-72** | El sistema debe permitir adjuntar un archivo de informe. |
| **RF-73** | El sistema debe generar un trámite de revisión para cada informe enviado. |
| **RF-74** | El Coordinador debe revisar el informe y derivarlo al Director. |
| **RF-75** | El Director debe aprobar, observar o rechazar el informe. |
| **RF-76** | El docente debe poder levantar observaciones del informe. |
| **RF-77** | El sistema debe mostrar el historial de informes presentados por proyecto. |

### 3.11. Gestión de resoluciones
| Código | Requisito funcional |
|---|---|
| **RF-78** | El Decano debe poder registrar resoluciones asociadas a trámites aprobados. |
| **RF-79** | El sistema debe permitir registrar número, fecha, asunto y archivo de resolución. |
| **RF-80** | El sistema debe asociar la resolución con el proyecto o plan de tesis correspondiente. |
| **RF-81** | El sistema debe actualizar el estado del trámite a aprobado con resolución cuando corresponda. |
| **RF-82** | El docente o estudiante debe poder visualizar y descargar la resolución asociada a su trámite. |

### 3.12. Gestión de evaluaciones
| Código | Requisito funcional |
|---|---|
| **RF-83** | El sistema debe permitir asignar evaluadores a proyectos o documentos. |
| **RF-84** | El evaluador debe poder visualizar proyectos asignados. |
| **RF-85** | El evaluador debe poder registrar resultados de evaluación. |
| **RF-86** | El sistema debe registrar observaciones, puntajes o resultados de evaluación según el diseño del módulo. |
| **RF-87** | El Director debe poder consultar las evaluaciones realizadas. |

### 3.13. Dashboards y reportes
| Código | Requisito funcional |
|---|---|
| **RF-88** | El sistema debe mostrar un dashboard general para el Administrador. |
| **RF-89** | El sistema debe mostrar un dashboard institucional para el Director de Investigación. |
| **RF-90** | El sistema debe mostrar un dashboard específico para cada Coordinador de Grupo. |
| **RF-91** | El dashboard del Coordinador debe mostrar solo miembros, proyectos, trámites e informes de su grupo. |
| **RF-92** | El sistema debe mostrar un dashboard para el Docente Investigador con sus proyectos, documentos, trámites e informes. |
| **RF-93** | El sistema debe mostrar un dashboard para el Evaluador con proyectos y evaluaciones asignadas. |
| **RF-94** | El sistema debe permitir generar reportes institucionales de proyectos, trámites, resoluciones e informes. |
| **RF-95** | El sistema debe permitir filtrar reportes por estado, grupo, fecha, tipo de trámite, investigador o convocatoria. |

### 3.14. Auditoría y trazabilidad
| Código | Requisito funcional |
|---|---|
| **RF-96** | El sistema debe registrar movimientos de cada trámite. |
| **RF-97** | El sistema debe registrar usuario, acción, fecha, estado anterior y estado nuevo de cada movimiento. |
| **RF-98** | El sistema debe registrar observaciones realizadas durante la revisión. |
| **RF-99** | El sistema debe permitir consultar la trazabilidad del trámite. |
| **RF-100** | El sistema debe registrar acciones sensibles como creación, edición, eliminación, aprobación, observación, rechazo y emisión de resolución. |

---

## 4. Requisitos no funcionales

### 4.1. Seguridad
| Código | Requisito no funcional |
|---|---|
| **RNF-01** | El sistema debe implementar autenticación obligatoria para todos los módulos internos. |
| **RNF-02** | El sistema debe aplicar control de acceso basado en roles. |
| **RNF-03** | El sistema debe impedir que un usuario acceda a información de otro usuario sin autorización. |
| **RNF-04** | Las contraseñas deben almacenarse usando hashing seguro. |
| **RNF-05** | El sistema debe proteger formularios mediante tokens CSRF. |
| **RNF-06** | El sistema debe validar todos los datos de entrada para prevenir inyección SQL, carga de archivos maliciosos y manipulación de parámetros. |
| **RNF-07** | El sistema debe restringir los tipos de archivos permitidos a formatos definidos como PDF, DOC y DOCX. |
| **RNF-08** | El sistema debe impedir acceso directo a archivos protegidos sin validación de permisos. |
| **RNF-09** | El sistema debe registrar acciones críticas para auditoría. |
| **RNF-10** | El sistema debe permitir desactivar usuarios que ya no deben ingresar. |

### 4.2. Usabilidad
| Código | Requisito no funcional |
|---|---|
| **RNF-11** | La interfaz debe ser clara, ordenada y comprensible para usuarios administrativos, docentes y estudiantes. |
| **RNF-12** | El sistema debe mostrar mensajes de éxito, error y advertencia después de cada acción relevante. |
| **RNF-13** | Los formularios deben indicar los campos obligatorios. |
| **RNF-14** | Los módulos deben estar organizados por rol para evitar confusión. |
| **RNF-15** | El sistema debe permitir realizar búsquedas y filtros en listados principales. |
| **RNF-16** | El diseño debe ser responsivo y poder visualizarse en computadoras, tablets y dispositivos móviles. |

### 4.3. Rendimiento
| Código | Requisito no funcional |
|---|---|
| **RNF-17** | El sistema debe responder a consultas comunes en un tiempo razonable para el usuario. |
| **RNF-18** | Los listados deben usar paginación para evitar sobrecarga de datos. |
| **RNF-19** | Las consultas deben filtrar información según el usuario autenticado y su rol. |
| **RNF-20** | El sistema debe evitar cargar documentos pesados innecesariamente en vistas principales. |

### 4.4. Disponibilidad y confiabilidad
| Código | Requisito no funcional |
|---|---|
| **RNF-21** | El sistema debe estar disponible durante el horario institucional de uso. |
| **RNF-22** | El sistema debe conservar los datos de trámites, documentos y movimientos aun cuando un trámite sea observado o rechazado. |
| **RNF-23** | El sistema debe evitar pérdida de datos durante operaciones críticas mediante transacciones. |
| **RNF-24** | El sistema debe manejar errores internos mostrando mensajes controlados al usuario. |
| **RNF-25** | El sistema debe permitir recuperación ante fallos mediante respaldos de base de datos y archivos. |

### 4.5. Mantenibilidad
| Código | Requisito no funcional |
|---|---|
| **RNF-26** | El sistema debe estar organizado mediante una arquitectura modular. |
| **RNF-27** | El código debe separar controladores, modelos, vistas, servicios y reglas de validación. |
| **RNF-28** | Los nombres de rutas, métodos y vistas deben ser consistentes con el rol o módulo correspondiente. |
| **RNF-29** | El sistema debe permitir agregar nuevos roles, estados o tipos de trámite con bajo impacto. |
| **RNF-30** | El sistema debe facilitar la corrección de errores y ampliación de funcionalidades. |

### 4.6. Escalabilidad
| Código | Requisito no funcional |
|---|---|
| **RNF-31** | El sistema debe permitir incorporar nuevos grupos de investigación. |
| **RNF-32** | El sistema debe permitir registrar nuevas líneas de investigación. |
| **RNF-33** | El sistema debe permitir agregar nuevos tipos de trámites. |
| **RNF-34** | El sistema debe soportar el crecimiento progresivo de usuarios, proyectos, documentos e informes. |
| **RNF-35** | El sistema debe permitir separar módulos futuros como repositorio de producción científica, indicadores o integración con sistemas externos. |

### 4.7. Integridad de datos
| Código | Requisito no funcional |
|---|---|
| **RNF-36** | El sistema debe garantizar que cada trámite tenga un solicitante válido. |
| **RNF-37** | El sistema debe garantizar que cada proyecto esté asociado a un responsable o investigador. |
| **RNF-38** | El sistema debe evitar duplicidad de correos institucionales. |
| **RNF-39** | El sistema debe evitar inconsistencias entre usuarios, roles, grupos y membresías. |
| **RNF-40** | El sistema debe registrar fechas de creación y actualización en las entidades principales. |

### 4.8. Compatibilidad
| Código | Requisito no funcional |
|---|---|
| **RNF-41** | El sistema debe ejecutarse en entorno web. |
| **RNF-42** | El sistema debe ser compatible con navegadores modernos como Google Chrome, Mozilla Firefox y Microsoft Edge. |
| **RNF-43** | El sistema debe funcionar adecuadamente sobre la infraestructura definida para la FIIS. |
| **RNF-44** | El sistema debe ser compatible con base de datos PostgreSQL. |
| **RNF-45** | El sistema debe permitir despliegue en servidor Linux con Nginx y PHP-FPM. |

### 4.9. Auditoría y trazabilidad
| Código | Requisito no funcional |
|---|---|
| **RNF-46** | El sistema debe conservar historial de movimientos de trámites. |
| **RNF-47** | El sistema debe permitir identificar qué usuario realizó cada acción. |
| **RNF-48** | El sistema debe permitir consultar fechas y estados anteriores de un trámite. |
| **RNF-49** | El sistema debe conservar historial de membresías de grupos de investigación. |
| **RNF-50** | El sistema debe permitir diferenciar miembros activos e inactivos. |

### 4.10. Respaldo y recuperación
| Código | Requisito no funcional |
|---|---|
| **RNF-51** | La base de datos debe ser respaldada periódicamente. |
| **RNF-52** | Los archivos cargados deben respaldarse junto con la base de datos. |
| **RNF-53** | El sistema debe contar con procedimientos de restauración ante fallos. |
| **RNF-54** | Los respaldos deben almacenarse en una ubicación segura. |

---

## 5. Reglas de negocio principales

| Código | Regla de negocio |
|---|---|
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
| **RN-12** | Para el grupo GINSOFT, las líneas visibles deben ser “Computacion” e “Ingenieria de software”. |

---

## 6. Alcance funcional propuesto para la siguiente fase
Se priorizan los siguientes módulos para el desarrollo:
1. Gestión completa de usuarios, roles y grupos de investigación.
2. Postulación de proyectos por docentes investigadores.
3. Registro y seguimiento de planes de tesis por estudiantes.
4. Flujo de revisión: Coordinador → Director → Decano.
5. Gestión de observaciones y subsanaciones.
6. Registro de resoluciones.
7. Dashboard por rol.
8. Trazabilidad y auditoría de trámites.
9. Gestión documental.
10. Reportes institucionales básicos.

---

## 7. Criterios de aceptación generales

| Código | Criterio de aceptación |
|---|---|
| **CA-01** | Un usuario debe poder ingresar solo si tiene credenciales válidas y estado activo. |
| **CA-02** | Un usuario debe ver únicamente los módulos permitidos por su rol. |
| **CA-03** | Un docente investigador debe poder registrar un proyecto y enviarlo al Coordinador. |
| **CA-04** | El Coordinador debe poder aprobar, observar o rechazar trámites de su grupo. |
| **CA-05** | El Director debe poder revisar trámites derivados por el Coordinador. |
| **CA-06** | El Decano debe poder registrar resoluciones de trámites aprobados. |
| **CA-07** | El sistema debe registrar la trazabilidad de cada trámite. |
| **CA-08** | El Administrador debe poder asignar docentes y coordinadores a grupos de investigación. |
| **CA-09** | El dashboard del Coordinador debe mostrar solo información de su grupo. |
| **CA-10** | El sistema debe impedir que un usuario visualice información que no le pertenece. |
