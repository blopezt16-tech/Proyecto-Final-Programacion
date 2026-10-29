pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()   // 👈 NUEVO: busca también en ~/.m2 (donde quedó gastos-core)
        maven { url = uri("../kotlin/repo") } // Respaldo automático del repositorio si no se ha corrido mvn install
    }
}

rootProject.name = "ControlGastos"
include(":app")

