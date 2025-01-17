/*
 * This file was generated by the Gradle 'init' task.
 *
 * This project uses @Incubating APIs which are subject to change.
 */

plugins {
    id("jskat.kotlin-application-conventions")

    id("org.openjfx.javafxplugin") version "0.0.14"
}

dependencies {
    implementation(project(":jskat-javafx-gui"))
}

javafx {
    modules = listOf("javafx.base", "javafx.fxml", "javafx.web", "javafx.swing")
    version = "20.0.+"
}

version = "0.22.0-SNAPSHOT"

val mainClassName = "org.jskat.JSkatKt"

application {
    // Define the main class for the application.
    mainClass.set(mainClassName)
}

tasks.register("fatjar", Jar::class.java) {

    manifest {
        attributes("Main-Class" to mainClassName)
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    archiveBaseName.set(rootProject.name)
    val os: OperatingSystem =
        org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem()
    if (os.isLinux) {
        archiveClassifier.set("linux")
    } else if (os.isMacOsX) {
        archiveClassifier.set("macos")
    } else if (os.isWindows) {
        archiveClassifier.set("windows")
    }

    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get()
        .onEach { println("add from dependencies : ${it.name}") }
        .map { if (it.isDirectory) it else zipTree(it) })
}
