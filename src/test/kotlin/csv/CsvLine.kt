package csv

import csv.CsvColumns.allColumns
import csv.CsvColumns.availableVersions
import csv.CsvColumns.commentPrefix
import csv.CsvColumns.mavenCoordinates
import csv.CsvColumns.propertyName
import csv.CsvColumns.separator
import java.io.File

fun File.writeCsvLines(lines: List<CsvLine>, comments: String) {
    val fileContent = if (canRead()) readText() else CsvColumns.allColumns.joinToString("  ${CsvColumns.separator}  ")
    val newContent = CsvLine.writeCsv(fileContent, lines, comments)
    writeText(newContent)
}

fun File.readCsvLines(): List<CsvLine> {
    val fileContent = if (canRead()) {
        readText()
    } else {
        val header = CsvColumns.allColumns.joinToString("  ${CsvColumns.separator}  ")
        val defaultFileContent = """
            $header
            ;automatically generated. you can customize the columns
            """.trimIndent()
        writeText(defaultFileContent)
        defaultFileContent
    }
    return CsvLine.readCsv(fileContent)
}

/**
 * Configure the CSV Format
 *
 * Comments start with [commentPrefix] and values are separated by [separator]
 *
 * Possible columns are defined by [allColumns]
 *
 * [mavenCoordinates] group:name or group:name:version
 * [propertyName] key from 'versions.properties' or 'libs.versions.toml' or Kotlin syntax like 'Kotlinx.serialization.core'
 * [availableVersions] availableVersions found by refreshVersions while scanning maven repositories
 * **/
object CsvColumns {
    const val commentPrefix = ";"
    const val separator = "|"

    val allColumns = mutableSetOf<String>()

    private val mapKeyIsValue: Map<String, String> =
        mutableMapOf<String, String>().withDefault { column: String ->
            allColumns += column
            column
        }

    val propertyName by mapKeyIsValue
    val mavenCoordinates by mapKeyIsValue
    val availableVersions by mapKeyIsValue

    fun csvLine(
        mavenCoordinates: String,
        propertyName: String,
        availableVersions: List<String> = emptyList(),
        lineNumber: Int? = null,
    ): CsvLine = CsvLine(
        data = mapOf(
            CsvColumns.propertyName to propertyName,
            CsvColumns.mavenCoordinates to mavenCoordinates,
            CsvColumns.availableVersions to availableVersions.joinToString(" ")
        ),
        lineNumber = lineNumber
    )
}


data class CsvLine(
    val data: Map<String, String>,
    val lineNumber: Int? = null,
) {
    val dataOrEmpty = data.withDefault { "" }

    /** See [CsvLine] **/
    val mavenCoordinates: String by dataOrEmpty
    val propertyName: String by dataOrEmpty
    val availableVersions: String by dataOrEmpty

    private val split = mavenCoordinates.split(":")
        .map { it.replace(" ", "") }
        .also { require(it.size >= 2) { "Invalid mavenCoordinates in $this" } }

    val maven: Maven = Maven(split[0], split[1])
    val version: String? = split.getOrNull(2)

    val groupNameVersion: String = when {
        version == null -> "${maven.group}:${maven.name}"
        else -> "${maven.group}:${maven.name}:$version"
    }

    fun cellFor(column: String): String = data[column] ?: error("Invalid column $column")

    companion object {

        internal fun writeCsv(fileContent: String, csvLines: List<CsvLine>, comments: String): String {
            val spaces = " ".repeat(10)
            val header = header(fileContent) ?: CsvColumns.allColumns.joinToString("$spaces${CsvColumns.separator}$spaces")
            val columns = parseHeader(header)


            val matrix = csvLines.map { csvLine ->
                columns.map { column ->
                    csvLine.cellFor(column)
                }
            }

            val formatCell = formatCell(columns, matrix, header)
            val formattedHeader = columns.withIndex()
                .joinToString("") { formatCell(it) }


            val lines = matrix.map { row ->
                row.withIndex()
                    .joinToString("") { formatCell(it) }
            }

            return """
                |$formattedHeader
                |${reformatComment(comments)}
                |${lines.joinToString("\n")}
                |
            """.trimMargin()
        }

        internal fun validLines(fileContent: String): List<IndexedValue<String>> = fileContent.lines()
            .withIndex()
            .filterNot { (_, value) -> value.startsWith(CsvColumns.commentPrefix) || value.contains("|").not() }

        internal fun header(fileContent: String): String? =
            validLines(fileContent).firstOrNull()
                ?.value

        internal fun readCsv(fileContent: String): List<CsvLine> {
            val header = header(fileContent) ?: error("empty content")
            val valueForColumn = valueForColumn(header)

            fun IndexedValue<String>.toCsvLine(): CsvLine {
                val (index, line) = this
                val data = CsvColumns.allColumns.associateWith { column ->
                    valueForColumn(line, header)
                }
                return CsvLine(data = data, lineNumber = index + 1)
            }

            return validLines(fileContent)
                .drop(1)
                .mapNotNull { (index, line) ->
                    try {
                        val data = CsvColumns.allColumns
                            .associateWith { column -> valueForColumn(line, column) }
                        CsvLine(data = data, lineNumber = index + 1)
                    } catch (e: IllegalArgumentException) {
                        println("w: at line:$index invalidCsvLine=$line")
                        null
                    }
                }
        }

        internal fun parseHeader(line: String): List<String> {
            return line
                .split(CsvColumns.separator)
                .map(String::trim)
                .filter { it.isNotBlank() }
                .also { allColumns ->
                    val invalidColumns = allColumns - CsvColumns.allColumns
                    require(invalidColumns.isEmpty()) { "Invalid columns $invalidColumns from header=$line allColumns=$allColumns" }
                }
        }

        internal fun valueForColumn(header: String): (String, String) -> String {
            val columnsIndexes = parseHeader(header)
                .mapIndexed { index: Int, column: String -> column to index }
                .toMap()

            val lambda: (String, String) -> String = { line, column ->
                val split = "$line|||||||".split("|").map(String::trim)
                val index = columnsIndexes.get(column)
                if (index == null) "" else split.get(index)
            }
            return lambda
        }

        internal fun formatCell(columns: List<String>, matrix: List<List<String>>, header: String): (IndexedValue<String>) -> String {
            val indices = matrix.firstOrNull()?.indices ?: header.indices

            val columnSizes = indices.map { index ->
                val fromMatrix = matrix.maxOf { row -> row[index].length }
                maxOf(fromMatrix, columns[index].length)
            }

            return { (index, value): IndexedValue<String> ->
                val size = columnSizes[index]
                "%-${size}s  |  ".format(value)
            }
        }

        internal fun reformatComment(comments: String): String {
            val regexComment = "(\"|//|##|^${CsvColumns.commentPrefix}+ *)".toRegex()
            return comments.lines()
                .mapNotNull {
                    it.replace(regexComment, "")
                        .trim()
                        .takeIf { it.isNotBlank() }
                }
                .joinToString("\n") { comment -> "${CsvColumns.commentPrefix} $comment" }
        }
    }
}

