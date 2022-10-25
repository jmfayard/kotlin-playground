@file:OptIn(ExperimentalSerializationApi::class)

package automation.checkvist

import automation.common.EnvironmentVariable
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.net.URL

object CheckvistConfig {
    const val authorizationHeader = "X-Client-Token"
    val createOpenApiKey = URL("https://checkvist.com/auth/check_pwd?remote_key=true")
    val CHECKVIST_USER by EnvironmentVariable
    val CHECKVIST_TOKEN by EnvironmentVariable

    val jsonFormat = configureKotlinxSerializationJson()
    val ktorClient = configureKtorClient(jsonFormat)
    val api = CheckvistApi(ktorClient)
}

@Serializable
data class CheckvistToken(val token: String)

fun configureKotlinxSerializationJson() = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
    isLenient = true
    ignoreUnknownKeys = true
}

fun configureKtorClient(json: Json): HttpClient =
    HttpClient(OkHttp) {
        expectSuccess = true
        engine {
            // this: OkHttpConfig
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            json(json)
        }
    }
