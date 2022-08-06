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
    val okhttp = CsvColumns.csvLine(
        mavenCoordinates = "com.squareup.okhttp3:okhttp:4.5",
        propertyName = "Square.okHttp3.okHttp",
        availableVersions = listOf("4.6", "4.7")
    )
    val okhttpNoAvailable = CsvColumns.csvLine(
        mavenCoordinates = "com.squareup.okhttp3:okhttp:4.5",
        propertyName = "Square.okHttp3.okHttp",
        availableVersions = emptyList()
    )
    val okhttpNoVersion = CsvColumns.csvLine(
        mavenCoordinates = "com.squareup.okhttp3:okhttp",
        propertyName = "Square.okHttp3.okHttp",
        availableVersions = emptyList()
    )
    val picasso = CsvColumns.csvLine(
        mavenCoordinates = "com.squareup.picasso:picasso:1.0",
        propertyName = "Square.picasso",
        availableVersions = emptyList()
    )

    context("validating properties") {
        test("maven") {
            val expected = Maven("com.squareup.okhttp3", "okhttp")
            okhttp.maven shouldBe expected
            okhttpNoVersion.maven shouldBe expected
        }

        test("version") {
            okhttp.version shouldBe "4.5"
            okhttpNoVersion.version shouldBe null
        }

        test("invalid csv lines") {
            shouldThrowAny {
                CsvColumns.csvLine("mavenCoordinates", "propertyName")
            }.message.shouldStartWith("Invalid mavenCoordinates")
        }

        test("groupNameVersion") {
            okhttp.groupNameVersion shouldBe "com.squareup.okhttp3:okhttp:4.5"
            okhttpNoVersion.groupNameVersion shouldBe "com.squareup.okhttp3:okhttp"
        }

    }

    context("reading CSV header") {
        with(CsvLine) {
            test("all columns") {
                val header = "mavenCoordinates | propertyName | availableVersions"
                parseHeader(header) shouldBe listOf(CsvColumns.mavenCoordinates, CsvColumns.propertyName, CsvColumns.availableVersions)
            }

            test("some columns") {
                val header = "propertyName | mavenCoordinates"
                parseHeader(header) shouldBe listOf(CsvColumns.propertyName, CsvColumns.mavenCoordinates)
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
                "${CsvColumns.mavenCoordinates}  |   ${CsvColumns.propertyName}   |   ${CsvColumns.availableVersions}"
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
            csvLines shouldBe listOf(
                okhttp.copy(lineNumber = 3),
                okhttpNoVersion.copy(lineNumber = 4),
                picasso.copy(lineNumber = 5),
            )
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
            fun CsvLine.format() =
                "$propertyName  |  $mavenCoordinates | ${availableVersions}"

            val fileContent = """
            propertyName | mavenCoordinates | availableVersions
            ; some comment
            ${okhttp.format()}
            ${okhttpNoVersion.format()}
            ${picasso.format()}
            """.trimIndent()

            val actual = CsvLine.readCsv(fileContent)
            actual shouldBe listOf(
                okhttp.copy(lineNumber = 3),
                okhttpNoVersion.copy(lineNumber = 4),
                picasso.copy(lineNumber = 5),
            )
        }

        test("missing column") {
            fun CsvLine.format() =
                "$mavenCoordinates | $propertyName"

            val fileContent = """
            mavenCoordinates | propertyName
            ; some comment
            ${okhttp.format()}
            ${okhttpNoVersion.format()}
            ${picasso.format()}
            """.trimIndent()

            val actual = CsvLine.readCsv(fileContent)
            actual shouldBe listOf(
                okhttpNoAvailable.copy(lineNumber = 3),
                okhttpNoVersion.copy(lineNumber = 4),
                picasso.copy(lineNumber = 5),
            )
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

data class Maven(
    val group: String,
    val name: String
)
