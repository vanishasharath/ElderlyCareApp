pluginManagement {
    repositories {
        google()
        mavenCentral() // This line tells Gradle where to find the Gemini SDK
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral() // This line is also needed here
    }
}
rootProject.name = "ElderlyCareAppnoai" // Use your project's root name
include(":app")
