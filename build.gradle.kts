import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    `maven-publish`
    application
}

allprojects {

    group = "me.deotime"
    version = project.property("version") ?: error("Version is required in gradle.properties")

    repositories {
        mavenCentral()
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.languageVersion = "1.8"
    }


    dependencies {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.22")
        implementation("com.squareup:kotlinpoet:1.12.0")
    }

    afterEvaluate {
        publishing {
            repositories {
                maven {
                    name = "deotime"
                    url = uri("https://repo.q64.io/deotime")
                    credentials {
                        username = "deotime"
                        password = System.getenv("RAIN_REPO_PW")
                    }
                }
            }
        }
    }
}


