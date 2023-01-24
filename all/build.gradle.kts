plugins {
    kotlin("jvm")
    `maven-publish`
}

dependencies {
    api(project(":dsl"))
    api(project(":ksp"))
    api(project(":metadata"))
}

publishing {
    publications {
        create<MavenPublication>("all") {
            artifactId = "kotlin-poet-dsl"
            from(components["java"])
        }
    }
}
