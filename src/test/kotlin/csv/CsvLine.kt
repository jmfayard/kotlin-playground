package csv

import java.io.File

fun File.writeCsvLines(lines: List<CsvLine>, comments: String) {
    val fileContent = if (canRead()) readText() else RefreshVersionsCsvConfig.allColumns.joinToString("  ${RefreshVersionsCsvConfig.separator}  ")
    val newContent = CsvLine.writeCsv(fileContent, lines, comments)
    writeText(newContent)
}

fun <T> File.readAndTransformCsvLines(config: CsvConfig<T>): List<T> =
    readCsvLines().map { line -> config.transform(line) }

fun File.readCsvLines(): List<CsvLine> {
    val fileContent = if (canRead()) {
        readText()
    } else {
        val header = RefreshVersionsCsvConfig.allColumns.joinToString("  ${RefreshVersionsCsvConfig.separator}  ")
        val defaultFileContent = """
            $header
            ;automatically generated. you can customize the columns
            """.trimIndent()
        writeText(defaultFileContent)
        defaultFileContent
    }
    return CsvLine.readCsv(fileContent)
}

abstract class CsvConfig<T> {
    open val commentPrefix = ";"
    open val separator = "|"

    val allColumns = mutableSetOf<String>()

    val mapKeyIsValue: Map<String, String> =
        mutableMapOf<String, String>().withDefault { column: String ->
            allColumns += column
            column
        }

    override fun toString() =
        "CsvColumns(separator='$separator', columns='$allColumns')"

    abstract fun transform(line: CsvLine): T
}

open class CsvLine(
    val data: Map<String, String>,
    val lineNumber: Int? = null,
) {
    fun cellFor(column: String): String = data[column] ?: ""

    fun <T> transform(config: CsvConfig<T>): T = config.transform(this)

    override fun toString(): String {
        val kotlinMap = data.entries
            .joinToString(separator = ", ", prefix = "mapOf(", postfix = ")") { (key, value) -> """"$key" to "$value"""" }
        return "CsvLine($kotlinMap)"
    }

    override fun equals(other: Any?) =
        (other is CsvLine) && data == other.data

    companion object {

        fun writeCsv(fileContent: String, csvLines: List<CsvLine>, comments: String): String {
            val spaces = " ".repeat(10)
            val header = header(fileContent) ?: RefreshVersionsCsvConfig.allColumns.joinToString("$spaces${RefreshVersionsCsvConfig.separator}$spaces")
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

        fun validLines(fileContent: String): List<IndexedValue<String>> = fileContent.lines()
            .withIndex()
            .filterNot { (_, value) -> value.startsWith(RefreshVersionsCsvConfig.commentPrefix) || value.contains("|").not() }

        fun header(fileContent: String): String? =
            validLines(fileContent).firstOrNull()
                ?.value

        fun readCsv(fileContent: String): List<CsvLine> {
            val header = header(fileContent) ?: error("empty content")
            val valueForColumn = valueForColumn(header)

            fun IndexedValue<String>.toCsvLine(): CsvLine {
                val (index, line) = this
                val data = RefreshVersionsCsvConfig.allColumns.associateWith { column ->
                    valueForColumn(line, header)
                }
                return CsvLine(data = data, lineNumber = index + 1)
            }

            return validLines(fileContent)
                .drop(1)
                .mapNotNull { (index, line) ->
                    try {
                        val data = RefreshVersionsCsvConfig.allColumns
                            .associateWith { column -> valueForColumn(line, column) }
                        CsvLine(data = data, lineNumber = index + 1)
                    } catch (e: IllegalArgumentException) {
                        println("w: at line:$index invalidCsvLine=$line")
                        null
                    }
                }
        }

        fun parseHeader(line: String): List<String> {
            return line
                .split(RefreshVersionsCsvConfig.separator)
                .map(String::trim)
                .filter { it.isNotBlank() }
                .also { allColumns ->
                    val invalidColumns = allColumns - RefreshVersionsCsvConfig.allColumns
                    require(invalidColumns.isEmpty()) { "Invalid columns $invalidColumns from header=$line allColumns=$allColumns" }
                }
        }

        fun valueForColumn(header: String): (String, String) -> String {
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

        fun formatCell(columns: List<String>, matrix: List<List<String>>, header: String): (IndexedValue<String>) -> String {
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

        fun reformatComment(comments: String): String {
            val regexComment = "(\"|//|##|^${RefreshVersionsCsvConfig.commentPrefix}+ *)".toRegex()
            return comments.lines()
                .mapNotNull {
                    it.replace(regexComment, "")
                        .trim()
                        .takeIf { it.isNotBlank() }
                }
                .joinToString("\n") { comment -> "${RefreshVersionsCsvConfig.commentPrefix} $comment" }
        }
    }
}

data class CsvReadWrite<T>(private val columns: CsvConfig<T>, private val transform: (CsvLine) -> T) {
    fun writeCsv(fileContent: String, csvLines: List<CsvLine>, comments: String): String {
        val spaces = " ".repeat(10)
        val header = header(fileContent) ?: columns.allColumns.joinToString("$spaces${columns.separator}$spaces")
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

    fun validLines(fileContent: String): List<IndexedValue<String>> = fileContent.lines()
        .withIndex()
        .filterNot { (_, value) -> value.startsWith(columns.commentPrefix) || value.contains("|").not() }

    fun header(fileContent: String): String? =
        validLines(fileContent).firstOrNull()
            ?.value

    fun readAndParseCsv(fileContent: String): List<T> = readCsv(fileContent).map(transform)

    fun readCsv(fileContent: String): List<CsvLine> {
        val header = header(fileContent) ?: error("empty content")
        val valueForColumn = valueForColumn(header)

        fun IndexedValue<String>.toCsvLine(): CsvLine {
            val (index, line) = this
            val data = RefreshVersionsCsvConfig.allColumns.associateWith { column ->
                valueForColumn(line, header)
            }
            return CsvLine(data = data, lineNumber = index + 1)
        }

        return validLines(fileContent)
            .drop(1)
            .mapNotNull { (index, line) ->
                try {
                    val data = columns.allColumns
                        .associateWith { column -> valueForColumn(line, column) }
                    CsvLine(data = data, lineNumber = index + 1)
                } catch (e: IllegalArgumentException) {
                    println("w: at line:$index invalidCsvLine=$line")
                    null
                }
            }
    }

    fun parseHeader(line: String): List<String> {
        return line
            .split(columns.separator)
            .map(String::trim)
            .filter { it.isNotBlank() }
            .also { allColumns ->
                val invalidColumns = allColumns - columns.allColumns
                require(invalidColumns.isEmpty()) { "Invalid columns $invalidColumns\n$line" }
            }
    }

    fun valueForColumn(header: String): (String, String) -> String {
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

    fun formatCell(columns: List<String>, matrix: List<List<String>>, header: String): (IndexedValue<String>) -> String {
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

    fun reformatComment(comments: String): String {
        val regexComment = "(\"|//|##|^${columns.commentPrefix}+ *)".toRegex()
        return comments.lines()
            .mapNotNull {
                it.replace(regexComment, "")
                    .trim()
                    .takeIf { it.isNotBlank() }
            }
            .joinToString("\n") { comment -> "${columns.commentPrefix} $comment" }
    }
}
