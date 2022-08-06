package github.domain


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubLicense(
    val key: String = "", // apache-2.0
    val name: String = "", // Apache License 2.0
    @SerialName("node_id")
    val nodeId: String = "", // MDc6TGljZW5zZTI=
    @SerialName("spdx_id")
    val spdxId: String = "", // Apache-2.0
    val url: String = "" // https://api.github.com/licenses/apache-2.0
)
