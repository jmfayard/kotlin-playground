package github.domain


import github.HasLogin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUser(
    @SerialName("avatar_url")
    val avatarUrl: String = "", // https://avatars.githubusercontent.com/u/459464?v=4
    val bio: String = "", // “Weeks of programming can save hours of planning.”
    val blog: String = "", // https://dev.to/jmfayard
    val company: String = "", // Mentoring, Tech Writing, Kotlin, Backend, Android
    @SerialName("created_at")
    val createdAt: String = "", // 2010-10-29T12:56:07Z
    val email: String = "",
    @SerialName("events_url")
    val eventsUrl: String = "", // https://api.github.com/users/jmfayard/events{/privacy}
    val followers: Int = 0, // 196
    @SerialName("followers_url")
    val followersUrl: String = "", // https://api.github.com/users/jmfayard/followers
    val following: Int = 0, // 275
    @SerialName("following_url")
    val followingUrl: String = "", // https://api.github.com/users/jmfayard/following{/other_user}
    @SerialName("gists_url")
    val gistsUrl: String = "", // https://api.github.com/users/jmfayard/gists{/gist_id}
    @SerialName("gravatar_id")
    val gravatarId: String = "",
    val hireable: Boolean = false, // true
    @SerialName("html_url")
    val htmlUrl: String = "", // https://github.com/jmfayard
    val id: Int = 0, // 459464
    val location: String = "", // Paris, France
    override val login: String = "", // jmfayard
    val name: String = "", // Jean-Michel Fayard
    @SerialName("node_id")
    val nodeId: String = "", // MDQ6VXNlcjQ1OTQ2NA==
    @SerialName("organizations_url")
    val organizationsUrl: String = "", // https://api.github.com/users/jmfayard/orgs
    @SerialName("public_gists")
    val publicGists: Int = 0, // 48
    @SerialName("public_repos")
    val publicRepos: Int = 0, // 147
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
    @SerialName("twitter_username")
    val twitterUsername: String = "", // jm_fayard
    val type: String = "", // User
    @SerialName("updated_at")
    val updatedAt: String = "", // 2022-08-05T09:51:52Z
    val url: String = "" // https://api.github.com/users/jmfayard
) : HasLogin
