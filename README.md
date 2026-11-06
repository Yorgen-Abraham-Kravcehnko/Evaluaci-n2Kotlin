# Tienda Departamental Rincón Sur – Control de Flujo y Accesos (DSY1105)

Aplicación móvil en **Kotlin** y **Jetpack Compose** para la **Evaluación Parcial 2** de *Desarrollo de Aplicaciones Móviles (DSY1105)*.

## 1. Descripción del caso
La Tienda Departamental Rincón Sur tiene 4 accesos peatonales y necesita diferenciar qué parte del flujo diario corresponde a clientes que **compran** y qué parte solo **transita** hacia la tienda vecina o el centro comercial, segmentando por sexo estimado y rango etario.

## 2. Integrantes
* **Sebastián Curi** (`IgorEeyore`)
* **Luis Carvallo** (`yorgen-abraham-kravchenko`)

## 3. Funcionalidades implementadas
* **Autenticación y registro (`LoginScreen`, `RegisterScreen`)**
  * Validación en tiempo real por campo: correo corporativo `@rinconsur.cl`, RUT con dígito verificador (módulo 11, acepta puntos y `K`), nombre solo letras y contraseña segura.
  * RUT y correo únicos: validación en el repositorio e índices únicos en Room.
  * Contraseñas almacenadas con **SHA-256 y salt aleatorio por usuario**; nunca en texto plano.
* **Sesión persistente (DataStore)**
  * La sesión se mantiene al cerrar y reabrir la app. Solo se guarda el id del usuario.
  * Cierre de sesión con diálogo de confirmación.
* **Panel principal y conteo (`HomeScreen`, `ConteoCrudScreen`)**
  * Selección de acceso (1 a 4) y turno (Mañana / Tarde).
  * Registro rápido con chips (Compró / Solo tránsito, sexo, rango etario).
  * Edición y eliminación con modales integrados, control de cambios pendientes y resumen al finalizar el turno.
* **Métricas e historial (`TotalesScreen`, `HistorialScreen`)**
  * Desglose por acceso.
  * Historial agrupado en tarjetas por fecha y turno, con detalle de compraron / tránsito.
  * Filtro de texto por acceso, sexo, rango etario, turno u hora.

## 4. Recursos nativos
| Recurso | Uso | Implementación |
|---|---|---|
| Vibración | 1 pulso al iniciar el conteo, 2 pulsos al finalizar el turno | `util/Vibrador.kt`, permiso `VIBRATE`, `VibrationEffect` en API 26+ con alternativa para API 24–25 |
| Cámara | Foto de perfil | `ActivityResultContracts.TakePicturePreview`, imagen guardada en el almacenamiento interno y ruta en Room. No requiere permiso `CAMERA` porque usa la app de cámara del sistema |

## 5. Arquitectura (MVVM)
* `data/local/`: entidades Room (`UserEntity`, `RegistroAccesoEntity`), DAOs y `AppDatabase`.
* `data/datastore/`: sesión (`SessionManager`).
* `data/repository/`: `UserRepository`, `RegistroRepository`.
* `domain/validation/`: `Validators.kt` y `PasswordHasher.kt`.
* `navigation/`: rutas y `NavHost`; el destino inicial depende de la sesión.
* `ui/viewmodel/`: `AuthViewModel`, `ConteoViewModel` con `StateFlow`.
* `ui/screen/`, `ui/components/`: pantallas y componentes Compose.
* `util/`: acceso a hardware y archivos (`Vibrador`, `FotoPerfilStorage`).

Las validaciones viven fuera de los Composables y los ViewModel no reciben `Context`.

## 6. Pruebas unitarias
19 pruebas JVM en `app/src/test`:
* `PasswordHasherTest` (4): verificación, contraseña incorrecta, salt distinto y formato hex.
* `RutValidatorTest` (7): dígito verificador, puntos, `K` y formatos inválidos.
* `EmailValidatorTest` (8): dominio corporativo, mayúsculas, espacios, dominio falso y puntos consecutivos.

Ejecutar: `./gradlew testDebugUnitTest`

## 7. Cómo ejecutar
1. Clonar el repositorio y abrirlo en **Android Studio**.
2. Sincronizar Gradle (8.13).
3. Ejecutar en un emulador o dispositivo con **Android 7.0 (API 24)** o superior.

### Usuarios de prueba (datos ficticios, precargados)
| Usuario | Correo | Contraseña |
|---|---|---|
| Sebastián Curi – Operador | `colaborador@rinconsur.cl` | `Password123!` |
| Luis Carvallo – Supervisor | `admin@rinconsur.cl` | `Admin123!` |

En la EP2 ambos usuarios usan el mismo flujo de operador. Las vistas del rol Administrador corresponden a la EFT.

## 8. Limitaciones conocidas y trabajo para la EFT
* El estado del turno activo vive en memoria del `ConteoViewModel`: los registros persisten en Room, pero el turno abierto se pierde al reiniciar la app.
* Pendientes que dependen de una tabla `turnos_laborales`: confirmación al iniciar turno, un solo turno abierto por usuario y cierre automático 15 minutos después del término del horario.
* Rol Administrador, backend (Spring Boot + API REST) y sincronización remota.