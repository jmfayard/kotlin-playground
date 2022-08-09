#!/usr/bin/env kotlin
// You can add an alias to your shell profile for convenience
// alias goto="path-to/my-kotlin-scripts/scripts/goto.main.kts"

@file:Repository("https://repo.maven.apache.org/maven2/")
@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")

// https://github.com/lordcodes/turtle
@file:DependsOn("com.lordcodes.turtle:turtle:0.7.0")

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptInput
import com.github.kinquirer.components.promptList
import com.lordcodes.turtle.ShellScript
import com.lordcodes.turtle.shellRun
import java.io.File
import java.util.Properties

val search = args.firstOrNull() ?: ""
val bookmarks = readBookmarks(filter = search)
when (bookmarks.size) {
    0 -> error("No bookmark found containing $search")
    1 -> openUrl(bookmarks.values.first())
    else -> {
        val keys = bookmarks.keys.toList()
        val key = promptFeatureName(keys)
        val url = bookmarks.getValue(key)
        openUrl(url)
    }
}

fun promptFeatureName(keys: List<String>) = KInquirer.promptList(
    message = "\$ goto $search ...  where?",
    choices = keys,
    viewOptions = listViewOptions()
)

fun promptGraphqlName(kind: String) = KInquirer.promptInput(
    message = "$kind name?",
    hint = "camelCase",
    validation = { "[a-z][a-zA-Z0-9]+".toRegex().matches(it) }
)

fun listViewOptions() = ListViewOptions(
    questionMarkPrefix = "",
    cursor = " 😎 ",
    nonCursor = "    ",
)

fun readBookmarks(filter: String): Map<String, String> =
    urlsPropertiesFiles()
        .readPropertiesFile()
        .filter { it.toString().contains(filter.trim()) }

fun File.readPropertiesFile(): Map<String, String> {
    check(extension == "properties") { "file $canonicalPath is not a properties file" }
    val properties = Properties()
    properties.load(reader())
    @Suppress("UNCHECKED_CAST")
    return (properties.toMap() as Map<String, String>)
        .filter { it.key.isNotBlank() && it.value.isNotBlank() }
        .toSortedMap()
}

fun openUrl(url: String) {
    println("Opening $url")
    shellRun { openUrl(url) }
    println("🎉 We hope you considered GOTO helpful! ")
}


infix fun Boolean.orFailWithMessage(message: String) {
    println("error: $message")
    if (not()) System.exit(1)
}

fun urlsPropertiesFiles(): File =
    try {
        val path = shellRun("git", listOf("rev-parse", "--show-toplevel"))
        File(path)
    } catch (e: Exception) {
        File(".").absoluteFile
    }.resolve("URLS.properties")
        .also {
            it.canRead() orFailWithMessage "Can't find a file ${it.canonicalPath}"
        }

fun ShellScript.openUrl(url: String): String {
    val osName: String = System.getProperty("os.name")
    return when {
        osName.contains("Windows") -> command("cmd.exe", listOf("/c", "start", url))
        osName.contains("Mac") -> command("open", listOf(url))
        else -> command("xdg-open", listOf(url))
    }
}
