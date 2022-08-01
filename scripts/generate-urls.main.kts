#!/usr/bin/env kotlin


// start group regex
val URL_REGEX = "https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()
val URL_TITLE_REGEX = "\\[([\\w\\s\\d]+)]\\((https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|])\\)".toRegex()
// end group regex

// start group MarkdownLink
data class MarkdownLink(val name: String?, val url: String)

fun extractLinksFromMarkdown(markdown: String): List<MarkdownLink> {

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


println("ok")