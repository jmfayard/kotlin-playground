package github.domain


import kotlinx.serialization.Serializable

@Serializable
data class GitHubPermissions(
    val admin: Boolean = false, // true
    val maintain: Boolean = false, // true
    val pull: Boolean = false, // true
    val push: Boolean = false, // true
    val triage: Boolean = false // true
)
