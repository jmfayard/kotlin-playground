#!/usr/bin/env kotlin
@file:Repository("https://repo.maven.apache.org/maven2/")
@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")

// https://github.com/lordcodes/turtle
@file:DependsOn("com.lordcodes.turtle:turtle:0.7.0")

import com.lordcodes.turtle.ShellScript
import com.lordcodes.turtle.shellRun
import kotlin.system.exitProcess

// customize me
val jiraUrl = "https://tignum.atlassian.net/browse"

// https://regexr.com/6rjbv
val jiraRegex = "^.*([A-Z]{2,}-[0-9]{2,}).*\$".toRegex()

val currentBranch = shellRun("git", listOf("rev-parse", "--abbrev-ref", "HEAD"))

val jiraId = jiraRegex.matchEntire(currentBranch)
    ?.destructured?.toList()?.firstOrNull()

println("branch: $currentBranch")
jiraId ?: kotlin.run {
    println("error: no JIRA id found in $currentBranch")
    exitProcess(1)
}

shellRun {
    val url = "$jiraUrl/$jiraId"
    println("🎉 Opening $url")
    openUrl(url)
}


fun ShellScript.openUrl(url: String): String {
    val osName: String = System.getProperty("os.name")
    return when {
        osName.contains("Windows") -> command("cmd.exe", listOf("/c", "start", url))
        osName.contains("Mac") -> command("open", listOf(url))
        else -> command("xdg-open", listOf(url))
    }
}