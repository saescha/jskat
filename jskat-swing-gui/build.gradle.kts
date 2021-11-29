plugins {
    id("org.openjfx.javafxplugin")
}

dependencies {
    implementation(project(":jskat-base"))
    implementation("com.google.guava", "guava", "31.0.1-jre")
    implementation("com.miglayout", "miglayout-swing", "11.0")
}

javafx {
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing")
    version = "17.0.+"
}
