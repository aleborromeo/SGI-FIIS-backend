# Requisitos funcionales y no funcionales del Sistema de Gestión de Investigación FIIS

## 1. Descripción general del sistema
El Sistema de Gestión de Investigación FIIS tiene como propósito automatizar, controlar y dar seguimiento a los procesos académicos y administrativos relacionados con la investigación en la Facultad de Ingeniería en Informática y Sistemas[cite: 1]. El sistema permite gestionar usuarios, grupos de investigación, convocatorias, proyectos de investigación, planes de tesis, informes de avance, documentos, observaciones, resoluciones, evaluaciones y trazabilidad de los trámites[cite: 1].

El sistema contempla la participación de distintos roles: Administrador, Estudiante/Tesista, Docente Investigador, Coordinador de Grupo de Investigación, Director de Investigación, Decano y Evaluador[cite: 1].

---

## 2. Actores del sistema

| Código | Actor | Descripción |
| :--- | :--- | :--- |
| **ACT-01** | Administrador | Usuario responsable de gestionar usuarios, roles, grupos de investigación, miembros y configuración general del sistema[cite: 1]. |
| **ACT-02** | Estudiante/Tesista | Usuario que registra, consulta y subsana planes de tesis o informes de tesis[cite: 1]. |
| **ACT-03** | Docente Investigador | Usuario que registra proyectos de investigación, presenta documentos e informes de avance[cite: 1]. |
| **ACT-04** | Coordinador de Grupo | Usuario responsable de revisar trámites de docentes o estudiantes pertenecientes a su grupo de investigación[cite: 1]. |
| **ACT-05** | Director de Investigación | Usuario responsable de revisar, aprobar, observar o derivar trámites hacia el Decano[cite: 1]. |
| **ACT-06** | Decano | Usuario responsable de emitir o registrar resoluciones asociadas a proyectos o planes de tesis[cite: 1]. |
| **ACT-07** | Evaluador | Usuario responsable de evaluar proyectos o documentos asignados[cite: 1]. |

---

## 3. Requisitos funcionales

### 3.1. Gestión de autenticación y acceso
| Código | Requisito funcional |
| :--- | :--- |
| **RF-01** | El sistema debe permitir el inicio de sesión mediante correo institucional y contraseña[cite: 1]. |
| **RF-02** | El sistema debe validar que el usuario se encuentre activo antes de permitir el ingreso[cite: 1]. |
| **RF-03** | El sistema debe redirigir al usuario a un dashboard según su rol[cite: 1]. |
| **RF-04** | El sistema debe restringir el acceso a módulos según el rol asignado[cite: 1]. |
| **RF-05** | El sistema debe permitir cerrar sesión de forma segura[cite: 1]. |
| **RF-06** | El sistema debe permitir obligar al usuario a cambiar su contraseña inicial cuando corresponda[cite: 1]. |

### 3.2. Gestión de usuarios
| Código | Requisito funcional |
| :--- | :--- |
| **RF-07** | El administrador debe poder registrar usuarios del sistema[cite: 1]. |
| **RF-08** | El administrador debe poder editar datos personales del usuario: DNI, nombres, apellidos, correo, teléfono, rol y estado[cite: 1]. |
| **RF-09** | El administrador debe poder asignar roles: ADMIN, ESTUDIANTE, DOCENTE_INVESTIGADOR, COORDINADOR_GRUPO, DIRECTOR_INVESTIGACION, DECANO y EVALUADOR[cite: 1]. |
| **RF-10** | El sistema debe generar automáticamente el correo institucional bajo el formato `primer_nombre.primer_apellido@unas.edu.pe` cuando no se ingrese un correo manualmente[cite: 1]. |
| **RF-11** | El administrador debe poder activar o desactivar usuarios[cite: 1]. |
| **RF-12** | El administrador debe poder reiniciar la contraseña de un usuario[cite: 1]. |
| **RF-13** | El sistema debe evitar que el administrador elimine su propio usuario[cite: 1]. |
| **RF-14** | El sistema debe permitir buscar usuarios por nombre, correo, DNI, rol o estado[cite: 1]. |

### 3.3. Gestión de grupos de investigación
| Código | Requisito funcional |
| :--- | :--- |
| **RF-15** | El sistema debe permitir visualizar los grupos de investigación registrados: GINSOFT, RESEGTI, GISI, CICO, EAP, MAP y EU[cite: 1]. |
| **RF-16** | El administrador debe poder consultar el coordinador asignado a cada grupo de investigación[cite: 1]. |
| **RF-17** | El administrador debe poder asignar docentes investigadores a un grupo de investigación[cite: 1]. |
| **RF-18** | El administrador debe poder asignar un coordinador a cada grupo de investigación[cite: 1]. |
| **RF-19** | El sistema debe registrar la membresía activa de cada docente en la tabla de miembros del grupo[cite: 1]. |
| **RF-20** | El sistema debe permitir retirar o desactivar miembros de un grupo sin eliminar su historial[cite: 1]. |
| **RF-21** | El sistema debe impedir que un usuario tenga más de una membresía activa en distintos grupos, salvo que se establezca una regla institucional diferente[cite: 1]. |
| **RF-22** | El sistema debe mostrar en cada grupo los miembros activos y el coordinador actual[cite: 1]. |
| **RF-23** | El sistema debe actualizar automáticamente el dashboard del coordinador según el grupo al que pertenece[cite: 1]. |

### 3.4. Gestión de líneas de investigación
| Código | Requisito funcional |
| :--- | :--- |
| **RF-24** | El sistema debe permitir registrar líneas de investigación asociadas a la facultad o programa académico[cite: 1]. |
| **RF-25** | El formulario de postulación debe mostrar el campo “Línea de investigación”[cite: 1]. |
| **RF-26** | Para el grupo GINSOFT, el sistema debe mostrar únicamente las líneas “Computacion” e “Ingenieria de software”, según la configuración actual[cite: 1]. |
| **RF-27** | El sistema debe permitir activar o desactivar líneas de investigación[cite: 1]. |
| **RF-28** | El sistema debe evitar mostrar líneas inactivas en los formularios de registro de proyectos[cite: 1]. |

### 3.5. Gestión de convocatorias
| Código | Requisito funcional |
| :--- | :--- |
| **RF-29** | El sistema debe permitir registrar convocatorias de investigación[cite: 1]. |
| **RF-30** | El sistema debe permitir definir fechas de inicio y fin de postulación[cite: 1]. |
| **RF-31** | El sistema debe permitir definir si una convocatoria está abierta, cerrada o finalizada[cite: 1]. |
| **RF-32** | El docente investigador debe poder visualizar convocatorias abiertas[cite: 1]. |
| **RF-33** | El docente investigador debe poder registrar un proyecto desde una convocatoria activa[cite: 1]. |
| **RF-34** | El sistema debe impedir registrar proyectos en convocatorias cerradas[cite: 1]. |

### 3.6. Gestión de proyectos de investigación
| Código | Requisito funcional |
| :--- | :--- |
| **RF-35** | El docente investigador debe poder registrar un proyecto de investigación[cite: 1]. |
| **RF-36** | El sistema debe registrar información general del proyecto: título, resumen, objetivo general, línea de investigación, presupuesto, fechas de inicio y fin, equipo de trabajo y lugar de ejecución[cite: 1]. |
| **RF-37** | El sistema debe permitir adjuntar el archivo del proyecto en formato PDF, DOC o DOCX[cite: 1]. |
| **RF-38** | El sistema debe generar automáticamente un código de proyecto[cite: 1]. |
| **RF-39** | El sistema debe vincular el proyecto al docente investigador que lo registra[cite: 1]. |
| **RF-40** | El sistema debe registrar automáticamente un trámite de postulación del proyecto[cite: 1]. |
| **RF-41** | El sistema debe enviar el proyecto al Coordinador de Grupo para revisión inicial[cite: 1]. |
| **RF-42** | El docente investigador debe poder consultar sus proyectos registrados[cite: 1]. |
| **RF-43** | El sistema debe impedir que un docente visualice proyectos que no le pertenecen[cite: 1]. |
| **RF-44** | El sistema debe mostrar el estado del proyecto: postulado, observado, aprobado, rechazado, en ejecución o finalizado[cite: 1]. |

### 3.7. Gestión de planes de tesis
| Código | Requisito funcional |
| :--- | :--- |
| **RF-45** | El estudiante debe poder registrar un plan de tesis[cite: 1]. |
| **RF-46** | El sistema debe asociar el plan de tesis al estudiante solicitante[cite: 1]. |
| **RF-47** | El sistema debe enviar el plan de tesis al Coordinador de Grupo para revisión[cite: 1]. |
| **RF-48** | El Coordinador debe poder aprobar, observar o rechazar el plan de tesis[cite: 1]. |
| **RF-49** | El Director de Investigación debe poder revisar el plan de tesis derivado por el Coordinador[cite: 1]. |
| **RF-50** | Si el Director observa un plan de tesis, el sistema debe devolverlo al Coordinador de Grupo, y el Coordinador debe comunicar la observación al estudiante[cite: 1]. |
| **RF-51** | El estudiante debe poder subsanar observaciones del plan de tesis[cite: 1]. |
| **RF-52** | El Decano debe poder registrar la resolución correspondiente al plan de tesis aprobado[cite: 1]. |
| **RF-53** | El producto final asociado al tesista debe considerar el informe de tesis[cite: 1]. |

### 3.8. Gestión de trámites y flujo de revisión
| Código | Requisito funcional |
| :--- | :--- |
| **RF-54** | El sistema debe registrar trámites asociados a proyectos, planes de tesis, informes y resoluciones[cite: 1]. |
| **RF-55** | Todo trámite debe tener código, tipo, solicitante, estado actual, rol revisor y fecha de envío[cite: 1]. |
| **RF-56** | El sistema debe permitir derivar trámites entre Coordinador, Director y Decano según el flujo establecido[cite: 1]. |
| **RF-57** | El Coordinador debe poder revisar trámites pendientes de su grupo[cite: 1]. |
| **RF-58** | El Director debe poder revisar trámites pendientes de Dirección de Investigación[cite: 1]. |
| **RF-59** | El Decano debe poder revisar trámites pendientes de resolución[cite: 1]. |
| **RF-60** | El sistema debe permitir aprobar, observar o rechazar trámites[cite: 1]. |
| **RF-61** | El sistema debe registrar la última observación realizada sobre un trámite[cite: 1]. |
| **RF-62** | El sistema debe permitir levantar o subsanar observaciones[cite: 1]. |
| **RF-63** | El sistema debe actualizar el estado y rol revisor del trámite según cada acción realizada[cite: 1]. |
| **RF-64** | El sistema debe mostrar la trazabilidad completa de cada trámite[cite: 1]. |

### 3.9. Gestión de documentos
| Código | Requisito funcional |
| :--- | :--- |
| **RF-65** | El sistema debe permitir cargar documentos asociados a proyectos, trámites, planes de tesis e informes[cite: 1]. |
| **RF-66** | El sistema debe almacenar datos del archivo: ruta, nombre original, tipo, tamaño, usuario que subió el archivo y fecha de carga[cite: 1]. |
| **RF-67** | El sistema debe permitir visualizar y descargar documentos según permisos del usuario[cite: 1]. |
| **RF-68** | El sistema debe impedir que un usuario acceda a documentos que no le corresponden[cite: 1]. |
| **RF-69** | El sistema debe permitir adjuntar documentos de subsanación[cite: 1]. |

### 3.10. Gestión de informes de avance
| Código | Requisito funcional |
| :--- | :--- |
| **RF-70** | El docente investigador debe poder registrar informes de avance de proyectos aprobados o en ejecución[cite: 1]. |
| **RF-71** | El sistema debe registrar tipo de informe, periodo, porcentaje de avance, logros, dificultades y recomendaciones[cite: 1]. |
| **RF-72** | El sistema debe permitir adjuntar un archivo de informe[cite: 1]. |
| **RF-73** | El sistema debe generar un trámite de revisión para cada informe enviado[cite: 1]. |
| **RF-74** | El Coordinador debe revisar el informe y derivarlo al Director[cite: 1]. |
| **RF-75** | El Director debe aprobar, observar o rechazar el informe[cite: 1]. |
| **RF-76** | El docente debe poder levantar observaciones del informe[cite: 1]. |
| **RF-77** | El sistema debe mostrar el historial de informes presentados por proyecto[cite: 1]. |

### 3.11. Gestión de resoluciones
| Código | Requisito funcional |
| :--- | :--- |
| **RF-80** | El sistema debe asociar la resolución con el proyecto o plan de tesis correspondiente[cite: 1]. |
| **RF-81** | El sistema debe actualizar el estado del trámite a aprobado con resolución cuando corresponda[cite: 1]. |
| **RF-82** | El docente o estudiante debe poder visualizar y descargar la resolución asociada a su trámite[cite: 1]. |

### 3.12. Gestión de evaluaciones
| Código | Requisito funcional |
| :--- | :--- |
| **RF-83** | El sistema debe permitir asignar evaluadores a proyectos o documentos[cite: 1]. |
| **RF-84** | El evaluador debe poder visualizar proyectos asignados[cite: 1]. |
| **RF-85** | El evaluador debe poder registrar resultados de evaluación[cite: 1]. |
| **RF-86** | El sistema debe registrar observaciones, puntajes o resultados de evaluación según el diseño del módulo[cite: 1]. |
| **RF-87** | El Director debe poder consultar las evaluaciones realizadas[cite: 1]. |

### 3.13. Dashboards y reportes
| Código | Requisito funcional |
| :--- | :--- |
| **RF-88** | El sistema debe mostrar un dashboard general para el Administrador[cite: 1]. |
| **RF-89** | El sistema debe mostrar un dashboard institucional para el Director de Investigación[cite: 1]. |
| **RF-90** | El sistema debe mostrar un dashboard específico para cada Coordinador de Grupo[cite: 1]. |
| **RF-91** | El dashboard del Coordinador debe mostrar solo miembros, proyectos, trámites e informes de su grupo[cite: 1]. |
| **RF-92** | El sistema debe mostrar un dashboard para el Docente Investigador con sus proyectos, documentos, trámites e informes[cite: 1]. |
| **RF-93** | El sistema debe mostrar un dashboard para el Evaluador con proyectos y evaluaciones asignadas[cite: 1]. |
| **RF-94** | El sistema debe permitir generar reportes institucionales de proyectos, trámites, resoluciones e informes[cite: 1]. |
| **RF-95** | El sistema debe permitir filtrar reportes por estado, grupo, fecha, tipo de trámite, investigador o convocatoria[cite: 1]. |

### 3.14. Auditoría y trazabilidad
| Código | Requisito funcional |
| :--- | :--- |
| **RF-96** | El sistema debe registrar movimientos de cada trámite[cite: 1]. |
| **RF-97** | El sistema debe registrar usuario, acción, fecha, estado anterior y estado nuevo de cada movimiento[cite: 1]. |
| **RF-98** | El sistema debe registrar observaciones realizadas durante la revisión[cite: 1]. |
| **RF-99** | El sistema debe permitir consultar la trazabilidad del trámite[cite: 1]. |
| **RF-100** | El sistema debe registrar acciones sensibles como creación, edición, eliminación, aprobación, observación, rechazo y emisión de resolución[cite: 1]. |

---

## 4. Requisitos no funcionales

### 4.1. Seguridad
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-01** | El sistema debe implementar autenticación obligatoria para todos los módulos internos[cite: 1]. |
| **RNF-02** | El sistema debe aplicar control de acceso basado en roles[cite: 1]. |
| **RNF-03** | El sistema debe impedir que un usuario acceda a información de otro usuario sin autorización[cite: 1]. |
| **RNF-04** | Las contraseñas deben almacenarse usando hashing seguro[cite: 1]. |
| **RNF-05** | El sistema debe proteger formularios mediante tokens CSRF[cite: 1]. |
| **RNF-06** | El sistema debe validar todos los datos de entrada para prevenir inyección SQL, carga de archivos maliciosos y manipulación de parámetros[cite: 1]. |
| **RNF-07** | El sistema debe restringir los tipos de archivos permitidos a formatos definidos como PDF, DOC y DOCX[cite: 1]. |
| **RNF-08** | El sistema debe impedir acceso directo a archivos protegidos sin validación de permisos[cite: 1]. |
| **RNF-09** | El sistema debe registrar acciones críticas para auditoría[cite: 1]. |
| **RNF-10** | El sistema debe permitir desactivar usuarios que ya no deben ingresar[cite: 1]. |

### 4.2. Usabilidad
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-11** | La interfaz debe ser clara, ordenada y comprensible para usuarios administrativos, docentes y estudiantes[cite: 1]. |
| **RNF-12** | El sistema debe mostrar mensajes de éxito, error y advertencia después de cada acción relevante[cite: 1]. |
| **RNF-13** | Los formularios deben indicar los campos obligatorios[cite: 1]. |
| **RNF-14** | Los módulos deben estar organizados por rol para evitar confusión[cite: 1]. |
| **RNF-15** | El sistema debe permitir realizar búsquedas y filtros en listados principales[cite: 1]. |
| **RNF-16** | El diseño debe ser responsivo y poder visualizarse en computadoras, tablets y dispositivos móviles[cite: 1]. |

### 4.3. Rendimiento
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-17** | El sistema debe responder a consultas comunes en un tiempo razonable para el usuario[cite: 1]. |
| **RNF-18** | Los listados deben usar paginación para evitar sobrecarga de datos[cite: 1]. |
| **RNF-19** | Las consultas deben filtrar información según el usuario autenticado y su rol[cite: 1]. |
| **RNF-20** | El sistema debe evitar cargar documentos pesados innecesariamente en vistas principales[cite: 1]. |

### 4.4. Disponibilidad y confiabilidad
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-21** | El sistema debe estar disponible durante el horario institucional de uso[cite: 1]. |
| **RNF-22** | El sistema debe conservar los datos de trámites, documentos y movimientos aun cuando un trámite sea observado o rechazado[cite: 1]. |
| **RNF-23** | El sistema debe evitar pérdida de datos durante operaciones críticas mediante transacciones[cite: 1]. |
| **RNF-24** | El sistema debe manejar errores internos mostrando mensajes controlados al usuario[cite: 1]. |
| **RNF-25** | El sistema debe permitir recuperación ante fallos mediante respaldos de base de datos y archivos[cite: 1]. |

### 4.5. Mantenibilidad
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-26** | El sistema debe estar organizado mediante una arquitectura modular[cite: 1]. |
| **RNF-27** | El código debe separar controladores, modelos, vistas, servicios y reglas de validación[cite: 1]. |
| **RNF-28** | Los nombres de rutas, métodos y vistas deben ser consistentes con el rol o módulo correspondiente[cite: 1]. |
| **RNF-29** | El sistema debe permitir agregar nuevos roles, estados o tipos de trámite con bajo impacto[cite: 1]. |
| **RNF-30** | El sistema debe facilitar la corrección de errores y ampliación de funcionalidades[cite: 1]. |

### 4.6. Escalabilidad
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-31** | El sistema debe permitir incorporar nuevos grupos de investigación[cite: 1]. |
| **RNF-32** | El sistema debe permitir registrar nuevas líneas de investigación[cite: 1]. |
| **RNF-33** | El sistema debe permitir agregar nuevos tipos de trámites[cite: 1]. |
| **RNF-34** | El sistema debe soportar el crecimiento progresivo de usuarios, proyectos, documentos e informes[cite: 1]. |
| **RNF-35** | El sistema debe permitir separar módulos futuros como repositorio de producción científica, indicadores o integración con sistemas externos[cite: 1]. |

### 4.7. Integridad de datos
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-36** | El sistema debe garantizar que cada trámite tenga un solicitante válido[cite: 1]. |
| **RNF-37** | El sistema debe garantizar que cada proyecto esté asociado a un responsable o investigador[cite: 1]. |
| **RNF-38** | El sistema debe evitar duplicidad de correos institucionales[cite: 1]. |
| **RNF-39** | El sistema debe evitar inconsistencies entre usuarios, roles, grupos y membresías[cite: 1]. |
| **RNF-40** | El sistema debe registrar fechas de creación y actualización en las entidades principales[cite: 1]. |

### 4.8. Compatibilidad
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-41** | El sistema debe ejecutarse en entorno web[cite: 1]. |
| **RNF-42** | El sistema debe ser compatible con navegadores modernos como Google Chrome, Mozilla Firefox y Microsoft Edge[cite: 1]. |
| **RNF-43** | El sistema debe funcionar adecuadamente sobre la infraestructura definida para la FIIS[cite: 1]. |
| **RNF-44** | El sistema debe ser compatible con base de datos PostgreSQL[cite: 1]. |
| **RNF-45** | El sistema debe permitir despliegue en servidor Linux con Docker, Nginx (frontend) y JRE 17 (backend)[cite: 1]. |

### 4.9. Auditoría y trazabilidad
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-46** | El sistema debe conservar historial de movimientos de trámites[cite: 1]. |
| **RNF-47** | El sistema debe permitir identificar qué usuario realizó cada acción[cite: 1]. |
| **RNF-48** | El sistema debe permitir consultar fechas y estados anteriores de un trámite[cite: 1]. |
| **RNF-49** | El sistema debe conservar historial de membresías de grupos de investigación[cite: 1]. |
| **RNF-50** | El sistema debe permitir diferenciar miembros activos e inactivos[cite: 1]. |

### 4.10. Respaldo y recuperación
| Código | Requisito no funcional |
| :--- | :--- |
| **RNF-51** | La base de datos debe ser respaldada periódicamente[cite: 1]. |
| **RNF-52** | Los archivos cargados deben respaldarse junto con la base de datos[cite: 1]. |
| **RNF-53** | El sistema debe contar con procedimientos de restauración ante fallos[cite: 1]. |
| **RNF-54** | Los respaldos deben almacenarse en una ubicación segura[cite: 1]. |

---

## 5. Reglas de negocio principales
| Código | Regla de negocio |
| :--- | :--- |
| **RN-01** | Cada usuario debe tener un rol principal asignado[cite: 1]. |
| **RN-02** | Cada docente investigador debe pertenecer a un grupo de investigación[cite: 1]. |
| **RN-03** | Cada grupo de investigación debe tener un coordinador asignado[cite: 1]. |
| **RN-04** | Un coordinador solo debe revisar trámites correspondientes a su grupo[cite: 1]. |
| **RN-05** | Un docente investigador solo debe visualizar sus proyectos, documentos e informes[cite: 1]. |
| **RN-06** | Un estudiante solo debe visualizar sus planes de tesis y trámites[cite: 1]. |
| **RN-07** | Si el Director observa un plan de tesis, este debe retornar al Coordinador, no directamente al estudiante[cite: 1]. |
| **RN-08** | El Coordinador es responsable de comunicar al estudiante las observaciones del Director sobre el plan de tesis[cite: 1]. |
| **RN-09** | El Decano registra la resolución final cuando el trámite ha sido aprobado[cite: 1]. |
| **RN-10** | Los documentos asociados a un trámite no deben eliminarse físicamente sin autorización administrativa[cite: 1]. |
| **RN-11** | Las líneas de investigación visibles en la postulación deben estar activas[cite: 1]. |
| **RN-12** | Para el grupo GINSOFT, las líneas visibles deben ser “Computacion” e “Ingenieria de software”[cite: 1]. |

---

## 6. Alcance funcional propuesto para la siguiente fase
Para la siguiente fase del proceso de construcción, se recomienda priorizar los siguientes módulos[cite: 1]:
* Gestión completa de usuarios, roles y grupos de investigación[cite: 1].
* Postulación de proyectos por docentes investigadores[cite: 1].
* Registro y seguimiento de planes de tesis por estudiantes[cite: 1].
* Flujo de revisión: Coordinador → Director → Decano[cite: 1].
* Gestión de observaciones y subsanaciones[cite: 1].
* Registro de resoluciones[cite: 1].
* Dashboard por rol[cite: 1].
* Trazabilidad y auditoría de trámites[cite: 1].
* Gestión documental[cite: 1].
* Reportes institucionales básicos[cite: 1].

---

## 7. Criterios de aceptación generales
| Código | Criterio de aceptación |
| :--- | :--- |
| **CA-01** | Un usuario debe poder ingresar solo si tiene credenciales válidas y estado activo[cite: 1]. |
| **CA-02** | Un usuario debe ver únicamente los módulos permitidos por su rol[cite: 1]. |
| **CA-03** | Un docente investigador debe poder registrar un proyecto y enviarlo al Coordinador[cite: 1]. |
| **CA-04** | El Coordinador debe poder aprobar, observar o rechazar trámites de su grupo[cite: 1]. |
| **CA-05** | El Director debe poder revisar trámites derivados por el Coordinador[cite: 1]. |
| **CA-06** | El Decano debe poder registrar resoluciones de trámites aprobados[cite: 1]. |
| **CA-07** | El sistema debe registrar la trazabilidad de cada trámite[cite: 1]. |
| **CA-08** | El Administrador debe poder asignar docentes y coordinadores a grupos de investigación[cite: 1]. |
| **CA-09** | El dashboard del Coordinador debe mostrar solo información de su grupo[cite: 1]. |
| **CA-10** | El sistema debe impedir que un usuario visualice información que no le pertenece[cite: 1]. |