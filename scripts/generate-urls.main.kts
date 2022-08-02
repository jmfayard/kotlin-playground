#!/usr/bin/env kotlin
@file:Repository("https://repo.maven.apache.org/maven2/")
@file:Repository("https://jitpack.io")
@file:DependsOn("com.github.ajalt.clikt:clikt:3.4.0")
@file:DependsOn("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")

// https://github.com/lordcodes/turtle
@file:DependsOn("com.lordcodes.turtle:turtle:0.7.0")

import com.github.kinquirer.KInquirer
import com.github.kinquirer.components.promptCheckboxObject
import com.github.kinquirer.components.promptConfirm
import com.github.kinquirer.components.promptInput
import com.github.kinquirer.components.promptInputNumber
import com.github.kinquirer.components.promptList
import com.github.kinquirer.core.Choice
import com.lordcodes.turtle.shellRun
import java.io.File
import java.math.BigDecimal
import java.net.URI

//pizzaDemo()
mainExtractUrls()

// start group MarkdownLink
fun extractLinksFromMarkdown(markdown: String): List<String> {
    val URL_REGEX = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
    val urlsOnly = URL_REGEX.findAll(markdown)
        .map { result ->
            result.value
        }
        .toList()
        .distinct()
    return urlsOnly
}
// end group MarkdownLink

// start group bookmark
data class Bookmark(val name: String, val url: String)

data class GitHubRepo(val owner: String, val repo: String)

val GitHubRepo.url: String get() = "https://github.com/$owner/$repo"

fun GitHubRepo.bookmarks() = listOf(
    Bookmark("repo.main", url),
    Bookmark("repo.issues", "$url/issues"),
    Bookmark("repo.pulls", "$url/pulls"),
    Bookmark("repo.actions", "$url/actions"),
    Bookmark("repo.projects", "$url/projects"),
    Bookmark("repo.wiki", "$url/wiki"),
)

fun List<Bookmark>.writetoFile(output: File) {
    val HEADER = """
        ## GOTO considered helpful!
    """.trimIndent()
    val content = this.joinToString("\n") {
        "${it.name} = ${it.url}"
    }
    output.writeText("$HEADER\n$content\n")
}

fun parseMarkdownRepo(input: String): GitHubRepo? {
    val s = input.trim().replace(".git", "")
    val path = s.substringAfter("github.com").substring(1)
    return when {
        s.contains("github.com").not() -> null
        else -> GitHubRepo(path.substringBefore("/"), path.substringAfter("/"))
    }
}

fun suggestBookmarks(urls: List<String>): List<Bookmark> {
    return urls.map { url ->
        Bookmark(suggestBookmarkName(url), url)
    }
        .distinctBy { it.url }
        .sortedBy { it.name }
}

fun suggestBookmarkName(url: String): String {
    val uri = URI(url)
    val githubRegex = "https://github.com/([^/]+)/([^/]+)/?(.*)$".toRegex()
    val hostname = uri.host.substringBeforeLast(".").substringAfter(".")
    val lastPath = uri.path.removeSuffix("/").substringAfterLast("/").substringBefore(".")
    return when {
        githubRegex.matches(url) -> {
            val (_, repo, path) = githubRegex.matchEntire(url)!!.destructured
            val cleanupPath = path.substringAfterLast("/").substringBefore(".")
            if (cleanupPath.isBlank()) repo else "$repo.$cleanupPath"
        }

        lastPath.isBlank() -> hostname
        else -> "$hostname.$lastPath"
    }
}
// end group bookmark


fun pizzaDemo() {
    data class PizzaOrder(
        val isDelivery: Boolean,
        val phoneNumber: String,
        val size: String,
        val quantity: BigDecimal,
        val toppings: List<String>,
        val beverage: String,
        val comments: String,
    )

    println("Hi, welcome to Kotlin's Pizza")
    val isDelivery: Boolean = KInquirer.promptConfirm("Is this for delivery?", default = false)
    val phoneNumber: String = KInquirer.promptInput(
        message = "What's your phone number?",
        filter = { s -> s.matches("\\d+".toRegex()) },
    )
    val size: String = KInquirer.promptList("What size do you need?", listOf("Large", "Medium", "Small"))
    val quantity: BigDecimal = KInquirer.promptInputNumber("How many do you need?")
    val toppings: List<String> = KInquirer.promptCheckboxObject(
        message = "What about the toppings?",
        choices = listOf(
            Choice("Pepperoni and cheese", "pepperonicheese"),
            Choice("All dressed", "alldressed"),
            Choice("Hawaiian", "hawaiian"),
        ),
    )
    val beverage: String = KInquirer.promptList("You also get a free 2L beverage", listOf("Pepsi", "7up", "Coke"))
    val comments: String = KInquirer.promptInput(
        message = "Any comments on your purchase experience?",
        hint = "Nope, all good!",
        default = "Nope, all good!",
    )

    val order = PizzaOrder(
        isDelivery = isDelivery,
        phoneNumber = phoneNumber,
        size = size,
        quantity = quantity,
        toppings = toppings,
        beverage = beverage,
        comments = comments,
    )

    println("====== Order receipt ======")
    println(order)
}

fun File.readable() = also {
    check(canRead()) { "Can't read file $canonicalPath" }
}

fun mainExtractUrls() {
    val gitDirectory = shellRun("git", listOf("rev-parse", "--show-toplevel"))
    val file = File("$gitDirectory/README.md").readable()
    println("Reading URLs from file ${file.canonicalPath}")
    val output = File("$gitDirectory/URLS.properties")
    val links = extractLinksFromMarkdown(file.readText())
    val readmeBookmarks = suggestBookmarks(links)

    val origins = shellRun { git.gitCommand(listOf("remote")) }.lines()

    val githubBookmarks = origins.mapNotNull { remote ->
        val input = shellRun {
            git.gitCommand(listOf("remote", "get-url", remote))
        }
        parseMarkdownRepo(input)
    }.flatMap { it.bookmarks() }

    val bookmarks = (githubBookmarks + readmeBookmarks).distinctBy { it.url }
    bookmarks.writetoFile(output)
    println("Updated file ${output.canonicalPath}")

    println("cp \$HOME/tignum/backend-tignum-x/goto .")
}
