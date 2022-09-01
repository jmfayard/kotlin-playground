package counterarguments

/**
 * Thesis:
 * When statements are often considered code smells and should be avoided.
 * https://medium.com/better-programming/avoid-using-when-expression-as-much-as-possible-use-polymorphism-instead-890b92389060
 *
 * Antithesis:
 * In fact, there is absolutely nothing wrong with when { },
 * a perfectly good and valid language construct.
 * Instead of being dogmatic with "this very useful thing is a code smell",
 * always select the simplest solution that works.
 * And here it's real easy to find one.
 */
data class ButtonSize(
    val heightDpInt: Int,
    val color: Color,
    val textResId: Res,
) {
    companion object {
        val small = ButtonSize(16, "Red", "R.string.small_button_text")
        val medium = ButtonSize(24, "Gray", "R.string.medium_button_text")
        val large = ButtonSize(32, "Green", "R.string.large_button_text")
    }
}

typealias Res = Any
typealias Color = Any
