@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)

package github

import github.domain.GitHubIssue
import github.domain.GitHubPull
import github.domain.GitHubUser
import github.domain.GithubRepo
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.File
import java.io.InputStream

fun main() {
    val login = GithubLogin("jmfayard")
    login.apiUrl("repos").debug("repos")

    val repo = GithuRepoRef(login, "refreshVersions")
    repo.apiUrl(null).debug("repo")

    parseOutJsonFiles()
}

fun parseOutJsonFiles() {
    jsonFormat.decodeFromStream<GitHubIssue>(
        jsonFile("github/issue.json")
    ).also { issue ->
        issue.number shouldBe 571
        issue.title shouldBe "Caveat: refreshVersions might show available updates in versions catalog for dependencies you don't actually use"
    }

    jsonFormat.decodeFromStream<GitHubPull>(
        jsonFile("github/pull.json")
    ).head?.sha shouldBe "eb7cf524dcb0c041d74fac9710eed10e8a885274"

    jsonFormat.decodeFromStream<GitHubUser>(
        jsonFile("github/user.json")
    ).bio shouldBe "“Weeks of programming can save hours of planning.”"

    val repo = jsonFormat.decodeFromStream<GithubRepo>(
        jsonFile("github/repo.json")
    )
    assertSoftly(repo) {
        fullName shouldBe "jmfayard/refreshVersions"
        permissions?.admin shouldBe true
        topics?.shouldContainInOrder(listOf("gradle", "kotlin"))
    }
}


fun jsonFile(path: String): InputStream =
    File("src/test/resources/$path").apply {
        check(canRead()) { "Can't read jsonFile($path) at $canonicalPath" }
    }.inputStream()

private val jsonFormat = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    isLenient = true
    ignoreUnknownKeys = true
}

private fun <T> T.debug(name: String) = apply {
    println("v: $name = $this")
}

data class GithuRepoRef(val user: HasLogin, val name: String) {
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
