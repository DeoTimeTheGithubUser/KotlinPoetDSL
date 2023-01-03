import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    `maven-publish`
    application
}

group = "me.deotime"

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

allprojects {
    repositories {
        mavenCentral()
    }

    dependencies {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.22")
        implementation("com.squareup:kotlinpoet:1.12.0")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {

            groupId = "me.deotime"
            artifactId = "kotlin-poet-dsl"

            from(components["java"])

            pom {
                name.set("kotlin-poet-dsl")
                description.set("DSL wrapper for the KotlinPoet library.")
            }
        }
    }
}
