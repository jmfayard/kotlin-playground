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
        extractLinksFromMarkdown(validUrlsMarkdown) shouldBe validUrls
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
        extractLinksFromMarkdown(markdownText)
            .shouldBe(validUrls)
    }
})

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
