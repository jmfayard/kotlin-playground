#!/usr/bin/env kotlin
@file:Repository("https://repo.maven.apache.org/maven2/")
@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")

// https://github.com/lordcodes/turtle
@file:DependsOn("com.lordcodes.turtle:turtle:0.7.0")

import Add_main.Command.*
import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.ListViewOptions
import com.github.kinquirer.components.promptInput
import com.github.kinquirer.components.promptList
import java.io.File

val command = chooseCommand()

when (command) {
    Feature -> addFeature()
    Query, Mutation -> addGraphQL(command)
    Issue, Jira -> createIssue(command)
}

fun addFeature() {
    val featureName: String =
        KInquirer.promptInput(
            message = "Feature name?",
            hint = "lowercase only",
            validation = { s -> s.all { it in 'a'..'z' } }
        )
    Feature(featureName).createDirsIfNecessary()
}

fun addGraphQL(command: Command) {
    val kind = if (command == Query) "Query" else "Mutation"
    val regex = "[a-z][a-zA-Z0-9]+".toRegex()
    val graphqlName: String =
        KInquirer.promptInput(
            message = "$kind name?",
            hint = "camelCase",
            validation = { regex.matches(it) }
        )
    val featureName: String =
        KInquirer.promptList(
            message = "Feature name?",
            choices = scanFeatures()
        )
    Feature(featureName).createGraphql(featureName, graphqlName, command == Query)
}

fun createIssue(command: Command) {
    println("createIssue $command")
}

enum class Command {
    Feature, Query, Mutation, Issue, Jira;

    companion object {
        fun choices() = values().map { it.name }
    }
}

fun chooseCommand(): Command {
    val continent: String = KInquirer.promptList(
        message = "What do you want to add:",
        choices = Command.choices(),
        hint = "press Enter to pick",
        pageSize = 6,
        viewOptions = listViewOptions()
    )
    return valueOf(continent)
}

fun listViewOptions() = ListViewOptions(
    questionMarkPrefix = "🌍",
    cursor = " 😎 ",
    nonCursor = "    ",
)

// start group Feature
fun xFolder(path: String) = File("/Users/jmfayard/tignum/backend-tignum-x/$path")

fun scanFeatures(): List<String> {
    val dir = xFolder("src/main/kotlin/com/tignum/backend/features/")
    return dir.listFiles()
        .filter { it.isDirectory }
        .map { it.name }
}

data class Feature(val name: String) {
    val repo = xFolder(".").canonicalFile
    val main = xFolder("src/main/kotlin/com/tignum/backend/features/$name")
    val mainData = main.resolve("data")
    val mainDomain = main.resolve("domain")
    val mainGraphql = main.resolve("graphql")
    val test = xFolder("src/test/kotlin/com/tignum/backend/features/$name")
    val testData = test.resolve("data")
    val testGraphql = test.resolve("graphql")
    val testDomain = test.resolve("domain")
    val resources = xFolder("src/main/resources/$name")
    fun allDirs(): List<File> =
        listOf(main, mainData, mainGraphql, mainDomain, test, testDomain, testData, testGraphql, testDomain, resources)

    fun createDirsIfNecessary() {
        println("checking directories for feature $name in $repo")
        allDirs().forEach { folder ->
            if (folder.exists().not()) {
                println("Creating folder ${folder.relativeTo(repo)}")
            }
        }
    }
}
// end group Feature
