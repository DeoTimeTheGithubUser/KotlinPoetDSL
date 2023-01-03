plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "me.deotime"
version = "1.0.5-SNAPSHOT"

repositories {
    mavenCentral()
}

publishing {
    publications {
        create<MavenPublication>("dsl") {

            groupId = "me.deotime"
            artifactId = "kotlin-poet-dsl-dsl"

            from(components["java"])
        }
    }
}
