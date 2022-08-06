package github

import kotlinx.serialization.json.Json
import java.io.File

fun main() {
    val login = GithubLogin("jmfayard")
    login.apiUrl("repos").debug("repos")

    val repo = GithuRepo(login, "refreshVersions")
    repo.apiUrl(null).debug("repo")

    parseOutJsonFiles()
}

fun jsonFile(path: String): File =
    File("src/test/resources/$path").apply {
        check(canRead()) { "Can't read jsonFile($path) at $canonicalPath" }
    }

fun parseOutJsonFiles() {
    val file = File("src/test/resources/csv/readCsvLines.csv")
    val format = Json { prettyPrint = true }
    jsonFile("github/issues.json")
}

private fun <T> T.debug(name: String) = apply {
    println("v: $name = $this")
}

data class GithuRepo(val user: HasLogin, val name: String) {
    fun apiUrl(resource: String?) =
        "https://api.github.com/repos/${user.login}/$name/$resource".removeSuffix("/null")

}

interface HasLogin {
    val login: String
}

data class GithubLogin(
    override val login: String
) : HasLogin

fun HasLogin.apiUrl(resource: String?) =
    "https://api.github.com/users/$login/$resource".removeSuffix("/null")

class GithubToken {

    companion object {
        val htmlCreateToken = "https://github.com/settings/tokens/new"
    }
}

