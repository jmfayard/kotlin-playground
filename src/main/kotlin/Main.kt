import Config.buildFolder
import Config.expectedRootFolder
import java.io.File

fun main(args: Array<String>) {
    println("== Extracting groups for Kotlin files ==")
    println("A group is defined as being between line comments 'start group NAME' and 'end group NAME' ")
    println()
    initializeBuildFuilder()
    File("src").walk()
        .filter { it.extension == "kt" }
        .forEach {
            val groups = it.extractMarkdownGroup()
            groups.forEach { group ->
                val output = buildFolder.resolve("${group.name}.kt")
                println("Extracting group=${group.name} from file=${it.name} into $output")
                output.writeText(group.content)
            }
        }

    println()
    println("== Updating scripts from the bin and scripts folder ==")
    (File("bin").walk() + File("scripts").walk())
        .filter { it.extension in listOf("kts", "main.kts") }
        .forEach {
            it.updateScriptFile()
        }
}

private fun File.updateScriptFile() {
    val groups = extractMarkdownGroup()
    if (groups.isEmpty()) return

    val groupNames = groups.map { it.name }
    println("Updating script=$this with groups=$groupNames")
    val invalidGroups = groupNames.filter { group ->
        buildFolder.resolve("$group.kt").exists().not()
    }
    require(invalidGroups.isEmpty()) { "Can't find groups $invalidGroups" }
    val startIndex = groups.map { it.start }.toSet()
    val deleteIndex = groups.flatMap { group -> (group.start + 1)..(group.end - 1) }.toSet()
    val newContent = readLines().mapIndexedNotNull { index, line ->
        when {
            index in deleteIndex -> null
            index in startIndex -> {
                val group = groups.first { it.start == index }.name
                val content = buildFolder.resolve("$group.kt").readText().trim()
                "$line\n$content"
            }

            else -> line
        }
    }
    writeText(newContent.joinToString("\n"))
}

private fun File.extractMarkdownGroup(): List<KotlinGroup> {
    val startRegex = ".*//\\sstart\\s+group\\s+([a-zA-Z0-9_-]+).*".toRegex()
    val endRegex = ".*//\\send\\s+group\\s+([a-zA-Z0-9_-]+).*".toRegex()
    val lines = readLines()
    val startGroups = lines.mapIndexedNotNull { index, line ->
        startRegex.matchEntire(line)?.let { result ->
            KotlinGroup(start = index, name = result.destructured.component1())
        }
    }

    val endGroups = lines.mapIndexedNotNull { index, line ->
        endRegex.matchEntire(line)?.let { result ->
            KotlinGroup(start = -1, end = index, name = result.destructured.component1())
        }
    }
    val map = endGroups.associateBy { it.name }

    val missingGroups = startGroups.filter { it.name !in map }.map { it.name }
    require(missingGroups.isEmpty()) { "File $canonicalPath\nThose groups were started but never ended\nInvalid groups: $missingGroups" }

    val groups = startGroups.mapNotNull { group ->
        val endLine = map.getValue(group.name)
        val content = try {
            lines.subList(group.start + 1, endLine.end).joinToString("\n").trim() + "\n"
        } catch (e: IllegalArgumentException) {
            println("Can't find group=$group in file=$this")
            return@mapNotNull null
        }
        group.copy(end = endLine.end, content = content)
    }
    return groups
}


object Config {
    val buildFolder = File("build/kotlin-scripts-groups")
    val expectedRootFolder = "my-kotlin-scripts"
}

data class KotlinGroup(val start: Int, val name: String, val end: Int = 0, val content: String = "")

private fun initializeBuildFuilder() {
    File(".").canonicalFile.also {
        require(it.name == expectedRootFolder) { "Invalid root folder: expected=$expectedRootFolder actual=${it.name}" }
    }
    buildFolder.also {
        it.deleteRecursively()
        it.mkdirs()
    }
}
