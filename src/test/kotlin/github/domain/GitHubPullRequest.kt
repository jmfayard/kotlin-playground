package github.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class GitHubPullRequest(
    val assignee: GitHubUser? = null,
    val assignees: List<GitHubUser> = listOf(),
    @SerialName("author_association")
    val authorAssociation: String = "", // CONTRIBUTOR
    val base: Base? = Base(),
    val body: String? = null,
    @SerialName("closed_at")
    val closedAt: String? = null,
    @SerialName("comments_url")
    val commentsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/569/comments
    @SerialName("commits_url")
    val commitsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/pulls/569/commits
    @SerialName("created_at")
    val createdAt: String = "", // 2022-08-02T21:10:09Z
    @SerialName("diff_url")
    val diffUrl: String = "", // https://github.com/jmfayard/refreshVersions/pull/569.diff
    val draft: Boolean = false, // false
    val head: GitHubHead? = GitHubHead(),
    @SerialName("html_url")
    val htmlUrl: String = "", // https://github.com/jmfayard/refreshVersions/pull/569
    val id: Int = 0, // 1015601669
    @SerialName("issue_url")
    val issueUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/569
    val labels: List<GitHubLabel> = listOf(),
    @SerialName("_links")
    val links: GitHubLinks? = null,
    val locked: Boolean = false, // false
    @SerialName("merge_commit_sha")
    val mergeCommitSha: String = "", // 71a6de2bd780d576217c9c55bb260c208ed8aa1d
    @SerialName("merged_at")
    val mergedAt: String? = null,
    val milestone: GitHubMilestone? = null,
    @SerialName("node_id")
    val nodeId: String = "", // PR_kwDOCP1xB848iNoF
    val number: Int = 0, // 569
    @SerialName("patch_url")
    val patchUrl: String = "", // https://github.com/jmfayard/refreshVersions/pull/569.patch
    @SerialName("requested_reviewers")
    val requestedReviewers: List<GitHubUser> = listOf(),
    @SerialName("requested_teams")
    val requestedTeams: List<JsonElement> = listOf(),
    @SerialName("review_comment_url")
    val reviewCommentUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/pulls/comments{/number}
    @SerialName("review_comments_url")
    val reviewCommentsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/pulls/569/comments
    val state: String = "", // open
    @SerialName("statuses_url")
    val statusesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/statuses/eb7cf524dcb0c041d74fac9710eed10e8a885274
    val title: String = "", // Add AndroidX.lifecycle.runtimeCompose dependency notation
    @SerialName("updated_at")
    val updatedAt: String = "", // 2022-08-02T21:10:12Z
    val url: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/pulls/569
    val user: GitHubUser? = null,
)

@Serializable
data class Base(
    val label: String = "", // jmfayard:main
    val ref: String = "", // main
    val repo: GitHubRepo? = GitHubRepo(),
    val sha: String = "", // a6e58b18de41f2571f7ecc4b67ca76b5d392aa0e
    val user: GitHubUser? = GitHubUser()
)

@Serializable
data class GitHubHead(
    val label: String = "", // necatisozer:lifecycle-runtime-compose
    val ref: String = "", // lifecycle-runtime-compose
    val repo: GitHubRepo? = GitHubRepo(),
    val sha: String = "", // eb7cf524dcb0c041d74fac9710eed10e8a885274
    val user: UserXX? = UserXX()
)

@Serializable
data class GitHubLabel(
    val color: String = "", // 3CBF00
    val default: Boolean = false, // false
    val description: String? = null,
    val id: Long = 0, // 4388616277
    val name: String = "", // size/XS
    @SerialName("node_id")
    val nodeId: String = "", // LA_kwDOCP1xB88AAAABBZT4VQ
    val url: String = "" // https://api.github.com/repos/jmfayard/refreshVersions/labels/size/XS
)

@Serializable
data class GitHubLinks(
    val comments: GitHubComments? = GitHubComments(),
    val commits: GitHubCommits? = GitHubCommits(),
    val html: GitHubHtml? = GitHubHtml(),
    val issue: GitHubIssue? = GitHubIssue(),
    @SerialName("review_comment")
    val reviewComment: GitHubReviewComment? = GitHubReviewComment(),
    @SerialName("review_comments")
    val reviewComments: GitHubReviewComments? = GitHubReviewComments(),
    val self: GitHubSelf? = GitHubSelf(),
    val statuses: GitHubStatuses? = GitHubStatuses()
)

@Serializable
data class UserXX(
    @SerialName("avatar_url")
    val avatarUrl: String = "", // https://avatars.githubusercontent.com/u/32370872?v=4
    @SerialName("events_url")
    val eventsUrl: String = "", // https://api.github.com/users/necatisozer/events{/privacy}
    @SerialName("followers_url")
    val followersUrl: String = "", // https://api.github.com/users/necatisozer/followers
    @SerialName("following_url")
    val followingUrl: String = "", // https://api.github.com/users/necatisozer/following{/other_user}
    @SerialName("gists_url")
    val gistsUrl: String = "", // https://api.github.com/users/necatisozer/gists{/gist_id}
    @SerialName("gravatar_id")
    val gravatarId: String = "",
    @SerialName("html_url")
    val htmlUrl: String = "", // https://github.com/necatisozer
    val id: Int = 0, // 32370872
    val login: String = "", // necatisozer
    @SerialName("node_id")
    val nodeId: String = "", // MDQ6VXNlcjMyMzcwODcy
    @SerialName("organizations_url")
    val organizationsUrl: String = "", // https://api.github.com/users/necatisozer/orgs
    @SerialName("received_events_url")
    val receivedEventsUrl: String = "", // https://api.github.com/users/necatisozer/received_events
    @SerialName("repos_url")
    val reposUrl: String = "", // https://api.github.com/users/necatisozer/repos
    @SerialName("site_admin")
    val siteAdmin: Boolean = false, // false
    @SerialName("starred_url")
    val starredUrl: String = "", // https://api.github.com/users/necatisozer/starred{/owner}{/repo}
    @SerialName("subscriptions_url")
    val subscriptionsUrl: String = "", // https://api.github.com/users/necatisozer/subscriptions
    val type: String = "", // User
    val url: String = "" // https://api.github.com/users/necatisozer
)

@Serializable
data class GitHubComments(
    val href: String = "" // https://api.github.com/repos/jmfayard/refreshVersions/issues/569/comments
)

@Serializable
data class GitHubCommits(
    val href: String = "" // https://api.github.com/repos/jmfayard/refreshVersions/pulls/569/commits
)

@Serializable
data class GitHubHtml(
    val href: String = "" // https://github.com/jmfayard/refreshVersions/pull/569
)


@Serializable
data class GitHubReviewComment(
    val href: String = "" // https://api.github.com/repos/jmfayard/refreshVersions/pulls/comments{/number}
)

@Serializable
data class GitHubReviewComments(
    val href: String = "" // https://api.github.com/repos/jmfayard/refreshVersions/pulls/569/comments
)

@Serializable
data class GitHubSelf(
    val href: String = "" // https://api.github.com/repos/jmfayard/refreshVersions/pulls/569
)

@Serializable
data class GitHubStatuses(
    val href: String = "" // https://api.github.com/repos/jmfayard/refreshVersions/statuses/eb7cf524dcb0c041d74fac9710eed10e8a885274
)
