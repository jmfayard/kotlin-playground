package kotest

import io.kotest.data.Table3
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TableTesting {

    @Test
    fun `kotest forall`() {
        // https://kotest.io/docs/assertions/inspectors.html
        val regexUrl = Regex("^https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_+.~#?&/=]*\$")
        val validUrls = """
            http://example.com
            https://example.com
            https://api.github.com/v1/pull_request/7891?limit=3
            http://password@patrick:example.com/endpoint?a=bde#ab
        """.trimIndent().lines().map(String::trim)
        validUrls.forAll { regexUrl.matches(it) shouldBe true }
    }

    @Test
    fun `kotest table header row forall`() {
        val table: Table3<String, String, String> = table(
            headers("mavenDependency", "kotlinName", "versionName"),
            row("io.kotest:kotest-core", "Testing.kotest.core", "version.kotest"),
            row("io.kotest:kotest-assertions-arrow", "Testing.kotest.assertions.arrow", "version.kotest"),
        )
        table.forAll { mavenDependency, kotlinName, versionName ->
            listOf(mavenDependency, kotlinName, versionName).all { it.contains("kotest") } shouldBe true
        }
    }

    @Test
    @Disabled
    fun readingFromCsv() {
        // see ticket https://github.com/kotest/kotest/issues/3179
    }

}
