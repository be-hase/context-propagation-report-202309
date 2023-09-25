pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
    }
}

rootProject.name = "context-propagation-report-202309"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":coroutines-issue")
include(":reactor-netty-issue")
