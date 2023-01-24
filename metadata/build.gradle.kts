plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    implementation(project(":dsl"))
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")
}

publishing {
    publications {
        create<MavenPublication>("ksp") {
            artifactId = "kotlin-poet-dsl-metadata"
            from(components["java"])
        }
    }
}
