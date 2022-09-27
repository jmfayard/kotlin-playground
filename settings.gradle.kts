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
    /**
     * BUG: everything is stsable now!
    > Task :refreshVersions
    rVI: Version(value=5.9.1) stabilityLevel=Stable
    rVI: Version(value=1.7.20-Beta) stabilityLevel=Stable
    rVI: Version(value=1.7.20-RC) stabilityLevel=Stable
    ..
    **/
    rejectVersionIf {
        // candidate.stabilityLevel.isLessStableThan(current.stabilityLevel)
        println("rVI: $candidate stabilityLevel=${candidate.stabilityLevel}")
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

