@file:Suppress("UnstableApiUsage")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    // 0.41.0-SNAPSHOT or 0.40.2
    id("de.fayard.refreshVersions") version "0.41.0-SNAPSHOT"


    // See https://dev.to/jmfayard/the-one-gradle-trick-that-supersedes-all-the-others-5bpg
    // See https://docs.gradle.com/enterprise/gradle-plugin/
    id("com.gradle.enterprise") version "3.9"
}

refreshVersions {
    versionsPropertiesFile = file("gradle/versions.properties")
    rejectVersionIf {
        candidate.stabilityLevel.isLessStableThan(current.stabilityLevel)
    }
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishOnFailure()
        publishAlwaysIf(System.getenv("CI") == "true")
        buildScanPublished {
            file("buildscan.log").appendText("${java.util.Date()} - $buildScanUri\n")
        }
    }
}



rootProject.name = "my-kotlin-scipts"

