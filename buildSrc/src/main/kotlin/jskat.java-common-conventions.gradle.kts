plugins {
    // Apply the java Plugin to add support for Java.
    java
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

val JUNIT_VERSION = "5.9.2"

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("ch.qos.logback:logback-classic:1.4.6")
    implementation("com.google.guava:guava:31.1-jre")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${JUNIT_VERSION}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${JUNIT_VERSION}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${JUNIT_VERSION}")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
    withJavadocJar()
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}