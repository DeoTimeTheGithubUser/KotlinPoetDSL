plugins {
    id("java")
}

group = "me.deotime"
version = "1.1.0"

dependencies {
    implementation(project(":dsl"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.0-1.0.6")
}