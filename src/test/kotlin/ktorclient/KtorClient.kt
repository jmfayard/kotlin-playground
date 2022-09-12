package ktorclient

import github.ktorClient
import io.kotest.common.runBlocking
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

/**
 * Demo of ktor-client with the OkHttp engine and kotlinx-serialization for json
 *
 * Authorization: not done here but see https://ktor.io/docs/auth.html#okhttp
 */
fun main() = runBlocking {
    telleMeJokes()
    createPostBody()
    todoAuthorization()
}

suspend fun todoAuthorization() {
    // See http://httpbin.org/#/Auth/get_bearer
}

suspend fun telleMeJokes() {
    val numbersOfJokes = 5

    val jokes: List<GeekJoke> = List(numbersOfJokes) { fetchGeekJoke() }
    val formattedJokes = jokes.joinToString("\n") { it.joke }
    println(
        """
== $numbersOfJokes geek jokes
$formattedJokes

""".trimIndent()
    )
}

private suspend fun fetchGeekJoke(): GeekJoke {
    val jokesApi = "https://geek-jokes.sameerkumar.website/api?format=json"
    val httpResponse: HttpResponse = ktorClient.get(jokesApi)
    val joke: GeekJoke = httpResponse.body()
    return joke
}

@Serializable // must have with kotlinx-serialization
data class GeekJoke(
    val joke: String
)


private val ktorClient: HttpClient = HttpClient(OkHttp) {
    // this: HttpClientConfig<OkHttpConfig>

    // HTTP engine: we chose OkHttp
    engine {
        preconfigured = buildOkHttpClient()
    }

    // Content negociation and serialization
    // We chose kotlinx-serialization here but it could be Jackson
    // https://ktor.io/docs/serialization-client.html#configure_serializer
    // https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
}

private fun buildOkHttpClient() = OkHttpClient.Builder()
    .followRedirects(true)
    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
    .build()

suspend fun createPostBody() {
    val httpbinUrl = "http://httpbin.org/post" // reply what you send him, make it easy to test an Http Client
    val eventDate: Instant = Instant.parse("2022-09-12T09:44:55+00:00")
    val durationBeforeEvent = 1.hours
    val couriersendBody = createCourierSendJson(eventDate, durationBeforeEvent)
    val response: HttpResponse = ktorClient.post(httpbinUrl) {
        contentType(ContentType.Application.Json) // request
        accept(ContentType.Application.Json) // response
        setBody(couriersendBody)
    }
    val data: String = response.bodyAsText()
    println(data)
}


/**
 * See https://www.courier.com/docs/reference/send/message/
 */
private fun createCourierSendJson(eventDate: Instant, durationBeforeEvent: Duration): JsonObject {
    val delay = 48.hours.inWholeMilliseconds // TODO
    return JsonObject(
        mapOf(
            "message" to JsonObject(
                mapOf(
                    "delay" to JsonObject(
                        mapOf(
                            "duration" to JsonPrimitive(delay)
                        )
                    )
                )
            )
        )
    )
}

@Serializable // must have with kotlinx-serialization
data class CourierSend(
    val message: CourierSendMessage
)

@Serializable
data class CourierSendMessage(
    val delay: CourierDuration
)

@Serializable
data class CourierDuration(
    val duration: Long
)

val requiredPostBody = """
{
  "message": {
    "delay": {
      "duration": 120000
    },
    "metadata": {
      "event": "notify-prepare-for-critical-moment"
    },
    "template": "prepare-for-critical-moments",
    "to": {
      "email": "o.arnaiz@tignum.com",
      "data": {
        "variable": "todo verify how to make it actually work",
      }
    }
  }
}
""".trimIndent()
