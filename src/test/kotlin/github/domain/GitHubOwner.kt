package github.domain


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubOwner(
    @SerialName("avatar_url")
    val avatarUrl: String = "", // https://avatars.githubusercontent.com/u/459464?v=4
    @SerialName("events_url")
    val eventsUrl: String = "", // https://api.github.com/users/jmfayard/events{/privacy}
    @SerialName("followers_url")
    val followersUrl: String = "", // https://api.github.com/users/jmfayard/followers
    @SerialName("following_url")
    val followingUrl: String = "", // https://api.github.com/users/jmfayard/following{/other_user}
    @SerialName("gists_url")
    val gistsUrl: String = "", // https://api.github.com/users/jmfayard/gists{/gist_id}
    @SerialName("gravatar_id")
    val gravatarId: String = "",
    @SerialName("html_url")
    val htmlUrl: String = "", // https://github.com/jmfayard
    val id: Int = 0, // 459464
    val login: String = "", // jmfayard
    @SerialName("node_id")
    val nodeId: String = "", // MDQ6VXNlcjQ1OTQ2NA==
    @SerialName("organizations_url")
    val organizationsUrl: String = "", // https://api.github.com/users/jmfayard/orgs
    @SerialName("received_events_url")
    val receivedEventsUrl: String = "", // https://api.github.com/users/jmfayard/received_events
    @SerialName("repos_url")
    val reposUrl: String = "", // https://api.github.com/users/jmfayard/repos
    @SerialName("site_admin")
    val siteAdmin: Boolean = false, // false
    @SerialName("starred_url")
    val starredUrl: String = "", // https://api.github.com/users/jmfayard/starred{/owner}{/repo}
    @SerialName("subscriptions_url")
    val subscriptionsUrl: String = "", // https://api.github.com/users/jmfayard/subscriptions
    val type: String = "", // User
    val url: String = "" // https://api.github.com/users/jmfayard
)
