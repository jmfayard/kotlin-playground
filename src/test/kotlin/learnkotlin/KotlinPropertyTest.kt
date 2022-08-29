package learnkotlin

fun main() {
    println("Starting at ${System.currentTimeMillis()}")
    repeat(3) { println(Lazy.somethingEpansive("naive")) }
    repeat(3) { println(Lazy.javaLazy()) }
    repeat(3) { println(Lazy.kotlinLazy) }
}


object Lazy {
    fun somethingEpansive(greeting: String): String {
        Thread.sleep(100)
        return "Hello $greeting at ${System.currentTimeMillis()}"
    }

    // Java common pattern
    fun javaLazy(): String {
        if (_data == null) _data = somethingEpansive("Java")
        return _data!!
    }

    private var _data: String? = null

    // Kotlin solution
    val kotlinLazy by lazy { somethingEpansive("Kotlin") }
}
