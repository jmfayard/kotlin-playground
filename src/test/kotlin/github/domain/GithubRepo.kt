package github.domain


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubRepo(
    @SerialName("allow_forking")
    val allowForking: Boolean = false, // true
    @SerialName("archive_url")
    val archiveUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/{archive_format}{/ref}
    val archived: Boolean = false, // false
    @SerialName("assignees_url")
    val assigneesUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/assignees{/user}
    @SerialName("blobs_url")
    val blobsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/git/blobs{/sha}
    @SerialName("branches_url")
    val branchesUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/branches{/branch}
    @SerialName("clone_url")
    val cloneUrl: String = "", // https://github.com/jmfayard/71694304.git
    @SerialName("collaborators_url")
    val collaboratorsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/collaborators{/collaborator}
    @SerialName("comments_url")
    val commentsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/comments{/number}
    @SerialName("commits_url")
    val commitsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/commits{/sha}
    @SerialName("compare_url")
    val compareUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/compare/{base}...{head}
    @SerialName("contents_url")
    val contentsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/contents/{+path}
    @SerialName("contributors_url")
    val contributorsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/contributors
    @SerialName("created_at")
    val createdAt: String = "", // 2018-01-08T12:09:36Z
    @SerialName("default_branch")
    val defaultBranch: String = "", // master
    @SerialName("deployments_url")
    val deploymentsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/deployments
    val description: String = "", // https://issuetracker.google.com/issues/71694304
    val disabled: Boolean = false, // false
    @SerialName("downloads_url")
    val downloadsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/downloads
    @SerialName("events_url")
    val eventsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/events
    val fork: Boolean = false, // false
    val forks: Int = 0, // 0
    @SerialName("forks_count")
    val forksCount: Int = 0, // 0
    @SerialName("forks_url")
    val forksUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/forks
    @SerialName("full_name")
    val fullName: String = "", // jmfayard/71694304
    @SerialName("git_commits_url")
    val gitCommitsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/git/commits{/sha}
    @SerialName("git_refs_url")
    val gitRefsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/git/refs{/sha}
    @SerialName("git_tags_url")
    val gitTagsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/git/tags{/sha}
    @SerialName("git_url")
    val gitUrl: String = "", // git://github.com/jmfayard/71694304.git
    @SerialName("has_downloads")
    val hasDownloads: Boolean = false, // true
    @SerialName("has_issues")
    val hasIssues: Boolean = false, // true
    @SerialName("has_pages")
    val hasPages: Boolean = false, // false
    @SerialName("has_projects")
    val hasProjects: Boolean = false, // true
    @SerialName("has_wiki")
    val hasWiki: Boolean = false, // true
    val homepage: String = "", // https://play.google.com/store/apps/details?id=com.duckduckgo.mobile.android
    @SerialName("hooks_url")
    val hooksUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/hooks
    @SerialName("html_url")
    val htmlUrl: String = "", // https://github.com/jmfayard/71694304
    val id: Int = 0, // 116672112
    @SerialName("is_template")
    val isTemplate: Boolean = false, // false
    @SerialName("issue_comment_url")
    val issueCommentUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/issues/comments{/number}
    @SerialName("issue_events_url")
    val issueEventsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/issues/events{/number}
    @SerialName("issues_url")
    val issuesUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/issues{/number}
    @SerialName("keys_url")
    val keysUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/keys{/key_id}
    @SerialName("labels_url")
    val labelsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/labels{/name}
    val language: String? = null, // Kotlin
    @SerialName("languages_url")
    val languagesUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/languages
    val license: GitHubLicense? = GitHubLicense(),
    @SerialName("merges_url")
    val mergesUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/merges
    @SerialName("milestones_url")
    val milestonesUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/milestones{/number}
    @SerialName("mirror_url")
    val mirrorUrl: String? = null,
    val name: String = "", // 71694304
    @SerialName("node_id")
    val nodeId: String = "", // MDEwOlJlcG9zaXRvcnkxMTY2NzIxMTI=
    @SerialName("notifications_url")
    val notificationsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/notifications{?since,all,participating}
    @SerialName("open_issues")
    val openIssues: Int = 0, // 0
    @SerialName("open_issues_count")
    val openIssuesCount: Int = 0, // 0
    val owner: GitHubOwner? = GitHubOwner(),
    val permissions: GitHubPermissions? = GitHubPermissions(),
    val `private`: Boolean = false, // false
    @SerialName("pulls_url")
    val pullsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/pulls{/number}
    @SerialName("pushed_at")
    val pushedAt: String = "", // 2018-02-01T14:21:35Z
    @SerialName("releases_url")
    val releasesUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/releases{/id}
    val size: Int = 0, // 125
    @SerialName("ssh_url")
    val sshUrl: String = "", // git@github.com:jmfayard/71694304.git
    @SerialName("stargazers_count")
    val stargazersCount: Int = 0, // 0
    @SerialName("stargazers_url")
    val stargazersUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/stargazers
    @SerialName("statuses_url")
    val statusesUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/statuses/{sha}
    @SerialName("subscribers_url")
    val subscribersUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/subscribers
    @SerialName("subscription_url")
    val subscriptionUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/subscription
    @SerialName("svn_url")
    val svnUrl: String = "", // https://github.com/jmfayard/71694304
    @SerialName("tags_url")
    val tagsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/tags
    @SerialName("teams_url")
    val teamsUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/teams
    val topics: List<String>? = listOf(),
    @SerialName("trees_url")
    val treesUrl: String = "", // https://api.github.com/repos/jmfayard/71694304/git/trees{/sha}
    @SerialName("updated_at")
    val updatedAt: String = "", // 2018-06-04T08:57:29Z
    val url: String = "", // https://api.github.com/repos/jmfayard/71694304
    val visibility: String = "", // public
    val watchers: Int = 0, // 0
    @SerialName("watchers_count")
    val watchersCount: Int = 0, // 0
    @SerialName("web_commit_signoff_required")
    val webCommitSignoffRequired: Boolean = false // false
)
