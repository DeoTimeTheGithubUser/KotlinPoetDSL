plugins {
    kotlin("jvm")
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("dsl") {
            artifactId = "kotlin-poet-dsl-dsl"
            from(components["java"])
        }
    }
}
