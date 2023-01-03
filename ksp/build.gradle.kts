plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(project(":dsl"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.0-1.0.6")
}

publishing {
    publications {
        create<MavenPublication>("ksp") {
            artifactId = "kotlin-poet-dsl-ksp"
            from(components["java"])
        }
    }
}
