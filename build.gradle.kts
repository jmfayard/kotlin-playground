import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("com.squareup.sqldelight")

    // https://www.apollographql.com/docs/kotlin/
    id("com.apollographql.apollo3")
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

    implementation("com.apollographql.apollo3:apollo-runtime:")
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

tasks.register("fixScripts") {
    group = "application"
    description = "Fix Kotlin Scripts (permissions, bash wrapper)"

    fun bashWrapper(name: String): String = """
        #!/usr/bin/env bash
        which kotlin >/dev/null || {
          echo "Install Kotlin compiler from https://kotlinlang.org/docs/command-line.html"
          exit 1
        }
        kotlin $name.main.kts
    """.trimIndent()


    doFirst {
        fun List<File>.log(message: String) =
            println("$message: ${joinToString { it.name }}")

        val kotlinScripts = file("scripts")
            .listFiles { file -> file.extension == "kts" }
            .toList()
            .also { it.log("kotlinScripts") }

        val notExecutable = kotlinScripts
            .filterNot { it.canExecute() }
        if (notExecutable.isEmpty()) {
            println("OK: all Kotlin scripts are executables")
        } else {
            println("FIX permissions for: " + notExecutable.joinToString { it.name })
            notExecutable.forEach { file ->
                file.setExecutable(true)
            }
        }

        val missingBashWrappers = kotlinScripts
            .mapNotNull { file ->
                file.resolveSibling(file.name.removeSuffix(".main.kts"))
                    .takeIf { it.canRead().not() }
            }
        if (missingBashWrappers.isEmpty()) {
            println("OK: all Kotlin scripts have a Bash wrapper")
        } else {
            missingBashWrappers.forEach { file ->
                println("FIX: creating Bash wrapper at ${file.canonicalPath}")
                file.writeText(bashWrapper(file.canonicalPath))
                file.setExecutable(true)
            }
        }
    }
}

apollo {
    packageName.set("github.graphql")
    schemaFile.set(file("src/test/resources/github/schema.graphql"))
}