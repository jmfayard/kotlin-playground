package parsemarkown

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MarkdownLinkTest : StringSpec({
    val validUrlsMarkdown = """
        https://tignum.atlassian.net/wiki/spaces/BAC/pages/2121072688/Project+Configuration
        https://tignum.atlassian.net/wiki/spaces/BAC/pages/2209447937/GraphQL
        https://example.com?something=good&other-thing=bad
    """.trimIndent()
    val validUrls = validUrlsMarkdown.lines().map(String::trim)

    "URL only" {
        extractLinksFromMarkdown(validUrlsMarkdown).map { it.url } shouldBe validUrls
    }

    val markdownText = """
        [first link](https://tignum.atlassian.net/wiki/spaces/BAC/pages/2121072688/Project+Configuration)
        bla bla
        [second link](https://tignum.atlassian.net/wiki/spaces/BAC/pages/2209447937/GraphQL)
        really cool man
        [third link](https://example.com?something=good&other-thing=bad)
        duplicate
        [duplicate first link](https://tignum.atlassian.net/wiki/spaces/BAC/pages/2121072688/Project+Configuration)
    """

    "URL and name" {
        val linkNames = listOf("first link", "second link", "third link")
        val expected = linkNames.zip(validUrls) { name, url -> MarkdownLink(name, url) }
        extractLinksFromMarkdown(markdownText)
            .shouldBe(expected)
    }
})

// start group MarkdownLink
data class MarkdownLink(val name: String?, val url: String)

fun extractLinksFromMarkdown(markdown: String): List<MarkdownLink> {
    // start group regex
    val URL_REGEX = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
    val URL_TITLE_REGEX = "\\[([\\w\\s\\d]+)]\\((https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])\\)".toRegex()
    // end group regex

    val withNames = URL_TITLE_REGEX.findAll(markdown)
        .map { result: MatchResult ->
            val (text, url) = result.destructured
            MarkdownLink(text, url)
        }
        .toList()
    val urlsOnly = URL_REGEX.findAll(markdown)
        .map { result ->
            MarkdownLink(null, result.value)
        }.toList()
    return (withNames + urlsOnly).distinctBy { it.url }
}
// end group MarkdownLink
