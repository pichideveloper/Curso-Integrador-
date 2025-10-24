Gestor del Club de Fútbol - Proyecto Final POO
No se pudo cargar la imagenVer enlace
(Reemplaza con una imagen real, ej. logo de fútbol o captura de la app)
Descripción
Este proyecto es un gestor interno para un club de fútbol, desarrollado en Java con Swing para la interfaz gráfica. Permite registrar y gestionar miembros, entrenadores, actividades y eventos, con funcionalidades como búsqueda, edición, eliminación, exportación a Excel/PDF y auditoría de acciones. Incorpora autenticación por roles (admin/secretarias) y validaciones de datos para robustez.
El sistema sigue principios SOLID, patrón MVC, DAO para persistencia, y TDD con JUnit 5 para testing. Desarrollado con NetBeans y Maven, conectado a MySQL para BD relacional.
Objetivo académico: Aplicar POO avanzada en un caso real, con énfasis en arquitectura modular y buenas prácticas.
Características Principales

Autenticación: Login con roles (admin full, secretarias limitadas).
CRUD Completo: Ingreso/edición/eliminación de miembros (con membresía ENUM), entrenadores (sueldo calculado), actividades/eventos (con JOINs y validación de conflictos horarios).
Búsquedas: Filtros por DNI/ID en vistas dedicadas (B classes).
Exportaciones: Tablas a Excel (POI) o PDF (PDFBox), con landscape para logs.
Auditoría: Logs de acciones en BD (TablaLogs), visibles solo para admin.
Validaciones: Filtros en tiempo real (solo letras/números, longitudes exactas ej. DNI 8 dígitos), rangos (edad 21-80, años 1-10).
UI Moderna: FlatLaf para temas, JDatePicker para fechas/horas.
Testing: TDD con JUnit 5 (cobertura 100% en DAOs).

Instalación
Requisitos

JDK 24 (o 21 LTS).
NetBeans 21+ con Maven.
MySQL 8.0+ (BD "BD_Club" con tablas predefinidas).
Git para clonar.

Pasos

Clona el repo:
textgit clone https://github.com/pichideveloper/Curso-integrador.git
cd Curso-integrador

Abre en NetBeans: File > Open Project > Selecciona la carpeta.
Configura BD: Crea "BD_Club" en MySQL Workbench, ejecuta script SQL (adjunto en /sql/schema.sql).
Actualiza credenciales en CConexion.java (user/pass).
Clean and Build: Clic derecho proyecto > Clean and Build.
Run: Clic derecho > Run (abre LoginFrame).

Dependencias (Maven)
Ver pom.xml para libs (MySQL, POI, PDFBox, JUnit, FlatLaf, JCalendar).
Uso

Login: Usa 'admin' / 'admin123' para full access, o 'secre_manana' / '1234' para limitada.
Navegación: Desde Menu, elige módulo (Miembros, Entrenadores, etc.) y acción (Ingresar, Mostrar, Buscar).
Ejemplo Miembros:

Ingresar: Llena form (DNI 8 dígitos, solo letras en nombre), guarda.
Mostrar: Tabla con todos, edita/borra (solo admin borra todo).
Buscar: Filtra por DNI, exporta resultado a Excel/PDF.


Auditoría: Admin ve logs en "Ver Logs" (exportables).
Tests: Clic derecho MiembroCCTest > Test File (ver Output para resultados).

Tecnologías y Herramientas

Lenguaje: Java 24 con Swing (UI).
BD: MySQL 8.0, JDBC para conexión.
Build: Maven 3.x (pom.xml para deps/plugins).
Testing: JUnit 5 (TDD en paquete test).
Git/GitHub: Control de versiones (25 commits, branches main/develop).
Otras: FlatLaf (UI moderna), POI/PDFBox (exports), JCalendar (fechas).

Estructura del Proyecto
textProjectoFinalPOO/
├── src/main/java/
│   ├── enlaces/          # DAOs (MiembroCC, etc.) + CConexion
│   ├── GUIEntrada/       # Forms de ingreso (MiembroE, LoginFrame)
│   ├── GUISalida/        # Vistas mostrar/editar (MiembroS)
│   ├── GUIBuscar/        # Búsquedas (MiembroB)
│   └── util/             # Sesion, exports utils
├── src/test/java/        # Tests JUnit (MiembroCCTest)
├── pom.xml               # Maven deps/plugins
├── README.md             # Este archivo
└── sql/schema.sql        # Script BD inicial
