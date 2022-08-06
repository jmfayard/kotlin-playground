package github.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubIssue(
    @SerialName("active_lock_reason")
    val assignee: GitHubUser? = GitHubUser(),
    val assignees: List<GitHubUser?> = listOf(),
    @SerialName("author_association")
    val authorAssociation: String = "", // OWNER
    val body: String = "", // The next version of refreshVersions will support Gradle Versions Catalog.## CaveatThe task `./gradlew refreshVersions` will show available updates inside `gradle/libs.versions.toml````toml [versions] okhttp3 = "3.5.0"### available = "3.6.0"### available = "3.7.0" [libraries] okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp3" }```The issue is: what happens to your build if you upgrade the version in that file?Well actually... nothing if you don't use `implementation(libs.okhttp)`  everywhere in your build.If you have hardcoded it with `implementation("com.squareup.okhttp3:okhttp:4.10.0")`, then nothing will happen.## WorkaroundIf you run the task `./gradlew refreshVersionsMigrate`, your build will be updated to use the dependency notationsIf the versions in Gradle (4.10.0) and the `gradle/libs.versions.toml` file are different, refreshVersions will detect that and mark the `okhttp` property as being unused (`## unused`) to avoid confusing you.On the other hand, if it's the same version, you will see the updates and only get the unused comment in the next run.
    @SerialName("closed_at")
    val closedAt: String? = null,
    @SerialName("closed_by")
    val closedBy: String? = null,
    val comments: Int = 0, // 0
    @SerialName("comments_url")
    val commentsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/571/comments
    @SerialName("created_at")
    val createdAt: String = "", // 2022-08-05T15:11:03Z
    @SerialName("events_url")
    val eventsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/571/events
    @SerialName("html_url")
    val htmlUrl: String = "", // https://github.com/jmfayard/refreshVersions/issues/571
    val id: Int = 0, // 1330061731
    val labels: List<GitHubLabel> = listOf(),
    @SerialName("labels_url")
    val labelsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/571/labels{/name}
    val locked: Boolean = false, // false
    val milestone: GitHubMilestone? = GitHubMilestone(),
    @SerialName("node_id")
    val nodeId: String = "", // I_kwDOCP1xB85PRyGj
    val number: Int = 0, // 571
    val reactions: GitHubReactions? = GitHubReactions(),
    @SerialName("repository_url")
    val repositoryUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions
    val state: String = "", // open
    @SerialName("timeline_url")
    val timelineUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/571/timeline
    val title: String = "", // Caveat: refreshVersions might show available updates in versions catalog for dependencies you don't actually use
    @SerialName("updated_at")
    val updatedAt: String = "", // 2022-08-06T21:07:28Z
    val url: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/571
    val user: GitHubUser? = GitHubUser()
)


@Serializable
data class GitHubMilestone(
    @SerialName("closed_at")
    val closedAt: String? = null,
    @SerialName("closed_issues")
    val closedIssues: Int = 0, // 20
    @SerialName("created_at")
    val createdAt: String = "", // 2019-10-13T05:40:03Z
    val creator: GitHubUser? = GitHubUser(),
    val description: String = "",
    @SerialName("due_on")
    val dueOn: String? = null,
    @SerialName("html_url")
    val htmlUrl: String = "", // https://github.com/jmfayard/refreshVersions/milestone/8
    val id: Int = 0, // 4746721
    @SerialName("labels_url")
    val labelsUrl: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/milestones/8/labels
    @SerialName("node_id")
    val nodeId: String = "", // MDk6TWlsZXN0b25lNDc0NjcyMQ==
    val number: Int = 0, // 8
    @SerialName("open_issues")
    val openIssues: Int = 0, // 8
    val state: String = "", // open
    val title: String = "", // v1.0.0
    @SerialName("updated_at")
    val updatedAt: String = "", // 2022-08-06T21:04:52Z
    val url: String = "" // https://api.github.com/repos/jmfayard/refreshVersions/milestones/8
)

@Serializable
data class GitHubReactions(
    val confused: Int = 0, // 0
    val eyes: Int = 0, // 0
    val heart: Int = 0, // 0
    val hooray: Int = 0, // 0
    val laugh: Int = 0, // 0
    val rocket: Int = 0, // 0
    @SerialName("total_count")
    val totalCount: Int = 0, // 0
    val url: String = "", // https://api.github.com/repos/jmfayard/refreshVersions/issues/571/reactions
    val `+1`: Int = 0, // 0
    val `-1`: Int = 0 // 0
)

