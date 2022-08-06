package csv

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
object RefreshVersionsCsvConfig : CsvConfig<DependencyLine>() {

    val propertyName by mapKeyIsValue
    val mavenCoordinates by mapKeyIsValue
    val availableVersions by mapKeyIsValue

    override fun transform(line: CsvLine) = DependencyLine(
        mavenCoordinates = line.cellFor(mavenCoordinates),
        propertyName = line.cellFor(propertyName),
        availableVersions = line.cellFor(availableVersions),
    )

    fun csvLine(
        mavenCoordinates: String,
        propertyName: String,
        availableVersions: List<String> = emptyList(),
        lineNumber: Int? = null,
    ): CsvLine = CsvLine(
        data = mapOf(
            RefreshVersionsCsvConfig.propertyName to propertyName,
            RefreshVersionsCsvConfig.mavenCoordinates to mavenCoordinates,
            RefreshVersionsCsvConfig.availableVersions to availableVersions.joinToString(" ")
        ),
        lineNumber = lineNumber
    )
}

data class DependencyLine(
    val mavenCoordinates: String,
    val propertyName: String,
    val availableVersions: String,
) {
    private val split = mavenCoordinates.split(":")
        .map { it.replace(" ", "") }
        .also { require(it.size >= 2) { "Invalid mavenCoordinates in $this" } }

    val maven: Maven = Maven(split[0], split[1])
    val version: String? = split.getOrNull(2)

    val groupNameVersion: String = when {
        version == null -> "${maven.group}:${maven.name}"
        else -> "${maven.group}:${maven.name}:$version"
    }
}

data class Maven(
    val group: String,
    val name: String
)
