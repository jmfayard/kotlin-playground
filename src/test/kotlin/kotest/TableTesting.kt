package kotest

import io.kotest.assertions.asClue
import io.kotest.data.Headers3
import io.kotest.data.Row3
import io.kotest.data.Table3
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.inspectors.forAll
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import java.io.File

val testResources: File = File("src/test/resources").apply(File::shouldExist)

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

    val header = "id, login, location, bio"

    @Test
    fun `table pojo`() {
        val table = table(
            headers("id", "login", "bio"),
            row(4, "Jean-Michel Fayard", Bio.Jmfayard),
            row(6, "Louis CAD", Bio.LouisCAD)
        )

        // forAll
        table.forAll { id, login, bio ->
            id shouldBeGreaterThan 0
            login shouldNotBe ""
        }

        // write to CSV
        val tableFile = testResources.resolve("kotest/write.table")
        table.writeTo(tableFile)

        // read from CSV
        val tableFromResources = table(
            source = tableFile,
            headers = headers("id", "login", "bio"),
            transform = { a: String, b: String, c: String ->
                row(a.toInt(), b, Bio.valueOf(c))
            }
        )
        println(tableFromResources)
        tableFromResources shouldBe table
    }
}

enum class Bio {
    Jmfayard, LouisCAD
}

private const val separator = "|"

fun <A, B, C> Table3<A, B, C>.writeTo(destination: File) {
    destination.extension shouldBe "table"
    val rowsContent = rows.joinToString("\n") { it.values().joinToString(separator) }
    val header = headers.values().joinToString(separator)
    destination.writeText(header + "\n" + rowsContent)
}

fun <A, B, C> table(
    source: File,
    headers: Headers3,
    transform: (String, String, String) -> Row3<A, B, C>
): Table3<A, B, C> {
    source.shouldExist()
    source.extension shouldBe "table"
    val lines = source.readText().lines()
    lines.shouldNotBeEmpty()
    val header = lines.first().replace(" ", "").split("|")
    headers.values() shouldContainAll header
    val rows = lines.drop(1)
        .filter { it.contains("|") }
        .map { line -> line.split("|").map(String::trim) }
    val size = headers.values().size

    "rows should have the same size as the header".asClue {
        rows.filter { it.size < size }.take(3).shouldBeEmpty()
    }
    val transformedRows = rows.map { list ->
        val (a, b, c) = list
        transform(a, b, c)
    }
    return table(headers, *transformedRows.toTypedArray())
}
