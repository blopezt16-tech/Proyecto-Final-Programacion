# Proyecto Control de Gastos — Librería Kotlin (`gastos-core`) + App Android (`ControlGastos`)

**Universidad Mariano Gálvez de Guatemala**  
**Curso:** Programación II  
**Catedrático:** Ing. Walter Aníbal Córdova Zeceña  
**Estudiante:** BRANDON OVIDIO JONATHAN LOPEZ TUN  
**Carnet:** 1990-25-28572  
**Repositorio GitHub:** [https://github.com/blopezt16-tech/Proyecto-Final-Programacion](https://github.com/blopezt16-tech/Proyecto-Final-Programacion)  
**Carpeta de Google Drive (.apk y Documentación):** [https://drive.google.com/drive/folders/1_3r4XBnCKpM2r4CD77-SJ0FlU7mQcTU1?usp=sharing](https://drive.google.com/drive/folders/1_3r4XBnCKpM2r4CD77-SJ0FlU7mQcTU1?usp=sharing)  

---

## 1. Descripción General y Aplicación de POO

Este proyecto implementa un sistema completo de **Control de Gastos Personales** dividido en dos módulos desacoplados que aplican los pilares de la **Programación Orientada a Objetos (POO)** y arquitectura limpia:

1. **Abstracción y Contratos (`interface GastoRepository`):**  
   Define las operaciones CRUD asíncronas (`agregar`, `listar`, `buscarPorId`, `actualizar`, `eliminar`) mediante funciones `suspend`. Quien consume el contrato desconoce si los datos residen en memoria RAM (`GastoRepositoryEnMemoria`) o en una base de datos SQLite/Room (`GastoRepositoryRoom`).
2. **Encapsulamiento (`GastoRepositoryEnMemoria`):**  
   Oculta la lista interna `private val gastos = mutableListOf<Gasto>()` y el contador `private var siguienteId = 1`. Al listar gastos, devuelve una copia inmutable (`gastos.toList()`) para impedir modificaciones externas no autorizadas.
3. **Inmutabilidad y Modelado de Dominio (`data class Gasto` y `enum class Categoria`):**  
   - `Gasto` utiliza propiedades inmutables (`val`) y aprovecha el método `.copy(id = siguienteId)` generado por Kotlin para asignar identificadores sin mutar la instancia original.
   - `Categoria` modela un conjunto cerrado de categorías (`ALIMENTACION`, `TRANSPORTE`, `ESTUDIOS`, `ENTRETENIMIENTO`, `SALUD`, `OTROS`) sobrescribiendo `toString()` con su `nombreVisible` para alimentar directamente controles visuales como el `Spinner` de Android.
4. **Polimorfismo e Inyección de Dependencias (`class ControlGastos`):**  
   Recibe cualquier implementación de `GastoRepository` por constructor (`class ControlGastos(private val repositorio: GastoRepository)`), centralizando las reglas de negocio (`validar`, `registrar`, `total`, `totalPorCategoria`, `resumen`) sin depender de ninguna tecnología de interfaz gráfica (sin `println` ni `Toast`).

---

## 2. Estructura de los Proyectos

El repositorio está organizado en dos carpetas principales siguiendo los lineamientos de clase:

```text
proyecto-gastos/
├── kotlin/                                 # Proyecto Maven: Librería de dominio (gastos-core)
│   ├── pom.xml                             # Coordenadas: umg.edu.gt.progra2:gastos-core:1.0.0
│   ├── README.md                           # Documentación de la librería Kotlin
│   └── src/main/kotlin/edu/umg/programacion2/gastos/
│       ├── modelo/
│       │   ├── Categoria.kt                # Enum con nombreVisible y toString()
│       │   └── Gasto.kt                    # Data class (id, descripcion, monto, categoria, fecha)
│       ├── datos/
│       │   ├── GastoRepository.kt          # Interface con funciones suspend (DAO/Repositorio)
│       │   └── GastoRepositoryEnMemoria.kt # Implementación en memoria con mutableListOf
│       ├── servicio/
│       │   └── ControlGastos.kt            # Lógica de negocio, validaciones y formatearQuetzales()
│       └── consola/
│           └── Main.kt                     # Menú interactivo de consola para pruebas previas
│
├── android/                                # Proyecto Gradle: Aplicación Android (ControlGastos)
│   ├── settings.gradle.kts                 # Configura mavenLocal() para resolver gastos-core
│   ├── build.gradle.kts                    # Configuración raíz de Android Gradle Plugin y Kotlin
│   └── app/
│       ├── build.gradle.kts                # Activa ViewBinding y declara umg.edu.gt.progra2:gastos-core:1.0.0
│       ├── libs/
│       │   └── gastos-core-1.0.0.jar       # Respaldo local (Plan B) de la librería compilada
│       └── src/main/
│           ├── AndroidManifest.xml         # Configuración de la app y MainActivity
│           ├── java/edu/umg/controlgastos/
│           │   └── MainActivity.kt         # Controlador de UI que consume ControlGastos del .jar
│           └── res/layout/
│               ├── activity_main.xml       # Formulario de registro, resumen, totales por categoría y lista
│               └── item_gasto.xml          # Vista individual de cada gasto con botón de eliminación
│
├── documentacion.txt                       # Archivo de entrega con datos del estudiante y enlaces
└── README.md                               # Este documento explicativo
```

### Conexión entre ambos proyectos

```text
 1. Proyecto Kotlin (Maven)                 2. Repositorio Local Maven         3. Proyecto Android (Gradle)
 ┌────────────────────────────┐            ┌──────────────────────────┐       ┌──────────────────────────────────┐
 │ modelo/  Gasto, Categoria  │ mvn install│ ~/.m2/repository/        │  lee  │ settings.gradle.kts              │
 │ datos/   GastoRepository   │ ─────────► │  umg/edu/gt/progra2/     │ ◄──── │   repositories { mavenLocal() }  │
 │ servicio/ControlGastos     │            │   gastos-core-1.0.0.jar  │       │ app/build.gradle.kts             │
 └────────────────────────────┘            └──────────────────────────┘       │   implementation("...:1.0.0")    │
                                                                              └──────────────────────────────────┘
```

---

## 3. Flujo Básico de la Aplicación

1. **Inicialización (`onCreate`):**
   - La actividad principal (`MainActivity`) infla la vista mediante **View Binding** (`ActivityMainBinding.inflate(layoutInflater)`), eliminando llamadas manuales a `findViewById`.
   - Se instancia el servicio de dominio `private val control = ControlGastos(GastoRepositoryEnMemoria())` proveniente del archivo `.jar` de `gastos-core`.
   - El `Spinner` de categorías (`spCategoria`) se enlaza directamente a `Categoria.entries` mediante un `ArrayAdapter`, mostrando los nombres legibles (`"Alimentación"`, `"Transporte"`, etc.).
   - Se ejecuta `lifecycleScope.launch { actualizarResumen() }` para mostrar el estado inicial (`0 gastos · Total: Q 0.00`).

2. **Registro y Validación de un Gasto (`guardar()`):**
   - El usuario ingresa la **Descripción**, el **Monto (Q)** y selecciona la **Categoría**.
   - **Validación de entrada en UI:** Se convierte el texto del monto con `.toDoubleOrNull()`. Si es `null` (campo vacío o formato inválido), se marca el campo con `binding.etMonto.error = "Ingresa un monto válido"` y se aplica *early return* evitando un cierre inesperado (`NumberFormatException`).
   - **Construcción del objeto:** Se instancia `Gasto(descripcion, monto, categoria, fecha = fechaDeHoy())`.
   - **Validación de negocio en la librería:** Dentro de `lifecycleScope.launch`, se invoca `val error = control.registrar(gasto)`. `ControlGastos.validar()` verifica que la descripción no esté vacía (`isBlank()`), que el monto sea mayor a `0`, y que la fecha cumpla el patrón `yyyy-MM-dd`.
   - Si existe algún error de negocio, la librería devuelve el mensaje y Android lo muestra en un `Toast` sin guardar el registro.
   - Si es válido (`error == null`), el repositorio le asigna un `id` autogenerado, se limpian las cajas de texto y se actualiza la interfaz.

3. **Actualización de Resumen, Totales por Categoría y Listado (`actualizarResumen()`):**
   - Se invoca `control.resumen()` para actualizar `tvResumen` (ej. `"2 gastos · Total: Q 55.50"`).
   - Se invoca `control.totalPorCategoria()` para agrupar y mostrar los subtotales acumulados por cada categoría registrada usando `formatearQuetzales()`.
   - Se invoca `control.listar()` para renderizar las tarjetas de cada gasto registrado, permitiendo también eliminar cualquier registro mediante `control.eliminar(gasto.id)`.

---

## 4. Decisiones Tomadas en el Flujo de la Aplicación

1. **Separación estricta entre Lógica de Dominio e Interfaz Gráfica:**  
   Toda la validación de reglas de negocio (`validar`), cálculos matemáticos (`total`, `totalPorCategoria`) y formato de moneda (`formatearQuetzales`) viven en `gastos-core` (Kotlin puro sin dependencias de Android). Esto permite probar la misma lógica desde consola (`Main.kt`) y desde el teléfono (`MainActivity.kt`) sin duplicar código.
2. **Funciones `suspend` desde el primer incremento:**  
   Aunque `GastoRepositoryEnMemoria` opera en memoria RAM de forma instantánea, todos los métodos de `GastoRepository` y `ControlGastos` se diseñaron como `suspend`. De esta manera, cuando se integre **Room (SQLite)**, no habrá que modificar el contrato ni las llamadas en `MainActivity` (que ya usan `lifecycleScope.launch`).
3. **Null Safety (`toDoubleOrNull()`) y *Early Returns*:**  
   Se evitó el uso de `.toDouble()` directo sobre el texto del `EditText` para prevenir que la aplicación colapse cuando el usuario presiona "Guardar" con el monto en blanco. Asimismo, las validaciones devuelven `String?` (`null` si todo está correcto, o el mensaje de error), simplificando el flujo de control.
4. **Compatibilidad con Android API 24 (`minSdk = 24`):**  
   Dado que `java.time.LocalDate` requiere API 26 o superior, se optó por representar la fecha como `String` en formato ISO `"yyyy-MM-dd"` (que permite ordenamiento lexicográfico natural) y generarla en Android con `SimpleDateFormat("yyyy-MM-dd", Locale.US)`.
5. **Doble soporte de resolución para la librería (`mavenLocal()` + repositorio local de respaldo):**  
   El proyecto Android configura `mavenLocal()` en `settings.gradle.kts` tal como indica la guía oficial para leer `~/.m2/repository/umg/edu/gt/progra2/gastos-core/1.0.0/gastos-core-1.0.0.jar`, e incluye además el respaldo en `kotlin/repo` y `app/libs/gastos-core-1.0.0.jar` (Plan B de la guía) para garantizar que cualquier docente o compañero que clone el repositorio pueda compilarlo de inmediato.

---

## 5. Instrucciones de Compilación y Ejecución

### Paso 1: Instalar la librería Kotlin en Maven Local (`~/.m2`)
```bash
cd kotlin
mvn clean install
```

### Paso 2: Compilar y ejecutar la aplicación Android
1. Abrir la carpeta `android` en **Android Studio**.
2. Ejecutar **File → Sync Project with Gradle Files**.
3. Ejecutar en un emulador o dispositivo físico con Android 7.0 (API 24) o superior, o generar el APK desde terminal:
```bash
cd android
./gradlew assembleDebug
```
El archivo instalable se genera en `android/app/build/outputs/apk/debug/app-debug.apk`.

