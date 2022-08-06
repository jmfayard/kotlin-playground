package parsemarkown

import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import java.io.File
import java.net.URI

class BookmarksTest : FunSpec({
    val sshUrl = "git@github.com:jmfayard/refreshVersions.git"
    val httpsUrl = "https://github.com/jmfayard/refreshVersions.git"

    test("Parsing git remote") {
        listOf(sshUrl, httpsUrl)
            .forAll {
                parseMarkdownRepo(it) shouldBe GitHubRepo("jmfayard", "refreshVersions")
            }

        listOf("https://gitlab.com/me/repo", "http://example.com")
            .forAll { parseMarkdownRepo(it) shouldBe null }

    }

    test("Suggesting GitHub URLs") {
        val githubUrls = parseMarkdownRepo(sshUrl)!!.bookmarks().map { it.url }.joinToString("\n")
        githubUrls shouldBe """
            https://github.com/jmfayard/refreshVersions
            https://github.com/jmfayard/refreshVersions/issues
            https://github.com/jmfayard/refreshVersions/pulls
            https://github.com/jmfayard/refreshVersions/actions
            https://github.com/jmfayard/refreshVersions/projects
            https://github.com/jmfayard/refreshVersions/wiki
        """.trimIndent()
    }

    @Language("Properties") val bookmarksContent = """
        awesome-contributing = https://github.com/mntnr/awesome-contributing
        awesome-readme = https://github.com/matiassingers/awesome-readme
        choosealicense = https://choosealicense.com
        github.about-readmes = https://help.github.com/articles/about-readmes/
        github.adding-a-license-to-a-repository = https://help.github.com/articles/adding-a-license-to-a-repository/
        github.adding-a-security-policy-to-your-repository = https://help.github.com/en/articles/adding-a-security-policy-to-your-repository
        github.building-a-strong-community = https://help.github.com/categories/building-a-strong-community/
        github.dependabot = https://github.com/dependabot
        i-sight.18-of-the-best-code-of-conduct-examples = https://i-sight.com/resources/18-of-the-best-code-of-conduct-examples/
        jekyllrb.structure = https://jekyllrb.com/docs/structure/
        markup.README = https://github.com/github/markup/blob/master/README.md#markups
        opensource = https://opensource.guide/
    """.trimIndent()

    val bookmarks = bookmarksContent.lines().map {
        val (name, url) = it.split("=")
        Bookmark(name.trim(), url.trim())
    }

    test("Creating URLs with names") {
        bookmarks.forAll {
            suggestBookmarkName(it.url) shouldBe it.name
        }
    }

    test("Suggesting names") {
        val urls = bookmarks.map { it.url }.shuffled()
        val expected = bookmarks
        suggestBookmarks(urls + urls) shouldBe expected
    }
})

// start group bookmark
data class Bookmark(val name: String, val url: String)

private data class GitHubRepo(val owner: String, val repo: String)

private val GitHubRepo.url: String get() = "https://github.com/$owner/$repo"

private fun GitHubRepo.bookmarks() = listOf(
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

private fun parseMarkdownRepo(input: String): GitHubRepo? {
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
