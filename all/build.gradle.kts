plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    api(project(":dsl"))
    api(project(":ksp"))
}

publishing {
    publications {
        create<MavenPublication>("all") {
            artifactId = "kotlin-poet-dsl"
            from(components["java"])
        }
    }
}
