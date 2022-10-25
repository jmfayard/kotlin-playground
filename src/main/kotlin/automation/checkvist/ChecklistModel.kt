package automation.checkvist

import kotlinx.serialization.SerialName

data class ChecklistModel(
    val archived: Boolean,
    val created_at: String,
    val id: Int,
    val item_count: Int,
    @SerialName("markdown?")
    val isMarkdown: Boolean,
    val name: String,
    val options: Int,
    val percent_completed: Int,
    val `public`: Boolean,
    val read_only: Boolean,
    val related_task_ids: Any,
    val tags_as_text: String,
    val task_completed: Int,
    val task_count: Int,
    val updated_at: String,
    val user_count: Int,
    val user_updated_at: String
)
