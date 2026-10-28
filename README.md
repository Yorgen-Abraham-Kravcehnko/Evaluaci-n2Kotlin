# Tienda Departamental Rincón Sur - Control de Flujo y Accesos (DSY1105)

Aplicación móvil desarrollada en **Kotlin** y **Jetpack Compose** para la **Evaluación Parcial 2** de la asignatura *Desarrollo de Aplicaciones Móviles (DSY1105)*.

## 1. Descripción del Caso
La **Tienda Departamental Rincón Sur** cuenta con 4 accesos peatonales y requiere diferenciar qué proporción del flujo diario de personas corresponde a clientes que **efectivamente realizan una compra** versus personas que **solo transitan por la tienda** como paso peatonal hacia la tienda vecina o el centro comercial, incorporando variables de segmentación (sexo estimado y rango etario).

## 2. Integrantes del Equipo
* **Sebastián Curi** (`IgorEeyore`)
* **Luis Carvallo** (`yorgen-abraham-kravchenko`)

## 3. Funcionalidades Implementadas
* **Autenticación y Registro Validado (`LoginScreen` y `RegisterScreen`):**
  * Formularios reactivos con Material Design 3 (`OutlinedTextField`, íconos de estado y mensajes de error por campo).
  * Validaciones desacopladas en `Validators.kt` (formato de correo, RUT, nombre solo letras y contraseña segura).
* **Configuración de Turno y Panel Principal (`HomeScreen` y `ProfileScreen`):**
  * Selección de Punto de Acceso (Accesos 1 al 4) y Turno (Mañana / Tarde).
  * Resumen en vivo de ingresos totales y porcentaje de conversión a compra.
  * Vista de perfil del colaborador activo.
* **Conteo Rápido en Vivo y CRUD con SQLite/Room (`ConteoCrudScreen`):**
  * Selección rápida mediante Chips (`Solo Tránsito` vs `Compró`, `Sexo`, `Rango Etario`).
  * Persistencia local en base de datos **Room (`AppDatabase`)** con datos semilla iniciales.
  * Edición y eliminación de registros mediante diálogos modales (`AlertDialog`).
* **Métricas Consolidadas e Historial (`TotalesScreen` y `HistorialScreen`):**
  * Desglose porcentual y barras de progreso por cada uno de los 4 accesos.
  * Filtro de búsqueda en tiempo real por fecha, acceso, turno o rango etario.

## 4. Arquitectura del Proyecto (MVVM)
* `data/local/`: Entidades Room (`UserEntity`, `RegistroAccesoEntity`), DAOs y `AppDatabase`.
* `data/repository/`: Repositorios (`UserRepository`, `RegistroRepository`).
* `domain/validation/`: Reglas de validación de formularios (`Validators.kt`).
* `navigation/`: Definición de rutas (`Routes.kt`) y gráfico de navegación (`AppNavigation.kt`).
* `ui/viewmodel/`: Gestión de estado reactivo con `StateFlow` (`AuthViewModel`, `ConteoViewModel`).
* `ui/screen/` y `ui/components/`: Pantallas y componentes reutilizables en Jetpack Compose.

## 5. Pasos para Ejecutar
1. Clonar el repositorio en **Android Studio**.
2. Esperar la sincronización automática de **Gradle (versión 8.13)**.
3. Ejecutar la aplicación en un emulador o dispositivo físico Android (API 24 o superior).
4. **Credenciales de prueba precargadas:**
   * **Correo:** `colaborador@rinconsur.cl`
   * **Contraseña:** `Password123!`
