plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(project(":dsl"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.8.0-1.0.8")
}

publishing {
    publications {
        create<MavenPublication>("ksp") {
            artifactId = "kotlin-poet-dsl-ksp"
            from(components["java"])
        }
    }
}
