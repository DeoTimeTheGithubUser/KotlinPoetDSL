import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    `maven-publish`
    application
}

group = "me.deotime"
version = "1.0.2"

repositories {
    mavenCentral()
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.22")
    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.22-1.0.8")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("maven") {

            groupId = "me.deotime"
            artifactId = "kotlin-poet-dsl"
            version = "1.0.2"

            from(components["java"])

            pom {
                name.set("kotlin-poet-dsl")
                description.set("DSL wrapper for the KotlinPoet library.")
            }
        }
    }
}
