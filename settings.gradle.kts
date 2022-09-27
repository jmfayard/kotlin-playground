@file:Suppress("UnstableApiUsage")

import de.fayard.refreshVersions.core.StabilityLevel

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        /** to use refreshVersions snapshots only */
        // maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.50.2"

    // See https://dev.to/jmfayard/the-one-gradle-trick-that-supersedes-all-the-others-5bpg
    // See https://docs.gradle.com/enterprise/gradle-plugin/
    id("com.gradle.enterprise") version "3.11.1"
}

refreshVersions {
    rejectVersionIf {
        // candidate.stabilityLevel.isLessStableThan(current.stabilityLevel)
        candidate.stabilityLevel != StabilityLevel.Stable
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



rootProject.name = "kotlin-playground"

