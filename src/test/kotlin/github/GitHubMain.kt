@file:OptIn(ExperimentalSerializationApi::class, ExperimentalSerializationApi::class)
@file:Suppress("UNCHECKED_CAST")

package github

import com.fasterxml.jackson.databind.JsonNode
import com.nfeld.jsonpathkt.JsonPath
import com.nfeld.jsonpathkt.extension.read
import github.Env.GITHUB_TOKEN
import github.domain.GitHubIssue
import github.domain.GitHubPullRequest
import github.domain.GitHubUser
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpMessageBuilder
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.InputStream

fun main() {
    fetchIssuesAndPRsWithOkhttpJsonPath()
//    val login = GithubLogin("jmfayard")
//    login.apiUrl("repos").debug("repos")
//
//    val repo = GithuRepoRef(login, "refreshVersions")
//    repo.apiUrl(null).debug("repo")
//
//    parseOutJsonFiles()
//    runBlocking {
//        fetchFromGitHub()
//    }
}

fun fetchIssuesAndPRsWithOkhttpJsonPath() {

    val issuesAndPrsUrl: HttpUrl = "https://api.github.com/repos/jmfayard/refreshVersions/issues".toHttpUrl().newBuilder()
        .addQueryParameter("state", "all")
        .addQueryParameter("sort", "updated")
        .addQueryParameter("direction", "desc")
        .addQueryParameter("per_page", "20")
        .build()

    val a: Iterator<String> = listOf("&").iterator()

    val issuesAndPrs = okhttpClient.fetchJsonOrThrow(issuesAndPrsUrl)
    val titles = issuesAndPrs.read<List<String?>>("\$.*.title")!!
    val urls = issuesAndPrs.read<List<String?>>("\$.*.url")!!
    val numbers = issuesAndPrs.read<List<Int?>>("\$.*.number")!!
    val all = List(issuesAndPrs.size()) { i ->
        val isPr = issuesAndPrs.path(i).path("pull_request").isEmpty
        GitHubIssueOrPr(numbers[i], titles[i], urls[i], isPr)
    }
    val (prs, issues) = all.partition { it.isPr }
    println(
        """
## Issues
${issues.joinToString("\n")}

## PRs
${prs.joinToString("\n")}
    """.trimIndent()
    )
}


data class GitHubIssueOrPr(val number: Int?, val title: String?, val url: String?, val isPr: Boolean)

data class ParsedGithubResult(val outer: List<Map<String, String>>)

fun String.toHttpUrl(urlBuilder: HttpUrl.Builder.() -> Unit): HttpUrl =
    toHttpUrl().newBuilder().apply(urlBuilder).build()

val okhttpClient: OkHttpClient = OkHttpClient.Builder()
    .build()

fun OkHttpClient.fetchJsonOrThrow(
    url: HttpUrl,
): JsonNode {
    val request = Request.Builder()
        .url(url)
        .build()
    val json = newCall(request).execute().use { response ->
        val bodyText = response.body.use { it?.string() ?: "{}" }
        require(response.isSuccessful) { response.errorResponse(bodyText) }
        bodyText
    }
    return JsonPath.parse(json) ?: error("Can't parse json\nURL: $url\n<<<\n$json\n>>>")
    //return json
}

fun Response.errorResponse(bodyText: String) =
    """
    ERROR HTTP $code $message

    ${headers.joinToString("\n")}

    $bodyText
    """.trimIndent()

suspend fun fetchFromGitHub() = ktorClient.use {
    val user: GitHubUser = ktorClient.get("https://api.github.com/user") {
        this.githubBasicAuth()
    }.body()
    println("v: user = $user")

    // https://docs.github.com/en/rest/pulls/pulls
    val pulls: List<GitHubPullRequest> = ktorClient.get("https://api.github.com/repos/jmfayard/refreshVersions/pulls") {
        githubBasicAuth()
        parameter("state", "all")
        parameter("sort", "updated")
        parameter("direction", "desc")
        parameter("per_page", "10")
    }.body()

    // https://docs.github.com/en/rest/pulls/pulls
    val issuesAndPrs: List<GitHubIssue> = ktorClient.get("https://api.github.com/repos/jmfayard/refreshVersions/issues") {
        githubBasicAuth()
        parameter("state", "all")
        parameter("sort", "updated")
        parameter("direction", "desc")
        parameter("per_page", "20")
    }.body()

    val (issues, prs) =
        issuesAndPrs.partition {
            it.pullRequest == null
        }

    println("\n\n== issues=")
    val issuesChoices = issues
        .map { pull -> "#${pull.number} ${pull.title}" to pull.url }
    println(issuesChoices.joinToString("\n"))

    val pullChoices = pulls
        .map { pull -> "#${pull.number} ${pull.title}" to pull.url }
    println("\n\n== PRs")
    println(pullChoices.joinToString("\n"))
}

fun parseOutJsonFiles() {
    jsonFormat.decodeFromStream<GitHubIssue>(
        jsonFile("github/issue.json")
    ).also { issue ->
        issue.number shouldBe 571
        issue.title shouldBe "Caveat: refreshVersions might show available updates in versions catalog for dependencies you don't actually use"
    }

    jsonFormat.decodeFromStream<GitHubPullRequest>(
        jsonFile("github/pull.json")
    ).head?.sha shouldBe "eb7cf524dcb0c041d74fac9710eed10e8a885274"

    jsonFormat.decodeFromStream<GitHubUser>(
        jsonFile("github/user.json")
    ).bio shouldBe "“Weeks of programming can save hours of planning.”"

    val repo = jsonFormat.decodeFromStream<github.domain.GitHubRepo>(
        jsonFile("github/repo.json")
    )
    assertSoftly(repo) {
        fullName shouldBe "jmfayard/refreshVersions"
        permissions?.admin shouldBe true
        topics?.shouldContainInOrder(listOf("gradle", "kotlin"))
    }
}

fun HttpMessageBuilder.githubBasicAuth() =
    basicAuth("jmfayard", GITHUB_TOKEN)

private object Env {
    val delegate = emptyMap<String, String>().withDefault {
        System.getenv(it) ?: "Environment variable not found: $it"
    }
    val GITHUB_TOKEN by delegate
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

val ktorClient: HttpClient = HttpClient(OkHttp) {
    expectSuccess = true
    engine {
        // this: OkHttpConfig
    }
    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.HEADERS
    }
    install(ContentNegotiation) {
        json(jsonFormat)
    }
}


private fun <T> T.debug(name: String): T = apply {
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

