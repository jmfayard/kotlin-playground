package automation.checkvist

import ChecklistModel
import automation.common.EnvironmentVariable
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.util.reflect.TypeInfo
import io.ktor.util.reflect.typeInfo
import kotlin.reflect.typeOf

/**
 * https://checkvist.com/auth/api
 */
class CheckvistApi(
    val ktorClient: HttpClient,
) {
    companion object {
        val baseUrl = "https://checkvist.com"
        val loginUrl = "/auth/login.json?version=2"
    }
    suspend fun login(): CheckvistToken {
        EnvironmentVariable.print()
        val response = ktorClient.post("$baseUrl/auth/login.json") {
            parameter("version", "2")
            parameter("remote_key", CheckvistConfig.CHECKVIST_TOKEN.value)
            parameter("username", CheckvistConfig.CHECKVIST_USER.value)
        }
        return response.body()
    }

    suspend fun userChecklists(token: CheckvistToken): List<ChecklistModel> {
        val options = mapOf(
            "archived" to "false",
            "order" to "id:desc",
            "skip_stats" to "false",
            "token" to token.token,
        )
        val response = ktorClient.get("$baseUrl/checklists.json") {
            parameters(options)
        }
        return response.body()
    }

}

fun HttpRequestBuilder.parameters(map: Map<String, String>) {
    map.forEach { parameter(it.key, it.value) }
}
