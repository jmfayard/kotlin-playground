package csv

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith
import java.io.File

class CsvLineTest : FunSpec({
    val config = RefreshVersionsCsvConfig

    val okhttp = config.csvLine(
        mavenCoordinates = "com.squareup.okhttp3:okhttp:4.5",
        propertyName = "Square.okHttp3.okHttp",
        availableVersions = listOf("4.6", "4.7")
    )
    val okhttpNoAvailable = config.csvLine(
        mavenCoordinates = "com.squareup.okhttp3:okhttp:4.5",
        propertyName = "Square.okHttp3.okHttp",
        availableVersions = emptyList()
    )
    val okhttpNoVersion = config.csvLine(
        mavenCoordinates = "com.squareup.okhttp3:okhttp",
        propertyName = "Square.okHttp3.okHttp",
        availableVersions = emptyList()
    )
    val picasso = config.csvLine(
        mavenCoordinates = "com.squareup.picasso:picasso:1.0",
        propertyName = "Square.picasso",
        availableVersions = emptyList()
    )

    test("CsvLine") {
        okhttp.toString() shouldBe """CsvLine(mapOf("propertyName" to "Square.okHttp3.okHttp", "mavenCoordinates" to "com.squareup.okhttp3:okhttp:4.5", "availableVersions" to "4.6 4.7"))"""

        okhttp shouldBe config.csvLine(
            mavenCoordinates = "com.squareup.okhttp3:okhttp:4.5",
            propertyName = "Square.okHttp3.okHttp",
            availableVersions = listOf("4.6", "4.7"),
            lineNumber = 42,
        )
    }

    context("reading CSV header") {
        with(CsvLine) {
            test("all columns") {
                val header = "mavenCoordinates | propertyName | availableVersions"
                parseHeader(header) shouldBe listOf(config.mavenCoordinates, config.propertyName, config.availableVersions)
            }

            test("some columns") {
                val header = "propertyName | mavenCoordinates"
                parseHeader(header) shouldBe listOf(config.propertyName, config.mavenCoordinates)
            }

            test("invalid columns") {
                val header = "propertyName | coffee | mavenCoordinates | orangina"
                shouldThrowAny {
                    parseHeader(header)
                }.message.shouldStartWith("Invalid columns [coffee, orangina]")
            }
        }
    }

    context("writeCsv") {
        val lines = listOf(okhttp, okhttpNoVersion, picasso)
        fun String.trimLines() = lines().map(String::trim).joinToString("\n").trim()

        val comments = """
            ; first comment
            ; second comment
        """.trimIndent()

        test("write to file writeCsvLines.csv") {
            val file = File("src/test/resources/csv/writeCsvLines.csv")
            file.writeCsvLines(lines, comments)
        }

        test("all columns") {
            val header = with(CsvLine) {
                "${config.mavenCoordinates}  |   ${config.propertyName}   |   ${config.availableVersions}"
            }
            val expectedCsv = """
                mavenCoordinates                  |  propertyName           |  availableVersions  |
                ; first comment
                ; second comment
                com.squareup.okhttp3:okhttp:4.5   |  Square.okHttp3.okHttp  |  4.6 4.7            |
                com.squareup.okhttp3:okhttp       |  Square.okHttp3.okHttp  |                     |
                com.squareup.picasso:picasso:1.0  |  Square.picasso         |                     |
            """.trimIndent()
            val actual = CsvLine.writeCsv(header, lines, comments).trimLines()

            actual shouldBe expectedCsv
        }

        test("only one column") {
            val header = "propertyName |"
            val expectedCsv = """
                propertyName           |
                ; first comment
                ; second comment
                Square.okHttp3.okHttp  |
                Square.okHttp3.okHttp  |
                Square.picasso         |
            """.trimIndent()
            val actual = CsvLine.writeCsv(header, lines, comments).trimLines()

            actual shouldBe expectedCsv
        }
    }

    context("readCsv") {
        test("read from file readCsvLines.csv") {
            val file = File("src/test/resources/csv/readCsvLines.csv")
            val csvLines: List<CsvLine> = file.readCsvLines()
            csvLines shouldBe listOf(okhttp, okhttpNoVersion, picasso)
            csvLines.map { it.lineNumber } shouldBe listOf(3, 5, 7)
        }

        test("read and transform from file readCsvLines.csv") {
            val expected = DependencyLine(mavenCoordinates = "com.squareup.okhttp3:okhttp:4.5", propertyName = "Square.okHttp3.okHttp", availableVersions = "4.6 4.7")
            val file = File("src/test/resources/csv/readCsvLines.csv")
            val csvLines = file.readAndTransformCsvLines(config)
            csvLines.firstOrNull() shouldBe expected
        }


        test("read from an non existing file should initialize it") {
            val file = File("src/test/resources/csv/${System.currentTimeMillis()}.csv")
            try {
                shouldNotThrowAny {
                    file.readCsvLines() shouldBe emptyList()
                }
                file.readText() shouldContain "automatically generated"
            } finally {
                file.delete()
            }
        }

        test("all columns present") {
            fun CsvLine.format() = with(config.transform(this)) {
                "$propertyName  |  $mavenCoordinates | ${availableVersions}"
            }

            val fileContent = """
            propertyName | mavenCoordinates | availableVersions
            ; some comment
            ${okhttp.format()}
            ${okhttpNoVersion.format()}
            ${picasso.format()}
            """.trimIndent()

            val actual = CsvLine.readCsv(fileContent)
            actual shouldBe listOf(okhttp, okhttpNoVersion, picasso)
        }

        test("missing column") {
            fun CsvLine.format() = with(config.transform(this)) {
                "$mavenCoordinates | $propertyName"
            }

            val fileContent = """
            mavenCoordinates | propertyName
            ; some comment
            ${okhttp.format()}
            ${okhttpNoVersion.format()}
            ${picasso.format()}
            """.trimIndent()

            val actual = CsvLine.readCsv(fileContent)
            actual shouldBe listOf(okhttpNoAvailable, okhttpNoVersion, picasso)
        }

        test("empty content") {
            val expectedMessage = "empty content"

            shouldThrowMessage(expectedMessage) {
                CsvLine.readCsv("")
            }
        }
        test("empty content - with comments") {
            val expectedMessage = "empty content"
            shouldThrowMessage(expectedMessage) {
                val fileContent = """
                ; no header here
                """.trimIndent()
                CsvLine.readCsv(fileContent)
            }
        }
    }

    test("reformatComments") {
        val initialComments = """


            ;;;; hello
            //   bonjour
            ##   hola
              this is cool


        """.trimIndent()

        val expectedComment = """
            ; hello
            ; bonjour
            ; hola
            ; this is cool
        """.trimIndent()

        CsvLine.reformatComment(initialComments) shouldBe expectedComment
    }
})

