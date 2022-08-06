package github.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private typealias Any = GitHubUser

@Serializable
data class GitHubPull(
    @SerialName("active_lock_reason")
    val activeLockReason: String = "", // null
    val assignee: Any? = Any(), // null
    val assignees: List<Any> = listOf(),
    @SerialName("author_association")
    val authorAssociation: String = "", // CONTRIBUTOR
    val base: Base? = Base(),
    val body: String = "",
    @SerialName("closed_at")
    val closedAt: String = "",
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
    val mergedAt: Any? = Any(), // null
    val milestone: Any? = Any(), // null
    @SerialName("node_id")
    val nodeId: String = "", // PR_kwDOCP1xB848iNoF
    val number: Int = 0, // 569
    @SerialName("patch_url")
    val patchUrl: String = "", // https://github.com/jmfayard/refreshVersions/pull/569.patch
    @SerialName("requested_reviewers")
    val requestedReviewers: List<Any> = listOf(),
    @SerialName("requested_teams")
    val requestedTeams: List<Any> = listOf(),
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
    val repo: Repo? = Repo(),
    val sha: String = "", // a6e58b18de41f2571f7ecc4b67ca76b5d392aa0e
    val user: GitHubUser? = GitHubUser()
)

@Serializable
data class GitHubHead(
    val label: String = "", // necatisozer:lifecycle-runtime-compose
    val ref: String = "", // lifecycle-runtime-compose
    val repo: GithubRepo? = GithubRepo(),
    val sha: String = "", // eb7cf524dcb0c041d74fac9710eed10e8a885274
    val user: UserXX? = UserXX()
)

@Serializable
data class GitHubLabel(
    val color: String = "", // 3CBF00
    val default: Boolean = false, // false
    val description: Any? = Any(), // null
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
data class Repo(
    @SerialName("allow_forking")
    val allowForking: Boolean = false, // true
    @SerialName("archive_url")
    val archiveUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/{archive_format}{/ref}
    val archived: Boolean = false, // false
    @SerialName("assignees_url")
    val assigneesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/assignees{/user}
    @SerialName("blobs_url")
    val blobsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/git/blobs{/sha}
    @SerialName("branches_url")
    val branchesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/branches{/branch}
    @SerialName("clone_url")
    val cloneUrl: String = "", // https://github.com/jmfayard/refreshVersions.git
    @SerialName("collaborators_url")
    val collaboratorsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/collaborators{/collaborator}
    @SerialName("comments_url")
    val commentsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/comments{/number}
    @SerialName("commits_url")
    val commitsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/commits{/sha}
    @SerialName("compare_url")
    val compareUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/compare/{base}...{head}
    @SerialName("contents_url")
    val contentsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/contents/{+path}
    @SerialName("contributors_url")
    val contributorsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/contributors
    @SerialName("created_at")
    val createdAt: String = "", // 2018-09-29T05:14:46Z
    @SerialName("default_branch")
    val defaultBranch: String = "", // main
    @SerialName("deployments_url")
    val deploymentsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/deployments
    val description: String = "", // Life is too short to google for dependencies and versions
    val disabled: Boolean = false, // false
    @SerialName("downloads_url")
    val downloadsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/downloads
    @SerialName("events_url")
    val eventsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/events
    val fork: Boolean = false, // false
    val forks: Int = 0, // 91
    @SerialName("forks_count")
    val forksCount: Int = 0, // 91
    @SerialName("forks_url")
    val forksUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/forks
    @SerialName("full_name")
    val fullName: String = "", // jmfayard/refreshVersions
    @SerialName("git_commits_url")
    val gitCommitsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/git/commits{/sha}
    @SerialName("git_refs_url")
    val gitRefsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/git/refs{/sha}
    @SerialName("git_tags_url")
    val gitTagsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/git/tags{/sha}
    @SerialName("git_url")
    val gitUrl: String = "", // git://github.com/jmfayard/refreshVersions.git
    @SerialName("has_downloads")
    val hasDownloads: Boolean = false, // true
    @SerialName("has_issues")
    val hasIssues: Boolean = false, // true
    @SerialName("has_pages")
    val hasPages: Boolean = false, // true
    @SerialName("has_projects")
    val hasProjects: Boolean = false, // true
    @SerialName("has_wiki")
    val hasWiki: Boolean = false, // true
    val homepage: String = "", // https://jmfayard.github.io/refreshVersions/
    @SerialName("hooks_url")
    val hooksUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/hooks
    @SerialName("html_url")
    val htmlUrl: String = "", // https://github.com/jmfayard/refreshVersions
    val id: Int = 0, // 150827271
    @SerialName("is_template")
    val isTemplate: Boolean = false, // false
    @SerialName("issue_comment_url")
    val issueCommentUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/comments{/number}
    @SerialName("issue_events_url")
    val issueEventsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/events{/number}
    @SerialName("issues_url")
    val issuesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues{/number}
    @SerialName("keys_url")
    val keysUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/keys{/key_id}
    @SerialName("labels_url")
    val labelsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/labels{/name}
    val language: String = "", // Kotlin
    @SerialName("languages_url")
    val languagesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/languages
    val license: GitHubLicense? = GitHubLicense(),
    @SerialName("merges_url")
    val mergesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/merges
    @SerialName("milestones_url")
    val milestonesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/milestones{/number}
    @SerialName("mirror_url")
    val mirrorUrl: Any? = Any(), // null
    val name: String = "", // refreshVersions
    @SerialName("node_id")
    val nodeId: String = "", // MDEwOlJlcG9zaXRvcnkxNTA4MjcyNzE=
    @SerialName("notifications_url")
    val notificationsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/notifications{?since,all,participating}
    @SerialName("open_issues")
    val openIssues: Int = 0, // 80
    @SerialName("open_issues_count")
    val openIssuesCount: Int = 0, // 80
    val owner: GitHubOwner? = GitHubOwner(),
    val `private`: Boolean = false, // false
    @SerialName("pulls_url")
    val pullsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/pulls{/number}
    @SerialName("pushed_at")
    val pushedAt: String = "", // 2022-08-06T17:08:23Z
    @SerialName("releases_url")
    val releasesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/releases{/id}
    val size: Int = 0, // 10363
    @SerialName("ssh_url")
    val sshUrl: String = "", // git@github.com:jmfayard/refreshVersions.git
    @SerialName("stargazers_count")
    val stargazersCount: Int = 0, // 1342
    @SerialName("stargazers_url")
    val stargazersUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/stargazers
    @SerialName("statuses_url")
    val statusesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/statuses/{sha}
    @SerialName("subscribers_url")
    val subscribersUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/subscribers
    @SerialName("subscription_url")
    val subscriptionUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/subscription
    @SerialName("svn_url")
    val svnUrl: String = "", // https://github.com/jmfayard/refreshVersions
    @SerialName("tags_url")
    val tagsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/tags
    @SerialName("teams_url")
    val teamsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/teams
    val topics: List<String>? = listOf(),
    @SerialName("trees_url")
    val treesUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/git/trees{/sha}
    @SerialName("updated_at")
    val updatedAt: String = "", // 2022-08-05T08:04:03Z
    val url: String = "", // https://api.github.com/repos/jmfayard/refreshVersions
    val visibility: String = "", // public
    val watchers: Int = 0, // 1342
    @SerialName("watchers_count")
    val watchersCount: Int = 0, // 1342
    @SerialName("web_commit_signoff_required")
    val webCommitSignoffRequired: Boolean = false // false
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
