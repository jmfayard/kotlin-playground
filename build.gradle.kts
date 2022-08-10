import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.squareup.sqldelight") version "1.5.3"
}

group = "de.fayard"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io") {
        content {
            includeGroupByRegex("com.github.*")
        }
    }
}

dependencies {
    testImplementation(Testing.junit.jupiter)
    testImplementation(Kotlin.test.junit5)
    testImplementation(Testing.kotest.runner.junit5)
    testImplementation(Testing.kotest.assertions.json)

    implementation(Square.okHttp3)
    // https://github.com/codeniko/JsonPathKt
    implementation("com.nfeld.jsonpathkt:jsonpathkt:_")

    implementation(Kotlin.stdlib.jdk8)

    implementation(KotlinX.datetime)
    implementation(KotlinX.collections.immutableJvmOnly)

    implementation(KotlinX.coroutines.core)
    implementation(KotlinX.coroutines.jdk8)

    implementation(KotlinX.serialization.core)
    implementation(KotlinX.serialization.json)
    implementation(KotlinX.serialization.properties)
    implementation("com.charleskorn.kaml:kaml:_")
    implementation("de.brudaswen.kotlinx.serialization:kotlinx-serialization-csv:_")

    implementation("org.hibernate.validator:hibernate-validator:_")

    implementation(Ktor.client.core)
    implementation(Ktor.client.json)
    implementation(Ktor.client.serialization)
    implementation(Ktor.client.okHttp)
    implementation(Ktor.client.logging)
    implementation("io.ktor:ktor-client-content-negotiation:_")
    implementation("io.ktor:ktor-serialization-kotlinx-json:_")

    implementation(Square.kotlinPoet)
    implementation(Square.sqlDelight.drivers.jdbc)

    implementation("com.github.kotlin-inquirer:kotlin-inquirer:_")
    implementation("com.github.ajalt.clikt:clikt:_")
    implementation("com.github.ajalt.mordant:mordant-jvm:_")

    implementation("org.hibernate.validator:hibernate-validator:_")
    implementation("io.arrow-kt:arrow-core:_")  // optional
}

tasks.test {
    useJUnitPlatform()
}

// Java version is set once for all in the file "system.properties"
val javaVersion = "17"

java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = javaVersion
}

application {
    mainClass.set("MainKt")
}
