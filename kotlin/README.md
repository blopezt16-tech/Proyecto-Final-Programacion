# Clase 12 - gastos-core

Librería **Kotlin puro** (sin nada de Android) con el modelo del dominio de la App de Gastos.
Se prueba en consola y luego se instala con `mvn install` para que el proyecto Android la use
como una dependencia más, igual que usamos el driver de MySQL en la Clase 5.

```
Proyecto Kotlin (Maven)            ~/.m2/repository             Proyecto Android (Gradle)
clase12-gastos-core  --mvn install-->  gastos-core-1.0.0.jar  <--mavenLocal()--  app/
```

## Estructura

```
src/main/kotlin/edu/umg/programacion2/gastos/
├── modelo/Categoria.kt                 -> enum class (alimenta el Spinner en Android)
├── modelo/Gasto.kt                     -> data class: id, descripcion, monto, categoria, fecha
├── datos/GastoRepository.kt            -> interface con funciones suspend (contrato)
├── datos/GastoRepositoryEnMemoria.kt   -> implementacion con mutableListOf
├── servicio/ControlGastos.kt           -> validar, registrar, total, totalPorCategoria, resumen
└── consola/Main.kt                     -> menu de consola (Android NO lo usa)
```

## Coordenadas de la librería

| groupId | artifactId | version |
|---|---|---|
| `umg.edu.gt.progra2` | `gastos-core` | `1.0.0` |

## Comandos

```bash
# Probar en consola
mvn compile exec:java

# Instalar la librería en el repositorio local (~/.m2) para que Android la encuentre
mvn clean install
```

Después de `mvn install` debe existir:

- macOS / Linux: `~/.m2/repository/umg/edu/gt/progra2/gastos-core/1.0.0/gastos-core-1.0.0.jar`
- Windows: `%USERPROFILE%\.m2\repository\umg\edu\gt\progra2\gastos-core\1.0.0\gastos-core-1.0.0.jar`

La guía paso a paso para conectarla con Android está en
`Clase 12/Clase12_Guia_Conectar_Kotlin_Android.md` (carpeta del curso).

## Requisitos

- JDK 11 o superior. Kotlin 2.1.20 lo descarga Maven.
- El proyecto Android debe usar Kotlin **2.0 o superior** (probado con 2.0.21).
